package service;

import domain.Prietenie;
import domain.Utilizator;
import repository.Repository;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.ArrayList;

public class SocialNetworkService {
    private final Repository<Long, Utilizator>  repoUtilizatori;
    private final Repository<Long, Prietenie> repoPrietenii;

    public SocialNetworkService(Repository<Long, Utilizator> repoUtilizatori, Repository<Long, Prietenie> repoPrietenii) {
        this.repoUtilizatori = repoUtilizatori;
        this.repoPrietenii = repoPrietenii;

        //iterare functionala peste iterable-ul de prietenii
        repoPrietenii.findAll().forEach(p ->{
            Utilizator u1 = repoUtilizatori.findOne(p.getIdUser1())
                    .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost gasit!"));
            Utilizator u2 = repoUtilizatori.findOne(p.getIdUser2())
                    .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost gasit!"));
            u1.addFriend(u2);
            u2.addFriend(u1);
        });
        }


    public void adaugaUtilizator(String prenume, String nume){
        Utilizator user = new Utilizator(prenume,nume);
        //Stream API pentur determinarea id-ului maxim
        Long newId = StreamSupport.stream(repoUtilizatori.findAll().spliterator(),false)
                .map(Utilizator::getId)
                .max(Long::compareTo)
                .orElse(0L);

        user.setId(newId + 1);
        repoUtilizatori.save(user);
    }
    public void stergeUtilizator(Long id) {
        Utilizator sters = repoUtilizatori.delete(id)
                .orElseThrow(() -> new IllegalArgumentException("Utilizatorul nu a fost gasit!"));

         //filtrare folosind metoda defaul findAll(Predicate)
            List<Prietenie> prieteniiDeSters = repoPrietenii.findAll(p->
                    p.getIdUser1().equals(id) || p.getIdUser2().equals(id));
            //parcurgere cu forEach si procesare cu ifPresent() din Optional
            prieteniiDeSters.forEach(p->{
                Long idPrieten = p.getIdUser1().equals(id) ? p.getIdUser2(): p.getIdUser1();

                repoUtilizatori.findOne(idPrieten).ifPresent(prieten -> prieten.removeFriend(sters));
                repoPrietenii.delete(p.getId());
            });
    }

    // Returnam List în loc de Iterable folosind StreamSupport
    public List<Utilizator> getTotiUtilizatorii() {
        return StreamSupport.stream(repoUtilizatori.findAll().spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public List<Prietenie> getToatePrieteniile() {
        return StreamSupport.stream(repoPrietenii.findAll().spliterator(), false)
                .collect(Collectors.toCollection(ArrayList::new));
    }


    public void adaugaPrietenie(Long id1, Long id2){
        Utilizator u1 = repoUtilizatori.findOne(id1)
                .orElseThrow(()-> new IllegalArgumentException("Utilizatorul 1 nu exista!"));
        Utilizator u2 = repoUtilizatori.findOne(id2)
                .orElseThrow(()-> new IllegalArgumentException("Utilizatorul 2 nu exista!"));

        //det id maxim cu stream
        Long newId = StreamSupport.stream(repoPrietenii.findAll().spliterator(), false)
                .map(Prietenie::getId)
                .max(Long::compareTo)
                .orElse(0L);
        Prietenie p = new Prietenie(id1,id2);
        p.setId(newId+1);
        repoPrietenii.save(p);
        u1.addFriend(u2);
        u2.addFriend(u1);
    }

    public void stergePrietenie(Long id1, Long id2) {
        //filtrare foloisind predicate pentru a gasi fix pritenia vizata
        List<Prietenie> prieteniiDeSters = repoPrietenii.findAll(p->
                (p.getIdUser1().equals(id1) && p.getIdUser2().equals(id2)) ||
                 p.getIdUser1().equals(id2) && p.getIdUser2().equals(id1)
                );
        prieteniiDeSters.forEach(p-> repoPrietenii.delete(p.getId()));

        //curatarea listelor de prieteni
        repoUtilizatori.findOne(id1).ifPresent(u1->
                repoUtilizatori.findOne(id2).ifPresent(u2->
                {
                    u1.removeFriend(u2);
                    u2.removeFriend(u1);
                })
        );
    }

}
