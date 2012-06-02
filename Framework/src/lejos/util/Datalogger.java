package lejos.util;


import java.util.ArrayList;
import lejos.nxt.comm.*;
import lejos.nxt.*;
import java.io.*;

/**
 * Datalogger class; stores float values then  then transmits  via bluetooth or usb<br>
 * works with DataViewer   in pctools.
 * 
 * @author  Roger Glassey   - revised 2/12/10 using ArrayList
 */
public class Datalogger
{

  private ArrayList<Float> log = new ArrayList<Float>();

  /**
   * write a float  value to the log
   * @param v
   */
  public void writeLog(float v)
  {
    Float f = new Float(v);
     log.add(f);
  }

/**
 * write 2 float values to the log
 * @param v0
 * @param v1
 */
  public void writeLog(float v0, float v1)
  {
    writeLog(v0);
    writeLog(v1);
  }
 /**
  * write 3 float values to the log
  * @param v0
  * @param v1
  * @param v2
  */
  public void writeLog(float v0, float v1, float v2)
  {
    writeLog(v0,v1);
    writeLog(v2);
  }
  /**
   * write 4 float values to the log
   * @param v0
   * @param v1
   * @param v2
   * @param v3
   */
  public void writeLog(float v0, float v1, float v2, float v3)
  {
    writeLog(v0, v1);
    writeLog(v2,v3);
  }


  /**
   * transmit the stored values to the PC via USB or bluetooth;<br>
   * Displays menu of choices for transmission mode. Scroll to select, press ENTER <br>
   * Then displays "wait for BT" or "wait for USB".  In DataViewer, click on "StartDownload"
   * When finished, displays the number values sent, and asks "Resend?".
   * Press ESC to exit the program, any other key to resend.  <br>
   * Then start the download in DataViewer.
   */
  public void transmit()
  {
    NXTConnection connection = null;
    DataOutputStream dataOut = null;
    InputStream is = null;
    String[] items ={" USB", " Bluetooth"};
    TextMenu tm = new TextMenu(items, 2, "Transmit using");
    int s = tm.select();
    LCD.clear();
    if (s == 0)
    {
      LCD.drawString("wait for USB", 0, 0);
      connection = USB.waitForConnection();

    } else
    {
      LCD.drawString("wait for BT", 0, 0);
      connection = Bluetooth.waitForConnection();
    }
    {
      try
      {
        is = connection.openInputStream();
        dataOut = connection.openDataOutputStream();
      } catch (Exception ie)
      {
      }
    }
    LCD.drawString("connected", 0, 1);
    boolean more = true;
    while (more)
    {
      try
      {
        LCD.clear();
        LCD.drawString("Wait for Viewer", 0, 0);
        int b = 0;
        b = is.read();
        LCD.drawInt(b, 8, 1);
      } catch (IOException ie)
      {
        LCD.drawString("no connection", 0, 0);
      }

      LCD.clear();
      LCD.drawString("sending ", 0, 0);
      LCD.drawInt(log.size(),4, 8, 0);
      try
      {

        dataOut.writeInt(log.size());
        dataOut.flush();
        for (int i = 0; i < log.size(); i++)
        {
          Float v = log.get(i);
          dataOut.writeFloat(v.floatValue());
        }
        dataOut.flush();
        dataOut.close();
      } catch (IOException e)
      {
        LCD.drawString("write error", 0, 0);
        LCD.refresh();
      }
      LCD.clear();
      Sound.beepSequence();
      LCD.drawString("Sent " + log.size(), 0, 0);
      tm.setTitle("Resend?         ");
      String[] itms ={"Yes", "No"};
      tm.setItems(itms);
      more = 0 == tm.select();
    }
    try
    {
      dataOut.close();
    } catch (IOException e)
    {
    }
  }
}
