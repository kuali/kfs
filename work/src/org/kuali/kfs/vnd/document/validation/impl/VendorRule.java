/*
 * Copyright 2007 The Kuali Foundation
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
package org.kuali.kfs.vnd.document.validation.impl;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Chart;
import org.kuali.kfs.coa.businessobject.Organization;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.PostalCodeValidationService;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.VendorKeyConstants;
import org.kuali.kfs.vnd.VendorParameterConstants;
import org.kuali.kfs.vnd.VendorPropertyConstants;
import org.kuali.kfs.vnd.businessobject.AddressType;
import org.kuali.kfs.vnd.businessobject.OwnershipType;
import org.kuali.kfs.vnd.businessobject.VendorAddress;
import org.kuali.kfs.vnd.businessobject.VendorAlias;
import org.kuali.kfs.vnd.businessobject.VendorCommodityCode;
import org.kuali.kfs.vnd.businessobject.VendorContact;
import org.kuali.kfs.vnd.businessobject.VendorContract;
import org.kuali.kfs.vnd.businessobject.VendorContractOrganization;
import org.kuali.kfs.vnd.businessobject.VendorCustomerNumber;
import org.kuali.kfs.vnd.businessobject.VendorDefaultAddress;
import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.kfs.vnd.businessobject.VendorHeader;
import org.kuali.kfs.vnd.businessobject.VendorType;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.kfs.vnd.service.CommodityCodeService;
import org.kuali.kfs.vnd.service.TaxNumberService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.datadictionary.validation.fieldlevel.FixedPointValidationPattern;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.AttributeDefinition;
import org.kuali.rice.krad.datadictionary.validation.ValidationPattern;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.PersistenceService;
import org.kuali.rice.krad.util.ErrorMessage;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.util.AutoPopulatingList;

/**
 * Business rules applicable to VendorDetail document.
 */
public class VendorRule extends MaintenanceDocumentRuleBase {

    private VendorDetail oldVendor;
    private VendorDetail newVendor;


    /**
     * Overrides the setupBaseConvenienceObjects from the superclass because we cannot use the setupBaseConvenienceObjects from the
     * superclass. The reason we cannot use the superclass method is because it calls the updateNonUpdateableReferences for
     * everything and we cannot do that for parent vendors, because we want to update vendor header information only on parent
     * vendors, so the saving of the vendor header is done manually. If we call the updateNonUpdateableReferences, it is going to
     * overwrite any changes that the user might have done in the vendor header with the existing values in the database.
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupBaseConvenienceObjects(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void setupBaseConvenienceObjects(MaintenanceDocument document) {
        oldVendor = (VendorDetail) document.getOldMaintainableObject().getBusinessObject();
        newVendor = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();
        super.setNewBo(newVendor);
        setupConvenienceObjects();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        // setup oldVendor convenience objects, make sure all possible sub-objects are populated
        refreshSubObjects(oldVendor);

        // setup newVendor convenience objects, make sure all possible sub-objects are populated
        refreshSubObjects(newVendor);
    }


    /**
     * Refreshes the references of vendor detail and its sub objects
     *
     * @param vendor VendorDetail document
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
            List<String> headerFieldNames = getObjectReferencesListFromBOClass(VendorHeader.class);
            vendor.getVendorHeader().refreshNonUpdateableReferences();
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
     * This is currently used as a helper to get a list of object references (e.g. vendorType, vendorOwnershipType, etc) from a
     * BusinessObject (e.g. VendorHeader, VendorDetail, etc) class dynamically. Feel free to enhance it, refactor it or move it to a
     * superclass or elsewhere as you see appropriate.
     *
     * @param theClass The Class name of the object whose objects references list are extracted
     * @return List a List of attributes of the class
     */
    private List getObjectReferencesListFromBOClass(Class theClass) {
        List<String> results = new ArrayList();
        for (Field theField : theClass.getDeclaredFields()) {
            // only get persistable business object references
            if ( PersistableBusinessObject.class.isAssignableFrom( theField.getType() ) ) {
                    results.add(theField.getName());
                }
            }
        return results;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = processValidation(document);
        return valid & super.processCustomApproveDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = processValidation(document);
        return valid & super.processCustomRouteDocumentBusinessRules(document);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        boolean valid = true;
        return valid & super.processCustomSaveDocumentBusinessRules(document);
    }

