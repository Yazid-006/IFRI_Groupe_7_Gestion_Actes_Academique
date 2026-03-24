package dao.utils;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import java.io.InputStream;
import java.util.Properties;

@Slf4j
public class HibernateUtil {
    private static final SessionFactory sessionFactory = buildSessionFactory();

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration();

            // Charger application.properties
            Properties props = new Properties();
            try (InputStream input = HibernateUtil.class.getClassLoader()
                    .getResourceAsStream("config/application.properties")) {
                props.load(input);
            }

            // Config Hibernate à partir des props
            configuration.setProperty("hibernate.connection.driver_class", props.getProperty("db.driver"));
            configuration.setProperty("hibernate.connection.url", props.getProperty("db.url"));
            configuration.setProperty("hibernate.connection.username", props.getProperty("db.username"));
            configuration.setProperty("hibernate.connection.password", props.getProperty("db.password"));
            configuration.setProperty("hibernate.dialect", props.getProperty("hibernate.dialect"));
            configuration.setProperty("hibernate.show_sql", props.getProperty("hibernate.show_sql"));
            configuration.setProperty("hibernate.format_sql", props.getProperty("hibernate.format_sql"));
            configuration.setProperty("hibernate.hbm2ddl.auto", props.getProperty("hibernate.hbm2ddl.auto"));

            // Ajouter les classes annotées
            configuration.addAnnotatedClass(model.Usager.class);
            configuration.addAnnotatedClass(model.AgentAdministratif.class);
            configuration.addAnnotatedClass(model.Administrateur.class);
            configuration.addAnnotatedClass(model.ActeAdministratif.class);
            configuration.addAnnotatedClass(model.Demande.class);
            configuration.addAnnotatedClass(model.DocumentJustificatif.class);
            configuration.addAnnotatedClass(model.Configuration.class);

            return configuration.buildSessionFactory();

        } catch (Throwable ex) {
            log.error("Initialisation de SessionFactory échouée : {}", ex.getMessage());
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        return sessionFactory;
    }

    public static void shutdown() {
        getSessionFactory().close();
    }

}