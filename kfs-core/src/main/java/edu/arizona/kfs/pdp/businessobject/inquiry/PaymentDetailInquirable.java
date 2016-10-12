package edu.arizona.kfs.pdp.businessobject.inquiry;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import edu.arizona.kfs.sys.KFSConstants;

import org.apache.commons.lang.StringUtils;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

public class PaymentDetailInquirable extends org.kuali.kfs.pdp.businessobject.inquiry.PaymentDetailInquirable {

    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {
        PaymentDetail paymentDetail = (PaymentDetail) businessObject;
        
        if (PdpPropertyConstants.PaymentDetail.PAYMENT_ID.equals(attributeName) && ObjectUtils.isNotNull(paymentDetail.getId())
               && !StringUtils.equals(paymentDetail.getFinancialDocumentTypeCode(), KFSConstants.DOCUWARE_DV_DOC_TYPE) 
               && !StringUtils.equals(paymentDetail.getFinancialDocumentTypeCode(), KFSConstants.DOCUWARE_PREQ_DOC_TYPE)) {
        	
            Properties params = new Properties();
            String tableValue = SpringContext.getBean(ParameterService.class).getParameterValueAsString(PaymentDetail.class, KFSConstants.DOCUWARE_TABLE_PARAMETER);
            params.put(KFSConstants.DOCUWARE_TABLE, tableValue);
            params.put(KFSConstants.DOCUWARE_IDVALUE, UrlFactory.encode(String.valueOf(paymentDetail.getId())));
            String baseUrl = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.APPLICATION_URL_KEY);
            String url = UrlFactory.parameterizeUrl(baseUrl + "/DocuwareCaller" , params);
            Map<String, String> fieldList = new HashMap<String, String>();
            fieldList.put(PdpPropertyConstants.PaymentDetail.PAYMENT_ID, paymentDetail.getId().toString());
            return getHyperLink(PaymentDetail.class, fieldList, url);
       }

        return super.getInquiryUrl(businessObject, attributeName, forceInquiry);
    }
}
