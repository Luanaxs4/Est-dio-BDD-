export type DecisaoSugestao = 'PENDENTE' | 'ACEITA' | 'REJEITADA';

export type MotivoRejeicao = 
  | 'INCORRETA' 
  | 'GENERICA' 
  | 'DUPLICADA' 
  | 'FORA_DO_ESCOPO' 
  | 'OUTRO';

// Espelho de SugestaoDto.Request
export interface SugestaoRequest {
  ferramenta: string;
  promptUtilizado?: string;
  conteudo: string;
}

// Espelho de SugestaoDto.DecisaoRequest
export interface SugestaoDecisaoRequest {
  decisao: 'ACEITA' | 'REJEITADA';
  motivoRejeicao?: MotivoRejeicao;
  justificativa?: string;
}

// Espelho de SugestaoDto.Response
export interface SugestaoResponse {
  id: number;
  ferramenta: string;
  promptUtilizado: string | null;
  conteudo: string;
  decisao: DecisaoSugestao;
  motivoRejeicao: MotivoRejeicao | null;
  justificativa: string | null;
  dataRegistro: string;           // LocalDate chega como "YYYY-MM-DD"
  dataDecisao: string | null;
  historiaId: number;
  registradaPorId: number;
  decididaPorId: number | null;
}