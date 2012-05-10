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

import static org.kuali.kfs.sys.fixture.UserNameFixture.kfs;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.batch.AutoDisapproveDocumentsStep;
import org.kuali.kfs.sys.batch.BatchSpringContext;
import org.kuali.kfs.sys.batch.Step;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.ProxyUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.dataaccess.UnitTestSqlDao;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.DocumentService;
import org.springframework.aop.support.AopUtils;

@ConfigureContext(session = kfs)
public class AutoDisapproveDocumentsServiceImplTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AutoDisapproveDocumentsServiceImplTest.class);

    private AutoDisapproveDocumentsServiceImpl autoDisapproveDocumentsService;
    private DateTimeService dateTimeService;
    private UnitTestSqlDao unitTestSqlDao;
    private DocumentService documentService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        autoDisapproveDocumentsService = (AutoDisapproveDocumentsServiceImpl) TestUtils.getUnproxiedService("sysMockAutoDisapproveDocumentsService");
        dateTimeService = SpringContext.getBean(DateTimeService.class);
        ConfigurationService kualiConfigurationService = SpringContext.getBean(ConfigurationService.class);
    }

    /**
     * This method tests if the YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE parameter is setup.  If not, accessing it will cause a NPE
     */
    public final void testCheckIfRunDateParameterExists() {
        boolean parameterExists = autoDisapproveDocumentsService.checkIfRunDateParameterExists();
        assertTrue("YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_RUN_DATE System parameter does not exist.", parameterExists);
    }

    /**
     * This method tests if the YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE parameter is setup.  If not, accessing it will cause a NPE
     */
    public final void testCheckIfParentDocumentTypeParameterExists() {
        boolean parameterExists = autoDisapproveDocumentsService.checkIfParentDocumentTypeParameterExists();
        assertTrue("YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE System parameter does not exist.", parameterExists);
    }

    /**
     * This method tests if the YEAR_END_AUTO_DISAPPROVE_PARENT_DOCUMENT_TYPE parameter is setup.  If not, accessing it will cause a NPE
     */
    public final void testCheckIfDocumentCompareCreateDateParameterExists() {
        boolean parameterExists = autoDisapproveDocumentsService.checkIfDocumentCompareCreateDateParameterExists();
        assertTrue("YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE System parameter does not exist.", parameterExists);
    }

    /**
     * This method tests if the YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES parameter is setup.  If not, accessing it will cause a NPE
     */
    public final void testCheckIfDocumentTypesExceptionParameterExists() {
        boolean parameterExists = autoDisapproveDocumentsService.checkIfParentDocumentTypeParameterExists();
        assertTrue("YEAR_END_AUTO_DISAPPROVE_DOCUMENT_TYPES System parameter does not exist.", parameterExists);
    }

    /**
     * This method tests if the YEAR_END_AUTO_DISAPPROVE_ANNOTATION parameter is setup.  If not, accessing it will cause a NPE
     */
    public final void testCheckIfAnnotationForDisapprovalParameterExists() {
        boolean parameterExists = autoDisapproveDocumentsService.checkIfAnnotationForDisapprovalParameterExists();
        assertTrue("YEAR_END_AUTO_DISAPPROVE_ANNOTATION System parameter does not exist.", parameterExists);
    }

    /**
     * This method prepares today's date and returns the today's date
     */
    protected Date getToday() {
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

        return compareDate;
    }

    /**
     * This method will test today's date and compare to that the system parameter date
     * Today's date will be extended by adding 1 second less than 24 and this will be compared to the returned value
     * from getDocumentCompareDateParameter() method.
     */
    public final void testGetDocumentCompareDateParameter() {
        boolean datesEqual = true;

        Date compareDate = getToday();

        Step step = BatchSpringContext.getStep("autoDisapproveDocumentsStep");
        AutoDisapproveDocumentsStep autoDisapproveDocumentsStep = (AutoDisapproveDocumentsStep) ProxyUtils.getTargetIfProxied(step);

        if (autoDisapproveDocumentsService.systemParametersForAutoDisapproveDocumentsJobExist()) {
            // set the parameter to today and then compare to the date retrieved from the method getDocumentCompareDateParameter..
            String today = dateTimeService.toDateString(dateTimeService.getCurrentDate());
            TestUtils.setSystemParameter(AopUtils.getTargetClass(autoDisapproveDocumentsStep), KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_CREATE_DATE, today);
            Date documentCompareDate = autoDisapproveDocumentsService.getDocumentCompareDateParameter();
            datesEqual = (compareDate.equals(documentCompareDate));
            assertTrue("The two Dates are not equal.  The getDocumentCompareDateParameter() method did not extend the date by 23 hours, 59 mins and 59 seconds", datesEqual);
        }
    }

    /**
     * This test method will test if the auto disapproval job can be run by comparing today's date to
     * the system parameter YEAR_END_AUTO_DISAPPROVE_DOCUMENTS_STEP_RUN_DATE value
     */
    public final void testCanAutoDisapproveJobRun() {
        Step step = BatchSpringContext.getStep("autoDisapproveDocumentsStep");
        AutoDisapproveDocumentsStep autoDisapproveDocumentsStep = (AutoDisapproveDocumentsStep) ProxyUtils.getTargetIfProxied(step);

        if (autoDisapproveDocumentsService.systemParametersForAutoDisapproveDocumentsJobExist()) {
            // intentionally set the run date to 00/00/00 so the test fails...
            TestUtils.setSystemParameter(AopUtils.getTargetClass(autoDisapproveDocumentsStep), KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_STEP_RUN_DATE, "00/00/00");

            boolean canJobRun = autoDisapproveDocumentsService.canAutoDisapproveJobRun();
            assertFalse("The canAutoDisapproveJobRun() method did not fail..", canJobRun);

            //set the system parameter to today's date.  The test should pass...
            String today = dateTimeService.toDateString(dateTimeService.getCurrentDate());
            TestUtils.setSystemParameter(AopUtils.getTargetClass(autoDisapproveDocumentsStep), KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_STEP_RUN_DATE, today);
            canJobRun = autoDisapproveDocumentsService.canAutoDisapproveJobRun();
            assertTrue("The canAutoDisapproveJobRun() method failed because system parameter date does not equal to today's date.", canJobRun);
        }
    }

    /**
     * This test method will add a document to the table and call the method in impl class
     * to see if the document can be retrieve properly.
     */
    public final void testAutoDisapproveDocumentsInEnrouteStatus() {

        Step step = BatchSpringContext.getStep("autoDisapproveDocumentsStep");
        AutoDisapproveDocumentsStep autoDisapproveDocumentsStep = (AutoDisapproveDocumentsStep) ProxyUtils.getTargetIfProxied(step);

        if (autoDisapproveDocumentsService.systemParametersForAutoDisapproveDocumentsJobExist()) {
            //set the system parameter to today's date.  The test should pass...
            String today = dateTimeService.toDateString(dateTimeService.getCurrentDate());
            TestUtils.setSystemParameter(AopUtils.getTargetClass(autoDisapproveDocumentsStep), KFSParameterKeyConstants.YearEndAutoDisapprovalConstants.YEAR_END_AUTO_DISAPPROVE_DOCUMENT_STEP_RUN_DATE, today);
            boolean success = autoDisapproveDocumentsService.autoDisapproveDocumentsInEnrouteStatus();
            assertTrue("The auto disproval job did not succeed.", success);
        }
    }
}
