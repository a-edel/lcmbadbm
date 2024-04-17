package edu.touro.mco152.bm.commands;

/**
 * The Command interface represents a command to be executed. Classes that implement
 * this interface encapsulate the details of a specific command and provide a method
 * to execute it.
 */
public interface Command {

    /**
     * Executes the command.
     *
     * @return true if the command is executed successfully, otherwise false
     * @throws Exception if an error occurs during command execution
     */
    Boolean execute() throws Exception;
}