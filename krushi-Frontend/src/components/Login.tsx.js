import React, {useState} from "react";
import api from "../api";
import { useAuth } from "../AuthContext";

export default function Login(){
  const { login } = useAuth();
  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [err, setErr] = useState("");

  const submit = async (e:React.FormEvent) => {
    e.preventDefault();
    try {
      const res = await api.post("/api/v1/auth/login", { username, password});
      const token = res.data?.token;
      if(token){
        login(token);
      } else {
        setErr("Login failed");
      }
    } catch (err:any){
      setErr("Invalid credentials");
    }
  };

  return (
    <form onSubmit={submit} style={{maxWidth:360}}>
      <h3>Login</h3>
      <input placeholder="username" value={username} onChange={e=>setUsername(e.target.value)} /><br/>
      <input placeholder="password" type="password" value={password} onChange={e=>setPassword(e.target.value)} /><br/>
      <button>Login</button>
      <div style={{color:"red"}}>{err}</div>
    </form>
  );
}
