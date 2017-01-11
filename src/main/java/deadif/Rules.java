/*
 * (c) Robert Forsström, mtekkie.
 */
package deadif;

import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Rules {
	
	private static final Logger LOGGER = Logger.getLogger( Rules.class.getName() );
	
	private ArrayList <RuleInterface> rules;
	
	public void load(){
		LOGGER.log(Level.FINE, "In Rules.load()");
		rules = new ArrayList <RuleInterface>();
	    Map<String, String> env = System.getenv();
	    for (String envName : env.keySet()) {
	    	LOGGER.log(Level.FINEST, "procesing environment variable: {0}", envName);
	    	if (envName.length() > 5){
		    		if (envName.substring(0, 6).equals("deadif")){
		    			
		    	    	LOGGER.log(Level.FINE, "found rule: {0}", env.get(envName));
		    			String delims = "[ ]+";
		    			String arg = env.get(envName).replace("\"", "");
		    			String[] arguments = arg.split(delims);
		    			
		    			LOGGER.log(Level.FINE, "rule is of type {0}", arguments[0]);
		    			
		    			switch (arguments[0]){
		    				case "mbean":
		    					RuleInterface new_rule = new MbeanRule();
								try {
									new_rule.load(envName,arguments);
								} catch (RuleParsingException e) {
									LOGGER.log(Level.WARNING, 
											"deadif rule {0} is not valid - please check syntax - it will be dropped. Reason {1}", 
											new Object[]{envName, e.getMessage()} );
								}
		    					rules.add(new_rule);
		    					break;
		    			}
		    			
	    			
	    	}
		}
	       
	}
	}
	
	public Status getStatus(){
		ArrayList<StatusListItem> slis = new ArrayList<StatusListItem>();
		boolean dead = false;
		for (RuleInterface r : rules){
			try {
				LOGGER.log(Level.FINER, "getting status on {0}", r.getId());
				
				StatusListItem sli = r.getStatus();
				if (sli.isDeadAccordingToRule()){
					
					LOGGER.log(Level.WARNING, "deadif rule {0} has been triggered {1}", new Object []{ r.getId(), sli.getMessage()});
					dead=true;
				}
				
				slis.add(sli);
				
				LOGGER.log(Level.FINER, "done getting status on {0}", r.getId());
				
			} catch (RuleCheckException e) {
				LOGGER.log(Level.WARNING, "Could not execute rule {0} due to an exection error: {1}", new Object[]{r.getId(),e.getMessage()});
			}
		}
		
		Status status = new Status();
		status.setRuleResult(slis);
		status.setDead(dead);
		
		
		return status;
		
	}

	

}
