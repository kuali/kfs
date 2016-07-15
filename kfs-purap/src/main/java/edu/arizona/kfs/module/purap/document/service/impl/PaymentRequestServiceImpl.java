package edu.arizona.kfs.module.purap.document.service.impl;

import org.kuali.rice.krad.util.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.vnd.businessobject.VendorDetail;

import org.kuali.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;

public class PaymentRequestServiceImpl extends org.kuali.kfs.module.purap.document.service.impl.PaymentRequestServiceImpl {
	

    /**
     * Update to baseline method to additionally set the payment method when the vendor is changed.
     */
    @Override
    public void changeVendor(PaymentRequestDocument preq, Integer headerId, Integer detailId) {
        super.changeVendor(preq, headerId, detailId);
        if ( preq instanceof edu.arizona.kfs.module.purap.document.PaymentRequestDocument ) {
            VendorDetail vd = vendorService.getVendorDetail(headerId, detailId);
            if (vd != null
                    && ObjectUtils.isNotNull(vd.getExtension()) ) {
                if ( vd.getExtension() instanceof VendorDetailExtension
                        && StringUtils.isNotBlank( ((VendorDetailExtension)vd.getExtension()).getDefaultB2BPaymentMethodCode() ) ) {
                    ((edu.arizona.kfs.module.purap.document.PaymentRequestDocument)preq).setPaymentMethodCode(
                            ((VendorDetailExtension)vd.getExtension()).getDefaultB2BPaymentMethodCode() );
                }
            }
        }
    }
}