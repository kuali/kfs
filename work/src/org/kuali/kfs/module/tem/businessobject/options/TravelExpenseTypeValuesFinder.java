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
import java.util.List;

import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.businessobject.TemTravelExpenseTypeCode;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.struts.TravelFormBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.util.ConcreteKeyValue;
import org.kuali.rice.core.api.util.KeyValue;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.krad.keyvalues.KeyValuesBase;
import org.kuali.rice.krad.service.KeyValuesService;

@SuppressWarnings("deprecation")
public class TravelExpenseTypeValuesFinder extends KeyValuesBase {

    private String groupTravelCount;
    private String tripType;
    private String travelerType;
    private String documentType;

    /**
     * @see org.kuali.rice.krad.keyvalues.KeyValuesFinder#getKeyValues()
     */
    @Override
    public List<KeyValue> getKeyValues() {
        List<TemTravelExpenseTypeCode> boList = (List<TemTravelExpenseTypeCode>) SpringContext.getBean(KeyValuesService.class).findAllOrderBy(TemTravelExpenseTypeCode.class, TemPropertyConstants.NAME, true);
        List<KeyValue> keyValues = new ArrayList<KeyValue>();
        keyValues.add(new ConcreteKeyValue("", ""));

        // defaults
        if (groupTravelCount == null) {
            groupTravelCount = "0";
        }

        if (tripType == null) {
            tripType = TemConstants.BLANKET_IN_STATE;
        }

        if (travelerType == null) {
            travelerType = TemConstants.EMP_TRAVELER_TYP_CD;
        }

        if (documentType == null) {
            TravelFormBase form = (TravelFormBase) KNSGlobalVariables.getKualiForm();
            TravelDocument travelDocument = form != null ? form.getTravelDocument() : null;

            String documentType = SpringContext.getBean(TravelDocumentService.class).getDocumentType(travelDocument);
        }

        if (documentType != null && (documentType.equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT) || documentType.equals(TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT))) {
            documentType = TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT;
        }

        for (TemTravelExpenseTypeCode temTravelExpenseTypeCode : boList) {
            if (temTravelExpenseTypeCode.isActive()) {
                if (temTravelExpenseTypeCode.getIndividual() || (temTravelExpenseTypeCode.getGroupTravel() && groupTravelCount != null && Integer.parseInt(groupTravelCount) > 0)) {
                    if (temTravelExpenseTypeCode.getTripType().equals(tripType) && temTravelExpenseTypeCode.getTravelerType().equals(travelerType) && temTravelExpenseTypeCode.getDocumentType().equals(documentType)) {
                        keyValues.add(new ConcreteKeyValue(temTravelExpenseTypeCode.getCode(), temTravelExpenseTypeCode.getCodeAndDescription()));
                    }
                }
            }
        }

        return keyValues;
    }

    public String getGroupTravelCount() {
        return groupTravelCount;
    }

    public void setGroupTravelCount(String groupTravelCount) {
        this.groupTravelCount = groupTravelCount;
    }

    public String getTripType() {
        return tripType;
    }

    public void setTripType(String tripType) {
        this.tripType = tripType;
    }

    public String getTravelerType() {
        return travelerType;
    }

    public void setTravelerType(String travelerType) {
        this.travelerType = travelerType;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }

}
