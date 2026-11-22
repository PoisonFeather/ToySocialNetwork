package util.paging;
import domain.Entity;
import repository.Repository;

/**
 * Extension of {@link Repository} that adds support for opaginated queries.
 *
 * @param <ID> the type of the entity identifier
 * @param <E> the type of entities maganed by the repository
 */

public interface PagingRepository<ID, E extends Entity<ID>>extends Repository<ID, E>{
        /**
         * Retireves a specific page of entities according to the given {@link Pagable} parameters
         * @param pageable the pagination parameters (page number and page size)
         * @return a {@link Page} containing the requested slice of entities and the total count
         */
        Page<E> findAllOnPage(Pageable pagealbe);
}
