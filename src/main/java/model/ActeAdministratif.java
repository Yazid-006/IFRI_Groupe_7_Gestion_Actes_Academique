package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import model.enums.TypeActe;
import model.enums.StatutActe;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "acte")
public class ActeAdministratif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "numeroActe", unique = true, nullable = false, length = 30)
    private String numeroActe;

    @Enumerated(EnumType.STRING)
    @Column(name = "typeActe", nullable = false, length = 30)
    private TypeActe typeActe;

    @Column(name = "objet", nullable = false, length = 200)
    private String objet;

    @Column(name = "contenu")
    private String contenu;

    @Column(name = "dateEmission", nullable = false)
    private LocalDateTime dateEmission;

    @Column(name = "dateValidite")
    private LocalDate dateValidite;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutActe statut;

    @Column(name = "cheminFichier", length = 255)
    private String cheminFichier;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usager_id", nullable = false)
    private Usager usager;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agent_id", nullable = false)
    private AgentAdministratif agentEmetteur;

}