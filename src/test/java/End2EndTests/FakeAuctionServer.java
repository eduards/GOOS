package End2EndTests;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import static java.lang.String.format;

/**
 * A FakeAuctionServer is a substitute server that allows the test to check
 * how the Auction Sniper interacts with an auction using XMPP messages. It
 * has three responsibilities:
 * (1) it must connect to the XMPP broker and accept a request to join the chat
 * from the Sniper;
 * (2) it must receive chat messages from the Sniper or fail if no message
 * arrives within some timeout;
 * (3) it must allow the test to send messages back to the Sniper as specified
 * by Southabee’s On-Line.
 *
 * @author ed
 */
public class FakeAuctionServer {

  public static final String ITEM_ID_AS_LOGIN = "auction-%s";
  public static final String AUCTION_RESOURCE = "Auction";
  public static final String XMPP_HOSTNAME = "localhost";
  public static final String AUCTION_PASSWORD = "auction";

  private final String itemId;
  private final XMPPConnection connection;
  private final SingleMessageListener messageListener = new SingleMessageListener();
  private Chat currentChat;

  public FakeAuctionServer(String itemId) {
    this.itemId = itemId;
    this.connection = new XMPPConnection(XMPP_HOSTNAME);
  }

  public void startSellingItem() throws XMPPException {
    connection.connect();
    connection.login(format(ITEM_ID_AS_LOGIN, itemId),
        AUCTION_PASSWORD, AUCTION_RESOURCE);
    connection.getChatManager().addChatListener(
        new ChatManagerListener() {
          public void chatCreated(Chat chat, boolean createdLocally) {
            currentChat = chat;
            chat.addMessageListener(messageListener);
          }
        });
  }

  public String getItemId() {
    return itemId;
  }

  /**
   * Check if any message has arrived
   * TODO method checks for an empty message as a temporary replacement, since it is the only event type supported so far
   *
   * @throws InterruptedException if no message is received within 5 seconds
   */
  public void hasReceivedJoinRequestFromSniper() throws InterruptedException {
    messageListener.receivesAMessage();
  }

  /**
   * Send a closing message
   * TODO method sends an empty message as a temporary replacement, since it is the only event type supported so far
   *
   * @throws XMPPException
   */
  public void announceClosed() throws XMPPException {
    currentChat.sendMessage(new Message());
  }

  /**
   * Close the connection
   */
  public void stop() {
    connection.disconnect();
  }

}

