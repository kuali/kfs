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
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail;
import org.kuali.kfs.module.ar.document.CustomerInvoiceDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerInvoiceDetailSubFundGroupReceivableValidation extends GenericValidation {

    protected CustomerInvoiceDetail customerInvoiceDetail;
    protected ParameterService parameterService;

    @Override
    public boolean validate(AttributedDocumentEvent event) {
        customerInvoiceDetail.refreshReferenceObject(KFSPropertyConstants.ACCOUNT);
        Account account = customerInvoiceDetail.getAccount();
        if (StringUtils.isNotEmpty(account.getSubFundGroupCode())) {
            String receivableObjectCode = parameterService.getSubParameterValueAsString(CustomerInvoiceDocument.class, ArConstants.GLPE_RECEIVABLE_OFFSET_OBJECT_CODE_BY_SUB_FUND, account.getSubFundGroupCode());

            if (StringUtils.isEmpty(receivableObjectCode)) {
                GlobalVariables.getMessageMap().putError(KFSConstants.SUB_FUND_GROUP_CODE_PROPERTY_NAME, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_WITH_NO_AR_OBJ_CD, account.getSubFundGroupCode(), account.getAccountNumber());
                return false;
            } else {
                customerInvoiceDetail.setAccountsReceivableObjectCode(receivableObjectCode);
                customerInvoiceDetail.refreshReferenceObject("accountsReceivableObject");

                if (ObjectUtils.isNull(customerInvoiceDetail.getAccountsReceivableObject())) {
                    GlobalVariables.getMessageMap().putError(KFSConstants.SUB_FUND_GROUP_CODE_PROPERTY_NAME, ArKeyConstants.ERROR_CUSTOMER_INVOICE_DOCUMENT_INVALID_SUBFUND_AR_OBJ_CD_IN_PARM, receivableObjectCode, account.getSubFundGroupCode(), account.getAccountNumber());
                    return false;
                }
            }
        }

        return true;
    }

    public void setCustomerInvoiceDetail(CustomerInvoiceDetail customerInvoiceDetail) {
        this.customerInvoiceDetail = customerInvoiceDetail;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
