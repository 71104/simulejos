package javax.bluetooth;

import java.io.IOException;

public class BluetoothStateException extends IOException {

	private static final long serialVersionUID = 1L;
	
	public BluetoothStateException() {
		super();
	}
	
	public BluetoothStateException(String msg) {
		super(msg);
	}
}