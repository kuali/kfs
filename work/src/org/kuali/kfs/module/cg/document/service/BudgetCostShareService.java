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

import org.kuali.module.kra.budget.bo.BudgetThirdPartyCostShare;
import org.kuali.module.kra.budget.bo.BudgetUniversityCostShare;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.bo.UniversityCostSharePersonnel;

/**
 * 
 * This interface defines methods that a BudgetCostShareService must provide.
 * 
 * @author Kuali Research Administration Team (kualidev@oncourse.iu.edu)
 */
public interface BudgetCostShareService {

    /**
     * This method will remove universityCostSharePersonnel entries that no longer have a personnel entry. It will also remove the
     * entire budgetUniversityCostShare list if universityCostShareIndicator is not set anymore. The same goes for
     * budgetThirdPartyCostShare and budgetThirdPartyCostShareIndicator.
     * 
     * @param universityCostShareIndicator if budgetUniversityCostShare should be removed
     * @param budgetUniversityCostShare
     * @param budgetThirdPartyCostShareIndicator if budgetThirdPartyCostShare should be removed
     * @param budgetThirdPartyCostShare
     * @param personnel list of personnel to check for chart / orgs
     * @param universityCostSharePersonnel chart / orgs to be removed if they arn't present in personnel list
     */
    public void cleanseCostShare(boolean universityCostShareIndicator, List<BudgetUniversityCostShare> budgetUniversityCostShare, boolean budgetThirdPartyCostShareIndicator, List<BudgetThirdPartyCostShare> budgetThirdPartyCostShare, List<BudgetUser> personnel, List<UniversityCostSharePersonnel> universityCostSharePersonnel);

    /**
     * This method will add University Cost Share Personnel entries that have personnel entries if they don't already exist. It does
     * not add entries that don't have a chart or org set (this happens for TO BE NAMEDs).
     * 
     * @param documentHeaderId
     * @param personnel
     * @param universityCostSharePersonnel
     */
    public void reconcileCostShare(String documentHeaderId, List<BudgetUser> personnel, List<UniversityCostSharePersonnel> universityCostSharePersonnel);
}
