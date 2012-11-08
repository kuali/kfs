/*
 * Copyright 2011 The Kuali Foundation.
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
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.UrlFactory;

@SuppressWarnings("rawtypes")
public class CreditCardStagingDataLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {
    
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
        
        //SW: will never able to edit, copy as thisi s not maintainable
//        // For matched records or if user is not travel manager edit and delete link will not be displayed .
//        if (stagingData.getMoveToHistoryIndicator()||!isTravelManager) {
//            // clear 'edit' and delete links
//            //anchorHtmlDataList.clear();
//        }
        
        //CLEANUP - need to be able to generate view link (or search result filter) base on accessibility
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
