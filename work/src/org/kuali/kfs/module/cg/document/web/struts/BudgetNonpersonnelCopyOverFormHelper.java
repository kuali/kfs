package org.kuali.module.kra.budget.web.struts.form;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.NonpersonnelCategory;
import org.kuali.module.kra.budget.document.BudgetDocument;
import org.kuali.module.kra.budget.service.BudgetPeriodService;

/**
 * This is used by the UI to get totals, counts, and other things needed to render nonpersonnel copy over properly.
 * 
 * @author KRA (era_team@indiana.edu)
 */
public class BudgetNonpersonnelCopyOverFormHelper {

    // This map holds the individual NonpersonnelCopyOverCategoryHelper objects.
    // The Key is the nonpersonnel category code, the value is the NonpersonnelCopyOverCategoryHelper.
    Map nonpersonnelCopyOverCategoryHelpers;

    /**
     * Constructs a BudgetNonpersonnelFormHelper. Default, no arg constructor
     */
    public BudgetNonpersonnelCopyOverFormHelper() {
        nonpersonnelCopyOverCategoryHelpers = new HashMap();
    }

    /**
     * Constructs a BudgetNonpersonnelCopyOverFormHelper based on data extracted from a BudgetForm.
     * 
     * @param budgetForm which is used to take the categories and nonpersonnel objects from
     */
    public BudgetNonpersonnelCopyOverFormHelper(BudgetForm budgetForm) {
        nonpersonnelCopyOverCategoryHelpers = new HashMap();
        List nonpersonnelCategories = budgetForm.getNonpersonnelCategories();
        List nonpersonnelItems = budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems();

        // Build NonpersonnelCopyOverCategoryHelper for each category.
        for (Iterator nonpersonnelCategoryIter = nonpersonnelCategories.iterator(); nonpersonnelCategoryIter.hasNext();) {
            NonpersonnelCategory nonpersonnelCategory = (NonpersonnelCategory) nonpersonnelCategoryIter.next();

            nonpersonnelCopyOverCategoryHelpers.put(nonpersonnelCategory.getCode(), new NonpersonnelCopyOverCategoryHelper(nonpersonnelCategory, budgetForm.getCurrentTaskNumber(), budgetForm.getBudgetDocument().getBudget().getPeriods(), budgetForm.getBudgetDocument().getBudget().getBudgetNonpersonnelInflationRate(), nonpersonnelItems));
        }

        // Calculate totals
        this.refresh(budgetForm.getBudgetDocument().getBudget().getPeriods().size());
    }

    /**
     * Copies the nonpersonnel objects contained in this object back into the nonpersonnel list in BudgetForm. The data duplication
     * is present in order to simplify display on the copy over page and most importantly to facilitate saving at the right time.
     * 
     * @param budgetForm which is used to copy the nonpersonnel objects into
     */
    public void deconstruct(BudgetForm budgetForm) {
        List nonpersonnelCategories = budgetForm.getNonpersonnelCategories();

        // iterate over each category
        for (Iterator nonpersonnelCategoryIter = nonpersonnelCategories.iterator(); nonpersonnelCategoryIter.hasNext();) {
            NonpersonnelCategory nonpersonnelCategory = (NonpersonnelCategory) nonpersonnelCategoryIter.next();

            NonpersonnelCopyOverCategoryHelper nonpersonnelCopyOverCategoryHelper = (NonpersonnelCopyOverCategoryHelper) nonpersonnelCopyOverCategoryHelpers.get(nonpersonnelCategory.getCode());
            if (nonpersonnelCopyOverCategoryHelper != null) {
                nonpersonnelCopyOverCategoryHelper.deconstruct((BudgetDocument) budgetForm.getDocument());
            }
        }
    }

    /**
     * Updates the datastructure with new values based on a page refresh or prior to a return. Specifically this involves ensuring
     * origin item checkboxes are correctly checked (checked for items that have been copied over) and totals are calculated.
     * 
     * @param periodsSize number of periods
     */
    public void refresh(int periodsSize) {
        Collection nprsHelpers = nonpersonnelCopyOverCategoryHelpers.values();

        // iterate over each category
        for (Iterator nprsHelpersIter = nprsHelpers.iterator(); nprsHelpersIter.hasNext();) {
            NonpersonnelCopyOverCategoryHelper nonpersonnelCopyOverCategoryHelper = (NonpersonnelCopyOverCategoryHelper) nprsHelpersIter.next();

            nonpersonnelCopyOverCategoryHelper.refresh(periodsSize);
        }
    }

