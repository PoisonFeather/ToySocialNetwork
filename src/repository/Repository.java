package repository;

import domain.Entity;
import domain.validators.ValidationException;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


/**
 * CRUD operations repository interface
 * @param <ID> type E must have an attribute of type ID
 * @param <E> type of entities saved in repository
 */
public interface Repository<ID, E extends Entity<ID>> {
    /**
     * Finds an entity by its identifier.
     * @param id the identifier of the entity to be returned; must not be null
     * @return en {@code Optional} encapsulating the entity with the given id, or an empty {@code Optional} it it does not exist
     * @throws IllegalArgumentException if id is null
     */
    Optional<E> findOne(ID id); // returns all entities
    Iterable<E> findAll(); // filters entites based on a given predicate
    default List<E> findAll(Predicate<E> filter) {
        return StreamSupport.stream(findAll().spliterator(), false)
                .filter(filter)
                .collect(Collectors.toList());
    }
    /**
     * Saves the given entity
     * @param entity the entity to save; must not be null
     * @return an empty {@code Optional} if the entity was saved successfully, or an {@code Optional} containing the entity if it already exists
     * @throws domain.validators.ValidationException if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null
     */
    Optional<E> save(E entity) throws ValidationException;
    /**
     * Removes the entity with the specified id.
     *
     * @param id the identifier of the entity to be removed; must not be null
     * @return an {@code Optional} containing the removed entity, or an empty {@code Optional} if there is no entity with the given id
     * @throws IllegalArgumentException if the given id is null
     */
    Optional<E> delete(ID id);

    /**
     * Updates the given entity.
     *
     * @param entity the entity to update; must not be null
     * @return an empty {@code Optional} if the entity was updated successfully, or an {@code Optional} containing the entity if it does not exist
     * @throws domain.validators.ValidationException if the entity is not valid
     * @throws IllegalArgumentException if the given entity is null
     */
    Optional<E> update(E entity);
}
