package edu.arizona.kfs.module.purap.businessobject;

import java.util.List;

import org.kuali.kfs.module.purap.PurapPropertyConstants;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.module.purap.document.service.PurapService;
import org.kuali.kfs.module.purap.exception.PurError;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.ObjectUtils;

public class PaymentRequestItem extends org.kuali.kfs.module.purap.businessobject.PaymentRequestItem {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentRequestItem.class);

	private static final long serialVersionUID = 1L;

	@Override
	public PurchaseOrderItem getPurchaseOrderItem() {
		if (ObjectUtils.isNotNull(this.getPurapDocumentIdentifier())) {
			if (ObjectUtils.isNull(this.getPaymentRequest())) {
				this.refreshReferenceObject(PurapPropertyConstants.PURAP_DOC);
			}
		}

		if (ObjectUtils.isNotNull(getPaymentRequest())) {
			PurchaseOrderDocument po = getPaymentRequest().getPurchaseOrderDocument();
			PurchaseOrderItem poi = null;
			if (this.getItemType().isLineItemIndicator()) {
				List<?> items = po.getItems();
				if (ObjectUtils.isNotNull(items)) {
					for (Object object : items) {
						PurchaseOrderItem item = (PurchaseOrderItem) object;
						if (ObjectUtils.isNotNull(item) && item.getItemLineNumber().equals(this.getItemLineNumber())) {
							poi = item;
							break;
						} else if (ObjectUtils.isNotNull(item) && getItemLineNumber().intValue() <= items.size()) { //This else if section was added to make sure the PREQ does not error when a tax withholding is added
							poi = item;
							break;
						}
					}
				}
			} else {
				poi = (PurchaseOrderItem) SpringContext.getBean(PurapService.class).getBelowTheLineByType(po, this.getItemType());
			}
			if (ObjectUtils.isNotNull(poi)) {
				return poi;
			} else {
				if (LOG.isDebugEnabled()) {
					LOG.debug("getPurchaseOrderItem() Returning null because PurchaseOrderItem object for line number" + getItemLineNumber() + "or itemType " + getItemTypeCode() + " is null");
				}
				return null;
			}
		} else {
			throw new PurError("Payment Request Object in Purchase Order item line number " + getItemLineNumber() + "or itemType " + getItemTypeCode() + " is null");
		}
	}
	
}
