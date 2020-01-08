


import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Callable;

import javafx.application.Platform;
import javafx.event.Event;

import static javafx.scene.control.Alert.AlertType.*;

/**
 * The controller is responsible for handling user requests and update the view
 * (and in some cases the model).
 *
 * @author anderslm@kth.se
 */
public class Controller{

    private final BooksPane booksView; // view
    private final BooksDbInterface booksDb; // model
    private final LoginDialog loginDialog;
    private final ReviewDialog reviewDialog;

    public Controller(BooksDbInterface booksDb, BooksPane booksView) {
        this.booksDb = booksDb;
        this.booksView = booksView;
        this.loginDialog = new LoginDialog(this);
        this.reviewDialog = new ReviewDialog();
    }

    protected void onSearchSelected(String searchFor, SearchMode mode) 
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try {
                    if (searchFor != null && searchFor.length() > 0) 
                    {
                        List<Book> result = null;
                        switch (mode) 
                        {
                            case Title:
                                result = booksDb.searchBooksByTitle(searchFor);
                                break;
                            case ISBN:
                                result = booksDb.searchBooksByISBN(searchFor);
                                break;
                            case Author:
                                result = booksDb.searchBooksByAuthor(searchFor);
                                System.out.println("Confirm");
                                break;
                            case Rating:
                                result = booksDb.searchBooksByRating(searchFor);
                                break;
                            case Genre:
                                result = booksDb.searchBooksByGenre(searchFor);
                                break;
                            default:
                        }
                        if (result == null || result.isEmpty()) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("No results found.", INFORMATION);
                                }
                            });
                        } 
                        else {
                            final List<Book> finalRes = result;
                            // kan skrivas såhär också
                            Platform.runLater(() -> { booksView.displayBooks(finalRes);});
                        }
                    } 
                    else {
                        Platform.runLater(() -> { booksView.showAlertAndWait("Enter a search string!", WARNING);});
                    }
                    } catch (IOException | SQLException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> { booksView.showAlertAndWait("Database error.",ERROR);});
                    }
                }
        }.start();
    }
    
    
    protected void onSearchSelectedAuthor(String searchFor, AuthorSearchMode mode)
    {
        new Thread()
        {
            @Override
            public void run()
            {
                try {
                    if (searchFor != null && searchFor.length() > 0) {
                        List<Author> result = null;
                        switch (mode) {
                            case ID:
                                result = booksDb.searchAuthorById(searchFor);
                                break;
                            case Name:
                                result = booksDb.searchAuthorByName(searchFor);
                                break;
                            default:
                        }
                        if (result == null || result.isEmpty()) {
                            Platform.runLater(new Runnable() {
                                @Override
                                public void run() {
                                    booksView.showAlertAndWait("No results found.", INFORMATION);
                                }
                            });
                        } 
                        else {
                            final List<Author> finalRes = result;
                            // kan skrivas såhär också
                            Platform.runLater(() -> { booksView.displayAuthors(finalRes);});
                        }
                    } 
                    else {
                        Platform.runLater(() -> { booksView.showAlertAndWait("Enter a search string!", WARNING);});
                    }
                    } catch (IOException | SQLException e) {
                        Platform.runLater(() -> { booksView.showAlertAndWait("Database error.",ERROR);});
                    }
                }
        }.start();
    }
    
    
    protected void handleAddAuthorExistingBook(String ISBN){

        if(!(booksDb.getCurrentUser() == null)) {
            Optional<Author> result = booksView.getDialog().getAuthorDialog().showAndWait();
            new Thread() {
                @Override
                public void run() {
                    if (result.isPresent()) {
                        try {
                            booksDb.addAuthorToExistingBook(result.get(), ISBN);
                            Platform.runLater(() -> {
                                booksView.showAlertAndWait("Author added!", INFORMATION);
                            });
                        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                            Platform.runLater(() -> {
                                booksView.showAlertAndWait("Author(ID) already exist in book", WARNING);
                            });
                        } catch (java.sql.SQLSyntaxErrorException e) {
                            Platform.runLater(() -> {
                                booksView.showAlertAndWait("You must be logged in to access this functionality", INFORMATION);
                            });
                        } catch (SQLException e) {
                            Platform.runLater(() -> {
                                booksView.showAlertAndWait("Database error.", ERROR);
                            });
                        }
                    }
                }
            }.start();
        }else{
            booksView.showAlertAndWait("You must be logged in to access this functionality", INFORMATION);
        }
    }
    
    
    protected void handleAddBookToDb()
    {
        // lägg till tråd
        if(booksDb.isLoggedIn()) {
            Optional<Book> result = booksView.getDialog().showAndWait();
            new Thread() {
                @Override
                public void run() {
                    if (result.isPresent()) {
                        try {
                            booksDb.addBookToDb(result.get());
                            Platform.runLater(() -> {
                                booksView.showAlertAndWait("Book added!", INFORMATION);
                            });
                        } catch (java.sql.SQLIntegrityConstraintViolationException e) {
                            Platform.runLater(() -> { booksView.showAlertAndWait("Book already exist in database", INFORMATION); });
                        } catch (java.sql.SQLSyntaxErrorException e) {
                            Platform.runLater(() -> {
                                booksView.showAlertAndWait("You must be logged in to access this functionality", INFORMATION);
                            });
                        } catch (IOException | SQLException e) {
                            e.printStackTrace();
                            Platform.runLater(() -> { booksView.showAlertAndWait("Database error.", ERROR); });
                        }
                    }
                }
            }.start();
        }else{
            booksView.showAlertAndWait("You must be logged in to access this functionality", INFORMATION);
        }
        
    }


    protected void handleLogoutAuthorizedUser(){
        try {
            booksDb.logoutAuthorizedUser();
        } catch (SQLException e) {
            booksView.showAlertAndWait("Database error", WARNING);
        }
    }

    protected boolean handleVerifyAccountExist(User user, Event event){
        boolean accountExist = false;
        try {
             accountExist = new Callable<Boolean>(){
                @Override
                public Boolean call() throws Exception {
                    try {
                        if(!booksDb.verifyAccount(user)){
                            //event.consume();
                            Platform.runLater(()-> booksView.showAlertAndWait("Wrong username/password", WARNING));
                            return false;
                        }
                        else{
                            return true;
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        Platform.runLater(() -> { booksView.showAlertAndWait("Database error.", ERROR);});
                    }
                    return true;
                }
            }.call();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(() -> {booksView.showAlertAndWait("Database error.", ERROR);});
        }
        return accountExist;
    }

    protected void handleLogin(){
        Optional<User> result = this.loginDialog.showAndWait();
        new Thread(){
            @Override
            public void run(){
                if(result.isPresent()){
                    try {
                        booksDb.loginUser(result.get());
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();

    }

    protected void handleAddReview(String ISBN){
        if(booksDb.isLoggedIn()) {
            Optional<String> result = this.reviewDialog.showAndWait();
            new Thread() {
                @Override
                public void run() {
                    if (result.isPresent()) {
                        try {
                            booksDb.addReview(ISBN, result.get());

                        } catch(java.sql.SQLIntegrityConstraintViolationException e){
                            Platform.runLater(()-> booksView.showAlertAndWait("You have already written a review", WARNING));
                        }
                        catch (SQLException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Platform.runLater(()-> booksView.showAlertAndWait("You must enter text", WARNING));
                    }
                }
            }.start();
        }else{
            booksView.showAlertAndWait("You must be logged in to access this functionality", INFORMATION);
        }
    }

    protected void handleWrongInput(String wrongInputText){
        booksView.showAlertAndWait(wrongInputText, WARNING);
    }


    protected void handleDeleteBook(String ISBN){
       if(booksDb.isLoggedIn()) {
           new Thread() {
               @Override
               public void run() {
                   try {
                       booksDb.deleteBook(ISBN);
                       Platform.runLater(()-> booksView.showAlertAndWait("Book deleted!", INFORMATION));
                   } catch (SQLException e) {
                       e.printStackTrace();
                   }
               }
           }.start();
       }
       else{
           booksView.showAlertAndWait("You must be logged in to access this functionality", INFORMATION);
       }
    }




    // TODO:
    // Add methods for all types of user interaction (e.g. via  menus).

}
