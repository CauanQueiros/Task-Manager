import { Component, OnDestroy, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { FormsModule } from "@angular/forms";
import { TarefaService } from "../../services/tarefa.service";
import { Subscription } from "rxjs";
import {
  TarefaResponse,
  StatusTarefa,
  PrioridadeTarefa,
} from "../../models/tarefa.model";

@Component({
  selector: "app-lista-tarefas",
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: "./lista-tarefas.component.html",
  styleUrls: ["./lista-tarefas.component.css"],
})
export class ListaTarefasComponent implements OnInit, OnDestroy {
  tarefas: TarefaResponse[] = [];
  loading: boolean = false;
  erro: string = "";
  busca = "";
  filtroStatus = "TODOS";
  filtroPrioridade: "TODAS" | PrioridadeTarefa = "TODAS";
  private requisicaoAtual?: Subscription;
  private atualizandoIds = new Set<number>();
  readonly StatusTarefa = StatusTarefa;

  get temFiltroAtivo(): boolean {
    return (
      !!this.busca.trim() ||
      this.filtroStatus !== "TODOS" ||
      this.filtroPrioridade !== "TODAS"
    );
  }

  /** Minúsculas e sem acentos, para "media" encontrar "Média" */
  private normalizar(texto: string): string {
    return (texto || "")
      .toLocaleLowerCase("pt-BR")
      .normalize("NFD")
      .replace(/[\u0300-\u036f]/g, "");
  }

  get tarefasFiltradas(): TarefaResponse[] {
    const termo = this.normalizar(this.busca.trim());
    const termoId = termo.replace(/^#/, "");
    return this.tarefas.filter((tarefa) => {
      const correspondeBusca =
        !termo ||
        this.normalizar(tarefa.titulo).includes(termo) ||
        this.normalizar(tarefa.descricao).includes(termo) ||
        String(tarefa.id) === termoId ||
        this.normalizar(this.getPrioridadeText(tarefa.prioridade)).includes(
          termo,
        );
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
    const precisaRecarregar = this.filtroPrioridade !== "TODAS";
    this.busca = "";
    this.filtroStatus = "TODOS";
    this.filtroPrioridade = "TODAS";
    if (precisaRecarregar) {
      this.carregarTarefas();
    }
  }

  /** O filtro de prioridade é feito no backend, então precisa buscar de novo */
  mudarPrioridade(valor: "TODAS" | PrioridadeTarefa): void {
    this.filtroPrioridade = valor;
    this.carregarTarefas();
  }

  constructor(private tarefaService: TarefaService) {}

  ngOnInit(): void {
    this.carregarTarefas();
  }

  ngOnDestroy(): void {
    this.requisicaoAtual?.unsubscribe();
  }

  carregarTarefas(): void {
    this.loading = true;
    this.erro = "";

    // Cancela a busca anterior para uma resposta antiga não sobrescrever a mais recente
    this.requisicaoAtual?.unsubscribe();

    const prioridade =
      this.filtroPrioridade === "TODAS" ? undefined : this.filtroPrioridade;

    this.requisicaoAtual = this.tarefaService
      .listarTarefas(prioridade)
      .subscribe({
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

  estaAtualizando(id: number): boolean {
    return this.atualizandoIds.has(id);
  }

  /** Iniciar, concluir, pausar ou reabrir: o backend valida a transição e preenche as datas */
  mudarStatus(tarefa: TarefaResponse, novoStatus: StatusTarefa): void {
    if (this.atualizandoIds.has(tarefa.id)) {
      return;
    }
    this.atualizandoIds.add(tarefa.id);
    this.erro = "";

    this.tarefaService.alterarStatus(tarefa.id, novoStatus).subscribe({
      next: (atualizada) => {
        this.tarefas = this.tarefas.map((t) =>
          t.id === atualizada.id ? atualizada : t,
        );
        this.atualizandoIds.delete(tarefa.id);
      },
      error: (err) => {
        this.erro = err.error?.message || "Erro ao alterar o status da tarefa";
        console.error("Erro:", err);
        this.atualizandoIds.delete(tarefa.id);
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

  getPrioridadeClass(prioridade: PrioridadeTarefa): string {
    switch (prioridade) {
      case PrioridadeTarefa.ALTA:
        return "prioridade-alta";
      case PrioridadeTarefa.BAIXA:
        return "prioridade-baixa";
      default:
        return "prioridade-media";
    }
  }

  getPrioridadeText(prioridade: PrioridadeTarefa): string {
    switch (prioridade) {
      case PrioridadeTarefa.ALTA:
        return "Alta";
      case PrioridadeTarefa.BAIXA:
        return "Baixa";
      default:
        return "Média";
    }
  }

  getConclusaoClass(concluida: boolean): string {
    return concluida ? "concluida" : "";
  }
}
