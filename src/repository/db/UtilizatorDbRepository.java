package repository.db;
import domain.Utilizator;
import domain.validators.Validator;
import repository.Repository;
import repository.RepositoryException;

import java.util.Optional;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UtilizatorDbRepository  implements Repository<Long,Utilizator> {

    /**
     * Construct a new database repository for users.
     *
     * @param url the jdbc database url
     * @param username the db username
     * @param password the db password
     * @param validator the validator used for user entities
     */
    private String url;
    private String username;
    private String password;
    private Validator<Utilizator> validator;

    public UtilizatorDbRepository(String url,String username, String password, Validator<Utilizator> validator){
        this.url = url;
        this.username = username;
        this.password = password;
        this.validator = validator;
    }
    //handles connection to the db
    private Connection getConnection() throws SQLException{
        return DriverManager.getConnection(url,username,password);
    }
    //extracts utilizator
    private Utilizator extractEntity(ResultSet rs) throws SQLException{
        Long id = rs.getLong("id");
        String firstName = rs.getString("first_name");
        String lastName = rs.getString("last_name");
        Utilizator user = new Utilizator(firstName,lastName);
        user.setId(id);
        return user;
    }

    @Override public Optional<Utilizator> findOne(Long id){
        if(id == null){
            throw new IllegalArgumentException("ID-ul nu poate fi null!");
        }

        String sql = "SELECT id, first_name,last_name FROM users WHERE id = ?";
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
                ps.setLong(1,id);
                try(ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return Optional.of(extractEntity(rs));
                    }
                }
        }catch(SQLException e){
            throw new RepositoryException("Eroare la baza de date cautand utilizatorul: "+ e.getMessage(),e);
        }
        return Optional.empty();

    }

    @Override public Iterable<Utilizator> findAll() {throw new UnsupportedOperationException("Baza de date nu e implementata inca!");};
    @Override public Optional<Utilizator> save(Utilizator entity) {throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
    @Override public Optional<Utilizator> delete(Long id){ throw new UnsupportedOperationException("Baza de date nu a fost implementata inca");};
    @Override public Optional<Utilizator> update(Utilizator entity){throw new UnsupportedOperationException("Baza de date nu a fost implementata inca!");};
}
