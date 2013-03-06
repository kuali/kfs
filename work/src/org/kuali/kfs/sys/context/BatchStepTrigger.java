/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.sys.context;


import java.util.Arrays;


/**
 * BatchStepTrigger writes .run files containing a job name and step name for BatchContainerStep to read. 
 * It loops and sleeps until either a .success or an .error file is written for the Step. Once a result file is found tt logs the results and exits. 
 * 
 * BatchStepTrigger also checks for BatchContainerStep's .runlock file. If it doesn't find one (indicating the batch container is not running) it exits.
 * BatchStepTrigger adds a ConsoleAppender to its Logger if one hasn't been configured.
 * 
 * Note that this class runs without starting the SpringContext. KFS Services and Beans are not available for use.
 *
 */
public class BatchStepTrigger {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchStepTrigger.class);
    
    private static BatchStepTriggerParameters batchStepTriggerParameters;
    
    /**
     * BatchStepTrigger is instantiated for each step in a brte script. 
     * The value used when exiting the system tells the brte script how to handle failures.
     * 0: okay, 4: the step returned false, 8: an exception occurred in the execution of the step or the trigger
     *
     * - verify that the batch container is running, exit with an 8 if not
     * - for each step
     *   - write a RUN semaphore file
     *   - wait and listen for a result file (either SUCCESS or ERROR)
     *   - if the result file is null the batch container is not running so remove the run file and exit with an 8
     *   - if the result file is an ERROR
     *     - if the file is EMPTY remove the result file and exit with a 4
     *     - otherwise log the error contained in the result file, remove the result file, and exit with an 8
     *   - otherwise remove the result file and exit with a 0  
     * 
     * @param args - refer to BatchStepTriggerParameters for details
     */
    public static void main(String[] args) {
        try {            
            Log4jConfigurer.configureLogging(false);
            
        	batchStepTriggerParameters = new BatchStepTriggerParameters(args);
        	
        	String[] stepNames = getStepNames();            
            String jobName = getJobName();            
            int stepIndex = getStepIndex();
            long sleepInterval = getSleepInterval();            
            BatchContainerDirectory batchContainerDirectory = getBatchContainerDirectory();
            
            LOG.info("Executing Job: " + jobName + ", STEP"+ stepIndex +", Step(s): " + Arrays.toString(stepNames));
            
			if (!batchContainerDirectory.isBatchContainerRunning()) {
				//an instance of the batch container is not running - exit. Exit status: 8
				LOG.error("The BatchContainer is not running - exiting without executing the steps: "+ Arrays.toString(stepNames));
	            LOG.info("Exit status: 8");
				System.exit(8);
			}
            
			//Need to humanize 'i'; the index 
            for (int i = (stepIndex-1); i < stepNames.length; i++) {
                String stepName = stepNames[i];
            	BatchStepFileDescriptor batchStepFile = new BatchStepFileDescriptor(jobName, stepName, BatchStepFileDescriptor.getFileExtensionRun());
            	
            	//write step start file
            	batchContainerDirectory.writeBatchStepRunFile(batchStepFile, i);

            	//wait for a result file from BatchContainer
            	BatchStepFileDescriptor resultFile = listenForResultFile(batchContainerDirectory, batchStepFile, sleepInterval);
            	
            	if (resultFile == null) {
            		//result file is null - something unexpected happened. Exit status: 8             		
            		batchContainerDirectory.removeBatchStepFileFromSystem(batchStepFile);            		
            		
            		LOG.error("No result files were returned- exiting without knowing whether the Step was executed");
                    LOG.info("Exit status: 8");
        			System.exit(8);
            	}
            	
            	if (resultFile.isStepFileAnErrorResultFile()) {            		
            		
            		if (batchContainerDirectory.isFileEmpty(resultFile)) {
                		//do not execute any more steps, but job should succeed. Exit status: 4                		
            			LOG.error(batchStepFile +" failed");
            			batchContainerDirectory.removeBatchStepFileFromSystem(resultFile);
            			
                        LOG.info("Exit status: 4");
                		System.exit(4);
            		}
            		else {
            			
                		//if file is not empty do not execute any more steps and fail job (write exception to log). Exit status: 8
            			LOG.error(batchStepFile +" failed with the following error message: ");
            			batchContainerDirectory.logFileContents(resultFile, LOG);
            			batchContainerDirectory.removeBatchStepFileFromSystem(resultFile);

                        LOG.info("Exit status: 8");
                    	System.exit(8);            		            			
            		}
            	}
            	
    			batchContainerDirectory.removeBatchStepFileFromSystem(resultFile);
                LOG.info("Exiting "+ batchStepFile);
                
            }
            
            //continue executing steps in the job. Exit status: 0
            LOG.info("Exit status: 0");
            
            System.exit(0);
        }
        catch (Throwable t) {
            System.err.println("ERROR: Exception caught: ");
            t.printStackTrace(System.err);
            LOG.error(t);
            
            System.exit(8);
        }
    }
    
    /**
     * Loop - look for a result file in the directory, if none is found then sleep. 
     * If the batch container is not running then write an error result file and return it.
     * 
     * @param batchContainerDirectory the directory in which the semaphore files are located
     * @param batchStepFile the step descriptor for the current step
     * @param sleepInterval the amount of time to sleep while waiting for a result file
     * @return the step descriptor of the result file
     */
    private static BatchStepFileDescriptor listenForResultFile(BatchContainerDirectory batchContainerDirectory, BatchStepFileDescriptor batchStepFile, long sleepInterval) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Waiting for result file for "+ batchStepFile);
        }
    	
    	while (true) {    		
	    	//look for a result file in file-system
	    	BatchStepFileDescriptor resultFile = batchContainerDirectory.getResultFile(batchStepFile);
	    	if (resultFile != null) {
    	        LOG.info("Found result file: "+ resultFile.getName());
	    		
	    		return resultFile;
	    	}
	    	
	    	if (batchContainerDirectory.isBatchContainerRunning()) {	    	
		    	sleep(sleepInterval);    		    		
	    	}
	    	else {
	    		//the batch container is not running - return an error file with an exception
	    		batchContainerDirectory.writeBatchStepErrorResultFile(batchStepFile, new RuntimeException("The BatchContainer is not running - exiting without knowing whether the Step executed"));
	    		
	    		resultFile = batchContainerDirectory.getResultFile(batchStepFile);
    			return resultFile;
	    	}
    	}     	
    }    
    
    /**
     * Sleep for the specified amount of time
     * 
     * @param sleepInterval the amount of time (in milliseconds) to wait before looking for a result file
     */
    private static void sleep(long sleepInterval) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Sleeping...");
        }
        try {
            Thread.sleep(sleepInterval);
        }
        catch (InterruptedException e) {
            throw new RuntimeException("BatchStepTrigger encountered interrupt exception while trying to wait for the specified batch step semaphore processing interval", e);
        }
    }
    
    /**
     * @return the names of the steps to be executed
     */
    private static String[] getStepNames() {
    	return batchStepTriggerParameters.getStepNames();
    }
    
    /**
     * @return the name of the job in which the steps are running
     */
    private static String getJobName() {
    	return batchStepTriggerParameters.getJobName();
    }
    
    /**
     * @return the index of the step in the job
     */
    private static int getStepIndex() {
        return batchStepTriggerParameters.getStepIndex();
    }
    
    /**
     * @return the amount of time to sleep (in milliseconds) while waiting for a result file
     */
    private static long getSleepInterval() {
    	return batchStepTriggerParameters.getSleepInterval();
    }
    
    /**
     * @return the directory in which the semaphore files are located
     */
    private static BatchContainerDirectory getBatchContainerDirectory() {
    	return batchStepTriggerParameters.getBatchContainerDirectory();
    }
}