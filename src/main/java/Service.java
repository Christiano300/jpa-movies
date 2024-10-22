import model.Actor;
import model.Movie;
import model.Studio;

import java.util.List;

public interface Service {
    Movie save(Movie movie);
    Actor save(Actor actor);
    Studio save(Studio studio);
    List<Movie> saveAll(Movie ... movies);
    List<Movie> releasedInYear(int year);
    List<Movie> findAll();
    List<Actor> findByAtLeastNumberOfMovies(int numberOfMovies);
    List<Actor> findByStudio(Studio studio);
    List<Actor> findActorByName(String lastName);
}
