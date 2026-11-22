import { Injectable } from "@angular/core";
import { HttpClient, HttpParams } from "@angular/common/http";
import { Observable } from "rxjs";
import { API_URL } from "../config/api.config";
import {
  PrioridadeTarefa,
  StatusTarefa,
  Tarefa,
  TarefaResponse,
} from "../models/tarefa.model";

@Injectable({
  providedIn: "root",
})
export class TarefaService {
  private apiUrl = `${API_URL}/tarefa`;

  constructor(private http: HttpClient) {}

  /**
   * Lista as tarefas. Prioridade e categoria são filtros opcionais, feitos no backend (banco)
   * e que podem ser combinados.
   */
  listarTarefas(
    prioridade?: PrioridadeTarefa,
    categoriaId?: number,
  ): Observable<TarefaResponse[]> {
    let params = new HttpParams();
    if (prioridade) {
      params = params.set("prioridade", prioridade);
    }
    if (categoriaId !== undefined) {
      params = params.set("categoriaId", categoriaId);
    }
    return this.http.get<TarefaResponse[]>(`${this.apiUrl}/listar`, { params });
  }

  criarTarefa(tarefa: Tarefa): Observable<TarefaResponse> {
    return this.http.post<TarefaResponse>(`${this.apiUrl}/cadastrar`, tarefa);
  }

  alterarStatus(id: number, status: StatusTarefa): Observable<TarefaResponse> {
    return this.http.patch<TarefaResponse>(`${this.apiUrl}/${id}/status`, {
      status,
    });
  }

  deletarTarefa(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deletar/${id}`);
  }
}
