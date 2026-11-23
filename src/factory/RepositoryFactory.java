package factory;

import domain.Prietenie;
import domain.Utilizator;
import domain.validators.Validator;
import repository.InMemoryRepository;
import repository.Repository;
import repository.RepositoryException;
import repository.RepositoryType;
import repository.db.PrietenieDbRepository;
import repository.db.UtilizatorDbRepository;
import repository.file.PrietenieFileRepository;
import repository.file.UtilizatorFileRepository;

import java.io.IOException;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;
import java.util.Properties;
import java.util.function.Supplier;

/**
 * Singleton factory responsible for instantiating repository implementations
 * (Memory, File, or Database) based on the settings in {@code config.properties}.
 */
public class RepositoryFactory {
    private static RepositoryFactory instance = null;
    private final Properties properties;

    private RepositoryFactory() {
        this.properties = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (is != null) {
                properties.load(is);
            } else {
                throw new RepositoryException("Fisierul de configuratie config.properties nu a fost gasit!");
            }
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citirea configurarilor: " + e.getMessage(), e);
        }
    }

    /**
     * Returns the singleton instance of {@code RepositoryFactory}.
     *
     * @return the shared factory instance
     */
    public static RepositoryFactory getInstance() {
        if (instance == null) {
            instance = new RepositoryFactory();
        }
        return instance;
    }

    /**
     * Creates a user repository configured according to {@code repository.type}.
     *
     * @param validator the validator used for {@link Utilizator} entities
     * @return a configured {@link Repository} for users
     */
    public Repository<Long, Utilizator> createUtilizatorRepository(Validator<Utilizator> validator) {
        String typeStr = properties.getProperty("repository.type", "MEMORY");
        RepositoryType type;

        try {
            type = RepositoryType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RepositoryException("Tip invalid de repository specificat in config.properties: " + typeStr);
        }

        Map<RepositoryType, Supplier<Repository<Long, Utilizator>>> repoStrategies = new EnumMap<>(RepositoryType.class);

        repoStrategies.put(RepositoryType.MEMORY, () -> new InMemoryRepository<>(validator));
        repoStrategies.put(RepositoryType.FILE, () -> {
            String path = properties.getProperty("utilizatoriFile.path", "data/utilizatori.csv");
            return new UtilizatorFileRepository(path, validator);
        });
        repoStrategies.put(RepositoryType.DATABASE, () -> {
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            return new UtilizatorDbRepository(url, user, password, validator);
        });

        Supplier<Repository<Long, Utilizator>> strategy = repoStrategies.get(type);
        if (strategy == null) {
            throw new RepositoryException("Tip invalid de repository!");
        }

        return strategy.get();
    }

    /**
     * Creates a friendship repository configured according to {@code repository.type}.
     *
     * @param validator the validator used for {@link Prietenie} entities
     * @return a configured {@link Repository} for friendships
     */
    public Repository<Long, Prietenie> createPrietenieRepository(Validator<Prietenie> validator) {
        String typeStr = properties.getProperty("repository.type", "MEMORY");
        RepositoryType type;

        try {
            type = RepositoryType.valueOf(typeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new RepositoryException("Tip invalid de repository specificat in config.properties: " + typeStr);
        }

        Map<RepositoryType, Supplier<Repository<Long, Prietenie>>> repoStrategies = new EnumMap<>(RepositoryType.class);

        repoStrategies.put(RepositoryType.MEMORY, () -> new InMemoryRepository<>(validator));
        repoStrategies.put(RepositoryType.FILE, () -> {
            String pathPrietenii = properties.getProperty("prieteniiFile.path", "data/prietenii.csv");
            return new PrietenieFileRepository(pathPrietenii, validator);
        });
        repoStrategies.put(RepositoryType.DATABASE, () -> {
            String url = properties.getProperty("db.url");
            String user = properties.getProperty("db.user");
            String password = properties.getProperty("db.password");
            return new PrietenieDbRepository(url, user, password, validator);
        });

        Supplier<Repository<Long, Prietenie>> strategy = repoStrategies.get(type);
        if (strategy == null) {
            throw new RepositoryException("Tip invalid de repository!");
        }

        return strategy.get();
    }

    /**
     * Reads the configured page size from {@code config.properties}
     * @return the positive page size (defaults to 5 if omitted)
     * @throws IllegalArgumentException if {@code page.size} is not a positive integer
     */
    public int getPageSize(){
        String raw = properties.getProperty("page.size","5");
        try{
            int pageSize = Integer.parseInt(raw.trim());
            if(pageSize < 0) throw new IllegalArgumentException("Page size must be > 0!");

            return pageSize;
        }catch (NumberFormatException e){ throw new IllegalArgumentException("Invalid value for page.size in config!"); }
    }
}