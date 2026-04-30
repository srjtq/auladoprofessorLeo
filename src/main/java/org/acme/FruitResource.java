package org.acme;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.ExampleObject;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.parameters.Parameter;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.resteasy.reactive.RestResponse;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@Path("/fruits")
public class FruitResource {

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Lista todas as frutas cadastradas.",
    description = "Retorna um array JSON com todas os registros cadastrados")
    @Tag(name = "Frutas")
    @APIResponse(responseCode = "200", description = "Frutas listadas com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Fruit.class)
            ))
    //@APIResponse(responseCode = "304", description = "Não houve alterações na aplicação desde a ultima requisição") // Se vc tiver cache, é um status code válido
    public List<Fruit> list(@QueryParam("page") int page){
        return Fruit.listAll(); // sempre ta procurando no banco
    }

    @GET
    @Path("{id}")
    @Produces(MediaType.APPLICATION_JSON)
    @Operation(summary = "Busca uma fruta por id", description = "Busca uma fruta por id")
    @Tag(name = "Frutas")
    @APIResponse(responseCode = "200", description = "Fruta encontrada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Fruit.class)
            ))
    @APIResponse(responseCode = "404", description = "Fruta não encontrada")
    public Response getFruitById(
            @Parameter(description = "ID da fruta", required = true, examples = {@ExampleObject(name = "ID da fruta", value = "1")})
            @PathParam("id") Long id){
        return  Fruit.findByIdOptional(id)
                .map(f -> Response.ok().entity(f).build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @APIResponse(responseCode = "201", description = "Fruta cadastrada com sucesso",
            content = @Content(
                    mediaType = "application/json",
                    schema = @Schema(implementation = Fruit.class)
            ))
    @APIResponse(responseCode = "400", description = "Requisição inválida")
    public Response add(Fruit fruit){
        Fruit.persist(fruit);
        return Response.created(URI.create("/fruits/"+fruit.id)).entity(fruit).build();
    }

    @DELETE
    @Transactional
    @APIResponse(responseCode = "404", description = "Fruta não encontrada")
    @APIResponse(responseCode = "204", description = "Fruta deletada com sucesso")
    public Response delete(Long id){
        Fruit.deleteById(id);
        return Response.noContent().build();
    }
}