    /**
     * Gets the nonpersonnelCopyOverCategoryHelpers attribute.
     * 
     * @return Returns the nonpersonnelCopyOverCategoryHelpers.
     */
    public Map getNonpersonnelCopyOverCategoryHelpers() {
        return nonpersonnelCopyOverCategoryHelpers;
    }

    /**
     * Sets the nonpersonnelCopyOverCategoryHelpers attribute value.
     * 
     * @param nonpersonnelCopyOverCategoryHelpers The nonpersonnelCopyOverCategoryHelpers to set.
     */
    public void setNonpersonnelCopyOverCategoryHelpers(Map budgetNonpersonnelHelperMap) {
        this.nonpersonnelCopyOverCategoryHelpers = budgetNonpersonnelHelperMap;
    }

    /**
     * Gets a NonpersonnelCopyOverCategoryHelper from nonpersonnelCopyOverCategoryHelpers. Creates it if it doesn't exist yet. This
     * is useful because the hidden variables on the copy over page need a place to be put.
     * 
     * @param key Key for the NonpersonnelCopyOverCategoryHelper to get
     * @return Returns a NonpersonnelCopyOverCategoryHelper.
     */
    public NonpersonnelCopyOverCategoryHelper getNonpersonnelCopyOverCategoryHelper(String key) {
        NonpersonnelCopyOverCategoryHelper nonpersonnelCopyOverCategoryHelper = (NonpersonnelCopyOverCategoryHelper) nonpersonnelCopyOverCategoryHelpers.get(key);

        if (nonpersonnelCopyOverCategoryHelper == null) {
            nonpersonnelCopyOverCategoryHelper = new NonpersonnelCopyOverCategoryHelper();

            nonpersonnelCopyOverCategoryHelpers.put(key, nonpersonnelCopyOverCategoryHelper);
        }

        return nonpersonnelCopyOverCategoryHelper;
    }

    /**
     * Class that stores the nonpersonnel items. From the copy over page seen this is a category with each having the line items for
     * all tasks.
     * 
     * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
     */
    public class NonpersonnelCopyOverCategoryHelper {
        private String nonpersonnelCategoryCode; // this field isn't critical, but it is helpful
        private List agencyRequestAmountTotal;
        private List budgetUniversityCostShareAmountTotal;
        private List budgetThirdPartyCostShareAmountTotal;
        private List nprsItems;

        /**
         * Constructs a NonpersonnelCopyOverCategoryHelper. Default, no arg constructor
         */
        public NonpersonnelCopyOverCategoryHelper() {
            this.nprsItems = new ArrayList();
        }

        /**
         * Constructor for NonpersonnelCopyOverCategoryHelper that represents a current period, task summary. It will pick out all
         * the appropriate nonpersonnel items meeting those criteria and place their amounts into
         * NonpersonnelCopyOverLineItemHelper.
         * 
         * @param nonpersonnelCategory category that this NonpersonnelCopyOverCategoryHelper represents
         * @param currentTaskNumber current task that this NonpersonnelCopyOverCategoryHelper represents ("task summar" basically)
         * @param periods Budget.periods
         * @param budgetNonpersonnelInflationRate inflation rate of this budget
         * @param nonpersonnelItems all nonpersonnel items, this method will pick the appropriate ones out, this needs to be ordered
         *        by sequence number.
         */
        public NonpersonnelCopyOverCategoryHelper(NonpersonnelCategory nonpersonnelCategory, Integer currentTaskNumber, List periods, KualiDecimal budgetNonpersonnelInflationRate, List nonpersonnelItems) {
            this();
            this.setNonpersonnelCategoryCode(nonpersonnelCategory.getCode());

            this.addBudgetNonpersonnelItem(currentTaskNumber, periods, budgetNonpersonnelInflationRate, nonpersonnelItems);
        }

        /**
         * @see org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverFormHelper#deconstruct(BudgetForm budgetForm)
         * @param budgetDocument Budget.document
         */
        public void deconstruct(BudgetDocument budgetDocument) {
            // iterate over each line item
            for (Iterator nprsItemsIter = nprsItems.iterator(); nprsItemsIter.hasNext();) {
                NonpersonnelCopyOverLineItemHelper nonpersonnelCopyOverLineItemHelper = (NonpersonnelCopyOverLineItemHelper) nprsItemsIter.next();

                nonpersonnelCopyOverLineItemHelper.deconstruct(budgetDocument);
            }
        }

