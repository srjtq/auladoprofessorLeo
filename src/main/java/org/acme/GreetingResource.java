package org.acme;

import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.jboss.resteasy.reactive.RestQuery;

import javax.print.attribute.standard.Media;
import java.util.List;
import java.util.stream.Collectors;

@Path("/hello")
public class GreetingResource {

    @Inject
    GreetingService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/greeting/{name}")
    public String greeting(String name){
        return service.greeting(name);
    }

    @GET
    @Transactional
    @Produces(MediaType.TEXT_PLAIN)
    public String hello(@RestQuery String name) {
        var gretting = new Greeting();
        gretting.name = name;
        gretting.persist();
        return "Hello " + name;
    }

    @GET
    @Path("names")
    @Produces(MediaType.TEXT_PLAIN)
    public String names(){
        List<Greeting> greetings = Greeting.listAll();
        var names = greetings.stream().map(g -> g.name)
                .collect(Collectors.joining(", "));
        return "I've said hello to this " + names;
    }
}
