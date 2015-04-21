package End2EndTests;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

/**
 * Helper class which wraps the message queue for the FakeAuctionServer.
 * It uses a single-element Blocking Queue, as we expect to process only
 * one message at a time.
 *
 * @author ed
 */
public class SingleMessageListener implements MessageListener {

  private final ArrayBlockingQueue<Message> messages =
      new ArrayBlockingQueue<>(1);

  public void processMessage(Chat chat, Message message) {
    messages.add(message);
  }

  /**
   * Check if any message was received
   *
   * @throws InterruptedException if no message is received within 5 seconds
   */
  public void receivesAMessage() throws InterruptedException {
    assertThat("Message",
        messages.poll(5, TimeUnit.SECONDS),
        is(notNullValue()));
  }

}
