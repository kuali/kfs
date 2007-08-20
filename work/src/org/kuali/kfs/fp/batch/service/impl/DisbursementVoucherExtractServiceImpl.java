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
import java.util.Map;
import java.util.Set;

import org.kuali.core.bo.FinancialSystemParameter;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.UserNotFoundException;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.UniversalUserService;
import org.kuali.module.financial.bo.DisbursementVoucherPayeeDetail;
import org.kuali.module.financial.bo.Payee;
import org.kuali.module.financial.dao.DisbursementVoucherDao;
import org.kuali.module.financial.document.DisbursementVoucherDocument;
import org.kuali.module.financial.rules.DisbursementVoucherRuleConstants;
import org.kuali.module.financial.service.DisbursementVoucherExtractService;
import org.kuali.module.pdp.PdpConstants;
import org.kuali.module.pdp.bo.Batch;
import org.kuali.module.pdp.bo.CustomerProfile;
import org.kuali.module.pdp.bo.PaymentDetail;
import org.kuali.module.pdp.bo.PaymentGroup;
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

    private KualiConfigurationService kualiConfigurationService;
    private DisbursementVoucherDao disbursementVoucherDao;
    private DateTimeService dateTimeService;
    private UniversalUserService universalUserService;
    private CustomerProfileService customerProfileService;
    private PaymentFileService paymentFileService;
    private PaymentGroupService paymentGroupService;
    private ReferenceService referenceService;

    public boolean extractPayments() {
        LOG.debug("extractPayments() started");

        Date processRunDate = dateTimeService.getCurrentDate();

        String userId = kualiConfigurationService.getApplicationParameterValue(DisbursementVoucherRuleConstants.DV_PDP_EXTRACT_GROUP_NM, DisbursementVoucherRuleConstants.DvPdpExtractGroup.DV_PDP_USER_ID);
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

        Collection<DisbursementVoucherDocument> dvd = getListByDocumentStatusCodeCampus("A", campusCode);
        for (DisbursementVoucherDocument document : dvd) {
            addPayment(document,batch);
            count++;
            totalAmount = totalAmount.add(document.getDisbVchrCheckTotalAmount().bigDecimalValue());
        }

        batch.setPaymentCount(count);
        batch.setPaymentTotalAmount(totalAmount);
        paymentFileService.saveBatch(batch);
    }

    private void addPayment(DisbursementVoucherDocument document,Batch batch) {
        PaymentGroup pg = buildPaymentGroup(document,batch);
//        PaymentDetail pd = buildPaymentDetail(document,batch);

//        pd.setPaymentGroup(pg);
//        pg.addPaymentDetails(pd);
        paymentGroupService.save(pg);
    }

    private PaymentGroup buildPaymentGroup(DisbursementVoucherDocument document,Batch batch) {
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


        // pg.setProcessImmediate();
        pg.setPymtAttachment(document.isDisbVchrAttachmentCode());

        PaymentStatus open = (PaymentStatus)referenceService.getCode("PaymentStatus", PdpConstants.PaymentStatusCodes.OPEN);
        pg.setPaymentStatus(open);

        return pg;
    }

    private PaymentDetail buildPaymentDetail(DisbursementVoucherDocument document,Batch batch) {
        PaymentDetail pd = new PaymentDetail();
        return pd;
    }

    private Batch createBatch(String campusCode,UniversalUser user,Date processRunDate) {
        String orgCode = kualiConfigurationService.getApplicationParameterValue(DisbursementVoucherRuleConstants.DV_PDP_EXTRACT_GROUP_NM, DisbursementVoucherRuleConstants.DvPdpExtractGroup.DV_PDP_ORG_CODE);
        String subUnitCode = kualiConfigurationService.getApplicationParameterValue(DisbursementVoucherRuleConstants.DV_PDP_EXTRACT_GROUP_NM, DisbursementVoucherRuleConstants.DvPdpExtractGroup.DV_PDP_SBUNT_CODE);
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
        Map<String,FinancialSystemParameter> parms = kualiConfigurationService.getParametersByGroup(DisbursementVoucherRuleConstants.DV_PAYMENT_REASON_CAMPUS_OVERRIDE);

        Set<String> campusSet = new HashSet<String>();

        Collection docs = disbursementVoucherDao.getDocumentsByHeaderStatus(statusCode);
        for (Iterator iter = docs.iterator(); iter.hasNext();) {
            DisbursementVoucherDocument element = (DisbursementVoucherDocument)iter.next();

            String campusCode = element.getCampusCode();
            DisbursementVoucherPayeeDetail dvpd = element.getDvPayeeDetail();
            if ( dvpd != null ) {
                FinancialSystemParameter param = parms.get(dvpd.getDisbVchrPaymentReasonCode());
                if ( param != null ) {
                    campusCode = param.getFinancialSystemParameterText();
                }
                campusSet.add(campusCode);
            }
        }
        return campusSet;
    }

    private Collection<DisbursementVoucherDocument> getListByDocumentStatusCodeCampus(String statusCode,String campusCode) {
        LOG.debug("getListByDocumentStatusCodeCampus() started");

        // Get the campus overide values
        Map<String,FinancialSystemParameter> parms = kualiConfigurationService.getParametersByGroup(DisbursementVoucherRuleConstants.DV_PAYMENT_REASON_CAMPUS_OVERRIDE);

        Collection<DisbursementVoucherDocument> list = new ArrayList<DisbursementVoucherDocument>();

        Collection docs = disbursementVoucherDao.getDocumentsByHeaderStatus(statusCode);
        for (Iterator iter = docs.iterator(); iter.hasNext();) {
            DisbursementVoucherDocument element = (DisbursementVoucherDocument)iter.next();

            String dvdCampusCode = element.getCampusCode();

            DisbursementVoucherPayeeDetail dvpd = element.getDvPayeeDetail();
            if ( dvpd != null ) {
                FinancialSystemParameter param = parms.get(dvpd.getDisbVchrPaymentReasonCode());
                if ( param != null ) {
                    dvdCampusCode = param.getFinancialSystemParameterText();
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
