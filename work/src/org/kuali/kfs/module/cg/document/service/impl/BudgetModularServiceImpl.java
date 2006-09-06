/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.kra.budget.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.module.cg.bo.Agency;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetModular;
import org.kuali.module.kra.budget.bo.BudgetModularPeriod;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.service.BudgetModularService;
import org.kuali.module.kra.budget.service.BudgetNonpersonnelService;
import org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelFormHelper;

/**
 * This class provides implementations for BudgetModularService interface
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetModularServiceImpl implements BudgetModularService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BudgetModularServiceImpl.class);

    private BudgetNonpersonnelService budgetNonpersonnelService;
    private ModularAgencyHelper modularAgencyHelper;

    /**
     * @see org.kuali.module.kra.budget.service.BudgetModularService#generateModularBudget(org.kuali.module.kra.budget.bo.Budget)
     */
    public void generateModularBudget(Budget budget) {

        List nonpersonnelCategories = new ArrayList();
        try {
            nonpersonnelCategories = budgetNonpersonnelService.getAllNonpersonnelCategories();
        }
        catch (Exception e) {
            LOG.error("Some exception retrieving nonpersonnelCategories.");
            throw new RuntimeException("Exception trying to retrieve nonpersonnelCategories: " + e);
        }

        generateModularBudget(budget, nonpersonnelCategories);
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetModularService#generateModularBudget(org.kuali.module.kra.budget.bo.Budget, List
     *      nonpersonnelCategories)
     */
    public void generateModularBudget(Budget budget, List nonpersonnelCategories) {

        BudgetModular modularBudget = initializeModularBudgetBaseValues(budget, nonpersonnelCategories);

        if (!isValidModularBudget(budget.getBudgetAgency(), modularBudget, budget.getPeriods().size())) {
            setupModularBudgetInvalidMode(modularBudget);
        }
        else {
            setupValidModularBudget(modularBudget);
        }
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetModularService#resetModularBudget(org.kuali.module.kra.budget.bo.Budget)
     */
    public void resetModularBudget(Budget budget) {

        budgetNonpersonnelService.refreshNonpersonnelObjectCode(budget.getNonpersonnelItems());

        List nonpersonnelCategories = new ArrayList();
        try {
            nonpersonnelCategories = budgetNonpersonnelService.getAllNonpersonnelCategories();
        }
        catch (Exception e) {
            LOG.error("Some exception retrieving nonpersonnelCategories.");
            throw new RuntimeException("Exception trying to retrieve nonpersonnelCategories: " + e);
        }

        BudgetModular modularBudget = initializeModularBudgetBaseValues(budget, nonpersonnelCategories);

        if (isValidModularBudget(budget.getBudgetAgency(), modularBudget, budget.getPeriods().size())) {
            modularBudget.setBudgetModularDirectCostAmount(determineModularDirectCost(modularBudget.getBudgetModularPeriods().size(), modularBudget));

            for (Iterator iter = budget.getModularBudget().getBudgetModularPeriods().iterator(); iter.hasNext();) {
                BudgetModularPeriod currentModularPeriod = (BudgetModularPeriod) iter.next();
                currentModularPeriod.setBudgetAdjustedModularDirectCostAmount(budget.getModularBudget().getBudgetModularDirectCostAmount());
            }
        }
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetModularService#agencySupportsModular(org.kuali.module.cg.bo.Agency)
     */
    public boolean agencySupportsModular(Agency agency) {
        if (ObjectUtils.isNull(agency)) {
            return false;
        }
        if (ObjectUtils.isNotNull(agency.getAgencyExtension())) {
            return agency.getAgencyExtension().isAgencyModularIndicator();
        }
        if (ObjectUtils.isNotNull(agency.getReportsToAgency())) {
            return agencySupportsModular(agency.getReportsToAgency());
        }
        return false;
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetModularService#determineBudgetPeriodMaximumAmount(org.kuali.module.cg.bo.Agency)
     */
    public KualiInteger determineBudgetPeriodMaximumAmount(Agency agency) {
        ModularAgencyHelper helper = getModularAgencyInformation(agency, new ModularAgencyHelper());
        return helper.getBudgetPeriodMaximumAmount();
    }

    /**
     * Setup initial Modular Budget values.
     * 
     * @param Budget budget
     * @param List nonpersonnelCategories
     * @return BudgetModular
     */
    private BudgetModular initializeModularBudgetBaseValues(Budget budget, List nonpersonnelCategories) {

        modularAgencyHelper = getModularAgencyInformation(budget.getBudgetAgency(), new ModularAgencyHelper());

        BudgetModular modularBudget;
        if (ObjectUtils.isNotNull(budget.getModularBudget())) { // If modular budget already present in budget, just rebuild that
                                                                // one.
            modularBudget = budget.getModularBudget();
            if (modularBudget.getBudgetModularIncrementAmount() == null) { // Might not be stored
                modularBudget.setBudgetModularIncrementAmount(modularAgencyHelper.getBudgetModularIncrementAmount());
            }
        }
        else { // First visit to Modular page - get a new Modular Budget.
            modularBudget = new BudgetModular(budget.getDocumentHeaderId());
            // This is stored, b/c if agency info changes, we still have to use the "legacy" increments if modular budget was
            // created using those values
            modularBudget.setBudgetModularIncrementAmount(modularAgencyHelper.getBudgetModularIncrementAmount());
            budget.setModularBudget(modularBudget);
        }

        modularBudget.setBudgetPeriodMaximumAmount(modularAgencyHelper.getBudgetPeriodMaximumAmount());
        calculateDirectCostAmountsByPeriod(budget, nonpersonnelCategories);
        modularBudget.setTotalActualDirectCostAmount(calculateTotalActualDirectCostAmount(modularBudget.getBudgetModularPeriods()));
        modularBudget.setIncrements(generateAgencyModularIncrements(modularBudget));
        return modularBudget;
    }

    /**
     * Setup appropriate values for an invalid ModularBudget.
     * 
     * @param BudgetModular modularBudget
     */
    private void setupModularBudgetInvalidMode(BudgetModular modularBudget) {

        List increments = new ArrayList();
        increments.add("0");
        modularBudget.setIncrements(increments);

        for (Iterator iter = modularBudget.getBudgetModularPeriods().iterator(); iter.hasNext();) {
            BudgetModularPeriod currentModularPeriod = (BudgetModularPeriod) iter.next();
            currentModularPeriod.setTotalPeriodDirectCostAmount(null);
        }

        modularBudget.setTotalConsortiumAmount(calculateTotalConsortiumAmount(modularBudget.getBudgetModularPeriods()));
        modularBudget.setTotalAdjustedModularDirectCostAmount(null);
        modularBudget.setTotalModularDirectCostAmount(null);
        modularBudget.setTotalDirectCostAmount(null);
        modularBudget.setBudgetModularDirectCostAmount(null);
        modularBudget.setInvalidMode(true);
    }

    /**
     * Setup appropriate values for an valid ModularBudget.
     * 
     * @param BudgetModular modularBudget
     */
    private void setupValidModularBudget(BudgetModular modularBudget) {
        modularBudget.setBudgetModularDirectCostAmount(determineModularDirectCost(modularBudget.getBudgetModularPeriods().size(), modularBudget));

        for (Iterator iter = modularBudget.getBudgetModularPeriods().iterator(); iter.hasNext();) {
            BudgetModularPeriod currentModularPeriod = (BudgetModularPeriod) iter.next();
            if (currentModularPeriod.getBudgetAdjustedModularDirectCostAmount() == null) {
                if (modularBudget.getBudgetModularDirectCostAmount().isNonZero()) {
                    currentModularPeriod.setBudgetAdjustedModularDirectCostAmount(modularBudget.getBudgetModularDirectCostAmount());
                }
                else {
                    currentModularPeriod.setBudgetAdjustedModularDirectCostAmount(new KualiInteger((String) modularBudget.getIncrements().get(0)));
                }
            }
            currentModularPeriod.setTotalPeriodDirectCostAmount(currentModularPeriod.getConsortiumAmount().add(currentModularPeriod.getBudgetAdjustedModularDirectCostAmount()));
        }

        modularBudget.setTotalModularDirectCostAmount(calculateTotalModularDirectCostAmount(modularBudget.getBudgetModularDirectCostAmount(), modularBudget.getBudgetModularPeriods()));
        modularBudget.setTotalAdjustedModularDirectCostAmount(calculateTotalAdjustedModularDirectCostAmount(modularBudget.getBudgetModularPeriods()));
        modularBudget.setTotalDirectCostAmount(calculateTotalDirectCostAmount(modularBudget.getBudgetModularPeriods()));
        modularBudget.setTotalConsortiumAmount(calculateTotalConsortiumAmount(modularBudget.getBudgetModularPeriods()));
        modularBudget.setInvalidMode(false);
    }

    /**
     * Setup Agency-related information needed for Modular Budget, based on given Agency.
     * 
     * @param Agency agency
     * @param ModularAgencyHelper helper
     * @return ModularAgencyHelper
     */
    private ModularAgencyHelper getModularAgencyInformation(Agency agency, ModularAgencyHelper helper) {
        if (ObjectUtils.isNotNull(agency.getAgencyExtension())) {
            if (helper.getBudgetModularIncrementAmount() == null && agency.getAgencyExtension().getBudgetModularIncrementAmount() != null) {
                helper.setBudgetModularIncrementAmount(agency.getAgencyExtension().getBudgetModularIncrementAmount());
            }
            if (helper.getBudgetPeriodMaximumAmount() == null && agency.getAgencyExtension().getBudgetPeriodMaximumAmount() != null) {
                helper.setBudgetPeriodMaximumAmount(agency.getAgencyExtension().getBudgetPeriodMaximumAmount());
            }
        }

        if (helper.isComplete()) {
            return helper;
        }
        else {
            return getModularAgencyInformation(agency.getReportsToAgency(), helper);
        }
    }

    /**
     * Generate list of modular increments.
     * 
     * @param BudgetModular mb
     * @return List
     */
    private List generateAgencyModularIncrements(BudgetModular mb) {
        List returnList = new ArrayList();

        if (mb.getTotalActualDirectCostAmount().isZero()) {
            returnList.add("0");
        }

        for (KualiInteger eval = mb.getBudgetModularIncrementAmount(); eval.compareTo(mb.getBudgetPeriodMaximumAmount()) <= 0; eval = eval.add(mb.getBudgetModularIncrementAmount())) {
            returnList.add(eval.toString());
        }

        return returnList;
    }

    /**
     * Calculate direct cost amounts by period for given Budget and nonpersonnel categories list.
     * 
     * @param Budget budget
     * @param List nonpersonnelCategories
     */
    private void calculateDirectCostAmountsByPeriod(Budget budget, List nonpersonnelCategories) {
        BudgetModular modularBudget = budget.getModularBudget();
        List periods = budget.getPeriods();

        Map directCostsMap = new HashMap();

        // setup hashmap
        for (Iterator iter = periods.iterator(); iter.hasNext();) {
            BudgetPeriod period = (BudgetPeriod) iter.next();
            directCostsMap.put(period.getBudgetPeriodSequenceNumber().toString(), new KualiInteger(0));
        }

        // calculate personnel direct costs
        List userTaskPeriodList = budget.getAllUserAppointmentTaskPeriods();
        for (Iterator iter = userTaskPeriodList.iterator(); iter.hasNext();) {
            UserAppointmentTaskPeriod currentUserAppointmentTaskPeriod = (UserAppointmentTaskPeriod) iter.next();
            String periodKey = currentUserAppointmentTaskPeriod.getBudgetPeriodSequenceNumber().toString();
            KualiInteger actualDirectCostAmountLessExcluded = (KualiInteger) directCostsMap.get(periodKey);

            actualDirectCostAmountLessExcluded = actualDirectCostAmountLessExcluded.add(currentUserAppointmentTaskPeriod.getAgencyRequestTotalAmount());

            actualDirectCostAmountLessExcluded = actualDirectCostAmountLessExcluded.add(currentUserAppointmentTaskPeriod.getAgencyFringeBenefitTotalAmount());

            directCostsMap.put(periodKey, actualDirectCostAmountLessExcluded);
        }

        // calculate nonpersonnel direct costs, then total all direct costs & set in BO
        for (int i = 0; i < periods.size(); i++) {
            BudgetPeriod period = (BudgetPeriod) periods.get(i);
            BudgetModularPeriod currentModularPeriod;
            if (modularBudget.getBudgetModularPeriods().size() > i) {
                currentModularPeriod = (BudgetModularPeriod) modularBudget.getBudgetModularPeriods().get(i);
            }
            else {
                currentModularPeriod = new BudgetModularPeriod(modularBudget.getDocumentHeaderId(), period.getBudgetPeriodSequenceNumber());
                modularBudget.getBudgetModularPeriods().add(currentModularPeriod);
            }

            KualiInteger actualDirectCostAmountTotal = new BudgetNonpersonnelFormHelper(new Integer(0), new Integer(i + 1), nonpersonnelCategories, budget.getNonpersonnelItems(), true).getNonpersonnelAgencyTotal();

            KualiInteger actualDirectCostAmountLessExcluded = new BudgetNonpersonnelFormHelper(new Integer(0), new Integer(i + 1), nonpersonnelCategories, budget.getNonpersonnelItems(), false).getNonpersonnelAgencyTotal();

            currentModularPeriod.setConsortiumAmount(actualDirectCostAmountTotal.subtract(actualDirectCostAmountLessExcluded));

            KualiInteger personnelDirectCostAmount = (KualiInteger) directCostsMap.get(period.getBudgetPeriodSequenceNumber().toString());
            actualDirectCostAmountLessExcluded = actualDirectCostAmountLessExcluded.add(personnelDirectCostAmount);

            currentModularPeriod.setActualDirectCostAmount(actualDirectCostAmountLessExcluded);
        }
    }

    /**
     * Determine the modular direct cost for given modular budget.
     * 
     * @param int numPeriods
     * @param BudgetModular modularBudget
     * @return KualiInteger
     */
    private KualiInteger determineModularDirectCost(int numPeriods, BudgetModular modularBudget) {
        KualiInteger returnVal = null;

        KualiInteger periods = new KualiInteger(numPeriods);
        KualiInteger budgetPeriodMaximumAmount = modularBudget.getBudgetPeriodMaximumAmount();
        KualiInteger budgetPeriodIncrementAmount = modularBudget.getBudgetModularIncrementAmount();

        BigDecimal rawPeriodActualDirectCost = modularBudget.getTotalActualDirectCostAmount().divide(periods);

        if (rawPeriodActualDirectCost.toBigInteger().remainder(budgetPeriodMaximumAmount.bigIntegerValue()).intValue() > 0) {

            KualiInteger absoluteCeiling = budgetPeriodMaximumAmount.multiply(periods);

            for (KualiInteger eval = new KualiInteger(0); eval.compareTo(absoluteCeiling) <= 0 && returnVal == null; eval = eval.add(budgetPeriodIncrementAmount)) {

                if (eval.compareTo(new KualiInteger(rawPeriodActualDirectCost)) >= 0) {
                    returnVal = eval;
                } // end if
            } // end for-loop
        }
        else {
            returnVal = new KualiInteger(rawPeriodActualDirectCost);
        } // end if/else
        return returnVal;
    }

    /**
     * Calculate the total actual direct cost for this budget.
     * 
     * @param List budgetModularPeriods
     * @return KualiInteger
     */
    private KualiInteger calculateTotalActualDirectCostAmount(List budgetModularPeriods) {
        KualiInteger total = new KualiInteger(0);
        for (Iterator iter = budgetModularPeriods.iterator(); iter.hasNext();) {
            BudgetModularPeriod currentModularPeriod = (BudgetModularPeriod) iter.next();
            total = total.add(currentModularPeriod.getActualDirectCostAmount());
        }
        return total;
    }

    /**
     * Calculate the total modular direct cost for this budget.
     * 
     * @param KualiInteger budgetModularDirectCostAmount
     * @param List budgetModularPeriods
     * @return KualiInteger
     */
    private KualiInteger calculateTotalModularDirectCostAmount(KualiInteger budgetModularDirectCostAmount, List budgetModularPeriods) {
        return budgetModularDirectCostAmount.multiply(new KualiInteger(budgetModularPeriods.size()));
    }

    /**
     * Calculate the total adjusted modular direct cost for this budget.
     * 
     * @param List budgetModularPeriods
     * @return KualiInteger
     */
    private KualiInteger calculateTotalAdjustedModularDirectCostAmount(List budgetModularPeriods) {
        KualiInteger total = new KualiInteger(0);
        for (Iterator iter = budgetModularPeriods.iterator(); iter.hasNext();) {
            BudgetModularPeriod currentModularPeriod = (BudgetModularPeriod) iter.next();
            total = total.add(currentModularPeriod.getBudgetAdjustedModularDirectCostAmount());
        }
        return total;
    }

    /**
     * Calculate the total consortium/F&A cost for this budget.
     * 
     * @param List budgetModularPeriods
     * @return KualiInteger
     */
    private KualiInteger calculateTotalConsortiumAmount(List budgetModularPeriods) {
        KualiInteger total = new KualiInteger(0);
        for (Iterator iter = budgetModularPeriods.iterator(); iter.hasNext();) {
            BudgetModularPeriod currentModularPeriod = (BudgetModularPeriod) iter.next();
            total = total.add(currentModularPeriod.getConsortiumAmount());
        }
        return total;
    }

    /**
     * Calculate the total direct cost for this budget.
     * 
     * @param List budgetModularPeriods
     * @return KualiInteger
     */
    private KualiInteger calculateTotalDirectCostAmount(List budgetModularPeriods) {
        KualiInteger total = new KualiInteger(0);
        for (Iterator iter = budgetModularPeriods.iterator(); iter.hasNext();) {
            BudgetModularPeriod currentModularPeriod = (BudgetModularPeriod) iter.next();
            total = total.add(currentModularPeriod.getTotalPeriodDirectCostAmount());
        }
        return total;
    }

    /**
     * Determine whether given Modular Budget is valid.
     * 
     * @param Agency agency
     * @param BudgetModular modularBudget
     * @param int numPeriods
     * @return KualiInteger
     */
    private boolean isValidModularBudget(Agency agency, BudgetModular modularBudget, int numPeriods) {
        // Total direct cost amount is greater than the number of periods times the period maximum
        KualiInteger periodMaximum = modularAgencyHelper.getBudgetPeriodMaximumAmount();
        KualiInteger absoluteCeiling = new KualiInteger(numPeriods).multiply(periodMaximum);
        KualiInteger actualAmount = modularBudget.getTotalActualDirectCostAmount();
        return actualAmount.isLessEqual(absoluteCeiling);
    }

    /**
     * Setter for BudgetNonpersonnelService property.
     * 
     * @param BudgetNonpersonnelService budgetNonpersonnelService
     */
    public void setBudgetNonpersonnelService(BudgetNonpersonnelService budgetNonpersonnelService) {
        this.budgetNonpersonnelService = budgetNonpersonnelService;
    }

    /**
     * This class encapsulates agency-related information needed to process Modular Budget objects.
     * 
     * @author Kuali Research Administration (kualidev@oncourse.iu.edu)
     */
    private class ModularAgencyHelper {
        private KualiInteger budgetModularIncrementAmount;
        private KualiInteger budgetPeriodMaximumAmount;

        public KualiInteger getBudgetModularIncrementAmount() {
            return budgetModularIncrementAmount;
        }

        public void setBudgetModularIncrementAmount(KualiInteger budgetModularIncrementAmount) {
            this.budgetModularIncrementAmount = budgetModularIncrementAmount;
        }

        public KualiInteger getBudgetPeriodMaximumAmount() {
            return budgetPeriodMaximumAmount;
        }

        public void setBudgetPeriodMaximumAmount(KualiInteger budgetPeriodMaximumAmount) {
            this.budgetPeriodMaximumAmount = budgetPeriodMaximumAmount;
        }

        public boolean isComplete() {
            return this.getBudgetModularIncrementAmount() != null && this.getBudgetPeriodMaximumAmount() != null;
        }
    }
}
