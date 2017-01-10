/*
 * (c) Robert Forsström, mtekkie.
 */
package deadif;

public class RuleParsingException extends Exception {

	private static final long serialVersionUID = 519716447761675936L;
	private String  message;
	
	public RuleParsingException(String message){
		this.message = message;
	}
	
	@Override
	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
	
	
}
