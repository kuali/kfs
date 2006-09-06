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
package org.kuali.module.kra.budget.web.struts.form;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.core.util.KualiInteger;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.NonpersonnelCategory;


/**
 * This is used by the UI to get totals, counts, and other things needed to render the page properly.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetNonpersonnelFormHelper {

    // This map holds the individual NonpersonnelCategoryHelper objects.
    // The Key is the nonpersonnel category code, the value is (obviously) the NonpersonnelCategoryHelper
    Map nonpersonnelCategoryHelperMap = new HashMap();

    /**
     * Constructs a BudgetNonpersonnelFormHelper.java. Default, no arg constructor
     */
    public BudgetNonpersonnelFormHelper() {
    }

    /**
     * Constructs a BudgetNonpersonnelFormHelper.java based on data extracted from a BudgetForm. Added as a convienence.
     */
    public BudgetNonpersonnelFormHelper(BudgetForm budgetForm) {
        this(budgetForm.getCurrentTaskNumber(), budgetForm.getCurrentPeriodNumber(), budgetForm.getNonpersonnelCategories(), budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems(), true);
    }

    /**
     * Constructs a BudgetNonpersonnelFormHelper.java based on data relevent data.
     */
    public BudgetNonpersonnelFormHelper(Integer currentTaskNumber, Integer currentPeriodNumber, List nonpersonnelCategories, List nonpersonnelItems, boolean includesModularExcluded) {
        this.refresh(nonpersonnelItems);
        setupNonpersonnelCategories(nonpersonnelCategories);
        addNonpersonnelItems(currentTaskNumber, currentPeriodNumber, nonpersonnelItems, includesModularExcluded);
    }

    /**
     * This ensures that on each page refresh the 3 amount fields are compared to 3 backup amount fields (*Duplicate). The purpose
     * of that is to detect a change in any of those fields and uncheck the respective copy over indicator. That is a functional
     * requirement.
     * 
     * @param nonpersonnelItems budgetForm.getBudgetDocument().getBudget().getNonpersonnelItems()
     */
    public void refresh(List nonpersonnelItems) {
        for (Iterator nonpersonnelItemsIter = nonpersonnelItems.iterator(); nonpersonnelItemsIter.hasNext();) {
            BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) nonpersonnelItemsIter.next();

            // Check each of the three indicators, ignore if they are null (that's first page entry or items
            // that are currently not displaied, don't care about that) and check if field was changed. If it
            // was, unset the copy indicator.
            if (budgetNonpersonnel.getAgencyRequestAmountBackup() != null && !budgetNonpersonnel.getAgencyRequestAmountBackup().equals(budgetNonpersonnel.getAgencyRequestAmount())) {
                budgetNonpersonnel.setAgencyCopyIndicator(false);
            }

            if (budgetNonpersonnel.getBudgetUniversityCostShareAmountBackup() != null && !budgetNonpersonnel.getBudgetUniversityCostShareAmountBackup().equals(budgetNonpersonnel.getBudgetUniversityCostShareAmount())) {
                budgetNonpersonnel.setBudgetUniversityCostShareCopyIndicator(false);
            }

            if (budgetNonpersonnel.getBudgetThirdPartyCostShareAmountBackup() != null && !budgetNonpersonnel.getBudgetThirdPartyCostShareAmountBackup().equals(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount())) {
                budgetNonpersonnel.setBudgetThirdPartyCostShareCopyIndicator(false);
            }

            // Set the duplicate amounts to what we currently have so that the comparision can take place again
            // next request.
            budgetNonpersonnel.setAgencyRequestAmountBackup(budgetNonpersonnel.getAgencyRequestAmount());
            budgetNonpersonnel.setBudgetUniversityCostShareAmountBackup(budgetNonpersonnel.getBudgetUniversityCostShareAmount());
            budgetNonpersonnel.setBudgetThirdPartyCostShareAmountBackup(budgetNonpersonnel.getBudgetThirdPartyCostShareAmount());
        }
    }

    /**
     * This method aggregates Nonpesronnel Item data into the proper Category breakdown.
     * 
     * @param currentTaskNumber
	 * @param currentPeriodNumber
	 * @param nonpersonnelItems
	 * @param includesModularExcluded
     */
    public void addNonpersonnelItems(Integer currentTaskNumber, Integer currentPeriodNumber, List nonpersonnelItems, boolean includesModularExcluded) {
        int itemIndex = 0;
        for (Iterator i = nonpersonnelItems.iterator(); i.hasNext(); itemIndex++) {
            BudgetNonpersonnel nonpersonnelItem = (BudgetNonpersonnel) i.next();
            if (includesModularExcluded || !nonpersonnelItem.getNonpersonnelObjectCode().getNonpersonnelSubCategory().isNonpersonnelModularExcludedIndicator()) {
                String nonpersonnelItemCategoryCode = nonpersonnelItem.getBudgetNonpersonnelCategoryCode();
                if ((currentTaskNumber.intValue() == 0 && currentPeriodNumber.intValue() == 0) || (currentTaskNumber.intValue() == 0 && nonpersonnelItem.getBudgetPeriodSequenceNumber().equals(currentPeriodNumber)) || (nonpersonnelItem.getBudgetTaskSequenceNumber().equals(currentTaskNumber) && currentPeriodNumber.intValue() == 0) || (nonpersonnelItem.getBudgetTaskSequenceNumber().equals(currentTaskNumber) && nonpersonnelItem.getBudgetPeriodSequenceNumber().equals(currentPeriodNumber))) {
                    if (nonpersonnelCategoryHelperMap.get(nonpersonnelItemCategoryCode) != null) {
                        NonpersonnelCategoryHelper categoryHelper = (NonpersonnelCategoryHelper) nonpersonnelCategoryHelperMap.get(nonpersonnelItemCategoryCode);
                        categoryHelper.add(nonpersonnelItem, itemIndex);
                    }
                    else {
                        nonpersonnelCategoryHelperMap.put(nonpersonnelItemCategoryCode, new NonpersonnelCategoryHelper(nonpersonnelItem, itemIndex));
                    }
                }
            }
        }
    }

    /**
     * This method sets up a NonpersonnelCategoryHelper object for each Nonpersonnel Category
     * 
     * @param nonpersonnelItems
     */
    public void setupNonpersonnelCategories(List nonpersonnelCategories) {
        for (Iterator i = nonpersonnelCategories.iterator(); i.hasNext();) {
            NonpersonnelCategory nonpersonnelCategory = (NonpersonnelCategory) i.next();
            if (nonpersonnelCategoryHelperMap.get(nonpersonnelCategory.getCode()) == null) {
                nonpersonnelCategoryHelperMap.put(nonpersonnelCategory.getCode(), new NonpersonnelCategoryHelper(nonpersonnelCategory));
                Collections.sort(nonpersonnelCategory.getNonpersonnelObjectCodes());
            }
        }
    }

    /**
     * Gets the budgetNonpersonnelHelperMap attribute.
     * 
     * @return Returns the budgetNonpersonnelHelperMap.
     */
    public Map getNonpersonnelCategoryHelperMap() {
        return nonpersonnelCategoryHelperMap;
    }

    /**
     * Sets the budgetNonpersonnelHelperMap attribute value.
     * 
     * @param budgetNonpersonnelHelperMap The budgetNonpersonnelHelperMap to set.
     */
    public void setNonpersonnelCategoryHelperMap(Map budgetNonpersonnelHelperMap) {
        this.nonpersonnelCategoryHelperMap = budgetNonpersonnelHelperMap;
    }

    /**
     * Gets the agencyTotal for all budgetNonpersonnelHelperMap attributes.
     * 
     * @return Returns the agencyTotal for all budgetNonpersonnelHelperMap attributes.
     */
    public KualiInteger getNonpersonnelAgencyTotal() {
        Iterator iter = nonpersonnelCategoryHelperMap.values().iterator();
        KualiInteger agencyTotal = new KualiInteger(0);

        while (iter.hasNext()) {
            NonpersonnelCategoryHelper helper = (NonpersonnelCategoryHelper) iter.next();
            agencyTotal = agencyTotal.add(helper.getAgencyTotal());
        }

        return agencyTotal;
    }

    /**
     * Gets the univCostShareTotal for all budgetNonpersonnelHelperMap attributes.
     * 
     * @return Returns the univCostShareTotal for all budgetNonpersonnelHelperMap attributes.
     */
    public KualiInteger getNonpersonnelUnivCostShareTotal() {
        Iterator iter = nonpersonnelCategoryHelperMap.values().iterator();
        KualiInteger univCostShareTotal = new KualiInteger(0);

        while (iter.hasNext()) {
            NonpersonnelCategoryHelper helper = (NonpersonnelCategoryHelper) iter.next();
            univCostShareTotal = univCostShareTotal.add(helper.getUnivCostShareTotal());
        }

        return univCostShareTotal;
    }

    /**
     * Gets the thirdPartyCostShareTotal for all budgetNonpersonnelHelperMap attributes.
     * 
     * @return Returns the thirdPartyCostShareTotal for all budgetNonpersonnelHelperMap attributes.
     */
    public KualiInteger getNonpersonnelThirdPartyCostShareTotal() {
        Iterator iter = nonpersonnelCategoryHelperMap.values().iterator();
        KualiInteger thirdPartyCostShareTotal = new KualiInteger(0);

        while (iter.hasNext()) {
            NonpersonnelCategoryHelper helper = (NonpersonnelCategoryHelper) iter.next();
            thirdPartyCostShareTotal = thirdPartyCostShareTotal.add(helper.getThirdPartyCostShareTotal());
        }

        return thirdPartyCostShareTotal;
    }

    /**
     * This class holds information relevent to each NonpersonnelCategory. It tells the number of items, totals (agency, univ. cost
     * share, and third party cost share), and item indexes.
     * 
     * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
     */

    public class NonpersonnelCategoryHelper {

        private String nonpersonnelCategoryCode;
        private int numItems = 0;
        private KualiInteger agencyTotal = new KualiInteger(0);
        private KualiInteger univCostShareTotal = new KualiInteger(0);
        private KualiInteger thirdPartyCostShareTotal = new KualiInteger(0);
        private List itemIndexes = new ArrayList();

        /**
         * Constructs a BudgetNonpersonnelFormHelper.java and initializes the NonpersonnelCategory that this helper helps
         * 
         * @param nonpersonnelCategory
         */
        public NonpersonnelCategoryHelper(NonpersonnelCategory nonpersonnelCategory) {
            this.setNonpersonnelCategoryCode(nonpersonnelCategory.getCode());
        }

        /**
         * Constructs a BudgetNonpersonnelFormHelper.java and initializes the NonpersonnelCategory via a NonpersonnelItem.
         * 
         * @param nonpersonnelCategory
         */
        public NonpersonnelCategoryHelper(BudgetNonpersonnel nonpersonnelItem, int itemIndex) {
            this.setNonpersonnelCategoryCode(nonpersonnelItem.getBudgetNonpersonnelCategoryCode());
            this.add(nonpersonnelItem, itemIndex);
        }

        /**
         * This method adds appropriate data about a NonpersonnelItem to this NonpersonnelCategoryHelper
         * 
         * @param nonpersonnelItem
         * @param itemIndex
         */
        public void add(BudgetNonpersonnel nonpersonnelItem, int itemIndex) {
            this.numItems++;
            if (nonpersonnelItem.getAgencyRequestAmount() != null)
                agencyTotal = agencyTotal.add(nonpersonnelItem.getAgencyRequestAmount());

            if (nonpersonnelItem.getBudgetUniversityCostShareAmount() != null)
                univCostShareTotal = univCostShareTotal.add(nonpersonnelItem.getBudgetUniversityCostShareAmount());

            if (nonpersonnelItem.getBudgetThirdPartyCostShareAmount() != null)
                thirdPartyCostShareTotal = thirdPartyCostShareTotal.add(nonpersonnelItem.getBudgetThirdPartyCostShareAmount());

            itemIndexes.add(Integer.toString(itemIndex));
        }

        /**
         * Gets the agencyTotal attribute.
         * 
         * @return Returns the agencyTotal.
         */
        public KualiInteger getAgencyTotal() {
            return agencyTotal;
        }

        /**
         * Sets the agencyTotal attribute value.
         * 
         * @param agencyTotal The agencyTotal to set.
         */
        public void setAgencyTotal(KualiInteger agencyTotal) {
            this.agencyTotal = agencyTotal;
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
         * Gets the numItems attribute.
         * 
         * @return Returns the numItems.
         */
        public int getNumItems() {
            return numItems;
        }

        /**
         * Sets the numItems attribute value.
         * 
         * @param numItems The numItems to set.
         */
        public void setNumItems(int numItems) {
            this.numItems = numItems;
        }

        /**
         * Gets the thirdPartyCostShareTotal attribute.
         * 
         * @return Returns the thirdPartyCostShareTotal.
         */
        public KualiInteger getThirdPartyCostShareTotal() {
            return thirdPartyCostShareTotal;
        }

        /**
         * Sets the thirdPartyCostShareTotal attribute value.
         * 
         * @param thirdPartyCostShareTotal The thirdPartyCostShareTotal to set.
         */
        public void setThirdPartyCostShareTotal(KualiInteger thirdPartyCostShareTotal) {
            this.thirdPartyCostShareTotal = thirdPartyCostShareTotal;
        }

        /**
         * Gets the univCostShareTotal attribute.
         * 
         * @return Returns the univCostShareTotal.
         */
        public KualiInteger getUnivCostShareTotal() {
            return univCostShareTotal;
        }

        /**
         * Sets the univCostShareTotal attribute value.
         * 
         * @param univCostShareTotal The univCostShareTotal to set.
         */
        public void setUnivCostShareTotal(KualiInteger univCostShareTotal) {
            this.univCostShareTotal = univCostShareTotal;
        }

        /**
         * Gets the itemIndexes attribute.
         * 
         * @return Returns the itemIndexes.
         */
        public List getItemIndexes() {
            return itemIndexes;
        }

        /**
         * Sets the itemIndexes attribute value.
         * 
         * @param itemIndexes The itemIndexes to set.
         */
        public void setItemIndexes(List itemIndexes) {
            this.itemIndexes = itemIndexes;
        }
    }
}
