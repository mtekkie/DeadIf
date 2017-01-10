/*
 * (c) Robert Forsström, mtekkie.
 */
package deadif;

import java.lang.management.ManagementFactory;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;

public class MbeanRule implements RuleInterface{
	private static final Logger LOGGER = Logger.getLogger( MbeanRule.class.getName() );

	private String objectName;
	private String attributeOrOperation;
	private String leftName;
	private String rightValue;
	private String comperator;
	private String id;

	private Object leftValueObj;



	@Override
	public void load(String id, String[] ruleArguments) throws RuleParsingException {

		LOGGER.log(Level.FINER, "loading rule with arguments: {0}", String.join("|", ruleArguments));
		this.id=id;
		// Check that we are ok.
		if (ruleArguments.length < 6 )
			throw new RuleParsingException("Number of arguments is less than 6, check syntax");

		
		//Sometimes there is a+ space in the ObjectName.
		// Need to ensure that we have everything from after mbean to before operation/attribute
		LOGGER.log(Level.FINE, "check that we have the whole ObjectName");
		LOGGER.log(Level.FINE, "find position of operation/attribute in arguments");
		
		int pos=0;
		for (int i=0; i < ruleArguments.length ; i++ ){
			String s = ruleArguments[i];
			if (s.equalsIgnoreCase("attribute") || s.equalsIgnoreCase("operation")){
				LOGGER.log(Level.FINE, "found position at {0}", i);
				pos=i;
			}
		}
		
		if (pos>2){
			int numberOfSpaces = pos-2;
			LOGGER.log(Level.FINE, "object name has spaces in it. number of spaces:{0}",numberOfSpaces);
			
			String objectName = "";
			
			for (int i = 1; i < pos; i++){
				objectName = objectName+" "+ruleArguments[i];
			}
			
			// mbean|WebSphere:type=Jvm Stats|attribute|UpTime|>|10000
			setObjectName(objectName);
			setAttributeOrOperation(ruleArguments[pos].toLowerCase());
			setLeftName(ruleArguments[pos+1]);
			setComperator(ruleArguments[pos+2]);
			setRightValue(ruleArguments[pos+3]);
			
		}else{
			// mbean|WebSphere:type=JvmStats|attribute|UpTime|>|10000
			setObjectName(ruleArguments[1]);
			setAttributeOrOperation(ruleArguments[2].toLowerCase());
			setLeftName(ruleArguments[3]);
			setComperator(ruleArguments[4]);
			setRightValue(ruleArguments[5]);	
			
		}
			
		
		




		LOGGER.log(Level.FINER, "checking if comperator is vaild, value: {0}", getComperator());
		if (!( getComperator().equals("=") ||
			   getComperator().equals("==") ||
			   getComperator().equals(">") ||
			   getComperator().equals("<") )){
				throw new RuleParsingException("Comperator is not one of the following: = == < >");
		}

		LOGGER.log(Level.FINER, "checking if operation or attribute is vaild, value: {0}", getAttributeOrOperation());
		if (!(getAttributeOrOperation().equals("operation") || getAttributeOrOperation().equals("attribute"))){
			throw new RuleParsingException("don't know if it is an operation or attribute, check syntax");
		}

		LOGGER.log(Level.FINER, "checking if comperator works togehter with rightValue : {0}", getRightValue());

		if (getComperator().equals(">") || getComperator().equals("<") ){
			if (!isNumber(getRightValue())){
				throw new RuleParsingException("comperator is < or > indicating that it will compare to a number. But the value that it is comparing to is not an integer.");
			}
		}


	}

	@Override
	public boolean isDead() throws RuleCheckException {
		LOGGER.log(Level.FINER, "Checking if {0} is complient", getObjectName());

		MBeanServer server = ManagementFactory.getPlatformMBeanServer();

		try {

			ObjectName objectName = new ObjectName (getObjectName());

			Object leftValueObj = new Object();

			if (getAttributeOrOperation().equals("attribute")){
				LOGGER.log(Level.FINER, "getting attribute {0}", getLeftName());
				leftValueObj = server.getAttribute(objectName, getLeftName());
			} else if (getAttributeOrOperation().equals("operation")){
				LOGGER.log(Level.FINER, "invoking operation {0}", getLeftName());
				leftValueObj = server.invoke(objectName, getLeftName(), null, null);
			}
			this.leftValueObj = leftValueObj;

			LOGGER.log(Level.FINER, "is result from getting attribute/invoking op an number? {0} that! ",isNumber(leftValueObj));

			if (getComperator().equals("<") || getComperator().equals(">")) {
				LOGGER.log(Level.FINER, "check if < > comps are fed a number ");

				if (!isNumber(leftValueObj))
					throw new RuleCheckException();
			}


			if (getComperator().equals("<") && isNumber(leftValueObj)) {
				if (getNumber(leftValueObj) < getNumber(getRightValue()))
					return true;
			}else if (getComperator().equals(">") && isNumber(leftValueObj)) {
				if (getNumber(leftValueObj) > getNumber(getRightValue()))
					return true;
			} else if (getComperator().equals("!=")) {
				if (!leftValueObj.equals(getRightValue()))
					return true;
			}else if (getComperator().equals("=") || getComperator().equals("==")) {
				if (leftValueObj.equals(getRightValue()))
					return true;
			}

		} catch (MalformedObjectNameException | AttributeNotFoundException | InstanceNotFoundException | MBeanException | ReflectionException | RuleCheckException  e) {
			 RuleCheckException rce = new RuleCheckException();
			 rce.setMessage(e.getMessage()+"-"+e.getCause());
			 throw rce;
		}


		return false;
	}

	@Override
	public StatusListItem getStatus() throws RuleCheckException {
		StatusListItem sli = new StatusListItem();
		sli.setId(getId());
		sli.setDeadAccordingToRule(isDead());
		sli.setMessage("Rule check: "+this.leftValueObj.toString()+" "+this.comperator+" "+this.rightValue);
		return sli;


	}


	private boolean isNumber(Object number){

		if (number instanceof Integer ||
			number instanceof Float ||
			number instanceof Double){
				return true;
		}

		try{
			new Integer(number.toString());
			return true;
		}catch (NumberFormatException e){
			return false;
		}
	}

	private int getNumber(Object number){

		if (number instanceof Integer ||
				number instanceof Float ||
				number instanceof Double){
					return (Integer) number;
			}
		return new Integer(number.toString());
	}

	public String getObjectName() {
		return objectName;
	}

	public void setObjectName(String objectName) {
		this.objectName = objectName;
	}

	public String getAttributeOrOperation() {
		return attributeOrOperation;
	}

	public void setAttributeOrOperation(String attributeOrOperation) {
		this.attributeOrOperation = attributeOrOperation;
	}


	public String getRightValue() {
		return rightValue;
	}

	public void setRightValue(String rightValue) {
		this.rightValue = rightValue;
	}

	public String getComperator() {
		return comperator;
	}

	public void setComperator(String comperator) {
		this.comperator = comperator;
	}

	public String getLeftName() {
		return leftName;
	}

	public void setLeftName(String leftName) {
		this.leftName = leftName;
	}


	@Override
	public String getId() {
		return id;
	}





}
