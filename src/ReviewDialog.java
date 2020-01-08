import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;

public class ReviewDialog extends Dialog<String> {

    private final TextField userText = new TextField();

    public ReviewDialog(){
        initDialog();
    }

    private void initDialog(){
        this.setTitle("Add review");
        this.setResizable(false);

        userText.setPrefWidth(150);

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5);
        grid.setVgap(5);
        grid.add(new Label("Review"), 1, 6);
        grid.add(userText, 2, 6);
        this.getDialogPane().setContent(grid);

        ButtonType buttonTypeOk = new ButtonType("Submit", ButtonBar.ButtonData.OK_DONE);
        this.getDialogPane().getButtonTypes().add(buttonTypeOk);

        this.setResultConverter(new Callback<ButtonType, String>() {
            @Override
            public String call(ButtonType param) {
                String userReview = null;
                if(param == buttonTypeOk){
                    if(!(userText.getText().isEmpty()))
                        userReview = userText.getText();
                    System.out.println("here");
                }
                return userReview;
            }
        });

        Button okButton = (Button) this.getDialogPane().lookupButton(buttonTypeOk);
        okButton.addEventFilter(ActionEvent.ACTION, new EventHandler() {
            @Override
            public void handle(Event event)
            {
                if (userText.getText() == null) {
                    event.consume();
                }
            }
        });
    }
    }




