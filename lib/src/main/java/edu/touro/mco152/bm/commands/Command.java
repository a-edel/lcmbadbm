package edu.touro.mco152.bm.commands;

/**
 * Represents a command to be performed, in this case either a 'write' or a 'read'. Classes that implement this interface
 * encapsulate the command's details and provide a method to execute it.
 */
public interface Command {

    /**
     * Executes the command.
     */
    void execute();
}

