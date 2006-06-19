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
package org.kuali.module.gl.web.struts.form;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.core.lookup.Lookupable;
import org.kuali.core.util.SpringServiceLocator;
import org.kuali.core.web.struts.form.LookupForm;
import org.kuali.core.web.uidraw.Field;
import org.kuali.core.web.uidraw.Row;
import org.kuali.module.gl.bo.GeneralLedgerPendingEntry;

/**
 * This class is the action form for balance inquiries.
 * 
 * @author Kuali Nervous System Team (kualidev@oncourse.iu.edu)
 */
public class BalanceInquiryForm extends LookupForm {
    private static final long serialVersionUID = 1L;

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(BalanceInquiryForm.class);

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
     */
    public void populate(HttpServletRequest request) {
        super.populate(request);

        try {
            Lookupable localLookupable = null;
            Lookupable localPendingEntryLookupable = null;
            
            if (StringUtils.isBlank(request.getParameter(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME)) && StringUtils.isBlank(getLookupableImplServiceName())) {
                
                // get the business object class for the lookup
                String localBusinessObjectClassName = request.getParameter(Constants.BUSINESS_OBJECT_CLASS_ATTRIBUTE);
                setBusinessObjectClassName(localBusinessObjectClassName);
                
                if (StringUtils.isBlank(localBusinessObjectClassName)) {
                    LOG.error("Business object class not passed to lookup.");
                    throw new RuntimeException("Business object class not passed to lookup.");
                }
                
                // call data dictionary service to get lookup impl for bo class
                String lookupImplID = SpringServiceLocator.getBusinessObjectDictionaryService().getLookupableID(Class.forName(localBusinessObjectClassName));
                if (lookupImplID == null) {
                    lookupImplID = "kualiLookupable";
                }

                setLookupableImplServiceName(lookupImplID);
            }
            localLookupable = SpringServiceLocator.getLookupable(getLookupableImplServiceName());
            
            if (localLookupable == null) {
                LOG.error("Lookup impl not found for lookup impl name " + getLookupableImplServiceName());
                throw new RuntimeException("Lookup impl not found for lookup impl name " + getLookupableImplServiceName());
            }

            // (laran) I put this here to allow the Exception to be thrown if the localLookupable is null.
            if("org.kuali.module.gl.bo.Entry".equals(getBusinessObjectClassName())) {
                localPendingEntryLookupable = SpringServiceLocator.getLookupable("glPendingEntryLookupableImpl");
            }

            if (request.getParameter(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME) != null) {
                setLookupableImplServiceName(request.getParameter(Constants.LOOKUPABLE_IMPL_ATTRIBUTE_NAME));
            }

            // check the doc form key is empty before setting so we don't override a restored lookup form
            if (request.getAttribute(Constants.DOC_FORM_KEY) != null && StringUtils.isBlank(this.getFormKey())) {
                setFormKey((String) request.getAttribute(Constants.DOC_FORM_KEY));
            }
            else if (request.getParameter(Constants.DOC_FORM_KEY) != null && StringUtils.isBlank(this.getFormKey())) {
                setFormKey(request.getParameter(Constants.DOC_FORM_KEY));
            }

            if (request.getParameter("returnLocation") != null) {
                setBackLocation(request.getParameter("returnLocation"));
            }
            if (request.getParameter("conversionFields") != null) {
                setConversionFields(request.getParameter("conversionFields"));
            }
            
            // init lookupable with bo class
            localLookupable.setBusinessObjectClass(Class.forName(getBusinessObjectClassName()));
            if(null != localPendingEntryLookupable) {
                localPendingEntryLookupable.setBusinessObjectClass(GeneralLedgerPendingEntry.class);
            }
            
            Map fieldValues = new HashMap();
            Map formFields = getFields();
            for (Iterator iter = localLookupable.getRows().iterator(); iter.hasNext();) {
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
            if (localLookupable.checkForAdditionalFields(fieldValues)) {
                for (Iterator iter = localLookupable.getRows().iterator(); iter.hasNext();) {
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
            fieldValues.put(Constants.DOC_FORM_KEY, this.getFormKey());
            fieldValues.put(Constants.BACK_LOCATION, this.getBackLocation());

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
            localLookupable.setFieldConversions(fieldConversionMap);
            if(null != localPendingEntryLookupable) {
                localPendingEntryLookupable.setFieldConversions(fieldConversionMap);
            }
            setLookupable(localLookupable);
            setPendingEntryLookupable(localPendingEntryLookupable);
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
     * @return Returns the kualiLookupable.
     */
    public Lookupable getLookupable() {
        return lookupable;
    }


    /**
     * @param lookupable The kualiLookupable to set.
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