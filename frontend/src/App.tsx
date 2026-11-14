import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom';
import { Header } from './components/Header';
import { UsuarioProvider, useUsuario } from './contexts/usuarioContext';
import { EstudioHistoriaPage } from './pages/EstudioHistoriasPage';
import { LoginPage } from './pages/LoginPage';
import { ProjetoDetalhesPage } from './pages/ProjetoDetalhesPage';
import { ProjetosPage } from './pages/ProjetosPage';
import type { ReactNode } from 'react';

// Protege as rotas internas: se não houver usuário logado, manda para /login
function RotaProtegida({ children }: { children: ReactNode }) {
  const { usuarioAtual, carregando } = useUsuario();

  if (carregando) {
    return <p style={{ textAlign: 'center', marginTop: '3rem' }}>Carregando...</p>;
  }

  if (!usuarioAtual) {
    return <Navigate to="/login" replace />;
  }

  return <>{children}</>;
}

export function App() {
  return (
    <UsuarioProvider>
      <BrowserRouter>
        <Header />
        <main style={{ maxWidth: '100%', margin: '0 auto', padding: '0 1.5rem 3rem' }}>
          <Routes>
            <Route path="/login" element={<LoginPage />} />

            <Route
              path="/projetos"
              element={
                <RotaProtegida>
                  <ProjetosPage />
                </RotaProtegida>
              }
            />

            <Route
              path="/projetos/:projetoId"
              element={
                <RotaProtegida>
                  <ProjetoDetalhesPage />
                </RotaProtegida>
              }
            />

            <Route
              path="/projetos/:projetoId/historias/:historiaId"
              element={
                <RotaProtegida>
                  <EstudioHistoriaPage />
                </RotaProtegida>
              }
            />

            <Route path="*" element={<Navigate to="/projetos" replace />} />
          </Routes>
        </main>
      </BrowserRouter>
    </UsuarioProvider>
  );
}

export default App;