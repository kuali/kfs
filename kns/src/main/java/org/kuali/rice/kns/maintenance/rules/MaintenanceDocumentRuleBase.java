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
package org.kuali.rice.kns.maintenance.rules;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.rice.core.api.CoreApiServiceLocator;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.mo.common.active.MutableInactivatable;
import org.kuali.rice.core.api.util.RiceKeyConstants;
import org.kuali.rice.core.web.format.Formatter;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.rule.AddCollectionLineRule;
import org.kuali.rice.kns.rules.DocumentRuleBase;
import org.kuali.rice.kns.rules.MaintenanceDocumentRule;
import org.kuali.rice.kns.service.BusinessObjectAuthorizationService;
import org.kuali.rice.kns.service.BusinessObjectDictionaryService;
import org.kuali.rice.kns.service.BusinessObjectMetaDataService;
import org.kuali.rice.kns.service.DictionaryValidationService;
import org.kuali.rice.kns.service.DocumentHelperService;
import org.kuali.rice.kns.service.KNSServiceLocator;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.kns.util.RouteToCompletionUtil;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.GlobalBusinessObject;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.InactivationBlockingMetadata;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.exception.ValidationException;
import org.kuali.rice.krad.rules.rule.event.ApproveDocumentEvent;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;
import org.kuali.rice.krad.service.DataObjectMetaDataService;
import org.kuali.rice.krad.service.InactivationBlockingDetectionService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.KRADServiceLocatorWeb;
import org.kuali.rice.krad.service.PersistenceStructureService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.ForeignKeyFieldsPopulationState;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.KRADPropertyConstants;
import org.kuali.rice.krad.util.MessageMap;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.util.UrlFactory;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.util.AutoPopulatingList;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

/**
 * Contains all of the business rules that are common to all maintenance documents
 *
 * @author Kuali Rice Team (rice.collab@kuali.org)
 */
public class MaintenanceDocumentRuleBase extends DocumentRuleBase implements MaintenanceDocumentRule, AddCollectionLineRule {
    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(MaintenanceDocumentRuleBase.class);

    // these two constants are used to correctly prefix errors added to
    // the global errors
    public static final String MAINTAINABLE_ERROR_PREFIX = KRADConstants.MAINTENANCE_NEW_MAINTAINABLE;
    public static final String DOCUMENT_ERROR_PREFIX = "document.";
    public static final String MAINTAINABLE_ERROR_PATH = DOCUMENT_ERROR_PREFIX + "newMaintainableObject";

    protected PersistenceStructureService persistenceStructureService;
    protected DataDictionaryService ddService;
    protected DocumentHelperService documentHelperService;
    protected BusinessObjectService boService;
    protected DictionaryValidationService dictionaryValidationService;
    protected ConfigurationService configService;
    protected MaintenanceDocumentDictionaryService maintDocDictionaryService;
    protected WorkflowDocumentService workflowDocumentService;
    protected PersonService personService;
    protected RoleService roleService;
    protected DataObjectMetaDataService dataObjectMetaDataService;
    protected BusinessObjectAuthorizationService businessObjectAuthorizationService;
    protected BusinessObjectMetaDataService businessObjectMetaDataService;
    protected BusinessObjectDictionaryService boDictionaryService;

    private Object oldBo;
    private Object newBo;
    private Class boClass;

    protected List priorErrorPath;

    /**
     * Default constructor a MaintenanceDocumentRuleBase.java.
     */
    public MaintenanceDocumentRuleBase() {

        priorErrorPath = new ArrayList();

        // Pseudo-inject some services.
        //
        // This approach is being used to make it simpler to convert the Rule classes
        // to spring-managed with these services injected by Spring at some later date.
        // When this happens, just remove these calls to the setters with
        // SpringServiceLocator, and configure the bean defs for spring.
        try {
            this.setPersistenceStructureService(KRADServiceLocator.getPersistenceStructureService());
            this.setDdService(KRADServiceLocatorWeb.getDataDictionaryService());
            this.setBusinessObjectMetaDataService(KNSServiceLocator.getBusinessObjectMetaDataService());
            this.setBoService(KRADServiceLocator.getBusinessObjectService());
            this.setBoDictionaryService(KNSServiceLocator.getBusinessObjectDictionaryService());
            this.setDictionaryValidationService(super.getDictionaryValidationService());
            this.setConfigService(KRADServiceLocator.getKualiConfigurationService());
            this.setDocumentHelperService(KNSServiceLocator.getDocumentHelperService());
            this.setMaintDocDictionaryService(KNSServiceLocator.getMaintenanceDocumentDictionaryService());
            this.setWorkflowDocumentService(KRADServiceLocatorWeb.getWorkflowDocumentService());
            this.setPersonService(KimApiServiceLocator.getPersonService());
            this.setBusinessObjectAuthorizationService(KNSServiceLocator.getBusinessObjectAuthorizationService());
        } catch (Exception ex) {
            // do nothing, avoid blowing up if called prior to spring initialization
        }
    }

    /**
     * @see org.kuali.rice.krad.rules.MaintenanceDocumentRule#processSaveDocument(Document)
     */
    @Override
    public boolean processSaveDocument(Document document) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        // remove all items from the errorPath temporarily (because it may not
        // be what we expect, or what we need)
        clearErrorPath();

        // setup convenience pointers to the old & new bo
        setupBaseConvenienceObjects(maintenanceDocument);

        // the document must be in a valid state for saving. this does not include business
        // rules, but just enough testing that the document is populated and in a valid state
        // to not cause exceptions when saved. if this passes, then the save will always occur,
        // regardless of business rules.
        if (!isDocumentValidForSave(maintenanceDocument)) {
            resumeErrorPath();
            return false;
        }

        // apply rules that are specific to the class of the maintenance document
        // (if implemented). this will always succeed if not overloaded by the
        // subclass
        processCustomSaveDocumentBusinessRules(maintenanceDocument);

        // return the original set of items to the errorPath
        resumeErrorPath();

