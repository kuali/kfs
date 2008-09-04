/*
 * Copyright 2008 The Kuali Foundation.
 *
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl1.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.cab.businessobject.lookup;

import java.util.ArrayList;
import java.util.List;

import org.kuali.kfs.module.cab.businessobject.GeneralLedgerEntry;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.util.KNSConstants;

/**
 * This class overrids the base getActionUrls method
 */
public class GeneralLedgerEntryLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(GeneralLedgerEntryLookupableHelperServiceImpl.class);

    /***
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        GeneralLedgerEntry entry = (GeneralLedgerEntry) bo;
        String href = "../cabGlLine.do?methodToCall=start&generalLedgerAccountIdentifier=" + entry.getGeneralLedgerAccountIdentifier();
        List<HtmlData> anchorHtmlDataList = new ArrayList<HtmlData>();
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, KNSConstants.START_METHOD, "Assets");
        anchorHtmlDataList.add(anchorHtmlData);
        return anchorHtmlDataList;
    }

}
