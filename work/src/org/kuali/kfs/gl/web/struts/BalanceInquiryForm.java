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
package org.kuali.kfs.gl.web.struts;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.businessobject.Entry;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.krad.bo.BusinessObject;

/**
 * This class is the action form for balance inquiries.
 */
public class BalanceInquiryForm extends LookupForm {
    private static final long serialVersionUID = 1L;

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(BalanceInquiryForm.class);

    private String formKey;
    private String backLocation;
    private Map fields;
    private String lookupableImplServiceName;
    private String conversionFields;
    private Map fieldConversions;
    private String businessObjectClassName;
    private Lookupable lookupable;
    private Lookupable pendingEntryLookupable;
    private boolean hideReturnLink = false;


    /**
     * Picks out business object name from the request to get retrieve a lookupable and set properties.
     * 
     * @see org.kuali.rice.kns.web.struts.form.LookupForm#populate(javax.servlet.http.HttpServletRequest)
     *      
     * KRAD Conversion: Lookupable performs customization of the fields and check for additional fields.
     *  
     * Fields are in data dictionary for bo Balance.
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);

        try {
            if (StringUtils.isBlank(request.getParameter(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME)) && StringUtils.isBlank(getLookupableImplServiceName())) {

                // get the business object class for the lookup
                String localBusinessObjectClassName = request.getParameter(KFSConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE);
                setBusinessObjectClassName(localBusinessObjectClassName);

                if (StringUtils.isBlank(localBusinessObjectClassName)) {
                    LOG.error("Business object class not passed to lookup.");
                    throw new RuntimeException("Business object class not passed to lookup.");
                }

                // call data dictionary service to get lookup impl for bo class
                String lookupImplID = SpringContext.getBean(BusinessObjectDictionaryService.class).getLookupableID(Class.forName(localBusinessObjectClassName));
                if (lookupImplID == null) {
                    lookupImplID = "lookupable";
                }

                setLookupableImplServiceName(lookupImplID);
            }
            setLookupable(LookupableSpringContext.getLookupable(getLookupableImplServiceName()));

            if (getLookupable() == null) {
                LOG.error("Lookup impl not found for lookup impl name " + getLookupableImplServiceName());
                throw new RuntimeException("Lookup impl not found for lookup impl name " + getLookupableImplServiceName());
            }

            // (laran) I put this here to allow the Exception to be thrown if the localLookupable is null.
            if (Entry.class.getName().equals(getBusinessObjectClassName())) {
                setPendingEntryLookupable(LookupableSpringContext.getLookupable(GeneralLedgerConstants.LookupableBeanKeys.PENDING_ENTRY));
            }

            if (request.getParameter(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME) != null) {
                setLookupableImplServiceName(request.getParameter(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME));
            }

            // check the doc form key is empty before setting so we don't override a restored lookup form
            if (request.getAttribute(KFSConstants.DOC_FORM_KEY) != null && StringUtils.isBlank(this.getFormKey())) {
                setFormKey((String) request.getAttribute(KFSConstants.DOC_FORM_KEY));
            }
            else if (request.getParameter(KFSConstants.DOC_FORM_KEY) != null && StringUtils.isBlank(this.getFormKey())) {
                setFormKey(request.getParameter(KFSConstants.DOC_FORM_KEY));
            }

            if (request.getParameter(KFSConstants.RETURN_LOCATION_PARAMETER) != null) {
                setBackLocation(request.getParameter(KFSConstants.RETURN_LOCATION_PARAMETER));
            }
            if (request.getParameter(KFSConstants.CONVERSION_FIELDS_PARAMETER) != null) {
                setConversionFields(request.getParameter(KFSConstants.CONVERSION_FIELDS_PARAMETER));
            }

            // init lookupable with bo class
            getLookupable().setBusinessObjectClass((Class<? extends BusinessObject>)Class.forName(getBusinessObjectClassName()));
            if (null != getPendingEntryLookupable()) {
                getPendingEntryLookupable().setBusinessObjectClass(GeneralLedgerPendingEntry.class);
            }

            Map fieldValues = new HashMap();
            Map formFields = getFields();
            Class boClass = Class.forName(getBusinessObjectClassName());
            for (Iterator iter = getLookupable().getRows().iterator(); iter.hasNext();) {
                Row row = (Row) iter.next();

                for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                    Field field = (Field) iterator.next();

                    // check whether form already has value for field
                    if (formFields != null && formFields.containsKey(field.getPropertyName())) {
                        field.setPropertyValue(formFields.get(field.getPropertyName()));
                    }

                    // override values with request
                    if (request.getParameter(field.getPropertyName()) != null) {
                        field.setPropertyValue(request.getParameter(field.getPropertyName()));
                    }

                    // force uppercase if necessary
                    field.setPropertyValue(org.kuali.rice.krad.lookup.LookupUtils.forceUppercase(boClass, field.getPropertyName(), field.getPropertyValue()));

                    fieldValues.put(field.getPropertyName(), field.getPropertyValue());
                }
            }
            if (getLookupable().checkForAdditionalFields(fieldValues)) {
                for (Iterator iter = getLookupable().getRows().iterator(); iter.hasNext();) {
                    Row row = (Row) iter.next();

                    for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                        Field field = (Field) iterator.next();

                        // check whether form already has value for field
                        if (formFields != null && formFields.containsKey(field.getPropertyName())) {
                            field.setPropertyValue(formFields.get(field.getPropertyName()));
                        }

                        // override values with request
                        if (request.getParameter(field.getPropertyName()) != null) {
                            field.setPropertyValue(request.getParameter(field.getPropertyName()));
                        }
                        fieldValues.put(field.getPropertyName(), field.getPropertyValue());
                    }
                }
            }
            fieldValues.put(KFSConstants.DOC_FORM_KEY, this.getFormKey());
            fieldValues.put(KFSConstants.BACK_LOCATION, this.getBackLocation());

            this.setFields(fieldValues);

            Map fieldConversionMap = new HashMap();
            if (StringUtils.isNotEmpty(this.getConversionFields())) {
                if (this.getConversionFields().indexOf(",") > 0) {
                    StringTokenizer token = new StringTokenizer(this.getConversionFields(), ",");
                    while (token.hasMoreTokens()) {
                        String element = token.nextToken();
                        fieldConversionMap.put(element.substring(0, element.indexOf(":")), element.substring(element.indexOf(":") + 1));
                    }
                }
                else {
                    fieldConversionMap.put(this.getConversionFields().substring(0, this.getConversionFields().indexOf(":")), this.getConversionFields().substring(this.getConversionFields().indexOf(":") + 1));
                }
            }
            setFieldConversions(fieldConversionMap);
            getLookupable().setFieldConversions(fieldConversionMap);
            if (null != getPendingEntryLookupable()) {
                getPendingEntryLookupable().setFieldConversions(fieldConversionMap);
            }
        }
        catch (ClassNotFoundException e) {
            LOG.error("Business Object class " + getBusinessObjectClassName() + " not found");
            throw new RuntimeException("Business Object class " + getBusinessObjectClassName() + " not found", e);
        }
    }

    /**
     * @return Returns the lookupableImplServiceName.
     */
    public String getLookupableImplServiceName() {
        return lookupableImplServiceName;
    }

