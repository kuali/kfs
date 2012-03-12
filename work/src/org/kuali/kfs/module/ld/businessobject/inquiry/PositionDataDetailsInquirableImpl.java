/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.module.ld.businessobject.inquiry;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.businessobject.PositionData;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This class is used to generate the URL for the user-defined attributes for the Position Inquiry screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class PositionDataDetailsInquirableImpl extends AbstractLaborInquirableImpl {

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#addMoreParameters(java.util.Properties,
     *      java.lang.String)
     */
    @Override
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();
        keys.add(KFSPropertyConstants.POSITION_NUMBER);
        keys.add(LaborPropertyConstants.EFFECTIVE_DATE);
        return keys;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        return KFSPropertyConstants.POSITION_NUMBER;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return KFSConstants.INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return PositionData.class;
    }

    /**
     * Returns the position value with the greatest effective date
     * 
     * @see org.kuali.rice.kns.inquiry.Inquirable#getBusinessObject(java.util.Map)
     */
    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        List<PositionData> positionList = new ArrayList<PositionData>(getLookupService().findCollectionBySearch(PositionData.class, fieldValues));
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");

        Date today = Calendar.getInstance().getTime();
        Date maxEffectiveDate = null;

        PositionData lookupValue = null;
        for (PositionData position : positionList) {
            Date effectiveDate = position.getEffectiveDate();
            if (effectiveDate.compareTo(today) <= 0 && (maxEffectiveDate == null || effectiveDate.compareTo(maxEffectiveDate) > 0)) {
                maxEffectiveDate = effectiveDate;
                lookupValue = position;
            }
        }

        return lookupValue;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        return keyValue;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return null;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();
        userDefinedAttributeMap.put(KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.POSITION_NUMBER);

        return userDefinedAttributeMap;
    }
}
