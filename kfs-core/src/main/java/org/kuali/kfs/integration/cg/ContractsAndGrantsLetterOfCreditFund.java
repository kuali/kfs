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
package org.kuali.kfs.integration.cg;

import java.sql.Date;

import org.kuali.rice.core.api.mo.common.active.Inactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.ExternalizableBusinessObject;

/**
 * Integration interface for LetterOfCreditFund
 */
public interface ContractsAndGrantsLetterOfCreditFund extends ExternalizableBusinessObject, Inactivatable {


    /**
     * Gets the letterOfCreditFund attribute.
     *
     * @return Returns the letterOfCreditFund.
     */
    public ContractsAndGrantsLetterOfCreditFundGroup getLetterOfCreditFundGroup();


    /**
     * Gets the fundCode attribute.
     *
     * @return Returns the fundCode.
     */
    public String getLetterOfCreditFundCode();


    /**
     * Gets the fundDescr attribute.
     *
     * @return Returns the fundDescr.
     */
    public String getLetterOfCreditFundDescription();


    /**
     * Gets the letterOfCreditFundGroupCode attribute.
     *
     * @return Returns the letterOfCreditFundGroupCode.
     */
    public String getLetterOfCreditFundGroupCode();


    /**
     * Gets the letterOfCreditFundAmount attribute.
     *
     * @return Returns the letterOfCreditFundAmount.
     */
    public KualiDecimal getLetterOfCreditFundAmount();


    /**
     * Gets the letterOfCreditFundStartDate attribute.
     *
     * @return Returns the letterOfCreditFundStartDate.
     */
    public Date getLetterOfCreditFundStartDate();


    /**
     * Gets the letterOfCreditFundExpirationDate attribute.
     *
     * @return Returns the letterOfCreditFundExpirationDate.
     */
    public Date getLetterOfCreditFundExpirationDate();
}
