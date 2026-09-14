export enum StatusTarefa {
  FAZER = 'FAZER',
  EM_PROGRESSO = 'EM_PROGRESSO',
  CONCLUIDA = 'CONCLUIDA'
}
 
export interface Tarefa {
  id?: number;
  titulo: string;
  descricao: string;
  status: StatusTarefa;
  dataDeInicio?: string;
  dataDeFim?: string;
  concluida: boolean;
}
 
export interface TarefaResponse {
  id: number;
  titulo: string;
  descricao: string;
  status: StatusTarefa;
  concluida: boolean;
}