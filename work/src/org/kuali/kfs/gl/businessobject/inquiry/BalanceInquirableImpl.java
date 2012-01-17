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
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.gl.businessobject.lookup.BusinessObjectFieldConverter;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.service.LookupService;

/**
 * This class is used to generate the URL for the user-defined attributes for the GL balace screen. It is entended the
 * KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class BalanceInquirableImpl extends AbstractGeneralLedgerInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquirableImpl.class);

    private BusinessObjectDictionaryService dataDictionary;
    private LookupService lookupService;
    private Class businessObjectClass;

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
        keys.add(KFSPropertyConstants.BALANCE_TYPE_CODE);
        keys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.OBJECT_CODE);
        keys.add(KFSPropertyConstants.SUB_OBJECT_CODE);
        keys.add(KFSPropertyConstants.OBJECT_TYPE_CODE);
        keys.add(Constant.PENDING_ENTRY_OPTION);

        return keys;
    }

    /**
     * The addition of all the month amounts, plus beginning balance and c&g balance as attributes
     * @return a Map of user defined attributes
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();

        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH1_AMOUNT, KFSConstants.MONTH1);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH2_AMOUNT, KFSConstants.MONTH2);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH3_AMOUNT, KFSConstants.MONTH3);

        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH4_AMOUNT, KFSConstants.MONTH4);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH5_AMOUNT, KFSConstants.MONTH5);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH6_AMOUNT, KFSConstants.MONTH6);

        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH7_AMOUNT, KFSConstants.MONTH7);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH8_AMOUNT, KFSConstants.MONTH8);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH9_AMOUNT, KFSConstants.MONTH9);

        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH10_AMOUNT, KFSConstants.MONTH10);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH11_AMOUNT, KFSConstants.MONTH11);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH12_AMOUNT, KFSConstants.MONTH12);
        userDefinedAttributeMap.put(KFSPropertyConstants.MONTH13_AMOUNT, KFSConstants.MONTH13);

        userDefinedAttributeMap.put(KFSPropertyConstants.BEGINNING_BALANCE_LINE_AMOUNT, KFSConstants.PERIOD_CODE_BEGINNING_BALANCE);
        userDefinedAttributeMap.put(KFSPropertyConstants.CONTRACTS_GRANTS_BEGINNING_BALANCE_AMOUNT, KFSConstants.PERIOD_CODE_CG_BEGINNING_BALANCE);

        return userDefinedAttributeMap;
    }

    /**
     * Changes the name of attributes on the fly...in this case, this just always returns the attribute name it's handed
     * @param attributeName the attribute to rename
     * @return a String with the new attribute name
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
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
            keyValue = Constant.EMPTY_STRING;
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
        return Constant.GL_LOOKUPABLE_ENTRY;
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
     * Retrieves the business class of the next class type to drill up...since balance summarizes entry, it's entry
     * @param attributeName the name to build the inquiry link to
     * @return the Class of the business object that should be inquired on
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        return Entry.class;
    }

    /**
     * Addes the lookup impl and period code attributes to the parameters
     * @param parameter the parameters used in the lookup
     * @param attributeName the attribute name that an inquiry URL is being built for
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());

        String periodCode = (String) getUserDefinedAttributeMap().get(attributeName);
        parameter.put(KFSConstants.UNIVERSITY_FISCAL_PERIOD_CODE_PROPERTY_NAME, periodCode);
    }
}
