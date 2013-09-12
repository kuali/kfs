/*
 * Copyright 2013 The Kuali Foundation.
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
package org.kuali.kfs.module.tem.document.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.tem.businessobject.TravelPayment;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService;
import org.kuali.kfs.pdp.PdpConstants;
import org.kuali.kfs.pdp.businessobject.PaymentAccountDetail;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentNoteText;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.businessobject.AccountingLine;
import org.kuali.kfs.sys.document.service.PaymentSourceHelperService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.core.api.util.type.KualiInteger;
import org.kuali.rice.kew.api.WorkflowDocument;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.krad.bo.DocumentHeader;
import org.kuali.rice.krad.util.GlobalVariables;
import org.kuali.rice.krad.workflow.service.WorkflowDocumentService;

/**
 * Base implementation of the TravelPaymentsHelperService
 */
public class TravelPaymentsHelperServiceImpl implements TravelPaymentsHelperService {
    org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(TravelPaymentsHelperServiceImpl.class);

    protected PersonService personService;
    protected WorkflowDocumentService workflowDocumentService;
    protected PaymentSourceHelperService paymentSourceHelperService;

    /**
     * Retrieves the campus code associated with the initiator of a passed in authorization document
     * @param document the authorization document to find a campus for
     * @param initiatorCampuses the cache of document initiator principal keys to campus codes
     * @return the campus code associated with the initiator of the given document
     */
    @Override
    public String findCampusForDocument(TravelDocument document, Map<String, String> initiatorCampuses) {
        try {
            final WorkflowDocument workflowDocument = getWorkflowDocumentService().loadWorkflowDocument(document.getDocumentNumber(), GlobalVariables.getUserSession().getPerson());
            return findCampusForInitiator(workflowDocument.getInitiatorPrincipalId(), initiatorCampuses);
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Could not load document: "+document.getDocumentNumber(), we);
        }
    }

    /**
     * Retrieves and caches the campus code for the given initiator's principal id
     * @param initiatorPrincipalId the principal id of the initiator of a document
     * @param initiatorCampuses the cache of initiator principal keys to campus codes
     * @return the campus code associated with the given principal id
     */
    protected String findCampusForInitiator(String initiatorPrincipalId, Map<String, String> initiatorCampuses) {
        if (!StringUtils.isBlank(initiatorCampuses.get(initiatorPrincipalId))) {
            return initiatorCampuses.get(initiatorPrincipalId);
        }
        final Person initiatorPerson = getPersonService().getPerson(initiatorPrincipalId);
        final String campusCode = initiatorPerson.getCampusCode();
        if (!StringUtils.isBlank(campusCode)) {
            initiatorCampuses.put(initiatorPrincipalId, campusCode);
        }
        return campusCode;
    }

