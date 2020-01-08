

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.control.cell.TextFieldTreeCell;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * The main pane for the view, extending VBox and including the menus. An
 * internal BorderPane holds the TableView for books and a search utility.
 *
 * @author anderslm@kth.se
 */
public class BooksPane extends VBox {



    private TableView<Author> authorsTable;
    private ObservableList<Author> authorsInTable;
    private ComboBox<AuthorSearchMode> authorSearchModeBox;
    private Button openAuthorTableButton;
    private Button searchAuthorButton;
    private TextField SearchFieldAuthor;
    private Button addReviewButton;
    private Button deleteBookBUtton;
    
    private TableView<Book> booksTable;
    private ObservableList<Book> booksInTable; // the data backing the table view
    private ComboBox<SearchMode> searchModeBox;
    private TextField searchField;
    private Button searchButton;

    private MenuBar menuBar;
    private AddBookDialog dialog;
    private final LoginDialog loginDialog;
    private final Controller controller;
    ObservableList<TablePosition> selectedCells;

    public AddBookDialog getDialog() {
        return dialog;
    }
    
    public BooksPane(BooksDb booksDb) 
    {
        //dialog = new AddBookDialog();
        controller = new Controller(booksDb, this);
        this.init(controller);
        dialog = new AddBookDialog(booksDb);
        loginDialog = new LoginDialog(controller);
    }

    /**
     * Display a new set of books, e.g. from a database select, in the
     * booksTable table view.
     *
     * @param books the books to display
     */
    public void displayBooks(List<Book> books) 
    {
        booksInTable.clear();
        booksInTable.addAll(books);
    }
    
    public void displayAuthors(List<Author> authors)
    {
        authorsInTable.clear();
        authorsInTable.addAll(authors);
    }
    
    
    
    /**
     * Notify user on input error or exceptions.
     * 
     * @param msg the message
     * @param type types: INFORMATION, WARNING et c.
     */
    protected void showAlertAndWait(String msg, Alert.AlertType type) {
        // types: INFORMATION, WARNING et c.
        Alert alert = new Alert(type, msg);
        alert.showAndWait();
    }

    private void init(Controller controller) {

        booksInTable = FXCollections.observableArrayList();
        initBooksTable();
        initAuthorSearchView(controller);
        
        authorsInTable = FXCollections.observableArrayList();
        // init views and event handlers

        initAuthorsTable();
        initSearchView(controller);
        initMenus();
        

        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(searchModeBox, searchField, searchButton, openAuthorTableButton, addReviewButton, deleteBookBUtton);
        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(booksTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));

