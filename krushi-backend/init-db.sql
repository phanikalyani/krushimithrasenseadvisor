-- create basic tables: users, otps, documents
CREATE TABLE IF NOT EXISTS users (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  username text UNIQUE NOT NULL,
  password_hash text,
  role text DEFAULT 'farmer',
  preferred_language text DEFAULT 'en',
  created_at timestamptz DEFAULT now()
);

CREATE TABLE IF NOT EXISTS otps (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  username text NOT NULL,  -- phone number or identifier
  code text NOT NULL,      -- short OTP code
  created_at timestamptz DEFAULT now(),
  expires_at timestamptz NOT NULL
);

-- documents table with pgvector vector column (vector dim: 384)
CREATE TABLE IF NOT EXISTS documents (
  id text PRIMARY KEY,
  title text,
  text text,
  lang text,
  created_at timestamptz DEFAULT now(),
  embedding vector(384)   -- requires pgvector extension (provided by image)
);
-- index for fast similarity search (ivfflat requires custom setup; fallback to cosine on small data)
CREATE INDEX IF NOT EXISTS idx_documents_embedding ON documents USING ivfflat (embedding vector_l2_ops) WITH (lists = 100);
