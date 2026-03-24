package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import model.enums.TypeActe;
import model.enums.StatutDemande;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "demande")
public class Demande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "numeroDemande", unique = true, nullable = false, length = 30)
    private String numeroDemande;

    @Enumerated(EnumType.STRING)
    @Column(name = "typeActeDemande", nullable = false, length = 30)
    private TypeActe typeActeDemande;

    @Column(name = "dateDemande", nullable = false)
    private LocalDateTime dateDemande;

    @Enumerated(EnumType.STRING)
    @Column(name = "statut", nullable = false, length = 20)
    private StatutDemande statut;

    @Column(name = "motif", length = 500)
    private String motif;

    @Column(name = "dateTraitement")
    private LocalDateTime dateTraitement;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "demandeur_id", nullable = false)
    private Usager demandeur;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "agentTraiteur_id")
    private AgentAdministratif agentTraiteur;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "acteGenere_id")
    private ActeAdministratif acteGenere;

    @OneToMany(mappedBy = "demande", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DocumentJustificatif> justificatifs;

}