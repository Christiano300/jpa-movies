package persistence;

import jakarta.persistence.EntityManager;
import model.Actor;
import model.Studio;

import java.util.List;

public class ActorRepo {

    public Actor save(Actor actor, EntityManager em) {
        return em.merge(actor);
    }

    public List<Actor> findByStudio(Studio studio, EntityManager entityManager) {
        return entityManager.createQuery("""
                SELECT a FROM Actor a JOIN a.movieSet m WHERE m.studio.id = :studio_id
            """, Actor.class).setParameter("studio_id", studio.getId()).getResultList();
    }

    public List<Actor> findWithNumberOfMovies(int numberOfMovies, EntityManager em) {
        return em.createQuery("""
                SELECT a FROM Actor a WHERE SIZE(a.movieSet) >= :numberOfMovies
                """, Actor.class).setParameter("numberOfMovies", numberOfMovies).getResultList();
    }

    public List<Actor> findByName(String lastName, EntityManager em) {
        return em.createQuery("""
                SELECT a FROM Actor a WHERE a.lastName = :lastName
                """, Actor.class).setParameter("lastName", lastName).getResultList();
    }
}
