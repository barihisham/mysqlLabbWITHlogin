import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

import java.sql.Date;

public class LoginDialog extends Dialog<User> {

    private final TextField usernameField = new TextField();
    private final TextField passwordField = new TextField();
    private final Controller controller;
    private User userResult = new User("publicUser", "123");


    public LoginDialog(Controller controller){
        this.controller = controller;
        initDialog();
    }

    private void initDialog()
    {
        this.setTitle("Login");
        this.setResizable(false); // really?

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label("Username "), 1, 6);
        grid.add(usernameField, 2, 6);
        grid.add(new Label("Password "), 1, 7);
        grid.add(passwordField, 2, 7);


        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);


        this.setResultConverter(new Callback<ButtonType, User>()
        {
            @Override
            public User call(ButtonType b)
            {
                //userResult = null;
                if (b == buttonTypeOk)
                {
                    userResult = new User(usernameField.getText(), passwordField.getText());
                }
                System.out.println(userResult.toString());
                //clearDataFromFields();
                return userResult;
            }
        });

        Button okButton = (Button) this.getDialogPane().lookupButton(buttonTypeOk);
        okButton.addEventFilter(ActionEvent.ACTION, new EventHandler()
        {
            @Override
            public void handle(Event event)
            {

                if(!controller.handleVerifyAccountExist(new User(usernameField.getText(), passwordField.getText()), event)){
                    event.consume();
                }
            }
        });
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
