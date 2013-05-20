/*
 * Copyright 2008 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cg.businessobject.lookup;

import java.util.List;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;

/**
 * Helper service class for Agency lookup
 */
public class AgencyLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AgencyLookupableHelperServiceImpl.class);

    /***
     * This method was overridden to add custom action components in Agency lookup
     * 
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) {
        List<HtmlData> htmlDataList = super.getCustomActionUrls(businessObject, pkNames);

        if (!CollectionUtils.isEmpty(htmlDataList)) {
            htmlDataList.add(getAgencyAwardLookupUrl(businessObject));
        }

        return htmlDataList;
    }


    /**
     * This method adds a link to the look up FOR the awards associated with a given Agency.
     * 
     * @param bo
     * @return
     */
    private AnchorHtmlData getAgencyAwardLookupUrl(BusinessObject bo) {
        Agency agency = (Agency) bo;

        String href = "../" + KFSConstants.LOOKUP_ACTION + "?businessObjectClassName=org.kuali.kfs.module.cg.businessobject.Award" + "&methodToCall=search" + "&" + CGPropertyConstants.AgencyFields.AGENCY_NUMBER + "=" + agency.getAgencyNumber();

        return new AnchorHtmlData(href, KFSConstants.SEARCH_METHOD, "Awards");
    }
}
