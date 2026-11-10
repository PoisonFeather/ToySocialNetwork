import domain.Prietenie;
import domain.Utilizator;
import domain.validators.UtilizatorValidator;
import domain.validators.ValidationException;
import domain.validators.Validator;
import repository.Repository;
import repository.UtilizatorFileRepository;
import service.SocialNetworkService;
import ui.ConsoleUI;
import repository.PrietenieFileRepository;
import service.ComunitatiService;
import factory.RepositoryFactory;
public class Main {
    public static void main(String[] args) {

        Validator<Utilizator> utilizatorValidator = new UtilizatorValidator();

        Validator<Prietenie> prietenieValidator = entity -> {};

        RepositoryFactory factory = RepositoryFactory.getInstance();
        Repository<Long, Utilizator> utilizatorRepository = factory.createUtilizatorRepository(utilizatorValidator);
        Repository<Long, Prietenie> prietenieRepository = factory.createPrietenieRepository(prietenieValidator);

        SocialNetworkService service = new SocialNetworkService(utilizatorRepository, prietenieRepository);
        ComunitatiService comunitatiService = new ComunitatiService(utilizatorRepository);

        ConsoleUI console = new ConsoleUI(service,comunitatiService);
        console.run();
    }
}