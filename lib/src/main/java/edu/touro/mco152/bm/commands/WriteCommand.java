package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.UiInterface;
import edu.touro.mco152.bm.persist.DiskRun;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.dataDir;
import static edu.touro.mco152.bm.App.testFile;
import static edu.touro.mco152.bm.DiskMark.MarkType.WRITE;

/**
 * Represents a command to perform a write operation. Implements the Command interface.
 */
public class WriteCommand extends IOCommand {

    public WriteCommand(UiInterface ui, int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence)
    {
        super(ui, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);

    }

    @Override
    public DiskRun createDiskRun(DiskRun.BlockSequence blockSequence)
    {
        return new DiskRun(DiskRun.IOMode.WRITE, blockSequence);
    }

    @Override
    public File createSingleTestDataFileIfNecessary()
    {
        if (!App.multiFile)
            return testFile = new File(dataDir.getAbsolutePath() + File.separator + "testdata.jdm");
        return null;
    }

    @Override
    public DiskMark createDiskMark()
    {
        return new DiskMark(WRITE);
    }

    @Override
    public String getTestFileMode()
    {
        String mode = "rw";
        if (App.writeSyncEnable) {
            mode = "rwd";
        }
        return mode;
    }

    @Override
    public Boolean handleException(Exception ex)  {
        if (ex.getClass() == IOException.class) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }
}
