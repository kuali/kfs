/*
 * Copyright 2006 The Kuali Foundation
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
package org.kuali.kfs.module.cg.document.validation.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.cg.ContractsAndGrantsModuleRetrieveService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGKeyConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.businessobject.AgencyType;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.businessobject.AwardFundManager;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Rules for processing Agency instances.
 */
public class AgencyRule extends CGMaintenanceDocumentRuleBase {
    protected static Logger LOG = org.apache.log4j.Logger.getLogger(AgencyRule.class);

    protected Agency newAgency;
    protected Agency oldAgency;
    protected boolean contractsGrantsBillingEnhancementsInd;

    BusinessObjectService businessObjectService;

    /**
     * Default constructor.
     */
    public AgencyRule() {
        super();
        businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        contractsGrantsBillingEnhancementsInd = SpringContext.getBean(ContractsAndGrantsModuleRetrieveService.class).isContractsGrantsBillingEnhancementsActive();
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomApproveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomApproveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("Entering AgencyRule.processCustomApproveDocumentBusinessRules");
        boolean success = super.processCustomApproveDocumentBusinessRules(document);

        success &= checkAgencyReportsTo(document);

        LOG.info("Leaving AgencyRule.processCustomApproveDocumentBusinessRules");
        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomRouteDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("Entering AgencyRule.processCustomRouteDocumentBusinessRules");
        boolean success = super.processCustomRouteDocumentBusinessRules(document);

        success &= checkAgencyReportsTo(document);

        // Only do further custom Contracts and Grants Billing validations for route document, if the enhancements are active
        if (contractsGrantsBillingEnhancementsInd) {
            // There must be at least one primary Agency Address in Agency
            success &= checkPrimary(newAgency.getAgencyAddresses(), AgencyAddress.class, KFSPropertyConstants.AGENCY_ADDRESSES, Agency.class);


            // Guide the user further
            if (!success) {
                GlobalVariables.getMessageMap().putInfo(MAINTAINABLE_ERROR_PREFIX + KFSPropertyConstants.AGENCY_ADDRESSES, CGKeyConstants.AgencyConstants.MESSAGE_SELECT_PRIMARY_AGENCY_ADDRESS_TYPE);
            }

            success &= validateAddresses(newAgency);

            // Make sure new agency customers have a Customer Type
            success &= validateCustomerType(newAgency);
        }

        LOG.info("Leaving AgencyRule.processCustomRouteDocumentBusinessRules");
        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processCustomSaveDocumentBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("Entering AgencyRule.processCustomSaveDocumentBusinessRules");
        boolean success = super.processCustomSaveDocumentBusinessRules(document);

        success &= checkAgencyReportsTo(document);
        success &= validateAgencyType(document);
        success &= validateAgencyReportingName(document);

        LOG.info("Leaving AgencyRule.processCustomSaveDocumentBusinessRules");
        return success;
    }

    /**
     * @param document
     * @return
     */
    protected boolean validateAgencyType(MaintenanceDocument document) {
        String agencyType = newAgency.getAgencyTypeCode();
        Map params = new HashMap();
        params.put("code", agencyType);
        Object o = businessObjectService.findByPrimaryKey(AgencyType.class, params);
        if (null == o) {
            putFieldError("agencyTypeCode", KFSKeyConstants.ERROR_AGENCY_TYPE_NOT_FOUND, agencyType);
            return false;
        }
        return true;
    }

    /**
     * @param document
     * @return
     */
    protected boolean validateAgencyReportingName(MaintenanceDocument document) {
        String agencyReportingName = newAgency.getReportingName();
        String agencyExistsValue = newAgency.getCustomerCreated();
        if (CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE.equalsIgnoreCase(agencyExistsValue)) {
            if (agencyReportingName.length() < 3) {
                putFieldError("reportingName", CGKeyConstants.AgencyConstants.ERROR_AGENCY_NAME_LESS_THAN_THREE_CHARACTERS);
                return false;
            }
            else if (agencyReportingName.substring(0, 3).contains(" ")) {
                putFieldError("reportingName", CGKeyConstants.AgencyConstants.ERROR_AGENCY_NAME_NO_SPACES_IN_FIRST_THREE_CHARACTERS);
                return false;
            }
        }
        return true;
    }

