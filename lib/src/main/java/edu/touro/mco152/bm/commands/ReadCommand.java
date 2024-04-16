package edu.touro.mco152.bm.commands;

import edu.touro.mco152.bm.App;
import edu.touro.mco152.bm.DiskMark;
import edu.touro.mco152.bm.UiInterface;
import edu.touro.mco152.bm.persist.DiskRun;
import edu.touro.mco152.bm.ui.Gui;

import javax.swing.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

import static edu.touro.mco152.bm.App.msg;
import static edu.touro.mco152.bm.DiskMark.MarkType.READ;

/**
 * Represents a command to perform a read operation. Implements the Command interface.
 */
public class ReadCommand extends IOCommand {

    public ReadCommand(UiInterface ui, int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence)
    {
        super(ui, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
    }

    @Override
    public DiskRun createDiskRun(DiskRun.BlockSequence blockSequence)
    {
        return new DiskRun(DiskRun.IOMode.READ, blockSequence);
    }

    @Override
    public File createSingleTestDataFileIfNecessary()
    {
        return null;
    }

    @Override
    public DiskMark createDiskMark()
    {
        return new DiskMark(READ);
    }

    @Override
    public String getTestFileMode()
    {
        return "r";
    }

    @Override
    public Boolean handleException(Exception ex) throws IOException {
        if (ex.getClass() == FileNotFoundException.class) {
            Logger.getLogger(App.class.getName()).log(Level.SEVERE, null, ex);
            String emsg = "May not have done Write Benchmarks, so no data available to read." +
                    ex.getMessage();
            JOptionPane.showMessageDialog(Gui.mainFrame, emsg, "Unable to READ", JOptionPane.ERROR_MESSAGE);
            msg(emsg);
            return false;
        }
        else if (ex.getClass() == IOException.class) {
            throw new IOException();
        }
        return true;
    }
}
