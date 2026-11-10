package domain;

import java.time.LocalDateTime;
public class Prietenie  extends Entity<Long>{
    private Long idUser1;
    private Long idUser2;
    private LocalDateTime date;

    public Prietenie(Long idUser1, Long idUser2){
        this.idUser1 = idUser1;
        this.idUser2 = idUser2;
        this.date = LocalDateTime.now().withNano(0); //data la care s-a facut prietenia
    }

    public Long getIdUser1(){
        return idUser1;
    }
    public Long getIdUser2(){
        return idUser2;
    }
    public LocalDateTime getDate(){
        return date;
    }
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    @Override
    public boolean equals(Object o){
        if (this == o) return true;
        if (!(o instanceof Prietenie)) return false;
        Prietenie that = (Prietenie) o;
        return getIdUser1().equals(that.getIdUser1()) && getIdUser2().equals(that.getIdUser2());
    }
    @Override
    public int hashCode(){
        return getIdUser1().hashCode() + getIdUser2().hashCode();
    }
    @Override
    public String toString(){
        return "Prietenie{" +
                "idUser1=" + idUser1 +
                ", idUser2=" + idUser2 +
                ", date=" + date +
                '}';
    }
}
