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
package org.kuali.module.kra.budget.bo;

import java.util.LinkedHashMap;

import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSPropertyConstants;

/**
 * 
 */

public class BudgetNonpersonnel extends PersistableBusinessObjectBase implements Comparable {
    private static final long serialVersionUID = -7058371220805374107L;

    private String documentNumber;
    private Integer budgetTaskSequenceNumber;
    private Integer budgetPeriodSequenceNumber;
    private String budgetNonpersonnelCategoryCode;
    private String budgetNonpersonnelSubCategoryCode;
    private Integer budgetNonpersonnelSequenceNumber;
    private String budgetNonpersonnelDescription;
    private KualiInteger agencyRequestAmount;
    private KualiInteger budgetThirdPartyCostShareAmount;
    private KualiInteger budgetInstitutionCostShareAmount;
    private Integer budgetOriginSequenceNumber;
    private boolean agencyCopyIndicator;
    private boolean budgetInstitutionCostShareCopyIndicator;
    private boolean budgetThirdPartyCostShareCopyIndicator;
    private KualiInteger budgetOriginAgencyAmount;
    private KualiInteger budgetOriginInstitutionCostShareAmount;
    private KualiInteger budgetOriginThirdPartyCostShareAmount;
    private String subcontractorNumber;

    private boolean copyToFuturePeriods = false; // Not in database, but field used by Nonpersonnel page.

    // Following 3 fields are not in database. They are used in order to determine after a page submit if
    // the indicators for each of those needs to be unchecked (functionally if a user modifies an amount,
    // the indicator should be dropped).
    private KualiInteger agencyRequestAmountBackup;
    private KualiInteger budgetThirdPartyCostShareAmountBackup;
    private KualiInteger budgetInstitutionCostShareAmountBackup;

    private NonpersonnelObjectCode nonpersonnelObjectCode;

    /**
     * Default no-arg constructor.
     */
    public BudgetNonpersonnel() {
        agencyRequestAmount = KualiInteger.ZERO;
        budgetThirdPartyCostShareAmount = KualiInteger.ZERO;
        budgetInstitutionCostShareAmount = KualiInteger.ZERO;
    }

    /**
     * Makes a 1:1 copy of the budgetNonpersonnel passed in.
     * 
     * @param budgetNonpersonnel to be used to copy source
     */
    public BudgetNonpersonnel(BudgetNonpersonnel budgetNonpersonnel) {
        documentNumber = budgetNonpersonnel.getDocumentNumber();
        budgetTaskSequenceNumber = budgetNonpersonnel.getBudgetTaskSequenceNumber();
        budgetPeriodSequenceNumber = budgetNonpersonnel.getBudgetPeriodSequenceNumber();
        budgetNonpersonnelCategoryCode = budgetNonpersonnel.getBudgetNonpersonnelCategoryCode();
        budgetNonpersonnelSubCategoryCode = budgetNonpersonnel.getBudgetNonpersonnelSubCategoryCode();
        budgetNonpersonnelSequenceNumber = budgetNonpersonnel.getBudgetNonpersonnelSequenceNumber();
        budgetNonpersonnelDescription = budgetNonpersonnel.getBudgetNonpersonnelDescription();
        agencyRequestAmount = budgetNonpersonnel.getAgencyRequestAmount();
        budgetThirdPartyCostShareAmount = budgetNonpersonnel.getBudgetThirdPartyCostShareAmount();
        budgetInstitutionCostShareAmount = budgetNonpersonnel.getBudgetInstitutionCostShareAmount();
        budgetOriginSequenceNumber = budgetNonpersonnel.getBudgetOriginSequenceNumber();
        agencyCopyIndicator = budgetNonpersonnel.getAgencyCopyIndicator();
        budgetInstitutionCostShareCopyIndicator = budgetNonpersonnel.getBudgetInstitutionCostShareCopyIndicator();
        budgetThirdPartyCostShareCopyIndicator = budgetNonpersonnel.getBudgetThirdPartyCostShareCopyIndicator();
        budgetOriginAgencyAmount = budgetNonpersonnel.getBudgetOriginAgencyAmount();
        budgetOriginInstitutionCostShareAmount = budgetNonpersonnel.getBudgetOriginInstitutionCostShareAmount();
        budgetOriginThirdPartyCostShareAmount = budgetNonpersonnel.getBudgetOriginThirdPartyCostShareAmount();
        subcontractorNumber = budgetNonpersonnel.getSubcontractorNumber();

        copyToFuturePeriods = budgetNonpersonnel.getCopyToFuturePeriods();

        nonpersonnelObjectCode = budgetNonpersonnel.getNonpersonnelObjectCode();
    }


