package org.kuali.module.purap.bo;

import java.util.LinkedHashMap;
import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.module.purap.document.PurchasingAccountsPayableDocumentBase;
import org.kuali.module.purap.document.RequisitionDocument;

/**
 * Requisition View Business Object.
 */
public class RequisitionView extends AbstractRelatedView {
    private Integer requisitionIdentifier;

	public Integer getRequisitionIdentifier() {
		return requisitionIdentifier;
	}

	public void setRequisitionIdentifier(Integer requisitionIdentifier) {
		this.requisitionIdentifier = requisitionIdentifier;
	}

    /**
     * The next three methods are overridden but shouldnt be!
     * If they arent overridden, they dont show up in the tag, not sure why at this point! (AAP)
     * 
     * @see org.kuali.module.purap.bo.AbstractRelatedView#getPurapDocumentIdentifier()
     */
    @Override
    public Integer getPurapDocumentIdentifier() {
        return super.getPurapDocumentIdentifier();
    }

    /**
     * @see org.kuali.module.purap.bo.AbstractRelatedView#getNotes()
     */
    @Override
    public List<Note> getNotes() {
        return super.getNotes();
    }

    /**
     * @see org.kuali.module.purap.bo.AbstractRelatedView#getUrl()
     */
    @Override
    public String getUrl() {
        return super.getUrl();
    }
}
