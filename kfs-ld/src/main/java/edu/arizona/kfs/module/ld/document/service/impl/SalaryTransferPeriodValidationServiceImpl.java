/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package edu.arizona.kfs.module.ld.document.service.impl;

import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ec.EffortCertificationReport;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.businessobject.ExpenseTransferAccountingLine;
import org.kuali.kfs.module.ld.document.SalaryExpenseTransferDocument;
import org.kuali.kfs.module.ld.document.service.SalaryTransferPeriodValidationService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.util.GlobalVariables;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @see SalaryTransferPeriodValidationService
 */
@Transactional
public class SalaryTransferPeriodValidationServiceImpl extends org.kuali.kfs.module.ld.document.service.impl.SalaryTransferPeriodValidationServiceImpl {

    private GlobalTransactionEditService globalTransactionEditService;

    /**
     * @see SalaryTransferPeriodValidationService#validateTransfers(SalaryExpenseTransferDocument)
     */
    @Override
    public boolean validateTransfers(SalaryExpenseTransferDocument document) {
        List<ExpenseTransferAccountingLine> transferLinesInOpenPeriod = new ArrayList<ExpenseTransferAccountingLine>();

        // check for closed or open reporting period(s) ... closed periods result in error, open periods require more validation
        List<ExpenseTransferAccountingLine> allLines = new ArrayList<ExpenseTransferAccountingLine>(document.getSourceAccountingLines());
        allLines.addAll(document.getTargetAccountingLines());
        for (ExpenseTransferAccountingLine transferLine : allLines) {
            // check we have enough data for validation, if not business rules will report error
            if (!containsNecessaryData(transferLine)) {
                continue;
            }

            // if closed report found then return error
            EffortCertificationReport closedReport = getClosedReportingPeriod(transferLine);
            if (closedReport != null) {
                putError(LaborKeyConstants.ERROR_EFFORT_CLOSED_REPORT_PERIOD, transferLine, closedReport);
                return false;
            }

            // if open report(s) found then add transfer line to list for further validation
            EffortCertificationReport openReport = getOpenReportingPeriod(transferLine);
            if (openReport != null) {
                transferLinesInOpenPeriod.add(transferLine);
            }
        }

        // verify transfers will not affect the open reporting period
        Map<String, KualiDecimal> accountPeriodTransfer = new HashMap<String, KualiDecimal>();
        for (ExpenseTransferAccountingLine transferLine : transferLinesInOpenPeriod) {
            EffortCertificationReport emplidReport = isEmployeeWithOpenCertification(transferLine, document.getEmplid());
            if (emplidReport != null) {
                // if employee has a report, transfer lines cannot use cost share sub-accounts
                if (isCostShareSubAccount(transferLine)) {
                    putError(LaborKeyConstants.ERROR_EFFORT_OPEN_PERIOD_COST_SHARE, transferLine, emplidReport);
                    return false;
                }

                // add line amount for validation later
                addAccountTransferAmount(accountPeriodTransfer, transferLine, emplidReport);
            } else {
                // if employee does not have a report, transfer lines cannot use CG accounts
                if (transferLine.getAccount().isForContractsAndGrants()) {
                    EffortCertificationReport openReport = getOpenReportingPeriod(transferLine);
                    putError(LaborKeyConstants.ERROR_EFFORT_OPEN_PERIOD_CG_ACCOUNT, transferLine, openReport);
                    return false;
                }
            }
        }

        // verify balance is same for accounts in transfer map
        for (String transferKey : accountPeriodTransfer.keySet()) {
            KualiDecimal transfer = accountPeriodTransfer.get(transferKey);
            if (transfer.isNonZero()) {
                String[] keyFields = StringUtils.split(transferKey, ",");
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, LaborKeyConstants.ERROR_EFFORT_OPEN_PERIOD_ACCOUNTS_NOT_BALANCED, new String[]{keyFields[4], keyFields[0], keyFields[1]});
                return false;
            }
        }

        for (ExpenseTransferAccountingLine transferLine : allLines) {
            // check we have enough data for validation, if not business rules will report error
            Message msg = globalTransactionEditService.isAccountingLineAllowable((AccountingLineBase) transferLine, document.getFinancialDocumentTypeCode());
            if (msg != null) {
                GlobalVariables.getMessageMap().putError(KFSPropertyConstants.SOURCE_ACCOUNTING_LINES, KFSKeyConstants.ERROR_CUSTOM, msg.getMessage());
                return false;
            }
        }

        return true;
    }


    /**
     * Sets the globalTransactionEditService attribute value.
     *
     * @param globalTransactionEditService The globalTransactionEditService to set.
     */
    public void setGlobalTransactionEditService(GlobalTransactionEditService globalTransactionEditService) {
        this.globalTransactionEditService = globalTransactionEditService;
    }

}

