import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { TarefaService } from '../../services/tarefa.service';
import { StatusTarefa, Tarefa } from '../../models/tarefa.model';

@Component({
  selector: 'app-cadastro-tarefa',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './cadastro-tarefa.component.html',
  styleUrls: ['./cadastro-tarefa.component.css']
})
export class CadastroTarefaComponent {
  formulario: FormGroup;
  statusOptions = Object.values(StatusTarefa);
  loading: boolean = false;
  sucesso: boolean = false;
  erro: string = '';

  constructor(
    private fb: FormBuilder,
    private tarefaService: TarefaService
  ) {
    this.formulario = this.createForm();
  }

  /**
   * Cria o formulário reativo
   */
  createForm(): FormGroup {
    return this.fb.group({
      titulo: ['', [Validators.required, Validators.minLength(3)]],
      descricao: ['', [Validators.required, Validators.minLength(5)]],
      status: [StatusTarefa.FAZER, Validators.required],
      dataDeInicio: [''],
      dataDeFim: [''],
      concluida: [false]
    });
  }

  /**
   * Submete o formulário
   */
  onSubmit(): void {
    if (this.formulario.invalid) {
      this.erro = 'Por favor, preencha todos os campos obrigatórios corretamente';
      return;
    }

    this.loading = true;
    this.erro = '';
    this.sucesso = false;

    const tarefa: Tarefa = this.formulario.value;

    this.tarefaService.criarTarefa(tarefa).subscribe({
      next: (resposta) => {
        this.sucesso = true;
        this.formulario.reset({
          status: StatusTarefa.FAZER,
          concluida: false
        });
        this.loading = false;

        // Limpa mensagem de sucesso após 3 segundos
        setTimeout(() => {
          this.sucesso = false;
        }, 3000);
      },
      error: (err) => {
        this.erro = 'Erro ao cadastrar tarefa: ' + (err.error?.message || 'Tente novamente');
        console.error('Erro:', err);
        this.loading = false;
      }
    });
  }

  /**
   * Retorna mensagem de erro do campo
   */
  getFieldError(fieldName: string): string {
    const field = this.formulario.get(fieldName);
    
    if (!field || !field.errors || !field.touched) {
      return '';
    }

    if (field.errors['required']) {
      return `${this.getFieldLabel(fieldName)} é obrigatório`;
    }
    if (field.errors['minlength']) {
      const minLength = field.errors['minlength'].requiredLength;
      return `${this.getFieldLabel(fieldName)} deve ter no mínimo ${minLength} caracteres`;
    }
    
    return '';
  }

  /**
   * Retorna label do campo
   */
  getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      titulo: 'Título',
      descricao: 'Descrição',
      status: 'Status',
      dataDeInicio: 'Data de Início',
      dataDeFim: 'Data de Fim'
    };
    return labels[fieldName] || fieldName;
  }

  /**
   * Verifica se um campo tem erro
   */
  hasError(fieldName: string): boolean {
    const field = this.formulario.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  /**
   * Limpa o formulário
   */
  limparFormulario(): void {
    this.formulario.reset({
      status: StatusTarefa.FAZER,
      concluida: false
    });
  }
}
