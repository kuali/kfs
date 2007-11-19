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

import java.util.List;
import java.util.Map;
import java.util.Properties;

/**
 * The inquirable class to support the cash balance lookup.  Since there are no user defined inquiries to
 * perform on cash balance fields, this class returns many, many nulls
 */
public class CashBalanceInquirableImpl extends AbstractGLInquirableImpl {

    /**
     * Since there are no user defined attributes, returns null
     * @return null - no user defined attributes
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    @Override
    public List buildUserDefinedAttributeKeyList() {
        return null;
    }

    /**
     * Returns null as the map, as there are no drill downs here
     * @return null for the map of attributes
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getUserDefinedAttributeMap()
     */
    @Override
    public Map getUserDefinedAttributeMap() {
        return null;
    }

    /**
     * Returns null for any attribute
     * @param attributeName the name of an attribute for the inquiry
     * @return null, no matter what
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getAttributeName(java.lang.String)
     */
    @Override
    public String getAttributeName(String attributeName) {
        return null;
    }

    /**
     * Returns null for any name/value pair its handed
     * @param keyName the name of the key to lookup
     * @param keyValue the value of the key to lookup
     * @return null, every time
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    @Override
    public Object getKeyValue(String keyName, Object keyValue) {
        return null;
    }

    /**
     * Given a key name, returns null
     * @param keyName the key name to change on the fly
     * @return null, every time
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyName(java.lang.String)
     */
    @Override
    public String getKeyName(String keyName) {
        return null;
    }

    /**
     * Returns null as the lookupable impl for this inquiry
     * @return null, there isn't a lookupable impl
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getLookupableImplAttributeName()
     */
    @Override
    public String getLookupableImplAttributeName() {
        return null;
    }

    /**
     * Returns the base inquiry url to search...in this case, nothing
     * @return null, as there's no URL to go to
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getBaseUrl()
     */
    @Override
    public String getBaseUrl() {
        return null;
    }

    /**
     * The class name of the business object that should be inquired on for the attribute
     * @param the attribute name to build an inquiry for
     * @return null, as there are no inquiries
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    @Override
    public Class getInquiryBusinessObjectClass(String attributeName) {
        return null;
    }

    /**
     * Adds no parameters at all
     * @param parameter the parameter map to add new properties
     * @param attributeName the name of the attribute being inquired on
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    @Override
    public void addMoreParameters(Properties parameter, String attributeName) {
    }
}
