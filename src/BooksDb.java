/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A mock implementation of the BooksDBInterface interface to demonstrate how to
 * use it together with the user interface.
 *
 * Your implementation should access a real database.
 *
 * @author anderslm@kth.se
 */
public class BooksDb implements BooksDbInterface {

    private List<Book> books;
    private Connection myConn;
    private PreparedStatement qStmt;
    private ResultSet qResult;
    PreparedStatement getAuthorsStmt;
    ResultSet authorRs;
    public BooksDb() 
    {
        //books = Arrays.asList(DATA);
        myConn = null;
        qStmt = null;
        qResult = null;
    }

    @Override
    public boolean connect(String database) throws SQLException
    {
        database = "sakila";
        String server
                = "jdbc:mysql://localhost:3306/" + database
                + "?UseClientEnc=UTF8";
        String user = "myuser";
        String password = "mypassword";
        myConn = DriverManager.getConnection(server, user, password);
        return myConn == null;
    }

    @Override
    public void disconnect() throws IOException, SQLException 
    {
        if(myConn != null)
        {
            try 
            {
                myConn.close();
            } catch (SQLException ex) 
            {
                System.out.println("wat");
            }
        }
        qStmt.close();
        qResult.close();
    }

    @Override
    public List<Book> searchBooksByTitle(String searchTitle) throws IOException, SQLException  
    {

        String sql = "SELECT * FROM T_Book WHERE title LIKE ?";
        qStmt = myConn.prepareStatement(sql);
        searchTitle = searchTitle.toLowerCase();
        qStmt.setString(1, "%" + searchTitle + "%");
        this.qResult = qStmt.executeQuery();
        return this.getResultBook(qResult);
    }

    @Override
    public List<Book> searchBooksByISBN(String ISBN) throws IOException, SQLException 
    {
        String sql = "SELECT * FROM T_Book WHERE ISBN LIKE ?";
        qStmt = myConn.prepareStatement(sql);
        ISBN = ISBN.toLowerCase();
        qStmt.setString(1, "%" + ISBN + "%");
        this.qResult = qStmt.executeQuery();
        return this.getResultBook(qResult);
    }

    @Override
    public List<Book> searchBooksByAuthor(String authorToSearch) throws IOException, SQLException 
    {
        // FIX INORDER TO BE ABLE TO SEARCH E.G WILLIAM S
        String sql = "SELECT* FROM t_book WHERE T_Book.ISBN IN( SELECT t_book_author.ISBN  FROM t_book_author WHERE t_book_author.authorID IN (SELECT T_Author.authorID FROM t_author WHERE CONCAT( t_author.firstName,  ' ', t_author.lastName ) LIKE  ?));";
        qStmt = myConn.prepareStatement(sql);
        authorToSearch = authorToSearch.toLowerCase();
        
        qStmt.setString(1, "%" + authorToSearch + "%");
        this.qResult = qStmt.executeQuery();
        return this.getResultBook(qResult);
    }

    @Override
    public List<Book> searchBooksByRating(String rating) throws IOException, SQLException 
    {
        String sql = "SELECT * FROM T_Book WHERE rating = ?";
        qStmt = myConn.prepareStatement(sql);
        qStmt.setString(1,rating);
        this.qResult = qStmt.executeQuery();
        return this.getResultBook(qResult);
    }
    
    private List<Book> getResultBook(ResultSet qResult) throws SQLException
    {
        String sql;
        List<Book> result = new ArrayList<>();
        
         while(this.qResult.next())
        {   
            sql = "SELECT* FROM T_Author WHERE T_Author.authorID IN ( SELECT authorID FROM T_Book_Author WHERE T_Book_Author.ISBN = ?)";
            getAuthorsStmt = myConn.prepareStatement(sql);
            getAuthorsStmt.setString(1, qResult.getString("ISBN"));
            authorRs = getAuthorsStmt.executeQuery();
            List<Author> authors = new ArrayList<>();
            while(authorRs.next())
            {
                Author author = new Author(authorRs.getInt("authorID"),
                        authorRs.getString("firstName"), authorRs.getString("lastName"),
                        authorRs.getDate("dob"));
                authors.add(author);
            }
            Book b = new Book(qResult.getString("ISBN"),
                    qResult.getString("title"),
                    qResult.getDate("dateCreated"),
                    BookGenre.valueOf(qResult.getString("genre")),
                    qResult.getInt("rating"), authors);
            System.out.println(b.toString()+ " FROM BOOK RESULT");
            result.add(b);
        }
         return result;
         
    }
    
    @Override
    public List<Book> searchBooksByGenre(String genre) throws IOException, SQLException 
    {
        String sql = "SELECT * FROM T_Book WHERE genre LIKE ?";
        qStmt = myConn.prepareStatement(sql);
        genre = genre.toLowerCase();
        qStmt.setString(1, "%" + genre + "%");
        this.qResult = qStmt.executeQuery();
        return this.getResultBook(qResult);
    }

    public void addAuthorToExistingBook(Author author, String ISBN) throws SQLException {
        try {
            ArrayList<Author> authors = new ArrayList<>();
            authors.add(author);

            myConn.setAutoCommit(false);
            this.addAuthorToDbIfNotExist(authors);

            qStmt = myConn.prepareStatement("INSERT INTO T_Book_Author(ISBN, authorID) VALUES(?, ?)");
            System.out.println(ISBN + " FROM DB ISBN");
            qStmt.setString(1, ISBN);
            qStmt.setInt(2, author.getAuthorID());
            qStmt.executeUpdate();


            myConn.commit();
            System.out.println(author.getAuthorID() + " AUTHOR ID FROM DB");
        } catch (SQLException e) {
            throw e;
        }finally {
            myConn.setAutoCommit(true);
        }
    }

