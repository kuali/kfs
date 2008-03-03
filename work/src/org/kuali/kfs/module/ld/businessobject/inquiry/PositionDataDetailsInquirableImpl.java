/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.inquirable;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.core.bo.BusinessObject;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.labor.LaborPropertyConstants;
import org.kuali.module.labor.bo.PositionData;

/**
 * This class is used to generate the URL for the user-defined attributes for the Position Inquiry screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class PositionDataDetailsInquirableImpl extends AbstractLaborInquirableImpl {

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#addMoreParameters(java.util.Properties,
     *      java.lang.String)
     */
    @Override
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();
        keys.add(KFSPropertyConstants.POSITION_NUMBER);
        keys.add(LaborPropertyConstants.EFFECTIVE_DATE);
        return keys;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        return KFSPropertyConstants.POSITION_NUMBER;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return KFSConstants.INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return PositionData.class;
    }

    /**
     * Returns the position value with the greatest effective date
     * 
     * @see org.kuali.core.inquiry.Inquirable#getBusinessObject(java.util.Map)
     */
    @Override
    public BusinessObject getBusinessObject(Map fieldValues) {
        List<PositionData> positionList = new ArrayList<PositionData>(getLookupService().findCollectionBySearch(getBusinessObjectClass(), fieldValues));
        SimpleDateFormat formatter = new SimpleDateFormat("MMddyyyy");
        Date today = formatter.getCalendar().getInstance().getTime();
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
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        return keyValue;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return null;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();
        userDefinedAttributeMap.put(KFSPropertyConstants.POSITION_NUMBER, KFSPropertyConstants.POSITION_NUMBER);

        return userDefinedAttributeMap;
    }
}