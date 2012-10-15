/*
 * Copyright 2012 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.document.CardApplicationDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.web.struts.FinancialSystemTransactionalDocumentFormBase;
import org.kuali.rice.kew.dto.RouteHeaderDTO;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.KualiConfigurationService;
import org.kuali.rice.kns.util.GlobalVariables;
import org.kuali.rice.kns.util.KNSConstants;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.kns.web.ui.HeaderField;
import org.kuali.rice.kns.workflow.service.KualiWorkflowDocument;


public class TemCardApplicationForm extends FinancialSystemTransactionalDocumentFormBase {
    KualiConfigurationService kualiConfigurationService;
    private boolean fiscalOfficer           = false;
    private boolean travelManager           = false;
    private boolean appliedToBank           = false;
    private boolean initiator               = false;
    private boolean emptyAccount            = false;
    private boolean emptyProfile            = false;
    private boolean multipleApplications    = false;
    
    @Override
    public List<ExtraButton> getExtraButtons() {
        List<ExtraButton> extraButtons = super.getExtraButtons();
        
        if (isTravelManager()){
            RouteHeaderDTO routeHeader = this.getDocument().getDocumentHeader().getWorkflowDocument().getRouteHeader();
            if (routeHeader.getAppDocStatus().equals(TemWorkflowConstants.RouteNodeNames.APPLY_TO_BANK)
                    || routeHeader.getAppDocStatus().equals(TemWorkflowConstants.RouteNodeNames.TRAVEL_OFFICE_REVIEW)){
                this.getDocumentActions().remove(KNSConstants.KUALI_ACTION_CAN_APPROVE);
            }
            String applyToBankButtonURL = getKualiConfigurationService().getPropertyString(KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY);
            String submitButtonURL = getKualiConfigurationService().getPropertyString(KFSConstants.RICE_EXTERNALIZABLE_IMAGES_URL_KEY);
            
            if (routeHeader.getAppDocStatus().equals(TemWorkflowConstants.RouteNodeNames.APPLY_TO_BANK)){
                addExtraButton("methodToCall.applyToBank", applyToBankButtonURL + "buttonsmall_applytobank.gif", "Apply To Bank");
            }
            else if (routeHeader.getAppDocStatus().equals(TemWorkflowConstants.RouteNodeNames.TRAVEL_OFFICE_REVIEW)) {
                addExtraButton("methodToCall.submit", submitButtonURL + "buttonsmall_submit.gif", "Submit");
            }
        }
        
        return extraButtons;
    }

    @Override
    public void setExtraButtons(List<ExtraButton> extraButtons) {
        // TODO Auto-generated method stub
        super.setExtraButtons(extraButtons);
    }

    @Override
    public ExtraButton getExtraButton(int index) {
        // TODO Auto-generated method stub
        return super.getExtraButton(index);
    }

    @Override
    public void setExtraButton(int index, ExtraButton extraButton) {
        // TODO Auto-generated method stub
        super.setExtraButton(index, extraButton);
    }

    public KualiConfigurationService getKualiConfigurationService() {
        if (kualiConfigurationService == null){
            kualiConfigurationService = SpringContext.getBean(KualiConfigurationService.class);
        }
        return kualiConfigurationService;
    }

    /**
     * Adds a new button to the extra buttons collection.
     * 
     * @param property - property for button
     * @param source - location of image
     * @param altText - alternate text for button if images don't appear
     */
    protected void addExtraButton(String property, String source, String altText) {

        ExtraButton newButton = new ExtraButton();

        newButton.setExtraButtonProperty(property);
        newButton.setExtraButtonSource(source);
        newButton.setExtraButtonAltText(altText);

        extraButtons.add(newButton);
    }

    
    /**
     * Gets the fiscalOfficer attribute.
     * 
     * @return Returns the fiscalOfficer
     */
    
    public boolean isFiscalOfficer() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        CardApplicationDocument document = (CardApplicationDocument) this.getDocument();
        
        TEMProfile profile = document.getTemProfile();
        if (profile != null){
            Account account = null;
            Map<String,String> fieldValues = new HashMap<String,String>();
            fieldValues.put("chartOfAccountsCode", profile.getDefaultChartCode());
            fieldValues.put("accountNumber", profile.getDefaultAccount());
            account = (Account) getBusinessObjectService().findByPrimaryKey(Account.class, fieldValues);
            return currentUser.getPrincipalId().equals(account.getAccountFiscalOfficerSystemIdentifier());
        }
        else{
            return false;
        }
    }


    /**
     * Gets the travelManager attribute.
     * 
     * @return Returns the travelManager
     */
    
    public boolean isTravelManager() {
        Person currentUser = GlobalVariables.getUserSession().getPerson();
        return SpringContext.getBean(TravelDocumentService.class).isTravelManager(currentUser);
    }


    /**
     * Gets the appliedToBank attribute.
     * 
     * @return Returns the appliedToBank
     */
    
    public boolean isAppliedToBank() {
        CardApplicationDocument document = (CardApplicationDocument) this.getDocument();
        if(document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus().equals(TemWorkflowConstants.RouteNodeNames.APPLIED_TO_BANK)
                || document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus().equals(TemWorkflowConstants.RouteNodeNames.APPROVED)
                ||document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus().equals(TemWorkflowConstants.RouteNodeNames.APPROVED_BY_BANK)
                ||document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus().equals(TemWorkflowConstants.RouteNodeNames.DECLINED)){
            setAppliedToBank(true);
        }
        return appliedToBank;
    }

    /**	
     * Sets the appliedToBank attribute.
     * 
     * @param appliedToBank The appliedToBank to set.
     */
    public void setAppliedToBank(boolean appliedToBank) {
        this.appliedToBank = appliedToBank;
    }

    /**
     * Gets the initiator attribute.
     * 
     * @return Returns the initiator
     */
    
    public boolean isInitiator() {
        return initiator;
    }

    /**	
     * Sets the initiator attribute.
     * 
     * @param initiator The initiator to set.
     */
    public void setInitiator(boolean initiator) {
        this.initiator = initiator;
    }

    /**
     * Gets the emptyAccount attribute.
     * 
     * @return Returns the emptyAccount
     */
    
    public boolean isEmptyAccount() {
        return emptyAccount;
    }

    /**	
     * Sets the emptyAccount attribute.
     * 
     * @param emptyAccount The emptyAccount to set.
     */
    public void setEmptyAccount(boolean emptyAccount) {
        this.emptyAccount = emptyAccount;
    }

    /**
     * Gets the emptyProfile attribute.
     * 
     * @return Returns the emptyProfile
     */
    
    public boolean isEmptyProfile() {
        return emptyProfile;
    }
    
    /**
     * Gets the multipleApplications attribute.
     * 
     * @return Returns the multipleApplications
     */
    
    public boolean isMultipleApplications() {
        return multipleApplications;
    }

    /**	
     * Sets the multipleApplications attribute.
     * 
     * @param multipleApplications The multipleApplications to set.
     */
    public void setMultipleApplications(boolean multipleApplications) {
        this.multipleApplications = multipleApplications;
    }

    /**	
     * Sets the emptyProfile attribute.
     * 
     * @param emptyProfile The emptyProfile to set.
     */
    public void setEmptyProfile(boolean emptyProfile) {
        this.emptyProfile = emptyProfile;
    }
    
    protected String getStatusCodeFieldName() {
        return "dummyAppDocStatus";
    }
    
    @Override
    public void populateHeaderFields(KualiWorkflowDocument workflowDocument) {
        super.populateHeaderFields(workflowDocument);
        final CardApplicationDocument document = (CardApplicationDocument) getDocument();

        String status = "Application";
        String appDocStatus = document.getDocumentHeader().getWorkflowDocument().getRouteHeader().getAppDocStatus();

        getDocInfo().add(new HeaderField(getDataDictionaryAttributeName(getStatusCodeFieldName()), StringUtils.isBlank(appDocStatus)?status:appDocStatus));
    }
    
    /**
     * Add necessary boilerplate prefix and suffix to lookup a DataDictionary Header field
     * 
     * @param attrName the name of the attribute you're looking for
     */
    protected String getDataDictionaryAttributeName(final String attrName) {
        return new StringBuilder("DataDictionary.").append(getDocument().getClass().getSimpleName()).append(".attributes.").append(attrName).toString();
    }
    
    protected BusinessObjectService getBusinessObjectService() {
        return SpringContext.getBean(BusinessObjectService.class);
    }
}
