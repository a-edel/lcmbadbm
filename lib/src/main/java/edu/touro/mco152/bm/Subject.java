package edu.touro.mco152.bm;

import edu.touro.mco152.bm.persist.DiskRun;

/**
 * The Subject interface represents a subject in the Observer Pattern.
 * Subjects are entities that are observed by one or more observers, and they notify observers
 * when they undergo a change in state or when specific events occur.
 */
public interface Subject {

    /**
     * Registers an observer with the subject.
     *
     * @param observer The observer to be registered.
     */
    void registerObserver(Observer observer);

    /**
     * Unregisters an observer from the subject.
     *
     * @param observer The observer to be unregistered.
     */
    void unregisterObserver(Observer observer);

    /**
     * Notifies all registered observers of a run event that occurred.
     *
     * @param run The DiskRun object representing the details of the run event that occurred.
     */
    void notifyObservers(DiskRun run);
}
