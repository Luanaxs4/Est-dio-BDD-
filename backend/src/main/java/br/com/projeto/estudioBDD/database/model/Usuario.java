package br.com.projeto.estudioBDD.database.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Usuario {
    @Id
    @GeneratedValue (strategy = GenerationType.SEQUENCE)
    private Long id;
    
    private String nome;
    private String email;

}
