/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
