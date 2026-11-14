import { api } from './api';
import type {
  DecisaoSugestao,
  SugestaoDecisaoRequest,
  SugestaoRequest,
  SugestaoResponse,
} from '../types/sugestao';

export const sugestaoService = {
  registrar: async (historiaId: number, dados: SugestaoRequest): Promise<SugestaoResponse> => {
    const response = await api.post<SugestaoResponse>(
      `/historias/${historiaId}/sugestoes`,
      dados
    );
    return response.data;
  },

  listarPorHistoria: async (
    historiaId: number,
    decisao?: DecisaoSugestao
  ): Promise<SugestaoResponse[]> => {
    const response = await api.get<SugestaoResponse[]>(`/historias/${historiaId}/sugestoes`, {
      params: decisao ? { decisao } : undefined,
    });
    return response.data;
  },

  decidir: async (id: number, dados: SugestaoDecisaoRequest): Promise<SugestaoResponse> => {
    const response = await api.put<SugestaoResponse>(`/sugestoes/${id}/decisao`, dados);
    return response.data;
  },
};