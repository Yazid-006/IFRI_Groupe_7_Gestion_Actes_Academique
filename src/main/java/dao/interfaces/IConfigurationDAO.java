package dao.interfaces;

import model.Configuration;
import java.util.Optional;

public interface IConfigurationDAO {

    Optional<Configuration> find();

    Configuration save(Configuration configuration);

}
