import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { TarefaService } from "../../services/tarefa.service";
import { TarefaResponse, StatusTarefa } from "../../models/tarefa.model";

@Component({
  selector: "app-lista-tarefas",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./lista-tarefas.component.html",
  styleUrls: ["./lista-tarefas.component.css"],
})
export class ListaTarefasComponent implements OnInit {
  tarefas: TarefaResponse[] = [];
  loading: boolean = false;
  erro: string = "";
  busca = "";
  filtroStatus = "TODOS";

  get tarefasFiltradas(): TarefaResponse[] {
    const termo = this.busca.trim().toLocaleLowerCase("pt-BR");
    return this.tarefas.filter((tarefa) => {
      const correspondeBusca =
        !termo ||
        tarefa.titulo.toLocaleLowerCase("pt-BR").includes(termo) ||
        (tarefa.descricao || "").toLocaleLowerCase("pt-BR").includes(termo);
      const correspondeStatus =
        this.filtroStatus === "TODOS" || tarefa.status === this.filtroStatus;
      return correspondeBusca && correspondeStatus;
    });
  }

  contarPorStatus(status: string): number {
    return this.tarefas.filter((tarefa) => tarefa.status === status).length;
  }

  get percentualConcluido(): number {
    return this.tarefas.length
      ? Math.round(
          (this.contarPorStatus("CONCLUIDA") / this.tarefas.length) * 100,
        )
      : 0;
  }

  limparFiltros(): void {
    this.busca = "";
    this.filtroStatus = "TODOS";
  }

  constructor(private tarefaService: TarefaService) {}

  ngOnInit(): void {
    this.carregarTarefas();
  }

  carregarTarefas(): void {
    this.loading = true;
    this.erro = "";

    this.tarefaService.listarTarefas().subscribe({
      next: (data) => {
        this.tarefas = data;
        this.loading = false;
      },
      error: (err) => {
        this.erro = "Erro ao carregar tarefas";
        console.error("Erro:", err);
        this.loading = false;
      },
    });
  }

  deletarTarefa(id: number): void {
    if (!confirm("Tem certeza que deseja excluir esta tarefa?")) {
      return;
    }

    this.tarefaService.deletarTarefa(id).subscribe({
      next: () => {
        this.tarefas = this.tarefas.filter((t) => t.id !== id);
      },
      error: (err) => {
        this.erro = "Erro ao excluir tarefa";
        console.error("Erro:", err);
      },
    });
  }

  getStatusClass(status: StatusTarefa): string {
    switch (status) {
      case StatusTarefa.FAZER:
        return "status-fazer";
      case StatusTarefa.EM_PROGRESSO:
        return "status-em-progresso";
      case StatusTarefa.CONCLUIDA:
        return "status-concluida";
      default:
        return "";
    }
  }

  getStatusText(status: StatusTarefa): string {
    switch (status) {
      case StatusTarefa.FAZER:
        return "A Fazer";
      case StatusTarefa.EM_PROGRESSO:
        return "Em Progresso";
      case StatusTarefa.CONCLUIDA:
        return "Concluída";
      default:
        return status;
    }
  }

  getConclusaoClass(concluida: boolean): string {
    return concluida ? "concluida" : "";
  }
}
