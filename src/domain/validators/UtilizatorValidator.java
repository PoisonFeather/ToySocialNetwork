package domain.validators;
import domain.Utilizator;
public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException{
        String errors = "";
        if (entity.getFirstName() == null || entity.getFirstName().trim().isEmpty() || !entity.getFirstName().matches("^[a-zA-Z]+$")) {
            errors += "Prenumele este invalid (nu poate fi vid si trebuie sa contina doar litere)!\n";
        }

        if(entity.getLastName() == null || entity.getLastName().trim().isEmpty()){
            errors += "Numele nu poate fi vid!\n";
        }
        if(!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}
