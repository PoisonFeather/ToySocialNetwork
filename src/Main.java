import domain.Prietenie;
import domain.Utilizator;
import domain.validators.UtilizatorValidator;
import domain.validators.ValidationException;
import domain.validators.Validator;
import repository.Repository;
import service.SocialNetworkService;
import ui.ConsoleUI;
import service.ComunitatiService;
import factory.RepositoryFactory;
public class Main {
    public static void main(String[] args) {

        Validator<Utilizator> utilizatorValidator = new UtilizatorValidator();

        Validator<Prietenie> prietenieValidator = entity -> {};

        try {
            RepositoryFactory factory = RepositoryFactory.getInstance();
            Repository<Long, Utilizator> utilizatorRepository = factory.createUtilizatorRepository(utilizatorValidator);

            Repository<Long, Prietenie> prietenieRepository = factory.createPrietenieRepository(prietenieValidator);

            SocialNetworkService service = new SocialNetworkService(utilizatorRepository, prietenieRepository);
            ComunitatiService comunitatiService = new ComunitatiService(utilizatorRepository, prietenieRepository);

            ConsoleUI console = new ConsoleUI(service, comunitatiService);
            console.run();
        } catch (repository.RepositoryException e) {
            System.out.println("Eroare la initializarea stocarii: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            System.out.println("Date inconsistente la incarcare: " + e.getMessage());
        }

    }
}