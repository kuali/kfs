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
    private String letterOfCreditFundDescription;
    private String letterOfCreditFundGroupCode;
    private KualiDecimal letterOfCreditFundAmount;
    private Date letterOfCreditFundStartDate;
    private Date letterOfCreditFundExpirationDate;

    private LetterOfCreditFundFixture(String letterOfCreditFundCode, String letterOfCreditFundDescription, String letterOfCreditFundGroupCode, KualiDecimal letterOfCreditFundAmount, Date letterOfCreditFundStartDate, Date letterOfCreditFundExpirationDate) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
        this.letterOfCreditFundDescription = letterOfCreditFundDescription;
        this.letterOfCreditFundAmount = letterOfCreditFundAmount;
        this.letterOfCreditFundStartDate = letterOfCreditFundStartDate;
        this.letterOfCreditFundExpirationDate = letterOfCreditFundExpirationDate;
    }

    public LetterOfCreditFund createLetterOfCreditFund() {
        LetterOfCreditFund letterOfCreditFund = new LetterOfCreditFund();
        letterOfCreditFund.setLetterOfCreditFundCode(this.letterOfCreditFundCode);
        letterOfCreditFund.setLetterOfCreditFundGroupCode(this.letterOfCreditFundGroupCode);
        letterOfCreditFund.setLetterOfCreditFundAmount(this.letterOfCreditFundAmount);
        letterOfCreditFund.setLetterOfCreditFundDescription(this.letterOfCreditFundDescription);
        letterOfCreditFund.setLetterOfCreditFundStartDate(this.letterOfCreditFundStartDate);
        letterOfCreditFund.setLetterOfCreditFundExpirationDate(this.letterOfCreditFundExpirationDate);
        return letterOfCreditFund;
    }
}
