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
import org.apache.struts.upload.FormFile;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.TravelAuthorizationParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelParameters;
import org.kuali.kfs.module.tem.TemKeyConstants;
import org.kuali.kfs.module.tem.TemParameterConstants;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TransportationMode;
import org.kuali.kfs.module.tem.businessobject.TransportationModeDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerDetailEmergencyContact;
import org.kuali.kfs.module.tem.document.TravelAuthorizationDocument;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelReimbursementService;
import org.kuali.kfs.module.tem.document.web.bean.TravelAuthorizationMvcWrapperBean;
import org.kuali.kfs.module.tem.service.TravelService;
import org.kuali.kfs.module.tem.util.MessageUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.web.format.CurrencyFormatter;
import org.kuali.rice.kns.web.ui.ExtraButton;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.service.DataDictionaryService;

public class TravelAuthorizationForm extends TravelFormBase implements TravelAuthorizationMvcWrapperBean{
    private TravelerDetailEmergencyContact newEmergencyContactLine;
    private TravelerDetail newTraveler;
    private boolean canCloseTA;
    private boolean canAmend;
    private boolean canCancelTA;
    private boolean canHold;
    private boolean canRemoveHold;
    private boolean canUnmask = false;
    private boolean travelArranger;

    private boolean allowIncidentals = true;
    private String policyURL;
    private Boolean multipleAdvances;
    private boolean showPolicy;
    private boolean waitingOnTraveler;
    private Boolean showCorporateCardTotal;
    private boolean showTravelAdvancesForTrip;

    protected transient FormFile advanceAccountingFile;

    private TemSourceAccountingLine newAdvanceAccountingLine;

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

        //TA doc - always deselected the expense type (ENCUMBRANCE) so it does not get distributed automatically
        for (AccountingDistribution accountdistribution : this.distribution){
            if (accountdistribution.getCardType().equals(travelDocument.getDefaultCardTypeCode())){
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
        TravelAuthorizationDocument document = (TravelAuthorizationDocument) getTravelDocument();
        boolean display = document.isTripGenerateEncumbrance(); // trips which do not generate encumbrances won't have accounting lines
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
     * @return new accounting line associated with travel advance
     */
    public TemSourceAccountingLine getNewAdvanceAccountingLine() {
        if (getTravelAuthorizationDocument() == null) {
            throw new IllegalArgumentException("TravelAuthorizationDocument cannot be null when creating new accounting line for advance");
        }
        if (newAdvanceAccountingLine == null) {
            newAdvanceAccountingLine = getTravelAuthorizationDocument().createNewAdvanceAccountingLine();
        }
        return newAdvanceAccountingLine;
    }

    /**
     * Sets new accounting line associated with travel advance
     * @param newTravelAdvanceAccountingLine new accounting line associated with travel advance
     */
    public void setNewAdvanceAccountingLine(TemSourceAccountingLine newTravelAdvanceAccountingLine) {
        this.newAdvanceAccountingLine = newTravelAdvanceAccountingLine;
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

        result.putAll(createPaymentExtraButtonMap());
        return result;
    }

    @Override
    public List<ExtraButton> getExtraButtons() {
        super.getExtraButtons();
        Map<String, ExtraButton> buttonsMap = createButtonsMap();

        if (getDocumentActions().keySet().contains(TemConstants.TravelAuthorizationActions.CAN_AMEND)) {
            extraButtons.add(buttonsMap.get("methodToCall.amendTa"));
        }
        if (getDocumentActions().keySet().contains(TemConstants.TravelAuthorizationActions.CAN_HOLD)) {
            extraButtons.add(buttonsMap.get("methodToCall.holdTa"));
        }
        if (getDocumentActions().keySet().contains(TemConstants.TravelAuthorizationActions.CAN_REMOVE_HOLD)) {
            extraButtons.add(buttonsMap.get("methodToCall.removeHoldTa"));
        }
        if (getDocumentActions().keySet().contains(TemConstants.TravelAuthorizationActions.CAN_CLOSE_TA)) {
            extraButtons.add(buttonsMap.get("methodToCall.closeTa"));
        }
        if (getDocumentActions().keySet().contains(TemConstants.TravelAuthorizationActions.CAN_CANCEL_TA)) {
            extraButtons.add(buttonsMap.get("methodToCall.cancelTa"));
        }
        if (getDocumentActions().keySet().contains(TemConstants.TravelAuthorizationActions.CAN_PAY_VENDOR)) {
            extraButtons.add(buttonsMap.get("methodToCall.payDVToVendor"));
        }

        return extraButtons;
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
        if (policyURL == null) {
            policyURL = getParameterService().getParameterValueAsString(TravelAuthorizationDocument.class, TravelAuthorizationParameters.TRAVEL_ADVANCES_POLICY_URL);
        }
        return MessageUtils.getMessage(TemKeyConstants.MESSAGE_TA_ADVANCE_POLICY, policyURL);
    }

    /**
     * @return true if travel advances for trip should be shown, false otherwise
     */
    public boolean isShowTravelAdvancesForTrip() {
        return showTravelAdvancesForTrip;
    }

    /**
     * Sets whether the travel advances for the rest of the trip (ie, not this document's travel advance, but other completed advances related to this trip)
     * should be shown
     * @param showTravelAdvancesForTrip true if travel advances for trip should be shown, false otherwise
     */
    public void setShowTravelAdvancesForTrip(boolean showTravelAdvancesForTrip) {
        this.showTravelAdvancesForTrip = showTravelAdvancesForTrip;
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
        if (showCorporateCardTotal == null) {
            showCorporateCardTotal = Boolean.valueOf(getParameterService().getParameterValueAsBoolean(TemParameterConstants.TEM_DOCUMENT.class, TravelParameters.AMOUNT_DUE_CORPORATE_CARD_TOTAL_LINE_IND, false));
        }
        return showCorporateCardTotal.booleanValue();
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

    /**
     * @return the encapsulation of an uploaded file of accounting lines to associate with the travel advance
     */
    public FormFile getAdvanceAccountingFile() {
        return advanceAccountingFile;
    }

    /**
     * Sets the form file for advance accounting line uploads
     * @param advanceFile a file of accounting lines to associate with paying for a travel advance
     */
    public void setAdvanceAccountingFile(FormFile advanceFile) {
        this.advanceAccountingFile = advanceFile;
    }

    /**
     * Retrieves the advance accounting lines total in a currency format with commas.
     * @return the currency formatted total
     */
    public String getCurrencyFormattedAdvanceTotal() {
        return (String) new CurrencyFormatter().format(getTravelAuthorizationDocument().getAdvanceTotal());
    }

    public boolean isAdvancePdpStatusTabShown() {
        if (getTravelDocument() != null) {
            return getTravelAuthorizationDocument().shouldProcessAdvanceForDocument();
        }
        return false;
    }


    public boolean isDefaultTravelAdvanceTab() {
        if (getTravelDocument() != null && getTravelAuthorizationDocument().shouldProcessAdvanceForDocument()) {

            if(TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_TRVLR.equals(getTravelDocument().getAppDocStatus()) ||
                TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_FISCAL.equals(getTravelDocument().getAppDocStatus()) ||
                TemConstants.TravelAuthorizationStatusCodeKeys.AWAIT_TRVL_MGR.equals(getTravelDocument().getAppDocStatus())) {
                return true;
            }
        }

       return false;
    }

}
