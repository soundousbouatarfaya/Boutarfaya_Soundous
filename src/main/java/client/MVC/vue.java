package client.MVC;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.scene.control.TextField;

import java.util.Stack;

public class vue extends Application {
    public static void main(String[] args) {
        vue.launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception{
        VBox loadClass = new VBox();
        loadClass.setSpacing(10);
        loadClass.setPadding(new Insets(10));
        loadClass.setStyle("-fx-border-color: blue");

        HBox codeClass= new HBox();
        codeClass.setSpacing(10);
        codeClass.setAlignment(Pos.BASELINE_LEFT);

        Label codeClassLabel= new Label("Code");
        TextArea codeClassText = new TextArea();
        codeClassText.setPrefColumnCount(1);
        codeClassText.setPrefRowCount(20);

        Label classLabel= new Label("Code");
        TextArea classLabelText = new TextArea();
        classLabelText.setPrefColumnCount(1);
        classLabelText.setPrefRowCount(20);

        codeClass.getChildren().addAll(classLabel, classLabelText,codeClassLabel,codeClassText);
        loadClass.getChildren().add(codeClass);

       /** Pane root = new Pane();

        StackPane loadClass = new StackPane();
        StackPane registration= new StackPane();

        loadClass.getChildren().add((new Label("Liste des cours")));
        registration.getChildren().add((new Label("Formulaire d'inscription")));
        SplitPane splitPane = new SplitPane();
        splitPane.getItems().addAll(loadClass,registration);


        Text titre = new Text("Hello Soundous");
        titre.setFont(Font.font("Purisa", 20));
        titre.setLayoutX(135);
        titre.setLayoutY(35);
        root.getChildren().add(titre);
         **/

        //Scene scene = new Scene(root, 600, 450);
        //primaryStage.setScene(scene);
        //primaryStage.setTitle("Inscription UdeM");
        //primaryStage.show();

    }
}
