import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink, RouterLinkActive, RouterOutlet } from "@angular/router";
import { AuthService } from "./services/auth.service";

@Component({
  selector: "app-root",
  standalone: true,
  imports: [CommonModule, RouterOutlet, RouterLink, RouterLinkActive],
  templateUrl: "./app.component.html",
  styleUrls: ["./app.component.css"],
})
export class AppComponent {
  constructor(public auth: AuthService) {}

  get inicialDoUsuario(): string {
    return (this.auth.usuario?.nome || "?").trim().charAt(0).toUpperCase();
  }

  sair(): void {
    this.auth.logout();
  }
}
