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

import java.util.HashMap;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

@ConfigureContext(session = khuntley)
public class CustomerInvoiceDetailChartCodeReceivableValidationTest extends KualiTestBase {
    
    private CustomerInvoiceDetailChartCodeReceivableValidation validation;
    
    private static final String VALID_CHART_OF_ACCOUNTS_CODE = "UA";
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();
        validation = new CustomerInvoiceDetailChartCodeReceivableValidation();
        validation.setCustomerInvoiceDetail(new CustomerInvoiceDetail());
        validation.getCustomerInvoiceDetail().setChartOfAccountsCode(VALID_CHART_OF_ACCOUNTS_CODE);
    }

    @Override
    protected void tearDown() throws Exception {
        validation = null;
        super.tearDown();
    }    
    
    public void testDoesChartCodeHaveReceivableObjectCode_True(){
        assertTrue(validation.validate(null));
    }
    
    public void testDoesChartCodeHaveReceivableObjectCode_False(){
     
        //Blank out receivable object code for chart.
        Map<String,String> criteria = new HashMap<String,String>();
        criteria.put("chartOfAccountsCode", VALID_CHART_OF_ACCOUNTS_CODE);
        
        Chart chart = (Chart)SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Chart.class, criteria);
        chart.setFinAccountsReceivableObjCode("");
        
        assertFalse(validation.validate(null));
    }

}

