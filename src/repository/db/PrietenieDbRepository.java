package repository.db;

import domain.Prietenie;
import domain.validators.Validator;
import repository.Repository;
import repository.RepositoryException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrietenieDbRepository implements Repository<Long,Prietenie> {
    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;

    /**
     * Construieste un repository pentru DB
     * @param url url-ul pentru jdbc
     * @param username username-ul pentru db
     * @param password parola pentru db
     * @param validator validator pentru entitatile de prietenie
     */

    public PrietenieDbRepository(String url, String username, String password, Validator<Prietenie> validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;

    }


    @Override public Optional<Prietenie> findOne(Long id){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
    @Override public Optional<Prietenie> save(Prietenie prieten){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
    @Override public Optional<Prietenie> delete(Long id){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
    @Override public Optional<Prietenie> update(Prietenie prieten){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
    @Override public Iterable<Prietenie> findAll(){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca");};

}
