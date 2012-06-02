package lejos.nxt.comm;

/**
 * Support for LCP commands over Bluetooth in user programs.
 * 
 * @author Lawrie Griffiths
 *
 */
public class LCPBTResponder extends LCPResponder {
    
    public LCPBTResponder()
    {
        super(Bluetooth.getConnector());
    }
}
