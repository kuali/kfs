/*
 * Copyright 2010 The Kuali Foundation.
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
