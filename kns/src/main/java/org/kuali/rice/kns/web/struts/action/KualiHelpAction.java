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
package org.kuali.rice.kns.web.struts.action;

import org.apache.commons.lang.StringUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.kuali.rice.core.api.config.property.ConfigContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.util.RiceConstants;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.coreservice.framework.CoreFrameworkServiceLocator;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.doctype.DocumentType;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.datadictionary.HeaderNavigation;
import org.kuali.rice.kns.datadictionary.KNSDocumentEntry;
import org.kuali.rice.kns.datadictionary.LookupDefinition;
import org.kuali.rice.kns.datadictionary.MaintainableFieldDefinition;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.WebUtils;
import org.kuali.rice.kns.web.struts.form.KualiHelpForm;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionary;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntry;
import org.kuali.rice.krad.datadictionary.HelpDefinition;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.util.KRADConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This class handles requests for help text.
 * 
 * 
 */
public class KualiHelpAction extends KualiAction {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(KualiHelpAction.class);

    private static final String VALIDATION_PATTERN_STRING = "ValidationPattern";
    private static final String NO = "No";
    private static final String YES = "Yes";
    static final String DEFAULT_LOOKUP_HELP_TEXT_RESOURCE_KEY = "lookupHelpText";
    
    private static DataDictionaryService dataDictionaryService;
    private static ConfigurationService kualiConfigurationService;
    private static ParameterService parameterService;
    private static MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;

    private DataDictionaryService getDataDictionaryService() {
        if ( dataDictionaryService == null ) {
            dataDictionaryService = KRADServiceLocatorWeb.getDataDictionaryService();
        }
        return dataDictionaryService;
    }
    private ConfigurationService getConfigurationService() {
        if ( kualiConfigurationService == null ) {
            kualiConfigurationService = KRADServiceLocator.getKualiConfigurationService();
        }
        return kualiConfigurationService;
    }
    private ParameterService getParameterService() {
        if ( parameterService == null ) {
            parameterService = CoreFrameworkServiceLocator.getParameterService();
        }
        return parameterService;
    }

