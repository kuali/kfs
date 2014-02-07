/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.fixture;

import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.AwardInvoiceAccount;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;

/**
 * Fixture class for CG AwardInvoiceAccount
 */
public enum ARAwardInvoiceAccountFixture {

    AWD_INV_ACCT_1(new Long(111), "BL", SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear(), "1031400", "5000", null, null, ArPropertyConstants.INCOME_ACCOUNT);

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