    /**
     * Validates VendorDetail and its VendorContracts.
     *
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
    private boolean processValidation(MaintenanceDocument document) {
        boolean valid = true;

        valid &= processVendorValidation(document);
        valid &= processContactValidation(document);
        if (ObjectUtils.isNotNull(newVendor.getVendorHeader().getVendorType())) {
            valid &= processAddressValidation(document);
            valid &= processContractValidation(document);
            valid &= processCommodityCodeValidation(document);
        }

        return valid;
    }

    /**
     * Validates VendorDetail document.
     *
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
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
        valid &= validateVendorSoldToNumber(vendorDetail);
        valid &= validateMinimumOrderAmount(vendorDetail);
        valid &= validateOwnershipCategory(vendorDetail);
        valid &= validateVendorWithholdingTaxDates(vendorDetail);
        valid &= validateVendorW8BenOrW9ReceivedIndicator(vendorDetail);
        valid &= validateSearchAliases(vendorDetail);
        valid &= validateContracts(vendorDetail);
        return valid;
    }

    private boolean validateContracts(VendorDetail vendorDetail) {
        boolean success = true;
        int vendorPos = 0;
        List<VendorContract> vendorContracts = vendorDetail.getVendorContracts();
        for (VendorContract vendorContract : vendorContracts) {
            List<VendorContractOrganization> organizations = vendorContract.getVendorContractOrganizations();
            List<VendorContractOrganization> organizationCopy = new ArrayList<VendorContractOrganization>(organizations);
            for (VendorContractOrganization organization :organizations ) {
                 String chartCode = organization.getChartOfAccountsCode();
                 String organizationCode = organization.getOrganizationCode();
                 if (StringUtils.isNotEmpty(chartCode) && StringUtils.isNotEmpty(organizationCode)) {
                     int counter = 0;
                     int organizationPos = 0;
                     for (VendorContractOrganization org : organizationCopy) {
                         if (chartCode.equalsIgnoreCase(org.getChartOfAccountsCode()) && organizationCode.equalsIgnoreCase(org.getOrganizationCode())) {
                             if (counter++ != 0) {
                                 organizationCopy.remove(organization);
                                 putFieldError(VendorPropertyConstants.VENDOR_CONTRACT + "[" + vendorPos + "]." +
                                         VendorPropertyConstants.VENDOR_CONTRACT_ORGANIZATION + "[" + organizationPos + "]." +
                                         VendorPropertyConstants.VENDOR_CUSTOMER_NUMBER_CHART_OF_ACCOUNTS_CODE,
                                         VendorKeyConstants.ERROR_DUPLICATE_ENTRY_NOT_ALLOWED,chartCode + " " + organizationCode );
                                 success = false;
                                 break;
                             }
                         }
                     }
                     organizationPos++;
                 }
                 vendorPos++;
            }
        }
        return success;
    }


    private boolean validateSearchAliases(VendorDetail vendorDetail) {
        boolean success = true;
        List<VendorAlias> searchAliases = vendorDetail.getVendorAliases();
        List<VendorAlias> aliasList = new ArrayList<VendorAlias>(searchAliases);
        int pos = 0;
        for (VendorAlias searchAlias : searchAliases) {
                String aliasName = searchAlias.getVendorAliasName();
               if (aliasName != null) {
                   int counter = 0;
                   for (VendorAlias alias : aliasList) {
                       if (aliasName.equals(alias.getVendorAliasName())) {
                           if (counter++ != 0) {
                               putFieldError(VendorPropertyConstants.VENDOR_SEARCH_ALIASES + "[" + pos + "]." + VendorPropertyConstants.VENDOR_ALIAS_NAME, VendorKeyConstants.ERROR_DUPLICATE_ENTRY_NOT_ALLOWED,aliasName);
                               aliasList.remove(searchAlias);
                               success = false;
                               break;
                           }
                       }
                   }
                }
               pos++;
        }
        return success;
    }

    /**
     * Validates that if the vendor is set to be inactive, the inactive reason is required.
     *
     * @param vendorDetail the VendorDetail object to be validated
     * @return boolean false if the vendor is inactive and the inactive reason is empty or if the vendor is active and the inactive reason is not empty
     */
    boolean validateInactiveReasonRequiredness(VendorDetail vendorDetail) {
        boolean activeIndicator = vendorDetail.isActiveIndicator();
        boolean emptyInactiveReason = StringUtils.isEmpty(vendorDetail.getVendorInactiveReasonCode());

        // return false if the vendor is inactive and the inactive reason is empty
        if (!activeIndicator && emptyInactiveReason) {
            putFieldError(VendorPropertyConstants.VENDOR_INACTIVE_REASON, VendorKeyConstants.ERROR_INACTIVE_REASON_REQUIRED);
            return false;
        }
        // return false if the vendor is active and the inactive reason is not empty
        if (activeIndicator && !emptyInactiveReason) {
            putFieldError(VendorPropertyConstants.VENDOR_INACTIVE_REASON, VendorKeyConstants.ERROR_INACTIVE_REASON_NOT_ALLOWED);
            return false;
        }
        return true;
    }

    /**
     * Validates that if the vendor is not foreign and if the vendor type's tax number required indicator is true, then the tax
     * number is required. If the vendor foreign indicator is true, then the tax number is not required regardless of its vendor
     * type.
     *
     * @param vendorDetail the VendorDetail object to be validated
     * @return boolean false if there is no tax number and the indicator is true.
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
     * Validates that, if the vendor is set to be restricted, the restricted reason is required.
     *
     * @param vendorDetail The VendorDetail object to be validated
     * @return boolean false if the vendor is restricted and the restricted reason is empty
     */
    boolean validateRestrictedReasonRequiredness(VendorDetail vendorDetail) {
        if (ObjectUtils.isNotNull(vendorDetail.getVendorRestrictedIndicator()) && vendorDetail.getVendorRestrictedIndicator() && StringUtils.isEmpty(vendorDetail.getVendorRestrictedReasonText())) {
            putFieldError(VendorPropertyConstants.VENDOR_RESTRICTED_REASON_TEXT, VendorKeyConstants.ERROR_RESTRICTED_REASON_REQUIRED);
            return false;
        }
        return true;
    }

    /**
     * Validates that if vendor is parent, then tax # and tax type combo should be unique by checking for the existence of vendor(s)
     * with the same tax # and tax type in the existing vendor header table. Ideally we're also supposed to check for pending
     * vendors, but at the moment, the pending vendors are under research investigation, so we're only checking the existing vendors
     * for now. If the vendor is a parent and the validation fails, display the actual error message. If the vendor is not a parent
     * and the validation fails, display the error message that the parent of this vendor needs to be changed, please contact
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
        criteria.put(VendorPropertyConstants.VENDOR_TAX_NUMBER, vendorDetail.getVendorHeader().getVendorTaxNumber());
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
     * Validates that the following business rules are satisfied: 1. Tax type cannot be blank if the tax # is not blank. 2. Tax type
     * cannot be set if the tax # is blank. If the vendor is a parent and the validation fails, we'll display an error message
     * indicating that the tax type cannot be blank if the tax # is not blank or that the tax type cannot be set if the tax # is
     * blank. If the vendor is not a parent and the validation fails, we'll display an error message indicating that the parent of
     * this vendor needs to be changed, please contact Purchasing Dept.
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
     * Validates the vendorName, vendorFirstName and vendorLastName fields according to these business rules: 1. At least one of the
     * three vendor name fields must be filled in. 2. Both of the two ways of entering vendor name (One vendor name field vs
     * VendorFirstName/VendorLastName) cannot be used 3. If either the vendor first name or the vendor last name have been entered,
     * the other must be entered.
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
            else {
                String vendorName = vendorDetail.getVendorLastName() + VendorConstants.NAME_DELIM + vendorDetail.getVendorFirstName();
                if (vendorName.length() > VendorConstants.MAX_VENDOR_NAME_LENGTH) {
                    putFieldError(VendorPropertyConstants.VENDOR_LAST_NAME, VendorKeyConstants.ERROR_VENDOR_NAME_TOO_LONG);
                    valid &= false;
                }

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
     * Validates the vendorSoldToNumber field to ensure that it's a valid existing vendor number;
     * and if so set vendorSoldToName accordingly.
     *
     * @param document - the maintenanceDocument being evaluated
     * @return boolean true if the vendorDetail in the document contains valid vendorSoldToNumber.
     */
    protected boolean validateVendorSoldToNumber(VendorDetail vendorDetail) {
        boolean valid = true;
        String vendorSoldToNumber = vendorDetail.getVendorSoldToNumber();

        // if vendor number is empty, clear all vendorSoldTo fields
        if (StringUtils.isEmpty(vendorSoldToNumber)) {
            vendorDetail.setSoldToVendorDetail(null);
            vendorDetail.setVendorSoldToGeneratedIdentifier(null);
            vendorDetail.setVendorSoldToAssignedIdentifier(null);
            vendorDetail.setVendorSoldToNumber(null);
            vendorDetail.setVendorSoldToName(null);
            return valid;
        }

        VendorDetail vendorSoldTo = SpringContext.getBean(VendorService.class).getVendorDetail(vendorSoldToNumber);
        if (vendorSoldTo != null) {
            // if vendor number is valid, set all vendorSoldTo fields
            vendorDetail.setSoldToVendorDetail(vendorSoldTo);
            vendorDetail.setVendorSoldToGeneratedIdentifier(vendorSoldTo.getVendorHeaderGeneratedIdentifier());
            vendorDetail.setVendorSoldToAssignedIdentifier(vendorSoldTo.getVendorDetailAssignedIdentifier());
            vendorDetail.setVendorSoldToName(vendorSoldTo.getVendorName());
        }
        else {
            // otherwise clear vendorSoldToName
            vendorDetail.setSoldToVendorDetail(null);
            vendorDetail.setVendorSoldToName(null);
            valid = false;
            putFieldError(VendorPropertyConstants.VENDOR_SOLD_TO_NUMBER, VendorKeyConstants.VENDOR_SOLD_TO_NUMBER_INVALID);
        }

        return valid;
    }

