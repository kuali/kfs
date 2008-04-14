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
package org.kuali.module.kra.budget.web.struts.form;

import java.math.BigDecimal;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;

/**
 * This is a wrapper for BudgetNonpersonnel business object. It's useful on the copy over page because it assists in housing and
 * computing the inflation rates.
 */
public class BudgetNonpersonnelCopyOverBoHelper extends BudgetNonpersonnel {
    private static final long serialVersionUID = -4975734069081678964L;

    private KualiInteger budgetInflatedAgencyAmount;
    private KualiInteger budgetInflatedInstitutionCostShareAmount;
    private KualiInteger budgetInflatedThirdPartyCostShareAmount;

    /**
     * Constructs a BudgetNonpersonnelCopyOverBoHelper. Default, no arg constructor
     */
    public BudgetNonpersonnelCopyOverBoHelper() {
        // to ensure an empty item like this is not considered an origin item. See
        // this.isOriginItem
        this.setBudgetNonpersonnelSequenceNumber(new Integer(-1));
    }

    /**
     * <p>
     * This does a 1:1 copy from BudgetNonpersonnel to BudgetNonpersonnelCopyOverBoHelper, with the exception of:
     * </p>
     * 1. If copyToFuturePeriods is set, it sets the three amount indicators.<br>
     * 2. The three inflation amounts are set to the regular amounts (since origin inflated amount = origin amount).<br>
     * <p>
     * Useful for origin items.
     * </p>
     * 
     * @param budgetNonpersonnel object to copy
     */
    public BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel budgetNonpersonnel) {
        super(budgetNonpersonnel); // This does a 1:1 copy

        // 1. set the three amount indicators if copyToFuturePeriods is set. This is to accommodate
        // functional requirements on the interface. Note that copyToFuturePeriods also needs
        // to be copied so that "following copy over items" know that the origin has
        // copyToFuturePeriods set.
        if (budgetNonpersonnel.getCopyToFuturePeriods()) {
            this.setAgencyCopyIndicator(true);
            this.setBudgetInstitutionCostShareCopyIndicator(true);
            this.setBudgetThirdPartyCostShareCopyIndicator(true);
        }

        // 2.
        this.setBudgetInflatedAgencyAmount(budgetNonpersonnel.getAgencyRequestAmount());
        this.setBudgetInflatedInstitutionCostShareAmount(budgetNonpersonnel.getBudgetInstitutionCostShareAmount());
        this.setBudgetInflatedThirdPartyCostShareAmount(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount());
    }

    /**
     * <p>
     * This does a 1:1 copy from BudgetNonpersonnel to BudgetNonpersonnelCopyOverBoHelper and populates inflation values based on
     * origin values, with the exception of:
     * </p> - Blanks out the three types of amounts based on whether the appropriate indicator are set. This is a functional
     * requirement<br>
     * <p>
     * Useful for items that are not origin items but already exist at the time of entering the copy over page (so either they were
     * created in the past or they were entered on NPRS).
     * </p>
     * 
     * @param budgetNonpersonnel object to copy
     * @param inflationLength
     * @param budgetNonpersonnelInflationRate inflation rate to use
     */
    public BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel budgetNonpersonnel, int inflationLength, KualiDecimal budgetNonpersonnelInflationRate) {
        super(budgetNonpersonnel); // This does a 1:1 copy

        // Set amounts to 0 if the indicator is set. Functionally this means if the indicator is checked and
        // they uncheck it, they see 0 instead of the nonpersonnel screen value.
        if (budgetNonpersonnel.getAgencyCopyIndicator()) {
            this.setAgencyRequestAmount(KualiInteger.ZERO);
        }
        if (budgetNonpersonnel.getBudgetInstitutionCostShareCopyIndicator()) {
            this.setBudgetThirdPartyCostShareAmount(KualiInteger.ZERO);
        }
        if (budgetNonpersonnel.getBudgetThirdPartyCostShareCopyIndicator()) {
            this.setBudgetInstitutionCostShareAmount(KualiInteger.ZERO);
        }

        // calculate inflation based on origin amounts per method specification.
        // Can always be done because of t=0 then it's the original value.
        // details here: http://en.wikipedia.org/wiki/Interest
        BigDecimal inflationFactor = budgetNonpersonnelInflationRate.bigDecimalValue().divide(BigDecimal.valueOf(100), 8, KualiDecimal.ROUND_BEHAVIOR).add(BigDecimal.valueOf(1));

        KualiInteger tmpBudgetAgencyAmount = this.getBudgetOriginAgencyAmount().multiply(new BigDecimal(Math.pow(inflationFactor.doubleValue(), inflationLength)));
        this.setBudgetInflatedAgencyAmount(tmpBudgetAgencyAmount);

        KualiInteger tmpBudgetInstitutionCostShareAmount = this.getBudgetOriginInstitutionCostShareAmount().multiply(new BigDecimal(Math.pow(inflationFactor.doubleValue(), inflationLength)));
        this.setBudgetInflatedInstitutionCostShareAmount(tmpBudgetInstitutionCostShareAmount);

        KualiInteger tmpBudgetThirdPartyCostShareAmount = this.getBudgetOriginThirdPartyCostShareAmount().multiply(new BigDecimal(Math.pow(inflationFactor.doubleValue(), inflationLength)));
        this.setBudgetInflatedThirdPartyCostShareAmount(tmpBudgetThirdPartyCostShareAmount);
    }

    /**
     * <p>
     * This does a 1:1 copy from BudgetNonpersonnel to BudgetNonpersonnelCopyOverBoHelper, with the exception of:
     * </p>
     * 1. Updates the period number with budgetPeriodSequenceNumberOverride.<br>
     * 2. Sets the amounts in this object to 0.<br>
     * 3. Sets the budgetOriginSequenceNumber to budgetNonpersonnelSequenceNumber.<br>
     * 4. Sets the origin amounts to the values of originBudgetNonpersonnel passed in.<br>
     * 5. If copyToFuturePeriods is set, it sets the three amount indicators.<br>
     * 6. Sets budgetNonpersonnelSequenceNumber to null because this item does not exist yet (don't use -1 because then it might be
     * mistaken as a pre-origin item by iterface).<br>
     * 7. Populates inflation values based on BudgetNonpersonnel amounts.<br>
     * <p>
     * Useful for non-origin items that have not been created yet.
     * </p>
     * 
     * @param originBudgetNonpersonnel object to copy (origin item)
     * @param budgetPeriodSequenceNumberOverride values used for the new budgetPeriodSequenceNumber value
     * @param inflationLength
     * @param budgetNonpersonnelInflationRate inflation rate to use
     */
    public BudgetNonpersonnelCopyOverBoHelper(BudgetNonpersonnel originBudgetNonpersonnel, Integer budgetPeriodSequenceNumberOverride, int inflationLength, KualiDecimal budgetNonpersonnelInflationRate) {
        super(originBudgetNonpersonnel); // This does a 1:1 copy

        // 1.
        this.setBudgetPeriodSequenceNumber(budgetPeriodSequenceNumberOverride);

        // 2.
        this.setAgencyRequestAmount(KualiInteger.ZERO);
        this.setBudgetThirdPartyCostShareAmount(KualiInteger.ZERO);
        this.setBudgetInstitutionCostShareAmount(KualiInteger.ZERO);

        // 3.
        this.setBudgetOriginSequenceNumber(originBudgetNonpersonnel.getBudgetNonpersonnelSequenceNumber());

        // 4.
        this.setBudgetOriginAgencyAmount(originBudgetNonpersonnel.getAgencyRequestAmount());
        this.setBudgetOriginInstitutionCostShareAmount(originBudgetNonpersonnel.getBudgetInstitutionCostShareAmount());
        this.setBudgetOriginThirdPartyCostShareAmount(originBudgetNonpersonnel.getBudgetThirdPartyCostShareAmount());

        // 5. set the three amount indicators if copyToFuturePeriods is set. This is to accomondate
        // functional requirements on the interface. Note that copyToFuturePeriods also needs
        // to be copied so that "following copy over items" know that the origin has
        // copyToFuturePeriods set.
        if (originBudgetNonpersonnel.getCopyToFuturePeriods()) {
            this.setAgencyCopyIndicator(true);
            this.setBudgetInstitutionCostShareCopyIndicator(true);
            this.setBudgetThirdPartyCostShareCopyIndicator(true);
        }

        // 6. (needs to happen late because 3 above uses this)
        this.setBudgetNonpersonnelSequenceNumber(null);

        // 7. Note: Does not use origin amounts like inflation calculation in other constructor.

        // calculate inflation based on amounts per method specification
        BigDecimal inflationFactor = budgetNonpersonnelInflationRate.bigDecimalValue().divide(BigDecimal.valueOf(100), 8, KualiDecimal.ROUND_BEHAVIOR).add(BigDecimal.valueOf(1));

        KualiInteger tmpBudgetAgencyAmount = this.getBudgetOriginAgencyAmount().multiply(new BigDecimal(Math.pow(inflationFactor.doubleValue(), inflationLength)));
        this.setBudgetInflatedAgencyAmount(tmpBudgetAgencyAmount);

        KualiInteger tmpBudgetInstitutionCostShareAmount = this.getBudgetOriginInstitutionCostShareAmount().multiply(new BigDecimal(Math.pow(inflationFactor.doubleValue(), inflationLength)));
        this.setBudgetInflatedInstitutionCostShareAmount(tmpBudgetInstitutionCostShareAmount);

        KualiInteger tmpBudgetThirdPartyCostShareAmount = this.getBudgetOriginThirdPartyCostShareAmount().multiply(new BigDecimal(Math.pow(inflationFactor.doubleValue(), inflationLength)));
        this.setBudgetInflatedThirdPartyCostShareAmount(tmpBudgetThirdPartyCostShareAmount);
    }

    /**
     * Gets the BudgetNonpersonnel representation of this object. Uses inflation values if appropriate indicators are set.
     * 
     * @return the BudgetNonpersonnel representation of this object.
     */
    public BudgetNonpersonnel getBudgetNonpersonnel() {
        // Check if inflations values have to be used.
        if (this.getAgencyCopyIndicator()) {
            this.setAgencyRequestAmount(budgetInflatedAgencyAmount);
        }
        if (this.getBudgetInstitutionCostShareCopyIndicator()) {
            this.setBudgetInstitutionCostShareAmount(budgetInflatedInstitutionCostShareAmount);
        }
        if (this.getBudgetThirdPartyCostShareCopyIndicator()) {
            this.setBudgetThirdPartyCostShareAmount(budgetInflatedThirdPartyCostShareAmount);
        }

        this.setCopyToFuturePeriods(false); // nothing to do with copy over, just make sure the checkbox is unchecked if "save" is
        // used to copy over items on NPRS page

        return (BudgetNonpersonnel) new BudgetNonpersonnel(this); // Don't just return "this" because giving a
        // BudgetNonpersonnelCopyOverBoHelper confuses OJB.
    }

    /**
     * Gets the agencyRequestAmount or budgetInflatedAgencyAmount attribute based on if agencyCopyIndicator is set or not.
     * 
     * @return amount per logic.
     */
    public KualiDecimal getDisplayAgencyRequestAmount() {
        return this.getAgencyCopyIndicator() ? new KualiDecimal(this.getBudgetInflatedAgencyAmount().longValue()) : new KualiDecimal(this.getAgencyRequestAmount().longValue());
    }

    /**
     * Gets the budgetInstitutionCostShareAmount or budgetInstitutionCostShareAmount attribute based on if
     * budgetInstitutionCostShareCopyIndicator is set or not.
     * 
     * @return amount per logic.
     */
    public KualiDecimal getDisplayBudgetInstitutionCostShareAmount() {
        return this.getBudgetInstitutionCostShareCopyIndicator() ? new KualiDecimal(this.getBudgetInflatedInstitutionCostShareAmount().longValue()) : new KualiDecimal(this.getBudgetInstitutionCostShareAmount().longValue());
    }

    /**
     * Gets the budgetThirdPartyCostShareCopyAmount or budgetInflatedThirdPartyCostShareCopyAmount attribute based on if
     * budgetThirdPartyCostShareCopyIndicator is set or not.
     * 
     * @return amount per logic.
     */
    public KualiDecimal getDisplayBudgetThirdPartyCostShareAmount() {
        return this.getBudgetThirdPartyCostShareCopyIndicator() ? new KualiDecimal(this.getBudgetInflatedThirdPartyCostShareAmount().longValue()) : new KualiDecimal(this.getBudgetThirdPartyCostShareAmount().longValue());
    }

    /**
     * Gets the budgetInflatedAgencyAmount attribute.
     * 
     * @return Returns the budgetInflatedAgencyAmount.
     */
    public KualiInteger getBudgetInflatedAgencyAmount() {
        return budgetInflatedAgencyAmount;
    }

    /**
     * Sets the budgetInflatedAgencyAmount attribute value.
     * 
     * @param budgetInflatedAgencyAmount The budgetInflatedAgencyAmount to set.
     */
    public void setBudgetInflatedAgencyAmount(KualiInteger o) {
        budgetInflatedAgencyAmount = o;
    }

    /**
     * Sets the budgetInflatedInstitutionCostShareAmount attribute value.
     * 
     * @param budgetInflatedInstitutionCostShareAmount The budgetInflatedInstitutionCostShareAmount to set.
     */
    public void setBudgetInflatedInstitutionCostShareAmount(KualiInteger o) {
        budgetInflatedInstitutionCostShareAmount = o;
    }

    /**
     * Gets the budgetInflatedInstitutionCostShareAmount attribute.
     * 
     * @return Returns the budgetInflatedInstitutionCostShareAmount.
     */
    public KualiInteger getBudgetInflatedInstitutionCostShareAmount() {
        return budgetInflatedInstitutionCostShareAmount;
    }

    /**
     * Sets the budgetInflatedThirdPartyCostShareAmount attribute value.
     * 
     * @param budgetInflatedThirdPartyCostShareAmount The budgetInflatedThirdPartyCostShareAmount to set.
     */
    public void setBudgetInflatedThirdPartyCostShareAmount(KualiInteger o) {
        budgetInflatedThirdPartyCostShareAmount = o;
    }

    /**
     * Gets the budgetInflatedThirdPartyCostShareAmount attribute.
     * 
     * @return Returns the budgetInflatedThirdPartyCostShareAmount.
     */
    public KualiInteger getBudgetInflatedThirdPartyCostShareAmount() {
        return budgetInflatedThirdPartyCostShareAmount;
    }
}
