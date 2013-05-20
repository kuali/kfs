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
package org.kuali.kfs.module.tem.document;

import java.beans.PropertyChangeEvent;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.fp.document.DisbursementVoucherDocument;
import org.kuali.kfs.integration.ar.AccountsReceivableCustomer;
import org.kuali.kfs.module.purap.document.RequisitionDocument;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.EntertainmentStatusCodeKeys;
import org.kuali.kfs.module.tem.TemConstants.TravelEntertainmentParameters;
import org.kuali.kfs.module.tem.TemConstants.TravelRelocationParameters;
import org.kuali.kfs.module.tem.TemPropertyConstants;
import org.kuali.kfs.module.tem.TemWorkflowConstants;
import org.kuali.kfs.module.tem.businessobject.Attendee;
import org.kuali.kfs.module.tem.businessobject.Purpose;
import org.kuali.kfs.module.tem.businessobject.TEMProfile;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TravelerType;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.service.TravelerService;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.businessobject.GeneralLedgerPendingEntry;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.kew.api.document.DocumentStatus;
import org.kuali.rice.kew.framework.postprocessor.DocumentRouteStatusChange;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

@Entity
@Table(name = "TEM_ENT_DOC_T")
public class TravelEntertainmentDocument extends TEMReimbursementDocument {

    protected static Logger LOG = Logger.getLogger(TravelEntertainmentDocument.class);

    private Integer hostProfileId;
    private String hostName;
    private String eventTitle;
    private Boolean hostCertified;
    private Boolean nonEmployeeCertified;
    protected Boolean spouseIncluded;
    private String description;
    private String purposeCode;
    private Purpose purpose;

    private Boolean attendeeListAttached;
    private Integer numberOfAttendees;
    private TravelerDetail host;
    private TEMProfile hostProfile;
    private TravelerDetail attendeeDetail;

    private List<Attendee> attendee = new ArrayList<Attendee>();

    public TravelEntertainmentDocument() {
    }

    @Column(name = "HOST_TEM_PROFILE_ID", length = 50, nullable = true)
    public Integer getHostProfileId() {
        return hostProfileId;
    }

    public void setHostProfileId(Integer hostProfileId) {
        this.hostProfileId = hostProfileId;
        BusinessObjectService service = (BusinessObjectService) SpringContext.getService("businessObjectService");
        Map<String, Object> primaryKeys = new HashMap<String, Object>();
        primaryKeys.put(TemPropertyConstants.TEMProfileProperties.PROFILE_ID, hostProfileId);
        setHostProfile(service.findByPrimaryKey(TEMProfile.class, primaryKeys));
    }

    @Column(name = "TITLE", length = 100, nullable = true)
    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    @Column(name = "HOST_CERTIFIED", length = 1, nullable = true)
    public Boolean getHostCertified() {
        return hostCertified;
    }

    public void setHostCertified(Boolean hostCertified) {
        this.hostCertified = hostCertified;
    }

    @Column(name = "NON_EMPLOYEE_CERTIFIED", length = 1, nullable = true)
    public Boolean getNonEmployeeCertified() {
        return nonEmployeeCertified;
    }

    public void setNonEmployeeCertified(Boolean nonEmployeeCertified) {
        this.nonEmployeeCertified = nonEmployeeCertified;
    }

    @Column(name = "SPOUSE_INCLUDED", nullable = true, length = 1)
    public Boolean getSpouseIncluded() {
        return spouseIncluded;
    }

    public Boolean getSpouseIncludedForSearching() {
        return spouseIncluded;
    }

    public Boolean isSpouseIncludedForSearching() {
        return spouseIncluded;
    }

    public void setSpouseIncluded(Boolean spouseIncluded) {
        this.spouseIncluded = spouseIncluded;
    }

