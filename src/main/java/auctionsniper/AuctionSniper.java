package auctionsniper;

/**
 * Created by Ed on 22.04.15.
 */
public class AuctionSniper implements AuctionEventListener {

  private final SniperListener sniperListener;
  private final Auction auction;

  public AuctionSniper(Auction auction, SniperListener sniperListener) {
    this.sniperListener = sniperListener;
    this.auction = auction;
  }

  @Override
  public void auctionClosed() {
    sniperListener.sniperLost();
  }

  @Override
  public void currentPrice(int price, int increment) {
    auction.bid(price + increment);
    sniperListener.sniperBidding();
  }

}
