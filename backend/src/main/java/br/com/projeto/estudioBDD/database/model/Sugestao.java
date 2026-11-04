package br.com.projeto.estudioBDD.database.model;

import java.time.LocalDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
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
    
    @Column (nullable = false)
    private String ferramenta;
    
    @Column (name = "prompt_utilizado", columnDefinition = "TEXT")
    private String promptUtilizado;

    @Column (columnDefinition = "TEXT", nullable = false)
    private String conteudo;

    /*@Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private DecisaoSugestao decisao = DecisaoSugestao.PENDENTE;
     */
    private String decisao;
    /*@Enumerated(EnumType.STRING)
    @Column(name = "motivo_rejeicao")
    private MotivoRejeicao motivoRejeicao; */
    @Column (name = "motivo_rejeicao")
    private String motivoRejeicao;

    @Column(columnDefinition = "TEXT")
    private String justificativa;

    @Column(name = "data_registro", nullable = false, updatable = false)
    private LocalDate dataRegistro;

    @Column(name = "data_decisao")
    private LocalDate dataDecisao;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "historia_usuario_id", nullable = false)
    private HistoriaUsuario historiaUsuario;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "registrada_por_id", nullable = false)
    private Usuario registradaPor;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "decidida_por_id")
    private Usuario decididaPor;

    @PrePersist
    void aoCriar() {
        if (dataRegistro == null) dataRegistro = LocalDate.now();
    }
}
