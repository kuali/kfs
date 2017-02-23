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
package edu.arizona.kfs.fp.document.validation.impl;

import edu.arizona.kfs.gl.service.GlobalTransactionEditService;
import org.kuali.kfs.fp.document.NonCheckDisbursementDocument;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.businessobject.AccountingLineBase;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.rice.krad.util.GlobalVariables;


public class NonCheckDisbursementRequiredReferenceFieldValidation extends org.kuali.kfs.fp.document.validation.impl.NonCheckDisbursementRequiredReferenceFieldValidation {

    private GlobalTransactionEditService globalTransactionEditService;


    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean valid = super.validate(event);
        NonCheckDisbursementDocument document = (NonCheckDisbursementDocument) event.getDocument();
        Message msg = getGlobalTransactionEditService().isAccountingLineAllowable((AccountingLineBase) getAccountingLineForValidation(), document.getFinancialDocumentTypeCode());
        if (msg != null) {
            GlobalVariables.getMessageMap().putError(KFSConstants.AMOUNT_PROPERTY_NAME, KFSKeyConstants.ERROR_CUSTOM, msg.getMessage());
            valid = false;
        }

        return valid;
    }


    private GlobalTransactionEditService getGlobalTransactionEditService() {
        return globalTransactionEditService;
    }


    public void setGlobalTransactionEditService(GlobalTransactionEditService globalTransactionEditService) {
        this.globalTransactionEditService = globalTransactionEditService;
    }

}
