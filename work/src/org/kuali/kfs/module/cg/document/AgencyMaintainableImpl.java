/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.cg.document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.cg.CGConstants;
import org.kuali.kfs.module.cg.CGPropertyConstants;
import org.kuali.kfs.module.cg.businessobject.Agency;
import org.kuali.kfs.module.cg.businessobject.AgencyAddress;
import org.kuali.kfs.module.cg.businessobject.Award;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
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
public class AgencyMaintainableImpl extends FinancialSystemMaintainable {

    protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AwardMaintainableImpl.class);

    private static final String CREATED_BY_AGENCY_DOC = "message.ar.createdByAgencyDocument";

    /**
     * Constructs an AgencyMaintainableImpl.
     */
    public AgencyMaintainableImpl() {
        super();
    }

    /**
     * Constructs a AgencyMaintainableImpl.
     *
     * @param agency
     */
    public AgencyMaintainableImpl(Agency agency) {
        super(agency);
        this.setBoClass(agency.getClass());
    }

    /**
     * Gets the underlying Agency.
     *
     * @return
     */
    public Agency getAgency() {
        return (Agency) getBusinessObject();
    }

    /**
     * This method overrides the parent method to create a new Customer document when Agency document goes to final.
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.krad.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader header) {
        super.doRouteStatusChange(header);
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
                    if (CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE.equalsIgnoreCase(agency.getCustomerCreated())) {
                        String customerNumber = SpringContext.getBean(AccountsReceivableModuleService.class).createAndSaveCustomer(description, agency);
                        agency.setCustomerCreated(CGConstants.AGENCY_USE_EXISTING_CUSTOMER_CODE);
                        agency.setCustomerNumber(customerNumber);

                    }
                    // If no customer was selected, clear out the link between the agency and the old customer
                    else if (CGConstants.AGENCY_NO_CUSTOMER_CODE.equalsIgnoreCase(agency.getCustomerCreated())) {
                        agency.setCustomerCreated(CGConstants.AGENCY_NO_CUSTOMER_CODE);
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
                        if (StringUtils.isNotEmpty(agency.getCustomerCreated())) {
                            if (agency.getCustomerCreated().equals(CGConstants.AGENCY_USE_EXISTING_CUSTOMER_CODE)) {
                                if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_NUMBER)) {
                                    field.setReadOnly(false);
                                }
                                else if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_TYPE_CODE)) {
                                    field.setReadOnly(true);
                                }
                            }
                            else if (agency.getCustomerCreated().equals(CGConstants.AGENCY_CREATE_NEW_CUSTOMER_CODE)) {
                                if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_NUMBER)) {
                                    agency.setCustomerNumber(null);
                                    field.setReadOnly(true);
                                }
                                else if (field.getPropertyName().equals(CGPropertyConstants.CUSTOMER_TYPE_CODE)) {
                                    field.setReadOnly(false);
                                    agency.setCustomerTypeCode(null);
                                }
                            }
                            else if (agency.getCustomerCreated().equals(CGConstants.AGENCY_NO_CUSTOMER_CODE)) {
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
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#refresh(java.lang.String, java.util.Map,
     *      org.kuali.rice.kns.document.MaintenanceDocument)
     */
    @Override
    public void refresh(String refreshCaller, Map fieldValues, MaintenanceDocument document) {
        Agency agency = getAgency();
        String customerNumber = (String) fieldValues.get("document.newMaintainableObject.customerNumber");

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
        String parameterAgencyFrequencyCode = "";
        String parameterDunningCampaignCode = "";
        // Default Billing Frequency
        try {
            // Retrieve default value from the corresponding default value parameter
            parameterDunningCampaignCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Agency.class, CGConstants.DEFAULT_DUNNING_CAMPAIGN_PARAMETER);
            parameterAgencyFrequencyCode = SpringContext.getBean(ParameterService.class).getParameterValueAsString(Agency.class, CGConstants.DEFAULT_PREFERRED_BILLING_FREQUENCY_PARAMETER);
            agency.setAgencyFrequencyCode(parameterAgencyFrequencyCode);
            agency.setDunningCampaign(parameterDunningCampaignCode);
        }
        catch (Exception e) {
            LOG.debug("Exception : " + e.getMessage() + " happened when getting default values from parameter");
        }
    }
}
