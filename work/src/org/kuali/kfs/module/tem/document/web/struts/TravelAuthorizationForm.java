/*
 * Copyright 2010 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.web.struts;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TransportationMode;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelAdvance;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.bean.TravelAuthorizationMvcWrapperBean;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.module.tem.util.MessageUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.document.Document;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;

public class TravelAuthorizationForm extends TravelFormBase implements TravelAuthorizationMvcWrapperBean{
    private TravelerDetailEmergencyContact newEmergencyContactLine;
    private TravelAdvance newTravelAdvanceLine;
    private TravelerDetail newTraveler;
    private boolean canCloseTA;
    private boolean canAmend;
    private boolean canCancelTA;
    private boolean canHold;
    private boolean canRemoveHold;
    private boolean canUnmask = false;
    private boolean travelArranger;

    private boolean allowIncidentals = true;
    private String policyURL = getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TravelAuthorizationParameters.TRAVEL_ADVANCES_POLICY_URL);
    private boolean multipleAdvances =getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.MULTIPLE_CASH_ADVANCES_ALLOWED_IND);
    private boolean showPaymentMethods = getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.DISPLAY_PAYMENT_METHOD_DROPDOWN_IND);
    private boolean showPolicy;
    private boolean waitingOnTraveler;
    private boolean showCorporateCardTotal = getParameterService().getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.AMOUNT_DUE_CORPORATE_CARD_TOTAL_LINE_IND);

    // parameters that affect the UI
    private List<String> tempSelectedTransportationModes = new ArrayList<String>();

    /**
     * Constructs a TravelAuthorizationForm.java.
     */
    public TravelAuthorizationForm() {
        super();
        this.setShowPerDiem(false);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.bean.TravelMvcWrapperBean#setDistribution(java.util.List)
     */
    @Override
    public void setDistribution(final List<AccountingDistribution> distribution) {
        super.setDistribution(distribution);

        TravelDocument travelDocument = (TravelDocument)getDocument();

        //TA doc - always deselected the expesne type (ENCUMBRANCE) so it does not get distributed automatically
        for (AccountingDistribution accountdistribution : this.distribution){
            if (accountdistribution.getCardType().equals(travelDocument.getExpenseTypeCode())){
                accountdistribution.setSelected(Boolean.FALSE);
                accountdistribution.setDisabled(Boolean.TRUE);
            }
        }
    }

    /**
     * Display accounting line only if - Trip is encumbrance OR for non-emcumbrance trip, if there are any imported expenses
     *
     * @return
     */
    public boolean isDisplayAccountingLines(){
        boolean display = true;
        TravelAuthorizationDocument document = (TravelAuthorizationDocument) getTravelDocument();
        //trip does not generate encumbrance - check if there is any imported expense to determine whether its necessary to display the tab
        if (!document.isTripGenerateEncumbrance()){
            //display the tab if there are imported expenses
            display = !document.getImportedExpenses().isEmpty();
        }
        return display;
    }

    /**
     * Display imported expense related tab base on Travel Authorization document base on system parameter
     *
     * @return
     */
    public boolean isDisplayImportedExpenseRelatedTab(){
        boolean display = getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TravelAuthorizationParameters.DISPLAY_IMPORTED_EXPENSE_IND);
        return display;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelFormBase#getPerDiemLabel()
     */
    @Override
    public String getPerDiemLabel(){
        return TemConstants.ENCUMBRANCE_PREFIX + super.getPerDiemLabel();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelFormBase#getExpenseLabel()
     */
    @Override
    public String getExpenseLabel(){
        return TemConstants.ENCUMBRANCE_PREFIX + StringUtils.substringAfter(super.getExpenseLabel(), " ");
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelFormBase#getExpenseTabLabel()
     */
    @Override
    public String getExpenseTabLabel(){
        return TemConstants.GENERAL_EXPENSES_LABEL;
    }

    /**
     * Gets the newEmergencyContactLine attribute.
     *
     * @return Returns the newEmergencyContactLine.
     */
    @Override
    public TravelerDetailEmergencyContact getNewEmergencyContactLine() {
        return newEmergencyContactLine;
    }

    /**
     * Sets the newEmergencyContactLine attribute value.
     *
     * @param newEmergencyContactLine The newEmergencyContactLine to set.
     */
    @Override
    public void setNewEmergencyContactLine(TravelerDetailEmergencyContact newEmergencyContactLine) {
        this.newEmergencyContactLine = newEmergencyContactLine;
    }

    /**
     * Gets the newTravelAdvanceLine attribute.
     *
     * @return Returns the newTravelAdvanceLine.
     */
    @Override
    public TravelAdvance getNewTravelAdvanceLine() {
        return newTravelAdvanceLine;
    }

    /**
     * Sets the newTravelAdvanceLine attribute value.
     *
     * @param newTravelAdvanceLine The newTravelAdvanceLine to set.
     */
    @Override
    public void setNewTravelAdvanceLine(TravelAdvance newTravelAdvanceLine) {
        this.newTravelAdvanceLine = newTravelAdvanceLine;
    }

    /**
     * Gets the selectedTransportationModes attribute.
     *
     * @return Returns the selectedTransportationModes.
     */
    @Override
    public List<String> getSelectedTransportationModes() {

        List<String> mySelectedTransportationModes = new ArrayList<String>();
        List<TransportationModeDetail> details = this.getTravelAuthorizationDocument().getTransportationModes();
        for (TransportationModeDetail detail : details) {
            mySelectedTransportationModes.add(detail.getTransportationModeCode());

        }
        return mySelectedTransportationModes;
    }

    /**
     * Sets the selectedTransportationModes attribute value.
     *
     * @param selectedTransportationModes The selectedTransportationModes to set.
     */
    @Override
    public void setSelectedTransportationModes(List<String> selectedTransportationModes) {
        this.tempSelectedTransportationModes = selectedTransportationModes;
    }

    @Override
    public List<String> getTempSelectedTransporationModes() {
        return this.tempSelectedTransportationModes;
    }

    /**
     * Retrieve the name of the document identifier field for data dictionary queries
     *
     * @return String with the field name of the document identifier
     */
    @Override
    protected String getDocumentIdentifierFieldName() {
        return "travelDocumentIdentifier";
    }

    /**
     * @return TravelAuthorizationDocument
     */
    @Override
    public TravelAuthorizationDocument getTravelAuthorizationDocument() {
        return (TravelAuthorizationDocument) getDocument();
    }


    @Override
    public void setNewTraveler(final TravelerDetail traveler) {
        this.newTraveler = traveler;
    }

    @Override
    public TravelerDetail getNewTraveler() {
        return this.newTraveler;
    }

    @Override
    protected String getDefaultDocumentTypeName() {
        return "TA";
    }

    /**
     * Creates a MAP for all the buttons to appear on the Travel Authorization Form, and sets the attributes of these buttons.
     *
     * @return the button map created.
     */
    protected Map<String, ExtraButton> createButtonsMap() {
        HashMap<String, ExtraButton> result = new HashMap<String, ExtraButton>();

        // Amend button
        ExtraButton amendButton = new ExtraButton();
        amendButton.setExtraButtonProperty("methodToCall.amendTa");
        amendButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_amend.gif");
        amendButton.setExtraButtonAltText("Amend");

        // Hold button
        ExtraButton holdButton = new ExtraButton();
        holdButton.setExtraButtonProperty("methodToCall.holdTa");
        holdButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_hold.gif");
        holdButton.setExtraButtonAltText("Hold");

        // Remove Hold button
        ExtraButton removeHoldButton = new ExtraButton();
        removeHoldButton.setExtraButtonProperty("methodToCall.removeHoldTa");
        removeHoldButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_removehold.gif");
        removeHoldButton.setExtraButtonAltText("Remove Hold");

        // Cancel Travel button
        ExtraButton cancelTravelButton = new ExtraButton();
        cancelTravelButton.setExtraButtonProperty("methodToCall.cancelTa");
        cancelTravelButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_cancelta.gif");
        cancelTravelButton.setExtraButtonAltText("Cancel Travel Authorization");

        // Close Travel button
        ExtraButton closeTAButton = new ExtraButton();
        closeTAButton.setExtraButtonProperty("methodToCall.closeTa");
        closeTAButton.setExtraButtonSource("${" + KFSConstants.EXTERNALIZABLE_IMAGES_URL_KEY + "}buttonsmall_closeta.gif");
        closeTAButton.setExtraButtonAltText("Close Travel Authorization");

        result.put(amendButton.getExtraButtonProperty(), amendButton);
        result.put(holdButton.getExtraButtonProperty(), holdButton);
        result.put(removeHoldButton.getExtraButtonProperty(), removeHoldButton);
        result.put(cancelTravelButton.getExtraButtonProperty(), cancelTravelButton);
        result.put(closeTAButton.getExtraButtonProperty(), closeTAButton);

        result.putAll(createDVREQSExtraButtonMap());
        return result;
    }

    @Override
    public List<ExtraButton> getExtraButtons() {
        super.getExtraButtons();
        Map<String, ExtraButton> buttonsMap = createButtonsMap();

        if (canAmend()) {
            extraButtons.add(buttonsMap.get("methodToCall.amendTa"));
        }
        if (canHold()) {
            extraButtons.add(buttonsMap.get("methodToCall.holdTa"));
        }
        if (canRemoveHold()) {
            extraButtons.add(buttonsMap.get("methodToCall.removeHoldTa"));
        }
        if (canCloseTA()) {
            extraButtons.add(buttonsMap.get("methodToCall.closeTa"));
        }
        if (canCancelTA()) {
            extraButtons.add(buttonsMap.get("methodToCall.cancelTa"));
        }

        boolean enablePayments = getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.VENDOR_PAYMENT_ALLOWED_BEFORE_FINAL_APPROVAL_IND);
        if (enablePayments && !SpringContext.getBean(TravelDocumentService.class).isUnsuccessful(this.getTravelDocument())){
            if (getTravelAuthorizationDocument().canPayDVToVendor()) {
                extraButtons.add(buttonsMap.get("methodToCall.payDVToVendor"));
            }
            if (getTravelAuthorizationDocument().canCreateREQSForVendor()) {
                extraButtons.add(buttonsMap.get("methodToCall.createREQSForVendor"));
            }
        }

        return extraButtons;
    }

    /**
     * Gets the canCloseTA attribute.
     * @return Returns the canCloseTA.
     */
    @Override
    public boolean canCloseTA() {
        return canCloseTA;
    }

    /**
     * Sets the canCloseTA attribute value.
     * @param canCloseTA The canCloseTA to set.
     */
    @Override
    public void setCanCloseTA(boolean canCloseTA) {
        this.canCloseTA = canCloseTA;
    }

    /**
     * Sets the canCloseTA attribute value.
     * @param canCloseTA The canCloseTA to set.
     */
    @Override
    public void setCanCancelTA(boolean canCancelTA) {
        this.canCancelTA = canCancelTA;
    }

    /**
     * Gets the canCancelTA attribute.
     * @return Returns the canCancelTA.
     */
    @Override
    public boolean canCancelTA() {
        return canCancelTA;
    }

    /**
     * Gets the canAmend attribute.
     * @return Returns the canAmend.
     */
    @Override
    public boolean canAmend() {
        return canAmend;
    }

    /**
     * Sets the canAmend attribute value.
     * @param canAmend The canAmend to set.
     */
    @Override
    public void setCanAmend(boolean canAmend) {
        this.canAmend = canAmend;
    }

    /**
     *
     * Sets the canHold attribute value
     * @param canHold
     */
    @Override
    public void setCanHold(boolean canHold) {
        this.canHold = canHold;

    }

    /**
     * This method determines if the user can or cannot hold a TA based on permissions and state
     *
     * @return true if they can hold a TA
     */
    private boolean canHold() {
        return this.canHold;
    }

    @Override
    public void setCanRemoveHold(boolean canRemoveHold) {
        this.canRemoveHold = canRemoveHold;
    }

    /**
     * This method determines if the user can or cannot remove a hold on a TA based on permissions and state
     *
     * @return true if they can hold a TA
     */
    private boolean canRemoveHold() {
        return this.canRemoveHold;
    }

    @Override
    public boolean isCanUnmask() {
        return canUnmask;
    }

    @Override
    public void setCanUnmask(boolean canUnmask) {
        this.canUnmask = canUnmask;
    }

    @Override
    public Map<String, String> getModesOfTransportation() {
        Map<String, String> modesOfTrans = new HashMap<String, String>();

        Collection<TransportationMode> bos = SpringContext.getBean(BusinessObjectService.class).findAll(TransportationMode.class);
        for (TransportationMode mode : bos) {
            modesOfTrans.put(mode.getCode(), mode.getName());
        }

        return modesOfTrans;
    }

    protected TravelReimbursementService getTravelReimbursementService() {
        return SpringContext.getBean(TravelReimbursementService.class);
    }

    protected TravelService getTravelService() {
        return SpringContext.getBean(TravelService.class);
    }

    public boolean isAllowIncidentals() {
        return allowIncidentals;
    }

    public void setAllowIncidentals(boolean allowIncidentals) {
        this.allowIncidentals = allowIncidentals;
    }

    /**
     * Gets the policyURL attribute.
     * @return Returns the policyURL.
     */
    public String getPolicyURL() {
        return MessageUtils.getMessage(TemKeyConstants.MESSAGE_TA_ADVANCE_POLICY, policyURL);
    }

    /**
     * Gets the multipleAdvances attribute.
     * @return Returns the multipleAdvances.
     */
    public boolean isMultipleAdvances() {
        return multipleAdvances;
    }

    /**
     * Gets the showPaymentMethods attribute.
     * @return Returns the showPaymentMethods.
     */
    public boolean isShowPaymentMethods() {
        return showPaymentMethods;
    }

    /**
     * Gets the waitingOnTraveler attribute.
     * @return Returns the waitingOnTraveler.
     */
    public boolean isWaitingOnTraveler() {
        return waitingOnTraveler;
    }

    /**
     * Sets the waitingOnTraveler attribute value.
     * @param waitingOnTraveler The waitingOnTraveler to set.
     */
    public void setWaitingOnTraveler(boolean waitingOnTraveler) {
        this.waitingOnTraveler = waitingOnTraveler;
    }

    /**
     * Gets the showCorporateCardTotal attribute.
     * @return Returns the showCorporateCardTotal.
     */
    public boolean getShowCorporateCardTotal() {
        return showCorporateCardTotal;
    }

    /**
     * Gets the showPolicy attribute.
     * @return Returns the showPolicy.
     */
    public boolean isShowPolicy() {
        return showPolicy;
    }

    /**
     * Sets the showPolicy attribute value.
     * @param showPolicy The showPolicy to set.
     */
    public void setShowPolicy(boolean showPolicy) {
        this.showPolicy = showPolicy;
    }

    /**
     *
     * @return a list of all the DV doc numbers related to this TA that have not been finalized
     */
    public List<String> getDVDocNumbersNotFinalized() {
        List<String> docNumbers = new ArrayList<String>();
        String docTypeName = getDataDictionaryService().getDocumentTypeNameByClass(DisbursementVoucherDocument.class);

        if(this.getRelatedDocuments().containsKey(docTypeName)) {
            for(Document document : this.getRelatedDocuments().get(docTypeName)) {
                if(document instanceof DisbursementVoucherDocument) {
                    WorkflowDocument workflow = document.getDocumentHeader().getWorkflowDocument();
                    if((workflow.isEnroute() || workflow.isInitiated() || workflow.isSaved()) && !workflow.isApproved()) {
                        docNumbers.add(document.getDocumentNumber());
                    }
                }
            }
        }

        return docNumbers;
    }

    protected DataDictionaryService getDataDictionaryService() {
        return SpringContext.getBean(DataDictionaryService.class);
    }


    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelFormBase#getEnableTaxable()
     */
    @Override
    public boolean getEnableImportedTaxable() {
        return false;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.web.struts.TravelFormBase#getTravelPaymentFormAction()
     */
    @Override
    public String getTravelPaymentFormAction() {
        return TemConstants.TRAVEL_AUTHORIZATION_ACTION_NAME;
    }

}
