/*
 * Copyright 2012 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.BatchContainerDirectory;
import org.kuali.kfs.sys.context.BatchLogger;
import org.kuali.kfs.sys.context.BatchStepExecutor;
import org.kuali.kfs.sys.context.BatchStepFileDescriptor;
import org.kuali.kfs.sys.context.ContainerStepListener;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * BatchContainerStep looks for .run files.
 * When one is found it deletes the file and creates a new thread (BatchStepExecutor) which executes the Step indicated by the .run file.
 * BatchContainerStep continues looking for .run files until it finds a stopBatchContainerStep.run file.
 * When BatchContainerStep begins it writes a .runlock file to indicate that the batch container is running, but will first look for an existing .runlock file
 * and if one is found it will exit immediately.
 *
 * BatchContainerStep adds a ConsoleAppender to its Logger if one hasn't been configured.
 *
 */
public class BatchContainerStep extends AbstractStep implements ContainerStepListener {
    static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchContainerStep.class);

    protected String batchContainerDirectory;
    protected Step batchContainerStopStep;

    protected BatchContainerDirectory directory;

    protected StringBuffer containerResults;
    protected Map<String, BatchStepFileDescriptor> startedSteps;
    protected List<BatchStepFileDescriptor[]> completedSteps;

    /**
     * This method begins an infinite loop in order to process semaphore files written by BatchStepTrigger (called via the brte scripts).
     * BatchStepTrigger writes .run files, BatchContainerStep reads .run files and calls BatchStepExecutor (its own thread) which will execute the
     * Step and write either a .success or .error result file which is then read by BatchStepTrigger.
     *
     * This method exits gracefully when it receives a .run file for the batchContainerStopStep.
     *
     */
	@Override
    public boolean execute(String jobName, Date jobRunDate) throws InterruptedException {
	    BatchLogger.addConsoleAppender(LOG);

		LOG.info("Starting the batch container in Job: "+ jobName +" on "+ jobRunDate);

		if (batchContainerDirectory == null) {
			throw new RuntimeException("The batchContainerDirectory has not been specified.");
		}
		if (batchContainerStopStep == null) {
			throw new RuntimeException("The batchContainerStopStep has not been specified.");
		}

		directory = new BatchContainerDirectory(batchContainerDirectory);

		if (directory.isBatchContainerRunning()) {
			//an instance of the batch container is already running - exit w/out trying to remove the batch container semaphore file
			LOG.error("The BatchContainer is already running");
			throw new RuntimeException("The BatchContainer is already running.");
		}

		initContainerResults();

		try {
			//write batch container run lock file to indicate the batch container is running
			directory.writeBatchContainerSemaphore(jobName, getName());
			directory.addShutdownHook();
			LOG.info("The BatchContainer is running");

	        ParameterService parameterService = getParameterService();
	        DateTimeService dateTimeService = getDateTimeService();

			Executor executor = Executors.newCachedThreadPool();
	        while(true) {

	            if (LOG.isDebugEnabled()) {
	                LOG.debug("Looking for steps...");
	            }
	        	File[] stepRunFiles = directory.getStepRunFiles();

	        	while (stepRunFiles != null && stepRunFiles.length > 0) {
	        		LOG.info("Found "+ stepRunFiles.length +" steps to execute");

	        		for(File stepRunFile : stepRunFiles) {
	        			BatchStepFileDescriptor batchStepFile = new BatchStepFileDescriptor(stepRunFile);

	        			Step step = getStep(batchStepFile);
	        			if (step == null) {
	        				directory.removeBatchStepFileFromSystem(batchStepFile);
	        				directory.writeBatchStepErrorResultFile(batchStepFile, new IllegalArgumentException("Unable to find bean for step: "+ batchStepFile.getStepName()));
	        			}
	        			else {

	        				if (isStopBatchContainerTriggered(step)) {
	        					directory.removeBatchStepFileFromSystem(batchStepFile);
	        					directory.writeBatchStepSuccessfulResultFile(batchStepFile);

	        					//Stop BatchContainer
	        					LOG.info("shutting down container");
	        					return true;
	        				}

	        				//retrieve the stepIndex before the file is removed
	        				int stepIndex = directory.getStepIndexFromFile(batchStepFile);

	        				directory.removeBatchStepFileFromSystem(batchStepFile);

	        				if (LOG.isDebugEnabled()) {
	        				    LOG.debug("Creating new thread to run "+ batchStepFile);
	        				}
	        				BatchStepExecutor batchStepExecutor = new BatchStepExecutor(parameterService, dateTimeService, directory, batchStepFile, step, stepIndex);
                            batchStepExecutor.addContainerStepListener(this);
	        				executor.execute(batchStepExecutor);

	        			}
	        		}

	        		if (LOG.isDebugEnabled()) {
	        		    LOG.debug("Looking for steps...");
	        		}
	            	stepRunFiles = directory.getStepRunFiles();

	        	}

	        	sleep();
	    		if (!directory.isBatchContainerRunning()) {
	    			//the batch container's runlock file no longer exists - exit
	    			LOG.error("The BatchContainer runlock file no longer exists - exiting");
	    			return false;
	    		}
	        }

		} finally {
			//remove batch container run lock file
			directory.removeBatchContainerSemaphore();
			LOG.info("The BatchContainer has stopped running");

			logContainerResultsSummary();
		}
	}

	/**
	 * Notification that the Step started. Log the Step's information
	 */
	@Override
    public void stepStarted(BatchStepFileDescriptor runFile, String logFileName) {
	    logStepStarted(runFile, logFileName);
	}

	/**
	 * Notification that the Step finished. Log the Step's information
	 */
	@Override
    public void stepFinished(BatchStepFileDescriptor resultFile, String logFileName) {
	    logStepFinished(resultFile, logFileName);
	}

	/**
	 * Retrieves the Step bean from the SpringContext
	 *
	 * @param batchStepFile the file descriptor for the step to run
	 * @return the Step bean from the SpringContext
	 */
	protected Step getStep(BatchStepFileDescriptor batchStepFile) {
	  if (LOG.isDebugEnabled()) {
	      LOG.debug("Converting step named in .run file into a Step class...");
	  }

	  Step step = null;
	  try {
	      step = BatchSpringContext.getStep(batchStepFile.getStepName());
	  } catch (RuntimeException runtimeException) {
	      LOG.error("Failed to getStep from spring context: ", runtimeException);
	  }
      if (step == null) {
      	LOG.error("Unable to find bean for step: "+ batchStepFile.getStepName());
      	return null;
      }

      LOG.info("Found valid step: "+ step.getName());
      return step;
	}

	/**
	 * @param step the Step specified by the .run file
	 * @return true if the Step received is the step to stop the batch container, false otherwise
	 */
    protected boolean isStopBatchContainerTriggered(Step step) {
    	if (step.getName().equals(batchContainerStopStep.getName())) {
    		LOG.info("Received Step: "+ batchContainerStopStep.getName() +". Stop listening for steps.");
    		return true;
    	}
    	return false;
 	}

    /**
     * Sleep for a specified amount of time before looking for more semaphore files to process
     */
    protected void sleep() {
        try {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Sleeping...");
            }
            Thread.sleep(getSemaphoreProcessingInterval());
        }
        catch (InterruptedException e) {
            throw new RuntimeException("BatchContainerStep encountered interrupt exception while trying to wait for the specified semaphore processing interval", e);
        }
    }

    /**
     * @return (in milliseconds) the amount of time to wait before looking for more semaphore files to process
     */
	protected long getSemaphoreProcessingInterval() {
		return Long.parseLong(getParameterService().getParameterValueAsString(BatchContainerStep.class, KFSConstants.SystemGroupParameterNames.BATCH_CONTAINER_SEMAPHORE_PROCESSING_INTERVAL));
	}

	/**
	 * Initialize the structures necessary for logging the Steps' statistics
	 */
    protected void initContainerResults() {
        containerResults = new StringBuffer("Container Results:\n");
        startedSteps = new HashMap<String, BatchStepFileDescriptor>();
        completedSteps = new ArrayList<BatchStepFileDescriptor[]>();
    }

    /**
     * Log the notification that the Step started to an internal buffer. Add the descriptor to the list of started steps.
     * The logFileName is used as a unique identifier in the list of started steps in order to identify it in the list of started steps on completion.
     *
     * @param runFile the step's run file descriptor
     * @param logFileName the name of the log created by the Step's executor.
     */
    protected void logStepStarted(BatchStepFileDescriptor runFile, String logFileName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("stepStarted: "+ runFile);
        }

        startedSteps.put(logFileName, runFile);

        containerResults.append("STARTED "+ runFile
                +" "+ runFile.getStartedDate()
                +" LOGFILE="+ logFileName
                +"\n");
    }

    /**
     * Log the notification that the Step finished to an internal buffer. Remove the run descriptor from the list of started steps and add the run descriptor
     * and the result descriptor to the list of completed steps. The logFileName is used to locate the run descriptor from the list of started steps.
     *
     * @param resultFile the step's result file descriptor
     * @param logFileName the name of the log created by the Step's executor
     */
    protected void logStepFinished(BatchStepFileDescriptor resultFile, String logFileName) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("stepFinished: "+ resultFile);
        }

        BatchStepFileDescriptor runFile = startedSteps.remove(logFileName);

        containerResults.append("COMPLETED "+ resultFile
                                +" "+ resultFile.getCompletedDate()
                                +" LOGFILE="+ logFileName
                                +" STATUS="+ resultFile.getExtension()
                                +(resultFile.isStepFileAnErrorResultFile() ? " EXCEPTION:\n"+ directory.getExceptionFromFile(resultFile) : "")
                                +"\n");

        BatchStepFileDescriptor[] files = {runFile, resultFile};
        completedSteps.add(files);
    }

    /**
     * Print a summary of the steps that ran and the steps that haven't completed yet.
     */
	protected void logContainerResultsSummary() {
	    LOG.info("Printing container results...");

	    containerResults.append("\n\nCompleted Steps: \n");
	    if (completedSteps.isEmpty()) { containerResults.append("None"); }

	    for(BatchStepFileDescriptor[] batchStepFile : completedSteps) {
	        String status = batchStepFile[1].getExtension();
	        Date startedDate = batchStepFile[0].getStartedDate();
	        Date completedDate = batchStepFile[1].getCompletedDate();

            containerResults.append(batchStepFile[0] +"=" +status +"; S:"+ startedDate +" F:"+ completedDate +"\n");
	    }

	    containerResults.append("\n\nIncomplete Steps: \n");
	    if (startedSteps.isEmpty()) { containerResults.append("None"); }

	    for(Iterator<BatchStepFileDescriptor> iter = startedSteps.values().iterator(); iter.hasNext();) {
	        BatchStepFileDescriptor batchStepFile = iter.next();

            Date startedDate = batchStepFile.getStartedDate();

            containerResults.append(batchStepFile +"; S:"+ startedDate +"\n");
	    }

	    LOG.info(containerResults);
	}

	/**
	 * The path to the directory in which BatchContainerStep, BatchStepExecutor, and BatchStepTrigger will read and write the step semaphore files.
	 *
	 * @param batchContainerDirectory
	 */
	public void setBatchContainerDirectory(String batchContainerDirectory) {
		this.batchContainerDirectory = batchContainerDirectory;
	}

	/**
	 * The Step which indicates to the Batch Container to shut itself down.
	 *
	 * @param batchContainerStopStep
	 */
	public void setBatchContainerStopStep(Step batchContainerStopStep) {
		this.batchContainerStopStep = batchContainerStopStep;
	}

	/**
	 * @return the batchContainerStopStep
	 */
	public Step getBatchContainerStopStep() {
	    return this.batchContainerStopStep;
	}
}
