import { Component } from "@angular/core";
import { CommonModule } from "@angular/common";
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  ValidationErrors,
  Validators,
} from "@angular/forms";
import { ActivatedRoute, Router } from "@angular/router";
import { HttpErrorResponse } from "@angular/common/http";
import { AuthService } from "../../services/auth.service";

type Modo = "entrar" | "criar";

@Component({
  selector: "app-login",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: "./login.component.html",
  styleUrls: ["./login.component.css"],
})
export class LoginComponent {
  modo: Modo = "entrar";
  formulario: FormGroup;
  loading = false;
  erro = "";
  aviso = "";
  mostrarSenha = false;

  constructor(
    private fb: FormBuilder,
    private auth: AuthService,
    private router: Router,
    private route: ActivatedRoute,
  ) {
    this.formulario = this.fb.group(
      {
        nome: [""],
        email: ["", [Validators.required, Validators.email]],
        senha: ["", [Validators.required]],
        confirmarSenha: [""],
      },
      { validators: (grupo: AbstractControl) => this.senhasIguais(grupo) },
    );

    if (this.route.snapshot.queryParamMap.get("motivo") === "expirada") {
      this.aviso = "Sua sessão expirou. Entre novamente para continuar.";
    }
  }

  alternarModo(modo: Modo): void {
    if (modo === this.modo) {
      return;
    }
    this.modo = modo;
    this.erro = "";
    this.aviso = "";
    this.mostrarSenha = false;

    const { nome, senha, confirmarSenha } = this.formulario.controls;
    if (modo === "criar") {
      nome.setValidators([Validators.required, Validators.maxLength(100)]);
      senha.setValidators([
        Validators.required,
        Validators.minLength(6),
        Validators.maxLength(72),
      ]);
      confirmarSenha.setValidators([Validators.required]);
    } else {
      nome.clearValidators();
      senha.setValidators([Validators.required]);
      confirmarSenha.clearValidators();
    }
    this.formulario.reset({ email: this.formulario.value.email });
    this.formulario.updateValueAndValidity();
  }

  onSubmit(): void {
    this.formulario.markAllAsTouched();
    if (this.formulario.invalid) {
      this.erro = "Confira os campos destacados.";
      return;
    }

    this.loading = true;
    this.erro = "";
    this.aviso = "";

    const { nome, email, senha } = this.formulario.value;
    const requisicao =
      this.modo === "entrar"
        ? this.auth.login({ email, senha })
        : this.auth.registrar({ nome, email, senha });

    requisicao.subscribe({
      next: () => {
        this.loading = false;
        this.router.navigateByUrl(this.destinoAposLogin());
      },
      error: (err: HttpErrorResponse) => {
        this.loading = false;
        this.erro = this.mensagemDeErro(err);
      },
    });
  }

  alternarVisibilidadeSenha(): void {
    this.mostrarSenha = !this.mostrarSenha;
  }

  /** Mensagem de erro de um campo (vazia se não houver erro para mostrar). */
  erroCampo(campo: string): string {
    const controle = this.formulario.get(campo);
    if (!controle || !controle.touched) {
      return "";
    }
    if (campo === "confirmarSenha" && this.formulario.hasError("senhasDiferentes")) {
      return "As senhas não conferem";
    }
    if (!controle.errors) {
      return "";
    }
    if (controle.errors["required"]) {
      return "Campo obrigatório";
    }
    if (controle.errors["email"]) {
      return "Informe um e-mail válido";
    }
    if (controle.errors["minlength"]) {
      return `Mínimo de ${controle.errors["minlength"].requiredLength} caracteres`;
    }
    if (controle.errors["maxlength"]) {
      return `Máximo de ${controle.errors["maxlength"].requiredLength} caracteres`;
    }
    return "";
  }

  temErro(campo: string): boolean {
    return this.erroCampo(campo) !== "";
  }

  private senhasIguais(grupo: AbstractControl): ValidationErrors | null {
    if (this.modo !== "criar") {
      return null;
    }
    const senha = grupo.get("senha")?.value;
    const confirmar = grupo.get("confirmarSenha")?.value;
    return senha !== confirmar ? { senhasDiferentes: true } : null;
  }

  /** Volta para a tela que o usuário tentou abrir antes de ser mandado ao login. */
  private destinoAposLogin(): string {
    const destino = this.route.snapshot.queryParamMap.get("returnUrl");
    return destino && destino.startsWith("/") && !destino.startsWith("//")
      ? destino
      : "/tarefas";
  }

  private mensagemDeErro(err: HttpErrorResponse): string {
    if (err.status === 0) {
      return "Não foi possível conectar ao servidor. Verifique se o backend está rodando.";
    }
    return (
      err.error?.message ||
      (this.modo === "entrar"
        ? "Não foi possível entrar. Tente novamente."
        : "Não foi possível criar a conta. Tente novamente.")
    );
  }
}
