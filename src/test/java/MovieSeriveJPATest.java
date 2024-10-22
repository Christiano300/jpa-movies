import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import model.Actor;
import model.Movie;
import model.Studio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

public class MovieSeriveJPATest {

    private JPAService movieService;
    private EntityManagerFactory emf;

    @BeforeEach
    public void setUp() {
        this.emf = Persistence.createEntityManagerFactory("movies");
        movieService = new JPAService(emf);
    }

    @Test
    public void checkActorIsSaved() {
        var actor = movieService.save(Actor.builder()
                .firstName("Bruce")
                .lastName("Willis").build());
        movieService.save(actor);
        EntityManager em = emf.createEntityManager();
        assertThat(em.find(Actor.class, actor.getId())
                        .getFirstName()).isEqualTo("Bruce");
        em.close();
    }

    @Test
    public void checkMovieIsSaved() {
        var movie = movieService.save(Movie.builder()
                .title("Pulp Fiction")
                .releaseDate(LocalDate.of(1999,1,1))
                .build());
        movieService.save(movie);
        EntityManager em = emf.createEntityManager();
        assertThat(em.find(Movie.class, movie.getId())
                .getTitle()).isEqualTo("Pulp Fiction");
        em.close();
    }

    @Test
    public void checkMovieIsSavedWithActor() {
        var actor = Actor.builder()
                .firstName("Bruce")
                .lastName("Willis").build();
        var movie = movieService.save(Movie.builder()
                .title("Pulp Fiction")
                .releaseDate(LocalDate.of(1999,1,1))
                .build());
        movie.playsRole(actor);
        var savedMovie = movieService.save(movie);

        EntityManager em = emf.createEntityManager();
        assertThat(em.createQuery("""
                        SELECT a FROM Actor a
                        JOIN FETCH Movie m
                        WHERE m.id = :movieId 
                        """, Actor.class)
                .setParameter("movieId", savedMovie.getId())
                .getResultList().getFirst().getMovieSet())
                .contains(movie);
        em.close();
    }


    @Test
    public void checkSaveStudioSavesMovie() {

        var movie = Movie.builder()
                .title("Pulp Fiction")
                .releaseDate(LocalDate.of(1999,1,1))
                .build();
        var studio = Studio.builder()
                .name("Sony").build();
        studio.produced(movie);
        var newStudio = movieService.save(studio);

        EntityManager em = emf.createEntityManager();
        assertThat(em.createQuery("""
                        SELECT m FROM Movie m
                        JOIN FETCH Studio s
                        WHERE s.id = :studioId 
                        """, Movie.class)
                .setParameter("studioId", newStudio.getId())
                .getResultList().getFirst().getStudio())
                .isEqualTo(newStudio);
        em.close();
    }


    @Test
    public void checkFindAllMovies() {
        var actor = Actor.builder()
                .firstName("Bruce")
                .lastName("Willis").build();
        actor = movieService.save(actor);
        assertThat(movieService.findActorByName("Willis")).contains(actor);
    }


    @Test
    public void checkActorByName() {
        var movie = Movie.builder()
                .title("Pulp Fiction")
                .releaseDate(LocalDate.of(1999,5,8))
                .build();
        var movie2 = Movie.builder()
                .title("Matrix")
                .releaseDate(LocalDate.of(2000,4,7))
                .build();
        var movie3 = Movie.builder()
                .title("Fight Club")
                .releaseDate(LocalDate.of(1998,11,6))
                .build();
        List<Movie> saved = movieService.saveAll(movie,movie2, movie3);
        assertThat(movieService.releasedInYear(2000)).contains(
                saved.stream()
                        .filter(m -> m.getReleaseDate()
                        .isAfter(LocalDate.of(2000,1,1))
                        ).findFirst().orElse(null)
        );
    }


    public void checkFindByReleaseDate() {
        var movie = Movie.builder()
                .title("Pulp Fiction")
                .releaseDate(LocalDate.of(1999,5,8))
                .build();
        var movie2 = Movie.builder()
                .title("Matrix")
                .releaseDate(LocalDate.of(2000,4,7))
                .build();
        var movie3 = Movie.builder()
                .title("Fight Club")
                .releaseDate(LocalDate.of(1998,11,6))
                .build();
        List<Movie> saved = movieService.saveAll(movie,movie2, movie3);
        assertThat(movieService.releasedInYear(2000)).contains(
                saved.stream()
                        .filter(m -> m.getReleaseDate()
                                .isAfter(LocalDate.of(2000,1,1))
                        ).findFirst().orElse(null)
        );
    }

    @Test
    public void checkFindActorsWithMovies() {
        var bruce = Actor.builder()
                .firstName("Bruce")
                .lastName("Willis").build();
        var travolta = Actor.builder()
                .firstName("John")
                .lastName("Travolta").build();
        var pulpFiction = Movie.builder()
                .title("Pulp Fiction")
                .releaseDate(LocalDate.of(1999,1,1))
                .build();
        var dieHard = Movie.builder()
                .title("Die Hard")
                .releaseDate(LocalDate.of(1999,1,1))
                .build();

        bruce = movieService.save(bruce);
        travolta = movieService.save(travolta);

        pulpFiction.playsRole(bruce);
        pulpFiction.playsRole(travolta);
        dieHard.playsRole(bruce);

        movieService.save(dieHard);
        movieService.save(pulpFiction);

        assertThat(movieService.findByAtLeastNumberOfMovies(2)
                .size()).isEqualTo(1);

    }

    @Test
    public void checkFindActorsWithStudios() {

        var bruce = Actor.builder()
                .firstName("Bruce")
                .lastName("Willis").build();
        var travolta = Actor.builder()
                .firstName("John")
                .lastName("Travolta").build();
        var pulpFiction = Movie.builder()
                .title("Pulp Fiction")
                .releaseDate(LocalDate.of(1999,1,1))
                .build();
        var studio = Studio.builder().name("SONY").build();
        studio = movieService.save(studio);

        var dieHard = Movie.builder()
                .title("Die Hard")
                .studio(studio)
                .releaseDate(LocalDate.of(1999,1,1))
                .build();

        bruce = movieService.save(bruce);
        travolta = movieService.save(travolta);

        pulpFiction.playsRole(bruce);
        pulpFiction.playsRole(travolta);
        dieHard.playsRole(bruce);

        movieService.save(dieHard);
        movieService.save(pulpFiction);

        assertThat(movieService.findByStudio(studio)).contains(bruce);

    }

}
