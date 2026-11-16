package repository.db;

import domain.Prietenie;
import domain.validators.Validator;
import repository.Repository;
import repository.RepositoryException;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PrietenieDbRepository implements Repository<Long,Prietenie> {
    private String url;
    private String username;
    private String password;
    private Validator<Prietenie> validator;

    /**
     * Construieste un repository pentru DB
     * @param url url-ul pentru jdbc
     * @param username username-ul pentru db
     * @param password parola pentru db
     * @param validator validator pentru entitatile de prietenie
     */

    public PrietenieDbRepository(String url, String username, String password, Validator<Prietenie> validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;

    }

    private Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url,username,password);
    }

    private long packId(long id1,long id2){
        return (id1<<32)  | (id2 & 0xFFFFFFFFL);
    }
    private long[] unpackId(long packedId){
        long id1 = packedId>>>32;
        long id2 = packedId & 0xFFFFFFFFL;
        return new long[]{id1,id2};
    }

    private Prietenie extractEntity(ResultSet rs) throws SQLException{
        long id1 = rs.getLong("id1");
        long id2 = rs.getLong("id2");
        Timestamp friendsFrom = rs.getTimestamp("friends_from");

        Prietenie prietenie = new Prietenie(id1,id2);
        prietenie.setId(packId(id1,id2));
        if(friendsFrom!=null){
            prietenie.setDate(friendsFrom.toLocalDateTime());
        }
        return prietenie;
    }
    @Override public Optional<Prietenie> findOne(Long id){
        if(id == null) throw new IllegalArgumentException("Id-ul nu poate fi null!");
        long[] ids = unpackId(id);
        String sql = "SELECT id1, id2, friends_from FROM friendships WHERE id1=? AND id2=?";
        try (Connection conn = getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setLong(1, ids[0]);
            ps.setLong(2, ids[1]);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(extractEntity(rs));
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Database error while finding friendship: " + e.getMessage(), e);
        }
        return Optional.empty();
    }
    @Override public Optional<Prietenie> save(Prietenie prieten){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
    @Override public Optional<Prietenie> delete(Long id){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
    @Override public Optional<Prietenie> update(Prietenie prieten){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
    @Override public Iterable<Prietenie> findAll(){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca");};

}
