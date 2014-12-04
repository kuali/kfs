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
package org.kuali.kfs.module.endow.document.web.struts;

import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine;
import org.kuali.kfs.module.endow.document.CorporateReorganizationDocument;

public class CorporateReorganizationDocumentForm extends EndowmentTransactionLinesDocumentFormBase {

    public CorporateReorganizationDocumentForm() {
        super();

        // Transaction amount is read only for this document; the filed is empty initially.
        newSourceTransactionLine.setTransactionAmount(null);
        newTargetTransactionLine.setTransactionAmount(null);
        
        // Don't show the Etran code field.
        setShowETranCode(false);
        
        // Don't show the import and add button on the target transaction lines.
        setShowTargetImport(false);
        setShowTargetAdd(true);
        
        // Don't show the import button on the source transaction lines.
        setShowSourceImport(false);
        
        // Make the KEMID field on the target transaction lines read only.
        setTargetKemidReadOnly(true);
        
        // Make the income/principal indicator on the target transaction lines read only.
        setTargetIncomePrincipalIndicatorReadOnly(true);
        
        // Don't want to show all the added target transaction lines since
        // only one target transaction exists.  We just want one line to show-up.
        //setShowTargetTransLines(true);
        
        setShowTargetBalance(false);
        setShowTargetDelete(false);
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "ECR";
    }
    
    /**
     * 
     * This method...
     * @return
     */
    public CorporateReorganizationDocument getCorporateReorganizationDocument() {
        return (CorporateReorganizationDocument) getDocument();
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentFormBase#setNewSourceTransactionLine(org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public void setNewSourceTransactionLine(EndowmentTransactionLine newSourceTransactionLine) {
        super.setNewSourceTransactionLine(newSourceTransactionLine);

        // transaction amount is read only for this document; the filed is empty initially
        newSourceTransactionLine.setTransactionAmount(null);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentFormBase#setNewTargetTransactionLine(org.kuali.kfs.module.endow.businessobject.EndowmentTransactionLine)
     */
    @Override
    public void setNewTargetTransactionLine(EndowmentTransactionLine newTargetTransactionLine) {
        super.setNewTargetTransactionLine(newTargetTransactionLine);
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentFormBase#getShowFromTransactionLine()
     */
    public boolean getShowFromTransactionLine() {
        CorporateReorganizationDocument document = getCorporateReorganizationDocument();
        if (document.getSourceTransactionLines() != null && document.getSourceTransactionLines().size() > 0)
            return false;
        else
            return true;
    }

    /**
     * @see org.kuali.kfs.module.endow.document.web.struts.EndowmentTransactionLinesDocumentFormBase#getShowToTransactionLine()
     */
    @Override
    public boolean getShowToTransactionLine() {
        CorporateReorganizationDocument document = getCorporateReorganizationDocument();
        if (document.getTargetTransactionLines() != null && document.getTargetTransactionLines().size() > 0)
            return false;
        else
            return true;
    }
    
}
