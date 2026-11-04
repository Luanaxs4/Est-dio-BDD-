package br.com.projeto.estudioBDD.database.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
@Table(name = "projeto")
public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column (nullable = false,unique = true)
    private String nome;

    @Column (columnDefinition = "TEXT")
    private String descricao;
    
    @Column (columnDefinition = "TEXT", name = "contexto_dominio")
    private String contextoDominio;
    

    @Column (name = "data_criacao", nullable = false, updatable = false)
    private LocalDate dataCriacao;

    @PrePersist
    void aoCriar() {
        if (dataCriacao == null) dataCriacao = LocalDate.now();
    }



    
}
