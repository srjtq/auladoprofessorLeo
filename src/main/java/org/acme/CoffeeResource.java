package org.acme;

import io.smallrye.faulttolerance.api.RateLimit;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.faulttolerance.CircuitBreaker;
import org.eclipse.microprofile.faulttolerance.Fallback;
import org.eclipse.microprofile.faulttolerance.Retry;
import org.eclipse.microprofile.faulttolerance.Timeout;
import org.jboss.logging.Logger;
import org.jboss.resteasy.reactive.RestQuery;

import java.time.temporal.ChronoUnit;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Path("/coffee")
public class CoffeeResource {
    private static final Logger LOGGER = Logger.getLogger(CoffeeResource.class);
    private AtomicLong counter = new AtomicLong(0);

    @GET
    @RateLimit(value = 15, window = 10, windowUnit = ChronoUnit.SECONDS)
    @Fallback(fallbackMethod = "getAllFallback")
    @Retry(maxRetries = 4, delay = 100)
    public Response getAll(){
        final long invocationNumber = counter.getAndIncrement();
        maybeFail(String.format("CoffeeResource#coffees() invocation #%d failed", invocationNumber));

        LOGGER.infof("CoffeeResource#coffees() invocation #%d returning successfully", invocationNumber);

        return Response.ok(Coffee.listAll()).build();
    }

    public Response getAllFallback(){
        return Response.status(Response.Status.TOO_MANY_REQUESTS).build();
    }


    private void maybeFail(String failureLogMessage) {
        if (new Random().nextBoolean()) {
            LOGGER.error(failureLogMessage);
            throw new RuntimeException("Resource failure.");
        }
    }


    @GET
    @Path("/{id}/recommendations")
    @Timeout(250)
    @Fallback(fallbackMethod = "recommendationsFallback")
    public Response getRecommendations(@PathParam("id") Long id){
        long started = System.currentTimeMillis();
        final long invocationNumber = counter.getAndIncrement();

        try {
            randomDelay();
            LOGGER.infof("CoffeeResource#recommendations() invocation #%d returning successfully", invocationNumber);
            return Response.ok().entity(Coffee.listAll()).build();
        } catch (Exception e) {
            LOGGER.errorf("CoffeeResource#recommendations() invocation #%d timed out after %d ms",
                    invocationNumber, System.currentTimeMillis() - started);
            return Response.status(Response.Status.GATEWAY_TIMEOUT).build();
        }
    }

    public Response recommendationsFallback(Long id){
        LOGGER.info("Falling back to RecommendationResource#recommendationsFallback()");
        return Response.ok().entity(Collections.emptyList()).build();
    }

    private void randomDelay() throws InterruptedException{
        Thread.sleep(new Random().nextInt(500));
    }

    @GET
    @Path("/{id}/availability")
    @CircuitBreaker(requestVolumeThreshold = 4)
    public int getAvailability(@PathParam("id") Long id){
        final long invocationNumber = counter.getAndIncrement();
        maybeFailAvailability(String.format("CoffeeResource#availability() invocation #%d failed", invocationNumber));

        LOGGER.infof("CoffeeResource#availability() invocation #%d returning successfully", invocationNumber);

        return new Random().nextInt(10);
    }

    public void maybeFailAvailability(String failureLogMessage){
        // introduce some artificial failures
        final Long invocationNumber = counter.getAndIncrement();
        if (invocationNumber % 4 > 1) { // alternate 2 successful and 2 failing invocations
            throw new RuntimeException("Service failed.");
        }
    }
}
