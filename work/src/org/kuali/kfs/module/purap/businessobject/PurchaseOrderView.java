package org.kuali.module.purap.bo;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.bo.Note;
import org.kuali.kfs.util.SpringServiceLocator;


/**
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class PurchaseOrderView extends AbstractRelatedView {
	private Boolean purchaseOrderCurrentIndicator;

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

    public Integer getPurapDocumentIdentifier() {
        return super.getPurapDocumentIdentifier();
    }

    public List<Note> getNotes() {
        return super.getNotes();
    }
    
    public String getUrl() {
        return super.getUrl();
    }
}
