/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.document;

import java.sql.Date;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.PropertyUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.ArPropertyConstants;
import org.kuali.kfs.module.ar.businessobject.ARCollector;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.businessobject.CustomerAddress;
import org.kuali.kfs.module.ar.businessobject.CustomerCollector;
import org.kuali.kfs.module.ar.businessobject.CustomerNote;
import org.kuali.kfs.module.ar.document.service.CustomerCollectorService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.identity.principal.Principal;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.util.KRADConstants;
import org.kuali.rice.krad.util.ObjectUtils;

public class CustomerMaintenableImpl extends FinancialSystemMaintainable {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerMaintenableImpl.class);

    private static final String REQUIRES_APPROVAL_NODE = "RequiresApproval";
    private static final String BO_NOTES = "boNotes";

    private transient DateTimeService dateTimeService;

    @Override
    @SuppressWarnings("unchecked")
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);

        // when we create new customer set the customerRecordAddDate to current date
        if (getMaintenanceAction().equalsIgnoreCase(KRADConstants.MAINTENANCE_NEW_ACTION)) {
            Customer oldCustomer = (Customer) document.getOldMaintainableObject().getBusinessObject();
            Customer newCustomer = (Customer) document.getNewMaintainableObject().getBusinessObject();
            Date currentDate = getDateTimeService().getCurrentSqlDate();
            newCustomer.setCustomerRecordAddDate(currentDate);
            newCustomer.setCustomerLastActivityDate(currentDate);
        }
    }

    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        super.doRouteStatusChange(documentHeader);

        // if new customer was created => dates have been already updated
        if (getMaintenanceAction().equalsIgnoreCase(KRADConstants.MAINTENANCE_NEW_ACTION)) {
            return;
        }

        if (documentHeader.getWorkflowDocument().isProcessed()) {
            DocumentService documentService = SpringContext.getBean(DocumentService.class);
            try {
                MaintenanceDocument document = (MaintenanceDocument) documentService.getByDocumentHeaderId(documentHeader.getDocumentNumber());
                Customer newCustomer = (Customer) document.getNewMaintainableObject().getBusinessObject();
                Customer oldCustomer = (Customer) document.getOldMaintainableObject().getBusinessObject();
                updateDates(oldCustomer, newCustomer);
            } catch (WorkflowException e) {
                LOG.error("caught exception while handling handleRouteStatusChange -> documentService.getByDocumentHeaderId(" + documentHeader.getDocumentNumber() + "). ", e);

            }
        }
    }

    // Update dates (last activity date and address change date)
    private void updateDates(Customer oldCustomer, Customer newCustomer) {
        Date currentDate = getDateTimeService().getCurrentSqlDate();
        List oldAddresses = oldCustomer.getCustomerAddresses();
        List newAddresses = newCustomer.getCustomerAddresses();
        boolean addressChangeFlag = false;

        // if new address was added or one of the old addresses was changed/deleted, set customerAddressChangeDate to the current date
        if (oldAddresses != null && newAddresses != null) {
            if (oldAddresses.size() != newAddresses.size()) {
                newCustomer.setCustomerAddressChangeDate(currentDate);
                newCustomer.setCustomerLastActivityDate(currentDate);
                addressChangeFlag = true;
            }
            else {
                for (int i = 0; i < oldAddresses.size(); i++) {
                    CustomerAddress oldAddress = (CustomerAddress) oldAddresses.get(i);
                    CustomerAddress newAddress = (CustomerAddress) newAddresses.get(i);
                    if (oldAddress.compareTo(newAddress) != 0) {
                        newCustomer.setCustomerAddressChangeDate(currentDate);
                        newCustomer.setCustomerLastActivityDate(currentDate);
                        addressChangeFlag = true;
                        break;
                    }
                }
            }
        }
        // if non address related change
        if (!addressChangeFlag && !oldCustomer.equals(newCustomer)) {
            newCustomer.setCustomerLastActivityDate(currentDate);
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

            if (KRADConstants.MAINTENANCE_NEW_ACTION.equalsIgnoreCase(getMaintenanceAction())) {

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
            if (KRADConstants.MAINTENANCE_EDIT_ACTION.equalsIgnoreCase(getMaintenanceAction()) || KRADConstants.MAINTENANCE_COPY_ACTION.equalsIgnoreCase(getMaintenanceAction())) {
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

        // editing a customer always requires approval, unless only Notes have changed
        if (maintDoc.isEdit()) {
            return !oldAndNewAreEqual(maintDoc);
        }

        // while creating a new customer, route when created by web application
        return (maintDoc.isNew() && createdByWebApp(maintDoc));
    }

    private boolean oldAndNewAreEqual(FinancialSystemMaintenanceDocument maintDoc) {
        Customer oldBo, newBo;
        CustomerAddress oldAddress, newAddress;

        oldBo = (Customer) maintDoc.getOldMaintainableObject().getBusinessObject();
        newBo = (Customer) maintDoc.getNewMaintainableObject().getBusinessObject();

        return oldAndNewObjectIsEqual("Customer", oldBo, newBo);
    }

    private boolean oldAndNewObjectIsEqual(String objectName, Object oldObject, Object newObject) {

        //  if both are null, then they're the same
        if (oldObject == null && newObject == null) {
            return true;
        }

        //  if only one is null, then they're different
        if (oldObject == null || newObject == null) {
            return false;
        }

        //  if they're different classes, then they're different
        if (!oldObject.getClass().getName().equals(newObject.getClass().getName())) {
            return false;
        }

        //  get the list of properties and unconverted values for readable props
        Map<String,Object> oldProps;
        Map<String,Object> newProps;
        try {
            oldProps = PropertyUtils.describe(oldObject);
            newProps = PropertyUtils.describe(newObject);
        }
        catch (Exception e) {
            throw new RuntimeException("Exception raised while trying to get a list of properties on OldCustomer.", e);
        }

        //  compare old to new on all readable properties
        Object oldValue, newValue;
        for (String propName : oldProps.keySet()) {
            oldValue = oldProps.get(propName);
            newValue = newProps.get(propName);

            if (!oldAndNewPropertyIsEqual(propName, oldValue, newValue)) {
                return false;
            }
        }

        //  if we didnt find any differences, then they are the same
        return true;
    }

    private boolean oldAndNewPropertyIsEqual(String propName, Object oldValue, Object newValue) {

        //  ignore anything named boNotes
        if (BO_NOTES.equalsIgnoreCase(propName)) {
            return true;
        }

        //  if both are null, then they're the same
        if (oldValue == null && newValue == null) {
            return true;
        }

        //  if only one is null, then they're different
        if (oldValue == null || newValue == null) {
            return false;
        }

        //  if they're different classes, then they're different
        if (!oldValue.getClass().getName().equals(newValue.getClass().getName())) {
            return false;
        }

        //  if they're a collection, then special handling
        if (Collection.class.isAssignableFrom(oldValue.getClass())) {
            Object[] oldCollection = ((Collection<Object>) oldValue).toArray();
            Object[] newCollection = ((Collection<Object>) newValue).toArray();

            //  if they have different numbers of addresses
            if (oldCollection.length != newCollection.length) {
                return false;
            }


            for (int i = 0; i < oldCollection.length; i++) {
                if (!oldAndNewObjectIsEqual("COLLECTION: " + propName, oldCollection[i], newCollection[i])) {
                    return false;
                }
            }
            return true;
        }
        else {
            boolean result = oldValue.toString().equals(newValue.toString());
            return result;
        }
    }

    private FinancialSystemMaintenanceDocument getParentMaintDoc() {
        //  how I wish for the ability to directly access the parent object
        DocumentService documentService = SpringContext.getBean(DocumentService.class);
        FinancialSystemMaintenanceDocument maintDoc = null;
        try {
            maintDoc =(FinancialSystemMaintenanceDocument) documentService.getByDocumentHeaderId(getDocumentNumber());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        return maintDoc;
    }

    private boolean createdByWebApp(FinancialSystemMaintenanceDocument maintDoc) {
        String initiatorPrincipalId = maintDoc.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        Principal initiatorPrincipal = KimApiServiceLocator.getIdentityService().getPrincipal(initiatorPrincipalId);
        return (initiatorPrincipal != null && !KFSConstants.SYSTEM_USER.equals(initiatorPrincipal.getPrincipalName()));
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

    /**
     * This method is called before the object is added in collection.
     * It sets the notePostedTimestamp and author for customerNote.
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#addNewLineToCollection(java.lang.String)
     */
    @Override
    public void addNewLineToCollection(String collectionName) {
        if (collectionName.equalsIgnoreCase(ArPropertyConstants.CustomerFields.CUSTOMER_NOTE_TAB)) {
            CustomerNote addLine = (CustomerNote) newCollectionLines.get(collectionName);
            addLine.setAuthorUniversalToCurrentUser();
        }
        refreshCustomer(false);
        super.addNewLineToCollection(collectionName);
    }

    private void refreshCustomer(boolean refreshFromLookup) {
        Customer customer = getCustomer();
        customer.refreshNonUpdateableReferences();

        getNewCollectionLine("customerNotes").refreshNonUpdateableReferences();

        // the org list doesn't need any refresh
        refreshNonUpdateableReferences(customer.getCustomerNotes());
    }

    private void refreshWithSecondaryKey(ARCollector arCollector) {
        Person collector = arCollector.getCollector();
        if (ObjectUtils.isNotNull(collector)) {
            String secondaryKey = collector.getPrincipalName();
            if (StringUtils.isNotBlank(secondaryKey)) {
                Person dir = SpringContext.getBean(PersonService.class).getPersonByPrincipalName(secondaryKey);
                arCollector.setPrincipalId(dir == null ? null : dir.getPrincipalId());
            }
            if (StringUtils.isNotBlank(arCollector.getPrincipalId())) {
                Person person = SpringContext.getBean(PersonService.class).getPerson(arCollector.getPrincipalId());
                if (person != null) {
                    ((PersistableBusinessObject) arCollector).refreshNonUpdateableReferences();
                }
            }
        }
    }

    /**
     * Gets the underlying Customer.
     *
     * @return
     */
    public Customer getCustomer() {
        return (Customer) getBusinessObject();
    }

    /**
     * @param collection
     */
    private static void refreshNonUpdateableReferences(Collection<? extends PersistableBusinessObject> collection) {
        for (PersistableBusinessObject item : collection) {
            item.refreshNonUpdateableReferences();
        }
    }

    @Override
    public void processAfterRetrieve() {
        refreshCustomer(false);
        super.processAfterRetrieve();
    }

    /**
     *
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
     */
    @Override
    public void saveBusinessObject() {
        super.saveBusinessObject();
        CustomerCollectorService customerCollectorService = SpringContext.getBean(CustomerCollectorService.class);
        CustomerCollector customerCollector = getCustomer().getCustomerCollector();
        if(ObjectUtils.isNotNull(customerCollector)){
        customerCollector.setCustomerNumber(getCustomer().getCustomerNumber());
        customerCollectorService.createCustomerCollector(customerCollector);
        }
    }
}
