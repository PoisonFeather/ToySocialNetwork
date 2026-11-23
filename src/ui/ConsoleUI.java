package ui;

import domain.Prietenie;
import domain.Utilizator;
import domain.validators.ValidationException;
import repository.RepositoryException;
import service.ComunitatiService;
import service.SocialNetworkService;
import util.paging.Page;

import java.util.Scanner;
import java.util.function.IntFunction;

/**
 * Console-based user interface for interacting with the social network application.
 */
public class ConsoleUI {
    private final SocialNetworkService service;
    private final Scanner scanner;
    private final ComunitatiService comunitatiService;
    private final int pageSize;

    /**
     * Constructs the console UI with required services and configured page size.
     *
     * @param service           the main social network service
     * @param comunitatiService the service handling community graph computations
     * @param pageSize          the number of items displayed per page
     */
    public ConsoleUI(SocialNetworkService service, ComunitatiService comunitatiService, int pageSize) {
        this.service = service;
        this.scanner = new Scanner(System.in);
        this.comunitatiService = comunitatiService;
        this.pageSize = pageSize;
    }

    /**
     * Starts the main interactive console loop.
     */
    public void run() {
        while (true) {
            System.out.println("\n--------------");
            System.out.println("1. Adauga utilizator");
            System.out.println("2. Sterge utilizator");
            System.out.println("3. Afiseaza toti utilizatorii");
            System.out.println("4. Adauga prietenie");
            System.out.println("5. Sterge prietenie");
            System.out.println("6. Afiseaza toate prieteniile");
            System.out.println("7. Afiseaza numarul de comunitati");
            System.out.println("8. Afiseaza cea mai sociabila comunitate");
            System.out.println("9. Afiseaza utilizatorii (paginat)");
            System.out.println("10. Afiseaza prietenii unui utilizator (paginat)");
            System.out.println("0. Iesire");
            System.out.print("Alege o optiune: ");

            String optiune = scanner.nextLine().trim();
            try {
                switch (optiune) {
                    case "1": adaugaUtilizator(); break;
                    case "2": stergeUtilizator(); break;
                    case "3": afiseazaUtilizatori(); break;
                    case "4": adaugaPrietenie(); break;
                    case "5": stergePrietenie(); break;
                    case "6": afiseazaPrieteniile(); break;
                    case "7": System.out.println("Numar comunitati (componente conexe): " + comunitatiService.getNumarComunitati()); break;
                    case "8": afiseazaCeaMaiSociabilaComunitate(); break;
                    case "9": afiseazaUtilizatoriPaginat(); break;
                    case "10": afiseazaPrieteniPaginat(); break;
                    case "0": return;
                    default: System.out.println("Optiune invalida!");
                }
            } catch (ValidationException ve) {
                System.out.println("Eroare la validare: " + ve.getMessage());
            } catch (RepositoryException re) {
                System.out.println("Eroare de sistem: " + re.getMessage());
            } catch (IllegalArgumentException | UnsupportedOperationException e) {
                System.out.println("Eroare: " + e.getMessage());
            }
        }
    }

    private void adaugaUtilizator() {
        System.out.print("Prenume: ");
        String prenume = scanner.nextLine();
        System.out.print("Nume: ");
        String nume = scanner.nextLine();
        Utilizator saved = service.adaugaUtilizator(prenume, nume);
        System.out.println("Utilizator adaugat cu succes! ID generat: " + saved.getId());
    }

    private void stergeUtilizator() {
        System.out.print("ID Utilizator: ");
        Long id = Long.parseLong(scanner.nextLine().trim());
        service.stergeUtilizator(id);
        System.out.println("Utilizator si prieteniile sale au fost sterse (daca exista).");
    }

    private void afiseazaUtilizatori() {
        System.out.println("Lista utilizatori:");
        for (Utilizator u : service.getTotiUtilizatorii()) {
            System.out.println(u);
        }
    }

    private void adaugaPrietenie() {
        System.out.print("ID Utilizator 1: ");
        Long id1 = Long.parseLong(scanner.nextLine().trim());
        System.out.print("ID Utilizator 2: ");
        Long id2 = Long.parseLong(scanner.nextLine().trim());
        service.adaugaPrietenie(id1, id2);
        System.out.println("Prietenie adaugata cu succes!");
    }

    private void stergePrietenie() {
        System.out.print("ID Utilizator 1: ");
        Long id1 = Long.parseLong(scanner.nextLine().trim());
        System.out.print("ID Utilizator 2: ");
        Long id2 = Long.parseLong(scanner.nextLine().trim());
        service.stergePrietenie(id1, id2);
        System.out.println("Prietenie stearsa (daca exista).");
    }

    private void afiseazaPrieteniile() {
        System.out.println("Lista prietenii:");
        for (Prietenie p : service.getToatePrieteniile()) {
            System.out.println("Prietenie intre ID " + p.getIdUser1() + " si ID " + p.getIdUser2() + " (creata la: " + p.getDate() + ")");
        }
    }

    private void afiseazaCeaMaiSociabilaComunitate() {
        System.out.println("Cea mai sociabila comunitate este formata din:");
        for (Utilizator u : comunitatiService.getCeaMaiSociabilaComunitate()) {
            System.out.println("- " + u.getFirstName() + " " + u.getLastName());
        }
    }

    private void afiseazaUtilizatoriPaginat() {
        navigheazaPagini(page -> service.getUtilizatoriOnPage(page, pageSize));
    }

    private void afiseazaPrieteniPaginat() {
        System.out.print("ID Utilizator: ");
        Long userId = Long.parseLong(scanner.nextLine().trim());
        navigheazaPagini(page -> service.getPrieteniOnPage(userId, page, pageSize));
    }

    private void navigheazaPagini(IntFunction<Page<Utilizator>> pageFetcher) {
        int currentPage = 0;
        while (true) {
            Page<Utilizator> page = pageFetcher.apply(currentPage);
            int totalElements = page.getTotalNumberOfElements();
            int totalPages = Math.max(1, (int) Math.ceil((double) totalElements / pageSize));

            System.out.println("\n--- Pagina " + (currentPage + 1) + " din " + totalPages + " (Total: " + totalElements + ") ---");
            boolean hasElements = false;
            for (Utilizator u : page.getElementsOnPage()) {
                System.out.println(u);
                hasElements = true;
            }
            if (!hasElements) {
                System.out.println("(Niciun element pe aceasta pagina)");
            }

            System.out.println("[n] Pagina urmatoare | [p] Pagina anterioara | [0] Inapoi la meniu");
            System.out.print("Comanda: ");
            String cmd = scanner.nextLine().trim().toLowerCase();

            if (cmd.equals("n")) {
                if (currentPage + 1 < totalPages) {
                    currentPage++;
                } else {
                    System.out.println("Esti deja pe ultima pagina!");
                }
            } else if (cmd.equals("p")) {
                if (currentPage > 0) {
                    currentPage--;
                } else {
                    System.out.println("Esti deja pe prima pagina!");
                }
            } else if (cmd.equals("0")) {
                break;
            } else {
                System.out.println("Comanda invalida!");
            }
        }
    }
}