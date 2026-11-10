package repository;
import domain.Prietenie;
import domain.validators.Validator;
import java.util.Optional;
public class PrietenieDbRepository implements Repository<Long,Prietenie> {
    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;



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
