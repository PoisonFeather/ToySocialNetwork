package repository;

import domain.Entity;
import domain.validators.Validator;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.concurrent.atomic.AtomicInteger;

public abstract class AbstractFileRepository<ID, E extends Entity<ID>> extends InMemoryRepository<ID, E> {
    private String fileName;

    public AbstractFileRepository(String fileName, Validator<E> validator) {
        super(validator);
        this.fileName = fileName;
        loadData();
    }

    private void loadData() {
        Path path = Paths.get(fileName);
        if (!Files.exists(path)) {
            return;
        }

        // Citire cu Stream API
        try (Stream<String> lines = Files.lines(path)) {
            AtomicInteger lineNumber = new AtomicInteger(1);
            lines.forEach(line -> {
                try {
                    List<String> data = Arrays.asList(line.split(";"));
                    E entity = extractEntity(data);
                    super.save(entity);
                } catch (RuntimeException e) {
                    System.err.println("Linie coruptă la rândul " + lineNumber.get() + ": " + line + " (" + e.getMessage() + ")");
                }
                lineNumber.incrementAndGet();
            });
        } catch (IOException e) {
            throw new RepositoryException("Eroare la citirea fișierului: " + e.getMessage(), e);
        }
    }

    private void writeAllToFile() {
        Path path = Paths.get(fileName);
        try {
            // Conversia obiectelor in string-uri cu stream() in loc de for clasic
            List<String> lines = entities.values().stream()
                    .map(this::createEntityAsString)
                    .collect(Collectors.toList());
            // Scriere cu NIO.2
            Files.write(path, lines);
        } catch (IOException e) {
            throw new RepositoryException("Eroare la scrierea în fișier: " + e.getMessage(), e);
        }
    }

    protected abstract E extractEntity(List<String> attributes);
    protected abstract String createEntityAsString(E entity);

    @Override
    public Optional<E> save(E entity) {
        Optional<E> optional = super.save(entity);
        if (optional.isEmpty()) {
            writeAllToFile();
        }
        return optional;
    }

    @Override
    public Optional<E> delete(ID id) {
        Optional<E> optional = super.delete(id);
        if (optional.isPresent()) {
            writeAllToFile();
        }
        return optional;
    }

    @Override
    public Optional<E> update(E entity) {
        Optional<E> optional = super.update(entity);
        if (optional.isEmpty()) {
            writeAllToFile();
        }
        return optional;
    }
}