/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.gl.businessobject.inquiry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.AccountBalanceByObject;
import org.kuali.kfs.gl.businessobject.lookup.BusinessObjectFieldConverter;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;

/**
 * This class is used to generate the URL for the user-defined attributes for the account balace by object screen. It is entended
 * the AbstractGeneralLedgerInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class AccountBalanceByObjectInquirableImpl extends AbstractGeneralLedgerInquirableImpl {
    @SuppressWarnings("unused")
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceByObjectInquirableImpl.class);

    /**
     * Builds the keys for this inquiry.
     * @return a List of Strings, holding the keys of this inquiry
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();

        keys.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keys.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.OBJECT_CODE);
        keys.add(KFSPropertyConstants.SUB_OBJECT_CODE);
        keys.add(Constant.COST_SHARE_OPTION);
        keys.add(Constant.CONSOLIDATION_OPTION);

        return keys;
    }

    /**
     * The addition of a couple attributes to the lookup - object code, object level, and link button
     * @return a Map of user defined attributes
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();
        userDefinedAttributeMap.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, "");
        userDefinedAttributeMap.put(GeneralLedgerConstants.DummyBusinessObject.LINK_BUTTON_OPTION, "");
        userDefinedAttributeMap.put(GeneralLedgerConstants.BalanceInquiryDrillDowns.OBJECT_LEVEL_CODE, "");
        return userDefinedAttributeMap;
    }

    /**
     * Changes the name of attributes on the fly...in this case, turns the link button to display its name as glpe
     * @param attributeName the attribute to rename
     * @return a String with the new attribute name
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        if (attributeName.equals(GeneralLedgerConstants.DummyBusinessObject.LINK_BUTTON_OPTION)) {
            attributeName = KFSPropertyConstants.GENERAL_LEDGER_PENDING_ENTRY;
        }
        return attributeName;
    }

    /**
     * If the key name sent in represents an "exclusive field", returns "" as the key value
     * @param keyName the name of the key that may be changed
     * @param keyValue the value of the key that may be changed
     * @return an Object with the perhaps modified value for the key
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        if (isExclusiveField(keyName, keyValue)) {
            keyValue = "";
        }
        return keyValue;
    }

    /**
     * Justs returns the key name given
     * @param keyName a key name
     * @return the key name given
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        keyName = BusinessObjectFieldConverter.convertToTransactionPropertyName(keyName);
        return keyName;
    }

    /**
     * Return a Spring bean for the lookup
     * @return the name of the Spring bean of the lookup
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return Constant.GL_LOOKUPABLE_PENDING_ENTRY;
    }

    /**
     * Return the page name of this lookup
     * @return the page name for all GL lookups
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return KFSConstants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * Retrieves the business class to use as the basis of an inquiry for the given attribute
     * @param attributeName the name to build the inquiry link to
     * @return the Class of the business object that should be inquired on
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        if (KFSPropertyConstants.GENERAL_LEDGER_PENDING_ENTRY.equals(attributeName)) {
            return GeneralLedgerPendingEntry.class;
        }
        return AccountBalanceByObject.class;
    }

    /**
     * Addes the lookup impl attribute to the parameters
     * @param parameter the parameters used in the lookup
     * @param attributeName the attribute name that an inquiry URL is being built for
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());
    }
}
