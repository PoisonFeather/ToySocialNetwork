package domain.validators;

/**
 * Functional interface used for validating entities.
 * @param <T> the type of the entity to be validated
 */
@FunctionalInterface
public interface Validator<T> {

    /**
     * Validates the given entity.
     * @param entity the entity to validate
     * @throws ValidationException if the entity does not meet the validation criteria
     */
    void validate(T entity) throws ValidationException;

    /**
     * Returns a composed validator that first executes this validation, then the other validation.
     * @param other the next validator to apply
     * @return a composed validator
     */
    default Validator<T> andThen(Validator<T> other) {
        return (T entity) -> {
            this.validate(entity);
            other.validate(entity);
        };
    }
}