        /**
         * @see org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverFormHelper#refresh()
         * @param periodsSize number of periods
         */
        public void refresh(int periodsSize) {
            KualiDecimal agencyRequestAmountTotalArr[] = new KualiDecimal[periodsSize];
            KualiDecimal budgetUniversityCostShareAmountTotalArr[] = new KualiDecimal[periodsSize];
            KualiDecimal budgetThirdPartyCostShareAmountTotalArr[] = new KualiDecimal[periodsSize];

            // initialize the arrays with 0 values
            for (int i = 0; i < periodsSize; i++) {
                agencyRequestAmountTotalArr[i] = new KualiDecimal(0);
                budgetUniversityCostShareAmountTotalArr[i] = new KualiDecimal(0);
                budgetThirdPartyCostShareAmountTotalArr[i] = new KualiDecimal(0);
            }

            // iterate over each line item
            for (Iterator nprsItemsIter = nprsItems.iterator(); nprsItemsIter.hasNext();) {
                NonpersonnelCopyOverLineItemHelper nonpersonnelCopyOverLineItemHelper = (NonpersonnelCopyOverLineItemHelper) nprsItemsIter.next();

                // handles checking appropriate checkboxes, note this is unrelated to calculating amount totals.
                nonpersonnelCopyOverLineItemHelper.refresh();

                for (int i = 0; i < periodsSize; i++) { // for each period
                    // add value to the total
                    BudgetNonpersonnelCopyOverBoHelper periodAmount = nonpersonnelCopyOverLineItemHelper.getPeriodAmount(i);
                    agencyRequestAmountTotalArr[i] = agencyRequestAmountTotalArr[i].add(periodAmount.getDisplayAgencyRequestAmount());
                    budgetUniversityCostShareAmountTotalArr[i] = budgetUniversityCostShareAmountTotalArr[i].add(periodAmount.getDisplayBudgetUniversityCostShareAmount());
                    budgetThirdPartyCostShareAmountTotalArr[i] = budgetThirdPartyCostShareAmountTotalArr[i].add(periodAmount.getDisplayBudgetThirdPartyCostShareAmount());
                }
            }

            // convert array to the list
            agencyRequestAmountTotal = Arrays.asList(agencyRequestAmountTotalArr);
            budgetUniversityCostShareAmountTotal = Arrays.asList(budgetUniversityCostShareAmountTotalArr);
            budgetThirdPartyCostShareAmountTotal = Arrays.asList(budgetThirdPartyCostShareAmountTotalArr);
        }

