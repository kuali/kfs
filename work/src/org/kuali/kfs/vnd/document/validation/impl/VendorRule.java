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
package org.kuali.module.vendor.rules;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.authorization.FieldAuthorization;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizations;
import org.kuali.core.document.authorization.MaintenanceDocumentAuthorizer;
import org.kuali.core.exceptions.UnknownDocumentIdException;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.ObjectUtils;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.context.SpringContext;
import org.kuali.module.chart.bo.Chart;
import org.kuali.module.chart.bo.Org;
import org.kuali.module.purap.PurapKeyConstants;
import org.kuali.module.purap.PurapPropertyConstants;
import org.kuali.module.purap.document.PurchasingDocument;
import org.kuali.module.vendor.VendorConstants;
import org.kuali.module.vendor.VendorKeyConstants;
import org.kuali.module.vendor.VendorPropertyConstants;
import org.kuali.module.vendor.VendorRuleConstants;
import org.kuali.module.vendor.bo.AddressType;
import org.kuali.module.vendor.bo.OwnershipType;
import org.kuali.module.vendor.bo.VendorAddress;
import org.kuali.module.vendor.bo.VendorContact;
import org.kuali.module.vendor.bo.VendorContract;
import org.kuali.module.vendor.bo.VendorContractOrganization;
import org.kuali.module.vendor.bo.VendorCustomerNumber;
import org.kuali.module.vendor.bo.VendorDefaultAddress;
import org.kuali.module.vendor.bo.VendorDetail;
import org.kuali.module.vendor.service.PhoneNumberService;
import org.kuali.module.vendor.service.TaxNumberService;

import edu.iu.uis.eden.exception.WorkflowException;

public class VendorRule extends MaintenanceDocumentRuleBase implements VendorRuleConstants {

    private VendorDetail oldVendor;
    private VendorDetail newVendor;
    private static KualiDecimal VENDOR_MIN_ORDER_AMOUNT;


    /**
     * This method is needed to override the setupBaseConvenienceObjects from the superclass because we cannot use the
     * setupBaseConvenienceObjects from the superclass. The reason we cannot use the superclass method is because it calls the
     * updateNonUpdateableReferences for everything and we cannot do that for parent vendors, because we want to update vendor
     * header information only on parent vendors, so the saving of the vendor header is done manually. If we call the
     * updateNonUpdateableReferences, it is going to overwrite any changes that the user might have done in the vendor header with
     * the existing values in the database.
     * 
     * @param document The MaintenanceDocument object containing the vendorDetail objects to be setup.
     */
    @Override
    public void setupBaseConvenienceObjects(MaintenanceDocument document) {
        oldVendor = (VendorDetail) document.getOldMaintainableObject().getBusinessObject();
        newVendor = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();
        super.setNewBo(newVendor);
        setupConvenienceObjects();
    }

    /**
     * This method setups oldVendor and newVendor convenience objects, make sure all possible sub-objects are populated.
     */
    @Override
    public void setupConvenienceObjects() {

        // setup oldVendor convenience objects, make sure all possible sub-objects are populated
        refreshSubObjects(oldVendor);

        // setup newVendor convenience objects, make sure all possible sub-objects are populated
        refreshSubObjects(newVendor);

    }

    /**
     * This method overrides the checkAuthorizationRestrictions in MaintenanceDocumentRuleBase. The reason we needed to override it
     * is because in vendor, we had to save the fields in the vendor header separately than vendor detail, and those fields are only
     * editable when the vendor is a parent. Therefore we had to override the setupBaseConvenienceObjects method, which then causes
     * us unable to set the oldBo of the super class because the oldBo is not accessible from outside the class. This will cause the
     * checkAuthorizationRestrictions of the superclass to fail while processing division vendors that contain those restricted
     * (uneditable) fields, because the oldBo is null and will throw the null pointer exception. Therefore we're overriding the
     * checkAuthorizationRestrictions in here and we'll use the oldVendor instead of oldBo of the superclass while comparing the old
     * and new values. This also does not enforce the authorization restrictions if the restricted fields are the fields in vendor
     * header or the vendor is not a parent vendor, because in this case, the fields are uneditable from the user interface.
     * 
     * @see org.kuali.core.maintenance.rules.MaintenanceDocumentRuleBase#checkAuthorizationRestrictions(org.kuali.core.document.MaintenanceDocument)
     */
    @Override
    protected boolean checkAuthorizationRestrictions(MaintenanceDocument document) {

        boolean success = true;
        boolean changed = false;

        boolean isInitiator = false;
        boolean isApprover = false;

        Object oldValue = null;
        Object newValue = null;
        Object savedValue = null;

        KualiWorkflowDocument workflowDocument = null;
        UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();
        try {
            workflowDocument = getWorkflowDocumentService().createWorkflowDocument(Long.valueOf(document.getDocumentNumber()), user);
        }
        catch (WorkflowException e) {
            throw new UnknownDocumentIdException("no document found for documentHeaderId '" + document.getDocumentNumber() + "'", e);
        }
        if (user.getPersonUserIdentifier().equalsIgnoreCase(workflowDocument.getInitiatorNetworkId())) {
            // if these are the same person then we know it is the initiator
            isInitiator = true;
        }
        else if (workflowDocument.isApprovalRequested()) {
            isApprover = true;
        }

        // get the correct documentAuthorizer for this document
        MaintenanceDocumentAuthorizer documentAuthorizer = (MaintenanceDocumentAuthorizer) documentAuthorizationService.getDocumentAuthorizer(document);

        // get a new instance of MaintenanceDocumentAuthorizations for this context
        MaintenanceDocumentAuthorizations auths = documentAuthorizer.getFieldAuthorizations(document, user);

        // load a temp copy of the document from the DB to compare to for changes
        MaintenanceDocument savedDoc = null;
        Maintainable savedNewMaintainable = null;
        PersistableBusinessObject savedNewBo = null;

        if (isApprover) {
            try {
                DocumentService docService = SpringContext.getBean(DocumentService.class);
                savedDoc = (MaintenanceDocument) docService.getByDocumentHeaderId(document.getDocumentNumber());
            }
            catch (WorkflowException e) {
                throw new RuntimeException("A WorkflowException was thrown which prevented the loading of " + "the comparison document (" + document.getDocumentNumber() + ")", e);
            }

            // attempt to retrieve the BO, but leave it blank if it or any of the objects on the path
            // to it are blank
            if (savedDoc != null) {
                savedNewMaintainable = savedDoc.getNewMaintainableObject();
                if (savedNewMaintainable != null) {
                    savedNewBo = savedNewMaintainable.getBusinessObject();
                }
            }
        }

        // setup in-loop members
        FieldAuthorization fieldAuthorization = null;

        // walk through all the restrictions
        Collection restrictedFields = auths.getAuthFieldNames();
        for (Iterator iter = restrictedFields.iterator(); iter.hasNext();) {
            String fieldName = (String) iter.next();

            if (fieldName.indexOf(VendorPropertyConstants.VENDOR_HEADER_PREFIX) < 0 || newVendor.isVendorParentIndicator()) {
                // get the specific field authorization structure
                fieldAuthorization = auths.getAuthFieldAuthorization(fieldName);

                // if there are any restrictions, then enforce them
                if (fieldAuthorization.isRestricted()) {
                    // reset the changed flag
                    changed = false;

                    // new value should always be the same regardles of who is
                    // making the request
                    newValue = ObjectUtils.getNestedValue(newVendor, fieldName);

                    // first we need to handle the case of edit doc && initiator
                    if (isInitiator && document.isEdit()) {
                        // old value must equal new value
                        oldValue = ObjectUtils.getNestedValue(oldVendor, fieldName);
                    }
                    else if (isApprover && savedNewBo != null) {
                        oldValue = ObjectUtils.getNestedValue(savedNewBo, fieldName);
                    }

                    // check to make sure nothing has changed
                    if (oldValue == null && newValue == null) {
                        changed = false;
                    }
                    else if ((oldValue == null && newValue != null) || (oldValue != null && newValue == null)) {
                        changed = true;
                    }
                    else if (oldValue != null && newValue != null) {
                        if (!oldValue.equals(newValue)) {
                            changed = true;
                        }
                    }

                    // if changed and a NEW doc, but the new value is the default value, then let it go
                    // we dont allow changing to default values for EDIT docs though, only NEW
                    if (changed && document.isNew()) {
                        String defaultValue = maintDocDictionaryService.getFieldDefaultValue(document.getNewMaintainableObject().getBoClass(), fieldName);

                        // get the string value of newValue
                        String newStringValue = newValue.toString();
                        // if the newValue is the default value, then ignore
                        if (newStringValue.equalsIgnoreCase(defaultValue)) {
                            changed = false;
                        }
                    }

                    // if anything has changed, complain
                    if (changed) {
                        String humanReadableFieldName = ddService.getAttributeLabel(document.getNewMaintainableObject().getBoClass(), fieldName);
                        putFieldError(fieldName, KFSKeyConstants.ERROR_DOCUMENT_AUTHORIZATION_RESTRICTED_FIELD_CHANGED, humanReadableFieldName);
                        success &= false;
                    }
                }
            }
        }
        return success;
    }

