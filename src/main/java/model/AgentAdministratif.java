package model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import model.enums.NiveauAcces;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "agent")
@PrimaryKeyJoinColumn(name = "id")
public class AgentAdministratif extends Personne {

    @Column(name = "matricule", unique = true, nullable = false, length = 20)
    private String matricule;

    @Column(name = "service", nullable = false, length = 50)
    private String service;

    @Column(name = "fonction", nullable = false, length = 50)
    private String fonction;

    @Enumerated(EnumType.STRING)
    @Column(name = "niveauAcces", nullable = false, length = 20)
    private NiveauAcces niveauAcces;

    @Column(name = "motDePasse", nullable = false, length = 255)
    private String motDePasse;

}