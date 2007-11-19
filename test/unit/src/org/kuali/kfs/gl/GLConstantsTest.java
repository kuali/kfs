/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.gl;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.test.ConfigureContext;

/**
 * A test to cover GLConstants
 */
@ConfigureContext
public class GLConstantsTest extends KualiTestBase {
    /**
     * Tests that none of the space constants in GLConstants return null.
     */
    public void testDDSpaceConstants() {
        assertNotNull(GLConstants.getSpaceDebitCreditCode());
        assertNotNull(GLConstants.getSpaceUniversityFiscalPeriodCode());
        assertNotNull(GLConstants.getSpaceBalanceTypeCode());
        assertNotNull(GLConstants.getSpaceFinancialSystemOriginationCode());
        assertNotNull(GLConstants.getSpaceFinancialObjectCode());
        assertNotNull(GLConstants.getSpaceTransactionDate());
        assertNotNull(GLConstants.getSpaceUniversityFiscalYear());
        assertNotNull(GLConstants.getSpaceTransactionEntrySequenceNumber());
        assertNotNull(GLConstants.getSpaceTransactionLedgetEntryDescription());
        assertNotNull(GLConstants.getSpaceSubAccountTypeCode());
    }
}
