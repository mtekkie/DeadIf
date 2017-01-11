/*
 * (c) Robert Forsström, mtekkie.
 */


package deadif;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.*;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.Response;

@ApplicationPath("healthz")
@Path("/")
public class DeadIfRest extends Application{

	private static final Logger LOGGER = Logger.getLogger( DeadIfRest.class.getName() );

	private Rules rules;

	public DeadIfRest(){
		LOGGER.log(Level.FINE, "loading rules");
		rules = new Rules();
		rules.load();
	}

	@GET
	@Produces("application/json")
	public Response getStatus(){
		LOGGER.log(Level.FINE, "getting status");
		
		Status status = rules.getStatus();
		
		if (status.isDead()){
			LOGGER.log(Level.SEVERE, "deadif rule(-s) have been triggered. Application is deemed down. Reporting HTTP/503/ServiceUnavailable to probe.");
			return Response.status(Response.Status.SERVICE_UNAVAILABLE).entity(status).build();
		}
		
		return Response.ok(status).build();
	}


}
