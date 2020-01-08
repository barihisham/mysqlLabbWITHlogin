
import java.util.ArrayList;
import java.sql.Date;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author barih
 */
public class Author {
    private int authorID;
    private String firstName;
    private String lastName;
    private Date dob;
    private User addedBy;
    private ArrayList<Book> books;
    
    public Author(int authorID, String firstName, String lastName, Date dob)
    {
        this.authorID = authorID;
        this.firstName = firstName;
        this.lastName = lastName;
        this.dob = dob;
    }

    public void addAddedBy(User addedBy){
        this.addedBy = addedBy;
    }

    public void addBook(Book book)
    {
        this.books.add(book);
    }

    public int getAuthorID() {
        return authorID;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public Date getDob() {
        return dob;
    }
    
    @Override
    public String toString()
    {

        String s = this.firstName + " " + this.lastName + " [" + this.addedBy + "]";
        return s;
    }
}
