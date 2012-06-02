package lejos.nxt;

import java.io.*;
import lejos.util.Delay;

/**
 * NXT sound routines.
 *
 */
public class Sound
{

    private static final int RIFF_HDR_SIZE = 44;
    private static final int RIFF_RIFF_SIG = 0x52494646;
    private static final int RIFF_WAVE_SIG = 0x57415645;
    private static final int RIFF_FMT_SIG = 0x666d7420;
    private static final short RIFF_FMT_PCM = 0x0100;
    private static final short RIFF_FMT_1CHAN = 0x0100;
    private static final short RIFF_FMT_8BITS = 0x0800;
    private static final int RIFF_DATA_SIG = 0x64617461;
    
    public static final int VOL_MAX = 100;
    public static final String VOL_SETTING = "lejos.volume";
    // Instruments (yes I know they don't sound anything like the names!)
    public final static int[] PIANO = new int[]{4, 25, 500, 7000, 5};
    public final static int[] FLUTE = new int[]{10, 25, 2000, 1000, 25};
    public final static int[] XYLOPHONE = new int[]{1, 8, 3000, 5000, 5};
    
    private static int masterVolume = 0;
    
    /**
     * Static contstructor to force loading of system settings
     */
    static {
        loadSettings();
    }

    private Sound()
    {
    }
    public static int C2 = 523;
    
    /**
     * Play a system sound.
     * <TABLE BORDER=1>
     * <TR><TH>aCode</TH><TH>Resulting Sound</TH></TR>
     * <TR><TD>0</TD><TD>short beep</TD></TR>
     * <TR><TD>1</TD><TD>double beep</TD></TR>
     * <TR><TD>2</TD><TD>descending arpeggio</TD></TR>
     * <TR><TD>3</TD><TD>ascending  arpeggio</TD></TR>
     * <TR><TD>4</TD><TD>long, low buzz</TD></TR>
     * </TABLE>
     */
    public static void systemSound(boolean aQueued, int aCode)
    {
        if (aCode == 0)
            playTone(600, 200);
        else if (aCode == 1)
        {
            playTone(600, 150);
            pause(200);
            playTone(600, 150);
            pause(150);
        }
        else if (aCode == 2)// C major arpeggio
            for (int i = 4; i < 8; i++)
            {
                playTone(C2 * i / 4, 100);
                pause(100);
            }
        else if (aCode == 3)
            for (int i = 7; i > 3; i--)
            {
                playTone(C2 * i / 4, 100);
                pause(100);
            }
        else if (aCode == 4)
        {
            playTone(100, 500);
            pause(500);
        }
    }

    /**
     * Beeps once.
     */
    public static void beep()
    {
        systemSound(true, 0);
    }

    /**
     * Beeps twice.
     */
    public static void twoBeeps()
    {
        systemSound(true, 1);
    }

    /**
     * Downward tones.
     */
    public static void beepSequence()
    {
        systemSound(true, 3);
    }

    /**
     * Upward tones.
     */
    public static void beepSequenceUp()
    {
        systemSound(true, 2);
    }

    /**
     * Low buzz 
     */
    public static void buzz()
    {
        systemSound(true, 4);
    }

    public static void pause(int t)
    {
        Delay.msDelay(t);
    }

    /**
     * Returns the number of milliseconds remaining of the current tone or sample.
     * @return milliseconds remaining
     */
    public static native int getTime();
    
    /**
     * Plays a tone, given its frequency and duration. 
     * @param aFrequency The frequency of the tone in Hertz (Hz).
     * @param aDuration The duration of the tone, in milliseconds.
     * @param aVolume The volume of the playback 100 corresponds to 100%
     */
    static native void playFreq(int aFrequency, int aDuration, int aVolume);


    /**
     * Plays a tone, given its frequency and duration. 
     * @param aFrequency The frequency of the tone in Hertz (Hz).
     * @param aDuration The duration of the tone, in milliseconds.
     * @param aVolume The volume of the playback 100 corresponds to 100%
     */
    public static void playTone(int aFrequency, int aDuration, int aVolume)
    {
        if (aVolume >= 0)
            aVolume = (aVolume*masterVolume)/100;
        else
            aVolume = -aVolume;
        playFreq(aFrequency, aDuration, aVolume);
    }
    

