package edu.arizona.kfs.vnd.service.impl;

import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.io.filefilter.SuffixFileFilter;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.sys.KFSConstants;

import edu.arizona.kfs.vnd.VendorConstants;
import edu.arizona.kfs.vnd.service.EcustomsFileService;

/**
 * UAF-66 MOD-PA7000-02 ECustoms - US Export Compliance
 *
 * New service to handle the creation of the eCustoms data file for validating vendor compliance with US Export Controls.
 *
 * @author Adam Kost kosta@email.arizona.edu
 */
public class EcustomsFileServiceImpl implements EcustomsFileService {

    private static final String ERROR_MISSING_OUTPUT_FILE_DIRECTORY = "The server property ecustoms.output.directory is blank.";
    private static final String ERROR_MISSING_OUTPUT_FILE_PREFIX = "The server property ecustoms.output.file.prefix is blank.";
    private static final String ERROR_CANNOT_CREATE_DIRECTORY = "Unable to create directory structure.";

    private static final Logger LOG = Logger.getLogger(EcustomsFileServiceImpl.class);
    private String outputFileDirectory;
    private String outputFileNamePrefix;

    // private getter for error checking purposes, so that this error checking can be reused whenever this field is used.
    private String getOutputFileDirectory() throws IOException {
        if (StringUtils.isBlank(outputFileDirectory)) {
            throw new IOException(ERROR_MISSING_OUTPUT_FILE_DIRECTORY);
        }
        return outputFileDirectory;
    }

    // private getter for error checking purposes, so that this error checking can be reused whenever this field is used.
    private String getOutputFileNamePrefix() throws IOException {
        if (StringUtils.isBlank(outputFileNamePrefix)) {
            throw new IOException(ERROR_MISSING_OUTPUT_FILE_PREFIX);
        }
        return outputFileNamePrefix;
    }

    public void setOutputFileDirectory(String outputFileDirectory) {
        this.outputFileDirectory = outputFileDirectory;
        LOG.debug("outputFileDirectory=" + outputFileDirectory);
    }

    public void setOutputFileNamePrefix(String outputFileNamePrefix) {
        this.outputFileNamePrefix = outputFileNamePrefix;
        LOG.debug("outputFileNamePrefix=" + outputFileNamePrefix);
    }

    @Override
    public File getDailyBatchDataFile(Date jobRunDate) throws IOException {
        String filename = getDailyBatchDataFileName(jobRunDate);
        File file = createFile(filename);
        return file;
    }

    @Override
    public File getAnnualBatchDataFile(Date jobRunDate) throws IOException {
        String filename = getAnnualBatchDataFileName(jobRunDate);
        File file = createFile(filename);
        return file;
    }

    @Override
    public File getDailyBatchVendorCountFile(Date jobRunDate) throws IOException {
        String filename = getDailyBatchVendorCountFileName(jobRunDate);
        File file = createFile(filename);
        return file;
    }

    @Override
    public File getAnnualBatchVendorCountFile(Date jobRunDate) throws IOException {
        String filename = getAnnualBatchVendorCountFileName(jobRunDate);
        File file = createFile(filename);
        return file;
    }

