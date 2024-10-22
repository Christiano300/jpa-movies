import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
import model.Actor;
import model.Movie;
import model.Studio;
import org.hibernate.cfg.NotYetImplementedException;
import persistence.ActorRepo;
import persistence.MovieRepo;
import persistence.StudioRepo;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class JPAService implements Service {

    private final EntityManagerFactory emf;

    public JPAService(EntityManagerFactory em) {
        this.emf = em;
    }

    @Override
    public Movie save(Movie movie) {
        var repo = new MovieRepo();
        return withEntityManager(em -> repo.save(movie, em));
    }

    @Override
    public Actor save(Actor actor) {
        var repo = new ActorRepo();
        return withEntityManager(em -> repo.save(actor, em));
    }

    @Override
    public Studio save(Studio studio) {
        var repo = new StudioRepo();
        return withEntityManager(em -> repo.save(studio, em));
    }

    @Override
    public List<Movie> releasedInYear(int year) {
        var repo = new MovieRepo();
        return repo.findInYear(year, emf.createEntityManager());
    }

    @Override
    public List<Movie> findAll() {
        throw new NotYetImplementedException();
    }

    @Override
    public List<Actor> findByAtLeastNumberOfMovies(int numberOfMovies) {
        var repo = new ActorRepo();
        var em = emf.createEntityManager();
        return repo.findWithNumberOfMovies(numberOfMovies, em);
    }

    public List<Actor> findActorByName(String lastName) {
        var repo = new ActorRepo();
        var em = emf.createEntityManager();
        return repo.findByName(lastName, em);
    }

    @Override
    public List<Actor> findByStudio(Studio studio) {
        var repo = new ActorRepo();
        return repo.findByStudio(studio, emf.createEntityManager());
    }

    public List<Movie> saveAll(Movie... movies) {
        var repo = new MovieRepo();
        return withEntityManager(em -> Arrays.stream(movies).map(movie -> repo.save(movie, em)).toList());
    }

    private <R> R withEntityManager(Function<EntityManager, R> function) {
        var entityManager = emf.createEntityManager();
        var transaction = entityManager.getTransaction();
        try {
            transaction.begin();
            var ret = function.apply(entityManager);
            transaction.commit();
            return ret;
        } catch (RuntimeException e) {
            if (transaction.isActive()) transaction.rollback();
            throw e;
        } finally {
            entityManager.close();
        }
    }
}
