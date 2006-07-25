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
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.KualiSystemCode;
import org.kuali.core.datadictionary.AttributeDefinition;
import org.kuali.core.datadictionary.AttributeReferenceDefinition;
import org.kuali.core.datadictionary.DataDictionaryEntryBase;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.core.lookup.KualiLookupableImpl;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.web.Constant;

/**
 * This class is the template class for the customized inqurable implementations used to generate balance inquiry screens.
 * 
 * @author Bin Gao from Michigan State University
 */
public abstract class AbstractGLInquirableImpl extends KualiInquirableImpl {

    /**
     * Helper method to build an inquiry url for a result field.
     * 
     * @param businessObject the business object instance to build the urls for
     * @param attributeName the attribute name which links to an inquirable
     * @return String url to inquiry
     */
    public String getInquiryUrl(BusinessObject businessObject, String attributeName) {

        BusinessObjectDictionaryService businessDictionary = SpringServiceLocator.getBusinessObjectDictionaryService();
        PersistenceStructureService persistenceStructureService = SpringServiceLocator.getPersistenceStructureService();

        String baseUrl = Constants.INQUIRY_ACTION;
        Properties parameters = new Properties();
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, Constants.START_METHOD);

        Object attributeValue = null;
        Class inquiryBusinessObjectClass = null;
        String attributeRefName = "";
        boolean isPkReference = false;

        Map userDefinedAttributeMap = getUserDefinedAttributeMap();
        boolean isUserDefinedAttribute = userDefinedAttributeMap == null ? false : userDefinedAttributeMap.containsKey(attributeName);

        // determine the type of the given attribute: user-defined, regular, nested-referenced or primitive reference
        if (isUserDefinedAttribute) {
            attributeName = getAttributeName(attributeName);
            inquiryBusinessObjectClass = getInquiryBusinessObjectClass();
            isPkReference = true;
        }
        else if (attributeName.equals(businessDictionary.getTitleAttribute(businessObject.getClass()))) {
            inquiryBusinessObjectClass = businessObject.getClass();
            isPkReference = true;
        }
        else if (ObjectUtils.isNestedAttribute(attributeName)) {
            inquiryBusinessObjectClass = KualiLookupableImpl.getNestedReferenceClass(businessObject, attributeName);
            //inquiryBusinessObjectClass = this.getNestedInquiryBusinessObjectClass(businessObject, attributeName);
        }
        else {
            Map primitiveReference = KualiLookupableImpl.getPrimitiveReference(businessObject, attributeName);
            if (primitiveReference != null && !primitiveReference.isEmpty()) {
                attributeRefName = (String) primitiveReference.keySet().iterator().next();
                inquiryBusinessObjectClass = (Class) primitiveReference.get(attributeRefName);
            }
            attributeValue = ObjectUtils.getPropertyValue(businessObject, attributeName);
            attributeValue = (attributeValue == null) ? "" : attributeValue.toString();
        }

        // process the business object class if the attribute name is not user-defined
        if (!isUserDefinedAttribute) {
            if (isExclusiveField(attributeName, attributeValue)) {
                return Constants.EMPTY_STRING;
            }

            if (inquiryBusinessObjectClass == null || businessDictionary.isInquirable(inquiryBusinessObjectClass) == null || !businessDictionary.isInquirable(inquiryBusinessObjectClass).booleanValue()) {
                return Constants.EMPTY_STRING;
            }

            if (KualiSystemCode.class.isAssignableFrom(inquiryBusinessObjectClass)) {
                inquiryBusinessObjectClass = KualiSystemCode.class;
            }
        }
        parameters.put(Constants.BUSINESS_OBJECT_CLASS_ATTRIBUTE, inquiryBusinessObjectClass.getName());

        List keys = new ArrayList();
        if (isUserDefinedAttribute) {
            baseUrl = getBaseUrl();
            keys = buildUserDefinedAttributeKeyList();

            parameters.put(Constants.RETURN_LOCATION_PARAMETER, Constant.RETURN_LOCATION_VALUE);
            parameters.put(Constants.GL_BALANCE_INQUIRY_FLAG, "true");
            parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, Constants.SEARCH_METHOD);
            parameters.put(Constants.DOC_FORM_KEY, "88888888");

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
                keyValue = (keyValue == null) ? "" : keyValue.toString();

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
     * This method gets the class name of the inquiry business object
     * 
     * @return the class name of the inquiry business object
     */
    protected abstract Class getInquiryBusinessObjectClass();

    /**
     * This method adds more parameters into the curren parameter map
     * 
     * @param parameter the current parameter map
     */
    protected abstract void addMoreParameters(Properties parameter, String attributeName);

