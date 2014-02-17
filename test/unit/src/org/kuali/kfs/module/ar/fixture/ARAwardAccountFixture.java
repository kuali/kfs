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

import java.sql.Date;

import org.kuali.kfs.module.cg.businessobject.AwardAccount;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for CG AwardAccount
 */
public enum ARAwardAccountFixture {

    AWD_ACCT_1(new Long(11), "BL", "2336320", false, null, null, new KualiDecimal(1), false),
    AWD_ACCT_2(new Long(11), "IN", "1292016", false, null, null, new KualiDecimal(1), false),
    AWD_ACCT_WITH_CCA_1(new Long(11), "BL", "1020087", false, null, null, new KualiDecimal(1), false),
    AWD_ACCT_WITH_CCA_2(new Long(11), "BL", "1021887", false, null, null, new KualiDecimal(1), false),
    AWD_ACCT_WITH_CCA_3(new Long(11), "BL", "2424704", false, null, null, new KualiDecimal(1), false);

    private Long proposalNumber;
    private String chartOfAccountsCode;
    private String accountNumber;
    private boolean active = true;
    private boolean finalBilledIndicator;
    private Date currentLastBilledDate;
    private Date previousLastBilledDate;
    private KualiDecimal amountToDraw = KualiDecimal.ZERO;
    private boolean locReviewIndicator;


    private ARAwardAccountFixture(Long proposalNumber, String chartOfAccountsCode, String accountNumber, boolean finalBilledIndicator, Date currentLastBilledDate, Date previousLastBilledDate, KualiDecimal amountToDraw, boolean locReviewIndicator) {

        this.proposalNumber = proposalNumber;
        this.chartOfAccountsCode = chartOfAccountsCode;
        this.accountNumber = accountNumber;
        this.finalBilledIndicator = finalBilledIndicator;
        this.currentLastBilledDate = currentLastBilledDate;
        this.previousLastBilledDate = previousLastBilledDate;
        this.amountToDraw = amountToDraw;
        this.locReviewIndicator = locReviewIndicator;


    }

    public AwardAccount createAwardAccount() {
        AwardAccount awardAccount = new AwardAccount();

        awardAccount.setProposalNumber(this.proposalNumber);
        awardAccount.setChartOfAccountsCode(this.chartOfAccountsCode);
        awardAccount.setAccountNumber(this.accountNumber);
        awardAccount.setFinalBilledIndicator(this.finalBilledIndicator);
        awardAccount.setCurrentLastBilledDate(this.currentLastBilledDate);
        awardAccount.setPreviousLastBilledDate(this.previousLastBilledDate);
        awardAccount.setAmountToDraw(this.amountToDraw);
        awardAccount.setLetterOfCreditReviewIndicator(this.locReviewIndicator);
        return awardAccount;
    }
}
