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

import java.util.Calendar;
import java.util.Date;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.context.TestUtils;
import org.kuali.kfs.sys.service.ConfigurableDateService;
import org.kuali.kfs.sys.service.impl.ConfigurableDateTimeServiceImpl;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDueDateValidationTest extends KualiTestBase {
    
    private CustomerInvoiceDueDateValidation validation;
    private final static String MAXIMUM_NUMBER_OF_DAYS = "10";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDueDateValidation();
        validation.setCustomerInvoiceDocument(new CustomerInvoiceDocument());
        validation.setParameterService(SpringContext.getBean(ParameterService.class));
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }   
    
    public void testValidBillingDateNotBeforeDueDate_True(){
        
        ConfigurableDateService dateTimeService = new ConfigurableDateTimeServiceImpl();
        
        //get date right now as billing date
        Date billingDate = new Date();

        //get billing date plus one day as due date
        Calendar c = Calendar.getInstance();
        c.setTime(billingDate);
        c.add(Calendar.DATE, 1);
        Date dueDateOneDayAfterBillingDate = c.getTime();
        
        //set current date for date time service for use as billing date
        dateTimeService.setCurrentDate(billingDate);
        validation.setDateTimeService(dateTimeService);
        
        //set due date as one day plus billing date
        validation.getCustomerInvoiceDocument().setInvoiceDueDate(new java.sql.Date(dueDateOneDayAfterBillingDate.getTime()));
        
        assertTrue(validation.validate(null));
    }        
    
    public void testValidBillingDateNotBeforeDueDate_False(){
        
        ConfigurableDateService dateTimeService = new ConfigurableDateTimeServiceImpl();
        
        //get date right now as billing date
        Date billingDate = new Date();
        
        //get billing date minus one day as due date
        Calendar c = Calendar.getInstance();
        c.setTime(billingDate);
        c.add(Calendar.DATE, -1);
        Date dueDateOneDayBeforeBillingDate = c.getTime();

        //set current date for date time service for use as billing date
        dateTimeService.setCurrentDate(billingDate);
        validation.setDateTimeService(dateTimeService);
        
        //set due date as one day minus billing date
        validation.getCustomerInvoiceDocument().setInvoiceDueDate(new java.sql.Date(dueDateOneDayBeforeBillingDate.getTime()));
        
        //asset false because invoice due date cannot be before billing date
        assertFalse(validation.validate(null));
        
        //set due date same as billing date
        Date dueDateSameDateAsBillingDate = billingDate;
        validation.getCustomerInvoiceDocument().setInvoiceDueDate(new java.sql.Date(dueDateSameDateAsBillingDate.getTime()));
        
        //assert false because invoice due date cannot be the same as the billing date
        assertFalse(validation.validate(null));
    }
    
    public void testValidBillingDateNotAfterMaximumNumberOfDays_True(){
        
        ConfigurableDateService dateTimeService = new ConfigurableDateTimeServiceImpl();
        
        //get date right now as billing date
        Date billingDate = new Date();
        
        //get billing date minus one day as due date
        Calendar c = Calendar.getInstance();
        c.setTime(billingDate);
        c.add(Calendar.DATE, 9);
        Date dueDateNineDaysAfterBillingDate = c.getTime();

        //set current date for date time service for use as billing date
        dateTimeService.setCurrentDate(billingDate);
        validation.setDateTimeService(dateTimeService);
        
        //set due date as one day minus billing date
        validation.getCustomerInvoiceDocument().setInvoiceDueDate(new java.sql.Date(dueDateNineDaysAfterBillingDate.getTime()));
        
        //set parameter to 10 days
        TestUtils.setSystemParameter(CustomerInvoiceDocument.class, ArConstants.MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE, MAXIMUM_NUMBER_OF_DAYS);
        
        //asset false because invoice due date cannot be before billing date
        assertTrue(validation.validate(null));
    }    
    
    public void testValidBillingDateNotAfterMaximumNumberOfDays_False(){
        
        //set parameter to 10 days
        TestUtils.setSystemParameter(CustomerInvoiceDocument.class, ArConstants.MAXIMUM_NUMBER_OF_DAYS_AFTER_CURRENT_DATE_FOR_INVOICE_DUE_DATE, MAXIMUM_NUMBER_OF_DAYS);
        
        ConfigurableDateService dateTimeService = new ConfigurableDateTimeServiceImpl();
        
        //get date right now as billing date
        Date billingDate = new Date();
        
        // set due date 10 days after billing date
        Calendar c = Calendar.getInstance();
        c.setTime(billingDate);
        c.add(Calendar.DATE, 10);
        Date dueDateElevenDaysAfterBillingDate = c.getTime();

        //set current date for date time service for use as billing date
        dateTimeService.setCurrentDate(billingDate);
        validation.setDateTimeService(dateTimeService);
        
        //set due date 10 days after billing date
        validation.getCustomerInvoiceDocument().setInvoiceDueDate(new java.sql.Date(dueDateElevenDaysAfterBillingDate.getTime()));
        
        //asset false because invoice due date cannot be equal to (billing date + max num of days)
        assertFalse(validation.validate(null));
        
        // set due date 11 days after billing date
        c.setTime(billingDate);
        c.add(Calendar.DATE, 11);
        Date dueDateTenDaysAfterBillingDate = c.getTime();
        
        // set due date 11 days after billing date
        validation.getCustomerInvoiceDocument().setInvoiceDueDate(new java.sql.Date(dueDateTenDaysAfterBillingDate.getTime()));
                
        //asset false because invoice due date cannot be more than (billing date + max num of days)
        assertFalse(validation.validate(null));
    }    
    
}

