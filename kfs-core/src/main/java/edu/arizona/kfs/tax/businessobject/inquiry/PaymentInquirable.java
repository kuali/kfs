package edu.arizona.kfs.tax.businessobject.inquiry;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.tax.TaxPropertyConstants;

public class PaymentInquirable extends KfsInquirableImpl {

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        if (TaxPropertyConstants.PAYMENT_DOCUMENT_NUMBER.equals(attributeName)) {
            String documentNumber = (String) ObjectUtils.getPropertyValue(businessObject, attributeName);

            if(StringUtils.isNotBlank(documentNumber)) {
                boolean exists = SpringContext.getBean(DocumentService.class).documentExists(documentNumber);

                if (!exists) {
                    return new AnchorHtmlData();
                }
            }
            
            AnchorHtmlData hRef = new AnchorHtmlData(KRADConstants.EMPTY_STRING, KRADConstants.EMPTY_STRING);
            hRef.setHref(SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KRADConstants.WORKFLOW_URL_KEY) + KRADConstants.DOCHANDLER_DO_URL + documentNumber + KRADConstants.DOCHANDLER_URL_CHUNK);

            return hRef;
        }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }

} 
