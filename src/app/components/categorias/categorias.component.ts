import { Component, OnInit } from "@angular/core";
import { CommonModule } from "@angular/common";
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from "@angular/forms";
import { RouterLink } from "@angular/router";
import { CategoriaService } from "../../services/categoria.service";
import { Categoria } from "../../models/categoria.model";

@Component({
  selector: "app-categorias",
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, RouterLink],
  templateUrl: "./categorias.component.html",
  styleUrls: ["./categorias.component.css"],
})
export class CategoriasComponent implements OnInit {
  readonly coresSugeridas = [
    "#7062e4",
    "#3886cf",
    "#229b68",
    "#df8738",
    "#cf5151",
    "#c9529a",
    "#5a6379",
  ];

  formulario: FormGroup;
  categorias: Categoria[] = [];
  loading = false;
  salvando = false;
  erro = "";
  sucesso = "";

  constructor(
    private fb: FormBuilder,
    private categoriaService: CategoriaService,
  ) {
    this.formulario = this.fb.group({
      nome: ["", [Validators.required, Validators.maxLength(60)]],
      cor: [this.coresSugeridas[0], [Validators.required]],
    });
  }

  ngOnInit(): void {
    this.carregarCategorias();
  }

  carregarCategorias(): void {
    this.loading = true;
    this.categoriaService.listarCategorias().subscribe({
      next: (dados) => {
        this.categorias = dados;
        this.loading = false;
      },
      error: (err) => {
        this.erro = "Erro ao carregar categorias";
        console.error("Erro:", err);
        this.loading = false;
      },
    });
  }

  escolherCor(cor: string): void {
    this.formulario.patchValue({ cor });
  }

  onSubmit(): void {
    const nome = (this.formulario.value.nome || "").trim();
    if (!nome || this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.erro = "Informe um nome de até 60 caracteres.";
      return;
    }

    this.salvando = true;
    this.erro = "";
    this.sucesso = "";

    this.categoriaService
      .criarCategoria({ nome, cor: this.formulario.value.cor })
      .subscribe({
        next: (criada) => {
          this.categorias = [...this.categorias, criada].sort((a, b) =>
            a.nome.localeCompare(b.nome, "pt-BR"),
          );
          this.formulario.patchValue({ nome: "" });
          this.formulario.markAsUntouched();
          this.sucesso = `Categoria "${criada.nome}" criada.`;
          this.salvando = false;
        },
        error: (err) => {
          this.erro = err.error?.message || "Erro ao criar categoria";
          console.error("Erro:", err);
          this.salvando = false;
        },
      });
  }

  deletarCategoria(categoria: Categoria): void {
    if (
      !confirm(
        `Excluir a categoria "${categoria.nome}"? As tarefas dela continuam existindo, só ficam sem categoria.`,
      )
    ) {
      return;
    }
    this.erro = "";
    this.sucesso = "";

    this.categoriaService.deletarCategoria(categoria.id).subscribe({
      next: () => {
        this.categorias = this.categorias.filter((c) => c.id !== categoria.id);
        this.sucesso = `Categoria "${categoria.nome}" excluída.`;
      },
      error: (err) => {
        this.erro = err.error?.message || "Erro ao excluir categoria";
        console.error("Erro:", err);
      },
    });
  }
}
