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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.PerDiem;
import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.lookup.CollectionIncomplete;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupResultsService;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.service.LookupService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.service.PersistenceStructureService;
import org.kuali.rice.kns.service.SequenceAccessorService;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Column;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;


public class PerDiemLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        List<PerDiem> results = (List<PerDiem>) super.getSearchResultsHelper(fieldValues, true);
        List<PerDiem> newResults = new ArrayList<PerDiem>();
        
        if (results.size() == 0){
            
            
            if (fieldValues.get(TemPropertyConstants.PER_DIEM_COUNTRY_STATE).toString().length() == 2){
                Map<String, String> tempFieldValues = new HashMap<String, String>();
                BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
                tempFieldValues.put(TemPropertyConstants.PER_DIEM_COUNTRY_STATE, KFSConstants.COUNTRY_CODE_UNITED_STATES);
                newResults = (List<PerDiem>) service.findMatching(PerDiem.class, tempFieldValues);
            }
            else{
                TravelService service = SpringContext.getBean(TravelService.class);
                newResults = (List<PerDiem>) service.findDefaultPrimaryDestinations(PerDiem.class, fieldValues.get(TemPropertyConstants.PER_DIEM_COUNTRY_STATE));
            }
            
        }
        else{
            Iterator<PerDiem> it = results.iterator();

            while (it.hasNext()){
                PerDiem perDiem = it.next();
                if (perDiem.getId().intValue() == TemConstants.CUSTOM_PER_DIEM_ID
                        || perDiem.getCountryStateName().equals(TemConstants.CONUS)
                        || perDiem.getCountryState().equals(TemConstants.ALL_STATES)
                        || perDiem.getPrimaryDestination().contains(TemConstants.OTHER_PRIMARY_DESTINATION)){
                    continue;
                }
                newResults.add(perDiem);
            }
        }
        
        CollectionIncomplete collection = null;
        Integer limit = LookupUtils.getSearchResultsLimit(PerDiem.class);
        if (newResults.size() > limit.intValue()){
            collection = new CollectionIncomplete(newResults.subList(0, limit), (long) newResults.size());
        }
        else{
            collection = new CollectionIncomplete(newResults, (long) 0);
        }
        return collection;

        
    }
}
