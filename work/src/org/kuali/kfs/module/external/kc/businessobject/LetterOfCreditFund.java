/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.external.kc.businessobject;

import java.sql.Date;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class LetterOfCreditFund implements ContractsAndGrantsLetterOfCreditFund, MutableInactivatable {

    private String letterOfCreditFundCode;
    private String fundDescription;

    public LetterOfCreditFund() { }
    public LetterOfCreditFund(String letterOfCreditFundCode, String fundDescription) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
        this.fundDescription = fundDescription;
    }

    @Override
    public void setActive(boolean active) { }

    @Override
    public boolean isActive() {
        return true;
    }

    @Override
    public void refresh() { }

    @Override
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }

    @Override
    public String getFundDescription() {
        return fundDescription;
    }

    public void setFundDescription(String fundDescription) {
        this.fundDescription = fundDescription;
    }

    @Override
    public ContractsAndGrantsLetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        // TODO No straight-forward equivalent in KC
        return null;
    }

    @Override
    public String getLetterOfCreditFundGroupCode() {
        // TODO No straight-forward equivalent in KC
        return null;
    }

    @Override
    public KualiDecimal getLetterOfCreditFundAmount() {
        // TODO No equivalent in KC
        return null;
    }

    @Override
    public Date getLetterOfCreditFundStartDate() {
        // TODO No equivalent in KC
        return null;
    }

    @Override
    public Date getLetterOfCreditFundExpirationDate() {
        // TODO No equivalent in KC
        return null;
    }

}
