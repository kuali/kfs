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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * BatchContainerDirectory knows the path to the directory for the BatchContainerStep and BatchStepTrigger semaphore files.
 * It also handles much of the logic for writing, reading, and removing those files.
 *
 * BatchContainerDirectory adds a ConsoleAppender to its Logger if one hasn't been configured.
 *
 */
public class BatchContainerDirectory {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BatchContainerDirectory.class);

	private static final String BATCH_CONTAINER_SEMAPHORE_EXTENSION = "runlock";

	private File directory;

	/**
	 * Creates a reference for the path to the batch container. Verifies that the directory exists and throws an error if it does not.
	 *
	 * @param batchContainerPath the path to the directory that contains the semaphore files
	 */
	public BatchContainerDirectory(String batchContainerPath) {
		directory = new File(batchContainerPath);

		if(!directory.exists()) {
		    throw new RuntimeException(batchContainerPath + " does not exist. Please verify and run again");
		}
		else {
			LOG.info("batchContainerDirectory=" + batchContainerPath);
		}
	}

	/**
	 * Writes the semaphore file which indicates that the batch container is running
	 *
	 * @param jobName the name of the job that is starting the batch container
	 * @param stepName the name of the step which starts the batch container
	 */
	public void writeBatchContainerSemaphore(String jobName, String stepName) {
		if (!isExistsBatchContainerSemaphore()) {
			BatchStepFileDescriptor batchStepFile = new BatchStepFileDescriptor(jobName, stepName, BATCH_CONTAINER_SEMAPHORE_EXTENSION);
			writeBatchStepFileToSystem(batchStepFile, null);
		}
	}

	/**
	 * Removes the semaphore file which indicates that the batch container is running
	 */
	public void removeBatchContainerSemaphore() {
		FilenameFilter semaphoreExtensionFilter = new FileExtensionFileFilter(BATCH_CONTAINER_SEMAPHORE_EXTENSION);
		File[] filteredFiles = directory.listFiles(semaphoreExtensionFilter);

		for(File semaphore: filteredFiles) {
			semaphore.delete();
		}
	}

	/**
	 * Checks whether the batch container is currently running (by checking for the presence of the batch container semaphore file)
	 *
	 * @return true if the batch container is running (if the semaphore file exists), false otherwise
	 */
	public boolean isBatchContainerRunning() {
		return isExistsBatchContainerSemaphore();
	}

	/**
	 * @return true if the batch container semaphore file exists in the batch container, false otherwise
	 */
	private boolean isExistsBatchContainerSemaphore() {
		FilenameFilter semaphoreExtensionFilter = new FileExtensionFileFilter(BATCH_CONTAINER_SEMAPHORE_EXTENSION);
		File[] filteredFiles = directory.listFiles(semaphoreExtensionFilter);

		if (filteredFiles != null && filteredFiles.length > 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/**
	 * Returns a list of files in the batch container with the run extension
	 *
	 * @return a list of step .run files
	 */
	public File[] getStepRunFiles() {
		// Find all the step .run files for all the jobs.
		FilenameFilter stepStartFilter = new FileExtensionFileFilter(BatchStepFileDescriptor.getFileExtensionRun());
		File[] stepStartFiles = directory.listFiles(stepStartFilter);

		// No .run files found
		if (stepStartFiles == null || stepStartFiles.length == 0) {
			return null;
		}

		return stepStartFiles;
	}

	/**
	 * Writes the run file for the Step specified in the descriptor
	 *
	 * @param batchStepFile the step for which a .run file will be written
	 * @param stepIndex the index of the step in the job
	 */
    public void writeBatchStepRunFile(BatchStepFileDescriptor batchStepFile, int stepIndex) {
    	BatchStepFileDescriptor runFile = getCopyWithNewExtension(batchStepFile, BatchStepFileDescriptor.getFileExtensionRun());

    	// If successFile already exists then container will think the Step completed before it actually did
    	File successFile = new File(getDirectoryPath() + runFile.getNameNoExtension() + BatchStepFileDescriptor.STEP_FILE_EXTENSION_SEPARATOR + BatchStepFileDescriptor.getFileExtensionSuccess());
    	if (successFile.exists()) {
    	    throw new RuntimeException(successFile.getName() + " shouldn't exist yet before Step is run");
    	}

    	writeBatchStepFileToSystem(runFile, new Integer(stepIndex));
    }

    /**
     * Writes the success file for the Step specified in the descriptor
     *
     * @param batchStepFile the step for which a .success file will be written
     */
    public void writeBatchStepSuccessfulResultFile(BatchStepFileDescriptor batchStepFile) {
    	BatchStepFileDescriptor successFile = getCopyWithNewExtension(batchStepFile, BatchStepFileDescriptor.getFileExtensionSuccess());
    	writeBatchStepFileToSystem(successFile, null);
    }

    /**
     * Writes the error file for the Step specified in the descriptor
     *
     * @param batchStepFile the step for which a .error file will be written
     */
    public void writeBatchStepErrorResultFile(BatchStepFileDescriptor batchStepFile) {
    	BatchStepFileDescriptor errorFile = getCopyWithNewExtension(batchStepFile, BatchStepFileDescriptor.getFileExtensionError());
    	writeBatchStepFileToSystem(errorFile, null);
    }


    /**
     * Writes the error file for the Step specified in the descriptor. The stack trace in the Throwable will be written to the file.
     *
     * @param batchStepFile the step for which a .error file will be written
     * @param error the error to write in the error file
     */
    public void writeBatchStepErrorResultFile(BatchStepFileDescriptor batchStepFile, Throwable error) {
    	BatchStepFileDescriptor errorFile = getCopyWithNewExtension(batchStepFile, BatchStepFileDescriptor.getFileExtensionError());
    	writeBatchStepFileToSystem(errorFile, error);
    }

    /**
     * Returns a copy of the descriptor passed into the method, but with the extension specified.
     *
     * @param batchStepFile the Step for which a new descriptor is needed
     * @param extension the extension to use for the new descriptor
     * @return a copy of the descriptor passed in, but with the specified extension
     */
	private BatchStepFileDescriptor getCopyWithNewExtension(BatchStepFileDescriptor batchStepFile, String extension) {
		return new BatchStepFileDescriptor(batchStepFile.getJobName(), batchStepFile.getStepName(), extension);
	}

	/**
	 * Writes a batch step semaphore file to the directory; Writes the details to the file.
	 * Throws a RuntimeException if the file already exists.
	 *
	 * @param batchStepFile the step for which a semaphore file will be written
	 * @param details additional details to add to the semaphore file
	 */
    private void writeBatchStepFileToSystem(BatchStepFileDescriptor batchStepFile, Object details) {
    	LOG.debug("Writing "+ batchStepFile.getExtension() +" file for "+ batchStepFile);

        String fileName = getDirectoryPath() + batchStepFile.getName();
        File file = new File(fileName);
        if (!file.exists()) {
            try {
            	LOG.info("Creating new "+ batchStepFile.getExtension() +" file: "+ file.getName());
                file.createNewFile();

                //if further details exist write them to the file
                if (details != null) {
                    if (details instanceof Throwable) {
                        writeErrorMessageToFile(file, (Throwable)details);
                    }
                    else if (details instanceof Integer) {
                        writeStepIndexToFile(file, (Integer)details);
                    }

                }

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else {
        	throw new RuntimeException("Step "+ batchStepFile.getExtension() +" file: "+ fileName +" already exists");
        }
    }

    /**
     * Removes the semaphore file for the specified step
     *
     * @param batchStepFile the step for which the semaphore file should be removed
     */
    public void removeBatchStepFileFromSystem(BatchStepFileDescriptor batchStepFile) {
    	LOG.info("Removing "+ batchStepFile.getExtension() +" file for "+ batchStepFile);

        String fileName = getDirectoryPath() + batchStepFile.getName();
        File file = new File(fileName);
        if (file != null && file.exists()) {
        	boolean successfulDelete = file.delete();
        	if (!successfulDelete) {
        		LOG.error("Failed to delete "+ fileName +" from the system.");
        	} else if (file.exists()) {
                // Some file systems use caching which can cause odd container behavior particularly
                // if SEMAPHORE_PROCESSING_INTERVAL is set low. This statement could be enhanced to
                // loop until the file disappears
                LOG.warn("Deleted "+ fileName +" from the system but file still exists. File system slow or cached?");
            }
        }
        else {
        	//don't need to throw an exception if the file doesn't exist- but note it in the logs
        	LOG.warn("Step "+ batchStepFile.getExtension() +" file: "+ fileName +" doesn't exist");
        }
    }

    /**
     * Returns the result file (either a success file  or an error file) if one exists for the specified step. Otherwise null is returned.
     *
     * @param batchStepFile the step for which a result file is requested
     * @return the descriptor of the result file if one exists, null otherwise
     */
    public BatchStepFileDescriptor getResultFile(BatchStepFileDescriptor batchStepFile) {
        LOG.debug("Looking for a result file for "+ batchStepFile);

        String successFileName = getDirectoryPath() + batchStepFile.getNameNoExtension() +"."+ BatchStepFileDescriptor.getFileExtensionSuccess();
        File successFile = new File(successFileName);
        if (successFile != null && successFile.exists()) {
    		LOG.info("Found .success result file for "+ batchStepFile);

        	return new BatchStepFileDescriptor(successFile);
        }

        String errorFileName = getDirectoryPath() + batchStepFile.getNameNoExtension() +"."+ BatchStepFileDescriptor.getFileExtensionError();
        File errorFile = new File(errorFileName);
        if (errorFile != null && errorFile.exists()) {
    		LOG.info("Found .error result file for "+ batchStepFile);

            return new BatchStepFileDescriptor(errorFile);
        }


        return null;
    }

    /**
     * Checks whether the step's semaphore file in the descriptor is null and if not whether the step's semaphore file in the directory is empty
     *
     * @param batchStepFile the descriptor for which a semaphore file existence check is requested
     * @return true if the step's semaphore file is empty, false otherwise. Throws a RuntimeException if the file does not exist.
     */
    public boolean isFileEmpty(BatchStepFileDescriptor batchStepFile) {
    	File resultFile = batchStepFile.getStepFile();
    	if (resultFile == null) {
    		throw new RuntimeException(batchStepFile + BatchStepFileDescriptor.STEP_FILE_EXTENSION_SEPARATOR + batchStepFile.getExtension() +" does not exist");
    	}

    	return isFileEmpty(resultFile);
    }

    /**
     * Returns the index of the step contained in the file
     *
     * @param batchStepFile the file from which to retrieve the step index
     * @return the step index, or -1 if there was no content in the file
     */
    public int getStepIndexFromFile(BatchStepFileDescriptor batchStepFile) {
        File runFile = batchStepFile.getStepFile();
        if (runFile != null) {
            List<String> contents = getFileContents(runFile);
            if (contents.size() > 0) {
                return Integer.parseInt(contents.get(0));
            }
        }

        return -1;
    }

    /**
     * Reads the contents of the semaphore file (normally the error file), and writes the contents to the requested Logger.
     *
     * @param batchStepFile the descriptor whose semaphore file's contents should be written to the Logger
     * @param log the log to write the file contents to
     */
    public void logFileContents(BatchStepFileDescriptor batchStepFile, Logger log) {
    	File resultFile = batchStepFile.getStepFile();
    	if (resultFile != null) {
    	    List<String> contents = getFileContents(resultFile);
            String toLog = "";

    	    for (String line : contents) {
                toLog += line + "\n";
    	    }

            log.error("Exception found in "+ resultFile.getName() +"\n"+ toLog);
    	}
    }

    /**
     * Returns the Exception contained in an error result file
     *
     * @param errorResultFile
     * @return the exception
     */
    public String getExceptionFromFile(BatchStepFileDescriptor errorResultFile) {
        if (errorResultFile.isStepFileAnErrorResultFile()) {
            File resultFile = errorResultFile.getStepFile();
            if (resultFile != null) {
                List<String> contents = getFileContents(resultFile);
                String toLog = "";

                for (String line : contents) {
                    toLog += line + "\n";
                }

                return toLog;
            }
        }

        return "";
    }

    /**
     * Creates a shutdown hook and adds it to the local Runtime so that if the container's VM is shut down,
     * the semaphore files will be removed
     */
    public void addShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread() {

            @Override
            public void run() {
                removeBatchContainerSemaphore();
                if (isBatchContainerRunning()) {
                    LOG.info("the batch container was not shut down successfully; .runlock still exists.");
                }
            }

        });
    }

    /**
     * Returns a List<String> containing each line in the file
     *
     * @param file the file whose contents we want to retrieve
     * @return a String array containing each line in the file
     */
    private List<String> getFileContents(File file) {
        ArrayList<String> results = new ArrayList<String>();
        try {
            FileReader fileReader = new FileReader(file.getAbsolutePath());

            String line = "";
            BufferedReader inputBufferedReader = new BufferedReader(fileReader);

            String currentLine = inputBufferedReader.readLine();
            while (currentLine != null) {
                results.add(currentLine);
                currentLine = inputBufferedReader.readLine();
            }
            inputBufferedReader.close();

            return results;

        } catch (IOException e) {
            throw new RuntimeException("getFileContents() : " + e.getMessage(), e);
        }
    }

    /**
     * Returns the absolute path to the directory
     *
     * @return the absolute path to the batch container directory
     */
	private String getDirectoryPath() {
		return directory.getAbsolutePath() + File.separator;
	}

	/**
	 * Checks whether the specified File is empty.
	 *
	 * @param file the file to check
	 * @return true if the file is empty, false otherwise
	 */
    private boolean isFileEmpty(File file) {
    	return file.length() == 0;
    }


    private void writeMessageToFile(File runFile , String message) {
        PrintStream printStream = initializePrintStream(runFile);

        //write index
        printStream.print(message);
        printStream.flush();

        destroyPrintStream(printStream);
    }

    /**
     * Writes the index of the step to the File
     *
     * @param runFile the file to write to
     * @param stepIndex the step index to write
     */
    private void writeStepIndexToFile(File runFile, Integer stepIndex) {
        PrintStream printStream = initializePrintStream(runFile);

        //write index
        printStream.print(stepIndex.intValue());
        printStream.flush();

        destroyPrintStream(printStream);
    }

    /**
     * Writes the stack trace of Throwable to the File
     *
     * @param errorFile the file to write to
     * @param error the error to write
     */
    private void writeErrorMessageToFile(File errorFile, Throwable error) {
    	PrintStream printStream = initializePrintStream(errorFile);

    	//write error
    	error.printStackTrace(printStream);

        //write contents of the batch container directory
        printStream.println("BatchContainerDirectory contents:");
        File[] batchContainerFiles = directory.listFiles();

        if (batchContainerFiles == null || batchContainerFiles.length == 0) {
            printStream.println("empty");
        }
        else {
              for(File batchContainerFile : batchContainerFiles) {
                  printStream.println(batchContainerFile.getAbsolutePath());
              }
        }

        printStream.flush();

    	destroyPrintStream(printStream);
    }

    /**
     * Initializes a PrintStream to use for writing to.
     *
     * @param errorFile the file to write to
     * @return a new PrintStream to use to write to the file
     */
    private PrintStream initializePrintStream(File errorFile) {
    	//initialize PrintStream
        PrintStream printStream;
    	try {
    		printStream = new PrintStream(errorFile);
    	}
    	catch (FileNotFoundException e) {
    		LOG.error(e);
    		throw new RuntimeException(e);
    	}

    	return printStream;
    }

    /**
     * Closes the PrintStream
     *
     * @param printStream the print stream to close
     */
    private void destroyPrintStream(PrintStream printStream) {
    	//close PrintStream
        if(printStream != null) {
            printStream.close();
            printStream = null;
        }
    }
}
