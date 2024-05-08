package edu.touro.mco152.bm;

import java.beans.PropertyChangeListener;

/**
 * The UiInterface interface defines methods for interacting with the user interface.
 */
public interface UiInterface {

    /**
     * Checks if the user interface is cancelled.
     *
     * @return true if the user interface is cancelled, false otherwise
     */
    Boolean isUiCancelled();

    /**
     * Sets the progress value on the user interface.
     *
     * @param progress the progress value to set
     */
    void setUiProgress(int progress);

    /**
     * Publishes a DiskMark.
     *
     * @param dm the DiskMark to publish
     */
    void uiPublish(DiskMark dm);

    /**
     * Cancels the user interface.
     *
     * @param cancelled true to cancel the user interface, false to keep it active
     */
    void uiCancel(Boolean cancelled);

    /**
     * Adds a property change listener.
     *
     * @param listener the property change listener to add
     */
    void uiAddPropertyChangeListener(PropertyChangeListener listener);

    /**
     * Starts the user interface.
     */
    void startUi();

    /**
     * Displays a message indicating both read and write operations are ongoing.
     */
    void uiShowReadAndWriteMessage();
}
