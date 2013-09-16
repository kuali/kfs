/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.integration.ld.businessobject.inquiry;


import java.sql.Date;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.kuali.kfs.gl.Constant;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.kfs.sys.businessobject.inquiry.KfsInquirableImpl;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kns.lookup.HtmlData;
import org.kuali.rice.kns.lookup.HtmlData.AnchorHtmlData;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;

/**
 * Main Abstract Labor Inquirable parent class that sets up the inquirable hierarchy that so that
 * labor inquiry logic can be used in labor and in other optional modules (ex: EC).
 */
public abstract class AbstractLaborIntegrationInquirableImpl extends KfsInquirableImpl {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AbstractLaborIntegrationInquirableImpl.class);

    public AbstractLaborIntegrationInquirableImpl() {
        super();
    }

    public HtmlData getInquiryUrl(BusinessObject businessObject, String attributeName) {
        BusinessObjectDictionaryService businessDictionary = SpringContext.getBean(BusinessObjectDictionaryService.class);
        PersistenceStructureService persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);

        HtmlData inquiryHref = new AnchorHtmlData(Constant.EMPTY_STRING, Constant.EMPTY_STRING);
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
            return inquiryHref;
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
                return inquiryHref;
            }

            if (inquiryBusinessObjectClass == null || businessDictionary.isInquirable(inquiryBusinessObjectClass) == null || !businessDictionary.isInquirable(inquiryBusinessObjectClass).booleanValue()) {
                return inquiryHref;
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
        }
        else if (persistenceStructureService.isPersistable(inquiryBusinessObjectClass)) {
            keys = persistenceStructureService.listPrimaryKeyFieldNames(inquiryBusinessObjectClass);
        }

        // build key value url parameters used to retrieve the business object
        Map<String,String> inquiryFields = new HashMap<String,String>();
        if (keys != null) {
            StringBuffer title = new StringBuffer(Constant.EMPTY_STRING);
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
                if(ObjectUtil.getSimpleTypeName(businessObject, keyConversion).equals(Date.class.getSimpleName())) {
                    Date date = (Date)ObjectUtil.valueOf(Date.class.getSimpleName(), keyValue.toString());

                    DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);
                    keyValue = dateTimeService.toDateString(new java.util.Date(date.getTime()));
                }

                keyValue = (keyValue == null) ? Constant.EMPTY_STRING : keyValue.toString();

                // convert the key value and name into the given ones
                Object tempKeyValue = this.getKeyValue(keyName, keyValue);
                keyValue = tempKeyValue == null ? keyValue : tempKeyValue;

                String tempKeyName = this.getKeyName(keyName);
                keyName = tempKeyName == null ? keyName : tempKeyName;

                // add the key-value pair into the parameter map
                if (keyName != null){
                    parameters.put(keyName, keyValue);
                    inquiryFields.put(keyName, keyValue.toString());
                }
            }
        }

        // add more customized parameters into the current parameter map
        if (isUserDefinedAttribute) {
            addMoreParameters(parameters, attributeName);
        }
        return getHyperLink(inquiryBusinessObjectClass, inquiryFields, UrlFactory.parameterizeUrl(baseUrl, parameters));
    }

    protected abstract List buildUserDefinedAttributeKeyList();

    protected abstract Map getUserDefinedAttributeMap();

    protected abstract String getAttributeName(String attributeName);

    protected abstract Object getKeyValue(String keyName, Object keyValue);

    protected abstract String getKeyName(String keyName);

    protected abstract String getLookupableImplAttributeName();

    protected abstract String getBaseUrl();

    protected abstract Class getInquiryBusinessObjectClass(String attributeName);

    protected void addMoreParameters(Properties parameter, String attributeName) {
        return;
    }

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
            else if (keyName.equals(KFSPropertyConstants.POSITION_NUMBER) && keyValue.equals(getPositionNumberKeyValue())) {
                return true;
            }
            else if (keyName.equals(KFSPropertyConstants.FINANCIAL_BALANCE_TYPE_CODE) && keyValue.equals(getFinancialBalanceTypeCodeKeyValue())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Overridden by sub-classes to return the PositionNumber keyValue used by isExclusiveFieldToBeALink
     *
     * @return the PositionNumber keyValue
     */
    protected abstract String getPositionNumberKeyValue();

    /**
     * Overridden by sub-classes to return the FinancialBalanceTypeCode keyValue used by isExclusiveFieldToBeALink
     *
     * @return the FinancialBalanceTypeCode keyValue
     */
    protected abstract String getFinancialBalanceTypeCodeKeyValue();


}