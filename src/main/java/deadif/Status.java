/*
 * (c) Robert Forsström, mtekkie.
 */
package deadif;

import java.util.ArrayList;

public class Status {

	private ArrayList<StatusListItem> ruleResult;
	private boolean dead;
	
	public ArrayList<StatusListItem> getRuleResult() {
		return ruleResult;
	}
	public void setRuleResult(ArrayList<StatusListItem> ruleResult) {
		this.ruleResult = ruleResult;
	}
	public boolean isDead() {
		return dead;
	}
	public void setDead(boolean dead) {
		this.dead = dead;
	}
	
	
	



}