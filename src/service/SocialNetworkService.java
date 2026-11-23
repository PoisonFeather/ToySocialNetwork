package service;

import domain.Prietenie;
import domain.Utilizator;

import repository.Repository;
import repository.db.PrietenieDbRepository;
import util.paging.Page;
import util.paging.Pageable;
import util.paging.PagingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Service layer managing users, friendships, and core social network operations.
 */
public class SocialNetworkService {
    private final Repository<Long, Utilizator> repoUtilizatori;
    private final Repository<Long, Prietenie> repoPrietenii;

    /**
     * Constructs the service and initializes in-memory friend references from the repositories.
     *
     * @param repoUtilizatori the repository for {@link Utilizator} entities
     * @param repoPrietenii   the repository for {@link Prietenie} entities
     */
    public SocialNetworkService(Repository<Long, Utilizator> repoUtilizatori, Repository<Long, Prietenie> repoPrietenii) {
        this.repoUtilizatori = repoUtilizatori;
        this.repoPrietenii = repoPrietenii;

        repoPrietenii.findAll().forEach(p -> {
            Utilizator u1 = repoUtilizatori.findOne(p.getIdUser1())
                    .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost gasit!"));
            Utilizator u2 = repoUtilizatori.findOne(p.getIdUser2())
                    .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost gasit!"));
            u1.addFriend(u2);
            u2.addFriend(u1);
        });
    }

    /**
     * Creates and persists a new user, returning the saved entity with its storage-generated ID.
     *
     * @param prenume the user's first name
     * @param nume    the user's last name
     * @return the persisted {@link Utilizator} with its assigned ID
     */
    public Utilizator adaugaUtilizator(String prenume, String nume) {
        Utilizator user = new Utilizator(prenume, nume);
        repoUtilizatori.save(user);
        return user;
    }

    /**
     * Deletes a user by ID and removes all associated friendships.
     *
     * @param id the ID of the user to delete
     * @throws IllegalArgumentException if the user does not exist
     */
    public void stergeUtilizator(Long id) {
        Utilizator sters = repoUtilizatori.delete(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost gasit!"));

        // Inainte (Lab 3): for clasic peste findAll() + if-uri manuale
        // Dupa (Lab 4): filtrare declarativa cu default findAll(Predicate) si Optional.ifPresent()
        List<Prietenie> prieteniiDeSters = repoPrietenii.findAll(p ->
                p.getIdUser1().equals(id) || p.getIdUser2().equals(id));

        prieteniiDeSters.forEach(p -> {
            Long idPrieten = p.getIdUser1().equals(id) ? p.getIdUser2() : p.getIdUser1();
            repoUtilizatori.findOne(idPrieten).ifPresent(prieten -> prieten.removeFriend(sters));
            repoPrietenii.delete(p.getId());
        });
    }

    /**
     * Retrieves all registered users as a list.
     *
     * @return a {@link List} containing all users
     */
    public List<Utilizator> getTotiUtilizatorii() {
        // Inainte (Lab 3): for (Utilizator u : repoUtilizatori.findAll()) si adaugare in ArrayList
        // Dupa (Lab 4): Stream API + operatia terminala collect
        return StreamSupport.stream(repoUtilizatori.findAll().spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Retrieves all registered friendships as a list.
     *
     * @return a {@link List} containing all friendships
     */
    public List<Prietenie> getToatePrieteniile() {
        return StreamSupport.stream(repoPrietenii.findAll().spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    /**
     * Creates and persists a bidirectional friendship between two existing users.
     *
     * @param id1 the ID of the first user
     * @param id2 the ID of the second user
     * @throws IllegalArgumentException if either user does not exist
     */
    public void adaugaPrietenie(Long id1, Long id2) {
        Utilizator u1 = repoUtilizatori.findOne(id1)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul 1 nu exista!"));
        Utilizator u2 = repoUtilizatori.findOne(id2)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul 2 nu exista!"));

        Long newId = StreamSupport.stream(repoPrietenii.findAll().spliterator(), false)
                .map(Prietenie::getId)
                .max(Long::compareTo)
                .orElse(0L);
        Prietenie p = new Prietenie(id1, id2);
        p.setId(newId + 1);
        repoPrietenii.save(p);
        u1.addFriend(u2);
        u2.addFriend(u1);
    }

    /**
     * Removes the friendship between two users if it exists.
     *
     * @param id1 the ID of the first user
     * @param id2 the ID of the second user
     */
    public void stergePrietenie(Long id1, Long id2) {
        List<Prietenie> prieteniiDeSters = repoPrietenii.findAll(p ->
                (p.getIdUser1().equals(id1) && p.getIdUser2().equals(id2)) ||
                        (p.getIdUser1().equals(id2) && p.getIdUser2().equals(id1))
        );
        prieteniiDeSters.forEach(p -> repoPrietenii.delete(p.getId()));

        repoUtilizatori.findOne(id1).ifPresent(u1 ->
                repoUtilizatori.findOne(id2).ifPresent(u2 -> {
                    u1.removeFriend(u2);
                    u2.removeFriend(u1);
                })
        );
    }
    /**
     * Retrieves a paginated slice of users from a {@link PagingRepository}
     * @param pageNumber zero-based page index (must be {@code >= 0}
     * @param pageSize  number of elements per page (must be {@code >0}
     *
     * @return a {@link Page} of {@link Utilizator}
     * @throws IllegalArgumentException if {@code pageNumber <0 } or {@code pageSize <0}
     * @throws UnsupportedOperationException if the confiugred repository does not support pagination
     */
    public Page<Utilizator> getUtilizatoriOnPage(int pageNumber, int pageSize){
        Pageable pageable = new Pageable(pageNumber,pageSize);
        if(repoUtilizatori instanceof PagingRepository<Long,Utilizator> pagingRepo){
            return pagingRepo.findAllOnPage(pageable);
        }
        throw new UnsupportedOperationException("Paginig is only supported by repository.type=DATABASE!");
    }

    /**
     * Retrieves a paginated slice of friends for a given user.
     *
     * @param userId the ID of the user whose friends are requested
     * @param pageNumber zero-based page index (must be {@code  >=0}
     * @param pageSize number of elements per page (must be {@code >0}
     * @return  a {@link Page} of {@link Utilizator} representing the user's friends
     * @throws IllegalArgumentException if {@code userId} does not exist or pagination params are invalid
     * @throws UnsupportedOperationException if the configured repository does not support pagination
     */
    public Page<Utilizator> getPrieteniOnPage(Long userId, int pageNumber, int pageSize){
        if(userId == null){
            throw new IllegalArgumentException("User ID must not be null!");
        }
        repoUtilizatori.findOne(userId)
                .orElseThrow(()-> new IllegalArgumentException("Utilizatorul cu ID-ul"+userId+" nu exista!"));
        Pageable pageable = new Pageable(pageNumber,pageSize);
        if(repoPrietenii instanceof PrietenieDbRepository prietenieDbRepo){
            return prietenieDbRepo.findFriendsOnPage(userId, pageable);
        }
        throw new UnsupportedOperationException("Friendship paging is only supported by repository.type=DATABASE");
    }
}