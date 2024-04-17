package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.UiInterface;
import edu.touro.mco152.bm.Util;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.persist.EM;
import edu.touro.mco152.bm.ui.Gui;
import jakarta.persistence.EntityManager;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import static edu.touro.mco152.bm.App.*;

/**
 * Represents a command to be performed. Classes that implement this interface
 * encapsulate the command's details and provide a method to execute it.
 */
public abstract class IOCommand implements Command {
    private UiInterface ui;
    private int numOfMarks;
    private int numOfBlocks;
    private int blockSizeKb;
    private DiskRun.BlockSequence blockSequence;

    /**
     * Constructs a WriteCommand object with the specified parameters.
     *
     * @param ui              the user interface
     * @param numOfMarks      the number of marks
     * @param numOfBlocks     the number of blocks
     * @param blockSizeKb     the block size in kilobytes
     * @param blockSequence   the block sequence
     */
    public IOCommand(UiInterface ui, int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence)
    {
        this.ui = ui;
        this.numOfMarks = numOfMarks;
        this.numOfBlocks = numOfBlocks;
        this.blockSizeKb = blockSizeKb;
        this.blockSequence = blockSequence;
    }

    @Override
    public Boolean execute() throws Exception
    {
        int wUnitsTotal = App.writeTest ? numOfBlocks * numOfMarks : 0;
        int rUnitsTotal = App.readTest ? numOfBlocks * numOfMarks : 0;
        int unitsTotal = wUnitsTotal + rUnitsTotal;
        float percentComplete;

        int blockSize = blockSizeKb * KILOBYTE;
        byte[] blockArr = new byte[blockSize];
        for (int b = 0; b < blockArr.length; b++) {
            if (b % 2 == 0) {
                blockArr[b] = (byte) 0xFF;
            }
        }

        DiskRun run = createDiskRun(blockSequence);
        run.setNumMarks(numOfMarks);
        run.setNumBlocks(numOfBlocks);
        run.setBlockSize(blockSizeKb);
        run.setTxSize(App.targetTxSizeKb());
        run.setDiskInfo(Util.getDiskInfo(dataDir));

        msg("disk info: (" + run.getDiskInfo() + ")");

        Gui.chartPanel.getChart().getTitle().setVisible(true);
        Gui.chartPanel.getChart().getTitle().setText(run.getDiskInfo());

        // Create a test data file using the default file system and config-specified location if necessary
        File testFile = createSingleTestDataFileIfNecessary();

        /*
          Begin an outer loop for specified duration (number of 'marks') of benchmark,
          that keeps writing data (in its own loop - for specified # of blocks). Each 'Mark' is timed
          and is reported to the GUI for display as each Mark completes.
         */
        int startFileNum = App.nextMarkNumber;
        for (int m = startFileNum; m < startFileNum + numOfMarks && !ui.isUiCancelled(); m++) {

            if (App.multiFile) {
                testFile = new File(dataDir.getAbsolutePath()
                        + File.separator + "testdata" + m + ".jdm");
            }
            DiskMark mark = createDiskMark();
            mark.setMarkNum(m);
            long startTime = System.nanoTime();
            long totalBytesProcessedInMark = 0;

            String mode = getTestFileMode();

            try {
                try (RandomAccessFile rAccFile = new RandomAccessFile(testFile, mode)) {
                    for (int b = 0; b < numOfBlocks; b++) {
                        if (blockSequence == DiskRun.BlockSequence.RANDOM) {
                            int rLoc = Util.randInt(0, numOfBlocks - 1);
                            rAccFile.seek((long) rLoc * blockSize);
                        } else {
                            rAccFile.seek((long) b * blockSize);
                        }
                        rAccFile.readFully(blockArr, 0, blockSize);
                        totalBytesProcessedInMark += blockSize;
                        incrementProcessUnitsComplete();
                        int unitsComplete = getWUnitsComplete() + getRUnitsComplete();
                        percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;

                        /*
                          Report to GUI what percentage level of Entire BM (#Marks * #Blocks) is done.
                         */
                        ui.setUiProgress((int) percentComplete);
                    }
                }
            }
            catch (Exception ex) {
                return handleException(ex);
            }

            /*
              Compute duration, throughput of this Mark's step of BM
             */
            long endTime = System.nanoTime();
            long elapsedTimeNs = endTime - startTime;
            double sec = (double) elapsedTimeNs / (double) 1000000000;
            double mbRead = (double) totalBytesProcessedInMark / (double) MEGABYTE;
            mark.setBwMbSec(mbRead / sec);
            msg("m:" + m + " " + run.getIoMode() + " IO is " + mark.getBwMbSec() + " MB/s    "
                    + "(MBread " + mbRead + " in " + sec + " sec)");
            App.updateMetrics(mark);

            /*
              Let the GUI know the interim result described by the current Mark
             */
            ui.uiPublish(mark);

            // Keep track of statistics to be displayed and persisted after all Marks are done.
            run.setRunMax(mark.getCumMax());
            run.setRunMin(mark.getCumMin());
            run.setRunAvg(mark.getCumAvg());
            run.setEndTime(new Date());
        }

        /*
          Persist info about the Read BM Run (e.g. into Derby Database) and add it to a GUI panel
         */
        EntityManager em = EM.getEntityManager();
        em.getTransaction().begin();
        em.persist(run);
        em.getTransaction().commit();

        Gui.runPanel.addRun(run);

        return true;
    }

    /**
     * Increments the count of io process units completed.
     */
    protected abstract void incrementProcessUnitsComplete();

    /**
     * Gets the total number of read units completed.
     *
     * @return The number of read units completed.
     */
    protected abstract int getRUnitsComplete();

    /**
     * Gets the total number of write units completed.
     *
     * @return The number of write units completed.
     */
    protected abstract int getWUnitsComplete();

    /**
     * Handles exceptions that occur during command execution.
     *
     * @param ex the exception to handle
     * @return true if the exception is handled successfully, otherwise false
     * @throws IOException if an I/O error occurs
     */
    protected abstract Boolean handleException(Exception ex) throws IOException;

    /**
     * Gets the test file mode.
     *
     * @return the test file mode
     */
    protected abstract String getTestFileMode();

    /**
     * Creates a DiskMark object.
     *
     * @return a DiskMark object
     */
    protected abstract DiskMark createDiskMark();

    /**
     * Creates a DiskRun object with the specified block sequence.
     *
     * @param blockSequence the block sequence
     * @return a DiskRun object
     */
    protected abstract DiskRun createDiskRun(DiskRun.BlockSequence blockSequence);

    /**
     * Creates a single test data file if necessary.
     *
     * @return the created test data file
     */
    protected abstract File createSingleTestDataFileIfNecessary();
}

