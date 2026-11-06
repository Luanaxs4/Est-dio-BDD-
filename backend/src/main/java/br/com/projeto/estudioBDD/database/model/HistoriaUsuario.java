package br.com.projeto.estudioBDD.database.model;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import br.com.projeto.estudioBDD.database.model.enums.Prioridade;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Builder
@Table(name = "historia_usuario",
       uniqueConstraints = @UniqueConstraint(columnNames = {"projeto_id", "titulo"}))
public class HistoriaUsuario {
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String papel;      // Como...

    @Column(nullable = false)
    private String objetivo;   // quero...

    @Column(nullable = false)
    private String beneficio;  // para...

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Prioridade prioridade;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDate dataCriacao;

    @Column(name = "data_atualizacao")
    private LocalDate dataAtualizacao;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "projeto_id", nullable = false)
    private Projeto projeto;

    @OneToMany(mappedBy = "historiaUsuario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posicao ASC")
    @Builder.Default
    private List<Cenario> cenarios = new ArrayList<>();

    @OneToMany(mappedBy = "historiaUsuario", cascade = CascadeType.REMOVE)
    @Builder.Default
    private List<Sugestao> sugestoes = new ArrayList<>();

    @PrePersist
    void aoCriar() {
        dataCriacao = LocalDate.now();
        dataAtualizacao = dataCriacao;
    }

    @PreUpdate
    void aoAtualizar() {
        dataAtualizacao = LocalDate.now();
    }

}
