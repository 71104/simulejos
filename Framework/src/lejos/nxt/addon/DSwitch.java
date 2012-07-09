package lejos.nxt.addon;

import lejos.nxt.*;

/**
 * This class manage the device dSwitch which
 * is a controller designed to be used with Lego NXT Mindstorms hardware and software
 * 
 * @author Juan Antonio Brenha Moral
 *
 */
public class DSwitch extends NXTMotor {

	  public DSwitch(TachoMotorPort port){
	    super(port);
        setPower(TachoMotorPort.MAX_POWER);
	  }
	  
	  /**
	   * This method turn on the device connected with DSwitch
	   */
	  public void turnOn(){
		  this.forward();
	  }
	  
	  /**
	   * This method turn off the device connected with DSwitch
	   */	  
	  public void turnOff(){
		  this.stop();
	  }
}