    @Override
    public void createDailyBatchDoneFile(Date jobRunDate) throws IOException {
        String filename = getDailyBatchDoneFileName(jobRunDate);
        File file = createFile(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        try {
            fileOutputStream.write(VendorConstants.ECUSTOMS_DONE_MESSAGE_DAILY.getBytes());
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (Exception e) {
            LOG.error("Unable to write to daily batch done file.");
        } finally {
            IOUtils.closeQuietly(fileOutputStream);
        }
    }

    @Override
    public void createAnnualBatchDoneFile(Date jobRunDate) throws IOException {
        String filename = getAnnualBatchDoneFileName(jobRunDate);
        File file = createFile(filename);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        fileOutputStream.write(VendorConstants.ECUSTOMS_DONE_MESSAGE_ANNUAL.getBytes());
        fileOutputStream.flush();
        fileOutputStream.close();
    }

    @Override
    public boolean deleteOldDailyDoneFiles() throws IOException {
        File outputDir = new File(getOutputFileDirectory());
        if (outputDir.exists() && outputDir.isDirectory()) {
            String filenameFilter = KFSConstants.WILDCARD_CHARACTER + VendorConstants.ECUSTOMS_DAILY_JOB_NAME + KFSConstants.WILDCARD_CHARACTER + VendorConstants.FILE_EXTENSION_DONE;
            FileFilter filter = new SuffixFileFilter(filenameFilter);
            File[] oldDoneFiles = outputDir.listFiles(filter);
            if (oldDoneFiles == null) {
                return false;
            }
            if (oldDoneFiles != null) {
                for (int i = 0; i < oldDoneFiles.length; ++i) {
                    LOG.debug("deleting file " + oldDoneFiles[i].getAbsolutePath());
                    if (!FileUtils.deleteQuietly(oldDoneFiles[i])) {
                        LOG.warn("unable to delete file " + oldDoneFiles[i].getAbsolutePath());
                    }
                }
            }
        }
        return true;
    };

    @Override
    public boolean deleteOldAnnualDoneFiles() throws IOException {
        File outputDir = new File(getOutputFileDirectory());
        if (outputDir.exists() && outputDir.isDirectory()) {
            String filenameFilter = KFSConstants.WILDCARD_CHARACTER + VendorConstants.ECUSTOMS_ANNUAL_JOB_NAME + KFSConstants.WILDCARD_CHARACTER + VendorConstants.FILE_EXTENSION_DONE;
            FileFilter filter = new SuffixFileFilter(filenameFilter);
            File[] oldDoneFiles = outputDir.listFiles(filter);
            if (oldDoneFiles == null) {
                return false;
            }
            if (oldDoneFiles != null) {
                for (int i = 0; i < oldDoneFiles.length; ++i) {
                    LOG.debug("deleting file " + oldDoneFiles[i].getAbsolutePath());
                    if (!FileUtils.deleteQuietly(oldDoneFiles[i])) {
                        LOG.warn("unable to delete file " + oldDoneFiles[i].getAbsolutePath());
                    }
                }
            }
        }
        return true;
    };

    private String getFileName(String extra, String fileExt, Date jobRunDate) throws IOException {
        StringBuilder retval = new StringBuilder();
        retval.append(getOutputFileDirectory());
        retval.append(File.separator);
        retval.append(getOutputFileNamePrefix());
        retval.append(VendorConstants.ECUSTOMS_UNDERSCORE);
        retval.append(VendorConstants.ECUSTOMS_FILENAME);
        retval.append(VendorConstants.ECUSTOMS_UNDERSCORE);
        retval.append(extra);
        retval.append(VendorConstants.ECUSTOMS_UNDERSCORE);
        retval.append(VendorConstants.ECUSTOMS_FILENAME_DATE_FORMAT.format(jobRunDate));
        retval.append(fileExt);

        LOG.debug("file name: " + retval.toString());
        return retval.toString();
    }

    private String getDailyBatchDataFileName(Date jobRunDate) throws IOException {
        String retval = getFileName(VendorConstants.ECUSTOMS_DAILY_JOB_NAME, VendorConstants.FILE_EXTENSION_DATA, jobRunDate);
        return retval;
    }

    private String getAnnualBatchDataFileName(Date jobRunDate) throws IOException {
        String retval = getFileName(VendorConstants.ECUSTOMS_ANNUAL_JOB_NAME, VendorConstants.FILE_EXTENSION_DATA, jobRunDate);
        return retval;
    }

    private String getDailyBatchDoneFileName(Date jobRunDate) throws IOException {
        String retval = getFileName(VendorConstants.ECUSTOMS_DAILY_JOB_NAME, VendorConstants.FILE_EXTENSION_DONE, jobRunDate);
        return retval;
    }

    private String getAnnualBatchDoneFileName(Date jobRunDate) throws IOException {
        String retval = getFileName(VendorConstants.ECUSTOMS_ANNUAL_JOB_NAME, VendorConstants.FILE_EXTENSION_DONE, jobRunDate);
        return retval;
    }

    private String getDailyBatchVendorCountFileName(Date jobRunDate) throws IOException {
        String retval = getFileName(VendorConstants.ECUSTOMS_DAILY_JOB_NAME, VendorConstants.FILE_EXTENSION_COUNT, jobRunDate);
        return retval;
    }

    private String getAnnualBatchVendorCountFileName(Date jobRunDate) throws IOException {
        String retval = getFileName(VendorConstants.ECUSTOMS_ANNUAL_JOB_NAME, VendorConstants.FILE_EXTENSION_COUNT, jobRunDate);
        return retval;
    }

    private File createFile(String filename) throws IOException {
        // check for the directory structure and create if it does not exist.
        File fdir = new File(getOutputFileDirectory());
        if (!fdir.exists()) {
            boolean makeDirs = fdir.mkdirs();
            if (!makeDirs) {
                throw new IOException(ERROR_CANNOT_CREATE_DIRECTORY);
            }
        }
        // create the data file.
        File file = new File(filename);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            throw new IOException("Unable to create file for vendor security compliance: " + file.getAbsolutePath(), ex);
        }
        return file;
    }
}