package e2eTests.helper;

import auctionsniper.ui.MainWindow;
import com.objogate.wl.swing.AWTEventQueueProber;
import com.objogate.wl.swing.driver.JFrameDriver;
import com.objogate.wl.swing.driver.JLabelDriver;
import com.objogate.wl.swing.gesture.GesturePerformer;

import static org.hamcrest.CoreMatchers.equalTo;

/**
 * An AuctionSniperDriver is an extension of a WindowLicker JFrameDriver
 * specialized for our tests. On construction, it attempts to find a visible
 * top-level window for the Auction Sniper within the given timeout.
 *
 * @author ed
 */
public class AuctionSniperDriver extends JFrameDriver {

  public AuctionSniperDriver(int timeoutMillis) {
    super(new GesturePerformer(),
        JFrameDriver.topLevelFrame(
            named(MainWindow.MAIN_WINDOW_NAME),
            showingOnScreen()),
        new AWTEventQueueProber(timeoutMillis, 100));
  }

  /**
   * Looks for the relevant label in the user interface and confirms that it
   * shows the given status.
   */
  public void showsSniperStatus(String statusText) {
    new JLabelDriver(this, named(MainWindow.SNIPER_STATUS_NAME))
        .hasText(equalTo(statusText));
  }

}
