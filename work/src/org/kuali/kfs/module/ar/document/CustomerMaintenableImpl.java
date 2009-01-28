/*
 * Copyright 2008 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.document;

import java.sql.Date;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.kew.exception.WorkflowException;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kim.service.PersonService;
import org.kuali.rice.kns.bo.PersistableBusinessObject;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.DateTimeService;
import org.kuali.rice.kns.service.DocumentService;
import org.kuali.rice.kns.util.KNSConstants;

public class CustomerMaintenableImpl extends FinancialSystemMaintainable {

    private static final String REQUIRES_APPROVAL_NODE = "RequiresApproval";

    private transient DateTimeService dateTimeService;
    
    @Override
    @SuppressWarnings("unchecked")
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);

        Maintainable oldMaintainable = document.getOldMaintainableObject();
        Maintainable newMaintainable = document.getNewMaintainableObject();

        Customer oldCustomer = (Customer) oldMaintainable.getBusinessObject();
        Customer newCustomer = (Customer) newMaintainable.getBusinessObject();

        // when we create new customer set the customerRecordAddDate to current date
        if (getMaintenanceAction().equalsIgnoreCase(KNSConstants.MAINTENANCE_NEW_ACTION)) {
            Date currentDate = getDateTimeService().getCurrentSqlDate();
            newCustomer.setCustomerRecordAddDate(currentDate);

        }

        List oldAddresses = oldCustomer.getCustomerAddresses();
        List newAddresses = newCustomer.getCustomerAddresses();

        // if new address was added or one of the old addresses was changes set customerAddressChangeDate to the current date
        if (oldAddresses != null && newAddresses != null) {
            if (oldAddresses.size() != newAddresses.size()) {
                Date currentDate = getDateTimeService().getCurrentSqlDate();
                newCustomer.setCustomerAddressChangeDate(currentDate);
            }
            else {
                for (int i = 0; i < oldAddresses.size(); i++) {
                    CustomerAddress oldAddress = (CustomerAddress) oldAddresses.get(i);
                    CustomerAddress newAddress = (CustomerAddress) newAddresses.get(i);
                    if (oldAddress.compareTo(newAddress) != 0) {
                        Date currentDate = getDateTimeService().getCurrentSqlDate();
                        newCustomer.setCustomerAddressChangeDate(currentDate);

                        break;
                    }
                }
            }
        }
    }

    @Override
    public PersistableBusinessObject initNewCollectionLine(String collectionName) {

        PersistableBusinessObject businessObject = super.initNewCollectionLine(collectionName);
        Customer customer = (Customer) this.businessObject;

        if (collectionName.equalsIgnoreCase(ArPropertyConstants.CustomerFields.CUSTOMER_TAB_ADDRESSES)) {

            CustomerAddress customerAddress = (CustomerAddress) businessObject;

            // set default address name to customer name
            customerAddress.setCustomerAddressName(customer.getCustomerName());

            if (KNSConstants.MAINTENANCE_NEW_ACTION.equalsIgnoreCase(getMaintenanceAction())) {

                boolean hasPrimaryAddress = false;

                for (CustomerAddress tempAddress : customer.getCustomerAddresses()) {
                    if (ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY.equalsIgnoreCase(tempAddress.getCustomerAddressTypeCode())) {
                        hasPrimaryAddress = true;
                        break;
                    }
                }
                // if maintenance action is NEW and customer already has a primary address set default value for address type code
                // to "Alternate"
                if (hasPrimaryAddress) {
                    customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
                }
                // otherwise set default value for address type code to "Primary"
                else {
                    customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_PRIMARY);
                }
            }

            // if maintenance action is EDIT or COPY set default value for address type code to "Alternate"
            if (KNSConstants.MAINTENANCE_EDIT_ACTION.equalsIgnoreCase(getMaintenanceAction()) || KNSConstants.MAINTENANCE_COPY_ACTION.equalsIgnoreCase(getMaintenanceAction())) {
                customerAddress.setCustomerAddressTypeCode(ArKeyConstants.CustomerConstants.CUSTOMER_ADDRESS_TYPE_CODE_ALTERNATE);
            }

        }

        return businessObject;

    }

    /**
     * Answers true for 2 conditions...
     * <li>a)New customer created by non-batch (web) user</li>
     * <li>b)Any edit to an existing customer record</li>
     * 
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        
        //  puke if we dont know how to handle the nodeName passed in
        if (!REQUIRES_APPROVAL_NODE.equals(nodeName)) {
            throw new UnsupportedOperationException("answerSplitNodeQuestion('" + nodeName + "') was called, but no handler is present for that nodeName.");
        }

        //  need the parent maint doc to see whether its a New or Edit, and 
        // to get the initiator
        FinancialSystemMaintenanceDocument maintDoc = getParentMaintDoc();
        
        // editing a customer always requires approval
        if (maintDoc.isEdit()) {
            return true;
        }
        
        // while creating a new customer, route when created by web application
        return (maintDoc.isNew() && createdByWebApp(maintDoc));
    }
    
    private FinancialSystemMaintenanceDocument getParentMaintDoc() {
        //  how I wish for the ability to directly access the parent object
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        FinancialSystemMaintenanceDocument maintDoc = null;
        try {
            maintDoc =(FinancialSystemMaintenanceDocument) documentService.getByDocumentHeaderId(this.documentNumber);
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        return maintDoc;
    }
    
    private boolean createdByWebApp(FinancialSystemMaintenanceDocument maintDoc) {
        String initiatorPrincipalId = maintDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        PersonService<Person> personService = SpringContext.getBean(PersonService.class);
        Person initiatorPerson = personService.getPerson(initiatorPrincipalId);
        return (initiatorPerson != null && !KFSConstants.SYSTEM_USER.equals(initiatorPerson.getPrincipalName()));
    }

    /**
     * Gets the dateTimeService attribute. 
     * @return Returns the dateTimeService.
     */
    public DateTimeService getDateTimeService() {
        if (dateTimeService == null) {
            dateTimeService = SpringContext.getBean(DateTimeService.class);
        }
        return dateTimeService;
    }

}
