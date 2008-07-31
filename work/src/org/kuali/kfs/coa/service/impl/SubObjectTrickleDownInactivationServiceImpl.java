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
package org.kuali.kfs.coa.service.impl;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.dao.MaintenanceDocumentDao;
import org.kuali.core.document.MaintenanceDocument;
import org.kuali.core.document.MaintenanceLock;
import org.kuali.core.maintenance.Maintainable;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DocumentHeaderService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.MaintenanceDocumentDictionaryService;
import org.kuali.core.service.NoteService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.businessobject.SubObjCd;
import org.kuali.kfs.coa.service.SubObjectTrickleDownInactivationService;
import org.kuali.kfs.sys.KFSKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.KNSServiceLocator;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

@Transactional
public class SubObjectTrickleDownInactivationServiceImpl implements SubObjectTrickleDownInactivationService {
    private static final Logger LOG = Logger.getLogger(SubObjectTrickleDownInactivationServiceImpl.class);
    
    protected BusinessObjectService businessObjectService;
    protected MaintenanceDocumentDictionaryService maintenanceDocumentDictionaryService;
    protected MaintenanceDocumentDao maintenanceDocumentDao;
    protected NoteService noteService;
    protected KualiConfigurationService kualiConfigurationService;
    protected UniversityDateService universityDateService;
    protected DocumentHeaderService documentHeaderService;
    
    public List<MaintenanceLock> generateTrickleDownMaintenanceLocks(Account inactivatedAccount, String documentNumber) {
        Collection<SubObjCd> subObjects = getAssociatedSubObjects(inactivatedAccount);
        List<MaintenanceLock> maintenanceLocks = generateTrickleDownMaintenanceLocks(subObjects, documentNumber);
        return maintenanceLocks;
    }

    public List<MaintenanceLock> generateTrickleDownMaintenanceLocks(ObjectCode inactivatedObjectCode, String documentNumber) {
        Collection<SubObjCd> subObjects = getAssociatedSubObjects(inactivatedObjectCode);
        List<MaintenanceLock> maintenanceLocks = generateTrickleDownMaintenanceLocks(subObjects, documentNumber);
        return maintenanceLocks;
    }

    public List<MaintenanceLock> generateTrickleDownMaintenanceLocks(Collection<SubObjCd> subObjects, String documentNumber) {
        Maintainable subObjectMaintainable = getSubObjectMaintainable(documentNumber);
        List<MaintenanceLock> maintenanceLocks = new ArrayList<MaintenanceLock>();
        for (SubObjCd subObjCd : subObjects) {
            subObjectMaintainable.setBusinessObject(subObjCd);
            maintenanceLocks.addAll(subObjectMaintainable.generateMaintenanceLocks());
        }
        return maintenanceLocks;
    }
    
    protected class TrickleDownInactivationStatus {
        public List<SubObjCd> inactivatedSubObjCds;
        public Map<SubObjCd, String> alreadyLockedSubObjCds;
        public List<SubObjCd> errorPersistingSubObjCds;
        
        public TrickleDownInactivationStatus() {
            inactivatedSubObjCds = new ArrayList<SubObjCd>();
            alreadyLockedSubObjCds = new HashMap<SubObjCd, String>();
            errorPersistingSubObjCds = new ArrayList<SubObjCd>();
        }
    }
    
    public void trickleDownInactivateSubObjects(Account inactivatedAccount, String documentNumber) {
        Collection<SubObjCd> subObjects = getAssociatedSubObjects(inactivatedAccount);
        TrickleDownInactivationStatus trickleDownInactivationStatus = trickleDownInactivate(subObjects, documentNumber);
        addNotesToDocument(trickleDownInactivationStatus, documentNumber);
    }

    public void trickleDownInactivateSubObjects(ObjectCode inactivatedObject, String documentNumber) {
        Collection<SubObjCd> subObjects = getAssociatedSubObjects(inactivatedObject);
        TrickleDownInactivationStatus trickleDownInactivationStatus = trickleDownInactivate(subObjects, documentNumber);
        addNotesToDocument(trickleDownInactivationStatus, documentNumber);
    }

    protected TrickleDownInactivationStatus trickleDownInactivate(Collection<SubObjCd> subObjects, String documentNumber) {
        TrickleDownInactivationStatus trickleDownInactivationStatus = new TrickleDownInactivationStatus();
        
        if (subObjects != null && !subObjects.isEmpty()) {
            Maintainable subObjectMaintainable = getSubObjectMaintainable(documentNumber);
            for (Iterator<SubObjCd> i = subObjects.iterator(); i.hasNext(); ) {
                SubObjCd subObjCd = i.next();
                if (subObjCd.isFinancialSubObjectActiveIndicator()) {
                    subObjectMaintainable.setBusinessObject(subObjCd);
                    List<MaintenanceLock> subAccountLocks = subObjectMaintainable.generateMaintenanceLocks();
                    
                    MaintenanceLock failedLock = verifyAllLocksFromThisDocument(subAccountLocks, documentNumber);
                    if (failedLock != null) {
                        // another document has locked this sub account, so we don't try to inactivate the account
                        trickleDownInactivationStatus.alreadyLockedSubObjCds.put(subObjCd, failedLock.getDocumentNumber());
                    }
                    else {
                        // no locks other than our own (but there may have been no locks at all), just go ahead and try to update
                        subObjCd.setFinancialSubObjectActiveIndicator(false);
                        
                        try {
                            subObjectMaintainable.saveBusinessObject();
                            trickleDownInactivationStatus.inactivatedSubObjCds.add(subObjCd);
                        }
                        catch (RuntimeException e) {
                            LOG.error("Unable to trickle-down inactivate sub-account " + subObjCd.toString(), e);
                            trickleDownInactivationStatus.errorPersistingSubObjCds.add(subObjCd);
                        }
                    }
                }
            }
        }
        
        return trickleDownInactivationStatus;
    }
    
