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
package org.kuali.kfs.module.cam.document.web.struts;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.module.cam.document.AssetTransferDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;

public class AssetTransferForm extends FinancialSystemTransactionalDocumentFormBase {
    protected boolean loanNoteAdded;

    public AssetTransferForm() {
        super();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "AT";
    }
    
    public AssetTransferDocument getAssetTransferDocument() {
        return (AssetTransferDocument) getDocument();
    }

    @Override
    public void populate(HttpServletRequest request) {
        super.populate(request);
        SpringContext.getBean(BusinessObjectDictionaryService.class).performForceUppercase(getAssetTransferDocument());
    }

    @Override
    public void reset(ActionMapping mapping, HttpServletRequest request) {
        super.reset(mapping, request);
        getAssetTransferDocument().setInterdepartmentalSalesIndicator(false);
    }

    public boolean isLoanNoteAdded() {
        return loanNoteAdded;
    }

    public void setLoanNoteAdded(boolean loanNoteAdded) {
        this.loanNoteAdded = loanNoteAdded;
    }

}
