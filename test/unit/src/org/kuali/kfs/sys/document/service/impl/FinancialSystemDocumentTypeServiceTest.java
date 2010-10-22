/*
 * Copyright 2009 The Kuali Foundation
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
package org.kuali.kfs.sys.document.service.impl;

import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.service.FinancialSystemDocumentTypeService;

@ConfigureContext
public class FinancialSystemDocumentTypeServiceTest extends KualiTestBase {
    private FinancialSystemDocumentTypeService financialSystemDocumentTypeService;
    
    protected static final String LEDGER_POSTING_DOC_TYPE_TRANS_1 = "DV";
    protected static final String LEDGER_POSTING_DOC_TYPE_TRANS_2 = "JV";
    protected static final String LEDGER_POSTING_DOC_TYPE_TRANS_3 = "SB";
//    protected static final String LEDGER_POSTING_DOC_TYPE_MAINT_1 = "AA";
//    protected static final String LEDGER_POSTING_DOC_TYPE_MAINT_2 = "ARG";
//    protected static final String NON_LEDGER_POSTING_DOC_TYPE_TRANS_1 = "EC";
//    protected static final String NON_LEDGER_POSTING_DOC_TYPE_TRANS_2 = "BC";
    protected static final String NON_LEDGER_POSTING_DOC_TYPE_MAINT_1 = "BANK";

    /**
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        financialSystemDocumentTypeService = SpringContext.getBean(FinancialSystemDocumentTypeService.class);
    }

    /**
     * Tests the isCurrentActiveAccountingDocumentType(String) method returns true for valid ledger posting doc types
     * 
     * @throws Exception
     */
    public void testIsCurrentActiveAccountingDocumentType_validLedgerPosting() throws Exception {
       assertTrue(LEDGER_POSTING_DOC_TYPE_TRANS_1 + " is not considered accounting document but should be", financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(LEDGER_POSTING_DOC_TYPE_TRANS_1));
       assertTrue(LEDGER_POSTING_DOC_TYPE_TRANS_2 + " is not considered accounting document but should be", financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(LEDGER_POSTING_DOC_TYPE_TRANS_2));       
       assertTrue(LEDGER_POSTING_DOC_TYPE_TRANS_3 + " is not considered accounting document but should be", financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(LEDGER_POSTING_DOC_TYPE_TRANS_3));      
//       assertTrue(LEDGER_POSTING_DOC_TYPE_MAINT_1 + " is not considered accounting document but should be", financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(LEDGER_POSTING_DOC_TYPE_MAINT_1));
//       assertTrue(LEDGER_POSTING_DOC_TYPE_MAINT_2 + " is not considered accounting document but should be", financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(LEDGER_POSTING_DOC_TYPE_MAINT_2));
    }
    
    /**
     * Tests the isCurrentActiveAccountingDocumentType(String) method returns false for non ledger posting doc types
     * 
     * @throws Exception
     */
    public void testIsCurrentActiveAccountingDocumentType_inValidLedgerPosting() throws Exception {
//       assertFalse(NON_LEDGER_POSTING_DOC_TYPE_TRANS_1 + " is considered accounting document but should not be", financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(NON_LEDGER_POSTING_DOC_TYPE_TRANS_1));
//       assertFalse(NON_LEDGER_POSTING_DOC_TYPE_TRANS_2 + " is considered accounting document but should not be", financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(NON_LEDGER_POSTING_DOC_TYPE_TRANS_2));
       assertFalse(NON_LEDGER_POSTING_DOC_TYPE_MAINT_1 + " is considered accounting document but should not be", financialSystemDocumentTypeService.isCurrentActiveAccountingDocumentType(NON_LEDGER_POSTING_DOC_TYPE_MAINT_1));
    }

}
