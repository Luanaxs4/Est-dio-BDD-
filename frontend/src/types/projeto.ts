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