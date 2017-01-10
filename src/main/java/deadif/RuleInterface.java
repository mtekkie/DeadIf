/*
 * (c) Robert Forsström, mtekkie.
 */
package deadif;

public interface RuleInterface {

	public String getId ();
	public void load (String id, String ruleArguments[])  throws RuleParsingException;
	public boolean isDead() throws RuleCheckException;
	public StatusListItem getStatus() throws RuleCheckException;
	
}
