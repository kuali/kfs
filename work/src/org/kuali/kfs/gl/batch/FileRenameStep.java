/*
 * Copyright 2005-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.gl.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.sys.batch.AbstractStep;
import org.springframework.util.StopWatch;

/**
 * A step to run the scrubber process.
 */
public class FileRenameStep extends AbstractStep {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileRenameStep.class);
    private String batchFileDirectoryName;
    
    
    public boolean execute(String jobName, Date jobRunDate) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start(jobName);
        String filePath = batchFileDirectoryName + File.separator;
        List<String> fileNameList = new ArrayList();
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.NIGHTLY_OUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.BACKUP_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.PRE_SCRUBBER_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_INPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_VALID_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_EXPIRED_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.SCRUBBER_ERROR_SORTED_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.DEMERGER_VAILD_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.DEMERGER_ERROR_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.POSTER_INPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_VALID_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.REVERSAL_POSTER_ERROR_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.POSTER_VALID_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.POSTER_ERROR_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.ICR_TRANSACTIONS_OUTPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_INPUT_FILE);
        fileNameList.add(GeneralLedgerConstants.BatchFileSystem.ICR_POSTER_ERROR_OUTPUT_FILE);
        
        //TODO: Shawn - need to change it to filename +  01-22-2009.12-43-43 (mm-dd-yyyy.hh-mm-ss)
        String timeString = jobRunDate.toString();
        String year = timeString.substring(timeString.length() - 4, timeString.length());
        String month = timeString.substring(4, 7);
        String day = timeString.substring(8, 10);
        String hour = timeString.substring(11, 13);
        String min = timeString.substring(14, 16);
        String sec = timeString.substring(17, 19);
        
        for (String fileName : fileNameList){
            File file = new File(filePath + fileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION);
            if (file.exists()) {
                String changedFileName = filePath + fileName + "." + year + "-" + month + "-" + day + "." + hour + "-" + min + "-" + sec;
                file.renameTo(new File(changedFileName + GeneralLedgerConstants.BatchFileSystem.EXTENSION));
            }
        }
        

        stopWatch.stop();
        if (LOG.isDebugEnabled()) {
            LOG.debug("FileRenameStep of " + jobName + " took " + (stopWatch.getTotalTimeSeconds() / 60.0) + " minutes to complete");
        }
        return true;
    }
    
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }

}