    protected void addNotesToDocument(TrickleDownInactivationStatus trickleDownInactivationStatus, String documentNumber) {
        if (trickleDownInactivationStatus.inactivatedSubObjCds.isEmpty() && trickleDownInactivationStatus.alreadyLockedSubObjCds.isEmpty() && trickleDownInactivationStatus.errorPersistingSubObjCds.isEmpty()) {
            // if we didn't try to inactivate any sub-objects, then don't bother
            return;
        }
        DocumentHeader noteParent = documentHeaderService.getDocumentHeaderById(documentNumber);
        Note newNote = new Note();
        
        addNotes(documentNumber, trickleDownInactivationStatus.inactivatedSubObjCds, KFSKeyConstants.SUB_OBJECT_TRICKLE_DOWN_INACTIVATION, noteParent, newNote);
        addNotes(documentNumber, trickleDownInactivationStatus.errorPersistingSubObjCds, KFSKeyConstants.SUB_OBJECT_TRICKLE_DOWN_INACTIVATION_ERROR_DURING_PERSISTENCE, noteParent, newNote);
        addMaintenanceLockedNotes(documentNumber, trickleDownInactivationStatus.alreadyLockedSubObjCds, KFSKeyConstants.SUB_OBJECT_TRICKLE_DOWN_INACTIVATION_RECORD_ALREADY_MAINTENANCE_LOCKED, noteParent, newNote);
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
    
    protected Maintainable getSubObjectMaintainable(String documentNumber) {
        Maintainable subObjectMaintainable;
        try {
            subObjectMaintainable = (Maintainable) maintenanceDocumentDictionaryService.getMaintainableClass(SubObjCd.class.getName()).newInstance();
            subObjectMaintainable.setBoClass(SubObjCd.class);
            subObjectMaintainable.setDocumentNumber(documentNumber);
        }
        catch (Exception e) {
            LOG.error("Unable to instantiate SubObject Maintainable" , e);
            throw new RuntimeException("Unable to instantiate SubObject Maintainable" , e);
        }
        return subObjectMaintainable;
    }
    
    protected Collection<SubObjCd> getAssociatedSubObjects(Account account) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityDateService.getCurrentFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, account.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.ACCOUNT_NUMBER, account.getAccountNumber());
        return businessObjectService.findMatching(SubObjCd.class, fieldValues);
    }
    
    protected Collection<SubObjCd> getAssociatedSubObjects(ObjectCode objectCode) {
        Map<String, Object> fieldValues = new HashMap<String, Object>();
        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, objectCode.getUniversityFiscalYear());
        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, objectCode.getChartOfAccountsCode());
        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, objectCode.getFinancialObjectCode());
        return businessObjectService.findMatching(SubObjCd.class, fieldValues);
    }

    protected void addNotes(String documentNumber, List<SubObjCd> listOfSubObjects, String messageKey, PersistableBusinessObject noteParent, Note noteTemplate) {
        for (int i = 0; i < listOfSubObjects.size(); i += getNumSubObjectsPerNote()) {
            try {
                String subAccountString = createSubObjectChunk(listOfSubObjects, i, i + getNumSubObjectsPerNote());
                if (StringUtils.isNotBlank(subAccountString)) {
                    String noteTextTemplate = kualiConfigurationService.getPropertyString(messageKey);
                    String noteText = MessageFormat.format(noteTextTemplate, subAccountString);
                    Note note = noteService.createNote(noteTemplate, noteParent);
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
    
    protected void addMaintenanceLockedNotes(String documentNumber, Map<SubObjCd, String> lockedSubObjects, String messageKey, PersistableBusinessObject noteParent, Note noteTemplate) {
        for (Map.Entry<SubObjCd, String> entry : lockedSubObjects.entrySet()) {
            try {
                SubObjCd subObjCd = entry.getKey();
                String subObjectString = subObjCd.getUniversityFiscalYear() + " - " + subObjCd.getChartOfAccountsCode() + " - " + subObjCd.getAccountNumber() + " - " + subObjCd.getFinancialObjectCode() + " - " + subObjCd.getFinancialSubObjectCode();
                if (StringUtils.isNotBlank(subObjectString)) {
                    String noteTextTemplate = kualiConfigurationService.getPropertyString(messageKey);
                    String noteText = MessageFormat.format(noteTextTemplate, subObjectString, entry.getValue());
                    Note note = noteService.createNote(noteTemplate, noteParent);
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
    
    protected String createSubObjectChunk(List<SubObjCd> listOfSubObjects, int startIndex, int endIndex) {
        StringBuilder buf = new StringBuilder(); 
        for (int i = startIndex; i < endIndex && i < listOfSubObjects.size(); i++) {
            SubObjCd subObjCd = listOfSubObjects.get(i);
            buf.append(subObjCd.getUniversityFiscalYear()).append(" - ").append(subObjCd.getChartOfAccountsCode()).append(" - ")
                    .append(subObjCd.getAccountNumber()).append(" - ").append(subObjCd.getFinancialObjectCode())
                    .append(" - ").append(subObjCd.getFinancialSubObjectCode());
            if (i + 1 < endIndex && i + 1 < listOfSubObjects.size()) {
                buf.append(", ");
            }
        }
        return buf.toString();
    }
    
    protected int getNumSubObjectsPerNote() {
        return 20;
    }
    
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
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

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setUniversityDateService(UniversityDateService universityDateService) {
        this.universityDateService = universityDateService;
    }

    public void setDocumentHeaderService(DocumentHeaderService documentHeaderService) {
        this.documentHeaderService = documentHeaderService;
    }
}
