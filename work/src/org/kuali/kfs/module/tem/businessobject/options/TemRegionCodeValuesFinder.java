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
import org.kuali.rice.krad.service.KeyValuesService;

public class TemRegionCodeValuesFinder extends KeyValuesBase {
    protected static volatile KeyValuesService keyValuesService;

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<KeyValue> getKeyValues() {

        String tripTypeCode = KFSConstants.EMPTY_STRING;



        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue(KFSConstants.EMPTY_STRING, KFSConstants.EMPTY_STRING));
        TravelService travelService = SpringContext.getBean(TravelService.class);

        List<TemRegion> usRegions = new ArrayList<TemRegion>();


        Map<String, Object> fieldValues = new HashMap<String, Object>();
        if (StringUtils.isEmpty(tripTypeCode) || tripTypeCode.equals(TemConstants.TemTripTypes.IN_STATE)) {
            fieldValues.put(TemPropertyConstants.TRIP_TYPE_CODE, TemConstants.TemTripTypes.IN_STATE);
            List<TemRegion> inRegions = (List<TemRegion>) getKeyValuesService().findMatching(TemRegion.class, fieldValues);
            usRegions.addAll(inRegions);
        }

        if (StringUtils.isEmpty(tripTypeCode) || tripTypeCode.equals(TemConstants.TemTripTypes.OUT_OF_STATE)) {
            fieldValues.put(TemPropertyConstants.TRIP_TYPE_CODE, TemConstants.TemTripTypes.OUT_OF_STATE);
            List<TemRegion> outRegions = (List<TemRegion>) getKeyValuesService().findMatching(TemRegion.class, fieldValues);
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
            List<TemRegion> intRegions = (List<TemRegion>)getKeyValuesService().findMatching(TemRegion.class, fieldValues);
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

    protected KeyValuesService getKeyValuesService() {
        if (keyValuesService == null) {
            keyValuesService = SpringContext.getBean(KeyValuesService.class);
        }
        return keyValuesService;
    }
}
