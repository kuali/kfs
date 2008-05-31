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

import org.apache.commons.lang.StringUtils;
import org.kuali.core.service.DateTimeService;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
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
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
        java.sql.Date testDate = dateTimeService.getCurrentSqlDate();
        assertTrue(this.rule.validatePostedDate(testDate));
                
        //Validating a posible not existing date.
        Calendar cal= dateTimeService.getCurrentCalendar();
        cal.roll(Calendar.YEAR, 500); //adding 500 years       
        testDate =  new java.sql.Date(cal.getTime().getTime());        
        assertFalse(this.rule.validatePostedDate(testDate));
    }
        
    /**
     * 
     * Validates the existance of a valid fiscal year and fiscal month in the university date table
     * 
     */
    public void testValidateFiscalPeriod() {
        Calendar cal= SpringContext.getBean(DateTimeService.class).getCurrentCalendar();
        String sMonth=StringUtils.leftPad((new Integer(cal.get(Calendar.MONTH)+1)).toString(), 2, "0");

        assertTrue(rule.validateFiscalPeriod(cal.get(Calendar.YEAR),sMonth));

        cal.roll(Calendar.YEAR, 500); //adding 500 years
        assertFalse(rule.validateFiscalPeriod(cal.get(Calendar.YEAR),sMonth));
    }
        
    public void testValidateDocumentType() {
        assertTrue(rule.validateDocumentType("MPAY")); // it exists
        assertFalse(rule.validateDocumentType("XXXX")); // it doesnt
        
    }
}
