package client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/*Client entry class handles switching of screens/modes*/
public class GameApplication extends Application {

  private Group root;
  private Scene scene;

  private PlayScreen playScreen;
  //private GameServer gameServer;

  private Stage stage;

  public static void main(String args[]) {
    launch(args);
  }

  @Override
  public void start(Stage primaryStage) throws Exception {
    primaryStage.setTitle("TimmyFightGoGo");

    //gameServer = new GameServer();
    //gameServer.start();

    // The root element in the javafx gui stack, all sub-elements attach to this
    root = new Group();

    // The scene where the root and all its children are displayed
    scene = new Scene(root);

    // Event handling (Input)
    scene.setOnKeyPressed(this::onKeyPressed);
    scene.setOnKeyReleased(this::onKeyReleased);

    // What you see when in 'fight' mode
    playScreen = new PlayScreen();
    playScreen.enter();

    root.getChildren().add(playScreen);

    // primaryStage is the stage provided by the javafx app instance
    primaryStage.setScene(scene);
    primaryStage.setOnCloseRequest(this::exit);
    primaryStage.show();

    // Save reference
    stage = primaryStage;
  }

  private void exit(WindowEvent windowEvent) {
    playScreen.exit();
  }

  private void onKeyPressed(KeyEvent event) {
    switch (event.getCode()) {
      case F11:
        stage.setFullScreen(!stage.isFullScreen());
        break;
      case ESCAPE:
        // Release the nukes
        Platform.exit();
        System.exit(0);
        break;
    }
    playScreen.onKeyPressed(event);
  }

  private void onKeyReleased(KeyEvent event) {
    playScreen.onKeyReleased(event);
  }
}
