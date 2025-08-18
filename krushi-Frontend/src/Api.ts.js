import axios from "axios";

const instance = axios.create({
  baseURL: "/"
});

export function setAuthToken(token?: string|null){
  if(token) instance.defaults.headers.common["Authorization"] = `Bearer ${token}`;
  else delete instance.defaults.headers.common["Authorization"];
}

export default instance;
