package lejos.nxt;

/**
 * Read and write access to flash memory in pages.
 * 
 * @author Lawrie Griffiths.
 *
 */
public class Flash {
	
	/**
	 * Maximum number of pages available to user flash memory.
     * This value is obtained automatically from the firmware.
	 * 
	 */
	public static final int MAX_USER_PAGES = NXT.getUserPages();
	
	/**
	 * Indicates the # of bytes per page in a page of Flash memory.
	 */
	public static short BYTES_PER_PAGE = 256;

	private Flash()
	{
		//Static methods only
	}
	
	static native int flashReadPage(byte[] buf, int pageNum);

	static native int flashWritePage(byte[] buf, int pageNum);
	
	static native int flashExec(int pageNum, int size);

    public static void readPage(byte[] buf, int pageNum) throws FlashError
    {
       if (flashReadPage(buf, pageNum) < 0)
           throw new FlashError("FRead:Bad address");
    }

    public static void writePage(byte[] buf, int pageNum) throws FlashError
    {
        String msg;
        int ret = flashWritePage(buf, pageNum);
        if (ret >= 0) return;
        switch(ret)
        {
            case -1:
                msg = "FWrite:TWI";
                break;
            case -2:
                msg = "FWrite:FTO";
                break;
            case -3:
                msg = "FWrite:Bad address";
                break;
            default:
                msg = "FWrite:Unkown" + ret;
                break;
       }
       throw new FlashError(msg);
    }

    public static void exec(int pageNum, int size) throws FlashError
    {
        if (flashExec(pageNum, size) < 0) throw new FlashError("FExec");
    }
}
