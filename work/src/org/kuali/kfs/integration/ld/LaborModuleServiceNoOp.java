/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.integration.ld;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.util.KualiDecimal;

public class LaborModuleServiceNoOp implements LaborModuleService {

    private Logger LOG = Logger.getLogger(getClass()); 

    public KualiDecimal calculateFringeBenefit(Integer fiscalYear, String chartCode, String objectCode, KualiDecimal salaryAmount) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return KualiDecimal.ZERO;
    }

    public KualiDecimal calculateFringeBenefitFromLaborObject(LaborLedgerObject laborLedgerObject, KualiDecimal salaryAmount) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return KualiDecimal.ZERO;
    }

    public int countPendingSalaryExpenseTransfer(String emplid) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return 0;
    }

    public void createAndBlankApproveSalaryExpenseTransferDocument(String documentDescription, String explanation, String annotation, List<String> adHocRecipients, List<LaborLedgerExpenseTransferAccountingLine> sourceAccoutingLines, List<LaborLedgerExpenseTransferAccountingLine> targetAccoutingLines) throws WorkflowException {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
    }

    public boolean doesLaborLedgerPositionObjectGroupExist(String positionObjectGroupCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public List<String> findEmployeesWithPayType(Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    public Collection<LaborLedgerBalance> findLedgerBalances(Map<String, List<String>> fieldValues, Map<String, List<String>> excludedFieldValues, Set<Integer> fiscalYears, List<String> balanceTypes, List<String> positionObjectGroupCodes) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

    public boolean hasFringeBenefitProducingObjectCodes(Integer fiscalYear, String chartOfAccountsCode, String financialObjectCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public boolean hasPendingLaborLedgerEntry(String chartOfAccountsCode, String accountNumber) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public boolean isEmployeeWithPayType(String emplid, Map<Integer, Set<String>> payPeriods, List<String> balanceTypes, Map<String, Set<String>> earnCodePayGroupMap) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return false;
    }

    public LaborLedgerObject retrieveLaborLedgerObject(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    public LaborLedgerObject retrieveLaborLedgerObject(ObjectCode financialObject) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return null;
    }

    public List<LaborLedgerPositionObjectBenefit> retrieveLaborPositionObjectBenefits(Integer fiscalYear, String chartOfAccountsCode, String objectCode) {
        LOG.warn( "Using No-Op " + getClass().getSimpleName() + " service." );
        return Collections.emptyList();
    }

}
