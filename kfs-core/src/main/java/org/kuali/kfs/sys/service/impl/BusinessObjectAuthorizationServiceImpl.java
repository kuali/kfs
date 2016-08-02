/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.sys.service.impl;

import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.datadictionary.BusinessObjectEntry;
import org.kuali.rice.kns.document.authorization.BusinessObjectRestrictions;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DataObjectAuthorizer;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.DataDictionaryEntryBase;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.document.DocumentAuthorizer;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.util.KRADConstants;

/**
 * Override of BusinessObjectAuthorizationServiceImpl to allow document authorizers to build qualifiers for checks in {@link #canFullyUnmaskField(Person, Class, String, Document)}
 * The developer of this class apologizes for its tortuous complexity - it was all to get TemProfile to work...
 * and {@link #canPartiallyUnmaskField(Person, Class, String, Document)}
 */
public class BusinessObjectAuthorizationServiceImpl extends org.kuali.rice.kns.service.impl.BusinessObjectAuthorizationServiceImpl {
    protected volatile PermissionService permissionServiceForUs;
    protected volatile ConfigurationService kualiConfigurationServiceForUs;
    protected volatile MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    protected volatile DataDictionaryService dataDictionaryService;

    /**
     * Overridden to defer to canFullyUnmaskFieldForBusinessObject and canPartiallyUnmaskFieldForBusinessObject
     * @see org.kuali.rice.kns.service.impl.BusinessObjectAuthorizationServiceImpl#considerBusinessObjectFieldUnmaskAuthorization(Object, Person, BusinessObjectRestrictions, String, Document)
     * 
     * @param dataObject
     * @param user
     * @param businessObjectRestrictions
     * @param propertyPrefix
     * @param document 
     */
    @Override
    protected void considerBusinessObjectFieldUnmaskAuthorization(Object dataObject, Person user, BusinessObjectRestrictions businessObjectRestrictions, String propertyPrefix, Document document) {
        // UAF-6.0 upgrade - null data object causes the application to throw NullPointerException
        if (dataObject != null) {
            final DataDictionaryEntryBase objectEntry = (dataObject instanceof Document) ?
                    getDataDictionaryService().getDataDictionary().getDocumentEntry(getDataDictionaryService().getDocumentTypeNameByClass(dataObject.getClass())) :
                    getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(dataObject.getClass().getName());

            BusinessObject permissionTarget = (dataObject instanceof BusinessObject) ? (BusinessObject)dataObject : document;

            for (String attributeName : objectEntry.getAttributeNames()) {
                AttributeDefinition attributeDefinition = objectEntry.getAttributeDefinition(attributeName);
                if (attributeDefinition.getAttributeSecurity() != null) {
                    if (attributeDefinition.getAttributeSecurity().isMask() &&
                            !canFullyUnmaskFieldForBusinessObject(user, dataObject.getClass(), attributeName, permissionTarget, document)) {
                        businessObjectRestrictions.addFullyMaskedField(propertyPrefix + attributeName, attributeDefinition.getAttributeSecurity().getMaskFormatter());
                    }
                    if (attributeDefinition.getAttributeSecurity().isPartialMask() &&
                            !canPartiallyUnmaskFieldForBusinessObject(user, dataObject.getClass(), attributeName, permissionTarget, document)) {
                        businessObjectRestrictions.addPartiallyMaskedField(propertyPrefix + attributeName, attributeDefinition.getAttributeSecurity().getPartialMaskFormatter());
                    }
                }
            }
        }
    }

    /**
     * Defers to canFullyUnmaskFieldForBusinessObject
     * @see org.kuali.rice.kns.service.impl.BusinessObjectAuthorizationServiceImpl#canFullyUnmaskField(Person, Class, String, Document)
     * 
     * @param user
     * @param dataObjectClass
     * @param fieldName
     * @param document
     * @return 
     */
    @Override
    public boolean canFullyUnmaskField(Person user, Class<?> dataObjectClass, String fieldName, Document document) {
        return canFullyUnmaskFieldForBusinessObject(user, dataObjectClass, fieldName, document, null);
    }

