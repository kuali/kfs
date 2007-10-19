package org.kuali.module.purap.bo;

import java.util.List;

import org.kuali.core.bo.Note;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.purap.service.PurchaseOrderService;


/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchaseOrderView extends AbstractRelatedView {
	private Boolean purchaseOrderCurrentIndicator;

    private List<Note> notes;

    /**
     * Gets the purchaseOrderCurrentIndicator attribute. 
     * @return Returns the purchaseOrderCurrentIndicator.
     */
    public boolean isPurchaseOrderCurrentIndicator() {
        return purchaseOrderCurrentIndicator;
    }

    /**
     * Gets the purchaseOrderCurrentIndicator attribute. 
     * @return Returns the purchaseOrderCurrentIndicator.
     */
    public boolean getPurchaseOrderCurrentIndicator() {
        return purchaseOrderCurrentIndicator;
    }

    /**
     * Sets the purchaseOrderCurrentIndicator attribute value.
     * @param purchaseOrderCurrentIndicator The purchaseOrderCurrentIndicator to set.
     */
    public void setPurchaseOrderCurrentIndicator(boolean purchaseOrderCurrentIndicator) {
        this.purchaseOrderCurrentIndicator = purchaseOrderCurrentIndicator;
    }

    /**
     * The next four methods are overridden but shouldnt be!
     * If they arent overridden, they dont show up in the tag, not sure why at this point! (AAP)
     */
    @Override
    public Integer getPurapDocumentIdentifier() {
        return super.getPurapDocumentIdentifier();
    }
    
    @Override
    public String getDocumentNumber() {
        return super.getDocumentNumber();
    }

    @Override
    public List<Note> getNotes() {
        if (this.isPurchaseOrderCurrentIndicator()) {
            if (notes == null) {
                notes = new TypedArrayList(Note.class);
                List<Note> tmpNotes = SpringContext.getBean(PurchaseOrderService.class).getPurchaseOrderNotes(this.getPurapDocumentIdentifier());
                for (Note note : tmpNotes) {
                    notes.add(note);
                }
            }
        } else {
            notes = null;
        }
        return notes;
    }
    
    @Override
    public String getUrl() {
        return super.getUrl();
    }
}
