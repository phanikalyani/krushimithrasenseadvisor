import os
import json
from flask import Flask, request, jsonify, send_file, make_response
from sentence_transformers import SentenceTransformer
import numpy as np
import psycopg2
from pgvector.psycopg2 import register_vector
from gtts import gTTS
import io
from datetime import datetime, timedelta
import base64

app = Flask(__name__)

DB_URL = os.environ.get("DATABASE_URL", "postgresql://krushi:krushi@postgres:5432/krushi")
EMBED_MODEL = os.environ.get("EMBED_MODEL", "paraphrase-multilingual-MiniLM-L12-v2")
TTS_PROVIDER = os.environ.get("TTS_PROVIDER", "gtts")  # or 'espeak' for offline
EMBED_DIM = 384

print("Loading embedding model:", EMBED_MODEL)
model = SentenceTransformer(EMBED_MODEL)

# connect to DB
conn = psycopg2.connect(DB_URL)
register_vector(conn)
conn.autocommit = True

# helper: upsert documents from docs_store.json into documents table
def load_docs_store():
    path = "docs_store.json"
    if not os.path.exists(path):
        print("No docs_store.json found")
        return
    with open(path, "r", encoding="utf-8") as f:
        docs = json.load(f)
    cur = conn.cursor()
    for d in docs:
        text = d.get("text","")
        emb = model.encode([text], convert_to_numpy=True)[0]
        # upsert
        cur.execute("""
            INSERT INTO documents (id, title, text, lang, embedding)
            VALUES (%s,%s,%s,%s,%s)
            ON CONFLICT (id) DO UPDATE SET title=EXCLUDED.title, text=EXCLUDED.text, lang=EXCLUDED.lang, embedding=EXCLUDED.embedding
        """, (d["id"], d.get("title"), text, d.get("lang","en"), emb.tolist()))
    cur.close()
    print("Loaded docs into DB.")

# call at startup
load_docs_store()


def top_k_similar_sql(query, k=3):
    q_emb = model.encode([query], convert_to_numpy=True)[0].tolist()
    cur = conn.cursor()
    # pgvector similarity operator: <#> is cosine distance (lower = more similar). We'll use 1 - distance for score.
    # But different operators may exist; using <-> is Euclidean. For small demo, compute cosine in Python after retrieval.
    cur.execute("SELECT id, title, text, lang, embedding FROM documents LIMIT 1000")
    rows = cur.fetchall()
    results = []
    for row in rows:
        docid, title, text, lang, emb = row
        emb_np = np.array(emb, dtype=float)
        q_np = np.array(q_emb, dtype=float)
        # cosine
        an = np.linalg.norm(emb_np)
        bn = np.linalg.norm(q_np)
        sim = float(np.dot(emb_np, q_np) / (an*bn)) if an>0 and bn>0 else 0.0
        results.append((sim, {"id":docid, "title":title, "text":text, "lang":lang}))
    results.sort(key=lambda x: x[0], reverse=True)
    top = [r[1] for r in results[:k]]
    return top, results[0][0] if results else 0.0

def compose_answer_simple(query, retrieved):
    if not retrieved:
        return "No matching documents found.", 0.0
    best = retrieved[0]
    first_sentence = best['text'].split(".")[0]
    lines = [f"Short advice (from {best['title']}):", first_sentence.strip() + "."]
    lines.append("\nEvidence:")
    for r in retrieved:
        lines.append(f"- {r['title']}: {r['text'][:140]}...")
    # simple confidence: average length of retrieved texts normalized
    conf = sum(len(r['text']) for r in retrieved) / (len(retrieved)*500.0)
    conf = min(0.99, conf)
    return "\n".join(lines), conf

@app.route("/api/answer", methods=["POST"])
def answer():
    payload = request.get_json(force=True)
    text = payload.get("text","")
    if not text:
        return jsonify({"answer":"Empty query","sources":[], "confidence":0.0})
    retrieved, sim = top_k_similar_sql(text, k=3)
    ans, conf = compose_answer_simple(text, retrieved)
    sources = [r["id"] for r in retrieved]
    return jsonify({"answer":ans, "sources":sources, "confidence":conf})

@app.route("/api/tts", methods=["POST"])
def tts():
    payload = request.get_json(force=True)
    text = payload.get("text","")
    lang = payload.get("lang","en")
    if not text:
        return jsonify({"error":"no text"}), 400

    if TTS_PROVIDER == "gtts":
        try:
            tts = gTTS(text=text, lang=lang if lang else "en")
            mp3_fp = io.BytesIO()
            tts.write_to_fp(mp3_fp)
            mp3_fp.seek(0)
            return send_file(mp3_fp, mimetype="audio/mpeg", as_attachment=False, download_name="speech.mp3")
        except Exception as e:
            return jsonify({"error": str(e)}), 500
    else:
        # placeholder for offline: call espeak or pyttsx3 and return WAV
        return jsonify({"error":"TTS provider not configured"}), 500

if __name__ == "__main__":
    app.run(host="0.0.0.0", port=5001)
