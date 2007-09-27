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
package org.kuali.module.financial.service.impl;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Parameter;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.bo.SourceAccountingLine;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeExpense;
import org.kuali.module.financial.bo.DisbursementVoucherNonEmployeeTravel;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.bo.DisbursementVoucherPreConferenceDetail;
import org.kuali.module.financial.bo.DisbursementVoucherPreConferenceRegistrant;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.dao.DisbursementVoucherDao;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;
import org.kuali.module.financial.service.DisbursementVoucherExtractService;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentAccountDetail;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
import org.kuali.module.pdp.bo.PaymentNoteText;
import org.kuali.module.pdp.bo.PaymentStatus;
import org.kuali.module.pdp.bo.PdpUser;
import org.kuali.module.pdp.service.CustomerProfileService;
import org.kuali.module.pdp.service.PaymentFileService;
import org.kuali.module.pdp.service.PaymentGroupService;
import org.kuali.module.pdp.service.ReferenceService;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class DisbursementVoucherExtractServiceImpl implements DisbursementVoucherExtractService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DisbursementVoucherExtractServiceImpl.class);

    private static final String CAMPUS_BY_PAYMENT_REASON_PARAM = "CAMPUS_BY_PAYMENT_REASON";
    
    private KualiConfigurationService kualiConfigurationService;
    private DisbursementVoucherDao disbursementVoucherDao;
    private DateTimeService dateTimeService;
    private UniversalUserService universalUserService;
    private CustomerProfileService customerProfileService;
    private PaymentFileService paymentFileService;
    private PaymentGroupService paymentGroupService;
    private ReferenceService referenceService;

    // This should only be set to true when testing this system.  Setting this to true will run the code but
    // won't set the doc status to extracted
    boolean testMode = false;

    public boolean extractPayments() {
        LOG.debug("extractPayments() started");

        Date processRunDate = dateTimeService.getCurrentDate();

        String userId = kualiConfigurationService.getParameterValue(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, DisbursementVoucherRuleConstants.DvPdpExtractGroup.DV_PDP_USER_ID);
        UniversalUser uuser;
        try {
            uuser = universalUserService.getUniversalUserByAuthenticationUserId(userId);
        }
        catch (UserNotFoundException e) {
            LOG.debug("extractPayments() Unable to find user " + userId);
            throw new IllegalArgumentException("Unable to find user " + userId);
        }

        // Get a list of campuses that have documents with an A status.
        Set<String> campusList = getCampusListByDocumentStatusCode("A");

        // Process each campus one at a time
        for (String campusCode : campusList) {
            extractPaymentsForCampus(campusCode, uuser, processRunDate);
        }

        return true;
    }

    private void extractPaymentsForCampus(String campusCode,UniversalUser user,Date processRunDate) {
        LOG.debug("extractPaymentsForCampus() started for campus: " + campusCode);

        Batch batch = createBatch(campusCode,user,processRunDate);
        Integer count = 0;
        BigDecimal totalAmount = new BigDecimal("0");

        Collection<DisbursementVoucherDocument> dvd = getListByDocumentStatusCodeCampus(DisbursementVoucherRuleConstants.DocumentStatusCodes.APPROVED, campusCode);
        for (DisbursementVoucherDocument document : dvd) {
            addPayment(document,batch,processRunDate);
            count++;
            totalAmount = totalAmount.add(document.getDisbVchrCheckTotalAmount().bigDecimalValue());
        }

        batch.setPaymentCount(count);
        batch.setPaymentTotalAmount(totalAmount);
        paymentFileService.saveBatch(batch);
    }

    private void addPayment(DisbursementVoucherDocument document,Batch batch,Date processRunDate) {
        LOG.debug("addPayment() started");

        PaymentGroup pg = buildPaymentGroup(document,batch);
        PaymentDetail pd = buildPaymentDetail(document,batch,processRunDate);

        pd.setPaymentGroup(pg);
        pg.addPaymentDetails(pd);
        paymentGroupService.save(pg);

        if ( ! testMode ) {
            document.getDocumentHeader().setFinancialDocumentStatusCode(DisbursementVoucherRuleConstants.DocumentStatusCodes.EXTRACTED);
            disbursementVoucherDao.save(document);
        }
    }

    private PaymentGroup buildPaymentGroup(DisbursementVoucherDocument document,Batch batch) {
        LOG.debug("buildPaymentGroup() started");

        PaymentGroup pg = new PaymentGroup();
        pg.setBatch(batch);
        pg.setCombineGroups(Boolean.TRUE);

        DisbursementVoucherPayeeDetail pd = document.getDvPayeeDetail();
        String rc = pd.getDisbVchrPaymentReasonCode();

        if ( pd.isVendor() ) {
            // TODO Write this when Vendor support is added

            pg.setTaxablePayment(Boolean.FALSE);
            
            // These are taxable
            //    ((VH.vndr_ownr_cd IN ('NP', 'FC')
            //            OR (VH.vndr_ownr_cd = 'CP' AND VH.vndr_ownr_ctgry_cd IS NULL))
            //    AND PD.dv_pmt_reas_cd IN ('H', 'J')) 
            //    OR 
            //    ((VH.vndr_ownr_cd = 'CP' AND VH.vndr_ownr_ctgry_cd = 'H')
            //    AND PD.dv_pmt_reas_cd IN ('C', 'E', 'H', 'L', 'J'))
            //    OR
            //    ((VH.vndr_ownr_cd IN ('NR', 'ID', 'PT')
            //            OR (VH.vndr_ownr_cd = 'CP' AND VH.vndr_ownr_ctgry_cd = 'H'))
            //    AND PD.dv_pmt_reas_cd IN ('A', 'C', 'E', 'H', 'R', 'T', 'X', 'Y', 'L', 'J')) 
            //    OR 
            //    ((VH.vndr_ownr_cd = 'CP' AND VH.vndr_ownr_ctgry_cd = 'L')
            //    AND PD.dv_pmt_reas_cd IN ('A', 'E', 'H', 'R', 'T', 'X', 'L', 'J')))
            pg.setTaxablePayment(Boolean.FALSE);
        } else if ( pd.isEmployee() ) {
            pg.setIuEmployee(Boolean.TRUE);
            pg.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.EMPLOYEE_ID);

            // All payments are taxable except research participant, rental & royalties
            pg.setTaxablePayment( (! DisbursementVoucherRuleConstants.PaymentReasonCodes.RESEARCH_PARTICIPANT.equals(rc)) &&
                    ( ! DisbursementVoucherRuleConstants.PaymentReasonCodes.RENTAL_PAYMENT.equals(rc)) &&
                    ( ! DisbursementVoucherRuleConstants.PaymentReasonCodes.ROYALTIES.equals(rc)) );
        } else if ( pd.isPayee() ) {
            pg.setIuEmployee(Boolean.FALSE);
            pg.setPayeeIdTypeCd(PdpConstants.PayeeIdTypeCodes.PAYEE_ID);

            Payee payee = disbursementVoucherDao.getPayee(pd.getDisbVchrPayeeIdNumber());
            String potc = payee.getPayeeOwnershipTypCd();

            // These determine if it is taxable
            pg.setTaxablePayment(Boolean.FALSE);
            if ( "C".equals(potc) && "HJ".indexOf(rc) >= 0 ) {
                pg.setTaxablePayment(Boolean.TRUE);
            }
            if ( "M".equals(potc) && "CEHLJ".indexOf(rc) >= 0 ) {
                pg.setTaxablePayment(Boolean.TRUE);
            }
            if ( "IPSH".indexOf(potc) >= 0 && "ACEHRTXYLJ".indexOf(rc) >= 0 ) {
                pg.setTaxablePayment(Boolean.TRUE);
            }
            if ( "L".equals(potc) && "AEHRTXLJ".indexOf(rc) >= 0 ) {
                pg.setTaxablePayment(Boolean.TRUE);
            }
        }

        pg.setCity(pd.getDisbVchrPayeeCityName());
        pg.setCountry(pd.getDisbVchrPayeeCountryCode());
        pg.setLine1Address(pd.getDisbVchrPayeeLine1Addr());
        pg.setLine2Address(pd.getDisbVchrPayeeLine2Addr());
        pg.setPayeeName(pd.getDisbVchrPayeePersonName());
        pg.setPayeeId(pd.getDisbVchrPayeeIdNumber());
        pg.setState(pd.getDisbVchrPayeeStateCode());
        pg.setZipCd(pd.getDisbVchrPayeeZipCode());

        pg.setPaymentDate(batch.getFileProcessTimestamp());

        // It doesn't look like the DV has a way to do immediates
        pg.setProcessImmediate(Boolean.FALSE);
        pg.setPymtAttachment(document.isDisbVchrAttachmentCode());
        pg.setPymtSpecialHandling(document.isDisbVchrSpecialHandlingCode());
        pg.setNraPayment(pd.isDisbVchrAlienPaymentCode());

        PaymentStatus open = (PaymentStatus)referenceService.getCode("PaymentStatus", PdpConstants.PaymentStatusCodes.OPEN);
        pg.setPaymentStatus(open);

        return pg;
    }

    private PaymentDetail buildPaymentDetail(DisbursementVoucherDocument document,Batch batch,Date processRunDate) {
        LOG.debug("buildPaymentDetail() started");

        PaymentDetail pd = new PaymentDetail();
        if ( StringUtils.isNotEmpty(document.getDocumentHeader().getOrganizationDocumentNumber()) ) {
            pd.setOrganizationDocNbr(document.getDocumentHeader().getOrganizationDocumentNumber());
        }
        pd.setCustPaymentDocNbr(document.getDocumentNumber());
        pd.setInvoiceDate(new Timestamp(processRunDate.getTime()));
        pd.setOrigInvoiceAmount(document.getDisbVchrCheckTotalAmount().bigDecimalValue());
        pd.setInvTotDiscountAmount(new BigDecimal("0"));
        pd.setInvTotOtherCreditAmount(new BigDecimal("0"));
        pd.setInvTotOtherDebitAmount(new BigDecimal("0"));
        pd.setInvTotShipAmount(new BigDecimal("0"));
        pd.setNetPaymentAmount(document.getDisbVchrCheckTotalAmount().bigDecimalValue());

        // Handle accounts
        if ( document.getSourceAccountingLines().size() == 0 ) {
            LOG.error("buildPaymentDetail() XXXXXXXXXXXXXXXXXXXXXXXXX No lines: " + document.getDocumentNumber());
        }
        for (Iterator iter = document.getSourceAccountingLines().iterator(); iter.hasNext();) {
            SourceAccountingLine sal = (SourceAccountingLine)iter.next();

            PaymentAccountDetail pad = new PaymentAccountDetail();
            pad.setFinChartCode(sal.getChartOfAccountsCode());
            pad.setAccountNbr(sal.getAccountNumber());
            if ( StringUtils.isNotEmpty(sal.getSubAccountNumber()) ) {
                pad.setSubAccountNbr(sal.getSubAccountNumber());
            } else {
                pad.setSubAccountNbr("-----");
            }
            pad.setFinObjectCode(sal.getFinancialObjectCode());
            if ( StringUtils.isNotEmpty(sal.getFinancialSubObjectCode()) ) {
                pad.setFinSubObjectCode(sal.getFinancialSubObjectCode());
            } else {
                pad.setFinSubObjectCode("---");
            }
            if ( StringUtils.isNotEmpty(sal.getOrganizationReferenceId()) ) {
                pad.setOrgReferenceId(sal.getOrganizationReferenceId());
            }
            if ( StringUtils.isNotEmpty(sal.getProjectCode()) ) {
                pad.setProjectCode(sal.getProjectCode());
            } else {
                pad.setProjectCode("----------");
            }
            pad.setAccountNetAmount(sal.getAmount().bigDecimalValue());
            pd.addAccountDetail(pad);
        }

        // Handle notes
        DisbursementVoucherPayeeDetail dvpd = document.getDvPayeeDetail();

        int line = 0;
        PaymentNoteText pnt = new PaymentNoteText();
        pnt.setCustomerNoteLineNbr(line++);
        pnt.setCustomerNoteText("Info: " + document.getDisbVchrContactPersonName() + " " + document.getDisbVchrContactPhoneNumber());
        pd.addNote(pnt);

        String dvRemitPersonName = null;
        String dvRemitLine1Address = null;
        String dvRemitLine2Address = null;
        String dvRemitCity = null;
        String dvRemitState = null;
        String dvRemitZip = null;
        
        if ( dvpd.isPayee() ) {
            Payee payee = disbursementVoucherDao.getPayee(dvpd.getDisbVchrPayeeIdNumber());
            dvRemitPersonName = payee.getPayeePersonName();
            dvRemitLine1Address = payee.getPayeeLine1Addr();
            dvRemitLine2Address = payee.getPayeeLine2Addr();
            dvRemitCity = payee.getPayeeCityName();
            dvRemitState = payee.getPayeeStateCode();
            dvRemitZip = payee.getPayeeZipCode();
        }

        if ( StringUtils.isNotEmpty(dvRemitPersonName) ) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(line++);
            pnt.setCustomerNoteText("Send Check To: " + dvRemitPersonName);
            pd.addNote(pnt);
        }
        if ( StringUtils.isNotEmpty(dvRemitLine1Address) ) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(line++);
            pnt.setCustomerNoteText(dvRemitLine1Address);
            pd.addNote(pnt);
        }
        if ( StringUtils.isNotEmpty(dvRemitLine2Address) ) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(line++);
            pnt.setCustomerNoteText(dvRemitLine2Address);
            pd.addNote(pnt);
        }
        if ( StringUtils.isNotEmpty(dvRemitCity) ) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(line++);
            pnt.setCustomerNoteText(dvRemitCity + ", " + dvRemitState + " " + dvRemitZip);
            pd.addNote(pnt);
        }
        if ( document.isDisbVchrAttachmentCode() ) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(line++);
            pnt.setCustomerNoteText("Attachment Included");
            pd.addNote(pnt);
        }

        String paymentReasonCode = dvpd.getDisbVchrPaymentReasonCode();
        if ( DisbursementVoucherRuleConstants.PaymentReasonCodes.TRAVEL_NONEMPLOYEE.equals(paymentReasonCode) || DisbursementVoucherRuleConstants.PaymentReasonCodes.TRAVEL_HONORARIUM.equals(paymentReasonCode) ) {
            DisbursementVoucherNonEmployeeTravel dvnet = document.getDvNonEmployeeTravel();
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(line++);
            pnt.setCustomerNoteText("Reimbursement associated with " + dvnet.getDisbVchrServicePerformedDesc());
            pd.addNote(pnt);

            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(line++);
            pnt.setCustomerNoteText("The total per diem amount for your daily expenses is " + dvnet.getDisbVchrPerdiemCalculatedAmt());
            pd.addNote(pnt);

            if ( dvnet.getDisbVchrPersonalCarAmount().compareTo(KualiDecimal.ZERO) != 0 ) {
                pnt = new PaymentNoteText();
                pnt.setCustomerNoteLineNbr(line++);
                pnt.setCustomerNoteText("The total dollar amount for your vehicle mileage is " + dvnet.getDisbVchrPersonalCarAmount());
                pd.addNote(pnt);

                for (Iterator iter = dvnet.getDvNonEmployeeExpenses().iterator(); iter.hasNext();) {
                    DisbursementVoucherNonEmployeeExpense exp = (DisbursementVoucherNonEmployeeExpense)iter.next();

                    if ( line < 19 ) {
                        pnt = new PaymentNoteText();
                        pnt.setCustomerNoteLineNbr(line++);
                        pnt.setCustomerNoteText(exp.getDisbVchrExpenseCompanyName() + " " + exp.getDisbVchrExpenseAmount());
                        pd.addNote(pnt);
                    }
               }
            }
        } else if ( DisbursementVoucherRuleConstants.PaymentReasonCodes.TRAVEL_PREPAID.equals(paymentReasonCode) ) {
            pnt = new PaymentNoteText();
            pnt.setCustomerNoteLineNbr(line++);
            pnt.setCustomerNoteText("Payment is for the following indviuals/charges:");
            pd.addNote(pnt);

            DisbursementVoucherPreConferenceDetail dvpcd = document.getDvPreConferenceDetail();

            for (Iterator iter = dvpcd.getDvPreConferenceRegistrants().iterator(); iter.hasNext();) {
                DisbursementVoucherPreConferenceRegistrant dvpcr = (DisbursementVoucherPreConferenceRegistrant)iter.next();
                
                if ( line < 19 ) {
                    pnt = new PaymentNoteText();
                    pnt.setCustomerNoteLineNbr(line++);
                    pnt.setCustomerNoteText(dvpcr.getDvConferenceRegistrantName() + " " + dvpcr.getDisbVchrExpenseAmount());
                    pd.addNote(pnt);
                }
            }
        }

        String text = document.getDisbVchrCheckStubText();
        if ( text.length() > 0 ) {
            String[] lines = text.split("\\n");
            for (int i = 0; i < lines.length; i++) {
                if ( line < 24 ) {
                    pnt = new PaymentNoteText();
                    pnt.setCustomerNoteLineNbr(line++);
                    pnt.setCustomerNoteText(lines[i]);
                    pd.addNote(pnt);
                }
            }
        }
        return pd;
    }

    private Batch createBatch(String campusCode,UniversalUser user,Date processRunDate) {
        String orgCode = kualiConfigurationService.getParameterValue(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, DisbursementVoucherRuleConstants.DvPdpExtractGroup.DV_PDP_ORG_CODE);
        String subUnitCode = kualiConfigurationService.getParameterValue(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, DisbursementVoucherRuleConstants.DvPdpExtractGroup.DV_PDP_SBUNT_CODE);
        CustomerProfile customer = customerProfileService.get(campusCode, orgCode, subUnitCode);
        if ( customer == null ) {
            throw new IllegalArgumentException("Unable to find customer profile for " + campusCode + "/" + orgCode + "/" + subUnitCode);
        }

        // Create the group for this campus
        Batch batch = new Batch();
        batch.setCustomerProfile(customer);
        batch.setCustomerFileCreateTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setFileProcessTimestamp(new Timestamp(processRunDate.getTime()));
        batch.setPaymentFileName("from_dv");
        batch.setSubmiterUser(new PdpUser(user));

        // Set these for now, we will update them later
        batch.setPaymentCount(0);
        batch.setPaymentTotalAmount(new BigDecimal("0"));
        paymentFileService.saveBatch(batch);

        return batch;
    }

    private Set<String> getCampusListByDocumentStatusCode(String statusCode) {
        LOG.debug("getCampusListByDocumentStatusCode() started");

        // Get the campus overide values        
        Parameter campusByPaymentReasonConstraint = kualiConfigurationService.getParameter(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, CAMPUS_BY_PAYMENT_REASON_PARAM);
        
        Set<String> campusSet = new HashSet<String>();

        Collection docs = disbursementVoucherDao.getDocumentsByHeaderStatus(statusCode);
        for (Iterator iter = docs.iterator(); iter.hasNext();) {
            DisbursementVoucherDocument element = (DisbursementVoucherDocument)iter.next();

            String campusCode = element.getCampusCode();
            DisbursementVoucherPayeeDetail dvpd = element.getDvPayeeDetail();
            if ( dvpd != null ) {
                List<String> campusCodes = kualiConfigurationService.getConstrainedValues(campusByPaymentReasonConstraint, dvpd.getDisbVchrPaymentReasonCode() );
                if ( campusCodes.size() > 0 && StringUtils.isNotBlank( campusCodes.get(0) ) ) {
                    campusCode = campusCodes.get(0);
                }
                campusSet.add(campusCode);
            }
        }
        return campusSet;
    }

    private Collection<DisbursementVoucherDocument> getListByDocumentStatusCodeCampus(String statusCode,String campusCode) {
        LOG.debug("getListByDocumentStatusCodeCampus() started");

        // Get the campus overide values        
        Parameter campusByPaymentReasonConstraint = kualiConfigurationService.getParameter(KFSConstants.FINANCIAL_NAMESPACE, KFSConstants.Components.DISBURSEMENT_VOUCHER_DOC, CAMPUS_BY_PAYMENT_REASON_PARAM);
            
        Collection<DisbursementVoucherDocument> list = new ArrayList<DisbursementVoucherDocument>();

        Collection docs = disbursementVoucherDao.getDocumentsByHeaderStatus(statusCode);
        for (Iterator iter = docs.iterator(); iter.hasNext();) {
            DisbursementVoucherDocument element = (DisbursementVoucherDocument)iter.next();

            String dvdCampusCode = element.getCampusCode();

            DisbursementVoucherPayeeDetail dvpd = element.getDvPayeeDetail();
            if ( dvpd != null ) {
                List<String> campusCodes = kualiConfigurationService.getConstrainedValues(campusByPaymentReasonConstraint, dvpd.getDisbVchrPaymentReasonCode() );
                if ( campusCodes.size() > 0 && StringUtils.isNotBlank( campusCodes.get(0) ) ) {
                    dvdCampusCode = campusCodes.get(0);
                }
            }

            if ( dvdCampusCode.equals(campusCode) ) {
                list.add(element);
            }
        }
        return list;
    }

    public void setDisbursementVoucherDao(DisbursementVoucherDao disbursementVoucherDao) {
        this.disbursementVoucherDao = disbursementVoucherDao;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setUniversalUserService(UniversalUserService universalUserService) {
        this.universalUserService = universalUserService;
    }

    public void setCustomerProfileService(CustomerProfileService customerProfileService) {
        this.customerProfileService = customerProfileService;
    }

    public void setPaymentFileService(PaymentFileService paymentFileService) {
        this.paymentFileService = paymentFileService;
    }

    public void setPaymentGroupService(PaymentGroupService paymentGroupService) {
        this.paymentGroupService = paymentGroupService;
    }

    public void setReferenceService(ReferenceService rs) {
        this.referenceService = rs;
    }
}
