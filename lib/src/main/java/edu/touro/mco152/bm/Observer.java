package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * The Observer interface represents an observer in the Observer Pattern.
 * Observers are entities that are notified when a subject they are observing undergoes a change.
 */
public interface Observer {

    /**
     * This method is called by the subject to notify the observer of a change in the subject's state.
     *
     * @param run The DiskRun object representing the details of the disk run that triggered the update.
     */
    void update(DiskRun run);
}
