

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

/**
 * This interface declares methods for querying a Books database.
 * Different implementations of this interface handles the connection and
 * queries to a specific DBMS and database, for example a MySQL or a MongoDB
 * database.
 * 
 * @author anderslm@kth.se
 */
public interface BooksDbInterface {
    
    /**
     * Connect to the database.
     * @param database
     * @return true on successful connection.
     */
    public boolean connect(String database) throws IOException, SQLException;
    
    public void disconnect() throws IOException, SQLException;
    
    public List<Book> searchBooksByTitle(String title) throws IOException, SQLException;
    
    public List<Book> searchBooksByISBN(String ISBN) throws IOException, SQLException;
    
    public List<Book> searchBooksByAuthor(String Author) throws IOException, SQLException;
    
    public List<Book> searchBooksByRating(String Rating) throws IOException, SQLException;
    
    public List<Book> searchBooksByGenre(String Genre) throws IOException, SQLException;
    
    public boolean addBookToDb(Book book) throws IOException, SQLException;
    
    public boolean isBookInDb(String ISBN)throws IOException, SQLException;
    
    public List<Author> searchAuthorById(String ID) throws IOException, SQLException;
    
    public List<Author> searchAuthorByName(String name) throws IOException, SQLException;
    //public List<Book> searchBooksbyAuthorID(String ID) throws IOException, SQLException;

    public void addAuthorToExistingBook(Author author, String ISBN) throws SQLException;
    // TODO: Add abstract methods for all inserts, deletes and queries 
    // mentioned in the instructions for the assignement.
}