        this.getChildren().addAll(menuBar, mainPane);
        VBox.setVgrow(mainPane, Priority.ALWAYS);
    }

    
    
    private void initAuthorsTable() {
        authorsTable = new TableView<>();
        authorsTable.setEditable(false);
        
        TableColumn<Author, Integer> idCol = new TableColumn<>("ID");
        TableColumn<Author, String> firstNameCol = new TableColumn<>("First Name");
        TableColumn<Author, String> lastNameCol = new TableColumn<>("Last Name");
        TableColumn<Author, Date> dobCol = new TableColumn<>("Date of birth");
        authorsTable.getColumns().addAll(idCol, firstNameCol, lastNameCol, dobCol);
        
        idCol.setCellValueFactory(new PropertyValueFactory<>("authorID"));
        firstNameCol.setCellValueFactory(new PropertyValueFactory<>("firstName"));
        lastNameCol.setCellValueFactory(new PropertyValueFactory<>("lastName"));
        dobCol.setCellValueFactory(new PropertyValueFactory<>("dob"));
        authorsTable.setItems(authorsInTable);
    }
    
    private void initBooksTable() {
        booksTable = new TableView<>();
        //booksTable.setEditable(false); // don't allow user updates (yet)
        // define columns
        booksTable.setEditable(true);
        //booksTable.getSelectionModel().setCellSelectionEnabled(true);
        selectedCells = booksTable.getSelectionModel().getSelectedCells() ;
        booksTable.setOnMouseClicked(event -> {
            if(event.getClickCount() == 2 && !selectedCells.isEmpty()){
                TablePosition selectedCell = selectedCells.get(0);
                TableColumn column = selectedCell.getTableColumn();
                int rowIndex = selectedCell.getRow();
                if(selectedCell.getColumn() == 5){
                    rowIndex = selectedCell.getRow();
                    System.out.println(booksTable.getItems().get(selectedCell.getRow()).getISBN());
                    controller.handleAddAuthorExistingBook(booksTable.getItems().get(selectedCell.getRow()).getISBN());
                }
            }
        });




        TableColumn<Book, String> titleCol = new TableColumn<>("Title");
        TableColumn<Book, String> isbnCol = new TableColumn<>("ISBN");
        TableColumn<Book, Date> publishedCol = new TableColumn<>("Published");
        //TableColumn<Book, String> genreCol = new TableColumn<>("Genre");
        TableColumn<Book, Integer> ratingCol = new TableColumn<>("Rating");
        TableColumn<Book, BookGenre> genreCol = new TableColumn<>("Genre");
        TableColumn<Book, Author> authorCol = new TableColumn<>("Author(s)");
        TableColumn<Book, User> addedByCol = new TableColumn<>("Added by");
        TableColumn<Book, Review> reviewCol = new TableColumn<>("Review(s)");

        booksTable.getColumns().addAll(titleCol, isbnCol, publishedCol,ratingCol, genreCol, authorCol, addedByCol, reviewCol);


        // give title column some extra space
        authorCol.prefWidthProperty().bind(booksTable.widthProperty().multiply(0.5));

        // define how to fill data for each cell, 
        // get values from Book properties
        titleCol.setCellValueFactory(new PropertyValueFactory<>("title"));
        isbnCol.setCellValueFactory(new PropertyValueFactory<>("ISBN"));
        publishedCol.setCellValueFactory(new PropertyValueFactory<>("published"));
        ratingCol.setCellValueFactory(new PropertyValueFactory<>("rating"));
        genreCol.setCellValueFactory(new PropertyValueFactory<>("genre"));
        authorCol.setCellValueFactory(new PropertyValueFactory<>("authors"));
        addedByCol.setCellValueFactory(new PropertyValueFactory<>("addedBy"));
        reviewCol.setCellValueFactory(new PropertyValueFactory<>("reviews"));
        // associate the table view with the data
        booksTable.setItems(booksInTable);
    }

    private void initSearchView(Controller controller) {
        searchField = new TextField();
        searchField.setPromptText("Search for...");
        searchModeBox = new ComboBox<>();
        searchModeBox.getItems().addAll(SearchMode.values());
        searchModeBox.setValue(SearchMode.Title);
        searchButton = new Button("Search");

        // event handling (dispatch to controller)
        searchButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                String searchFor = searchField.getText();
                SearchMode mode = searchModeBox.getValue();
                controller.onSearchSelected(searchFor, mode);
            }
        });

        addReviewButton = new Button("Add review");
        addReviewButton.setOnAction(e-> {
            if(!selectedCells.isEmpty()) {
                TablePosition selectedCell = selectedCells.get(0);
                TableColumn column = selectedCell.getTableColumn();
                int rowIndex = selectedCell.getRow();
                rowIndex = selectedCell.getRow();
                System.out.println(booksTable.getItems().get(selectedCell.getRow()).getISBN());
                controller.handleAddReview(booksTable.getItems().get(selectedCell.getRow()).getISBN());
            }else{
                controller.handleWrongInput("You must select a book");
                e.consume();
            }
        });

        deleteBookBUtton = new Button("Delete book");
        deleteBookBUtton.setOnAction(e-> {
            if(!selectedCells.isEmpty()) {
                TablePosition selectedCell = selectedCells.get(0);
                TableColumn column = selectedCell.getTableColumn();
                int rowIndex = selectedCell.getRow();
                rowIndex = selectedCell.getRow();
                System.out.println(booksTable.getItems().get(selectedCell.getRow()).getISBN());
                controller.handleDeleteBook(booksTable.getItems().get(selectedCell.getRow()).getISBN());
            }else{
                controller.handleWrongInput("You must select a book");
                e.consume();
            }
        });

    }

    private void initAuthorSearchView(Controller controller)
    {
        SearchFieldAuthor = new TextField();
        SearchFieldAuthor.setPromptText("Search...");
        authorSearchModeBox = new ComboBox<>();
        authorSearchModeBox.getItems().addAll(AuthorSearchMode.values());
        authorSearchModeBox.setValue(AuthorSearchMode.ID);
        searchAuthorButton = new Button("Search");
        
        searchAuthorButton.setOnAction(e-> { 
        String SearchFor = SearchFieldAuthor.getText();
        AuthorSearchMode mode = authorSearchModeBox.getValue();
        controller.onSearchSelectedAuthor(SearchFor, mode);
        
        });

        openAuthorTableButton = new Button("Search Author");
        openAuthorTableButton.setOnAction(e-> {this.authorTable();});


    }


    private void initMenus() {

        Menu fileMenu = new Menu("File");
        MenuItem exitItem = new MenuItem("Exit");
        MenuItem connectItem = new MenuItem("Connect to Db");
        MenuItem disconnectItem = new MenuItem("Disconnect");
        MenuItem loginItem = new MenuItem("Login");
        MenuItem logoutItem = new MenuItem("Logout");
        loginItem.setOnAction(e-> this.controller.handleLogin());
        logoutItem.setOnAction(e-> this.controller.handleLogoutAuthorizedUser());
        fileMenu.getItems().addAll(exitItem, connectItem, disconnectItem, loginItem, logoutItem);

        Menu searchMenu = new Menu("Search");
        MenuItem titleItem = new MenuItem("Title");
        MenuItem isbnItem = new MenuItem("ISBN");
        MenuItem authorItem = new MenuItem("Author");
        MenuItem ratingItem = new MenuItem("Rating");
        MenuItem genreItem = new MenuItem("genre");
        MenuItem idItem = new MenuItem("ID");
        searchMenu.getItems().addAll(titleItem, isbnItem, authorItem, ratingItem, genreItem, idItem);

        Menu manageMenu = new Menu("Manage");
        MenuItem addItem = new MenuItem("Add");
        MenuItem removeItem = new MenuItem("Remove");
        MenuItem updateItem = new MenuItem("Update");
        
        addItem.setOnAction(e -> this.controller.handleAddBookToDb());
        
        manageMenu.getItems().addAll(addItem, removeItem, updateItem);

        menuBar = new MenuBar();
        menuBar.getMenus().addAll(fileMenu, searchMenu, manageMenu);
    }
    
    
    public void authorTable()
    {
        Stage window = new Stage();
        VBox vbox = new VBox();
        FlowPane bottomPane = new FlowPane();
        bottomPane.setHgap(10);
        bottomPane.setPadding(new Insets(10, 10, 10, 10));
        bottomPane.getChildren().addAll(authorSearchModeBox, SearchFieldAuthor, searchAuthorButton);
        
        BorderPane mainPane = new BorderPane();
        mainPane.setCenter(authorsTable);
        mainPane.setBottom(bottomPane);
        mainPane.setPadding(new Insets(10, 10, 10, 10));
        
        vbox.getChildren().addAll(mainPane);
        Scene scene = new Scene(vbox, 400, 400);
        window.setTitle("Authors Database Client");
        window.setScene(scene);
        window.show();
    }
    
    
    
    
}
