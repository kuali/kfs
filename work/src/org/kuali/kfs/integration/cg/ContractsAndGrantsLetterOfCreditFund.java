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
package org.kuali.kfs.integration.cg;

import java.sql.Date;

import org.kuali.rice.krad.bo.ExternalizableBusinessObject;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * Integration interface for LetterOfCreditFund
 */
public interface ContractsAndGrantsLetterOfCreditFund extends MutableInactivatable, ExternalizableBusinessObject {


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
    public String getFundDescription();


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
