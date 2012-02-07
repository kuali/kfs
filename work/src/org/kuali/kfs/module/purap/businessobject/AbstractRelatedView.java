/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.purap.businessobject;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiConstants;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Base class for Related View Business Objects.
 */
public abstract class AbstractRelatedView extends PersistableBusinessObjectBase {

    private Integer accountsPayablePurchasingDocumentLinkIdentifier;
    private Integer purapDocumentIdentifier;
    private String documentNumber;
    private String poNumberMasked;

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
            notes = new ArrayList<Note>();
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
        String documentTypeName = this.getDocumentTypeName();
        DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(documentTypeName);
        String docHandlerUrl = docType.getResolvedDocumentHandlerUrl();
        int endSubString = docHandlerUrl.lastIndexOf("/");
        String serverName = docHandlerUrl.substring(0, endSubString);
        String handler = docHandlerUrl.substring(endSubString + 1, docHandlerUrl.lastIndexOf("?"));
        return serverName + "/" + KRADConstants.PORTAL_ACTION + "?channelTitle=" + docType.getName() + "&channelUrl=" + handler + "?" + KRADConstants.DISPATCH_REQUEST_PARAMETER + "=" + KRADConstants.DOC_HANDLER_METHOD +"&" + KRADConstants.PARAMETER_DOC_ID + "=" + this.getDocumentNumber() + "&" + KRADConstants.PARAMETER_COMMAND + "=" + KewApiConstants.DOCSEARCH_COMMAND;
    }

    public String getDocumentIdentifierString() {
        if (purapDocumentIdentifier != null) {
            return purapDocumentIdentifier.toString();
        } else {
            return documentNumber;
        }
    }


    /**
     * Returns the document label according to the label specified in the data dictionary.
     *
     * @return
     * @throws WorkflowException
     */
    public String getDocumentLabel() throws WorkflowException{
        return SpringContext.getBean(DataDictionaryService.class).getDocumentLabelByTypeName(getDocumentTypeName());
    }

    /**
     * @return the document type name for the documents pulled back by this RelatedView
     */
    public abstract String getDocumentTypeName();

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        if (this.accountsPayablePurchasingDocumentLinkIdentifier != null) {
            m.put("accountsPayablePurchasingDocumentLinkIdentifier", this.accountsPayablePurchasingDocumentLinkIdentifier.toString());
        }
        return m;
    }

    /**
     * Gets the poNumberMasked attribute.
     *
     * @return Returns the poNumberMasked
     */

    public String getPoNumberMasked() {
        return poNumberMasked;
    }

    /**
     * Sets the poNumberMasked attribute.
     *
     * @param poNumberMasked The poNumberMasked to set.
     */
    public void setPoNumberMasked(String poNumberMasked) {
        this.poNumberMasked = poNumberMasked;
    }
}
