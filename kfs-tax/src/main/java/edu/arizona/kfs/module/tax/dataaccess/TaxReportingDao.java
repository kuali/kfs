package edu.arizona.kfs.module.tax.dataaccess;

import java.sql.Timestamp;
import java.util.List;

import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.vnd.businessobject.VendorDetail;

import edu.arizona.kfs.module.tax.businessobject.DocumentPaymentInformation;
import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.businessobject.Payment;
import edu.arizona.kfs.module.tax.businessobject.PaymentDetailSearch;

public interface TaxReportingDao {
    /**
     * Retrieve all Payments based on the search criteria.
     */
    public List<PaymentDetail> getAllPaymentsForSearchCriteria(PaymentDetailSearch pds);

    /**
     * Retrieve all Payments between the dates given for the given tax year.
     */
    public List<Payment> getAllExistingPayments(Timestamp startDt, Timestamp endDt, Integer taxYear);

    /**
     * Retrieve Vendors based on the vendorOwnershipCode.
     * 
     * @param isAllow
     *            true indicates whether the vendorOwnershipCategoryCode may be null.
     */
    public List<VendorDetail> getVendors(String vendorOwnershipCode, String vendorOwnershipCategoryCode, boolean isAllow);

    /**
     * Retrieve the PuraapIdentifier from the given document number, assuming the given class type.
     */
    @SuppressWarnings("rawtypes")
    public Integer getPurapIdentifierFromDocumentNumber(Class clazz, String documentNumber);

    /**
     * Retrieve the payment information from the given document.
     */
    public DocumentPaymentInformation getDocumentPaymentInformation(String documentType, String documentNumber);

    /**
     * Retrieve the Non-PDP DV document numbers for the given vendor.
     */
    public List<String> getNonPdpDVDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp taxYearStartDate, Timestamp taxYearEndDate, boolean b);

    /**
     * Retrieve the Non-PDP PREQ document numbers for the given vendor.
     */
    public List<String> getNonPdpPreqDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp taxYearStartDate, Timestamp taxYearEndDate, boolean b);

    /**
     * Retrieve the Non-PDP CM document numbers for the given vendor.
     */
    public List<String> getNonPdpCMDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp taxYearStartDate, Timestamp taxYearEndDate, boolean b);

    /**
     * Retrieve the Payees based on the given criteria.
     */
    public List<Payee> getPayees(String vendorName, String headerTaxNumber, String vendorNumber, Integer taxYear);

}
