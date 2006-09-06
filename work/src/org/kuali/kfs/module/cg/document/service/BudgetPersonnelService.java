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

import java.util.HashMap;
import java.util.List;

import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.KualiInteger;
import org.kuali.module.kra.budget.bo.BudgetFringeRate;
import org.kuali.module.kra.budget.bo.BudgetPeriod;
import org.kuali.module.kra.budget.bo.BudgetUser;
import org.kuali.module.kra.budget.document.BudgetDocument;

/**
 * This class...
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public interface BudgetPersonnelService {

    public void createPersonnelDetail(BudgetUser budgetUser, BudgetDocument budgetDocument);

    public KualiInteger calculatePeriodSalary(BudgetUser budgetUser, BudgetFringeRate budgetFringeRate, BudgetPeriod period, KualiDecimal inflationRate);

    public void calculatePersonSalary(BudgetUser budgetUser, BudgetDocument budgetDocument);

    public void calculatePersonRequestAmounts(BudgetUser budgetUser, List budgetFringeRates);

    public void calculatePersonCompensation(BudgetUser budgetUser, BudgetDocument budgetDocument);

    public void calculateAllPersonnelCompensation(BudgetDocument budgetDocument);

    public void reconcileAllPersonnelTaskPeriod(BudgetDocument budgetDocument);

    public void reconcilePersonTaskPeriod(BudgetUser budgetUser, BudgetDocument budgetDocument);

    public HashMap getAppointmentTypeMappings();

    public void cleansePersonnel(BudgetDocument budgetDocument);

    public void reconcileProjectDirector(BudgetDocument budgetDocument);

    public void reconcileAndCalculatePersonnel(BudgetDocument budgetDocument);

}