        // return the original set of items to the errorPath, to ensure no impact
        // on other upstream or downstream items that rely on the errorPath
        return true;
    }

    /**
     * @see org.kuali.rice.krad.rules.MaintenanceDocumentRule#processRouteDocument(Document)
     */
    @Override
    public boolean processRouteDocument(Document document) {
        LOG.info("processRouteDocument called");

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) document;

        boolean completeRequestPending = RouteToCompletionUtil.checkIfAtleastOneAdHocCompleteRequestExist(maintenanceDocument);

        // Validate the document if the header is valid and no pending completion requests
        if (completeRequestPending) {
            return true;
        }
        
        // get the documentAuthorizer for this document
        MaintenanceDocumentAuthorizer documentAuthorizer =
                (MaintenanceDocumentAuthorizer) getDocumentHelperService().getDocumentAuthorizer(document);

        // remove all items from the errorPath temporarily (because it may not
        // be what we expect, or what we need)
        clearErrorPath();

        // setup convenience pointers to the old & new bo
        setupBaseConvenienceObjects(maintenanceDocument);

        // apply rules that are common across all maintenance documents, regardless of class
        processGlobalSaveDocumentBusinessRules(maintenanceDocument);

        // from here on, it is in a default-success mode, and will route unless one of the
        // business rules stop it.
        boolean success = true;

        WorkflowDocument workflowDocument = document.getDocumentHeader().getWorkflowDocument();
        if (workflowDocument.isInitiated() || workflowDocument.isSaved()) {
            success &= documentAuthorizer
                    .canCreateOrMaintain((MaintenanceDocument) document, GlobalVariables.getUserSession().getPerson());
            if (success == false) {
                GlobalVariables.getMessageMap()
                        .putError(KRADConstants.DOCUMENT_ERRORS, RiceKeyConstants.AUTHORIZATION_ERROR_DOCUMENT,
                                new String[]{GlobalVariables.getUserSession().getPerson().getPrincipalName(),
                                        "Create/Maintain",
                                        this.getMaintDocDictionaryService().getDocumentTypeName(newBo.getClass())});
            }
        }
        // apply rules that are common across all maintenance documents, regardless of class
        success &= processGlobalRouteDocumentBusinessRules(maintenanceDocument);

        // apply rules that are specific to the class of the maintenance document
        // (if implemented). this will always succeed if not overloaded by the
        // subclass
        success &= processCustomRouteDocumentBusinessRules(maintenanceDocument);

        success &= processInactivationBlockChecking(maintenanceDocument);

        // return the original set of items to the errorPath, to ensure no impact
        // on other upstream or downstream items that rely on the errorPath
        resumeErrorPath();

        return success;
    }

    /**
     * Determines whether a document is inactivating the record being maintained
     *
     * @param maintenanceDocument
     * @return true iff the document is inactivating the business object; false otherwise
     */
    protected boolean isDocumentInactivatingBusinessObject(MaintenanceDocument maintenanceDocument) {
        if (maintenanceDocument.isEdit()) {
            Class boClass = maintenanceDocument.getNewMaintainableObject().getDataObjectClass();
            // we can only be inactivating a business object if we're editing it
            if (boClass != null && MutableInactivatable.class.isAssignableFrom(boClass)) {
                MutableInactivatable oldInactivateableBO = (MutableInactivatable) oldBo;
                MutableInactivatable newInactivateableBO = (MutableInactivatable) newBo;

                return oldInactivateableBO.isActive() && !newInactivateableBO.isActive();
            }
        }
        return false;
    }

    /**
     * Determines whether this document has been inactivation blocked
     *
     * @param maintenanceDocument
     * @return true iff there is NOTHING that blocks this record
     */
    protected boolean processInactivationBlockChecking(MaintenanceDocument maintenanceDocument) {
        if (isDocumentInactivatingBusinessObject(maintenanceDocument)) {
            Class boClass = maintenanceDocument.getNewMaintainableObject().getDataObjectClass();
            Set<InactivationBlockingMetadata> inactivationBlockingMetadatas =
                    ddService.getAllInactivationBlockingDefinitions(boClass);

            if (inactivationBlockingMetadatas != null) {
                for (InactivationBlockingMetadata inactivationBlockingMetadata : inactivationBlockingMetadatas) {
                    // for the purposes of maint doc validation, we only need to look for the first blocking record

                    // we found a blocking record, so we return false
                    if (!processInactivationBlockChecking(maintenanceDocument, inactivationBlockingMetadata)) {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    /**
     * Given a InactivationBlockingMetadata, which represents a relationship that may block inactivation of a BO, it
     * determines whether there
     * is a record that violates the blocking definition
     *
     * @param maintenanceDocument
     * @param inactivationBlockingMetadata
     * @return true iff, based on the InactivationBlockingMetadata, the maintenance document should be allowed to route
     */
    protected boolean processInactivationBlockChecking(MaintenanceDocument maintenanceDocument,
            InactivationBlockingMetadata inactivationBlockingMetadata) {
        if (newBo instanceof PersistableBusinessObject) {
            String inactivationBlockingDetectionServiceBeanName =
                    inactivationBlockingMetadata.getInactivationBlockingDetectionServiceBeanName();
            if (StringUtils.isBlank(inactivationBlockingDetectionServiceBeanName)) {
                inactivationBlockingDetectionServiceBeanName =
                        KRADServiceLocatorWeb.DEFAULT_INACTIVATION_BLOCKING_DETECTION_SERVICE;
            }
            InactivationBlockingDetectionService inactivationBlockingDetectionService = KRADServiceLocatorWeb
                    .getInactivationBlockingDetectionService(inactivationBlockingDetectionServiceBeanName);

            boolean foundBlockingRecord = inactivationBlockingDetectionService
                    .hasABlockingRecord((PersistableBusinessObject) newBo, inactivationBlockingMetadata);

            if (foundBlockingRecord) {
                putInactivationBlockingErrorOnPage(maintenanceDocument, inactivationBlockingMetadata);
            }

            return !foundBlockingRecord;
        }

        return true;
    }

    /**
     * If there is a violation of an InactivationBlockingMetadata, it prints out an appropriate error into the error
     * map
     *
     * @param document
     * @param inactivationBlockingMetadata
     */
    protected void putInactivationBlockingErrorOnPage(MaintenanceDocument document,
            InactivationBlockingMetadata inactivationBlockingMetadata) {
        if (!persistenceStructureService.hasPrimaryKeyFieldValues(newBo)) {
            throw new RuntimeException("Maintenance document did not have all primary key values filled in.");
        }
        Properties parameters = new Properties();
        parameters.put(KRADConstants.BUSINESS_OBJECT_CLASS_ATTRIBUTE,
                inactivationBlockingMetadata.getBlockedBusinessObjectClass().getName());
        parameters
                .put(KRADConstants.DISPATCH_REQUEST_PARAMETER, KRADConstants.METHOD_DISPLAY_ALL_INACTIVATION_BLOCKERS);

        List keys = new ArrayList();
        if (getPersistenceStructureService().isPersistable(newBo.getClass())) {
            keys = getPersistenceStructureService().listPrimaryKeyFieldNames(newBo.getClass());
        }

        // build key value url parameters used to retrieve the business object
        String keyName = null;
        for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
            keyName = (String) iter.next();

            Object keyValue = null;
            if (keyName != null) {
                keyValue = ObjectUtils.getPropertyValue(newBo, keyName);
            }

            if (keyValue == null) {
                keyValue = "";
            } else if (keyValue instanceof java.sql.Date) { //format the date for passing in url
                if (Formatter.findFormatter(keyValue.getClass()) != null) {
                    Formatter formatter = Formatter.getFormatter(keyValue.getClass());
                    keyValue = (String) formatter.format(keyValue);
                }
            } else {
                keyValue = keyValue.toString();
            }

            // Encrypt value if it is a secure field
            if (businessObjectAuthorizationService.attributeValueNeedsToBeEncryptedOnFormsAndLinks(
                    inactivationBlockingMetadata.getBlockedBusinessObjectClass(), keyName)) {
                try {
                    if(CoreApiServiceLocator.getEncryptionService().isEnabled()) {
                        keyValue = CoreApiServiceLocator.getEncryptionService().encrypt(keyValue);
                    }
                } catch (GeneralSecurityException e) {
                    LOG.error("Exception while trying to encrypted value for inquiry framework.", e);
                    throw new RuntimeException(e);
                }
            }

            parameters.put(keyName, keyValue);
        }

        String blockingUrl =
                UrlFactory.parameterizeUrl(KRADConstants.DISPLAY_ALL_INACTIVATION_BLOCKERS_ACTION, parameters);

        // post an error about the locked document
        GlobalVariables.getMessageMap()
                .putError(KRADConstants.GLOBAL_ERRORS, RiceKeyConstants.ERROR_INACTIVATION_BLOCKED, blockingUrl);
    }

    /**
     * @see org.kuali.rice.krad.rules.MaintenanceDocumentRule#processApproveDocument(ApproveDocumentEvent)
     */
    @Override
    public boolean processApproveDocument(ApproveDocumentEvent approveEvent) {

        MaintenanceDocument maintenanceDocument = (MaintenanceDocument) approveEvent.getDocument();

        // remove all items from the errorPath temporarily (because it may not
        // be what we expect, or what we need)
        clearErrorPath();

        // setup convenience pointers to the old & new bo
        setupBaseConvenienceObjects(maintenanceDocument);

        // apply rules that are common across all maintenance documents, regardless of class
        processGlobalSaveDocumentBusinessRules(maintenanceDocument);

        // from here on, it is in a default-success mode, and will approve unless one of the
        // business rules stop it.
        boolean success = true;

        // apply rules that are common across all maintenance documents, regardless of class
        success &= processGlobalApproveDocumentBusinessRules(maintenanceDocument);

        // apply rules that are specific to the class of the maintenance document
        // (if implemented). this will always succeed if not overloaded by the
        // subclass
        success &= processCustomApproveDocumentBusinessRules(maintenanceDocument);

        // return the original set of items to the errorPath, to ensure no impact
        // on other upstream or downstream items that rely on the errorPath
        resumeErrorPath();

        return success;
    }

    /**
     * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field,
     * but
     * applicable to the whole document).
     *
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     */
    protected void putGlobalError(String errorConstant) {
        if (!errorAlreadyExists(KRADConstants.DOCUMENT_ERRORS, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(KRADConstants.DOCUMENT_ERRORS, errorConstant);
        }
    }

    /**
     * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field,
     * but
     * applicable to the whole document).
     *
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameter - Replacement value for part of the error message.
     */
    protected void putGlobalError(String errorConstant, String parameter) {
        if (!errorAlreadyExists(KRADConstants.DOCUMENT_ERRORS, errorConstant)) {
            GlobalVariables.getMessageMap()
                    .putErrorWithoutFullErrorPath(KRADConstants.DOCUMENT_ERRORS, errorConstant, parameter);
        }
    }

    /**
     * This method is a convenience method to easily add a Document level error (ie, one not tied to a specific field,
     * but
     * applicable to the whole document).
     *
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameters - Array of replacement values for part of the error message.
     */
    protected void putGlobalError(String errorConstant, String[] parameters) {
        if (!errorAlreadyExists(KRADConstants.DOCUMENT_ERRORS, errorConstant)) {
            GlobalVariables.getMessageMap()
                    .putErrorWithoutFullErrorPath(KRADConstants.DOCUMENT_ERRORS, errorConstant, parameters);
        }
    }

    /**
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes
     * sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as
     * errored in
     * the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     */
    protected void putFieldError(String propertyName, String errorConstant) {
        if (!errorAlreadyExists(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getMessageMap()
                    .putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant);
        }
    }

    /**
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes
     * sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as
     * errored in
     * the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameter - Single parameter value that can be used in the message so that you can display specific values
     * to the
     * user.
     */
    protected void putFieldError(String propertyName, String errorConstant, String parameter) {
        if (!errorAlreadyExists(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getMessageMap()
                    .putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant, parameter);
        }
    }

    /**
     * This method is a convenience method to add a property-specific error to the global errors list. This method makes
     * sure that
     * the correct prefix is added to the property name so that it will display correctly on maintenance documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as
     * errored in
     * the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameters - Array of strings holding values that can be used in the message so that you can display
     * specific values
     * to the user.
     */
    protected void putFieldError(String propertyName, String errorConstant, String[] parameters) {
        if (!errorAlreadyExists(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getMessageMap()
                    .putErrorWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + propertyName, errorConstant, parameters);
        }
    }

    /**
     * Adds a property-specific error to the global errors list, with the DD short label as the single argument.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as
     * errored in
     * the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     */
    protected void putFieldErrorWithShortLabel(String propertyName, String errorConstant) {
        String shortLabel = ddService.getAttributeShortLabel(boClass, propertyName);
        putFieldError(propertyName, errorConstant, shortLabel);
    }

    /**
     * This method is a convenience method to add a property-specific document error to the global errors list. This
     * method makes
     * sure that the correct prefix is added to the property name so that it will display correctly on maintenance
     * documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as
     * errored in
     * the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameter - Single parameter value that can be used in the message so that you can display specific values
     * to the
     * user.
     */
    protected void putDocumentError(String propertyName, String errorConstant, String parameter) {
        if (!errorAlreadyExists(DOCUMENT_ERROR_PREFIX + propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + propertyName, errorConstant, parameter);
        }
    }

    /**
     * This method is a convenience method to add a property-specific document error to the global errors list. This
     * method makes
     * sure that the correct prefix is added to the property name so that it will display correctly on maintenance
     * documents.
     *
     * @param propertyName - Property name of the element that is associated with the error. Used to mark the field as
     * errored in
     * the UI.
     * @param errorConstant - Error Constant that can be mapped to a resource for the actual text message.
     * @param parameters - Array of String parameters that can be used in the message so that you can display specific
     * values to the
     * user.
     */
    protected void putDocumentError(String propertyName, String errorConstant, String[] parameters) {
        GlobalVariables.getMessageMap().putError(DOCUMENT_ERROR_PREFIX + propertyName, errorConstant, parameters);
    }

    /**
     * Convenience method to determine whether the field already has the message indicated.
     *
     * This is useful if you want to suppress duplicate error messages on the same field.
     *
     * @param propertyName - propertyName you want to test on
     * @param errorConstant - errorConstant you want to test
     * @return returns True if the propertyName indicated already has the errorConstant indicated, false otherwise
     */
    protected boolean errorAlreadyExists(String propertyName, String errorConstant) {

        if (GlobalVariables.getMessageMap().fieldHasMessage(propertyName, errorConstant)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method specifically doesn't put any prefixes before the error so that the developer can do things specific
     * to the
     * globals errors (like newDelegateChangeDocument errors)
     *
     * @param propertyName
     * @param errorConstant
     */
    protected void putGlobalsError(String propertyName, String errorConstant) {
        if (!errorAlreadyExists(propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(propertyName, errorConstant);
        }
    }

    /**
     * This method specifically doesn't put any prefixes before the error so that the developer can do things specific
     * to the
     * globals errors (like newDelegateChangeDocument errors)
     *
     * @param propertyName
     * @param errorConstant
     * @param parameter
     */
    protected void putGlobalsError(String propertyName, String errorConstant, String parameter) {
        if (!errorAlreadyExists(propertyName, errorConstant)) {
            GlobalVariables.getMessageMap().putErrorWithoutFullErrorPath(propertyName, errorConstant, parameter);
        }
    }

    /**
     * This method is used to deal with error paths that are not what we expect them to be. This method, along with
     * resumeErrorPath() are used to temporarily clear the errorPath, and then return it to the original state after the
     * rule is
     * executed.
     *
     * This method is called at the very beginning of rule enforcement and pulls a copy of the contents of the errorPath
     * ArrayList
     * to a local arrayList for temporary storage.
     */
    protected void clearErrorPath() {

        // add all the items from the global list to the local list
        priorErrorPath.addAll(GlobalVariables.getMessageMap().getErrorPath());

        // clear the global list
        GlobalVariables.getMessageMap().getErrorPath().clear();
    }

    /**
     * This method is used to deal with error paths that are not what we expect them to be. This method, along with
     * clearErrorPath()
     * are used to temporarily clear the errorPath, and then return it to the original state after the rule is
     * executed.
     *
     * This method is called at the very end of the rule enforcement, and returns the temporarily stored copy of the
     * errorPath to
     * the global errorPath, so that no other classes are interrupted.
     */
    protected void resumeErrorPath() {
        // revert the global errorPath back to what it was when we entered this
        // class
        GlobalVariables.getMessageMap().getErrorPath().addAll(priorErrorPath);
    }

    /**
     * This method executes the DataDictionary Validation against the document.
     *
     * @param document
     * @return true if it passes DD validation, false otherwise
     */
    protected boolean dataDictionaryValidate(MaintenanceDocument document) {
        LOG.debug("MaintenanceDocument validation beginning");

        // explicitly put the errorPath that the dictionaryValidationService
        // requires
        GlobalVariables.getMessageMap().addToErrorPath("document.newMaintainableObject");

        // document must have a newMaintainable object
        Maintainable newMaintainable = document.getNewMaintainableObject();
        if (newMaintainable == null) {
            GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");
            throw new ValidationException(
                    "Maintainable object from Maintenance Document '" + document.getDocumentTitle() +
                            "' is null, unable to proceed.");
        }

        // document's newMaintainable must contain an object (ie, not null)
        Object dataObject = newMaintainable.getDataObject();
        if (dataObject == null) {
            GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject.");
            throw new ValidationException("Maintainable's component business object is null.");
        }

        // if the Maintainable object is a PBO and there is a legacy maintDefinition
        // then use the old validation methods
        if (newBo instanceof PersistableBusinessObject && CollectionUtils.isNotEmpty(maintDocDictionaryService
                .getMaintainableSections(document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName()))) {

            BusinessObject businessObject = (BusinessObject) newBo;

            // run required check from maintenance data dictionary
            maintDocDictionaryService.validateMaintenanceRequiredFields(document);

            //check for duplicate entries in collections if necessary
            maintDocDictionaryService.validateMaintainableCollectionsForDuplicateEntries(document);

            // run the DD DictionaryValidation (non-recursive)
            dictionaryValidationService.validateBusinessObjectOnMaintenanceDocument(businessObject,
                    document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());

            // do default (ie, mandatory) existence checks
            dictionaryValidationService.validateDefaultExistenceChecks(businessObject);
        } else {
            GlobalVariables.getMessageMap().addToErrorPath("dataObject");

            dictionaryValidationService.validate(newBo);

            GlobalVariables.getMessageMap().removeFromErrorPath("dataObject");
        }

        // explicitly remove the errorPath we've added
        GlobalVariables.getMessageMap().removeFromErrorPath("document.newMaintainableObject");

        LOG.debug("MaintenanceDocument validation ending");
        return true;
    }

    /**
     * This method checks the two major cases that may violate primary key integrity.
     *
     * 1. Disallow changing of the primary keys on an EDIT maintenance document. Other fields can be changed, but once
     * the primary
     * keys have been set, they are permanent.
     *
     * 2. Disallow creating a new object whose primary key values are already present in the system on a CREATE NEW
     * maintenance
     * document.
     *
     * This method also will add new Errors to the Global Error Map.
     *
     * @param document - The Maintenance Document being tested.
     * @return Returns false if either test failed, otherwise returns true.
     */
    protected boolean primaryKeyCheck(MaintenanceDocument document) {

        // default to success if no failures
        boolean success = true;
        Class<?> boClass = document.getNewMaintainableObject().getDataObjectClass();

        Object oldBo = document.getOldMaintainableObject().getDataObject();
        Object newBo = document.getNewMaintainableObject().getDataObject();

        // We dont do primaryKeyChecks on Global Business Object maintenance documents. This is
        // because it doesnt really make any sense to do so, given the behavior of Globals. When a
        // Global Document completes, it will update or create a new record for each BO in the list.
        // As a result, there's no problem with having existing BO records in the system, they will
        // simply get updated.
        if (newBo instanceof GlobalBusinessObject) {
            return success;
        }

        // fail and complain if the person has changed the primary keys on
        // an EDIT maintenance document.
        if (document.isEdit()) {
            if (!getDataObjectMetaDataService().equalsByPrimaryKeys(oldBo, newBo)) {
                // add a complaint to the errors
                putDocumentError(KRADConstants.DOCUMENT_ERRORS,
                        RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_PRIMARY_KEYS_CHANGED_ON_EDIT,
                        getHumanReadablePrimaryKeyFieldNames(boClass));
                success &= false;
            }
        }

        // fail and complain if the person has selected a new object with keys that already exist
        // in the DB.
        else if (document.isNew()) {

            // TODO: when/if we have standard support for DO retrieval, do this check for DO's
            if (newBo instanceof PersistableBusinessObject) {

                // get a map of the pk field names and values
                Map<String, ?> newPkFields = getDataObjectMetaDataService().getPrimaryKeyFieldValues(newBo);

                // TODO: Good suggestion from Aaron, dont bother checking the DB, if all of the
                // objects PK fields dont have values. If any are null or empty, then
                // we're done. The current way wont fail, but it will make a wasteful
                // DB call that may not be necessary, and we want to minimize these.

                // attempt to do a lookup, see if this object already exists by these Primary Keys
                PersistableBusinessObject testBo =
                        boService.findByPrimaryKey(boClass.asSubclass(PersistableBusinessObject.class), newPkFields);

                // if the retrieve was successful, then this object already exists, and we need
                // to complain
                if (testBo != null) {
                    putDocumentError(KRADConstants.DOCUMENT_ERRORS,
                            RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_KEYS_ALREADY_EXIST_ON_CREATE_NEW,
                            getHumanReadablePrimaryKeyFieldNames(boClass));
                    success &= false;
                }
            }
        }

        return success;
    }

    /**
     * This method creates a human-readable string of the class' primary key field names, as designated by the
     * DataDictionary.
     *
     * @param boClass
     * @return
     */
    protected String getHumanReadablePrimaryKeyFieldNames(Class<?> boClass) {

        String delim = "";
        StringBuffer pkFieldNames = new StringBuffer();

        // get a list of all the primary key field names, walk through them
        List<String> pkFields = getDataObjectMetaDataService().listPrimaryKeyFieldNames(boClass);
        for (Iterator<String> iter = pkFields.iterator(); iter.hasNext(); ) {
            String pkFieldName = (String) iter.next();

            // TODO should this be getting labels from the view dictionary
            // use the DataDictionary service to translate field name into human-readable label
            String humanReadableFieldName = ddService.getAttributeLabel(boClass, pkFieldName);

            // append the next field
            pkFieldNames.append(delim + humanReadableFieldName);

            // separate names with commas after the first one
            if (delim.equalsIgnoreCase("")) {
                delim = ", ";
            }
        }

        return pkFieldNames.toString();
    }

    /**
     * This method enforces all business rules that are common to all maintenance documents which must be tested before
     * doing an
     * approval.
     *
     * It can be overloaded in special cases where a MaintenanceDocument has very special needs that would be contrary
     * to what is
     * enforced here.
     *
     * @param document - a populated MaintenanceDocument instance
     * @return true if the document can be approved, false if not
     */
    protected boolean processGlobalApproveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    /**
     * This method enforces all business rules that are common to all maintenance documents which must be tested before
     * doing a
     * route.
     *
     * It can be overloaded in special cases where a MaintenanceDocument has very special needs that would be contrary
     * to what is
     * enforced here.
     *
     * @param document - a populated MaintenanceDocument instance
     * @return true if the document can be routed, false if not
     */
    protected boolean processGlobalRouteDocumentBusinessRules(MaintenanceDocument document) {

        boolean success = true;

        // require a document description field
        success &= checkEmptyDocumentField(
                KRADPropertyConstants.DOCUMENT_HEADER + "." + KRADPropertyConstants.DOCUMENT_DESCRIPTION,
                document.getDocumentHeader().getDocumentDescription(), "Description");

        return success;
    }

    /**
     * This method enforces all business rules that are common to all maintenance documents which must be tested before
     * doing a
     * save.
     *
     * It can be overloaded in special cases where a MaintenanceDocument has very special needs that would be contrary
     * to what is
     * enforced here.
     *
     * Note that although this method returns a true or false to indicate whether the save should happen or not, this
     * result may not
     * be followed by the calling method. In other words, the boolean result will likely be ignored, and the document
     * saved,
     * regardless.
     *
     * @param document - a populated MaintenanceDocument instance
     * @return true if all business rules succeed, false if not
     */
    protected boolean processGlobalSaveDocumentBusinessRules(MaintenanceDocument document) {

        // default to success
        boolean success = true;

        // do generic checks that impact primary key violations
        primaryKeyCheck(document);

        // this is happening only on the processSave, since a Save happens in both the
        // Route and Save events.
        this.dataDictionaryValidate(document);

        return success;
    }

    /**
     * This method should be overridden to provide custom rules for processing document saving
     *
     * @param document
     * @return boolean
     */
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    /**
     * This method should be overridden to provide custom rules for processing document routing
     *
     * @param document
     * @return boolean
     */
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    /**
     * This method should be overridden to provide custom rules for processing document approval.
     *
     * @param document
     * @return booelan
     */
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        return true;
    }

    // Document Validation Helper Methods

    /**
     * This method checks to see if the document is in a state that it can be saved without causing exceptions.
     *
     * Note that Business Rules are NOT enforced here, only validity checks.
     *
     * This method will only return false if the document is in such a state that routing it will cause
     * RunTimeExceptions.
     *
     * @param maintenanceDocument - a populated MaintenaceDocument instance.
     * @return boolean - returns true unless the object is in an invalid state.
     */
    protected boolean isDocumentValidForSave(MaintenanceDocument maintenanceDocument) {

        boolean success = true;

        success &= super.isDocumentOverviewValid(maintenanceDocument);
        success &= validateDocumentStructure((Document) maintenanceDocument);
        success &= validateMaintenanceDocument(maintenanceDocument);
        success &= validateGlobalBusinessObjectPersistable(maintenanceDocument);
        return success;
    }

    /**
     * This method makes sure the document itself is valid, and has the necessary fields populated to be routable.
     *
     * This is not a business rules test, rather its a structure test to make sure that the document will not cause
     * exceptions
     * before routing.
     *
     * @param document - document to be tested
     * @return false if the document is missing key values, true otherwise
     */
    protected boolean validateDocumentStructure(Document document) {
        boolean success = true;

        // document must have a populated documentNumber
        String documentHeaderId = document.getDocumentNumber();
        if (documentHeaderId == null || StringUtils.isEmpty(documentHeaderId)) {
            throw new ValidationException("Document has no document number, unable to proceed.");
        }

        return success;
    }

    /**
     * This method checks to make sure the document is a valid maintenanceDocument, and has the necessary values
     * populated such that
     * it will not cause exceptions in later routing or business rules testing.
     *
     * This is not a business rules test.
     *
     * @param maintenanceDocument - document to be tested
     * @return whether maintenance doc passes
     * @throws ValidationException
     *
     */
    protected boolean validateMaintenanceDocument(MaintenanceDocument maintenanceDocument) {
        boolean success = true;
        Maintainable newMaintainable = maintenanceDocument.getNewMaintainableObject();

        // document must have a newMaintainable object
        if (newMaintainable == null) {
            throw new ValidationException(
                    "Maintainable object from Maintenance Document '" + maintenanceDocument.getDocumentTitle() +
                            "' is null, unable to proceed.");
        }

        // document's newMaintainable must contain an object (ie, not null)
        if (newMaintainable.getDataObject() == null) {
            throw new ValidationException("Maintainable's component data object is null.");
        }

        return success;
    }

    /**
     * This method checks whether this maint doc contains Global Business Objects, and if so, whether the GBOs are in a
     * persistable
     * state. This will return false if this method determines that the GBO will cause a SQL Exception when the document
     * is
     * persisted.
     *
     * @param document
     * @return False when the method determines that the contained Global Business Object will cause a SQL Exception,
     *         and the
     *         document should not be saved. It will return True otherwise.
     */
    protected boolean validateGlobalBusinessObjectPersistable(MaintenanceDocument document) {
        boolean success = true;

        if (document.getNewMaintainableObject() == null) {
            return success;
        }
        if (document.getNewMaintainableObject().getDataObject() == null) {
            return success;
        }
        if (!(document.getNewMaintainableObject().getDataObject() instanceof GlobalBusinessObject)) {
            return success;
        }

        PersistableBusinessObject bo = (PersistableBusinessObject) document.getNewMaintainableObject().getDataObject();
        GlobalBusinessObject gbo = (GlobalBusinessObject) bo;
        return gbo.isPersistable();
    }

    /**
     * This method tests to make sure the MaintenanceDocument passed in is based on the class you are expecting.
     *
     * It does this based on the NewMaintainableObject of the MaintenanceDocument.
     *
     * @param document - MaintenanceDocument instance you want to test
     * @param clazz - class you are expecting the MaintenanceDocument to be based on
     * @return true if they match, false if not
     */
    protected boolean isCorrectMaintenanceClass(MaintenanceDocument document, Class clazz) {

        // disallow null arguments
        if (document == null || clazz == null) {
            throw new IllegalArgumentException("Null arguments were passed in.");
        }

        // compare the class names
        if (clazz.toString().equals(document.getNewMaintainableObject().getDataObjectClass().toString())) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * This method accepts an object, and attempts to determine whether it is empty by this method's definition.
     *
     * OBJECT RESULT null false empty-string false whitespace false otherwise true
     *
     * If the result is false, it will add an object field error to the Global Errors.
     *
     * @param valueToTest - any object to test, usually a String
     * @param propertyName - the name of the property being tested
     * @return true or false, by the description above
     */
    protected boolean checkEmptyBOField(String propertyName, Object valueToTest, String parameter) {

        boolean success = true;

        success = checkEmptyValue(valueToTest);

        // if failed, then add a field error
        if (!success) {
            putFieldError(propertyName, RiceKeyConstants.ERROR_REQUIRED, parameter);
        }

        return success;
    }

    /**
     * This method accepts document field (such as , and attempts to determine whether it is empty by this method's
     * definition.
     *
     * OBJECT RESULT null false empty-string false whitespace false otherwise true
     *
     * If the result is false, it will add document field error to the Global Errors.
     *
     * @param valueToTest - any object to test, usually a String
     * @param propertyName - the name of the property being tested
     * @return true or false, by the description above
     */
    protected boolean checkEmptyDocumentField(String propertyName, Object valueToTest, String parameter) {
        boolean success = true;
        success = checkEmptyValue(valueToTest);
        if (!success) {
            putDocumentError(propertyName, RiceKeyConstants.ERROR_REQUIRED, parameter);
        }
        return success;
    }

    /**
     * This method accepts document field (such as , and attempts to determine whether it is empty by this method's
     * definition.
     *
     * OBJECT RESULT null false empty-string false whitespace false otherwise true
     *
     * It will the result as a boolean
     *
     * @param valueToTest - any object to test, usually a String
     */
    protected boolean checkEmptyValue(Object valueToTest) {
        boolean success = true;

        // if its not a string, only fail if its a null object
        if (valueToTest == null) {
            success = false;
        } else {
            // test for null, empty-string, or whitespace if its a string
            if (valueToTest instanceof String) {
                if (StringUtils.isBlank((String) valueToTest)) {
                    success = false;
                }
            }
        }

        return success;
    }

    /**
     * This method is used during debugging to dump the contents of the error map, including the key names. It is not
     * used by the
     * application in normal circumstances at all.
     */
    protected void showErrorMap() {

        if (GlobalVariables.getMessageMap().hasNoErrors()) {
            return;
        }

        for (Iterator i = GlobalVariables.getMessageMap().getAllPropertiesAndErrors().iterator(); i.hasNext(); ) {
            Map.Entry e = (Map.Entry) i.next();

            AutoPopulatingList errorList = (AutoPopulatingList) e.getValue();
            for (Iterator j = errorList.iterator(); j.hasNext(); ) {
                ErrorMessage em = (ErrorMessage) j.next();

                if (em.getMessageParameters() == null) {
                    LOG.error(e.getKey().toString() + " = " + em.getErrorKey());
                } else {
                    LOG.error(e.getKey().toString() + " = " + em.getErrorKey() + " : " +
                            em.getMessageParameters().toString());
                }
            }
        }
    }

    /**
     * @see org.kuali.rice.krad.rules.MaintenanceDocumentRule#setupBaseConvenienceObjects(org.kuali.rice.krad.maintenance.MaintenanceDocument)
     */
    public void setupBaseConvenienceObjects(MaintenanceDocument document) {

        // setup oldAccount convenience objects, make sure all possible sub-objects are populated
        oldBo = document.getOldMaintainableObject().getDataObject();
        if (oldBo != null && oldBo instanceof PersistableBusinessObject) {
            ((PersistableBusinessObject) oldBo).refreshNonUpdateableReferences();
        }

        // setup newAccount convenience objects, make sure all possible sub-objects are populated
        newBo = document.getNewMaintainableObject().getDataObject();
        if (newBo instanceof PersistableBusinessObject) {
            ((PersistableBusinessObject) newBo).refreshNonUpdateableReferences();
        }

        boClass = document.getNewMaintainableObject().getDataObjectClass();

        // call the setupConvenienceObjects in the subclass, if a subclass exists
        setupConvenienceObjects();
    }

    public void setupConvenienceObjects() {
        // should always be overriden by subclass
    }

    /**
     * This method checks to make sure that if the foreign-key fields for the given reference attributes have any fields
     * filled out,
     * that all fields are filled out.
     *
     * If any are filled out, but all are not, it will return false and add a global error message about the problem.
     *
     * @param referenceName - The name of the reference object, whose foreign-key fields must be all-or-none filled
     * out.
     * @return true if this is the case, false if not
     */
    protected boolean checkForPartiallyFilledOutReferenceForeignKeys(String referenceName) {
        boolean success = true;

        if (newBo instanceof PersistableBusinessObject) {
            ForeignKeyFieldsPopulationState fkFieldsState;
            fkFieldsState = persistenceStructureService
                    .getForeignKeyFieldsPopulationState((PersistableBusinessObject) newBo, referenceName);

            // determine result
            if (fkFieldsState.isAnyFieldsPopulated() && !fkFieldsState.isAllFieldsPopulated()) {
                success = false;

                // add errors if appropriate

                // get the full set of foreign-keys
                List fKeys = new ArrayList(persistenceStructureService
                        .getForeignKeysForReference(newBo.getClass().asSubclass(PersistableBusinessObject.class),
                                referenceName).keySet());
                String fKeysReadable = consolidateFieldNames(fKeys, ", ").toString();

                // walk through the missing fields
                for (Iterator iter = fkFieldsState.getUnpopulatedFieldNames().iterator(); iter.hasNext(); ) {
                    String fieldName = (String) iter.next();

                    // get the human-readable name
                    String fieldNameReadable = ddService.getAttributeLabel(newBo.getClass(), fieldName);

                    // add a field error
                    putFieldError(fieldName, RiceKeyConstants.ERROR_DOCUMENT_MAINTENANCE_PARTIALLY_FILLED_OUT_REF_FKEYS,
                            new String[]{fieldNameReadable, fKeysReadable});
                }
            }
        }

        return success;
    }

    /**
     * This method turns a list of field property names, into a delimited string of the human-readable names.
     *
     * @param fieldNames - List of fieldNames
     * @return A filled StringBuffer ready to go in an error message
     */
    protected StringBuffer consolidateFieldNames(List fieldNames, String delimiter) {

        StringBuffer sb = new StringBuffer();

        // setup some vars
        boolean firstPass = true;
        String delim = "";

        // walk through the list
        for (Iterator iter = fieldNames.iterator(); iter.hasNext(); ) {
            String fieldName = (String) iter.next();

            // get the human-readable name
            // add the new one, with the appropriate delimiter
            sb.append(delim + ddService.getAttributeLabel(newBo.getClass(), fieldName));

            // after the first item, start using a delimiter
            if (firstPass) {
                delim = delimiter;
                firstPass = false;
            }
        }

        return sb;
    }

    /**
     * This method translates the passed in field name into a human-readable attribute label.
     *
     * It assumes the existing newBO's class as the class to examine the fieldName for.
     *
     * @param fieldName The fieldName you want a human-readable label for.
     * @return A human-readable label, pulled from the DataDictionary.
     */
    protected String getFieldLabel(String fieldName) {
        return ddService.getAttributeLabel(newBo.getClass(), fieldName) + "(" +
                ddService.getAttributeShortLabel(newBo.getClass(), fieldName) + ")";
    }

    /**
     * This method translates the passed in field name into a human-readable attribute label.
     *
     * It assumes the existing newBO's class as the class to examine the fieldName for.
     *
     * @param boClass The class to use in combination with the fieldName.
     * @param fieldName The fieldName you want a human-readable label for.
     * @return A human-readable label, pulled from the DataDictionary.
     */
    protected String getFieldLabel(Class boClass, String fieldName) {
        return ddService.getAttributeLabel(boClass, fieldName) + "(" +
                ddService.getAttributeShortLabel(boClass, fieldName) + ")";
    }

    /**
     * Gets the boService attribute.
     *
     * @return Returns the boService.
     */
    protected final BusinessObjectService getBoService() {
        return boService;
    }

    /**
     * Sets the boService attribute value.
     *
     * @param boService The boService to set.
     */
    public final void setBoService(BusinessObjectService boService) {
        this.boService = boService;
    }

    /**
     * Gets the configService attribute.
     *
     * @return Returns the configService.
     */
    protected final ConfigurationService getConfigService() {
        return configService;
    }

    /**
     * Sets the configService attribute value.
     *
     * @param configService The configService to set.
     */
    public final void setConfigService(ConfigurationService configService) {
        this.configService = configService;
    }

    /**
     * Gets the ddService attribute.
     *
     * @return Returns the ddService.
     */
    protected final DataDictionaryService getDdService() {
        return ddService;
    }

    /**
     * Sets the ddService attribute value.
     *
     * @param ddService The ddService to set.
     */
    public final void setDdService(DataDictionaryService ddService) {
        this.ddService = ddService;
    }

    /**
     * Gets the dictionaryValidationService attribute.
     *
     * @return Returns the dictionaryValidationService.
     */
    protected final DictionaryValidationService getDictionaryValidationService() {
        return dictionaryValidationService;
    }

    /**
     * Sets the dictionaryValidationService attribute value.
     *
     * @param dictionaryValidationService The dictionaryValidationService to set.
     */
    public final void setDictionaryValidationService(DictionaryValidationService dictionaryValidationService) {
        this.dictionaryValidationService = dictionaryValidationService;
    }

    /**
     * Gets the maintDocDictionaryService attribute.
     *
     * @return Returns the maintDocDictionaryService.
     */
    protected final MaintenanceDocumentDictionaryService getMaintDocDictionaryService() {
        return maintDocDictionaryService;
    }

    /**
     * Sets the maintDocDictionaryService attribute value.
     *
     * @param maintDocDictionaryService The maintDocDictionaryService to set.
     */
    public final void setMaintDocDictionaryService(MaintenanceDocumentDictionaryService maintDocDictionaryService) {
        this.maintDocDictionaryService = maintDocDictionaryService;
    }

    /**
     * Gets the newBo attribute.
     *
     * @return Returns the newBo.
     */
    protected final Object getNewBo() {
        return newBo;
    }

    protected void setNewBo(Object newBo) {
        this.newBo = newBo;
    }

    /**
     * Gets the oldBo attribute.
     *
     * @return Returns the oldBo.
     */
    protected final Object getOldBo() {
        return oldBo;
    }

    /**
     * Gets the persistenceStructureService attribute.
     *
     * @return Returns the persistenceStructureService.
     */
    protected final PersistenceStructureService getPersistenceStructureService() {
        return persistenceStructureService;
    }

    /**
     * Sets the persistenceStructureService attribute value.
     *
     * @param persistenceStructureService The persistenceStructureService to set.
     */
    public final void setPersistenceStructureService(PersistenceStructureService persistenceStructureService) {
        this.persistenceStructureService = persistenceStructureService;
    }

    /**
     * Gets the workflowDocumentService attribute.
     *
     * @return Returns the workflowDocumentService.
     */
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     *
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName,
            PersistableBusinessObject bo) {
        LOG.debug("processAddCollectionLineBusinessRules");

        // setup convenience pointers to the old & new bo
        setupBaseConvenienceObjects(document);

        // sanity check on the document object
        this.validateMaintenanceDocument(document);

        boolean success = true;
        MessageMap map = GlobalVariables.getMessageMap();
        int errorCount = map.getErrorCount();
        map.addToErrorPath(MAINTAINABLE_ERROR_PATH);
        if (LOG.isDebugEnabled()) {
            LOG.debug("processAddCollectionLineBusinessRules - BO: " + bo);
            LOG.debug("Before Validate: " + map);
        }
        //getBoDictionaryService().performForceUppercase(bo);
        getMaintDocDictionaryService().validateMaintainableCollectionsAddLineRequiredFields(document,
                document.getNewMaintainableObject().getBusinessObject(), collectionName);
        String errorPath = KRADConstants.MAINTENANCE_ADD_PREFIX + collectionName;
        map.addToErrorPath(errorPath);

        getDictionaryValidationService().validateBusinessObject(bo, false);
        success &= map.getErrorCount() == errorCount;
        success &= dictionaryValidationService.validateDefaultExistenceChecksForNewCollectionItem(
                document.getNewMaintainableObject().getBusinessObject(), bo, collectionName);
        success &= validateDuplicateIdentifierInDataDictionary(document, collectionName, bo);
        success &= processCustomAddCollectionLineBusinessRules(document, collectionName, bo);

        map.removeFromErrorPath(errorPath);
        map.removeFromErrorPath(MAINTAINABLE_ERROR_PATH);
        if (LOG.isDebugEnabled()) {
            LOG.debug("After Validate: " + map);
            LOG.debug("processAddCollectionLineBusinessRules returning: " + success);
        }

        return success;
    }

    /**
     * This method validates that there should only exist one entry in the collection whose
     * fields match the fields specified within the duplicateIdentificationFields in the
     * maintenance document data dictionary.
     * If the duplicateIdentificationFields is not specified in the DD, by default it would
     * allow the addition to happen and return true.
     * It will return false if it fails the uniqueness validation.
     *
     * @param document
     * @param collectionName
     * @param bo
     * @return
     */
    protected boolean validateDuplicateIdentifierInDataDictionary(MaintenanceDocument document, String collectionName,
            PersistableBusinessObject bo) {
        boolean valid = true;
        PersistableBusinessObject maintBo = document.getNewMaintainableObject().getBusinessObject();
        Collection maintCollection = (Collection) ObjectUtils.getPropertyValue(maintBo, collectionName);
        List<String> duplicateIdentifier = document.getNewMaintainableObject()
                .getDuplicateIdentifierFieldsFromDataDictionary(
                        document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName(), collectionName);
        if (duplicateIdentifier.size() > 0) {
            List<String> existingIdentifierString = document.getNewMaintainableObject()
                    .getMultiValueIdentifierList(maintCollection, duplicateIdentifier);
            if (document.getNewMaintainableObject()
                    .hasBusinessObjectExisted(bo, existingIdentifierString, duplicateIdentifier)) {
                valid = false;
                GlobalVariables.getMessageMap()
                        .putError(duplicateIdentifier.get(0), RiceKeyConstants.ERROR_DUPLICATE_ELEMENT, "entries in ",
                                document.getDocumentHeader().getWorkflowDocument().getDocumentTypeName());
            }
        }
        return valid;
    }

    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName,
            PersistableBusinessObject line) {
        return true;
    }

    public PersonService getPersonService() {
        return personService;
    }

    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    public DateTimeService getDateTimeService() {
        return CoreApiServiceLocator.getDateTimeService();
    }

    /**
     * @return the documentHelperService
     */
    public DocumentHelperService getDocumentHelperService() {
        return this.documentHelperService;
    }

    /**
     * @param documentHelperService the documentHelperService to set
     */
    public void setDocumentHelperService(DocumentHelperService documentHelperService) {
        this.documentHelperService = documentHelperService;
    }

    protected RoleService getRoleService() {
        if (this.roleService == null) {
            this.roleService = KimApiServiceLocator.getRoleService();
        }
        return this.roleService;
    }

    protected DataObjectMetaDataService getDataObjectMetaDataService() {
        if (dataObjectMetaDataService == null) {
            this.dataObjectMetaDataService = KRADServiceLocatorWeb.getDataObjectMetaDataService();
        }
        return dataObjectMetaDataService;
    }

    public void setDataObjectMetaDataService(DataObjectMetaDataService dataObjectMetaDataService) {
        this.dataObjectMetaDataService = dataObjectMetaDataService;
    }

    public void setBusinessObjectAuthorizationService(
            BusinessObjectAuthorizationService businessObjectAuthorizationService) {
        this.businessObjectAuthorizationService = businessObjectAuthorizationService;
    }

    public void setBusinessObjectMetaDataService(BusinessObjectMetaDataService businessObjectMetaDataService) {
        this.businessObjectMetaDataService = businessObjectMetaDataService;
    }

    public void setBoDictionaryService(BusinessObjectDictionaryService boDictionaryService) {
        this.boDictionaryService = boDictionaryService;
    }
}

