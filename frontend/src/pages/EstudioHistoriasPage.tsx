import { useEffect, useState, type FormEvent } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { ModalFeature } from '../components/ModalFeature';
import { useUsuario } from '../contexts/usuarioContext';
import { cenarioService } from '../services/cenarioService';
import { historiaService, type ArquivoFeature } from '../services/historiaService';
import { sugestaoService } from '../services/sugestaoService';
import { extrairMensagemErro } from '../types/api';
import type { CenarioResponse, OrigemCenario, PassoRequest, TipoPasso } from '../types/cenario';
import type { HistoriaResponse, Prioridade } from '../types/historia';
import type { DecisaoSugestao, MotivoRejeicao, SugestaoResponse } from '../types/sugestao';

export function EstudioHistoriaPage() {
  const { projetoId, historiaId } = useParams();
  const idProjeto = Number(projetoId);
  const idHistoria = Number(historiaId);
  const navigate = useNavigate();
  const { usuarioAtual, obterNomeUsuario } = useUsuario();

  const [historia, setHistoria] = useState<HistoriaResponse | null>(null);
  const [sugestoes, setSugestoes] = useState<SugestaoResponse[]>([]);
  const [filtroDecisao, setFiltroDecisao] = useState<DecisaoSugestao | ''>('');
  const [carregando, setCarregando] = useState(true);
  const [erro, setErro] = useState<string | null>(null);
  const [sucesso, setSucesso] = useState<string | null>(null);

  // Modal .feature
  const [arquivoExportado, setArquivoExportado] = useState<ArquivoFeature | null>(null);

  // Edição da História
  const [editandoHistoria, setEditandoHistoria] = useState(false);
  const [tituloHist, setTituloHist] = useState('');
  const [papel, setPapel] = useState('');
  const [objetivo, setObjetivo] = useState('');
  const [beneficio, setBeneficio] = useState('');
  const [prioridade, setPrioridade] = useState<Prioridade>('MEDIA');

  // Formulário de Cenário (Criar ou Editar)
  const [mostrarFormCenario, setMostrarFormCenario] = useState(false);
  const [cenarioEditandoId, setCenarioEditandoId] = useState<number | null>(null);
  const [tituloCenario, setTituloCenario] = useState('');
  const [origemCenario, setOrigemCenario] = useState<OrigemCenario>('HUMANO');
  const [passosForm, setPassosForm] = useState<PassoRequest[]>([
    { tipo: 'DADO', texto: '' },
    { tipo: 'QUANDO', texto: '' },
    { tipo: 'ENTAO', texto: '' },
  ]);

  // Formulário de Nova Sugestão IA
  const [ferramenta, setFerramenta] = useState('');
  const [promptUtilizado, setPromptUtilizado] = useState('');
  const [conteudoSugestao, setConteudoSugestao] = useState('');

  // Controle de Decisão (Rejeição/Aceitação) de uma Sugestão
  const [decidindoId, setDecidindoId] = useState<number | null>(null);
  const [tipoDecisaoForm, setTipoDecisaoForm] = useState<'ACEITA' | 'REJEITADA'>('ACEITA');
  const [motivoRejeicao, setMotivoRejeicao] = useState<MotivoRejeicao | ''>('');
  const [justificativa, setJustificativa] = useState('');

  const carregarTudo = async () => {
    try {
      setCarregando(true);
      setErro(null);
      const [dadosHistoria, listaSugestoes] = await Promise.all([
        historiaService.buscarPorId(idHistoria),
        sugestaoService.listarPorHistoria(idHistoria, filtroDecisao || undefined),
      ]);
      setHistoria(dadosHistoria);
      setSugestoes(listaSugestoes);

      // Preenche campos de edição da história
      setTituloHist(dadosHistoria.titulo);
      setPapel(dadosHistoria.papel);
      setObjetivo(dadosHistoria.objetivo);
      setBeneficio(dadosHistoria.beneficio);
      setPrioridade(dadosHistoria.prioridade);
    } catch (err) {
      setErro(extrairMensagemErro(err));
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    if (!Number.isNaN(idHistoria)) {
      carregarTudo();
    }
  }, [idHistoria, filtroDecisao]);

  const exibirMensagemSucesso = (msg: string) => {
    setSucesso(msg);
    setTimeout(() => setSucesso(null), 3500);
  };

  // --- AÇÕES DA HISTÓRIA ---
  const handleSalvarHistoria = async (e: FormEvent) => {
    e.preventDefault();
    try {
      setErro(null);
      await historiaService.atualizar(idHistoria, {
        titulo: tituloHist,
        papel,
        objetivo,
        beneficio,
        prioridade,
      });
      setEditandoHistoria(false);
      exibirMensagemSucesso('História atualizada com sucesso!');
      await carregarTudo();
    } catch (err) {
      setErro(extrairMensagemErro(err));
    }
  };

  const handleExcluirHistoria = async () => {
    if (!window.confirm('Tem certeza que deseja excluir esta história e todos os seus cenários?')) return;
    try {
      setErro(null);
      await historiaService.excluir(idHistoria);
      navigate(`/projetos/${idProjeto}`);
    } catch (err) {
      setErro(extrairMensagemErro(err));
    }
  };

  const handleExportarFeature = async () => {
    try {
      setErro(null);
      const arquivo = await historiaService.exportarFeature(idHistoria);
      setArquivoExportado(arquivo);
    } catch (err) {
      setErro(extrairMensagemErro(err));
    }
  };

  // --- AÇÕES DE CENÁRIO E PASSOS ---
  const abrirNovoCenario = () => {
    setCenarioEditandoId(null);
    setTituloCenario('');
    setOrigemCenario('HUMANO');
    setPassosForm([
      { tipo: 'DADO', texto: '' },
      { tipo: 'QUANDO', texto: '' },
      { tipo: 'ENTAO', texto: '' },
    ]);
    setMostrarFormCenario(true);
  };

  const abrirEdicaoCenario = (c: CenarioResponse) => {
    setCenarioEditandoId(c.id);
    setTituloCenario(c.titulo);
    setOrigemCenario(c.origem);
    setPassosForm(c.passos.map((p) => ({ tipo: p.tipo, texto: p.texto })));
    setMostrarFormCenario(true);
  };

  const adicionarPasso = () => {
    setPassosForm([...passosForm, { tipo: 'E', texto: '' }]);
  };

  const removerPasso = (index: number) => {
    setPassosForm(passosForm.filter((_, i) => i !== index));
  };

  const moverPasso = (index: number, direcao: -1 | 1) => {
    const novoIndex = index + direcao;
    if (novoIndex < 0 || novoIndex >= passosForm.length) return;
    const copia = [...passosForm];
    const [item] = copia.splice(index, 1);
    copia.splice(novoIndex, 0, item);
    setPassosForm(copia);
  };

  const atualizarPasso = (index: number, campo: 'tipo' | 'texto', valor: string) => {
    const copia = [...passosForm];
    copia[index] = { ...copia[index], [campo]: valor } as PassoRequest;
    setPassosForm(copia);
  };

  const handleSalvarCenario = async (e: FormEvent) => {
    e.preventDefault();
    try {
      setErro(null);
      const payload = {
        titulo: tituloCenario,
        origem: origemCenario,
        passos: passosForm,
      };

      if (cenarioEditandoId) {
        await cenarioService.atualizar(cenarioEditandoId, payload);
        exibirMensagemSucesso('Cenário atualizado!');
      } else {
        await cenarioService.criar(idHistoria, payload);
        exibirMensagemSucesso('Cenário criado!');
      }

      setMostrarFormCenario(false);
      await carregarTudo();
    } catch (err) {
      setErro(extrairMensagemErro(err));
    }
  };

  const handleExcluirCenario = async (cenarioId: number) => {
    if (!window.confirm('Excluir este cenário?')) return;
    try {
      setErro(null);
      await cenarioService.excluir(cenarioId);
      exibirMensagemSucesso('Cenário excluído.');
      await carregarTudo();
    } catch (err) {
      setErro(extrairMensagemErro(err));
    }
  };

  // --- AÇÕES DE SUGESTÕES DE IA ---
  const handleRegistrarSugestao = async (e: FormEvent) => {
    e.preventDefault();
    try {
      setErro(null);
      await sugestaoService.registrar(idHistoria, {
        ferramenta,
        promptUtilizado: promptUtilizado || undefined,
        conteudo: conteudoSugestao,
      });
      setFerramenta('');
      setPromptUtilizado('');
      setConteudoSugestao('');
      exibirMensagemSucesso('Sugestão de IA registrada como PENDENTE!');
      await carregarTudo();
    } catch (err) {
      setErro(extrairMensagemErro(err));
    }
  };

  const abrirDecisao = (sugestaoId: number, decisao: 'ACEITA' | 'REJEITADA') => {
    setDecidindoId(sugestaoId);
    setTipoDecisaoForm(decisao);
    setMotivoRejeicao('');
    setJustificativa('');
  };

  const handleConfirmarDecisao = async (e: FormEvent) => {
    e.preventDefault();
    if (!decidindoId) return;
    try {
      setErro(null);
      await sugestaoService.decidir(decidindoId, {
        decisao: tipoDecisaoForm,
        motivoRejeicao: tipoDecisaoForm === 'REJEITADA' && motivoRejeicao ? motivoRejeicao : undefined,
        justificativa: justificativa || undefined,
      });
      setDecidindoId(null);
      exibirMensagemSucesso(`Sugestão marcada como ${tipoDecisaoForm}!`);
      await carregarTudo();
    } catch (err) {
      setErro(extrairMensagemErro(err));
    }
  };

  if (carregando && !historia) {
    return <p>Carregando estúdio...</p>;
  }

  const ehAutor = Boolean(historia && usuarioAtual && historia.autorId === usuarioAtual.id);

  return (
    <div>
      {/* Barra superior de navegação e exportação */}
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', flexWrap: 'wrap', gap: '0.75rem' }}>
        <Link to={`/projetos/${idProjeto}`} style={{ color: '#2563eb', textDecoration: 'none', fontSize: '0.9rem', fontWeight: 500 }}>
          ← Voltar para Histórias do Projeto
        </Link>

        <button
          type="button"
          className="btn-primary"
          style={{ width: 'auto', backgroundColor: '#0f172a',color:'#ffffff'}}
          onClick={handleExportarFeature}
        >
          📄 Exportar .feature
        </button>
      </div>

      {/* Alertas de Erro (ProblemDetail) ou Sucesso */}
      {erro && (
        <div style={{ padding: '0.85rem 1rem', backgroundColor: '#fef2e2', border: '1px solid #fecaca', color: '#991b1b', borderRadius: '8px', marginBottom: '1.25rem', fontWeight: 500 }}>
          ⚠️ {erro}
        </div>
      )}

      {sucesso && (
        <div style={{ padding: '0.85rem 1rem', backgroundColor: '#dcfce7', border: '1px solid #86efac', color: '#166534', borderRadius: '8px', marginBottom: '1.25rem', fontWeight: 500 }}>
          ✓ {sucesso}
        </div>
      )}

      {/* LAYOUT DIVIDIDO EM DUAS COLUNAS (Seção 8) */}
      <div className="estudio-grid">
        {/* ================= COLUNA ESQUERDA: HISTÓRIA E CENÁRIOS ================= */}
        <div style={{ display: 'grid', gap: '1.5rem' }}>
          {historia && (
            <section className="card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start', gap: '1rem', marginBottom: '0.75rem' }}>
                <div>
                  <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem', marginBottom: '0.35rem' }}>
                    <span className={`badge badge-${historia.prioridade}`}>{historia.prioridade}</span>
                    <small style={{ color: '#64748b' }}>
                      Autor: <strong>{historia.autorNome}</strong> {ehAutor && '(Você)'}
                    </small>
                  </div>
                  <h1 style={{ fontSize: '1.35rem' }}>{historia.titulo}</h1>
                </div>

                {ehAutor && (
                  <div style={{ display: 'flex', gap: '0.5rem' }}>
                    <button
                      type="button"
                      className="btn-secondary"
                      onClick={() => setEditandoHistoria(!editandoHistoria)}
                    >
                      {editandoHistoria ? 'Cancelar' : 'Editar'}
                    </button>
                    <button type="button" className="btn-danger" onClick={handleExcluirHistoria}>
                      Excluir
                    </button>
                  </div>
                )}
              </div>

              {!editandoHistoria || !ehAutor ? (
                <div style={{ backgroundColor: '#f8fafc', padding: '1rem', borderRadius: '8px', border: '1px solid #e2e8f0', display: 'grid', gap: '0.35rem', fontSize: '0.95rem' }}>
                  <p><strong>Como</strong> {historia.papel}</p>
                  <p><strong>Quero</strong> {historia.objetivo}</p>
                  <p><strong>Para</strong> {historia.beneficio}</p>
                </div>
              ) : (
                <form onSubmit={handleSalvarHistoria} style={{ display: 'grid', gap: '0.75rem', marginTop: '1rem' }}>
                  <div className="form-group">
                    <label className="form-label">Título *</label>
                    <input className="form-input" value={tituloHist} onChange={(e) => setTituloHist(e.target.value)} required />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Prioridade *</label>
                    <select className="form-input" value={prioridade} onChange={(e) => setPrioridade(e.target.value as Prioridade)}>
                      <option value="ALTA">Alta</option>
                      <option value="MEDIA">Média</option>
                      <option value="BAIXA">Baixa</option>
                    </select>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Como... *</label>
                    <input className="form-input" value={papel} onChange={(e) => setPapel(e.target.value)} required />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Quero... *</label>
                    <input className="form-input" value={objetivo} onChange={(e) => setObjetivo(e.target.value)} required />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Para... *</label>
                    <input className="form-input" value={beneficio} onChange={(e) => setBeneficio(e.target.value)} required />
                  </div>
                  <button type="submit" className="btn-primary" style={{ width: 'auto' }}>Salvar Alterações</button>
                </form>
              )}
            </section>
          )}

          {/* Seção de Cenários */}
          <section className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem' }}>
              <h2 style={{ fontSize: '1.15rem' }}>Cenários BDD ({historia?.cenarios.length ?? 0})</h2>
              <button type="button" className="btn-primary" style={{ width: 'auto' }} onClick={abrirNovoCenario}>
                + Novo Cenário
              </button>
            </div>

            {/* Formulário de Cenário e Passos Ordenados */}
            {mostrarFormCenario && (
              <form
                onSubmit={handleSalvarCenario}
                style={{ backgroundColor: '#f8fafc', padding: '1.15rem', borderRadius: '8px', border: '1px solid #93c5fd', marginBottom: '1.25rem', display: 'grid', gap: '0.9rem' }}
              >
                <h3 style={{ fontSize: '1rem' }}>{cenarioEditandoId ? 'Editar Cenário' : 'Novo Cenário'}</h3>

                <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '0.75rem' }}>
                  <div className="form-group">
                    <label className="form-label">Título do Cenário *</label>
                    <input
                      className="form-input"
                      value={tituloCenario}
                      onChange={(e) => setTituloCenario(e.target.value)}
                      placeholder="Ex: Empréstimo realizado com sucesso"
                      required
                    />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Origem</label>
                    <select
                      className="form-input"
                      value={origemCenario}
                      onChange={(e) => setOrigemCenario(e.target.value as OrigemCenario)}
                    >
                      <option value="HUMANO">Humano</option>
                      <option value="SUGESTAO_IA">Sugestão de IA</option>
                    </select>
                  </div>
                </div>

                <div>
                  <label className="form-label" style={{ display: 'block', marginBottom: '0.4rem' }}>
                    Passos (Dado → Quando → Então) *
                  </label>
                  <div style={{ display: 'grid', gap: '0.5rem' }}>
                    {passosForm.map((passo, idx) => (
                      <div key={idx} style={{ display: 'flex', gap: '0.4rem', alignItems: 'center' }}>
                        <select
                          className="form-input"
                          style={{ width: '115px', padding: '0.5rem' }}
                          value={passo.tipo}
                          onChange={(e) => atualizarPasso(idx, 'tipo', e.target.value as TipoPasso)}
                        >
                          <option value="DADO">Dado</option>
                          <option value="QUANDO">Quando</option>
                          <option value="ENTAO">Então</option>
                          <option value="E">E</option>
                          <option value="MAS">Mas</option>
                        </select>

                        <input
                          className="form-input"
                          style={{ padding: '0.5rem' }}
                          value={passo.texto}
                          onChange={(e) => atualizarPasso(idx, 'texto', e.target.value)}
                          placeholder="Descreva o passo..."
                          required
                        />

                        <button type="button" className="btn-secondary" style={{ padding: '0.45rem 0.6rem' }} onClick={() => moverPasso(idx, -1)} title="Subir">↑</button>
                        <button type="button" className="btn-secondary" style={{ padding: '0.45rem 0.6rem' }} onClick={() => moverPasso(idx, 1)} title="Descer">↓</button>
                        <button type="button" className="btn-danger" style={{ padding: '0.45rem 0.6rem' }} onClick={() => removerPasso(idx)} title="Remover">✕</button>
                      </div>
                    ))}
                  </div>
                  <button type="button" className="btn-secondary" style={{ marginTop: '0.6rem', fontSize: '0.85rem' }} onClick={adicionarPasso}>
                    + Adicionar Passo
                  </button>
                </div>

                <div style={{ display: 'flex', gap: '0.5rem', justifyContent: 'flex-end' }}>
                  <button type="button" className="btn-secondary" onClick={() => setMostrarFormCenario(false)}>Cancelar</button>
                  <button type="submit" className="btn-primary" style={{ width: 'auto' }}>Salvar Cenário</button>
                </div>
              </form>
            )}

            {/* Lista de Cenários Cadastrados */}
            {historia?.cenarios.length === 0 ? (
              <p style={{ color: '#64748b', fontSize: '0.9rem' }}>Nenhum cenário cadastrado ainda.</p>
            ) : (
              <div style={{ display: 'grid', gap: '1rem' }}>
                {historia?.cenarios.map((c) => (
                  <div key={c.id} style={{ border: '1px solid #e2e8f0', borderRadius: '8px', padding: '1rem' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.6rem' }}>
                      <div style={{ display: 'flex', alignItems: 'center', gap: '0.5rem' }}>
                        <span className={`badge ${c.origem === 'SUGESTAO_IA' ? 'badge-IA' : 'badge-HUMANO'}`}>
                          {c.origem === 'SUGESTAO_IA' ? '🤖 Sugestão IA' : '👤 Humano'}
                        </span>
                        <strong>{c.titulo}</strong>
                      </div>
                      {ehAutor && (
                        <div style={{ display: 'flex', gap: '0.4rem' }}>
                          <button type="button" className="btn-secondary" style={{ padding: '0.3rem 0.65rem', fontSize: '0.8rem' }} onClick={() => abrirEdicaoCenario(c)}>
                            Editar
                          </button>
                          <button type="button" className="btn-danger" style={{ padding: '0.3rem 0.65rem', fontSize: '0.8rem' }} onClick={() => handleExcluirCenario(c.id)}>
                            Excluir
                          </button>
                        </div>
                      )}
                    </div>

                    <ul style={{ listStyle: 'none', display: 'grid', gap: '0.25rem', fontSize: '0.9rem', paddingLeft: '0.5rem' }}>
                      {c.passos.map((p) => (
                        <li key={p.id}>
                          <strong style={{ color: '#2563eb' }}>{p.tipo}</strong> {p.texto}
                        </li>
                      ))}
                    </ul>
                  </div>
                ))}
              </div>
            )}
          </section>
        </div>

        {/* ================= COLUNA DIREITA: SUGESTÕES DE IA ================= */}
        <div style={{ display: 'grid', gap: '1.5rem' }}>
          {/* Formulário de Registro de Sugestão */}
          <section className="card">
            <h2 style={{ fontSize: '1.1rem', marginBottom: '0.9rem' }}>Registrar Sugestão de IA</h2>
            <form onSubmit={handleRegistrarSugestao} style={{ display: 'grid', gap: '0.8rem' }}>
              <div className="form-group">
                <label className="form-label">Ferramenta de IA usada *</label>
                <input
                  className="form-input"
                  value={ferramenta}
                  onChange={(e) => setFerramenta(e.target.value)}
                  placeholder="Ex: ChatGPT, Claude, Gemini..."
                  required
                />
              </div>

              <div className="form-group">
                <label className="form-label">Prompt utilizado</label>
                <textarea
                  className="form-input"
                  rows={2}
                  value={promptUtilizado}
                  onChange={(e) => setPromptUtilizado(e.target.value)}
                  placeholder="Cole o prompt que você enviou para a IA..."
                />
              </div>

              <div className="form-group">
                <label className="form-label">Conteúdo sugerido pela IA *</label>
                <textarea
                  className="form-input"
                  rows={4}
                  value={conteudoSugestao}
                  onChange={(e) => setConteudoSugestao(e.target.value)}
                  placeholder="Cole os cenários ou melhorias sugeridos..."
                  required
                />
              </div>

              <button type="submit" className="btn-primary">
                Registrar Sugestão
              </button>
            </form>
          </section>

          {/* Lista de Sugestões Registradas */}
          <section className="card">
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '1rem', gap: '0.5rem' }}>
              <h2 style={{ fontSize: '1.1rem' }}>Sugestões ({sugestoes.length})</h2>
              <select
                className="form-input"
                style={{ width: 'auto', padding: '0.35rem 0.6rem', fontSize: '0.85rem' }}
                value={filtroDecisao}
                onChange={(e) => setFiltroDecisao(e.target.value as DecisaoSugestao | '')}
              >
                <option value="">Todas</option>
                <option value="PENDENTE">Pendentes</option>
                <option value="ACEITA">Aceitas</option>
                <option value="REJEITADA">Rejeitadas</option>
              </select>
            </div>

            {sugestoes.length === 0 ? (
              <p style={{ color: '#64748b', fontSize: '0.9rem' }}>Nenhuma sugestão encontrada.</p>
            ) : (
              <div style={{ display: 'grid', gap: '1rem' }}>
                {sugestoes.map((s) => (
                  <div key={s.id} style={{ border: '1px solid #e2e8f0', borderRadius: '8px', padding: '1rem', backgroundColor: s.decisao === 'PENDENTE' ? '#fffbeb' : '#fff' }}>
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '0.5rem' }}>
                      <strong>🤖 {s.ferramenta}</strong>
                      <span className={`badge badge-${s.decisao}`}>{s.decisao}</span>
                    </div>

                    {s.promptUtilizado && (
                      <p style={{ fontSize: '0.8rem', color: '#64748b', marginBottom: '0.5rem' }}>
                        <strong>Prompt:</strong> {s.promptUtilizado}
                      </p>
                    )}

                    <pre style={{ whiteSpace: 'pre-wrap', fontFamily: 'inherit', fontSize: '0.9rem', backgroundColor: '#f8fafc', padding: '0.75rem', borderRadius: '6px', border: '1px solid #e2e8f0', marginBottom: '0.6rem' }}>
                      {s.conteudo}
                    </pre>

                    <small style={{ display: 'block', color: '#64748b', marginBottom: '0.5rem' }}>
                      Registrada por {obterNomeUsuario(s.registradaPorId)} em {s.dataRegistro}
                    </small>

                    {/* Se já foi decidida, exibe o resultado (Regra 4: não pode alterar) */}
                    {s.decisao !== 'PENDENTE' ? (
                      <div style={{ fontSize: '0.85rem', paddingTop: '0.5rem', borderTop: '1px dashed #cbd5e1', color: '#334155' }}>
                        <p>Decidida por <strong>{obterNomeUsuario(s.decididaPorId)}</strong> em {s.dataDecisao}</p>
                        {s.motivoRejeicao && <p><strong>Motivo:</strong> {s.motivoRejeicao}</p>}
                        {s.justificativa && <p><strong>Justificativa:</strong> {s.justificativa}</p>}
                      </div>
                    ) : decidindoId === s.id ? (
                      /* Formulário de Aceitação ou Rejeição */
                      <form onSubmit={handleConfirmarDecisao} style={{ marginTop: '0.75rem', paddingTop: '0.75rem', borderTop: '1px solid #cbd5e1', display: 'grid', gap: '0.6rem' }}>
                        <strong style={{ fontSize: '0.9rem', color: tipoDecisaoForm === 'ACEITA' ? '#166534' : '#991b1b' }}>
                          {tipoDecisaoForm === 'ACEITA' ? 'Confirmar Aceitação' : 'Confirmar Rejeição'}
                        </strong>

                        {tipoDecisaoForm === 'REJEITADA' && (
                          <div className="form-group">
                            <label className="form-label">Motivo da rejeição *</label>
                            <select
                              className="form-input"
                              value={motivoRejeicao}
                              onChange={(e) => setMotivoRejeicao(e.target.value as MotivoRejeicao)}
                            >
                              <option value="">Selecione um motivo...</option>
                              <option value="INCORRETA">Incorreta</option>
                              <option value="GENERICA">Genérica</option>
                              <option value="DUPLICADA">Duplicada</option>
                              <option value="FORA_DO_ESCOPO">Fora do escopo</option>
                              <option value="OUTRO">Outro</option>
                            </select>
                          </div>
                        )}

                        <div className="form-group">
                          <label className="form-label">Justificativa</label>
                          <input
                            className="form-input"
                            value={justificativa}
                            onChange={(e) => setJustificativa(e.target.value)}
                            placeholder="Explique sua decisão..."
                          />
                        </div>

                        <div style={{ display: 'flex', gap: '0.5rem' }}>
                          <button type="submit" className="btn-primary" style={{ width: 'auto', padding: '0.45rem 0.9rem' }}>
                            Confirmar
                          </button>
                          <button type="button" className="btn-secondary" onClick={() => setDecidindoId(null)}>
                            Cancelar
                          </button>
                        </div>
                      </form>
                    ) : (
                      /* Botões iniciais para sugestão PENDENTE */
                      <div style={{ display: 'flex', gap: '0.5rem', marginTop: '0.5rem' }}>
                        <button
                          type="button"
                          className="btn-primary"
                          style={{ width: 'auto', backgroundColor: '#16a34a', padding: '0.4rem 0.85rem', fontSize: '0.85rem' }}
                          onClick={() => abrirDecisao(s.id, 'ACEITA')}
                        >
                          ✓ Aceitar
                        </button>
                        <button
                          type="button"
                          className="btn-danger"
                          onClick={() => abrirDecisao(s.id, 'REJEITADA')}
                        >
                          ✕ Rejeitar
                        </button>
                      </div>
                    )}
                  </div>
                ))}
              </div>
            )}
          </section>
        </div>
      </div>

      {/* Modal de Exportação .feature */}
      {arquivoExportado && (
        <ModalFeature
          arquivo={arquivoExportado}
          onFechar={() => setArquivoExportado(null)}
        />
      )}
    </div>
  );
}