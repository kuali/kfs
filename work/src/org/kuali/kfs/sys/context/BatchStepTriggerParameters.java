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
package org.kuali.kfs.sys.context;

import org.apache.commons.lang.StringUtils;

/**
 * BatchStepTriggerParameters provides parsing and error checking for the arguments needed by the BatchStepTrigger.
 *
 *  stepName:                args[0]:  String- a comma delimited list of step names (no spaces)
 *  jobName:                 args[1]:  String- the name of the job in which the steps are being executed
 *  stepIndex:               args[2]:  String- the index of the step in the job
 *  batchContainerDirectory: args[3]:  String- the path to the directory in which the semaphore files are created.
 *                                             This value needs to match 'staging.directory.sys.batchContainer' in iu/build/configuration.properties
 *  sleepInterval:           args[4]:  String- the amount of time (in seconds) to wait before looking for the result file from BatchContainerStep
 */
public class BatchStepTriggerParameters {

	private String[] stepNames;
	private String jobName;
	private int stepIndex;
	private long sleepInterval;

	private BatchContainerDirectory batchContainerDirectory;

	/**
	 * @param args
	 *  stepName:                args[0]:  String- a comma delimited list of step names (no spaces)
	 *  jobName:                 args[1]:  String- the name of the job in which the steps are being executed
	 *  stepIndex:               args[2]:  String- the index of the step in the job
	 *  batchContainerDirectory: args[2]:  String- the path to the directory in which the semaphore files are created.
	 *                                             This value needs to match 'staging.directory.sys.batchContainer' in iu/build/configuration.properties
	 *  sleepInterval:           args[3]:  String- the amount of time (in seconds) to wait before looking for the result file from BatchContainerStep
	 */
	protected BatchStepTriggerParameters(String[] args) {
        if (args.length < 1) {
            System.err.println("ERROR: You must pass the name of the step to run on the command line.");
            System.exit(8);
        } else if (args.length < 2) {
        	System.err.println("ERROR: You must pass the name of the job to run on the command line.");
        	System.exit(8);
        } else if (args.length < 3) {
            System.err.println("ERROR: You must pass the index of the step in the job on the command line.");
            System.exit(8);
        } else if (args.length < 4) {
            System.err.println("ERROR: You must pass the path of the directory in which to write and read result files on the command line.");
            System.exit(8);
        } else if (args.length < 5) {
            System.err.println("ERROR: You must pass the amount of time (in seconds) to sleep while waiting for the step to run on the command line.");
            System.exit(8);
        }

        if (args[0].indexOf(",") > 0) {
            stepNames = StringUtils.split(args[0], ",");
        }
        else {
            stepNames = new String[] { args[0] };
        }

        jobName = args[1];
        stepIndex = Integer.parseInt(args[2]);
        String directory = args[3];
        sleepInterval = Long.parseLong(args[4]) * 1000;
        batchContainerDirectory = new BatchContainerDirectory(directory);
	}

	/**
	 * @return a comma delimited list of step names (no spaces)
	 */
	protected String[] getStepNames() {
		return stepNames;
	}

	/**
	 * @return the name of the job in which the step(s) are being executed
	 */
	protected String getJobName() {
		return jobName;
	}

	/**
	 * @return the index of the step in the job
	 */
	protected int getStepIndex() {
	    return stepIndex;
	}

	/**
	 * @return the amount of time to sleep (in seconds) while waiting for a result file from the BatchContainerStep
	 */
	protected long getSleepInterval() {
		return sleepInterval;
	}

	/**
	 * @return the directory in which the semaphore files are located
	 */
	protected BatchContainerDirectory getBatchContainerDirectory() {
		return batchContainerDirectory;
	}
}
