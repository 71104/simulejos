package lejos.util;
import lejos.nxt.*;
import lejos.util.Delay;
import lejos.util.Stopwatch;

/**
This is class is for data entry using the NXT keyboard.
Counts number of presses of left and right buttons <br>
Press ENTER together with LEFT  or RIGHT to decrease the count.
The count will continue to change as long as the buttons are held down.
Press ESCAPE to end counting. 
Displays the count as it is entered, and makes a sound with each button press.


@author Roger Glassey 8/20/07
*/
 
public class ButtonCounter

{
/**
	Output only not used in calculations ; use this value after count() returns;
 */
	public int _rightCount= 0;

	/**
	Output only not used in calculations; use after count() returns;
	 */
	public int _leftCount= 0;

/**
 * identifies the ENTER button
 */
	static final byte ENTER=0x01;
/**
 * identifies the left button
 */
 	static final byte LEFT=0x02;
 /**
 * identifies the right button
 */
	static final byte RIGHT = 0x04;
	/**
	 * identifies  escape button
	 */
	static final byte ESC = 0x08;
	static final String BLANK = "                ";
     static final int delay = 400;//ms between automatic increment
     static final Stopwatch sw = new Stopwatch();
/**
 * Use this method after counting is complete;
 * @return value of right count.
 */	
	public int  getRightCount() { return _rightCount; }
	/**
	 * Use this method after counting is complete;
	 * @return value of left count.
	 */ 
	public int  getLeftCount() { return _leftCount; }
	private boolean _reset = true;

/**
Start counting; parameter string is displayed; use it to identify this  particular call <br>
returns when ESC or ENTER button is pressed <br>
Hold Enter and left or right to decrease count,  otherwise it increases <br>
counters are reset when this method is called.
*/
	public void count(String s)
	{
	   LCD.drawString(BLANK, 0, 0);
	   LCD.drawString(s,0,0);
	   _reset = true;
	   count();
	}
/**
 * called by count( string) 
 */	
	public void count()
	{
       if(_reset)       
       {
           _rightCount = 0;
           _leftCount = 0;
       }
	   while(Button.readButtons() > 0);// wait for release
		boolean counting = true;
		show();
		while(counting)
		{
            int b = Button.waitForAnyPress();
            Delay.msDelay(200);
            b = Button.readButtons();
		    if( b == ESC )counting = false;
			else
			{
                  sw.reset();
				if(b == LEFT)
                    {
                       _leftCount++;
                       while(buttonHeld(b)) _leftCount++;
                    }
				if(b == RIGHT)
                    {
                       _rightCount++;
                        while(buttonHeld(b)) _rightCount++;
                    }
				if(b == LEFT + ENTER)
                    {
                       _leftCount--;
                        while(buttonHeld(b)) _leftCount--;
                    }
				if(b == RIGHT + ENTER)
                    {
                       _rightCount--;
                        while(buttonHeld(b))_rightCount--;                   
                    }			
			}
			show();	
		}//end while when ESC is pressed
	}

     private boolean buttonHeld(int button)
   {
      boolean held = false;
      show();
      while (Button.readButtons() == button && sw.elapsed() < delay);
      if (sw.elapsed() >= delay)
      {
         sw.reset();
         held = true;
         Sound.beep();
         show();
      }
      return held;


   }

/**
 * Initializes values of left count and right count.
 * Displays parameter   s   ; sets _leftcount = left,  _rightCount = right
 */
	public void count(String s, int left, int right)
	{
		LCD.drawString(BLANK, 0, 0);
		LCD.drawString(s,0,0);
		this._reset = false;
		_leftCount = left;
		_rightCount = right;
		count();
	}

	private void show()
	{
       LCD.drawString(BLANK,0, 1);
       LCD.drawInt(_leftCount,4,0,1);
       LCD.drawInt(_rightCount,4, 8, 1); 
	}



}
