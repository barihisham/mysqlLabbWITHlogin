import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/**
 * A simplified example of a form, using JavaFX Dialog and DialogPane. Type
 * parameterized for FooBook.
 *
 * @author Anders Lindström, anderslm@kth.se
 */
public class AddBookDialog extends Dialog<Book> {

    private final TextField titleField = new TextField("test06");
    private final TextField isbnField = new TextField("1534567890124");
    private final TextField publishedField = new TextField("2000-10-20");
    private final TextField ratingField = new TextField("5");
    private final TextField firstNameField = new TextField("lastNmaelabla");
    private final TextField lastNameField = new TextField("fistNameblabla");
    private final TextField dobField = new TextField("1999-10-24");
    private final TextField authorIdField = new TextField();
    private final DatePicker datePublished = new DatePicker();
    private final DatePicker dobPicker = new DatePicker();
    //private final LocalDate date = new LocalDate();
    private final ComboBox<BookGenre> genreChoice = new ComboBox(FXCollections.observableArrayList(BookGenre.values()));
    //private final Controller controller;
    private final AddAuthorDialog authorDialog;
    private final BooksDb booksDb;
    public AddBookDialog(BooksDb booksDb )
    {
        this.booksDb = booksDb;
        authorDialog = new AddAuthorDialog();
        buildAddBookDialog();
        //this.controller = controller;
    }

    public AddAuthorDialog getAuthorDialog() {
        return authorDialog;
    }

    private void buildAddBookDialog() {

        this.setTitle("Add a new author");
        this.setResizable(false); // really?

        
        Button addAuthorButton = new Button("Add more authours...");
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.add(new Label("Title "), 1, 1);
        grid.add(titleField, 2, 1);
        grid.add(new Label("ISBN "), 1, 2);
        grid.add(isbnField, 2, 2);
        grid.add(new Label("Published "), 1, 3);
        grid.add(datePublished, 2, 3);
        grid.add(new Label("Rating "), 1, 4);
        grid.add(ratingField, 2, 4);
        grid.add(new Label("Genre "), 1, 5);
        grid.add(genreChoice, 2, 5);
        grid.add(new Label("Authors Firstname "), 1, 6);
        grid.add(firstNameField, 2, 6);
        grid.add(new Label("Authors Lastname "), 1, 7);
        grid.add(lastNameField, 2, 7);
        grid.add(new Label("Authors birthday "), 1, 8);
        grid.add(dobPicker, 2, 8);
        grid.add(new Label("AuthorID "), 1, 9);
        grid.add(authorIdField, 2, 9);
        grid.add(addAuthorButton, 2, 10);
        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);
        
        ButtonType buttonTypeCancel= new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getDialogPane().getButtonTypes().add(buttonTypeCancel);
        
        List<Author> authors = new ArrayList<>();
        
        addAuthorButton.setOnAction(e -> 
        {
            Optional<Author> result = this.authorDialog.showAndWait();
            if(result.isPresent())
            {
                authors.add(result.get());
            }
            
            System.out.println("PRESSED BUTTON");
            
        });
        
        
        
        // this callback returns the result from our dialog, via 
        // Optional<FooBook> result = dialog.showAndWait();
        // FooBook book = result.get();
        // see DialogExample, line 31-34
        this.setResultConverter(new Callback<ButtonType, Book>() 
        {
            @Override
            public Book call(ButtonType b) 
            {
                Book result = null;
                if (b == buttonTypeOk) 
                {
                    if (isValidData()) 
                    {
                        System.out.println("DATEPICKER" + datePublished.getValue());
                        Author author = new Author(Integer.parseInt(authorIdField.getText()), firstNameField.getText(), lastNameField.getText(), Date.valueOf(dobPicker.getValue()));
                        result = new Book(isbnField.getText(), titleField.getText(), Date.valueOf(datePublished.getValue()), genreChoice.getValue(),Integer.parseInt(ratingField.getText())
                        , author, booksDb.getCurrentUser());
                        if(authors.size() > 0)
                        {
                            result.addAuthors(authors);
                            authors.removeAll(authors);
                        }
                        
                    }
                }
                clearDataFromFields();
                return result;
            }
        });

        // add an event filter to keep the dialog active if validation fails
        // (yes, this is ugly in FX)
        Button okButton = (Button) this.getDialogPane().lookupButton(buttonTypeOk);
        okButton.addEventFilter(ActionEvent.ACTION, new EventHandler() 
        {
            @Override
            public void handle(Event event) 
            {
                if (!isValidData())
                {
                    event.consume();
                    //showErrorAlert("Form error", "Invalid input");
                }
            }
        });
    }

    // TODO for the student: check each input separately, to give better
    // feedback to the user
    private boolean isValidData() 
    {
        if (genreChoice.getValue() == null) 
        {
            showErrorAlert("Form error", "You must choose genre");
            return false;
        }
        
        if (!Book.isValidISBN(isbnField.getText())) 
        {
            showErrorAlert("Form error", "Invalid ISBN");
            return false;
        }
        
        if (titleField.getText() == null || !titleField.getText().matches("^[a-zA-Z0-9 ]+$") ) 
        {
            showErrorAlert("Form error", "Invalid title");
            return false;
        }
        
        if (datePublished.getValue() == null || !datePublished.getValue().toString().matches("[0-9-]{10}") ) 
        {
            showErrorAlert("Form error", "Invalid date");
            return false;
        }
        
        if(firstNameField.getText() == null || !firstNameField.getText().matches("^[a-zA-Z]+$"))
        {
            showErrorAlert("Form error", "Invalid first name");
            return false;
        }
        if(lastNameField.getText() == null || !lastNameField.getText().matches("^[a-zA-Z]+$"))
        {
            showErrorAlert("Form error", "Invalid last name");
            return false;
        }
        
        if (dobPicker.getValue() == null || !dobPicker.getValue().toString().matches("[0-9-]{10}") ) 
        {
            showErrorAlert("Form error", "Invalid author date");
            return false;
        }
        
        if(authorIdField.getText() == null || !authorIdField.getText().matches("[0-9]+"))
        {
            showErrorAlert("Form error", "Invalid author ID");
            return false;
        }
        
        
        
        return true;
    }
    
    


    private void clearDataFromFields() 
    {
        //titleField.setText("");
        //isbnField.setText("");
        //genreChoice.setValue(null);
    }

    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private void showErrorAlert(String title, String info)
    {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(info);
        errorAlert.show();
    }
}
