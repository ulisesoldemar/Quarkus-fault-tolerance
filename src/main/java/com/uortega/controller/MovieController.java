package com.uortega.controller;

import com.uortega.model.Movie;
import org.eclipse.microprofile.faulttolerance.*;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;
import java.util.logging.Logger;
import java.util.stream.Collectors;

@Path("/movies")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class MovieController {
    List<Movie> movieList = new ArrayList<Movie>();
    Logger LOGGER = Logger.getLogger("Demologger");

    @GET
    @Timeout(value = 5000L)
    @Retry(maxRetries = 4)
    @CircuitBreaker(delay = 15000L)
    @Bulkhead(value = 1)
    @Fallback(fallbackMethod = "getMovieFallbackList")
    public List<Movie> getMovies() {
        LOGGER.info("Ejecutando movie list");
        doFail();
        doWait();
        return this.movieList;
    }

    public List<Movie> getMovieFallbackList() {
        var movie = new Movie(-1L, "Default", "Default", 1970);
        return List.of(movie);
    }

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    @Path("/size")
    public Integer countMovies() {
        return this.movieList.size();
    }

    @POST
    public Response createMovie(Movie newMovie) {
        Optional<Movie> searchingMovie = this.movieList.stream()
                .filter(movie -> movie.getMovieId().equals(newMovie.getMovieId()))
                .findFirst();
        if (!searchingMovie.isPresent()) {
            movieList.add(newMovie);
            return Response.ok(movieList).build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    @PUT
    @Path("{id}/{title}")
    public Response updateMovie(
            @PathParam("id") Long id,
            @QueryParam("title") String title) {
        this.movieList = this.movieList.stream().map(
                movie -> {
                    if (movie.getMovieId().equals(id)) {
                        movie.setTitle(title);
                    }
                    return movie;
                }
        ).collect(Collectors.toList());
        return Response.ok(this.movieList).build();
    }

    @DELETE
    @Path("{id}")
    public Response deleteMovie(
            @PathParam("id") Long id) {
        Optional<Movie> movieToDelete = this.movieList.stream()
                .filter(movie -> movie.getMovieId().equals(id))
                .findFirst();
        boolean removed = false;
        if (movieToDelete.isPresent()) {
            removed = this.movieList.remove(movieToDelete.get());
        }
        if (removed) {
            return Response.noContent().build();
        }
        return Response.status(Response.Status.BAD_REQUEST).build();
    }

    private void doWait() {
        var random = new Random();
        try {
            LOGGER.warning("Delay de tiempo");
            Thread.sleep((random.nextInt(10) + 1) * 1000L);
        } catch (Exception e) {

        }
    }

    private void doFail() {
        var random = new Random();
        if (random.nextBoolean()) {
            LOGGER.warning("Se produce una falla");
            throw new RuntimeException("Fallo generado aleatoriamente");
        }
    }
}