    /**
     * This method refreshes the references of vendor detail and its sub objects
     */
    void refreshSubObjects(VendorDetail vendor) {
        if (vendor == null) {
            return;
        }

        // If this is a division vendor, we need to do a refreshNonUpdateableReferences
        // and also refreshes the vendor header, since the user aren't supposed to
        // make any updates of vendor header's attributes while editing a division vendor
        if (!vendor.isVendorParentIndicator()) {
            vendor.refreshNonUpdateableReferences();
            vendor.getVendorHeader().refreshNonUpdateableReferences();

        }
        else {
            // Retrieve the references objects of the vendor header of this vendor.
            List<String> headerFieldNames = getObjectReferencesListFromBOClass(vendor.getVendorHeader().getClass());
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(vendor.getVendorHeader(), headerFieldNames);

            // We still need to retrieve all the other references of this vendor in addition to
            // vendor header. Since this is a parent vendor, whose vendor header saving is handled manually,
            // we have already retrieved references for vendor header's attributes above, so we should
            // exclude retrieving reference objects of vendor header.
            List<String> detailFieldNames = getObjectReferencesListFromBOClass(vendor.getClass());
            detailFieldNames.remove(VendorConstants.VENDOR_HEADER_ATTR);
            SpringContext.getBean(PersistenceService.class).retrieveReferenceObjects(vendor, detailFieldNames);
        }

        // refresh addresses
        if (vendor.getVendorAddresses() != null) {
            for (VendorAddress address : vendor.getVendorAddresses()) {
                address.refreshNonUpdateableReferences();
                if (address.getVendorDefaultAddresses() != null) {
                    for (VendorDefaultAddress defaultAddress : address.getVendorDefaultAddresses()) {
                        defaultAddress.refreshNonUpdateableReferences();
                    }
                }
            }
        }
        // refresh contacts
        if (vendor.getVendorContacts() != null) {
            for (VendorContact contact : vendor.getVendorContacts()) {
                contact.refreshNonUpdateableReferences();
            }
        }
        // refresh contracts
        if (vendor.getVendorContracts() != null) {
            for (VendorContract contract : vendor.getVendorContracts()) {
                contract.refreshNonUpdateableReferences();
            }
        }
    }

