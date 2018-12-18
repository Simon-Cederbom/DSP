package project2;

import java.net.URI;
import java.util.Scanner;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class PrimeNumberClient {

	public static void main(String[] args) {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);
		WebTarget target = client.target(getBaseURI());
		
		Scanner scanner = new Scanner(System.in);
		int inputNumber;
		boolean unListed = false;

		while (true) {
			unListed = false;
			System.out.println("Enter an integer to check if it is a prime number.");
			inputNumber = scanner.nextInt();
			String primeAwnser = target.path("checkPrime").path("" + inputNumber).request().accept(MediaType.TEXT_PLAIN)
					.get(String.class);
			if (primeAwnser.equals("-1")) {
				unListed = true;
				System.out.println(inputNumber + " is not listed in server. Calculating on client.");
				primeAwnser = calculatePrime(inputNumber);
			}
			if (primeAwnser.equals("1")) {
				System.out.println(inputNumber + " is a prime number");
				if (unListed) {
					target.path("addPrime").path("" + inputNumber).request().accept(MediaType.TEXT_PLAIN)
							.get(String.class);
				}
			} else {
				System.out.println(inputNumber + " is not a prime number");
				if (unListed) {
					target.path("addNonPrime").path("" + inputNumber).request().accept(MediaType.TEXT_PLAIN)
							.get(String.class);
				}
			}
		}
	}

	private static URI getBaseURI() {
		return UriBuilder.fromUri("http://localhost:8080/Project2/rest/primeCheck").build();
	}

	private static String calculatePrime(int num) {

		if (num == 0 || num == 1)
			return "0";
		for (int i = 2; i <= num / 2; i++) {
			if (num % i == 0)
				return "0";
		}
		return "1";
	}

}
