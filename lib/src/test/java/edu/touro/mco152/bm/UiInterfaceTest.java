package edu.touro.mco152.bm;

import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;

import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is a non-Swing implementation of the UiInterface and provides testing for its methods.
 */
public class UiInterfaceTest implements UiInterface
{
    private int progress = 0;
    private DiskWorker diskWorker = new DiskWorker(this);


    /**
     * Bruteforce setup of static classes/fields to allow DiskWorker to run.
     */
    static void setupDefaultAsPerProperties()
    {
        // Do the minimum of what  App.init() would do to allow to run.
        Gui.mainFrame = new MainFrame();
        App.p = new Properties();
        App.loadConfig();

        Gui.progressBar = Gui.mainFrame.getProgressBar(); //must be set or get Nullptr

        // configure the embedded DB in .jDiskMark
        System.setProperty("derby.system.home", App.APP_CACHE_DIR);

        // code from startBenchmark
        //4. create data dir reference

        // may be null when tests not run in original proj dir, so use a default area
        if (App.locationDir == null) {
            App.locationDir = new File(System.getProperty("user.home"));
        }

        App.dataDir = new File(App.locationDir.getAbsolutePath()+File.separator+App.DATADIRNAME);

        //5. remove existing test data if exist
        if (App.dataDir.exists()) {
            if (App.dataDir.delete()) {
                App.msg("removed existing data dir");
            } else {
                App.msg("unable to remove existing data dir");
            }
        }
        else
        {
            App.dataDir.mkdirs(); // create data dir if not already present
        }
    }

    @BeforeAll
    static void setupBeforeAll() {
        setupDefaultAsPerProperties();
    }

    @Override
    public void setUiProgress(int progress) {
        this.progress = progress;
        assertTrue(progress >= 0 && progress <= 100);
    }

    @Override
    public void uiPublish(DiskMark dm) {
    }

    @Override
    public void startUi() {
        try {
            assertTrue(diskWorker.startBenchmarking());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void benchmarkTest()
    {
        assertEquals(0, progress);
        startUi();
        assertEquals(100, progress);
    }

    @Override
    public void uiShowReadAndWriteMessage() {
        System.out.println("""
                        For valid READ measurements please clear the disk cache by
                        using the included RAMMap.exe or flush-mem.exe utilities.
                        Removable drives can be disconnected and reconnected.
                        For system drives use the WRITE and READ operations
                        independently by doing a cold reboot after the WRITE. 
                        """);

    }

    // following methods not instantiated for basic ui
    @Override
    public void uiCancel(Boolean b) {

    }

    @Override
    public Boolean isUiCancelled() {
        return false;
    }

    @Override
    public void uiAddPropertyChangeListener(PropertyChangeListener listener) {

    }
}
