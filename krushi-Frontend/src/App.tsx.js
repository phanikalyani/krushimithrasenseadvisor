import React from "react";
import { useAuth } from "./AuthContext";
import Login from "./components/Login";
import Signup from "./components/Signup";
import Chat from "./components/Chat";


import Chat from "./components/Chat";

export default function App(){
  return (
    <div style={{padding:20, fontFamily:"Arial"}}>
      <h2>Krushi Mithra — Sense Advisor (Demo)</h2>
      <Chat />
    </div>
  )
}
export default function App(){
  const { token } = useAuth();
  if(!token){
    return (
      <div style={{display:"flex", gap:20}}>
        <div><Signup/></div>
        <div><Login/></div>
      </div>
    );
  }
  return   <Chat />;
}
