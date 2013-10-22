/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.sec.service.impl;

import org.apache.commons.lang.StringUtils;
import org.apache.cxf.common.i18n.Exception;
import org.apache.xpath.operations.String;
import org.hibernate.engine.Collections;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleService;
import org.kuali.kfs.sec.SecConstants;
import org.kuali.kfs.sec.SecConstants.SecurityTemplateNames;
import org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo;
import org.kuali.kfs.sec.datadictionary.AccessSecurityAttributeRestrictionEntry;
import org.kuali.kfs.sec.identity.SecKimAttributes;
import org.kuali.kfs.sec.service.AccessPermissionEvaluator;
import org.kuali.kfs.sec.service.AccessSecurityService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.businessobject.ReportBusinessObject;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.businessobject.TargetAccountingLine;
import org.kuali.kfs.sys.businessobject.datadictionary.FinancialSystemBusinessObjectEntry;
import org.kuali.kfs.sys.document.AccountingDocument;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.common.template.Template;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.permission.Permission;
import org.kuali.rice.kim.api.permission.PermissionService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;


/**
 * @see org.kuali.kfs.sec.service.AccessSecurityService
 */
public class AccessSecurityServiceImpl implements AccessSecurityService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AccessSecurityServiceImpl.class);

    protected DataDictionaryService dataDictionaryService;
    protected ParameterService parameterService;
    private PermissionService permissionService;
    private RoleService roleService;
    protected ContractsAndGrantsModuleService contractsAndGrantsModuleService;
    protected ConfigurationService configurationService;

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#applySecurityRestrictionsForGLInquiry(java.util.List,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public void applySecurityRestrictionsForGLInquiry(List<? extends BusinessObject> results, Person person) {
        applySecurityRestrictions(results, person, getInquiryWithFieldValueTemplate(), Collections.singletonMap(KimConstants.AttributeConstants.NAMESPACE_CODE, KFSConstants.CoreModuleNamespaces.GL));
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#applySecurityRestrictionsForLaborInquiry(java.util.List,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public void applySecurityRestrictionsForLaborInquiry(List<? extends BusinessObject> results, Person person) {
        applySecurityRestrictions(results, person, getInquiryWithFieldValueTemplate(), Collections.singletonMap(KimConstants.AttributeConstants.NAMESPACE_CODE, KFSConstants.OptionalModuleNamespaces.LABOR_DISTRIBUTION));
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#applySecurityRestrictionsForLookup(java.util.List,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public void applySecurityRestrictionsForLookup(List<? extends BusinessObject> results, Person person) {
        applySecurityRestrictions(results, person, getLookupWithFieldValueTemplate(), null);
    }

    /**
     * Retrieves any setup security permissions for the given person and evaluates against List of business objects. Any instances
     * not passing validation are removed from given list.
     *
     * @param results List of business object instances with data to check
     * @param person Person to apply security for
     * @param templateId KIM template id for permissions to check
     * @param additionalPermissionDetails Any additional details that should be matched on when retrieving permissions
     */
    @Override
    public void applySecurityRestrictions(List<? extends BusinessObject> results, Person person, Template permissionTemplate, Map<String,String> additionalPermissionDetails) {
        if (!isAccessSecurityEnabled()) {
            return;
        }

        if (results == null || results.isEmpty()) {
            return;
        }

        // evaluate permissions against business object instances
        List<BusinessObject> restrictedRecords = new ArrayList<BusinessObject>();
        for (BusinessObject businessObject : results) {
            boolean accessAllowed = evaluateSecurityPermissionsByTemplate(businessObject, businessObject.getClass(), person, permissionTemplate, additionalPermissionDetails, new HashMap<String,String>());
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
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canEditDocumentAccountingLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.api.identity.Person,
     *      org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo)
     */
    @Override
    public boolean canEditDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person, AccessSecurityRestrictionInfo restrictionInfo) {
        // check for edit line overrides
        boolean meetsOverrideCondition = checkForEditLineOverrides(document, accountingLine, person);
        if (meetsOverrideCondition) {
            return true;
        }

        Class<?> entryClass = SourceAccountingLine.class;
        if (TargetAccountingLine.class.isAssignableFrom(accountingLine.getClass())) {
            entryClass = TargetAccountingLine.class;
        }

        if (restrictionInfo != null) {
            restrictionInfo.setDocumentNumber(document.getDocumentNumber());
        }

        return evaluateSecurityPermissionsByTemplate(accountingLine, entryClass, person, getEditAccountingLineWithFieldValueTemplate(), getDocumentTypeDetail(document), restrictionInfo);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canEditDocumentAccountingLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canEditDocumentAccountingLine(AccountingDocument document, AccountingLine accountingLine, Person person) {
        return canEditDocumentAccountingLine(document, accountingLine, person, new AccessSecurityRestrictionInfo());
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canViewDocument(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.rice.kim.api.identity.Person, org.kuali.kfs.sec.businessobject.AccessSecurityRestrictionInfo)
     */
    @Override
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

        return evaluateSecurityOnAccountingLinesByTemplate(document, person, getViewDocumentWithFieldValueTemplate(), restrictionInfo);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canViewDocumentAccountingLine(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.kfs.sys.businessobject.AccountingLine, org.kuali.rice.kim.api.identity.Person)
     */
    @Override
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

        return evaluateSecurityPermissionsByTemplate(accountingLine, entryClass, person, getViewAccountingLineWithFieldValueTemplate(), getDocumentTypeDetail(document), null);
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#canViewDocumentNotesAttachments(org.kuali.kfs.sys.document.AccountingDocument,
     *      org.kuali.rice.kim.api.identity.Person)
     */
    @Override
    public boolean canViewDocumentNotesAttachments(AccountingDocument document, Person person) {
        // check for parameter overrides
        boolean meetsOverrideCondition = checkForViewDocumentOverrides(document, person);
        if (meetsOverrideCondition) {
            return true;
        }

        return evaluateSecurityOnAccountingLinesByTemplate(document, person, getViewNotesAttachmentsWithFieldValueTemplate(), null);
    }

    /**
     * Iterates through source and target accounting lines for the given document and evaluates any permissions with the given
     * template id against the accounting line values
     *
     * @param document AccountingDocument instance with accounting lines to check, doc type of instance is used for retrieving
     *        permissions
     * @param person the user who we are checking access for
     * @param templateId KIM template id for the permissions to check
     * @param restrictionInfo Object providing information on a restriction if one is found
     * @return boolean true if all accounting lines pass permissions, false if at least one accounting line does not pass
     */
    protected boolean evaluateSecurityOnAccountingLinesByTemplate(AccountingDocument document, Person person, Template permissionTemplate, AccessSecurityRestrictionInfo restrictionInfo) {
        boolean success = true;

        if (!isAccessSecurityEnabled()) {
            return success;
        }

        Map<String,String> permissionDetails = getDocumentTypeDetail(document);

        // check source lines
        for ( AccountingLine accountingLine : (List<AccountingLine>)document.getSourceAccountingLines() ) {

            // check for overrides
            boolean meetsOverrideCondition = false;
            if (permissionTemplate.getId().equals(getViewDocumentWithFieldValueTemplate().getId())) {
                meetsOverrideCondition = checkForViewLineOverrides(document, accountingLine, person);
            }
            else {
                meetsOverrideCondition = checkForEditLineOverrides(document, accountingLine, person);
            }

            if (meetsOverrideCondition) {
                continue;
            }

            success = evaluateSecurityPermissionsByTemplate(accountingLine, SourceAccountingLine.class, person, permissionTemplate, permissionDetails, restrictionInfo);
            if (!success) {
                break;
            }
        }

        // if source lines are ok, check target lines
        if (success) {
            for ( AccountingLine accountingLine : (List<AccountingLine>)document.getTargetAccountingLines() ) {
                // check for overrides
                boolean meetsOverrideCondition = false;
                if (permissionTemplate.equals(getViewDocumentWithFieldValueTemplate())) {
                    meetsOverrideCondition = checkForViewLineOverrides(document, accountingLine, person);
                }
                else {
                    meetsOverrideCondition = checkForEditLineOverrides(document, accountingLine, person);
                }

                if (meetsOverrideCondition) {
                    continue;
                }

                success = evaluateSecurityPermissionsByTemplate(accountingLine, TargetAccountingLine.class, person, permissionTemplate, permissionDetails, restrictionInfo);
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
        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();

        return workflowDocument.isApprovalRequested() || workflowDocument.isAcknowledgeRequested() || workflowDocument.isFYIRequested();
    }

    /**
     * Checks parameter overrides for view document permission. Currently only have initiator override parameter
     *
     * @param document Document that we are checking permissions for
     * @param person Person we are checking permissions for
     * @return boolean true if overrides are turned on and the person meets the override conditions, false if overrides are not
     *         turned on or the person does not meet condition
     */
    protected boolean checkForViewDocumentOverrides(AccountingDocument document, Person person) {
        boolean alwaysAllowInitiatorAccess = parameterService.getParameterValueAsBoolean(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_INITIATOR_DOCUMENT_ACCESS_IND);
        if (alwaysAllowInitiatorAccess) {
            WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
            if (StringUtils.equals(workflowDocument.getInitiatorPrincipalId(), person.getPrincipalId())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Checks parameter overrides for view line permission. Currently only have initiator, fiscal officer, and principal
     * investigator overrides
     *
     * @param document Document that we are checking permissions for
     * @param person Person we are checking permissions for
     * @param line AccountingLine we are checking permissions for
     * @return boolean true if any override is turned on and the person meets the override conditions, false otherwise
     */
    protected boolean checkForViewLineOverrides(AccountingDocument document, AccountingLine line, Person person) {
        // initiator override
        boolean alwaysAllowInitiatorAccess = parameterService.getParameterValueAsBoolean(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_INITIATOR_LINE_ACCESS_IND);
        if (alwaysAllowInitiatorAccess) {
            WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
            if (StringUtils.equals(workflowDocument.getInitiatorPrincipalId(), person.getPrincipalId())) {
                return true;
            }
        }

        // account must be not be empty for further override checks
        if (line.getAccount() == null) {
            return false;
        }

        // fiscal officer override
        boolean alwaysAllowFOAccess = parameterService.getParameterValueAsBoolean(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_FISCAL_OFFICER_LINE_ACCESS_IND);
        if (alwaysAllowFOAccess) {
            if (ObjectUtils.isNotNull(line.getAccount())) {
                if (StringUtils.equals(line.getAccount().getAccountFiscalOfficerSystemIdentifier(), person.getPrincipalId())) {
                    return true;
                }
            }
        }

        // pi override
        boolean alwaysAllowPIAccess = parameterService.getParameterValueAsBoolean(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_PRINCIPAL_INVESTIGATOR_LINE_ACCESS_IND);
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
        // account must not be empty for override checks
        if (ObjectUtils.isNull(line.getAccount()) || line.getAccountNumber() == null) {
            return false;
        }

        // fiscal officer override
        boolean alwaysAllowFOAccess = parameterService.getParameterValueAsBoolean(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_FISCAL_OFFICER_LINE_ACCESS_IND);
        if (alwaysAllowFOAccess) {
            if (StringUtils.equals(line.getAccount().getAccountFiscalOfficerSystemIdentifier(), person.getPrincipalId())) {
                return true;
            }
        }

        // pi override
        boolean alwaysAllowPIAccess = parameterService.getParameterValueAsBoolean(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ALWAYS_ALLOW_PRINCIPAL_INVESTIGATOR_LINE_ACCESS_IND);
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
    protected boolean evaluateSecurityPermissionsByTemplate(BusinessObject businessObject, Class entryClass, Person person, Template permissionTemplate, Map<String,String> additionalPermissionDetails, AccessSecurityRestrictionInfo restrictionInfo) {
        boolean success = true;

        if (!isAccessSecurityEnabled()) {
            return success;
        }

        // get all configured restricted attributes for this business object through it data dictionary entry
        FinancialSystemBusinessObjectEntry businessObjectEntry = (FinancialSystemBusinessObjectEntry) dataDictionaryService.getDataDictionary().getBusinessObjectEntryForConcreteClass(entryClass.getName());

        //if the business object is of class ReportBusinessObject interface, use refreshNonUpdateableForReport();
        if (ReportBusinessObject.class.isAssignableFrom(businessObject.getClass())) {
            ((ReportBusinessObject) businessObject).refreshNonUpdateableForReport();
        }
        else if (PersistableBusinessObject.class.isAssignableFrom(businessObject.getClass())) {
            ((PersistableBusinessObject) businessObject).refreshNonUpdateableReferences();
        } else {
            businessObject.refresh();
        }

        for (AccessSecurityAttributeRestrictionEntry accessRestrictedAttribute : businessObjectEntry.getAccessRestrictedAttributes()) {
            Map<String,String> permissionDetails = new HashMap<String,String>();
            permissionDetails.put(KimConstants.AttributeConstants.PROPERTY_NAME, accessRestrictedAttribute.getSecurityAttributeName());

            if (additionalPermissionDetails != null) {
                permissionDetails.putAll(additionalPermissionDetails);
            }

            List<Permission> permissions = getPermissionService().getAuthorizedPermissionsByTemplate(person.getPrincipalId(), permissionTemplate.getNamespaceCode(), permissionTemplate.getName(), permissionDetails, new HashMap<String, String>(0));
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
     * Constructs a new Map<String,String> and adds document type name detail with value from document instance
     *
     * @param document AccountingDocument instance which document type will be set from
     * @return Map<String,String> containing document type name detail
     */
    protected Map<String,String> getDocumentTypeDetail(AccountingDocument document) {
        Map<String,String> details = new HashMap<String,String>();
        details.put(KimConstants.AttributeConstants.DOCUMENT_TYPE_NAME, document.getFinancialDocumentTypeCode());

        return details;
    }

    /**
     * Checks whether the given value is allowed based on the given permissions and user
     *
     * @param accessPermissionEvaluatorClass Class of type AccessPermissionEvaluator that will be used to evaluate the security
     *        restriction
     * @param permissions List of permissions to evaluate
     * @param value the value that will be checked
     * @param person the user who we are checking access for
     * @param otherKeyValues Map for other key field name/value pairs
     * @return boolean true if access is allowed false if not
     */
    protected boolean evaluateSecurityPermissions(Class<? extends AccessPermissionEvaluator> accessPermissionEvaluatorClass, List<Permission> permissions, String value, Person person, Map<String, Object> otherKeyValues) {
        boolean success = true;

        List<Map<String,String>> qualficationsToEvaluate = new ArrayList<Map<String,String>>();
        for (Permission permission : permissions) {
            // find all roles that have been granted this permission
            List<String> roleIds = getPermissionService().getRoleIdsForPermission(permission.getNamespaceCode(), permission.getName() );

            // for all the roles that have this permission, find the users qualification in those roles (if any)
            List<Map<String,String>> qualfications = getRoleService().getNestedRoleQualifiersForPrincipalByRoleIds(person.getPrincipalId(), roleIds, null);

            if (qualfications != null) {
                qualficationsToEvaluate.addAll(qualfications);
            }
        }

        // cycle through the users qualifications and evaluate against the given value
        boolean hasAllowQualification = false;
        boolean allowQualificationSuccess = false;
        boolean hasDenyFailure = false;
        boolean hasAllowOverride = false;
        for (Map<String,String> attributeSet : qualficationsToEvaluate) {
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

        if ((hasDenyFailure && !hasAllowOverride) || (hasAllowQualification && !allowQualificationSuccess)) {
            success = false;
        }

        return success;
    }

    /**
     * Constructs a new instance of the AccessPermissionEvaluator class and sets the constraint, operator, and value based on the
     * given qualification
     *
     * @param accessPermissionEvaluatorClass Class to create instance of (must implement AccessPermissionEvaluator interface)
     * @param attributeSet Map<String,String> containing the qualifier values
     * @param otherKeyValues Map for other key field name/value pairs
     * @param person Person who permission should be evaluated for
     * @return new instance of type AccessPermissionEvaluator
     * @see org.kuali.kfs.sec.service.AccessPermissionEvaluator
     */
    protected AccessPermissionEvaluator constructAccessPermissionEvaluator(Class<? extends AccessPermissionEvaluator> accessPermissionEvaluatorClass, Map<String,String> attributeSet, Map<String, Object> otherKeyValues, Person person) {
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
        return parameterService.getParameterValueAsBoolean(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ENABLE_ACCESS_SECURITY);
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

    public void setConfigurationService(ConfigurationService configurationService) {
        this.configurationService = configurationService;
    }

    public PermissionService getPermissionService() {
        if ( permissionService == null ) {
            permissionService = KimApiServiceLocator.getPermissionService();
        }
        return permissionService;
    }

    public RoleService getRoleService() {
        if ( roleService == null ) {
            roleService = KimApiServiceLocator.getRoleService();
        }
        return roleService;
    }

    /**
     * Sets the contractsAndGrantsModuleService attribute value.
     *
     * @param contractsAndGrantsModuleService The contractsAndGrantsModuleService to set.
     */
    public void setContractsAndGrantsModuleService(ContractsAndGrantsModuleService contractsAndGrantsModuleService) {
        this.contractsAndGrantsModuleService = contractsAndGrantsModuleService;
    }

    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#getEditAccountingLineWithFieldValueTemplateId()
     */
    @Override
    public Template getEditAccountingLineWithFieldValueTemplate() {
        Template templateInfo = getPermissionService().findPermTemplateByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, SecurityTemplateNames.EDIT_ACCOUNTING_LINE_FIELD_VALUE);
        if ( templateInfo != null ) {
            return templateInfo;
        } else {
            throw new RuntimeException(SecurityTemplateNames.EDIT_ACCOUNTING_LINE_FIELD_VALUE + " parameter does not exist");
        }
    }


    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#getEditDocumentWithFieldValueTemplateId()
     */
    @Override
    public Template getEditDocumentWithFieldValueTemplate() {
        Template templateInfo = getPermissionService().findPermTemplateByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, SecurityTemplateNames.EDIT_DOCUMENT_FIELD_VALUE);
        if ( templateInfo != null ) {
            return templateInfo;
        } else {
            throw new RuntimeException(SecurityTemplateNames.EDIT_DOCUMENT_FIELD_VALUE + " parameter does not exist");
        }
    }


    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#getInquiryWithFieldValueTemplateId()
     */
    @Override
    public Template getInquiryWithFieldValueTemplate() {
        Template templateInfo = getPermissionService().findPermTemplateByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, SecurityTemplateNames.INQUIRY_FIELD_VALUE);
        if ( templateInfo != null ) {
            return templateInfo;
        } else {
            throw new RuntimeException(SecurityTemplateNames.INQUIRY_FIELD_VALUE + " parameter does not exist");
        }
    }


    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#getLookupWithFieldValueTemplateId()
     */
    @Override
    public Template getLookupWithFieldValueTemplate() {
        Template templateInfo = getPermissionService().findPermTemplateByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, SecurityTemplateNames.LOOKUP_FIELD_VALUE);
        if ( templateInfo != null ) {
            return templateInfo;
        } else {
            throw new RuntimeException(SecurityTemplateNames.LOOKUP_FIELD_VALUE + " parameter does not exist");
        }
    }


    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#getViewAccountingLineWithFieldValueTemplateId()
     */
    @Override
    public Template getViewAccountingLineWithFieldValueTemplate() {
        Template templateInfo = getPermissionService().findPermTemplateByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, SecurityTemplateNames.VIEW_ACCOUNTING_LINE_FIELD_VALUE);
        if ( templateInfo != null ) {
            return templateInfo;
        } else {
            throw new RuntimeException(SecurityTemplateNames.VIEW_ACCOUNTING_LINE_FIELD_VALUE + " parameter does not exist");
        }
    }


    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#getViewDocumentWithFieldValueTemplateId()
     */
    @Override
    public Template getViewDocumentWithFieldValueTemplate() {
        Template templateInfo = getPermissionService().findPermTemplateByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, SecurityTemplateNames.VIEW_DOCUMENT_FIELD_VALUE);
        if ( templateInfo != null ) {
            return templateInfo;
        } else {
            throw new RuntimeException(SecurityTemplateNames.VIEW_DOCUMENT_FIELD_VALUE + " parameter does not exist");
        }
    }


    /**
     * @see org.kuali.kfs.sec.service.AccessSecurityService#getViewNotesAttachmentsWithFieldValueTemplateId()
     */
    @Override
    public Template getViewNotesAttachmentsWithFieldValueTemplate() {
        Template templateInfo = getPermissionService().findPermTemplateByNamespaceCodeAndName(KFSConstants.CoreModuleNamespaces.ACCESS_SECURITY, SecurityTemplateNames.VIEW_NOTES_ATTACHMENTS_FIELD_VALUE);
        if ( templateInfo != null ) {
            return templateInfo;
        } else {
            throw new RuntimeException(SecurityTemplateNames.VIEW_NOTES_ATTACHMENTS_FIELD_VALUE + " parameter does not exist");
        }
    }

    /**
     * Calls access security service to check view access on given GLPE for current user. Access to view the GLPE on the document should be related to the view permissions for an
     * accounting line with the same account attributes. Called from generalLedgerPendingEntries.tag
     *
     * @param pendingEntry GeneralLedgerPendingEntry to check access for
     * @return boolean true if given user has view permission, false otherwise
     */
    @Override
    public boolean canViewGLPE(Document document, GeneralLedgerPendingEntry pendingEntry, Person person) {
        boolean canView = true;

        // If the module has not been loaded, then just skip any further checks as the services will not be defined
        if ( configurationService.getPropertyValueAsBoolean(SecConstants.ACCESS_SECURITY_MODULE_ENABLED_PROPERTY_NAME) ) {
            if (document instanceof AccountingDocument) {
                AccountingLine line = new SourceAccountingLine();

                line.setPostingYear(pendingEntry.getUniversityFiscalYear());
                line.setChartOfAccountsCode(pendingEntry.getChartOfAccountsCode());
                line.setAccountNumber(pendingEntry.getAccountNumber());
                line.setSubAccountNumber(pendingEntry.getSubAccountNumber());
                line.setFinancialObjectCode(pendingEntry.getFinancialObjectCode());
                line.setFinancialSubObjectCode(pendingEntry.getFinancialSubObjectCode());
                line.setProjectCode(pendingEntry.getProjectCode());

                line.refreshNonUpdateableReferences();

                canView = canViewDocumentAccountingLine((AccountingDocument) document, line, GlobalVariables.getUserSession().getPerson());
            }
        }

        return canView;
    }

    /**
     * Compares the size of the given list against the given previous size and if different adds an info message
     *
     * @param previousListSize int giving previous size of list to compare to
     * @param results List to get size for and compare
     * @param messageKey String key of message that should be added
     */
    @Override
    public void compareListSizeAndAddMessageIfChanged(int previousListSize, List<?> results, String messageKey) {
        int currentListSize = results.size();

        if (previousListSize != currentListSize) {
            GlobalVariables.getMessageMap().putInfo(KFSConstants.GLOBAL_MESSAGES, messageKey, Integer.toString(previousListSize - currentListSize));
        }
    }

    @Override
    public Collection<String> getAccessSecurityControlledDocumentTypeNames() {
        return parameterService.getParameterValuesAsString(SecConstants.ACCESS_SECURITY_NAMESPACE_CODE, SecConstants.ALL_PARAMETER_DETAIL_COMPONENT, SecConstants.SecurityParameterNames.ACCESS_SECURITY_DOCUMENT_TYPES);
    }

    @Override
    public boolean isAccessSecurityControlledDocumentType(String documentTypeName) {
        return getAccessSecurityControlledDocumentTypeNames().contains(documentTypeName);
    }
}
