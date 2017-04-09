package net.aircommunity;

/**
 * The root exception for the AIR Platform.
 * 
 * @author Bin.Zhang
 */
public class AirException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	private final Code code;

	public AirException(Code code) {
		this.code = code;
	}

	public AirException(Code code, String message) {
		super(message);
		this.code = code;
	}

	public AirException(Code code, Throwable cause) {
		super(cause);
		this.code = code;
	}

	public AirException(Code code, String message, Throwable cause) {
		super(message, cause);
		this.code = code;
	}

	/**
	 * Return the code of the exception.
	 * 
	 * @return the code
	 */
	public Code getCode() {
		return code;
	}

}
