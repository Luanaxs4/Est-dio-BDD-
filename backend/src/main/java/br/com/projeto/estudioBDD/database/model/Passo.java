package br.com.projeto.estudioBDD.database.model;

import br.com.projeto.estudioBDD.database.model.enums.TipoPasso;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GenerationType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
public class Passo {
    @Id 
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoPasso tipo; 

    @Column(nullable = false, columnDefinition = "TEXT")
    private String texto;
    
    @Column(nullable = false)
    private int posicao;
    
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "cenario_id", nullable = false)
    private Cenario cenario;
}
