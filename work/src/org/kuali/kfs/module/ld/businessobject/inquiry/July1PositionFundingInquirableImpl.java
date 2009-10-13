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
import org.kuali.kfs.gl.businessobject.lookup.BusinessObjectFieldConverter;
import org.kuali.kfs.module.ld.businessobject.LedgerBalance;
import org.kuali.kfs.sys.KFSConstants;

/**
 * This class is used to generate the URL for the user-defined attributes for the July1 Position Funds screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class July1PositionFundingInquirableImpl extends AbstractLaborInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(July1PositionFundingInquirableImpl.class);

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List<String> keys = new ArrayList<String>();
        return keys;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {

        Map userDefinedAttributeMap = new HashMap();
        return userDefinedAttributeMap;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        return attributeName;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        if (isExclusiveField(keyName, keyValue)) {
            keyValue = Constant.EMPTY_STRING;
        }
        return keyValue;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        keyName = BusinessObjectFieldConverter.convertToTransactionPropertyName(keyName);
        return keyName;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return "july1PositionFundingLookupable";
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return KFSConstants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return LedgerBalance.class;
    }
}