    public static void playTone(int freq, int duration)
    {
        playTone(freq, duration, VOL_MAX);
    }

    /**
     * Internal method used to play sound sample from a file
     * @param page the start page of the file
     * @param offset the start of the samples in the file
     * @param len the length of the sample to play
     * @param freq the sampling frequency 
     * @param vol the volume 100 corresponds to 100%
     */
    static native void playSample(int page, int offset, int len, int freq, int vol);

    
    /**
     * Read an LSB format
     * @param d stream to read from
     * @return the read int
     * @throws java.io.IOException
     */
    private static int readLSBInt(DataInputStream d) throws IOException
    {
        int val = d.readByte() & 0xff;
        val |= (d.readByte() & 0xff) << 8;
        val |= (d.readByte() & 0xff) << 16;
        val |= (d.readByte() & 0xff) << 24;
        return val;
    }
    /**
     * Play a wav file
     * @param file the 8-bit PWM (WAV) sample file
     * @param vol the volume percentage 0 - 100
     * @return The number of milliseconds the sample will play for or < 0 if
     *         there is an error.
     */
    public static int playSample(File file, int vol)
    {
        // First check that we have a wave file. File must be at least 44 bytes
        // in size to contain a RIFF header.
        int offset = 0;
    	int sampleRate = 0;
    	int dataLen = 0;
    	
        if (file.length() < RIFF_HDR_SIZE)
            return -9;
        // Now check for a RIFF header
        try
        {
        	FileInputStream f = new FileInputStream(file);
        	DataInputStream d = new DataInputStream(f);

            if (d.readInt() != RIFF_RIFF_SIG)
                return -1;
            // Skip chunk size
            d.readInt();
            // Check we have a wave file
            if (d.readInt() != RIFF_WAVE_SIG)
                return -2;
            if (d.readInt() != RIFF_FMT_SIG)
                return -3;
            offset += 16;
            // Now check that the format is PCM, Mono 8 bits. Note that these
            // values are stored little endian.
            int sz = readLSBInt(d);
            if (d.readShort() != RIFF_FMT_PCM)
                return -4;
            if (d.readShort() != RIFF_FMT_1CHAN)
                return -5;
            sampleRate = readLSBInt(d);
            d.readInt();
            d.readShort();
            if (d.readShort() != RIFF_FMT_8BITS)
                return -6;
            // Skip any data in this chunk after the 16 bytes above
            sz -= 16;
            offset += 20 + sz;
            while (sz-- > 0)
                d.readByte();
            // Skip optional chunks until we find a data sig (or we hit eof!)
            for(;;)
            {
                int chunk = d.readInt();
                dataLen = readLSBInt(d); 
                offset += 8;
                if (chunk == RIFF_DATA_SIG) break;
                // Skip to the start of the next chunk
                offset += dataLen;
                while(dataLen-- > 0)
                    d.readByte();
            }
            d.close();
        }
        catch (IOException e)
        {
            return -8;
        }
        if (vol >= 0)
            vol = (vol*masterVolume)/100;
        else
            vol = -vol;
        playSample(file.getPage(), offset, dataLen, sampleRate, vol);
        return getTime();
    }


    /**
     * Play a wav file
     * @param file the 8-bit PWM (WAV) sample file
     * @return The number of milliseconds the sample will play for or < 0 if
     *         there is an error.
     */
    public static int playSample(File file)
    {
        return playSample(file, VOL_MAX);
    }

    /**
     * Internal method used to queue a series of PCM samples to play at the
     * specified volume and sample rate.
     * Returns the number of samples that have actually been queued.
     * @param data Buffer containing the samples
     * @param offset Offset of the first sample in the buffer
     * @param len Number of samples to queue
     * @param freq Sample rate
     * @param vol playback volume
     * @return Number of samples actually queued
     */
    static native int playQueuedSample(byte [] data, int offset, int len, int freq, int vol);

