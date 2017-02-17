package server;

import common.ActionCycle.CYCLE;
import common.GamePlayer;
import common.GamePlayer.ACTION;
import java.util.HashMap;
import java.util.HashSet;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/*Handles the state of a player each tick. Takes into consideration input from users*/
public class PlayerController implements GameController {

  public GamePlayer player;

  private HashMap<KeyCode, ACTION> keyBinds;
  private HashSet<ACTION> actions; //actions to be performed during update

  public PlayerController(GamePlayer player) {
    this.player = player;
    keyBinds = new HashMap<>();
    actions = new HashSet<>();
  }

  @Override
  public void update(double delta) {
    if (actions.contains(ACTION.MOVE_LEFT)) {
      player.setFaceRight(false);
      player.setPosition(player.getPosition().add(-320 * delta, 0));
    }
    if (actions.contains(ACTION.MOVE_RIGHT)) {
      player.setFaceRight(true);
      player.setPosition(player.getPosition().add(320 * delta, 0));
    }
    if (actions.contains(ACTION.JUMP)) {
      if (player.isOnGround()) {
        player.accelerate(new Point2D(0, -400));
        player.setOnGround(false);
      }
    }
    if (actions.contains(ACTION.FALL)) {
      if (!player.isOnGround()) {
        player.setPosition(player.getPosition().add(0, 1600 * delta));
      }
    }

    if (player.statePunching.isActive()) {

    }

    player.statePunching.update(delta);
    player.stateStunned.update(delta);

    player.setPosition(player.getPosition().add(player.getVelocity().multiply(delta)));
  }

  @Override
  public void attach(GameEngine engine) {
    engine.addController(this);
  }

  public void bindKey(KeyCode code, ACTION action) {
    keyBinds.put(code, action);
  }

  @Override
  public void onKeyPressed(KeyEvent event) {
    if (!actions.contains(ACTION.HIT) && keyBinds.get(event.getCode()) == ACTION.HIT) {
      if (player.statePunching.isReady() && !player.stateStunned.isActive()) {
        player.statePunching.enterCycle(CYCLE.SPOOL_UP);
      }
    }

    if (keyBinds.containsKey(event.getCode())) {
      actions.add(keyBinds.get(event.getCode()));
    }
  }

  @Override
  public void onKeyReleased(KeyEvent event) {
    if (keyBinds.containsKey(event.getCode())) {
      actions.remove(keyBinds.get(event.getCode()));
    }
  }
}
