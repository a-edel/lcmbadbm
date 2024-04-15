package edu.touro.mco152.bm;

import java.beans.PropertyChangeListener;

public interface UiInterface {

    Boolean isUiCancelled();

    void setUiProgress(int i);

    void uiPublish(DiskMark dm);

    void uiCancel(Boolean b);

    void uiAddPropertyChangeListener(PropertyChangeListener listener);

    void startUi();

    void uiShowReadAndWriteMessage();
}