    /**
     * Validates the ownership type codes that aren't allowed for the tax type of the vendor. The rules are : 1. If tax type is
     * "SSN", then check the ownership type against the allowed types for "SSN" in the Rules table. 2. If tax type is "FEIN", then
     * check the ownership type against the allowed types for "FEIN" in the Rules table. If the vendor is a parent and the
     * validation fails, display the actual error message. If the vendor is not a parent and the validation fails, display the error
     * message that the parent of this vendor needs to be changed, please contact Purchasing Dept.
     *
     * @param vendorDetail The VendorDetail object to be validated
     * @return boolean true if the ownership type is allowed and FALSE otherwise.
     */
    private boolean validateOwnershipTypeAllowed(VendorDetail vendorDetail) {
        boolean valid = true;
        boolean isParent = vendorDetail.isVendorParentIndicator();
        String ownershipTypeCode = vendorDetail.getVendorHeader().getVendorOwnershipCode();
        String taxTypeCode = vendorDetail.getVendorHeader().getVendorTaxTypeCode();
        if (StringUtils.isNotEmpty(ownershipTypeCode) && StringUtils.isNotEmpty(taxTypeCode)) {
            if (VendorConstants.TAX_TYPE_FEIN.equals(taxTypeCode)) {
                if (!/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(VendorDetail.class, VendorParameterConstants.FEIN_ALLOWED_OWNERSHIP_TYPES, ownershipTypeCode).evaluationSucceeds()) {
                    valid &= false;
                }
            }
            else if (VendorConstants.TAX_TYPE_SSN.equals(taxTypeCode)) {
                if (!/*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(VendorDetail.class, VendorParameterConstants.SSN_ALLOWED_OWNERSHIP_TYPES, ownershipTypeCode).evaluationSucceeds()) {
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
     * Validates that the minimum order amount is less than the maximum allowed amount.
     *
     * @param vendorDetail The VendorDetail object to be validated
     * @return booelan true if the vendorMinimumOrderAmount is less than the maximum allowed amount.
     */
    private boolean validateMinimumOrderAmount(VendorDetail vendorDetail) {
        boolean valid = true;
        KualiDecimal minimumOrderAmount = vendorDetail.getVendorMinimumOrderAmount();
        if (ObjectUtils.isNotNull(minimumOrderAmount)) {
            KualiDecimal VENDOR_MIN_ORDER_AMOUNT = new KualiDecimal(SpringContext.getBean(ParameterService.class).getParameterValueAsString(VendorDetail.class, VendorParameterConstants.VENDOR_MIN_ORDER_AMOUNT));
            if (ObjectUtils.isNotNull(VENDOR_MIN_ORDER_AMOUNT) && (VENDOR_MIN_ORDER_AMOUNT.compareTo(minimumOrderAmount) < 1) || (minimumOrderAmount.isNegative())) {
                putFieldError(VendorPropertyConstants.VENDOR_MIN_ORDER_AMOUNT, VendorKeyConstants.ERROR_VENDOR_MAX_MIN_ORDER_AMOUNT, VENDOR_MIN_ORDER_AMOUNT.toString());
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * Validates that if the ownership category allowed indicator is false, the vendor does not have ownership category. It will
     * return false if the vendor contains ownership category. If the vendor is a parent and the validation fails, display the
     * actual error message. If the vendor is not a parent and the validation fails, display the error message that the parent of
     * this vendor needs to be changed, please contact Purchasing Dept.
     *
     * @param vendorDetail The VendorDetail to be validated
     * @return boolean true if the vendor does not contain ownership category and false otherwise
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
     * Calls the methods in TaxNumberService to validate the tax number for these business rules: 1. Tax number must be 9 digits and
     * cannot be all zeros (but can be blank). 2. First three digits of a SSN cannot be 000. 3. First three digits of a SSN cannot
     * be 666. 4. Middle two digits of a SSN cannot be 00. 5. Last four digits of a SSN cannot be 0000. 6. First two digits of a
     * FEIN cannot be 00. 7. Check system parameters for not allowed tax numbers
     *
     * @param vendorDetail The VendorDetail object to be validated
     * @return boolean true if the tax number is a valid tax number and false otherwise.
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

    /**
     * Validates commodity code related rules.
     *
     * @param document MaintenanceDocument
     * @return boolean false or true
     */
    boolean processCommodityCodeValidation(MaintenanceDocument document) {
        boolean valid = true;
        List<VendorCommodityCode> vendorCommodities = newVendor.getVendorCommodities();
        boolean commodityCodeRequired = newVendor.getVendorHeader().getVendorType().isCommodityRequiredIndicator();
        if (commodityCodeRequired) {
            if (vendorCommodities.size() == 0) {
                //display error that the commodity code is required for this type of vendor.
                String propertyName = "add." + VendorPropertyConstants.VENDOR_COMMODITIES_CODE_PURCHASING_COMMODITY_CODE;
                putFieldError(propertyName, VendorKeyConstants.ERROR_VENDOR_COMMODITY_CODE_IS_REQUIRED_FOR_THIS_VENDOR_TYPE);
                valid = false;
            }
            //We only need to validate the default indicator if there is at least
            //one commodity code for the vendor.
            else if (vendorCommodities.size() > 0) {
                valid &= validateCommodityCodeDefaultIndicator(vendorCommodities);
            }
        }
        else if (vendorCommodities.size() > 0) {
            //If the commodity code is not required, but the vendor contains at least one commodity code,
            //we have to check that there is only one commodity code with default indicator = Y.
            int defaultCount = 0;
            for (int i=0; i < vendorCommodities.size(); i++) {
                VendorCommodityCode vcc = vendorCommodities.get(i);
                if (vcc.isCommodityDefaultIndicator()) {
                    defaultCount ++;
                    if (defaultCount > 1) {
                        valid = false;
                        String propertyName = VendorPropertyConstants.VENDOR_COMMODITIES_CODE + "[" + i + "]." + VendorPropertyConstants.VENDOR_COMMODITIES_DEFAULT_INDICATOR;
                        putFieldError(propertyName, VendorKeyConstants.ERROR_VENDOR_COMMODITY_CODE_REQUIRE_ONE_DEFAULT_IND);
                        break;
                    }
                }
            }
        }

        return valid;
    }

    /**
     * Validates that there is one and only one default indicator selected
     * for commodity code if the vendor contains at least one commodity code.
     *
     * @param vendorCommodities the list of VendorCommodityCode to be validated
     * @return boolean true or false
     */
    private boolean validateCommodityCodeDefaultIndicator(List<VendorCommodityCode> vendorCommodities) {
        boolean valid = true;

        boolean foundDefaultIndicator = false;
        for (int i=0; i < vendorCommodities.size(); i++) {
            VendorCommodityCode vcc = vendorCommodities.get(i);
            if (vcc.isCommodityDefaultIndicator()) {
                if (!foundDefaultIndicator) {
                    foundDefaultIndicator = true;
                }
                else {
                    //display error that there can only be 1 commodity code with default indicator = true.
                    String propertyName = VendorPropertyConstants.VENDOR_COMMODITIES_CODE + "[" + i + "]." + VendorPropertyConstants.VENDOR_COMMODITIES_DEFAULT_INDICATOR;
                    putFieldError(propertyName, VendorKeyConstants.ERROR_VENDOR_COMMODITY_CODE_REQUIRE_ONE_DEFAULT_IND);
                    valid = false;
                }
            }
        }
        if (!foundDefaultIndicator && vendorCommodities.size() > 0) {
            //display error that there must be one commodity code selected as the default commodity code for the vendor.
            String propertyName = VendorPropertyConstants.VENDOR_COMMODITIES_CODE + "[0]." + VendorPropertyConstants.VENDOR_COMMODITIES_DEFAULT_INDICATOR;
            putFieldError(propertyName, VendorKeyConstants.ERROR_VENDOR_COMMODITY_CODE_REQUIRE_ONE_DEFAULT_IND);
            valid = false;
        }
        return valid;
    }

    /**
     * Validates vendor address fields.
     *
     * @param document MaintenanceDocument
     * @return boolean false or true
     */
    boolean processAddressValidation(MaintenanceDocument document) {
        boolean valid = true;
        boolean validAddressType = false;

        List<VendorAddress> addresses = newVendor.getVendorAddresses();
        String vendorTypeCode = newVendor.getVendorHeader().getVendorTypeCode();
        String vendorAddressTypeRequiredCode = newVendor.getVendorHeader().getVendorType().getVendorAddressTypeRequiredCode();

        for (int i = 0; i < addresses.size(); i++) {
            VendorAddress address = addresses.get(i);
            String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_ADDRESS + "[" + i + "]";
            GlobalVariables.getMessageMap().clearErrorPath();
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);

            this.getDictionaryValidationService().validateBusinessObject(address);
            if (GlobalVariables.getMessageMap().hasErrors()) {
                valid = false;
            }

            if (address.getVendorAddressTypeCode().equals(vendorAddressTypeRequiredCode)) {
                validAddressType = true;
            }

            valid &= checkAddressCountryEmptyStateZip(address);

            GlobalVariables.getMessageMap().clearErrorPath();
        }

        // validate Address Type
        String vendorAddressTabPrefix = KFSConstants.ADD_PREFIX + "." + VendorPropertyConstants.VENDOR_ADDRESS + ".";
        if (!StringUtils.isBlank(vendorTypeCode) && !StringUtils.isBlank(vendorAddressTypeRequiredCode) && !validAddressType) {
            String[] parameters = new String[] { vendorTypeCode, vendorAddressTypeRequiredCode };
            putFieldError(vendorAddressTabPrefix + VendorPropertyConstants.VENDOR_ADDRESS_TYPE_CODE, VendorKeyConstants.ERROR_ADDRESS_TYPE, parameters);
            String addressLine1Label = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(VendorAddress.class, VendorPropertyConstants.VENDOR_ADDRESS_LINE_1);
            String addressCityLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(VendorAddress.class, VendorPropertyConstants.VENDOR_ADDRESS_CITY);
            String addressCountryLabel = SpringContext.getBean(DataDictionaryService.class).getAttributeLabel(VendorAddress.class, VendorPropertyConstants.VENDOR_ADDRESS_COUNTRY);
            putFieldError(vendorAddressTabPrefix + VendorPropertyConstants.VENDOR_ADDRESS_LINE_1, KFSKeyConstants.ERROR_REQUIRED, addressLine1Label);
            putFieldError(vendorAddressTabPrefix + VendorPropertyConstants.VENDOR_ADDRESS_CITY, KFSKeyConstants.ERROR_REQUIRED, addressCityLabel);
            putFieldError(vendorAddressTabPrefix + VendorPropertyConstants.VENDOR_ADDRESS_COUNTRY, KFSKeyConstants.ERROR_REQUIRED, addressCountryLabel);
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
            // We need to exclude the first one Since we already checked for this in valid AddressType above.
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

                    //divisions without the desired address type should only be an warning
                    GlobalVariables.getMessageMap().putWarningWithoutFullErrorPath(MAINTAINABLE_ERROR_PREFIX + vendorAddressTabPrefix + VendorPropertyConstants.VENDOR_ADDRESS_TYPE_CODE, VendorKeyConstants.ERROR_ADDRESS_TYPE_DIVISIONS, parameters);
                }
            }
        }

        return valid;
    }

    /**
     * Validates that if US is selected for the country then the state and zip cannot be empty. Also,
     * zip format validation is added if US is selected.
     *
     * @param addresses VendorAddress which is being validated
     * @return boolean false if the country is United States and there is no state or zip code
     */
    boolean checkAddressCountryEmptyStateZip(VendorAddress address) {
        //GlobalVariables.getMessageMap().clearErrorPath();
        //GlobalVariables.getMessageMap().addToErrorPath(KFSPropertyConstants.DOCUMENT + "." + KFSPropertyConstants.NEW_MAINTAINABLE_OBJECT + "." + VendorPropertyConstants.VENDOR_ADDRESS);
        boolean valid = SpringContext.getBean(PostalCodeValidationService.class).validateAddress(address.getVendorCountryCode(), address.getVendorStateCode(), address.getVendorZipCode(), VendorPropertyConstants.VENDOR_ADDRESS_STATE, VendorPropertyConstants.VENDOR_ADDRESS_ZIP);
        //GlobalVariables.getMessageMap().clearErrorPath();
		return valid;
    }

    /**
     * Checks if the "allow default indicator" is true or false for this address.
     *
     * @param addresses VendorAddress which is being validated
     * @return boolean false or true
     */

    boolean findAllowDefaultAddressIndicatorHelper(VendorAddress vendorAddress) {

        AddressType addressType = new AddressType();

        addressType = vendorAddress.getVendorAddressType();
        if (ObjectUtils.isNull(addressType)) {
            return false;
        }
        // Retrieving the Default Address Indicator for this Address Type:
        return addressType.getVendorDefaultIndicator();

    }

    /**
     * If add button is selected on Default Address, checks if the allow default indicator is set to false for this address type
     * then it does not allow user to select a default address for this address and if it is true then it allows only one campus to
     * be default for this address.
     *
     * @param vendorDetail VendorDetail document
     * @param addedDefaultAddress VendorDefaultAddress which is being added
     * @param parent The VendorAddress which we are adding a default address to it
     * @return boolean false or true
     */
    boolean checkDefaultAddressCampus(VendorDetail vendorDetail, VendorDefaultAddress addedDefaultAddress, VendorAddress parent) {
        VendorAddress vendorAddress = parent;
        if (ObjectUtils.isNull(vendorAddress)) {
            return false;
        }

        int j = vendorDetail.getVendorAddresses().indexOf(vendorAddress);
        String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_ADDRESS + "[" + j + "]";
        GlobalVariables.getMessageMap().addToErrorPath(errorPath);

        // Retrieving the Default Address Indicator for this Address Type:
        boolean allowDefaultAddressIndicator = findAllowDefaultAddressIndicatorHelper(vendorAddress);
        String addedAddressCampusCode = addedDefaultAddress.getVendorCampusCode();
        String addedAddressTypeCode = vendorAddress.getVendorAddressTypeCode();

        // if the selected address type does not allow defaults, then the user should not be allowed to
        // select the default indicator or add any campuses to the address
        if (allowDefaultAddressIndicator == false) {
            String[] parameters = new String[] { addedAddressTypeCode };
            GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS + "[" + 0 + "]." + VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_CAMPUS, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_CAMPUS_NOT_ALLOWED, parameters);
            return false;
        }

        List<VendorDefaultAddress> vendorDefaultAddresses = vendorAddress.getVendorDefaultAddresses();
        for (int i = 0; i < vendorDefaultAddresses.size(); i++) {
            VendorDefaultAddress vendorDefaultAddress = vendorDefaultAddresses.get(i);
            if (vendorDefaultAddress.getVendorCampusCode().equalsIgnoreCase(addedAddressCampusCode)) {
                GlobalVariables.getMessageMap().clearErrorPath();
                GlobalVariables.getMessageMap().addToErrorPath(errorPath);
                String[] parameters = new String[] { addedAddressCampusCode, addedAddressTypeCode };
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS + "[" + i + "]." + VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_CAMPUS, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_CAMPUS, parameters);
                return false;
            }
        }

        return true;
    }

    /**
     * Checks if the allow default indicator is set to false for this address the default indicator cannot be set to true/yes. If
     * "allow default indicator" is set to true/yes for address type, one address must have the default indicator set (no more, no
     * less) and only one campus to be set as default for this address.
     *
     * @param vendorDetail VendorDetail document
     * @return boolean false or true
     */

    boolean validateDefaultAddressCampus(VendorDetail vendorDetail) {
        List<VendorAddress> vendorAddresses = vendorDetail.getVendorAddresses();
        String addressTypeCode;
        String addressTypeDesc;
        String campusCode;
        boolean valid = true;
        boolean previousValue = false;

        // This is a HashMap to store the default Address Type Codes and their associated default Indicator
        HashMap addressTypeCodeDefaultIndicator = new HashMap();

        // This is a HashMap to store Address Type Codes and Address Campus Codes for Default Addresses
        HashMap addressTypeDefaultCampus = new HashMap();

        // This is a HashSet for storing only the Address Type Codes which have at least one default Indicator set to true
        HashSet addressTypesHavingDefaultTrue = new HashSet();

        int i = 0;
        for (VendorAddress address : vendorAddresses) {
            addressTypeCode = address.getVendorAddressTypeCode();
            addressTypeDesc = address.getVendorAddressType().getVendorAddressTypeDescription();
            String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_ADDRESS + "[" + i + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
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
                    GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_INDICATOR, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_INDICATOR,addressTypeDesc );
                    valid = false;
                }

            }
            // If "allow default indicator" is set to false/no for address type, the default indicator cannot be set to true/yes.
            else {
                if (address.isVendorDefaultAddressIndicator()) {
                    GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_INDICATOR, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_ADDRESS_NOT_ALLOWED, parameters);
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
                    GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS + "[" + j + "]." + VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_CAMPUS, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_CAMPUS, newParameters);
                    valid = false;
                }
                j++;
            }
            i++;
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
        }

        // If "allow default indicator" is set to true/yes for address type, one address must have the default indicator set to true
        if (!addressTypeCodeDefaultIndicator.isEmpty()) {
            Set<String> addressTypes = addressTypeCodeDefaultIndicator.keySet();

            for (String addressType : addressTypes) {
                if (!addressTypesHavingDefaultTrue.contains(addressType)) {

                    int addressIndex = 0;
                    for (VendorAddress address : vendorAddresses) {
                        String[] parameters = new String[] { address.getVendorAddressType().getVendorAddressTypeDescription() };
                        String propertyName = VendorPropertyConstants.VENDOR_ADDRESS + "[" + addressIndex + "]." + VendorPropertyConstants.VENDOR_DEFAULT_ADDRESS_INDICATOR;
                        if (address.getVendorAddressType().getVendorAddressTypeCode().equalsIgnoreCase(addressType)) {
                            putFieldError(propertyName, VendorKeyConstants.ERROR_ADDRESS_DEFAULT_INDICATOR, parameters);
                            break;
                        }
                        addressIndex++;
                    }
                    valid = false;
                }

            }
        }

        return valid;
    }


    /**
     * A stub method as placeholder for future Contact Validation
     *
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
    private boolean processContactValidation(MaintenanceDocument document) {
        boolean valid = true;
        int i = 0;
        for (VendorContact contact : newVendor.getVendorContacts()) {
            String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_CONTACT + "[" + i + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);

            this.getDictionaryValidationService().validateBusinessObject(contact);
            Map<String, AutoPopulatingList<ErrorMessage>> errors = GlobalVariables.getMessageMap().getErrorMessages();
            if ((errors != null ) && (!errors.isEmpty())) {
                valid = false;
            }
            i++;
            GlobalVariables.getMessageMap().clearErrorPath();
        }
        return valid;
    }

    /**
     * Validates vendor customer numbers
     *
     * @param document MaintenanceDocument instance
     * @return boolean false or true
     */
    private boolean processCustomerNumberValidation(MaintenanceDocument document) {
        boolean valid = true;

        List<VendorCustomerNumber> customerNumbers = newVendor.getVendorCustomerNumbers();
        for (VendorCustomerNumber customerNumber : customerNumbers) {
            valid &= validateVendorCustomerNumber(customerNumber);
        }
        return valid;
    }

    /**
     * Validates vendor customer number. The chart and org must exist in the database.
     *
     * @param customerNumber VendorCustomerNumber
     * @return boolean false or true
     */
    boolean validateVendorCustomerNumber(VendorCustomerNumber customerNumber) {
        boolean valid = true;

        // The chart and org must exist in the database.
        String chartOfAccountsCode = customerNumber.getChartOfAccountsCode();
        String orgCode = customerNumber.getVendorOrganizationCode();
        if (!StringUtils.isBlank(chartOfAccountsCode) && !StringUtils.isBlank(orgCode)) {
            Map chartOrgMap = new HashMap();
            chartOrgMap.put("chartOfAccountsCode", chartOfAccountsCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Chart.class, chartOrgMap) < 1) {
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CUSTOMER_NUMBER_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_EXISTENCE, chartOfAccountsCode);
                valid &= false;
            }
            chartOrgMap.put("organizationCode", orgCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Organization.class, chartOrgMap) < 1) {
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CUSTOMER_NUMBER_ORGANIZATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, orgCode);
                valid &= false;
            }
        }
        return valid;
    }

    /**
     * Validates vendor contract. If the vendorContractAllowedIndicator is false, it cannot have vendor contracts, then return false
     *
     * @param document MaintenanceDocument
     * @return boolean false or true
     */
    private boolean processContractValidation(MaintenanceDocument document) {
        boolean valid = true;
        List<VendorContract> contracts = newVendor.getVendorContracts();
        if (ObjectUtils.isNull(contracts)) {
            return valid;
        }

        // If the vendorContractAllowedIndicator is false, it cannot have vendor contracts, return false;
        if (contracts.size() > 0 && !newVendor.getVendorHeader().getVendorType().isVendorContractAllowedIndicator()) {
            valid = false;
            String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_CONTRACT + "[0]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
            GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_NAME, VendorKeyConstants.ERROR_VENDOR_CONTRACT_NOT_ALLOWED);
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
            return valid;
        }

        for (int i = 0; i < contracts.size(); i++) {
            VendorContract contract = contracts.get(i);

            String errorPath = MAINTAINABLE_ERROR_PREFIX + VendorPropertyConstants.VENDOR_CONTRACT + "[" + i + "]";
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);

            valid &= validateVendorContractPOLimitAndExcludeFlagCombination(contract);
            valid &= validateVendorContractBeginEndDates(contract);
            valid &= processContractB2BValidation(document, contract, i);
            if (contract.getOrganizationAutomaticPurchaseOrderLimit() != null) {
                org.kuali.rice.krad.datadictionary.BusinessObjectEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(VendorContract.class.getName());
                AttributeDefinition attributeDefinition = entry.getAttributeDefinition(VendorPropertyConstants.VENDOR_CONTRACT_DEFAULT_APO_LIMIT);
                valid &= validateAPOAmount(contract.getOrganizationAutomaticPurchaseOrderLimit(), attributeDefinition);
            }
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
        }


        return valid;
    }

    /**
     * Validates that the APO amount is a valid amount according to
     * the FixedPointValidationPattern (i.e. non negative number with the precision and scale
     * as defined in the data dictionary).
     *
     * @param apoAmount
     * @param attributeDefinition
     * @return
     */
    boolean validateAPOAmount(KualiDecimal apoAmount, AttributeDefinition attributeDefinition) {
        boolean valid = true;

        if (ObjectUtils.isNotNull(attributeDefinition)) {
            final ValidationPattern validationPattern = attributeDefinition.getValidationPattern();

            if (ObjectUtils.isNotNull(validationPattern) && validationPattern instanceof FixedPointValidationPattern) {
                FixedPointValidationPattern fixedPointPattern = (FixedPointValidationPattern) validationPattern;
                if (!fixedPointPattern.matches(apoAmount.toString())) {
                    valid &= false;
                    String scale = Integer.toString(fixedPointPattern.getScale());
                    String precision = Integer.toString(fixedPointPattern.getPrecision());
                    GlobalVariables.getMessageMap().putError(attributeDefinition.getName(), attributeDefinition.getValidationPattern().getValidationErrorMessageKey(), attributeDefinition.getLabel(), precision, scale);
                }
            }
        }

        return valid;

    }

    /**
     * Validates that the proper combination of Exclude Indicator and APO Amount is present on a vendor contract. Do not perform
     * this validation on Contract add line as the user cannot currently enter the sub-collection of contract-orgs so we should not
     * force this until the document is submitted. The rules are : 1. Must enter a Default APO Limit or at least one organization
     * with an APO Amount. 2. If the Exclude Indicator for an organization is N, an organization APO Amount is required. 3. If the
     * Exclude Indicator for an organization is Y, the organization APO Amount is not allowed.
     *
     * @param contract VendorContract
     * @return boolean true if the proper combination of Exclude Indicator and APO Amount is present, otherwise flase.
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
                valid &= validateVendorContractOrganization(organization, organizationCounter);
                organizationCounter++;
            }
        }
        if (NoOrgHasApoLimit && ObjectUtils.isNull(contract.getOrganizationAutomaticPurchaseOrderLimit())) {
            // Rule #1 in the above java doc has been violated.
            GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_DEFAULT_APO_LIMIT, VendorKeyConstants.ERROR_VENDOR_CONTRACT_NO_APO_LIMIT);
            valid &= false;
        }
        return valid;
    }

    /**
     * Validates that: 1. If the VendorContractBeginningDate is entered then the VendorContractEndDate is also entered, and vice
     * versa. 2. If both dates are entered, the VendorContractBeginningDate is before the VendorContractEndDate. The date fields are
     * required so we should know that we have valid dates.
     *
     * @param contract VendorContract
     * @return boolean true if the beginning date is before the end date, false if only one date is entered or the beginning date is
     *         after the end date.
     */
    boolean validateVendorContractBeginEndDates(VendorContract contract) {
        boolean valid = true;

        if (ObjectUtils.isNotNull(contract.getVendorContractBeginningDate()) && ObjectUtils.isNull(contract.getVendorContractEndDate())) {
            GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_END_DATE, VendorKeyConstants.ERROR_VENDOR_CONTRACT_BEGIN_DATE_NO_END_DATE);
            valid &= false;
        }
        else {
            if (ObjectUtils.isNull(contract.getVendorContractBeginningDate()) && ObjectUtils.isNotNull(contract.getVendorContractEndDate())) {
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_BEGIN_DATE, VendorKeyConstants.ERROR_VENDOR_CONTRACT_END_DATE_NO_BEGIN_DATE);
                valid &= false;
            }
        }
        if (valid && ObjectUtils.isNotNull(contract.getVendorContractBeginningDate()) && ObjectUtils.isNotNull(contract.getVendorContractEndDate())) {
            if (contract.getVendorContractBeginningDate().after(contract.getVendorContractEndDate())) {
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_BEGIN_DATE, VendorKeyConstants.ERROR_VENDOR_CONTRACT_BEGIN_DATE_AFTER_END);
                valid &= false;
            }
        }

        return valid;
    }

    /**
     * Validates a vendor contract organization. The rules are : 1. If the Exclude Indicator for the organization is N, an
     * organization APO Amount is required. 2. If the Exclude Indicator for the organization is Y, an organization APO Amount is not
     * allowed. 3. The chart and org for the organization must exist in the database.
     *
     * @param organization VendorContractOrganization
     * @return boolean true if these three rules are passed, otherwise false.
     */
    boolean validateVendorContractOrganization(VendorContractOrganization organization, int counter) {
        boolean valid = true;
        List<String> previousErrorPaths = GlobalVariables.getMessageMap().getErrorPath();
        String errorPath = VendorPropertyConstants.VENDOR_CONTRACT_ORGANIZATION + "[" + counter + "]";
        boolean shouldAddToErrorPath = true;
        // if the error path already contained something like "add." then we don't need to add anything to the error path anymore.
        for (String previous : previousErrorPaths) {
            if (previous.startsWith(KRADConstants.ADD_PREFIX)) {
                shouldAddToErrorPath = false;
                break;
            }
        }
        if (shouldAddToErrorPath) {
            GlobalVariables.getMessageMap().addToErrorPath(errorPath);
        }

        boolean isExcluded = organization.isVendorContractExcludeIndicator();
        if (isExcluded) {
            if (ObjectUtils.isNotNull(organization.getVendorContractPurchaseOrderLimitAmount())) {
                // Rule #2 in the above java doc has been violated.
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_ORGANIZATION_APO_LIMIT, VendorKeyConstants.ERROR_VENDOR_CONTRACT_ORG_EXCLUDED_WITH_APO_LIMIT);
                valid &= false;
            }
        }
        else { // isExcluded = false
            if (ObjectUtils.isNull(organization.getVendorContractPurchaseOrderLimitAmount())) {
                // Rule #1 in the above java doc has been violated.
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_ORGANIZATION_APO_LIMIT, VendorKeyConstants.ERROR_VENDOR_CONTRACT_ORG_NOT_EXCLUDED_NO_APO_LIMIT);
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
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_CHART_OF_ACCOUNTS_CODE, KFSKeyConstants.ERROR_EXISTENCE, chartOfAccountsCode);
                valid &= false;
            }
            chartOrgMap.put("organizationCode", orgCode);
            if (SpringContext.getBean(BusinessObjectService.class).countMatching(Organization.class, chartOrgMap) < 1) {
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_ORGANIZATION_CODE, KFSKeyConstants.ERROR_EXISTENCE, orgCode);
                valid &= false;
            }
        }

        if (shouldAddToErrorPath && organization.getVendorContractPurchaseOrderLimitAmount() != null) {
            org.kuali.rice.krad.datadictionary.BusinessObjectEntry entry = SpringContext.getBean(DataDictionaryService.class).getDataDictionary().getBusinessObjectEntry(VendorContractOrganization.class.getName());
            AttributeDefinition attributeDefinition = entry.getAttributeDefinition(VendorPropertyConstants.VENDOR_CONTRACT_ORGANIZATION_APO_LIMIT);
            valid &= validateAPOAmount(organization.getVendorContractPurchaseOrderLimitAmount(), attributeDefinition);
        }

        if (shouldAddToErrorPath) {
            GlobalVariables.getMessageMap().removeFromErrorPath(errorPath);
        }

        return valid;
    }

    /**
     * Validates vendor contracts against single B2B restriction on a vendor/campus basis. Only one B2B contract allowed per vendor/campus
     *
     * @param document MaintenanceDocument
     * @return boolean false or true
     */
    private boolean processContractB2BValidation(MaintenanceDocument document, VendorContract contract, int contractPos) {
        boolean valid = true;
        List<Integer> indexOfB2BContracts = new ArrayList();
        //list of contracts already associated with vendor
        List<VendorContract> contracts = newVendor.getVendorContracts();
        if (ObjectUtils.isNull(contracts)) {
            return valid;
        }
        //find all b2b contracts for comparison
        if(contractPos == -1){
            if(contract.getVendorB2bIndicator()){
                for (int i = 0; i < contracts.size(); i++) {
                    VendorContract vndrContract = contracts.get(i);
                    if(vndrContract.getVendorB2bIndicator()){
                        //check for duplicate campus; vendor is implicitly the same
                        if(contract.getVendorCampusCode().equals(vndrContract.getVendorCampusCode())){
                            valid &= false;
                            GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_B2B_INDICATOR, VendorKeyConstants.ERROR_VENDOR_CONTRACT_B2B_LIMIT_EXCEEDED, contract.getVendorCampusCode());
                        }
                    }
                }
            }
        } else
        {
            if(contract.getVendorB2bIndicator()){
                for (int i = 0; i < contracts.size(); i++) {
                    VendorContract vndrContract = contracts.get(i);
                    if(vndrContract.getVendorB2bIndicator()){
                        //make sure we're not checking contracts against themselves
                        if(i != contractPos){
                            //check for duplicate campus; vendor is implicitly the same
                            if(contract.getVendorCampusCode().equals(vndrContract.getVendorCampusCode())){
                                valid &= false;
                                String [] errorArray = new String []{contract.getVendorContractName(), contract.getVendorCampusCode()};
                                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.VENDOR_CONTRACT_B2B_INDICATOR, VendorKeyConstants.ERROR_VENDOR_CONTRACT_B2B_LIMIT_EXCEEDED_DB, errorArray);
                            }
                        }
                    }
                }
            }
        }
       return valid;
    }

    /**
     * Validates business rules for VendorDetail document collection add lines. Add lines are the initial lines on a collections,
     * i.e. the ones next to the "Add" button
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processCustomAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        boolean success = true;

        // this incoming bo needs to be refreshed because it doesn't have its subobjects setup
        bo.refreshNonUpdateableReferences();

        if (bo instanceof VendorAddress) {
            VendorAddress address = (VendorAddress) bo;
            success &= checkAddressCountryEmptyStateZip(address);
            VendorDetail vendorDetail = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();
        }
        if (bo instanceof VendorContract) {
            VendorContract contract = (VendorContract) bo;
            success &= validateVendorContractBeginEndDates(contract);
            success &= processContractB2BValidation(document,contract, -1);
        }
        if (bo instanceof VendorContractOrganization) {
            VendorContractOrganization contractOrg = (VendorContractOrganization) bo;
            success &= validateVendorContractOrganization(contractOrg, 0);
        }
        if (bo instanceof VendorCustomerNumber) {
            VendorCustomerNumber customerNumber = (VendorCustomerNumber) bo;
            success &= validateVendorCustomerNumber(customerNumber);
        }
        if (bo instanceof VendorDefaultAddress) {
            VendorDefaultAddress defaultAddress = (VendorDefaultAddress) bo;
            String parentName = StringUtils.substringBeforeLast(collectionName, ".");
            VendorAddress parent = (VendorAddress) ObjectUtils.getPropertyValue(this.getNewBo(), parentName);
            VendorDetail vendorDetail = (VendorDetail) document.getNewMaintainableObject().getBusinessObject();
            success &= checkDefaultAddressCampus(vendorDetail, defaultAddress, parent);
        }

        if (bo instanceof VendorCommodityCode) {
            VendorCommodityCode commodityCode = (VendorCommodityCode) bo;
            String purchasingCommodityCode = commodityCode.getPurchasingCommodityCode();
            boolean found = ObjectUtils.isNotNull(commodityCode) && StringUtils.isNotBlank(purchasingCommodityCode) && checkVendorCommodityCode(commodityCode);

            if (!found) {
                GlobalVariables.getMessageMap().putError(VendorPropertyConstants.PURCHASING_COMMODITY_CODE, KFSKeyConstants.ERROR_EXISTENCE, purchasingCommodityCode);
            }

            success &= found;
        }

        return success;
    }

    /**
     * Validates the rule that if a document has a federal witholding tax begin date and end date, the begin date should come before
     * the end date.
     *
     * @param vdDocument VendorDetail
     * @return boolean false or true
     */
    private boolean validateVendorWithholdingTaxDates(VendorDetail vdDocument) {
        boolean valid = true;
        DateTimeService dateTimeService = SpringContext.getBean(DateTimeService.class);

        Date beginDate = vdDocument.getVendorHeader().getVendorFederalWithholdingTaxBeginningDate();
        Date endDate = vdDocument.getVendorHeader().getVendorFederalWithholdingTaxEndDate();
        if (ObjectUtils.isNotNull(beginDate) && ObjectUtils.isNotNull(endDate)) {
            if (dateTimeService.dateDiff(beginDate, endDate, false) <= 0) {
                putFieldError(VendorPropertyConstants.VENDOR_FEDERAL_WITHOLDING_TAX_BEGINNING_DATE, VendorKeyConstants.ERROR_VENDOR_TAX_BEGIN_DATE_AFTER_END);
                valid &= false;
            }
        }
        return valid;

    }

    /**
     * Validates the rule that both w9 received and w-8ben cannot be set to yes
     *
     * @param vdDocument VendorDetail
     * @return boolean false or true
     */
    private boolean validateVendorW8BenOrW9ReceivedIndicator(VendorDetail vdDocument) {
        boolean valid = true;

        if (ObjectUtils.isNotNull(vdDocument.getVendorHeader().getVendorW9ReceivedIndicator()) && ObjectUtils.isNotNull(vdDocument.getVendorHeader().getVendorW8BenReceivedIndicator()) && vdDocument.getVendorHeader().getVendorW9ReceivedIndicator() && vdDocument.getVendorHeader().getVendorW8BenReceivedIndicator()) {
            putFieldError(VendorPropertyConstants.VENDOR_W9_RECEIVED_INDICATOR, VendorKeyConstants.ERROR_VENDOR_W9_AND_W8_RECEIVED_INDICATOR_BOTH_TRUE);
            valid &= false;
        }
        return valid;

    }

    /**
     * Overrides the method in MaintenanceDocumentRuleBase to give error message to the user when
     * the user tries to add a vendor contract when the vendor type of the vendor does not allow
     * contract.
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument, java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject bo) {
        if (collectionName.equals(VendorPropertyConstants.VENDOR_CONTRACT)) {
            VendorDetail vendorDetail = (VendorDetail)document.getDocumentBusinessObject();
            vendorDetail.getVendorHeader().refreshReferenceObject("vendorType");
            VendorType vendorType = vendorDetail.getVendorHeader().getVendorType();
            if (!vendorType.isVendorContractAllowedIndicator()) {
                String propertyName = "add." + collectionName + "." + VendorPropertyConstants.VENDOR_CONTRACT_NAME;
                putFieldError(propertyName, VendorKeyConstants.ERROR_VENDOR_CONTRACT_NOT_ALLOWED);
                return false;
            }
        }
        return super.processAddCollectionLineBusinessRules(document, collectionName, bo);
    }

    protected boolean checkVendorCommodityCode(VendorCommodityCode commodityCode) {
        String purchasingCommodityCode = commodityCode.getPurchasingCommodityCode();

        CommodityCodeService commodityCodeService = SpringContext.getBean(CommodityCodeService.class);

        return ObjectUtils.isNotNull(commodityCodeService.getByPrimaryId(purchasingCommodityCode));
    }
}