    /**
     * @param budgetTaskSequenceNumber
     * @param budgetPeriodSequenceNumber
     * @param budgetNonpersonnelCategoryCode
     * @param budgetNonpersonnelSubCategoryCode
     * @param budgetNonpersonnelDescription
     * @param agencyRequestAmount
     * @param budgetInstitutionCostShareAmount
     */
    public BudgetNonpersonnel(Integer budgetTaskSequenceNumber, Integer budgetPeriodSequenceNumber, String budgetNonpersonnelCategoryCode, String budgetNonpersonnelSubCategoryCode, String budgetNonpersonnelDescription, KualiInteger agencyRequestAmount, KualiInteger budgetInstitutionCostShareAmount) {
        this();

        this.budgetTaskSequenceNumber = budgetTaskSequenceNumber;
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
        this.budgetNonpersonnelCategoryCode = budgetNonpersonnelCategoryCode;
        this.budgetNonpersonnelSubCategoryCode = budgetNonpersonnelSubCategoryCode;
        this.budgetNonpersonnelDescription = budgetNonpersonnelDescription;
        this.agencyRequestAmount = agencyRequestAmount;
        this.budgetInstitutionCostShareAmount = budgetInstitutionCostShareAmount;
    }

    /**
     * Determined if this item has already been copied over into other periods. Located in this Business Object because it needs to
     * be available on the NPRS page (not just on the copy over page).
     * 
     * @return NPRS copy over origin item.
     */
    public boolean isCopiedOverItem() {
        return budgetOriginSequenceNumber != null;
    }

    /**
     * Determines if this object is an origin item in the term of NPRS copy over. Located in this Business Object because it needs
     * to be available in BudgetNonpersonnel objects.
     * 
     * @return NPRS copy over origin item.
     */
    public boolean isOriginItem() {
        boolean result = false;

        // while the below seems slightly complicated it has to be noted that this
        // should work on both the copy over page & NPRS page.

        if (budgetNonpersonnelSequenceNumber == null || budgetNonpersonnelSequenceNumber.intValue() == -1) {
            // newly copied over may be null or -1 per BoHelper constructor.
            result = false;
        }
        else if (budgetOriginSequenceNumber == null) { // newly added items on NPRS page have no origin number
            result = true;
        }
        else {
            // origin items have those two values equal per constructor in BudgetNonpersonnelCopyOverBoHelper
            result = this.getBudgetOriginSequenceNumber().intValue() == this.getBudgetNonpersonnelSequenceNumber().intValue();
        }

        return result;
    }

    /**
     * Gets the budgetPeriodSequenceNumber attribute.
     * 
     * @return Returns the budgetPeriodSequenceNumber.
     */
    public Integer getBudgetPeriodSequenceNumber() {
        return budgetPeriodSequenceNumber;
    }

    /**
     * Sets the budgetPeriodSequenceNumber attribute value.
     * 
     * @param budgetPeriodSequenceNumber The budgetPeriodSequenceNumber to set.
     */
    public void setBudgetPeriodSequenceNumber(Integer budgetPeriodSequenceNumber) {
        this.budgetPeriodSequenceNumber = budgetPeriodSequenceNumber;
    }

    /**
     * Gets the budgetTaskSequenceNumber attribute.
     * 
     * @return Returns the budgetTaskSequenceNumber.
     */
    public Integer getBudgetTaskSequenceNumber() {
        return budgetTaskSequenceNumber;
    }

    /**
     * Sets the budgetTaskSequenceNumber attribute value.
     * 
     * @param budgetTaskSequenceNumber The budgetTaskSequenceNumber to set.
     */
    public void setBudgetTaskSequenceNumber(Integer budgetTaskSequenceNumber) {
        this.budgetTaskSequenceNumber = budgetTaskSequenceNumber;
    }

    /**
     * Gets the documentNumber attribute.
     * 
     * @return Returns the documentNumber.
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * Sets the documentNumber attribute value.
     * 
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * o
     */
    public void setBudgetNonpersonnelSequenceNumber(Integer o) {
        budgetNonpersonnelSequenceNumber = o;
    }

    /**
     * budgetNonpersonnelSequenceNumber
     */
    public Integer getBudgetNonpersonnelSequenceNumber() {
        return budgetNonpersonnelSequenceNumber;
    }

