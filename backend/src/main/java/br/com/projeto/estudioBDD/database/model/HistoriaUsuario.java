package br.com.projeto.estudioBDD.database.model;

import java.time.LocalDate;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor 
@AllArgsConstructor
@Table(name = "historia_usuario")
public class HistoriaUsuario {
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    private Long id;

    private String titulo;
    private String papel;
    private String objetivo;
    private String beneficio;
    private String prioridade;
    private LocalDate dataCriacao;
    private LocalDate dataAtualizacao;

    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
    
    @ManyToOne
    @JoinColumn(name = "projeto_id")
    private Projeto projeto;

}
