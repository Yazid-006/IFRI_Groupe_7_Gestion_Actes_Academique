package dao.impl;

import dao.interfaces.IConfigurationDAO;
import dao.utils.HibernateUtil;
import lombok.extern.slf4j.Slf4j;
import model.Configuration;
import org.hibernate.Session;
import org.hibernate.Transaction;

import java.util.Optional;

@Slf4j
public class ConfigurationDAO implements IConfigurationDAO {

    @Override
    public Optional<Configuration> find() {
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            Configuration config = session.get(Configuration.class, 1);
            return Optional.ofNullable(config);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération de la configuration: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Configuration save(Configuration configuration) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            session.merge(configuration);
            transaction.commit();
            log.info("Configuration sauvegardée avec succès");
            return configuration;
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            log.error("Erreur lors de la sauvegarde de la configuration: {}", e.getMessage());
            return null;
        }
    }

}