    /**
     * Returns the initiator for the initiator of this document
     * @see org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService#getInitiator(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public Person getInitiator(TravelDocument document) {
        try {
            final WorkflowDocument workflowDocument = getWorkflowDocumentService().loadWorkflowDocument(document.getDocumentNumber(), GlobalVariables.getUserSession().getPerson());
            return getPersonService().getPerson(workflowDocument.getInitiatorPrincipalId());
        }
        catch (WorkflowException we) {
            throw new RuntimeException("Could not load document: "+document.getDocumentNumber(), we);
        }
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService#buildGenericPaymentGroup(org.kuali.kfs.module.tem.businessobject.TravelerDetail, org.kuali.kfs.module.tem.businessobject.TravelPayment, java.lang.String)
     */
    @Override
    public PaymentGroup buildGenericPaymentGroup(TravelerDetail traveler, TravelPayment payment, String bankCode) {
        PaymentGroup pg = new PaymentGroup();
        pg.setCombineGroups(Boolean.TRUE);
        pg.setCampusAddress(Boolean.FALSE);

        pg.setCity(traveler.getCityName());
        pg.setCountry(traveler.getCountryCode());
        pg.setLine1Address(traveler.getStreetAddressLine1());
        pg.setLine2Address(traveler.getStreetAddressLine2());
        pg.setPayeeName(traveler.getFirstName() + " " + traveler.getLastName());
        pg.setState(traveler.getStateCode());
        pg.setZipCd(traveler.getZipCode());
        pg.setPaymentDate(payment.getDueDate());
        pg.setProcessImmediate(payment.isImmediatePaymentIndicator());
        pg.setPymtAttachment(payment.isAttachmentCode());
        pg.setPymtSpecialHandling(payment.isSpecialHandlingCode());
        pg.setNraPayment(payment.isAlienPaymentCode());

        pg.setBankCode(bankCode);
        pg.setPaymentStatusCode(PdpConstants.PaymentStatusCodes.OPEN);

        return pg;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService#buildGenericPaymentDetail(org.kuali.rice.krad.bo.DocumentHeader, java.sql.Date, org.kuali.kfs.module.tem.businessobject.TravelPayment, org.kuali.rice.kim.api.identity.Person, java.lang.String)
     */
    @Override
    public PaymentDetail buildGenericPaymentDetail(DocumentHeader documentHeader, Date processDate, TravelPayment travelPayment, Person initiator, String achCheckDocumentType) {
        PaymentDetail pd = new PaymentDetail();
        if (StringUtils.isNotEmpty(documentHeader.getOrganizationDocumentNumber())) {
            pd.setOrganizationDocNbr(documentHeader.getOrganizationDocumentNumber());
        }
        pd.setCustPaymentDocNbr(documentHeader.getDocumentNumber());
        pd.setInvoiceDate(new java.sql.Date(processDate.getTime()));
        pd.setOrigInvoiceAmount(travelPayment.getCheckTotalAmount());
        pd.setInvTotDiscountAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherCreditAmount(KualiDecimal.ZERO);
        pd.setInvTotOtherDebitAmount(KualiDecimal.ZERO);
        pd.setInvTotShipAmount(KualiDecimal.ZERO);
        pd.setNetPaymentAmount(travelPayment.getCheckTotalAmount());
        pd.setPrimaryCancelledPayment(Boolean.FALSE);
        pd.setFinancialDocumentTypeCode(achCheckDocumentType);
        pd.setFinancialSystemOriginCode(KFSConstants.ORIGIN_CODE_KUALI);

        int line = 0;
        PaymentNoteText pnt = new PaymentNoteText();
        pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
        pnt.setCustomerNoteText("Info: " + initiator.getPrincipalName() + " " + initiator.getPhoneNumber());
        pd.addNote(pnt);

        if (StringUtils.isNotEmpty(travelPayment.getSpecialHandlingPersonName())) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Send Check To: " + travelPayment.getSpecialHandlingPersonName());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling person name note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(travelPayment.getSpecialHandlingLine1Addr())) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(travelPayment.getSpecialHandlingLine1Addr());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling address 1 note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(travelPayment.getSpecialHandlingLine2Addr())) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(travelPayment.getSpecialHandlingLine2Addr());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling address 2 note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (StringUtils.isNotEmpty(travelPayment.getSpecialHandlingCityName())) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText(travelPayment.getSpecialHandlingCityName() + ", " + travelPayment.getSpecialHandlingStateCode() + " " + travelPayment.getSpecialHandlingZipCode());
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating special handling city note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        if (travelPayment.isAttachmentCode()) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(new KualiInteger(line++));
            pnt.setCustomerNoteText("Attachment Included");
            if (LOG.isDebugEnabled()) {
                LOG.debug("create attachment note: "+pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }

        final String text = travelPayment.getCheckStubText();
        if (!StringUtils.isBlank(text)) {
            pnt = getPaymentSourceHelperService().buildNoteForCheckStubText(text, line);
            if (LOG.isDebugEnabled()) {
                LOG.debug("Creating check stub text note: " + pnt.getCustomerNoteText());
            }
            pd.addNote(pnt);
        }
        return pd;
    }

    /**
     *
     * @see org.kuali.kfs.module.tem.document.service.TravelPaymentsHelperService#buildGenericPaymentAccountDetails(java.util.List)
     */
    @Override
    public List<PaymentAccountDetail> buildGenericPaymentAccountDetails(List<? extends AccountingLine> accountingLines) {
        List<PaymentAccountDetail> details = new ArrayList<PaymentAccountDetail>();
        for (AccountingLine accountingLine : accountingLines) {
            PaymentAccountDetail pad = new PaymentAccountDetail();
            pad.setFinChartCode(accountingLine.getChartOfAccountsCode());
            pad.setAccountNbr(accountingLine.getAccountNumber());
            if (!StringUtils.isBlank(accountingLine.getSubAccountNumber())) {
                pad.setSubAccountNbr(accountingLine.getSubAccountNumber());
            }
            else {
                pad.setSubAccountNbr(KFSConstants.getDashSubAccountNumber());
            }
            pad.setFinObjectCode(accountingLine.getFinancialObjectCode());
            if (!StringUtils.isBlank(accountingLine.getFinancialSubObjectCode())) {
                pad.setFinSubObjectCode(accountingLine.getFinancialSubObjectCode());
            }
            else {
                pad.setFinSubObjectCode(KFSConstants.getDashFinancialSubObjectCode());
            }
            if (!StringUtils.isBlank(accountingLine.getOrganizationReferenceId())) {
                pad.setOrgReferenceId(accountingLine.getOrganizationReferenceId());
            }
            if (!StringUtils.isBlank(accountingLine.getProjectCode())) {
                pad.setProjectCode(accountingLine.getProjectCode());
            }
            else {
                pad.setProjectCode(KFSConstants.getDashProjectCode());
            }
            pad.setAccountNetAmount(accountingLine.getAmount());
            details.add(pad);
        }
        return details;
    }

    /**
     * @return an implementation of the PersonService
     */
    public PersonService getPersonService() {
        return personService;
    }

    /**
     * Sets the implementation of the PersonService for this service to use
     * @param parameterService an implementation of PersonService
     */
    public void setPersonService(PersonService personService) {
        this.personService = personService;
    }

    /**
     * @return an implementation of the WorkflowDocumentService
     */
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    /**
     * Sets the implementation of the WorkflowDocumentService for this service to use
     * @param parameterService an implementation of WorkflowDocumentService
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * @return an implementation of the PaymentSourceHelperService
     */
    public PaymentSourceHelperService getPaymentSourceHelperService() {
        return paymentSourceHelperService;
    }

    /**
     * Sets the implementation of the PaymentSourceHelperService for this service to use
     * @param paymentSourceHelperService an implementation of PaymentSourceHelperService
     */
    public void setPaymentSourceHelperService(PaymentSourceHelperService paymentSourceHelperService) {
        this.paymentSourceHelperService = paymentSourceHelperService;
    }
}
