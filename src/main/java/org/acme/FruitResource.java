package org.acme;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.Response;

import java.net.URI;
import java.util.List;

@Path("/fruits")
public class FruitResource {

    @GET
    public List<Fruit> list(){
        return Fruit.listAll();
    }

    @POST
    @Transactional
    public Response add(Fruit fruit){
        fruit.persist();
        return Response.created(URI.create("/fruits/"+fruit.id)).entity(fruit).build();
    }

    @DELETE
    @Transactional
    public Response delete(Long id){
        Fruit.deleteById(id);
        return Response.noContent().build();
    }
}
