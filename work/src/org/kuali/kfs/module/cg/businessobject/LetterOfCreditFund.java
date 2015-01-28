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
package org.kuali.kfs.module.cg.businessobject;

import java.sql.Date;
import java.util.LinkedHashMap;

import org.kuali.kfs.integration.cg.ContractsAndGrantsLetterOfCreditFund;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Letter Of Credit Fund under Contracts & Grants section.
 */
public class LetterOfCreditFund extends PersistableBusinessObjectBase implements ContractsAndGrantsLetterOfCreditFund, MutableInactivatable {

    private String letterOfCreditFundCode;
    private String letterOfCreditFundDescription;
    private String letterOfCreditFundGroupCode;
    private KualiDecimal letterOfCreditFundAmount;
    private Date letterOfCreditFundStartDate;
    private Date letterOfCreditFundExpirationDate;
    private LetterOfCreditFundGroup letterOfCreditFundGroup;
    private boolean active;

    /**
     * Gets the active attribute.
     *
     * @return Returns the active.
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Sets the active attribute value.
     *
     * @param active The active to set.
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets the letterOfCreditFundGroup attribute.
     *
     * @return Returns the letterOfCreditFundGroup.
     */
    @Override
    public LetterOfCreditFundGroup getLetterOfCreditFundGroup() {
        return letterOfCreditFundGroup;
    }

    /**
     * Sets the letterOfCreditFundGroup attribute value.
     *
     * @param letterOfCreditFundGroup The letterOfCreditFundGroup to set.
     */
    public void setLetterOfCreditFundGroup(LetterOfCreditFundGroup letterOfCreditFundGroup) {
        this.letterOfCreditFundGroup = letterOfCreditFundGroup;
    }

    /**
     * Gets the letterOfCreditFundCode attribute.
     *
     * @return Returns the letterOfCreditFundCode.
     */
    @Override
    public String getLetterOfCreditFundCode() {
        return letterOfCreditFundCode;
    }

    /**
     * Sets the letterOfCreditFundCode attribute value.
     *
     * @param letterOfCreditFundCode The letterOfCreditFundCode to set.
     */
    public void setLetterOfCreditFundCode(String letterOfCreditFundCode) {
        this.letterOfCreditFundCode = letterOfCreditFundCode;
    }

    /**
     * Gets the letterOfCreditFundDescription attribute.
     *
     * @return Returns the letterOfCreditFundDescription.
     */
    @Override
    public String getLetterOfCreditFundDescription() {
        return letterOfCreditFundDescription;
    }

    /**
     * Sets the letterOfCreditFundDescription attribute value.
     *
     * @param letterOfCreditFundDescription The letterOfCreditFundDescription to set.
     */
    public void setLetterOfCreditFundDescription(String letterOfCreditFundDescription) {
        this.letterOfCreditFundDescription = letterOfCreditFundDescription;
    }

    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     *
     * @return Returns the letterOfCreditFundGroupCode.
     */
    @Override
    public String getLetterOfCreditFundGroupCode() {
        return letterOfCreditFundGroupCode;
    }

    /**
     * Sets the letterOfCreditFundGroupCode attribute value.
     *
     * @param letterOfCreditFundGroupCode The letterOfCreditFundGroupCode to set.
     */
    public void setLetterOfCreditFundGroupCode(String letterOfCreditFundGroupCode) {
        this.letterOfCreditFundGroupCode = letterOfCreditFundGroupCode;
    }

    /**
     * Gets the letterOfCreditFundAmount attribute.
     *
     * @return Returns the letterOfCreditFundAmount.
     */
    @Override
    public KualiDecimal getLetterOfCreditFundAmount() {
        return letterOfCreditFundAmount;
    }

    /**
     * Sets the letterOfCreditFundAmount attribute value.
     *
     * @param letterOfCreditFundAmount The letterOfCreditFundAmount to set.
     */
    public void setLetterOfCreditFundAmount(KualiDecimal letterOfCreditFundAmount) {
        this.letterOfCreditFundAmount = letterOfCreditFundAmount;
    }

    /**
     * Gets the letterOfCreditFundStartDate attribute.
     *
     * @return Returns the letterOfCreditFundStartDate.
     */
    @Override
    public Date getLetterOfCreditFundStartDate() {
        return letterOfCreditFundStartDate;
    }

    /**
     * Sets the letterOfCreditFundStartDate attribute value.
     *
     * @param letterOfCreditFundStartDate The letterOfCreditFundStartDate to set.
     */
    public void setLetterOfCreditFundStartDate(Date letterOfCreditFundStartDate) {
        this.letterOfCreditFundStartDate = letterOfCreditFundStartDate;
    }

    /**
     * Gets the letterOfCreditFundExpirationDate attribute.
     *
     * @return Returns the letterOfCreditFundExpirationDate.
     */
    @Override
    public Date getLetterOfCreditFundExpirationDate() {
        return letterOfCreditFundExpirationDate;
    }

    /**
     * Sets the letterOfCreditFundExpirationDate attribute value.
     *
     * @param letterOfCreditFundExpirationDate The letterOfCreditFundExpirationDate to set.
     */
    public void setLetterOfCreditFundExpirationDate(Date letterOfCreditFundExpirationDate) {
        this.letterOfCreditFundExpirationDate = letterOfCreditFundExpirationDate;
    }

    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap<String, String> m = new LinkedHashMap<String, String>();
        m.put("letterOfCreditFundCode", this.letterOfCreditFundCode.toString());
        m.put("fundDescr", this.letterOfCreditFundDescription.toString());
        m.put("letterOfCreditFundGroupCode", this.letterOfCreditFundGroupCode.toString());
        m.put("letterOfCreditFundAmount", this.letterOfCreditFundAmount.toString());
        m.put("letterOfCreditFundStartDate", this.letterOfCreditFundStartDate.toString());
        m.put("letterOfCreditFundExpirationDate", this.letterOfCreditFundExpirationDate.toString());
        return m;
    }

}
