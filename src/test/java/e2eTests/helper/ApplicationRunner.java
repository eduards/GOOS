package e2eTests.helper;

import auctionsniper.Main;
import auctionsniper.ui.MainWindow;

/**
 * This class wraps up all management and communicating with the Swing
 * application and the application as if from the command line
 *
 * @author ed
 */
public class ApplicationRunner {

  public static final String SNIPER_ID = "sniper";
  public static final String SNIPER_PASSWORD = "sniper";
  public static final String XMPP_HOSTNAME = "localhost";
  public static final String SNIPER_XMPP_ID = "sniper@localhost/Auction";

  private AuctionSniperDriver driver;

  public void startBiddingIn(final FakeAuctionServer auction) {
    Thread thread = new Thread("Test Application") {
      @Override
      public void run() {
        try {
          Main.main(XMPP_HOSTNAME, SNIPER_ID, SNIPER_PASSWORD, auction.getItemId());
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    };
    thread.setDaemon(true);
    thread.start();
    driver = new AuctionSniperDriver(1000);
    driver.showsSniperStatus(MainWindow.STATUS_JOINING);
  }

  /**
   * Assert that the Sniper shows a Lost state
   */
  public void showsSniperHasLostAuction() {
    driver.showsSniperStatus(MainWindow.STATUS_LOST);
  }

  /**
   * After the test, we tell the driver to dispose of the window to make sure
   * it won’t be picked up in another test before being garbage-collected.
   */
  public void stop() {
    if (driver != null) {
      driver.dispose();
    }
  }

  /**
   * Assert that the Sniper shows a Bidding state
   */
  public void hasShownSniperIsBidding() {
    driver.showsSniperStatus(MainWindow.STATUS_BIDDING);
  }

}
