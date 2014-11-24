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
package org.kuali.kfs.module.ld.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.businessobject.LaborJournalVoucherDetail;
import org.kuali.kfs.module.ld.businessobject.PositionData;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Validates that a labor journal voucher document's accounting lines have valid Position Code
 */
public class LaborJournalVoucherPositionCodeExistenceCheckValidation extends GenericValidation {
    private AccountingLine accountingLineForValidation;

    /**
     * Validates that the accounting line in the labor journal voucher document for valid position code
     * 
     * @see org.kuali.kfs.validation.Validation#validate(java.lang.Object[])
     */
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = true;

        LaborJournalVoucherDetail laborJournalVoucherDetail = (LaborJournalVoucherDetail) getAccountingLineForValidation();
        String positionNumber = laborJournalVoucherDetail.getPositionNumber();

        if (StringUtils.isBlank(positionNumber) || LaborConstants.getDashPositionNumber().equals(positionNumber)) {
            return true;
        }

        if (!positionCodeExistenceCheck(positionNumber)) {
            result = false;
        }
        return result;
    }

    /**
     * Checks whether employee id exists
     * 
     * @param positionNumber positionNumber is checked against the collection of position number matches
     * @return True if the given position number exists, false otherwise.
     */
    protected boolean positionCodeExistenceCheck(String positionNumber) {

        boolean positionNumberExists = true;

        Map<String, Object> criteria = new HashMap<String, Object>();
        criteria.put(KFSPropertyConstants.POSITION_NUMBER, positionNumber);

        Collection<PositionData> positionNumberMatches = SpringContext.getBean(BusinessObjectService.class).findMatching(PositionData.class, criteria);
        if (positionNumberMatches == null || positionNumberMatches.isEmpty()) {
            String label = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(PositionData.class.getName()).getAttributeDefinition(KFSPropertyConstants.POSITION_NUMBER).getLabel();
            GlobalVariables.getMessageMap().putError(KFSPropertyConstants.POSITION_NUMBER, KFSKeyConstants.ERROR_EXISTENCE, label);
            positionNumberExists = false;
        }

        return positionNumberExists;
    }

    /**
     * Gets the accountingLineForValidation attribute.
     * 
     * @return Returns the accountingLineForValidation.
     */
    public AccountingLine getAccountingLineForValidation() {
        return accountingLineForValidation;
    }

    /**
     * Sets the accountingLineForValidation attribute value.
     * 
     * @param accountingLineForValidation The accountingLineForValidation to set.
     */
    public void setAccountingLineForValidation(AccountingLine accountingLineForValidation) {
        this.accountingLineForValidation = accountingLineForValidation;
    }
}
