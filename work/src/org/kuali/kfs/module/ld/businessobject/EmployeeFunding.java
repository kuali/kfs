/*
 * Copyright 2006-2007 The Kuali Foundation.
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

package org.kuali.module.labor.bo;

import java.math.BigDecimal;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.core.bo.user.PersonPayrollId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.bo.user.UserId;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.labor.LaborConstants;

public class EmployeeFunding extends LedgerBalance {

    private String personName;
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
     * This method returns the person name
     * 
     * @return
     */
    public String getPersonName() {
        UserId empl = new PersonPayrollId(getEmplid());
        UniversalUser universalUser = null;

        try {
            universalUser = SpringContext.getBean(UniversalUserService.class).getUniversalUser(empl);
        }
        catch (UserNotFoundException e) {
            return LaborConstants.BalanceInquiries.UnknownPersonName;
        }

        return universalUser.getPersonName();
    }

    /**
     * This method set thes persons name
     * 
     * @param personName
     */

    public void setPersonName(String personName) {
        this.personName = personName;
    }

    /**
     * Gets the csfAmount attribute.
     * 
     * @return Returns the csfAmount.
     */
    public KualiDecimal getCsfAmount() {
        return csfAmount;
    }

    /**
     * Sets the csfAmount attribute value.
     * 
     * @param csfAmount The csfAmount to set.
     */
    public void setCsfAmount(KualiDecimal csfAmount) {
        this.csfAmount = csfAmount;
    }

    /**
     * Gets the csfDeleteCode attribute.
     * 
     * @return Returns the csfDeleteCode.
     */
    public String getCsfDeleteCode() {
        return csfDeleteCode;
    }

    /**
     * Sets the csfDeleteCode attribute value.
     * 
     * @param csfDeleteCode The csfDeleteCode to set.
     */
    public void setCsfDeleteCode(String csfDeleteCode) {
        this.csfDeleteCode = csfDeleteCode;
    }

    /**
     * Gets the csfFundingStatusCode attribute.
     * 
     * @return Returns the csfFundingStatusCode.
     */
    public String getCsfFundingStatusCode() {
        return csfFundingStatusCode;
    }

    /**
     * Sets the csfFundingStatusCode attribute value.
     * 
     * @param csfFundingStatusCode The csfFundingStatusCode to set.
     */
    public void setCsfFundingStatusCode(String csfFundingStatusCode) {
        this.csfFundingStatusCode = csfFundingStatusCode;
    }

    /**
     * Gets the csfTimePercent attribute.
     * 
     * @return Returns the csfTimePercent.
     */
    public BigDecimal getCsfTimePercent() {
        return csfTimePercent;
    }

    /**
     * Sets the csfTimePercent attribute value.
     * 
     * @param csfTimePercent The csfTimePercent to set.
     */
    public void setCsfTimePercent(BigDecimal csfTimePercent) {
        this.csfTimePercent = csfTimePercent;
    }

    /**
     * Gets the currentAmount attribute.
     * 
     * @return Returns the currentAmount.
     */
    public KualiDecimal getCurrentAmount() {
        return currentAmount;
    }

    /**
     * Sets the currentAmount attribute value.
     * 
     * @param currentAmount The currentAmount to set.
     */
    public void setCurrentAmount(KualiDecimal currentAmount) {
        this.currentAmount = currentAmount;
    }

    /**
     * Gets the outstandingEncumbrance attribute.
     * 
     * @return Returns the outstandingEncumbrance.
     */
    public KualiDecimal getOutstandingEncumbrance() {
        return outstandingEncumbrance;
    }

    /**
     * Sets the outstandingEncumbrance attribute value.
     * 
     * @param outstandingEncumbrance The outstandingEncumbrance to set.
     */
    public void setOutstandingEncumbrance(KualiDecimal outstandingEncumbrance) {
        this.outstandingEncumbrance = outstandingEncumbrance;
    }

    /**
     * This method returns a total amount based upon adding any outstanding encumberence records to the annual balance amount.
     * 
     * @return
     */
    public KualiDecimal getTotalAmount() {
        return this.currentAmount.add(this.outstandingEncumbrance);
    }

    /**
     * This method sets a total amount value
     * 
     * @param totalAmount
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
     * Gets the csfFullTimeEmploymentQuantity attribute. 
     * @return Returns the csfFullTimeEmploymentQuantity.
     */
    public BigDecimal getCsfFullTimeEmploymentQuantity() {
        return csfFullTimeEmploymentQuantity;
    }

    /**
     * Sets the csfFullTimeEmploymentQuantity attribute value.
     * @param csfFullTimeEmploymentQuantity The csfFullTimeEmploymentQuantity to set.
     */
    public void setCsfFullTimeEmploymentQuantity(BigDecimal csfFullTimeEmploymentQuantity) {
        this.csfFullTimeEmploymentQuantity = csfFullTimeEmploymentQuantity;
    }
}