/*
 * Copyright 2010 The Kuali Foundation.
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

import static org.kuali.kfs.module.tem.util.BufferedLogger.error;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.MileageRateObjCode;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.util.KeyLabelPair;
import org.kuali.rice.kns.lookup.keyvalues.KeyValuesBase;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.ObjectUtils;

public class MileageRateValuesFinder extends KeyValuesBase {
    private String queryDate;

    public List getKeyValues() {
        Date searchDate = null;
        final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        if(StringUtils.isNotEmpty(queryDate)) {
            try {
                searchDate = df.parse(queryDate);
            } catch (ParseException ex) {
                error("unable to parse date: " + queryDate);
            }
        }
        final List keyValues = new ArrayList();
        TravelDocument document = (TravelDocument) ((TravelFormBase)GlobalVariables.getKualiForm()).getDocument();
        Map<String,Object> fieldValues = new HashMap<String,Object>();
        
        if(document.getTripTypeCode() != null){
            fieldValues.put(TemPropertyConstants.TravelAuthorizationFields.TRIP_TYPE, document.getTripTypeCode());
        }

        String documentType = SpringContext.getBean(TravelDocumentService.class).getDocumentType(document);        
        
        if (documentType != null) {
            fieldValues.put("documentType", documentType);
        }
        
        fieldValues.put(TemPropertyConstants.TRVL_DOC_TRAVELER_TYP_CD, document.getTraveler().getTravelerTypeCode());
        fieldValues.put("active", "Y");
               
        final Collection<MileageRateObjCode> bos = SpringContext.getBean(BusinessObjectService.class).findMatching(MileageRateObjCode.class,fieldValues);
               
        for (final MileageRateObjCode typ : bos) {
            typ.refreshNonUpdateableReferences();
            final Date fromDate = typ.getMileageRate().getActiveFromDate();
            final Date toDate = typ.getMileageRate().getActiveToDate();
            if(ObjectUtils.isNull(searchDate)) {
                //just add them all
                keyValues.add(new KeyLabelPair(typ.getId(), typ.getMileageRate().getName()));
            } else if((fromDate.equals(searchDate) || fromDate.before(searchDate)) && (toDate.equals(searchDate) || toDate.after(searchDate))) {
                if (typ.getMileageRate() != null && typ.getMileageRate().isActive()) {
                    keyValues.add(new KeyLabelPair(typ.getMileageRateId(), typ.getMileageRate().getCodeAndRate())); 
                } 
            }            
        } 
        
        //sort by label
        Comparator<KeyLabelPair> labelComparator = new Comparator<KeyLabelPair>() {
            @Override
            public int compare(KeyLabelPair o1, KeyLabelPair o2) {
                try{
                    return o1.getLabel().compareTo(o2.getLabel());
                }catch (NullPointerException e){
                    return -1;
                }
            }
        };

        Collections.sort(keyValues, labelComparator);

        return keyValues;
    }

    /**
     * Gets the queryDate attribute. 
     * @return Returns the queryDate.
     */
    public String getQueryDate() {
        return queryDate;
    }

    /**
     * Sets the queryDate attribute value.
     * @param queryDate The queryDate to set.
     */
    public void setQueryDate(String queryDate) {
        this.queryDate = queryDate;
    }
    
}
