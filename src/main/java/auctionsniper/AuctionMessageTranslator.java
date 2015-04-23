package auctionsniper;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.HashMap;
import java.util.Map;

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
    AuctionEvent event = AuctionEvent.from(message.getBody());
    switch (event.type()) {
      case "CLOSE":
        listener.auctionClosed();
        break;
      case "PRICE":
        listener.currentPrice(event.currentPrice(), event.increment());
        break;
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

  private static class AuctionEvent {
    private final Map<String, String> fields = new HashMap<>();

    static AuctionEvent from(String messageBody) {
      AuctionEvent event = new AuctionEvent();
      for (String field : fieldsIn(messageBody)) {
        event.addField(field);
      }
      return event;
    }

    static String[] fieldsIn(String messageBody) {
      return messageBody.split(";");
    }

    public String type() {
      return get("Event");
    }

    public int currentPrice() {
      return getInt("CurrentPrice");
    }

    public int increment() {
      return getInt("Increment");
    }

    private int getInt(String fieldName) {
      return Integer.parseInt(get(fieldName));
    }

    private String get(String fieldName) {
      return fields.get(fieldName);
    }

    private void addField(String field) {
      String[] pair = field.split(":");
      fields.put(pair[0].trim(), pair[1].trim());
    }
  }
}
