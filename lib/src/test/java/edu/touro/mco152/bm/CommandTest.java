package edu.touro.mco152.bm;

import edu.touro.mco152.bm.commands.Executor;
import edu.touro.mco152.bm.commands.ReadCommand;
import edu.touro.mco152.bm.commands.WriteCommand;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;
import edu.touro.mco152.bm.ui.MainFrame;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ReadCommand and WriteCommand.
 * Tests the execution of read and write commands.
 */
public class CommandTest {
    private UiInterface ui;
    private Executor executor;
    private int numOfMarks;
    private int numOfBlocks;
    private int blockSizeKb;
    private DiskRun.BlockSequence blockSequence;

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

    /**
     * Setup method to run before all tests.
     */
    @BeforeAll
    static void setupBeforeAll() {
        setupDefaultAsPerProperties();
    }

    /**
     * Setup method to run before each test.
     */
    @BeforeEach
    void setup() {
        ui = new UiInterfaceTest();
        executor = new Executor();
        numOfMarks = 25;
        numOfBlocks = 128;
        blockSizeKb = 2048;
        blockSequence = DiskRun.BlockSequence.SEQUENTIAL;
    }

    /**
     * Test method for write command execution.
     */
    @Test
    public void writeTest() throws Exception {
        WriteCommand writeCommand = new WriteCommand(ui, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
        assertTrue(executor.executeCommand(writeCommand));
    }

    /**
     * Test method for read command execution.
     */
    @Test
    public void readTest() throws Exception {
        ReadCommand readCommand = new ReadCommand(ui, numOfMarks, numOfBlocks, blockSizeKb, blockSequence, 0, 0);
        assertTrue(executor.executeCommand(readCommand));
    }
}
