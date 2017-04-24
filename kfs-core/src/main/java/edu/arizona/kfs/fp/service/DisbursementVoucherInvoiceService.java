package edu.arizona.kfs.fp.service;

import java.util.List;

public interface DisbursementVoucherInvoiceService {
    /**
     * Returns a set of document IDs that use the same payee ID/type/invoice
     * number
     * 
     * @param payeeId
     *            employee ID or vendor number
     * @param payeeType
     *            the payee type code stored in
     *            {@link org.kuali.kfs.fp.businessobject.DisbursementVoucherPayeeDetail#setDisbursementVoucherPayeeTypeCode(String)}
     * @param invoiceNumber
     *            should not be null
     * @return
     */
    public List<String> findDisbursementVouchersWithInvoiceNumber(String payeeId, String payeeType, String invoiceNumber);

}
