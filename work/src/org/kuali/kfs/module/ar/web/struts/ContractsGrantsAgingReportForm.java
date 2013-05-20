/*
 * Copyright 2006-2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.web.struts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.lookup.LookupableSpringContext;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.lookup.LookupUtils;
import org.kuali.rice.kns.lookup.Lookupable;
import org.kuali.rice.krad.service.BusinessObjectDictionaryService;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;


/**
 * This class is the action form for Contracts and Grants Aging Reports.
 */
public class ContractsGrantsAgingReportForm extends LookupForm {
    private static final long serialVersionUID = 1L;

    private static final org.apache.commons.logging.Log LOG = org.apache.commons.logging.LogFactory.getLog(ContractsGrantsAgingReportForm.class);
    private static final String AWARD_INQUIRY_TITLE_PROPERTY = "message.inquiry.award.title";
    private String awardInquiryTitle;
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

    private String total0to30;
    private String total31to60;
    private String total61to90;
    private String total91toSYSPR;
    private String totalSYSPRplus1orMore;
    private String totalOpenInvoices;
    private String totalCredits;
    private String totalWriteOffs;

    private final String userLookupRoleNamespaceCode = KFSConstants.OptionalModuleNamespaces.ACCOUNTS_RECEIVABLE;
    private final String userLookupRoleName = KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR;

    /**
     * @see org.kuali.rice.kns.web.struts.form.KualiForm#getExtraButtons()
     */
    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> buttons = new ArrayList<ExtraButton>();

        // Print button
        ExtraButton printButton = new ExtraButton();
        printButton.setExtraButtonProperty("methodToCall.print");
        printButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_genprintfile.gif");
        printButton.setExtraButtonAltText("Print");
        buttons.add(printButton);

        // Export button
        ExtraButton exportButton = new ExtraButton();
        exportButton.setExtraButtonProperty("methodToCall.export");
        exportButton.setExtraButtonSource("${" + KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_export.gif");
        exportButton.setExtraButtonAltText("Export");
        exportButton.setExtraButtonOnclick("excludeSubmitRestriction=true");
        buttons.add(exportButton);
        return buttons;
    }

    /**
     * Picks out business object name from the request to get retrieve a lookupable and set properties.
     * 
     * @see org.kuali.rice.kns.web.struts.form.LookupForm#populate(javax.servlet.http.HttpServletRequest)
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
                if (ObjectUtils.isNull(lookupImplID)) {
                    lookupImplID = "lookupable";
                }

                setLookupableImplServiceName(lookupImplID);
            }
            setLookupable(LookupableSpringContext.getLookupable(getLookupableImplServiceName()));

            if (ObjectUtils.isNull(getLookupable())) {
                LOG.error("Lookup impl not found for lookup impl name " + getLookupableImplServiceName());
                throw new RuntimeException("Lookup impl not found for lookup impl name " + getLookupableImplServiceName());
            }

            if (ObjectUtils.isNotNull(request.getParameter(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME))) {
                setLookupableImplServiceName(request.getParameter(KFSConstants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME));
            }

            // check the doc form key is empty before setting so we don't override a restored lookup form
            if (ObjectUtils.isNotNull(request.getAttribute(KFSConstants.DOC_FORM_KEY)) && StringUtils.isBlank(this.getFormKey())) {
                setFormKey((String) request.getAttribute(KFSConstants.DOC_FORM_KEY));
            }
            else if (ObjectUtils.isNotNull(request.getParameter(KFSConstants.DOC_FORM_KEY)) && StringUtils.isBlank(this.getFormKey())) {
                setFormKey(request.getParameter(KFSConstants.DOC_FORM_KEY));
            }

            if (ObjectUtils.isNotNull(request.getParameter(KFSConstants.RETURN_LOCATION_PARAMETER))) {
                setBackLocation(request.getParameter(KFSConstants.RETURN_LOCATION_PARAMETER));
            }
            if (ObjectUtils.isNotNull(request.getParameter(KFSConstants.CONVERSION_FIELDS_PARAMETER))) {
                setConversionFields(request.getParameter(KFSConstants.CONVERSION_FIELDS_PARAMETER));
            }

            // init lookupable with bo class
            getLookupable().setBusinessObjectClass(Class.forName(getBusinessObjectClassName()));
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
                    if (ObjectUtils.isNotNull(formFields) && formFields.containsKey(field.getPropertyName())) {
                        field.setPropertyValue(formFields.get(field.getPropertyName()));
                    }
                    // override values with request
                    if (ObjectUtils.isNotNull(request.getParameter(field.getPropertyName()))) {
                        field.setPropertyValue(request.getParameter(field.getPropertyName()));
                    }
                    // force uppercase if necessary
                    field.setPropertyValue(LookupUtils.forceUppercase(boClass, field.getPropertyName(), field.getPropertyValue()));
                    fieldValues.put(field.getPropertyName(), field.getPropertyValue());
                }
            }
            if (getLookupable().checkForAdditionalFields(fieldValues)) {
                for (Iterator iter = getLookupable().getRows().iterator(); iter.hasNext();) {
                    Row row = (Row) iter.next();
                    for (Iterator iterator = row.getFields().iterator(); iterator.hasNext();) {
                        Field field = (Field) iterator.next();
                        // check whether form already has value for field
                        if (ObjectUtils.isNotNull(formFields) && formFields.containsKey(field.getPropertyName())) {
                            field.setPropertyValue(formFields.get(field.getPropertyName()));
                            LOG.info("\n\n\n\n");
                            LOG.info("field " + field.toString() + " = " + field.getPropertyValue() + " ***\n\n");
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
     * Gets the awardInquiryTitle attribute.
     * 
     * @return Returns the awardInquiryTitle.
     */
    public String getAwardInquiryTitle() {
        return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(AWARD_INQUIRY_TITLE_PROPERTY);
    }

