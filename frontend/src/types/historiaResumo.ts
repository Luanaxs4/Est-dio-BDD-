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
  dataCriacao: string;
  dataAtualizacao: string;
  projetoId: number;
  autorId: number;
  autorNome: string;
  cenarios: CenarioResponse[];
}

// Espelho de HistoriaResumo (item da listagem de histórias de um projeto - Regra 6)
export interface HistoriaResumo {
  id: number;
  titulo: string;
  prioridade: Prioridade;
  qtdCenarios: number;
  qtdSugestoesPendentes: number;
} 