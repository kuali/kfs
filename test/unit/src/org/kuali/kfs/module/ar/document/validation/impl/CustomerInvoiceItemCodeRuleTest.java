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

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.math.BigDecimal;

import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceItemCode;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.document.validation.MaintenanceRuleTestBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceItemCodeRuleTest extends MaintenanceRuleTestBase {

    CustomerInvoiceItemCode customerInvoiceItemCode;
    
    private static String CHART_CODE = "BA";
    private static String ORGANIZATION_CODE = "ACAC";
    private static String INVOICE_ITEM_CODE = "TRE";
    private static String INVOICE_ITEM_DESCRIPTION = "TEST";
    private static String POSITIVE_VALUE = "1";
    private static String NON_POSITIVE_VALUE = "0";
    private static boolean ACTIVE_INDEX = true;
    private static String INCOME_OBJECT_CODE = "0110"; // BALANCE FORWARD has type INCOME
    private static String EXPENSE_OBJECT_CODE = "2000"; // ACADEMIC SALARY  has type EXPENSE
   
    @Override
    protected void setUp() throws Exception {
    
        super.setUp();
        customerInvoiceItemCode = new CustomerInvoiceItemCode();
        customerInvoiceItemCode.setChartOfAccountsCode(CHART_CODE);
        customerInvoiceItemCode.setOrganizationCode(ORGANIZATION_CODE);
        customerInvoiceItemCode.setInvoiceItemCode(INVOICE_ITEM_CODE);
        customerInvoiceItemCode.setInvoiceItemDescription(INVOICE_ITEM_DESCRIPTION);
        customerInvoiceItemCode.setActive(ACTIVE_INDEX);
    }
    /**
     * This method tests if the isCustomerInvoiceItemCodeObjectValid rule returns true when default invoice financial object is set to an income object code
     */
    public void testIsCustomerInvoiceItemCodeObjectValid_True(){

        customerInvoiceItemCode.setDefaultInvoiceFinancialObjectCode(INCOME_OBJECT_CODE);
        customerInvoiceItemCode.setDefaultInvoiceChartOfAccountsCode(CHART_CODE);
        customerInvoiceItemCode.refreshReferenceObject("defaultInvoiceFinancialObject");
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);

        boolean result = rule.isCustomerInvoiceItemCodeObjectValid(customerInvoiceItemCode);
        assertEquals( "When default invoice financial  is " + INCOME_OBJECT_CODE + ", isDefaultInvoiceFinancialObjectValidIncome should return true. ", true, result );

    }
    /**
     * This method tests if the isCustomerInvoiceItemCodeObjectValid rule returns true when default invoice financial object is set to an income object code
     */
    public void testIsCustomerInvoiceItemCodeObjectValid_False(){

        customerInvoiceItemCode.setDefaultInvoiceFinancialObjectCode(EXPENSE_OBJECT_CODE);
        customerInvoiceItemCode.setDefaultInvoiceChartOfAccountsCode(CHART_CODE);
        customerInvoiceItemCode.refreshReferenceObject("defaultInvoiceFinancialObject");
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);

        boolean result = rule.isCustomerInvoiceItemCodeObjectValid(customerInvoiceItemCode);
        assertEquals( "When default invoice financial  is " + EXPENSE_OBJECT_CODE + ", isDefaultInvoiceFinancialObjectValidIncome should return false. ", false, result );

    }
    /**
     * This method tests if the validateItemDefaultPrice rule returns true when itemDefaultPrice is positive.
     */
    public void testValidateItemDefaultPrice_True(){
        customerInvoiceItemCode.setItemDefaultPrice(new KualiDecimal(POSITIVE_VALUE));
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);
        
        boolean result = rule.validateItemDefaultPrice(customerInvoiceItemCode);
        assertEquals( "When item default price is " + POSITIVE_VALUE + ", validateItemDefaultPrice should return true. ", true, result );
    }
    
    /**
     * This method tests if the validateItemDefaultPrice rule returns false when itemDefaultPrice is not positive.
     */
    public void testValidateItemDefaultPrice_False(){  
        customerInvoiceItemCode.setItemDefaultPrice(new KualiDecimal(NON_POSITIVE_VALUE));
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);
        
        boolean result = rule.validateItemDefaultPrice(customerInvoiceItemCode);
        assertEquals( "When item default price is " + NON_POSITIVE_VALUE + ", validateItemDefaultPrice should return false. ", false, result );
        
    }
    
    /**
     * This method tests if the validItemDefaultQuantity rule returns true when itemDefaultQuantity is positive
     */

    public void testValidItemDefaultQuantity_True(){
        customerInvoiceItemCode.setItemDefaultQuantity(new BigDecimal(POSITIVE_VALUE));
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);
        
        boolean result = rule.validateItemDefaultQuantity(customerInvoiceItemCode);
        assertEquals( "When item default quantity is " + POSITIVE_VALUE + ", validItemDefaultQuantity should return true. ", true, result );        
    }

    /**
     * This method tests if the validItemDefaultQuantity rule returns false when itemDefaultQuantity is not positive
     */

    public void testValidItemDefaultQuantity_False(){
        customerInvoiceItemCode.setItemDefaultQuantity(new BigDecimal(NON_POSITIVE_VALUE));
        CustomerInvoiceItemCodeRule rule = (CustomerInvoiceItemCodeRule) setupMaintDocRule(newMaintDoc(customerInvoiceItemCode), CustomerInvoiceItemCodeRule.class);
        
        boolean result = rule.validateItemDefaultQuantity(customerInvoiceItemCode);
        assertEquals( "When item default quantity is " + NON_POSITIVE_VALUE + ", validateItemDefaultQuantity should return false. ", false, result );  
        
    }
    


}


