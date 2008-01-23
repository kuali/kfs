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

import org.kuali.core.maintenance.MaintenanceRuleTestBase;
import org.kuali.module.ar.bo.SystemInformation;
import org.kuali.test.ConfigureContext;

/*
* This class tests the business rules for the SystemInformation Maint. Doc.
*/
@ConfigureContext(session = KHUNTLEY)
public class SystemInformationRuleTest extends MaintenanceRuleTestBase {

    SystemInformation systemInformation;
    
    private static Integer UNIVERSITY_FISCAL_YEAR = new Integer(2008);
    private static String EXPENSE_OBJECT_CODE = "1958";
    private static String INCOME_OBJECT_CODE = "1999";
    private static String CHART_CODE = "BA";
    private static String ORGANIZATION_CODE = "ACPR";
   
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        systemInformation = new SystemInformation();
        systemInformation.setUniversityFiscalYear(UNIVERSITY_FISCAL_YEAR);
        systemInformation.setProcessingChartOfAccountCode(CHART_CODE);
        systemInformation.setProcessingOrganizationCode(ORGANIZATION_CODE);
    }
    
    /**
     * This method tests if the checkRefundObjectValidCode rule returns true when refundFinancialObjectCode is set to an expense object code
     */
    public void testCheckRefundObjectValidCode_True(){
        
        systemInformation.setRefundFinancialObjectCode(EXPENSE_OBJECT_CODE);
        systemInformation.refreshReferenceObject("refundFinancialObject");
        SystemInformationRule rule = (SystemInformationRule) setupMaintDocRule(newMaintDoc(systemInformation), SystemInformationRule.class);
        
        boolean result = rule.checkRefundObjectValidCode(systemInformation);
        assertEquals( "When refund object code is " + EXPENSE_OBJECT_CODE + ", checkRefundObjectValidCode should return true. ", true, result );
    }
    
    /**
     * This method tests if the checkRefundObjectValidCode rule returns false when refundFinancialObjectCode is set to an income object code
     */
    public void testCheckRefundObjectValidCode_False(){
        systemInformation.setRefundFinancialObjectCode(INCOME_OBJECT_CODE);
        systemInformation.refreshReferenceObject("refundFinancialObject");
        SystemInformationRule rule = (SystemInformationRule) setupMaintDocRule(newMaintDoc(systemInformation), SystemInformationRule.class);
        
        boolean result = rule.checkRefundObjectValidCode(systemInformation);
        assertEquals( "When refund object code is " + INCOME_OBJECT_CODE + ", checkRefundObjectValidCode should return false. ", false, result );
        
    }
    
    /**
     * This method tests if the checkSalesTaxObjectValidCode rule returns true when salesTaxFinancialObjectCode is set to an income object code
     */

    public void testCheckSalesTaxObjectValidCode_True(){
        systemInformation.setSalesTaxFinancialObjectCode(INCOME_OBJECT_CODE);
        systemInformation.refreshReferenceObject("salesTaxFinancialObject");
        SystemInformationRule rule = (SystemInformationRule) setupMaintDocRule(newMaintDoc(systemInformation), SystemInformationRule.class);
        
        boolean result = rule.checkSalesTaxObjectValidCode(systemInformation);
        assertEquals( "When sales tax object code is " + INCOME_OBJECT_CODE + ", checkSalesTaxObjectValidCode should return true. ", true, result );        
    }

    /**
     * This method tests if the checkSalesTaxObjectValidCode rule returns false when salesTaxFinancialObjectCode is set to an expense object code
     */

    public void testCheckSalesTaxObjectValidCode_False(){
        systemInformation.setSalesTaxFinancialObjectCode(EXPENSE_OBJECT_CODE);
        systemInformation.refreshReferenceObject("salesTaxFinancialObject");
        SystemInformationRule rule = (SystemInformationRule) setupMaintDocRule(newMaintDoc(systemInformation), SystemInformationRule.class);
        
        boolean result = rule.checkSalesTaxObjectValidCode(systemInformation);
        assertEquals( "When sales tax object code is " + EXPENSE_OBJECT_CODE + ", checkSalesTaxObjectValidCode should return false. ", false, result );  
        
    }
}
