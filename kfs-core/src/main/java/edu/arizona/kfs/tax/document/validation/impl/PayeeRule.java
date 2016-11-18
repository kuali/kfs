package edu.arizona.kfs.tax.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.VendorConstants;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.maintenance.MaintenanceDocument;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.tax.TaxPropertyConstants;
import edu.arizona.kfs.tax.businessobject.Payee;
import edu.arizona.kfs.tax.util.RecordUtil;

public class PayeeRule extends MaintenanceDocumentRuleBase {
	private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeRule.class);

	protected Payee oldPayee;
	protected Payee newPayee;

	@Override
	public void setupConvenienceObjects() {
		oldPayee = (Payee) super.getOldBo();
		newPayee = (Payee) super.getNewBo();
	}

	protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
		LOG.info("processCustomSaveDocumentBusinessRules called");
		// call the route rules to report all of the messages, but ignore the result
		processCustomRouteDocumentBusinessRules(document);

		// Save always succeeds, even if there are business rule failures
		return true;
	}

	protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
		LOG.info("processCustomRouteDocumentBusinessRules called");
		setupConvenienceObjects();

		boolean valid = true;

		if (StringUtils.equals(KFSConstants.COUNTRY_CODE_UNITED_STATES, newPayee.getAddressCountryCode())) {
			if (!RecordUtil.isValidStateCode(newPayee.getAddressStateCode())) {
				putFieldError("addressStateCode", "error.tax.statecode");
				valid = false;
			}
			if (!RecordUtil.isValidZipCode(newPayee.getAddressZipCode())) {
				putFieldError("addressZipCode", "error.tax.zipcode");
				valid = false;
			}
		}

		VendorService vendorService = SpringContext.getBean(VendorService.class);

		if (vendorService.getByVendorNumber(newPayee.getVendorNumber()) == null) {
			putFieldError("vendorNumber", "error.tax.vendornum");
		}

		if (newPayee.getVendorHeaderGeneratedIdentifier() == null) {
			putFieldError("vendorNumber", "error.tax.vendornum.lookup");
		}

		valid &= newTaxIdIsAvailable(newPayee.getHeaderTaxNumber(), newPayee.getVendorNumber(), newPayee.getTaxYear());

		return valid;
	}

	protected boolean newTaxIdIsAvailable(String newTaxId, String newVendorId, Integer newTaxYear) {
		if (StringUtils.isBlank(newTaxId) || StringUtils.isBlank(newVendorId) || StringUtils.isBlank(newTaxYear.toString())) {
			return true;
		}
		boolean result = true;
		BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);

		Map<String, Object> searchValues = new HashMap<String, Object>();
		searchValues.put(TaxPropertyConstants.TAX_YEAR, newTaxYear);
		searchValues.put(TaxPropertyConstants.HEADER_TAX_NUMBER, newTaxId);
		Collection<Payee> payees = boService.findMatching(Payee.class, searchValues);

		// if any other payees in the same TaxYear have the same TaxNumber, but a different VendorNumber, then fail.
		for (Payee payee : payees) {
			String existingVendorNumber = payee.getVendorNumber();
			if (!this.hasSameVendorHeaderGeneratedIdentifier(newVendorId, existingVendorNumber)) {
				result = false;
				putFieldError(TaxPropertyConstants.HEADER_TAX_NUMBER, KFSKeyConstants.PAYEE_TAXID_USED_BY_DIFF_VENDOR, payee.getVendorNumber());
				break;
			}
		}
		return result;
	}

	// determine whether the given two vendor numbers has the same vendor header id
	protected boolean hasSameVendorHeaderGeneratedIdentifier(String newVendorNumber, String existingVendorNumber) {
		String headerIdOfNewVendor = StringUtils.substringBefore(newVendorNumber, VendorConstants.DASH);
		String headerIdOfExistingVendor = StringUtils.substringBefore(existingVendorNumber, VendorConstants.DASH);

		return StringUtils.equalsIgnoreCase(headerIdOfNewVendor, headerIdOfExistingVendor);
	}
}
