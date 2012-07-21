/*
 * Copyright 2011 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.document.TaxableRamificationDocument;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.document.Document;

public class TaxableRamificationForm extends FinancialSystemTransactionalDocumentFormBase {
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxableRamificationForm.class);
    
    private Map<String, List<Document>> relatedDocuments;
    private Map<String, List<Note>> relatedDocumentNotes;
    private boolean canUnmask = false;

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase#getDefaultDocumentTypeName()
     */
    @Override
    protected String getDefaultDocumentTypeName() {
        return "TXRF";
    }

    /**
     * Gets the relatedDocuments attribute. 
     * @return Returns the relatedDocuments.
     */
    public Map<String, List<Document>> getRelatedDocuments() {
        return relatedDocuments;
    }

    /**
     * Sets the relatedDocuments attribute value.
     * @param relatedDocuments The relatedDocuments to set.
     */
    public void setRelatedDocuments(Map<String, List<Document>> relatedDocuments) {
        this.relatedDocuments = relatedDocuments;
    }
    
    /**
     * get the tax ramification document
     */
    public TaxableRamificationDocument getTaxableRamificationDocument(){
        return (TaxableRamificationDocument)(this.getDocument());
    }

    /**
     * Gets the relatedDocumentNotes attribute. 
     * @return Returns the relatedDocumentNotes.
     */
    public Map<String, List<Note>> getRelatedDocumentNotes() {
        return relatedDocumentNotes;
    }

    /**
     * Sets the relatedDocumentNotes attribute value.
     * @param relatedDocumentNotes The relatedDocumentNotes to set.
     */
    public void setRelatedDocumentNotes(Map<String, List<Note>> relatedDocumentNotes) {
        this.relatedDocumentNotes = relatedDocumentNotes;
    }

    /**
     * Gets the canUnmask attribute. 
     * @return Returns the canUnmask.
     */
    public boolean isCanUnmask() {
        return canUnmask;
    }

    /**
     * Sets the canUnmask attribute value.
     * @param canUnmask The canUnmask to set.
     */
    public void setCanUnmask(boolean canUnmask) {
        this.canUnmask = canUnmask;
    }
}
