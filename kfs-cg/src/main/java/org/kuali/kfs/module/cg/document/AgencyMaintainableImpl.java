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
package org.kuali.kfs.module.cg.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleBillingService;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.module.cg.service.ContractsAndGrantsBillingService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Methods for the Agency maintenance document UI.
 */
public class AgencyMaintainableImpl extends ContractsGrantsBillingMaintainable {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AgencyMaintainableImpl.class);

    private static final String CREATED_BY_AGENCY_DOC = "message.ar.createdByAgencyDocument";
    private static volatile AccountsReceivableModuleBillingService accountsReceivableModuleBillingService;

    /**
     * Gets the underlying Agency.
     *
     * @return
     */
    public Agency getAgency() {
        return (Agency) getBusinessObject();
    }

    /**
     * This method overrides the parent method to create a new Customer document when Agency document goes to final
     * if CGB is enabled.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader header) {
        super.doRouteStatusChange(header);

        if (getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            WorkflowDocument workflowDoc = header.getWorkflowDocument();
            Agency agency = getAgency();
            String description = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(CREATED_BY_AGENCY_DOC);
            // Use the isProcessed() method so this code is only executed when the final approval occurs
            if (workflowDoc.isProcessed()) {
                try {
                    if (this.businessObject != null) {
                        // To save the agency and address before creating customer to copy over the address.
                        SpringContext.getBean(BusinessObjectService.class).save(agency);
                        for (AgencyAddress agencyAddress : agency.getAgencyAddresses()) {
                            agencyAddress.setAgency(agency);

                            SpringContext.getBean(BusinessObjectService.class).save(agencyAddress);
                        }
                        // To create customer only if "create new customer" was selected on the document.
                        if (CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE.equalsIgnoreCase(agency.getCustomerCreationOptionCode())) {
                            String customerNumber = SpringContext.getBean(AccountsReceivableModuleService.class).createAndSaveCustomer(description, agency);
                            agency.setCustomerCreationOptionCode(CGConstants.AGENCY_USE_EXISTING_CUSTOMER_CODE);
                            agency.setCustomerNumber(customerNumber);

                        }
                        // If no customer was selected, clear out the link between the agency and the old customer
                        else if (CGConstants.AGENCY_NO_CUSTOMER_CODE.equalsIgnoreCase(agency.getCustomerCreationOptionCode())) {
                            agency.setCustomerCreationOptionCode(CGConstants.AGENCY_NO_CUSTOMER_CODE);
                            agency.setCustomerNumber(null);
                            agency.setCustomer(null);
                            agency.setCustomerTypeCode(null);
                        }

                        // To set dunningCampaign value from Agency to all the awards in the agency.

                        List<Award> awards = new ArrayList<Award>();
                        Map<String, Object> criteria = new HashMap<String, Object>();
                        criteria.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
                        awards = (List<Award>) SpringContext.getBean(BusinessObjectService.class).findMatching(Award.class, criteria);
                        for (Award award : awards) {
                            award.setDunningCampaign(agency.getDunningCampaign());
                            if (ObjectUtils.isNotNull(agency.getCustomer()) && ObjectUtils.isNotNull(agency.getCustomer().isStopWorkIndicator())) {
                                award.setStopWorkIndicator(agency.getCustomer().isStopWorkIndicator());
                            }
                            SpringContext.getBean(BusinessObjectService.class).save(award);
                        }

                    }

                }
                catch (WorkflowException ex) {
                    throw new RuntimeException("Error creating Customer Document from Agency document.", ex);

                }
            }
        }
    }

    /**
     * Override the getSections method on this maintainable so that the document type name field can be set to read-only for
     */
    @Override
    public List getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        List<Section> sections = super.getSections(document, oldMaintainable);
        Agency oldAgency = (Agency) document.getOldMaintainableObject().getBusinessObject();
        Agency agency = (Agency) document.getNewMaintainableObject().getBusinessObject();
        for (Section section : sections) {
            String sectionId = section.getSectionId();
            if (sectionId.equalsIgnoreCase(CGPropertyConstants.CUSTOMER)) {
                for (Row row : section.getRows()) {
                    for (Field field : row.getFields()) {
                        if (StringUtils.isNotEmpty(agency.getCustomerCreationOptionCode())) {
                            if (agency.getCustomerCreationOptionCode().equals(CGConstants.AGENCY_USE_EXISTING_CUSTOMER_CODE)) {
                                if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_NUMBER)) {
                                    field.setReadOnly(false);
                                }
                                else if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_TYPE_CODE)) {
                                    field.setReadOnly(true);
                                    if (ObjectUtils.isNotNull(agency.getCustomer())) {
                                        agency.setCustomerTypeCode(agency.getCustomer().getCustomerTypeCode());
                                    } else {
                                        agency.setCustomerTypeCode(null);
                                    }
                                }
                            }
                            else if (agency.getCustomerCreationOptionCode().equals(CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE)) {
                                if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_NUMBER)) {
                                    agency.setCustomerNumber(null);
                                    field.setReadOnly(true);
                                }
                                else if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_TYPE_CODE)) {
                                    field.setReadOnly(false);
                                }
                            }
                            else if (agency.getCustomerCreationOptionCode().equals(CGConstants.AGENCY_NO_CUSTOMER_CODE)) {
                                if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_NUMBER)) {
                                    agency.setCustomerNumber(null);
                                    field.setReadOnly(true);
                                }
                                else if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_TYPE_CODE)) {
                                    field.setReadOnly(true);
                                    agency.setCustomerTypeCode(null);
                                }
                            }
                        }
                        else {
                            if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_NUMBER)) {
                                agency.setCustomerNumber(null);
                                field.setReadOnly(true);
                            }
                            else if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_TYPE_CODE)) {
                                agency.setCustomerTypeCode(null);
                                field.setReadOnly(true);
                            }
                        }

                    }
                }
            }
        }
        return sections;
    }

    /**
     * If the Contracts & Grants Billing (CGB) enhancement is disabled, we don't want to
     * process sections only related to CGB.
     *
     * @return Collection of section ids to ignore
     */
    @Override
    protected Collection<?> getSectionIdsToIgnore() {
        if (!getAccountsReceivableModuleBillingService().isContractsGrantsBillingEnhancementActive()) {
            return SpringContext.getBean(ContractsAndGrantsBillingService.class).getAgencyContractsGrantsBillingSectionIds();
        } else {
            return CollectionUtils.EMPTY_COLLECTION;
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        Agency agency = getAgency();
        String customerNumber = (String) fieldValues.get(KFSConstants.MAINTENANCE_NEW_MAINTAINABLE + "." + CGPropertyConstants.CUSTOMER_NUMBER);

        if (ObjectUtils.isNotNull(customerNumber)) {
            AccountsReceivableCustomer customer = SpringContext.getBean(AccountsReceivableModuleService.class).findCustomer(customerNumber);

            agency.setCustomerTypeCode(customer.getCustomerTypeCode());

        }
        super.refresh(refreshCaller, fieldValues, document);
    }


    /**
     * Overridden to set the default values on the Agency document.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterNew(org.kuali.rice.kns.document.MaintenanceDocument,
     *      java.util.Map)
     */
    @Override
    public void processAfterNew(MaintenanceDocument document, Map<String, String[]> parameters) {

        super.processAfterNew(document, parameters);

        Agency agency = getAgency();
        // Default Billing Frequency
        // Retrieve default value from the corresponding default value parameter

        String parameterDunningCampaignCode = getAccountsReceivableModuleBillingService().getDefaultDunningCampaignCode();
        if (!StringUtils.isBlank(parameterDunningCampaignCode)) {
            agency.setDunningCampaign(parameterDunningCampaignCode);
        }
    }

    public static AccountsReceivableModuleBillingService getAccountsReceivableModuleBillingService() {
        if (accountsReceivableModuleBillingService == null) {
            accountsReceivableModuleBillingService = SpringContext.getBean(AccountsReceivableModuleBillingService.class);
        }
        return accountsReceivableModuleBillingService;
    }

    @Override
    public void processAfterCopy(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterCopy(document, parameters);

        Agency agency = (Agency) getBusinessObject();
        List<AgencyAddress> agencyAddresses = agency.getAgencyAddresses();
        for (AgencyAddress agencyAddress : agencyAddresses) {
            agencyAddress.setAgencyAddressIdentifier(null);
        }
    }

}
