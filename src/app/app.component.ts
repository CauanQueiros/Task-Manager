import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule, Router } from '@angular/router';
import { ListaTarefasComponent } from './components/lista-tarefas/lista-tarefas.component';
import { CadastroTarefaComponent } from './components/cadastro-tarefa/cadastro-tarefa.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, RouterModule, ListaTarefasComponent, CadastroTarefaComponent],
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  paginaAtual: 'lista' | 'cadastro' = 'lista';

  constructor(private router: Router) {}

  irPara(pagina: 'lista' | 'cadastro'): void {
    this.paginaAtual = pagina;
  }
}
