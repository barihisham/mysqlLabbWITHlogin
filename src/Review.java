import java.sql.Date;

public class Review {


    private String userText;
    private Date date;
    private User user;

    public Review(String userText, Date date, User user){
        this.userText = userText;
        this.date = date;
        this.user = user;
    }

    public String getUserText() {
        return userText;
    }

    public Date getDate() {
        return date;
    }

    public User getUser() {
        return user;
    }

    @Override
    public String toString() {
        return this.userText + " [" + this.user + "]";
    }
}
