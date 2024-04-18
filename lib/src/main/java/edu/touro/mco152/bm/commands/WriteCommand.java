package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.UiInterface;
import edu.touro.mco152.bm.persist.DiskRun;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.dataDir;
import static edu.touro.mco152.bm.App.testFile;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;

/**
 * Represents a command to perform a write operation. Implements the Command interface.
 */
public class WriteCommand extends IOCommand {
    private int unitsComplete;

    public WriteCommand(UiInterface ui, int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence)
    {
        super(ui, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
    }

    @Override
    protected void setUnitsCompleteSoFar(int unitsComplete)
    {
        this.unitsComplete = unitsComplete;
    }

    @Override
    public int getUnitsCompleteSoFar() {
        return unitsComplete;
    }

    @Override
    protected DiskRun createDiskRun(DiskRun.BlockSequence blockSequence)
    {
        return new DiskRun(DiskRun.IOMode.WRITE, blockSequence);
    }

    @Override
    protected File createSingleTestDataFileIfNecessary()
    {
        if (!App.multiFile)
            return testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        return null;
    }

    @Override
    protected DiskMark createDiskMark()
    {
        return new DiskMark(WRITE);
    }

    @Override
    protected String getTestFileMode()
    {
        String mode = "rw";
        if (App.writeSyncEnable) {
            mode = "rwd";
        }
        return mode;
    }

    @Override
    protected Boolean handleException(Exception ex)  {
        if (ex.getClass() == IOException.class) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    @Override
    protected void processRAccFile(RandomAccessFile rAccFile, byte[] blockArr, int blockSize) throws IOException {
        rAccFile.write(blockArr, 0, blockSize);
    }
}
