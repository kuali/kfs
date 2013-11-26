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
package org.kuali.kfs.module.tem.businessobject.lookup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.businessobject.TemProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerProfileForLookup;
import org.kuali.kfs.module.tem.identity.TemKimAttributes;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.identity.KfsKimAttributes;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kns.util.FieldUtils;
import org.kuali.rice.kns.util.KNSGlobalVariables;
import org.kuali.rice.kns.web.struts.form.KualiDocumentFormBase;
import org.kuali.rice.kns.web.struts.form.KualiForm;
import org.kuali.rice.kns.web.struts.form.LookupForm;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.service.SessionDocumentService;
import org.kuali.rice.krad.util.GlobalVariables;

/**
 * Overridden to filter results to only those available to the currently logged in user to use.  This will be used within TA, TR, ENT, and RELO
 * document traveler lookups - NOT the non-document TemProfile lookup
 */
public class TravelerProfileDocLookupableHelperServiceImpl extends TemProfileLookupableHelperServiceImpl {
    protected PermissionService permissionService;

    /**
     * Filters searched for profiles so they include only those the user should be able to access to use as a traveler on a travel, relocation, or entertainment document
     * @see org.kuali.kfs.module.tem.businessobject.lookup.TemProfileLookupableHelperServiceImpl#getSearchResults(java.util.Map)
     */
    @Override
    public List<? extends BusinessObject> getSearchResults(Map<String, String> fieldValues) {
        final Map<String, String> viewRecordPermissionDetails = getPermissionDetailsForViewRecordsCheck();
        final String currentUserPrincipalId = GlobalVariables.getUserSession().getPrincipalId();
        final String documentTypeName = updateAuthorizationDocumentType(getCurrentDocumentTypeName());

        final List<TemProfile> allProfiles = (List<TemProfile>)super.getSearchResults(fieldValues);
        List<TemProfile> qualifyingProfiles = new ArrayList<TemProfile>();
        for (TemProfile profile : allProfiles) {
            final Map<String, String> roleQualifier = getRoleQualifierForViewRecordsCheck(profile, documentTypeName);
            if (getPermissionService().isAuthorizedByTemplate(currentUserPrincipalId, KFSConstants.PermissionTemplate.VIEW_RECORD.namespace, KFSConstants.PermissionTemplate.VIEW_RECORD.name, viewRecordPermissionDetails, roleQualifier)) {
                qualifyingProfiles.add(profile);
            }
        }
        return qualifyingProfiles;
    }

    /**
     * @return the PermissionDetails map to do permission checks on TravelerProfileForLookup records
     */
    protected Map<String, String> getPermissionDetailsForViewRecordsCheck() {
        Map<String, String> permissionDetails = new HashMap<String, String>();
        permissionDetails.put(KimConstants.AttributeConstants.COMPONENT_NAME, TravelerProfileForLookup.class.getSimpleName());
        return permissionDetails;
    }

    /**
     * Pulls the principal id, home chart, and home organization codes from the profile to build a role qualifier
     * @param profile the profile to pull qualifications from
     * @return a role qualifier with attribute values from the profile
     */
    protected Map<String, String> getRoleQualifierForViewRecordsCheck(TemProfile profile, String documentTypeName) {
        Map<String, String> roleQualifier = new HashMap<String, String>();
        roleQualifier.put(KfsKimAttributes.CHART_OF_ACCOUNTS_CODE, profile.getHomeDeptChartOfAccountsCode());
        roleQualifier.put(KfsKimAttributes.ORGANIZATION_CODE, profile.getHomeDeptOrgCode());
        roleQualifier.put(TemKimAttributes.PROFILE_PRINCIPAL_ID, profile.getPrincipalId());
        roleQualifier.put(TemKimAttributes.CUSTOMER_PROFILE_ID, profile.getCustomerNumber());
        roleQualifier.put(TemKimAttributes.PROFILE_ID, profile.getProfileId().toString());

        if (!StringUtils.isBlank(documentTypeName)) {
            roleQualifier.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, documentTypeName);
        }