    /**
     * @param document
     * @return
     */
    protected boolean checkAgencyReportsTo(MaintenanceDocument document) {
        boolean success = true;

        if (newAgency.getReportsToAgencyNumber() != null) {
            if (newAgency.getReportsToAgency() == null) { // Agency must exist

                putFieldError("reportsToAgencyNumber", KFSKeyConstants.ERROR_AGENCY_NOT_FOUND, newAgency.getReportsToAgencyNumber());
                success = false;

            }
            else if (!newAgency.getReportsToAgency().isActive()) { // Agency must be active. See KULCG-263

                putFieldError("reportsToAgencyNumber", KFSKeyConstants.ERROR_AGENCY_INACTIVE, newAgency.getReportsToAgencyNumber());
                success = false;

            }
            else if (newAgency.getAgencyNumber().equals(newAgency.getReportsToAgencyNumber())) {

                putFieldError("reportsToAgencyNumber", KFSKeyConstants.ERROR_AGENCY_REPORTS_TO_SELF, newAgency.getAgencyNumber());
                success = false;

            }
            else { // No circular references

                List agencies = new ArrayList();

                Agency agency = newAgency;

                while (agency.getReportsToAgency() != null && success) {
                    if (!agencies.contains(agency.getAgencyNumber())) {
                        agencies.add(agency.getAgencyNumber());
                    }
                    else {

                        putFieldError("reportsToAgencyNumber", KFSKeyConstants.ERROR_AGENCY_CIRCULAR_REPORTING, agency.getAgencyNumber());
                        success = false;
                    }

                    agency = agency.getReportsToAgency();
                }
            }
        }
        return success;
    }

    /**
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#setupConvenienceObjects()
     */
    @Override
    public void setupConvenienceObjects() {
        newAgency = (Agency) super.getNewBo();
        oldAgency = (Agency) super.getOldBo();
    }


    /**
     * Overrides the method in MaintenanceDocumentRuleBase to give error message to the user when the user tries to add multiple
     * Primary Agency Addresses. At most one Primary Agency Address is allowed. contract.
     *
     * @see org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase#processAddCollectionLineBusinessRules(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.lang.String, org.kuali.rice.krad.bo.PersistableBusinessObject)
     */
    @Override
    public boolean processAddCollectionLineBusinessRules(MaintenanceDocument document, String collectionName, PersistableBusinessObject line) {

        // Only do if the CG Billing enhancements are turned on
        if (contractsGrantsBillingEnhancementsInd) {

            if (line instanceof AgencyAddress) {
                AgencyAddress newAgencyAddress = (AgencyAddress) line;
                if (collectionName.equals(KFSPropertyConstants.AGENCY_ADDRESSES)) {
                    newAgency = (Agency) document.getNewMaintainableObject().getBusinessObject();
                    List<AgencyAddress> agencyAddresses = newAgency.getAgencyAddresses();
                    String tmpCode = newAgencyAddress.getCustomerAddressTypeCode();

                    // Check if there is an Agency Primary Address in the collection lines or not.
                    int count = 0;
                    for (AgencyAddress agencyAddress : agencyAddresses) {
                        String customerAddressTypeCode = agencyAddress.getCustomerAddressTypeCode();
                        if (customerAddressTypeCode != null && customerAddressTypeCode.equals(CGConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE)) {
                            count++;
                            if (ObjectUtils.isNotNull(tmpCode) && !tmpCode.isEmpty() && tmpCode.equals(CGConstants.AGENCY_PRIMARY_ADDRESSES_TYPE_CODE)) {
                                String elementLabel = SpringContext.getBean(DataDictionaryService.class).getCollectionElementLabel(Agency.class.getName(), collectionName, AgencyAddress.class);
                                putFieldError(collectionName, KFSKeyConstants.ERROR_MULTIPLE_PRIMARY, elementLabel);
                                return false;
                            }
                            else {
                                boolean isValid = checkAddressIsValid(newAgencyAddress);
                                if (!isValid) {
                                    return isValid;
                                }
                            }
                        }
                    }

                    if (count > 1) {
                        String elementLabel = SpringContext.getBean(DataDictionaryService.class).getCollectionElementLabel(Award.class.getName(), collectionName, AwardFundManager.class);
                        putFieldError(collectionName, KFSKeyConstants.ERROR_MULTIPLE_PRIMARY, elementLabel);
                        return false;
                    }
                    else {
                        boolean isValid = checkAddressIsValid(newAgencyAddress);
                        if (!isValid) {
                            return isValid;
                        }
                    }
                }
            }

        }

        return super.processAddCollectionLineBusinessRules(document, collectionName, line);
    }

