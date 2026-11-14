import { createContext, useContext, useEffect, useState, type ReactNode } from 'react';
import { CHAVE_USUARIO_STORAGE } from '../services/api';
import { usuarioService } from '../services/usuarioService';
import type { UsuarioResponse } from '../types/usuario';

interface UsuarioContextData {
  usuarios: UsuarioResponse[];
  usuarioAtual: UsuarioResponse | null;
  selecionarUsuario: (usuario: UsuarioResponse) => void;
  sair: () => void;
  recarregarUsuarios: () => Promise<void>;
  obterNomeUsuario: (id: number | null) => string;
  carregando: boolean;
}

const UsuarioContext = createContext<UsuarioContextData>({} as UsuarioContextData);

export function UsuarioProvider({ children }: { children: ReactNode }) {
  const [usuarios, setUsuarios] = useState<UsuarioResponse[]>([]);
  const [usuarioAtual, setUsuarioAtual] = useState<UsuarioResponse | null>(null);
  const [carregando, setCarregando] = useState(true);

  const selecionarUsuario = (usuario: UsuarioResponse) => {
    setUsuarioAtual(usuario);
    localStorage.setItem(CHAVE_USUARIO_STORAGE, String(usuario.id));
  };

  const sair = () => {
    setUsuarioAtual(null);
    localStorage.removeItem(CHAVE_USUARIO_STORAGE);
  };

  const recarregarUsuarios = async () => {
    try {
      setCarregando(true);
      const lista = await usuarioService.listar();
      setUsuarios(lista);

      const idSalvo = localStorage.getItem(CHAVE_USUARIO_STORAGE);
      const encontrado = lista.find((u) => String(u.id) === idSalvo);

      setUsuarioAtual(encontrado ?? null);
    } catch (erro) {
      console.error('Erro ao carregar usuários:', erro);
    } finally {
      setCarregando(false);
    }
  };

  useEffect(() => {
    recarregarUsuarios();
  }, []);

  const obterNomeUsuario = (id: number | null): string => {
    if (id === null) return '-';
    const user = usuarios.find((u) => u.id === id);
    return user ? user.nome : `Usuário #${id}`;
  };

  return (
    <UsuarioContext.Provider
      value={{
        usuarios,
        usuarioAtual,
        selecionarUsuario,
        sair,
        recarregarUsuarios,
        obterNomeUsuario,
        carregando,
      }}
    >
      {children}
    </UsuarioContext.Provider>
  );
}

export function useUsuario() {
  return useContext(UsuarioContext);
}