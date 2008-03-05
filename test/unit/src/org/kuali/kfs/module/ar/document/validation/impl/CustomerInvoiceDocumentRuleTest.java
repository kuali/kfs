/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.module.ar.rules;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.ar.bo.CustomerInvoiceDetail;
import org.kuali.module.ar.document.CustomerInvoiceDocument;
import org.kuali.test.ConfigureContext;

/**
 * This class tests the rules in CustomerInvoiceDocumentRule
 */
@ConfigureContext(session = KHUNTLEY)
public class CustomerInvoiceDocumentRuleTest extends KualiTestBase {

    private CustomerInvoiceDocumentRule rule;
    private CustomerInvoiceDocument document;
    
    private final String INVALID_CHART_OF_ACCOUNTS_CODE = "XX";
    private final String VALID_CHART_OF_ACCOUNTS_CODE = "UA";
    private final String INVALID_ORGANIZATION_CODE = "XXXX";
    private final String VALID_ORGANIZATION_CODE = "VPIT";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new CustomerInvoiceDocumentRule();
        document = new CustomerInvoiceDocument();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        document = null;
        super.tearDown();
    }
    
    /**
     * This method tests if isValidAndActiveCustomer rule returns true when passed an active and valid customer
     */
    public void testIsValidAndActiveCustomer_True() {
        //TODO Need to check if customer or vendor is going to be used.
        assertTrue(true);
    }
    
    /**
     * This method tests if isValidAndActiveCustomer rule returns false when passed an invalid customer
     */
    public void testIsValidAndActiveCustomer_False_InactiveCustomer() {
        //TODO Need to check if customer or vendor is going to be used.        
        assertFalse(false);
    }
    
    
    /**
     * This method tests if isValidAndActiveCustomer rule returns false when passed an invalid customer
     */
    public void testIsValidAndActiveCustomer_False_InvalidCustomer() {
        //TODO Need to check if customer or vendor is going to be used.        
        assertFalse(false);
    }        
    
    
    /**
     * This method tests if isValidBilledByChartOfAccountsCode rule returns true when passed a valid chart of accounts code 
     */
    public void testIsValidBilledByChartOfAccountsCode_True() {
        document.setBillByChartOfAccountCode(VALID_CHART_OF_ACCOUNTS_CODE);
        assertTrue(rule.isValidBilledByChartOfAccountsCode(document));
    }
    
    /**
     * This method tests if isValidBilledByChartOfAccountsCode rule returns false when passed an invalid chart of accounts code 
     */    
    public void testIsValidBilledByChartOfAccountsCode_False() {
        document.setBillByChartOfAccountCode(INVALID_CHART_OF_ACCOUNTS_CODE);
        assertFalse(rule.isValidBilledByChartOfAccountsCode(document));     
    }    
    
    
    /**
     * This method tests if isValidBilledByOrganizationCode rule returns true when passed a valid organization code 
     */     
    public void testIsValidBilledByOrganizationCode_True() {
        document.setBillByChartOfAccountCode(VALID_CHART_OF_ACCOUNTS_CODE);
        document.setBilledByOrganizationCode(VALID_ORGANIZATION_CODE);
        assertTrue(rule.isValidBilledByOrganizationCode(document));     
    }
    
    /**
     * This method tests if isValidBilledByOrganizationCode rule returns false when passed an invalid organization code 
     */    
    public void testIsValidBilledByOrganizationCode_False() {
        document.setBilledByOrganizationCode(INVALID_ORGANIZATION_CODE);
        assertFalse(rule.isValidBilledByOrganizationCode(document));     
    }
    
    
    /**
     * This method tests if isValidInvoiceDueDate will return true when passed a valid invoice due date (i.e a due date equal to or less 30 days from the creation date)
     */
    public void testIsValidInvoiceDueDate_True() {
        
        //TODO Write this test
        assertTrue(true);
    }
    
    /**
     * This method tests if isValidInvoiceDueDate will return false when passed a invalid invoice due date (i.e a due date 30 days greater than the creation date)
     */    
    public void testIsValidInvoiceDueDate_False() {
        
        //TODO Write this test
        assertFalse(false);
    }

    /**
     * This method tests if the isCustomerInvoiceDetailItemUnitPriceGreaterThanZero returns true when the unit price is set to 1
     */    
    public void testIsCustomerInvoiceDetailItemUnitPriceGreaterThanZero_True() {
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setInvoiceItemUnitPrice(new KualiDecimal(1));
        assertTrue( rule.isCustomerInvoiceDetailItemUnitPriceGreaterThanZero((customerInvoiceDetail)));    
    }
    
    /**
     * This method tests if the isCustomerInvoiceDetailItemUnitPriceGreaterThanZero returns false when the unit price is set to 0
     */    
    
    public void testIsCustomerInvoiceDetailItemUnitPriceGreaterThanZero_False() {
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setInvoiceItemUnitPrice(KualiDecimal.ZERO);
        assertFalse( rule.isCustomerInvoiceDetailItemUnitPriceGreaterThanZero((customerInvoiceDetail)));       
    }
    
    /**
     * This method tests if the isCustomerInvoiceDetailItemQuantityGreaterThanZero returns true when the quantity is set to 1
     */    
    public void testIsCustomerInvoiceDetailItemQuantityGreaterThanZero_True() {
        
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setInvoiceItemQuantity(BigDecimal.ONE);
        assertTrue( rule.isCustomerInvoiceDetailItemQuantityGreaterThanZero(customerInvoiceDetail) );        
    }
    
    /**
     * This method tests if the isCustomerInvoiceDetailItemQuantityGreaterThanZero returns false when the quantity is set to 0
     */
    public void testIsCustomerInvoiceDetailItemQuantityGreaterThanZero_False() {
        
        CustomerInvoiceDetail customerInvoiceDetail = new CustomerInvoiceDetail();
        customerInvoiceDetail.setInvoiceItemQuantity(BigDecimal.ZERO);
        assertFalse( rule.isCustomerInvoiceDetailItemQuantityGreaterThanZero(customerInvoiceDetail) );
    }
    
    /**
     * This method tests if the isValidUnitOfMeasure method returns true when passed in a valid unit of measure
     */
    public void isValidUnitOfMeasure_True() {
        //TODO need to see if isValidUnitOfMeasure method is even needed
        assertTrue(true);        
    }
    
    
    /**
     * This method tests if the isValidUnitOfMeasure method returns false when passed in a valid unit of measure
     */
    public void isValidUnitOfMeasure_False() {
        //TODO need to see if isValidUnitOfMeasure method is even needed
        assertFalse(false);
    }
    
}
