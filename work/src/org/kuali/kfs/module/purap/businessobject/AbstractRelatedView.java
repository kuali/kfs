/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.businessobject;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kns.bo.Note;
import org.kuali.rice.kns.bo.PersistableBusinessObjectBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.NoteService;
import org.kuali.rice.kns.util.TypedArrayList;

/**
 * Base class for Related View Business Objects.
 */
public abstract class AbstractRelatedView extends PersistableBusinessObjectBase {

    private Integer accountsPayablePurchasingDocumentLinkIdentifier;
    private Integer purapDocumentIdentifier;
    private String documentNumber;

    private List<Note> notes;

    public Integer getAccountsPayablePurchasingDocumentLinkIdentifier() {
        return accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public void setAccountsPayablePurchasingDocumentLinkIdentifier(Integer accountsPayablePurchasingDocumentLinkIdentifier) {
        this.accountsPayablePurchasingDocumentLinkIdentifier = accountsPayablePurchasingDocumentLinkIdentifier;
    }

    public Integer getPurapDocumentIdentifier() {
        return purapDocumentIdentifier;
    }

    public String getDocumentIdentifierString() {
        if (purapDocumentIdentifier != null) {
            return purapDocumentIdentifier.toString();
        } else {
            return documentNumber;
        }
    }
    
    public void setPurapDocumentIdentifier(Integer purapDocumentIdentifier) {
        this.purapDocumentIdentifier = purapDocumentIdentifier;
    }

    public String getDocumentNumber() {
        return documentNumber;
    }

    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    public List<Note> getNotes() {
        if (notes == null) {
            notes = new TypedArrayList(Note.class);
            List<Note> tmpNotes = SpringContext.getBean(NoteService.class).getByRemoteObjectId(this.getObjectId());

            //FIXME if NoteService returns notes in descending order (newer ones first) then remove the following
            // reverse the order of notes retrieved so that newest note is in the front
            for (int i = tmpNotes.size()-1; i>=0; i--) {
                Note note = tmpNotes.get(i);
                notes.add(note);
            }
        }
        
        return notes;
    }

    public String getUrl() {
        return SpringContext.getBean(KualiConfigurationService.class).getPropertyString(KFSConstants.WORKFLOW_URL_KEY) + "/DocHandler.do?docId=" + getDocumentNumber() + "&command=displayDocSearchView";
    }

    /**
     * Returns the document label according to the label specified in the data dictionary.
     * 
     * @return
     * @throws WorkflowException
     */
    public String getDocumentLabel() throws WorkflowException{
        Class documentClass = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(this.getDocumentNumber()).getClass();
        return SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByClass(documentClass);      
    }
    
    /**
     * @see org.kuali.rice.kns.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.accountsPayablePurchasingDocumentLinkIdentifier != null) {
            m.put("accountsPayablePurchasingDocumentLinkIdentifier", this.accountsPayablePurchasingDocumentLinkIdentifier.toString());
        }
        return m;
    }
}
