
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Representation of a book.
 * 
 * @author anderslm@kth.se
 */
public class Book {
    
    //private int bookId;
    private String ISBN; // should check format
    private String title;
    private Date published;
    //private String storyLine = "";
    private BookGenre genre;
    private int rating; // should check 1-5
    private ArrayList<Author> authors;
    // TODO: 
    // Add authors, and corresponding methods, to your implementation 
    // as well, i.e. "private ArrayList<Author> authors;"
    
    public Book(String ISBN, String title, Date published, BookGenre genre, int rating) 
    {
        if(!(isValidISBN(ISBN)) || !(rating > 0 && rating < 6))
        {
            throw new IllegalArgumentException("ISBN must be 13 digits && rating must be 1-5");
        }
        authors = new ArrayList<>();
        this.ISBN = ISBN;
        this.title = title;
        this.published = published;
        this.genre = genre;
        this.rating = rating;
        //this.authors.add(author);
    }
    
    public Book(String ISBN, String title, Date published, BookGenre genre, int rating, Author author)
    {
        this(ISBN, title, published, genre, rating);
        this.addAuthor(author);
    }
    
    
    public Book(String ISBN, String title, Date published, BookGenre genre, int rating, List<Author> authors)
    {
        this(ISBN, title, published, genre, rating);
        this.authors.addAll(authors);
    }
    
    public void addAuthors(List<Author> authors)
    {
        this.authors.addAll(authors);
    }
    
    public void addAuthor(Author author)
    {
        this.authors.add(author);
    }
    
    public static boolean isValidISBN(String ISBN)
    {
        return ISBN.matches("[0-9]{13}");
    }

    public String getISBN() {
        return ISBN;
    }

    public BookGenre getGenre() {
        return genre;
    }

    public int getRating() {
        return rating;
    }

    public ArrayList<Author> getAuthors() {
        return authors;
    }
    
    
    
//    public Book(String isbn, String title, Date published) {
//        this(-1, isbn, title, published); 
//    }
    
//    public void addAuthor(Author author)
//    {
//        this.authors.add(author);
//    }
    

    //public int getBookId() { return bookId; }
    public String getTitle() { return title; }
    public Date getPublished() { return published; }
    //public String getStoryLine() { return storyLine; }
    
//    public void setStoryLine(String storyLine) {
//        this.storyLine = storyLine;
//    }
    
    @Override
    public String toString() 
    {
        return "TITLE: " + title + " ISBN: " + ISBN + " DATE: " + this.published.toString() + " GENRE: " + this.genre + " RATING: " + this.rating + " Authors: " + this.authors.toString();
    }
}
