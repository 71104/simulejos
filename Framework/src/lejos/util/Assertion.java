package lejos.util;

/**
 * Class used in debugging to test assertions. Implementation
 * is platform dependent. For example the unix_impl version
 * will print the string and throw an error if an assertion fails.
 * The rcx_impl version will only throw an error.
 *
 * @author Paul Andrews
 */
public class Assertion {
	/**
	 * @param s A string that may be printed along with any other
	 *          error text.
	 * @param flag if true then an Error will be thrown, otherwise
	 *             nothing will happen.
	 * @throws Error if 'flag' is false.
	 */
	public static void test(String s, boolean flag)
    {
        if (flag)
        {
            System.out.println(s);
            throw new AssertionError();
        }
    }
	
	/**
	 * If the actual value is not equal to the expected value, throw an Error.
	 * @param s A string that may be printed along with any other
	 *          error text.
	 * @param expected the expected value.
	 * @param was the actual value.
	 * @throws Error if 'flag' is false.
	 */
	public static void testEQ(String s, int expected, int was)
    {
        test(s, expected != was);
    }
}
