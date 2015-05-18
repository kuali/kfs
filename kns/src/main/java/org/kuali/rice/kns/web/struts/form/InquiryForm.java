/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.form;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts.upload.FormFile;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.ConfigurationException;
import org.kuali.rice.kns.authorization.AuthorizationConstants;
import org.kuali.rice.core.api.encryption.EncryptionService;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.document.MaintenanceDocumentBase;
import org.kuali.rice.kns.inquiry.Inquirable;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.krad.bo.Exporter;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.exception.UnknownBusinessClassAttributeException;
import org.kuali.rice.krad.datadictionary.exception.UnknownDocumentTypeException;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.service.ModuleService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

/**
 * This class is the action form for inquiries.
 */
public class InquiryForm extends KualiForm {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(InquiryForm.class);

    private static final long serialVersionUID = 1L;
    private String fieldConversions;
    private List sections;
    private String businessObjectClassName;
    private Map editingMode;
    private String formKey;
    private boolean canExport;

    @Override
    public void addRequiredNonEditableProperties(){
    	super.addRequiredNonEditableProperties();
    	registerRequiredNonEditableProperty(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE);
    	registerRequiredNonEditableProperty(KRADConstants.DISPATCH_REQUEST_PARAMETER);
    	registerRequiredNonEditableProperty(KRADConstants.DOC_FORM_KEY);
    	registerRequiredNonEditableProperty(KRADConstants.FORM_KEY);
    	registerRequiredNonEditableProperty(KRADConstants.FIELDS_CONVERSION_PARAMETER);
    	registerRequiredNonEditableProperty(KRADConstants.BACK_LOCATION);
    }

    /**
     * The following map is used to pass primary key values between invocations of the inquiry screens after the start method has been called.  Values in this map will remain encrypted
     * if the value was passed in as encrypted
     */
    private Map<String, String> inquiryPrimaryKeys;

    private Map<String, String> inquiryDecryptedPrimaryKeys;

    /**
     * A map of collection name -> Boolean mappings.  Used to denote whether a collection name is configured to show inactive records.
     */
    private Map<String, Boolean> inactiveRecordDisplay;

    private Inquirable inquirable;

    public InquiryForm() {
        super();
        this.editingMode = new HashMap();
        this.editingMode.put(AuthorizationConstants.EditMode.VIEW_ONLY, "TRUE");
        this.inactiveRecordDisplay = null;
    }

    @Override
    public void populate(HttpServletRequest request) {
    // set to null for security reasons (so POJO form base can't access it), then we'll make an new instance of it after
    // POJO form base is done
        this.inquirable = null;
        super.populate(request);
        if (request.getParameter("returnLocation") != null) {
            setBackLocation(request.getParameter("returnLocation"));
        }
        if (request.getParameter(KRADConstants.DOC_FORM_KEY) != null) {
            setFormKey(request.getParameter(KRADConstants.DOC_FORM_KEY));
        }
        //if the action is download attachment then skip the following populate logic
        if(!KRADConstants.DOWNLOAD_BO_ATTACHMENT_METHOD.equals(getMethodToCall())){
        	inquirable = getInquirable(getBusinessObjectClassName());

        	// the following variable is true if the method to call is not start, meaning that we already called start
        	boolean passedFromPreviousInquiry = !KRADConstants.START_METHOD.equals(getMethodToCall()) && !KRADConstants.CONTINUE_WITH_INQUIRY_METHOD_TO_CALL.equals(getMethodToCall()) && !KRADConstants
                    .DOWNLOAD_CUSTOM_BO_ATTACHMENT_METHOD.equals(getMethodToCall());

        	// There is no requirement that an inquiry screen must display the primary key values.  However, when clicking on hide/show (without javascript) and
        	// hide/show inactive, the PK values are needed to allow the server to properly render results after the user clicks on a hide/show button that results
        	// in server processing.  This line will populate the form with the PK values so that they may be used in subsequent requests.  Note that encrypted
        	// values will remain encrypted in this map.
        	this.inquiryPrimaryKeys = new HashMap<String, String>();
        	this.inquiryDecryptedPrimaryKeys = new HashMap<String, String>();

        	populatePKFieldValues(request, getBusinessObjectClassName(), passedFromPreviousInquiry);

        	populateInactiveRecordsInIntoInquirable(inquirable, request);
        	populateExportCapabilities(request, getBusinessObjectClassName());
        }
    }

