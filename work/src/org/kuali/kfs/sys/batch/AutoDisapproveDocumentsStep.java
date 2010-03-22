/*
 * Copyright 2006 The Kuali Foundation
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.service.AutoDisapproveDocumentsService;

/**
 * Runs the batch job that gathers all documents that are in ENROUTE status and cancels them.
 */
public class AutoDisapproveDocumentsStep extends AbstractStep {
    private AutoDisapproveDocumentsService autoDisapproveDocumentsService;
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoDisapproveDocumentsStep.class);
    private String batchFileDirectoryName;

    /**
     * This step will auto disapprove the documents that are in ENROUTE status.
     * 
     * @return true if the job completed successfully, false if otherwise
     * @see org.kuali.kfs.sys.batch.Step#execute(String, Date)
     */
    public boolean execute(String jobName, Date jobRunDate) {
        boolean success = true;
        
        //open the output error file and write the headers...
        PrintStream outputErrorFile_ps = openPrintStreamForAutoDisapproveErrorsAndWriteHeader();
        
        if (systemParametersForAutoDisapproveDocumentsJobExist(outputErrorFile_ps)) {
            if (canAutoDisapproveJobRun(outputErrorFile_ps)) {
                success = autoDisapproveDocumentsService.autoDisapproveDocumentsInEnrouteStatus(outputErrorFile_ps);
            }
        }
        else {
            outputErrorFile_ps.close();
        }

        return success;            
    }

    /**
     * This method will open the error file and writes the header information.
     */
    protected PrintStream openPrintStreamForAutoDisapproveErrorsAndWriteHeader() {
        LOG.info("openPrintStreamForAutoDisapproveErrorsAndWriteHeader() started.");
        
        try {
            
            PrintStream outputErrorFile_ps = new PrintStream(batchFileDirectoryName + File.separator + GeneralLedgerConstants.BatchFileSystem.AUTO_DISAPPROVE_DOCUMENTS_ERROR_OUTPUT_FILE + GeneralLedgerConstants.BatchFileSystem.TEXT_EXTENSION);
            outputErrorFile_ps.printf("Auto Disapproval Process - Errors - Job run date: %s\n", getDateTimeService().getCurrentDate().toString());
            outputErrorFile_ps.printf("%s\n\n", "--------------------------------------------------------------------------------------------------------------------");
            return outputErrorFile_ps;            
        }         
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
            throw new RuntimeException("Can not open Error output fle for AutoDisapprovalStep process: " + e1.getMessage(), e1);
        }
    }
    
    /**
     * This method checks if the System parameters have been set up for this batch job.
     * @result return true if the system parameters exist, else false
     */
    public boolean systemParametersForAutoDisapproveDocumentsJobExist(PrintStream outputErrorFile_ps) {
        LOG.info("systemParametersForAutoDisapproveDocumentsJobExist() started.");
        
        boolean systemParametersExists = true;
        
        systemParametersExists &= checkIfRunDateParameterExists(outputErrorFile_ps);
        systemParametersExists &= checkIfParentDocumentTypeParameterExists(outputErrorFile_ps);  
        systemParametersExists &= checkIfDocumentCompareCreateDateParameterExists(outputErrorFile_ps);  
        systemParametersExists &= checkIfDocumentTypesExceptionParameterExists(outputErrorFile_ps);          
        systemParametersExists &= checkIfAnnotationForDisapprovalParameterExists(outputErrorFile_ps);
        
        return systemParametersExists;
    }
    
    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE exists else false
     */
    protected boolean checkIfRunDateParameterExists(PrintStream outputErrorFile_ps) {
        boolean parameterExists = true;
        
        // check to make sure the system parameter for run date check has already been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_STEP_RUN_DATE)) {
            LOG.warn("YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE System parameter does not exist in the parameters list.  The job can not continue without this parameter");
            outputErrorFile_ps.printf("%s\n", "YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE System parameter does not exist in the parameters list.  The job can not continue without this parameter");          
            return false;
        }
        
        return parameterExists;
    }

    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE exists else false
     */
    protected boolean checkIfParentDocumentTypeParameterExists(PrintStream outputErrorFile_ps) {
        boolean parameterExists = true;
        
        // check to make sure the system parameter for Parent Document Type = FP has been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE)) {
            LOG.warn("YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE System parameter does not exist in the parameters list.  The job can not continue without this parameter");
            outputErrorFile_ps.printf("%s\n", "YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE System parameter does not exist in the parameters list.  The job can not continue without this parameter");          
            return false;
        }
        
        return parameterExists;
    }
    
    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE exists else false
     */
    protected boolean checkIfDocumentCompareCreateDateParameterExists(PrintStream outputErrorFile_ps) {
        boolean parameterExists = true;
        
        // check to make sure the system parameter for create date to compare has been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE)) {
          LOG.warn("YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          outputErrorFile_ps.printf("%s\n", "YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE System parameter does not exist in the parameters list.  The job can not continue without this parameter");          
          return false;
        }
        
        return parameterExists;
    }
    
    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES exists else false
     */
    protected boolean checkIfDocumentTypesExceptionParameterExists(PrintStream outputErrorFile_ps) {
        boolean parameterExists = true;
        
        // check to make sure the system parameter for Document Types that are exceptions has been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES)) {
          LOG.warn("YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          outputErrorFile_ps.printf("%s\n", "YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES System parameter does not exist in the parameters list.  The job can not continue without this parameter");          
          return false;
        }
        
        return parameterExists;
    }

    /**
     * This method checks for the system parameter for YEAR_END_AUTO_DISAPPROVE_ANNOTATION
     * @param outputErrorFile_ps output error file stream to write any error messages.
     * @return true if YEAR_END_AUTO_DISAPPROVE_ANNOTATION exists else false
     */
    protected boolean checkIfAnnotationForDisapprovalParameterExists(PrintStream outputErrorFile_ps) {
        boolean parameterExists = true;
        
        // check to make sure the system parameter for annotation for notes has been setup...
        if (!getParameterService().parameterExists(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_ANNOTATION)) {
          LOG.warn("YEAR_END_AUTO_DISAPPROVE_ANNOTATION System parameter does not exist in the parameters list.  The job can not continue without this parameter");
          outputErrorFile_ps.printf("%s\n", "YEAR_END_AUTO_DISAPPROVE_ANNOTATION System parameter does not exist in the parameters list.  The job can not continue without this parameter");          
          return false;
        }        
        
        return parameterExists;
    }
    
    /**
     * This method will compare today's date to the system parameter for year end auto disapproval run date
     * @return true if today's date equals to the system parameter run date
     */
    protected boolean canAutoDisapproveJobRun(PrintStream outputErrorFile_ps) {
      boolean autoDisapproveCanRun = true;
      
      // IF trunc(SYSDATE - 14/24) = v_yec_cncl_doc_run_dt THEN...FIS CODE equivalent here...
      String yearEndAutoDisapproveRunDate = getParameterService().getParameterValue(AutoDisapproveDocumentsStep.class, KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_STEP_RUN_DATE);
      
      String today = getDateTimeService().toDateString(getDateTimeService().getCurrentDate());
      
      if (!yearEndAutoDisapproveRunDate.equals(today)) {
          LOG.warn("Automatic disapproval bypassed - date test failed. The date on which the auto disapproval step should run: " + yearEndAutoDisapproveRunDate + " does not equal to today's date: " + today);
          outputErrorFile_ps.printf("Automatic disapproval bypassed - date test failed. The date on which the auto disapproval step should run: %s does not equal to today's date: %s\n", yearEndAutoDisapproveRunDate, today);          
          outputErrorFile_ps.close();
          autoDisapproveCanRun = false;
      }
      
      return autoDisapproveCanRun;
    }
        
    /**
     * Sets the autoDisapproveEDocsService attribute value.
     * 
     * @param autoDisapproveEDocsService The autoDisapproveEDocsService to set.
     * @see org.kuali.kfs.sys.service.AutoDisapproveEDocsService
     */
    public void setAutoDisapproveDocumentsService(AutoDisapproveDocumentsService autoDisapproveDocumentsService) {
        this.autoDisapproveDocumentsService = autoDisapproveDocumentsService;
    }
    
    /**
     * This method sets the batchFileDirectoryName
     * @param batchFileDirectoryName
     */
    public void setBatchFileDirectoryName(String batchFileDirectoryName) {
        this.batchFileDirectoryName = batchFileDirectoryName;
    }
}
