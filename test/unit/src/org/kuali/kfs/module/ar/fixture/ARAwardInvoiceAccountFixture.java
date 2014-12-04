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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.cg.businessobject.AwardInvoiceAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;

/**
 * Fixture class for CG AwardInvoiceAccount
 */
public enum ARAwardInvoiceAccountFixture {

    AWD_INV_ACCT_1(new Long(111), "BL", SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear(), "1031400", "5000", null, null, ArConstants.INCOME_ACCOUNT);

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private Integer universityFiscalYear;
    private String accountNumber;
    private String objectCode;
    private String subObjectCode;
    private String subAccountNumber;

    private String accountType;


    private ARAwardInvoiceAccountFixture(Long proposalNumber, String chartOfAccountsCode, Integer universityFiscalYear, String accountNumber, String objectCode, String subObjectCode, String subAccountNumber, String accountType) {

        this.proposalNumber = proposalNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.universityFiscalYear = universityFiscalYear;
        this.accountNumber = accountNumber;
        this.objectCode = objectCode;
        this.subObjectCode = subObjectCode;
        this.subAccountNumber = subAccountNumber;
        this.accountType = accountType;


    }

    public AwardInvoiceAccount createAwardInvoiceAccount() {
        AwardInvoiceAccount awardInvoiceAccount = new AwardInvoiceAccount();

        awardInvoiceAccount.setProposalNumber(this.proposalNumber);
        awardInvoiceAccount.setChartOfAccountsCode(this.chartOfAccountsCode);
        awardInvoiceAccount.setUniversityFiscalYear(this.universityFiscalYear);
        awardInvoiceAccount.setAccountNumber(this.accountNumber);
        awardInvoiceAccount.setObjectCode(this.objectCode);
        awardInvoiceAccount.setSubObjectCode(this.subObjectCode);
        awardInvoiceAccount.setSubAccountNumber(this.subAccountNumber);
        awardInvoiceAccount.setAccountType(this.accountType);
        return awardInvoiceAccount;
    }
}
