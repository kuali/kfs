/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
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
 * @author Bin Gao from Michigan State University
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

        userDefinedAttributeMap.put(PropertyConstants.MONTH1_AMOUNT, Constant.MONTH1);
        userDefinedAttributeMap.put(PropertyConstants.MONTH2_AMOUNT, Constant.MONTH2);
        userDefinedAttributeMap.put(PropertyConstants.MONTH3_AMOUNT, Constant.MONTH3);

        userDefinedAttributeMap.put(PropertyConstants.MONTH4_AMOUNT, Constant.MONTH4);
        userDefinedAttributeMap.put(PropertyConstants.MONTH5_AMOUNT, Constant.MONTH5);
        userDefinedAttributeMap.put(PropertyConstants.MONTH6_AMOUNT, Constant.MONTH6);

        userDefinedAttributeMap.put(PropertyConstants.MONTH7_AMOUNT, Constant.MONTH7);
        userDefinedAttributeMap.put(PropertyConstants.MONTH8_AMOUNT, Constant.MONTH8);
        userDefinedAttributeMap.put(PropertyConstants.MONTH9_AMOUNT, Constant.MONTH9);

        userDefinedAttributeMap.put(PropertyConstants.MONTH10_AMOUNT, Constant.MONTH10);
        userDefinedAttributeMap.put(PropertyConstants.MONTH11_AMOUNT, Constant.MONTH11);
        userDefinedAttributeMap.put(PropertyConstants.MONTH12_AMOUNT, Constant.MONTH12);
        userDefinedAttributeMap.put(PropertyConstants.MONTH13_AMOUNT, Constant.MONTH13);
        
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
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getInquiryBusinessObjectClass()
     */
    protected Class getInquiryBusinessObjectClass() {
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