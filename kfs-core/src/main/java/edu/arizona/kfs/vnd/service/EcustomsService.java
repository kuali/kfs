package edu.arizona.kfs.vnd.service;

import java.util.Date;

/**
 * UAF-66 MOD-PA7000-02 ECustoms - US Export Compliance
 *
 * New service is to perform the batch job steps for validating vendor compliance with US Export Controls.
 *
 * @author Adam Kost kosta@email.arizona.edu
 */
public interface EcustomsService {

    /**
     * This method creates an eCustoms file for vendors created or updated since the last time this was run successfully.
     *
     * @param jobName
     * @param jobRunDate
     * @return true if file successfully created
     * @throws Exception
     */
    public boolean createEcustomsDailyFile(String jobName, Date jobRunDate) throws Exception;

    /**
     * This method creates an eCustoms file for all vendors for annual verification.
     *
     * @param jobName
     * @param jobRunDate
     * @return true if file successfully created
     * @throws Exception
     */
    public boolean createEcustomsAnnualFile(String jobName, Date jobRunDate) throws Exception;

}