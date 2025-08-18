import React, {createContext, useContext, useState, ReactNode} from "react";
import { setAuthToken } from "./api";

type Auth = {
  token: string | null;
  login: (token:string) => void;
  logout: () => void;
};

const AuthContext = createContext<Auth>({token: null, login: ()=>{}, logout: ()=>{}});

export const AuthProvider = ({children}:{children:ReactNode}) => {
  const [token, setToken] = useState<string|null>(localStorage.getItem("token"));

  const login = (t:string) => {
    localStorage.setItem("token", t);
    setAuthToken(t);
    setToken(t);
  };
  const logout = () => {
    localStorage.removeItem("token");
    setAuthToken(undefined);
    setToken(null);
  };

  // set token into axios on init
  if(token) setAuthToken(token);

  return <AuthContext.Provider value={{token, login, logout}}>{children}</AuthContext.Provider>
}

export const useAuth = ()=> useContext(AuthContext);
