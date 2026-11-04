package br.com.projeto.estudioBDD.database.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
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
public class Sugestao {
    @Id 
    @GeneratedValue(strategy = GenerationType.SEQUENCE) 
    private Long id;
    
    private String ferramenta;
    @Column (name = "prompt_utilizado")
    private String promptUtilizado;
    private String conteudo;
    private String decisao;
    @Column (name = "motivo_rejeicao")
    private String motivoRejeicao;
    private String justificativa;
    @Column (name = "data_registro")
    private LocalDate dataRegistro;
    @Column (name = "data_decisao")
    private LocalDate dataDecisao;

    @ManyToOne
    @JoinColumn(name = "historia_usuario_id")
    private Projeto projeto;
    
    @ManyToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;
}
