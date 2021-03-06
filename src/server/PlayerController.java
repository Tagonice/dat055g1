package server;

import common.ActionCycle.CYCLE;
import common.GamePlayer;
import common.GamePlayer.ACTION;
import java.util.HashMap;
import java.util.HashSet;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Handles the state of a player each tick. Takes into consideration input from users
 *
 * @author Alexander Andersson (alexaan)
 * @author Linus Berglund (belinus)
 * @author Erik Källberg (kalerik)
 * @author Timmy Truong (timmyt)
 * @author Karl Ängermark (karlang)
 * @version 2017-02-28
 */
public class PlayerController implements GameController {

  /**
   * player reference.
   */
  public GamePlayer player;

  private HashMap<KeyCode, ACTION> keyBinds;
  private HashSet<ACTION> actions; //actions to be performed during update

  /**
   * Creates an instance of PlayerController.
   * @param player the player to control
   */
  public PlayerController(GamePlayer player) {
    this.player = player;
    keyBinds = new HashMap<>();
    actions = new HashSet<>();
  }

  /**
   * Updates player based on {@param delta} time.
   * @param delta the time difference between this and the previous tick, used for scaling
   */
  @Override
  public void update(double delta) {
    if (actions.contains(ACTION.MOVE_LEFT)) {
      player.setFaceRight(false);
      player.setPosition(player.getPosition().add(-4 * delta, 0));
    }
    if (actions.contains(ACTION.MOVE_RIGHT)) {
      player.setFaceRight(true);
      player.setPosition(player.getPosition().add(4 * delta, 0));
    }
    if (actions.contains(ACTION.JUMP)) {
      if (player.isOnGround()) {
        player.accelerate(new Point2D(0, -6));
        player.setOnGround(false);
      }
    }
    if (actions.contains(ACTION.FALL)) {
      if (!player.isOnGround()) {
        player.setPosition(player.getPosition().add(0, 2 * delta));
      }
    }
    if (player.stateKicking.isActive()) {

    }

    if (player.statePunching.isActive()) {

    }

    player.statePunching.update(delta);
    player.stateStunned.update(delta);
    player.stateKicking.update(delta);
    player.setPosition(player.getPosition().add(player.getVelocity().multiply(delta)));
  }

  /**
   * Attaches this controller to an engine.
   * @param engine the engine to attach this controller to
   */
  @Override
  public void attach(GameEngine engine) {
    engine.addController(this);
  }

  /**
   * Binds a key to an action.
   * @param code the key code to bind
   * @param action the action to be bound to
   */
  public void bindKey(KeyCode code, ACTION action) {
    keyBinds.put(code, action);
  }

  /**
   * Handles key presses based on key binds.
   * @param event the event that has been fired
   */
  @Override
  public void onKeyPressed(KeyEvent event) {
    if (keyBinds.get(event.getCode()) == ACTION.HIT && player.statePunching.isReady()
        && !player.stateStunned.isActive()) {
      if (!player.stateKicking.isSpoolingUp() && !player.stateKicking.isActive()
          && !player.stateKicking.isOnCoolDown()) {
        player.statePunching.enterCycle(CYCLE.SPOOL_UP);
      }

    }
    if (keyBinds.get(event.getCode()) == ACTION.KICK && player.stateKicking.isReady()
        && !player.stateStunned.isActive()) {
      if (!player.statePunching.isSpoolingUp() && !player.statePunching.isActive()
          && !player.statePunching.isOnCoolDown()) {
        player.stateKicking.enterCycle(CYCLE.SPOOL_UP);
      }
    }
    if (keyBinds.containsKey(event.getCode())) {
      actions.add(keyBinds.get(event.getCode()));
    }
  }

  /**
   * Handles key releases based on key binds.
   * @param event the event that has been fired
   */
  @Override
  public void onKeyReleased(KeyEvent event) {
    if (keyBinds.containsKey(event.getCode())) {
      actions.remove(keyBinds.get(event.getCode()));
    }
  }

  /**
   * Starts a player action.
   * @param action action to be started
   */
  public void actionStart(ACTION action) {
    if (!actions.contains(ACTION.HIT) && action == ACTION.HIT) {
      if (player.statePunching.isReady() && !player.stateStunned.isActive()) {
        player.statePunching.enterCycle(CYCLE.SPOOL_UP);
      }
    }

    actions.add(action);
  }

  /**
   * Ends a player action.
   * @param action action to end
   */
  public void actionEnd(ACTION action) {
    actions.remove(action);
  }
}
