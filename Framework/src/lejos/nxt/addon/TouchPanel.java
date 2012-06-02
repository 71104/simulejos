package lejos.nxt.addon;

import java.awt.Point;
import java.util.ArrayList;

import javax.microedition.lcdui.Graphics;

import lejos.nxt.Button;
import lejos.nxt.I2CPort;
import lejos.nxt.I2CSensor;
import lejos.nxt.LCD;
import lejos.nxt.Sound;
import lejos.util.Delay;


/**
 * This Class manages the MINDSENSORS Touch Panel.
 * 
 * @author Daniele Benedettelli, January 2012
 * @version 1.0
 */
public class TouchPanel extends I2CSensor {

	private byte[] inBuf = new byte[16];
	private static final byte TP_ADDRESS = 0x04;
	
	private static final byte TP_COMMAND = 0x41;
	
	private static final byte TP_REG_TOUCH_X = 0x42;
//	private static final byte TP_REG_TOUCH_Y = 0x43;
	private static final byte TP_REG_BUTTONS = 0x44;

	private static final byte TP_REG_CAL_XD1 = 0x45;
	private static final byte TP_REG_CAL_YD1 = 0x46;
	private static final byte TP_REG_CAL_XT1 = 0x47;
	private static final byte TP_REG_CAL_YT1 = 0x48;
	private static final byte TP_REG_CAL_XD2 = 0x49;
	private static final byte TP_REG_CAL_YD2 = 0x4a;
	private static final byte TP_REG_CAL_XT2 = 0x4b;
	private static final byte TP_REG_CAL_YT2 = 0x4c;

	// raw byte count of the gesture data
	private static final byte TP_REG_RAW_N = 0x4d;
	private static final byte TP_REG_RAW_X = 0x4e;
	private static final byte TP_REG_RAW_Y = 0x4f;
	
	public static final byte BTN_L4 = 0x0;
	public static final byte BTN_L3 = 0x1;
	public static final byte BTN_L2 = 0x2;
	public static final byte BTN_L1 = 0x3;
	public static final byte BTN_R4 = 0x4;
	public static final byte BTN_R3 = 0x5;
	public static final byte BTN_R2 = 0x6;
	public static final byte BTN_R1 = 0x7;
	
	/**
	 * Virtual button 1 at right (topmost)
	 */
	public final VirtualButton R1;
	/**
	 * Virtual button 2 at right 
	 */	
	public final VirtualButton R2;
	/**
	 * Virtual button 3 at right 
	 */	
	public final VirtualButton R3;
	/**
	 * Virtual button 4 at right (downmost) 
	 */		
	public final VirtualButton R4;
	/**
	 * Virtual button 1 at left (topmost)
	 */
	public final VirtualButton L1;
	/**
	 * Virtual button 2 at left 
	 */
	public final VirtualButton L2;
	/**
	 * Virtual button 3 at left
	 */
	public final VirtualButton L3;
	/**
	 * Virtual button 4 at left (downmost)
	 */
	public final VirtualButton L4;
	
	private final static Point nullPoint = new Point(0,0);
	private Point lastPoint;
	private boolean calibrated = true;
	private byte lastButtons = 0;
	
	/**
	 * Instantiates a new Touch Panel sensor.
	 *
	 * @param port the port the sensor is attached to
	 */
	public TouchPanel(I2CPort port) {
		super(port);
		port.setMode(I2CPort.HIGH_SPEED);
		this.address = TP_ADDRESS;
		R1 = new VirtualButton (this,BTN_R1);
		R2 = new VirtualButton (this,BTN_R2);
		R3 = new VirtualButton (this,BTN_R3);
		R4 = new VirtualButton (this,BTN_R4);
		L1 = new VirtualButton (this,BTN_L1);
		L2 = new VirtualButton (this,BTN_L2);
		L3 = new VirtualButton (this,BTN_L3);
		L4 = new VirtualButton (this,BTN_L4);
		lastPoint = new Point();
		setCalibratedMode(true);
	}
	
	private boolean isNear(int num, int target, int within)
	{
		return ( num < (target+within)) && (num > (target-within));
	}
	
