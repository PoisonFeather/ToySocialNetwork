Arhitectura:
Domain : modelele de date : utilizator,prietenie
Repository : abstractizeaza persistenta datelor. PrietenieFileRepository se ocupa strict de citirea si scrierea datelor in fisier.
Service: Logica
UI: interfata tip consola

Principii de design:
SingleResponsibility: fiecare modul face un singur lucur
Dependency Injection:  repo-urile sunt injectate prin constructor in clasele de service.
Strategy/Validator Pattern:  folosirea intefetei validator<t> permite definirea unor reguli stricte si interschimbabile pentru orice timp de de entitate.
