/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.labor.web.inquirable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.core.bo.BusinessObject;
import org.kuali.core.lookup.LookupUtils;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.UrlFactory;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.inquiry.KfsInquirableImpl;
import org.kuali.module.chart.bo.KualiSystemCode;
import org.kuali.module.gl.web.Constant;
import org.kuali.module.labor.LaborConstants;

/**
 * This class is the template class for the customized inqurable implementations used to generate balance inquiry screens.
 */
public abstract class AbstractLaborInquirableImpl extends KfsInquirableImpl {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractLaborInquirableImpl.class);

    /**
     * Helper method to build an inquiry url for a result field.
     * 
     * @param businessObject the business object instance to build the urls for
     * @param attributeName the attribute name which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject businessObject, String attributeName) {
        BusinessObjectDictionaryService businessDictionary = SpringContext.getBean(BusinessObjectDictionaryService.class);
        PersistenceStructureService persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);

        String baseUrl = KFSConstants.INQUIRY_ACTION;
        Properties parameters = new Properties();
        parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.START_METHOD);

        Object attributeValue = null;
        Class inquiryBusinessObjectClass = null;
        String attributeRefName = Constant.EMPTY_STRING;
        boolean isPkReference = false;

        Map userDefinedAttributeMap = getUserDefinedAttributeMap();
        boolean isUserDefinedAttribute = userDefinedAttributeMap == null ? false : userDefinedAttributeMap.containsKey(attributeName);

        // determine the type of the given attribute: user-defined, regular, nested-referenced or primitive reference
        if (isUserDefinedAttribute) {
            attributeName = getAttributeName(attributeName);
            inquiryBusinessObjectClass = getInquiryBusinessObjectClass(attributeName);
            isPkReference = true;
        }
        else if (attributeName.equals(businessDictionary.getTitleAttribute(businessObject.getClass()))) {
            inquiryBusinessObjectClass = businessObject.getClass();
            isPkReference = true;
        }
        else if (ObjectUtils.isNestedAttribute(attributeName)) {
            return Constant.EMPTY_STRING;
        }
        else {
            Map primitiveReference = LookupUtils.getPrimitiveReference(businessObject, attributeName);
            if (primitiveReference != null && !primitiveReference.isEmpty()) {
                attributeRefName = (String) primitiveReference.keySet().iterator().next();
                inquiryBusinessObjectClass = (Class) primitiveReference.get(attributeRefName);
            }
            attributeValue = ObjectUtils.getPropertyValue(businessObject, attributeName);
            attributeValue = (attributeValue == null) ? "" : attributeValue.toString();
        }

        // process the business object class if the attribute name is not user-defined
        if (!isUserDefinedAttribute) {
            if (isExclusiveFieldToBeALink(attributeName, attributeValue)) {
                return Constant.EMPTY_STRING;
            }

            if (inquiryBusinessObjectClass == null || businessDictionary.isInquirable(inquiryBusinessObjectClass) == null || !businessDictionary.isInquirable(inquiryBusinessObjectClass).booleanValue()) {
                return Constant.EMPTY_STRING;
            }

            if (KualiSystemCode.class.isAssignableFrom(inquiryBusinessObjectClass)) {
                inquiryBusinessObjectClass = KualiSystemCode.class;
            }
        }
        parameters.put(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, inquiryBusinessObjectClass.getName());

        List keys = new ArrayList();
        if (isUserDefinedAttribute) {
            baseUrl = getBaseUrl();
            keys = buildUserDefinedAttributeKeyList();

            parameters.put(KFSConstants.RETURN_LOCATION_PARAMETER, Constant.RETURN_LOCATION_VALUE);
            parameters.put(KFSConstants.GL_BALANCE_INQUIRY_FLAG, "true");
            parameters.put(KFSConstants.DISPATCH_REQUEST_PARAMETER, KFSConstants.SEARCH_METHOD);
            parameters.put(KFSConstants.DOC_FORM_KEY, "88888888");

            // add more customized parameters into the current parameter map
            addMoreParameters(parameters, attributeName);
        }
        else if (persistenceStructureService.isPersistable(inquiryBusinessObjectClass)) {
            keys = persistenceStructureService.listPrimaryKeyFieldNames(inquiryBusinessObjectClass);
        }

        // build key value url parameters used to retrieve the business object
        if (keys != null) {
            for (Iterator keyIterator = keys.iterator(); keyIterator.hasNext();) {
                String keyName = (String) keyIterator.next();

                // convert the key names based on their formats and types
                String keyConversion = keyName;
                if (ObjectUtils.isNestedAttribute(attributeName)) {
                    if (isUserDefinedAttribute) {
                        keyConversion = keyName;
                    }
                    else {
                        keyConversion = ObjectUtils.getNestedAttributePrefix(attributeName) + "." + keyName;
                    }
                }
                else {
                    if (isPkReference) {
                        keyConversion = keyName;
                    }
                    else {
                        keyConversion = persistenceStructureService.getForeignKeyFieldName(businessObject.getClass(), attributeRefName, keyName);
                    }
                }

                Object keyValue = ObjectUtils.getPropertyValue(businessObject, keyConversion);
                keyValue = (keyValue == null) ? Constant.EMPTY_STRING : keyValue.toString();

                // convert the key value and name into the given ones
                Object tempKeyValue = this.getKeyValue(keyName, keyValue);
                keyValue = tempKeyValue == null ? keyValue : tempKeyValue;

                String tempKeyName = this.getKeyName(keyName);
                keyName = tempKeyName == null ? keyName : tempKeyName;

                // add the key-value pair into the parameter map
                if (keyName != null)
                    parameters.put(keyName, keyValue);
            }
        }

        return UrlFactory.parameterizeUrl(baseUrl, parameters);
    }

    /**
     * This method builds the inquiry url for user-defined attribute
     * 
     * @return key list
     */
    protected abstract List buildUserDefinedAttributeKeyList();

    /**
     * This method defines the user-defined attribute map
     * 
     * @return the user-defined attribute map
     */
    protected abstract Map getUserDefinedAttributeMap();

    /**
     * This method finds the matching attribute name of given one
     * 
     * @param attributeName the given attribute name
     * @return the attribute name from the given one
     */
    protected abstract String getAttributeName(String attributeName);

    /**
     * This method finds the matching the key value of the given one
     * 
     * @param keyName the given key name
     * @param keyValue the given key value
     * @return the key value from the given key value
     */
    protected abstract Object getKeyValue(String keyName, Object keyValue);

    /**
     * This method finds the matching the key name of the given one
     * 
     * @param keyName the given key name
     * @return the key value from the given key name
     */
    protected abstract String getKeyName(String keyName);

    /**
     * This method defines the lookupable implementation attribute name
     * 
     * @return the lookupable implementation attribute name
     */
    protected abstract String getLookupableImplAttributeName();

    /**
     * This method defines the base inquiry url
     * 
     * @return the base inquiry url
     */
    protected abstract String getBaseUrl();

    /**
     * This method gets the class name of the inquiry business object for a given attribute.
     * 
     * @return the class name of the inquiry business object for a given attribute
     */
    protected abstract Class getInquiryBusinessObjectClass(String attributeName);

    /**
     * This method adds more parameters into the curren parameter map
     * 
     * @param parameter the current parameter map
     */
    protected void addMoreParameters(Properties parameter, String attributeName) {
        return;
    }

    /**
     * This method determines whether the input name-value pair is exclusive from the processing
     * 
     * @param keyName the name of the name-value pair
     * @param keyValue the value of the name-value pair
     * @return true if the input key is in the exclusive list; otherwise, false
     */
    protected boolean isExclusiveField(Object keyName, Object keyValue) {

        if (keyName != null && keyValue != null) {

            if (keyName.equals(KFSPropertyConstants.SUB_ACCOUNT_NUMBER) && keyValue.equals(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER)) {
                return true;
            }
            else if (keyName.equals(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE) && keyValue.equals(Constant.CONSOLIDATED_SUB_OBJECT_CODE)) {
                return true;
            }
            else if (keyName.equals(KFSPropertyConstants.FINANCIAL_OBJECT_TYPE_CODE) && keyValue.equals(Constant.CONSOLIDATED_OBJECT_TYPE_CODE)) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method determines whether the input name-value pair is exclusive to be a link
     * 
     * @param keyName the name of the name-value pair
     * @param keyValue the value of the name-value pair
     * @return true if the input key is in the exclusive list; otherwise, false
     */
    protected boolean isExclusiveFieldToBeALink(Object keyName, Object keyValue) {

        if (keyName != null && keyValue != null) {

            if (isExclusiveField(keyName, keyValue)) {
                return true;
            }
            else if (keyName.equals(KFSPropertyConstants.SUB_ACCOUNT_NUMBER) && keyValue.equals(KFSConstants.getDashSubAccountNumber())) {
                return true;
            }
            else if (keyName.equals(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE) && keyValue.equals(KFSConstants.getDashFinancialSubObjectCode())) {
                return true;
            }
            else if (keyName.equals(KFSPropertyConstants.PROJECT_CODE) && keyValue.equals(KFSConstants.getDashProjectCode())) {
                return true;
            }
            else if (keyName.equals(KFSPropertyConstants.POSITION_NUMBER) && keyValue.equals(LaborConstants.getDashPositionNumber())) {
                return true;
            }
            else if (keyName.equals(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE) && keyValue.equals(LaborConstants.BalanceInquiries.BALANCE_TYPE_AC_AND_A21)) {
                return true;
            }
        }
        return false;
    }
}
