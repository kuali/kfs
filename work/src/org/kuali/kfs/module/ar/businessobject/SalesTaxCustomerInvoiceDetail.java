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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class SalesTaxCustomerInvoiceDetail extends CustomerInvoiceDetail {

    private TaxDetail taxDetail;
    private CustomerInvoiceDetail customerInvoiceDetail;

    public SalesTaxCustomerInvoiceDetail(TaxDetail taxDetail, CustomerInvoiceDetail customerInvoiceDetail) {
        this.taxDetail = taxDetail;
        this.customerInvoiceDetail = customerInvoiceDetail;
    }

    @Override
    public Account getAccount() {
        return SpringContext.getBean(AccountService.class).getByPrimaryId(taxDetail.getChartOfAccountsCode(), taxDetail.getAccountNumber());
    }

    @Override
    public String getAccountNumber() {
        return taxDetail.getAccountNumber();
    }

    @Override
    public KualiDecimal getAmount() {
        return taxDetail.getTaxAmount();
    }

    @Override
    public String getChartOfAccountsCode() {
        return taxDetail.getChartOfAccountsCode();
    }


    @Override
    public String getFinancialObjectCode() {
        return taxDetail.getFinancialObjectCode();
    }

    @Override
    public ObjectCode getObjectCode() {
        return SpringContext.getBean(ObjectCodeService.class).getByPrimaryIdForCurrentYear(taxDetail.getChartOfAccountsCode(), taxDetail.getFinancialObjectCode());
    }

    /**
     * This returns the AR object code from customer invoice detail because sales tax detail doesn't have AR info
     * 
     * @see org.kuali.kfs.module.ar.businessobject.CustomerInvoiceDetail#getAccountsReceivableObject()
     */
    @Override
    public ObjectCode getAccountsReceivableObject() {
        return customerInvoiceDetail.getAccountsReceivableObject();
    }
    
    @Override
    public String getAccountsReceivableObjectCode() {
        return customerInvoiceDetail.getAccountsReceivableObjectCode();
    }    

    @Override
    public String getFinancialSubObjectCode() {
        return null;
    }

    @Override
    public String getOrganizationReferenceId() {
        return null;
    }

    @Override
    public String getProjectCode() {
        return null;
    }

    @Override
    public String getSubAccountNumber() {
        return null;
    }

}
