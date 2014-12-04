/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.batch.service;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.batch.CollectorStep;
import org.kuali.kfs.gl.batch.MockCollectorBatch;
import org.kuali.kfs.gl.businessobject.CollectorDetail;
import org.kuali.kfs.gl.businessobject.OriginEntryFull;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;

// import org.kuali.kfs.suite.RelatesTo;

/**
 * Test the CollectorService.
 */
@ConfigureContext
public class CollectorServiceTest extends KualiTestBase {
    private ParameterService parameterService;
    private CollectorHelperService collectorHelperService;

    /**
     * Initializes services needed by this test
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        parameterService = SpringContext.getBean(ParameterService.class);
        collectorHelperService = SpringContext.getBean(CollectorHelperService.class);
    }

    /**
     * Verifies system parameters needed to send the collector email exists.
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testEmailSystemParametersExist() throws Exception {
        String subject = parameterService.getParameterValueAsString(CollectorStep.class, SystemGroupParameterNames.COLLECTOR_VALIDATOR_EMAIL_SUBJECT_PARAMETER_NAME);
        assertTrue("system parameter " + SystemGroupParameterNames.COLLECTOR_VALIDATOR_EMAIL_SUBJECT_PARAMETER_NAME + " is not setup or is empty", StringUtils.isNotBlank(subject));

        String[] documentTypes = parameterService.getParameterValuesAsString(CollectorStep.class, SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES).toArray(new String[] {});
        assertTrue("system parameter " + SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES + " is not setup or is empty", documentTypes.length > 0);
    }

    /**
     * Verifies an error is added when the batch header is a duplicate (a batch loaded previously had the same header).
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testPerformValidate_duplicateHeader() throws Exception {

    }

    /**
     * Verifies an error is added when the batch entries contain multiple document types. Note: Actual test values here do not have
     * to be valid document types, only need to be different.
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testPerformValidation_mixedDocumentTypes() throws Exception {
        MockCollectorBatch collectorBatch = new MockCollectorBatch();
        populateMockCollectorBatch(collectorBatch);
        OriginEntryFull entry1 = new OriginEntryFull();
        entry1.setFinancialDocumentTypeCode("foo1");
        collectorBatch.addOriginEntry(entry1);

        OriginEntryFull entry2 = new OriginEntryFull();
        entry2.setFinancialDocumentTypeCode("foo2");
        collectorBatch.addOriginEntry(entry2);

        boolean isValid = collectorHelperService.performValidation(collectorBatch);
        assertFalse("returned batch was valid but there were multiple document types", isValid);
        assertTrue("error message for mixed document types does not exist", GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.Collector.MIXED_DOCUMENT_TYPES));
    }

    protected void populateMockCollectorBatch(MockCollectorBatch mockCollectorBatch) {
        mockCollectorBatch.setChartOfAccountsCode("BL");
        mockCollectorBatch.setOrganizationCode("ACAC");
        mockCollectorBatch.setCampusCode("BL");
        mockCollectorBatch.setPhoneNumber("555-555-5555");
        mockCollectorBatch.setMailingAddress("Somewhere");
        mockCollectorBatch.setDepartmentName("Some Dept");
    }
    /**
     * Verifies an error is added when a collector detail key does not have a matching gl entry. Note: Actual test values do have to
     * be valid, only need to be different from the gl record to the detail
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testPerformValidation_unmatchedDetailKey() throws Exception {
        MockCollectorBatch collectorBatch = new MockCollectorBatch();
        populateMockCollectorBatch(collectorBatch);
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
        assertTrue("error message for unmatched detail key does not exist", GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.Collector.NONMATCHING_DETAIL_KEY));
    }

    /**
     * Verifies an error is added when the batch entries contain multiple balance types. Note: Actual test values here do not have
     * to be valid balance types, only need to be different.
     */
    // @RelatesTo(RelatesTo.JiraIssue.KULUT31)
    public final void testPerformValidation_mixedBalanceTypes() throws Exception {
        MockCollectorBatch collectorBatch = new MockCollectorBatch();
        populateMockCollectorBatch(collectorBatch);
        OriginEntryFull entry1 = new OriginEntryFull();
        entry1.setFinancialBalanceTypeCode("AC");
        collectorBatch.addOriginEntry(entry1);

        OriginEntryFull entry2 = new OriginEntryFull();
        entry2.setFinancialBalanceTypeCode("IE");
        collectorBatch.addOriginEntry(entry2);

        boolean isValid = collectorHelperService.performValidation(collectorBatch);
        assertFalse("returned batch was valid but there were multiple balance types", isValid);
        assertTrue("error message for mixed balance types does not exist", GlobalVariables.getMessageMap().containsMessageKey(KFSKeyConstants.Collector.MIXED_BALANCE_TYPES));
    }
}
