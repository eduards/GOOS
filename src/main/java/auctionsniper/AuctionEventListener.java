package auctionsniper;

/**
 * Listener interface for auction events
 * @author ed
 */
public interface AuctionEventListener {

  void auctionClosed();

  void currentPrice(int price, int increment);

}
