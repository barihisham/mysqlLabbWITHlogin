
import java.sql.Date;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bari
 */
public class AddAuthorDialog extends Dialog<Author>{
    
    private final TextField firstNameField = new TextField("lastNmaelabla");
    private final TextField lastNameField = new TextField("fistNameblabla");
    private final TextField dobField = new TextField("1999-10-24");
    private final DatePicker dobPicker = new DatePicker();
    private final TextField authorIdField = new TextField();

    
    public AddAuthorDialog()
    {

        initDialog();
    }
    
    
    private void initDialog()
    {
        this.setTitle("Add a new book");
        this.setResizable(false); // really?
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label("Authors Firstname "), 1, 6);
        grid.add(firstNameField, 2, 6);
        grid.add(new Label("Authors Lastname "), 1, 7);
        grid.add(lastNameField, 2, 7);
        grid.add(new Label("Authors birthday "), 1, 8);
        grid.add(dobPicker, 2, 8);
        grid.add(new Label("AuthorID "), 1, 9);
        grid.add(authorIdField, 2, 9);
        
        
        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);
        
        
        this.setResultConverter(new Callback<ButtonType, Author>() 
        {
            @Override
            public Author call(ButtonType b) 
            {
                Author authorResult = null;
                if (b == buttonTypeOk) 
                {
                    if (isValidData()) 
                    {
                        authorResult = new Author(Integer.parseInt(authorIdField.getText()), firstNameField.getText(), lastNameField.getText(), Date.valueOf(dobPicker.getValue()));
                    }
                }
                //clearDataFromFields();
                return authorResult;
            }
        });
        
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
    
    
    private boolean isValidData() 
    {

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
    
    private final Alert errorAlert = new Alert(Alert.AlertType.ERROR);

    private void showErrorAlert(String title, String info)
    {
        errorAlert.setTitle(title);
        errorAlert.setHeaderText(null);
        errorAlert.setContentText(info);
        errorAlert.show();
    }
}
