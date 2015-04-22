package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;

/**
 * Responsible for the translation of auction events and notification
 * of appropriate message listeners if a message occurs
 *
 * @author ed
 */
public class AuctionMessageTranslator implements MessageListener {

  private final AuctionEventListener listener;

  public AuctionMessageTranslator(AuctionEventListener listener) {
    this.listener = listener;
  }

  /**
   * Unpack the message type from an auction event notify the registered
   * message listener
   */
  public void processMessage(Chat chat, Message message) {
    HashMap<String, String> event = unpackEventFrom(message);
    String type = event.get("Event");
    switch (type) {
      case "CLOSE":
        listener.auctionClosed(); break;
      case "PRICE":
        listener.currentPrice(Integer.parseInt(event.get("CurrentPrice")),
          Integer.parseInt(event.get("Increment"))); break;
    }
  }

  private HashMap<String, String> unpackEventFrom(Message message) {
    HashMap<String, String> event = new HashMap<>();
    for (String element : message.getBody().split(";")) {
      String[] pair = element.split(":");
      event.put(pair[0].trim(), pair[1].trim());
    }
    return event;
  }

}
