/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.endow.businessobject.lookup;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.krad.bo.BusinessObject;

public class HoldingTaxLotRebalanceLookupableHelperService extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) 
    {
        return super.getSearchResults(fieldValues);
    }
    
    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.krad.bo.BusinessObject,
     *      java.util.List)
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject businessObject, List pkNames) 
    {
        // Change "Actions" column to show "Rebalance" as a link to the maintenance document.
        List<HtmlData> anchorHtmlDataList = super.getCustomActionUrls(businessObject, pkNames);
        for (HtmlData htmlData : anchorHtmlDataList) {
            htmlData.setDisplayText(EndowConstants.HoldingTaxLotRebalanceCodes.RESULTS_ACTIONS_LINK);
        }
        
        return anchorHtmlDataList;
    }

    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#allowsMaintenanceNewOrCopyAction()
     */
    @Override
    public boolean allowsMaintenanceNewOrCopyAction() {

        return false;
    }

}
