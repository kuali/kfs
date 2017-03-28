package edu.arizona.kfs.fp.businessobject.lookup;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.util.UrlFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;


public class ProcurementCardDefaultLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl{

    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        //override pcard credit card number PK (which is encrypted and should not be displayed in links)  with pcard id
        List overriddenKeys = new ArrayList(1);
        overriddenKeys.add( new String(KFSConstants.PCARD_DEFAULT_ID_FIELD) );
        List<HtmlData> htmlDataList = super.getCustomActionUrls(businessObject, overriddenKeys);

        return htmlDataList;
    }

    @Override
    protected String getActionUrlHref(BusinessObject businessObject, String methodToCall, List pkNames) {
        Properties parameters = new Properties();
        parameters.put("methodToCall", methodToCall);
        parameters.put("businessObjectClassName", businessObject.getClass().getName());
        //override pcard credit card number PK (which is encrypted and should not be displayed in links)  with pcard id
        parameters.put(KFSConstants.OVERRIDE_KEYS, KFSConstants.PCARD_DEFAULT_ID_FIELD);
        parameters.putAll(this.getParametersFromPrimaryKey(businessObject, pkNames));
        if(StringUtils.isNotBlank(this.getReturnLocation())) {
            parameters.put("returnLocation", this.getReturnLocation());
        }

        return UrlFactory.parameterizeUrl("maintenance.do", parameters);
    }
}
