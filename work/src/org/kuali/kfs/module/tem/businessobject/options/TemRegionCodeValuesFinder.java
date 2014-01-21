/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.businessobject.options;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TemRegion;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.BusinessObjectService;

public class TemRegionCodeValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<KeyValue> getKeyValues() {

        String tripTypeCode = KFSConstants.EMPTY_STRING;



        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        TravelService travelService = SpringContext.getBean(TravelService.class);

        List<TemRegion> usRegions = new ArrayList<TemRegion>();


        Map<String, String> fieldValues = new HashMap<String, String>();
        if (StringUtils.isEmpty(tripTypeCode) || tripTypeCode.equals(TemConstants.TemTripTypes.IN_STATE)) {
            fieldValues.put(TemPropertyConstants.TRIP_TYPE_CODE, TemConstants.TemTripTypes.IN_STATE);
            List<TemRegion> inRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
            usRegions.addAll(inRegions);
        }

        if (StringUtils.isEmpty(tripTypeCode) || tripTypeCode.equals(TemConstants.TemTripTypes.OUT_OF_STATE)) {
            fieldValues.put(TemPropertyConstants.TRIP_TYPE_CODE, TemConstants.TemTripTypes.OUT_OF_STATE);
            List<TemRegion> outRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
            Collections.sort(outRegions);
            usRegions.addAll(outRegions);

        }



        Iterator<TemRegion> it = usRegions.iterator();

        String key = KFSConstants.EMPTY_STRING;
        while (it.hasNext()){
            TemRegion temRegion = it.next();

            String tempKey = temRegion.getRegionName();
            if (!tempKey.equals(key)){
                keyValues.add(new ConcreteKeyValue(temRegion.getRegionCode().toUpperCase(), temRegion.getRegionCode().toUpperCase()));
            }
            key = tempKey;
        }


        if (StringUtils.isEmpty(tripTypeCode) || tripTypeCode.equals(TemConstants.TemTripTypes.INTERNATIONAL)) {

            fieldValues.put(TemPropertyConstants.TRIP_TYPE_CODE, TemConstants.TemTripTypes.INTERNATIONAL);
            List<TemRegion> intRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
            Collections.sort(intRegions);


            keyValues.add(new ConcreteKeyValue("---", "---"));
            it = intRegions.iterator();
            while (it.hasNext()) {
                TemRegion temRegion = it.next();
                String tempKey = temRegion.getRegionName();
                if (!tempKey.equals(key)) {
                    keyValues.add(new ConcreteKeyValue(temRegion.getRegionCode().toUpperCase(), temRegion.getRegionCode().toUpperCase()));
                }
                key = tempKey;
            }
        }

        return keyValues;
    }
}