    /**
     * o
     */
    public void setBudgetNonpersonnelDescription(String o) {
        budgetNonpersonnelDescription = o;
    }

    /**
     * budgetNonpersonnelDescription
     */
    public String getBudgetNonpersonnelDescription() {
        return budgetNonpersonnelDescription;
    }

    /**
     * Gets the agencyRequestAmount attribute.
     * 
     * @return Returns the agencyRequestAmount.
     */
    public KualiInteger getAgencyRequestAmount() {
        return agencyRequestAmount;
    }

    /**
     * Sets the agencyRequestAmount attribute value.
     * 
     * @param agencyRequestAmount The agencyRequestAmount to set.
     */
    public void setAgencyRequestAmount(KualiInteger agencyRequestAmount) {
        this.agencyRequestAmount = agencyRequestAmount;
    }

    /**
     * Gets the budgetThirdPartyCostShareAmount attribute.
     * 
     * @return Returns the budgetThirdPartyCostShareAmount.
     */
    public KualiInteger getBudgetThirdPartyCostShareAmount() {
        return budgetThirdPartyCostShareAmount;
    }

    /**
     * Sets the budgetThirdPartyCostShareAmount attribute value.
     * 
     * @param budgetThirdPartyCostShareAmount The budgetThirdPartyCostShareAmount to set.
     */
    public void setBudgetThirdPartyCostShareAmount(KualiInteger budgetThirdPartyCostShareAmount) {
        this.budgetThirdPartyCostShareAmount = budgetThirdPartyCostShareAmount;
    }

    /**
     * Gets the budgetInstitutionCostShareAmount attribute.
     * 
     * @return Returns the budgetInstitutionCostShareAmount.
     */
    public KualiInteger getBudgetInstitutionCostShareAmount() {
        return budgetInstitutionCostShareAmount;
    }

    /**
     * Sets the budgetInstitutionCostShareAmount attribute value.
     * 
     * @param budgetInstitutionCostShareAmount The budgetInstitutionCostShareAmount to set.
     */
    public void setBudgetInstitutionCostShareAmount(KualiInteger budgetInstitutionCostShareAmount) {
        this.budgetInstitutionCostShareAmount = budgetInstitutionCostShareAmount;
    }

    /**
     * o
     */
    public void setBudgetOriginSequenceNumber(Integer o) {
        budgetOriginSequenceNumber = o;
    }

    /**
     * budgetOriginSequenceNumber
     */
    public Integer getBudgetOriginSequenceNumber() {
        return budgetOriginSequenceNumber;
    }

    /**
     * o
     */
    public void setAgencyCopyIndicator(boolean o) {
        agencyCopyIndicator = o;
    }

    /**
     * agencyCopyIndicator
     */
    public boolean getAgencyCopyIndicator() {
        return agencyCopyIndicator;
    }

    /**
     * o
     */
    public void setBudgetInstitutionCostShareCopyIndicator(boolean o) {
        budgetInstitutionCostShareCopyIndicator = o;
    }

    /**
     * budgetInstitutionCostShareCopyIndicator
     */
    public boolean getBudgetInstitutionCostShareCopyIndicator() {
        return budgetInstitutionCostShareCopyIndicator;
    }

    /**
     * o
     */
    public void setBudgetThirdPartyCostShareCopyIndicator(boolean o) {
        budgetThirdPartyCostShareCopyIndicator = o;
    }

    /**
     * budgetThirdPartyCostShareCopyIndicator
     */
    public boolean getBudgetThirdPartyCostShareCopyIndicator() {
        return budgetThirdPartyCostShareCopyIndicator;
    }

    /**
     * o
     */
    public void setBudgetOriginAgencyAmount(KualiInteger o) {
        budgetOriginAgencyAmount = o;
    }

    /**
     * budgetOriginAgencyAmount
     */
    public KualiInteger getBudgetOriginAgencyAmount() {
        return budgetOriginAgencyAmount;
    }

    /**
     * o
     */
    public void setBudgetOriginInstitutionCostShareAmount(KualiInteger o) {
        budgetOriginInstitutionCostShareAmount = o;
    }

    /**
     * budgetOriginInstitutionCostShareAmount
     */
    public KualiInteger getBudgetOriginInstitutionCostShareAmount() {
        return budgetOriginInstitutionCostShareAmount;
    }

    /**
     * o
     */
    public void setBudgetOriginThirdPartyCostShareAmount(KualiInteger o) {
        budgetOriginThirdPartyCostShareAmount = o;
    }

