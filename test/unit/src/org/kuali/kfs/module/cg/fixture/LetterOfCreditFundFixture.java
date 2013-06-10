/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.fixture;

import java.sql.Date;

import org.kuali.kfs.module.cg.businessobject.LetterOfCreditFund;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Fixture class for LetterOfCreditFund
 */
public enum LetterOfCreditFundFixture {


    CG_LOCF("111", "TEST", "DHHS", new KualiDecimal(100), Date.valueOf("2010-01-01"), Date.valueOf("2012-01-01"));


    private String letterOfCreditFundCode;
    private String fundDescription;

    private String letterOfCreditFundGroupCode;
    private KualiDecimal letterOfCreditFundAmount;

    private Date letterOfCreditFundStartDate;
    private Date letterOfCreditFundExpirationDate;


    private LetterOfCreditFundFixture(String letterOfCreditFundCode, String fundDescription, String letterOfCreditFundGroupCode, KualiDecimal letterOfCreditFundAmount, Date letterOfCreditFundStartDate, Date letterOfCreditFundExpirationDate) {

        this.letterOfCreditFundCode = letterOfCreditFundCode;
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
        this.fundDescription = fundDescription;
        this.letterOfCreditFundAmount = letterOfCreditFundAmount;
        this.letterOfCreditFundStartDate = letterOfCreditFundStartDate;
        this.letterOfCreditFundExpirationDate = letterOfCreditFundExpirationDate;
    }

    public LetterOfCreditFund createLetterOfCreditFund() {
        LetterOfCreditFund letterOfCreditFund = new LetterOfCreditFund();
        letterOfCreditFund.setLetterOfCreditFundCode(this.letterOfCreditFundCode);
        letterOfCreditFund.setLetterOfCreditFundGroupCode(this.letterOfCreditFundGroupCode);
        letterOfCreditFund.setLetterOfCreditFundAmount(letterOfCreditFundAmount);
        letterOfCreditFund.setFundDescription(this.fundDescription);
        letterOfCreditFund.setLetterOfCreditFundStartDate(this.letterOfCreditFundStartDate);
        letterOfCreditFund.setLetterOfCreditFundExpirationDate(this.letterOfCreditFundExpirationDate);
        return letterOfCreditFund;
    }
}
