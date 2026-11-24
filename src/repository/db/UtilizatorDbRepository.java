package repository.db;
import domain.Utilizator;
import domain.validators.Validator;
import repository.Repository;
import repository.RepositoryException;
import util.paging.Page;
import util.paging.Pageable;
import util.paging.PagingRepository;

import java.util.Optional;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;


public class UtilizatorDbRepository  implements PagingRepository<Long,Utilizator> {

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

    @Override public Iterable<Utilizator> findAll() {
        List<Utilizator> users = new ArrayList<>();
        String sql = "SELECT id, first_name,last_name FROM users ORDER BY id";
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql);
            ResultSet  rs = ps.executeQuery()){
            while(rs.next()){
                users.add(extractEntity(rs));
            }
        } catch(SQLException e){
            throw new RepositoryException("Eroare la baza de date cand cauta toti utilizatorii: "+e.getMessage(),e);
        }
        return users;
    }
    @Override public Optional<Utilizator> save(Utilizator entity) {
        if(entity == null){
            throw new IllegalArgumentException("Entitatea nu poate fi null");
        }
        validator.validate(entity);
        String sql = "INSERT INTO users (first_name,last_name) VALUES (?,?)";
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS)){
            ps.setString(1,entity.getFirstName());
            ps.setString(2,entity.getLastName());

            int affectedRows = ps.executeUpdate();
            if(affectedRows>0){
                try(ResultSet generatedKeys = ps.getGeneratedKeys()){
                    if(generatedKeys.next()){
                        entity.setId(generatedKeys.getLong(1));
                    }
                }
                return Optional.empty();
            }
        } catch(SQLException e){
            throw new RepositoryException("Eroare la db cand am salvat user: "+e.getMessage(),e);
        }
        return Optional.of(entity);

    }
    @Override public Optional<Utilizator> delete(Long id){
        if(id == null){
            throw new IllegalArgumentException("Id-ul nu poate fi null!");
        }
        Optional<Utilizator> existing = findOne(id);
        if(existing.isEmpty()){
            return Optional.empty();
        }
        String sql = "DELETE FROM users WHERE id = ?";
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setLong(1,id);
            int affectedRows = ps.executeUpdate();
            if(affectedRows > 0){
                return existing;
            }
        }catch(SQLException e){
            throw new RepositoryException("Eroare la db cand am vrut sa sterg user: "+e.getMessage(),e);
        }
        return Optional.empty();
    }
    @Override public Optional<Utilizator> update(Utilizator entity){
        if(entity == null){
            throw new IllegalArgumentException("Entitatea nu poate fi null");
        }
        if(entity.getId() == null){
            throw new IllegalArgumentException("Id-ul entitatii nu poate fii null");
        }
        validator.validate(entity);
        String sql = "UPDATE users SET first_name = ?, last_name = ?, WHERE id=?";
        try(Connection conn = getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)){
            ps.setString(1,entity.getFirstName());
            ps.setString(2,entity.getLastName());
            ps.setLong(3,entity.getId());

            int affectedRows = ps.executeUpdate();
            if(affectedRows > 0){
                return Optional.empty();
            }
        }catch(SQLException e){
            throw new RepositoryException("Eroare la db cand am dat update la user "+ e.getMessage(),e);
        }
        return Optional.of(entity);
    }

    /**
     * Retrieves a paginated slice of users ordered by ID, executing both the total count
     * and the paginated query on the same db connection
     *
     * @param pageable the pagination parameters (page number and page size)
     * @return a {@link Page} containing the users on the requested page and the total user count
     * @throws IllegalArgumentException if {@code pageable is null}
     * @throws RepositoryException if  a db access error occurs
     */

    @Override
    public Page<Utilizator> findAllOnPage(Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable nu poate fi null!");
        }

        List<Utilizator> users = new ArrayList<>();
        int totalCount = 0;

        String countSql = "SELECT COUNT(*) AS total FROM users";
        String pageSql = "SELECT id, first_name, last_name FROM users ORDER BY id LIMIT ? OFFSET ?";

        try (Connection conn = getConnection()) {
            // Numaram totalul utilizatorilor pe aceeasi conexiune
            try (PreparedStatement countPs = conn.prepareStatement(countSql);
                 ResultSet countRs = countPs.executeQuery()) {
                if (countRs.next()) {
                    totalCount = countRs.getInt("total");
                }
            }

            // Extragem doar pagina curenta folosind LIMIT si OFFSET
            try (PreparedStatement pagePs = conn.prepareStatement(pageSql)) {
                pagePs.setInt(1, pageable.getPageSize());
                pagePs.setInt(2, pageable.getPageNumber() * pageable.getPageSize());

                try (ResultSet rs = pagePs.executeQuery()) {
                    while (rs.next()) {
                        long id = rs.getLong("id");
                        String firstName = rs.getString("first_name");
                        String lastName = rs.getString("last_name");
                        Utilizator u = new Utilizator(firstName, lastName);
                        u.setId(id);
                        users.add(u);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RepositoryException("Database error while retrieving paginated users: " + e.getMessage(), e);
        }

        return new Page<>(users, totalCount);
    }
}
