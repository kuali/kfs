/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.gl.service;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.UnitTestSqlDao;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.MockCollectorBatch;
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.batch.collector.CollectorInputFileType;
import org.kuali.module.gl.bo.CollectorDetail;
import org.kuali.module.gl.bo.OriginEntryFull;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.util.CollectorReportData;
import org.kuali.module.gl.util.CollectorScrubberStatus;
import org.kuali.test.ConfigureContext;
import org.kuali.test.suite.RelatesTo;

/**
 * Test the CollectorService.
 */
@ConfigureContext
public class CollectorServiceTest extends KualiTestBase {
    private KualiConfigurationService configurationService;
    private CollectorHelperService collectorHelperService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        configurationService = SpringContext.getBean(KualiConfigurationService.class);
        collectorHelperService = SpringContext.getBean(CollectorHelperService.class);
    }

    /**
     * Verifies system parameters needed to send the collector email exists.
     */
    @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testEmailSystemParametersExist() throws Exception {
        String subject = configurationService.getParameterValue(KFSConstants.GL_NAMESPACE, GLConstants.Components.COLLECTOR_STEP, SystemGroupParameterNames.COLLECTOR_VALIDATOR_EMAIL_SUBJECT_PARAMETER_NAME);
        assertTrue("system parameter " + SystemGroupParameterNames.COLLECTOR_VALIDATOR_EMAIL_SUBJECT_PARAMETER_NAME + " is not setup or is empty", StringUtils.isNotBlank(subject));
        
        String[] documentTypes = configurationService.getParameterValues(KFSConstants.GL_NAMESPACE, GLConstants.Components.COLLECTOR_STEP, SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES);
        assertTrue("system parameter " + SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES + " is not setup or is empty", documentTypes.length > 0);
    }

    /**
     * Verifies an error is added when the batch header is a duplicate (a batch loaded previously had the same header).
     */
    @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testPerformValidate_duplicateHeader() throws Exception {

    }

    /**
     * Verifies an error is added when the batch entries contain multiple document types. Note: Actual test values here do not have
     * to be valid document types, only need to be different.
     */
    @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testPerformValidation_mixedDocumentTypes() throws Exception {
        MockCollectorBatch collectorBatch = new MockCollectorBatch();

        OriginEntryFull entry1 = new OriginEntryFull();
        entry1.setFinancialDocumentTypeCode("foo1");
        collectorBatch.addOriginEntry(entry1);

        OriginEntryFull entry2 = new OriginEntryFull();
        entry2.setFinancialDocumentTypeCode("foo2");
        collectorBatch.addOriginEntry(entry2);

        boolean isValid = collectorHelperService.performValidation(collectorBatch);
        assertFalse("returned batch was valid but there were multiple document types", isValid);
        assertTrue("error message for mixed document types does not exist", GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.Collector.MIXED_DOCUMENT_TYPES));
    }

    /**
     * Verifies an error is added when a collector detail key does not have a matching gl entry. Note: Actual test values do have
     * to be valid, only need to be different from the gl record to the detail
     */
    @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testPerformValidation_unmatchedDetailKey() throws Exception {
        MockCollectorBatch collectorBatch = new MockCollectorBatch();

        OriginEntryFull entry = new OriginEntryFull();
        entry.setUniversityFiscalYear(new Integer(2007));
        entry.setChartOfAccountsCode("BA");
        entry.setAccountNumber("1912610");
        collectorBatch.addOriginEntry(entry);

        CollectorDetail collectorDetail = new CollectorDetail();
        collectorDetail.setUniversityFiscalYear(new Integer(2007));
        collectorDetail.setChartOfAccountsCode("UA");
        collectorDetail.setAccountNumber("1912660");
        collectorBatch.addCollectorDetail(collectorDetail);

        boolean isValid = collectorHelperService.performValidation(collectorBatch);
        assertFalse("returned batch was valid but there was detail record without a matching gl entry", isValid);
        assertTrue("error message for unmatched detail key does not exist", GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.Collector.NONMATCHING_DETAIL_KEY));
    }
    
    /**
     * Verifies an error is added when the batch entries contain multiple balance types. Note: Actual test values here do not have
     * to be valid balance types, only need to be different.
     */
    @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testPerformValidation_mixedBalanceTypes() throws Exception {
        MockCollectorBatch collectorBatch = new MockCollectorBatch();

        OriginEntryFull entry1 = new OriginEntryFull();
        entry1.setFinancialBalanceTypeCode("AC");
        collectorBatch.addOriginEntry(entry1);

        OriginEntryFull entry2 = new OriginEntryFull();
        entry2.setFinancialBalanceTypeCode("IE");
        collectorBatch.addOriginEntry(entry2);

        boolean isValid = collectorHelperService.performValidation(collectorBatch);
        assertFalse("returned batch was valid but there were multiple balance types", isValid);
        assertTrue("error message for mixed balance types does not exist", GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.Collector.MIXED_BALANCE_TYPES));
    }
    
    /**
     * Tests that the ID billing detail amounts are negative when appropriate. See https://test.kuali.org/jira/browse/KULRNE-4845 for specs.
     * 
     * @throws Exception
     */
    @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testNegativeCollectorDetailBillingAmounts() throws Exception {
        String collectorDirectoryName = SpringContext.getBean(CollectorInputFileType.class).getDirectoryPath();
        String fileName = collectorDirectoryName + File.separator + "gl_collector3.xml";
        
        UnitTestSqlDao unitTestSqlDao = SpringContext.getBean(UnitTestSqlDao.class);
        unitTestSqlDao.sqlCommand("delete from GL_ID_BILL_T");
        
        OriginEntryGroup group = SpringContext.getBean(OriginEntryGroupService.class).createGroup(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate(), 
                OriginEntrySource.COLLECTOR, true, false, true);
        collectorHelperService.loadCollectorFile(fileName, group, new CollectorReportData(), new ArrayList<CollectorScrubberStatus>());
        
        Map<String, String> criteria = new HashMap<String, String>();
        // empty criteria, so return everything
        Collection<CollectorDetail> allCollectorDetails = SpringContext.getBean(BusinessObjectService.class).findMatching(CollectorDetail.class, criteria);
        
        assertEquals("Incorrect number of CollectorDetail records found", 3, allCollectorDetails.size());
        
        
        // this code assumes that all values in the details start off as positive, so that if it were negated, then
        // the values would be negative.
        
        for (CollectorDetail collectorDetail : allCollectorDetails) {
            if (KFSConstants.GL_DEBIT_CODE.equals(collectorDetail.getObjectType().getFinObjectTypeDebitcreditCd())) {
                assertTrue("Collector detail amount for obj code with debit object type must be negated (assuming initial value is positive)", 
                        collectorDetail.getCollectorDetailItemAmount().compareTo(KualiDecimal.ZERO) < 0);
            }
            else if (KFSConstants.GL_CREDIT_CODE.equals(collectorDetail.getObjectType().getFinObjectTypeDebitcreditCd())) {
                assertTrue("Collector detail amount for obj code with credit object type must not be negated (assuming initial value is positive)", 
                        collectorDetail.getCollectorDetailItemAmount().compareTo(KualiDecimal.ZERO) > 0);
            }
            else {
                // not sure if we need to take into account the blank debit/credit indicator for the object type
                fail("Unrecognized ID Billing detail object code information " + collectorDetail);
            }
        }
    }
}
