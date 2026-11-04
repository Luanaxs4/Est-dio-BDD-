package br.com.projeto.estudioBDD.database.model;

import jakarta.persistence.Entity;
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

    private String tipo;
    private String texto;
    private int posicao;
    
    @ManyToOne
    @JoinColumn(name = "cenario_id")
    private Cenario cenario;
}
