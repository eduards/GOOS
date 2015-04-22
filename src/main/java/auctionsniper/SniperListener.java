package auctionsniper;

import java.util.EventListener;

/**
 * @author ed
 */
public interface SniperListener extends EventListener {

  void sniperLost();

  void sniperBidding();

}
