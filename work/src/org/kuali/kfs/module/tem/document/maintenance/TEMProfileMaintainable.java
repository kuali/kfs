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
package org.kuali.kfs.module.tem.document.maintenance;

import static org.kuali.kfs.module.tem.TemConstants.EMP_TRAVELER_TYP_CD;
import static org.kuali.kfs.module.tem.TemConstants.NONEMP_TRAVELER_TYP_CD;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerAddress;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomerType;
import org.kuali.kfs.integration.ar.AccountsReceivableModuleService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemPropertyConstants.TEMProfileProperties;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TEMProfileAccount;
import org.kuali.kfs.module.tem.datadictionary.mask.CreditCardMaskFormatter;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TEMRoleService;
import org.kuali.kfs.module.tem.service.TemProfileService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.FinancialSystemMaintainable;
import org.kuali.kfs.sys.document.FinancialSystemMaintenanceDocument;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.impl.identity.PersonImpl;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.Maintainable;
import org.kuali.rice.kns.web.ui.Field;
import org.kuali.rice.kns.web.ui.Row;
import org.kuali.rice.kns.web.ui.Section;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.bo.Note;
import org.kuali.rice.krad.bo.PersistableBusinessObject;
import org.kuali.rice.krad.datadictionary.mask.Mask;
import org.kuali.rice.krad.service.DocumentService;
import org.kuali.rice.krad.service.KRADServiceLocator;
import org.kuali.rice.krad.service.NoteService;
import org.kuali.rice.krad.service.SequenceAccessorService;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.util.ObjectUtils;

public class TEMProfileMaintainable extends FinancialSystemMaintainable {

    private static final Logger LOG = Logger.getLogger(TEMProfileMaintainable.class);

