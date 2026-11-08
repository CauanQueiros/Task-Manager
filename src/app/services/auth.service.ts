import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Router } from "@angular/router";
import { Observable, tap } from "rxjs";
import { API_URL } from "../config/api.config";
import {
  AuthResponse,
  LoginRequest,
  RegistroRequest,
  Sessao,
} from "../models/auth.model";

const CHAVE_SESSAO = "taskflow.sessao";

@Injectable({
  providedIn: "root",
})
export class AuthService {
  private apiUrl = `${API_URL}/auth`;
  private sessao: Sessao | null = this.carregarSessao();

  constructor(
    private http: HttpClient,
    private router: Router,
  ) {}

  login(dados: LoginRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/login`, dados)
      .pipe(tap((resposta) => this.salvarSessao(resposta)));
  }

  registrar(dados: RegistroRequest): Observable<AuthResponse> {
    return this.http
      .post<AuthResponse>(`${this.apiUrl}/registrar`, dados)
      .pipe(tap((resposta) => this.salvarSessao(resposta)));
  }

  logout(motivo?: "expirada"): void {
    this.sessao = null;
    this.guardar(null);
    this.router.navigate(["/login"], {
      queryParams: motivo ? { motivo } : {},
    });
  }

  /** Token válido (não expirado) ou null. */
  get token(): string | null {
    return this.estaAutenticado() ? this.sessao!.token : null;
  }

  get usuario(): { nome: string; email: string } | null {
    return this.estaAutenticado()
      ? { nome: this.sessao!.nome, email: this.sessao!.email }
      : null;
  }

  estaAutenticado(): boolean {
    if (!this.sessao) {
      return false;
    }
    const expiracao = Date.parse(this.sessao.expiraEm);
    if (Number.isNaN(expiracao) || expiracao <= Date.now()) {
      this.sessao = null;
      this.guardar(null);
      return false;
    }
    return true;
  }

  private salvarSessao(resposta: AuthResponse): void {
    this.sessao = {
      token: resposta.token,
      expiraEm: resposta.expiraEm,
      nome: resposta.nome,
      email: resposta.email,
    };
    this.guardar(this.sessao);
  }

  private carregarSessao(): Sessao | null {
    try {
      const bruto = localStorage.getItem(CHAVE_SESSAO);
      return bruto ? (JSON.parse(bruto) as Sessao) : null;
    } catch {
      return null;
    }
  }

  private guardar(sessao: Sessao | null): void {
    try {
      if (sessao) {
        localStorage.setItem(CHAVE_SESSAO, JSON.stringify(sessao));
      } else {
        localStorage.removeItem(CHAVE_SESSAO);
      }
    } catch {
      // armazenamento indisponível (modo privado, por exemplo): a sessão vale só em memória
    }
  }
}
