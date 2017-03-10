package edu.arizona.kfs.module.cam.businessobject.lookup;

import java.util.List;
import java.util.Properties;

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;

public class AssetRetirementReasonLookupableHelperServiceImpl extends org.kuali.kfs.module.cam.businessobject.lookup.AssetRetirementReasonLookupableHelperServiceImpl {
    
    @Override
    protected HtmlData getReturnAnchorHtmlData(BusinessObject businessObject, Properties parameters, LookupForm lookupForm, List returnKeys, BusinessObjectRestrictions businessObjectRestrictions) {
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, edu.arizona.kfs.module.cam.businessobject.AssetRetirementGlobal.class.getName());
        return super.getReturnAnchorHtmlData(businessObject, parameters, lookupForm, returnKeys, businessObjectRestrictions);
    }
}
