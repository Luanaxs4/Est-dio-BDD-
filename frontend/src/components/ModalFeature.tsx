
import { useState } from 'react';
import type { ArquivoFeature } from '../services/historiaService';

interface ModalFeatureProps {
  arquivo: ArquivoFeature;
  onFechar: () => void;
}

export function ModalFeature({ arquivo, onFechar }: ModalFeatureProps) {
  const [copiado, setCopiado] = useState(false);

  const handleCopiar = async () => {
    await navigator.clipboard.writeText(arquivo.conteudo);
    setCopiado(true);
    setTimeout(() => setCopiado(false), 2000);
  };

  const handleBaixar = () => {
    const blob = new Blob([arquivo.conteudo], { type: 'text/plain;charset=utf-8' });
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = arquivo.nomeArquivo;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
  };

  return (
    <div className="modal-backdrop" onClick={onFechar}>
      <div className="modal-content" onClick={(e) => e.stopPropagation()}>
        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
          <div>
            <h2 style={{ fontSize: '1.2rem' }}>Exportação Gherkin (.feature)</h2>
            <small style={{ color: '#64748b' }}>Arquivo: {arquivo.nomeArquivo}</small>
          </div>
          <button type="button" className="btn-secondary" onClick={onFechar}>
            ✕ Fechar
          </button>
        </div>

        <pre
          style={{
            backgroundColor: '#0f172a',
            color: '#e2e8f0',
            padding: '1.25rem',
            borderRadius: '8px',
            overflowX: 'auto',
            fontSize: '0.9rem',
            lineHeight: 1.6,
            marginBottom: '1.25rem',
            fontFamily: 'Consolas, Monaco, "Courier New", monospace',
          }}
        >
          {arquivo.conteudo}
        </pre>

        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
          <button type="button" className="btn-secondary" onClick={handleCopiar}>
            {copiado ? '✓ Copiado!' : 'Copiar texto'}
          </button>
          <button type="button" className="btn-primary" style={{ width: 'auto' }} onClick={handleBaixar}>
            ⬇ Baixar {arquivo.nomeArquivo}
          </button>
        </div>
      </div>
    </div>
  );
}