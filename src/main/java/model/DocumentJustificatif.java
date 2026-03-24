package model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "document")
public class DocumentJustificatif {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "nomFichier", nullable = false, length = 255)
    private String nomFichier;

    @Column(name = "typeFichier", nullable = false, length = 50)
    private String typeFichier;

    @Column(name = "chemin", nullable = false, length = 255)
    private String chemin;

    @Column(name = "dateUpload", nullable = false)
    private LocalDateTime dateUpload;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "demande_id", nullable = false)
    private Demande demande;

}