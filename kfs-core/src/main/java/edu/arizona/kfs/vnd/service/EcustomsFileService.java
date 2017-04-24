package edu.arizona.kfs.vnd.service;

import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * UAF-66 MOD-PA7000-02 ECustoms - US Export Compliance
 *
 * New service to handle the creation of the eCustoms data file for validating vendor compliance with US Export Controls.
 *
 * @author Adam Kost kosta@email.arizona.edu
 */

public interface EcustomsFileService {

    /**
     * This method creates and returns a file for output of daily results.
     *
     * @return File for daily batch output.
     * @throws IOException
     * @throws Exception
     */
    public File getDailyBatchDataFile(Date jobRunDate) throws IOException;

    /**
     * This method creates and returns a File for storing the vendor count of the daily job.
     *
     * @return File
     * @throws IOException
     * @throws Exception
     */
    public File getDailyBatchVendorCountFile(Date jobRunDate) throws IOException;

    /**
     * This method creates a file that indicates the daily job completed successfully.
     *
     * @throws IOException
     */
    public void createDailyBatchDoneFile(Date jobRunDate) throws IOException;


    /**
     * This method creates and returns a file for output of annual results.
     *
     * @return File for annual batch output.
     * @throws IOException
     * @throws Exception
     */
    public File getAnnualBatchDataFile(Date jobRunDate) throws IOException;

    /**
     * This method creates and returns a File for storing the vendor count of the annual job.
     *
     * @return File
     * @throws IOException
     * @throws Exception
     */
    public File getAnnualBatchVendorCountFile(Date jobRunDate) throws IOException;

    /**
     * This method creates a file that indicates the annual job completed successfully.
     *
     * @throws IOException
     */
    public void createAnnualBatchDoneFile(Date jobRunDate) throws IOException;

    /**
     * This method deletes any existing done files created for annual jobs.
     *
     * @return false if no files were deleted, otherwise true.
     * @throws IOException
     */
    public boolean deleteOldAnnualDoneFiles() throws IOException;

    /**
     * This method deletes existing done files
     *
     * @return false if no files were deleted, otherwise true.
     * @throws IOException
     */
    public boolean deleteOldDailyDoneFiles() throws IOException;

}