    /**
     * This method validates the addresses when Customer is created from Agency
     *
     * @param agencyAddress
     * @return
     */
    public boolean checkAddressIsValid(AgencyAddress agencyAddress) {
        boolean isValid = true;
        // To validate only if the Agency agency exists field says "Create New Customer".
        String customerExistsValue = newAgency.getCustomerCreated();
        if (CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE.equalsIgnoreCase(customerExistsValue)) {
            if (CGKeyConstants.AgencyConstants.AGENCY_ADDRESS_TYPE_CODE_US.equalsIgnoreCase(agencyAddress.getAgencyCountryCode())) {

                if (agencyAddress.getAgencyZipCode() == null || "".equalsIgnoreCase(agencyAddress.getAgencyZipCode())) {
                    isValid = false;
                    GlobalVariables.getMessageMap().putError(CGPropertyConstants.AgencyFields.AGENCY_ADDRESS_ZIP_CODE, CGKeyConstants.AgencyConstants.ERROR_AGENCY_ADDRESS_ZIP_CODE_REQUIRED_WHEN_COUNTTRY_US);
                }
                if (agencyAddress.getAgencyStateCode() == null || "".equalsIgnoreCase(agencyAddress.getAgencyStateCode())) {
                    isValid = false;
                    GlobalVariables.getMessageMap().putError(CGPropertyConstants.AgencyFields.AGENCY_ADDRESS_STATE_CODE, CGKeyConstants.AgencyConstants.ERROR_AGENCY_ADDRESS_STATE_CODE_REQUIRED_WHEN_COUNTTRY_US);
                }
            }
            else {
                if (agencyAddress.getAgencyInternationalMailCode() == null || "".equalsIgnoreCase(agencyAddress.getAgencyInternationalMailCode())) {
                    isValid = false;
                    GlobalVariables.getMessageMap().putError(CGPropertyConstants.AgencyFields.AGENCY_ADDRESS_INTERNATIONAL_MAIL_CODE, CGKeyConstants.AgencyConstants.ERROR_AGENCY_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US);
                }
                if (agencyAddress.getAgencyAddressInternationalProvinceName() == null || "".equalsIgnoreCase(agencyAddress.getAgencyAddressInternationalProvinceName())) {
                    isValid = false;
                    GlobalVariables.getMessageMap().putError(CGPropertyConstants.AgencyFields.AGENCY_ADDRESS_INTERNATIONAL_PROVINCE_NAME, CGKeyConstants.AgencyConstants.ERROR_AGENCY_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US);
                }
            }
        }
        return isValid;
    }

