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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.core.document.Document;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.bo.Budget;
import org.kuali.module.kra.budget.bo.BudgetFringeRate;
import org.kuali.module.kra.budget.bo.BudgetModular;
import org.kuali.module.kra.budget.bo.BudgetModularPeriod;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetTask;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UserAppointmentTask;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.dao.BudgetPeriodDao;
import org.kuali.module.kra.budget.dao.BudgetTaskDao;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.service.BudgetCostShareService;
import org.kuali.module.kra.budget.service.BudgetFringeRateService;
import org.kuali.module.kra.budget.service.BudgetGraduateAssistantRateService;
import org.kuali.module.kra.budget.service.BudgetIndirectCostService;
import org.kuali.module.kra.budget.service.BudgetModularService;
import org.kuali.module.kra.budget.service.BudgetPersonnelService;
import org.kuali.module.kra.budget.service.BudgetService;
import org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverBoHelper;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetServiceImpl implements BudgetService {

    private BudgetFringeRateService budgetFringeRateService;
    private BudgetGraduateAssistantRateService budgetGraduateAssistantRateService;
    private BudgetPersonnelService budgetPersonnelService;
    private BudgetCostShareService budgetCostShareService;
    private BudgetTaskDao budgetTaskDao;
    private BudgetPeriodDao budgetPeriodDao;
    private DocumentService documentService;
    private BudgetModularService budgetModularService;
    private BudgetIndirectCostService budgetIndirectCostService;

    /**
     * @see org.kuali.module.kra.budget.service.BudgetService#initializeBudget(org.kuali.module.kra.budget.bo.Budget)
     */
    public void initializeBudget(BudgetDocument budgetDocument) {
        Budget budget = budgetDocument.getBudget();
        budgetFringeRateService.setupDefaultFringeRates(budget);
        budgetGraduateAssistantRateService.setupDefaultGradAssistantRates(budget);
        budgetIndirectCostService.setupIndirectCostRates(budget);
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetService#prepareBudgetForSave(org.kuali.module.kra.budget.document.BudgetDocument)
     */
    public void prepareBudgetForSave(BudgetDocument budgetDocument) throws WorkflowException {
        // Materialize tasks/periods if necessary (i.e., they are proxy objects currently)
        ObjectUtils.materializeObjects(budgetDocument.getBudget().getTasks());
        ObjectUtils.materializeObjects(budgetDocument.getBudget().getPeriods());

        Document databaseDocument = documentService.getByDocumentHeaderId(budgetDocument.getFinancialDocumentNumber());

        Budget budget = budgetDocument.getBudget();
        String documentHeaderId = budget.getDocumentHeaderId();
        List personnel = budget.getPersonnel();
        List universityCostSharePersonnelItems = budget.getUniversityCostSharePersonnelItems();

        if (budgetDocument.isCleanseBudgetOnSave()) {
            if (databaseDocument != null) {
                BudgetDocument databaseBudgetDocument = (BudgetDocument) databaseDocument;

                // First get rid of items that need to be deleted - cleansing should get rid items that are no longer valid because the
                // associated task/period are no longer in the list.
                cleanseNonpersonnel(budgetDocument);
                budgetPersonnelService.cleansePersonnel(budgetDocument);
                budgetCostShareService.cleanseCostShare(budget.isUniversityCostShareIndicator(), budget.getUniversityCostShareItems(), budget.isBudgetThirdPartyCostShareIndicator(), budget.getThirdPartyCostShareItems(), personnel, budget.getUniversityCostSharePersonnelItems());
                cleanseModular(budgetDocument);

                // Find what's changed that has a down-stream effect on other parts of the Budget, if anything, since the last save
                List modifiedPeriods = findModifiedPeriods(budgetDocument, databaseBudgetDocument);
                List modifiedFringeRates = findModifiedFringeRates(budgetDocument, databaseBudgetDocument);
                // List modifiedPersonnel = findModifiedPersonnel(budgetDocument, databaseBudgetDocument);

                boolean isPersonnelInflationRateModified = isPersonnelInflationRateModified(budgetDocument, databaseBudgetDocument);

                boolean isNonpersonnelInflationRateModified = isNonpersonnelInflationRateModified(budgetDocument, databaseBudgetDocument);

                boolean isInstitutionCostShareInclusionModified = isInstitutionCostShareInclusionModified(budgetDocument, databaseBudgetDocument);

                // Update effected Personnel/Nonpersonnel entries based on what's changed above
                if (modifiedPeriods.size() > 0 || modifiedFringeRates.size() > 0 || isPersonnelInflationRateModified) {
                    updatePersonnelDetail(budgetDocument, modifiedPeriods, modifiedFringeRates, isPersonnelInflationRateModified, isInstitutionCostShareInclusionModified);
                }

                if (isInstitutionCostShareInclusionModified && !isInstitutionCostShareIncludeBoxChecked(budgetDocument)) {
                    updatePersonnelCostShare(budgetDocument);
                }

                if (isNonpersonnelInflationRateModified || modifiedPeriods.size() > 0 || !(isInstitutionCostShareIncludeBoxChecked(budgetDocument)) || !(isThirdPartyCostShareIncludeBoxChecked(budgetDocument))) {
                    updateNonpersonnelDetail(budgetDocument, isNonpersonnelInflationRateModified, modifiedPeriods);
                }

                // Do these after personnel/nonpersonnel update
                List modifiedUserAppointmentTaskPeriods = findModifiedUserAppointmentTaskPeriods(budgetDocument, databaseBudgetDocument);
                List modifiedNonpersonnel = findModifiedNonpersonnel(budgetDocument, databaseBudgetDocument);

                if (budgetDocument.getBudget().isAgencyModularIndicator() && ((modifiedPeriods.size() > 0 || modifiedUserAppointmentTaskPeriods.size() > 0) || modifiedNonpersonnel.size() > 0)) {
                    updateModular(budgetDocument);
                }
            }
            
//          Add new data, based on changes that may have occurred prior to the save (e.g., Project Director from Parameters)
            budgetPersonnelService.reconcileProjectDirector(budgetDocument);

            // Add new Cost Share data based on personnel
            budgetCostShareService.reconcileCostShare(documentHeaderId, personnel, universityCostSharePersonnelItems);

            // Clean up indirect cost and task/period items.
            budgetIndirectCostService.reconcileIndirectCost(budgetDocument);
        }
    }

    /**
     * @see org.kuali.module.kra.budget.service.BudgetService#isCostShareInclusionModified(org.kuali.module.kra.budget.document.BudgetDocument)
     */
    public String buildCostShareRemovedCode(BudgetDocument budgetDocument) throws WorkflowException {
        BudgetDocument databaseBudgetDocument = (BudgetDocument) documentService.getByDocumentHeaderId(budgetDocument.getFinancialDocumentNumber());
        if (databaseBudgetDocument == null) {
            return "";
        }

        StringBuffer codes = new StringBuffer();
        if (databaseBudgetDocument.getBudget().isUniversityCostShareIndicator() && !budgetDocument.getBudget().isUniversityCostShareIndicator()) {
            codes.append(KraConstants.INSTITUTION_COST_SHARE_CODE);
        }
        if (databaseBudgetDocument.getBudget().isBudgetThirdPartyCostShareIndicator() && !budgetDocument.getBudget().isBudgetThirdPartyCostShareIndicator()) {
            codes.append(KraConstants.THIRD_PARTY_COST_SHARE_CODE);
        }
        return codes.toString();
    }

    /**
     * This method will determine if Third Party Cost Share has changed
     * 
     * @param budgetDocument
     * @param databaseBudgetDocument
     * @return boolean
     */
    private boolean isThirdPartyCostShareInclusionModified(BudgetDocument budgetDocument, BudgetDocument databaseBudgetDocument) {
        return !(budgetDocument.getBudget().isBudgetThirdPartyCostShareIndicator() == databaseBudgetDocument.getBudget().isBudgetThirdPartyCostShareIndicator());
    }

    /**
     * This method will determine if Institution Cost Share has changed
     * 
     * @param budgetDocument
     * @return
     */
    private boolean isInstitutionCostShareInclusionModified(BudgetDocument budgetDocument, BudgetDocument databaseBudgetDocument) {
        return !(budgetDocument.getBudget().isUniversityCostShareIndicator() == databaseBudgetDocument.getBudget().isUniversityCostShareIndicator());
    }

    /**
     * This method will determine if the institution cost share include box is checked.
     * 
     * @param budgetDocument
     * @return
     */
    private boolean isInstitutionCostShareIncludeBoxChecked(BudgetDocument budgetDocument) {
        return budgetDocument.getBudget().isUniversityCostShareIndicator();
    }

    /**
     * This method will determine if the 3rd party cost share include check box is checked.
     * 
     * @param budgetDocument
     * @return
     */
    private boolean isThirdPartyCostShareIncludeBoxChecked(BudgetDocument budgetDocument) {
        return budgetDocument.getBudget().isBudgetThirdPartyCostShareIndicator();
    }


    /**
     * This method will recalculate Nonpersonnel entries based on whether the nonpersonnel inflation rate has been modified, whether
     * institution cost share inclusion has changed, or whether third party cost share inclusion has been changed. In practice, this
     * will only apply to nonpersonnel entries that have been "copied forward" from previous periods.
     * 
     * @param budgetDocument
     * @param isNonpersonnelInflationRateModified identifies if the inflation rate changed
     * @param modifiedPeriods list of modified periods
     */
    private void updateNonpersonnelDetail(BudgetDocument budgetDocument, boolean isNonpersonnelInflationRateModified, List modifiedPeriods) {
        boolean isInstitutionCostShareCheckBoxChecked = isInstitutionCostShareIncludeBoxChecked(budgetDocument);
        boolean isThirdPartyCostShareCheckBoxChecked = isThirdPartyCostShareIncludeBoxChecked(budgetDocument);

        List nonpersonnelItemsList = new ArrayList(budgetDocument.getBudget().getNonpersonnelItems()); // necessary to make copy
                                                                                                        // because period fill up
                                                                                                        // below adds to the list,
                                                                                                        // want to avoid concurrent
                                                                                                        // modification exception
        List periods = budgetDocument.getBudget().getPeriods();
        KualiDecimal budgetNonpersonnelInflationRate = budgetDocument.getBudget().getBudgetNonpersonnelInflationRate();

        for (Iterator nonpersonnelItem = nonpersonnelItemsList.iterator(); nonpersonnelItem.hasNext();) {
            BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) nonpersonnelItem.next();

            // if Institution Cost Share check box or Third Party Cost share check boxes are un-checked,
            // set the corresponding amounts to zero.
            if (!(isInstitutionCostShareCheckBoxChecked)) {
                budgetNonpersonnel.setBudgetUniversityCostShareAmount(new KualiInteger(0));
            }
            if (!(isThirdPartyCostShareCheckBoxChecked)) {
                budgetNonpersonnel.setBudgetThirdPartyCostShareAmount(new KualiInteger(0));
            }

            // if periods were added and this is an origin item that has been copied over, we need to fill in a new item for each
            // new period
            if (modifiedPeriods.size() > 0 && budgetNonpersonnel.isOriginItem() && budgetNonpersonnel.isCopiedOverItem()) {
                // Iterate over each added period since each one needs new NPRS copy over items
                for (Iterator periodsIter = modifiedPeriods.iterator(); periodsIter.hasNext();) {
                    BudgetPeriod period = (BudgetPeriod) periodsIter.next();
                    // check if this is a new (added) period or truly modified
                    if (period.getVersionNumber() == null) {
                        int inflationLength = SpringServiceLocator.getBudgetPeriodService().getPeriodIndex(period.getBudgetPeriodSequenceNumber(), periods);

                        // Create new item
                        BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnelCopyOverBoHelper = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, period.getBudgetPeriodSequenceNumber(), inflationLength, budgetNonpersonnelInflationRate);

                        // indicators should always be false for these "new period" items
                        budgetNonpersonnelCopyOverBoHelper.setAgencyCopyIndicator(false);
                        budgetNonpersonnelCopyOverBoHelper.setBudgetUniversityCostShareCopyIndicator(false);
                        budgetNonpersonnelCopyOverBoHelper.setBudgetThirdPartyCostShareCopyIndicator(false);

                        // add it to the Budget Document per the standard methods provided.
                        BudgetNonpersonnel newNonpersonnel = budgetNonpersonnelCopyOverBoHelper.getBudgetNonpersonnel();
                        newNonpersonnel.setBudgetNonpersonnelSequenceNumber(budgetDocument.getNonpersonnelNextSequenceNumber());
                        budgetDocument.addNonpersonnel(newNonpersonnel);
                    }
                }
            }

            // if inflation rate changed, if it did we have to update copy over items only
            if (isNonpersonnelInflationRateModified && !budgetNonpersonnel.isOriginItem() && budgetNonpersonnel.isCopiedOverItem()) {
                // Figure the inflationLength (current item period seq#) and call the constructor which will calculate inflation
                // values for us
                int inflationLength = SpringServiceLocator.getBudgetPeriodService().getPeriodIndex(budgetNonpersonnel.getBudgetPeriodSequenceNumber(), periods);
                BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnelCopyOverBoHelper = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, inflationLength, budgetNonpersonnelInflationRate);

                // update appropriate amounts per indicators set
                if (budgetNonpersonnel.getAgencyCopyIndicator()) {
                    budgetNonpersonnel.setAgencyRequestAmount(budgetNonpersonnelCopyOverBoHelper.getBudgetInflatedAgencyAmount());
                }
                if (budgetNonpersonnel.getBudgetUniversityCostShareCopyIndicator()) {
                    budgetNonpersonnel.setBudgetUniversityCostShareAmount(budgetNonpersonnelCopyOverBoHelper.getBudgetInflatedUniversityCostShareAmount());
                }
                if (budgetNonpersonnel.getBudgetThirdPartyCostShareCopyIndicator()) {
                    budgetNonpersonnel.setBudgetThirdPartyCostShareAmount(budgetNonpersonnelCopyOverBoHelper.getBudgetInflatedThirdPartyCostShareAmount());
                }
            }
        }
    }

    /**
     * This method will recalculate Personnel entries based on changes to the length of periods, changes to fringe rates, or a
     * change to the personnel inflation rate, or a change to cost share inclusion
     * 
     * @param budgetDocument
     * @param modifiedPeriods
     * @param modifiedFringeRates
     * @param isPersonnelInflationRateModified
     */
    private void updatePersonnelDetail(BudgetDocument budgetDocument, List modifiedPeriods, List modifiedFringeRates, boolean isPersonnelInflationRateModified, boolean isInstitutionCostShareInclusionModified) {
        budgetPersonnelService.reconcileAndCalculatePersonnel(budgetDocument);
    }

    private void updatePersonnelCostShare(BudgetDocument budgetDocument) {
        // if Institution Cost Share check box check box is un-checked, set the corresponding amounts to zero.
        for (Iterator budgetUserIter = budgetDocument.getBudget().getPersonnel().iterator(); budgetUserIter.hasNext();) {
            BudgetUser budgetUser = (BudgetUser) budgetUserIter.next();
            for (Iterator userAppointmentTaskIter = budgetUser.getUserAppointmentTasks().iterator(); userAppointmentTaskIter.hasNext();) {
                UserAppointmentTask userAppointmentTask = (UserAppointmentTask) userAppointmentTaskIter.next();
                for (Iterator userAppointmentTaskPeriodIter = userAppointmentTask.getUserAppointmentTaskPeriods().iterator(); userAppointmentTaskPeriodIter.hasNext();) {
                    UserAppointmentTaskPeriod userAppointmentTaskPeriod = (UserAppointmentTaskPeriod) userAppointmentTaskPeriodIter.next();
                    userAppointmentTaskPeriod.setUniversityCostSharePercentEffortAmount(new KualiInteger(0));
                    userAppointmentTaskPeriod.setUserUniversityHours(new KualiInteger(0));
                    userAppointmentTaskPeriod.setUniversityFullTimeEquivalentPercent(new KualiInteger(0));
                    userAppointmentTaskPeriod.setUniversityHealthInsuranceAmount(new KualiInteger(0));
                    userAppointmentTaskPeriod.setUniversityRequestedFeesAmount(new KualiInteger(0));
                    userAppointmentTaskPeriod.setUniversitySalaryAmount(new KualiInteger(0));
                    userAppointmentTaskPeriod.setUniversityCostShareFringeBenefitTotalAmount(new KualiInteger(0));
                    userAppointmentTaskPeriod.setUniversityCostShareRequestTotalAmount(new KualiInteger(0));
                }
            }
        }
    }

    /**
     * This method will recalculate Modular entries based on changes to the length of periods, changes to personnel or nonpersonnel
     * items
     * 
     * @param budgetDocument
     * @param modifiedPeriods
     * @param modifiedFringeRates
     * @param isPersonnelInflationRateModified
     */
    private void updateModular(BudgetDocument budgetDocument) {
        budgetModularService.resetModularBudget(budgetDocument.getBudget());
    }

    /**
     * This method will determine if the Nonpersonnel Inflation rate has changed.
     * 
     * @param budgetDocument
     * @return whether or not nonpersonnel inflation rate changed
     */
    private boolean isNonpersonnelInflationRateModified(BudgetDocument budgetDocument, BudgetDocument databaseBudgetDocument) {
        return !budgetDocument.getBudget().getBudgetNonpersonnelInflationRate().equals(databaseBudgetDocument.getBudget().getBudgetNonpersonnelInflationRate());
    }

    /**
     * This method...
     * 
     * @param budgetDocument
     * @return
     */
    private boolean isPersonnelInflationRateModified(BudgetDocument budgetDocument, BudgetDocument databaseBudgetDocument) {
        return !budgetDocument.getBudget().getBudgetPersonnelInflationRate().equals(databaseBudgetDocument.getBudget().getBudgetPersonnelInflationRate());
    }

    /**
     * This method will remove Nonpersonnel entries that are no longer valid as a result of the task or period that they reference
     * are no longer present.
     * 
     * @param budgetDocument
     */
    private void cleanseNonpersonnel(BudgetDocument budgetDocument) {
        List budgetNonpersonnelItems = budgetDocument.getBudget().getNonpersonnelItems();
        List budgetTasks = budgetDocument.getBudget().getTasks();
        List budgetPeriods = budgetDocument.getBudget().getPeriods();

        for (Iterator i = budgetNonpersonnelItems.iterator(); i.hasNext();) {
            BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) i.next();

            BudgetTask budgetTask = budgetTaskDao.getBudgetTask(budgetNonpersonnel.getDocumentHeaderId(), budgetNonpersonnel.getBudgetTaskSequenceNumber());
            BudgetPeriod budgetPeriod = budgetPeriodDao.getBudgetPeriod(budgetNonpersonnel.getDocumentHeaderId(), budgetNonpersonnel.getBudgetPeriodSequenceNumber());

            if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(budgetTasks, budgetTask) || !ObjectUtils.collectionContainsObjectWithIdentitcalKey(budgetPeriods, budgetPeriod)) {
                i.remove();
            }
        }
    }

    /**
     * This method will remove Modular period entries that are no longer valid as a result of the task or period that they reference
     * are no longer present.
     * 
     * @param budgetDocument
     */
    private void cleanseModular(BudgetDocument budgetDocument) {
        Budget budget = budgetDocument.getBudget();
        if (!budget.isAgencyModularIndicator()) {
            // Not a modular budget - wipe out existing modular budget, if any
            Long versionNumber = null;
            if (ObjectUtils.isNotNull(budget.getModularBudget())) {
                versionNumber = budget.getModularBudget().getVersionNumber();
            }
            budget.setModularBudget(new BudgetModular(budget.getDocumentHeaderId()));
            if (versionNumber != null) {
                budget.getModularBudget().setVersionNumber(versionNumber);
            }
        }
        else if (budgetDocument.getBudget().getModularBudget() != null) {
            List modularPeriods = budget.getModularBudget().getBudgetModularPeriods();
            List budgetPeriods = budget.getPeriods();

            for (Iterator i = modularPeriods.iterator(); i.hasNext();) {
                BudgetModularPeriod currentModularPeriod = (BudgetModularPeriod) i.next();
                BudgetPeriod budgetPeriod = budgetPeriodDao.getBudgetPeriod(currentModularPeriod.getDocumentHeaderId(), currentModularPeriod.getBudgetPeriodSequenceNumber());

                if (!ObjectUtils.collectionContainsObjectWithIdentitcalKey(budgetPeriods, budgetPeriod)) {
                    i.remove();
                }
            }
        }
    }


    /**
     * This method finds and reports periods that have been modified from the last save.
     * 
     * @param BudgetDocument The BudgetDocument whose periods are being checked.
     * @return List A List of modified BudgetPeriods
     */
    private List findModifiedPeriods(BudgetDocument budgetDocument, BudgetDocument databaseBudgetDocument) {
        List modifiedPeriods = new ArrayList();

        List budgetPeriods = budgetDocument.getBudget().getPeriods();

        for (Iterator i = budgetPeriods.iterator(); i.hasNext();) {
            BudgetPeriod budgetPeriod = (BudgetPeriod) i.next();
            if (!databaseBudgetDocument.getBudget().getPeriods().contains(budgetPeriod)) {
                modifiedPeriods.add(budgetPeriod);
            }
        }
        return modifiedPeriods;
    }


    /**
     * This method will find and report any Appointment Types that have had changes to the associated Fringe Rates since the last
     * save.
     * 
     * @param budgetDocument
     * @return List a list of appointments whose fringe rates have been modified
     */
    private List findModifiedFringeRates(BudgetDocument budgetDocument, BudgetDocument databaseBudgetDocument) {
        List modifiedFringeRates = new ArrayList();

        List budgetFringeRates = budgetDocument.getBudget().getFringeRates();

        for (Iterator i = budgetFringeRates.iterator(); i.hasNext();) {
            BudgetFringeRate budgetFringeRate = (BudgetFringeRate) i.next();
            if (!databaseBudgetDocument.getBudget().getFringeRates().contains(budgetFringeRate)) {
                modifiedFringeRates.add(budgetFringeRate);
            }
        }
        return modifiedFringeRates;
    }

    private List findModifiedUserAppointmentTaskPeriods(BudgetDocument budgetDocument, BudgetDocument databaseBudgetDocument) {
        List modifiedList = new ArrayList();
        List budgetList = budgetDocument.getBudget().getAllUserAppointmentTaskPeriods(false);
        List databaseList = databaseBudgetDocument.getBudget().getAllUserAppointmentTaskPeriods(false);

        for (Iterator iter = budgetList.iterator(); iter.hasNext();) {
            UserAppointmentTaskPeriod userAppointmentTaskPeriod = (UserAppointmentTaskPeriod) iter.next();
            if (!databaseList.contains(userAppointmentTaskPeriod)) {
                modifiedList.add(userAppointmentTaskPeriod);
            }
        }

        return modifiedList;
    }

    /**
     * This method will find and report any Appointment Types that have had changes to the associated Fringe Rates since the last
     * save.
     * 
     * @param budgetDocument
     * @return List a list of appointments whose fringe rates have been modified
     */
    private List findModifiedNonpersonnel(BudgetDocument budgetDocument, BudgetDocument databaseBudgetDocument) {
        List modifiedNonpersonnel = new ArrayList();
        List budgetList = budgetDocument.getBudget().getNonpersonnelItems();
        List databaseList = databaseBudgetDocument.getBudget().getNonpersonnelItems();

        for (Iterator i = budgetList.iterator(); i.hasNext();) {
            BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) i.next();
            if (!databaseList.contains(budgetNonpersonnel)) {
                modifiedNonpersonnel.add(budgetNonpersonnel);
            }
        }
        return modifiedNonpersonnel;
    }

    /**
     * The following methods are here for Spring's Dependency Injection to work.
     */

    /**
     * Sets the budgetFringeRateService attribute value.
     * 
     * @param budgetFringeRateService The budgetFringeRateService to set.
     */
    public void setBudgetFringeRateService(BudgetFringeRateService budgetFringeRateService) {
        this.budgetFringeRateService = budgetFringeRateService;
    }

    /**
     * Sets the budgetGraduateAssistantRateService attribute value.
     * 
     * @param budgetGraduateAssistantRateService The budgetGraduateAssistantRateService to set.
     */
    public void setBudgetGraduateAssistantRateService(BudgetGraduateAssistantRateService budgetGraduateAssistantRateService) {
        this.budgetGraduateAssistantRateService = budgetGraduateAssistantRateService;
    }

    /**
     * Sets the budgetTaskDao attribute value.
     * 
     * @param The budgetTaskDao to set.
     */
    public void setBudgetTaskDao(BudgetTaskDao budgetTaskDao) {
        this.budgetTaskDao = budgetTaskDao;
    }

    /**
     * Sets the budgetTaskDao attribute value.
     * 
     * @param The budgetTaskDao to set.
     */
    public void setBudgetPeriodDao(BudgetPeriodDao budgetPeriodDao) {
        this.budgetPeriodDao = budgetPeriodDao;
    }

    /**
     * Sets the budgetPersonnelService attribute value.
     * 
     * @param budgetPersonnelService The budgetPersonnelService to set.
     */
    public void setBudgetPersonnelService(BudgetPersonnelService budgetPersonnelService) {
        this.budgetPersonnelService = budgetPersonnelService;
    }

    /**
     * Sets the budgetCostShareService attribute value.
     * 
     * @param budgetCostShareService The budgetCostShareService to set.
     */
    public void setBudgetCostShareService(BudgetCostShareService budgetCostShareService) {
        this.budgetCostShareService = budgetCostShareService;
    }

    /**
     * Sets the documentService attribute value.
     * 
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Sets the budgetModularService attribute value.
     * 
     * @param budgetModularService The budgetModularService to set.
     */
    public void setBudgetModularService(BudgetModularService budgetModularService) {
        this.budgetModularService = budgetModularService;
    }

    /**
     * @param budgetIndirectCostService The budgetIndirectCostService to set.
     */
    public void setBudgetIndirectCostService(BudgetIndirectCostService budgetIndirectCostService) {
        this.budgetIndirectCostService = budgetIndirectCostService;
    }
}
