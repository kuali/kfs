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
import org.kuali.module.gl.GLConstants;
import org.kuali.module.gl.bo.AccountBalance;
import org.kuali.module.gl.bo.AccountBalanceByLevel;
import org.kuali.module.gl.bo.AccountBalanceByObject;
import org.kuali.module.gl.web.Constant;

/**
 * This class is used to generate the URL for the user-defined attributes for the account balace by level screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 * 
 * 
 */
public class AccountBalanceByLevelInquirableImpl extends AbstractGLInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceByLevelInquirableImpl.class);

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
        keys.add(PropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(GLConstants.BalanceInquiryDrillDowns.OBJECT_LEVEL_CODE);
        keys.add(GLConstants.BalanceInquiryDrillDowns.REPORTING_SORT_CODE);
        keys.add(Constant.COST_SHARE_OPTION);
        keys.add(Constant.CONSOLIDATION_OPTION);
        keys.add(Constant.PENDING_ENTRY_OPTION);

        return keys;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();
        userDefinedAttributeMap.put(GLConstants.DummyBusinessObject.LINK_BUTTON_OPTION, "");
        return userDefinedAttributeMap;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        if (attributeName.equals(GLConstants.DummyBusinessObject.LINK_BUTTON_OPTION)) {
            attributeName = PropertyConstants.OBJECT_CODE;
        }
        return attributeName;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        if (isExclusiveField(keyName, keyValue)) {
            keyValue = "";
        }
        return keyValue;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return Constant.GL_LOOKUPABLE_ACCOUNT_BALANCE_BY_OBJECT;
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
        Class c = null;
        /*
         * if("financialObject.financialObjectLevel.financialConsolidationObjectCode".equals(attributeName)) { c =
         * AccountBalanceByConsolidation.class; } else
         */if (GLConstants.BalanceInquiryDrillDowns.OBJECT_LEVEL_CODE.equals(attributeName)) {
            c = AccountBalance.class;
        }
        else if (PropertyConstants.OBJECT_CODE.equals(attributeName)) {
            c = AccountBalanceByObject.class;
        }
        else {
            c = AccountBalanceByLevel.class;
        }

        return c;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());
    }
}
