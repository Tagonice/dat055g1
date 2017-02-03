package client;

import common.GamePlayer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class PlayerRenderer implements GameRenderer {

  private GamePlayer player;

  //  Constructor
  public PlayerRenderer(GamePlayer player) {
    this.player = player;
  }

  //  Renders the player (which is now a 16x16 circle(oval) inside a rectangle) on the canvas
  @Override
  public void render(Canvas canvas) {
    GraphicsContext gc = canvas.getGraphicsContext2D();
    gc.setFill(Color.RED);
    gc.fillOval(player.getPosition().getX(), player.getPosition().getY(), 16, 16);

    gc.setStroke(Color.RED); // Outline color

    gc.beginPath();

    gc.rect(player.getPosition().getX(), player.getPosition().getY(), player.getWidth()
        , player.getHeight());

    gc.stroke();
  }
}
