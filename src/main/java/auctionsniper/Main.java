package auctionsniper;

import auctionsniper.ui.MainWindow;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import javax.swing.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/**
 * Main class which contains the application entry point
 *
 * @author ed
 */
public class Main {

  public static final String AUCTION_RESOURCE = "Auction";
  public static final String ITEM_ID_AS_LOGIN = "auction-%s";
  public static final String AUCTION_ID_FORMAT =
      ITEM_ID_AS_LOGIN + "@%s/" + AUCTION_RESOURCE;
  public static final String JOIN_COMMAND_FORMAT =
      "SOLVersion: 1.1; Command: JOIN; Price: %d;";
  public static final String BID_COMMAND_FORMAT =
      "SOLVersion: 1.1; Command: BID; Price: %d;";

  private static final int ARG_HOSTNAME = 0;
  private static final int ARG_USERNAME = 1;
  private static final int ARG_PASSWORD = 2;
  private static final int ARG_ITEM_ID = 3;

  private MainWindow ui;
  @SuppressWarnings("unused")
  private Chat notToBeGCd;

  /**
   * This constructor starts the user interface implicitly
   *
   * @throws Exception
   */
  public Main() throws Exception {
    startUserInterface();
  }

  /**
   * Application entry point.
   *
   * @param args [0] XMPP-Server Hostname [1] XMPP Username
   *             [3] XMPP Password [4] Auction item id
   * @throws Exception
   */
  public static void main(String... args) throws Exception {
    Main main = new Main();
    main.joinAuction(
        connection(args[ARG_HOSTNAME], args[ARG_USERNAME], args[ARG_PASSWORD]),
        args[ARG_ITEM_ID]);
  }

  private static XMPPConnection connection(
      String hostname, String username, String password) throws XMPPException {
    XMPPConnection connection = new XMPPConnection(hostname);
    connection.connect();
    connection.login(username, password, AUCTION_RESOURCE);
    return connection;
  }

  private static String auctionId(String itemId, XMPPConnection connection) {
    return String.format(AUCTION_ID_FORMAT, itemId,
        connection.getServiceName());
  }

  private void joinAuction(XMPPConnection connection, String itemId)
      throws XMPPException {
    disconnectWhenUICloses(connection);
    final Chat chat = connection.getChatManager().createChat(
        auctionId(itemId, connection), null);
    this.notToBeGCd = chat;

    Auction auction = new XMPPAuction(chat);
    chat.addMessageListener(
        new AuctionMessageTranslator(
            new AuctionSniper(auction, new SniperStateDisplayer())));

    chat.sendMessage(JOIN_COMMAND_FORMAT);
  }

  private void startUserInterface() throws Exception {
    SwingUtilities.invokeAndWait(() -> ui = new MainWindow());
  }

  private void disconnectWhenUICloses(final XMPPConnection connection) {
    ui.addWindowListener(new WindowAdapter() {
      @Override
      public void windowClosed(WindowEvent e) {
        connection.disconnect();
      }
    });
  }

  public class SniperStateDisplayer implements SniperListener {

    public void sniperBidding() {
      showStatus(MainWindow.STATUS_BIDDING);
    }

    public void sniperLost() {
      showStatus(MainWindow.STATUS_LOST);
    }

    public void sniperWinning() {
      showStatus(MainWindow.STATUS_WINNING);
    }

    private void showStatus(final String status) {
      SwingUtilities.invokeLater(() -> ui.showStatus(status));
    }
  }

}
