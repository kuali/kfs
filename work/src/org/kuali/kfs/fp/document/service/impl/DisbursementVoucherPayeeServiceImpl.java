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
package org.kuali.kfs.fp.document.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.AdHocRoutePerson;
import org.kuali.core.bo.AdHocRouteRecipient;
import org.kuali.core.bo.Note;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.DataDictionaryService;
import org.kuali.core.service.DocumentService;
import org.kuali.core.util.ObjectUtils;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.FinancialSystemUser;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.FinancialSystemUserService;
import org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService;

import edu.iu.uis.eden.EdenConstants;
import edu.iu.uis.eden.exception.WorkflowException;

/**
 * This class...
 */
public class DisbursementVoucherPayeeServiceImpl implements DisbursementVoucherPayeeService {

    private org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherPayeeServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private DataDictionaryService dataDictionaryService;
    private DocumentService documentService;
    
    /**
     * @see org.kuali.kfs.fp.document.service.DisbursementVoucherPayeeService#checkPayeeAddressForChanges(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    public void checkPayeeAddressForChanges(DisbursementVoucherDocument dvDoc) {
        Map<String, String> pks = new HashMap<String, String>();
        pks.put("documentNumber", dvDoc.getDocumentNumber());
        
        DisbursementVoucherDocument savedDv = (DisbursementVoucherDocument) businessObjectService.findByPrimaryKey(DisbursementVoucherDocument.class, pks);
        DisbursementVoucherPayeeDetail newPayeeDetail = dvDoc.getDvPayeeDetail();
        DisbursementVoucherPayeeDetail oldPayeeDetail = savedDv.getDvPayeeDetail();
        
        if(ObjectUtils.isNotNull(oldPayeeDetail) && ObjectUtils.isNotNull(newPayeeDetail)) {
            if(!oldPayeeDetail.hasSameAddress(newPayeeDetail)) {// Addresses don't match, so let's start the recording of changes
                
                // Put a note on the document to record the change to the address
                try {
                    String noteText = buildPayeeChangedNoteText(newPayeeDetail, oldPayeeDetail);
                    
                    int noteMaxSize = dataDictionaryService.getAttributeMaxLength("Note", "noteText");
                    
                    // Break up the note into multiple pieces if the note is too large to fit in the database field.
                    while(noteText.length()>noteMaxSize) {
                        int fromIndex = 0;
                        fromIndex = noteText.lastIndexOf(';', noteMaxSize);
                        
                        String noteText1 = noteText.substring(0, fromIndex);
                        Note note1 = documentService.createNoteFromDocument(dvDoc, noteText1);
                        documentService.addNoteToDocument(dvDoc, note1);
                        noteText = noteText.substring(fromIndex);
                    }
                    
                    Note note = documentService.createNoteFromDocument(dvDoc, noteText);
                    documentService.addNoteToDocument(dvDoc, note);
                } catch (Exception e) {
                    LOG.error("Exception while attempting to create or add note: "+e);
                }
                
                // Send out FYIs to all previous approvers so they're aware of the changes to the address
                try {
                    Set<UniversalUser> priorApprovers = dvDoc.getDocumentHeader().getWorkflowDocument().getAllPriorApprovers();
                    String initiatorUserId = dvDoc.getDocumentHeader().getWorkflowDocument().getRouteHeader().getInitiator().getUuId();
                    FinancialSystemUser finSysUser = SpringContext.getBean(FinancialSystemUserService.class).getFinancialSystemUser(initiatorUserId);
                    setupFYIs(dvDoc, priorApprovers, finSysUser.getPersonUserIdentifier());
                } catch(WorkflowException we) {
                    LOG.error("Exception while attempting to retrieve all prior approvers from workflow: "+we);
                } catch(UserNotFoundException unfe) {
                    LOG.error("Exception while attempting to retrieve all prior approvers for a disbursement voucher: "+unfe);
                }
            }
        }
    }

    /**
     * 
     * This method...
     * @param newPayeeDetail
     * @param oldPayeeDetail
     * @return
     */
    private String buildPayeeChangedNoteText(DisbursementVoucherPayeeDetail newPayeeDetail, DisbursementVoucherPayeeDetail oldPayeeDetail) {
        StringBuilder noteText = new StringBuilder();
        String valueLabel = "";
        try {
            noteText.append("The following changes were made to the payee address: ");

            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_LINE1_ADDR);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeLine1Addr(), newPayeeDetail.getDisbVchrPayeeLine1Addr()));
            
            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_LINE2_ADDR);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeLine2Addr(), newPayeeDetail.getDisbVchrPayeeLine2Addr()));
            
            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_CITY_NAME);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeCityName(), newPayeeDetail.getDisbVchrPayeeCityName()));
            
            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_STATE_CODE);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeStateCode(), newPayeeDetail.getDisbVchrPayeeStateCode()));
            
            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_ZIP_CODE);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeZipCode(), newPayeeDetail.getDisbVchrPayeeZipCode()));
            
            valueLabel = dataDictionaryService.getAttributeLabel(DisbursementVoucherPayeeDetail.class, KFSPropertyConstants.DISB_VCHR_PAYEE_COUNTRY_CODE);
            noteText.append(buildAddressValueDifferenceText(valueLabel, oldPayeeDetail.getDisbVchrPayeeCountryCode(), newPayeeDetail.getDisbVchrPayeeCountryCode()));
        } catch(Exception ex) {
            LOG.error("Error while attempting to build out note text for payee address change note: "+ex);
        }
        
        return noteText.toString();
    }
    
    /**
     * 
     * This method...
     * @param valueName
     * @param oldValue
     * @param newValue
     * @return
     */
    private String buildAddressValueDifferenceText(String valueName, String oldValue, String newValue) {
        // Nothing to log if values are still the same
        if(StringUtils.equals(oldValue, newValue)) {
            return "";
        }
        
        StringBuilder text = new StringBuilder();
        
        text.append(valueName).append(" was changed from ");
        text.append(oldValue==null?"(no value entered)":oldValue).append(" to ");
        text.append(newValue).append("; ");
        
        return text.toString();
    }
    
    
    /**
     * 
     * This method...
     * @param dvDoc
     * @param priorApprovers
     */
    private void setupFYIs(DisbursementVoucherDocument dvDoc, Set<UniversalUser> priorApprovers, String initiatorUserId) {
        List<AdHocRouteRecipient> adHocRoutePersons = dvDoc.getAdHocRoutePersons();

        // Add FYI for each approver who has already approved the document
        for(UniversalUser approver : priorApprovers) {
            String approverPersonUserId = approver.getPersonUserIdentifier();
            adHocRoutePersons.add(buildFyiRecipient(approverPersonUserId));
        }

        // Add FYI for initiator
        adHocRoutePersons.add(buildFyiRecipient(initiatorUserId));
    }

    /**
     * 
     * This method...
     * @param userId
     * @return
     */
    private AdHocRouteRecipient buildFyiRecipient(String userId) {
        AdHocRouteRecipient adHocRouteRecipient = new AdHocRoutePerson();
        adHocRouteRecipient.setActionRequested(EdenConstants.ACTION_REQUEST_FYI_REQ);
        adHocRouteRecipient.setId(userId);
        return adHocRouteRecipient;
    }
    
    // HELPER SERVICE METHODS
    
    /**
     * Gets the businessObjectService attribute. 
     * @return Returns the businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    /**
     * Sets the documentService attribute value.
     * @param documentService The documentService to set.
     */
    public void setDocumentService(DocumentService documentService) {
        this.documentService = documentService;
    }

    /**
     * Gets the documentService attribute. 
     * @return Returns the documentService.
     */
    public DocumentService getDocumentService() {
        return documentService;
    }

    /**
     * Gets the dataDictionaryService attribute. 
     * @return Returns the dataDictionaryService.
     */
    public DataDictionaryService getDataDictionaryService() {
        return dataDictionaryService;
    }

    /**
     * Sets the dataDictionaryService attribute value.
     * @param dataDictionaryService The dataDictionaryService to set.
     */
    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }

}
