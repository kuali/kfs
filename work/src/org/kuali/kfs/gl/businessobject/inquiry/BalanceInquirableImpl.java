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
import org.kuali.PropertyConstants;
import org.kuali.core.bo.BusinessObject;
import org.kuali.core.bo.KualiSystemCode;
import org.kuali.core.inquiry.KualiInquirableImpl;
import org.kuali.core.lookup.KualiLookupableImpl;
import org.kuali.core.service.BusinessObjectDictionaryService;
import org.kuali.core.service.LookupService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.util.UrlFactory;
import org.kuali.module.gl.bo.Entry;
import org.kuali.module.gl.util.BusinessObjectFieldConverter;
import org.kuali.module.gl.web.Constant;

/**
 * This class...
 * 
 * @author Bin Gao from Michigan State University
 */
public class BalanceInquirableImpl extends KualiInquirableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquirableImpl.class);

    private BusinessObjectDictionaryService dataDictionary;
    private LookupService lookupService;
    private Class businessObjectClass;

    /**
     * Helper method to build an inquiry url for a result field.
     * 
     * @param businessObject the business object instance to build the urls for
     * @param attributeName the property which links to an inquirable
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
        if (isUserDefinedAttribute || attributeName.equals(businessDictionary.getTitleAttribute(businessObject.getClass()))) {
            inquiryBusinessObjectClass = (new Entry()).getClass();
            isPkReference = true;
        }
        else if (isUserDefinedAttribute || attributeName.equals(businessDictionary.getTitleAttribute(businessObject.getClass()))) {
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

            keys = buildUserDefinedAttributeKeyList(attributeName);
            baseUrl = Constants.GL_MODIFIED_INQUIRY_ACTION;

            parameters.put(Constants.RETURN_LOCATION_PARAMETER, Constant.RETURN_LOCATION_VALUE);
            parameters.put(Constants.GL_BALANCE_INQUIRY_FLAG, "true");
            parameters.put(Constants.DISPATCH_REQUEST_PARAMETER, "search");
            parameters.put(Constants.DOC_FORM_KEY, "88888888");
            parameters.put(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME, Constant.GL_LOOKUPABLE_ENTRY);

            String periodCode = (String) userDefinedAttributeMap.get(attributeName);
            parameters.put(Constants.UNIVERSITY_FISCAL_PERIOD_CODE_PROPERTY_NAME, periodCode);
        }
        else if (persistenceStructureService.isPersistable(inquiryBusinessObjectClass)) {
            keys = persistenceStructureService.listPrimaryKeyFieldNames(inquiryBusinessObjectClass);
        }

        // build key value url parameters used to retrieve the business object
        for (Iterator keyIterator = keys.iterator(); keyIterator.hasNext();) {
            String keyName = (String) keyIterator.next();
            String keyConversion = keyName;

            if (ObjectUtils.isNestedAttribute(attributeName)) {
                keyConversion = ObjectUtils.getNestedAttributePrefix(attributeName) + "." + keyName;
            }
            else {
                if (isPkReference) {
                    keyConversion = keyName;
                }
                else {
                    keyConversion = persistenceStructureService.getForeignKeyFieldName(businessObject.getClass(), attributeRefName,
                            keyName);
                }
            }

            Object keyValue = ObjectUtils.getPropertyValue(businessObject, keyConversion);
            keyValue = (keyValue == null) ? "" : keyValue.toString();

            if(isExclusiveField(keyName, keyValue)){
                keyValue = "";
            }

            keyName = BusinessObjectFieldConverter.convertToTransactionPropertyName(keyName);
            parameters.put(keyName, keyValue);
        }
        return UrlFactory.paremeterizeUrl(baseUrl, parameters);
    }

    /**
     * This method builds the inquiry url for user-defined attribute
     * 
     * @param attributeName
     * @return key list
     */
    private static List buildUserDefinedAttributeKeyList(String attributeName) {
        List keys = new ArrayList();

        keys.add(PropertyConstants.UNIVERSITY_FISCAL_YEAR);
        keys.add(PropertyConstants.ACCOUNT_NUMBER);
        keys.add(PropertyConstants.CHART_OF_ACCOUNTS_CODE);
        keys.add(PropertyConstants.BALANCE_TYPE_CODE);
        keys.add(PropertyConstants.SUB_ACCOUNT_NUMBER);
        keys.add(PropertyConstants.OBJECT_CODE);
        keys.add(PropertyConstants.SUB_OBJECT_CODE);
        keys.add(PropertyConstants.OBJECT_TYPE_CODE);

        return keys;
    }

    private static Map getUserDefinedAttributeMap() {
        Map userDefinedAttributeMap = new HashMap();

        userDefinedAttributeMap.put(PropertyConstants.MONTH1_AMOUNT, "01");
        userDefinedAttributeMap.put(PropertyConstants.MONTH2_AMOUNT, "02");
        userDefinedAttributeMap.put(PropertyConstants.MONTH3_AMOUNT, "03");

        userDefinedAttributeMap.put(PropertyConstants.MONTH4_AMOUNT, "04");
        userDefinedAttributeMap.put(PropertyConstants.MONTH5_AMOUNT, "05");
        userDefinedAttributeMap.put(PropertyConstants.MONTH6_AMOUNT, "06");

        userDefinedAttributeMap.put(PropertyConstants.MONTH7_AMOUNT, "07");
        userDefinedAttributeMap.put(PropertyConstants.MONTH8_AMOUNT, "08");
        userDefinedAttributeMap.put(PropertyConstants.MONTH9_AMOUNT, "09");

        userDefinedAttributeMap.put(PropertyConstants.MONTH10_AMOUNT, "10");
        userDefinedAttributeMap.put(PropertyConstants.MONTH11_AMOUNT, "11");
        userDefinedAttributeMap.put(PropertyConstants.MONTH12_AMOUNT, "12");

        return userDefinedAttributeMap;
    }

    /**
     * This method determines whether the input name-value pair is exclusive from the processing
     * @param keyName the name of the name-value pair
     * @param keyValue the value of the name-value pair
     * @return true if the input key is in the exclusive list; otherwise, false
     */
    private static boolean isExclusiveField(Object keyName, Object keyValue) {
        
        if (keyName != null && keyValue != null) {
            if (keyName.equals(PropertyConstants.SUB_ACCOUNT_NUMBER) 
                    && keyValue.equals(Constant.CONSOLIDATED_SUB_ACCOUNT_NUMBER)) {
                return true;
            }
            else if (keyName.equals(PropertyConstants.SUB_OBJECT_CODE) 
                    && keyValue.equals(Constant.CONSOLIDATED_SUB_OBJECT_CODE)) {
                return true;
            }
            else if (keyName.equals(PropertyConstants.OBJECT_TYPE_CODE) 
                    && keyValue.equals(Constant.CONSOLIDATED_OBJECT_TYPE_CODE)) {
                return true;
            }
        }
        return false;
    }
}