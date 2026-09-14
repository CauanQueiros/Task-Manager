import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Tarefa, TarefaResponse } from '../models/tarefa.model';
 
@Injectable({
  providedIn: 'root'
})
export class TarefaService {
  private apiUrl = 'http://localhost:8080/api/tarefa';
 
  constructor(private http: HttpClient) { }
 
  listarTarefas(): Observable<TarefaResponse[]> {
    return this.http.get<TarefaResponse[]>(`${this.apiUrl}/listar`);
  }
 
  criarTarefa(tarefa: Tarefa): Observable<TarefaResponse> {
    return this.http.post<TarefaResponse>(`${this.apiUrl}/cadastrar`, tarefa);
  }

  deletarTarefa(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deletar/${id}`);
  }
}