package ui;

import service.SocialNetworkService;
import domain.Utilizator;
import java.util.Scanner;
import service.ComunitatiService;

public class ConsoleUI {
    private final SocialNetworkService service;
    private final Scanner scanner;
    private final ComunitatiService comunitatiService;
    public ConsoleUI(SocialNetworkService service, ComunitatiService comunitatiService) {
        this.service = service;
        this.scanner = new Scanner(System.in);
        this.comunitatiService = comunitatiService;
    }

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
            System.out.println("0. Iesire");
            System.out.print("Alege o optiune: ");

            String optiune = scanner.nextLine();
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
                    case "0": return;
                    default: System.out.println("Optiune invalida!");
                }
            } catch (domain.validators.ValidationException ve) {
                System.out.println("Eroare la validare: " + ve.getMessage());
            }catch(repository.RepositoryException re){
                System.out.println("Eroare de sistem: " +re.getMessage());
            }catch(IllegalArgumentException iae){
                System.out.println("Eroare: " + iae.getMessage());
            }
        }
    }

    private void adaugaUtilizator() {
        System.out.print("Prenume: ");
        String prenume = scanner.nextLine();
        System.out.print("Nume: ");
        String nume = scanner.nextLine();
        service.adaugaUtilizator(prenume, nume);
        System.out.println("Utilizator adaugat cu succes!");
    }

    private void stergeUtilizator() {
        System.out.print("ID Utilizator: ");
        Long id = Long.parseLong(scanner.nextLine());
        service.stergeUtilizator(id);
        System.out.println("Utilizator si prieteniile sale au fost sterse (dacă exista).");
    }

    private void afiseazaUtilizatori() {
        System.out.println("Lista utilizatori:");
        for (Utilizator u : service.getTotiUtilizatorii()) {
            System.out.println(u);
        }
    }

    private void adaugaPrietenie() {
        System.out.print("ID Utilizator 1: ");
        Long id1 = Long.parseLong(scanner.nextLine());
        System.out.print("ID Utilizator 2: ");
        Long id2 = Long.parseLong(scanner.nextLine());
        service.adaugaPrietenie(id1, id2);
        System.out.println("Prietenie adaugată cu succes!");
    }

    private void stergePrietenie() {
        System.out.print("ID Utilizator 1: ");
        Long id1 = Long.parseLong(scanner.nextLine());
        System.out.print("ID Utilizator 2: ");
        Long id2 = Long.parseLong(scanner.nextLine());
        service.stergePrietenie(id1, id2);
        System.out.println("Prietenie stearsă (daca exista).");
    }
    private void afiseazaPrieteniile() {
        System.out.println("Lista prietenii:");
        for (domain.Prietenie p : service.getToatePrieteniile()) {
            System.out.println("Prietenie intre ID " + p.getIdUser1() + " si ID " + p.getIdUser2() + " (creata la: " + p.getDate() + ")");
        }
    }
    private void afiseazaCeaMaiSociabilaComunitate() {
        System.out.println("Cea mai sociabilă comunitate este formata din:");
        for (Utilizator u : comunitatiService.getCeaMaiSociabilaComunitate()) {
            System.out.println("- " + u.getFirstName() + " " + u.getLastName());
        }
    }
}