/*
 * Copyright 2005-2006 The Kuali Foundation
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
package org.kuali.kfs.fp.document.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.fp.businessobject.CapitalAssetInformation;
import org.kuali.kfs.fp.document.CapitalAssetEditable;
import org.kuali.kfs.fp.document.GeneralErrorCorrectionDocument;

/**
 * This class is the Struts specific form object that works in conjunction with the pojo utilities to build the UI.
 */
public class GeneralErrorCorrectionForm extends CapitalAccountingLinesFormBase implements CapitalAssetEditable{
    protected static final long serialVersionUID = 1L;
    
    protected List<CapitalAssetInformation> capitalAssetInformation;

    /**
     * Constructs a GeneralErrorCorrectionForm.java.
     */
    public GeneralErrorCorrectionForm() {
        super();
        
        capitalAssetInformation = new ArrayList<CapitalAssetInformation>();
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "GEC";
    }
    
    /**
     * @return Returns the generalErrorCorrectionDocument.
     */
    public GeneralErrorCorrectionDocument getGeneralErrorCorrectionDocument() {
        return (GeneralErrorCorrectionDocument) getDocument();
    }

    /**
     * @param generalErrorCorrectionDocument The generalErrorCorrectionDocument to set.
     */
    public void setGeneralErrorCorrectionDocument(GeneralErrorCorrectionDocument generalErrorCorrectionDocument) {
        setDocument(generalErrorCorrectionDocument);
    }
    
    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#getCapitalAssetInformation()
     */
    public List<CapitalAssetInformation> getCapitalAssetInformation() {
        return this.capitalAssetInformation;
    }

    /**
     * @see org.kuali.kfs.fp.document.CapitalAssetEditable#setCapitalAssetInformation(org.kuali.kfs.fp.businessobject.CapitalAssetInformation)
     */
    public void setCapitalAssetInformation(List<CapitalAssetInformation> capitalAssetInformation) {
        this.capitalAssetInformation = capitalAssetInformation;        
    }
}
