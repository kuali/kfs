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
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAwardAccount;
import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.validation.SuspensionCategoryBase;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Suspension Category that checks to see if the award has any active but expired accounts.
 */
public class ActiveExpiredAccountsSuspensionCategory extends SuspensionCategoryBase {

    protected DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.module.ar.document.validation.SuspensionCategory#shouldSuspend(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        Date now = dateTimeService.getCurrentDate();
        List<ContractsAndGrantsBillingAwardAccount> awardAccounts = contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().getAward().getActiveAwardAccounts();
        for (ContractsAndGrantsBillingAwardAccount awardAccount : awardAccounts) {
            if (ObjectUtils.isNotNull(awardAccount.getAccount())) {
                Date accountExpirationDate = awardAccount.getAccount().getAccountExpirationDate();
                if (accountExpirationDate != null && now.after(accountExpirationDate) && awardAccount.getAccount().isActive()) {
                    return true;
                }
            }
        }
        return false;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