    /**
     * budgetOriginThirdPartyCostShareAmount
     */
    public KualiInteger getBudgetOriginThirdPartyCostShareAmount() {
        return budgetOriginThirdPartyCostShareAmount;
    }

    /**
     * o
     */
    public void setNonpersonnelObjectCode(NonpersonnelObjectCode o) {
        nonpersonnelObjectCode = o;
    }

    /**
     * nonpersonnelObjectCode
     */
    public NonpersonnelObjectCode getNonpersonnelObjectCode() {
        return nonpersonnelObjectCode;
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("budgetTaskSequenceNumber", this.budgetTaskSequenceNumber);
        m.put("budgetPeriodSequenceNumber", this.budgetPeriodSequenceNumber);
        m.put("budgetNonpersonnelCategoryCode", this.budgetNonpersonnelCategoryCode);
        m.put("budgetNonpersonnelSequenceNumber", this.budgetNonpersonnelSequenceNumber);
        m.put("budgetNonpersonnelSubCategoryCode", this.budgetNonpersonnelSubCategoryCode);
        return m;

    }

    /**
     * Gets the budgetNonpersonnelCategoryCode attribute.
     * 
     * @return Returns the budgetNonpersonnelCategoryCode.
     */
    public String getBudgetNonpersonnelCategoryCode() {
        return budgetNonpersonnelCategoryCode;
    }

    /**
     * Sets the budgetNonpersonnelCategoryCode attribute value.
     * 
     * @param budgetNonpersonnelCategoryCode The budgetNonpersonnelCategoryCode to set.
     */
    public void setBudgetNonpersonnelCategoryCode(String budgetNonpersonnelCategoryCode) {
        this.budgetNonpersonnelCategoryCode = budgetNonpersonnelCategoryCode;
    }

    /**
     * Gets the budgetNonpersonnelSubCategoryCode attribute.
     * 
     * @return Returns the budgetNonpersonnelSubCategoryCode.
     */
    public String getBudgetNonpersonnelSubCategoryCode() {
        return budgetNonpersonnelSubCategoryCode;
    }

    /**
     * Sets the budgetNonpersonnelSubCategoryCode attribute value.
     * 
     * @param budgetNonpersonnelSubCategoryCode The budgetNonpersonnelSubCategoryCode to set.
     */
    public void setBudgetNonpersonnelSubCategoryCode(String budgetNonpersonnelSubCategoryCode) {
        this.budgetNonpersonnelSubCategoryCode = budgetNonpersonnelSubCategoryCode;
    }

    /**
     * Gets the subcontractorNumber attribute.
     * 
     * @return Returns the subcontractorNumber.
     */
    public String getSubcontractorNumber() {
        return subcontractorNumber;
    }

    /**
     * Sets the subcontractorNumber attribute value.
     * 
     * @param subcontractorNumber The subcontractorNumber to set.
     */
    public void setSubcontractorNumber(String subcontractorNumber) {
        this.subcontractorNumber = subcontractorNumber;
    }

    /**
     * Gets the copyToFuturePeriods attribute.
     * 
     * @return Returns the copyToFuturePeriods.
     */
    public boolean getCopyToFuturePeriods() {
        return copyToFuturePeriods;
    }

    /**
     * Sets the copyToFuturePeriods attribute value.
     * 
     * @param copyToFuturePeriods The copyToFuturePeriods to set.
     */
    public void setCopyToFuturePeriods(boolean copyToFuturePeriods) {
        this.copyToFuturePeriods = copyToFuturePeriods;
    }

    /**
     * Gets the agencyRequestAmountBackup attribute.
     * 
     * @return Returns the agencyRequestAmountBackup.
     */
    public KualiInteger getAgencyRequestAmountBackup() {
        return agencyRequestAmountBackup;
    }

    /**
     * Sets the agencyRequestAmountBackup attribute value.
     * 
     * @param agencyRequestAmountBackup The agencyRequestAmountBackup to set.
     */
    public void setAgencyRequestAmountBackup(KualiInteger agencyRequestAmountDuplicate) {
        this.agencyRequestAmountBackup = agencyRequestAmountDuplicate;
    }

    /**
     * Gets the budgetThirdPartyCostShareAmountBackup attribute.
     * 
     * @return Returns the budgetThirdPartyCostShareAmountBackup.
     */
    public KualiInteger getBudgetThirdPartyCostShareAmountBackup() {
        return budgetThirdPartyCostShareAmountBackup;
    }

