/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.module.ld.businessobject;

import java.math.BigDecimal;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kim.api.identity.Person;

/**
 * Labor business object for Employee Funding.
 */
public class EmployeeFunding extends LedgerBalance {

    private String name;
    private String csfDeleteCode;
    private String csfFundingStatusCode;
    private BigDecimal csfTimePercent;
    private BigDecimal csfFullTimeEmploymentQuantity;
    private KualiDecimal csfAmount;
    private KualiDecimal currentAmount;
    private KualiDecimal outstandingEncumbrance;
    private KualiDecimal totalAmount;

    /**
     * Although the title of this class is EmployeeFunding, it is really a representation of the AccountStatusCurrentFunds business
     * object, however it is generated using the fiscal year and employee ID.
     */
    public EmployeeFunding() {
        super();
        this.setMonth1Amount(KualiDecimal.ZERO);
        this.setCurrentAmount(KualiDecimal.ZERO);
        this.setOutstandingEncumbrance(KualiDecimal.ZERO);
        this.setTotalAmount(KualiDecimal.ZERO);
    }

    /**
     * Gets the person name.
     * 
     * @return Returns the PersonName.
     */
    public String getName() {
        Person person = this.getLedgerPerson();
        if (person == null) {
            return LaborConstants.BalanceInquiries.UnknownPersonName;
        }
        
        return person.getName();
    }

    /**
     * Sets the persons name.
     * 
     * @param personName The personName to set.
     */

    public void setName(String personName) {
        this.name = personName;
    }

    /**
     * Gets the csfAmount
     * 
     * @return Returns the csfAmount.
     */
    public KualiDecimal getCsfAmount() {
        return csfAmount;
    }

    /**
     * Sets the csfAmount.
     * 
     * @param csfAmount The csfAmount to set.
     */
    public void setCsfAmount(KualiDecimal csfAmount) {
        this.csfAmount = csfAmount;
    }

    /**
     * Gets the csfDeleteCode.
     * 
     * @return Returns the csfDeleteCode.
     */
    public String getCsfDeleteCode() {
        return csfDeleteCode;
    }

    /**
     * Sets the csfDeleteCode.
     * 
     * @param csfDeleteCode The csfDeleteCode to set.
     */
    public void setCsfDeleteCode(String csfDeleteCode) {
        this.csfDeleteCode = csfDeleteCode;
    }

    /**
     * Gets the csfFundingStatusCode.
     * 
     * @return Returns the csfFundingStatusCode.
     */
    public String getCsfFundingStatusCode() {
        return csfFundingStatusCode;
    }

    /**
     * Sets the csfFundingStatusCode.
     * 
     * @param csfFundingStatusCode The csfFundingStatusCode to set.
     */
    public void setCsfFundingStatusCode(String csfFundingStatusCode) {
        this.csfFundingStatusCode = csfFundingStatusCode;
    }

    /**
     * Gets the csfTimePercent.
     * 
     * @return Returns the csfTimePercent.
     */
    public BigDecimal getCsfTimePercent() {
        return csfTimePercent;
    }

    /**
     * Sets the csfTimePercent.
     * 
     * @param csfTimePercent The csfTimePercent to set.
     */
    public void setCsfTimePercent(BigDecimal csfTimePercent) {
        this.csfTimePercent = csfTimePercent;
    }

    /**
     * Gets the currentAmount.
     * 
     * @return Returns the currentAmount.
     */
    public KualiDecimal getCurrentAmount() {
        return currentAmount;
    }

    /**
     * Sets the currentAmount.
     * 
     * @param currentAmount The currentAmount to set.
     */
    public void setCurrentAmount(KualiDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    /**
     * Gets the outstandingEncumbrance.
     * 
     * @return Returns the outstandingEncumbrance.
     */
    public KualiDecimal getOutstandingEncumbrance() {
        return outstandingEncumbrance;
    }

    /**
     * Sets the outstandingEncumbrance.
     * 
     * @param outstandingEncumbrance The outstandingEncumbrance to set.
     */
    public void setOutstandingEncumbrance(KualiDecimal outstandingEncumbrance) {
        this.outstandingEncumbrance = outstandingEncumbrance;
    }

    /**
     * Returns a total amount based upon adding any outstanding encumberence records to the annual balance amount.
     * 
     * @return TotalAmount
     */
    public KualiDecimal getTotalAmount() {
        return this.currentAmount.add(this.outstandingEncumbrance);
    }

    /**
     * Sets the total amount.
     * 
     * @param totalAmount The totalAmount to set.
     */
    public void setTotalAmount(KualiDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((getAccountNumber() == null) ? 0 : getAccountNumber().hashCode());
        result = PRIME * result + ((getChartOfAccountsCode() == null) ? 0 : getChartOfAccountsCode().hashCode());
        result = PRIME * result + ((getFinancialObjectCode() == null) ? 0 : getFinancialObjectCode().hashCode());
        result = PRIME * result + ((getFinancialSubObjectCode() == null) ? 0 : getFinancialSubObjectCode().hashCode());
        result = PRIME * result + ((getSubAccountNumber() == null) ? 0 : getSubAccountNumber().hashCode());
        result = PRIME * result + ((getUniversityFiscalYear() == null) ? 0 : getUniversityFiscalYear().hashCode());
        result = PRIME * result + ((getEmplid() == null) ? 0 : getEmplid().hashCode());
        result = PRIME * result + ((getPositionNumber() == null) ? 0 : getPositionNumber().hashCode());

        return result;
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        final EmployeeFunding other = (EmployeeFunding) obj;

        if (!ObjectUtils.equals(getAccountNumber(), other.getAccountNumber())) {
            return false;
        }
        else if (!ObjectUtils.equals(getChartOfAccountsCode(), other.getChartOfAccountsCode())) {
            return false;
        }
        else if (!ObjectUtils.equals(getFinancialObjectCode(), other.getFinancialObjectCode())) {
            return false;
        }
        else if (!ObjectUtils.equals(getFinancialSubObjectCode(), other.getFinancialSubObjectCode())) {
            return false;
        }
        else if (!ObjectUtils.equals(getSubAccountNumber(), other.getSubAccountNumber())) {
            return false;
        }
        else if (!ObjectUtils.equals(getUniversityFiscalYear(), other.getUniversityFiscalYear())) {
            return false;
        }
        else if (!ObjectUtils.equals(getEmplid(), other.getEmplid())) {
            return false;
        }
        else if (!ObjectUtils.equals(getPositionNumber(), other.getPositionNumber())) {
            return false;
        }

        return true;
    }

    /**
     * Gets the csfFullTimeEmploymentQuantity.
     * 
     * @return Returns the csfFullTimeEmploymentQuantity.
     */
    public BigDecimal getCsfFullTimeEmploymentQuantity() {
        return csfFullTimeEmploymentQuantity;
    }

    /**
     * Sets the csfFullTimeEmploymentQuantity.
     * 
     * @param csfFullTimeEmploymentQuantity The csfFullTimeEmploymentQuantity to set.
     */
    public void setCsfFullTimeEmploymentQuantity(BigDecimal csfFullTimeEmploymentQuantity) {
        this.csfFullTimeEmploymentQuantity = csfFullTimeEmploymentQuantity;
    }
}

