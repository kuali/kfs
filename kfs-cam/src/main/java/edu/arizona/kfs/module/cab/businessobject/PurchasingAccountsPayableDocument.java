package edu.arizona.kfs.module.cab.businessobject;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import edu.arizona.kfs.module.cab.CabConstants;
import org.kuali.kfs.module.cab.CabPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.util.ObjectUtils;

public class PurchasingAccountsPayableDocument extends org.kuali.kfs.module.cab.businessobject.PurchasingAccountsPayableDocument {
	
    @Override
    public String getStatusDescription() {

        if (StringUtils.isNotBlank(this.statusDescription)) {
            return this.statusDescription;
        }
        else {
            Map<String, Integer> objectKeys = new HashMap<String, Integer>();
            objectKeys.put(CabPropertyConstants.PurchasingAccountsPayableDocument.PURAP_DOCUMENT_IDENTIFIER, this.getPurapDocumentIdentifier());

            if (CabConstants.PREQ.equals(getDocumentTypeCode()) || CabConstants.PRNC.equals(getDocumentTypeCode())) {

                PaymentRequestDocument paymentRequestDocument = (PaymentRequestDocument) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(PaymentRequestDocument.class, objectKeys);
                if (ObjectUtils.isNotNull(paymentRequestDocument)) {
                    statusDescription = paymentRequestDocument.getApplicationDocumentStatus();
                }
            }
            else {
                VendorCreditMemoDocument vendorCreditMemoDocument = (VendorCreditMemoDocument) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(VendorCreditMemoDocument.class, objectKeys);
                if (ObjectUtils.isNotNull(vendorCreditMemoDocument)) {
                    statusDescription = vendorCreditMemoDocument.getApplicationDocumentStatus();
                }
            }
        }

        return statusDescription;
    }
}
