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
 * This class is used to generate the URL for the user-defined attributes for the GL entry screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 * 
 * 
 */
public class EntryInquirableImpl extends AbstractGLInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntryInquirableImpl.class);

    @Override
    protected List buildUserDefinedAttributeKeyList() {
        return null;
    }

    @Override
    protected Map getUserDefinedAttributeMap() {
        return null;
    }

    @Override
    protected String getAttributeName(String attributeName) {
        return null;
    }

    @Override
    protected Object getKeyValue(String keyName, Object keyValue) {
        return null;
    }

    @Override
    protected String getKeyName(String keyName) {
        return null;
    }

    @Override
    protected String getLookupableImplAttributeName() {
        return null;
    }

    @Override
    protected String getBaseUrl() {
        return null;
    }

    @Override
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return null;
    }

    @Override
    protected void addMoreParameters(Properties parameter, String attributeName) {
    }
}
