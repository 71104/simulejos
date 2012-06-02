package lejos.addon.gps;

import java.util.NoSuchElementException;
import java.util.StringTokenizer;

/**
 * This class has been designed to manage a GSV Sentence
 * 
 * GPS Satellites in view
 * 
 * eg. $GPGSV,3,1,11,03,03,111,00,04,15,270,00,06,01,010,00,13,06,292,00*74
 *     $GPGSV,3,2,11,14,25,170,00,16,57,208,39,18,67,296,40,19,40,246,00*74
 *     $GPGSV,3,3,11,22,42,067,42,24,14,311,43,27,05,244,00,,,,*4D
 * 
 * 
 *     $GPGSV,1,1,13,02,02,213,,03,-3,000,,11,00,121,,14,13,172,05*67
 * 
 * 1    = Total number of messages of this type in this cycle
 * 2    = Message number
 * 3    = Total number of SVs in view
 * 4    = SV PRN number
 * 5    = Elevation in degrees, 90 maximum
 * 6    = Azimuth, degrees from true north, 000 to 359
 * 7    = SNR, 00-99 dB (null when not tracking)
 * 8-11 = Information about second SV, same as field 4-7
 * 12-15= Information about third SV, same as field 4-7
 * 16-19= Information about fourth SV, same as field 4-7
 * 
 * @author Juan Antonio Brenha Moral
 * 
 */
public class GSVSentence extends NMEASentence{
	
	//GGA
	private String nmeaHeader = "";
	private int satellitesInView = 0;
	private final int MAXIMUMSATELLITES = 4;
	private Satellite ns1;
	private Satellite ns2;
	private Satellite ns3;
	private Satellite ns4;
	
	//Header
	public static final String HEADER = "$GPGSV";

	/*
	 * Constructor
	 */
	public GSVSentence(){
		ns1 = new Satellite();
		ns2 = new Satellite();
		ns3 = new Satellite();
		ns4 = new Satellite();
	}
	
	
	/*
	 * GETTERS & SETTERS
	 */

	/**
	 * Returns the NMEA header for this sentence.
	 */
	@Override
	public String getHeader() {
		return HEADER;
	}
	
	/**
	 * Returns the number of satellites being tracked to
	 * determine the coordinates.
	 * 
	 * @return Number of satellites e.g. 8
	 */
	public int getSatellitesInView() {
		return satellitesInView;
	}

	/**
	 * Return a NMEA Satellite object
	 * 
	 * @param index
	 * @return
	 */
	public Satellite getSatellite(int index){
		Satellite ns = new Satellite();
		if(index == 0){
			ns = ns1;
		}else if(index == 1){
			ns = ns2;
		}else if(index == 2){
			ns = ns3;
		}else if(index == 3){
			ns = ns4;
		}
		return ns;
	}

	
	/**
	 * Method used to parse a GSV Sentence
	 */
	public void parse(String sentence){

		//TODO StringTokenizer must not be used to parse NMEA sentences since it doesn't return empty tokens 
		StringTokenizer st = new StringTokenizer(sentence,",");
		int PRN = 0;
		int elevation = 0;
		int azimuth = 0;
		int SNR = 0;

		try{
			
			//Extracting data from a GSV Sentence
			
			//TODO Length of GSV Sentence varies.
			// See http://www.gpsinformation.org/dale/nmea.htm for an example
			
			String part0 = st.nextToken();//NMEA header
			st.nextToken();//Number of messages
			st.nextToken();//Message number
			String part3 = st.nextToken();//
			String part4 = st.nextToken();//
			String part5 = st.nextToken();//
			String part6 = st.nextToken();//
			String part7 = st.nextToken();//
			String part8 = st.nextToken();//
			String part9 = st.nextToken();//
			String part10 = st.nextToken();//
			String part11 = st.nextToken();//
			String part12 = st.nextToken();//
			String part13 = st.nextToken();//
			String part14 = st.nextToken();//
			String part15 = st.nextToken();//
			String part16 = st.nextToken();//
			String part17 = st.nextToken();//
			String part18 = st.nextToken();//
			String part19 = st.nextToken();//
			
			st = null;
			
			nmeaHeader = part0;
			
			if(part3.length() == 0){
				satellitesInView = 0;
			}else{
				satellitesInView = Math.round(Float.parseFloat(part3));
			}
			
			if(satellitesInView > 0){
				
				//SAT 1
				
				if(part4.length() == 0){
					PRN = 0;
				}else{
					PRN = Math.round(Float.parseFloat(part4));
				}

				if(part5.length() == 0){
					elevation = 0;
				}else{
					elevation = Math.round(Float.parseFloat(part5));
				}

				if(part6.length() == 0){
					azimuth = 0;
				}else{
					azimuth = Math.round(Float.parseFloat(part6));
				}

				if(part7.length() == 0){
					SNR = 0;
				}else{
					SNR = Math.round(Float.parseFloat(part7));
				}

				ns1.setPRN(PRN);
				ns1.setElevation(elevation);
				ns1.setAzimuth(azimuth);
				ns1.setSignalNoiseRatio(SNR);
				
				//SAT 2
				
				if(part8.length() == 0){
					PRN = 0;
				}else{
					PRN = Math.round(Float.parseFloat(part8));
				}

				if(part9.length() == 0){
					elevation = 0;
				}else{
					elevation = Math.round(Float.parseFloat(part9));
				}

				if(part10.length() == 0){
					azimuth = 0;
				}else{
					azimuth = Math.round(Float.parseFloat(part10));
				}

				if(part11.length() == 0){
					SNR = 0;
				}else{
					SNR = Math.round(Float.parseFloat(part11));
				}
				
				ns2.setPRN(PRN);
				ns2.setElevation(elevation);
				ns2.setAzimuth(azimuth);
				ns2.setSignalNoiseRatio(SNR);
				
				//SAT 3

				if(part12.length() == 0){
					PRN = 0;
				}else{
					PRN = Math.round(Float.parseFloat(part12));
				}

				if(part13.length() == 0){
					elevation = 0;
				}else{
					elevation = Math.round(Float.parseFloat(part13));
				}

				if(part14.length() == 0){
					azimuth = 0;
				}else{
					azimuth = Math.round(Float.parseFloat(part14));
				}

				if(part15.length() == 0){
					SNR = 0;
				}else{
					SNR = Math.round(Float.parseFloat(part15));
				}
				
				ns3.setPRN(PRN);
				ns3.setElevation(elevation);
				ns3.setAzimuth(azimuth);
				ns3.setSignalNoiseRatio(SNR);
				
				// SAT 4

				if(part16.length() == 0){
					PRN = 0;
				}else{
					PRN = Math.round(Float.parseFloat(part16));
				}

				if(part17.length() == 0){
					elevation = 0;
				}else{
					elevation = Math.round(Float.parseFloat(part17));
				}

				if(part18.length() == 0){
					azimuth = 0;
				}else{
					azimuth = Math.round(Float.parseFloat(part18));
				}

				if(part19.length() == 0){
					SNR = 0;
				}else{
					SNR = Math.round(Float.parseFloat(part19));
				}
				
				ns4.setPRN(PRN);
				ns4.setElevation(elevation);
				ns4.setAzimuth(azimuth);
				ns4.setSignalNoiseRatio(SNR);				
				
			}
			
		}catch(NoSuchElementException e){
			//System.err.println("GSVSentence: NoSuchElementException");
		}catch(NumberFormatException e){
			//System.err.println("GSVSentence: NumberFormatException");
		}catch(Exception e){
			//System.err.println("GSVSentence: Exception");
		}

	}//End parse
	
}//End class
