/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.chart.bo;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.GlobalBusinessObject;
import org.kuali.core.bo.GlobalBusinessObjectDetail;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.bo.PersistableBusinessObjectBase;
import org.kuali.core.exceptions.BusinessObjectNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.PersistenceStructureService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.core.util.TypedArrayList;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;

/**
 * This class simply acts as a container to hold the List of Delegate Changes and the list of Account entries, for the Global
 * Delegate Change Document.
 */
public class DelegateGlobal extends PersistableBusinessObjectBase implements GlobalBusinessObject {

    private String documentNumber;

    private String modelName;
    private String modelChartOfAccountsCode;
    private String modelOrganizationCode;

    private OrganizationRoutingModelName model;

    List<AccountGlobalDetail> accountGlobalDetails;
    List<DelegateGlobalDetail> delegateGlobals;

    /**
     * Constructs a DelegateGlobal.java.
     */
    public DelegateGlobal() {
        super();
        accountGlobalDetails = new TypedArrayList(AccountGlobalDetail.class);
        delegateGlobals = new TypedArrayList(DelegateGlobalDetail.class);
    }

    /**
     * This method adds a single AccountGlobalDetail instance to the list. If one is already present in the list with the same
     * chartCode and accountNumber, then this new one will not be added.
     * 
     * @param accountGlobalDetail - populated AccountGlobalDetail instance
     */
    public void addAccount(AccountGlobalDetail accountGlobalDetail) {

        // validate the argument
        if (accountGlobalDetail == null) {
            throw new IllegalArgumentException("The accountGlobalDetail instanced passed in was null.");
        }
        else if (StringUtils.isBlank(accountGlobalDetail.getChartOfAccountsCode())) {
            throw new IllegalArgumentException("The chartOfAccountsCode member of the accountGlobalDetail object was not populated.");
        }
        else if (StringUtils.isBlank(accountGlobalDetail.getAccountNumber())) {
            throw new IllegalArgumentException("The accountNumber member of the accountGlobalDetail object was not populated.");
        }

        // add the object if one doesnt already exist, otherwise silently do nothing
        AccountGlobalDetail testObject = getAccount(accountGlobalDetail.getChartOfAccountsCode(), accountGlobalDetail.getAccountNumber());
        if (testObject == null) {
            this.accountGlobalDetails.add(accountGlobalDetail);
        }
    }

    /**
     * This method retrieves the specific AccountGlobalDetail object that corresponds to your requested chartCode and accountNumber
     * (or a null object if there is no match).
     * 
     * @param chartCode
     * @param accountNumber
     * @return returns the AccountGlobalDetail instance matching the chartCode & accountNumber passed in, or Null if none match
     */
    public AccountGlobalDetail getAccount(String chartCode, String accountNumber) {

        // validate the argument
        if (StringUtils.isBlank(chartCode)) {
            throw new IllegalArgumentException("The chartCode argument was null or empty.");
        }
        else if (StringUtils.isBlank(accountNumber)) {
            throw new IllegalArgumentException("The accountNumber argument was null or empty.");
        }

        // walk the list of AccountGlobalDetail objects
        for (Iterator iter = this.accountGlobalDetails.iterator(); iter.hasNext();) {
            AccountGlobalDetail accountGlobalDetail = (AccountGlobalDetail) iter.next();

            // if this one is a match, then quit
            if (chartCode.equalsIgnoreCase(accountGlobalDetail.getChartOfAccountsCode()) && accountNumber.equalsIgnoreCase(accountGlobalDetail.getAccountNumber())) {
                return accountGlobalDetail;
            }
        }

        // we return null if one is not found
        return null;
    }

    /**
     * @see org.kuali.core.document.GlobalBusinessObject#getGlobalChangesToDelete()
     */
    public List<PersistableBusinessObject> generateDeactivationsToPersist() {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);

        // retreive all the existing delegates for these accounts
        List<Delegate> bosToDeactivate = new ArrayList();
        Map<String, Object> fieldValues;
        Collection existingDelegates;
        for (AccountGlobalDetail accountDetail : getAccountGlobalDetails()) {
            fieldValues = new HashMap();
            fieldValues.put("chartOfAccountsCode", accountDetail.getChartOfAccountsCode());
            fieldValues.put("accountNumber", accountDetail.getAccountNumber());
            fieldValues.put("accountDelegateActiveIndicator", true);
            existingDelegates = boService.findMatching(Delegate.class, fieldValues);
            bosToDeactivate.addAll(existingDelegates);
        }

