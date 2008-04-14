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
package org.kuali.module.kra.budget.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.service.ParameterService;
import org.kuali.kfs.service.impl.ParameterConstants;
import org.kuali.module.kra.KraConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetBaseCode;
import org.kuali.module.kra.budget.bo.BudgetIndirectCost;
import org.kuali.module.kra.budget.bo.BudgetIndirectCostLookup;
import org.kuali.module.kra.budget.bo.BudgetModularPeriod;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetTask;
import org.kuali.module.kra.budget.bo.BudgetTaskPeriodIndirectCost;
import org.kuali.module.kra.budget.bo.IndirectCostLookup;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.service.BudgetIndirectCostService;
import org.kuali.module.kra.budget.service.BudgetModularService;

public class BudgetIndirectCostServiceImpl implements BudgetIndirectCostService {

    private BudgetModularService budgetModularService;
    private ParameterService parameterService;
    private BusinessObjectService businessObjectService;

    /**
     * Generate our task/period list items based on idc data. Each task/period list item is basically a mapping between one task and
     * one period ordered by sequence number. This should not be called for existing budgets, because we do not want to overwrite
     * existing data.
     * 
     * @param BudgetDocument budgetDocument
     */
    private void createTaskPeriodIdcList(BudgetDocument budgetDocument) {
        // Get our tasks and periods lists from our budget.
        List tasks = budgetDocument.getBudget().getTasks();
        List periods = budgetDocument.getBudget().getPeriods();

        // Get our idc object from the budget as well.
        BudgetIndirectCost idc = budgetDocument.getBudget().getIndirectCost();
        if (idc == null) {
            idc = new BudgetIndirectCost(budgetDocument.getBudget().getDocumentNumber());
            budgetDocument.getBudget().setIndirectCost(idc);
        }

        // Now we want to loop through our tasks, and for each task we want to add
        // multiple taskPeriod items to our idc's collection.
        for (Iterator taskIterator = tasks.iterator(); taskIterator.hasNext();) {
            BudgetTask task = (BudgetTask) taskIterator.next();

            for (Iterator periodIterator = periods.iterator(); periodIterator.hasNext();) {
                BudgetPeriod period = (BudgetPeriod) periodIterator.next();

                // Create our new taskPeriod.
                BudgetTaskPeriodIndirectCost taskPeriod = new BudgetTaskPeriodIndirectCost();

                // Set our parameters.
                taskPeriod.setDocumentNumber(idc.getDocumentNumber() != null ? idc.getDocumentNumber() : null);
                taskPeriod.setBudgetTaskSequenceNumber(task.getBudgetTaskSequenceNumber());
                taskPeriod.setBudgetPeriodSequenceNumber(period.getBudgetPeriodSequenceNumber());
                taskPeriod.setTask(task);
                taskPeriod.setPeriod(period);

                // Make sure that this item doesn't already exist in the list.
                if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(idc.getBudgetTaskPeriodIndirectCostItems(), taskPeriod)) {
                    // Tack it on to the collection.
                    idc.getBudgetTaskPeriodIndirectCostItems().add(taskPeriod);
                }
            }
        }

