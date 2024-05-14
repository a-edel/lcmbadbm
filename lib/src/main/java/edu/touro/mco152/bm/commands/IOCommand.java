package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.*;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static edu.touro.mco152.bm.App.*;

/**
 * Represents an io command to be performed. Classes that implement this interface
 * encapsulate the io command's details and provide a method to execute it.
 */
public abstract class IOCommand implements Command, Subject {
    private List<Observer> observers = new ArrayList<>();
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

        int unitsComplete = getUnitsCompleteSoFar();

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
                        processRAccFile(rAccFile, blockArr, blockSize);
                        totalBytesProcessedInMark += blockSize;
                        unitsComplete++;
                        float percentComplete = (float) unitsComplete / (float) unitsTotal * 100f;

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

        setUnitsCompleteSoFar(unitsComplete);

        notifyObservers(run);

        return true;
    }

    @Override
    public void registerObserver(Observer observer) {
        observers.add(observer);
    }

    @Override
    public void unregisterObserver(Observer observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(DiskRun run) {
        for (Observer observer : observers)
            observer.update(run);
    }

    /**
     * Processes the RandomAccessFile with the given blockArr and blockSize.
     *
     * @param rAccFile   the RandomAccessFile to process
     * @param blockArr   the byte array representing the block
     * @param blockSize  the size of the block
     * @throws IOException if an I/O error occurs
     */
    protected abstract void processRAccFile(RandomAccessFile rAccFile, byte[] blockArr, int blockSize) throws IOException;

    /**
     * Sets the total number of units completed so far.
     *
     * @param unitsComplete the total number of units completed so far
     */
    protected abstract void setUnitsCompleteSoFar(int unitsComplete);

    /**
     * Gets the total number of units completed so far, if any, from a previous command.
     *
     * @return The number of units completed so far.
     */
    public abstract int getUnitsCompleteSoFar();

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

