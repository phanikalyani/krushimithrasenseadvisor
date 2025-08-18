import React, {useState, useEffect} from "react";
import api from "../api";
import { useAuth } from "../AuthContext";
import Avatar from "./Avatar";

export default function Chat(){
  const [text, setText] = useState("");
  const [history, setHistory] = useState<{q:string, a:string}[]>([]);
  const [loading, setLoading] = useState(false);
  const [speaking, setSpeaking] = useState(false);
  const { token } = useAuth();

  useEffect(()=>{
    // attach auth token automatically if it exists (AuthContext sets axios header)
  }, [token]);

  // Speech recognition (browser Web Speech API)
  const startSpeechRecognition = () => {
    const w:any = window;
    const SpeechRecognition = w.SpeechRecognition || w.webkitSpeechRecognition;
    if(!SpeechRecognition){
      alert("Speech recognition not supported in this browser.");
      return;
    }
    const rec = new SpeechRecognition();
    rec.lang = "en-IN"; // changeable
    rec.interimResults = false;
    rec.maxAlternatives = 1;
    rec.onresult = (ev:any) => {
      const spoken = ev.results[0][0].transcript;
      setText(spoken);
    };
    rec.onerror = (err:any) => {
      console.error(err);
      alert("Speech recognition error");
    };
    rec.start();
  };

  // Text-to-speech (browser)
  const speakText = (content:string) => {
    const w:any = window;
    if(!w.speechSynthesis) {
      console.warn("No TTS available");
      return;
    }
    const utter = new SpeechSynthesisUtterance(content);
    // choose language from user or response; for demo use en-IN
    utter.lang = "en-IN";
    utter.rate = 0.95;
    utter.onstart = ()=> setSpeaking(true);
    utter.onend = ()=> setSpeaking(false);
    w.speechSynthesis.cancel();
    w.speechSynthesis.speak(utter);
  };

  const send = async () => {
    if(!text.trim()) return;
    const q = text.trim();
    setText("");
    setHistory(prev => [...prev, {q, a: "..." }]);
    setLoading(true);
    try {
      const res = await api.post("/api/v1/query", { text: q });
      const ans = res.data?.answer ?? "No answer";
      setHistory(prev => {
        const copy = [...prev];
        copy[copy.length-1].a = ans;
        return copy;
      });
      // speak the answer
      speakText(ans);
    } catch (err:any) {
      setHistory(prev => {
        const copy = [...prev];
        copy[copy.length-1].a = "Error contacting server";
        return copy;
      });
    } finally {
      setLoading(false);
    }
  }

  return (
    <div style={{maxWidth:760}}>
      <div style={{display:"flex", justifyContent:"space-between", alignItems:"center", marginBottom:12}}>
        <Avatar speaking={speaking} />
        <div>
          <button onClick={startSpeechRecognition} style={{marginRight:8}}>🎙️ Speak</button>
          <button onClick={()=>{
            // logout if needed
            localStorage.removeItem("token");
            window.location.reload();
          }}>Logout</button>
        </div>
      </div>

      <div style={{marginBottom:12}}>
        <textarea rows={3} value={text} onChange={e=>setText(e.target.value)} style={{width:"100%"}} placeholder="Ask e.g. 'When should I irrigate paddy?'" />
        <div style={{marginTop:8}}>
          <button onClick={send} disabled={loading}>Ask</button>
        </div>
      </div>

      <div>
        {history.map((h, i)=>(
          <div key={i} style={{border:"1px solid #ddd", padding:10, marginBottom:8, borderRadius:6}}>
            <div style={{fontWeight:600}}>You: {h.q}</div>
            <div style={{marginTop:6}}>Advisor: <pre style={{whiteSpace:"pre-wrap"}}>{h.a}</pre></div>
          </div>
        ))}
      </div>
    </div>
  )
}
