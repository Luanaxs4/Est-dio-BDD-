import { api } from './api';
import type { CenarioRequest, CenarioResponse } from '../types/cenario';

export const cenarioService = {
  listar: async (historiaId: number): Promise<CenarioResponse[]> => {
    const response = await api.get<CenarioResponse[]>(`/historias/${historiaId}/cenarios`);
    return response.data;
  },

  criar: async (historiaId: number, dados: CenarioRequest): Promise<CenarioResponse> => {
    const response = await api.post<CenarioResponse>(`/historias/${historiaId}/cenarios`, dados);
    return response.data;
  },

  atualizar: async (id: number, dados: CenarioRequest): Promise<CenarioResponse> => {
    const response = await api.put<CenarioResponse>(`/cenarios/${id}`, dados);
    return response.data;
  },

  excluir: async (id: number): Promise<void> => {
    await api.delete(`/cenarios/${id}`);
  },
};