/*
 * (c) Robert Forsström, mtekkie.
 */
package deadif;

public class RuleCheckException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6020718378929090200L;
	
	private String message;
	
	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
