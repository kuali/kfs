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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.CreditCardStagingData;
import org.kuali.kfs.module.tem.businessobject.HistoricalTravelExpense;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.UrlFactory;

@SuppressWarnings({ "rawtypes", "deprecation" })
public class CreditCardStagingDataLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        if (backLocation.contains("maintenance.do")){   //coming from clearing maint doc, not from TEM trans doc
            List<CreditCardStagingData> cardStagingDataList =  (List<CreditCardStagingData>) super.getSearchResultsHelper(fieldValues, true);
            List<CreditCardStagingData> newCardStagingDataList = new ArrayList<CreditCardStagingData>();

            // look through
            for (CreditCardStagingData cardStagingData : cardStagingDataList){
                Map<String, Object> hteFieldValues = new HashMap<String, Object>();
                hteFieldValues.put("creditCardStagingDataId", cardStagingData.getId());
                hteFieldValues.put("reconciled", TemConstants.ReconciledCodes.UNRECONCILED);
                List<HistoricalTravelExpense> expenseList = (List<HistoricalTravelExpense>) SpringContext.getBean(BusinessObjectService.class).findMatching(HistoricalTravelExpense.class, hteFieldValues);

                if (expenseList.size() > 0){
                    newCardStagingDataList.add(cardStagingData);
                }
            }
            CollectionIncomplete collection = null;
            Integer limit = LookupUtils.getSearchResultsLimit(CreditCardStagingData.class);
            if (newCardStagingDataList.size() > limit.intValue()){
                collection = new CollectionIncomplete(newCardStagingDataList.subList(0, limit), (long) newCardStagingDataList.size());
            }
            else{
                collection = new CollectionIncomplete(newCardStagingDataList, (long) 0);
            }
            return collection;
        }
        else{
            return super.getSearchResults(fieldValues);
        }
    }


    /**
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getCustomActionUrls(org.kuali.rice.kns.bo.BusinessObject, java.util.List)
     */
    @Override
    public List<HtmlData> getCustomActionUrls(BusinessObject bo, List pkNames) {
        List<HtmlData> anchorHtmlDataList = super.getCustomActionUrls(bo, pkNames);

        CreditCardStagingData stagingData = (CreditCardStagingData) bo;
        boolean isTravelManager = isUserTravelManager();

        if(true){
            anchorHtmlDataList.add(getViewUrl(stagingData));
        }
        return anchorHtmlDataList;
    }

    /**
     *
     * @return
     */
    private boolean isUserTravelManager() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        boolean isTravelManager= SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser);
        return isTravelManager;
    }

    /**
     * Create a view url for credit card staging data
     *
     * @param stagingData
     * @return
     */
    private HtmlData getViewUrl(CreditCardStagingData stagingData) {
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
        parameters.put("id", stagingData.getId().toString());
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, CreditCardStagingData.class.getName());

        String href = UrlFactory.parameterizeUrl(TemConstants.INQUIRY_URL, parameters);
        AnchorHtmlData anchorHtmlData = new AnchorHtmlData(href, KFSConstants.START_METHOD, TemConstants.VIEW);
        anchorHtmlData.setTarget("blank");
        return anchorHtmlData;
    }


}
