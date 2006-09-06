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
package org.kuali.module.kra.budget.service;

import java.util.List;

import org.kuali.core.util.KualiInteger;
import org.kuali.module.cg.bo.Agency;
import org.kuali.module.kra.budget.bo.Budget;

/**
 * This interface defines methods that a BudgetModular service must provide
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public interface BudgetModularService {

    /**
     * Populate the derived Modular Budget values based on the given Budget object.
     * 
     * @param Budget budget
     */
    public void generateModularBudget(Budget budget);

    /**
     * Populate the derived Modular Budget values based on the given Budget object and nonpersonnelCategories.
     * 
     * @param Budget budget
     * @param List nonpersonnelCategories
     */
    public void generateModularBudget(Budget budget, List nonpersonnelCategories);

    /**
     * Recalculate certain Modular Budget values based on the given Budget object.
     * 
     * @param Budget budget
     * @param List nonpersonnelCategories
     */
    public void resetModularBudget(Budget budget);

    /**
     * Determine whether the given agency supports modular budgets.
     * 
     * @param Agency agency
     * @return boolean
     */
    public boolean agencySupportsModular(Agency agency);

    /**
     * Determine the maximum total direct cost amount allowed per period for the given agency.
     * 
     * @param Agency agency
     * @return boolean
     */
    public KualiInteger determineBudgetPeriodMaximumAmount(Agency agency);
}
