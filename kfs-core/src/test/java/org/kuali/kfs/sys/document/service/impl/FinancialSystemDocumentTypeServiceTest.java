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