        /**
         * Helper method that removes business logic from constructor to properly place each item of budgetNonpersonnel.
         * 
         * @param currentTaskNumber current task that this NonpersonnelCopyOverCategoryHelper represents ("task summar" basically)
         * @param periods Budget.periods
         * @param budgetNonpersonnelInflationRate inflation rate of this budget
         * @param nonpersonnelItems all nonpersonnel items, this method will pick the appropriate ones out, this needs to be ordered
         *        by sequence number.
         */
        private void addBudgetNonpersonnelItem(Integer currentTaskNumber, List periods, KualiDecimal budgetNonpersonnelInflationRate, List nonpersonnelItems) {
            HashMap nprsCopyOverLineItemHelpers = new HashMap();

            // Loop and check below assumes origin items are always found before copy over items. Sort to make sure.
            Collections.sort(nonpersonnelItems);

            // loop over all nonpersonnelItems for this budget
            for (Iterator nonpersonnelItemsIter = nonpersonnelItems.iterator(); nonpersonnelItemsIter.hasNext();) {
                BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) nonpersonnelItemsIter.next();

                // check if this nonpersonnelItem is part of this task and category, otherwise skip
                if (budgetNonpersonnel.getBudgetTaskSequenceNumber().equals(currentTaskNumber) && budgetNonpersonnel.getBudgetNonpersonnelCategoryCode().equals(this.getNonpersonnelCategoryCode())) {

                    if (budgetNonpersonnel.isOriginItem()) { // it is an origin item
                        // Create a new BudgetNonpersonnelCopyOverBoHelper, without inflation (so rate = 0)
                        BudgetNonpersonnelCopyOverBoHelper nonpersonnelCopyOverBoHelper = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel);
                        // Create a new NonpersonnelCopyOverLineItemHelper and initilize it with list of length of periods
                        NonpersonnelCopyOverLineItemHelper nprsCopyOverLineItemHelper = new NonpersonnelCopyOverLineItemHelper(nonpersonnelCopyOverBoHelper, periods, budgetNonpersonnelInflationRate);

                        // Add it to the local class list and to a local method HashMap. The local method
                        // HashMap is so that it's easily found by possible already existing copy over
                        // items (see else clause below).
                        this.nprsItems.add(nprsCopyOverLineItemHelper);
                        nprsCopyOverLineItemHelpers.put(nonpersonnelCopyOverBoHelper.getBudgetNonpersonnelSequenceNumber(), nprsCopyOverLineItemHelper);
                    }
                    else { // it is a copied over item
                        // find this items' origin item
                        NonpersonnelCopyOverLineItemHelper nprsCopyOverLineItemHelper = (NonpersonnelCopyOverLineItemHelper) nprsCopyOverLineItemHelpers.get(budgetNonpersonnel.getBudgetOriginSequenceNumber());

                        // new item with inflation rate, and add it to list (appropriate spot of period number)
                        int inflationLength = SpringServiceLocator.getBudgetPeriodService().getPeriodsRange(nprsCopyOverLineItemHelper.getOriginBudgetPeriodSequenceNumber(), budgetNonpersonnel.getBudgetPeriodSequenceNumber(), periods);
                        BudgetNonpersonnelCopyOverBoHelper nonpersonnelCopyOverBoHelper = new BudgetNonpersonnelCopyOverBoHelper(budgetNonpersonnel, inflationLength, budgetNonpersonnelInflationRate);
                        nprsCopyOverLineItemHelper.add(nonpersonnelCopyOverBoHelper, periods);
                    }

                }
            }
        }

        /**
         * Gets the nonpersonnelCategoryCode attribute.
         * 
         * @return Returns the nonpersonnelCategoryCode.
         */
        public String getNonpersonnelCategoryCode() {
            return nonpersonnelCategoryCode;
        }

        /**
         * Sets the nonpersonnelCategoryCode attribute value.
         * 
         * @param nonpersonnelCategoryCode The nonpersonnelCategoryCode to set.
         */
        public void setNonpersonnelCategoryCode(String nonpersonnelCategoryCode) {
            this.nonpersonnelCategoryCode = nonpersonnelCategoryCode;
        }

        /**
         * Gets the agencyRequestAmountTotal attribute.
         * 
         * @return Returns the agencyRequestAmountTotal.
         */
        public List getAgencyRequestAmountTotal() {
            return agencyRequestAmountTotal;
        }

        /**
         * Sets the agencyRequestAmountTotal attribute value.
         * 
         * @param agencyRequestAmountTotal The agencyRequestAmountTotal to set.
         */
        public void setAgencyRequestAmountTotal(List agencyRequestAmountTotal) {
            this.agencyRequestAmountTotal = agencyRequestAmountTotal;
        }

        /**
         * Gets the budgetThirdPartyCostShareAmountTotal attribute.
         * 
         * @return Returns the budgetThirdPartyCostShareAmountTotal.
         */
        public List getBudgetThirdPartyCostShareAmountTotal() {
            return budgetThirdPartyCostShareAmountTotal;
        }

        /**
         * Sets the budgetThirdPartyCostShareAmountTotal attribute value.
         * 
         * @param budgetThirdPartyCostShareAmountTotal The budgetThirdPartyCostShareAmountTotal to set.
         */
        public void setBudgetThirdPartyCostShareAmountTotal(List budgetThirdPartyCostShareAmountTotal) {
            this.budgetThirdPartyCostShareAmountTotal = budgetThirdPartyCostShareAmountTotal;
        }

        /**
         * Gets the budgetUniversityCostShareAmountTotal attribute.
         * 
         * @return Returns the budgetUniversityCostShareAmountTotal.
         */
        public List getBudgetUniversityCostShareAmountTotal() {
            return budgetUniversityCostShareAmountTotal;
        }

        /**
         * Sets the budgetUniversityCostShareAmountTotal attribute value.
         * 
         * @param budgetUniversityCostShareAmountTotal The budgetUniversityCostShareAmountTotal to set.
         */
        public void setBudgetUniversityCostShareAmountTotal(List budgetUniversityCostShareAmountTotal) {
            this.budgetUniversityCostShareAmountTotal = budgetUniversityCostShareAmountTotal;
        }

        /**
         * Gets the nprsItems attribute.
         * 
         * @return Returns the nprsItems.
         */
        public List getNprsItems() {
            return nprsItems;
        }

        /**
         * Sets the nprsItems attribute value.
         * 
         * @param nprsItems The nprsItems to set.
         */
        public void setNprsItems(List nprsItems) {
            this.nprsItems = nprsItems;
        }

        /**
         * Gets the newNonpersonnel attribute.
         * 
         * @return Returns the newNonpersonnel.
         */
        public NonpersonnelCopyOverLineItemHelper getNprsItem(int index) {
            while (getNprsItems().size() <= index) {
                getNprsItems().add(new NonpersonnelCopyOverLineItemHelper());
            }
            return (NonpersonnelCopyOverLineItemHelper) getNprsItems().get(index);
        }

        /**
         * For each item NonpersonnelCopyOverCategoryHelper we have this object to essentially represent each nonpersonnel item per
         * period (it helps to look at the nonpersonnel copy over page -- periods go out horizontally, that is what this object with
         * its list represents).
         * 
         * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
         */
        public class NonpersonnelCopyOverLineItemHelper {
            // these two are the same for each item in the periodAmounts list. It is in this object
            // for display convenience.
            private String nonpersonnelSubCategoryName;
            private String budgetNonpersonnelDescription;

            private Integer originBudgetPeriodSequenceNumber; // for ease of determining where the boss is
            private List periodAmounts; // mostly static in size (would have preferred []), but ArrayList will make implementation
                                        // of getPeriodAmount easier which is needed by struts for hidden variables

            /**
             * Constructs a NonpersonnelCopyOverCategoryHelper. Default, no arg constructor
             */
            public NonpersonnelCopyOverLineItemHelper() {
                periodAmounts = new ArrayList();
            }

            /**
             * Constructor for this object. This object does not do too much other then tracking pertinent information for easy
             * retrieval on the copy over page and then (finally!) holding each BudgetNonpersonnelCopyOverBoHelper object in an
             * array.
             * 
             * @param budgetNonpersonnelCopyOverBoHelper the row that this objects represents
             * @param periods Budget.periods
             * @param budgetNonpersonnelInflationRate inflation rate of this budget
             */
            public NonpersonnelCopyOverLineItemHelper(BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnelCopyOverBoHelper, List periods, KualiDecimal budgetNonpersonnelInflationRate) {
                this.nonpersonnelSubCategoryName = budgetNonpersonnelCopyOverBoHelper.getNonpersonnelObjectCode().getNonpersonnelSubCategory().getName();
                this.budgetNonpersonnelDescription = budgetNonpersonnelCopyOverBoHelper.getBudgetNonpersonnelDescription();
                this.originBudgetPeriodSequenceNumber = budgetNonpersonnelCopyOverBoHelper.getBudgetPeriodSequenceNumber();

                // We do this with an array because otherwise we have to fill the list with empty items
                // upto the location of the origin item. The code gets a bit dirty then.
                BudgetNonpersonnelCopyOverBoHelper[] periodAmountsArr = new BudgetNonpersonnelCopyOverBoHelper[periods.size()];

                // Place the origin item
                int originItemIndex = SpringServiceLocator.getBudgetPeriodService().getPeriodIndex(budgetNonpersonnelCopyOverBoHelper.getBudgetPeriodSequenceNumber(), periods);
                periodAmountsArr[originItemIndex] = budgetNonpersonnelCopyOverBoHelper;

                // Fill the items before the origin item.
                this.backwardFillPeriodAmounts(periodAmountsArr, originItemIndex);

                // Next we check if this item has been copied forward, if it hasn't then we have to
                // forward fill it with copy over items that have inflation rate.
                if (!budgetNonpersonnelCopyOverBoHelper.isCopiedOverItem()) {
                    this.forwardFillPeriodAmounts(periodAmountsArr, originItemIndex, budgetNonpersonnelCopyOverBoHelper, periods, budgetNonpersonnelInflationRate);
                }

                periodAmounts = Arrays.asList(periodAmountsArr);
            }

            /**
             * @see org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverFormHelper#deconstruct(BudgetForm budgetForm)
             * @param budgetDocument Budget.document
             */
            public void deconstruct(BudgetDocument budgetDocument) {
                // check if any of the indicators are set, if not, we can skip this item
                if (this.isCopyIndicatorSet()) {
                    // item gets copied from & to every period, starting from the location of the origin item
                    for (int i = this.findOriginItemIndex(); i < periodAmounts.size(); i++) {
                        BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnelCopyOverBoHelper = (BudgetNonpersonnelCopyOverBoHelper) periodAmounts.get(i);

                        if (budgetNonpersonnelCopyOverBoHelper.getBudgetNonpersonnelSequenceNumber() == null) {
                            // item didn't exist as nonpersonnel item yet.

                            // add it to the Budget Document per the standard methods provided, addNonpersonnel will set sequence
                            // number
                            BudgetNonpersonnel newNonpersonnel = budgetNonpersonnelCopyOverBoHelper.getBudgetNonpersonnel();
                            budgetDocument.addNonpersonnel(newNonpersonnel);
                        }
                        else {
                            // item existed before, could be an origin item

                            // find nonpersonnelItem that is to be updated
                            List nonpersonnelItems = budgetDocument.getBudget().getNonpersonnelItems();
                            BudgetNonpersonnel budgetNonpersonnel = SpringServiceLocator.getBudgetNonpersonnelService().findBudgetNonpersonnel(budgetNonpersonnelCopyOverBoHelper.getBudgetNonpersonnelSequenceNumber(), nonpersonnelItems);

                            // update indicators
                            budgetNonpersonnel.setAgencyCopyIndicator(budgetNonpersonnelCopyOverBoHelper.getAgencyCopyIndicator());
                            budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(budgetNonpersonnelCopyOverBoHelper.getBudgetUniversityCostShareCopyIndicator());
                            budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(budgetNonpersonnelCopyOverBoHelper.getBudgetThirdPartyCostShareCopyIndicator());

                            budgetNonpersonnel.setCopyToFuturePeriods(false); // nothing to do with copy over, just make sure the
                                                                                // checkbox is unchecked if "save" is used to copy
                                                                                // over items on NPRS page

                            // update amounts. If indicator set, use inflated amounts, otherwise use original amounts. Need to
                            // check both cases because we don't know if user checked or unchecked field.
                            if (budgetNonpersonnelCopyOverBoHelper.getAgencyCopyIndicator()) {
                                budgetNonpersonnel.setAgencyRequestAmount(budgetNonpersonnelCopyOverBoHelper.getBudgetInflatedAgencyAmount());
                            }
                            else {
                                budgetNonpersonnel.setAgencyRequestAmount(budgetNonpersonnelCopyOverBoHelper.getAgencyRequestAmount());
                            }
                            if (budgetNonpersonnelCopyOverBoHelper.getBudgetUniversityCostShareCopyIndicator()) {
                                budgetNonpersonnel.setBudgetUniversityCostShareAmount(budgetNonpersonnelCopyOverBoHelper.getBudgetInflatedUniversityCostShareAmount());
                            }
                            else {
                                budgetNonpersonnel.setBudgetUniversityCostShareAmount(budgetNonpersonnelCopyOverBoHelper.getBudgetUniversityCostShareAmount());
                            }
                            if (budgetNonpersonnelCopyOverBoHelper.getBudgetThirdPartyCostShareCopyIndicator()) {
                                budgetNonpersonnel.setBudgetThirdPartyCostShareAmount(budgetNonpersonnelCopyOverBoHelper.getBudgetInflatedThirdPartyCostShareAmount());
                            }
                            else {
                                budgetNonpersonnel.setBudgetThirdPartyCostShareAmount(budgetNonpersonnelCopyOverBoHelper.getBudgetThirdPartyCostShareAmount());
                            }

                            if (budgetNonpersonnelCopyOverBoHelper.isOriginItem()) {
                                // update budgetOriginSequenceNumber. This is important if it was an origin item
                                // so that the interface can easily tell if an item has been copied over. This
                                // isn't done in the BudgetNonpersonnelCopyOverBoHelper constructors because we
                                // want to make sure it's only done for items that are copied over.
                                budgetNonpersonnel.setBudgetOriginSequenceNumber(budgetNonpersonnelCopyOverBoHelper.getBudgetNonpersonnelSequenceNumber());
                            }
                        }
                    }
                }
            }

            /**
             * @see org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverFormHelper#refresh()
             */
            public void refresh() {
                BudgetNonpersonnelCopyOverBoHelper originItem = (BudgetNonpersonnelCopyOverBoHelper) periodAmounts.get(this.findOriginItemIndex());

                for (int i = 0; i < periodAmounts.size(); i++) {
                    BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnelCopyOverBoHelper = (BudgetNonpersonnelCopyOverBoHelper) periodAmounts.get(i);

                    if (budgetNonpersonnelCopyOverBoHelper.getAgencyCopyIndicator()) {
                        originItem.setAgencyCopyIndicator(true);
                    }
                    if (budgetNonpersonnelCopyOverBoHelper.getBudgetUniversityCostShareCopyIndicator()) {
                        originItem.setBudgetUniversityCostShareCopyIndicator(true);
                    }
                    if (budgetNonpersonnelCopyOverBoHelper.getBudgetThirdPartyCostShareCopyIndicator()) {
                        originItem.setBudgetThirdPartyCostShareCopyIndicator(true);
                    }
                }
            }

            /**
             * Fills items before the origin item with dummy Nonpersonnel objects. This is so that the interface displays 0 amounts
             * for those. This could be handled via the tag but since struts does the same thing when evaluating hidden variables,
             * we might as well do the same via the constructor.
             * 
             * @see org.kuali.module.kra.budget.web.struts.form.BudgetNonpersonnelCopyOverFormHelper.NonpersonnelCopyOverCategoryHelper.NonpersonnelCopyOverLineItemHelper#getPeriodAmount(int
             *      index)
             * @param periodAmountsArr array passed from constructor so that it's avoided to asList / toArray unnecessarily
             * @param originItemIndex index of the originItem in the array (note: originItemIndex !=
             *        originItem.budgetPeriodSequenceNumberOverride)
             */
            protected void backwardFillPeriodAmounts(BudgetNonpersonnelCopyOverBoHelper[] periodAmountsArr, int originItemIndex) {
                for (int i = 0; i < originItemIndex; i++) {
                    BudgetNonpersonnelCopyOverBoHelper nonpersonnelCopyOverBoHelper = new BudgetNonpersonnelCopyOverBoHelper();
                    periodAmountsArr[i] = nonpersonnelCopyOverBoHelper;
                }
            }

            /**
             * Used for Nonpersonnel items that have not been copied forward. For those we need to forward fill the list with items
             * that contain inflation rates. This will enable displaying those rates (and checkboxes) propertly on the interface. It
             * also makes returning to nonpersonnel easier.
             * 
             * @param periodAmountsArr array passed from constructor so that it's avoided to asList / toArray unnecessarily
             * @param originItemIndex index of the originItem in the array (note: originItemIndex !=
             *        originItem.budgetPeriodSequenceNumberOverride)
             * @param originItem originItem itself
             * @param budgetNonpersonnelInflationRate inflation rate of this budget
             */
            protected void forwardFillPeriodAmounts(BudgetNonpersonnelCopyOverBoHelper[] periodAmountsArr, int originItemIndex, BudgetNonpersonnelCopyOverBoHelper originItem, List periods, KualiDecimal budgetNonpersonnelInflationRate) {
                // Loop over the array starting after the origin item (hence: forwardFill)
                for (int i = originItemIndex + 1; i < periodAmountsArr.length; i++) {

                    // Retrieve a few variables that we need to create the new forward filling object
                    BudgetPeriodService budgetPeriodService = SpringServiceLocator.getBudgetPeriodService();
                    Integer budgetPeriodSequenceNumberOverride = budgetPeriodService.getPeriodAfterOffset(originItem.getBudgetPeriodSequenceNumber(), i - originItemIndex, periods).getBudgetPeriodSequenceNumber();
                    int inflationLength = budgetPeriodService.getPeriodsRange(originItem.getBudgetPeriodSequenceNumber(), budgetPeriodSequenceNumberOverride, periods);

                    // Create the new object and put it in the list
                    BudgetNonpersonnelCopyOverBoHelper nonpersonnelCopyOverBoHelper = new BudgetNonpersonnelCopyOverBoHelper(originItem, budgetPeriodSequenceNumberOverride, inflationLength, budgetNonpersonnelInflationRate);
                    periodAmountsArr[i] = nonpersonnelCopyOverBoHelper;
                }
            }

            /**
             * Adds a BudgetNonpersonnelCopyOverBoHelper object to periodAmounts in this object.
             * 
             * @param budgetNonpersonnelCopyOverBoHelper to be added.
             * @param periods Budget.periods. This is important so that
             *        budgetNonpersonnelCopyOverBoHelper.getBudgetPeriodSequenceNumber() can be used to figure out the spot it
             *        belongs in.
             */
            public void add(BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnelCopyOverBoHelper, List periods) {
                // Since this List was created with Arrays.asList, using List.add is unsupported per
                // JavaDoc. That is why it's put into Array format, added and put back into List. This
                // might seem a bit complicated but as part of a code walkthrough this seemed the cleanest
                // solution.
                BudgetNonpersonnelCopyOverBoHelper[] periodAmountsArr = (BudgetNonpersonnelCopyOverBoHelper[]) periodAmounts.toArray();

                int targetIndex = SpringServiceLocator.getBudgetPeriodService().getPeriodIndex(budgetNonpersonnelCopyOverBoHelper.getBudgetPeriodSequenceNumber(), periods);

                periodAmountsArr[targetIndex] = budgetNonpersonnelCopyOverBoHelper;
                periodAmounts = Arrays.asList(periodAmountsArr);
            }

            /**
             * Checks if any of the get*CopyIndicator's are set for any of this objects elements.
             * 
             * @return indicating if any of the get*CopyIndicator indicators are set for its elements.
             */
            public boolean isCopyIndicatorSet() {
                for (int i = 0; i < periodAmounts.size(); i++) {
                    BudgetNonpersonnelCopyOverBoHelper budgetNonpersonnelCopyOverBoHelper = (BudgetNonpersonnelCopyOverBoHelper) periodAmounts.get(i);

                    if (budgetNonpersonnelCopyOverBoHelper.getAgencyCopyIndicator() || budgetNonpersonnelCopyOverBoHelper.getBudgetUniversityCostShareCopyIndicator() || budgetNonpersonnelCopyOverBoHelper.getBudgetThirdPartyCostShareCopyIndicator()) {
                        return true;
                    }
                }

                return false;
            }

            /**
             * Finds the origin item index in periodAmounts. Returns -1 if it could not be found (although that should never happen
             * except if this object does not have any periodAmounts yet).
             * 
             * @return origin item index
             */
            public int findOriginItemIndex() {
                int originItemIndex = -1;

                // Looks for the origin item (first item that != null) and breaks loop
                // as soon as it is found.
                for (int i = 0; i < periodAmounts.size() && originItemIndex == -1; i++) {
                    if (periodAmounts.get(i) != null && ((BudgetNonpersonnelCopyOverBoHelper) periodAmounts.get(i)).isOriginItem()) {
                        originItemIndex = i;
                    }
                }

                return originItemIndex;
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
             * Sets the budgetNonpersonnelDescription attribute value.
             * 
             * @param budgetNonpersonnelDescription The budgetNonpersonnelDescription to set.
             */
            public void setBudgetNonpersonnelDescription(String budgetNonpersonnelDescription) {
                this.budgetNonpersonnelDescription = budgetNonpersonnelDescription;
            }

            /**
             * Gets the nonpersonnelSubCategoryName attribute.
             * 
             * @return Returns the nonpersonnelSubCategoryName.
             */
            public String getNonpersonnelSubCategoryName() {
                return nonpersonnelSubCategoryName;
            }

            /**
             * Sets the nonpersonnelSubCategoryName attribute value.
             * 
             * @param nonpersonnelSubCategoryName The nonpersonnelSubCategoryName to set.
             */
            public void setNonpersonnelSubCategoryName(String nonpersonnelSubCategoryName) {
                this.nonpersonnelSubCategoryName = nonpersonnelSubCategoryName;
            }

            /**
             * Gets the originBudgetPeriodSequenceNumber attribute.
             * 
             * @return Returns the originBudgetPeriodSequenceNumber.
             */
            public Integer getOriginBudgetPeriodSequenceNumber() {
                return originBudgetPeriodSequenceNumber;
            }

            /**
             * Sets the originBudgetPeriodSequenceNumber attribute value.
             * 
             * @param originBudgetPeriodSequenceNumber The originBudgetPeriodSequenceNumber to set.
             */
            public void setOriginBudgetPeriodSequenceNumber(Integer originBudgetPeriodSequenceNumber) {
                this.originBudgetPeriodSequenceNumber = originBudgetPeriodSequenceNumber;
            }

            /**
             * Gets the periodAmounts attribute.
             * 
             * @return Returns the periodAmounts.
             */
            public List getPeriodAmounts() {
                return periodAmounts;
            }

            /**
             * Sets the periodAmounts attribute value.
             * 
             * @param periodAmounts The periodAmounts to set.
             */
            public void setPeriodAmounts(List periods) {
                this.periodAmounts = periods;
            }

            /**
             * Gets the newNonpersonnel attribute.
             * 
             * @return Returns the newNonpersonnel.
             */
            public BudgetNonpersonnelCopyOverBoHelper getPeriodAmount(int index) {
                while (getPeriodAmounts().size() <= index) {
                    getPeriodAmounts().add(new BudgetNonpersonnelCopyOverBoHelper());
                }
                return (BudgetNonpersonnelCopyOverBoHelper) getPeriodAmounts().get(index);
            }
        }
    }
}
