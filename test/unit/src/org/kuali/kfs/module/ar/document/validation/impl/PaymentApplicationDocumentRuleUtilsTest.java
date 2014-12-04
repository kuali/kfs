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
package org.kuali.kfs.module.ar.document.validation.impl;

import org.kuali.kfs.module.ar.businessobject.NonInvoiced;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

@ConfigureContext
public class PaymentApplicationDocumentRuleUtilsTest extends KualiTestBase {

    private static final String NON_AR_CHART_CD = "BL";
    private static final String NON_AR_ACCOUNT_NBR = "1031420";
    private static final String NON_AR_OBJ_CD = "1500";
    private static final String NON_AR_ACCOUNT_NBR_BAD = "999999999999999999";
    
    public void testNonArLineOverFundsInvoice() {
        
        //  build the nonArLine
        NonInvoiced nonArLine = new NonInvoiced();
        nonArLine.setChartOfAccountsCode(NON_AR_CHART_CD);
        nonArLine.setAccountNumber(NON_AR_ACCOUNT_NBR);
        nonArLine.setFinancialObjectCode(NON_AR_OBJ_CD);
        nonArLine.setFinancialDocumentLineAmount(new KualiDecimal("500.00"));
        
        boolean result = true;
        
        // These cases are tested in PaymentApplicationDocumentTest
        
//        //  test correctly funding from non-AR (to make sure it doesnt false-positive fail)
//        result = PaymentApplicationDocumentRuleUtil.validateNonInvoiced(nonArLine, new KualiDecimal("750.00"));
//        assertTrue("Rule should pass.", result);
//        
//        //  test overfunding from non-AR
//        result = PaymentApplicationDocumentRuleUtil.validateNonInvoiced(nonArLine, new KualiDecimal("250.00"));
//        assertFalse("Rule should fail, since nonArLine is overfunded.", result);
//        
//        //  test not allowing zero
//        nonArLine.setFinancialDocumentLineAmount(new KualiDecimal("0.00"));
//        result = PaymentApplicationDocumentRuleUtil.validateNonInvoiced(nonArLine, new KualiDecimal("500.00"));
//        assertFalse("Rules should fail, since nonArLine total is zero.", result);
//        
//        //  test failing validateBO test
//        nonArLine.setAccountNumber(NON_AR_ACCOUNT_NBR_BAD);
//        nonArLine.setFinancialDocumentLineAmount(new KualiDecimal("500.00"));
//        result = PaymentApplicationDocumentRuleUtil.validateNonInvoiced(nonArLine, new KualiDecimal("500.00"));
//        assertFalse("Rule should fail as nonsense account number is used.", result);
        
    }
    
}
