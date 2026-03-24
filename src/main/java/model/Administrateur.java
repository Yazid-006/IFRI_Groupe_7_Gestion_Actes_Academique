package model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.experimental.SuperBuilder;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
@Entity
@Table(name = "administrateur")
@PrimaryKeyJoinColumn(name = "id")
public class Administrateur extends Personne {

    @Column(name = "login", unique = true, nullable = false, length = 30)
    private String login;

    @Column(name = "motDePasse", nullable = false, length = 255)
    private String motDePasse;

    @Column(name = "derniereConnexion")
    private LocalDateTime derniereConnexion;

}