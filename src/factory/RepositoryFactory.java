package factory;

import domain.Prietenie;
import domain.Utilizator;
import domain.validators.Validator;
import repository.*;

import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

public class RepositoryFactory {
    private static RepositoryFactory instance = null;
    private final Properties properties;

    private RepositoryFactory() {
        this.properties = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new RuntimeException("Fisierul de configuratie nu a fost gasit");
            }
        } catch (Exception e) {
            throw new RuntimeException("Eroare la citirea configurarilor: " + e.getMessage());
        }
    }

    public static RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    public Repository<Long, Utilizator> createUtilizatorRepository(Validator<Utilizator> validator) {
        String typeStr = properties.getProperty("repository.type", "MEMORY");
        RepositoryType type;

        try {
            type = RepositoryType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RepositoryException("Tip invalid de repository specificat în config.properties: " + typeStr);
        }

        // switch -> map + supplier
        Map<RepositoryType, Supplier<Repository<Long, Utilizator>>> repoStrategies = new EnumMap<>(RepositoryType.class);

        repoStrategies.put(RepositoryType.MEMORY, () -> new InMemoryRepository<>(validator));
        repoStrategies.put(RepositoryType.FILE, () -> {
            String path = properties.getProperty("utilizatoriFile.path", "data/utilizatori.csv");
            return new UtilizatorFileRepository(path, validator);
        });
        repoStrategies.put(RepositoryType.DATABASE, () -> {
            throw new UnsupportedOperationException("Metoda pentru baza de date nu a fost inca implementanta!");
        });

        Supplier<Repository<Long, Utilizator>> strategy = repoStrategies.get(type);
        if (strategy == null) {
            throw new RepositoryException("Tip invalid de repository!");
        }

        return strategy.get();
    }

    public Repository<Long, Prietenie> createPrietenieRepository(Validator<Prietenie> validator) {
        String typeStr = properties.getProperty("repository.type", "MEMORY");
        RepositoryType type;

        try {
            type = RepositoryType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RepositoryException("Tip invalid de repository specificat în config.properties: " + typeStr);
        }

        //  switch -> map + supplier
        Map<RepositoryType, Supplier<Repository<Long, Prietenie>>> repoStrategies = new EnumMap<>(RepositoryType.class);

        repoStrategies.put(RepositoryType.MEMORY, () -> new InMemoryRepository<>(validator));
        repoStrategies.put(RepositoryType.FILE, () -> {
            String pathPrietenii = properties.getProperty("prieteniiFile.path", "data/prietenii.csv");
            return new PrietenieFileRepository(pathPrietenii, validator);
        });
        repoStrategies.put(RepositoryType.DATABASE, () -> {
            throw new UnsupportedOperationException("Metoda pentru baza de date inca nu este implementata!");
        });

        Supplier<Repository<Long, Prietenie>> strategy = repoStrategies.get(type);
        if (strategy == null) {
            throw new RepositoryException("Tip invalid de repository!");
        }

        return strategy.get();
    }
}