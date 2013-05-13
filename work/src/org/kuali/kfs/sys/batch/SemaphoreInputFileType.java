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

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.BatchStepFileDescriptor;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;

/**
 * This class allows the batch process semaphore files to be uploaded. Only the .error files are currently configured for uploading.
 * To enable the other file types to be uploaded configure a spring bean for each type supplying the
 * directoryPath, the fileExtension, and fileTypeIdentifier properties
 */
public class SemaphoreInputFileType extends BatchInputFileTypeBase {

	private static Logger LOG = Logger.getLogger(SemaphoreInputFileType.class);

	private String fileTypeIdentifier;

	/**
	 * @param principalName - unused
	 * @param parsedFileContents List<String> in which the first line is the name of the Job and the Step (JobName~StepName)
	 * @param fileUserIdentifier is not used
	 * @return first line of the parsedFileContents which should be in the form jobName~stepName
	 *
	 * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(String, Object, String)
	 */
	@Override
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
		String fileName = "";

		if (!validate(parsedFileContents)) {
			fileName += "invalidJob"+ BatchStepFileDescriptor.STEP_FILE_NAME_SEPARATOR +"invalidStep";
		}
		else {
			List<String> content = getParsedFileContent(parsedFileContents);

			String firstLine = content.get(0);
			fileName = firstLine;
		}

		return fileName;
	}

	/**
	 * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
	 */
	@Override
    public String getFileTypeIdentifer() {
		return fileTypeIdentifier;
	}

	public void setFileTypeIdentifier(String fileTypeIdentifier) {
		this.fileTypeIdentifier = fileTypeIdentifier;
	}

	/**
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    @Override
    public String getTitleKey() {
    	return KFSKeyConstants.Semaphore.MESSAGE_BATCH_UPLOAD_TITLE_PREFIX +"."+ getFileExtension();
    }

	/**
	 * Returns a List<String> in which the first line is the Step and all subsequent lines are considered part of the error message.
	 *
	 * @see org.kuali.kfs.sys.batch.BatchInputFileType#parse(byte[])
	 */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
    	BufferedReader bufferedFileReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));
    	List<String> content = new ArrayList<String>();
    	try {
    		String fileLine = "";
    		while ((fileLine = bufferedFileReader.readLine()) != null) {
    			content.add(fileLine);
    		}
    	}catch (Exception e) {
    		LOG.error("An unexpected error occurred while parsing the file: ", e);
    		throw new ParseException("An unexpected error occurred while parsing the file: "+ e.getMessage(), e);
    	}

    	return content;
    }

    /**
     * Remove the done file from the file system if it exists. The done file is not used by the batch process.
     *
     * @param fileName the absolute path, file name, and extension of the file
     * @param parsedFileContents
     *
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#process(String, Object)
     */
	@Override
    public void process(String fileName, Object parsedFileContents) {
		//remove done file
		File doneFile = generateDoneFileObject(fileName);
		if (doneFile.exists()) {
			LOG.info("Removing the .done file for "+ fileName);
			if (!doneFile.delete()) {
		    	LOG.error("Unable to delete the .done file for "+ fileName);
		    	MessageMap errors = new MessageMap();
		    	errors.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Semaphore.ERROR_BATCH_UPLOAD_DELETE_DONE_FILE);
		    	GlobalVariables.mergeErrorMap(errors);
			}
		}
	}

	/**
	 * Validate that the first line of the file contains a job name and a valid Step (the bean id)
	 * in the format jobName.stepName
	 *
	 * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(Object)
	 */
	@Override
    public boolean validate(Object parsedFileContents) {
		List<String> content = getParsedFileContent(parsedFileContents);

		if (content.size() <= 0) {
	    	MessageMap errors = new MessageMap();
	    	errors.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Semaphore.ERROR_BATCH_UPLOAD_INVALID_STEP);
	    	GlobalVariables.mergeErrorMap(errors);
	    	return false;
		}

		String firstLine = content.get(0);
		String stepName = StringUtils.substring(firstLine, StringUtils.lastIndexOf(firstLine, BatchStepFileDescriptor.STEP_FILE_NAME_SEPARATOR)+1);

		Step step = BatchSpringContext.getStep(stepName);
		if (step == null) {
			LOG.error("Unable to find bean for step: "+ stepName);
			MessageMap errors = new MessageMap();
			errors.putError(KFSConstants.GLOBAL_ERRORS, KFSKeyConstants.Semaphore.ERROR_BATCH_UPLOAD_INVALID_STEP);
			GlobalVariables.mergeErrorMap(errors);
			return false;
		}

		return true;
	}

	/**
	 * The author principal name is not added to the name of the file or in the file contents
	 * @return an empty String
	 *
	 * @see org.kuali.kfs.sys.batch.BatchInputFileType#getAuthorPrincipalName(File)
	 */
	@Override
    public String getAuthorPrincipalName(File file) {
		return "";
	}

    /**
     * This method is responsible for creating a File object that represents the done file. The real file represented on disk may
     * not exist
     *
     * @param batchInputFileName
     * @return a File object representing the done file. The real file may not exist on disk, but the return value can be used to
     *         create that file.
     */
    private File generateDoneFileObject(String batchInputFileName) {
        String doneFileName = StringUtils.substringBeforeLast(batchInputFileName, ".") + ".done";
        File doneFile = new File(doneFileName);
        return doneFile;
    }

    @SuppressWarnings("unchecked")
    private List<String> getParsedFileContent(Object parsedFileContent) {
    	List<String> content = (List<String>)parsedFileContent;
    	return content;
    }
}