    protected Inquirable getInquirable(String boClassName) {
        try {
            Class customInquirableClass = null;

            try {
                BusinessObjectEntry entry = (BusinessObjectEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(boClassName);
                customInquirableClass = entry.getInquiryDefinition().getInquirableClass();
            }
            catch (Exception e) {
                LOG.error("Unable to correlate business object class with maintenance document entry");
            }

            Inquirable kualiInquirable = KNSServiceLocator.getKualiInquirable(); // get inquirable impl from Spring

            if (customInquirableClass != null) {
                Class[] defaultConstructor = new Class[] {};
                Constructor cons = customInquirableClass.getConstructor(defaultConstructor);
                kualiInquirable = (Inquirable) cons.newInstance();
            }

            kualiInquirable.setBusinessObjectClass(Class.forName(boClassName));

            return kualiInquirable;
        }
        catch (Exception e) {
            LOG.error("Error attempting to retrieve inquirable.", e);
            throw new RuntimeException("Error attempting to retrieve inquirable.");
        }
    }

    /**
     * Gets the alt keys for a class.  Will not return null but and empty list if no keys exist.
     *
     * @param clazz the class.
     * @return the alt keys
     */
    private List<List<String>> getAltkeys(Class<?> clazz) {
    	final KualiModuleService kualiModuleService = KRADServiceLocatorWeb.getKualiModuleService();
    	final ModuleService moduleService = kualiModuleService.getResponsibleModuleService(clazz);

        List<List<String>> altKeys = null;
        if (moduleService != null) {
        	altKeys = moduleService.listAlternatePrimaryKeyFieldNames(clazz);
        }

        return altKeys != null ? altKeys : new ArrayList<List<String>>();
    }

    protected void populatePKFieldValues(HttpServletRequest request, String boClassName, boolean passedFromPreviousInquiry) {
        try {
            EncryptionService encryptionService = CoreApiServiceLocator.getEncryptionService();
            DataDictionaryService dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
            BusinessObjectAuthorizationService businessObjectAuthorizationService = KNSServiceLocator
                    .getBusinessObjectAuthorizationService();
            BusinessObjectMetaDataService businessObjectMetaDataService = KNSServiceLocator
                    .getBusinessObjectMetaDataService();

            Class businessObjectClass = Class.forName(boClassName);

            // build list of key values from request, if all keys not given throw error
            List<String> boPKeys = businessObjectMetaDataService.listPrimaryKeyFieldNames(businessObjectClass);
            final List<List<String>> altKeys = this.getAltkeys(businessObjectClass);

            altKeys.add(boPKeys);
            boolean bFound = false;
            for(List<String> boKeys : altKeys ){
            	if(bFound)
            		break;
	            int keyCount = boKeys.size();
	            int foundCount = 0;
                for (String boKey : boKeys) {
                    String pkParamName = boKey;
	                if (passedFromPreviousInquiry) {
	                    pkParamName = KRADConstants.INQUIRY_PK_VALUE_PASSED_FROM_PREVIOUS_REQUEST_PREFIX + pkParamName;
	                }

	                if (request.getParameter(pkParamName) != null) {
	                	foundCount++;
	                	String parameter = request.getParameter(pkParamName);
                        
                        Boolean forceUppercase = Boolean.FALSE;
                        try {
                            forceUppercase = dataDictionaryService.getAttributeForceUppercase(businessObjectClass, boKey);
                        } catch (UnknownBusinessClassAttributeException ex) {
                            // swallowing exception because this check for ForceUppercase would
                            // require a DD entry for the attribute.  it is only checking keys
                            // so most likely there should be an entry.
                            LOG.warn("BO class " + businessObjectClassName + " property " + boKey + " should probably have a DD definition.", ex);
                        }
                        String parameterCopy = parameter;
                        if (forceUppercase) {
	                		parameter = parameter.toUpperCase();
	                	}

                        inquiryPrimaryKeys.put(boKey, parameter);
                        if (businessObjectAuthorizationService.attributeValueNeedsToBeEncryptedOnFormsAndLinks(businessObjectClass, boKey)) {
                            try {
                                if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                                    inquiryDecryptedPrimaryKeys.put(boKey, encryptionService.decrypt(parameterCopy));
                                }
                            } catch (GeneralSecurityException e) {
                                LOG.error("BO class " + businessObjectClassName + " property " + boKey + " should have been encrypted, but there was a problem decrypting it.");
                                throw e;
                            }
                        }
                        else {
                            inquiryDecryptedPrimaryKeys.put(boKey, parameter);
                        }
                    }
                }
                if (foundCount == keyCount) {
                    bFound = true;
                }
            }
            if(!bFound){
                LOG.error("All keys not given to lookup for bo class name " + businessObjectClass.getName());
                throw new RuntimeException("All keys not given to lookup for bo class name " + businessObjectClass.getName());
            }
        }
        catch (ClassNotFoundException e) {
        	LOG.error("Can't instantiate class: " + boClassName, e);
        	throw new RuntimeException("Can't instantiate class: " + boClassName);
        }
        catch (GeneralSecurityException e) {
        	LOG.error("Can't decrypt value", e);
        	throw new RuntimeException("Can't decrypt value");
        }
    }

