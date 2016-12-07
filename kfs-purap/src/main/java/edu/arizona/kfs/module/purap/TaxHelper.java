package edu.arizona.kfs.module.purap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.fp.document.DisbursementVoucherConstants;

import edu.arizona.kfs.fp.document.DisbursementVoucherDocument;
import edu.arizona.kfs.module.purap.document.PaymentRequestDocument;
import edu.arizona.kfs.module.purap.document.VendorCreditMemoDocument;
import edu.arizona.kfs.tax.TaxPropertyConstants;

/**
 * This Helper class helps to determine if a given Document Type (as a String) or Document Class (as a Class) is usable in the 1099 Reporting Module.
 * 
 * @author akost
 */
public class TaxHelper {

	/**
	 * Is the documentType a Payment Request?
	 * 
	 * @param documentType
	 * @return
	 */
	public static boolean isPreq(String documentType) {
		return (PurapConstants.PurapDocTypeCodes.PAYMENT_REQUEST_DOCUMENT.equals(documentType) || PaymentRequestDocument.DOCUMENT_TYPE_NON_CHECK.equals(documentType));
	}

	/**
	 * Is the documentType a Credit Memo?
	 * 
	 * @param documentType
	 * @return
	 */
	public static boolean isCm(String documentType) {
		return PurapConstants.PurapDocTypeCodes.CREDIT_MEMO_DOCUMENT.equals(documentType);
	}

	/**
	 * Is the documentType a Disbursement Voucher?
	 * 
	 * @param documentType
	 * @return
	 */
	public static boolean isDv(String documentType) {
		return (DisbursementVoucherConstants.DOCUMENT_TYPE_CHECKACH.equals(documentType) || TaxPropertyConstants.DV_DOCUMENT_TYPE_CODE.equals(documentType) || DisbursementVoucherDocument.DOCUMENT_TYPE_DV_NON_CHECK.equals(documentType));
	}

	/**
	 * is the class a Payment Request Document?
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isPreqClass(Class clazz) {
		return PaymentRequestDocument.class.equals(clazz);
	}

	/**
	 * Is the class a Credit Memo Document?
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isCmClass(Class clazz) {
		return VendorCreditMemoDocument.class.equals(clazz);
	}

	/**
	 * Is the class a Disbursement Voucher Document?
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isDvClass(Class clazz) {
		return DisbursementVoucherDocument.class.equals(clazz);
	}

	/**
	 * Get the class used for the given documentType.
	 * 
	 * @param documentType
	 * @return
	 */
	public static Class getClassForDocumentType(String documentType) {
		Class retval = null;

		if (StringUtils.isNotBlank(documentType)) {
			if (isPreq(documentType)) {
				retval = PaymentRequestDocument.class;
			} else if (isCm(documentType)) {
				retval = VendorCreditMemoDocument.class;
			} else if (isDv(documentType)) {
				retval = DisbursementVoucherDocument.class;
			}
		}

		return retval;
	}

	/**
	 * Is the Class a Valid Income Type Documenet Class?
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isValidIncomeTypeDocumentClass(Class clazz) {
		return ((clazz != null) && (isCmClass(clazz) || isPreqClass(clazz) || isDvClass(clazz)));
	}

	/**
	 * Is the Class a KFS-PURAP Document Class?
	 * 
	 * @param clazz
	 * @return
	 */
	public static boolean isValidPurapDocumentClass(Class clazz) {
		return ((clazz != null) && (isCmClass(clazz) || isPreqClass(clazz)));
	}

}
