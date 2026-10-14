import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PrioridadeTarefa, Tarefa, TarefaResponse } from '../models/tarefa.model';

@Injectable({
  providedIn: 'root'
})
export class TarefaService {
  private apiUrl = 'http://localhost:8080/api/tarefa';

  constructor(private http: HttpClient) { }

  /**
   * Lista as tarefas. Se a prioridade for informada, o filtro é feito no backend (banco).
   */
  listarTarefas(prioridade?: PrioridadeTarefa): Observable<TarefaResponse[]> {
    let params = new HttpParams();
    if (prioridade) {
      params = params.set('prioridade', prioridade);
    }
    return this.http.get<TarefaResponse[]>(`${this.apiUrl}/listar`, { params });
  }

  criarTarefa(tarefa: Tarefa): Observable<TarefaResponse> {
    return this.http.post<TarefaResponse>(`${this.apiUrl}/cadastrar`, tarefa);
  }

  deletarTarefa(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deletar/${id}`);
  }
}
