/*
 * Copyright (c) 2004, 2005 The National Association of College and University 
 * Business Officers, Cornell University, Trustees of Indiana University, 
 * Michigan State University Board of Trustees, Trustees of San Joaquin Delta 
 * College, University of Hawai'i, The Arizona Board of Regents on behalf of the 
 * University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); 
 * By obtaining, using and/or copying this Original Work, you agree that you 
 * have read, understand, and will comply with the terms and conditions of the 
 * Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, 
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE 
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM,  DAMAGES OR OTHER 
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 */
package org.kuali.module.kra.budget.web.struts.form;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.util.KualiInteger;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.KraConstants;
import org.kuali.module.kra.budget.bo.BudgetAbstractCostShare;
import org.kuali.module.kra.budget.bo.BudgetAbstractPeriodCostShare;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetPeriodUniversityCostShare;
import org.kuali.module.kra.budget.bo.BudgetTaskPeriodIndirectCost;
import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetUniversityCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UniversityCostSharePersonnel;
import org.kuali.module.kra.budget.bo.UserAppointmentTask;
import org.kuali.module.kra.budget.bo.UserAppointmentTaskPeriod;
import org.kuali.module.kra.budget.service.BudgetPeriodService;


/**
 * Used by UI to get totals, counts, aggregations, and other things to render the Cost Share page.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public class BudgetCostShareFormHelper {

    private BudgetPeriodService budgetPeriodService;

    private Direct institutionDirect;
    private Direct thirdPartyDirect;

    private List<Subcontractor> subcontractors = new ArrayList();

    // Totals Section, arrays are the period totals while the total* values are the period array summations
    private KualiInteger[] institutionIndirectCostShare;
    private KualiInteger[] subcontractorCostShare;
    private KualiInteger[] total;
    private KualiInteger totalInstitutionIndirectCostShare = new KualiInteger(0);
    private KualiInteger totalSubcontractorCostShare = new KualiInteger(0);
    private KualiInteger totalTotal = new KualiInteger(0);

    public BudgetCostShareFormHelper() {
        budgetPeriodService = SpringServiceLocator.getBudgetPeriodService();
    }

    /**
     * Constructor for the BudgetCostShareFormHelper object. Convenience argument of BudgetForm.
     * @param budgetForm
     */
    public BudgetCostShareFormHelper(BudgetForm budgetForm) {
        this(budgetForm.getBudgetDocument().getBudget().getPeriods(),
                budgetForm.getBudgetDocument().getBudget().getPersonnel(),
                budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems(),
                budgetForm.getBudgetDocument().getBudget().getUniversityCostSharePersonnelItems(),
                budgetForm.getBudgetDocument().getBudget().getUniversityCostShareItems(),
                budgetForm.getBudgetDocument().getBudget().getThirdPartyCostShareItems(),
                budgetForm.getBudgetIndirectCostFormHelper());
    }
    
    /**
     * Constructor for the BudgetCostShareFormHelper object. Initializes all sections in order for the Cost Share page to function.
     * 
     * @param periods
     * @param personnel
     * @param budgetNonpersonnelItems
     * @param universityCostSharePersonnel
     * @param budgetUniversityCostShare
     * @param budgetThirdPartyCostShare
     * @param budgetIndirectCostFormHelper
     */
    public BudgetCostShareFormHelper(List<BudgetPeriod> periods, List<BudgetUser> personnel, List<BudgetNonpersonnel> budgetNonpersonnelItems, List<UniversityCostSharePersonnel> universityCostSharePersonnel, List<BudgetUniversityCostShare> budgetUniversityCostShare, List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare, BudgetIndirectCostFormHelper budgetIndirectCostFormHelper) {
        this();
        
        // Subcontractors has to happen before 3rd Party Direct so that it can calculate correct totals.
        setupSubcontractors(periods, budgetNonpersonnelItems);
        
        setupDirect(periods, personnel, budgetNonpersonnelItems, universityCostSharePersonnel, budgetUniversityCostShare, budgetThirdPartyCostShare);

        setupTotals(periods, budgetIndirectCostFormHelper);
    }
    
    /**
     * Constructor for the BudgetCostShareFormHelper object. Initializes direct (institution & third party) sections in order for audit mode
     * to do its checks.
     * 
     * @param periods
     * @param personnel
     * @param budgetNonpersonnelItems
     * @param universityCostSharePersonnel
     * @param budgetUniversityCostShare
     * @param budgetThirdPartyCostShare
     */
    public BudgetCostShareFormHelper(List<BudgetPeriod> periods, List<BudgetUser> personnel, List<BudgetNonpersonnel> budgetNonpersonnelItems, List<UniversityCostSharePersonnel> universityCostSharePersonnel, List<BudgetUniversityCostShare> budgetUniversityCostShare, List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare) {
        this();
        
        setupDirect(periods, personnel, budgetNonpersonnelItems, universityCostSharePersonnel, budgetUniversityCostShare, budgetThirdPartyCostShare);
    }

    /**
     * Initializes the Institution & 3rd Party sections. Method should really be private but easier to access it from
     * BudgetCostShareFormHelperTest this way.
     * 
     * @param periods
     * @param nonpersonnelItems
     * @param budgetUniversityCostShare
     * @param budgetThirdPartyCostShare
     */
    private void setupDirect(List<BudgetPeriod> periods, List<BudgetUser> personnel, List<BudgetNonpersonnel> budgetNonpersonnelItems, List<UniversityCostSharePersonnel> universityCostSharePersonnel, List<BudgetUniversityCostShare> budgetUniversityCostShare, List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare) {
        institutionDirect = new Direct(periods, personnel, budgetNonpersonnelItems, universityCostSharePersonnel, budgetUniversityCostShare);
        thirdPartyDirect = new Direct(subcontractorCostShare, periods, budgetNonpersonnelItems, budgetThirdPartyCostShare);
    }

    /**
     * Initializes the Subcontractor section. Method should really be private but easier to access it from
     * BudgetCostShareFormHelperTest this way.
     * 
     * @param periods
     * @param nonpersonnelItems
     */
    private void setupSubcontractors(List<BudgetPeriod> periods, List<BudgetNonpersonnel> nonpersonnelItems) {
        HashMap<String, Subcontractor> addedSubcontractors = new HashMap();

        subcontractorCostShare = new KualiInteger[periods.size()];
        Arrays.fill(subcontractorCostShare, new KualiInteger(0));

        // Loop and check below assumes origin items are always found before copy over items. Sort to make sure.
        Collections.sort(nonpersonnelItems);

        // Go over each budgetNonpersonnel item, but only look at the subcontractor ones
        for (BudgetNonpersonnel budgetNonpersonnel : nonpersonnelItems) {
            // With the if statement below and the hashMap we aggregate nonpersonnel items that span several periods. This is
            // similar to
            // what can be found in BudgetNonpersonnelCopyOverHelper.NonpersonnelCopyOverCategoryHelper.addBudgetNonpersonnelItem.
            if (KraConstants.SUBCONTRACTOR_CATEGORY_CODE.equals(budgetNonpersonnel.getBudgetNonpersonnelCategoryCode())) {
                int periodIndex = budgetPeriodService.getPeriodIndex(budgetNonpersonnel.getBudgetPeriodSequenceNumber(), periods);

                // Update total that displays at the bottom of the page
                subcontractorCostShare[periodIndex] = subcontractorCostShare[periodIndex].add(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount());
                totalSubcontractorCostShare = totalSubcontractorCostShare.add(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount());

                if (addedSubcontractors.containsKey(budgetNonpersonnel.getSubcontractorNumber())) {
                    Subcontractor subcontractor = addedSubcontractors.get(budgetNonpersonnel.getSubcontractorNumber());

                    subcontractor.addPeriodAmount(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount(), periodIndex);
                }
                else {
                    Subcontractor subcontractor = new Subcontractor(budgetNonpersonnel, periods.size(), periodIndex);

                    subcontractors.add(subcontractor);
                    addedSubcontractors.put(budgetNonpersonnel.getSubcontractorNumber(), subcontractor);
                }
            }
        }
    }

    /**
     * Initializes the Totals section. Method should really be private but easier to access it from BudgetCostShareFormHelperTest
     * this way. It assumes that other setup methods already ran because the grand total calculation relies on certain numbers being
     * present.
     * 
     * @param budgetIndirectCostFormHelper
     * @param periods
     */
    private void setupTotals(List<BudgetPeriod> periods, BudgetIndirectCostFormHelper budgetIndirectCostFormHelper) {
        // Deal with indirect cost totals first. They are all calculated in the helper.
        institutionIndirectCostShare = new KualiInteger[periods.size()];
        for (int i = 0; i < institutionIndirectCostShare.length; i++) {
            BudgetTaskPeriodIndirectCost budgetTaskPeriodIndirectCost = (BudgetTaskPeriodIndirectCost) budgetIndirectCostFormHelper.getPeriodTotals().get(i);

            institutionIndirectCostShare[i] = budgetTaskPeriodIndirectCost.getCostShareCalculatedIndirectCost().add(budgetTaskPeriodIndirectCost.getCostShareUnrecoveredIndirectCost());
            totalInstitutionIndirectCostShare = totalInstitutionIndirectCostShare.add(institutionIndirectCostShare[i]);
        }

        // Deal with the grand total. This assums other setup methods ran.
        total = new KualiInteger[periods.size()];
        for (int i = 0; i < total.length; i++) {
            total[i] = institutionDirect.getAmountDistributed()[i].add(institutionIndirectCostShare[i].add(thirdPartyDirect.getAmountDistributed()[i].add(subcontractorCostShare[i])));
            totalTotal = totalTotal.add(total[i]);
        }
    }

    /**
     * Gets the institutionDirect attribute.
     * 
     * @return Returns the institutionDirect.
     */
    public Direct getInstitutionDirect() {
        return institutionDirect;
    }

    /**
     * Gets the thirdPartyDirect attribute.
     * 
     * @return Returns the thirdPartyDirect.
     */
    public Direct getThirdPartyDirect() {
        return thirdPartyDirect;
    }

    /**
     * Gets the subcontractors attribute.
     * 
     * @return Returns the subcontractors.
     */
    public List<Subcontractor> getSubcontractors() {
        return subcontractors;
    }

    /**
     * Gets the institutionIndirectCostShare attribute.
     * 
     * @return Returns the institutionIndirectCostShare.
     */
    public KualiInteger[] getInstitutionIndirectCostShare() {
        return institutionIndirectCostShare;
    }

    /**
     * Gets the subcontractorCostShare attribute.
     * 
     * @return Returns the subcontractorCostShare.
     */
    public KualiInteger[] getSubcontractorCostShare() {
        return subcontractorCostShare;
    }

    /**
     * Gets the total attribute.
     * 
     * @return Returns the total.
     */
    public KualiInteger[] getTotal() {
        return total;
    }

    /**
     * Gets the totalInstitutionIndirectCostShare attribute.
     * 
     * @return Returns the totalInstitutionIndirectCostShare.
     */
    public KualiInteger getTotalInstitutionIndirectCostShare() {
        return totalInstitutionIndirectCostShare;
    }

    /**
     * Gets the totalSubcontractorCostShare attribute.
     * 
     * @return Returns the totalSubcontractorCostShare.
     */
    public KualiInteger getTotalSubcontractorCostShare() {
        return totalSubcontractorCostShare;
    }

    /**
     * Gets the totalTotal attribute.
     * 
     * @return Returns the totalTotal.
     */
    public KualiInteger getTotalTotal() {
        return totalTotal;
    }

    /**
     * Inner helper class that assists in the management of the data for the Institution & 3rd party direct section.
     * 
     * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
     */
    public class Direct {
        private KualiInteger[] totalBudgeted;
        private KualiInteger[] amountDistributed;
        private KualiInteger[] balanceToBeDistributed;
        private KualiInteger totalTotalBudgeted = new KualiInteger(0);
        private KualiInteger totalAmountDistributed = new KualiInteger(0);
        private KualiInteger totalBalanceToBeDistributed = new KualiInteger(0);

        private KualiInteger[] totalSource;
        
        // Following fields are only used for institution direct cost share. That is the only one that imports the personnel amounts.
        private KualiInteger[][] institutionDirectPersonnel;
        private KualiInteger[] totalInstitutionDirectPersonnel;

        /**
         * Constructs a BudgetCostShareFormHelper, helper constructor with common variable initializations.
         * @param periodsSize
         * @param sourceSize
         */
        public Direct(int periodsSize, int sourceSize) {
            // Initialize variables and fill the arrays with 0 values
            totalBudgeted = new KualiInteger[periodsSize];
            amountDistributed = new KualiInteger[periodsSize];
            balanceToBeDistributed = new KualiInteger[periodsSize];
            Arrays.fill(totalBudgeted, new KualiInteger(0));
            Arrays.fill(amountDistributed, new KualiInteger(0));
            Arrays.fill(balanceToBeDistributed, new KualiInteger(0));

            totalSource = new KualiInteger[sourceSize];
            Arrays.fill(totalSource, new KualiInteger(0));
        }
        
        /**
         * Constructs a BudgetCostShareFormHelper with university cost share values.
         * @param periods
         * @param personnel
         * @param budgetNonpersonnelItems
         * @param budgetThirdPartyCostShareItems
         */
        public Direct(List<BudgetPeriod> periods, List<BudgetUser> personnel, List<BudgetNonpersonnel> budgetNonpersonnelItems, List<UniversityCostSharePersonnel> universityCostSharePersonnel, List<BudgetUniversityCostShare> budgetUniversityCostShareItems) {
            this(periods.size(), budgetUniversityCostShareItems.size());
            
            // Setup arrays for institution direct personnel data (third party doesn't use those, so they arn't done in the commen constructor).
            institutionDirectPersonnel = new KualiInteger[universityCostSharePersonnel.size()][periods.size()];
            totalInstitutionDirectPersonnel = new KualiInteger[universityCostSharePersonnel.size()];
            for(int i = 0; i < institutionDirectPersonnel.length; i++) {
                Arrays.fill(institutionDirectPersonnel[i], new KualiInteger(0));
            }
            Arrays.fill(totalInstitutionDirectPersonnel, new KualiInteger(0));
            
            calculateInstitutionDirectPersonnel(periods, personnel, universityCostSharePersonnel);
            
            calculateNonpersonnelInstitutionTotalBudgeted(periods, budgetNonpersonnelItems);

            calculateAmountDistributedAndTotalSource(budgetUniversityCostShareItems);

            calculateSubcontractorAndBalanceToBeDistributed(null);
        }
        
        /**
         * Constructs a BudgetCostShareFormHelper with third party cost share values.
         * @param subcontractorCostShare
         * @param periods
         * @param budgetNonpersonnelItems
         * @param budgetThirdPartyCostShareItems
         */
        public Direct(KualiInteger[] subcontractorCostShare, List<BudgetPeriod> periods, List<BudgetNonpersonnel> budgetNonpersonnelItems, List<BudgetThirdPartyCostShare> budgetThirdPartyCostShareItems) {
            this(periods.size(), budgetThirdPartyCostShareItems.size());
            
            calculateNonpersonnelThirdPartyTotalBudgeted(periods, budgetNonpersonnelItems);

            calculateAmountDistributedAndTotalSource(budgetThirdPartyCostShareItems);

            calculateSubcontractorAndBalanceToBeDistributed(subcontractorCostShare);
        }

        /**
         * Calculates totalBudgeted for personnel institution cost share.
         * @param periods
         * @param personnel
         * @param universityCostSharePersonnel
         */
        private void calculateInstitutionDirectPersonnel(List<BudgetPeriod> periods, List<BudgetUser> personnel, List<UniversityCostSharePersonnel> universityCostSharePersonnel) {
            // First we look at each chart/org.
            for(int i = 0; i < universityCostSharePersonnel.size(); i++) {
                UniversityCostSharePersonnel universityCostSharePerson = universityCostSharePersonnel.get(i);
                
                // Second we check each person if it matches the chart/org we are currently evaluating.
                for (BudgetUser person : personnel) {
                    if(universityCostSharePerson.getChartOfAccountsCode().equals(StringUtils.defaultString(person.getFiscalCampusCode())) &&
                            universityCostSharePerson.getOrganizationCode().equals(StringUtils.defaultString(person.getPrimaryDepartmentCode()))) {
                        
                        // Third we look at each Task.
                        for(UserAppointmentTask userAppointmentTask : person.getUserAppointmentTasks()) {
                            // Fourth we look at each Period. Cost Share takes task summation / display of each period.
                            for(UserAppointmentTaskPeriod userAppointmentTaskPeriod : userAppointmentTask.getUserAppointmentTaskPeriods()) {
                                int periodIndex = budgetPeriodService.getPeriodIndex(userAppointmentTaskPeriod.getBudgetPeriodSequenceNumber(), periods);
                                KualiInteger institutionDirectPersonnelAmount = userAppointmentTaskPeriod.getUniversityCostShareFringeBenefitTotalAmount().add(userAppointmentTaskPeriod.getUniversityCostShareRequestTotalAmount());
                                
                                // Take the value and put it into i (location of the chart / org) and j (period location).
                                institutionDirectPersonnel[i][periodIndex] = institutionDirectPersonnel[i][periodIndex].add(institutionDirectPersonnelAmount);
                                totalInstitutionDirectPersonnel[i] = totalInstitutionDirectPersonnel[i].add(institutionDirectPersonnelAmount);
                                
                                // Finally update the total budgeted and amount distributed with the same numbers.
                                totalBudgeted[periodIndex] = totalBudgeted[periodIndex].add(institutionDirectPersonnelAmount);
                                totalTotalBudgeted = totalTotalBudgeted.add(institutionDirectPersonnelAmount);
                                
                                amountDistributed[periodIndex] = amountDistributed[periodIndex].add(institutionDirectPersonnelAmount);
                                totalAmountDistributed = totalAmountDistributed.add(institutionDirectPersonnelAmount);
                            }
                        }
                    }
                }
            }
        }

        /**
         * Calculates Amount Distributed and Total Source. Both of them are done together so to eliminate unnecessary loops. This is
         * just a helper method to consolidate code.
         * @param budgetThirdPartyCostShareItems
         */
        private void calculateAmountDistributedAndTotalSource(List budgetAbstractCostShareItems) {
            for (int i = 0; i < budgetAbstractCostShareItems.size(); i++) {
                BudgetAbstractCostShare budgetAbstractCostShare = (BudgetAbstractCostShare) budgetAbstractCostShareItems.get(i);

                for (int j = 0; j < budgetAbstractCostShare.getBudgetPeriodCostShare().size(); j++) {
                    BudgetAbstractPeriodCostShare budgetAbstractPeriodCostShare = (BudgetAbstractPeriodCostShare) budgetAbstractCostShare.getBudgetPeriodCostShare().get(j);

                    // check for null because it may be a newly added object
                    if (budgetAbstractPeriodCostShare.getBudgetCostShareAmount() != null) {
                        totalSource[i] = totalSource[i].add(budgetAbstractPeriodCostShare.getBudgetCostShareAmount());
                        amountDistributed[j] = amountDistributed[j].add(budgetAbstractPeriodCostShare.getBudgetCostShareAmount());
                        totalAmountDistributed = totalAmountDistributed.add(budgetAbstractPeriodCostShare.getBudgetCostShareAmount());
                    }
                }
            }
        }

        /**
         * Calculates Institution Total Budget for Nonpersonnel items. This is just a helper method to consolidate code.
         * @param periods
         * @param budgetNonpersonnelItems
         */
        private void calculateNonpersonnelInstitutionTotalBudgeted(List<BudgetPeriod> periods, List<BudgetNonpersonnel> budgetNonpersonnelItems) {
            for (BudgetNonpersonnel budgetNonpersonnel : budgetNonpersonnelItems) {
                int periodIndex = budgetPeriodService.getPeriodIndex(budgetNonpersonnel.getBudgetPeriodSequenceNumber(), periods);

                totalBudgeted[periodIndex] = totalBudgeted[periodIndex].add(budgetNonpersonnel.getBudgetUniversityCostShareAmount());
                totalTotalBudgeted = totalTotalBudgeted.add(budgetNonpersonnel.getBudgetUniversityCostShareAmount());
            }
        }
        
        /**
         * Calculates Third Party Total Budget for Nonpersonnel items. This is just a helper method to consolidate code.
         * @param periods
         * @param budgetNonpersonnelItems
         */
        private void calculateNonpersonnelThirdPartyTotalBudgeted(List<BudgetPeriod> periods, List<BudgetNonpersonnel> budgetNonpersonnelItems) {
            for (BudgetNonpersonnel budgetNonpersonnel : budgetNonpersonnelItems) {
                int periodIndex = budgetPeriodService.getPeriodIndex(budgetNonpersonnel.getBudgetPeriodSequenceNumber(), periods);

                totalBudgeted[periodIndex] = totalBudgeted[periodIndex].add(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount());
                totalTotalBudgeted = totalTotalBudgeted.add(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount());
            }
        }

        /**
         * Calculates the balance to be distributed (and it's total) based on totalBudgeted - amountDistributed. This is just a helper method to
         * consolidate code.
         * @param subcontractorCostShare applicable for 3rd Party Direct cost share, otherwise null
         */
        private void calculateSubcontractorAndBalanceToBeDistributed(KualiInteger[] subcontractorCostShare) {
            // calculate totalBalanceToBeDistributed
            for (int i = 0; i < balanceToBeDistributed.length; i++) {
                if(subcontractorCostShare != null) {
                    // subcontractorCostShare needs to be added in
                    amountDistributed[i] = amountDistributed[i].add(subcontractorCostShare[i]);
                    totalAmountDistributed = totalAmountDistributed.add(subcontractorCostShare[i]);
                }
                
                balanceToBeDistributed[i] = totalBudgeted[i].subtract(amountDistributed[i]);
                totalBalanceToBeDistributed = totalBalanceToBeDistributed.add(balanceToBeDistributed[i]);
            }
        }

        /**
         * Gets the amountDistributed attribute.
         * 
         * @return Returns the amountDistributed.
         */
        public KualiInteger[] getAmountDistributed() {
            return amountDistributed;
        }

        /**
         * Gets the balanceToBeDistributed attribute.
         * 
         * @return Returns the balanceToBeDistributed.
         */
        public KualiInteger[] getBalanceToBeDistributed() {
            return balanceToBeDistributed;
        }

        /**
         * Gets the totalAmountDistributed attribute.
         * 
         * @return Returns the totalAmountDistributed.
         */
        public KualiInteger getTotalAmountDistributed() {
            return totalAmountDistributed;
        }

        /**
         * Gets the totalBalanceToBeDistributed attribute.
         * 
         * @return Returns the totalBalanceToBeDistributed.
         */
        public KualiInteger getTotalBalanceToBeDistributed() {
            return totalBalanceToBeDistributed;
        }

        /**
         * Gets the totalBudgeted attribute.
         * 
         * @return Returns the totalBudgeted.
         */
        public KualiInteger[] getTotalBudgeted() {
            return totalBudgeted;
        }

        /**
         * Gets the totalTotalBudgeted attribute.
         * 
         * @return Returns the totalTotalBudgeted.
         */
        public KualiInteger getTotalTotalBudgeted() {
            return totalTotalBudgeted;
        }

        /**
         * Gets the totalSource attribute.
         * 
         * @return Returns the totalSource.
         */
        public KualiInteger[] getTotalSource() {
            return totalSource;
        }

        /**
         * Gets the institutionDirectPersonnel attribute. 
         * @return Returns the institutionDirectPersonnel.
         */
        public KualiInteger[][] getInstitutionDirectPersonnel() {
            return institutionDirectPersonnel;
        }

        /**
         * Gets the totalInstitutionDirectPersonnel attribute. 
         * @return Returns the totalInstitutionDirectPersonnel.
         */
        public KualiInteger[] getTotalInstitutionDirectPersonnel() {
            return totalInstitutionDirectPersonnel;
        }
    }

    /**
     * Inner helper class that assists in the management of the data for the Subcontractor section.
     * 
     * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
     */
    public class Subcontractor {
        private String budgetNonpersonnelDescription;

        private KualiInteger[] periodAmounts;
        private KualiInteger totalPeriodAmount;

        /**
         * Constructs a Subcontractor object. Initializes periodAmounts array to all 0s, sets initial amount, and adds that amount
         * to totalPeriodAmount.
         * 
         * @param budgetNonpersonnel
         * @param periodsSize
         * @param periodAmountsIndex
         */
        public Subcontractor(BudgetNonpersonnel budgetNonpersonnel, int periodsSize, int periodAmountsIndex) {
            budgetNonpersonnelDescription = budgetNonpersonnel.getBudgetNonpersonnelDescription();

            periodAmounts = new KualiInteger[periodsSize];
            Arrays.fill(periodAmounts, new KualiInteger(0));

            periodAmounts[periodAmountsIndex] = budgetNonpersonnel.getBudgetThirdPartyCostShareAmount();
            totalPeriodAmount = budgetNonpersonnel.getBudgetThirdPartyCostShareAmount();
        }

        /**
         * Adds an amount to a certain period for a subcontractor.
         * 
         * @param budgetThirdPartyCostShareAmount
         * @param periodAmountsIndex
         */
        public void addPeriodAmount(KualiInteger budgetThirdPartyCostShareAmount, int periodAmountsIndex) {
            periodAmounts[periodAmountsIndex] = periodAmounts[periodAmountsIndex].add(budgetThirdPartyCostShareAmount);
            totalPeriodAmount = totalPeriodAmount.add(budgetThirdPartyCostShareAmount);
        }

        /**
         * Gets the budgetNonpersonnelDescription attribute.
         * 
         * @return Returns the budgetNonpersonnelDescription.
         */
        public String getBudgetNonpersonnelDescription() {
            return budgetNonpersonnelDescription;
        }

        /**
         * Gets the periodAmounts attribute.
         * 
         * @return Returns the periodAmounts.
         */
        public KualiInteger[] getPeriodAmounts() {
            return periodAmounts;
        }

        /**
         * Gets the totalPeriodAmount attribute.
         * 
         * @return Returns the totalPeriodAmount.
         */
        public KualiInteger getTotalPeriodAmount() {
            return totalPeriodAmount;
        }
    }
}
