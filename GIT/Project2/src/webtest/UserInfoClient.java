package webtest;

import java.net.URI;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class UserInfoClient {

    public static void main(String[] args) {
        ClientConfig config = new ClientConfig();

        Client client = ClientBuilder.newClient(config);

        WebTarget target = client.target(getBaseURI());

        String xmlAnswer1 =
                target.path("name").path("YourName").request().accept(MediaType.TEXT_XML).get(String.class);

        String xmlAnswer2 =
                target.path("age").path("20").request().accept(MediaType.TEXT_XML).get(String.class);

        System.out.println(xmlAnswer1);
        System.out.println(xmlAnswer2);
    }

    private static URI getBaseURI() {
        return UriBuilder.fromUri("http://localhost:8080/Project2/rest/UserInfoService").build();
    }
}