package edu.arizona.kfs.module.purap.document.dataaccess;

import java.sql.Timestamp;
import java.util.List;

import org.kuali.kfs.pdp.businessobject.PaymentDetail;
import org.kuali.kfs.vnd.businessobject.VendorDetail;

import edu.arizona.kfs.tax.businessobject.Payee;
import edu.arizona.kfs.tax.businessobject.Payment;
import edu.arizona.kfs.tax.businessobject.PaymentDetailSearch;
import edu.arizona.kfs.tax.document.web.struts.PayeeSearchForm;
import edu.arizona.kfs.tax.service.impl.DocumentPaymentInformation;

public interface TaxReporting1099Dao {

	public List<VendorDetail> getVendors(String vendorOwnershipCode, String vendorOwnershipCategoryCode, boolean isAllow);

	public List<Payee> getPayees(PayeeSearchForm form);

	public List<String> getObjectCodes(List<String> values, String type);

	public List<Payment> getAllExistingPayments(Timestamp startDt, Timestamp endDt, Integer taxYear);

	public List<PaymentDetail> getAllPaymentsForSearchCriteria(PaymentDetailSearch pds);

	public Integer getPurapIdentifierFromDocumentNumber(Class clazz, String documentNumber);

	public DocumentPaymentInformation getDocumentPaymentInformation(Class clazz, String documentNumber);

	public List<String> getNonPdpDVDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp startDate, Timestamp endDate, boolean newEntriesOnly);

	public List<String> getNonPdpPreqDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp startDate, Timestamp endDate, boolean newEntriesOnly);

	public List<String> getNonPdpCMDocumentNumbersForVendor(Integer vendorHeaderGeneratedIdentifier, Integer vendorDetailAssignedIdentifier, Timestamp startDate, Timestamp endDate, boolean newEntriesOnly);

}