    @Override
    public boolean addBookToDb(Book book) throws IOException, SQLException
    {
        myConn.setAutoCommit(false);
        this.addAuthorToDbIfNotExist(book.getAuthors());
        
        qStmt = myConn.prepareStatement("INSERT INTO T_Book(ISBN, genre, title, dateCreated, rating) VALUES(?, ?, ?, ?, ?)");
        qStmt.setString(1,book.getISBN());
        qStmt.setString(2,book.getGenre().name());
        qStmt.setString(3,book.getTitle());
        qStmt.setString(4,book.getPublished().toString());
        qStmt.setInt(5, book.getRating());
        qStmt.executeUpdate();
        
        for(int i = 0; i < book.getAuthors().size(); i++)
        {
            qStmt = myConn.prepareStatement("INSERT INTO T_Book_Author(ISBN, authorID) VALUES(?, ?)");
            qStmt.setString(1, book.getISBN());
            qStmt.setInt(2, book.getAuthors().get(i).getAuthorID());
            qStmt.executeUpdate();
        }
        
        
        myConn.commit();
        myConn.setAutoCommit(true);
        
        return true; // ?
    }
    
    private void addAuthorToDbIfNotExist(ArrayList<Author> authors) throws SQLException
    {
        qStmt = myConn.prepareStatement("SELECT * FROM T_Author WHERE authorID = ?");
        
        PreparedStatement aStmt;
        
        for(int i = 0; i < authors.size(); i++)
        {
            qStmt.setInt(1, authors.get(i).getAuthorID());
            this.qResult = qStmt.executeQuery();
            if(!this.qResult.next())
            {
                aStmt = myConn.prepareStatement("INSERT INTO T_Author(authorID, firstName, lastName, dob) VALUES(?, ?, ?, ?)");
                aStmt.setInt(1,authors.get(i).getAuthorID());
                aStmt.setString(2,authors.get(i).getFirstName());
                aStmt.setString(3, authors.get(i).getLastName());
                aStmt.setString(4,authors.get(i).getDob().toString());
                aStmt.executeUpdate();
                
                System.out.println(authors.get(i).getFirstName());
                System.out.println(authors.get(i).getLastName());
                System.out.println(authors.get(i).getDob().toString());
            }
        }
        
    }
//   @Override
//    public List<Book> searchBooksbyAuthorID(String ID) throws IOException, SQLException{
//        String sql = "SELECT * FROM T_Book_Author WHERE authorID = ?";
//        System.out.println("here");
//        qStmt = myConn.prepareStatement(sql);
//        qStmt.setInt(1, Integer.parseInt(ID));
//        this.qResult = qStmt.executeQuery();
//        
//        //return this.getResultBook(qResult);
//        
//        
//        
//        while(qResult.next())
//        {
//            Author author = new Author(qResult.getInt("authorID"), qResult.getString("firstName"), qResult.getString("lastName"), qResult.getDate("dob"));
//        }
//        
//        sql = "SELECT * FROM T_Book_Author WHERE authorID = ?";
//        
//       
//    }
    
    @Override
    public boolean isBookInDb(String ISBN)throws IOException, SQLException
    {
        int count = 0;
        
        qStmt = myConn.prepareStatement("SELECT * FROM T_Book WHERE ISBN = ?");
        qStmt.setString(1, ISBN);
        this.qResult = qStmt.executeQuery();
        
        while(qResult.next())
        {
            count++;
        }
        return count > 0;
    }
    
    public boolean isAuthoInDb()
    {
                 return true;
    }

    @Override
    public List<Author> searchAuthorById(String ID) throws IOException, SQLException 
    {
        String sql = "SELECT * FROM T_Author WHERE authorID = ?";
        qStmt = myConn.prepareStatement(sql);
        qStmt.setInt(1, Integer.parseInt(ID));
        this.qResult = qStmt.executeQuery();
        
        List<Author> authors = new ArrayList<>();
        
        while(qResult.next())
        {
            Author a = new Author(qResult.getInt("authorID"), qResult.getString("firstName"), qResult.getString("lastName"), qResult.getDate("dob"));
            System.out.println(qResult.getInt("authorID"));
            System.out.println(qResult.getString("firstName"));
            System.out.println(qResult.getString("lastName"));
            System.out.println(qResult.getDate("dob").toString());
            authors.add(a);
        }
        
        System.out.println(authors.toString() + "From DATABASE");
        return authors;
    }

    @Override
    public List<Author> searchAuthorByName(String name) throws IOException, SQLException 
    {
        String sql = "SELECT * FROM t_author WHERE CONCAT( t_author.firstName,  ' ', t_author.lastName ) LIKE ?;";
        qStmt = myConn.prepareStatement(sql);
        qStmt.setString(1, "%" + name + "%");
        this.qResult = qStmt.executeQuery();
        
        List<Author> authors = new ArrayList<>();
        
        while(qResult.next())
        {
            authors.add(new Author(qResult.getInt("authorID"), qResult.getString("firstName"), qResult.getString("lastName"), qResult.getDate("dob")));
        }
        return authors;
    }
    
    

    
    
}
