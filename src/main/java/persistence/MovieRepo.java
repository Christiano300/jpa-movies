package persistence;

import jakarta.persistence.EntityManager;
import model.Movie;

import java.util.List;

public class MovieRepo {
    public Movie save(Movie movie, EntityManager em) {
        return em.merge(movie);
    }

    public List<Movie> findInYear(int year, EntityManager em) {
        return em.createQuery("""
                SELECT m FROM Movie m WHERE YEAR(m.releaseDate) = :year""", Movie.class)
                .setParameter("year", year).getResultList();
    }
}
