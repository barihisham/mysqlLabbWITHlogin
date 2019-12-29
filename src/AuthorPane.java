
import javafx.collections.ObservableList;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Bari
 */
public class AuthorPane extends VBox {
    
    
    private TableView<Author> authorsTable;
    private ObservableList<Author> authorsInTable;
    private ComboBox<AuthorSearchMode> authorSearchModeBox;
    private Button openAuthorTableButton;
    private Button searchAuthorButton;
    private TextField SearchFieldAuthor;
    
    private Controller controller;
    
    //TEST
    
    
    
    
    
    
    public AuthorPane(Controller controller) 
    {
        
    }
}