    /**
     * This method determines whether the input name-value pair is exclusive from the processing
     * 
     * @param keyName the name of the name-value pair
     * @param keyValue the value of the name-value pair
     * @return true if the input key is in the exclusive list; otherwise, false
     */
    protected boolean isExclusiveField(Object keyName, Object keyValue) {

        if (keyName != null && keyValue != null) {
            String convertedKeyName = BusinessObjectFieldConverter.convertFromTransactionPropertyName(keyName.toString());

            if (convertedKeyName.equals(PropertyConstants.SUB_ACCOUNT_NUMBER) && keyValue.equals(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER)) {
                return true;
            }
            else if (convertedKeyName.equals(PropertyConstants.SUB_OBJECT_CODE) && keyValue.equals(Constant.CONSOLIDATED_SUB_OBJECT_CODE)) {
                return true;
            }
            else if (convertedKeyName.equals(PropertyConstants.OBJECT_TYPE_CODE) && keyValue.equals(Constant.CONSOLIDATED_OBJECT_TYPE_CODE)) {
                return true;
            }
            if (convertedKeyName.equals(PropertyConstants.SUB_ACCOUNT_NUMBER) && keyValue.equals(Constants.DASHES_SUB_ACCOUNT_NUMBER)) {
                return true;
            }
            else if (convertedKeyName.equals(PropertyConstants.SUB_OBJECT_CODE) && keyValue.equals(Constants.DASHES_SUB_OBJECT_CODE)) {
                return true;
            }
            else if (convertedKeyName.equals(PropertyConstants.PROJECT_CODE) && keyValue.equals(Constants.DASHES_PROJECT_CODE)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * This method recovers the values of the given keys
     * @param fieldValues
     * @param keyName
     * @param keyValue
     * @return
     */
    protected String recoverFieldValueFromConsolidation(Map fieldValues, Object keyName, Object keyValue){
        if(fieldValues == null || keyName == null || keyValue == null){
            return Constant.EMPTY_STRING;
        }
        
        Map convertedFieldValues = BusinessObjectFieldConverter.convertFromTransactionFieldValues(fieldValues);
        String convertedKeyName = BusinessObjectFieldConverter.convertFromTransactionPropertyName(keyName.toString());

        if (convertedKeyName.equals(PropertyConstants.SUB_ACCOUNT_NUMBER) && keyValue.equals(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER)) {
            return this.getValueFromFieldValues(convertedFieldValues, keyName);
        }
        else if (convertedKeyName.equals(PropertyConstants.SUB_OBJECT_CODE) && keyValue.equals(Constant.CONSOLIDATED_SUB_OBJECT_CODE)) {
            return this.getValueFromFieldValues(convertedFieldValues, keyName);
        }
        else if (convertedKeyName.equals(PropertyConstants.OBJECT_TYPE_CODE) && keyValue.equals(Constant.CONSOLIDATED_OBJECT_TYPE_CODE)) {
            return this.getValueFromFieldValues(convertedFieldValues, keyName);
        }
        
        return Constant.EMPTY_STRING;
    }
    
    // get the value of the given key from the field values 
    private String getValueFromFieldValues(Map fieldValues, Object keyName){
        String keyValue = Constant.EMPTY_STRING;
        
        if(fieldValues.containsKey(keyName)){
            keyValue = (String)fieldValues.get(keyName);
        }        
        return keyValue;
    }

    public Map getFieldValues(Map fieldValues){
        return fieldValues;
    }
    
    // TODO: not finished
    public Class getNestedInquiryBusinessObjectClass(BusinessObject businessObject, String attributeName){
        Class inquiryBusinessObjectClass = null;
        String entryName = businessObject.getClass().getName();
        System.out.println("businessObject: " + entryName);
        System.out.println("attributeName: " + attributeName);
        
        DataDictionaryService dataDictionary = SpringServiceLocator.getDataDictionaryService();
        AttributeDefinition attributeDefinition = null;
        
        if (StringUtils.isBlank(attributeName)) {
            throw new IllegalArgumentException("invalid (blank) attributeName");
        }

        DataDictionaryEntryBase entry = (DataDictionaryEntryBase) dataDictionary.getDataDictionary().getDictionaryObjectEntry(entryName);
        if (entry != null) {
            attributeDefinition = entry.getAttributeDefinition(attributeName);
            inquiryBusinessObjectClass = KualiLookupableImpl.getNestedReferenceClass(businessObject, attributeName);
        }
        
        if (attributeDefinition instanceof AttributeReferenceDefinition) {
            AttributeReferenceDefinition attributeReferenceDefinition = (AttributeReferenceDefinition) attributeDefinition;
            System.out.println("Source Classname = " + attributeReferenceDefinition.getSourceClassName());
            System.out.println("Source Attribute = " + attributeReferenceDefinition.getSourceAttributeName());
            
            try{
                inquiryBusinessObjectClass = Class.forName(attributeReferenceDefinition.getSourceClassName()); 
            }
            catch(Exception e){
                throw new IllegalArgumentException("fail to construct a Class");
            }
        }
        
        return inquiryBusinessObjectClass;
    }
}
