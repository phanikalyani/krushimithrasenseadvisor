import React from "react";

export default function Avatar({speaking}:{speaking:boolean}){
  // simple avatar with mouth that grows when speaking
  return (
    <div style={{display:"flex", alignItems:"center", gap:12}}>
      <div style={{
        width:80, height:80, borderRadius:40, background:"#80c080",
        display:"flex", alignItems:"center", justifyContent:"center", position:"relative"
      }}>
        <div style={{fontSize:32}}>🌾</div>
        <div style={{
          position:"absolute", bottom:10, width: speaking?36:12, height:speaking?12:6,
          background:"#332", borderRadius:8, transition:"all 120ms ease"
        }} />
      </div>
      <div>
        <div style={{fontWeight:600}}>Krushi Mithra</div>
        <div style={{fontSize:12, color:"#666"}}>Your agricultural advisor</div>
      </div>
    </div>
  );
}
