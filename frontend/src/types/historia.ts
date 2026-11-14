import type { CenarioResponse } from './cenario';

export type Prioridade = 'ALTA' | 'MEDIA' | 'BAIXA';

// Espelho de HistoriaDto.Request
export interface HistoriaRequest {
  titulo: string;
  papel: string;
  objetivo: string;
  beneficio: string;
  prioridade: Prioridade;
}

// Espelho de HistoriaDto.Response
export interface HistoriaResponse {
  id: number;
  titulo: string;
  papel: string;
  objetivo: string;
  beneficio: string;
  prioridade: Prioridade;
  dataCriacao: string;     // LocalDate chega como "YYYY-MM-DD"
  dataAtualizacao: string; // LocalDate chega como "YYYY-MM-DD"
  projetoId: number;
  autorId: number;
  autorNome: string;
  cenarios: CenarioResponse[];
}