    /**
     * Examines the BusinessObject's data dictionary entry to determine if it supports
     * XML export or not and set's canExport appropriately.
     */
    protected void populateExportCapabilities(HttpServletRequest request, String boClassName) {
    	setCanExport(false);
    	BusinessObjectEntry businessObjectEntry = (BusinessObjectEntry) KRADServiceLocatorWeb.getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(boClassName);
    	Class<? extends Exporter> exporterClass = businessObjectEntry.getExporterClass();
    	if (exporterClass != null) {
    		try {
    			Exporter exporter = exporterClass.newInstance();
    			if (exporter.getSupportedFormats(businessObjectEntry.getBusinessObjectClass()).contains(KRADConstants.XML_FORMAT)) {
    				setCanExport(true);
    			}
    		} catch (Exception e) {
    			LOG.error("Failed to locate or create exporter class: " + exporterClass, e);
    			throw new RuntimeException("Failed to locate or create exporter class: " + exporterClass);
    		}
    	}
    }


    /**
     * @return Returns the fieldConversions.
     */
    public String getFieldConversions() {
        return fieldConversions;
    }


    /**
     * @param fieldConversions The fieldConversions to set.
     */
    public void setFieldConversions(String fieldConversions) {
        this.fieldConversions = fieldConversions;
    }


    /**
     * @return Returns the inquiry sections.
     */
    public List getSections() {
        return sections;
    }


    /**
     * @param sections The sections to set.
     */
    public void setSections(List sections) {
        this.sections = sections;
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

    public Map getEditingMode() {
        return editingMode;
    }

    /**
     * Gets the map used to pass primary key values between invocations of the inquiry screens after the start method has been called.  All field values that were passed in encrypted will
     * be encrypted in this map
     *
     * @return
     */
    public Map<String, String> getInquiryPrimaryKeys() {
        return this.inquiryPrimaryKeys;
    }

    /**
     * Gets the map used to pass primary key values between invocations of the inquiry screens after the start method has been called.  All fields will be decrypted
     *
     * Purposely not named as a getter, to make it harder for POJOFormBase to access it
     *
     * @return
     */
    public Map<String, String> retrieveInquiryDecryptedPrimaryKeys() {
        return this.inquiryDecryptedPrimaryKeys;
    }

    /**
     * Sets the map used to pass primary key values between invocations of the inquiry screens after the start method has been called.
     *
     * @param inquiryPrimaryKeys
     */
    public void setInquiryPrimaryKeys(Map<String, String> inquiryPrimaryKeys) {
        this.inquiryPrimaryKeys = inquiryPrimaryKeys;
    }

    /**
     * Gets map of collection name -> Boolean mappings.  Used to denote whether a collection name is configured to show inactive records.
     *
     * @return
     */
    public Map<String, Boolean> getInactiveRecordDisplay() {
        return getInquirable().getInactiveRecordDisplay();
    }

    public Inquirable getInquirable() {
        return inquirable;
    }

    protected void populateInactiveRecordsInIntoInquirable(Inquirable inquirable, HttpServletRequest request) {
        for (Enumeration e = request.getParameterNames(); e.hasMoreElements();) {
            String paramName = (String) e.nextElement();
            if (paramName.startsWith(KRADConstants.INACTIVE_RECORD_DISPLAY_PARAM_PREFIX)) {
                String collectionName = StringUtils.substringAfter(paramName, KRADConstants.INACTIVE_RECORD_DISPLAY_PARAM_PREFIX);
                Boolean showInactive = Boolean.parseBoolean(request.getParameter(paramName));
                inquirable.setShowInactiveRecords(collectionName, showInactive);
            }
        }
    }

    public String getFormKey() {
        return this.formKey;
    }

    public void setFormKey(String formKey) {
        this.formKey = formKey;
    }

	/**
	 * Returns true if this Inquiry supports XML export of the BusinessObject.
	 */
	public boolean isCanExport() {
		return this.canExport;
	}

	/**
	 * Sets whether or not this Inquiry supports XML export of it's BusinessObject.
	 */
	public void setCanExport(boolean canExport) {
		this.canExport = canExport;
	}


}
