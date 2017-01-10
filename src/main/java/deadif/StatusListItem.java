/*
 * (c) Robert Forsström, mtekkie.
 */
package deadif;

public class StatusListItem {
	
	private boolean deadAccordingToRule;
	private String message;
	private String id;
	


	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isDeadAccordingToRule() {
		return deadAccordingToRule;
	}

	public void setDeadAccordingToRule(boolean deadAccordingToRule) {
		this.deadAccordingToRule = deadAccordingToRule;
	}
	
	

}