    private MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        if ( maintenanceDocumentDictionaryService == null ) {
            maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
        }
        return maintenanceDocumentDictionaryService;
    }
    
    /**
     * Convenience method for accessing <code>{@link DataDictionaryEntry}</code> for the given business object
     * 
     * @param businessObjectClassName
     * @return DataDictionaryEntry
     */
    private DataDictionaryEntry getDataDictionaryEntry(String businessObjectClassName) {
        return getDataDictionaryService().getDataDictionary().getDictionaryObjectEntry(businessObjectClassName);
    }

    /**
     * Convenience method for accessing the <code>{@link AttributeDefinition}</code> for a specific business object attribute
     * defined in the DataDictionary.
     * 
     * @param businessObjectClassName
     * @param attributeName
     * @return AttributeDefinition
     */
    private AttributeDefinition getAttributeDefinition(String businessObjectClassName, String attributeName) throws ClassNotFoundException {
        AttributeDefinition retval = null;

        if (getDataDictionaryEntry(businessObjectClassName) != null) {
            retval = getDataDictionaryEntry(businessObjectClassName).getAttributeDefinition(attributeName);
        }
        return retval;
    }

    /**
     * @param attribute <code>{@link AttributeDefinition}</code>
     * @return String
     */
    private String getAttributeMaxLength(AttributeDefinition attribute) throws Exception {
        return attribute.getMaxLength().toString();
    }

    /**
     * @param attribute <code>{@link AttributeDefinition}</code>
     * @return String
     */
    private String getAttributeValidationPatternName(AttributeDefinition attribute) throws Exception {
        String retval = new String();
        if (attribute.getValidationPattern() != null) {
            retval = attribute.getValidationPattern().getClass().getName();
        }

        if (retval.indexOf(".") > 0) {
            retval = retval.substring(retval.lastIndexOf(".") + 1);
        }
        if (retval.endsWith(VALIDATION_PATTERN_STRING)) {
            retval = retval.substring(0, retval.lastIndexOf(VALIDATION_PATTERN_STRING));
        }

        return retval;
    }

    /**
     * Retrieves help information from the data dictionary for the business object attribute.
     * 
     * @return ActionForward
     */
    public ActionForward getAttributeHelpText(ActionMapping mapping, KualiHelpForm helpForm, HttpServletRequest request, HttpServletResponse response) throws Exception {

        AttributeDefinition attribute;

        if (StringUtils.isBlank(helpForm.getBusinessObjectClassName()) || StringUtils.isBlank(helpForm.getAttributeName())) {
            throw new RuntimeException("Business object and attribute name not specified.");
        }
        attribute = getAttributeDefinition(helpForm.getBusinessObjectClassName(), helpForm.getAttributeName());

        if ( LOG.isDebugEnabled() ) {
            LOG.debug( "Request for help on: " + helpForm.getBusinessObjectClassName() + " -- " + helpForm.getAttributeName() );
            LOG.debug( "  attribute: " + attribute );
        }
                
        if (attribute == null || StringUtils.isBlank(attribute.getSummary())) {
            helpForm.setResourceKey(RiceKeyConstants.MESSAGE_NO_HELP_TEXT);
            return getResourceHelpText(mapping, helpForm, request, response);
        }

        boolean required = attribute.isRequired().booleanValue();
        // KULRNE-4392 - pull the required attribute on BO maintenance documents from the document def rather than the BO
        try {
            Class boClass = Class.forName( helpForm.getBusinessObjectClassName() );
            String docTypeName = getMaintenanceDocumentDictionaryService().getDocumentTypeName( boClass );
            if (StringUtils.isNotBlank(docTypeName)) {
                // maybe it's not a maint doc
                MaintainableFieldDefinition field = getMaintenanceDocumentDictionaryService().getMaintainableField( docTypeName, helpForm.getAttributeName() );
                if ( field != null ) {
                    required = field.isRequired();
                }
            }
            else {
                if (log.isInfoEnabled()) {
                    log.info("BO class " + boClass.getName() + " does not have a maint doc definition.  Defaulting to using DD for definition");
                }
            }
        } catch ( ClassNotFoundException ex ) {
            // do nothing
            LOG.warn( "Unable to obtain maintainable field for BO property.", ex );
        }
        
        helpForm.setHelpLabel(attribute.getLabel());
        helpForm.setHelpSummary(attribute.getSummary());
        helpForm.setHelpDescription(attribute.getDescription());
        helpForm.setHelpRequired(required?YES:NO);
        helpForm.setHelpMaxLength(getAttributeMaxLength(attribute));
        helpForm.setValidationPatternName(getAttributeValidationPatternName(attribute));

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Retrieves help information from the data dictionary for the business object attribute.
     * 
     * @return ActionForward
     */
    public ActionForward getAttributeHelpText(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        return getAttributeHelpText(mapping, (KualiHelpForm) form, request, response);
    }

    /**
     * Retrieves help information from the data dictionary for the document type.
     */
    public ActionForward getDocumentHelpText(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiHelpForm helpForm = (KualiHelpForm) form;

        String documentTypeName = helpForm.getDocumentTypeName();

        if (StringUtils.isBlank(documentTypeName)) {
            throw new RuntimeException("Document type name not specified.");
        }

        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
        org.kuali.rice.krad.datadictionary.DocumentEntry entry = (org.kuali.rice.krad.datadictionary.DocumentEntry ) dataDictionary.getDocumentEntry(documentTypeName);

        String label = "";
        String summary = "";
        String description = "";
        HelpDefinition helpDefinition = null;
        String apcHelpUrl = null;
        if (entry != null) {
            DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(entry.getDocumentTypeName());
            label = docType.getLabel();
            description = docType.getDescription();
            if (StringUtils.isNotBlank(docType.getHelpDefinitionUrl())) {
            	apcHelpUrl = WebUtils.toAbsoluteURL(ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.EXTERNALIZABLE_HELP_URL_KEY), docType.getHelpDefinitionUrl());
            }
        }

        if ( StringUtils.isNotBlank(apcHelpUrl) ) {
            response.sendRedirect(apcHelpUrl);
            return null;
        }

        helpForm.setHelpLabel(label);
        helpForm.setHelpSummary(summary);
        helpForm.setHelpDescription(description);
        helpForm.setHelpDefinition(helpDefinition);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Retrieves help information from the data dictionary for the document type.
     */
    public ActionForward getBusinessObjectHelpText(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiHelpForm helpForm = (KualiHelpForm) form;

        String objectClassName = helpForm.getBusinessObjectClassName();

        if (StringUtils.isBlank(objectClassName)) {
            throw new RuntimeException("Document type name not specified.");
        }

        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
        BusinessObjectEntry entry = (BusinessObjectEntry) dataDictionary.getBusinessObjectEntry(objectClassName);

        HelpDefinition helpDefinition = null;
        String apcHelpUrl = null;
        String label = "";
        String objectDescription = "";
        if (entry != null) {
            helpDefinition = entry.getHelpDefinition();
            label = entry.getObjectLabel();
            objectDescription = entry.getObjectDescription();
            if (null != helpDefinition && null != helpDefinition.getParameterNamespace() && null != helpDefinition.getParameterDetailType() && null != helpDefinition.getParameterName()) {
                apcHelpUrl = getHelpUrl(helpDefinition.getParameterNamespace(), helpDefinition.getParameterDetailType(), helpDefinition.getParameterName());
                }
                }

        if ( !StringUtils.isBlank(apcHelpUrl) ) {
            response.sendRedirect(apcHelpUrl);
            return null;
        }
        helpForm.setHelpLabel(label);
        helpForm.setHelpDescription(objectDescription);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * Retrieves help information from the data dictionary for the document type.
     */
    public ActionForward getPageHelpText(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiHelpForm helpForm = (KualiHelpForm) form;

        String documentTypeName = helpForm.getDocumentTypeName();
        String pageName = helpForm.getPageName();

        if (StringUtils.isBlank(documentTypeName)) {
            throw new RuntimeException("Document type name not specified.");
        }
        
        if (StringUtils.isBlank(pageName)) {
            throw new RuntimeException("Page name not specified.");
        }

        DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
        KNSDocumentEntry entry = (KNSDocumentEntry) dataDictionary.getDocumentEntry(documentTypeName);

        String apcHelpUrl = null;
        String label = "";
        String objectDescription = "";
        if (entry != null) {
            for ( HeaderNavigation headerNavigation : entry.getHeaderNavigationList() ) {
                if (headerNavigation.getHeaderTabDisplayName().equals(pageName)) {
                    HelpDefinition helpDefinition = headerNavigation.getHelpDefinition();
                    if (null != helpDefinition && null != helpDefinition.getParameterNamespace() && null != helpDefinition.getParameterDetailType() && null != helpDefinition.getParameterName()) {
                        apcHelpUrl = getHelpUrl(helpDefinition.getParameterNamespace(), helpDefinition.getParameterDetailType(), helpDefinition.getParameterName());
                    }
                }
            }
        }

        if ( !StringUtils.isBlank(apcHelpUrl) ) {
            response.sendRedirect(apcHelpUrl);
            return null;
        }
        helpForm.setHelpLabel(pageName);
        helpForm.setHelpDescription("No help content available.");

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * Retrieves help content to link to based on security group/parameter
     */
    public ActionForward getStoredHelpUrl(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiHelpForm helpForm = (KualiHelpForm) form;
        
        String helpParameterNamespace = helpForm.getHelpParameterNamespace();
        String helpParameterDetailType = helpForm.getHelpParameterDetailType();
        String helpParameterName = helpForm.getHelpParameterName();
        
        if (StringUtils.isBlank(helpParameterNamespace)) {
            throw new RuntimeException("Parameter Namespace not specified.");
        }
        
        if (StringUtils.isBlank(helpParameterDetailType)) {
            throw new RuntimeException("Detail Type not specified.");
        }

        if (StringUtils.isBlank(helpParameterName)) {
            throw new RuntimeException("Parameter Name not specified.");
        }
        
        String apcHelpUrl = getHelpUrl(helpParameterNamespace, helpParameterDetailType, helpParameterName);
        
        if ( !StringUtils.isBlank(apcHelpUrl) ) {
            response.sendRedirect(apcHelpUrl);
            return null;
        }
        
        helpForm.setHelpDescription("No help content available.");
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }

    /**
     * Retrieves help information from resources by key.
     */
    public ActionForward getResourceHelpText(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiHelpForm helpForm = (KualiHelpForm) form;

        String resourceKey = helpForm.getResourceKey();
        populateHelpFormForResourceText(helpForm, resourceKey);

        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * Utility method that populates a KualiHelpForm with the description from a given resource key
     * @param helpForm the KualiHelpForm to populate with help text
     * @param resourceKey the resource key to use as help text
     */
    protected void populateHelpFormForResourceText(KualiHelpForm helpForm, String resourceKey) {
    	if (StringUtils.isBlank(resourceKey)) {
            throw new RuntimeException("Help resource key not specified.");
        }

        helpForm.setHelpLabel("");
        helpForm.setHelpSummary("");
        helpForm.setHelpDescription(getConfigurationService().getPropertyValueAsString(resourceKey));
    }
    
    /**
     * Retrieves help for a lookup
     */
    public ActionForward getLookupHelpText(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
    	KualiHelpForm helpForm = (KualiHelpForm) form;

        // handle doc search custom help urls
    	if (!StringUtils.isEmpty(helpForm.getSearchDocumentTypeName())) {
    	    DocumentType docType = KewApiServiceLocator.getDocumentTypeService().getDocumentTypeByName(helpForm.getSearchDocumentTypeName());
    	    if (docType != null && !StringUtils.isEmpty(docType.getDocSearchHelpUrl())) {
    	        String docSearchHelpUrl = WebUtils.toAbsoluteURL(ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.EXTERNALIZABLE_HELP_URL_KEY), docType.getDocSearchHelpUrl());

    	        if ( StringUtils.isNotBlank(docSearchHelpUrl) ) {
    	            response.sendRedirect(docSearchHelpUrl);
    	            return null;
    	        }
    	    }
    	}

    	final String lookupBusinessObjectClassName = helpForm.getLookupBusinessObjectClassName();
    	if (!StringUtils.isBlank(lookupBusinessObjectClassName)) {
    		final DataDictionary dataDictionary = getDataDictionaryService().getDataDictionary();
    		final BusinessObjectEntry entry = (BusinessObjectEntry) dataDictionary.getBusinessObjectEntry(lookupBusinessObjectClassName);
    		final LookupDefinition lookupDefinition = entry.getLookupDefinition();
    		
    		if (lookupDefinition != null) {
    			if (lookupDefinition.getHelpDefinition() != null && !StringUtils.isBlank(lookupDefinition.getHelpDefinition().getParameterNamespace()) && !StringUtils.isBlank(lookupDefinition.getHelpDefinition().getParameterDetailType()) && !StringUtils.isBlank(lookupDefinition.getHelpDefinition().getParameterName())) {
    				final String apcHelpUrl = getHelpUrl(lookupDefinition.getHelpDefinition().getParameterNamespace(), lookupDefinition.getHelpDefinition().getParameterDetailType(), lookupDefinition.getHelpDefinition().getParameterName());
    		        
    		        if ( !StringUtils.isBlank(apcHelpUrl) ) {
    		            response.sendRedirect(apcHelpUrl);
    		            return null;
    		        }
    			} else if (!StringUtils.isBlank(lookupDefinition.getHelpUrl())) {
    				final String apcHelpUrl = WebUtils.toAbsoluteURL(ConfigContext.getCurrentContextConfig().getProperty(KRADConstants.EXTERNALIZABLE_HELP_URL_KEY), lookupDefinition.getHelpUrl());
    				response.sendRedirect(apcHelpUrl);
    				return null;
    			}
    		}
    	}
    	
    	// still here?  guess we're defaulting...
    	populateHelpFormForResourceText(helpForm, getDefaultLookupHelpResourceKey());
        return mapping.findForward(RiceConstants.MAPPING_BASIC);
    }
    
    /**
     * @return the key of the default lookup help resource text
     */
    protected String getDefaultLookupHelpResourceKey() {
    	return KualiHelpAction.DEFAULT_LOOKUP_HELP_TEXT_RESOURCE_KEY;
    }

    private String getHelpUrl(String parameterNamespace, String parameterDetailTypeCode, String parameterName) {
        return WebUtils.toAbsoluteURL(getConfigurationService().getPropertyValueAsString(KRADConstants.EXTERNALIZABLE_HELP_URL_KEY), getParameterService().getParameterValueAsString(parameterNamespace, parameterDetailTypeCode, parameterName));
    }    
    
    /**
     * Retrieves help content to link to based on parameterNamespace and parameterName
     */
    public ActionForward getHelpUrlByNamespace(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
        KualiHelpForm helpForm = (KualiHelpForm) form;
       return getStoredHelpUrl(mapping, form, request, response); 
    }
}
