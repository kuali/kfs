/*
 * Copyright 2006 The Kuali Foundation.
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
