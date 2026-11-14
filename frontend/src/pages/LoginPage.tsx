import { useState, type FormEvent } from 'react';
import { useNavigate } from 'react-router-dom';
import { useUsuario } from '../contexts/usuarioContext';
import { usuarioService } from '../services/usuarioService';
import { extrairMensagemErro } from '../types/api';

export function LoginPage() {
  const { usuarios, selecionarUsuario, recarregarUsuarios } = useUsuario();
  const navigate = useNavigate();

  const [nome, setNome] = useState('');
  const [email, setEmail] = useState('');
  const [salvando, setSalvando] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const handleEntrarOuCadastrar = async (e: FormEvent) => {
    e.preventDefault();

    const nomeLimpo = nome.trim();
    const emailLimpo = email.trim().toLowerCase();

    try {
      setSalvando(true);
      setErro(null);

      // Verifica na lista atual ou consulta a API para garantir dados atualizados
      const listaAtualizada =
        usuarios.length > 0 ? usuarios : await usuarioService.listar();

      const usuarioExistente = listaAtualizada.find(
        (u) => u.email.trim().toLowerCase() === emailLimpo
      );

      if (usuarioExistente) {
        if (usuarioExistente.nome.trim().toLowerCase() !== nomeLimpo.toLowerCase()) {
          setErro('Este e-mail já está cadastrado com outro nome.');
          return;
        }

        selecionarUsuario(usuarioExistente);
        navigate('/projetos');
        return;
      }

      // E-mail não existe -> cadastra novo usuário e entra
      const novoUsuario = await usuarioService.criar({
        nome: nomeLimpo,
        email: emailLimpo,
      });

      await recarregarUsuarios();
      selecionarUsuario(novoUsuario);
      navigate('/projetos');
    } catch (err) {
      setErro(extrairMensagemErro(err));
    } finally {
      setSalvando(false);
    }
  };

  return (
    <div style={{ maxWidth: '480px', margin: '3rem auto', padding: '0 1rem' }}>
      <div style={{ textAlign: 'center', marginBottom: '2rem' }}>
        <h1 style={{ fontSize: '1.8rem', color: '#0f172a' }}>Estúdio BDD</h1>
        <p style={{ color: '#64748b' }}>
          Identifique-se para gerenciar histórias de usuário e cenários Gherkin
        </p>
      </div>

      {erro && (
        <div
          style={{
            padding: '0.75rem 1rem',
            backgroundColor: '#fee2e2',
            color: '#991b1b',
            borderRadius: '6px',
            marginBottom: '1rem',
          }}
        >
          {erro}
        </div>
      )}

      <section
        style={{
          backgroundColor: '#fff',
          padding: '1.5rem',
          borderRadius: '8px',
          boxShadow: '0 1px 3px rgba(0,0,0,0.1)',
        }}
      >
        <h2 style={{ fontSize: '1.1rem', marginBottom: '0.35rem' }}>Entrar ou Cadastrar</h2>
        <p style={{ fontSize: '0.875rem', color: '#64748b', marginBottom: '1.1rem' }}>
          Se o e-mail já possuir cadastro, você entrará automaticamente. Caso contrário, criaremos sua conta.
        </p>

        <form onSubmit={handleEntrarOuCadastrar} style={{ display: 'grid', gap: '0.9rem' }}>
          <div>
            <label style={{ display: 'block', fontWeight: 500, marginBottom: '0.25rem' }}>
              Nome *
            </label>
            <input
              type="text"
              value={nome}
              onChange={(e) => setNome(e.target.value)}
              placeholder="Ex: Maria Silva"
              required
              style={{
                width: '100%',
                padding: '0.5rem',
                borderRadius: '4px',
                border: '1px solid #cbd5e1',
              }}
            />
          </div>

          <div>
            <label style={{ display: 'block', fontWeight: 500, marginBottom: '0.25rem' }}>
              E-mail *
            </label>
            <input
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="Ex: maria@empresa.com"
              required
              style={{
                width: '100%',
                padding: '0.5rem',
                borderRadius: '4px',
                border: '1px solid #cbd5e1',
              }}
            />
          </div>

          <button
            type="submit"
            disabled={salvando}
            style={{
              backgroundColor: '#2563eb',
              color: '#fff',
              border: 'none',
              padding: '0.65rem',
              borderRadius: '6px',
              fontWeight: 500,
              marginTop: '0.25rem',
              cursor: salvando ? 'not-allowed' : 'pointer',
            }}
          >
            {salvando ? 'Entrando...' : 'Entrar / Cadastrar'}
          </button>
        </form>
      </section>
    </div>
  );
}