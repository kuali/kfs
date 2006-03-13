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

import org.kuali.Constants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.KualiSystemCode;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.core.lookup.KualiLookupableImpl;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.module.gl.web.Constant;

/**
 * This class is the template class for the customized inqurable implementations used to generate balance inquiry screens
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
    public static String getInquiryUrl(BusinessObject businessObject, String attributeName, boolean forceInquiry) {

        BusinessObjectDictionaryService businessDictionary = SpringServiceLocator.getBusinessObjectDictionaryService();
        PersistenceStructureService persistenceStructureService = SpringServiceLocator.getPersistenceStructureService();

        String baseUrl = Constants.INQUIRY_ACTION;
        Properties parameters = new Properties();
        parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, "start");

        Class inquiryBusinessObjectClass = null;
        String attributeRefName = "";
        boolean isPkReference = false;

        Map userDefinedAttributeMap = getUserDefinedAttributeMap();
        boolean isUserDefinedAttribute = userDefinedAttributeMap.containsKey(attributeName);

        if (isUserDefinedAttribute) {
            attributeName = getAttributeName(attributeName);
            inquiryBusinessObjectClass = getInquiryBusinessObjectClass();
            isPkReference = true;
        }
        else if (attributeName.equals(businessDictionary.getTitleAttribute(businessObject.getClass()))) {
            inquiryBusinessObjectClass = businessObject.getClass();
            isPkReference = true;
        }
        else {
            if (ObjectUtils.isNestedAttribute(attributeName)) {
                inquiryBusinessObjectClass = KualiLookupableImpl.getNestedReferenceClass(businessObject, attributeName);
            }
            else {
                Map primitiveReference = KualiLookupableImpl.getPrimitiveReference(businessObject, attributeName);
                if (primitiveReference != null && !primitiveReference.isEmpty()) {
                    attributeRefName = (String) primitiveReference.keySet().iterator().next();
                    inquiryBusinessObjectClass = (Class) primitiveReference.get(attributeRefName);
                }
            }
        }

        // process the business object class if the attribute name is not user-defined
        if (!isUserDefinedAttribute) {
            if (inquiryBusinessObjectClass == null || businessDictionary.isInquirable(inquiryBusinessObjectClass) == null
                    || !businessDictionary.isInquirable(inquiryBusinessObjectClass).booleanValue()) {
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
            parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, "search");
            parameters.put(Constants.DOC_FORM_KEY, "88888888");

            // add more customized parameters into the current parameter map
            addMoreParameters(parameters);
        }
        else if (persistenceStructureService.isPersistable(inquiryBusinessObjectClass)) {
            keys = persistenceStructureService.listPrimaryKeyFieldNames(inquiryBusinessObjectClass);
        }

        // build key value url parameters used to retrieve the business object
        Iterator keyIterator = keys.iterator();
        while (keyIterator.hasNext()) {
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
                    keyConversion = persistenceStructureService.
                    	getForeignKeyFieldName(businessObject.getClass(), attributeRefName, keyName);
                }
            }
            Object keyValue = ObjectUtils.getPropertyValue(businessObject, keyConversion);
            keyValue = (keyValue == null) ? "" : keyValue.toString();

            // convert the key value and name into the given ones
            keyValue = getKeyValue(keyValue);
            keyName = getKeyName(keyName);

            // add the key-value pair into the parameter map
            parameters.put(keyName, keyValue);
        }

        return UrlFactory.paremeterizeUrl(baseUrl, parameters);
    }

    /**
     * This method builds the inquiry url for user-defined attribute
     * 
     * @return key list
     */
    private static List buildUserDefinedAttributeKeyList() {
        return new ArrayList();
    }

    /**
     * This method defines the user-defined attribute map
     * 
     * @return the user-defined attribute map
     */
    private static Map getUserDefinedAttributeMap() {
        return new HashMap();
    }

    /**
     * This method finds the matching attribute name of given one
     * 
     * @param attributeName the given attribute name
     * @return the attribute name from the given one
     */
    private static String getAttributeName(String attributeName) {
        return attributeName;
    }

    /**
     * This method finds the matching the key value of the given one
     * 
     * @param keyValue the given key value
     * @return the key value from the given key value
     */
    private static Object getKeyValue(Object keyValue) {
        return keyValue;
    }

    /**
     * This method finds the matching the key name of the given one
     * 
     * @param keyName the given key name
     * @return the key value from the given key name
     */
    private static String getKeyName(String keyName) {
        return keyName;
    }

    /**
     * This method defines the lookupable implementation attribute name
     * 
     * @return the lookupable implementation attribute name
     */
    private static String getLookupableImplAttributeName() {
        return Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME;
    }

    /**
     * This method defines the base inquiry url
     * 
     * @return the base inquiry url
     */
    private static String getBaseUrl() {
        return Constants.INQUIRY_ACTION;
    }

    /**
     * This method gets the class name of the inquiry business object
     * 
     * @return the class name of the inquiry business object
     */
    private static Class getInquiryBusinessObjectClass() {
        return AbstractGLInquirableImpl.class;
    }

    /**
     * This method adds more parameters into the curren parameter map
     * 
     * @param parameter the current parameter map
     */
    private static void addMoreParameters(Properties parameter) {
        parameter.put(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, getLookupableImplAttributeName());
    }
}
