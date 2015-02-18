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
package org.kuali.kfs.module.ar.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.service.CustomerDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.bo.BusinessObject;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;
import org.springframework.transaction.annotation.Transactional;

/**
 * Implementation of the Customer Document Service.
 */
@Transactional
public class CustomerDocumentServiceImpl implements CustomerDocumentService {

    protected CustomerService customerService;
    protected DocumentService documentService;
    protected DataDictionaryService dataDictionaryService;
    protected KualiModuleService kualiModuleService;
    protected MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    protected WorkflowDocumentService workflowDocumentService;

    @Override
    public String createAndSaveCustomer(String description, ContractsAndGrantsBillingAgency agency) throws WorkflowException {
        MaintenanceDocument doc = null;
        doc = (MaintenanceDocument) documentService.getNewDocument(maintenanceDocumentDictionaryService.getDocumentTypeName(Customer.class));
        // set a description to say that this application document has been created by the Agency Document
        doc.getDocumentHeader().setDocumentDescription(truncateField(DocumentHeader.class, KFSPropertyConstants.DOCUMENT_DESCRIPTION, description));
        // to set the explanation to reference the agency number
        doc.getDocumentHeader().setExplanation(description + "(Agency Number - " + agency.getAgencyNumber() + " )");
        // refresh nonupdatable references and save the Customer Document
        doc.refreshNonUpdateableReferences();


        // If the document gets created successfully, then we set values to the agency as well as the customer.
        Customer customer = (Customer) doc.getNewMaintainableObject().getBusinessObject();

        // this step is done before so that the customer name is
        // in - should be uppercase for the Customer to be
        // identified.
        customer.setCustomerName(truncateField(agency.getClass(), ArPropertyConstants.CustomerFields.CUSTOMER_NAME, agency.getReportingName().toUpperCase()));
        customer.setCustomerTypeCode(truncateField(agency.getClass(), ArPropertyConstants.CustomerTypeFields.CUSTOMER_TYPE_CODE, agency.getCustomerTypeCode()));

        if (agency.isActive()) {
            customer.setActive(true);
        }

        // To call the customer service to get the customer number and set it to the customer number
        String customerNumber = customerService.getNextCustomerNumber(customer);
        customer.setCustomerNumber(customerNumber);

        List<? extends ContractsAndGrantsAgencyAddress> agencyAddresses = new ArrayList<ContractsAndGrantsAgencyAddress>();
        if (agency.getAgencyAddresses() == null || agency.getAgencyAddresses().isEmpty()) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
            agencyAddresses = kualiModuleService.getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsAgencyAddress.class, map);
        } else {
            agencyAddresses = agency.getAgencyAddresses();
        }
        for (ContractsAndGrantsAgencyAddress agencyAddress : agencyAddresses) {
            CustomerAddress customerAddress = new CustomerAddress();
            customerAddress.setCustomerAddressName(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_ADDRESS_NAME, agencyAddress.getAgencyAddressName()));
            customerAddress.setCustomerAddressTypeCode(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_ADDRESS_TYPE_CODE, agencyAddress.getCustomerAddressTypeCode()));
            customerAddress.setCustomerLine1StreetAddress(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_LINE1_STREET_ADDRESS, agencyAddress.getAgencyLine1StreetAddress()));
            customerAddress.setCustomerLine2StreetAddress(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_LINE2_STREET_ADDRESS, agencyAddress.getAgencyLine2StreetAddress()));
            customerAddress.setCustomerCityName(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_CITY_NAME, agencyAddress.getAgencyCityName()));
            customerAddress.setCustomerCountryCode(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_COUNTRY_CODE, agencyAddress.getAgencyCountryCode()));
            customerAddress.setCustomerStateCode(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_STATE_CODE, agencyAddress.getAgencyStateCode()));
            customerAddress.setCustomerZipCode(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_ZIP_CODE, agencyAddress.getAgencyZipCode()));
            customerAddress.setCustomerAddressInternationalProvinceName(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_ADDRESS_INTERNATIONAL_PROVINCE_NAME, agencyAddress.getAgencyAddressInternationalProvinceName()));
            customerAddress.setCustomerInternationalMailCode(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_INTERNATIONAL_MAIL_CODE, agencyAddress.getAgencyInternationalMailCode()));
            customerAddress.setCustomerEmailAddress(truncateField(CustomerAddress.class, ArPropertyConstants.CustomerAddressFields.CUSTOMER_EMAIL_ADDRESS, agencyAddress.getAgencyContactEmailAddress()));
            customer.getCustomerAddresses().add(customerAddress);
        }

        documentService.saveDocument(doc);
        documentService.prepareWorkflowDocument(doc);
        // the next step is to route the document through the workflow
        workflowDocumentService.route(doc.getDocumentHeader().getWorkflowDocument(), "", null);


        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
        documentAttributeIndexingQueue.indexDocument(doc.getDocumentNumber());

        return customerNumber;
    }

    /**
     * Truncates the given value to the length set in the data dictionary for the given business object class and property name
     * @param boClass the business object class the value is being set on
     * @param targetPropertyName the name of the property on an object of that class where we're setting the value
     * @param value the value to set
     * @return the truncated value
     */
    protected String truncateField(Class<? extends BusinessObject> boClass, String targetPropertyName, String value) {
        final Integer maxLength = getDataDictionaryService().getAttributeMaxLength(boClass, targetPropertyName);
        if (StringUtils.isBlank(value) || ObjectUtils.isNull(maxLength) || maxLength.intValue() > value.length()) {
            return value;
        }
        return value.substring(0, maxLength.intValue());
    }

    /**
     * This method gets the document service
     *
     * @return the document service
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * This method sets the document service
     *
     * @param documentService
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
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

    public CustomerService getCustomerService() {
        return customerService;
    }

    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    /**
     * Sets the kualiModuleService attribute value.
     *
     * @param kualiModuleService The kualiModuleService to set.
     */
    @NonTransactional
    public void setKualiModuleService(KualiModuleService kualiModuleService) {
        this.kualiModuleService = kualiModuleService;
    }

    public MaintenanceDocumentDictionaryService getMaintenanceDocumentDictionaryService() {
        return maintenanceDocumentDictionaryService;
    }

    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }

    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
}
