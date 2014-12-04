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
package org.kuali.kfs.module.purap.document.validation.impl;

import java.util.Date;
import java.util.List;

import org.kuali.kfs.gl.batch.ScrubberStep;
import org.kuali.kfs.module.purap.businessobject.PurApAccountingLine;
import org.kuali.kfs.module.purap.businessobject.PurApItem;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.util.KNSGlobalVariables;

public class PaymentRequestExpiredAccountWarningValidation extends GenericValidation {

    private PurApItem itemForValidation;
    private DateTimeService dateTimeService;
    private ParameterService parameterService;

    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = true;
        List<PurApAccountingLine> accountingLines = itemForValidation.getSourceAccountingLines();
        for (PurApAccountingLine accountingLine : accountingLines) {
            if (accountingLine.getAccount().isExpired() && !accountingLine.getAccountExpiredOverride()) {
                Date current = dateTimeService.getCurrentDate();
                Date accountExpirationDate = accountingLine.getAccount().getAccountExpirationDate();
                String expirationExtensionDays = parameterService.getParameterValueAsString(ScrubberStep.class, KFSConstants.SystemGroupParameterNames.GL_SCRUBBER_VALIDATION_DAYS_OFFSET);
                int expirationExtensionDaysInt = 3 * 30; // default to 90 days (approximately 3 months)

                if (expirationExtensionDays.trim().length() > 0) {

                    expirationExtensionDaysInt = new Integer(expirationExtensionDays).intValue();
                }
                
                if (!accountingLine.getAccount().isForContractsAndGrants() ||
                     dateTimeService.dateDiff(accountExpirationDate, current, false) < expirationExtensionDaysInt) {
                    KNSGlobalVariables.getMessageList().add(KFSKeyConstants.ERROR_ACCOUNT_EXPIRED);
                    valid &= false;
                    break;
                }
            }
        }
        return valid;
    }

    public DateTimeService getDateTimeService() {
        return dateTimeService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public PurApItem getItemForValidation() {
        return itemForValidation;
    }

    public void setItemForValidation(PurApItem itemForValidation) {
        this.itemForValidation = itemForValidation;
    }

    public ParameterService getParameterService() {
        return parameterService;
    }

    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

}