	/**
	 * Starts the calibration routine. The user is instructed how to calibrate the touch panel.
	 */
	public void calibrate() {
		  int  X,Y;
		  Graphics g = new Graphics();
		  final int READINGS = 20;
		  int stylus_pressed = 0;
	  
		  // show splash screen
		  LCD.clear();
		  LCD.drawString("--TouchPanel ---", 0,0);
		  LCD.drawString("--Calibration---", 0,1);
		  LCD.drawString("Watch screen and", 0,2);
		  LCD.drawString("aftr beep, press", 0,3);
		  LCD.drawString("and hold Stylus ", 0,4);
		  LCD.drawString("on the Crosshair", 0,5);
		  LCD.drawString("center point.   ", 0,6);
		  LCD.drawString("ORNG BTN to go  ", 0,7);
		  
		  setCalibratedMode(false);// change mode to un-calibrated readings.
		  Delay.msDelay(50);
		  setZero(); // pick up zero position.
		  Delay.msDelay(50);
		  setCalibratedMode(false); // change mode to un-calibrated readings.
		  int btn = 0;
		  while((btn=Button.readButtons())==0) Delay.msDelay(1);
		  if (btn==Button.ID_ESCAPE) {
			  while(Button.ESCAPE.isDown()) Delay.msDelay(1);
			  return;
		  }

		  LCD.clear();
		  LCD.setPixel(10, 60, 1);
		  g.drawLine(5, 60, 15, 60);
		  g.drawLine(10, 55, 10, 65);
		  Sound.playTone(440,50);
		  stylus_pressed = READINGS;
		  X = 0;
		  Y = 0;

		  while (stylus_pressed > 0) {
			  lastPoint = getPoint();
			  LCD.drawString(">>X: "+lastPoint.x+" Y: "+lastPoint.y+"   ", 0, 5);
			  // xd1 10,60 - 80,45 
			  if ( isNear(lastPoint.x, 80, 7) && isNear(lastPoint.y, 45, 7)  ) {
				  X += lastPoint.x;
				  Y += lastPoint.y;
				  stylus_pressed --;
			  }
		  }
		  X /= READINGS;
		  Y /= READINGS;
		  sendData(TP_REG_CAL_XD1, (byte)10);
		  sendData(TP_REG_CAL_YD1, (byte)10);
		  sendData(TP_REG_CAL_XT1, (byte)X);
		  sendData(TP_REG_CAL_YT1, (byte)Y);		  

		  LCD.clear();
		  LCD.drawString("X: "+X+" Y: "+Y+"   ", 0, 6);

		  Delay.msDelay(600);
		  LCD.drawString("ORNG BTN to go  ", 0,7);

		  Button.ENTER.waitForPressAndRelease();
		  
		  LCD.clear();
		  LCD.setPixel(90, 10, 1);
		  g.drawLine(85, 10, 95, 10);
		  g.drawLine(90, 5, 90, 15);
		  Sound.playTone(440,50);
		  stylus_pressed = READINGS;
		  X = 0;
		  Y = 0;
		  
		  while (stylus_pressed > 0) {
			  lastPoint = getPoint();
			  LCD.drawString(">>X: "+lastPoint.x+" Y: "+lastPoint.y+"   ", 0, 5);
			  // xd2 90,10 - 193,104 
			  if ( isNear(lastPoint.x, 193, 7) && isNear(lastPoint.y, 104, 7)  ) {
				  X += lastPoint.x;
				  Y += lastPoint.y;
				  stylus_pressed --;
			  }
		  }
		  X /= READINGS;
		  Y /= READINGS;
		  sendData(TP_REG_CAL_XD2, (byte)90);
		  sendData(TP_REG_CAL_YD2, (byte)60);
		  sendData(TP_REG_CAL_XT2, (byte)X);
		  sendData(TP_REG_CAL_YT2, (byte)Y);

		  LCD.drawString("X: "+X+" Y: "+Y+"   ", 0, 6);
		  LCD.drawString("Calibrating.....", 0,7);
		  saveCalibration();
		  Delay.msDelay(1000);

		  LCD.drawString("ORNG BTN to return  ", 0,7);

		  Button.ENTER.waitForPressAndRelease();
	}
	
	/**
	 * Gets the point where the touch panel is being touched.
	 *
	 * @return the point
	 */
	public Point getPoint() {
		int ret = getData(TP_REG_TOUCH_X, inBuf, 2);
		if (ret==0) {
			if (calibrated) {
				lastPoint.x = (inBuf[0] & 0x7F);
				if (inBuf[0]==0 && inBuf[1]==0) // to have a zero reading when not touched 
					lastPoint.y = 0; 
				else 
					lastPoint.y = LCD.SCREEN_HEIGHT-(inBuf[1] & 0x7F); // Y-mirror due to LejOS LCD coordinate system
			} else {
				lastPoint.x = (inBuf[0] & 0xFF);
				lastPoint.y = (inBuf[1] & 0x7F);
			}
			return lastPoint;
		}
		return nullPoint;
	}
	
	/**
	 * Gets the button state after last readButtons call.
	 *
	 * @param btnId the button id 
	 * @return the button last state
	 */
	public boolean getButtonLastState(byte btnId) {
		return ( (lastButtons>>btnId) & 0x01) == 0;
	}

	/**
	 * Private function to read the buttons register.
	 * To read buttons, use the VirtualButton R1,R2,R3,R4,L1,L2,L3,L4 methods isDown() and isUp()
	 * 
	 * @return the byte
	 */	
	public byte readButtons() {
		int ret = getData(TP_REG_BUTTONS, inBuf, 1);
		if (ret==0) { 
			lastButtons = inBuf[0];
		} else 
			lastButtons = 0;
		return lastButtons;
	}
	
