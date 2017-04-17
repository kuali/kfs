package edu.arizona.kfs.fp.document.validation.impl;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.document.validation.event.AttributedDocumentEvent;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.fp.businessobject.PaymentMethod;
import edu.arizona.kfs.vnd.businessobject.VendorDetailExtension;

/**
 * Adding check to make sure default payment method on the vendor is valid for use on the DV document.
 */
public class DisbursementVoucherVendorInformationValidation extends org.kuali.kfs.fp.document.validation.impl.DisbursementVoucherVendorInformationValidation {

    protected BusinessObjectService businessObjectService;
    
    @Override
    public boolean validate(AttributedDocumentEvent event) {
        boolean result = super.validate(event);
        if ( result ) { // only continue check if vendor is otherwise valid
            DisbursementVoucherDocument document = (DisbursementVoucherDocument) getAccountingDocumentForValidation();
            DisbursementVoucherPayeeDetail payeeDetail = document.getDvPayeeDetail();

            if (payeeDetail.isVendor()) { // only continue check if a vendor
                VendorDetail vendor = retrieveVendorDetail(payeeDetail.getDisbVchrVendorHeaderIdNumberAsInteger(), payeeDetail.getDisbVchrVendorDetailAssignedIdNumberAsInteger());

                MessageMap errors = GlobalVariables.getMessageMap();
                errors.addToErrorPath(KFSPropertyConstants.DOCUMENT);
                
                // check if we have a proper extension object to work with
                if ( ObjectUtils.isNotNull( vendor.getExtension() ) && vendor.getExtension() instanceof VendorDetailExtension ) {
                    VendorDetailExtension ve = (VendorDetailExtension)vendor.getExtension();
                    if ( StringUtils.isNotBlank( ve.getDefaultB2BPaymentMethodCode() ) ) {
                        PaymentMethod pm = businessObjectService.findBySinglePrimaryKey(PaymentMethod.class, ve.getDefaultB2BPaymentMethodCode() );
                        // finally, if we can get to the flag on the payment method and it is not allowed on the DV, set the result to false
                        // and include an error message
                        if ( pm != null && !pm.isDisplayOnDisbursementVoucherDocument() ) {
                            errors.putError(DV_PAYEE_ID_NUMBER_PROPERTY_PATH, KFSKeyConstants.ERROR_DV_DISALLOWED_BY_PAYMENT_METHOD);
                            result = false;
                        }
                    }
                }
                
                errors.removeFromErrorPath(KFSPropertyConstants.DOCUMENT);
            }
        }
        return result;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
