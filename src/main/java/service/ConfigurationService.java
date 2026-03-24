package service;

import dao.impl.ConfigurationDAO;
import dao.interfaces.IConfigurationDAO;
import lombok.extern.slf4j.Slf4j;
import model.Configuration;

@Slf4j
public class ConfigurationService {

    private final IConfigurationDAO configurationDAO;

    public ConfigurationService() {
        this.configurationDAO = new ConfigurationDAO();
    }

    public Configuration getConfiguration() {
        return configurationDAO.find().orElseGet(() -> {
            log.info("Aucune configuration trouvée, création des valeurs par défaut");
            return Configuration.builder()
                    .id(1)
                    .nomEtablissement("IFRI")
                    .adresse("")
                    .anneeAcademique("2025-2026")
                    .prefixeDemande("IFRI")
                    .delaiTraitement(5)
                    .nomSignataire("")
                    .fonctionSignataire("")
                    .build();
        });
    }

    public Configuration sauvegarder(Configuration configuration) {
        return configurationDAO.save(configuration);
    }

}
