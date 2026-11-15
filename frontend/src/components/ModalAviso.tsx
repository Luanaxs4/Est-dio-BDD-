import { createPortal } from 'react-dom';

interface ModalAvisoProps {
  aberto: boolean;
  titulo?: string;
  mensagem: string;
  onFechar: () => void;
  onConfirmar?: () => void;
  textoConfirmar?: string;
}

export function ModalAviso({
  aberto,
  titulo = 'Erro',
  mensagem,
  onFechar,
  onConfirmar,
  textoConfirmar = 'Confirmar',
}: ModalAvisoProps) {
  if (!aberto) return null;

  const ehConfirmacao = Boolean(onConfirmar);

  return createPortal(
    <div
      style={{
        position: 'fixed',
        inset: 0,
        backgroundColor: 'rgba(15, 23, 42, 0.6)',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        zIndex: 9999,
        padding: '1rem',
      }}
      onClick={onFechar}
    >
      <div
        className="card"
        style={{
          maxWidth: '420px',
          width: '100%',
          padding: '1.5rem',
          backgroundColor: '#ffffff',
          boxShadow: '0 20px 35px rgba(0, 0, 0, 0.3)',
        }}
        onClick={(e) => e.stopPropagation()}
      >
        <h3
          style={{
            fontSize: '1.1rem',
            color: ehConfirmacao ? '#0f172a' : '#991b1b',
            marginBottom: '0.6rem',
          }}
        >
          {titulo}
        </h3>
        <p style={{ fontSize: '0.95rem', color: '#334155', marginBottom: '1.25rem', lineHeight: 1.5 }}>
          {mensagem}
        </p>
        <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.6rem' }}>
          {ehConfirmacao ? (
            <>
              <button
                type="button"
                className="btn-secondary"
                style={{ width: 'auto', padding: '0.5rem 1rem' }}
                onClick={onFechar}
              >
                Cancelar
              </button>
              <button
                type="button"
                className="btn-danger"
                style={{ width: 'auto', padding: '0.5rem 1.1rem' }}
                onClick={() => {
                  onConfirmar?.();
                  onFechar();
                }}
              >
                {textoConfirmar}
              </button>
            </>
          ) : (
            <button
              type="button"
              className="btn-primary"
              style={{ width: 'auto', padding: '0.5rem 1.25rem' }}
              onClick={onFechar}
            >
              OK
            </button>
          )}
        </div>
      </div>
    </div>,
    document.body
  );
}