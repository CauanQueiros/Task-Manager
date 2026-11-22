import { Categoria } from "./categoria.model";

export enum StatusTarefa {
  FAZER = "FAZER",
  EM_PROGRESSO = "EM_PROGRESSO",
  CONCLUIDA = "CONCLUIDA",
}

export enum PrioridadeTarefa {
  BAIXA = "BAIXA",
  MEDIA = "MEDIA",
  ALTA = "ALTA",
}

export interface Tarefa {
  id?: number;
  titulo: string;
  descricao: string;
  status: StatusTarefa;
  prioridade: PrioridadeTarefa;
  dataDeInicio?: string | null;
  dataDeFim?: string | null;
  concluida: boolean;
  categoriaId?: number | null;
}

export interface TarefaResponse {
  id: number;
  titulo: string;
  descricao: string;
  status: StatusTarefa;
  prioridade: PrioridadeTarefa;
  dataDeInicio?: string | null;
  dataDeFim?: string | null;
  concluida: boolean;
  categoria?: Categoria | null;
}
