package server;

import common.GamePlayer;
import common.GamePlayer.ACTION;
import common.GameStage;
import common.NetworkPacket;
import common.NetworkPacket.TYPE;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;

/**
 * A game server
 *
 * @author Alexander Andersson (alexaan)
 * @author Linus Berglund (belinus)
 * @author Erik Källberg (kalerik)
 * @author Timmy Truong (timmyt)
 * @author Karl Ängermark (karlang)
 * @version 2017-02-28
 */
public class GameServer {

  private GameStage gameStage;
  private GamePlayer player1;
  private GamePlayer player2;
  private StageController stageController;
  private PlayerController player1Controller;
  private PlayerController player2Controller;
  private GameEngine gameEngine;
  private ServerSocket serverSocket;
  private Socket client1;
  private Socket client2;
  private Thread gameThread;
  private HashSet<Thread> clientThreads;

  /**
   * Creates an instance of GameServer
   *
   * @param port target port to host the server on
   * @throws IOException thrown if anything goes wrong with the sockets TODO: handle errors
   */
  public GameServer(int port) throws IOException {
    serverSocket = new ServerSocket(port);

    gameStage = new GameStage();
    player1 = gameStage.getPlayer1();
    player2 = gameStage.getPlayer2();

    stageController = new StageController(gameStage);
    player1Controller = new PlayerController(player1);
    player2Controller = new PlayerController(player2);

    gameEngine = new GameEngine();
    gameEngine.addController(stageController);
    gameEngine.addController(player1Controller);
    gameEngine.addController(player2Controller);

    gameThread = new Thread(gameEngine);
  }

  /**
   * Entry point for running a server.
   *
   * @param args totally ignored
   * @throws IOException on any exception
   */
  public static void main(String[] args) throws IOException {
    GameServer server = new GameServer(8022);
    server.start();
  }

  /**
   * Sends a game-state synchronization packet to each player.
   *
   * @throws IOException on any exception
   */
  void syncClients() throws IOException {
    OutputStream outputStream = client1.getOutputStream();
    outputStream.write(NetworkPacket.sync(player1, 1));
    outputStream.write(NetworkPacket.sync(player2, 2));
    outputStream = client2.getOutputStream();
    outputStream.write(NetworkPacket.sync(player1, 1));
    outputStream.write(NetworkPacket.sync(player2, 2));
  }

  /**
   * Starting point for the server awaits two connections before starting the game engine
   */
  public void start() {
    while (client1 == null || client2 == null) {
      try {
        System.out.println("Waiting for new connection");
        Socket client = serverSocket.accept();
        System.out.println("New connection!");
        if (client1 == null) {
          client1 = client;
        } else {
          client2 = client;
        }
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    Thread clientThread1 = new Thread(new ClientListener(client1, player1Controller));
    Thread clientThread2 = new Thread(new ClientListener(client2, player2Controller));

    clientThread1.start();
    clientThread2.start();
    gameThread.start();

    while (true) {
      try {
        syncClients();
        Thread.sleep(16l);
      } catch (InterruptedException | IOException e) {
        e.printStackTrace();
      }
    }
  }

  /**
   * Inner class for running socket threads for client communication
   */
  private class ClientListener implements Runnable {

    private InputStream inputStream;
    private PlayerController playerController;
    private Socket socket;

    /**
     * Creates an instance
     *
     * @param socket clients socket
     * @param playerController player controller associated with this client's player
     */
    public ClientListener(Socket socket, PlayerController playerController) {
      this.socket = socket;
      this.playerController = playerController;
    }

    /**
     * Reads a single byte from the socket to determine type, this must be the first byte in each
     * packet.
     * @return the type of packet
     */
    public TYPE identifyPacket() throws IOException {
      int maybeType = inputStream.read();

      if (maybeType < 0 || maybeType >= TYPE.values().length) {
        System.err.println("Received packet type is unknown");
        return TYPE.ERROR;
      }

      NetworkPacket.TYPE type = NetworkPacket.TYPE.values()[(byte) maybeType];

      return type;
    }

    /**
     * Entry point for thread
     */
    @Override
    public void run() {
      try {
        inputStream = socket.getInputStream();
        while (socket.isConnected()) {
          switch (identifyPacket()) {
            default:
              System.err.println("Unsupported packet");
              break;
            case C_ACTION_START:
              int actionStartInt = inputStream.read();
              if (actionStartInt < 0 || actionStartInt >= ACTION.values().length) {
                System.err.println("Unknown action started");
              } else {
                ACTION action = ACTION.values()[actionStartInt];
                playerController.actionStart(action);
              }
              break;
            case C_ACTION_END:
              int actionEndInt = inputStream.read();
              if (actionEndInt < 0 || actionEndInt >= ACTION.values().length) {
                System.err.println("Unknown action started");
              } else {
                ACTION action = ACTION.values()[actionEndInt];
                playerController.actionEnd(action);
              }
              break;
          }
        }
        socket.close();
      } catch (IOException e) {
        e.printStackTrace();
      }
    }
  }
}