    /**
     * This method validates the addresses when Customer is created from Agency
     *
     * @param agencyAddress
     * @param ind
     * @return
     */
    public boolean checkAddressIsValid(AgencyAddress agencyAddress, int ind) {
        boolean isValid = true;
        String propertyName = CGPropertyConstants.AgencyFields.AGENCY_TAB_ADDRESSES + "[" + ind + "].";
        // To validate only if the Agency agencyexists field says "Create New Customer".
        String customerExistsValue = newAgency.getCustomerCreated();
        if (CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE.equalsIgnoreCase(customerExistsValue)) {
            if (CGKeyConstants.AgencyConstants.AGENCY_ADDRESS_TYPE_CODE_US.equalsIgnoreCase(agencyAddress.getAgencyCountryCode())) {

                if (agencyAddress.getAgencyZipCode() == null || "".equalsIgnoreCase(agencyAddress.getAgencyZipCode())) {
                    isValid = false;
                    putFieldError(propertyName + CGPropertyConstants.AgencyFields.AGENCY_ADDRESS_ZIP_CODE, CGKeyConstants.AgencyConstants.ERROR_AGENCY_ADDRESS_ZIP_CODE_REQUIRED_WHEN_COUNTTRY_US);
                }
                if (agencyAddress.getAgencyStateCode() == null || "".equalsIgnoreCase(agencyAddress.getAgencyStateCode())) {
                    isValid = false;
                    putFieldError(propertyName + CGPropertyConstants.AgencyFields.AGENCY_ADDRESS_STATE_CODE, CGKeyConstants.AgencyConstants.ERROR_AGENCY_ADDRESS_STATE_CODE_REQUIRED_WHEN_COUNTTRY_US);
                }
            }
            else {
                if (agencyAddress.getAgencyInternationalMailCode() == null || "".equalsIgnoreCase(agencyAddress.getAgencyInternationalMailCode())) {
                    isValid = false;
                    putFieldError(propertyName + CGPropertyConstants.AgencyFields.AGENCY_ADDRESS_INTERNATIONAL_MAIL_CODE, CGKeyConstants.AgencyConstants.ERROR_AGENCY_ADDRESS_INTERNATIONAL_MAIL_CODE_REQUIRED_WHEN_COUNTTRY_NON_US);
                }
                if (agencyAddress.getAgencyAddressInternationalProvinceName() == null || "".equalsIgnoreCase(agencyAddress.getAgencyAddressInternationalProvinceName())) {
                    isValid = false;
                    putFieldError(propertyName + CGPropertyConstants.AgencyFields.AGENCY_ADDRESS_INTERNATIONAL_PROVINCE_NAME, CGKeyConstants.AgencyConstants.ERROR_AGENCY_ADDRESS_INTERNATIONAL_PROVINCE_NAME_REQUIRED_WHEN_COUNTTRY_NON_US);
                }
            }
        }
        return isValid;
    }


    /**
     * This method validates the addresses when Customer is created from Agency
     *
     * @param agency
     * @return
     */
    public boolean validateAddresses(Agency agency) {
        boolean isValid = true;
        int i = 0;

        i = 0;
        for (AgencyAddress agencyAddress : agency.getAgencyAddresses()) {
            isValid &= checkAddressIsValid(agencyAddress, i);
            i++;
        }


        return isValid;
    }

    /**
     * This method validates that a customer type is selected when the create new customer
     *  option is selected.
     *
     * @param agency
     * @return
     */
    public boolean validateCustomerType(Agency agency) {
        boolean isValid = true;

        // Only validate if new customer create option is selected
        if ( CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE.equalsIgnoreCase(agency.getCustomerCreated()) ){
            // Customer Type must be filled-in
            if( StringUtils.isEmpty(agency.getCustomerTypeCode()) ){
                putFieldError(CGPropertyConstants.AgencyFields.AGENCY_CUSTOMER_TYPE_CODE, CGKeyConstants.AgencyConstants.ERROR_AGENCY_CUSTOMER_TYPE_CODE_REQUIRED_WHEN_AGENCY_CUSTOMER_NEW);
                isValid &= false;
            }
        }

        return isValid;
    }
}
