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

import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.module.ld.businessobject.PositionFunding;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;

/**
 * This class is used to generate the URL for the user-defined attributes for the Position Inquiry screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class PositionDataInquirableImpl extends AbstractLaborInquirableImpl {

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();
        keys.add(KFSPropertyConstants.POSITION_NUMBER);
        return keys;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        return attributeName;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return KFSConstants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.kfs.module.ld.businessobject.inquiry.AbstractLaborInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return PositionFunding.class;
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
        userDefinedAttributeMap.put(GeneralLedgerConstants.DummyBusinessObject.LINK_BUTTON_OPTION, "");

        return userDefinedAttributeMap;
    }
}
