/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.pdp.service.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.ExtractionUnit;
import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.pdp.dataaccess.PaymentDetailDao;
import org.kuali.kfs.pdp.service.PaymentDetailService;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.krad.service.BusinessObjectService;

@NonTransactional
public class PaymentDetailServiceImpl implements PaymentDetailService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentDetailServiceImpl.class);

    private PaymentDetailDao paymentDetailDao;
    private BusinessObjectService businessObjectService;

    public void setPaymentDetailDao(PaymentDetailDao c) {
        paymentDetailDao = c;
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentDetailService#getByDisbursementNumber(java.lang.Integer)
     */
    @Override
    public Iterator getByDisbursementNumber(Integer disbursementNumber) {
        LOG.debug("getByDisbursementNumber() started");

        Map fieldValues = new HashMap();
        fieldValues.put(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER, disbursementNumber);
        List<PaymentDetail> paymentDetailByDisbursementNumberList= (List<PaymentDetail>)this.businessObjectService.findMatching(PaymentDetail.class, fieldValues);
        DynamicCollectionComparator.sort(paymentDetailByDisbursementNumberList, PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_FINANCIAL_DOCUMENT_TYPE_CODE, PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_CUST_PAYMENT_DOC_NBR);

        return paymentDetailByDisbursementNumberList.iterator();
    }

    /**
     * Returns all PaymentDetail records with the given disbursement number and a group with the given process id, disbursement type, and bank code
     * @param disbursementNumber the disbursement number of the payment details to find
     * @param processId the process id of the payment group of payment details to find
     * @param disbursementType the disbursement type of the payment group of payment details to find
     * @param bankCode the bank code of the payment group of payment details to find
     * @return an iterator of PaymentDetail records matching the given criteria
     */
    @Override
    public Iterator<PaymentDetail> getByDisbursementNumber(Integer disbursementNumber, Integer processId, String disbursementType, String bankCode) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("getByDisbursementNumber() started");
        }

        Map fieldValues = new HashMap();
        fieldValues.put(PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_NUMBER, disbursementNumber);
        fieldValues.put(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP+"."+PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PROCESS_ID, processId);
        fieldValues.put(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP+"."+PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE, disbursementType);
        fieldValues.put(PdpPropertyConstants.PaymentDetail.PAYMENT_GROUP+"."+PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_BANK_CODE, bankCode);
        List<PaymentDetail> paymentDetailByDisbursementNumberList= (List<PaymentDetail>)this.businessObjectService.findMatching(PaymentDetail.class, fieldValues);
        DynamicCollectionComparator.sort(paymentDetailByDisbursementNumberList, PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_FINANCIAL_DOCUMENT_TYPE_CODE, PdpPropertyConstants.PaymentDetail.PAYMENT_DISBURSEMENT_CUST_PAYMENT_DOC_NBR);

        return paymentDetailByDisbursementNumberList.iterator();
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentDetailService#getUnprocessedCancelledDetails(java.util.List)
     */
    @Override
    public Iterator getUnprocessedCancelledDetails(List<ExtractionUnit> extractionUnits) {
        LOG.debug("getUnprocessedCancelledDetails() started");

        return paymentDetailDao.getUnprocessedCancelledDetails(extractionUnits);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentDetailService#getUnprocessedPaidDetails(java.util.List)
     */
    @Override
    public Iterator getUnprocessedPaidDetails(List<ExtractionUnit> extractionUnits) {
        LOG.debug("getUnprocessedPaidDetails() started");

        return paymentDetailDao.getUnprocessedPaidDetails(extractionUnits);
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
