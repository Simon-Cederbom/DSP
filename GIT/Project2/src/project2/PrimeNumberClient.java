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
		while (true) {
			System.out.println("Enter an integer to check if it is a prime number.");
			inputNumber = scanner.nextInt();
			String primeAwnser = target.path("checkPrime").path("" + inputNumber).request().accept(MediaType.TEXT_PLAIN)
					.get(String.class);
			if(primeAwnser == "-1") {
				System.out.println(inputNumber + " is not listed in server. Calculating on client.");
				primeAwnser = calculatePrime(inputNumber);
			}
			if(primeAwnser == "1") System.out.println(inputNumber + " is a prime number");
			else System.out.println(inputNumber + " is not a prime number");
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
