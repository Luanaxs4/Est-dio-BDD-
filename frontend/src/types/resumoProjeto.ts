import type { OrigemCenario } from './cenario';
import type { DecisaoSugestao, MotivoRejeicao } from './sugestao';

// Espelho de ProjetoDTO.Request
export interface ProjetoRequest {
  nome: string;
  descricao?: string;
  contextoDominio?: string;
}

// Espelho de ProjetoDTO.Response
export interface ProjetoResponse {
  id: number;
  nome: string;
  descricao: string | null;
  contextoDominio: string | null;
  dataCriacao: string; // LocalDate chega como "YYYY-MM-DD"
}

// Espelho de ResumoProjeto.MotivoContagem
export interface MotivoContagem {
  motivo: MotivoRejeicao;
  quantidade: number;
}

// Espelho de ResumoProjeto (Regra 7)
export interface ResumoProjeto {
  projetoId: number;
  projetoNome: string;
  totalHistorias: number;
  cenariosPorOrigem: Partial<Record<OrigemCenario, number>>;
  sugestoesPorDecisao: Partial<Record<DecisaoSugestao, number>>;
  motivosRejeicaoMaisFrequentes: MotivoContagem[];
}