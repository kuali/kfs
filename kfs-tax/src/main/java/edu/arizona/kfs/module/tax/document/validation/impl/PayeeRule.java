package edu.arizona.kfs.module.tax.document.validation.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.document.service.VendorService;
import org.kuali.rice.kns.document.MaintenanceDocument;
import org.kuali.rice.kns.maintenance.rules.MaintenanceDocumentRuleBase;
import org.kuali.rice.krad.service.BusinessObjectService;

import edu.arizona.kfs.module.tax.TaxKeyConstants;
import edu.arizona.kfs.module.tax.TaxPropertyConstants;
import edu.arizona.kfs.module.tax.businessobject.Payee;
import edu.arizona.kfs.module.tax.util.RecordUtil;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.vnd.VendorConstants;

/**
 * This class is the business rule validation for the Payer Maintenance Document (TXPR). It makes use of
 * the deprecated Maintenance Document and MaintenanceDocumentRuleBase because the current version of Rice
 * requires it.
 * 
 * @author Refactored by kosta@email.arizona.edu.
 */
@SuppressWarnings("deprecation")
public class PayeeRule extends MaintenanceDocumentRuleBase {

    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeRule.class);

    protected Payee oldPayee;
    protected Payee newPayee;

    @Override
    public void setupConvenienceObjects() {
        oldPayee = (Payee) super.getOldBo();
        newPayee = (Payee) super.getNewBo();
    }

    @Override
    protected boolean processCustomSaveDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomSaveDocumentBusinessRules called");
        // call the route rules to report all of the messages, but ignore the result
        processCustomRouteDocumentBusinessRules(document);

        // Save always succeeds, even if there are business rule failures
        return true;
    }

    @Override
    protected boolean processCustomRouteDocumentBusinessRules(MaintenanceDocument document) {
        LOG.info("processCustomRouteDocumentBusinessRules called");
        setupConvenienceObjects();

        boolean valid = true;

        if (StringUtils.equals(KFSConstants.COUNTRY_CODE_UNITED_STATES, newPayee.getAddressCountryCode())) {
            if (!RecordUtil.isValidStateCode(newPayee.getAddressStateCode())) {
                putFieldError(TaxPropertyConstants.PayeeFields.ADDRESS_STATE_CODE, TaxKeyConstants.ERROR_TAX_STATECODE);
                valid = false;
            }
            if (!RecordUtil.isValidZipCode(newPayee.getAddressZipCode())) {
                putFieldError(TaxPropertyConstants.PayeeFields.ADDRESS_ZIP_CODE, TaxKeyConstants.ERROR_TAX_ZIPCODE);
                valid = false;
            }
        }

        VendorService vendorService = SpringContext.getBean(VendorService.class);

        if (vendorService.getByVendorNumber(newPayee.getVendorNumber()) == null) {
            putFieldError(TaxPropertyConstants.PayeeFields.VENDOR_NUMBER, TaxKeyConstants.ERROR_TAX_VENDORNUM);
        }

        if (newPayee.getVendorHeaderGeneratedIdentifier() == null) {
            putFieldError(TaxPropertyConstants.PayeeFields.VENDOR_NUMBER, TaxKeyConstants.ERROR_TAX_VENDORNUM_LOOKUP);
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
        searchValues.put(TaxPropertyConstants.PayeeFields.TAX_YEAR, newTaxYear);
        searchValues.put(TaxPropertyConstants.PayeeFields.HEADER_TAX_NUMBER, newTaxId);
        Collection<Payee> payees = boService.findMatching(Payee.class, searchValues);

        // if any other payees in the same TaxYear have the same TaxNumber, but a different VendorNumber, then fail.
        for (Payee payee : payees) {
            String existingVendorNumber = payee.getVendorNumber();
            if (!this.hasSameVendorHeaderGeneratedIdentifier(newVendorId, existingVendorNumber)) {
                result = false;
                putFieldError(TaxPropertyConstants.PayeeFields.HEADER_TAX_NUMBER, TaxKeyConstants.PAYEE_TAXID_USED_BY_DIFF_VENDOR, payee.getVendorNumber());
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
