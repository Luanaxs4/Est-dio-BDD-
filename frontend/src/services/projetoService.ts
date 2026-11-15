import { api } from './api';
import type { ProjetoRequest, ProjetoResponse } from '../types/projeto';
import type { ResumoProjeto } from '../types/resumoProjeto';

export const projetoService = {
  criar: async (dados: ProjetoRequest): Promise<ProjetoResponse> => {
    const response = await api.post<ProjetoResponse>('/projetos', dados);
    return response.data;
  },

  listar: async (): Promise<ProjetoResponse[]> => {
    const response = await api.get<ProjetoResponse[]>('/projetos');
    return response.data;
  },

  buscarPorId: async (id: number): Promise<ProjetoResponse> => {
    const response = await api.get<ProjetoResponse>(`/projetos/${id}`);
    return response.data;
  },

  obterResumo: async (id: number): Promise<ResumoProjeto> => {
    const response = await api.get<ResumoProjeto>(`/projetos/${id}/resumo`);
    return response.data;
  },

  atualizar: (id: number, dados: { nome: string; descricao?: string; contextoDominio?: string }) =>
  api.put<ProjetoResponse>(`/projetos/${id}`, dados),
};