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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSConstants.ParameterGroups;
import org.kuali.kfs.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.service.MockCollectorBatch;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.gl.bo.InterDepartmentalBilling;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.test.KualiTestBase;
import org.kuali.test.WithTestSpringContext;

/**
 * Test the CollectorService.
 */
@WithTestSpringContext
public class CollectorServiceTest extends KualiTestBase {
    private KualiConfigurationService configurationService;
    private CollectorService collectorService;

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        configurationService = SpringServiceLocator.getKualiConfigurationService();
        collectorService = SpringServiceLocator.getCollectorService();
    }

    /**
     * Verifies system parameters needed to send the collector email exists.
     */
    public final void testEmailSystemParametersExist() throws Exception {
        String subject = configurationService.getApplicationParameterValue(ParameterGroups.COLLECTOR_SECURITY_GROUP_NAME, SystemGroupParameterNames.COLLECTOR_EMAIL_SUBJECT_PARAMETER_NAME);
        assertTrue("system parameter " + SystemGroupParameterNames.COLLECTOR_EMAIL_SUBJECT_PARAMETER_NAME + " is not setup or is empty", StringUtils.isNotBlank(subject));
        
        String[] documentTypes = configurationService.getApplicationParameterValues(ParameterGroups.COLLECTOR_SECURITY_GROUP_NAME, SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES);
        assertTrue("system parameter " + SystemGroupParameterNames.COLLECTOR_EQUAL_DC_TOTAL_DOCUMENT_TYPES + " is not setup or is empty", documentTypes.length > 0);
    }

    /**
     * Verifies an error is added when the batch header is a duplicate (a batch loaded previously had the same header).
     */
    public final void testPerformValidate_duplicateHeader() throws Exception {

    }

    /**
     * Verifies an error is added when the batch entries contain multiple document types. Note: Actual test values here do not have
     * to be valid document types, only need to be different.
     */
    public final void testPerformValidation_mixedDocumentTypes() throws Exception {
        MockCollectorBatch collectorBatch = new MockCollectorBatch();

        OriginEntry entry1 = new OriginEntry();
        entry1.setFinancialDocumentTypeCode("foo1");
        collectorBatch.addOriginEntry(entry1);

        OriginEntry entry2 = new OriginEntry();
        entry2.setFinancialDocumentTypeCode("foo2");
        collectorBatch.addOriginEntry(entry2);

        boolean isValid = collectorService.performValidation(collectorBatch);
        assertFalse("returned batch was valid but there were multiple document types", isValid);
        assertTrue("error message for mixed document types does not exist", GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.Collector.MIXED_DOCUMENT_TYPES));
    }

    /**
     * Verifies an error is added when a detail (id billing) key does not have a matching gl entry. Note: Actual test values do have
     * to be valid, only need to be different from the gl record to the detail
     */
    public final void testPerformValidation_unmatchedDetialKey() throws Exception {
        MockCollectorBatch collectorBatch = new MockCollectorBatch();

        OriginEntry entry = new OriginEntry();
        entry.setChartOfAccountsCode("BA");
        entry.setAccountNumber("1912610");
        collectorBatch.addOriginEntry(entry);

        InterDepartmentalBilling idBilling = new InterDepartmentalBilling();
        idBilling.setChartOfAccountsCode("UA");
        idBilling.setAccountNumber("1912660");
        collectorBatch.addIDBilling(idBilling);

        boolean isValid = collectorService.performValidation(collectorBatch);
        assertFalse("returned batch was valid but there was detail record without a matching gl entry", isValid);
        assertTrue("error message for unmatched detail key does not exist", GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.Collector.NONMATCHING_DETAIL_KEY));
    }
    
    /**
     * Verifies an error is added when the batch entries contain multiple balance types. Note: Actual test values here do not have
     * to be valid balance types, only need to be different.
     */
    public final void testPerformValidation_mixedBalanceTypes() throws Exception {
        MockCollectorBatch collectorBatch = new MockCollectorBatch();

        OriginEntry entry1 = new OriginEntry();
        entry1.setFinancialBalanceTypeCode("AC");
        collectorBatch.addOriginEntry(entry1);

        OriginEntry entry2 = new OriginEntry();
        entry2.setFinancialBalanceTypeCode("IE");
        collectorBatch.addOriginEntry(entry2);

        boolean isValid = collectorService.performValidation(collectorBatch);
        assertFalse("returned batch was valid but there were multiple balance types", isValid);
        assertTrue("error message for mixed balance types does not exist", GlobalVariables.getErrorMap().containsMessageKey(KFSKeyConstants.Collector.MIXED_BALANCE_TYPES));
    }
}
