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
