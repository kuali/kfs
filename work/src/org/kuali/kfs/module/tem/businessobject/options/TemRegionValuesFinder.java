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
import org.kuali.kfs.module.tem.businessobject.TemRegion;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DocumentService;

public class TemRegionValuesFinder extends KeyValuesBase {

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @SuppressWarnings("rawtypes")
    @Override
    public List<KeyValue> getKeyValues() {

        KualiForm form = KNSGlobalVariables.getKualiForm();
        String tripTypeCode = "";

        if (form instanceof LookupForm) {
            LookupForm lookupForm = (LookupForm)form;
            final String docNum = lookupForm.getDocNum();
            if(!StringUtils.isBlank(docNum)) {
               TravelDocument document = null;
               try {
                   document = (TravelDocument)SpringContext.getBean(DocumentService.class).getByDocumentHeaderIdSessionless(docNum);
               } catch (WorkflowException ex) {
                // TODO Auto-generated catch block
                ex.printStackTrace();
               }
        //       tripTypeCode = document.getTripTypeCode();
            }
        }
        if (form instanceof TravelFormBase) {

            TravelDocument document = ((TravelFormBase)form).getTravelDocument();
            tripTypeCode = document.getTripTypeCode();
        }



        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("", ""));
        BusinessObjectService service = SpringContext.getBean(BusinessObjectService.class);
        TravelService travelService = SpringContext.getBean(TravelService.class);

        List<TemRegion> usRegions = new ArrayList<TemRegion>();


        Map<String, String> fieldValues = new HashMap<String, String>();
        if (StringUtils.isEmpty(tripTypeCode) || tripTypeCode.equals(TemConstants.TEMTripTypes.IN_STATE)) {
            fieldValues.put("tripTypeCode", TemConstants.TEMTripTypes.IN_STATE);
            List<TemRegion> inRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
            usRegions.addAll(inRegions);
        }
        if (StringUtils.isEmpty(tripTypeCode) || tripTypeCode.equals(TemConstants.TEMTripTypes.BLANKET_IN_STATE)) {
            fieldValues.put("tripTypeCode", TemConstants.TEMTripTypes.BLANKET_IN_STATE);
            List<TemRegion> blnRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
            usRegions.addAll(blnRegions);

        }
        if (StringUtils.isEmpty(tripTypeCode) || tripTypeCode.equals(TemConstants.TEMTripTypes.OUT_OF_STATE)) {
            fieldValues.put("tripTypeCode", TemConstants.TEMTripTypes.OUT_OF_STATE);
            List<TemRegion> outRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
            Collections.sort(outRegions);
            usRegions.addAll(outRegions);

        }



        Iterator<TemRegion> it = usRegions.iterator();

        String key = "";
        while (it.hasNext()){
            TemRegion temRegion = it.next();

            String tempKey = temRegion.getRegionName();
            if (!tempKey.equals(key)){
                keyValues.add(new ConcreteKeyValue(temRegion.getRegionCode().toUpperCase(), temRegion.getRegionName().toUpperCase()));
            }
            key = tempKey;
        }


        if (StringUtils.isEmpty(tripTypeCode) || tripTypeCode.equals(TemConstants.TEMTripTypes.INTERNATIONAL)) {

            fieldValues.put("tripTypeCode", TemConstants.TEMTripTypes.INTERNATIONAL);
            List<TemRegion> intRegions = (List<TemRegion>) service.findMatching(TemRegion.class, fieldValues);
            Collections.sort(intRegions);


            keyValues.add(new ConcreteKeyValue("---", "------------------------------------------"));
            it = intRegions.iterator();
            while (it.hasNext()) {
                TemRegion temRegion = it.next();
                String tempKey = temRegion.getRegionName();
                if (!tempKey.equals(key)) {
                    keyValues.add(new ConcreteKeyValue(temRegion.getRegionCode().toUpperCase(), temRegion.getRegionName().toUpperCase()));
                }
                key = tempKey;
            }
        }

        return keyValues;
    }
}
