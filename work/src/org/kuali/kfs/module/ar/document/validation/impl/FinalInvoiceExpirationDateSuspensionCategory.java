/*
 * Copyright 2014 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ar.document.validation.impl;

import java.util.Date;

import org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument;
import org.kuali.kfs.module.ar.document.validation.SuspensionCategoryBase;
import org.kuali.rice.core.api.datetime.DateTimeService;

/**
 * Suspension Category that checks to make sure the invoice is final if the award is already expired.
 */
public class FinalInvoiceExpirationDateSuspensionCategory extends SuspensionCategoryBase {

    protected DateTimeService dateTimeService;

    /**
     * @see org.kuali.kfs.module.ar.document.validation.SuspensionCategory#shouldSuspend(org.kuali.kfs.module.ar.document.ContractsGrantsInvoiceDocument)
     */
    @Override
    public boolean shouldSuspend(ContractsGrantsInvoiceDocument contractsGrantsInvoiceDocument) {
        Date now = dateTimeService.getCurrentDate();

        return now.after(contractsGrantsInvoiceDocument.getAward().getAwardEndingDate()) && !contractsGrantsInvoiceDocument.getInvoiceGeneralDetail().isFinalBillIndicator();
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

}