    /**
     * Defers to canPartiallyUnmaskFieldForBusinessObject
     * @see org.kuali.rice.kns.service.impl.BusinessObjectAuthorizationServiceImpl#canPartiallyUnmaskField(Person, Class, String, Document)
     * 
     * @param user
     * @param dataObjectClass
     * @param fieldName
     * @param document
     * @return 
     */
    @Override
    public boolean canPartiallyUnmaskField(Person user, Class<?> dataObjectClass, String fieldName, Document document) {
        return canPartiallyUnmaskFieldForBusinessObject(user, dataObjectClass, fieldName, document, null);
    }

    /**
     * Determines if the given field on the given business object can be unmasked, using the business object to build role qualifiers if possible
     * @param user the person to check if there is full unmask field permission for
     * @param dataObjectClass the class of the data object being checked
     * @param fieldName the name of the field to potentially unmask
     * @param businessObject the business object containing sensitive information
     * @param document the document we are acting on, or null if no document is known
     * @return true if the field on the business object can be fully unmasked, false otherwise
     */
    protected boolean canFullyUnmaskFieldForBusinessObject(Person user, Class<?> dataObjectClass, String fieldName, BusinessObject businessObject, Document document) {
        if(isNonProductionEnvAndUnmaskingTurnedOffForUs()) {
            return false;
        }

        if(user==null || StringUtils.isEmpty(user.getPrincipalId())) {
            return false;
        }

        DataObjectAuthorizer authorizer = null;
        BusinessObject boForAuthorization = null;
        if (document != null) {
            authorizer = findDocumentAuthorizerForBusinessObject(document);
            boForAuthorization = document;
        }
        if (authorizer == null) {
            authorizer = findDocumentAuthorizerForBusinessObject(businessObject);
            if (authorizer == null) {
                authorizer = findInquiryAuthorizerForBusinessObject(businessObject);
            }
            boForAuthorization = businessObject;
        }
        if (authorizer == null) {
            return getPermissionServiceForUs().isAuthorizedByTemplate(user.getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                    KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD, new HashMap<String, String>(
                    getFieldPermissionDetails(dataObjectClass, fieldName)), Collections.<String, String>emptyMap());
        }
        return authorizer
                    .isAuthorizedByTemplate( boForAuthorization,
                            KRADConstants.KNS_NAMESPACE,
                            KimConstants.PermissionTemplateNames.FULL_UNMASK_FIELD,
                            user.getPrincipalId(), getFieldPermissionDetails(dataObjectClass, fieldName), Collections.<String, String>emptyMap());
    }

    /**
     * Determines if the given field on the given business object can be unmasked, using the business object to build role qualifiers if possible
     * @param user the person to check if there is partial unmask field permission for
     * @param dataObjectClass the class of the data object being checked
     * @param fieldName the name of the field to potentially unmask
     * @param businessObject the business object containing sensitive information
     * @param document the document we are acting on, or null if no document is known
     * @return true if the field on the business object can be partially unmasked, false otherwise
     */
    protected boolean canPartiallyUnmaskFieldForBusinessObject(Person user, Class<?> dataObjectClass, String fieldName, BusinessObject businessObject, Document document) {
        if(isNonProductionEnvAndUnmaskingTurnedOffForUs()) {
            return false;
        }

        if(user==null || StringUtils.isEmpty(user.getPrincipalId())) {
            return false;
        }

        DataObjectAuthorizer authorizer = null;
        BusinessObject boForAuthorization = null;
        if (document != null) {
            authorizer = findDocumentAuthorizerForBusinessObject(document);
            boForAuthorization = document;
        }
        if (authorizer == null) {
            authorizer = findDocumentAuthorizerForBusinessObject(businessObject);
            if (authorizer == null) {
                authorizer = findInquiryAuthorizerForBusinessObject(businessObject);
            }
            boForAuthorization = businessObject;
        }
        if ( authorizer == null ) {
            return getPermissionServiceForUs().isAuthorizedByTemplate(user.getPrincipalId(), KRADConstants.KNS_NAMESPACE,
                    KimConstants.PermissionTemplateNames.PARTIAL_UNMASK_FIELD, new HashMap<String, String>(
                    getFieldPermissionDetails(dataObjectClass, fieldName)), Collections.<String, String>emptyMap());
        } else { // if a document was passed, evaluate the permission in the context of a document
            return authorizer
                    .isAuthorizedByTemplate( boForAuthorization,
                                             KRADConstants.KNS_NAMESPACE,
                                             KimConstants.PermissionTemplateNames.PARTIAL_UNMASK_FIELD,
                                             user.getPrincipalId(), getFieldPermissionDetails(dataObjectClass, fieldName), Collections.<String, String>emptyMap() );
        }
    }