        // mark all the delegates as inactive
        for (Delegate accountDelegate : bosToDeactivate) {
            accountDelegate.setAccountDelegateActiveIndicator(false);
        }
        return new ArrayList<PersistableBusinessObject>(bosToDeactivate);
    }

    /**
     * @see org.kuali.core.document.GlobalBusinessObject#applyGlobalChanges(org.kuali.core.bo.BusinessObject)
     */
    @SuppressWarnings("deprecation")
    public List<PersistableBusinessObject> generateGlobalChangesToPersist() {

        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        List<Delegate> persistables = new ArrayList();

        List<DelegateGlobalDetail> changeDocuments = this.getDelegateGlobals();
        List<AccountGlobalDetail> accountDetails = this.getAccountGlobalDetails();

        for (DelegateGlobalDetail changeDocument : changeDocuments) {
            for (AccountGlobalDetail accountDetail : accountDetails) {

                Account account = (Account) boService.findByPrimaryKey(Account.class, accountDetail.getPrimaryKeys());

                // if the account doesnt exist, fail fast, as this should never happen,
                // the busines rules for this document should have caught this.
                if (account == null) {
                    throw new BusinessObjectNotFoundException("Account [" + accountDetail.getChartOfAccountsCode() + "-" + accountDetail.getAccountNumber() + "] was not present in the database. " + "This should never happen under normal circumstances, as an invalid account should have " + "been caught by the Business Rules infrastructure.");
                }

                // attempt to load the existing Delegate from the DB, if it exists. we do this to avoid
                // versionNumber conflicts if we tried to just insert a new record that already existed.
                Map pkMap = new HashMap();
                pkMap.putAll(accountDetail.getPrimaryKeys()); // chartOfAccountsCode & accountNumber
                pkMap.put("financialDocumentTypeCode", changeDocument.getFinancialDocumentTypeCode());
                pkMap.put("accountDelegateSystemId", changeDocument.getAccountDelegateUniversalId());
                Delegate delegate = (Delegate) boService.findByPrimaryKey(Delegate.class, pkMap);

                // if there is no existing Delegate with these primary keys, then we're creating a new one,
                // so lets populate it with the primary keys
                if (delegate == null) {
                    delegate = new Delegate();
                    delegate.setChartOfAccountsCode(accountDetail.getChartOfAccountsCode());
                    delegate.setAccountNumber(accountDetail.getAccountNumber());
                    delegate.setAccountDelegateSystemId(changeDocument.getAccountDelegateUniversalId());
                    delegate.setFinancialDocumentTypeCode(changeDocument.getFinancialDocumentTypeCode());
                    delegate.setAccountDelegateActiveIndicator(true);
                }
                else {
                    delegate.setAccountDelegateActiveIndicator(true);
                }

                // APPROVAL FROM AMOUNT
                if (changeDocument.getApprovalFromThisAmount() != null) {
                    if (!changeDocument.getApprovalFromThisAmount().equals(KualiDecimal.ZERO)) {
                        delegate.setFinDocApprovalFromThisAmt(changeDocument.getApprovalFromThisAmount());
                    }
                }

                // APPROVAL TO AMOUNT
                if (changeDocument.getApprovalToThisAmount() != null) {
                    if (!changeDocument.getApprovalToThisAmount().equals(KualiDecimal.ZERO)) {
                        delegate.setFinDocApprovalToThisAmount(changeDocument.getApprovalToThisAmount());
                    }
                }

                // PRIMARY ROUTING
                delegate.setAccountsDelegatePrmrtIndicator(changeDocument.getAccountDelegatePrimaryRoutingIndicator());

                // START DATE
                if (changeDocument.getAccountDelegateStartDate() != null) {
                    delegate.setAccountDelegateStartDate(new Timestamp(changeDocument.getAccountDelegateStartDate().getTime()));
                }

                persistables.add(delegate);

            }
        }

        return new ArrayList<PersistableBusinessObject>(persistables);
    }

    /**
     * @see org.kuali.core.bo.BusinessObjectBase#toStringMapper()
     */
    @Override
    protected LinkedHashMap toStringMapper() {

        LinkedHashMap m = new LinkedHashMap();

        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        return m;
    }

    /**
     * @see org.kuali.core.document.GlobalBusinessObject#getDocumentNumber()
     */
    public String getDocumentNumber() {
        return documentNumber;
    }

    /**
     * @see org.kuali.core.document.GlobalBusinessObject#setDocumentNumber(java.lang.String)
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;

    }

    /**
     * Gets the accountGlobalDetails attribute.
     * 
     * @return Returns the accountGlobalDetails.
     */
    public final List<AccountGlobalDetail> getAccountGlobalDetails() {
        return accountGlobalDetails;
    }

    /**
     * Sets the accountGlobalDetails attribute value.
     * 
     * @param accountGlobalDetails The accountGlobalDetails to set.
     */
    public final void setAccountGlobalDetails(List<AccountGlobalDetail> accountGlobalDetails) {
        this.accountGlobalDetails = accountGlobalDetails;
    }

    /**
     * Gets the delegateGlobals attribute.
     * 
     * @return Returns the delegateGlobals.
     */
    public final List<DelegateGlobalDetail> getDelegateGlobals() {
        return delegateGlobals;
    }

    /**
     * Sets the delegateGlobals attribute value.
     * 
     * @param delegateGlobals The delegateGlobals to set.
     */
    public final void setDelegateGlobals(List<DelegateGlobalDetail> delegateGlobals) {
        this.delegateGlobals = delegateGlobals;
    }

    /**
     * @see org.kuali.core.document.GlobalBusinessObject#isPersistable()
     */
    public boolean isPersistable() {
        PersistenceStructureService persistenceStructureService = SpringContext.getBean(PersistenceStructureService.class);

        // fail if the PK for this object is emtpy
        if (StringUtils.isBlank(documentNumber)) {
            return false;
        }

        // fail if the PKs for any of the contained objects are empty
        for (DelegateGlobalDetail delegateGlobals : getDelegateGlobals()) {
            if (!persistenceStructureService.hasPrimaryKeyFieldValues(delegateGlobals)) {
                return false;
            }
        }
        for (AccountGlobalDetail account : getAccountGlobalDetails()) {
            if (!persistenceStructureService.hasPrimaryKeyFieldValues(account)) {
                return false;
            }
        }

        // otherwise, its all good
        return true;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String loadModelName) {
        this.modelName = loadModelName;
    }

    public String getModelChartOfAccountsCode() {
        return modelChartOfAccountsCode;
    }

    public void setModelChartOfAccountsCode(String loadModelChartOfAccountsCode) {
        this.modelChartOfAccountsCode = loadModelChartOfAccountsCode;
    }

    public String getModelOrganizationCode() {
        return modelOrganizationCode;
    }

    public void setModelOrganizationCode(String loadModelOrganizationCode) {
        this.modelOrganizationCode = loadModelOrganizationCode;
    }

    public OrganizationRoutingModelName getModel() {
        return model;
    }

    public void setModel(OrganizationRoutingModelName loadModel) {
        this.model = loadModel;
    }

    public List<? extends GlobalBusinessObjectDetail> getAllDetailObjects() {
        ArrayList<GlobalBusinessObjectDetail> details = new ArrayList<GlobalBusinessObjectDetail>(accountGlobalDetails.size() + delegateGlobals.size());
        details.addAll(accountGlobalDetails);
        details.addAll(delegateGlobals);
        return details;
    }

    @Override
    public void linkEditableUserFields() {
        super.linkEditableUserFields();
        if (this == null) {
            throw new IllegalArgumentException("globalDelegate parameter passed in was null");
        }
        List bos = new ArrayList();
        bos.addAll(getDelegateGlobals());
        SpringContext.getBean(BusinessObjectService.class).linkUserFields(bos);
    }

    /**
     * @see org.kuali.core.bo.PersistableBusinessObjectBase#buildListOfDeletionAwareLists()
     */
    @Override
    public List buildListOfDeletionAwareLists() {
        List<List> managedLists = super.buildListOfDeletionAwareLists();

        managedLists.add(getAccountGlobalDetails());
        managedLists.add(getDelegateGlobals());

        return managedLists;
    }
}
