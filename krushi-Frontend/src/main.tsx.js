/* global createRoot */

import { AuthProvider } from "./AuthContext";
// ...
createRoot(...).render(
  <AuthProvider>
    <App />
  </AuthProvider>
)
