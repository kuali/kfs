/*
 * Copyright 2006-2007 The Kuali Foundation.
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
 * Builds an inquirable to build inquiry links for fields in the encumbrance lookup. That lookup
 * has no drill downs outside of chart attributes, so this class returns null for many classes.
 */
public class EncumbranceInquirableImpl extends AbstractGLInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceInquirableImpl.class);

    /**
     * Since there are no user defined attributes, returns null
     * @return null - no user defined attributes
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    @Override
    protected List buildUserDefinedAttributeKeyList() {
        return null;
    }

    /**
     * Returns null as the map, as there are no drill downs here
     * @return null for the map of attributes
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getUserDefinedAttributeMap()
     */
    @Override
    protected Map getUserDefinedAttributeMap() {
        return null;
    }

    /**
     * Returns null for any attribute
     * @param attributeName the name of an attribute for the inquiry
     * @return null, no matter what
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getAttributeName(java.lang.String)
     */
    @Override
    protected String getAttributeName(String attributeName) {
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
    protected Object getKeyValue(String keyName, Object keyValue) {
        return null;
    }

    /**
     * Given a key name, returns null
     * @param keyName the key name to change on the fly
     * @return null, every time
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyName(java.lang.String)
     */
    @Override
    protected String getKeyName(String keyName) {
        return null;
    }

    /**
     * Returns null as the lookupable impl for this inquiry
     * @return null, there isn't a lookupable impl
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getLookupableImplAttributeName()
     */
    @Override
    protected String getLookupableImplAttributeName() {
        return null;
    }

    /**
     * Returns the base inquiry url to search...in this case, nothing
     * @return null, as there's no URL to go to
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getBaseUrl()
     */
    @Override
    protected String getBaseUrl() {
        return null;
    }

    /**
     * The class name of the business object that should be inquired on for the attribute
     * @param the attribute name to build an inquiry for
     * @return null, as there are no inquiries
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    @Override
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return null;
    }

    /**
     * Adds no parameters at all
     * @param parameter the parameter map to add new properties
     * @param attributeName the name of the attribute being inquired on
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    @Override
    protected void addMoreParameters(Properties parameter, String attributeName) {
    }
}
