package repository;
import domain.Utilizator;
import domain.validators.Validator;

import java.util.Optional;

public class UtilizatorDbRepository  implements Repository<Long,Utilizator>{
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDbRepository(String url,String username, String password, Validator<Utilizator> validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }

    @Override public Optional<Utilizator> findOne(Long id){ throw new UnsupportedOperationException("Baza de date nu e implementata inca!");};
    @Override public Iterable<Utilizator> findAll() {throw new UnsupportedOperationException("Baza de date nu e implementata inca!");};
    @Override public Optional<Utilizator> save(Utilizator entity) {throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
    @Override public Optional<Utilizator> delete(Long id){ throw new UnsupportedOperationException("Baza de date nu a fost implementata inca");};
    @Override public Optional<Utilizator> update(Utilizator entity){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
}
