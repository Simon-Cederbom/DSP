package project2;

import java.util.ArrayList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

// @Path here defines class level path. Identifies the URI path that 
// a resource class will serve requests for.
@Path("primeCheck")
public class PrimeNumbersServer {
	
	private static ArrayList<Integer> primeNumbers = new ArrayList<Integer>();
	private static ArrayList<Integer> nonPrimeNumbers = new ArrayList<Integer>();

	
	
	@GET
	@Path("/checkPrime/{i}")
	@Produces(MediaType.TEXT_PLAIN)
	public String checkPrime(@PathParam("i") int i) {
		
		int checkPrime = i;
		
		if(primeNumbers.contains(i)) return "1";
		if(nonPrimeNumbers.contains(i)) return "0";
		
		return "-1";
	}

	/*@GET
	@Path("/age/{j}")
	@Produces(MediaType.TEXT_XML)
	public String userAge(@PathParam("j") int j) {

		int age = j;
		return "<User>" + "<Age>" + age + "</Age>" + "</User>";
	}*/
}