/*
 * Copyright 2012 The Kuali Foundation
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

import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.tem.businessobject.PrimaryDestination;
import org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.lookup.CollectionIncomplete;


@SuppressWarnings("deprecation")
public class PrimaryDestinationLookupableHelperServiceImpl extends KualiLookupableHelperServiceImpl {

    /**
     * @see org.kuali.rice.kns.lookup.KualiLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        fieldValues.put("active", "true");
        List<PrimaryDestination> results = (List<PrimaryDestination>) super.getSearchResultsHelper(fieldValues, true);
       // List<PrimaryDestination> newResults = new ArrayList<PrimaryDestination>();

//        if (results.size() == 0){
//            if (fieldValues.get(TemPropertyConstants.PER_DIEM_COUNTRY_CODE).toString().length() == 2){
//                Map<String, String> tempFieldValues = new HashMap<String, String>();
//                BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
//                tempFieldValues.put(TemPropertyConstants.PER_DIEM_COUNTRY_CODE, KFSConstants.COUNTRY_CODE_UNITED_STATES);
//                newResults = (List<PrimaryDestination>) service.findMatching(PrimaryDestination.class, tempFieldValues);
//            }
//            else{
//                TravelService service = SpringContext.getBean(TravelService.class);
//                newResults = service.findDefaultPrimaryDestinations(PrimaryDestination.class, fieldValues.get(TemPropertyConstants.PER_DIEM_COUNTRY_CODE));
//            }
//        }
//        else{
  //          Iterator<PrimaryDestination> it = results.iterator();

        //    String key = "";
//            while (it.hasNext()){
//                PrimaryDestination tempPrimaryDestination = it.next();
//                //skip dummy value for custom expenses
//                if (tempPrimaryDestination.getId().intValue() == TemConstants.CUSTOM_PER_DIEM_ID
//                        || tempPrimaryDestination.getCountryStateName().equals(TemConstants.CONUS)
//                        || tempPrimaryDestination.getCountryState().equals(TemConstants.ALL_STATES)
//                        || tempPrimaryDestination.getPrimaryDestinationName().contains(TemConstants.OTHER_PRIMARY_DESTINATION)){
//                    continue;
//                }
//                String tempKey = tempPrimaryDestination.getTripTypeCode()+tempPrimaryDestination.getPrimaryDestinationName()+tempPrimaryDestination.getCountryState()+tempPrimaryDestination.getCounty();
//                if (!tempKey.equals(key)){
//                    newResults.add(tempPrimaryDestination);
//                }
//                key = tempKey;
//            }
      //  }

        CollectionIncomplete collection = null;
        Integer limit = LookupUtils.getSearchResultsLimit(PrimaryDestination.class);
        if (results.size() > limit.intValue()){
            collection = new CollectionIncomplete(results.subList(0, limit), (long) results.size());
        }
        else{
            collection = new CollectionIncomplete(results, (long) 0);
        }

        return collection;


    }

}
