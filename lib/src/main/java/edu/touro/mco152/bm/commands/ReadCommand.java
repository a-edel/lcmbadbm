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
    private int wUnitsComplete;
    private int rUnitsComplete;

    public ReadCommand(UiInterface ui, int numOfMarks, int numOfBlocks, int blockSizeKb, DiskRun.BlockSequence blockSequence, int wUnitsComplete, int rUnitsComplete)
    {
        super(ui, numOfMarks, numOfBlocks, blockSizeKb, blockSequence);
        this.wUnitsComplete = wUnitsComplete;
        this.rUnitsComplete = rUnitsComplete;
    }

    @Override
    protected void incrementProcessUnitsComplete() {
        rUnitsComplete++;
    }

    @Override
    protected int getWUnitsComplete() {
        return wUnitsComplete;
    }

    @Override
    protected int getRUnitsComplete() {
        return rUnitsComplete;
    }

    @Override
    protected DiskRun createDiskRun(DiskRun.BlockSequence blockSequence)
    {
        return new DiskRun(DiskRun.IOMode.READ, blockSequence);
    }

    @Override
    protected File createSingleTestDataFileIfNecessary()
    {
        return null;
    }

    @Override
    protected DiskMark createDiskMark()
    {
        return new DiskMark(READ);
    }

    @Override
    protected String getTestFileMode()
    {
        return "r";
    }

    @Override
    protected Boolean handleException(Exception ex) throws IOException {
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