    /**
     * Queue a series of PCM samples to play at the
     * specified volume and sample rate.
     * Returns the number of samples that have actually been queued.
     * If the queue is full, the method exits immediately and returns 0.
     * 
     * @param data Buffer containing the samples
     * @param offset Offset of the first sample in the buffer
     * @param len Number of samples to queue
     * @param freq Sample rate
     * @param vol playback volume
     * @return Number of samples actually queued
     */
    public static int playSample(byte [] data, int offset, int len, int freq, int vol)
    {
        if (vol >= 0)
            vol = (vol*masterVolume)/100;
        else
            vol = -vol;
        return playQueuedSample(data, offset, len, freq, vol);
    }

    static int waitUntil(int t)
    {
        int t2;
        while ((t2 = (int) System.currentTimeMillis()) < t)
            Thread.yield();
        return t2;
    }

    /**
     * Play a note with attack, decay, sustain and release shape. This function
     * allows the playing of a more musical sounding note. It uses a set of
     * supplied "instrument" parameters to define the shape of the notes 
     * envelope.
     * @param inst Instrument definition
     * @param freq The note to play (in Hz)
     * @param len  The duration of the note (in ms)
     */
    public static void playNote(int[] inst, int freq, int len)
    {
        // Generate an enevlope controlled note. The instrument array contains
        // the shape of the envelope. The attack period, the decay period the
        // level to decay to and the level to decay to during the sustain part
        // of the note finally the length of the actual release period. All
        // periods are given in 2000th of a second units. This is because the
        // generation of a tone using the playTone function will often take
        // more than 1ms to execute. This means that the minimum tone segment
        // that we can reliably generate is 2ms and so we use this unit as the
        // basis of note generation.
        int segLen = inst[0];
        // All volume settings are scaled by 100.
        int step = 8000 / segLen;
        int vol = 2000;
        // We do not really have fine grained enough timing so try and keep
        // things aligned as closely as possible to a tick by waiting here
        // before we start for the next tick.
        int t = waitUntil((int) System.currentTimeMillis() + 1);
        // Generate the attack profile from 20 to full volume
        len /= 2;
        for (int i = 0; i < segLen; i++)
        {
            Sound.playTone(freq, 10, vol / 100);
            vol += step;
            t = waitUntil(t + 2);
        }
        len -= segLen;
        // Now do the decay
        segLen = inst[1];
        if (segLen > 0)
        {
            step = inst[2] / segLen;
            for (int i = 0; i < segLen; i++)
            {
                Sound.playTone(freq, 100, vol / 100);
                vol -= step;
                t = waitUntil(t + 2);
            }
            len -= segLen;
        }
        segLen = inst[4];
        len -= segLen;
        // adjust length of the sustain and possibly the release to match the
        // requested note length
        if (len > 0)
        {
            step = inst[3] / len;
            for (int i = 0; i < len; i++)
            {
                Sound.playTone(freq, 100, vol / 100);
                vol -= step;
                t = waitUntil(t + 2);
            }
        }
        else
            segLen += len;
        // Finally do the release
        if (segLen > 0)
        {
            step = (vol - 1000) / segLen;
            for (int i = 0; i < segLen; i++)
            {
                Sound.playTone(freq, 2, vol / 100);
                vol -= step;
                t = waitUntil(t + 2);
            }
        }
    }

    /**
     * Set the master volume level
     * @param vol 0-100
     */
    public static void setVolume(int vol)
    {
        if (vol > VOL_MAX) vol = VOL_MAX;
        if (vol < 0) vol = 0;
        masterVolume = vol;
    }

    /**
     * Get the current master volume level
     * @return the current master volume 0-100
     */
    public static int getVolume()
    {
        return masterVolume;
    }
    
    /**
     * Load the current system settings associated with this class. Called
     * automatically to initialize the class. May be called if it is required
     * to reload any settings.
     */
    public static void loadSettings()
    {
        masterVolume = SystemSettings.getIntSetting(VOL_SETTING, 80);
    }
}
