/*
 * Copyright 2006-2007 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.sys.document;

import java.security.GeneralSecurityException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.ojb.broker.util.collections.RemovalAwareCollection;
import org.kuali.core.bo.user.AuthenticationUserId;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.Document;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.DocumentAuthorizer;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.maintenance.KualiMaintainableImpl;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.FieldUtils;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.web.ui.Field;
import org.kuali.core.web.ui.Row;
import org.kuali.core.web.ui.Section;
import org.kuali.kfs.sys.document.authorization.FinancialSystemUserDocumentAuthorizer;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.businessobject.FinancialSystemUserOrganizationSecurity;
import org.kuali.kfs.sys.businessobject.FinancialSystemUserPrimaryOrganization;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.rice.KNSServiceLocator;
import org.kuali.rice.core.service.EncryptionService;
import org.kuali.rice.kns.util.KNSPropertyConstants;

import edu.iu.uis.eden.exception.WorkflowException;

public class FinancialSystemUserMaintainable extends KualiMaintainableImpl {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FinancialSystemUserMaintainable.class);

    protected static DocumentService documentService;
    protected static FinancialSystemUserService financialSystemUserService;
    protected static BusinessObjectService businessObjectService;
    FinancialSystemUserDocumentAuthorizer docAuth = null;

    public FinancialSystemUserMaintainable() {
        super();
        initStatics();
        DocumentAuthorizer docAuthTemp = KNSServiceLocator.getDocumentAuthorizationService().getDocumentAuthorizer(getMaintenanceDocumentDictionaryService().getDocumentTypeName(FinancialSystemUser.class));
        if ( docAuthTemp instanceof FinancialSystemUserDocumentAuthorizer ) {
            docAuth = (FinancialSystemUserDocumentAuthorizer)docAuthTemp;
        }
    }
    
    @Override
    public void saveBusinessObject() {
        // save KFS user attributes
        FinancialSystemUser user = (FinancialSystemUser)getBusinessObject();
        // only attempt to save the UU object if the initiator is in the appropriate group
        // get the group name that we need here
        UniversalUser initiator = null;
        try {
            Document doc = documentService.getByDocumentHeaderId( documentNumber );
            if ( doc != null ) {
                String initiatorId = doc.getDocumentHeader().getWorkflowDocument().getInitiatorNetworkId();
                if ( initiatorId != null ) {
                    initiator = financialSystemUserService.getUniversalUser( new AuthenticationUserId( initiatorId ) );
                }
            }
        } catch ( WorkflowException ex ) {
            LOG.error( "unable to get initiator ID for document " + documentNumber, ex );
        } catch ( UserNotFoundException ex ) {
            LOG.error( "unable to get initator UniversalUser for for document " + documentNumber, ex );
        }
        if ( docAuth != null && docAuth.canEditUniversalUserAttributes(initiator)) {
            businessObjectService.save(user.getEmbeddedUniversalUser());
        }
        // only save the KFS user portion if initiator is in the proper workgroup
        if ( docAuth != null && docAuth.canEditFinancialSystemUserAttributes(initiator)) {
            super.saveBusinessObject();
            // CHECK: should these be deleted?
            Iterator<FinancialSystemUserPrimaryOrganization> primaryOrgs = user.getPrimaryOrganizations().iterator();
            while ( primaryOrgs.hasNext() ) {
                FinancialSystemUserPrimaryOrganization org = primaryOrgs.next();
                if ( !org.isActive() ) {
                    businessObjectService.delete(org);
                }
            }
            Iterator<FinancialSystemUserOrganizationSecurity> securityOrgs = user.getOrganizationSecurity().iterator();
            while ( securityOrgs.hasNext() ) {
                FinancialSystemUserOrganizationSecurity org  = securityOrgs.next();
                if ( !org.isActive() ) {
                    businessObjectService.delete(org);
                }
            }
        }
    }
    
    protected void initStatics() {
        if ( businessObjectService == null ) { // they're all set at the same time, so only need one check
            financialSystemUserService = SpringContext.getBean(FinancialSystemUserService.class);
            documentService = KNSServiceLocator.getDocumentService();
            businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        }
    }

    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterEdit(document, parameters);
        ((FinancialSystemUser)document.getOldMaintainableObject().getBusinessObject()).refreshEmbeddedUniversalUser();
        ((FinancialSystemUser)document.getNewMaintainableObject().getBusinessObject()).refreshEmbeddedUniversalUser();
    }
   
    /**
     * @see org.kuali.core.maintenance.Maintainable#processAfterCopy()
     */
    @Override
    public void processAfterCopy( MaintenanceDocument document, Map<String,String[]> parameters ) {
        super.processAfterCopy( document, parameters );
        FinancialSystemUser newUser = (FinancialSystemUser)document.getNewMaintainableObject().getBusinessObject();
        FinancialSystemUser oldUser = (FinancialSystemUser)document.getOldMaintainableObject().getBusinessObject();
        newUser.setPersonUniversalIdentifier(oldUser.getPersonUniversalIdentifier());
        newUser.refreshEmbeddedUniversalUser();
        newUser.clearPersonUniversalIdentifierForCopy();
        oldUser.refreshEmbeddedUniversalUser();
    }
    
    /**
     * Overrides section creation to remove the add lines if the KFS user properties can not be edited.
     * 
     * @see org.kuali.core.maintenance.KualiMaintainableImpl#getSections(org.kuali.core.maintenance.Maintainable)
     */
    @Override
    public List getSections(Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(oldMaintainable);

        if ( docAuth != null && !docAuth.canEditFinancialSystemUserAttributes(GlobalVariables.getUserSession().getFinancialSystemUser())) {
            for ( Section section : sections ) {
                if ( section.getSectionId().equals("PrimaryOrganizations") 
                        || section.getSectionId().equals("OrganizationSecurity") ) {
                    Iterator<Row> rows = section.getRows().iterator();
                    while (rows.hasNext()) {
                        Row row = rows.next();
                        if (row.getFields().size() > 0) {
                            Field field = row.getFields().get(0);
                            if (StringUtils.equals(field.getPropertyName(), "primaryOrganizations")
                                    || StringUtils.equals(field.getPropertyName(), "organizationSecurity")) {
                                rows.remove();
                            }
                        }
                    }
                }
            }
        }
        
        
        return sections;
    }
    /**
     * @see org.kuali.core.maintenance.Maintainable#populateBusinessObject(java.util.Map)
     */
    public Map populateBusinessObject(Map fieldValues) {
        // need to make sure that the UUID is populated first for later fields
        if ( fieldValues.containsKey( KNSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER ) ) {
            String personUniversalIdentifier = (String)fieldValues.remove(KNSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER);
            ((UniversalUser)getBusinessObject()).setPersonUniversalIdentifier( personUniversalIdentifier.toUpperCase() );
            
        }
        fieldValues = decryptEncryptedData(fieldValues);
   
        Map cachedValues = FieldUtils.populateBusinessObjectFromMap(getBusinessObject(), fieldValues);
        getBusinessObjectDictionaryService().performForceUppercase(getBusinessObject());
        return cachedValues;
    }
    /**
     * Special hidden parameters are set on the maintenance jsp starting with a prefix that tells us which fields have been
     * encrypted. This field finds the those parameters in the map, whose value gives us the property name that has an encrypted
     * value. We then need to decrypt the value in the Map before the business object is populated.
     * 
     * @param fieldValues - possibly with encrypted values
     * @return Map fieldValues - with no encrypted values
     */
    private Map decryptEncryptedData(Map fieldValues) {
        try {
        for (Iterator iter = fieldValues.keySet().iterator(); iter.hasNext();) {
                String fieldName = (String) iter.next();
                String fieldValue = (String) fieldValues.get(fieldName);
                if (fieldValue != null && fieldValue.endsWith(EncryptionService.ENCRYPTION_POST_PREFIX)) {
                    String encryptedValue = fieldValue;

                    // take of the postfix
                    encryptedValue = StringUtils.stripEnd(encryptedValue, EncryptionService.ENCRYPTION_POST_PREFIX);
                    String decryptedValue = getEncryptionService().decrypt(encryptedValue);

                    fieldValues.put(fieldName, decryptedValue);
                }
            }
        }
                catch (GeneralSecurityException e) {
                    throw new RuntimeException("Unable to decrypt secure data: " + e.getMessage());
                }
        
        return fieldValues;
    }
}