        // Make sure our list is sorted.
        Collections.sort(budgetDocument.getBudget().getIndirectCost().getBudgetTaskPeriodIndirectCostItems());
    }

    /**
     * Calculate personnel and non-personnel agency request amounts for a given task/period. This method will loop over personnel /
     * nonpersonnel items to gather the total amount requested for the given BudgetTaskPeriodIndirectCost (taskPeriod).
     * 
     * @param budgetDocument
     */
    private KualiInteger calculateTotalDirectCost(BudgetTaskPeriodIndirectCost taskPeriod, BudgetDocument budgetDocument) {
        Budget budget = budgetDocument.getBudget();

        List userAppointmentTaskPeriods = budget.getAllUserAppointmentTaskPeriods(false);
        List nonPersonnelItems = budget.getNonpersonnelItems();
        List<BudgetNonpersonnel> temporaryNonPersonnelItems = new ArrayList<BudgetNonpersonnel>();

        // Task / Period totals for nonpersonnel, personnel and modular.
        KualiInteger personnelTotal = calculatePersonnelTotalDirectCost(userAppointmentTaskPeriods, taskPeriod, temporaryNonPersonnelItems);
        KualiInteger nonPersonnelTotal = KualiInteger.ZERO;
        KualiInteger modularTotal = KualiInteger.ZERO;

        // Loop over nonpersonnel items to get the total amount requested for this taskPeriod.
        for (Iterator nonPersonnelIterator = nonPersonnelItems.iterator(); nonPersonnelIterator.hasNext();) {
            BudgetNonpersonnel nonPersonnelItem = (BudgetNonpersonnel) nonPersonnelIterator.next();

            // If our task and period sequence numbers match, add the value to the total.
            if (nonPersonnelItem.getBudgetTaskSequenceNumber().equals(taskPeriod.getBudgetTaskSequenceNumber()) && nonPersonnelItem.getBudgetPeriodSequenceNumber().equals(taskPeriod.getBudgetPeriodSequenceNumber())) {
                nonPersonnelTotal = nonPersonnelTotal.add(nonPersonnelItem.getAgencyRequestAmount());
            }
        }

        // Now add temporary nonpersonnel amounts that may have been added as a side-effect of personnel
        for (BudgetNonpersonnel tempNonpersonnelItem : temporaryNonPersonnelItems) {
            if (tempNonpersonnelItem.getBudgetTaskSequenceNumber().equals(taskPeriod.getBudgetTaskSequenceNumber()) && tempNonpersonnelItem.getBudgetPeriodSequenceNumber().equals(taskPeriod.getBudgetPeriodSequenceNumber())) {
                nonPersonnelTotal = nonPersonnelTotal.add(tempNonpersonnelItem.getAgencyRequestAmount());
            }
        }

        // Include modular variance if we have a modular budget and there are values for module variance in the BudgetModularPeriod
        // list.
        if (budget.isAgencyModularIndicator()) {
            budget.refreshReferenceObject("budgetAgency");
            budgetModularService.generateModularBudget(budget);
            List modularPeriods = budgetDocument.getBudget().getModularBudget().getBudgetModularPeriods();

            for (Iterator modularIterator = modularPeriods.iterator(); modularIterator.hasNext();) {
                BudgetModularPeriod modularPeriod = (BudgetModularPeriod) modularIterator.next();

                // Add to the modular total only if the period matches, and the task is marked as including modular variance.
                if (taskPeriod.getBudgetPeriodSequenceNumber().equals(modularPeriod.getBudgetPeriodSequenceNumber()) && taskPeriod.getBudgetTaskSequenceNumber().equals(budget.getModularBudget().getBudgetModularTaskNumber())) {
                    modularTotal = modularTotal.add(modularPeriod.getModularVarianceAmount());
                }
            }
        }

        // After we've iterated over all possible userAppointmentTaskPeriods and nonpersonnelItems, we set our total.
        return personnelTotal.add(nonPersonnelTotal.add(modularTotal));
    }

    private KualiInteger calculateBaseCost(BudgetTaskPeriodIndirectCost taskPeriod, BudgetDocument budgetDocument) {
        return calculateBaseCost(taskPeriod, budgetDocument.getBudget().getIndirectCost().getBudgetBaseCode(), budgetDocument);
    }

    /**
     * Calculate base cost amounts based on the budget base code for a given task/period. This calculation is made based on the
     * budget base, and follows the following logic: TDC - The value calculated in column 1 Standard Base/Modified TDC (MTDC) - TDC
     * (Column) minus 'Excluded' Non-Personnel Expenses; Nonpersonnel Expenses (BO) have a single Nonpersonnel Object Code (BO)
     * which has a single Nonpersonnel SubCategory Code (BO), which has an indicator called nonpersonnelMtdcExcludedIndicator which,
     * if true, should be excluded from MTDC calculation Manual - No calculated amount, text-box shows up on UI for users to
     * manually enter the Base s
     * 
     * @param budgetDocument
     */
    private KualiInteger calculateBaseCost(BudgetTaskPeriodIndirectCost taskPeriod, String baseCode, BudgetDocument budgetDocument) {
        Budget budget = budgetDocument.getBudget();

        List userAppointmentTaskPeriods = budget.getAllUserAppointmentTaskPeriods(false);
        List nonPersonnelItems = budget.getNonpersonnelItems();
        List<BudgetNonpersonnel> temporaryNonPersonnelItems = new ArrayList<BudgetNonpersonnel>();

        KualiInteger baseCost = KualiInteger.ZERO;

        // Task / Period totals for nonpersonnel and personnel.
        KualiInteger personnelTotal = KualiInteger.ZERO;
        KualiInteger nonPersonnelTotal = KualiInteger.ZERO;
        KualiInteger modularTotal = KualiInteger.ZERO;

        // If there is no base, or if the base is manual, we don't need any calculations.
        if (baseCode != null && !KraConstants.MANUAL_BASE.equals(baseCode)) {

            personnelTotal = calculatePersonnelTotalDirectCost(userAppointmentTaskPeriods, taskPeriod, temporaryNonPersonnelItems);

            // Loop over nonpersonnel items to get the total amount requested for this taskPeriod.
            for (Iterator nonPersonnelIterator = nonPersonnelItems.iterator(); nonPersonnelIterator.hasNext();) {
                BudgetNonpersonnel nonPersonnelItem = (BudgetNonpersonnel) nonPersonnelIterator.next();

                // If our task and period sequence numbers match, add the value to the total.
                if (nonPersonnelItem.getBudgetTaskSequenceNumber().equals(taskPeriod.getBudgetTaskSequenceNumber()) && nonPersonnelItem.getBudgetPeriodSequenceNumber().equals(taskPeriod.getBudgetPeriodSequenceNumber())) {

                    // If we have a MTDC base, we need to exclude certain nonpersonnel items.
                    // But first, test to see if the base code is null or not MTDC.
                    if (!KraConstants.MODIFIED_TOTAL_DIRECT_COST.equals(baseCode) || nonPersonnelItem.getNonpersonnelObjectCode() == null || !nonPersonnelItem.getNonpersonnelObjectCode().getNonpersonnelSubCategory().isNonpersonnelMtdcExcludedIndicator()) {
                        nonPersonnelTotal = nonPersonnelTotal.add(nonPersonnelItem.getAgencyRequestAmount());
                    }
                }
            }

            // Now add temporary nonpersonnel amounts that may have been added as a side-effect of personnel
            for (BudgetNonpersonnel tempNonpersonnelItem : temporaryNonPersonnelItems) {
                if (tempNonpersonnelItem.getBudgetTaskSequenceNumber().equals(taskPeriod.getBudgetTaskSequenceNumber()) && tempNonpersonnelItem.getBudgetPeriodSequenceNumber().equals(taskPeriod.getBudgetPeriodSequenceNumber())) {
                    if (!KraConstants.MODIFIED_TOTAL_DIRECT_COST.equals(baseCode) || tempNonpersonnelItem.getNonpersonnelObjectCode() == null || !tempNonpersonnelItem.getNonpersonnelObjectCode().getNonpersonnelSubCategory().isNonpersonnelMtdcExcludedIndicator()) {
                        nonPersonnelTotal = nonPersonnelTotal.add(tempNonpersonnelItem.getAgencyRequestAmount());
                    }
                }
            }

            // Include modular variance if we have a modular budget and there are values for module variance in the
            // BudgetModularPeriod list.
            if (budget.isAgencyModularIndicator()) {
                budget.refreshReferenceObject("budgetAgency");
                budgetModularService.generateModularBudget(budget);
                List modularPeriods = budgetDocument.getBudget().getModularBudget().getBudgetModularPeriods();

                for (Iterator modularIterator = modularPeriods.iterator(); modularIterator.hasNext();) {
                    BudgetModularPeriod modularPeriod = (BudgetModularPeriod) modularIterator.next();

                    // Add to the modular total only if the period matches, and the task is marked as including modular variance.
                    if (taskPeriod.getBudgetPeriodSequenceNumber().equals(modularPeriod.getBudgetPeriodSequenceNumber()) && taskPeriod.getBudgetTaskSequenceNumber().equals(budget.getModularBudget().getBudgetModularTaskNumber())) {
                        modularTotal = modularTotal.add(modularPeriod.getModularVarianceAmount());
                    }
                }
            }

            // After we've iterated over all possible userAppointmentTaskPeriods and nonpersonnelItems, we set our total.
            baseCost = personnelTotal.add(nonPersonnelTotal.add(modularTotal));
        }
        else if (KraConstants.MANUAL_BASE.equals(baseCode)) {
            baseCost = new KualiInteger(taskPeriod.getBudgetManualMtdcAmount() != null ? taskPeriod.getBudgetManualMtdcAmount().intValue() : 0);
        }

        return baseCost;
    }

    private KualiInteger calculatePersonnelTotalDirectCost(List userAppointmentTaskPeriods, BudgetTaskPeriodIndirectCost taskPeriod, List<BudgetNonpersonnel> temporaryNonpersonnelItems) {

        KualiInteger personnelTotal = KualiInteger.ZERO;

        List<String> graduateAssistantAppointmentTypes = parameterService.getParameterValues(BudgetDocument.class, KraConstants.KRA_BUDGET_PERSONNEL_GRADUATE_RESEARCH_ASSISTANT_APPOINTMENT_TYPES);

        String graduateAssistentNonpersonnelCategoryCode = parameterService.getParameterValue(ParameterConstants.RESEARCH_ADMINISTRATION_DOCUMENT.class, KraConstants.GRADUATE_ASSISTANT_NONPERSONNEL_CATEGORY_CODE);
        String graduateAssistantNonpesonnelSubcategoryCode = parameterService.getParameterValue(ParameterConstants.RESEARCH_ADMINISTRATION_DOCUMENT.class, KraConstants.GRADUATE_ASSISTANT_NONPERSONNEL_SUB_CATEGORY_CODE);

        // Loop over user appointments to get the total amount requested for this taskPeriod.
        for (Iterator userAppointmentTaskPeriodIterator = userAppointmentTaskPeriods.iterator(); userAppointmentTaskPeriodIterator.hasNext();) {
            UserAppointmentTaskPeriod userAppointmentTaskPeriod = (UserAppointmentTaskPeriod) userAppointmentTaskPeriodIterator.next();

            // If our task and period sequence numbers match, add the value to the total.
            if (userAppointmentTaskPeriod.getBudgetTaskSequenceNumber().equals(taskPeriod.getBudgetTaskSequenceNumber()) && userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber().equals(taskPeriod.getBudgetPeriodSequenceNumber())) {
                // Have to look in a different place for grad lines.
                if (graduateAssistantAppointmentTypes.contains(userAppointmentTaskPeriod.getInstitutionAppointmentTypeCode())) {
                    personnelTotal = personnelTotal.add(userAppointmentTaskPeriod.getAgencySalaryAmount());
                    personnelTotal = personnelTotal.add(userAppointmentTaskPeriod.getAgencyHealthInsuranceAmount());

                    // If it is a GA, we need to add Nonpersonnel Fee Remission to Nonpersonnel list (not stored in database).
                    BudgetNonpersonnel budgetNonpersonnel = new BudgetNonpersonnel(userAppointmentTaskPeriod.getBudgetTaskSequenceNumber(), userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber(), graduateAssistentNonpersonnelCategoryCode, graduateAssistantNonpesonnelSubcategoryCode, "", userAppointmentTaskPeriod.getAgencyRequestedFeesAmount(), userAppointmentTaskPeriod.getInstitutionRequestedFeesAmount());
                    budgetNonpersonnel.refreshReferenceObject("nonpersonnelObjectCode");
                    budgetNonpersonnel.getNonpersonnelObjectCode().refreshReferenceObject("nonpersonnelSubCategory");

                    temporaryNonpersonnelItems.add(budgetNonpersonnel);

                }
                else {
                    personnelTotal = personnelTotal.add(userAppointmentTaskPeriod.getAgencyRequestTotalAmount());
                    personnelTotal = personnelTotal.add(userAppointmentTaskPeriod.getAgencyFringeBenefitTotalAmount());
                }
            }
        }

        return personnelTotal;
    }

    private KualiDecimal getIndirectCostRate(BudgetTaskPeriodIndirectCost taskPeriod, BudgetDocument budgetDocument, boolean overrideManualRateIndicator) {
        // First, we attempt to pull an existing rate from the taskPeriod.
        KualiDecimal rate = taskPeriod.getBudgetManualIndirectCostRate();

        // If the existing rate is not already set, we need to look it up based on budgetTaskOnCampus and budgetPurposeCode.
        // The corresponding table is ER_IDC_LU_T.
        if (overrideManualRateIndicator || "N".equals(budgetDocument.getBudget().getIndirectCost().getBudgetManualRateIndicator())) {
            BudgetIndirectCostLookup tempBicl = new BudgetIndirectCostLookup();
            tempBicl.setDocumentNumber(budgetDocument.getBudget().getDocumentNumber());
            tempBicl.setBudgetOnCampusIndicator(taskPeriod.getTask().isBudgetTaskOnCampus());
            tempBicl.setBudgetPurposeCode(budgetDocument.getBudget().getIndirectCost().getBudgetPurposeCode());

            if (ObjectUtils.collectionContainsObjectWithIdentitcalKey(budgetDocument.getBudget().getBudgetIndirectCostLookups(), tempBicl)) {
                rate = ((BudgetIndirectCostLookup) ObjectUtils.retrieveObjectWithIdentitcalKey(budgetDocument.getBudget().getBudgetIndirectCostLookups(), tempBicl)).getBudgetIndirectCostRate();
            }
            else {
                IndirectCostLookup idcLookup = (IndirectCostLookup) businessObjectService.retrieve(new IndirectCostLookup(taskPeriod.getTask().isBudgetTaskOnCampus(), budgetDocument.getBudget().getIndirectCost().getBudgetPurposeCode()));
                rate = idcLookup.getBudgetIndirectCostRate();
                tempBicl.setBudgetIndirectCostRate(rate);
                budgetDocument.getBudget().getBudgetIndirectCostLookups().add(tempBicl);
            }
        }

        return rate;
    }

    private KualiDecimal getIndirectCostRate(BudgetTaskPeriodIndirectCost taskPeriod, BudgetDocument budgetDocument) {
        return getIndirectCostRate(taskPeriod, budgetDocument, false);
    }

    /**
     * Calculate cost share base.
     * 
     * @param taskPeriod
     * @param budgetDocument
     */
    private KualiInteger calculateCostShareBaseCost(BudgetTaskPeriodIndirectCost taskPeriod, BudgetDocument budgetDocument) {
        // Task / Period totals for nonpersonnel and personnel.
        KualiInteger personnelTotal = KualiInteger.ZERO;
        KualiInteger nonPersonnelTotal = KualiInteger.ZERO;

        if (budgetDocument.getBudget().getIndirectCost().getBudgetIndirectCostCostShareIndicator()) {
            List<String> graduateAssistantAppointmentTypes = parameterService.getParameterValues(BudgetDocument.class, KraConstants.KRA_BUDGET_PERSONNEL_GRADUATE_RESEARCH_ASSISTANT_APPOINTMENT_TYPES);
            String graduateAssistentNonpersonnelCategoryCode = parameterService.getParameterValue(ParameterConstants.RESEARCH_ADMINISTRATION_DOCUMENT.class, KraConstants.GRADUATE_ASSISTANT_NONPERSONNEL_CATEGORY_CODE);
            String graduateAssistantNonpersonnelSubcategoryCode = parameterService.getParameterValue(ParameterConstants.RESEARCH_ADMINISTRATION_DOCUMENT.class, KraConstants.GRADUATE_ASSISTANT_NONPERSONNEL_SUB_CATEGORY_CODE);

            List<BudgetNonpersonnel> temporaryNonpersonnelItems = new ArrayList<BudgetNonpersonnel>();

            // Loop over user appointments to get the total amount requested for this taskPeriod.
            for (UserAppointmentTaskPeriod userAppointmentTaskPeriod : budgetDocument.getBudget().getAllUserAppointmentTaskPeriods(false)) {
                if (userAppointmentTaskPeriod.getBudgetTaskSequenceNumber().equals(taskPeriod.getBudgetTaskSequenceNumber()) && userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber().equals(taskPeriod.getBudgetPeriodSequenceNumber())) {
                    // If our task and period sequence numbers match, add the value to the total.
                    if (graduateAssistantAppointmentTypes.contains(userAppointmentTaskPeriod.getInstitutionAppointmentTypeCode())) {
                        personnelTotal = personnelTotal.add(userAppointmentTaskPeriod.getInstitutionSalaryAmount());
                        personnelTotal = personnelTotal.add(userAppointmentTaskPeriod.getInstitutionHealthInsuranceAmount());

                        // If it is a GA, we need to add Nonpersonnel Fee Remission to Nonpersonnel list (not stored in database).
                        BudgetNonpersonnel budgetNonpersonnel = new BudgetNonpersonnel(userAppointmentTaskPeriod.getBudgetTaskSequenceNumber(), userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber(), graduateAssistentNonpersonnelCategoryCode, graduateAssistantNonpersonnelSubcategoryCode, "", userAppointmentTaskPeriod.getAgencyRequestedFeesAmount(), userAppointmentTaskPeriod.getInstitutionRequestedFeesAmount());
                        budgetNonpersonnel.refreshReferenceObject("nonpersonnelObjectCode");
                        budgetNonpersonnel.getNonpersonnelObjectCode().refreshReferenceObject("nonpersonnelSubCategory");

                        temporaryNonpersonnelItems.add(budgetNonpersonnel);

                    }
                    else {
                        personnelTotal = personnelTotal.add(userAppointmentTaskPeriod.getInstitutionCostShareRequestTotalAmount());
                        personnelTotal = personnelTotal.add(userAppointmentTaskPeriod.getInstitutionCostShareFringeBenefitTotalAmount());
                    }
                }
            }

            // Loop over nonpersonnel items to get the total amount requested for this taskPeriod.
            for (BudgetNonpersonnel nonPersonnelItem : budgetDocument.getBudget().getNonpersonnelItems()) {

                // If our task and period sequence numbers match, add the value to the total.
                if (nonPersonnelItem.getBudgetTaskSequenceNumber().equals(taskPeriod.getBudgetTaskSequenceNumber()) && nonPersonnelItem.getBudgetPeriodSequenceNumber().equals(taskPeriod.getBudgetPeriodSequenceNumber())) {
                    if (nonPersonnelItem.getNonpersonnelObjectCode() == null || !nonPersonnelItem.getNonpersonnelObjectCode().getNonpersonnelSubCategory().isNonpersonnelMtdcExcludedIndicator()) {
                        nonPersonnelTotal = nonPersonnelTotal.add(nonPersonnelItem.getBudgetInstitutionCostShareAmount());
                    }
                }
            }

            // Now add temporary nonpersonnel amounts that may have been added as a side-effect of personnel
            for (BudgetNonpersonnel tempNonpersonnelItem : temporaryNonpersonnelItems) {
                if (tempNonpersonnelItem.getBudgetTaskSequenceNumber().equals(taskPeriod.getBudgetTaskSequenceNumber()) && tempNonpersonnelItem.getBudgetPeriodSequenceNumber().equals(taskPeriod.getBudgetPeriodSequenceNumber())) {
                    if (tempNonpersonnelItem.getNonpersonnelObjectCode() == null || !tempNonpersonnelItem.getNonpersonnelObjectCode().getNonpersonnelSubCategory().isNonpersonnelMtdcExcludedIndicator()) {
                        nonPersonnelTotal = nonPersonnelTotal.add(tempNonpersonnelItem.getAgencyRequestAmount());
                    }
                }
            }
        } // else 0 because budgetIndirectCostCostShareIndicator == false

        // After we've iterated over all possible userAppointmentTaskPeriods and nonpersonnelItems, we set our total.
        return personnelTotal.add(nonPersonnelTotal);
    }

    /**
     * Calculates Cost Share Indirect Cost. Get the system rate to calculate this, it is always coming from the system.
     * 
     * @param taskPeriod
     * @param budgetDocument
     */
    private KualiInteger calculateCostShareIndirectCost(BudgetTaskPeriodIndirectCost taskPeriod, BudgetDocument budgetDocument) {
        KualiDecimal rate = taskPeriod.getCostShareIndirectCostRate();
        KualiInteger costShareCalculatedIndirectCost = KualiInteger.ZERO;

        if (budgetDocument.getBudget().getIndirectCost().getBudgetIndirectCostCostShareIndicator()) {
            costShareCalculatedIndirectCost = new KualiInteger(taskPeriod.getCostShareBaseCost().multiply(rate).divide(new KualiInteger(100)));
        }

        return costShareCalculatedIndirectCost;
    }

    /**
     * Calculate unrecovered indirect cost. This is the difference between what the IDC would be with the system rates and what it
     * would be with a manual rate. This can only be calculated when a manual rate has been chosen.
     */
    private KualiInteger calculateCostShareUnrecoveredIndirectCost(BudgetTaskPeriodIndirectCost taskPeriod, BudgetDocument budgetDocument) {
        KualiDecimal rate = getIndirectCostRate(taskPeriod, budgetDocument, true);
        KualiInteger costShareUnrecoveredIndirectCost = KualiInteger.ZERO;

        if (budgetDocument.getBudget().isInstitutionCostShareIndicator() && "Y".equals(budgetDocument.getBudget().getIndirectCost().getBudgetManualRateIndicator()) && budgetDocument.getBudget().getIndirectCost().isBudgetUnrecoveredIndirectCostIndicator()) {
            costShareUnrecoveredIndirectCost = new KualiInteger(calculateBaseCost(taskPeriod, KraConstants.MODIFIED_TOTAL_DIRECT_COST, budgetDocument).multiply(rate).divide(new KualiInteger(100))).subtract(taskPeriod.getCalculatedIndirectCost());
        }
        return costShareUnrecoveredIndirectCost;
    }

    /**
     * Iterate over all task/period lines and calculate Idc values based on personnel/non-personnel expenses.
     * 
     * @param budgetDocument
     */
    private void calculateTaskPeriodIdcListValues(BudgetDocument budgetDocument) {

        // Attempt to pull the idc object. If it doesn't exist yet we are dealing with a new
        // or rotten budget, so we will have to create it.
        BudgetIndirectCost idc = budgetDocument.getBudget().getIndirectCost();
        if (idc == null) {
            this.createTaskPeriodIdcList(budgetDocument);
        }

        // We should now have a list, since createTaskPeriodIdcList would have
        // set it up for us just now, or on any previous save.
        List idcItems = budgetDocument.getBudget().getIndirectCost().getBudgetTaskPeriodIndirectCostItems();

        // Iterate over all existing idc list items and calculate the appropriate values for each taskPeriodLine.
        for (Iterator idcItemsIterator = idcItems.iterator(); idcItemsIterator.hasNext();) {
            BudgetTaskPeriodIndirectCost taskPeriod = (BudgetTaskPeriodIndirectCost) idcItemsIterator.next();

            taskPeriod.setTotalDirectCost(this.calculateTotalDirectCost(taskPeriod, budgetDocument));
            taskPeriod.setBaseCost(this.calculateBaseCost(taskPeriod, budgetDocument));
            taskPeriod.setIndirectCostRate(this.getIndirectCostRate(taskPeriod, budgetDocument));
            taskPeriod.setCalculatedIndirectCost(new KualiInteger(taskPeriod.getBaseCost().multiply(taskPeriod.getIndirectCostRate()).divide(new KualiInteger(100))));
            taskPeriod.setCostShareBaseCost(this.calculateCostShareBaseCost(taskPeriod, budgetDocument));
            taskPeriod.setCostShareIndirectCostRate(this.getIndirectCostRate(taskPeriod, budgetDocument, true));
            taskPeriod.setCostShareCalculatedIndirectCost(this.calculateCostShareIndirectCost(taskPeriod, budgetDocument));
            taskPeriod.setCostShareUnrecoveredIndirectCost(calculateCostShareUnrecoveredIndirectCost(taskPeriod, budgetDocument));
        }
    }

    /**
     * Verify that our IDC object and our taskPeriodIDC objects all match what is found in the budget. We don't want to have
     * mismatched task/period sequence numbers.
     * 
     * @param budgetDocument
     */
    public void reconcileIndirectCost(BudgetDocument budgetDocument) {
        this.cleanseIndirectCost(budgetDocument);
        if (!budgetDocument.getBudget().isInstitutionCostShareIndicator() && !budgetDocument.getBudget().isBudgetThirdPartyCostShareIndicator() && budgetDocument.getBudget().getIndirectCost() != null) {
            budgetDocument.getBudget().getIndirectCost().setBudgetIndirectCostCostShareIndicator(false);
            budgetDocument.getBudget().getIndirectCost().setBudgetUnrecoveredIndirectCostIndicator(false);
        }
        this.createTaskPeriodIdcList(budgetDocument);
    }

    /**
     * Refresh an IndirectCost object.
     * 
     * @0param budgetDocument
     */
    public void refreshIndirectCost(BudgetDocument budgetDocument) {
        if (budgetDocument.getBudget().getIndirectCost() != null && budgetDocument.getBudget().getIndirectCost().getBudgetTaskPeriodIndirectCostItems() != null) {
            BudgetIndirectCost budgetIndirectCost = budgetDocument.getBudget().getIndirectCost();
            List taskPeriodItems = budgetIndirectCost.getBudgetTaskPeriodIndirectCostItems();

            for (Iterator i = taskPeriodItems.iterator(); i.hasNext();) {
                BudgetTaskPeriodIndirectCost taskPeriod = (BudgetTaskPeriodIndirectCost) i.next();

                taskPeriod.refreshReferenceObject("task");
                taskPeriod.refreshReferenceObject("period");
            }

            budgetDocument.getBudget().refreshReferenceObject("personnel");
            budgetDocument.getBudget().refreshReferenceObject("nonpersonnelItems");
            calculateTaskPeriodIdcListValues(budgetDocument);

            if ("N".equals(budgetIndirectCost.getBudgetManualRateIndicator())) {
                budgetIndirectCost.setBudgetManualRateIndicatorDescription(parameterService.getParameterValue(BudgetDocument.class, KraConstants.KRA_BUDGET_INDIRECT_COST_PROVIDED_SYSTEM));
            }
            else {
                budgetIndirectCost.setBudgetManualRateIndicatorDescription(parameterService.getParameterValue(BudgetDocument.class, KraConstants.KRA_BUDGET_INDIRECT_COST_PROVIDED_MANUALLY));
            }
        }
    }

    /**
     * Cleanse the task period list to make sure we don't have orphaned task period items.
     * 
     * @param budgetDocument
     */
    private void cleanseIndirectCost(BudgetDocument budgetDocument) {
        // if there are no lines in the list, then it's likely the first time that the save is occurring. We don't care about
        // cleansing in that case.
        if (budgetDocument.getBudget().getIndirectCost() != null && budgetDocument.getBudget().getIndirectCost().getBudgetTaskPeriodIndirectCostItems() != null) {
            List taskPeriodItems = budgetDocument.getBudget().getIndirectCost().getBudgetTaskPeriodIndirectCostItems();
            List budgetTasks = budgetDocument.getBudget().getTasks();
            List budgetPeriods = budgetDocument.getBudget().getPeriods();

            for (Iterator i = taskPeriodItems.iterator(); i.hasNext();) {
                BudgetTaskPeriodIndirectCost taskPeriod = (BudgetTaskPeriodIndirectCost) i.next();

                BudgetTask budgetTask = (BudgetTask) businessObjectService.retrieve(new BudgetTask(taskPeriod.getDocumentNumber(), taskPeriod.getBudgetTaskSequenceNumber()));

                BudgetPeriod budgetPeriod = (BudgetPeriod) businessObjectService.retrieve(new BudgetPeriod(taskPeriod.getDocumentNumber(), taskPeriod.getBudgetPeriodSequenceNumber()));

                if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(budgetTasks, budgetTask) || !ObjectUtils.collectionContainsObjectWithIdentitcalKey(budgetPeriods, budgetPeriod)) {
                    i.remove();
                }
            }
        }
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetIndirectCostService#setupIndirectCostRates(org.kuali.module.kra.budget.bo.Budget)
     */
    public void setupIndirectCostRates(Budget budget) {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        List<IndirectCostLookup> indirectCostLookups = new ArrayList<IndirectCostLookup>(businessObjectService.findMatching(IndirectCostLookup.class, fieldValues));
        List<BudgetIndirectCostLookup> budgetIndirectCostLookupList = new ArrayList();
        for (IndirectCostLookup indirectCostLookup : indirectCostLookups) {
            budgetIndirectCostLookupList.add(new BudgetIndirectCostLookup(budget, indirectCostLookup));
        }
        budget.setBudgetIndirectCostLookups(budgetIndirectCostLookupList);
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetIndirectCostService#getDefaultBudgetBaseCodeValues()
     */
    public List<BudgetBaseCode> getDefaultBudgetBaseCodeValues() {
        Map fieldValues = new HashMap();
        fieldValues.put(KFSPropertyConstants.ACTIVE, KFSConstants.ACTIVE_INDICATOR);

        return new ArrayList(businessObjectService.findMatching(BudgetBaseCode.class, fieldValues));
    }

    /**
     * @return Returns the budgetModularService.
     */
    public BudgetModularService getBudgetModularService() {
        return budgetModularService;
    }

    /**
     * @param budgetModularService The budgetModularService to set.
     */
    public void setBudgetModularService(BudgetModularService budgetModularService) {
        this.budgetModularService = budgetModularService;
    }


    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
