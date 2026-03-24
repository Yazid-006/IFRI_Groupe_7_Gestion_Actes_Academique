package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "configuration")
public class Configuration {

    @Id
    @Column(name = "id")
    private int id = 1;

    @Column(name = "nomEtablissement", nullable = false, length = 100)
    private String nomEtablissement;

    @Column(name = "adresse", length = 255)
    private String adresse;

    @Column(name = "anneeAcademique", length = 20)
    private String anneeAcademique;

    @Column(name = "prefixeDemande", length = 20)
    private String prefixeDemande;

    @Column(name = "delaiTraitement")
    private int delaiTraitement;

    @Column(name = "nomSignataire", length = 100)
    private String nomSignataire;

    @Column(name = "fonctionSignataire", length = 100)
    private String fonctionSignataire;

}