        return roleQualifier;
    }

    /**
     * Looks in the form from KNSGlobalVariables to try to figure out what the document type of the current document is
     * @return
     */
    protected String getCurrentDocumentTypeName() {
        final KualiForm form = KNSGlobalVariables.getKualiForm();
        if (form != null) {
            if (form instanceof KualiDocumentFormBase) {
                return ((KualiDocumentFormBase)KNSGlobalVariables.getKualiForm()).getDocTypeName();
            } else if (form instanceof LookupForm) {
                final String docNum = ((LookupForm)KNSGlobalVariables.getKualiForm()).getDocNum();
                if(!StringUtils.isBlank(docNum)) {
                    WorkflowDocument workflowDocument = SpringContext.getBean(SessionDocumentService.class).getDocumentFromSession(GlobalVariables.getUserSession(), docNum);
                    return workflowDocument.getDocumentTypeName();
                }
            }
        }
        return null;
    }

    /**
     * If the passed in document type represents one of the travel authorization child documents, upgrade to travel authorization document; otherwise return the given document type unmodified
     * @param documentType the document type name to filter
     * @return TA if TAC or TAA is passed in as the document type, otherwise the passed in document type
     */
    protected String updateAuthorizationDocumentType(String documentType) {
       if (!StringUtils.isBlank(documentType) && (TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_CLOSE_DOCUMENT.equals(documentType) || TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_AMEND_DOCUMENT.equals(documentType))) {
           return TemConstants.TravelDocTypes.TRAVEL_AUTHORIZATION_DOCUMENT;
       }
       return documentType;
    }

    /**
     * Overridden to always return TemProfile
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#getBusinessObjectClass()
     */
    @Override
    public Class getBusinessObjectClass() {
        return TemProfile.class;
    }

    /**
     * This lookup only occurs within documents; it should never have a supplemental menu bar
     * @see org.kuali.kfs.module.tem.businessobject.lookup.TemProfileLookupableHelperServiceImpl#getSupplementalMenuBar()
     */
    @Override
    public String getSupplementalMenuBar() {
        return KFSConstants.EMPTY_STRING;
    }

    /**
     * Overrides set rows so that the _rows_ always look at TravelerProfileForLookup as the business object class - we want that lookup,
     * but we want to search for TemProfiles....
     * @see org.kuali.rice.kns.lookup.AbstractLookupableHelperServiceImpl#setRows()
     */
    @Override
    protected void setRows() {
        List<String> lookupFieldAttributeList = null;
        final Class<? extends BusinessObject> businessObjectClazz = TravelerProfileForLookup.class;
        if (getBusinessObjectMetaDataService().isLookupable(businessObjectClazz)) {
            lookupFieldAttributeList = getBusinessObjectMetaDataService().getLookupableFieldNames(
                    businessObjectClazz);
        }
        if (lookupFieldAttributeList == null) {
            throw new RuntimeException("Lookup not defined for business object " + getBusinessObjectClass());
        }

        // construct field object for each search attribute
        List fields = new ArrayList();
        try {
            fields = FieldUtils.createAndPopulateFieldsForLookup(lookupFieldAttributeList, getReadOnlyFieldsList(),
                    businessObjectClazz);
        } catch (InstantiationException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to create instance of business object class" + e.getMessage());
        }

        int numCols = getBusinessObjectDictionaryService().getLookupNumberOfColumns(businessObjectClazz);

        this.rows = FieldUtils.wrapFields(fields, numCols);
    }

    /**
     * @return the injected implementation KIM's PermissionService
     */
    public PermissionService getPermissionService() {
        return permissionService;
    }

    /**
     * Injects an implementation of KIM's PermissionService
     * @param permissionService an implementation of KIM's PermissionService
     */
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

}
