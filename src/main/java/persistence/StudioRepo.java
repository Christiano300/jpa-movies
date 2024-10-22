package persistence;

import jakarta.persistence.EntityManager;
import model.Studio;

public class StudioRepo {

    public Studio save(Studio studio, EntityManager em) {
        return em.merge(studio);
    }
}
