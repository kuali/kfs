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
package org.kuali.kfs.coa.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.SubAccount;
import org.kuali.kfs.coa.service.SubAccountTrickleDownInactivationService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.service.MaintenanceDocumentDictionaryService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.dao.MaintenanceDocumentDao;
import org.kuali.rice.krad.maintenance.MaintenanceLock;
import org.kuali.rice.krad.service.DocumentHeaderService;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class SubAccountTrickleDownInactivationServiceImpl implements SubAccountTrickleDownInactivationService {
    private static final Logger LOG = Logger.getLogger(SubAccountTrickleDownInactivationServiceImpl.class);

    protected MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    protected MaintenanceDocumentDao maintenanceDocumentDao;
    protected NoteService noteService;
    protected ConfigurationService kualiConfigurationService;
    protected DocumentHeaderService documentHeaderService;
    
    /**
     * Will generate Maintenance Locks for all (active or not) sub-accounts in the system related to the inactivated account using the sub-account
     * maintainable registered for the sub-account maintenance document
     * 
     * This version of the method assumes that the sub-account maintainable only requires that the SubAccount BOClass, document number, and SubAccount
     * instance only needs to be passed into it
     * @see org.kuali.kfs.gl.service.SubAccountTrickleDownInactivationService#generateTrickleDownMaintenanceLocks(org.kuali.kfs.coa.businessobject.Account, java.lang.String)
     */
    public List<MaintenanceLock> generateTrickleDownMaintenanceLocks(Account inactivatedAccount, String documentNumber) {
        inactivatedAccount.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNTS);
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        
        Maintainable subAccountMaintainable;
        try {
            subAccountMaintainable = (Maintainable) maintenanceDocumentDictionaryService.getMaintainableClass(SubAccount.class.getName()).newInstance();
            subAccountMaintainable.setBoClass(SubAccount.class);
            subAccountMaintainable.setDocumentNumber(documentNumber);
        }
        catch (Exception e) {
            LOG.error("Unable to instantiate SubAccount Maintainable" , e);
            throw new RuntimeException("Unable to instantiate SubAccount Maintainable" , e);
        }
        
        if (ObjectUtils.isNotNull(inactivatedAccount.getSubAccounts()) && !inactivatedAccount.getSubAccounts().isEmpty()) {
            for (Iterator<SubAccount> i = inactivatedAccount.getSubAccounts().iterator(); i.hasNext(); ) {
                SubAccount subAccount = i.next();
                
                subAccountMaintainable.setBusinessObject(subAccount);
                maintenanceLocks.addAll(subAccountMaintainable.generateMaintenanceLocks());
            }
        }
        return maintenanceLocks;
    }
    
    public void trickleDownInactivateSubAccounts(Account inactivatedAccount, String documentNumber) {
        List<SubAccount> inactivatedSubAccounts = new ArrayList<SubAccount>();
        Map<SubAccount, String> alreadyLockedSubAccounts = new HashMap<SubAccount, String>();
        List<SubAccount> errorPersistingSubAccounts = new ArrayList<SubAccount>();
        
        Maintainable subAccountMaintainable;
        try {
            subAccountMaintainable = (Maintainable) maintenanceDocumentDictionaryService.getMaintainableClass(SubAccount.class.getName()).newInstance();
            subAccountMaintainable.setBoClass(SubAccount.class);
            subAccountMaintainable.setDocumentNumber(documentNumber);
        }
        catch (Exception e) {
            LOG.error("Unable to instantiate SubAccount Maintainable" , e);
            throw new RuntimeException("Unable to instantiate SubAccount Maintainable" , e);
        }
        
        inactivatedAccount.refreshReferenceObject(KFSPropertyConstants.SUB_ACCOUNTS);
        if (ObjectUtils.isNotNull(inactivatedAccount.getSubAccounts()) && !inactivatedAccount.getSubAccounts().isEmpty()) {
            for (Iterator<SubAccount> i = inactivatedAccount.getSubAccounts().iterator(); i.hasNext(); ) {
                SubAccount subAccount = i.next();
                if (subAccount.isActive()) {
                    subAccountMaintainable.setBusinessObject(subAccount);
                    List<MaintenanceLock> subAccountLocks = subAccountMaintainable.generateMaintenanceLocks();
                    
                    MaintenanceLock failedLock = verifyAllLocksFromThisDocument(subAccountLocks, documentNumber);
                    if (failedLock != null) {
                        // another document has locked this sub account, so we don't try to inactivate the account
                        alreadyLockedSubAccounts.put(subAccount, failedLock.getDocumentNumber());
                    }
                    else {
                        // no locks other than our own (but there may have been no locks at all), just go ahead and try to update
                        subAccount.setActive(false);
                        
                        try {
                            subAccountMaintainable.saveBusinessObject();
                            inactivatedSubAccounts.add(subAccount);
                        }
                        catch (RuntimeException e) {
                            LOG.error("Unable to trickle-down inactivate sub-account " + subAccount.toString(), e);
                            errorPersistingSubAccounts.add(subAccount);
                        }
                    }
                }
            }
            
            addNotesToDocument(documentNumber, inactivatedSubAccounts, alreadyLockedSubAccounts, errorPersistingSubAccounts);
        }
    }

    protected void addNotesToDocument(String documentNumber, List<SubAccount> inactivatedSubAccounts, Map<SubAccount, String> alreadyLockedSubAccounts, List<SubAccount> errorPersistingSubAccounts) {
        if (inactivatedSubAccounts.isEmpty() && alreadyLockedSubAccounts.isEmpty() && errorPersistingSubAccounts.isEmpty()) {
            // if we didn't try to inactivate any sub-accounts, then don't bother
            return;
        }
        DocumentHeader noteParent = documentHeaderService.getDocumentHeaderById(documentNumber);
        Note newNote = new Note();
        
        addNotes(documentNumber, inactivatedSubAccounts, KFSKeyConstants.SUB_ACCOUNT_TRICKLE_DOWN_INACTIVATION, noteParent, newNote);
        addNotes(documentNumber, errorPersistingSubAccounts, KFSKeyConstants.SUB_ACCOUNT_TRICKLE_DOWN_INACTIVATION_ERROR_DURING_PERSISTENCE, noteParent, newNote);
        addMaintenanceLockedNotes(documentNumber, alreadyLockedSubAccounts, KFSKeyConstants.SUB_ACCOUNT_TRICKLE_DOWN_INACTIVATION_RECORD_ALREADY_MAINTENANCE_LOCKED, noteParent, newNote);
    }
    
    protected void addMaintenanceLockedNotes(String documentNumber, Map<SubAccount, String> lockedSubAccounts, String messageKey, PersistableBusinessObject noteParent, Note noteTemplate) {
        for (Map.Entry<SubAccount, String> entry : lockedSubAccounts.entrySet()) {
            try {
                SubAccount subAccount = entry.getKey();
                String subAccountString = subAccount.getChartOfAccountsCode() + " - " + subAccount.getAccountNumber() + " - " + subAccount.getSubAccountNumber();
                if (StringUtils.isNotBlank(subAccountString)) {
                    String noteTextTemplate = kualiConfigurationService.getPropertyValueAsString(messageKey);
                    String noteText = MessageFormat.format(noteTextTemplate, subAccountString, entry.getValue());
                    Note note = noteService.createNote(noteTemplate, noteParent, GlobalVariables.getUserSession().getPrincipalId());
                    note.setNoteText(noteText);
                    noteService.save(note);
                }
            }
            catch (Exception e) {
                LOG.error("Unable to create/save notes for document " + documentNumber, e);
                throw new RuntimeException("Unable to create/save notes for document " + documentNumber, e);
            }
        }
    }

    protected void addNotes(String documentNumber, List<SubAccount> listOfSubAccounts, String messageKey, PersistableBusinessObject noteParent, Note noteTemplate) {
        for (int i = 0; i < listOfSubAccounts.size(); i += getNumSubAccountsPerNote()) {
            try {
                String subAccountString = createSubAccountChunk(listOfSubAccounts, i, i + getNumSubAccountsPerNote());
                if (StringUtils.isNotBlank(subAccountString)) {
                    String noteTextTemplate = kualiConfigurationService.getPropertyValueAsString(messageKey);
                    String noteText = MessageFormat.format(noteTextTemplate, subAccountString);
                    Note note = noteService.createNote(noteTemplate, noteParent, GlobalVariables.getUserSession().getPrincipalId());
                    note.setNoteText(noteText);
                    note.setNotePostedTimestampToCurrent();
                    noteService.save(note);
                }
            }
            catch (Exception e) {
                LOG.error("Unable to create/save notes for document " + documentNumber, e);
                throw new RuntimeException("Unable to create/save notes for document " + documentNumber, e);
            }
        }
    }
    
    protected String createSubAccountChunk(List<SubAccount> listOfSubAccounts, int startIndex, int endIndex) {
        StringBuilder buf = new StringBuilder(); 
        for (int i = startIndex; i < endIndex && i < listOfSubAccounts.size(); i++) {
            SubAccount subAccount = listOfSubAccounts.get(i);
            buf.append(subAccount.getChartOfAccountsCode()).append(" - ").append(subAccount.getAccountNumber()).append(" - ")
                    .append(subAccount.getSubAccountNumber());
            if (i + 1 < endIndex && i + 1 < listOfSubAccounts.size()) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }
    
    protected int getNumSubAccountsPerNote() {
        return 20;
    }
    
    protected MaintenanceLock verifyAllLocksFromThisDocument(List<MaintenanceLock> maintenanceLocks, String documentNumber) {
        for (MaintenanceLock maintenanceLock : maintenanceLocks) {
            String lockingDocNumber = maintenanceDocumentDao.getLockingDocumentNumber(maintenanceLock.getLockingRepresentation(), documentNumber);
            if (StringUtils.isNotBlank(lockingDocNumber)) {
                return maintenanceLock;
            }
        }
        return null;
    }

    public void setMaintenanceDocumentDictionaryService(MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService) {
        this.maintenanceDocumentDictionaryService = maintenanceDocumentDictionaryService;
    }



    public void setMaintenanceDocumentDao(MaintenanceDocumentDao maintenanceDocumentDao) {
        this.maintenanceDocumentDao = maintenanceDocumentDao;
    }



    public void setNoteService(NoteService noteService) {
        this.noteService = noteService;
    }

    public void setConfigurationService(ConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setDocumentHeaderService(DocumentHeaderService documentHeaderService) {
        this.documentHeaderService = documentHeaderService;
    }
}
