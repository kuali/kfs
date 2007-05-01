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
import org.kuali.module.labor.bo.LedgerEntry;

/**
 * This class is used to generate the URL for the user-defined attributes for the GL balace screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 * 
 * 
 */
public class LedgerBalanceInquirableImpl extends AbstractLaborInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LedgerBalanceInquirableImpl.class);

    private BusinessObjectDictionaryService dataDictionary;
    private LookupService lookupService;
    private Class businessObjectClass;

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();

        keys.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keys.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(KFSPropertyConstants.BALANCE_TYPE_CODE);
        keys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.OBJECT_CODE);
        keys.add(KFSPropertyConstants.SUB_OBJECT_CODE);
        keys.add(KFSPropertyConstants.OBJECT_TYPE_CODE);
        keys.add(KFSPropertyConstants.EMPLID);
        keys.add(KFSPropertyConstants.POSITION_NUMBER);

        return keys;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();

        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH1_AMOUNT, KFSConstants.MONTH1);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH2_AMOUNT, KFSConstants.MONTH2);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH3_AMOUNT, KFSConstants.MONTH3);

        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH4_AMOUNT, KFSConstants.MONTH4);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH5_AMOUNT, KFSConstants.MONTH5);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH6_AMOUNT, KFSConstants.MONTH6);

        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH7_AMOUNT, KFSConstants.MONTH7);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH8_AMOUNT, KFSConstants.MONTH8);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH9_AMOUNT, KFSConstants.MONTH9);

        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH10_AMOUNT, KFSConstants.MONTH10);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH11_AMOUNT, KFSConstants.MONTH11);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH12_AMOUNT, KFSConstants.MONTH12);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH13_AMOUNT, KFSConstants.MONTH13);

        userDefinedAttributeMap.put(KFSPropertyConstants.BEGINNING_BALANCE_LINE_AMOUNT, KFSConstants.BEGINNING_BALANCE);
        userDefinedAttributeMap.put(KFSPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT, KFSConstants.CG_BEGINNING_BALANCE);

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
        // TODO: investigate change to this constant
        return Constant.GL_LOOKUPABLE_ENTRY;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        // TODO: investigate change to this constant
        return KFSConstants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return LedgerEntry.class;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());

        String periodCode = (String) getUserDefinedAttributeMap().get(attributeName);
        parameter.put(KFSConstants.UNIVERSITY_FISCAL_PERIOD_CODE_PROPERTY_NAME, periodCode);
    }
}