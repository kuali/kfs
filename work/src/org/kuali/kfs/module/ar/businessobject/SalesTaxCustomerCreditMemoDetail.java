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
package org.kuali.kfs.module.ar.businessobject;

import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.sys.businessobject.TaxDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class SalesTaxCustomerCreditMemoDetail extends CustomerCreditMemoDetail {
    private TaxDetail taxDetail;
    private CustomerCreditMemoDetail customerCreditMemoDetail;

    public SalesTaxCustomerCreditMemoDetail(TaxDetail taxDetail, CustomerCreditMemoDetail customerCreditMemoDetail) {
        this.taxDetail = taxDetail;
        this.customerCreditMemoDetail = customerCreditMemoDetail;
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
