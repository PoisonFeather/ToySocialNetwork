package service;

import domain.Prietenie;
import domain.Utilizator;
import repository.Repository;

import java.util.*;

/**
 * Service responsible for analyzing social network communities (connected components)
 * and identifying the most sociable community (longest elementary path).
 */
public class ComunitatiService {
    private final Repository<Long, Utilizator> repoUtilizatori;
    private final Repository<Long, Prietenie> repoPrietenii;

    /**
     * Constructs a new community service using user and friendship repositories.
     *
     * @param repoUtilizatori the repository containing user entities
     * @param repoPrietenii   the repository containing friendship entities
     */
    public ComunitatiService(Repository<Long, Utilizator> repoUtilizatori, Repository<Long, Prietenie> repoPrietenii) {
        this.repoUtilizatori = repoUtilizatori;
        this.repoPrietenii = repoPrietenii;
    }

    /**
     * Reconstructs the friendship adjacency lists in memory from the repositories,
     * ensuring accurate graph traversal regardless of the underlying storage type (DB, File, Memory).
     *
     * @return a collection of all users with their friend lists populated
     */
    private Collection<Utilizator> getUtilizatoriCuPrieteni() {
        Map<Long, Utilizator> usersMap = new HashMap<>();
        // Mapam toti utilizatorii dupa ID
        for (Utilizator u : repoUtilizatori.findAll()) {
            usersMap.put(u.getId(), u);
        }
        // Reconstruim legaturile bidirectionale din tabela/fisierul de prietenii
        for (Prietenie p : repoPrietenii.findAll()) {
            Utilizator u1 = usersMap.get(p.getIdUser1());
            Utilizator u2 = usersMap.get(p.getIdUser2());
            if (u1 != null && u2 != null) {
                u1.addFriend(u2);
                u2.addFriend(u1);
            }
        }
        return usersMap.values();
    }

    /**
     * Calculates the total number of distinct communities (connected components) in the network.
     *
     * @return the number of connected components
     */
    public int getNumarComunitati() {
        Set<Long> vizitate = new HashSet<>();
        int numarComunitati = 0;

        // Parcurgem fiecare utilizator si numaram componentele conexe prin DFS
        for (Utilizator u : getUtilizatoriCuPrieteni()) {
            if (!vizitate.contains(u.getId())) {
                numarComunitati++;
                dfsComunitate(u, vizitate);
            }
        }
        return numarComunitati;
    }

    private void dfsComunitate(Utilizator u, Set<Long> vizitate) {
        vizitate.add(u.getId());
        // Parcurgere functionala a vecinilor nevizitati
        u.getFriends().stream()
                .filter(prieten -> !vizitate.contains(prieten.getId()))
                .forEach(prieten -> dfsComunitate(prieten, vizitate));
    }

    /**
     * Finds the most sociable community, defined as the connected component
     * containing the longest elementary path.
     *
     * @return a list of users belonging to the most sociable community, or an empty list if none exist
     */
    public List<Utilizator> getCeaMaiSociabilaComunitate() {
        Set<Long> vizitate = new HashSet<>();
        List<List<Utilizator>> comunitati = new ArrayList<>();

        // Extragem toate comunitatile (componentele conexe) intr-o lista de liste
        for (Utilizator u : getUtilizatoriCuPrieteni()) {
            if (!vizitate.contains(u.getId())) {
                List<Utilizator> componenta = new ArrayList<>();
                extrageComponenta(u, vizitate, componenta);
                comunitati.add(componenta);
            }
        }

        // Determinam comunitatea cu cel mai lung drum elementar folosind Stream API
        return comunitati.stream()
                .max(Comparator.comparingInt(this::calculeazaCelMaiLungDrum))
                .orElse(Collections.emptyList());
    }

    private void extrageComponenta(Utilizator u, Set<Long> vizitate, List<Utilizator> componenta) {
        vizitate.add(u.getId());
        componenta.add(u);

        u.getFriends().stream()
                .filter(prieten -> !vizitate.contains(prieten.getId()))
                .forEach(prieten -> extrageComponenta(prieten, vizitate, componenta));
    }

    private int calculeazaCelMaiLungDrum(List<Utilizator> componenta) {
        // Calculam cel mai lung drum pornind din fiecare nod al componentei
        return componenta.stream()
                .mapToInt(u -> {
                    Set<Long> vizitatePath = new HashSet<>();
                    return dfsCelMaiLungDrum(u, vizitatePath);
                })
                .max()
                .orElse(0);
    }

    private int dfsCelMaiLungDrum(Utilizator u, Set<Long> vizitate) {
        vizitate.add(u.getId());
        // Backtracking pentru a gasi adancimea maxima a unui drum elementar
        int maxDepth = u.getFriends().stream()
                .filter(prieten -> !vizitate.contains(prieten.getId()))
                .mapToInt(prieten -> dfsCelMaiLungDrum(prieten, vizitate))
                .max()
                .orElse(0);

        vizitate.remove(u.getId());
        return maxDepth + 1;
    }
}