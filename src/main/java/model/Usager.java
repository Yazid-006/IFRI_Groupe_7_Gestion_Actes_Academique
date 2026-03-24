package model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "usager")
@PrimaryKeyJoinColumn(name = "id")
public class Usager extends Personne {

    @Column(name = "numeroUsager", unique = true, nullable = false, length = 20)
    private String numeroUsager;

    @Column(name = "adresse")
    private String adresse;

    @Column(name = "dateNaissance", nullable = false)
    private LocalDate dateNaissance;

    @Column(name = "pieceIdentite", length = 50)
    private String pieceIdentite;

    @OneToMany(mappedBy = "demandeur", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Demande> demandes;

}