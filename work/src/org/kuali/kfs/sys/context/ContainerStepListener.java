package org.kuali.kfs.sys.context;

/**
 * Any class adding this listener class can broadcast when a step starts and finishes.
 * Any class implementing this listener can be notified when a step starts and finishes.
 */
public interface ContainerStepListener {

    /**
     * Notify the listener that the Step has started. 
     * 
     * @param runFile The Step's .run file descriptor
     * @param logFile The Step's log file created by its executor. The logFile is used to provide a unique identifier for this run of the Step
     */
    public void stepStarted(BatchStepFileDescriptor runFile, String logFile);
    
    /**
     * Notify the listener that the Step has finished.
     * 
     * @param resultFile the Step's .success or .error file descriptor
     * @param logFile The Step's log file created by its executor when the step started. The logFile is used to provide a unique identifier for this run of the Step
     */
    public void stepFinished(BatchStepFileDescriptor resultFile, String logFile);    
}
