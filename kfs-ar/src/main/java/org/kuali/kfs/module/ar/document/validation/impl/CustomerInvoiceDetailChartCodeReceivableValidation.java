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
