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
package org.kuali.module.cams.rules;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.util.Calendar;

import org.kuali.kfs.context.KualiTestBase;
import org.kuali.module.purap.document.PaymentRequestDocument;
import org.kuali.module.purap.rules.PaymentRequestDocumentRule;
import org.kuali.test.ConfigureContext;

@ConfigureContext(session = KHUNTLEY)
public class AssetPaymentDocumentRuleTest extends KualiTestBase {
    AssetPaymentDocumentRule rule;
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        rule = new AssetPaymentDocumentRule();
    }

    @Override
    protected void tearDown() throws Exception {
        rule = null;
        super.tearDown();
    }

    
    /**
     * 
     * Testing the method that checks that the posted date exists in the university date table
     */
    public void testValidatePostedDate() {
        java.sql.Date testDate;
        Calendar cal= Calendar.getInstance();
        
        testDate =  new java.sql.Date(cal.getTime().getTime() );        
        assertTrue(this.rule.validatePostedDate(testDate));
        
        
        cal.set(2010, 1, 1);        
        testDate =  new java.sql.Date(cal.getTime().getTime() );        
        assertFalse(this.rule.validatePostedDate(testDate));
    }
        
    /**
     * 
     * Validates the existance of a valid fiscal year and fiscal month in the university date table
     * 
     */
    public void testValidateFiscalPeriod() {        
        assertTrue(rule.validateFiscalPeriod(2008,"01"));
        assertFalse(rule.validateFiscalPeriod(2999,"01"));
    }
    
    public void testValidateAssetEligibilityForPayment() {
        assertTrue(rule.validateAssetEligibilityForPayment(new Long("376601")));
        //assertFalse(rule.validateAssetEligibilityForPayment(new Long("392212")));
    }
    
    
    public void testValidateDocumentType() {
        assertTrue(rule.validateDocumentType("MPAY")); // it exists
        assertFalse(rule.validateDocumentType("XXXX")); // it doesnt
        
    }
}
