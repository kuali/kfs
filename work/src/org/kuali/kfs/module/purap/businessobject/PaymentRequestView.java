package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.purap.document.PaymentRequestDocument;

/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PaymentRequestView extends AbstractRelatedView {
    public List<Note> getNotes() {
        return super.getNotes();
    }
    
    public Integer getPurapDocumentIdentifier() {
        return super.getPurapDocumentIdentifier();
    }
    
    public String getUrl() {
        return super.getUrl();
    }
}
