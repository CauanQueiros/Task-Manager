import { Routes } from "@angular/router";
import { authGuard, visitanteGuard } from "./guards/auth.guard";
import { LoginComponent } from "./components/login/login.component";
import { ListaTarefasComponent } from "./components/lista-tarefas/lista-tarefas.component";
import { CadastroTarefaComponent } from "./components/cadastro-tarefa/cadastro-tarefa.component";

export const routes: Routes = [
  { path: "", pathMatch: "full", redirectTo: "tarefas" },
  { path: "login", component: LoginComponent, canActivate: [visitanteGuard] },
  { path: "tarefas", component: ListaTarefasComponent, canActivate: [authGuard] },
  {
    path: "tarefas/nova",
    component: CadastroTarefaComponent,
    canActivate: [authGuard],
  },
  { path: "**", redirectTo: "tarefas" },
];
