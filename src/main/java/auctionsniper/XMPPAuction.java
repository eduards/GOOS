package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.XMPPException;

/**
* @author ed
*/
public class XMPPAuction implements Auction {
  private final Chat chat;

  public XMPPAuction(Chat chat) {
    this.chat = chat;
  }

  public void bid(int amount) {
    sendMessage(String.format(Main.BID_COMMAND_FORMAT, amount));
  }

  @Override
  public void join() {
    sendMessage(Main.JOIN_COMMAND_FORMAT);
  }

  private void sendMessage(final String message) {
    try {
      chat.sendMessage(message);
    } catch (XMPPException e) {
      e.printStackTrace();
    }
  }
}
