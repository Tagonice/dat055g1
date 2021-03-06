package client;


import client.screen.PlayScreen;
import common.GameDefaults;
import common.GamePlayer;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.shape.Rectangle;

/**
 * Renders the HealthBars in the game.
 *
 * @author Alexander Andersson (alexaan)
 * @author Linus Berglund (belinus)
 * @author Erik Källberg (kalerik)
 * @author Timmy Truong (timmyt)
 * @author Karl Ängermark (karlang)
 * @version 2017-02-28
 */
public class HealthRenderer implements GameRenderer {

  public double percentage;
  private GamePlayer player;
  private boolean leftBar;

  /**
   * Creates an instance of <tt>HealthRenderer</tt>.
   *
   * @param player the player corresponding to the Healthbar
   * @param leftBar boolean for wether the bar is to the left
   */
  public HealthRenderer(GamePlayer player, boolean leftBar) {
    this.player = player;
    this.leftBar = leftBar;
  }

  /**
   * Renders the HealthBar of a player.
   *
   * @param canvas javafx canvas to render to
   */
  @Override
  public void render(Canvas canvas) {
    GraphicsContext gc = canvas.getGraphicsContext2D();

    double scaleX = canvas.getWidth() / 16;
    double scaleY = canvas.getHeight() / 9;

    gc.save();

    gc.scale(scaleX, scaleY);
    gc.setLineWidth(gc.getLineWidth() / scaleX);

    Rectangle healthBar;

    if (leftBar == true) {
      healthBar = new Rectangle(0.6, 0.3, 5.7, 0.4);
    } else {
      healthBar = new Rectangle(16 - 0.6 - 5.7, 0.3, 5.7, 0.4);
    }

    percentage = (double) player.getHP() / player.getMaxHP();

    if (percentage >= 0.75) {
      gc.setFill(GameDefaults.HEALTHBAR_GOOD);
    } else if (percentage > 0.4) {
      gc.setFill(GameDefaults.HEALTHBAR_BAD);
    } else {
      gc.setFill(GameDefaults.HEALTHBAR_AWFUL);
    }

    if (leftBar) {
      gc.fillRect(healthBar.getX(), healthBar.getY(), healthBar.getWidth() * percentage,
          healthBar.getHeight());
    } else {
      gc.fillRect(healthBar.getX() + healthBar.getWidth() * (1d - percentage), healthBar.getY(),
          healthBar.getWidth() * percentage, healthBar.getHeight());
    }

    gc.setStroke(GameDefaults.HEALTHBAR_BORDER);
    gc.strokeRect(healthBar.getX(), healthBar.getY(), healthBar.getWidth(), healthBar.getHeight());

    gc.setLineWidth(gc.getLineWidth() * scaleX);
    gc.restore();
    setHealth();
  }

  /**
   * Updates the current health of a player.
   */
  public void setHealth() {
    PlayScreen.intProperty1.set(player.getHP());
  }

  /**
   * Returns the current health of a player.
   * @return the health of a player
   */
  public double getHealth() {
    return player.getHP();
  }
}
