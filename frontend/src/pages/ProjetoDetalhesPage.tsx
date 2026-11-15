import { useEffect, useState, type SubmitEvent } from 'react';
import { Link, useParams } from 'react-router-dom';
import { ModalAviso } from '../components/ModalAviso';
import { historiaService } from '../services/historiaService';
import { projetoService } from '../services/projetoService';
import { extrairMensagemErro } from '../types/api';
import type { Prioridade } from '../types/historia';
import type { HistoriaResumo } from '../types/historiaResumo';
import type { ProjetoResponse } from '../types/projeto';
import type { ResumoProjeto } from '../types/resumoProjeto';

export function ProjetoDetalhesPage() {
  const { projetoId } = useParams();
  const idProjeto = Number(projetoId);

  const [projeto, setProjeto] = useState<ProjetoResponse | null>(null);
  const [historias, setHistorias] = useState<HistoriaResumo[]>([]);
  const [resumo, setResumo] = useState<ResumoProjeto | null>(null);

  const [abaAtiva, setAbaAtiva] = useState<'historias' | 'resumo'>('historias');
  const [filtroPrioridade, setFiltroPrioridade] = useState<Prioridade | ''>('');
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [sucesso, setSucesso] = useState<string | null>(null);

  // Estados de edição do projeto
  const [editandoProjeto, setEditandoProjeto] = useState(false);
  const [nomeProjeto, setNomeProjeto] = useState('');
  const [descricaoProjeto, setDescricaoProjeto] = useState('');
  const [contextoDominioProjeto, setContextoDominioProjeto] = useState('');
  const [salvandoProjeto, setSalvandoProjeto] = useState(false);

  // Estados do formulário de nova história
  const [mostrarFormHistoria, setMostrarFormHistoria] = useState(false);
  const [titulo, setTitulo] = useState('');
  const [papel, setPapel] = useState('');
  const [objetivo, setObjetivo] = useState('');
  const [beneficio, setBeneficio] = useState('');
  const [prioridade, setPrioridade] = useState<Prioridade>('MEDIA');
  const [salvando, setSalvando] = useState(false);

  const carregarDadosProjeto = async () => {
    try {
      setCarregando(true);
      setErro(null);
      const [dadosProjeto, dadosResumo] = await Promise.all([
        projetoService.buscarPorId(idProjeto),
        projetoService.obterResumo(idProjeto),
      ]);
      setProjeto(dadosProjeto);
      setResumo(dadosResumo);

      setNomeProjeto(dadosProjeto.nome);
      setDescricaoProjeto(dadosProjeto.descricao ?? '');
      setContextoDominioProjeto(dadosProjeto.contextoDominio ?? '');
    } catch (err) {
      setErro(extrairMensagemErro(err));
    } finally {
      setCarregando(false);
    }
  };

  const carregarHistorias = async (prio?: Prioridade) => {
    try {
      const lista = await historiaService.listarPorProjeto(idProjeto, prio);
      setHistorias(lista);
    } catch (err) {
      setErro(extrairMensagemErro(err));
    }
  };

  useEffect(() => {
    if (!Number.isNaN(idProjeto)) {
      carregarDadosProjeto();
    }
  }, [idProjeto]);

  useEffect(() => {
    if (!Number.isNaN(idProjeto)) {
      carregarHistorias(filtroPrioridade || undefined);
    }
  }, [idProjeto, filtroPrioridade]);

  const exibirMensagemSucesso = (msg: string) => {
    setSucesso(msg);
    setTimeout(() => setSucesso(null), 3500);
  };

  const handleSalvarProjeto = async (e: SubmitEvent) => {
    e.preventDefault();
    try {
      setSalvandoProjeto(true);
      setErro(null);
      await projetoService.atualizar(idProjeto, {
        nome: nomeProjeto,
        descricao: descricaoProjeto,
        contextoDominio: contextoDominioProjeto,
      });
      setEditandoProjeto(false);
      exibirMensagemSucesso('Projeto atualizado com sucesso!');
      await carregarDadosProjeto();
    } catch (err) {
      setErro(extrairMensagemErro(err));
    } finally {
      setSalvandoProjeto(false);
    }
  };

  const handleCriarHistoria = async (e: SubmitEvent) => {
    e.preventDefault();
    try {
      setSalvando(true);
      setErro(null);
      await historiaService.criar(idProjeto, {
        titulo,
        papel,
        objetivo,
        beneficio,
        prioridade,
      });

      setTitulo('');
      setPapel('');
      setObjetivo('');
      setBeneficio('');
      setPrioridade('MEDIA');
      setMostrarFormHistoria(false);

      await Promise.all([
        carregarHistorias(filtroPrioridade || undefined),
        carregarDadosProjeto(),
      ]);
    } catch (err) {
      setErro(extrairMensagemErro(err));
    } finally {
      setSalvando(false);
    }
  };

  if (carregando && !projeto) {
    return <p>Carregando detalhes do projeto...</p>;
  }

  // Normaliza motivosRejeicaoMaisFrequentes (aceita tanto Array quanto Objeto/Map)
  const motivosRejeicaoLista: { motivo: string; quantidade: number }[] = (() => {
    const raw = resumo?.motivosRejeicaoMaisFrequentes as unknown;
    if (!raw) return [];
    if (Array.isArray(raw)) return raw;
    if (typeof raw === 'object') {
      return Object.entries(raw as Record<string, number>).map(([motivo, quantidade]) => ({
        motivo,
        quantidade: Number(quantidade) || 0,
      }));
    }
    return [];
  })();

  return (
    <div>
      {/* Navegação de volta */}
      <div style={{ marginBottom: '1rem' }}>
        <Link to="/projetos" style={{ color: '#2563eb', textDecoration: 'none', fontSize: '0.9rem', fontWeight: 500 }}>
          ← Voltar para Projetos
        </Link>
      </div>

      <ModalAviso
        aberto={erro !== null}
        titulo="Erro"
        mensagem={erro ?? ''}
        onFechar={() => setErro(null)}
      />

      {sucesso && (
        <div style={{ padding: '0.85rem 1rem', backgroundColor: '#dcfce7', border: '1px solid #86efac', color: '#166534', borderRadius: '8px', marginBottom: '1rem', fontWeight: 500 }}>
          ✓ {sucesso}
        </div>
      )}

      {/* Cabeçalho do Projeto */}
      {projeto && (
        <section className="card" style={{ marginBottom: '1.5rem' }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '1rem', flexWrap: 'wrap' }}>
            <div>
              <h1 style={{ fontSize: '1.6rem', marginBottom: '0.35rem' }}>{projeto.nome}</h1>
              {!editandoProjeto && projeto.descricao && <p style={{ color: '#475569' }}>{projeto.descricao}</p>}
            </div>

            <div style={{ display: 'flex', alignItems: 'center', gap: '0.75rem' }}>
              <small style={{ color: '#64748b' }}>Criado em {projeto.dataCriacao}</small>
              <button
                type="button"
                className="btn-secondary"
                style={{ width: 'auto', padding: '0.4rem 0.85rem', fontSize: '0.85rem' }}
                onClick={() => {
                  if (editandoProjeto) {
                    setNomeProjeto(projeto.nome);
                    setDescricaoProjeto(projeto.descricao ?? '');
                    setContextoDominioProjeto(projeto.contextoDominio ?? '');
                  }
                  setEditandoProjeto(!editandoProjeto);
                }}
              >
                {editandoProjeto ? 'Cancelar' : 'Editar Projeto'}
              </button>
            </div>
          </div>

          {!editandoProjeto ? (
            projeto.contextoDominio && (
              <div style={{ marginTop: '1rem', padding: '0.85rem', backgroundColor: '#f8fafc', borderRadius: '8px', borderLeft: '4px solid #2563eb' }}>
                <strong style={{ display: 'block', fontSize: '0.85rem', color: '#1e40af', marginBottom: '0.25rem' }}>
                  Contexto do Domínio / Glossário
                </strong>
                <p style={{ fontSize: '0.9rem', color: '#334155', whiteSpace: 'pre-wrap' }}>
                  {projeto.contextoDominio}
                </p>
              </div>
            )
          ) : (
            <form onSubmit={handleSalvarProjeto} style={{ display: 'grid', gap: '0.9rem', marginTop: '1rem' }}>
              <div className="form-group">
                <label className="form-label">Nome do Projeto *</label>
                <input
                  type="text"
                  className="form-input"
                  value={nomeProjeto}
                  onChange={(e) => setNomeProjeto(e.target.value)}
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Descrição</label>
                <textarea
                  rows={2}
                  className="form-input"
                  value={descricaoProjeto}
                  onChange={(e) => setDescricaoProjeto(e.target.value)}
                  placeholder="Objetivo do projeto..."
                />
              </div>

              <div className="form-group">
                <label className="form-label">Contexto do Domínio / Glossário</label>
                <textarea
                  rows={4}
                  className="form-input"
                  value={contextoDominioProjeto}
                  onChange={(e) => setContextoDominioProjeto(e.target.value)}
                  placeholder="Termos técnicos, regras gerais do negócio, glossário..."
                />
              </div>

              <div>
                <button type="submit" className="btn-primary" style={{ width: 'auto' }} disabled={salvandoProjeto}>
                  {salvandoProjeto ? 'Salvando...' : 'Salvar Alterações'}
                </button>
              </div>
            </form>
          )}
        </section>
      )}

      {/* Abas: Histórias vs Resumo do Projeto */}
      <div style={{ display: 'flex', gap: '0.5rem', marginBottom: '1.25rem', borderBottom: '1px solid #cbd5e1', paddingBottom: '0.5rem' }}>
        <button
          type="button"
          className={abaAtiva === 'historias' ? 'btn-primary' : 'btn-secondary'}
          style={{ width: 'auto' }}
          onClick={() => setAbaAtiva('historias')}
        >
          Histórias de Usuário ({resumo?.totalHistorias ?? historias.length})
        </button>
        <button
          type="button"
          className={abaAtiva === 'resumo' ? 'btn-primary' : 'btn-secondary'}
          style={{ width: 'auto' }}
          onClick={() => {
            setAbaAtiva('resumo');
            carregarDadosProjeto();
          }}
        >
          Resumo do Projeto
        </button>
      </div>

      {/* CONTEÚDO ABA 1: HISTÓRIAS DE USUÁRIO */}
      {abaAtiva === 'historias' && (
        <div>
          {/* Barra de ações e filtro */}
          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '1rem', flexWrap: 'wrap', marginBottom: '1.25rem' }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem' }}>
              <label htmlFor="filtro-prio" className="form-label">Filtrar por prioridade:</label>
              <select
                id="filtro-prio"
                className="form-input"
                style={{ width: 'auto', padding: '0.45rem 0.75rem' }}
                value={filtroPrioridade}
                onChange={(e) => setFiltroPrioridade(e.target.value as Prioridade | '')}
              >
                <option value="">Todas</option>
                <option value="ALTA">Alta</option>
                <option value="MEDIA">Média</option>
                <option value="BAIXA">Baixa</option>
              </select>
            </div>

            <button
              type="button"
              className="btn-primary"
              style={{ width: 'auto' }}
              onClick={() => setMostrarFormHistoria(!mostrarFormHistoria)}
            >
              {mostrarFormHistoria ? 'Cancelar' : '+ Nova História de Usuário'}
            </button>
          </div>

          {/* Formulário de Nova História */}
          {mostrarFormHistoria && (
            <section className="card" style={{ marginBottom: '1.5rem', borderColor: '#93c5fd' }}>
              <h2 style={{ fontSize: '1.1rem', marginBottom: '1rem' }}>Cadastrar História de Usuário</h2>
              <form onSubmit={handleCriarHistoria} style={{ display: 'grid', gap: '0.9rem' }}>
                <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '1rem' }}>
                  <div className="form-group">
                    <label className="form-label">Título (único no projeto) *</label>
                    <input
                      type="text"
                      className="form-input"
                      value={titulo}
                      onChange={(e) => setTitulo(e.target.value)}
                      placeholder="Ex: Empréstimo de livro"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Prioridade *</label>
                    <select
                      className="form-input"
                      value={prioridade}
                      onChange={(e) => setPrioridade(e.target.value as Prioridade)}
                    >
                      <option value="ALTA">Alta</option>
                      <option value="MEDIA">Média</option>
                      <option value="BAIXA">Baixa</option>
                    </select>
                  </div>
                </div>

                <div className="form-group">
                  <label className="form-label">Como... (Papel) *</label>
                  <input
                    type="text"
                    className="form-input"
                    value={papel}
                    onChange={(e) => setPapel(e.target.value)}
                    placeholder="Ex: leitor cadastrado"
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Quero... (Objetivo) *</label>
                  <input
                    type="text"
                    className="form-input"
                    value={objetivo}
                    onChange={(e) => setObjetivo(e.target.value)}
                    placeholder="Ex: reservar um livro disponível"
                    required
                  />
                </div>

                <div className="form-group">
                  <label className="form-label">Para... (Benefício) *</label>
                  <input
                    type="text"
                    className="form-input"
                    value={beneficio}
                    onChange={(e) => setBeneficio(e.target.value)}
                    placeholder="Ex: garantir minha leitura sem ir à biblioteca à toa"
                    required
                  />
                </div>

                <div>
                  <button type="submit" className="btn-primary" style={{ width: 'auto' }} disabled={salvando}>
                    {salvando ? 'Salvando...' : 'Salvar História'}
                  </button>
                </div>
              </form>
            </section>
          )}

          {/* Listagem das Histórias */}
          {historias.length === 0 ? (
            <div className="card" style={{ textAlign: 'center', color: '#64748b' }}>
              Nenhuma história encontrada para este filtro.
            </div>
          ) : (
            <div style={{ display: 'grid', gap: '0.85rem' }}>
              {historias.map((h) => (
                <div
                  key={h.id}
                  className="card"
                  style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', gap: '1rem', padding: '1.15rem 1.5rem' }}
                >
                  <div>
                    <div style={{ display: 'flex', alignItems: 'center', gap: '0.6rem', marginBottom: '0.4rem' }}>
                      <span className={`badge badge-${h.prioridade}`}>{h.prioridade}</span>
                      <h3 style={{ fontSize: '1.05rem' }}>{h.titulo}</h3>
                    </div>
                    <div style={{ display: 'flex', gap: '1.25rem', fontSize: '0.875rem', color: '#475569' }}>
                      <span>📋 <strong>{h.qtdCenarios}</strong> cenário(s)</span>
                      <span style={{ color: h.qtdSugestoesPendentes > 0 ? '#b45309' : '#475569' }}>
                        🤖 <strong>{h.qtdSugestoesPendentes}</strong> sugestão(ões) pendente(s)
                      </span>
                    </div>
                  </div>

                  <Link
                    to={`/projetos/${idProjeto}/historias/${h.id}`}
                    className="btn-primary"
                    style={{ width: 'auto', textDecoration: 'none' }}
                  >
                    Abrir Estúdio →
                  </Link>
                </div>
              ))}
            </div>
          )}
        </div>
      )}

      {/* CONTEÚDO ABA 2: RESUMO DO PROJETO (REGRA 7) */}
      {abaAtiva === 'resumo' && resumo && (
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(260px, 1fr))', gap: '1.25rem' }}>
          <div className="card">
            <h3 style={{ fontSize: '0.9rem', color: '#64748b', marginBottom: '0.5rem' }}>Total de Histórias</h3>
            <p style={{ fontSize: '2.2rem', fontWeight: 700, color: '#0f172a' }}>{resumo.totalHistorias}</p>
          </div>

          <div className="card">
            <h3 style={{ fontSize: '0.9rem', color: '#64748b', marginBottom: '0.75rem' }}>Cenários por Origem</h3>
            <div style={{ display: 'grid', gap: '0.4rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span>Humano:</span>
                <strong>{resumo.cenariosPorOrigem?.HUMANO ?? 0}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span>Sugestão de IA:</span>
                <strong>{resumo.cenariosPorOrigem?.SUGESTAO_IA ?? 0}</strong>
              </div>
            </div>
          </div>

          <div className="card">
            <h3 style={{ fontSize: '0.9rem', color: '#64748b', marginBottom: '0.75rem' }}>Sugestões por Decisão</h3>
            <div style={{ display: 'grid', gap: '0.4rem' }}>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span>Pendentes:</span>
                <strong>{resumo.sugestoesPorDecisao?.PENDENTE ?? 0}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span>Aceitas:</span>
                <strong>{resumo.sugestoesPorDecisao?.ACEITA ?? 0}</strong>
              </div>
              <div style={{ display: 'flex', justifyContent: 'space-between' }}>
                <span>Rejeitadas:</span>
                <strong>{resumo.sugestoesPorDecisao?.REJEITADA ?? 0}</strong>
              </div>
            </div>
          </div>

          <div className="card">
            <h3 style={{ fontSize: '0.9rem', color: '#64748b', marginBottom: '0.75rem' }}>Motivos de Rejeição Frequentes</h3>
            {motivosRejeicaoLista.length === 0 ? (
              <p style={{ fontSize: '0.9rem', color: '#94a3b8' }}>Nenhuma rejeição registrada.</p>
            ) : (
              <div style={{ display: 'grid', gap: '0.4rem' }}>
                {motivosRejeicaoLista.map((item) => (
                  <div key={item.motivo} style={{ display: 'flex', justifyContent: 'space-between' }}>
                    <span>{item.motivo}:</span>
                    <strong>{item.quantidade}</strong>
                  </div>
                ))}
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
}