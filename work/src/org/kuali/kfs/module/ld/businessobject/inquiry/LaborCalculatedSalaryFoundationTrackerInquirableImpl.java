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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.LookupService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.LaborConstants;
import org.kuali.module.labor.bo.July1PositionFunding;
import org.kuali.module.labor.bo.LedgerBalance;

/**
 * This class is used to generate the URL for the user-defined attributes for the Current Funds screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class LaborCalculatedSalaryFoundationTrackerInquirableImpl extends AbstractLaborInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCalculatedSalaryFoundationTrackerInquirableImpl.class);

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List<String> keys = new ArrayList<String>();

        keys.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keys.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        keys.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        keys.add(KFSPropertyConstants.POSITION_NUMBER);
        keys.add(KFSPropertyConstants.EMPLID);
        
        return keys;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGLInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
               
        Map userDefinedAttributeMap = new HashMap();          
        //userDefinedAttributeMap.put(KFSPropertyConstants.JULY1_BUDGET_AMOUNT, "");          
        return userDefinedAttributeMap;
    }
    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGLInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        return attributeName;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGLInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        if (isExclusiveField(keyName, keyValue)) {
            keyValue = Constant.EMPTY_STRING;
        }
        return keyValue;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGLInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGLInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return "CSFTrackerBalanceLookupable";
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGLInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return KFSConstants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGLInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return July1PositionFunding.class;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGLInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {     
    }
}