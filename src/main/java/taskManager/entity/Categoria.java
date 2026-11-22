package taskManager.entity;

import jakarta.persistence.*;

/**
 * Categoria criada pelo usuário para agrupar suas tarefas (ex.: Trabalho, Estudos, Casa).
 * Cada categoria pertence a um único usuário e o nome é único dentro da conta dele.
 */
@Entity
@Table(
        name = "tb_categoria",
        uniqueConstraints = @UniqueConstraint(name = "uk_categoria_usuario_nome", columnNames = {"usuario_id", "nome"})
)
public class Categoria {

    public static final String COR_PADRAO = "#7062e4";

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 60)
    private String nome;

    @Column(nullable = false, length = 7)
    private String cor;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false, foreignKey = @ForeignKey(name = "fk_categoria_usuario"))
    private Usuario usuario;

    public Categoria() {
    }

    public Categoria(String nome, String cor, Usuario usuario) {
        this.nome = nome;
        this.cor = cor;
        this.usuario = usuario;
    }

    public Long getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCor() {
        return cor;
    }

    public void setCor(String cor) {
        this.cor = cor;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }
}