    /**
     * This method is currently used as a helper to get a list of object references (e.g. vendorType, vendorOwnershipType, etc) from
     * a BusinessObject (e.g. VendorHeader, VendorDetail, etc) class dynamically. Feel free to enhance it, refactor it or move it to
     * a superclass or elsewhere as you see appropriate.
     * 
     * @return List a List of attributes of the class
     */
    private List getObjectReferencesListFromBOClass(Class theClass) {
        List<String> results = new ArrayList();
        for (Field theField : theClass.getDeclaredFields()) {
            try {
                theField.getType().asSubclass(PersistableBusinessObjectBase.class);
                // only add the field to the result list if this is not
                // a UniversalUser
                if (!theField.getType().equals(UniversalUser.class)) {
                    results.add(theField.getName());
                }
            }
            catch (ClassCastException e) {
                // If we catches this, it means the "theField" can't be casted as a BusinessObjectBase,
                // so we won't add it to the results list because this method is aiming at getting
                // a list of object references that are subclasses of BusinessObjectBase.
            }
        }
        return results;
    }

    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = processValidation(document);
        return valid & super.processCustomApproveDocumentBusinessRules(document);
    }

    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = processValidation(document);
        return valid & super.processCustomRouteDocumentBusinessRules(document);
    }

    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;
        return valid & super.processCustomSaveDocumentBusinessRules(document);
    }

    private boolean processValidation(MaintenanceDocument document) {
        boolean valid = true;

        valid &= processVendorValidation(document);
        if (ObjectUtils.isNotNull(newVendor.getVendorHeader().getVendorType())) {
            valid &= processAddressValidation(document);
        }
        valid &= processContractValidation(document);

        return valid;
    }

    boolean processVendorValidation(MaintenanceDocument document) {
        boolean valid = true;
        VendorDetail vendorDetail = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();

        valid &= validateTaxTypeAndTaxNumberBlankness(vendorDetail);
        valid &= validateParentVendorTaxNumber(vendorDetail);
        valid &= validateOwnershipTypeAllowed(vendorDetail);
        valid &= validateTaxNumberFromTaxNumberService(vendorDetail);
        valid &= validateRestrictedReasonRequiredness(vendorDetail);
        valid &= validateInactiveReasonRequiredness(vendorDetail);

        if (ObjectUtils.isNotNull(vendorDetail.getVendorHeader().getVendorType())) {
            valid &= validateTaxNumberRequiredness(vendorDetail);
        }

        valid &= validateVendorNames(vendorDetail);
        valid &= validateMinimumOrderAmount(vendorDetail);
        valid &= validateOwnershipCategory(vendorDetail);
        valid &= validateVendorWithholdingTaxDates(vendorDetail);
        valid &= validateVendorW8BenOrW9ReceivedIndicator(vendorDetail);
        return valid;
    }

    /**
     * This method validates that if the vendor is set to be inactive, the inactive reason is required.
     * 
     * @param vendorDetail the VendorDetail object to be validated
     * @return False if the vendor is inactive and the inactive reason is empty
     */
    boolean validateInactiveReasonRequiredness(VendorDetail vendorDetail) {
        if (!vendorDetail.isActiveIndicator() && StringUtils.isEmpty(vendorDetail.getVendorInactiveReasonCode())) {
            putFieldError(VendorPropertyConstants.VENDOR_INACTIVE_REASON, VendorKeyConstants.ERROR_INACTIVE_REASON_REQUIRED);
            return false;
        }
        return true;
    }

    /**
     * This method validates that if the vendor is not foreign and if the vendor type's tax number required indicator is true, then
     * the tax number is required. If the vendor foreign indicator is true, then the tax number is not required regardless of its
     * vendor type.
     * 
     * @param vendorDetail the VendorDetail object to be validated
     * @return False if there is no tax number and the indicator is true.
     */
    boolean validateTaxNumberRequiredness(VendorDetail vendorDetail) {
        if (!vendorDetail.getVendorHeader().getVendorForeignIndicator() && vendorDetail.getVendorHeader().getVendorType().isVendorTaxNumberRequiredIndicator() && StringUtils.isBlank(vendorDetail.getVendorHeader().getVendorTaxNumber())) {
            if (vendorDetail.isVendorParentIndicator()) {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_NUMBER, VendorKeyConstants.ERROR_VENDOR_TYPE_REQUIRES_TAX_NUMBER, vendorDetail.getVendorHeader().getVendorType().getVendorTypeDescription());
            }
            else {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_NUMBER, VendorKeyConstants.ERROR_VENDOR_PARENT_NEEDS_CHANGED);
            }
            return false;
        }
        return true;
    }
    
    /**
     * This method validates that, if the vendor is set to be restricted, the restricted reason is required.
     * 
     * @param vendorDetail      The VendorDetail object to be validated
     * @return  False if the vendor is restricted and the restricted reason is empty
     */
    boolean validateRestrictedReasonRequiredness(VendorDetail vendorDetail) {
        if (ObjectUtils.isNotNull(vendorDetail.getVendorRestrictedIndicator()) && vendorDetail.getVendorRestrictedIndicator() && StringUtils.isEmpty(vendorDetail.getVendorRestrictedReasonText() )) {
            putFieldError(VendorPropertyConstants.VENDOR_RESTRICTED_REASON_TEXT, VendorKeyConstants.ERROR_RESTRICTED_REASON_REQUIRED);
            return false;
        }
        return true;
    }

    /**
     * This method validates that if vendor is parent, then tax # and tax type combo should be unique by checking for the existence
     * of vendor(s) with the same tax # and tax type in the existing vendor header table. Ideally we're also supposed to check for
     * pending vendors, but at the moment, the pending vendors are under research investigation, so we're only checking the existing
     * vendors for now. If the vendor is a parent and the validation fails, display the actual error message. If the vendor is not a
     * parent and the validation fails, display the error message that the parent of this vendor needs to be changed, please contact
     * Purchasing Dept. While checking for the existence of vendors with the same tax # and tax type, exclude the vendors with the
     * same id. KULPURAP-302: Allow a duplication of a tax number in vendor header if there are only "inactive" header records with
     * the duplicate record
     * 
     * @param vendorDetail the VendorDetail object to be validated
     * @return boolean true if the vendorDetail passes the unique tax # and tax type validation.
     */
    boolean validateParentVendorTaxNumber(VendorDetail vendorDetail) {
        boolean valid = true;
        boolean isParent = vendorDetail.isVendorParentIndicator();
        Map criteria = new HashMap();
        criteria.put(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE, vendorDetail.getVendorHeader().getVendorTaxTypeCode());
        criteria.put(VendorPropertyConstants.VENDOR_TAX_NUMBER, vendorDetail.getVendorHeader().getVendorTaxNumber());
        criteria.put(KFSPropertyConstants.ACTIVE_INDICATOR, true);
        Map negativeCriteria = new HashMap();
        int existingVendor = 0;
        // If this is editing an existing vendor, we have to include the current vendor's
        // header generated id in the negative criteria so that the current vendor is
        // excluded from the search
        if (ObjectUtils.isNotNull(vendorDetail.getVendorHeaderGeneratedIdentifier())) {
            negativeCriteria.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, vendorDetail.getVendorHeaderGeneratedIdentifier());
            existingVendor = getBoService().countMatching(VendorDetail.class, criteria, negativeCriteria);
        }
        else {
            // If this is creating a new vendor, we can't include the header generated id
            // in the negative criteria because it's null, so we'll only look for existing
            // vendors with the same tax # and tax type regardless of the vendor header generated id.
            existingVendor = getBoService().countMatching(VendorDetail.class, criteria);
        }
        if (existingVendor > 0) {
            if (isParent) {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_NUMBER, VendorKeyConstants.ERROR_VENDOR_TAX_TYPE_AND_NUMBER_COMBO_EXISTS);
            }
            else {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_NUMBER, VendorKeyConstants.ERROR_VENDOR_PARENT_NEEDS_CHANGED);
            }
            valid &= false;
        }
        return valid;
    }

    /**
     * This method validates that the following business rules are satisfied: 1. Tax type cannot be blank if the tax # is not blank.
     * 2. Tax type cannot be set if the tax # is blank. If the vendor is a parent and the validation fails, we'll display an error
     * message indicating that the tax type cannot be blank if the tax # is not blank or that the tax type cannot be set if the tax #
     * is blank. If the vendor is not a parent and the validation fails, we'll display an error message indicating that the parent
     * of this vendor needs to be changed, please contact Purchasing Dept.
     * 
     * @param vendorDetail the VendorDetail object to be validated
     * @return boolean true if the vendor Detail passes the validation and false otherwise.
     */
    boolean validateTaxTypeAndTaxNumberBlankness(VendorDetail vendorDetail) {
        boolean valid = true;
        boolean isParent = vendorDetail.isVendorParentIndicator();
        if (!StringUtils.isBlank(vendorDetail.getVendorHeader().getVendorTaxNumber()) && (StringUtils.isBlank(vendorDetail.getVendorHeader().getVendorTaxTypeCode()))) {
            if (isParent) {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE, VendorKeyConstants.ERROR_VENDOR_TAX_TYPE_CANNOT_BE_BLANK);
            }
            valid &= false;
        }
        else if (StringUtils.isBlank(vendorDetail.getVendorHeader().getVendorTaxNumber()) && !StringUtils.isBlank(vendorDetail.getVendorHeader().getVendorTaxTypeCode())) {
            if (isParent) {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE, VendorKeyConstants.ERROR_VENDOR_TAX_TYPE_CANNOT_BE_SET);
            }
            valid &= false;
        }

        if (!valid && !isParent) {
            putFieldError(VendorPropertyConstants.VENDOR_TAX_TYPE_CODE, VendorKeyConstants.ERROR_VENDOR_PARENT_NEEDS_CHANGED);
        }

        return valid;
    }


    /**
     * This method validates the vendorName, vendorFirstName and vendorLastName fields according to these business rules: 1. At
     * least one of the three vendor name fields must be filled in. 2. Both of the two ways of entering vendor name (One vendor name
     * field vs VendorFirstName/VendorLastName) cannot be used 3. If either the vendor first name or the vendor last name have been
     * entered, the other must be entered.
     * 
     * @param vendorDetail The VendorDetail object to be validated
     * @return boolean true if the vendorDetail passes this validation and false otherwise.
     */
    protected boolean validateVendorNames(VendorDetail vendorDetail) {
        boolean valid = true;
        if (StringUtils.isBlank(vendorDetail.getVendorName())) {
            // At least one of the three vendor name fields must be filled in.
            if (StringUtils.isBlank(vendorDetail.getVendorFirstName()) && StringUtils.isBlank(vendorDetail.getVendorLastName())) {

                putFieldError(VendorPropertyConstants.VENDOR_NAME, VendorKeyConstants.ERROR_VENDOR_NAME_REQUIRED);
                valid &= false;
            }
            // If either the vendor first name or the vendor last name have been entered, the other must be entered.
            else if (StringUtils.isBlank(vendorDetail.getVendorFirstName()) || StringUtils.isBlank(vendorDetail.getVendorLastName())) {

                putFieldError(VendorPropertyConstants.VENDOR_NAME, VendorKeyConstants.ERROR_VENDOR_BOTH_NAME_REQUIRED);
                valid &= false;
            }
        }
        else {
            // Both of the two ways of entering vendor name (One vendor name field vs VendorFirstName/VendorLastName) cannot be used
            if (!StringUtils.isBlank(vendorDetail.getVendorFirstName()) || !StringUtils.isBlank(vendorDetail.getVendorLastName())) {

                putFieldError(VendorPropertyConstants.VENDOR_NAME, VendorKeyConstants.ERROR_VENDOR_NAME_INVALID);
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * This method validates the ownership type codes that aren't allowed for the tax type of the vendor. The rules are : 1. If tax
     * type is "SSN", then check the ownership type against the allowed types for "SSN" in the Rules table. 2. If tax type is
     * "FEIN", then check the ownership type against the allowed types for "FEIN" in the Rules table. If the vendor is a parent and
     * the validation fails, display the actual error message. If the vendor is not a parent and the validation fails, display the
     * error message that the parent of this vendor needs to be changed, please contact Purchasing Dept.
     * 
     * @param vendorDetail The VendorDetail object to be validated
     * @return TRUE if the ownership type is allowed and FALSE otherwise.
     */
    private boolean validateOwnershipTypeAllowed(VendorDetail vendorDetail) {
        boolean valid = true;
        boolean isParent = vendorDetail.isVendorParentIndicator();
        String ownershipTypeCode = vendorDetail.getVendorHeader().getVendorOwnershipCode();
        String taxTypeCode = vendorDetail.getVendorHeader().getVendorTaxTypeCode();
        if (StringUtils.isNotEmpty(ownershipTypeCode) && StringUtils.isNotEmpty(taxTypeCode)) {
            if (VendorConstants.TAX_TYPE_FEIN.equals(taxTypeCode)) {
                if ( getKualiConfigurationService().failsRule( KFSConstants.VENDOR_NAMESPACE, VendorConstants.Components.VENDOR, PURAP_FEIN_ALLOWED_OWNERSHIP_TYPES, ownershipTypeCode ) ) {
                    valid &= false;
                }
            }
            else if (VendorConstants.TAX_TYPE_SSN.equals(taxTypeCode)) {
                if ( getKualiConfigurationService().failsRule( KFSConstants.VENDOR_NAMESPACE, VendorConstants.Components.VENDOR, PURAP_SSN_ALLOWED_OWNERSHIP_TYPES, ownershipTypeCode ) ) {
                    valid &= false;
                }
            }
        }
        if (!valid && isParent) {
            putFieldError(VendorPropertyConstants.VENDOR_OWNERSHIP_CODE, VendorKeyConstants.ERROR_OWNERSHIP_TYPE_CODE_NOT_ALLOWED, new String[] { vendorDetail.getVendorHeader().getVendorOwnership().getVendorOwnershipDescription(), taxTypeCode });
        }
        else if (!valid && !isParent) {
            putFieldError(VendorPropertyConstants.VENDOR_OWNERSHIP_CODE, VendorKeyConstants.ERROR_VENDOR_PARENT_NEEDS_CHANGED);
        }
        return valid;
    }


    /**
     * Per code review 9/19, these business rules should be moved to the rule table. This method validates that the minimum order
     * amount is less than the minimum order amount constant that is currently defined in VendorConstants.java but someday should be
     * moved to APC.
     * 
     * @param vendorDetail The VendorDetail object to be validated
     * @return True if the vendorMinimumOrderAmount is less than the minimum order amount specified in the VendorConstants (in the
     *         future the amount will be in APC).
     */
    private boolean validateMinimumOrderAmount(VendorDetail vendorDetail) {
        boolean valid = true;
        KualiDecimal minimumOrderAmount = vendorDetail.getVendorMinimumOrderAmount();
        if (minimumOrderAmount != null) {
            if (ObjectUtils.isNull(VENDOR_MIN_ORDER_AMOUNT)) {
                VENDOR_MIN_ORDER_AMOUNT = new KualiDecimal(getKualiConfigurationService().getParameterValue(KFSConstants.PURAP_NAMESPACE, VendorConstants.Components.VENDOR, PURAP_VENDOR_MIN_ORDER_AMOUNT));
            }
            if ((VENDOR_MIN_ORDER_AMOUNT.compareTo(minimumOrderAmount) < 1) || (minimumOrderAmount.isNegative())) {
                putFieldError(VendorPropertyConstants.VENDOR_MIN_ORDER_AMOUNT, VendorKeyConstants.ERROR_VENDOR_MAX_MIN_ORDER_AMOUNT, VENDOR_MIN_ORDER_AMOUNT.toString());
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * This method validates that if the ownership category allowed indicator is false, the vendor does not have ownership category.
     * It will return false if the vendor contains ownership category. If the vendor is a parent and the validation fails, display
     * the actual error message. If the vendor is not a parent and the validation fails, display the error message that the parent
     * of this vendor needs to be changed, please contact Purchasing Dept.
     * 
     * @param vendorDetail The VendorDetail to be validated
     * @return true if the vendor does not contain ownership category and false otherwise
     */
    private boolean validateOwnershipCategory(VendorDetail vendorDetail) {
        boolean valid = true;
        boolean isParent = vendorDetail.isVendorParentIndicator();
        OwnershipType ot = vendorDetail.getVendorHeader().getVendorOwnership();
        if (ot != null && !ot.getVendorOwnershipCategoryAllowedIndicator()) {
            if (ObjectUtils.isNotNull(vendorDetail.getVendorHeader().getVendorOwnershipCategory())) {
                valid &= false;
            }
        }
        if (!valid && isParent) {
            putFieldError(VendorPropertyConstants.VENDOR_OWNERSHIP_CATEGORY_CODE, VendorKeyConstants.ERROR_OWNERSHIP_CATEGORY_CODE_NOT_ALLOWED, new String[] { vendorDetail.getVendorHeader().getVendorOwnershipCategory().getVendorOwnershipCategoryDescription(), vendorDetail.getVendorHeader().getVendorOwnership().getVendorOwnershipDescription() });
        }
        else if (!valid && !isParent) {
            putFieldError(VendorPropertyConstants.VENDOR_OWNERSHIP_CODE, VendorKeyConstants.ERROR_VENDOR_PARENT_NEEDS_CHANGED);
        }
        return valid;
    }

    /**
     * This method calls the methods in TaxNumberService to validate the tax number for these business rules: 1. Tax number must be
     * 9 digits and cannot be all zeros (but can be blank). 2. First three digits of a SSN cannot be 000. 3. First three digits of a
     * SSN cannot be 666. 4. Middle two digits of a SSN cannot be 00. 5. Last four digits of a SSN cannot be 0000. 6. First two
     * digits of a FEIN cannot be 00. 7. TODO: This tax number is not allowed: 356001673
     * 
     * @param vendorDetail The VendorDetail object to be validated
     * @return true if the tax number is a valid tax number and false otherwise.
     */
    private boolean validateTaxNumberFromTaxNumberService(VendorDetail vendorDetail) {
        boolean valid = true;
        boolean isParent = vendorDetail.isVendorParentIndicator();
        String taxNumber = vendorDetail.getVendorHeader().getVendorTaxNumber();
        String taxType = vendorDetail.getVendorHeader().getVendorTaxTypeCode();
        if (!StringUtils.isEmpty(taxType) && !StringUtils.isEmpty(taxNumber)) {
            valid = SpringContext.getBean(TaxNumberService.class).isValidTaxNumber(taxNumber, taxType);
            if (!valid && isParent) {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_NUMBER, VendorKeyConstants.ERROR_TAX_NUMBER_INVALID);
            }
            valid = SpringContext.getBean(TaxNumberService.class).isAllowedTaxNumber(taxNumber);
            if (!valid && isParent) {
                putFieldError(VendorPropertyConstants.VENDOR_TAX_NUMBER, VendorKeyConstants.ERROR_TAX_NUMBER_NOT_ALLOWED);
            }
        }
        if (!valid && !isParent) {
            putFieldError(VendorPropertyConstants.VENDOR_TAX_NUMBER, VendorKeyConstants.ERROR_VENDOR_PARENT_NEEDS_CHANGED);
        }
        return valid;
    }

    boolean processAddressValidation(MaintenanceDocument document) {
        boolean valid = true;
        boolean validAddressType = false;

        List<VendorAddress> addresses = newVendor.getVendorAddresses();

        String vendorTypeCode = newVendor.getVendorHeader().getVendorTypeCode();
        String vendorAddressTypeRequiredCode = newVendor.getVendorHeader().getVendorType().getVendorAddressTypeRequiredCode();

        for (int i = 0; i < addresses.size(); i++) {
            VendorAddress address = addresses.get(i);
            String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_ADDRESS + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);

            if (address.getVendorAddressTypeCode().equals(vendorAddressTypeRequiredCode)) {
                validAddressType = true;
            }


            valid &= checkFaxNumber(address);
            valid &= checkAddressCountryEmptyStateZip(address);

            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
        }

        // validate Address Type

        if (!StringUtils.isBlank(vendorTypeCode) && !StringUtils.isBlank(vendorAddressTypeRequiredCode) && !validAddressType) {
            String[] parameters = new String[] { vendorTypeCode, vendorAddressTypeRequiredCode };
            String vendorAddressTabPrefix = KFSConstants.ADD_PREFIX + "." + VendorPropertyConstants.VENDOR_ADDRESS + "." ;
            putFieldError(vendorAddressTabPrefix + VendorPropertyConstants.VENDOR_ADDRESS_TYPE_CODE, VendorKeyConstants.ERROR_ADDRESS_TYPE, parameters);
            putFieldError(vendorAddressTabPrefix + VendorPropertyConstants.VENDOR_ADDRESS_LINE_1, KFSKeyConstants.ERROR_REQUIRED, KFSPropertyConstants.ADDRESS_LINE1);
            putFieldError(vendorAddressTabPrefix + VendorPropertyConstants.VENDOR_ADDRESS_CITY, KFSKeyConstants.ERROR_REQUIRED, KFSPropertyConstants.CITY);
            putFieldError(vendorAddressTabPrefix + VendorPropertyConstants.VENDOR_ADDRESS_COUNTRY, KFSKeyConstants.ERROR_REQUIRED, KFSPropertyConstants.COUNTRY_CODE);
            valid = false;

        }


        valid &= validateDefaultAddressCampus(newVendor);

        // Check to see if all divisions have one desired address for this vendor type
        Map fieldValues = new HashMap();
        fieldValues.put(VendorPropertyConstants.VENDOR_HEADER_GENERATED_ID, newVendor.getVendorHeaderGeneratedIdentifier());
        // Find all the addresses for this vendor and its divisions:
        List<VendorAddress> vendorDivisionAddresses = new ArrayList(SpringContext.getBean(BusinessObjectService.class).findMatchingOrderBy(VendorAddress.class, fieldValues, VendorPropertyConstants.VENDOR_DETAIL_ASSIGNED_ID, true));
        
        // This set stores the vendorDetailedAssignedIds for the vendor divisions which is
        // bascically the division numbers 0, 1, 2, ...
        HashSet<Integer> vendorDetailedIds = new HashSet();
        // This set stores the vendor division numbers of the ones which have one address of the desired type
        HashSet<Integer> vendorDivisionsIdsWithDesiredAddressType = new HashSet();

        for (VendorAddress vendorDivisionAddress : vendorDivisionAddresses) {
            // We need to exclude the first one  Since we already checked for this in valid AddressType above.
            if (vendorDivisionAddress.getVendorDetailAssignedIdentifier() != 0) {
                vendorDetailedIds.add(vendorDivisionAddress.getVendorDetailAssignedIdentifier());
                if (vendorDivisionAddress.getVendorAddressTypeCode().equalsIgnoreCase(vendorAddressTypeRequiredCode)) {
                    vendorDivisionsIdsWithDesiredAddressType.add(vendorDivisionAddress.getVendorDetailAssignedIdentifier());
                }
            }
        }

        // If the number of divisions with the desired address type is less than the number of divisions for his vendor
        if (vendorDivisionsIdsWithDesiredAddressType.size() < vendorDetailedIds.size()) {

            Iterator itr = vendorDetailedIds.iterator();

            Integer value;
            String vendorId;

            while (itr.hasNext()) {
                value = (Integer) itr.next();
                if (!vendorDivisionsIdsWithDesiredAddressType.contains(value)) {
                    vendorId = newVendor.getVendorHeaderGeneratedIdentifier().toString() + '-' + value.toString();
                    String[] parameters = new String[] { vendorId, vendorTypeCode, vendorAddressTypeRequiredCode };
                    putFieldError(VendorPropertyConstants.VENDOR_TYPE_CODE, VendorKeyConstants.ERROR_ADDRESS_TYPE_DIVISIONS, parameters);
                    valid = false;
                }
            }
        }

        return valid;

    }

    /**
     * This method validates that if US is selcted for country that the state and zip are not empty
     * 
     * @param addresses
     * @return
     */
    boolean checkAddressCountryEmptyStateZip(VendorAddress address) {

        boolean valid = true;
        boolean noPriorErrMsg = true;

        Country country = address.getVendorCountry();
        if (ObjectUtils.isNotNull(country) && StringUtils.equals(KFSConstants.COUNTRY_CODE_UNITED_STATES, country.getPostalCountryCode())) {

            if ((ObjectUtils.isNull(address.getVendorState()) || StringUtils.isEmpty(address.getVendorState().getPostalStateCode()))) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_ADDRESS_STATE, VendorKeyConstants.ERROR_US_REQUIRES_STATE);
                valid &= false;
                noPriorErrMsg = false;
            }
            // The error message here will be the same for both, and should not be repeated (KULPURAP-516).
            if (noPriorErrMsg && StringUtils.isEmpty(address.getVendorZipCode())) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_ADDRESS_ZIP, VendorKeyConstants.ERROR_US_REQUIRES_ZIP);
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * This method checks if the "allow default indicator" is true or false for this address.
     * 
     * @param addresses
     * @return
     */

    boolean findAllowDefaultAddressIndicatorHelper(VendorAddress vendorAddress) {

        AddressType addressType = new AddressType();

        addressType = vendorAddress.getVendorAddressType();
        if (ObjectUtils.isNull(addressType)) {
            return false;
        }
        // Retreiving the Default Address Indicator for this Address Type:
        return addressType.getVendorDefaultIndicator();

    }

    /**
     * This method When add button is selected on Default Address, checks if the allow default indicator is set to false for this
     * address type then it does not allow user to select a default address for this address and if it is true then it allows only
     * one campus to be default for this address.
     * 
     * @param addresses
     * @return
     */
    // TODO: Naser See if there is a way to put the error message in the address tab instead of down the page
    boolean checkDefaultAddressCampus(VendorDetail vendorDetail, VendorDefaultAddress addedDefaultAddress, VendorAddress parent) {

        VendorAddress vendorAddress = parent;
        if (ObjectUtils.isNull(vendorAddress)) {
            return false;
        }
        int j = vendorDetail.getVendorAddresses().indexOf(vendorAddress);
        String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_ADDRESS + "[" + j + "]";
        GlobalVariables.getErrorMap().addToErrorPath(errorPath);
        // Retreiving the Default Address Indicator for this Address Type:
        boolean allowDefaultAddressIndicator = findAllowDefaultAddressIndicatorHelper(vendorAddress);
        String addedAddressCampusCode = addedDefaultAddress.getVendorCampusCode();
        String addedAddressTypeCode = vendorAddress.getVendorAddressTypeCode();
        // if the selected address type does not allow defaults, then the user should not be allowed to
        // select the default indicator or add any campuses to the address
        if (allowDefaultAddressIndicator == false) {
            String[] parameters = new String[] { addedAddressTypeCode };
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS + "[" + 0 + "]." + VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_CAMPUS, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_CAMPUS_NOT_ALLOWED, parameters);
            return false;
        }

        List<VendorDefaultAddress> vendorDefaultAddresses = vendorAddress.getVendorDefaultAddresses();

        for (int i = 0; i < vendorDefaultAddresses.size(); i++) {
            VendorDefaultAddress vendorDefaultAddress = vendorDefaultAddresses.get(i);
            if (vendorDefaultAddress.getVendorCampusCode().equalsIgnoreCase(addedAddressCampusCode)) {
                String[] parameters = new String[] { addedAddressCampusCode, addedAddressTypeCode };
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS + "[" + i + "]." + VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_CAMPUS, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_CAMPUS, parameters);
                // GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
                return false;
            }

        }

        return true;
    }

    /**
     * This method checks if the allow default indicator is set to false for this address the default indicator cannot be set to
     * true/yes. If "allow default indicator" is set to true/yes for address type, one address must have the default indicator set
     * (no more, no less) and only one campus to be set as default for this address.
     * 
     * @param vendorDetail
     * @return false or true
     */

    boolean validateDefaultAddressCampus(VendorDetail vendorDetail) {
        List<VendorAddress> vendorAddresses = vendorDetail.getVendorAddresses();
        String addressTypeCode;
        String campusCode;
        boolean valid = true;
        boolean previousValue = false;

        // This is a HashMap to store the default Address Type Codes and their associated default Indicator
        HashMap addressTypeCodeDefaultIndicator = new HashMap();

        // This is a HashMap to store Address Type Codes and Address Campus Codes for Default Addresses
        HashMap addressTypeDefaultCampus = new HashMap();

        // This is a HashSet for storing only the Address Type Codes which have at leat one default Indicator set to true
        HashSet addressTypesHavingDefaultTrue = new HashSet();

        int i = 0;
        for (VendorAddress address : vendorAddresses) {
            addressTypeCode = address.getVendorAddressTypeCode();
            String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_ADDRESS + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);
            String[] parameters = new String[] { addressTypeCode };

            // If "allow default indicator" is set to true/yes for address type, one address must have the default indicator set (no
            // more, no less).
            // For example, if a vendor contains three PO type addresses and the PO address type is set to allow defaults in the
            // address type table,
            // then only one of these PO addresses can have the default indicator set to true/yes.

            if (findAllowDefaultAddressIndicatorHelper(address)) {
                if (address.isVendorDefaultAddressIndicator()) {
                    addressTypesHavingDefaultTrue.add(addressTypeCode);
                }
                if (!addressTypeCodeDefaultIndicator.isEmpty() && addressTypeCodeDefaultIndicator.containsKey(addressTypeCode)) {
                    previousValue = ((Boolean) addressTypeCodeDefaultIndicator.get(addressTypeCode)).booleanValue();
                }

                if (addressTypeCodeDefaultIndicator.put(addressTypeCode, address.isVendorDefaultAddressIndicator()) != null && previousValue && address.isVendorDefaultAddressIndicator()) {
                    GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_INDICATOR, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_INDICATOR, parameters);
                    valid = false;
                }

            }
            // If "allow default indicator" is set to false/no for address type, the default indicator cannot be set to true/yes.
            else {
                if (address.isVendorDefaultAddressIndicator()) {
                    GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_INDICATOR, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_ADDRESS_NOT_ALLOWED, parameters);
                    valid = false;
                }

            }

            List<VendorDefaultAddress> vendorDefaultAddresses = address.getVendorDefaultAddresses();

            // If "allow default indicator" is set to true/yes for address type, a campus can only be set on one of each type of
            // Address.
            // For example, Bloomington can not be included in the campus list for two PO type addresses.
            // Each campus can only have one default address.
            int j = 0;
            for (VendorDefaultAddress defaultAddress : vendorDefaultAddresses) {
                campusCode = (String) addressTypeDefaultCampus.put(addressTypeCode, defaultAddress.getVendorCampusCode());
                if (StringUtils.isNotBlank(campusCode) && campusCode.equalsIgnoreCase(defaultAddress.getVendorCampusCode())) {
                    String[] newParameters = new String[] { defaultAddress.getVendorCampusCode(), addressTypeCode };
                    GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS + "[" + j + "]." + VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_CAMPUS, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_CAMPUS, newParameters);
                    valid = false;
                }
                j++;
            }
            i++;
            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
        }

        // If "allow default indicator" is set to true/yes for address type, one address must have the default indicator set to true
        if (!addressTypeCodeDefaultIndicator.isEmpty()) {
            Set<String> addressTypes = addressTypeCodeDefaultIndicator.keySet();

            for (String addressType : addressTypes) {
                if (!addressTypesHavingDefaultTrue.contains(addressType)) {
                    String[] parameters = new String[] { addressType };
                    GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_INDICATOR, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_INDICATOR, parameters);
                    valid = false;
                }
            }
        }

        return valid;
    }


    /**
     * This method validates that the Vendor Fax Number is a valid phone number.
     * 
     * @param addresses
     * @return
     */
    boolean checkFaxNumber(VendorAddress address) {
        boolean valid = true;
        String faxNumber = address.getVendorFaxNumber();
        if (StringUtils.isNotEmpty(faxNumber) && !SpringContext.getBean(PhoneNumberService.class).isValidPhoneNumber(faxNumber)) {
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_FAX_NUMBER, VendorKeyConstants.ERROR_FAX_NUMBER);
            valid &= false;
        }
        return valid;
    }


    private boolean processContactValidation(MaintenanceDocument document) {
        boolean valid = true;
        // leaving stub method here as placeholder for future Contact Validation
        return valid;
    }

    private boolean processCustomerNumberValidation(MaintenanceDocument document) {
        boolean valid = true;

        List<VendorCustomerNumber> customerNumbers = newVendor.getVendorCustomerNumbers();
        for (VendorCustomerNumber customerNumber : customerNumbers) {
            valid &= validateVendorCustomerNumber(customerNumber);
        }
        return valid;
    }

    boolean validateVendorCustomerNumber(VendorCustomerNumber customerNumber) {
        boolean valid = true;

        // The chart and org must exist in the database.
        String chartOfAccountsCode = customerNumber.getChartOfAccountsCode();
        String orgCode = customerNumber.getVendorOrganizationCode();
        if (!StringUtils.isBlank(chartOfAccountsCode) && !StringUtils.isBlank(orgCode)) {
            Map chartOrgMap = new HashMap();
            chartOrgMap.put("chartOfAccountsCode", chartOfAccountsCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Chart.class, chartOrgMap) < 1) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CUSTOMER_NUMBER_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_EXISTENCE, chartOfAccountsCode);
                valid &= false;
            }
            chartOrgMap.put("organizationCode", orgCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Org.class, chartOrgMap) < 1) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CUSTOMER_NUMBER_ORGANIZATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, orgCode);
                valid &= false;
            }
        }
        return valid;
    }

    private boolean processContractValidation(MaintenanceDocument document) {
        boolean valid = true;
        List<VendorContract> contracts = newVendor.getVendorContracts();
        if (ObjectUtils.isNull(contracts)) {
            return valid;
        }

        //If the vendorContractAllowedIndicator is false, it cannot have vendor contracts, return false;
        if (contracts.size() > 0 && !newVendor.getVendorHeader().getVendorType().isVendorContractAllowedIndicator()) {
            valid = false;
            String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_CONTRACT + "[0]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);
            GlobalVariables.getErrorMap().putError( VendorPropertyConstants.VENDOR_CONTRACT_NAME, VendorKeyConstants.ERROR_VENDOR_CONTRACT_NOT_ALLOWED );
            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
            return valid;
        }
        
        for (int i = 0; i < contracts.size(); i++) {
            VendorContract contract = contracts.get(i);

            String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_CONTRACT + "[" + i + "]";
            GlobalVariables.getErrorMap().addToErrorPath(errorPath);

            valid &= validateVendorContractPOLimitAndExcludeFlagCombination(contract);
            valid &= validateVendorContractBeginEndDates(contract);

            GlobalVariables.getErrorMap().removeFromErrorPath(errorPath);
        }
        return valid;
    }

    /**
     * This method validates that the proper combination of Exclude Indicator and APO Amount is present on a vendor contract. Do not
     * perform this validation on Contract add line as the user cannot currently enter the sub-collection of contract-orgs so we
     * should not force this until the document is submitted. The rules are : 1. Must enter a Default APO Limit or at least one
     * organization with an APO Amount. 2. If the Exclude Indicator for an organization is N, an organization APO Amount is
     * required. 3. If the Exclude Indicator for an organization is Y, the organization APO Amount is not allowed.
     * 
     * @return True if the proper combination of Exclude Indicator and APO Amount is present. False otherwise.
     */
    boolean validateVendorContractPOLimitAndExcludeFlagCombination(VendorContract contract) {
        boolean valid = true;
        boolean NoOrgHasApoLimit = true;

        List<VendorContractOrganization> organizations = contract.getVendorContractOrganizations();
        if (ObjectUtils.isNotNull(organizations)) {
            int organizationCounter = 0;
            for (VendorContractOrganization organization : organizations) {
                if (ObjectUtils.isNotNull(organization.getVendorContractPurchaseOrderLimitAmount())) {
                    NoOrgHasApoLimit = false;
                }
                valid &= validateVendorContractOrganization(organization);
                organizationCounter++;
            }
        }
        if (NoOrgHasApoLimit && ObjectUtils.isNull(contract.getOrganizationAutomaticPurchaseOrderLimit())) {
            // Rule #1 in the above java doc has been violated.
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_DEFAULT_APO_LIMIT, VendorKeyConstants.ERROR_VENDOR_CONTRACT_NO_APO_LIMIT);
            valid &= false;
        }
        return valid;
    }

    /**
     * This method validates that: 1. If the VendorContractBeginningDate is entered then the VendorContractEndDate is also entered,
     * and vice versa. 2. If both dates are entered, the VendorContractBeginningDate is before the VendorContractEndDate. The date
     * fields are required so we should know that we have valid dates.
     * 
     * @return True if the beginning date is before the end date. False if only one date is entered or the beginning date is after
     *         the end date.
     */
    boolean validateVendorContractBeginEndDates(VendorContract contract) {
        boolean valid = true;

        if (ObjectUtils.isNotNull(contract.getVendorContractBeginningDate()) && ObjectUtils.isNull(contract.getVendorContractEndDate())) {
            GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_END_DATE, VendorKeyConstants.ERROR_VENDOR_CONTRACT_BEGIN_DATE_NO_END_DATE);
            valid &= false;
        }
        else {
            if (ObjectUtils.isNull(contract.getVendorContractBeginningDate()) && ObjectUtils.isNotNull(contract.getVendorContractEndDate())) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_BEGIN_DATE, VendorKeyConstants.ERROR_VENDOR_CONTRACT_END_DATE_NO_BEGIN_DATE);
                valid &= false;
            }
        }
        if (valid && ObjectUtils.isNotNull(contract.getVendorContractBeginningDate()) && ObjectUtils.isNotNull(contract.getVendorContractEndDate())) {
            if (contract.getVendorContractBeginningDate().after(contract.getVendorContractEndDate())) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_BEGIN_DATE, VendorKeyConstants.ERROR_VENDOR_CONTRACT_BEGIN_DATE_AFTER_END);
                valid &= false;
            }
        }
        // contractCounter++;
        // }
        return valid;
    }

    /**
     * This method validates a vendor contract organization. The rules are : 1. If the Exclude Indicator for the organization is N,
     * an organization APO Amount is required. 2. If the Exclude Indicator for the organization is Y, an organization APO Amount is
     * not allowed. 3. The chart and org for the organization must exist in the database.
     * 
     * @return True if these three rules are passed. False otherwise.
     */
    boolean validateVendorContractOrganization(VendorContractOrganization organization) {
        boolean valid = true;

        boolean isExcluded = organization.isVendorContractExcludeIndicator();
        if (isExcluded) {
            if (ObjectUtils.isNotNull(organization.getVendorContractPurchaseOrderLimitAmount())) {
                // Rule #2 in the above java doc has been violated.
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_ORGANIZATION_APO_LIMIT, VendorKeyConstants.ERROR_VENDOR_CONTRACT_ORG_EXCLUDED_WITH_APO_LIMIT);
                valid &= false;
            }
        }
        else { // isExcluded = false
            if (ObjectUtils.isNull(organization.getVendorContractPurchaseOrderLimitAmount())) {
                // Rule #1 in the above java doc has been violated.
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_ORGANIZATION_APO_LIMIT, VendorKeyConstants.ERROR_VENDOR_CONTRACT_ORG_NOT_EXCLUDED_NO_APO_LIMIT);
                valid &= false;
            }
        }

        // The chart and org must exist in the database.
        String chartOfAccountsCode = organization.getChartOfAccountsCode();
        String orgCode = organization.getOrganizationCode();
        if (!StringUtils.isBlank(chartOfAccountsCode) && !StringUtils.isBlank(orgCode)) {
            Map chartOrgMap = new HashMap();
            chartOrgMap.put("chartOfAccountsCode", chartOfAccountsCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Chart.class, chartOrgMap) < 1) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_EXISTENCE, chartOfAccountsCode);
                valid &= false;
            }
            chartOrgMap.put("organizationCode", orgCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Org.class, chartOrgMap) < 1) {
                GlobalVariables.getErrorMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_ORGANIZATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, orgCode);
                valid &= false;
            }
        }

        return valid;
    }

    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;


        // this incoming bo needs to be refreshed because it doesn't have its subobjects setup
        // TODO: can this be moved up?
        bo.refreshNonUpdateableReferences();

        if (bo instanceof VendorAddress) {
            VendorAddress address = (VendorAddress) bo;
            success &= checkAddressCountryEmptyStateZip(address);
            VendorDetail vendorDetail = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();

        }
        if (bo instanceof VendorContract) {
            VendorContract contract = (VendorContract) bo;
            success &= validateVendorContractBeginEndDates(contract);
        }
        if (bo instanceof VendorContractOrganization) {
            VendorContractOrganization contractOrg = (VendorContractOrganization) bo;
            success &= validateVendorContractOrganization(contractOrg);
        }
        if (bo instanceof VendorCustomerNumber) {
            VendorCustomerNumber customerNumber = (VendorCustomerNumber) bo;
            success &= validateVendorCustomerNumber(customerNumber);
        }
        if (bo instanceof VendorDefaultAddress) {
            VendorDefaultAddress defaultAddress = (VendorDefaultAddress) bo;

            // TODO: this is a total hack we shouldn't have to set the foreign key here, this should be set in the parent
            // in a much more general way see issue KULPURAP-266 for a preliminary discussion
            String parentName = StringUtils.substringBeforeLast(collectionName, ".");
            VendorAddress parent = (VendorAddress) ObjectUtils.getPropertyValue(this.getNewBo(), parentName);
            VendorDetail vendorDetail = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();
            success &= checkDefaultAddressCampus(vendorDetail, defaultAddress, parent);
        }

        return success;
    }
    
    /**
     * This method is the implementation of the rule that if a document has a federal witholding tax begin date and end date, the begin
     * date should come before the end date. 
     * 
     * @param vdDocument
     * @return
     */
    private boolean validateVendorWithholdingTaxDates(VendorDetail vdDocument) {
        boolean valid = true;
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        Date beginDate = vdDocument.getVendorHeader().getVendorFederalWithholdingTaxBeginningDate();
        Date endDate = vdDocument.getVendorHeader().getVendorFederalWithholdingTaxEndDate();
        if (ObjectUtils.isNotNull(beginDate) && ObjectUtils.isNotNull(endDate)) {
            if (dateTimeService.dateDiff( beginDate, endDate, false ) <= 0 ) {
               putFieldError(VendorPropertyConstants.VENDOR_FEDERAL_WITHOLDING_TAX_BEGINNING_DATE, VendorKeyConstants.ERROR_VENDOR_TAX_BEGIN_DATE_AFTER_END);
               valid &= false;
            }
        }
        return valid;

    }
    
    /**
     * This method is the implementation of the rule that both w9 received and w-8ben  cannot be set to yes
     * 
     * @param vdDocument
     * @return
     */
    private boolean validateVendorW8BenOrW9ReceivedIndicator(VendorDetail vdDocument) {
        boolean valid = true;

        if (ObjectUtils.isNotNull(vdDocument.getVendorHeader().getVendorW9ReceivedIndicator()) && ObjectUtils.isNotNull(vdDocument.getVendorHeader().getVendorW8BenReceivedIndicator()) && vdDocument.getVendorHeader().getVendorW9ReceivedIndicator() && vdDocument.getVendorHeader().getVendorW8BenReceivedIndicator()) {
            putFieldError(VendorPropertyConstants.VENDOR_W9_RECEIVED_INDICATOR, VendorKeyConstants.ERROR_VENDOR_W9_AND_W8_RECEIVED_INDICATOR_BOTH_TRUE);
            valid &= false;
        }
        return valid;

    }
}