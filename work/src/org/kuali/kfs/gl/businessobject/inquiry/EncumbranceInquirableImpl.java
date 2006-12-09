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
 * This class...
 * 
 * 
 */
public class EncumbranceInquirableImpl extends AbstractGLInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceInquirableImpl.class);

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    @Override
    protected List buildUserDefinedAttributeKeyList() {
        return null;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getUserDefinedAttributeMap()
     */
    @Override
    protected Map getUserDefinedAttributeMap() {
        return null;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getAttributeName(java.lang.String)
     */
    @Override
    protected String getAttributeName(String attributeName) {
        return null;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    @Override
    protected Object getKeyValue(String keyName, Object keyValue) {
        return null;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getKeyName(java.lang.String)
     */
    @Override
    protected String getKeyName(String keyName) {
        return null;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getLookupableImplAttributeName()
     */
    @Override
    protected String getLookupableImplAttributeName() {
        return null;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getBaseUrl()
     */
    @Override
    protected String getBaseUrl() {
        return null;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    @Override
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return null;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    @Override
    protected void addMoreParameters(Properties parameter, String attributeName) {
    }
}
