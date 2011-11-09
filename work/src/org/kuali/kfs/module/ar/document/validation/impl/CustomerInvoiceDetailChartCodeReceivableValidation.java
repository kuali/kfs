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

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;

public class CustomerInvoiceDetailChartCodeReceivableValidation extends GenericValidation {

    private CustomerInvoiceDetail customerInvoiceDetail;

    public boolean validate(AttributedDocumentEvent event) {
        customerInvoiceDetail.refreshReferenceObject(KFSPropertyConstants.CHART);
        Chart chart = customerInvoiceDetail.getChart();
        if (StringUtils.isEmpty(chart.getFinAccountsReceivableObjCode())) {
            GlobalVariables.getMessageMap().putError(KFSConstants.CHART_OF_ACCOUNTS_CODE_PROPERTY_NAME, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_CHART_WITH_NO_AR_OBJ_CD, chart.getChartOfAccountsCode());
            return false;
        }

        return true;
    }
    
    public CustomerInvoiceDetail getCustomerInvoiceDetail() {
        return customerInvoiceDetail;
    }

    public void setCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        this.customerInvoiceDetail = customerInvoiceDetail;
    }    

}
