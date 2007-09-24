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

import java.util.List;
import java.util.Map;

/**
 * This class is used to generate the URL for the user-defined attributes for the pending ledger entry screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class LedgerPendingEntryInquirableImpl extends AbstractLaborInquirableImpl {

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        return null;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        return null;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return null;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return null;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        return null;
    }

    /**
     * @see org.kuali.module.labor.web.inquirable.AbstractLaborInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        return null;
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
        return null;
    }
}