    @Column(name = "DESCRIPTION", nullable = true, length = 255)
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "PURPOSE_CODE", nullable = true, length = 4)
    public String getPurposeCode() {
        return purposeCode;
    }

    public void setPurpose(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    @JoinColumn(name = "PURPOSE_CODE", nullable = true)
    public Purpose getPurpose() {
        return purpose;
    }

    public void setPurpose(Purpose purpose) {
        this.purpose = purpose;
    }

    public void setPurposeCode(String purposeCode) {
        this.purposeCode = purposeCode;
    }

    @Override
    protected TravelDocumentService getTravelDocumentService() {
        return SpringContext.getBean(TravelDocumentService.class);
    }

    @Column(name = "ATTENDEE_LIST_ATTACHED", nullable = true, length = 1)
    public Boolean getAttendeeListAttached() {
        return attendeeListAttached;
    }

    public void setAttendeeListAttached(Boolean attendeeListAttached) {
        this.attendeeListAttached = attendeeListAttached;
    }

    @Column(name = "NUMBER_ATTENDEES", nullable = true, length = 50)
    public Integer getNumberOfAttendees() {
        return numberOfAttendees;
    }

    public void setNumberOfAttendees(Integer numberOfAttendees) {
        this.numberOfAttendees = numberOfAttendees;
    }

    public List<Attendee> getAttendee() {
        return attendee;
    }

    public void setAttendee(List<Attendee> attendee) {
        this.attendee = attendee;
    }

    @Column(name = "HOST_NAME", nullable = true, length = 40)
    public String getHostName() {
        return hostName;
    }

    public void setHostName(String hostName) {
        this.hostName = hostName;
    }

    public TravelerDetail getHost() {
        return host;
    }

    public void setHost(TravelerDetail host) {
        this.host = host;
    }

    public TravelerDetail getAttendeeDetail() {
        return attendeeDetail;
    }

    public void setAttendeeDetail(TravelerDetail attendeeDetail) {
        this.attendeeDetail = attendeeDetail;
    }

    public TEMProfile getHostProfile() {
        return hostProfile;
    }

    /**
     * @see org.kuali.kfs.sys.document.FinancialSystemTransactionalDocumentBase#answerSplitNodeQuestion(java.lang.String)
     */
    @Override
    public boolean answerSplitNodeQuestion(String nodeName) throws UnsupportedOperationException {
        if (nodeName.equals(TemWorkflowConstants.REQUIRES_TRAVELER_REVIEW)) {
            return requiresTravelerApprovalRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.SPECIAL_REQUEST)) {
            return requiresSpecialRequestReviewRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.TAX_MANAGER)) {
            return requiresTaxManagerApprovalRouting();
        }
        if (nodeName.equals(TemWorkflowConstants.SEPARATION_OF_DUTIES)) {
            return requiresSeparationOfDutiesRouting();
        }
        throw new UnsupportedOperationException("Cannot answer split question for this node you call \"" + nodeName + "\"");
    }

    /**
     *
     * @return
     */
    private boolean requiresEntertainmentManagerRouting() {
        return true;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#requiresSpecialRequestReviewRouting()
     */
    @Override
    protected boolean requiresSpecialRequestReviewRouting() {
        if (super.requiresSpecialRequestReviewRouting()) {
            return true;
        }

        if (getPurpose() != null) {
            String purposeCode = getPurpose().getPurposeCode();
            if (getPurpose().isReviewRequiredIndicator() != null && getPurpose().isReviewRequiredIndicator()) {
                return true;
            }
        }

        if ((ObjectUtils.isNotNull(getSpouseIncluded()) && getSpouseIncluded())) {
            return true;
        }

        return false;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#populateDisbursementVoucherFields(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void populateDisbursementVoucherFields(DisbursementVoucherDocument disbursementVoucherDocument) {
        super.populateDisbursementVoucherFields(disbursementVoucherDocument);

        //override the check stub text on ENT doc
        disbursementVoucherDocument.setDisbVchrCheckStubText(getTravelDocumentIdentifier() + " " + StringUtils.defaultString(getEventTitle()) + getTripBegin());
        //change to use payment method from the document
        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(getPaymentMethod());

        final String paymentReasonCode = getParameterService().getParameterValueAsString(TravelEntertainmentDocument.class, TravelEntertainmentParameters.PAYMENT_REASON_CODE);
        disbursementVoucherDocument.getDvPayeeDetail().setDisbVchrPaymentReasonCode(paymentReasonCode);

        final String paymentLocationCode = getParameterService().getParameterValueAsString(TravelEntertainmentDocument.class, TravelEntertainmentParameters.DOCUMENT_LOCATION_CODE);
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(paymentLocationCode);
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateRequisitionFields(org.kuali.kfs.module.purap.document.RequisitionDocument, org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public void populateRequisitionFields(RequisitionDocument reqsDoc, TravelDocument document) {
        super.populateRequisitionFields(reqsDoc, document);
        TravelEntertainmentDocument entDocument = (TravelEntertainmentDocument) document;
        reqsDoc.getDocumentHeader().setDocumentDescription("Generated for ENT doc: " + StringUtils.defaultString(entDocument.getEventTitle()));
        reqsDoc.getDocumentHeader().setOrganizationDocumentNumber(entDocument.getTravelDocumentIdentifier());
        Calendar calendar = getDateTimeService().getCurrentCalendar();
        calendar.setTime(entDocument.getTripBegin());
        reqsDoc.setPostingYear(calendar.get(Calendar.YEAR));
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#doRouteStatusChange(org.kuali.rice.kew.dto.DocumentRouteStatusChange)
     */
    @Override
    public void doRouteStatusChange(DocumentRouteStatusChange statusChangeEvent) {
        super.doRouteStatusChange(statusChangeEvent);

        if (DocumentStatus.FINAL.getCode().equals(statusChangeEvent.getNewRouteStatus())
                || DocumentStatus.PROCESSED.getCode().equals(statusChangeEvent.getNewRouteStatus())) {

            LOG.debug("New route status is " + statusChangeEvent.getNewRouteStatus());

            // for some reason when it goes to final it never updates to the last status
            updateAppDocStatus(EntertainmentStatusCodeKeys.ENT_MANAGER_APPROVED);

            // If the hold new fiscal year encumbrance indicator is true and the trip end date
            // is after the current fiscal year end date then mark all the gl pending entries
            // as 'H' (Hold) otherwise mark all the gl pending entries as 'A' (approved)
            if (getGeneralLedgerPendingEntries() != null && !getGeneralLedgerPendingEntries().isEmpty()) {
                if (getParameterService().getParameterValueAsBoolean(TravelAuthorizationDocument.class, TemConstants.TravelAuthorizationParameters.HOLD_NEW_FISCAL_YEAR_ENCUMBRANCES_IND)) {
                    UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
                    java.util.Date endDate = universityDateService.getLastDateOfFiscalYear(universityDateService.getCurrentFiscalYear());
                    if (ObjectUtils.isNotNull(getTripEnd()) && getTripEnd().after(endDate)) {
                        for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
                            glpe.setFinancialDocumentApprovedCode(KFSConstants.PENDING_ENTRY_APPROVED_STATUS_CODE.HOLD);
                        }
                    }
                }
                else {
                    for (GeneralLedgerPendingEntry glpe : getGeneralLedgerPendingEntries()) {
                        glpe.setFinancialDocumentApprovedCode(KFSConstants.DocumentStatusCodes.APPROVED);
                    }
                }
                getBusinessObjectService().save(getGeneralLedgerPendingEntries());
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#initiateDocument()
     */
    @Override
    public void initiateDocument() {
        super.initiateDocument();
        updateAppDocStatus(EntertainmentStatusCodeKeys.IN_PROCESS);
    }

    /**
     * Given the <code>financialObjectCode</code>, determine the total of the {@link SourceAccountingLine} instances with that
     * <code>financialObjectCode</code>
     *
     * @param financialObjectCode to search for total on
     * @return @{link KualiDecimal} with total value for {@link AccountingLines} with <code>finanncialObjectCode</code>
     */
    @Override
    public KualiDecimal getTotalFor(final String financialObjectCode) {
        KualiDecimal retval = KualiDecimal.ZERO;

        LOG.debug("Getting total for " + financialObjectCode);

        for (final AccountingLine line : (List<AccountingLine>) getSourceAccountingLines()) {
            LOG.debug("Comparing " + financialObjectCode + " to " + line.getObjectCode().getCode());
            if (line.getObjectCode().getCode().equals(financialObjectCode)) {
                retval = retval.add(line.getAmount());
            }
        }

        return retval;
    }

    public boolean canShowHostCertification() {
        return (getHostProfile() != null && getTemProfile() != null && !getHostProfile().getProfileId().equals(getTemProfile().getProfileId()) && !getDocumentHeader().getWorkflowDocument().isInitiated());
    }

    public boolean canDisplayNonEmployeeCheckbox() {
        return ((getHostProfile() != null && getHostProfile().getTravelerTypeCode().equals(TemConstants.NONEMP_TRAVELER_TYP_CD)) || (getTemProfile() != null && getTemProfile().getTravelerTypeCode().equals(TemConstants.NONEMP_TRAVELER_TYP_CD)));
    }

    @Override
    protected String generateDescription() {
        StringBuffer sb = new StringBuffer();
        SimpleDateFormat format = new SimpleDateFormat("MM/dd/yyyy");
        PersonService ps = SpringContext.getBean(PersonService.class);

        Person person = ps.getPerson(getTraveler().getPrincipalId());

        this.getTraveler().refreshReferenceObject(TemPropertyConstants.CUSTOMER);

        AccountsReceivableCustomer customer = getTraveler().getCustomer();
        if (person != null) {
            sb.append(person.getLastName() + ", " + person.getFirstName() + " " + person.getMiddleName() + " ");
        }
        else if (customer != null) {
            sb.append(customer.getCustomerName() + " ");
        }
        else {
            sb.append(getTraveler().getFirstName() + " " + getTraveler().getLastName() + " ");
        }

        if (this.getTripBegin() != null) {
            sb.append(format.format(this.getTripBegin()) + " ");
        }
        if (eventTitle != null) {
            sb.append(this.eventTitle);
        }
        String tempStr = sb.toString();

        if (tempStr.length() > 40) {
            tempStr = tempStr.substring(0, 39);
        }

        return tempStr;
    }

    @Transient
    public void addAttendee(final Attendee line) {
        final String sequenceName = line.getSequenceName();
        final Long sequenceNumber = getSequenceAccessorService().getNextAvailableSequenceNumber(sequenceName, Attendee.class);
        line.setId(sequenceNumber.intValue());
        line.setDocumentNumber(this.documentNumber);
        notifyChangeListeners(new PropertyChangeEvent(this, "attendee", null, line));
        getAttendee().add(line);
    }

    @Transient
    public void removeAttendee(final Integer index) {
        final Attendee line = getAttendee().remove((int) index);
        notifyChangeListeners(new PropertyChangeEvent(this, "attendee", line, null));
    }

    /**
     *
     * @param hostProfile
     */
    public void setHostProfile(TEMProfile hostProfile) {
        this.hostProfile = hostProfile;
        if (hostProfile != null) {
            TravelerService service = (TravelerService) SpringContext.getService("travelerService");
            service.populateTEMProfile(hostProfile);
            if (hostProfile.getTravelerType() == null) {
                BusinessObjectService boService = (BusinessObjectService) SpringContext.getService("businessObjectService");
                Map<String, Object> fieldValues = new HashMap<String, Object>();
                fieldValues.put("code", hostProfile.getTravelerTypeCode());
                List<TravelerType> types = (List<TravelerType>) boService.findMatching(TravelerType.class, fieldValues);
                hostProfile.setTravelerType(types.get(0));
            }
        }
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocument#getReportPurpose()
     */
    @Override
    public String getReportPurpose() {
        return purpose != null? purpose.getPurposeName() : null;
    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#populateVendorPayment(org.kuali.kfs.fp.document.DisbursementVoucherDocument)
     */
    @Override
    public void populateVendorPayment(DisbursementVoucherDocument disbursementVoucherDocument) {
        super.populateVendorPayment(disbursementVoucherDocument);

        String locationCode = getParameterService().getParameterValueAsString(TravelEntertainmentDocument.class, TravelRelocationParameters.DOCUMENTATION_LOCATION_CODE);
        String checkStubText = this.getTravelDocumentIdentifier() + ", " + this.getEventTitle();

        disbursementVoucherDocument.setDisbVchrPaymentMethodCode(TemConstants.DisbursementVoucherPaymentMethods.CHECK_ACH_PAYMENT_METHOD_CODE);
        disbursementVoucherDocument.setDisbursementVoucherDocumentationLocationCode(locationCode);
        disbursementVoucherDocument.setDisbVchrCheckStubText(checkStubText);

    }

    /**
     * @see org.kuali.kfs.module.tem.document.TravelDocumentBase#getDisapprovedAppDocStatusMap()
     */
    @Override
    public Map<String, String> getDisapprovedAppDocStatusMap() {
        return EntertainmentStatusCodeKeys.getDisapprovedAppDocStatusMap();
    }

    /**
     * Returns ENCA
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#getAchCheckDocumentType()
     */
    @Override
    public String getAchCheckDocumentType() {
        return TemConstants.TravelDocTypes.ENTERTAINMENT_CHECK_ACH_DOCUMENT;
    }

    /**
     * This is the tale of ENWF, daughter of the scion of Gorn, fair lady of Vale of Shasteen, who took the sword in battle at the age of sixteen against the dark forces
     * of the armies of Rarrg, and who in that act of heroism, was separated from her noble house and was forced to wander the far stretches, having multiple adventures
     * and facing divers dangers, before finally defeating the king of Rarrg and taking her place as the Queen of the Eight Plains
     * @see org.kuali.kfs.module.tem.document.TEMReimbursementDocument#getWireTransferOrForeignDraftDocumentType()
     */
    @Override
    public String getWireTransferOrForeignDraftDocumentType() {
        return TemConstants.TravelDocTypes.ENTERTAINMENT_WIRE_OR_FOREIGN_DRAFT_DOCUMENT;
    }

}