	/**
     * This will create a new profile from either a principal id or from a customer number depending on what got filled out
     *
     *
     * @see org.kuali.rice.kns.maintenance.Maintainable#setupNewFromExisting()
     */
    @Override
    public void setupNewFromExisting(MaintenanceDocument document, Map<String,String[]> parameters) {
        super.setupNewFromExisting(document, parameters);
        TravelerService travelerService = SpringContext.getBean(TravelerService.class);
        SequenceAccessorService sas = SpringContext.getBean(SequenceAccessorService.class);

        TEMProfile temProfile = (TEMProfile) super.getBusinessObject();
        Integer profileId = temProfile.getProfileId();
        if(ObjectUtils.isNull(profileId)) {
            Integer newProfileId = sas.getNextAvailableSequenceNumber(TemConstants.TEM_PROFILE_SEQ_NAME).intValue();
            temProfile.setProfileId(newProfileId);
        }

        String principalId = ((TEMProfile) super.getBusinessObject()).getPrincipalId();
        if(StringUtils.isNotBlank(principalId)) {
            //we want to set the principal
            PersonImpl person = (PersonImpl)getPersonService().getPerson(principalId);
            temProfile.setPrincipal(person);
            if(travelerService.isKimPersonEmployee(person)) {
                temProfile.setTravelerTypeCode(EMP_TRAVELER_TYP_CD);
            } else {
                temProfile.setTravelerTypeCode(NONEMP_TRAVELER_TYP_CD);
            }
        }

        String customerNumber = ((TEMProfile) super.getBusinessObject()).getCustomerNumber();
        if(StringUtils.isNotBlank(customerNumber)) {
            //we want to set the customer
            AccountsReceivableCustomer person = getAccountsReceivableModuleService().findCustomer(customerNumber);
            temProfile.setCustomer(person);
            if(travelerService.isCustomerEmployee(person)) {
                temProfile.setTravelerTypeCode(EMP_TRAVELER_TYP_CD);
            } else {
                temProfile.setTravelerTypeCode(NONEMP_TRAVELER_TYP_CD);
            }
        }

        travelerService.populateTEMProfile(temProfile);
        addAdHocFYIRecipient(document);
        if (document.isNew()) {
            if (StringUtils.isNotBlank(principalId)) {
                document.getDocumentHeader().setDocumentDescription(trimDescription(TemConstants.NEW_TEM_PROFILE_DESCRIPTION_PREFIX + temProfile.getPrincipal().getName()));
            }
            else if (StringUtils.isNotBlank(customerNumber)) {
                document.getDocumentHeader().setDocumentDescription(trimDescription(TemConstants.NEW_TEM_PROFILE_DESCRIPTION_PREFIX + temProfile.getCustomer().getCustomerName()));
            }
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getSections(org.kuali.rice.kns.document.MaintenanceDocument, org.kuali.rice.kns.maintenance.Maintainable)
     */
    @Override
    public List<Section> getSections(MaintenanceDocument document, Maintainable oldMaintainable) {
        Person user = GlobalVariables.getUserSession().getPerson();

        List<Section> sections = super.getSections(document, document.getOldMaintainableObject());
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        TEMProfile temProfile = (TEMProfile) super.getBusinessObject();
        boolean profileAdmin = getTEMRoleService().isProfileAdmin(currentUser, temProfile.getHomeDepartment());

        if (!user.getPrincipalId().equals(((TEMProfile)document.getOldMaintainableObject().getBusinessObject()).getPrincipalId())){
            if (!profileAdmin) {
                // user is not the traveler or a profile admin
                for (Section section : sections){
                    if (section.getSectionId().equals(TemPropertyConstants.TEMProfileProperties.TEM_PROFILE_ADMINISTRATOR)){
                    	for (Row row : section.getRows()){
                            for (Field field : row.getFields()){
                               if (field.getContainerRows() != null && field.getContainerRows().size() > 0){
                                    for (Row containerRow :field.getContainerRows()){
                                        for (Field containerField : containerRow.getFields()){
                                            //Get only the accounts that have already been created
                                            if (containerField.getPropertyName().contains("]." + TemPropertyConstants.TEMProfileProperties.ACCOUNT_NUMBER)){
                                                String[] splitTemp = containerField.getPropertyName().split("\\[");
                                                splitTemp = splitTemp[1].split("\\]");
                                                int index = Integer.parseInt(splitTemp[0]);
                                                containerField.setSecure(true);
                                                CreditCardMaskFormatter formatter = new CreditCardMaskFormatter();
                                                Mask mask = new Mask();
                                                mask.setMaskFormatter(formatter);
                                                String display = mask.maskValue(((TEMProfile)document.getDocumentBusinessObject()).getAccounts().get(index).getAccountNumber());
                                                containerField.setDisplayMaskValue(display);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        for (Section section : sections){
            if (section.getSectionId().equals(TemPropertyConstants.TEMProfileProperties.TEM_PROFILE_ADMINISTRATOR)){
                if (!profileAdmin) {
                    section.setReadOnly(true);
                }
            }
        }

        return sections;
    }

    /**
     * Populate the TEMProfile details
     *
     * @param profile
     */
    protected void populateInfo(TEMProfile profile) {
        SpringContext.getBean(TravelerService.class).populateTEMProfile(profile);
        SpringContext.getBean(TemProfileService.class).updateACHAccountInfo(profile);
    }

    protected void maskAccountNumbers(TEMProfile profile) {
        for (TEMProfileAccount account : profile.getAccounts()){
            String accountSubStr = account.getAccountNumber().substring(account.getAccountNumber().length()-4);
            account.setAccountNumber("************"+accountSubStr);
        }
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#processAfterEdit(org.kuali.rice.kns.document.MaintenanceDocument, java.util.Map)
     */
    @Override
    public void processAfterEdit(MaintenanceDocument document, Map<String, String[]> parameters) {
        populateInfo((TEMProfile)document.getOldMaintainableObject().getBusinessObject());
        populateInfo((TEMProfile)document.getNewMaintainableObject().getBusinessObject());
        super.processAfterEdit(document, parameters);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#doRouteStatusChange(org.kuali.rice.kns.bo.DocumentHeader)
     */
    @Override
    public void doRouteStatusChange(DocumentHeader documentHeader) {
        if (documentHeader.getWorkflowDocument().isFinal()
                || documentHeader.getWorkflowDocument().isProcessed()){
            TEMProfile profile = (TEMProfile) businessObject;

            if (profile.getCustomer() == null){
                if (StringUtils.isEmpty(profile.getCustomerNumber())){
                    profile.setCustomer(getAccountsReceivableModuleService().createCustomer());
                    profile.getCustomer().setCustomerName(profile.getName());

                    String newCustNumber = getAccountsReceivableModuleService().getNextCustomerNumber(profile.getCustomer());
                    newCustNumber = newCustNumber.toUpperCase();
                    profile.setCustomerNumber(newCustNumber);
                    profile.getCustomer().setCustomerNumber(newCustNumber);

                    //Set to customer type code to travel and make the customer active
                    String customerTypeCode = "";
                    List<AccountsReceivableCustomerType> customerTypes = getAccountsReceivableModuleService().findByCustomerTypeDescription(TemConstants.CUSTOMER_TRAVLER_TYPE_CODE);
                    for (AccountsReceivableCustomerType customerType : customerTypes) {
                    	customerTypeCode = customerType.getCustomerTypeCode();
                    	break;
                    }
                    profile.getCustomer().setCustomerTypeCode(customerTypeCode);
                    profile.getCustomer().setActive(true);

                    getTravelerService().copyTEMProfileToCustomer(profile, profile.getCustomer());


                    try {
                    	//Add note to indicate a customer has been generated for this profile to the document
                    	MaintenanceDocument document = (MaintenanceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(getDocumentNumber());
                    	document.addNote(addCustomerCreatedNote(profile));

                    	//Update the document with the latest profile with the newly generated customer number
                    	document.getNewMaintainableObject().setBusinessObject(profile);

                    	//Save the document and generated customer
                    	SpringContext.getBean(DocumentService.class).saveDocument(document);
                    	getAccountsReceivableModuleService().saveCustomer(profile.getCustomer());

                    	//Refresh all the reference objects after the save
                    	List<AccountsReceivableCustomerAddress> customerAddresses = profile.getCustomer().getAccountsReceivableCustomerAddresses();
                    	for(AccountsReceivableCustomerAddress address : customerAddresses) {
                    		address.refresh();
                    	}
                    	profile.getCustomer().setAccountsReceivableCustomerAddresses(customerAddresses);

                    	profile.refresh();
                    	profile.getCustomer().refresh();
					} catch (WorkflowException ex) {
			            throw new RuntimeException(ex);
					}

                    documentHeader.setDocumentDescription(trimDescription(TemConstants.NEW_TEM_PROFILE_DESCRIPTION_PREFIX + profile.getCustomer().getCustomerName()));
                }
                else{
                    profile.setCustomer(getAccountsReceivableModuleService().findCustomer(profile.getCustomerNumber()));
                    getTravelerService().copyTEMProfileToCustomer(profile, profile.getCustomer());
                }
            }
            else{
                getTravelerService().copyTEMProfileToCustomer(profile, profile.getCustomer());
            }
        }
    }

    /**
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    protected boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.TAX_MANAGER_REQUIRED)) {
            return taxManagerRequiredRouting();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     * Overriding to return - TEMProfile doesn't need this
     *
     * @see org.kuali.kfs.sys.document.FinancialSystemMaintainable#populateChartOfAccountsCodeFields()
     */
    @Override
    protected void populateChartOfAccountsCodeFields() {
    }

    /**
     *
     * This method returns true if:<br/>
     * 1. The non-resident alien property is set<br/>
     * 2. The non-resident alien property has changed<br/>
     * 3. The citizenship property has changed<br/>
     * @return
     */
    protected boolean taxManagerRequiredRouting() {
        TEMProfile newTemProfile = (TEMProfile) getParentMaintDoc().getNewMaintainableObject().getBusinessObject();
        TEMProfile oldTemProfile = (TEMProfile) getParentMaintDoc().getOldMaintainableObject().getBusinessObject();

        // If NRA is selected, route to tax manager
        if (newTemProfile.isNonResidentAlien()) {
            return true;
        }

        // for now assuming that during initial creation of a doc, the oldTemProfile object is null
        if (ObjectUtils.isNotNull(oldTemProfile)) {
            // If NRA changed, route to tax manager
            if (newTemProfile.isNonResidentAlien() != oldTemProfile.isNonResidentAlien()) {
                return true;
            }

            // If citizenship changed, route to tax manager
            if (!StringUtils.equalsIgnoreCase(newTemProfile.getCitizenship(), oldTemProfile.getCitizenship())) {
                return true;
            }
        }

        return false;
    }

    protected FinancialSystemMaintenanceDocument getParentMaintDoc() {
        FinancialSystemMaintenanceDocument maintDoc = null;
        try {
            maintDoc = (FinancialSystemMaintenanceDocument) SpringContext.getBean(DocumentService.class).getByDocumentHeaderId(getDocumentNumber());
        }
        catch (WorkflowException e) {
            throw new RuntimeException(e);
        }
        return maintDoc;
    }

    @Override
    protected void refreshReferences(String referencesToRefresh) {
        //make call to super
        super.refreshReferences( removeReferenceFromString(referencesToRefresh, "temProfileAddress") );
    }

    /**
     * Removes a named reference from a referencesToRefresh string
     */
    protected String removeReferenceFromString(String referencesToRefresh, String referenceToRemove){
        String newReference = referencesToRefresh;

        if(ObjectUtils.isNotNull(newReference)){
            int index = newReference.indexOf(referenceToRemove);
            if(index != -1){
                //remove from beginning
                if(index == 0){

                    String suffix = "";
                    //add comma at end since there is more after this word
                    if(newReference.length() != referenceToRemove.length()){
                        suffix = ",";
                    }
                    newReference = referencesToRefresh.replaceAll("temProfileAddress" + suffix, "");

                }else{
                    //removing from middle to end... either way, comma will be in front
                    newReference = referencesToRefresh.replaceAll("," + "temProfileAddress", "");
                }
            }
        }

        return newReference;
    }

    /**
     * Add send FYI after post
     */
    protected void addAdHocFYIRecipient(MaintenanceDocument document) {
        TEMProfile profile = (TEMProfile) super.getBusinessObject();
        String initiatorUserId = document.getDocumentHeader().getWorkflowDocument().getInitiatorPrincipalId();
        if(!initiatorUserId.equals(profile.getPrincipalId())){
            SpringContext.getBean(TravelDocumentService.class).addAdHocFYIRecipient(document, profile.getPrincipalId());
        }
    }

    @Override
    public void processAfterPost(MaintenanceDocument document, Map<String, String[]> parameters) {
        super.processAfterPost(document, parameters);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public Map populateBusinessObject(Map<String, String> fieldValues, MaintenanceDocument maintenanceDocument, String methodToCall) {

        //populate maintenanceDocument with notes from the BO
        List<Note> documentNotes = maintenanceDocument.getNotes();
        if (documentNotes == null){
            documentNotes = new ArrayList<Note>();
        }else{
            documentNotes.clear();
        }

        List<Note> boNotes = new ArrayList<Note>();
        if (maintenanceDocument.getOldMaintainableObject().getBusinessObject().getObjectId() != null) {
            boNotes = getNoteService().getByRemoteObjectId(this.getBusinessObject().getObjectId());
        }
        documentNotes.addAll(boNotes);

        if(fieldValues.containsKey(TEMProfileProperties.PROFILE_ID)) {
        	fieldValues.remove(TEMProfileProperties.PROFILE_ID);
        }

        return super.populateBusinessObject(fieldValues, maintenanceDocument, methodToCall);
    }

    @Override
    public void prepareForSave() {
        TEMProfile temProfile = (TEMProfile) super.getBusinessObject();

        super.prepareForSave();
    }

	/**
	 * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#saveBusinessObject()
	 */
	@Override
	public void saveBusinessObject() {
        TEMProfile temProfile = (TEMProfile) super.getBusinessObject();

        super.saveBusinessObject();
	}

	/**
	 *
	 * This method trims the descriptionText to 40 characters.
	 * @param descriptionText
	 * @return
	 */
	protected String trimDescription(String descriptionText) {

        if (descriptionText.length() > 40) {
            descriptionText = descriptionText.substring(0, 39);
        }

        return descriptionText;
	}

	/**
	 * Reference getDocumentService.createNoteFromDocument
	 *
	 * This method creates a note on the maintenance doc indicating that a AR Customer record has been generated.
	 * @param temProfile
	 * @return
	 */
	protected Note addCustomerCreatedNote(TEMProfile temProfile) {
	    String text = "AR Customer ID " + temProfile.getCustomer().getCustomerNumber() + " has been generated";
        Note note = new Note();

        note.setNotePostedTimestamp(SpringContext.getBean(DateTimeService.class).getCurrentTimestamp());
        note.setVersionNumber(Long.valueOf(1));
        note.setNoteText(text);
        note.setNoteTypeCode(KFSConstants.NoteTypeEnum.BUSINESS_OBJECT_NOTE_TYPE.getCode());

        Person kualiUser = GlobalVariables.getUserSession().getPerson();
        note = getNoteService().createNote(note, temProfile, kualiUser.getPrincipalId());
        return note;
	}

    protected AccountsReceivableModuleService getAccountsReceivableModuleService() {

        return SpringContext.getBean(AccountsReceivableModuleService.class);
    }

    /**
     * @see org.kuali.rice.kns.maintenance.KualiMaintainableImpl#getNewCollectionLine(java.lang.String)
     */
    @Override
    public PersistableBusinessObject getNewCollectionLine( String collectionName ) {
        PersistableBusinessObject addLine = super.getNewCollectionLine(collectionName);
        if (collectionName.equals("accounts")){
            TEMProfileAccount account = (TEMProfileAccount) addLine;
            TEMProfile temProfile = (TEMProfile) super.getBusinessObject();
            account.setProfile(temProfile);
            return account;
        }

        return addLine;
    }

    public TEMRoleService getTEMRoleService(){
        return SpringContext.getBean(TEMRoleService.class);
    }

    public TravelerService getTravelerService(){
        return SpringContext.getBean(TravelerService.class);
    }

    public DocumentService getDocumentService(){
        return SpringContext.getBean(DocumentService.class);
    }

    public NoteService getNoteService(){
        return KRADServiceLocator.getNoteService();
    }
}
