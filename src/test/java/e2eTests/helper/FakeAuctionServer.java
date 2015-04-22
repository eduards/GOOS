package e2eTests.helper;

import auctionsniper.Main;
import org.hamcrest.Matcher;
import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import static java.lang.String.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

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

  /**
   * Check if JOIN message has arrived
   *
   * @throws InterruptedException if no message is received within 5 seconds
   */
  public void hasReceivedJoinRequestFrom(String sniperId)
      throws InterruptedException {
    receivesAMessageMatching(sniperId, equalTo(Main.JOIN_COMMAND_FORMAT));
  }

  /**
   * Check if BID message has arrived
   *
   * @throws InterruptedException if no message is received within 5 seconds
   */
  public void hasReceivedBid(int bid, String sniperId)
      throws InterruptedException {
    receivesAMessageMatching(sniperId,
        equalTo(format(Main.BID_COMMAND_FORMAT, bid)));
  }

  /**
   * Send a closing message
   * TODO method sends an empty message as a temporary replacement, since it is the only event type supported so far
   *
   * @throws XMPPException
   */
  public void announceClosed() throws XMPPException {
    currentChat.sendMessage("SOLVersion: 1.1; Event: CLOSE;");
  }

  /**
   * Sends a price message through the chat
   */
  public void reportPrice(int price, int increment, String bidder)
      throws XMPPException {
    currentChat.sendMessage(
        String.format("SOLVersion: 1.1; Event: PRICE; "
                + "CurrentPrice: %d; Increment: %d; Bidder: %s;",
            price, increment, bidder));
  }

  /**
   * Close the connection
   */
  public void stop() {
    connection.disconnect();
  }

  public String getItemId() {
    return itemId;
  }

  private void receivesAMessageMatching(String sniperId,
                                        Matcher<? super String> messageMatcher)
      throws InterruptedException {
    messageListener.receivesAMessage(messageMatcher);
    assertThat(currentChat.getParticipant(), equalTo(sniperId));
  }

}

