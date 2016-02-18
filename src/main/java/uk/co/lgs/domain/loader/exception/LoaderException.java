package uk.co.lgs.domain.loader.exception;

public class LoaderException extends Exception{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1966104956853845825L;
	
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public LoaderException(String message) {
		super();
		this.message = message;
	}

	private String message;
}
