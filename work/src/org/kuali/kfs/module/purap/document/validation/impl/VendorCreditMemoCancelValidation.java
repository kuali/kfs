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

import org.kuali.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.module.purap.document.service.CreditMemoService;
import org.kuali.kfs.sys.document.validation.GenericValidation;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;

public class VendorCreditMemoCancelValidation extends GenericValidation {

    private CreditMemoService creditMemoService;
    
    public boolean validate(AttributedDocumentEvent event) {
        VendorCreditMemoDocument creditMemoDocument = (VendorCreditMemoDocument) event.getDocument();
        //TODO hjs- need to review, but shouldn't need to check cancel rules here
//        return creditMemoService.canCancelCreditMemo(creditMemoDocument, GlobalVariables.getUserSession().getPerson());
        return true;
    }

    public CreditMemoService getCreditMemoService() {
        return creditMemoService;
    }

    public void setCreditMemoService(CreditMemoService creditMemoService) {
        this.creditMemoService = creditMemoService;
    }

}
