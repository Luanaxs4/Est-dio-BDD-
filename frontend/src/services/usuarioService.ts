import { api } from './api';
import type { UsuarioRequest, UsuarioResponse } from '../types/usuario';

export const usuarioService = {
  criar: async (dados: UsuarioRequest): Promise<UsuarioResponse> => {
    const response = await api.post<UsuarioResponse>('/usuarios', dados);
    return response.data;
  },

  listar: async (): Promise<UsuarioResponse[]> => {
    const response = await api.get<UsuarioResponse[]>('/usuarios');
    return response.data;
  },

  buscarPorId: async (id: number): Promise<UsuarioResponse> => {
    const response = await api.get<UsuarioResponse>(`/usuarios/${id}`);
    return response.data;
  },
};