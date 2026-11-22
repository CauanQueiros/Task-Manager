import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";
import { API_URL } from "../config/api.config";
import { Categoria, CategoriaRequest } from "../models/categoria.model";

@Injectable({
  providedIn: "root",
})
export class CategoriaService {
  private apiUrl = `${API_URL}/categoria`;

  constructor(private http: HttpClient) {}

  listarCategorias(): Observable<Categoria[]> {
    return this.http.get<Categoria[]>(`${this.apiUrl}/listar`);
  }

  criarCategoria(categoria: CategoriaRequest): Observable<Categoria> {
    return this.http.post<Categoria>(`${this.apiUrl}/cadastrar`, categoria);
  }

  deletarCategoria(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/deletar/${id}`);
  }
}