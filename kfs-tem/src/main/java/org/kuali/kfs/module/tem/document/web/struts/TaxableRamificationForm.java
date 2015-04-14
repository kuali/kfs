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
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.document.TaxableRamificationDocument;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;

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
