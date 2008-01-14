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
import org.kuali.module.ar.bo.OrganizationAccountingDefault;
import org.kuali.module.chart.rules.AccountRule;
import org.kuali.test.ConfigureContext;

/**
 * This class tests the business rules for the OrganizationAccountingDefault Maint. Doc.
 */
@ConfigureContext(session = KHUNTLEY)
public class OrganizationAccountingDefaultRuleTest extends MaintenanceRuleTestBase {
    
    OrganizationAccountingDefault organizationAccountingDefault;
    
    private static Integer UNIVERSITY_FISCAL_YEAR = new Integer(2007);
    private static String EXPENSE_OBJECT_CODE = "EX";
    private static String INCOME_OBJECT_CODE = "IN";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        organizationAccountingDefault = new OrganizationAccountingDefault();
        organizationAccountingDefault.setUniversityFiscalYear(UNIVERSITY_FISCAL_YEAR);
    }
    
    /**
     * This method tests if the isWriteOffObjectValidExpense rule returns true when write off object is set to an expense object code
     */
    public void testIsWriteOffObjectValidExpense_True(){
        
        organizationAccountingDefault.setWriteoffObjectCode(EXPENSE_OBJECT_CODE);
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isWriteOffObjectValidExpense(organizationAccountingDefault);
        assertEquals( "When write off object code is " + EXPENSE_OBJECT_CODE + ", isWriteOffObjectValidExpense should return true. ", true, result );
    }
    
    /**
     * This method tests if the isWriteOffObjectValidExpense rule returns false when write off object is set to an income object code
     */
    public void testIsWriteOffObjectValidExpense_False(){
        organizationAccountingDefault.setWriteoffObjectCode(INCOME_OBJECT_CODE);
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isWriteOffObjectValidExpense(organizationAccountingDefault);
        assertEquals( "When write off object code is " + INCOME_OBJECT_CODE + ", isWriteOffObjectValidExpense should return false. ", false, result );
        
    }
    
    /**
     * This method tests if the isLateChargeObjectValidIncome rule returns true when late charge object is set to an income object code
     */
    public void testIsLateChargeObjectValidIncome_True(){
        organizationAccountingDefault.setOrganizationLateChargeObjectCode(INCOME_OBJECT_CODE);
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isLateChargeObjectValidIncome(organizationAccountingDefault);
        assertEquals( "When late charge object code is " + INCOME_OBJECT_CODE + ", isLateChargeObjectValidIncome should return true. ", true, result );        
    }

    /**
     * This method tests if the isLateChargeObjectValidIncome rule returns false when late charge object is set to an expense object code
     */
    public void testIsLateChargeObjectValidIncome_False(){
        organizationAccountingDefault.setOrganizationLateChargeObjectCode(EXPENSE_OBJECT_CODE);
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isLateChargeObjectValidIncome(organizationAccountingDefault);
        assertEquals( "When late charge object code is " + EXPENSE_OBJECT_CODE + ", isLateChargeObjectValidIncome should return false. ", false, result );  
        
    }
    
    /**
     * This method tests if the isDefaultInvoiceFinancialObjectValidIncome rule returns true when default invoice financial object is set to an income object code
     */
    public void testIsDefaultInvoiceFinancialObjectValidIncome_True(){
        organizationAccountingDefault.setDefaultInvoiceFinancialObjectCode(INCOME_OBJECT_CODE);
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isDefaultInvoiceFinancialObjectValidIncome(organizationAccountingDefault);
        assertEquals( "When default invoice financial  is " + INCOME_OBJECT_CODE + ", isDefaultInvoiceFinancialObjectValidIncome should return true. ", true, result );        
        
    }
    
    /**
     * This method tests if the isDefaultInvoiceFinancialObjectValidIncome rule returns false when default invoice financial object is set to an expense object code
     */
    public void testIsDefaultInvoiceFinancialObjectValidIncome_False(){
        organizationAccountingDefault.setDefaultInvoiceFinancialObjectCode(EXPENSE_OBJECT_CODE);
        OrganizationAccountingDefaultRule rule = (OrganizationAccountingDefaultRule) setupMaintDocRule(newMaintDoc(organizationAccountingDefault), OrganizationAccountingDefaultRule.class);
        
        boolean result = rule.isDefaultInvoiceFinancialObjectValidIncome(organizationAccountingDefault);
        assertEquals( "When default invoice financial object code is " + EXPENSE_OBJECT_CODE + ", isDefaultInvoiceFinancialObjectValidIncome should return false. ", false, result );  
    }

}
