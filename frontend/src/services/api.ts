import axios from 'axios';

export const CHAVE_USUARIO_STORAGE = 'estudio_bdd_usuario_id';

export const api = axios.create({
  baseURL: 'http://localhost:8080/api',
});

// Interceptor: antes de qualquer requisição sair, anexa o X-Usuario-Id salvo
api.interceptors.request.use((config) => {
  const usuarioId = localStorage.getItem(CHAVE_USUARIO_STORAGE);
  if (usuarioId) {
    config.headers['X-Usuario-Id'] = usuarioId;
  }
  return config;
});