import axios from 'axios';

// Espelho do ProblemDetail retornado pelo GlobalExceptionHandler
export interface ProblemDetail {
  type?: string;
  title?: string;
  status: number;
  detail: string;
  instance?: string;
  campos?: Record<string, string>; // Preenchido nos erros de @Valid (400 Bad Request)
}

/**
 * Função utilitária para extrair a mensagem de erro da API de forma segura
 * para exibir em alertas ou mensagens na tela.
 */
export function extrairMensagemErro(erro: unknown): string {
  if (axios.isAxiosError(erro) && erro.response?.data) {
    const pd = erro.response.data as ProblemDetail;

    // Se houver erros específicos de campos (@Valid), junta todos em uma frase
    if (pd.campos && Object.keys(pd.campos).length > 0) {
      const detalhesCampos = Object.values(pd.campos).join(' | ');
      return `${pd.detail} (${detalhesCampos})`;
    }

    if (pd.detail) {
      return pd.detail;
    }
  }

  return 'Ocorreu um erro inesperado ao comunicar com o servidor.';
}