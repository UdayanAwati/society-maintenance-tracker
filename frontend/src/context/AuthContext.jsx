import { createContext, useContext, useEffect, useMemo, useState } from 'react';
import { api } from '../services/api.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null);
  const [loading, setLoading] = useState(Boolean(localStorage.getItem('smt_token')));
  const [dark, setDark] = useState(localStorage.getItem('smt_dark') === 'true');

  useEffect(() => {
    document.documentElement.classList.toggle('dark', dark);
    localStorage.setItem('smt_dark', String(dark));
  }, [dark]);

  useEffect(() => {
    if (!localStorage.getItem('smt_token')) return;
    api.get('/auth/me').then((res) => setUser(res.data)).finally(() => setLoading(false));
  }, []);

  const value = useMemo(() => ({
    user,
    loading,
    dark,
    setUser,
    setDark,
    async login(payload) {
      const res = await api.post('/auth/login', payload);
      localStorage.setItem('smt_token', res.data.token);
      setUser(res.data.user);
    },
    async register(payload) {
      const res = await api.post('/auth/register', payload);
      localStorage.setItem('smt_token', res.data.token);
      setUser(res.data.user);
    },
    logout() {
      localStorage.removeItem('smt_token');
      setUser(null);
    },
  }), [user, loading, dark]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  return useContext(AuthContext);
}
