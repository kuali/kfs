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
package org.kuali.kfs.module.external.kc.businessobject;

import java.sql.Date;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFundGroup;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;

public class LetterOfCreditFund implements ContractsAndGrantsLetterOfCreditFund, MutableInactivatable {

    private String letterOfCreditFundCode;
    private String letterOfCreditFundDescription;

    public LetterOfCreditFund() { }
    public LetterOfCreditFund(String letterOfCreditFundCode, String letterOfCreditFundDescription) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
        this.letterOfCreditFundDescription = letterOfCreditFundDescription;
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
    public String getLetterOfCreditFundDescription() {
        return letterOfCreditFundDescription;
    }

    public void setLetterOfCreditFundDescription(String letterOfCreditFundDescription) {
        this.letterOfCreditFundDescription = letterOfCreditFundDescription;
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
