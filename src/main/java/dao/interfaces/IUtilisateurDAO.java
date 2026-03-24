package dao.interfaces;

import model.Usager;
import model.AgentAdministratif;
import model.Administrateur;
import model.Personne;
import java.util.List;
import java.util.Optional;

public interface IUtilisateurDAO {

    // Usager

    Usager saveUsager(Usager usager);

    Optional<Usager> findUsagerById(int id);

    Optional<Usager> findUsagerByNumero(String numeroUsager);

    List<Usager> findAllUsagers();

    Usager updateUsager(Usager usager);

    void deleteUsager(int id);

    // Agent

    AgentAdministratif saveAgent(AgentAdministratif agent);

    Optional<AgentAdministratif> findAgentById(int id);

    Optional<AgentAdministratif> findAgentByMatricule(String matricule);

    List<AgentAdministratif> findAllAgents();

    AgentAdministratif updateAgent(AgentAdministratif agent);

    void deleteAgent(int id);

    // Administrateur

    Administrateur saveAdmin(Administrateur admin);

    Optional<Administrateur> findAdminById(int id);

    Optional<Administrateur> findAdminByLogin(String login);

    List<Administrateur> findAllAdmins();

    Administrateur updateAdmin(Administrateur admin);

    void deleteAdmin(int id);

    Optional<Personne> authenticate(String email, String password);

}