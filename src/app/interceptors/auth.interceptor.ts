import { HttpErrorResponse, HttpInterceptorFn } from "@angular/common/http";
import { inject } from "@angular/core";
import { catchError, throwError } from "rxjs";
import { API_URL } from "../config/api.config";
import { AuthService } from "../services/auth.service";

/**
 * Anexa o token JWT nas chamadas à API e, se o backend responder 401
 * (token expirado ou inválido), encerra a sessão e volta para o login.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);

  const chamadaDaApi = req.url.startsWith(API_URL);
  const chamadaDeAuth = req.url.startsWith(`${API_URL}/auth/`);
  const token = auth.token;

  const requisicao =
    chamadaDaApi && token
      ? req.clone({ setHeaders: { Authorization: `Bearer ${token}` } })
      : req;

  return next(requisicao).pipe(
    catchError((erro: HttpErrorResponse) => {
      // 401 no login/cadastro é "senha errada", não sessão expirada
      if (erro.status === 401 && chamadaDaApi && !chamadaDeAuth) {
        auth.logout("expirada");
      }
      return throwError(() => erro);
    }),
  );
};
