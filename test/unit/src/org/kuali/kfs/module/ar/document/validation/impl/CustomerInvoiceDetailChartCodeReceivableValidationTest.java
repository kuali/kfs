/*
 * Copyright 2008 The Kuali Foundation
 * 
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl2.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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