    /**
     * @param lookupableImplServiceName The lookupableImplServiceName to set.
     */
    public void setLookupableImplServiceName(String lookupableImplServiceName) {
        this.lookupableImplServiceName = lookupableImplServiceName;
    }


    /**
     * @return Returns the backLocation.
     */
    public String getBackLocation() {
        return backLocation;
    }

    /**
     * @param backLocation The backLocation to set.
     */
    public void setBackLocation(String backLocation) {
        this.backLocation = backLocation;
    }

    /**
     * @return Returns the formKey.
     */
    public String getFormKey() {
        return formKey;
    }

    /**
     * @param formKey The formKey to set.
     */
    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

    /**
     * @return Returns the fields.
     */
    public Map getFields() {
        return fields;
    }

    /**
     * @param fields The fields to set.
     */
    public void setFields(Map fields) {
        this.fields = fields;
    }


    /**
     * @return Returns the conversionFields.
     */
    public String getConversionFields() {
        return conversionFields;
    }

    /**
     * @param conversionFields The conversionFields to set.
     */
    public void setConversionFields(String conversionFields) {
        this.conversionFields = conversionFields;
    }

    /**
     * @return Returns the fieldConversions.
     */
    public Map getFieldConversions() {
        return fieldConversions;
    }

    /**
     * @param fieldConversions The fieldConversions to set.
     */
    public void setFieldConversions(Map fieldConversions) {
        this.fieldConversions = fieldConversions;
    }

    /**
     * @return Returns the businessObjectClassName.
     */
    public String getBusinessObjectClassName() {
        return businessObjectClassName;
    }

    /**
     * @param businessObjectClassName The businessObjectClassName to set.
     */
    public void setBusinessObjectClassName(String businessObjectClassName) {
        this.businessObjectClassName = businessObjectClassName;
    }


    /**
     * @return Returns the lookupable.
     */
    public Lookupable getLookupable() {
        return lookupable;
    }


    /**
     * @param lookupable The lookupable to set.
     */
    public void setLookupable(Lookupable lookupable) {
        this.lookupable = lookupable;
    }


    /**
     * @return Returns the hideReturnLink.
     */
    public boolean isHideReturnLink() {
        return hideReturnLink;
    }


    /**
     * @param hideReturnLink The hideReturnLink to set.
     */
    public void setHideReturnLink(boolean hideReturnLink) {
        this.hideReturnLink = hideReturnLink;
    }


    /**
     * @param pendingEntryLookupable
     */
    public void setPendingEntryLookupable(Lookupable pendingEntryLookupable) {
        this.pendingEntryLookupable = pendingEntryLookupable;
    }


    /**
     * @return Returns the pendingEntryLookupable.
     */
    public Lookupable getPendingEntryLookupable() {
        return this.pendingEntryLookupable;
    }
}
