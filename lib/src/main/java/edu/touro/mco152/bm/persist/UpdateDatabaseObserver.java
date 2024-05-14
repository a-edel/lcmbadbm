package edu.touro.mco152.bm.persist;

import edu.touro.mco152.bm.Observer;
import jakarta.persistence.EntityManager;

/**
 * The UpdateDatabaseObserver class implements the Observer interface and defines behavior for observing disk run events
 * and updating a database with the relevant information.
 */
public class UpdateDatabaseObserver implements Observer {

    @Override
    public void update(DiskRun run) {
        /*
          Persist info about the BM Run (e.g. into Derby Database)
         */
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();
    }
}
