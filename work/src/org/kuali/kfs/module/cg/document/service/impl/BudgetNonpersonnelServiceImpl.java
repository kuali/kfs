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

import java.util.Iterator;
import java.util.List;

import org.kuali.core.service.impl.DocumentNoteServiceImpl;
import org.kuali.module.kra.budget.bo.BudgetNonpersonnel;
import org.kuali.module.kra.budget.bo.NonpersonnelCategory;
import org.kuali.module.kra.budget.dao.BudgetNonpersonnelDao;
import org.kuali.module.kra.budget.dao.NonpersonnelCategoryDao;
import org.kuali.module.kra.budget.dao.NonpersonnelObjectCodesDao;
import org.kuali.module.kra.budget.service.BudgetNonpersonnelService;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BudgetNonpersonnelServiceImpl implements BudgetNonpersonnelService {

    // set up logging
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DocumentNoteServiceImpl.class);

    private BudgetNonpersonnelDao budgetNonpersonnelDao;
    private NonpersonnelCategoryDao nonpersonnelCategoryDao;
    private NonpersonnelObjectCodesDao nonpersonnelObjectCodesDao;

 
    public void refreshNonpersonnelObjectCode(List nonpersonnelItems) {
        for (Iterator nonpersonnelItem = nonpersonnelItems.iterator(); nonpersonnelItem.hasNext();) {
            BudgetNonpersonnel budgetNonpersonnel = (BudgetNonpersonnel) nonpersonnelItem.next();
            budgetNonpersonnel.refreshReferenceObject("nonpersonnelObjectCode");
        }
    }

    public BudgetNonpersonnel findBudgetNonpersonnel(Integer budgetNonpersonnelSequenceNumber, List nonpersonnelItems) {
        BudgetNonpersonnel budgetNonpersonnel = null;

        for (Iterator nonpersonnelItemsIter = nonpersonnelItems.iterator(); nonpersonnelItemsIter.hasNext();) {
            budgetNonpersonnel = (BudgetNonpersonnel) nonpersonnelItemsIter.next();

            if (budgetNonpersonnelSequenceNumber.equals(budgetNonpersonnel.getBudgetNonpersonnelSequenceNumber())) {
                break;
            }
        }

        return budgetNonpersonnel;
    }

    /**
     * Returns all nonpersonnel categories
     * 
     * @throws Exception
     */
    public List getAllNonpersonnelCategories() {
        List<NonpersonnelCategory> nonpersonnelCategories = nonpersonnelCategoryDao.findAllNonpersonnelCategories();
        
        for(NonpersonnelCategory nonpersonnelCategory : nonpersonnelCategories) {
            nonpersonnelCategory.setNonpersonnelObjectCodes(nonpersonnelObjectCodesDao.findActiveNonpersonnelObjectCodes(nonpersonnelCategory.getCode()));
        }
        
        return nonpersonnelCategories;
    }

    // needed for Spring injection
    /**
     * Sets the data access object
     * 
     * @param d
     */
    public void setBudgetNonpersonnelDao(BudgetNonpersonnelDao d) {
        this.budgetNonpersonnelDao = d;
    }

    /**
     * Sets the nonpersonnelCategoryDao attribute value.
     * 
     * @param nonpersonnelCategoryDao The nonpersonnelCategoryDao to set.
     */
    public void setNonpersonnelCategoryDao(NonpersonnelCategoryDao nonpersonnelCategoryDao) {
        this.nonpersonnelCategoryDao = nonpersonnelCategoryDao;
    }

    /**
     * Sets the nonpersonnelObjectCodesDao attribute value.
     * @param nonpersonnelObjectCodesDao The nonpersonnelObjectCodesDao to set.
     */
    public void setNonpersonnelObjectCodesDao(NonpersonnelObjectCodesDao nonpersonnelObjectCodesDao) {
        this.nonpersonnelObjectCodesDao = nonpersonnelObjectCodesDao;
    }
}