    /**
     * Attempts to find a DocumentAuthorizer for the given business object.  If the business object is a document, simply looks up
     * its associated authorizer.  Otherwise, it checks to see if there's a maintenance document associated with the business object and uses
     * the document authorizer associated with that
     * @param businessObject the business object to attempt to find a DocumentAuthorizer for
     * @return an instantiated DocumentAuthorizer associated with the business object, or null if none could be found
     */
    protected DocumentAuthorizer findDocumentAuthorizerForBusinessObject(BusinessObject businessObject) {
        if (businessObject == null) {
            return null;
        }
        if (businessObject instanceof Document) {
            return getDocumentHelperService().getDocumentAuthorizer( (Document)businessObject );
        }
        final String maintDocType = getMaintenanceDocumentDictionaryService().getDocumentTypeName(businessObject.getClass());
        if (StringUtils.isBlank(maintDocType)) {
            return null;
        }
        return getDocumentHelperService().getDocumentAuthorizer(maintDocType);
    }

    /**
     * Attempts to find an InquiryAuthorizer for the given business object, by looking at the inquiry definition
     * @param businessObject the business object to attempt to find a InquiryAuthorizer for
     * @return an instantiated InquiryAuthorizer associated with the business object, or null if none could be found
     */
    protected DataObjectAuthorizer findInquiryAuthorizerForBusinessObject(BusinessObject businessObject) {
        if (businessObject == null) {
            return null;
        }
        final BusinessObjectEntry boEntry = (BusinessObjectEntry)getDataDictionaryService().getDataDictionary().getBusinessObjectEntry(businessObject.getClass().getName());
        if (boEntry != null && boEntry.getInquiryDefinition() != null && boEntry.getInquiryDefinition().getAuthorizerClass() != null) {
            try {
                return (DataObjectAuthorizer)boEntry.getInquiryDefinition().getAuthorizerClass().newInstance();
            }
            catch (InstantiationException ie) {
                throw new RuntimeException("Could not instantiate authorizer for inquiry of "+businessObject.getClass().getName(), ie);
            }
            catch (IllegalAccessException iae) {
                throw new RuntimeException("Could not instantiate authorizer for inquiry of "+businessObject.getClass().getName(), iae);
            }
        }
        return null;
    }

    /**
     * Renamed to avoid shadowing
     * 
     * @return 
     */
    protected boolean isNonProductionEnvAndUnmaskingTurnedOffForUs(){
        return !getKualiConfigurationServiceForUs().getPropertyValueAsString(KRADConstants.PROD_ENVIRONMENT_CODE_KEY)
                .equalsIgnoreCase(
                        getKualiConfigurationServiceForUs().getPropertyValueAsString(KRADConstants.ENVIRONMENT_KEY)) &&
                !getKualiConfigurationServiceForUs().getPropertyValueAsBoolean(KRADConstants.ENABLE_NONPRODUCTION_UNMASKING);
    }

    /**
     * Renamed to avoid shadowing
     * 
     * @return 
     */
    protected PermissionService getPermissionServiceForUs() {
        if (permissionServiceForUs == null) {
            permissionServiceForUs = KimApiServiceLocator
                    .getPermissionService();
        }
        return permissionServiceForUs;
    }

    /**
     * Renamed to avoid shadowing
     * 
     * @return 
     */
    protected ConfigurationService getKualiConfigurationServiceForUs() {
        if (kualiConfigurationServiceForUs == null) {
            kualiConfigurationServiceForUs = KRADServiceLocator.getKualiConfigurationService();
        }
        return kualiConfigurationServiceForUs;
    }

    protected MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        if (maintenanceDocumentDictionaryService == null) {
            maintenanceDocumentDictionaryService = KNSServiceLocator.getMaintenanceDocumentDictionaryService();
        }
        return maintenanceDocumentDictionaryService;
    }

    @Override
    protected DataDictionaryService getDataDictionaryService() {
        if (dataDictionaryService == null) {
            dataDictionaryService = KNSServiceLocator.getDataDictionaryService();
        }
        return dataDictionaryService;
    }
}
