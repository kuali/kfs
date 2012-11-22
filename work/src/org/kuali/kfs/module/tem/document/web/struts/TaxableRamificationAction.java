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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.document.TaxableRamificationDocument;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.DynamicCollectionComparator.SortOrder;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentActionBase;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.ObjectUtils;

public class TaxableRamificationAction extends FinancialSystemTransactionalDocumentActionBase {
    private final static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TaxableRamificationAction.class);

    /**
     * @see org.kuali.rice.kns.web.struts.action.KualiDocumentActionBase#loadDocument(org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase)
     */
    @Override
    protected void loadDocument(KualiDocumentFormBase kualiDocumentFormBase) throws WorkflowException {
        super.loadDocument(kualiDocumentFormBase);

        TaxableRamificationForm taxRamificationForm = (TaxableRamificationForm) kualiDocumentFormBase;
        this.refreshRelatedDocuments(taxRamificationForm);
        this.refreshRelatedDocumentNotes(taxRamificationForm);
    }

    /**
     * refresh the related documents
     */
    protected void refreshRelatedDocuments(TaxableRamificationForm taxRamificationForm) {
        Map<String, List<Document>> relatedDocuments = taxRamificationForm.getRelatedDocuments();
        if (ObjectUtils.isNotNull(relatedDocuments) && !relatedDocuments.isEmpty()) {
            return;
        }

        try {
            TaxableRamificationDocument taxRamificationDocument = taxRamificationForm.getTaxableRamificationDocument();
            TravelAdvance travelAdvance = taxRamificationDocument.getTravelAdvance();

            if (ObjectUtils.isNotNull(travelAdvance)) {
                String travelDocumentNumber = travelAdvance.getDocumentNumber();

                relatedDocuments = getTravelDocumentService().getDocumentsRelatedTo(travelDocumentNumber);
                this.addTravelAuthorizationDocumentToRelated(relatedDocuments, travelAdvance);

                taxRamificationForm.setRelatedDocuments(relatedDocuments);
            }
        }
        catch (WorkflowException ex) {
            LOG.error("Failed to get related documents" + ex);

            throw new RuntimeException("Failed to get related documents", ex);
        }
    }

    /**
     * add the related travel authorization document to the related document list
     */
    protected void addTravelAuthorizationDocumentToRelated(Map<String, List<Document>> relatedDocuments, TravelAdvance travelAdvance) throws WorkflowException{
        String travelDocumentNumber = travelAdvance.getDocumentNumber();

        Document travelAuthorizationDocument = SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(travelDocumentNumber);

        String docTypeName = getDataDictionaryService().getDocumentTypeNameByClass(TravelAuthorizationDocument.class);

        List<Document> documents = relatedDocuments.get(docTypeName);
        if (documents == null) {
            documents = new ArrayList<Document>();
        }

        documents.add(travelAuthorizationDocument);

        relatedDocuments.put(docTypeName, documents);
    }

    /**
     *  refresh the related document notes
     */
    protected void refreshRelatedDocumentNotes(TaxableRamificationForm taxRamificationForm) {
        Map<String, List<Document>> relatedDocuments = taxRamificationForm.getRelatedDocuments();
        if (ObjectUtils.isNull(relatedDocuments)  || relatedDocuments.isEmpty()) {
            return;
        }

        Map<String, List<Note>> relatedDocumentNotes = taxRamificationForm.getRelatedDocumentNotes();
        if (ObjectUtils.isNotNull(relatedDocumentNotes) && !relatedDocumentNotes.isEmpty()) {
            return;
        }

        relatedDocumentNotes = new HashMap<String, List<Note>>();
        for (List<Document> documents : relatedDocuments.values()) {
            for (Document document : documents) {
                List<Note> listOfNotes = SpringContext.getBean(NoteService.class).getByRemoteObjectId(document.getDocumentHeader().getObjectId());
                DynamicCollectionComparator.sort(listOfNotes, SortOrder.DESC, "noteIdentifier");
                relatedDocumentNotes.put(document.getDocumentNumber(), listOfNotes);
            }
        }

        taxRamificationForm.setRelatedDocumentNotes(relatedDocumentNotes);
    }

    // get travel document service
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }
}