    /**
     * Sets the awardInquiryTitle attribute value.
     * 
     * @param awardInquiryTitle The awardInquiryTitle to set.
     */
    public void setAwardInquiryTitle(String awardInquiryTitle) {
        this.awardInquiryTitle = awardInquiryTitle;
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

    /**
     * Gets the total0to30 attribute.
     * 
     * @return Returns the total0to30.
     */
    public String getTotal0to30() {
        return total0to30;
    }

    /**
     * Sets the total0to30 attribute value.
     * 
     * @param total0to30 The total0to30 to set.
     */
    public void setTotal0to30(String total0to30) {
        this.total0to30 = total0to30;
    }

    /**
     * Gets the total31to60 attribute.
     * 
     * @return Returns the total31to60.
     */
    public String getTotal31to60() {
        return total31to60;
    }

    /**
     * Sets the total31to60 attribute value.
     * 
     * @param total31to60 The total31to60 to set.
     */
    public void setTotal31to60(String total31to60) {
        this.total31to60 = total31to60;
    }

    /**
     * Gets the total61to90 attribute.
     * 
     * @return Returns the total61to90.
     */
    public String getTotal61to90() {
        return total61to90;
    }

    /**
     * Sets the total61to90 attribute value.
     * 
     * @param total61to90 The total61to90 to set.
     */
    public void setTotal61to90(String total61to90) {
        this.total61to90 = total61to90;
    }

    /**
     * Gets the total91toSYSPR attribute.
     * 
     * @return Returns the total91toSYSPR.
     */
    public String getTotal91toSYSPR() {
        return total91toSYSPR;
    }

    /**
     * Sets the total91toSYSPR attribute value.
     * 
     * @param total91toSYSPR The total91toSYSPR to set.
     */
    public void setTotal91toSYSPR(String total91toSYSPR) {
        this.total91toSYSPR = total91toSYSPR;
    }

    /**
     * Gets the totalSYSPRplus1orMore attribute.
     * 
     * @return Returns the totalSYSPRplus1orMore.
     */
    public String getTotalSYSPRplus1orMore() {
        return totalSYSPRplus1orMore;
    }

    /**
     * Sets the totalSYSPRplus1orMore attribute value.
     * 
     * @param totalSYSPRplus1orMore The totalSYSPRplus1orMore to set.
     */
    public void setTotalSYSPRplus1orMore(String totalSYSPRplus1orMore) {
        this.totalSYSPRplus1orMore = totalSYSPRplus1orMore;
    }

    /**
     * Gets the totalOpenInvoices attribute.
     * 
     * @return Returns the totalOpenInvoices.
     */
    public String getTotalOpenInvoices() {
        return totalOpenInvoices;
    }

    /**
     * Sets the totalOpenInvoices attribute value.
     * 
     * @param totalOpenInvoices The totalOpenInvoices to set.
     */
    public void setTotalOpenInvoices(String totalOpenInvoices) {
        this.totalOpenInvoices = totalOpenInvoices;
    }

    /**
     * Gets the totalCredits attribute.
     * 
     * @return Returns the totalCredits.
     */
    public String getTotalCredits() {
        return totalCredits;
    }

    /**
     * Sets the totalCredits attribute value.
     * 
     * @param totalCredits The totalCredits to set.
     */
    public void setTotalCredits(String totalCredits) {
        this.totalCredits = totalCredits;
    }

    /**
     * Gets the totalWriteOffs attribute.
     * 
     * @return Returns the totalWriteOffs.
     */
    public String getTotalWriteOffs() {
        return totalWriteOffs;
    }

    /**
     * Sets the totalWriteOffs attribute value.
     * 
     * @param totalWriteOffs The totalWriteOffs to set.
     */
    public void setTotalWriteOffs(String totalWriteOffs) {
        this.totalWriteOffs = totalWriteOffs;
    }

    /**
     * Gets the userLookupRoleNamespaceCode attribute.
     * 
     * @return Returns the userLookupRoleNamespaceCode.
     */
    public String getUserLookupRoleNamespaceCode() {
        return userLookupRoleNamespaceCode;
    }

    /**
     * Gets the userLookupRoleName attribute.
     * 
     * @return Returns the userLookupRoleName.
     */
    public String getUserLookupRoleName() {
        return userLookupRoleName;
    }

}
