/*
 * (c) Robert Forsström, mtekkie.
 */


package deadif;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;

@ApplicationPath("healthz")
@Path("/")
public class DeadIfRest extends Application{

	private static final Logger LOGGER = Logger.getLogger( DeadIfRest.class.getName() );

	private Rules rules;

	public DeadIfRest(){
		rules = new Rules();
		rules.load();
	}

	@GET
	@Produces("application/json")
	public Status getStatus(){
		LOGGER.log(Level.INFO, "Getting Status");
		return rules.getStatus();
	}


}
