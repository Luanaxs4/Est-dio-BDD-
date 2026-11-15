import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { ModalAviso } from '../components/ModalAviso';

interface Projeto {
  id: number;
  nome: string;
  descricao?: string;
  contextoDominio?: string;
}

export function ProjetosPage() {
  const [projetos, setProjetos] = useState<Projeto[]>([]);
  const [carregando, setCarregando] = useState(true);
  const [modalAberto, setModalAberto] = useState(false);
  const [erro, setErro] = useState<string | null>(null);

  const [nome, setNome] = useState('');
  const [descricao, setDescricao] = useState('');
  const [contextoDominio, setContextoDominio] = useState('');
  const [salvando, setSalvando] = useState(false);

  const navigate = useNavigate();

  const API_URL = 'http://localhost:8080/api/projetos';

  const carregarProjetos = async () => {
    setCarregando(true);
    try {
      const response = await fetch(API_URL);
      if (response.ok) {
        const data = await response.json();
        setProjetos(data);
      }
    } catch (error) {
      console.error('Erro ao buscar projetos:', error);
      setErro('Erro ao carregar projetos. Verifique se o backend está rodando.');
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    carregarProjetos();
  }, []);

  const fecharModal = () => {
    setModalAberto(false);
    setNome('');
    setDescricao('');
    setContextoDominio('');
  };

  const handleCriarProjeto = async (e: React.FormEvent) => {
    e.preventDefault();
    const nomeLimpo = nome.trim();
    if (!nomeLimpo) return;

    // Verifica se já existe um projeto com o mesmo nome antes de chamar a API
    const nomeDuplicado = projetos.some(
      (projeto) => projeto.nome.trim().toLowerCase() === nomeLimpo.toLowerCase()
    );

    if (nomeDuplicado) {
      setErro('Já existe um projeto cadastrado com este nome. Escolha outro nome.');
      return;
    }

    setSalvando(true);
    try {
      const response = await fetch(API_URL, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({
          nome: nomeLimpo,
          descricao,
          contextoDominio,
        }),
      });

      if (response.status === 409) {
        setErro('Já existe um projeto cadastrado com este nome. Escolha outro nome.');
        return;
      }

      if (!response.ok) {
        throw new Error('Falha ao criar projeto');
      }

      fecharModal();
      await carregarProjetos();
    } catch (error) {
      console.error(error);
      setErro('Erro ao salvar o projeto. Verifique o console e se o backend está rodando.');
    } finally {
      setSalvando(false);
    }
  };

  return (
    <div style={{ padding: '2rem', maxWidth: '1100px', margin: '0 auto' }}>
      <ModalAviso
        aberto={erro !== null}
        titulo="Erro"
        mensagem={erro ?? ''}
        onFechar={() => setErro(null)}
      />

      {/* Cabeçalho com Título + Botão */}
      <div
        style={{
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          marginBottom: '2rem',
        }}
      >
        <h1 style={{ margin: 0 }}>Lista de Projetos</h1>

        <button
          onClick={() => setModalAberto(true)}
          style={{
            backgroundColor: '#1e293b',
            color: '#ffffff',
            border: 'none',
            padding: '0.65rem 1.25rem',
            borderRadius: '6px',
            fontWeight: 'bold',
            cursor: 'pointer',
            fontSize: '0.95rem',
          }}
        >
          + Novo Projeto
        </button>
      </div>

      {/* Lista de Cards dos Projetos */}
      {carregando ? (
        <p>Carregando projetos...</p>
      ) : projetos.length === 0 ? (
        <div
          style={{
            padding: '3rem',
            textAlign: 'center',
            border: '1px dashed #cbd5e1',
            borderRadius: '8px',
            color: '#64748b',
          }}
        >
          <p style={{ marginBottom: '1rem' }}>Nenhum projeto cadastrado ainda.</p>
          <button
            onClick={() => setModalAberto(true)}
            style={{
              backgroundColor: '#1e293b',
              color: '#fff',
              border: 'none',
              padding: '0.5rem 1rem',
              borderRadius: '6px',
              cursor: 'pointer',
            }}
          >
            Criar primeiro projeto
          </button>
        </div>
      ) : (
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
            gap: '1.25rem',
          }}
        >
          {projetos.map((projeto) => (
            <div
              key={projeto.id}
              onClick={() => navigate(`/projetos/${projeto.id}`)}
              style={{
                border: '1px solid #e2e8f0',
                borderRadius: '8px',
                padding: '1.25rem',
                backgroundColor: '#ffffff',
                boxShadow: '0 1px 3px rgba(0,0,0,0.08)',
                cursor: 'pointer',
              }}
            >
              <h3 style={{ margin: '0 0 0.5rem 0', color: '#0f172a' }}>{projeto.nome}</h3>
              <p style={{ margin: 0, color: '#475569', fontSize: '0.9rem' }}>
                {projeto.descricao || 'Sem descrição'}
              </p>
            </div>
          ))}
        </div>
      )}

      {/* Modal de Novo Projeto */}
      {modalAberto && (
        <div
          style={{
            position: 'fixed',
            inset: 0,
            backgroundColor: 'rgba(0, 0, 0, 0.5)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'center',
            zIndex: 1000,
          }}
        >
          <div
            style={{
              backgroundColor: '#ffffff',
              padding: '1.75rem',
              borderRadius: '8px',
              width: '100%',
              maxWidth: '480px',
              boxShadow: '0 10px 25px rgba(0,0,0,0.2)',
            }}
          >
            <h2 style={{ marginTop: 0, marginBottom: '1.25rem' }}>Novo Projeto</h2>

            <form onSubmit={handleCriarProjeto}>
              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', marginBottom: '0.4rem', fontWeight: 600 }}>
                  Nome do Projeto *
                </label>
                <input
                  type="text"
                  required
                  value={nome}
                  onChange={(e) => setNome(e.target.value)}
                  placeholder="Ex: Estúdio BDD"
                  style={{
                    width: '100%',
                    padding: '0.6rem',
                    borderRadius: '4px',
                    border: '1px solid #cbd5e1',
                    boxSizing: 'border-box',
                  }}
                />
              </div>

              <div style={{ marginBottom: '1rem' }}>
                <label style={{ display: 'block', marginBottom: '0.4rem', fontWeight: 600 }}>
                  Descrição
                </label>
                <textarea
                  rows={2}
                  value={descricao}
                  onChange={(e) => setDescricao(e.target.value)}
                  placeholder="Objetivo do projeto..."
                  style={{
                    width: '100%',
                    padding: '0.6rem',
                    borderRadius: '4px',
                    border: '1px solid #cbd5e1',
                    boxSizing: 'border-box',
                  }}
                />
              </div>

              <div style={{ marginBottom: '1.5rem' }}>
                <label style={{ display: 'block', marginBottom: '0.4rem', fontWeight: 600 }}>
                  Contexto do Domínio (glossário e informações do negócio)
                </label>
                <textarea
                  rows={4}
                  value={contextoDominio}
                  onChange={(e) => setContextoDominio(e.target.value)}
                  placeholder="Termos técnicos, regras gerais do negócio, glossário..."
                  style={{
                    width: '100%',
                    padding: '0.6rem',
                    borderRadius: '4px',
                    border: '1px solid #cbd5e1',
                    boxSizing: 'border-box',
                  }}
                />
              </div>

              <div style={{ display: 'flex', justifyContent: 'flex-end', gap: '0.75rem' }}>
                <button
                  type="button"
                  onClick={fecharModal}
                  style={{
                    padding: '0.55rem 1rem',
                    borderRadius: '4px',
                    border: '1px solid #cbd5e1',
                    backgroundColor: '#f8fafc',
                    cursor: 'pointer',
                  }}
                >
                  Cancelar
                </button>
                <button
                  type="submit"
                  disabled={salvando}
                  style={{
                    padding: '0.55rem 1.2rem',
                    borderRadius: '4px',
                    border: 'none',
                    backgroundColor: '#1e293b',
                    color: '#ffffff',
                    fontWeight: 'bold',
                    cursor: 'pointer',
                  }}
                >
                  {salvando ? 'Salvando...' : 'Salvar Projeto'}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}