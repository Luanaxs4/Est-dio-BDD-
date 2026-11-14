import { Link, useNavigate } from 'react-router-dom';
import { useUsuario } from '../contexts/usuarioContext';

export function Header() {
  const { usuarios, usuarioAtual, selecionarUsuario, sair } = useUsuario();
  const navigate = useNavigate();

  if (!usuarioAtual) return null;

  const handleSair = () => {
    sair();
    navigate('/login');
  };

  return (
    <header
      style={{
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '1rem 2rem',
        backgroundColor: '#1e293b',
        color: '#fff',
        marginBottom: '1.5rem',
      }}
    >
      <div style={{ display: 'flex', alignItems: 'center', gap: '1.5rem' }}>
        <Link to="/projetos" style={{ color: '#fff', textDecoration: 'none', fontWeight: 'bold', fontSize: '1.2rem' }}>
          Estúdio BDD
        </Link>
        <Link to="/projetos" style={{ color: '#cbd5e1', textDecoration: 'none' }}>
          Projetos
        </Link>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
        <span style={{ fontSize: '0.9rem', color: '#cbd5e1' }}>Usuário:</span>
        <select
          value={usuarioAtual.id}
          onChange={(e) => {
            const selecionado = usuarios.find((u) => u.id === Number(e.target.value));
            if (selecionado) selecionarUsuario(selecionado);
          }}
          style={{ padding: '0.35rem 0.6rem', borderRadius: '4px', border: 'none' }}
        >
          {usuarios.map((u) => (
            <option key={u.id} value={u.id}>
              {u.nome} ({u.email})
            </option>
          ))}
        </select>

        <button
          type="button"
          onClick={handleSair}
          style={{
            backgroundColor: '#334155',
            color: '#f8fafc',
            border: '1px solid #475569',
            padding: '0.35rem 0.75rem',
            borderRadius: '4px',
            fontSize: '0.85rem',
          }}
        >
          Sair
        </button>
      </div>
    </header>
  );
}