	/**
	 * Checks if any virtual button is down.
	 *
	 * @return true, if any button is down.
	 */
	public boolean isAnyButtonDown() {
		return readButtons()!=0;
	}

	/**
	 * Checks if no virtual button is down.
	 *
	 * @return true, if no button is down.
	 */
	public boolean isNoButtonDown() {
		return readButtons()==0;
	}
	
	/**
	 * Sets the mode (calibrated or not)
	 *
	 * @return true, if successful
	 */
	public boolean setCalibratedMode(boolean yes)
	{
		calibrated = yes;
		int res = -1;
		if (yes)
			res = sendData(TP_COMMAND, (byte)'B');
		else 
			res = sendData(TP_COMMAND, (byte)'U');
		return res==0;
	}	
	
	/**
	 * Commits calibration data to permanent memory
	 *
	 * @return true, if successful
	 */
	public boolean saveCalibration()
	{
		return sendData(TP_COMMAND, (byte)'C')==0;
	}	
	
	/**
	 * To be called when panel is not touched, sets the zero condition. 
	 * Not present in original MINDSENSORS documentation!
	 *
	 * @return true, if successful
	 */
	private boolean setZero() {
		return sendData(TP_COMMAND,(byte)'Z')==0;
	}
	
	/**
	 * Restores calibration data to factory defaults
	 *
	 * @return true, if successful
	 */
	public boolean restoreDefaultCalibration()
	{
		return sendData(TP_COMMAND, (byte)'F')==0;
	}	
	
	/**
	 * Sets the gesture sampling rate, 
	 * 
	 * @param rate can be a number from 1 to 8, 1 being the fastest, 8 being the slowest, default is 4
	 * @throws IllegalArgumentException if rate is outside the range 1..8 
	 *
	 * @return true, if successful
	 */
	public boolean setSamplingRate(int rate) throws IllegalArgumentException 
	{
		if (rate<1 || rate>8) throw new IllegalArgumentException("Valid sample rates are between 1 and 8!");
		return sendData(TP_COMMAND, (byte)(rate+0x30) )==0;
	}		
	
	
	/**
	 * Read all available points from the device. Maximum available points are 110.
	 * @return the array of Points 
	 */
	public ArrayList<Point> getGesture() {

		final int CC = 15;
		boolean continueReading;
		ArrayList<Point> list = new ArrayList<Point>();
		list.clear();
		setCalibratedMode(true);
		
		int count = CC;
		int len = 0;
		int pointsAdded = 0;
		int x = 0;
		int y = 0;

		// read if there is any data available
		if (getData(TP_REG_RAW_N, inBuf, 1)==0)
			len = inBuf[0];
		else len = 0;

		if ( len > 0 ) {
			continueReading = true;
		} else {
			return list;
		}

		// read if there is new data available
		while ( continueReading )
		{
			if ( getData(TP_REG_RAW_N, inBuf, count)==0 )
			{
				len = inBuf[0];

				if ( len > ((CC-1)/2) ) {
					len = ((CC-1)/2);
				} else {
					continueReading = false;  // data is smaller than our buffer, indicating end of data.
				}

				for ( int i = 1; i <= len*2; i+=2) {
					x = (inBuf[i] & 0x7F);
					y = 0;
					if (inBuf[i]!=0 || inBuf[i+1]!=0) // to have a zero reading when not touched 
						y = LCD.SCREEN_HEIGHT-(inBuf[i+1] & 0x7F); // Y-mirror due to LejOS LCD coordinate system

					list.add(new Point(x, y));
					pointsAdded++;
					if ( pointsAdded > 220) {
						return list;
					}
				}
			}
			else {
				continueReading = false; // no more data
			}
		}
		return list;
	}
	
	
	/**
	 * The inner class VirtualButton is used to read the touch panel 8 virtual buttons.
	 */
	public class VirtualButton {
		
		private final byte iCode;
		private final TouchPanel myTP;
		
		VirtualButton(TouchPanel tp, byte aCode)
		{
			iCode = aCode;
			myTP = tp;
		}
		
		public boolean isDown() {
			return ( (myTP.readButtons()>>iCode) & 0x01) !=0;
		}
		
		public boolean isUp() {
			return ( (myTP.readButtons()>>iCode) & 0x01) == 0;
		}
		
		public void waitForRelease() {
			while(isDown()) Delay.msDelay(10);
		}
		
		@Override
		public String toString() {
			switch(iCode) {
				case 0: return "L4";
				case 1: return "L3";
				case 2: return "L2";
				case 3: return "L1";
				case 4: return "R4";
				case 5: return "R3";
				case 6: return "R2";
				case 7: return "R1";
			}
			return "";
		}
		
	}

}
