package service;
import domain.Utilizator;
import repository.Repository;
import java.util.*;
public class ComunitatiService {
    private final Repository<Long,Utilizator> repoUtilizatori;

    public ComunitatiService(Repository<Long,Utilizator> repoUtilizatori){
        this.repoUtilizatori = repoUtilizatori;
    }

    //numarul de comunitati
    public int getNumarComunitati() {
        Set<Long> vizitate = new HashSet<>();
        int numarComunitati = 0;
        for (Utilizator u : repoUtilizatori.findAll()) {
            if (!vizitate.contains(u.getId())) {
                numarComunitati++;
                dfsComunitate(u,vizitate);
            }
        }
        return numarComunitati;
    }

    private void dfsComunitate(Utilizator u, Set<Long> vizitate){
        vizitate.add(u.getId());
        //for clasic -> stream filter pe vecini
        u.getFriends().stream()
                .filter(prieten->!vizitate.contains(prieten.getId()))
                .forEach(prieten -> dfsComunitate(prieten,vizitate));
    }



    public List<Utilizator> getCeaMaiSociabilaComunitate(){
        Set<Long> vizitate = new HashSet<>();
        List<List<Utilizator>> comunitati = new ArrayList<>();

        int drumMaximGlobal = -1;

        // Extragem toate comunitățile într-o listă de liste
        for (Utilizator u : repoUtilizatori.findAll()) {
            if (!vizitate.contains(u.getId())) {
                List<Utilizator> componenta = new ArrayList<>();
                extrageComponenta(u, vizitate, componenta);
                comunitati.add(componenta);
            }
        }
        // Determinam maximul folosind Stream API și Comparator.comparingInt exact ca în sugestiile de laborator
        return comunitati.stream()
                .max(Comparator.comparingInt(this::calculeazaCelMaiLungDrum))
                .orElse(Collections.emptyList());
    }

    private void extrageComponenta(Utilizator u, Set<Long> vizitate, List<Utilizator> componenta){
        vizitate.add(u.getId());
        componenta.add(u);

        u.getFriends().stream()
                .filter(prieten -> !vizitate.contains(prieten.getId()))
                .forEach(prieten -> extrageComponenta(prieten, vizitate, componenta));
    }

    private int calculeazaCelMaiLungDrum(List<Utilizator> componenta){
        return componenta.stream()
                .mapToInt(u -> {
                    Set<Long> vizitatePath = new HashSet<>();
                    return dfsCelMaiLungDrum(u, vizitatePath);
                })
                .max()
                .orElse(0);
    }
    private int dfsCelMaiLungDrum(Utilizator u, Set<Long> vizitate){
        vizitate.add(u.getId());
        // Aflăm adâncimea maximă pe vecini direct prin mapToInt și max
        int maxDepth = u.getFriends().stream()
                .filter(prieten -> !vizitate.contains(prieten.getId()))
                .mapToInt(prieten -> dfsCelMaiLungDrum(prieten, vizitate))
                .max()
                .orElse(0);

        vizitate.remove(u.getId());
        return maxDepth + 1;
    }
}
