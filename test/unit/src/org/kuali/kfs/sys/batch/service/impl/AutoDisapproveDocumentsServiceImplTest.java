/*
 * Copyright 2008-2009 The Kuali Foundation
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
package org.kuali.kfs.sys.batch.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.AutoDisapproveDocumentsStep;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.springframework.aop.support.AopUtils;

@ConfigureContext
public class AutoDisapproveDocumentsServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoDisapproveDocumentsServiceImplTest.class);
    
    private AutoDisapproveDocumentsServiceImpl autoDisapproveDocumentsService;    
    private DateTimeService dateTimeService;
    private String reportsDirectory;
    private PrintStream outputErrorFile_ps;
    
    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        autoDisapproveDocumentsService = (AutoDisapproveDocumentsServiceImpl) TestUtils.getUnproxiedService("autoDisapproveService");
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        KualiConfigurationService kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class); 
        reportsDirectory = kualiConfigurationService.getPropertyString(KFSConstants.REPORTS_DIRECTORY_KEY) + "/sys";
        
        outputErrorFile_ps = openPrintStreamForAutoDisapproveErrorsAndWriteHeader();
        
    }

    /**
     * This method will open the error file and writes the header information.
     */
    protected PrintStream openPrintStreamForAutoDisapproveErrorsAndWriteHeader() {
        LOG.info("openPrintStreamForAutoDisapproveErrorsAndWriteHeader() started.");
        
        try {
            PrintStream printStreamForErrorOutput = new PrintStream(reportsDirectory + File.separator + "UnitTest_sys_autoDisapprove_errs" + GeneralLedgerConstants.BatchFileSystem.TEXT_EXTENSION);
            printStreamForErrorOutput.printf("Auto Disapproval Process - Errors - Job run date: %s\n", dateTimeService.getCurrentDate().toString());
            printStreamForErrorOutput.printf("%s\n\n", "------------------------------------------------------------------------------------");
            return printStreamForErrorOutput;            
        }         
        catch (FileNotFoundException e1) {
            e1.printStackTrace();
            throw new RuntimeException("Can not open Error output fle for AutoDisapprovalStep process: " + e1.getMessage(), e1);
        }
    }
    
    /**
     * This method tests if the system parameters have been setup.  If they have not been setup, accessing them will cause a NPE
     */
    public final void testSystemParametersExist() {
        Step step = BatchSpringContext.getStep("autoDisapproveDocumentsStep");
        AutoDisapproveDocumentsStep autoDisapproveDocumentsStep = (AutoDisapproveDocumentsStep) ProxyUtils.getTargetIfProxied(step);

        boolean systemParametersExist = autoDisapproveDocumentsStep.systemParametersForAutoDisapproveDocumentsJobExist(outputErrorFile_ps);
   //     assertTrue("System Parameters for this autoDisapproveDocumentsStep job have not been setup.", systemParametersExist);
    }
    
    /**
     * This method will test today's date and compare to that the system parameter date
     * Today's date will be extended by adding 1 second less than 24 and this will be compared to the returned value
     * from getDocumentCompareDateParameter() method.
     */
    public final void testGetDocumentCompareDateParameter() {
        boolean datesEqual = true;
        Date compareDate = null;
        
        String today = dateTimeService.toDateString(dateTimeService.getCurrentDate());
        try {
            Date tmpcompareDate = dateTimeService.convertToDate(today);
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(tmpcompareDate);
            calendar.set(Calendar.HOUR, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            compareDate = calendar.getTime();
        }
        catch (ParseException pe) {
            
        }
        
        Step step = BatchSpringContext.getStep("autoDisapproveDocumentsStep");
        AutoDisapproveDocumentsStep autoDisapproveDocumentsStep = (AutoDisapproveDocumentsStep) ProxyUtils.getTargetIfProxied(step);
        
        if (autoDisapproveDocumentsStep.systemParametersForAutoDisapproveDocumentsJobExist(outputErrorFile_ps)) {
            TestUtils.setSystemParameter(AopUtils.getTargetClass(autoDisapproveDocumentsStep), KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE, today);

            Date documentCompareDate = autoDisapproveDocumentsService.getDocumentCompareDateParameter(outputErrorFile_ps);
            datesEqual = (compareDate.equals(documentCompareDate));
        }
        
    //    assertTrue("The two Dates are not equal.  The getDocumentCompareDateParameter() method did not extend the date by 23 hours, 59 mins and 59 seconds", datesEqual);
    }
}
