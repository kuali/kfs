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
package org.kuali.kfs.module.ar.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.kuali.kfs.integration.cg.ContractsAndGrantsAgencyAddress;
import org.kuali.kfs.integration.cg.ContractsAndGrantsBillingAgency;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.module.ar.service.CustomerDocumentService;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.KewApiServiceLocator;
import org.kuali.rice.kew.api.document.attribute.DocumentAttributeIndexingQueue;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KualiModuleService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Implementation of the Customer Document Service.
 */
public class CustomerDocumentServiceImpl implements CustomerDocumentService {

    private DocumentService documentService;
    private WorkflowDocumentService workflowDocumentService;

    public String createAndSaveCustomer(String description, ContractsAndGrantsBillingAgency agency) throws WorkflowException {
        MaintenanceDocument doc = null;
        doc = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getNewDocument(SpringContext.getBean(MaintenanceDocumentDictionaryService.class).getDocumentTypeName(Customer.class));
        // set a description to say that this application document has been created by the Agency Document
        doc.getDocumentHeader().setDocumentDescription(description);
        // to set the explanation to reference the agency number
        doc.getDocumentHeader().setExplanation(description + "(Agency Number - " + agency.getAgencyNumber() + " )");
        // refresh nonupdatable references and save the Customer Document
        doc.refreshNonUpdateableReferences();


        // If the document gets created successfully, then we set values to the agency as well as the customer.
        Customer customer = (Customer) doc.getNewMaintainableObject().getBusinessObject();

        // this step is done before so that the customer name is
        // in - should be uppercase for the Customer to be
        // identified.
        customer.setCustomerName(agency.getReportingName().toUpperCase());
        customer.setCustomerTypeCode(agency.getCustomerTypeCode());

        if (agency.isActive()) {
            customer.setActive(true);
        }

        // To call the customer service to get the customer number and set it to the customer number
        CustomerService customerService = SpringContext.getBean(CustomerService.class);
        String customerNumber = customerService.getNextCustomerNumber(customer);
        customer.setCustomerNumber(customerNumber);

        List<ContractsAndGrantsAgencyAddress> agencyAddresses = new ArrayList<ContractsAndGrantsAgencyAddress>();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put(KFSPropertyConstants.AGENCY_NUMBER, agency.getAgencyNumber());
        agencyAddresses = SpringContext.getBean(KualiModuleService.class).getResponsibleModuleService(ContractsAndGrantsAgencyAddress.class).getExternalizableBusinessObjectsList(ContractsAndGrantsAgencyAddress.class, map);

        // to set the primary agency address to the customer
        for (ContractsAndGrantsAgencyAddress agencyAddress : agencyAddresses) {
            if (agencyAddress.isPrimary()) {
                if(CollectionUtils.isEmpty(customer.getCustomerAddresses())){
                    customer.getCustomerAddresses().add(new CustomerAddress());
                }
                CustomerAddress customerAddress = customer.getCustomerAddresses().get(0);
                customerAddress.setCustomerAddressName(agencyAddress.getAgencyAddressName());
                customerAddress.setCustomerAddressTypeCode(agencyAddress.getAgencyAddressTypeCode());
                customerAddress.setCustomerLine1StreetAddress(agencyAddress.getAgencyLine1StreetAddress());
                customerAddress.setCustomerLine2StreetAddress(agencyAddress.getAgencyLine2StreetAddress());
                customerAddress.setCustomerCityName(agencyAddress.getAgencyCityName());
                customerAddress.setCustomerCountryCode(agencyAddress.getAgencyCountryCode());
                customerAddress.setCustomerStateCode(agencyAddress.getAgencyStateCode());
                customerAddress.setCustomerZipCode(agencyAddress.getAgencyZipCode());
                customerAddress.setCustomerAddressInternationalProvinceName(agencyAddress.getAgencyAddressInternationalProvinceName());
                customerAddress.setCustomerInternationalMailCode(agencyAddress.getAgencyInternationalMailCode());
                
            }
        }

        documentService.saveDocument(doc);
        documentService.prepareWorkflowDocument(doc);
        // the next step is to route the document through the workflow
        workflowDocumentService.route(doc.getDocumentHeader().getWorkflowDocument(), "", null);


        final DocumentAttributeIndexingQueue documentAttributeIndexingQueue = KewApiServiceLocator.getDocumentAttributeIndexingQueue();
        documentAttributeIndexingQueue.indexDocument(doc.getDocumentNumber());

//        final WorkflowDocumentActions workflowDocumentActions = SpringContext.getBean(WorkflowDocumentActions.class);
//        workflowDocumentActions.indexDocument(new Long(doc.getDocumentNumber()));

        return customerNumber;
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


}