    /**
     * Sets the budgetThirdPartyCostShareAmountBackup attribute value.
     * 
     * @param budgetThirdPartyCostShareAmountBackup The budgetThirdPartyCostShareAmountBackup to set.
     */
    public void setBudgetThirdPartyCostShareAmountBackup(KualiInteger budgetThirdPartyCostShareAmountDuplicate) {
        this.budgetThirdPartyCostShareAmountBackup = budgetThirdPartyCostShareAmountDuplicate;
    }

    /**
     * Gets the budgetInstitutionCostShareAmountBackup attribute.
     * 
     * @return Returns the budgetInstitutionCostShareAmountBackup.
     */
    public KualiInteger getBudgetInstitutionCostShareAmountBackup() {
        return budgetInstitutionCostShareAmountBackup;
    }

    /**
     * Sets the budgetInstitutionCostShareAmountBackup attribute value.
     * 
     * @param budgetInstitutionCostShareAmountBackup The budgetInstitutionCostShareAmountBackup to set.
     */
    public void setBudgetInstitutionCostShareAmountBackup(KualiInteger budgetInstitutionCostShareAmountDuplicate) {
        this.budgetInstitutionCostShareAmountBackup = budgetInstitutionCostShareAmountDuplicate;
    }

    /**
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    public int compareTo(Object o) {
        BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) o;
        return this.budgetNonpersonnelSequenceNumber.compareTo(budgetNonpersonnel.getBudgetNonpersonnelSequenceNumber());
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object obj) {
        boolean equals = true;

        if (ObjectUtils.isNotNull(obj) && obj instanceof BudgetNonpersonnel) {
            BudgetNonpersonnel objCompare = (BudgetNonpersonnel) obj;
            equals &= this.documentNumber.equals(objCompare.getDocumentNumber());
            equals &= this.agencyCopyIndicator == objCompare.getAgencyCopyIndicator();
            equals &= this.budgetThirdPartyCostShareCopyIndicator == objCompare.getBudgetThirdPartyCostShareCopyIndicator();
            equals &= this.budgetInstitutionCostShareCopyIndicator == objCompare.getBudgetInstitutionCostShareCopyIndicator();

            if (this.agencyRequestAmount == null && objCompare.getAgencyRequestAmount() == null) {
            }
            else {
                equals &= this.agencyRequestAmount != null && objCompare.getAgencyRequestAmount() != null && this.agencyRequestAmount.equals(objCompare.getAgencyRequestAmount());
            }
            if (this.budgetNonpersonnelCategoryCode == null && objCompare.getBudgetNonpersonnelCategoryCode() == null) {
            }
            else {
                equals &= this.budgetNonpersonnelCategoryCode != null && objCompare.getBudgetNonpersonnelCategoryCode() != null && this.budgetNonpersonnelCategoryCode.equals(objCompare.getBudgetNonpersonnelCategoryCode());
            }
            if (this.budgetNonpersonnelDescription == null && objCompare.getBudgetNonpersonnelDescription() == null) {
            }
            else {
                equals &= this.budgetNonpersonnelDescription != null && objCompare.getBudgetNonpersonnelDescription() != null && this.budgetNonpersonnelDescription.equals(objCompare.getBudgetNonpersonnelDescription());
            }
            if (this.budgetNonpersonnelSequenceNumber == null && objCompare.getBudgetNonpersonnelSequenceNumber() == null) {
            }
            else {
                equals &= this.budgetNonpersonnelSequenceNumber != null && objCompare.getBudgetNonpersonnelSequenceNumber() != null && this.budgetNonpersonnelSequenceNumber.equals(objCompare.getBudgetNonpersonnelSequenceNumber());
            }
            if (this.budgetNonpersonnelSubCategoryCode == null && objCompare.getBudgetNonpersonnelSubCategoryCode() == null) {
            }
            else {
                equals &= this.budgetNonpersonnelSubCategoryCode != null && objCompare.getBudgetNonpersonnelSubCategoryCode() != null && this.budgetNonpersonnelSubCategoryCode.equals(objCompare.getBudgetNonpersonnelSubCategoryCode());
            }
            if (this.budgetOriginAgencyAmount == null && objCompare.getBudgetOriginAgencyAmount() == null) {
            }
            else {
                equals &= this.budgetOriginAgencyAmount != null && objCompare.getBudgetOriginAgencyAmount() != null && this.budgetOriginAgencyAmount.equals(objCompare.getBudgetOriginAgencyAmount());
            }
            if (this.budgetOriginSequenceNumber == null && objCompare.getBudgetOriginSequenceNumber() == null) {
            }
            else {
                equals &= this.budgetOriginSequenceNumber != null && objCompare.getBudgetOriginSequenceNumber() != null && this.budgetOriginSequenceNumber.equals(objCompare.getBudgetOriginSequenceNumber());
            }
            if (this.budgetOriginThirdPartyCostShareAmount == null && objCompare.getBudgetOriginThirdPartyCostShareAmount() == null) {
            }
            else {
                equals &= this.budgetOriginThirdPartyCostShareAmount != null && objCompare.getBudgetOriginThirdPartyCostShareAmount() != null && this.budgetOriginThirdPartyCostShareAmount.equals(objCompare.getBudgetOriginThirdPartyCostShareAmount());
            }
            if (this.budgetOriginInstitutionCostShareAmount == null && objCompare.getBudgetOriginInstitutionCostShareAmount() == null) {
            }
            else {
                equals &= this.budgetOriginInstitutionCostShareAmount != null && objCompare.getBudgetOriginInstitutionCostShareAmount() != null && this.budgetOriginInstitutionCostShareAmount.equals(objCompare.getBudgetOriginInstitutionCostShareAmount());
            }
            if (this.budgetPeriodSequenceNumber == null && objCompare.getBudgetPeriodSequenceNumber() == null) {
            }
            else {
                equals &= this.budgetPeriodSequenceNumber != null && objCompare.getBudgetPeriodSequenceNumber() != null && this.budgetPeriodSequenceNumber.equals(objCompare.getBudgetPeriodSequenceNumber());
            }
            if (ObjectUtils.isNull(this.budgetTaskSequenceNumber) && ObjectUtils.isNull(objCompare.getBudgetTaskSequenceNumber())) {
            }
            else {
                equals &= ObjectUtils.isNotNull(this.budgetTaskSequenceNumber) && ObjectUtils.isNotNull(objCompare.getBudgetTaskSequenceNumber()) && this.budgetTaskSequenceNumber.equals(objCompare.getBudgetTaskSequenceNumber());
            }
            if (this.budgetThirdPartyCostShareAmount == null && objCompare.getBudgetThirdPartyCostShareAmount() == null) {
            }
            else {
                equals &= this.budgetThirdPartyCostShareAmount != null && objCompare.getBudgetThirdPartyCostShareAmount() != null && this.budgetThirdPartyCostShareAmount.equals(objCompare.getBudgetThirdPartyCostShareAmount());
            }
            if (this.budgetInstitutionCostShareAmount == null && objCompare.getBudgetInstitutionCostShareAmount() == null) {
            }
            else {
                equals &= this.budgetInstitutionCostShareAmount != null && objCompare.getBudgetInstitutionCostShareAmount() != null && this.budgetInstitutionCostShareAmount.equals(objCompare.getBudgetInstitutionCostShareAmount());
            }
            if (this.subcontractorNumber == null && objCompare.getSubcontractorNumber() == null) {
            }
            else {
                equals &= this.subcontractorNumber != null && objCompare.getSubcontractorNumber() != null && this.subcontractorNumber.equals(objCompare.getSubcontractorNumber());
            }
        }
        else {
            return false;
        }

        return equals;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        String hashString = this.getDocumentNumber() + "|" + this.getBudgetNonpersonnelCategoryCode() + "|" + this.getBudgetNonpersonnelDescription() + "|" + this.getBudgetNonpersonnelSubCategoryCode() + "|" + this.getDocumentNumber() + "|" + this.getSubcontractorNumber() + "|" + this.getAgencyRequestAmount().toString() + "|" + this.getAgencyRequestAmountBackup().toString() + "|" + this.getBudgetNonpersonnelSequenceNumber().toString() + "|" + this.getBudgetOriginAgencyAmount().toString() + "|" + this.getBudgetPeriodSequenceNumber().toString() + "|" + this.getBudgetTaskSequenceNumber().toString() + "|" + this.getBudgetOriginSequenceNumber().toString() + "|" + this.getBudgetOriginThirdPartyCostShareAmount().toString() + "|" + this.getBudgetOriginInstitutionCostShareAmount().toString() + "|" + this.getBudgetInstitutionCostShareAmountBackup().toString() + "|" + this.getNonpersonnelObjectCode().toString();
        return hashString.hashCode();
    }
}
