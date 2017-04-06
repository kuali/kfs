package edu.arizona.kfs.sys.service.impl;

import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.web.struts.ElectronicFundTransferForm;

public class ElectronicFundTransferClaimActionHelper extends org.kuali.kfs.sys.service.impl.ElectronicFundTransferClaimActionHelper{

	@Override
    public ActionForward performAction(ElectronicFundTransferForm form, ActionMapping mapping, Map paramMap, String basePath) {

		ActionForward forward = super.performAction(form, mapping, paramMap, basePath);
		if (StringUtils.equals(forward.getName(), KFSConstants.MAPPING_PORTAL)) {
        	// UAF-3061 :  To make sure it is forward to https if basePath is https, and also return to lookup screen.
        	return new ActionForward(getClaimingLookupUrl(form, basePath), true);
		} else {
			return forward;
		}
    }

    private String getClaimingLookupUrl(ElectronicFundTransferForm form, String basePath) {
        // UAF-3061 : url created by UrlFactory is not working
    	StringBuffer sb = new StringBuffer();
    	sb.append(basePath).append("/portal.do?channelTitle=Electronic Payment Claim&channelUrl=").
    	    append(basePath).append("/electronicFundTransfer.do?methodToCall=start");
    	return sb.toString();
    }

}
