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

import java.io.File;
import java.util.Date;

import org.apache.commons.lang.StringUtils;

/**
 * BatchStepFileDescriptor contains the properties of a batch step file (name of the job, name of the step, and the file type extension).
 * The index of the step, the start time/date, and the completion time/date of the step can also be stored.
 *
 */
public class BatchStepFileDescriptor {

	private static final String STEP_FILE_SUFFIX_RUN = "run";
	private static final String STEP_FILE_SUFFIX_RESULT_SUCCESS = "success";
	private static final String STEP_FILE_SUFFIX_RESULT_ERROR = "error";

	public static final String STEP_FILE_EXTENSION_SEPARATOR = ".";
	public static final String STEP_FILE_NAME_SEPARATOR = "~";

	private String jobName;
	private String stepName;
	private String extension;

	private File stepFile;

	private Integer stepIndex;
	private Date startedDate;
	private Date completedDate;

	/**
	 * Create a descriptor using the properties provided. getStepFile() will return null when this constructor is used.
	 * This constructor is used to provide the details about a semaphore file to look for in the directory.
	 *
	 * @param jobName the name of the job in which the step is running
	 * @param stepName the name of the step to be executed
	 * @param extension the type of file to work with (RUN, SUCCESS, or ERROR)
	 */
	public BatchStepFileDescriptor(String jobName, String stepName, String extension) {
		this.jobName = jobName;
		this.stepName = stepName;
		this.extension = extension;
	}

	/**
	 * @param stepFile the semaphore file in the directory. The jobName, stepName, and extension are retrieved from the semaphore file name.
	 */
	public BatchStepFileDescriptor(File stepFile) {
		this.stepFile = stepFile;

		this.jobName = getJobNameFromFile(stepFile);
		this.stepName = getStepNameFromFile(stepFile);
		this.extension = getExtensionFromFile(stepFile);
	}

	/**
	 * @return the name of the job in which the step is running
	 */
	public String getJobName() {
		return jobName;
	}

	/**
	 * @return the name of the step being executed
	 */
	public String getStepName() {
		return stepName;
	}

	/**
	 * @return the extension of the semaphore file
	 */
	public String getExtension() {
		return extension;
	}

	/**
	 * @return the semaphore file in the directory
	 */
	public File getStepFile() {
		return stepFile;
	}

	/**
	 * @return the name of the file with the extension (jobName~stepName.extension)
	 */
	public String getName() {
		return jobName + STEP_FILE_NAME_SEPARATOR + stepName + STEP_FILE_EXTENSION_SEPARATOR + extension;
	}

	/**
	 * @return the name of the file without an extension (jobName~stepName)
	 */
	public String getNameNoExtension() {
		return jobName + STEP_FILE_NAME_SEPARATOR + stepName;
	}

	/**
	 * Return a representation of the step file descriptor. STEP and the stepIndex are only printed if the stepIndex is not null.
	 *
	 * @return [jobName] STEP[stepIndex]-[stepName]  or [jobName] [stepName]
	 */
    @Override
    public String toString() {
        return getJobName() +" "+ (stepIndex != null ? "STEP"+ stepIndex +"-":"") + getStepName();
    }

    /**
     * @return true if the semaphore file is an ERROR file, false otherwise
     */
    public boolean isStepFileAnErrorResultFile() {
    	return getName().endsWith(BatchStepFileDescriptor.getFileExtensionError());
    }

    /**
     * @return the index of the step in its job
     */
    public Integer getStepIndex() {
        return stepIndex;
    }

    /**
     * @param stepIndex the index of the step in its job
     */
    public void setStepIndex(Integer stepIndex) {
        this.stepIndex = stepIndex;
    }

    /**
     * @return the time the step started
     */
    public Date getStartedDate() {
        return startedDate;
    }

    /**
     * @param startedDate the time the step started
     */
    public void setStartedDate(Date startedDate) {
        this.startedDate = startedDate;
    }

    /**
     * @return the time the step completed
     */
    public Date getCompletedDate() {
        return completedDate;
    }

    /**
     * @param completedDate the time the step completed
     */
    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    /**
     * Retrieves the name of the job from the File
     *
     * @param runFile the semaphore file
     * @return the job name
     */
    private String getJobNameFromFile(File runFile) {
    	String runFileName = runFile.getName();
		int indexOfExtension = runFileName.lastIndexOf(STEP_FILE_EXTENSION_SEPARATOR);
		String fileNameNoExtension = StringUtils.substring(runFileName, 0, indexOfExtension);
		int indexOfStep = fileNameNoExtension.lastIndexOf(STEP_FILE_NAME_SEPARATOR);
		String jobName = StringUtils.substring(fileNameNoExtension, 0, indexOfStep);
		return jobName;
    }

    /**
     * Retrieves the name of the step from the File
     *
     * @param runFile the semaphore file
     * @return the step name
     */
    private String getStepNameFromFile(File runFile) {
    	String runFileName = runFile.getName();
		int indexOfExtension = runFileName.lastIndexOf(STEP_FILE_EXTENSION_SEPARATOR);
		String fileNameNoExtension = StringUtils.substring(runFileName, 0, indexOfExtension);
		int indexOfStep = fileNameNoExtension.lastIndexOf(STEP_FILE_NAME_SEPARATOR);
		String stepName = StringUtils.substring(fileNameNoExtension, indexOfStep+1);
		return stepName;
    }

    /**
     * Retrieves the extension from the File
     *
     * @param runFile the semaphore file
     * @return the extension
     */
    private String getExtensionFromFile(File runFile) {
    	String runFileName = runFile.getName();
		int indexOfExtension = runFileName.lastIndexOf(STEP_FILE_EXTENSION_SEPARATOR);
		String extension = StringUtils.substring(runFileName, indexOfExtension+1);
		return extension;
    }

    /**
     * @return the extension for the RUN file
     */
    public static String getFileExtensionRun() {
    	return BatchStepFileDescriptor.STEP_FILE_SUFFIX_RUN;
    }

    /**
     * @return the extension for the SUCCESS file
     */
    public static String getFileExtensionSuccess() {
    	return BatchStepFileDescriptor.STEP_FILE_SUFFIX_RESULT_SUCCESS;
    }

    /**
     * @return the extension for the ERROR file
     */
    public static String getFileExtensionError() {
    	return BatchStepFileDescriptor.STEP_FILE_SUFFIX_RESULT_ERROR;
    }
}
