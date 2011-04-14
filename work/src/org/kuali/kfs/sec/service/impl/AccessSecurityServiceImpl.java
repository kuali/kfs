/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo;
import org.kuali.kfs.sec.datadictionary.AccessSecurityAttributeRestrictionEntry;
import org.kuali.kfs.sec.datadictionary.AccessSecurityBusinessObjectEntry;
import org.kuali.kfs.sec.identity.SecKimAttributes;
import org.kuali.kfs.sec.service.AccessPermissionEvaluator;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.bo.role.dto.KimPermissionInfo;
import org.kuali.rice.kim.bo.role.dto.KimPermissionTemplateInfo;
import org.kuali.rice.kim.bo.types.dto.AttributeSet;
import org.kuali.rice.kim.service.PermissionService;
import org.kuali.rice.kim.service.RoleManagementService;
import org.kuali.rice.kns.bo.BusinessObject;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.datadictionary.AttributeDefinition;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.ObjectUtils;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


/**
 * @see org.kuali.kfs.sec.service.AccessSecurityService
 */
public class AccessSecurityServiceImpl implements AccessSecurityService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccessSecurityServiceImpl.class);

    protected DataDictionaryService dataDictionaryService;
    protected ParameterService parameterService;
    protected PermissionService permissionService;
    protected RoleManagementService roleManagementService;
    protected ContractsAndGrantsModuleService contractsAndGrantsModuleService;

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#applySecurityRestrictionsForGLInquiry(java.util.List, org.kuali.rice.kim.bo.Person)
     */
    public void applySecurityRestrictionsForGLInquiry(List<? extends BusinessObject> results, Person person) {
        AttributeSet additionalPermissionDetails = new AttributeSet();
        additionalPermissionDetails.put(SecKimAttributes.NAMESPACE_CODE, KFSConstants.ParameterNamespaces.GL);

        applySecurityRestrictions(results, person, SecConstants.SecurityTemplateIds.INQUIRY_FIELD_VALUE, additionalPermissionDetails);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#applySecurityRestrictionsForLaborInquiry(java.util.List, org.kuali.rice.kim.bo.Person)
     */
    public void applySecurityRestrictionsForLaborInquiry(List<? extends BusinessObject> results, Person person) {
        AttributeSet additionalPermissionDetails = new AttributeSet();
        additionalPermissionDetails.put(SecKimAttributes.NAMESPACE_CODE, SecConstants.LABOR_MODULE_NAMESPACE_CODE);

        applySecurityRestrictions(results, person, SecConstants.SecurityTemplateIds.INQUIRY_FIELD_VALUE, additionalPermissionDetails);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#applySecurityRestrictionsForLookup(java.util.List, org.kuali.rice.kim.bo.Person)
     */
    public void applySecurityRestrictionsForLookup(List<? extends BusinessObject> results, Person person) {
        applySecurityRestrictions(results, person, SecConstants.SecurityTemplateIds.LOOKUP_FIELD_VALUE, null);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#applySecurityRestrictions(java.util.List, org.kuali.rice.kim.bo.Person, java.lang.String,
     *      org.kuali.rice.kim.bo.types.dto.AttributeSet)
     */
    public void applySecurityRestrictions(List<? extends BusinessObject> results, Person person, String templateId, AttributeSet additionalPermissionDetails) {
        if (!isAccessSecurityEnabled()) {
            return;
        }

        if (results == null || results.isEmpty()) {
            return;
        }

        // evaluate permissions against business object instances
        List<BusinessObject> restrictedRecords = new ArrayList();
        for (BusinessObject businessObject : results) {
            boolean accessAllowed = evaluateSecurityPermissionsByTemplate(businessObject, businessObject.getClass(), person, templateId, additionalPermissionDetails, null);
            if (!accessAllowed) {
                restrictedRecords.add(businessObject);
            }
        }

        // remove restricted records from result list
        for (BusinessObject businessObject : restrictedRecords) {
            results.remove(businessObject);
        }
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#checkSecurityRestrictionsForBusinessObject(org.kuali.rice.kns.bo.BusinessObject, org.kuali.rice.kim.bo.Person,
     *      org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo)
     */
    public boolean checkSecurityRestrictionsForBusinessObject(BusinessObject businessObject, Person person, AccessSecurityRestrictionInfo restrictionInfo) {
        return evaluateSecurityPermissionsByTemplate(businessObject, businessObject.getClass(), person, SecConstants.SecurityTemplateIds.LOOKUP_FIELD_VALUE, null, restrictionInfo);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canEditDocument(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.rice.kim.bo.Person)
     */
    public boolean canEditDocument(AccountingDocument document, Person person) {
        return evaluateSecurityOnAccountingLinesByTemplate(document, person, SecConstants.SecurityTemplateIds.EDIT_DOCUMENT_FIELD_VALUE, null);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canEditDocumentAccountingLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.bo.Person, org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo)
     */
    public boolean canEditDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person, AccessSecurityRestrictionInfo restrictionInfo) {
        // check for edit line overrides
        boolean meetsOverrideCondition = checkForEditLineOverrides(document, accountingLine, person);
        if (meetsOverrideCondition) {
            return true;
        }

        Class entryClass = SourceAccountingLine.class;
        if (TargetAccountingLine.class.isAssignableFrom(accountingLine.getClass())) {
            entryClass = TargetAccountingLine.class;
        }

        if (restrictionInfo != null) {
            restrictionInfo.setDocumentNumber(document.getDocumentNumber());
        }

        return evaluateSecurityPermissionsByTemplate(accountingLine, entryClass, person, SecConstants.SecurityTemplateIds.EDIT_ACCOUNTING_LINE_FIELD_VALUE, getDocumentTypeDetail(document), restrictionInfo);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canEditDocumentAccountingLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.bo.Person)
     */
    public boolean canEditDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person) {
        return canEditDocumentAccountingLine(document, accountingLine, person, new AccessSecurityRestrictionInfo());
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canViewDocument(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.rice.kim.bo.Person,
     *      org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo)
     */
    public boolean canViewDocument(AccountingDocument document, Person person, AccessSecurityRestrictionInfo restrictionInfo) {
        // any workflow requests override view document access permissions
        boolean hasWorkflowRequests = checkForWorkflowRoutingRequests(document, person);
        if (hasWorkflowRequests) {
            return true;
        }

        // check for parameter overrides
        boolean meetsOverrideCondition = checkForViewDocumentOverrides(document, person);
        if (meetsOverrideCondition) {
            return true;
        }

        if (restrictionInfo != null) {
            restrictionInfo.setDocumentNumber(document.getDocumentNumber());
        }

        return evaluateSecurityOnAccountingLinesByTemplate(document, person, SecConstants.SecurityTemplateIds.VIEW_DOCUMENT_FIELD_VALUE, restrictionInfo);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canViewDocumentAccountingLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.bo.Person)
     */
    public boolean canViewDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person) {
        // check for view line overrides
        boolean meetsOverrideCondition = checkForViewLineOverrides(document, accountingLine, person);
        if (meetsOverrideCondition) {
            return true;
        }

        Class entryClass = SourceAccountingLine.class;
        if (TargetAccountingLine.class.isAssignableFrom(accountingLine.getClass())) {
            entryClass = TargetAccountingLine.class;
        }

        return evaluateSecurityPermissionsByTemplate(accountingLine, entryClass, person, SecConstants.SecurityTemplateIds.VIEW_ACCOUNTING_LINE_FIELD_VALUE, getDocumentTypeDetail(document), null);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canViewDocumentNotesAttachments(org.kuali.kfs.sys.document.AccountingDocument, org.kuali.rice.kim.bo.Person)
     */
    public boolean canViewDocumentNotesAttachments(AccountingDocument document, Person person) {
        // check for parameter overrides
        boolean meetsOverrideCondition = checkForViewDocumentOverrides(document, person);
        if (meetsOverrideCondition) {
            return true;
        }

        return evaluateSecurityOnAccountingLinesByTemplate(document, person, SecConstants.SecurityTemplateIds.VIEW_NOTES_ATTACHMENTS_FIELD_VALUE, null);
    }

    /**
     * Iterates through source and target accounting lines for the given document and evaluates any permissions with the given template id against the accounting line values
     * 
     * @param document AccountingDocument instance with accounting lines to check, doc type of instance is used for retrieving permissions
     * @param person the user who we are checking access for
     * @param templateId KIM template id for the permissions to check
     * @param restrictionInfo Object providing information on a restriction if one is found
     * @return boolean true if all accounting lines pass permissions, false if at least one accounting line does not pass
     */
    protected boolean evaluateSecurityOnAccountingLinesByTemplate(AccountingDocument document, Person person, String templateId, AccessSecurityRestrictionInfo restrictionInfo) {
        boolean success = true;

        if (!isAccessSecurityEnabled()) {
            return success;
        }

        AttributeSet permissionDetails = getDocumentTypeDetail(document);

        // check source lines
        for (Iterator iterator = document.getSourceAccountingLines().iterator(); iterator.hasNext();) {
            AccountingLine accountingLine = (AccountingLine) iterator.next();

            // check for overrides
            boolean meetsOverrideCondition = false;
            if (templateId.equals(SecConstants.SecurityTemplateIds.VIEW_DOCUMENT_FIELD_VALUE)) {
                meetsOverrideCondition = checkForViewLineOverrides(document, accountingLine, person);
            }
            else {
                meetsOverrideCondition = checkForEditLineOverrides(document, accountingLine, person);
            }

            if (meetsOverrideCondition) {
                continue;
            }

            success = evaluateSecurityPermissionsByTemplate(accountingLine, SourceAccountingLine.class, person, templateId, permissionDetails, restrictionInfo);
            if (!success) {
                break;
            }
        }

        // if source lines are ok, check target lines
        if (success) {
            for (Iterator iterator = document.getTargetAccountingLines().iterator(); iterator.hasNext();) {
                AccountingLine accountingLine = (AccountingLine) iterator.next();

                // check for overrides
                boolean meetsOverrideCondition = false;
                if (templateId.equals(SecConstants.SecurityTemplateIds.VIEW_DOCUMENT_FIELD_VALUE)) {
                    meetsOverrideCondition = checkForViewLineOverrides(document, accountingLine, person);
                }
                else {
                    meetsOverrideCondition = checkForEditLineOverrides(document, accountingLine, person);
                }

                if (meetsOverrideCondition) {
                    continue;
                }

                success = evaluateSecurityPermissionsByTemplate(accountingLine, TargetAccountingLine.class, person, templateId, permissionDetails, restrictionInfo);
                if (!success) {
                    break;
                }
            }
        }

        return success;
    }

    /**
     * Checks for any workflow requests (approve, acknowledge, fyi) for the document to the given person
     * 
     * @param document Document to check for routing requests
     * @param person Person to check for routing requests
     * @return boolean true if there are workflow requests, false if not
     */
    protected boolean checkForWorkflowRoutingRequests(AccountingDocument document, Person person) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        if (workflowDocument.isApprovalRequested() || workflowDocument.isAcknowledgeRequested() || workflowDocument.isFYIRequested()) {
            return true;
        }

        return false;
    }

    /**
     * Checks parameter overrides for view document permission. Currently only have initiator override parameter
     * 
     * @param document Document that we are checking permissions for
     * @param person Person we are checking permissions for
     * @return boolean true if overrides are turned on and the person meets the override conditions, false if overrides are not turned on or the person does not meet condition
     */
    protected boolean checkForViewDocumentOverrides(AccountingDocument document, Person person) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        boolean alwaysAllowInitiatorAccess = parameterService.getIndicatorParameter(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_INITIATOR_DOCUMENT_ACCESS_IND);
        if (alwaysAllowInitiatorAccess) {
            if (StringUtils.equals(workflowDocument.getInitiatorPrincipalId(), person.getPrincipalId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks parameter overrides for view line permission. Currently only have initiator, fiscal officer, and principal investigator overrides
     * 
     * @param document Document that we are checking permissions for
     * @param person Person we are checking permissions for
     * @param line AccountingLine we are checking permissions for
     * @return boolean true if any override is turned on and the person meets the override conditions, false otherwise
     */
    protected boolean checkForViewLineOverrides(AccountingDocument document, AccountingLine line, Person person) {
        KualiWorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        // initiator override
        boolean alwaysAllowInitiatorAccess = parameterService.getIndicatorParameter(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_INITIATOR_LINE_ACCESS_IND);
        if (alwaysAllowInitiatorAccess) {
            if (StringUtils.equals(workflowDocument.getInitiatorPrincipalId(), person.getPrincipalId())) {
                return true;
            }
        }

        // account must be not be empty for further override checks
        if (line.getAccount() == null) {
            return false;
        }

        // fiscal officer override
        boolean alwaysAllowFOAccess = parameterService.getIndicatorParameter(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_FISCAL_OFFICER_LINE_ACCESS_IND);
        if (alwaysAllowFOAccess) {
            if (StringUtils.equals(line.getAccount().getAccountFiscalOfficerSystemIdentifier(), person.getPrincipalId())) {
                return true;
            }
        }

        // pi override
        boolean alwaysAllowPIAccess = parameterService.getIndicatorParameter(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_PRINCIPAL_INVESTIGATOR_LINE_ACCESS_IND);
        if (alwaysAllowPIAccess) {
            Person principalInvestigator = contractsAndGrantsModuleService.getProjectDirectorForAccount(line.getAccount());
            if (principalInvestigator != null && StringUtils.equals(principalInvestigator.getPrincipalId(), person.getPrincipalId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks parameter overrides for edit line permission. Currently only have fiscal officer and principal investigator overrides
     * 
     * @param document Document that we are checking permissions for
     * @param person Person we are checking permissions for
     * @param line AccountingLine we are checking permissions for
     * @return boolean true if any override is turned on and the person meets the override conditions, false otherwise
     */
    protected boolean checkForEditLineOverrides(AccountingDocument document, AccountingLine line, Person person) {
        // account must be not be empty for override checks
        if (line.getAccount() == null || line.getAccountNumber() == null) {
            return false;
        }

        // fiscal officer override
        boolean alwaysAllowFOAccess = parameterService.getIndicatorParameter(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_FISCAL_OFFICER_LINE_ACCESS_IND);
        if (alwaysAllowFOAccess) {
            if (StringUtils.equals(line.getAccount().getAccountFiscalOfficerSystemIdentifier(), person.getPrincipalId())) {
                return true;
            }
        }

        // pi override
        boolean alwaysAllowPIAccess = parameterService.getIndicatorParameter(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_PRINCIPAL_INVESTIGATOR_LINE_ACCESS_IND);
        if (alwaysAllowPIAccess) {
            Person principalInvestigator = contractsAndGrantsModuleService.getProjectDirectorForAccount(line.getAccount());
            if (principalInvestigator != null && StringUtils.equals(principalInvestigator.getPrincipalId(), person.getPrincipalId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Validates any security permissions setup for the user and attributes of the class against the business object values
     * 
     * @param businessObject instance with attribute values to validate
     * @param entryClass Class of business object to pull attribute restrictions for
     * @param person the user who we are checking access for
     * @param templateId type of security permissions to check
     * @param additionalPermissionDetails any additional details that should be used for retrieving permissions
     * @param restrictionInfo Object providing information on a restriction if one is found
     * @return boolean true if user has access given by template to the business object, false otherwise
     */
    protected boolean evaluateSecurityPermissionsByTemplate(BusinessObject businessObject, Class entryClass, Person person, String templateId, AttributeSet additionalPermissionDetails, AccessSecurityRestrictionInfo restrictionInfo) {
        boolean success = true;

        if (!isAccessSecurityEnabled()) {
            return success;
        }

        // get all configured restricted attributes for this business object through it data dictionary entry
        AccessSecurityBusinessObjectEntry businessObjectEntry = (AccessSecurityBusinessObjectEntry) dataDictionaryService.getDataDictionary().getBusinessObjectEntry(entryClass.getName());

        // get template name from id
        KimPermissionTemplateInfo templateInfo = permissionService.getPermissionTemplate(templateId);
        String templateName = templateInfo.getName();

        if (PersistableBusinessObject.class.isAssignableFrom(businessObject.getClass())) {
            ((PersistableBusinessObject) businessObject).refreshNonUpdateableReferences();
        }
        else {
            businessObject.refresh();
        }

        for (AccessSecurityAttributeRestrictionEntry accessRestrictedAttribute : businessObjectEntry.getAccessRestrictedAttributes()) {
            AttributeSet permissionDetails = new AttributeSet();
            permissionDetails.put(SecKimAttributes.PROPERTY_NAME, accessRestrictedAttribute.getSecurityAttributeName());

            if (additionalPermissionDetails != null) {
                permissionDetails.putAll(additionalPermissionDetails);
            }

            List<KimPermissionInfo> permissions = permissionService.getAuthorizedPermissionsByTemplateName(person.getPrincipalId(), SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, templateName, permissionDetails, null);
            if (permissions == null || permissions.isEmpty()) {
                continue;
            }

            // retrieve field value to check
            Object propertyValue = ObjectUtils.getPropertyValue(businessObject, accessRestrictedAttribute.getAttribute().getName());
            if (propertyValue != null && StringUtils.isNotEmpty(propertyValue.toString())) {
                // retrieve other field values that might be necessary to validate main field
                Map<String, Object> otherKeyValues = new HashMap<String, Object>();
                for (String keyFieldName : accessRestrictedAttribute.getOtherKeyFields().keySet()) {
                    AttributeDefinition fieldDefinition = accessRestrictedAttribute.getOtherKeyFields().get(keyFieldName);

                    Object keyFieldValue = ObjectUtils.getPropertyValue(businessObject, fieldDefinition.getName());
                    otherKeyValues.put(keyFieldName, keyFieldValue);
                }

                success = evaluateSecurityPermissions(accessRestrictedAttribute.getAccessPermissionEvaluatorClass(), permissions, propertyValue.toString(), person, otherKeyValues);
                if (!success) {
                    if (restrictionInfo != null) {
                        restrictionInfo.setSecurityAttributeName(accessRestrictedAttribute.getSecurityAttributeName());
                        restrictionInfo.setPropertyName(accessRestrictedAttribute.getAttribute().getName());
                        restrictionInfo.setPropertyLabel(accessRestrictedAttribute.getAttribute().getLabel());
                        restrictionInfo.setRetrictedValue((String) propertyValue);
                    }

                    break;
                }
            }
        }

        return success;
    }

    /**
     * Constructs a new AttributeSet and adds document type name detail with value from document instance
     * 
     * @param document AccountingDocument instance which document type will be set from
     * @return AttributeSet containing document type name detail
     */
    protected AttributeSet getDocumentTypeDetail(AccountingDocument document) {
        AttributeSet details = new AttributeSet();
        details.put(SecKimAttributes.DOCUMENT_TYPE_NAME, document.getFinancialDocumentTypeCode());

        return details;
    }

    /**
     * Checks whether the given value is allowed based on the given permissions and user
     * 
     * @param accessPermissionEvaluatorClass Class of type AccessPermissionEvaluator that will be used to evaluate the security restriction
     * @param permissions List of permissions to evaluate
     * @param value the value that will be checked
     * @param person the user who we are checking access for
     * @param otherKeyValues Map for other key field name/value pairs
     * @return boolean true if access is allowed false if not
     */
    protected boolean evaluateSecurityPermissions(Class<? extends AccessPermissionEvaluator> accessPermissionEvaluatorClass, List<KimPermissionInfo> permissions, String value, Person person, Map<String, Object> otherKeyValues) {
        boolean success = true;

        List<AttributeSet> qualficationsToEvaluate = new ArrayList<AttributeSet>();
        for (KimPermissionInfo permission : permissions) {
            // find all roles that have been granted this permission
            List<String> roleIds = permissionService.getRoleIdsForPermissionId(permission.getPermissionId());

            // for all the roles that have this permission, find the users qualification in those roles (if any)
            List<AttributeSet> qualfications = roleManagementService.getRoleQualifiersForPrincipalIncludingNested(person.getPrincipalId(), roleIds, null);

            if (qualfications != null) {
                qualficationsToEvaluate.addAll(qualfications);
            }
        }

        // cycle through the users qualifications and evaluate against the given value
        boolean hasAllowQualification = false;
        boolean allowQualificationSuccess = false;
        boolean hasDenyFailure = false;
        boolean hasAllowOverride = false;
        for (AttributeSet attributeSet : qualficationsToEvaluate) {
            AccessPermissionEvaluator accessPermissionEvaluator = constructAccessPermissionEvaluator(accessPermissionEvaluatorClass, attributeSet, otherKeyValues, person);
            boolean allowed = accessPermissionEvaluator.valueIsAllowed(value);

            // all qualifications with constraint 'D' (deny) must pass
            String constraintCode = attributeSet.get(SecKimAttributes.CONSTRAINT_CODE);
            if (!allowed && StringUtils.contains(constraintCode, SecConstants.SecurityConstraintCodes.DENIED)) {
                hasDenyFailure = true;
            }

            // if there are any 'A' (allow) qualifications, at least one must succeed
            if (StringUtils.contains(constraintCode, SecConstants.SecurityConstraintCodes.ALLOWED)) {
                hasAllowQualification = true;
                if (allowed) {
                    allowQualificationSuccess = true;
                    
                    // check for override of deny
                    String overrideDeny = attributeSet.get(SecKimAttributes.OVERRIDE_DENY);
                    if (Boolean.parseBoolean(overrideDeny)) {
                        hasAllowOverride = true;
                    }
                }
            }
        }

        if ((hasDenyFailure && ! hasAllowOverride) || (hasAllowQualification && !allowQualificationSuccess)) {
            success = false;
        }

        return success;
    }

    /**
     * Constructs a new instance of the AccessPermissionEvaluator class and sets the constraint, operator, and value based on the given qualification
     * 
     * @param accessPermissionEvaluatorClass Class to create instance of (must implement AccessPermissionEvaluator interface)
     * @param attributeSet AttributeSet containing the qualifier values
     * @param otherKeyValues Map for other key field name/value pairs
     * @param person Person who permission should be evaluated for
     * @return new instance of type AccessPermissionEvaluator
     * @see org.kuali.kfs.sec.service.AccessPermissionEvaluator
     */
    protected AccessPermissionEvaluator constructAccessPermissionEvaluator(Class<? extends AccessPermissionEvaluator> accessPermissionEvaluatorClass, AttributeSet attributeSet, Map<String, Object> otherKeyValues, Person person) {
        AccessPermissionEvaluator accessPermissionEvaluator = null;
        try {
            accessPermissionEvaluator = accessPermissionEvaluatorClass.newInstance();
        }
        catch (Exception e) {
            String msg = "Unable to create new instance of AccessPermissionEvaluator class: " + accessPermissionEvaluatorClass.getName();
            LOG.error(msg, e);
            throw new RuntimeException(msg, e);
        }

        accessPermissionEvaluator.setConstraintCode(attributeSet.get(SecKimAttributes.CONSTRAINT_CODE));
        accessPermissionEvaluator.setOperatorCode(attributeSet.get(SecKimAttributes.OPERATOR));
        accessPermissionEvaluator.setPropertyValue(attributeSet.get(SecKimAttributes.PROPERTY_VALUE));
        accessPermissionEvaluator.setOtherKeyFieldValueMap(otherKeyValues);
        accessPermissionEvaluator.setPerson(person);

        return accessPermissionEvaluator;
    }

    /**
     * Helper method to check system parameter that turns access security on/off
     * 
     * @return boolean indicating whether access security is turned on (true) or off (false)
     */
    protected boolean isAccessSecurityEnabled() {
        return parameterService.getIndicatorParameter(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ENABLE_ACCESS_SECURITY);
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * 
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

    /**
     * Sets the parameterService attribute value.
     * 
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    /**
     * Sets the permissionService attribute value.
     * 
     * @param permissionService The permissionService to set.
     */
    public void setPermissionService(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    /**
     * Sets the roleManagementService attribute value.
     * 
     * @param roleManagementService The roleManagementService to set.
     */
    public void setRoleManagementService(RoleManagementService roleManagementService) {
        this.roleManagementService = roleManagementService;
    }

    /**
     * Sets the contractsAndGrantsModuleService attribute value.
     * 
     * @param contractsAndGrantsModuleService The contractsAndGrantsModuleService to set.
     */
    public void setContractsAndGrantsModuleService(ContractsAndGrantsModuleService contractsAndGrantsModuleService) {
        this.contractsAndGrantsModuleService = contractsAndGrantsModuleService;
    }

}
