import { api } from './api';
import type {
  HistoriaRequest,
  HistoriaResponse,
  Prioridade,
} from '../types/historia';

import type {
  HistoriaResumo
} from '../types/historiaResumo';

export interface ArquivoFeature {
  nomeArquivo: string;
  conteudo: string;
}

export const historiaService = {
  criar: async (projetoId: number, dados: HistoriaRequest): Promise<HistoriaResponse> => {
    const response = await api.post<HistoriaResponse>(
      `/projetos/${projetoId}/historias`,
      dados
    );
    return response.data;
  },

  listarPorProjeto: async (
    projetoId: number,
    prioridade?: Prioridade
  ): Promise<HistoriaResumo[]> => {
    const response = await api.get<HistoriaResumo[]>(`/projetos/${projetoId}/historias`, {
      params: prioridade ? { prioridade } : undefined,
    });
    return response.data;
  },

  buscarPorId: async (id: number): Promise<HistoriaResponse> => {
    const response = await api.get<HistoriaResponse>(`/historias/${id}`);
    return response.data;
  },

  atualizar: async (id: number, dados: HistoriaRequest): Promise<HistoriaResponse> => {
    const response = await api.put<HistoriaResponse>(`/historias/${id}`, dados);
    return response.data;
  },

  excluir: async (id: number): Promise<void> => {
    await api.delete(`/historias/${id}`);
  },

  exportarFeature: async (id: number): Promise<ArquivoFeature> => {
    try {
      const response = await api.get<string>(`/historias/${id}/feature`, {
        responseType: 'text',
      });

      const disposition = response.headers['content-disposition'] as string | undefined;
      let nomeArquivo = `historia-${id}.feature`;

      if (disposition) {
        const match = disposition.match(/filename\*?=(?:UTF-8'')?"?([^";\n]+)"?/i);
        if (match && match[1]) {
          nomeArquivo = decodeURIComponent(match[1]);
        }
      }

      return {
        nomeArquivo,
        conteudo: response.data,
      };
    } catch (err: any) {
      // Como responseType é 'text', o Axios devolve o JSON de erro como string
      if (err?.response?.data && typeof err.response.data === 'string') {
        try {
          err.response.data = JSON.parse(err.response.data);
        } catch {
          // Se não for JSON válido, mantém a string original
        }
      }
      throw err;
    }
  },
};