/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.report.service.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.identity.FinancialSystemUserRoleTypeServiceImpl;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.dataaccess.CustomerDao;
import org.kuali.kfs.module.ar.document.CollectionsDocumentWithCustomer;
import org.kuali.kfs.module.ar.identity.ArKimAttributes;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.kim.api.KimConstants;
import org.kuali.rice.kim.api.role.RoleService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * Abstract Base class for report generation for contracts and grants reports where collector is part of the report criteria.
 */
public abstract class ContractsGrantsCollectorReportServiceImplBase extends ContractsGrantsReportServiceImplBase {

    protected CustomerDao customerDao;
    protected RoleService roleService;

    /**
     * Removes any documents from the sourceCollectionDocuments that have customers that don't match the collector
     * based on the role qualifiers for the collector assigned to the CGB Collector role.
     *
     * @param collector String principalId for the collector used to match against customers and filter the documents
     * @param sourceCollectionDocuments Collection of documents to filter
     */
    protected void filterRecordsForCollector(String collector, Collection<? extends CollectionsDocumentWithCustomer> sourceCollectionDocuments) {
        List<Customer> allCustomers = new ArrayList<Customer>();

        // get role qualifiers for CGB collector role
        Map<String, String> qualification = new HashMap<String, String>(2);
        qualification.put(FinancialSystemUserRoleTypeServiceImpl.PERFORM_QUALIFIER_MATCH, "true");
        qualification.put(KimConstants.AttributeConstants.NAMESPACE_CODE, ArConstants.AR_NAMESPACE_CODE);

        List<Map<String, String>> roleQualifiers = roleService.getRoleQualifersForPrincipalByNamespaceAndRolename(collector, ArConstants.AR_NAMESPACE_CODE, KFSConstants.SysKimApiConstants.ACCOUNTS_RECEIVABLE_COLLECTOR, qualification);
        if ((roleQualifiers != null) && !roleQualifiers.isEmpty()) {
            for (Map<String, String> roleQualifier: roleQualifiers) {
                String startingLetter = roleQualifier.get(ArKimAttributes.CUSTOMER_LAST_NAME_STARTING_LETTER);
                String endingLetter = roleQualifier.get(ArKimAttributes.CUSTOMER_LAST_NAME_ENDING_LETTER);
                if (StringUtils.isNotEmpty(startingLetter) && StringUtils.isNotEmpty(endingLetter)) {
                    // get customers that match
                    if (StringUtils.isNotBlank(startingLetter) && StringUtils.isNotBlank(endingLetter)) {
                        Collection<Customer> customers = customerDao.getByNameRange(startingLetter, endingLetter);
                        allCustomers.addAll(customers);
                    }
                }
            }

            filterDocsAccordingToCustomerNumbers(sourceCollectionDocuments, allCustomers);
        }
    }

    /**
     * Checks if the customer for the document is a customer for the collector and if not, removes it from the
     * sourceCollectionDocs collection.
     *
     * @param sourceCollectionDocs Collection of documents to filter
     * @param customers Collection of customers for the collector
     */
    private void filterDocsAccordingToCustomerNumbers(Collection<? extends CollectionsDocumentWithCustomer> sourceCollectionDocs, Collection<Customer> customers) {
        if (ObjectUtils.isNotNull(sourceCollectionDocs) && CollectionUtils.isNotEmpty(sourceCollectionDocs)) {
            for (CollectionsDocumentWithCustomer refDoc : sourceCollectionDocs) {
                if (!isCustomerAvailableInList(refDoc.getCustomerNumber(), customers)) {
                    sourceCollectionDocs.remove(refDoc);
                }
            }
        }
    }

    /**
     * Checks if the customerNumber is in the Collection of customers.
     *
     * @param customerNumber String number of customer to search Collection of customers for.
     * @param customers Collection of customers to search.
     * @return true if customer is in Collection, false otherwise
     */
    private boolean isCustomerAvailableInList(String customerNumber, Collection<Customer> customers) {
        boolean isAvail = false;
        if (ObjectUtils.isNotNull(customers) && !customers.isEmpty()) {
            for (Customer customer : customers) {
                if (customer.getCustomerNumber().equalsIgnoreCase(customerNumber)) {
                    isAvail = true;
                    break;
                }
            }
        }
        else {
            isAvail = false;
        }
        return isAvail;
    }

    public CustomerDao getCustomerDao() {
        return customerDao;
    }

    public void setCustomerDao(CustomerDao customerDao) {
        this.customerDao = customerDao;
    }

    public RoleService getRoleService() {
        return roleService;
    }

    public void setRoleService(RoleService roleService) {
        this.roleService = roleService;
    }

}
