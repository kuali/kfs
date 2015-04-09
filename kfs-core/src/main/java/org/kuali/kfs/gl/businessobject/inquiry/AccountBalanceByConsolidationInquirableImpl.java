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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.AccountBalanceByLevel;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.service.LookupService;

/**
 * This class is used to generate the URL for the user-defined attributes for the account balace by consolidation screen. It is
 * entended the KualiInquirableImpl class, so it covers both the default implementation and customized implemetnation.
 */
public class AccountBalanceByConsolidationInquirableImpl extends AbstractGeneralLedgerInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccountBalanceByConsolidationInquirableImpl.class);

    private BusinessObjectDictionaryService dataDictionary;
    private LookupService lookupService;
    private Class businessObjectClass;

    /**
     * Builds a list of attributes for finding the values for this inquiry.
     * 
     * @return a List of attribute keys to inquire on
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#buildUserDefinedAttributeKeyList()
     */
    protected List buildUserDefinedAttributeKeyList() {
        List keys = new ArrayList();

        keys.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keys.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        keys.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add("financialObject.financialObjectLevel.financialConsolidationObject.finConsolidationObjectCode");
        keys.add(Constant.COST_SHARE_OPTION);
        keys.add(Constant.CONSOLIDATION_OPTION);
        keys.add(Constant.PENDING_ENTRY_OPTION);

        return keys;
    }

    /**
     * Generates an attribute map of fields created simply for this inquiry
     * 
     * @return a Map with a link button attribute in it
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getUserDefinedAttributeMap()
     */
    protected Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();
        // userDefinedAttributeMap.put("financialObject.financialObjectLevel.financialConsolidationObject.financialConsolidationObjectCode",
        // "");
        userDefinedAttributeMap.put(GeneralLedgerConstants.DummyBusinessObject.LINK_BUTTON_OPTION, "");
        return userDefinedAttributeMap;
    }

    /**
     * Converts attribute names for the inquiry screen
     * 
     * @return the converted name of the attribute
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getAttributeName(java.lang.String)
     */
    protected String getAttributeName(String attributeName) {
        // if (attributeName.equals("dummyBusinessObject.linkButtonOption")) {
        // attributeName = "financialObject.financialObjectLevel";
        // }
        return attributeName;
    }

    /**
     * Overrides the key value with a blank string if it's an exclusive value
     * 
     * @param keyName the keyName of the key to check
     * @param keyValue the value of the key to check
     * @return a new value
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyValue(java.lang.String, java.lang.Object)
     */
    protected Object getKeyValue(String keyName, Object keyValue) {
        if (isExclusiveField(keyName, keyValue)) {
            keyValue = "";
        }
        return keyValue;
    }

    /**
     * Given a key name, returns the key name; this implementation just passes back what it gets
     * 
     * @return the key name passed in
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getKeyName(java.lang.String)
     */
    protected String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * The name of this inquiry
     * 
     * @return a String with this inquiry's name
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getLookupableImplAttributeName()
     */
    protected String getLookupableImplAttributeName() {
        return Constant.GL_LOOKUPABLE_ACCOUNT_BALANCE_BY_LEVEL;
    }

    /**
     * The base url that inquires of this type need to be sent to
     * 
     * @return the base url
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getBaseUrl()
     */
    protected String getBaseUrl() {
        return KFSConstants.GL_MODIFIED_INQUIRY_ACTION;
    }

    /**
     * The business object class this inquiry should be returning for a given attribute - in this case, AccountBalanceByLevel for the LINK_BUTTON_OPTION
     * 
     * @param attributeName the attribute to return a class for
     * @return a class for the attribute
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#getInquiryBusinessObjectClass(String)
     */
    protected Class getInquiryBusinessObjectClass(String attributeName) {
        Class c = null;
        if (GeneralLedgerConstants.DummyBusinessObject.LINK_BUTTON_OPTION.equals(attributeName)) {
            c = AccountBalanceByLevel.class;
        }
        return c;
    }

    /**
     * For a given attribute, lets this inquiry add more parameters
     * @param parameter the set of parameters for the inquiry
     * @param attributeName the attributeName parameters are being set for
     * @see org.kuali.kfs.gl.businessobject.inquiry.AbstractGeneralLedgerInquirableImpl#addMoreParameters(java.util.Properties, java.lang.String)
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        parameter.put(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());
    }
}
