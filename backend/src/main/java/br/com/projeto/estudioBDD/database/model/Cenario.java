package br.com.projeto.estudioBDD.database.model;

import java.util.ArrayList;
import java.util.List;

import br.com.projeto.estudioBDD.database.model.enums.OrigemCenario;
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
@Table(name = "cenario",
       uniqueConstraints = @UniqueConstraint(columnNames = {"historia_usuario_id", "titulo"}))
public class Cenario {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private int posicao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private OrigemCenario origem = OrigemCenario.HUMANO;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_usuario_id", nullable = false)
    private HistoriaUsuario historiaUsuario;

    // composição: passos são removidos junto com o cenário
    @OneToMany(mappedBy = "cenario", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("posicao ASC")
    @Builder.Default
    private List<Passo> passos = new ArrayList<>();
}
