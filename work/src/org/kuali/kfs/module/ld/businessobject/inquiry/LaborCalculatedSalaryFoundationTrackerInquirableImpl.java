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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.module.ld.businessobject.July1PositionFunding;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * This class is used to generate the URL for the user-defined attributes for the CSF tracker screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class LaborCalculatedSalaryFoundationTrackerInquirableImpl extends AbstractLaborInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LaborCalculatedSalaryFoundationTrackerInquirableImpl.class);

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#buildUserDefinedAttributeKeyList()
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
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {

        Map userDefinedAttributeMap = new HashMap();
        // userDefinedAttributeMap.put(KFSPropertyConstants.JULY1_BUDGET_AMOUNT, "");
        return userDefinedAttributeMap;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        return attributeName;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        if (isExclusiveField(keyName, keyValue)) {
            keyValue = Constant.EMPTY_STRING;
        }
        return keyValue;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return "CSFTrackerBalanceLookupable";
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return KFSConstants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return July1PositionFunding.class;
    }
}
