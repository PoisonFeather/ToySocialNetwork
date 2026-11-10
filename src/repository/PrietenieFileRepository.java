package repository;

import domain.Prietenie;
import domain.validators.Validator;
import java.time.LocalDateTime;
import java.util.List;

public class PrietenieFileRepository extends AbstractFileRepository<Long, Prietenie> {

    public PrietenieFileRepository(String fileName, Validator<Prietenie> validator) {
        super(fileName, validator);
    }

    @Override
    protected Prietenie extractEntity(List<String> attributes) {
        Long id = Long.parseLong(attributes.get(0));
        Long id1 = Long.parseLong(attributes.get(1));
        Long id2 = Long.parseLong(attributes.get(2));
        Prietenie p = new Prietenie(id1, id2);
        p.setId(id);
        p.setDate(LocalDateTime.parse(attributes.get(3)));
        return p;
    }

    @Override
    protected String createEntityAsString(Prietenie entity) {
        return entity.getId() + ";" + entity.getIdUser1() + ";" + entity.getIdUser2() + ";" + entity.getDate();
    }
}