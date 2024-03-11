package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.dataDir;

/**
 * This class provides a Swing implementation of the UiInterface by extending SwingWorker and leveraging its methods.
 */
public class SwingUi extends SwingWorker<Boolean, DiskMark> implements UiInterface{
    // Record any success or failure status returned from SwingWorker (might be us or super)
    private Boolean lastStatus = null;  // so far unknown
    private DiskWorker diskWorker;

    public SwingUi()
    {
        diskWorker = new DiskWorker(this);
    }

    @Override
    public void setUiProgress(int i) {
        setProgress(i);
    }

    @Override
    public void uiPublish(DiskMark dm) {
        publish(dm);
    }

    @Override
    public void uiCancel(Boolean b) {
        cancel(b);
    }

    @Override
    public void uiAddPropertyChangeListener(PropertyChangeListener listener) {
        addPropertyChangeListener(listener);
    }

    @Override
    public Boolean isUiCancelled() {
        return isCancelled();
    }

    @Override
    public void startUi() {
        execute();
    }

    @Override
    public void uiShowReadAndWriteMessage() {
        JOptionPane.showMessageDialog(Gui.mainFrame,
                """
                        For valid READ measurements please clear the disk cache by
                        using the included RAMMap.exe or flush-mem.exe utilities.
                        Removable drives can be disconnected and reconnected.
                        For system drives use the WRITE and READ operations\s
                        independently by doing a cold reboot after the WRITE""",
                "Clear Disk Cache Now", JOptionPane.PLAIN_MESSAGE);
    }

    @Override
    protected Boolean doInBackground() throws Exception {
        return diskWorker.startBenchmarking();
    }

    /**
     * Process a list of 'chunks' that have been processed, ie that our thread has previously
     * published to Swing. For my info, watch Professor Cohen's video -
     * Module_6_RefactorBadBM Swing_DiskWorker_Tutorial.mp4
     * @param markList a list of DiskMark objects reflecting some completed benchmarks
     */
    @Override
    protected void process(List<DiskMark> markList) {
        markList.stream().forEach((dm) -> {
            if (dm.type == DiskMark.MarkType.WRITE) {
                Gui.addWriteMark(dm);
            } else {
                Gui.addReadMark(dm);
            }
        });
    }

    @Override
    protected void done() {
        // Obtain final status, might from doInBackground ret value, or SwingWorker error
        try {
            lastStatus = super.get();   // record for future access
        } catch (Exception e) {
            Logger.getLogger(App.class.getName()).warning("Problem obtaining final status: " + e.getMessage());
        }

        if (App.autoRemoveData) {
            Util.deleteDirectory(dataDir);
        }
        App.state = App.State.IDLE_STATE;
        Gui.mainFrame.adjustSensitivity();
    }

    public Boolean getLastStatus()
    {
        return lastStatus;
    }
}

