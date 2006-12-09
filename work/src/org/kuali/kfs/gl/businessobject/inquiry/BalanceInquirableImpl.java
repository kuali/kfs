/*
 * Copyright 2006 The Kuali Foundation.
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
package org.kuali.module.gl.web.inquirable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.LookupService;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.web.Constant;

/**
 * This class is used to generate the URL for the user-defined attributes for the GL balace screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 * 
 * 
 */
public class BalanceInquirableImpl extends AbstractGLInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquirableImpl.class);

    private BusinessObjectDictionaryService dataDictionary;
    private LookupService lookupService;
    private Class businessObjectClass;

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();

        keys.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keys.add(PropertyConstants.ACCOUNT_NUMBER);
        keys.add(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(PropertyConstants.BALANCE_TYPE_CODE);
        keys.add(PropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(PropertyConstants.OBJECT_CODE);
        keys.add(PropertyConstants.SUB_OBJECT_CODE);
        keys.add(PropertyConstants.OBJECT_TYPE_CODE);
        keys.add(Constant.PENDING_ENTRY_OPTION);

        return keys;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();

        userDefinedAttributeMap.put(PropertyConstants.MONTH1_AMOUNT, Constants.MONTH1);
        userDefinedAttributeMap.put(PropertyConstants.MONTH2_AMOUNT, Constants.MONTH2);
        userDefinedAttributeMap.put(PropertyConstants.MONTH3_AMOUNT, Constants.MONTH3);

        userDefinedAttributeMap.put(PropertyConstants.MONTH4_AMOUNT, Constants.MONTH4);
        userDefinedAttributeMap.put(PropertyConstants.MONTH5_AMOUNT, Constants.MONTH5);
        userDefinedAttributeMap.put(PropertyConstants.MONTH6_AMOUNT, Constants.MONTH6);

        userDefinedAttributeMap.put(PropertyConstants.MONTH7_AMOUNT, Constants.MONTH7);
        userDefinedAttributeMap.put(PropertyConstants.MONTH8_AMOUNT, Constants.MONTH8);
        userDefinedAttributeMap.put(PropertyConstants.MONTH9_AMOUNT, Constants.MONTH9);

        userDefinedAttributeMap.put(PropertyConstants.MONTH10_AMOUNT, Constants.MONTH10);
        userDefinedAttributeMap.put(PropertyConstants.MONTH11_AMOUNT, Constants.MONTH11);
        userDefinedAttributeMap.put(PropertyConstants.MONTH12_AMOUNT, Constants.MONTH12);
        userDefinedAttributeMap.put(PropertyConstants.MONTH13_AMOUNT, Constants.MONTH13);

        userDefinedAttributeMap.put(PropertyConstants.BEGINNING_BALANCE_LINE_AMOUNT, Constants.BEGINNING_BALANCE);
        userDefinedAttributeMap.put(PropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT, Constants.CG_BEGINNING_BALANCE);

        return userDefinedAttributeMap;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        return attributeName;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        if (isExclusiveField(keyName, keyValue)) {
            keyValue = Constant.EMPTY_STRING;
        }
        return keyValue;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        keyName = BusinessObjectFieldConverter.convertToTransactionPropertyName(keyName);
        return keyName;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return Constant.GL_LOOKUPABLE_ENTRY;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return Constants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return Entry.class;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());

        String periodCode = (String) getUserDefinedAttributeMap().get(attributeName);
        parameter.put(Constants.UNIVERSITY_FISCAL_PERIOD_CODE_PROPERTY_NAME, periodCode);
    }
}