export type TipoPasso = 'DADO' | 'QUANDO' | 'ENTAO' | 'E' | 'MAS';
export type OrigemCenario = 'HUMANO' | 'SUGESTAO_IA';

export interface PassoRequest {
  tipo: TipoPasso;
  texto: string;
}

export interface PassoResponse {
  id: number;
  tipo: TipoPasso;
  texto: string;
  posicao: number;
}

export interface CenarioRequest {
  titulo: string;
  origem?: OrigemCenario;
  passos: PassoRequest[];
}

export interface CenarioResponse {
  id: number;
  titulo: string;
  posicao: number;
  origem: OrigemCenario;
  passos: PassoResponse[];
}