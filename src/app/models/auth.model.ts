export interface LoginRequest {
  email: string;
  senha: string;
}

export interface RegistroRequest {
  nome: string;
  email: string;
  senha: string;
}

export interface AuthResponse {
  token: string;
  tipo: string;
  expiraEm: string;
  nome: string;
  email: string;
}

export interface Sessao {
  token: string;
  expiraEm: string;
  nome: string;
  email: string;
}
