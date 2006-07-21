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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.LookupService;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.web.Constant;

/**
 * This class is used to generate the URL for the user-defined attributes for the GL entry screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 * 
 * @author Bin Gao from Michigan State University
 */
public class EntryInquirableImpl extends AbstractGLInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EntryInquirableImpl.class);

    private BusinessObjectDictionaryService dataDictionary;
    private LookupService lookupService;
    private Class businessObjectClass;

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        return new ArrayList();
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();

        userDefinedAttributeMap.put(PropertyConstants.FINANCIAL_DOCUMENT_NUMBER, "");
        return userDefinedAttributeMap;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
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
        keyName = BusinessObjectFieldConverter.convertToTransactionPropertyName(keyName);
        return keyName;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return Constant.GL_LOOKUPABLE_ENTRY;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return Constants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#getInquiryBusinessObjectClass()
     */
    protected Class getInquiryBusinessObjectClass() {
        return Entry.class;
    }

    /**
     * @see org.kuali.module.gl.web.inquirable.AbstractGLInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        String documentNumber = (String) getUserDefinedAttributeMap().get(attributeName);
        parameter.put(Constants.DISPATCH_REQUEST_PARAMETER, Constants.DOC_HANDLER_METHOD);
        parameter.put(Constants.PARAMETER_COMMAND, Constants.METHOD_DISPLAY_DOC_SEARCH_VIEW);
        parameter.put(Constants.PARAMETER_DOC_ID, documentNumber);
    }
}
