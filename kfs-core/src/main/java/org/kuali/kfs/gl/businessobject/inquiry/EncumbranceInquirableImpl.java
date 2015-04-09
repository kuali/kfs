/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.gl.businessobject.inquiry;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.gl.businessobject.Encumbrance;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * Builds an inquirable to build inquiry links for fields in the encumbrance lookup. That lookup
 * has no drill downs outside of chart attributes, so this class returns null for many classes.
 */
public class EncumbranceInquirableImpl extends AbstractGeneralLedgerInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EncumbranceInquirableImpl.class);

    /**
     * Since there are no user defined attributes, returns null
     * @return null - no user defined attributes
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    @Override
    protected List buildUserDefinedAttributeKeyList() {
        return null;
    }

    /**
     * Returns null as the map, as there are no drill downs here
     * @return null for the map of attributes
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getUserDefinedAttributeMap()
     */
    @Override
    protected Map getUserDefinedAttributeMap() {
        return null;
    }

    /**
     * Returns null for any attribute
     * @param attributeName the name of an attribute for the inquiry
     * @return null, no matter what
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getAttributeName(java.lang.String)
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
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    @Override
    protected Object getKeyValue(String keyName, Object keyValue) {
        return null;
    }

    /**
     * Given a key name, returns null
     * @param keyName the key name to change on the fly
     * @return null, every time
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyName(java.lang.String)
     */
    @Override
    protected String getKeyName(String keyName) {
        return null;
    }

    /**
     * Returns null as the lookupable impl for this inquiry
     * @return null, there isn't a lookupable impl
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getLookupableImplAttributeName()
     */
    @Override
    protected String getLookupableImplAttributeName() {
        return null;
    }

    /**
     * Returns the base inquiry url to search...in this case, nothing
     * @return null, as there's no URL to go to
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getBaseUrl()
     */
    @Override
    protected String getBaseUrl() {
        return null;
    }

    /**
     * The class name of the business object that should be inquired on for the attribute
     * @param the attribute name to build an inquiry for
     * @return null, as there are no inquiries
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(java.lang.String)
     */
    @Override
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return null;
    }

    /**
     * Adds no parameters at all
     * @param parameter the parameter map to add new properties
     * @param attributeName the name of the attribute being inquired on
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    @Override
    protected void addMoreParameters(Properties parameter, String attributeName) {
    }
    
    /*
     * Add inquiry url for encumbrance document type code
     * 
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getInquiryUrl(org.kuali.rice.krad.bo.BusinessObject,
     *      java.lang.String)
     */
    @Override
    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName) {
        HtmlData url = null; 
        if (KFSPropertyConstants.ENCUMBRANCE_DOCUMENT_TYPE_CODE.equals(attributeName)) {
            String docTypeCode = ((Encumbrance)businessObject).getDocumentTypeCode();
            url = getDocTypeInquiryUrl(docTypeCode);
        }else{
            url = super.getInquiryUrl(businessObject, attributeName);
        }
        return url;
    }
    
}
