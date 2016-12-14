package edu.arizona.kfs.module.purap.document.service.impl;

import java.util.List;

import org.apache.commons.lang.StringUtils;

import edu.arizona.kfs.module.purap.document.service.ElectronicFilingService;
import edu.arizona.kfs.module.purap.service.TaxReporting1099Service;
import edu.arizona.kfs.tax.TaxConstants;
import edu.arizona.kfs.tax.businessobject.ElectronicFile;
import edu.arizona.kfs.tax.businessobject.Payee;
import edu.arizona.kfs.tax.businessobject.PayeeRecord;
import edu.arizona.kfs.tax.businessobject.Payer;
import edu.arizona.kfs.tax.businessobject.RecordGroup;
import edu.arizona.kfs.tax.service.TaxParameterHelperService;
import edu.arizona.kfs.tax.util.RecordUtil;

public class ElectronicFilingServiceImpl implements ElectronicFilingService {

	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ElectronicFilingService.class);

	private TaxReporting1099Service taxReporting1099Service;
	private TaxParameterHelperService taxParameterHelperService;

	private static final String NINE_SPACES = "         ";

	public void setTaxReporting1099Service(TaxReporting1099Service taxReporting1099Service) {
		this.taxReporting1099Service = taxReporting1099Service;
	}

	public void setTaxParameterHelperService(TaxParameterHelperService taxParameterHelperService) {
		this.taxParameterHelperService = taxParameterHelperService;
	}

	public ElectronicFile getElectronicFile(Integer year) throws Exception {
		LOG.info("Tax Year : " + year);

		Payer payer = taxReporting1099Service.getDefaultPayer();

		if (payer == null) {
			throw new Exception("No default payer record was found.");
		} else {
			LOG.info("Default Payer : " + payer.getCompanyName1());
		}

		double totalTaxAmount = taxParameterHelperService.getTaxThreshholdAmount();
		LOG.info("Total Tax Amount Threshold:" + totalTaxAmount);

		ElectronicFile ef = new ElectronicFile();
		setTranmitterRecord(year, payer, ef);

		RecordGroup rg = ef.addRecordGroup();
		setPayerRecord(year, payer, rg);

		List<Payee> payees = taxReporting1099Service.loadPayees(year);
		LOG.info("Payees Found Above Total Amount: " + payees.size());

		PayeeRecord pr = null;

		for (Payee payee : payees) {
			pr = rg.addPayeeRecord();
			setPayeeRecord(year, pr, payee);
		}

		rg.updateTotals();
		ef.updateTotals();

		return ef;
	}

	private void setPayeeRecord(Integer year, PayeeRecord pr, Payee payee) {
		pr.setPaymentYear(year);
		pr.setTin(payee.getHeaderTaxNumber() == null ? NINE_SPACES : payee.getHeaderTaxNumber());
		pr.setPayeeCity(payee.getAddressCityName());
		if (StringUtils.isNotBlank(payee.getAddressLine1Address()) && payee.getAddressLine1Address().startsWith(TaxConstants.DBA_BUSINESS_PREFIX)) {
			pr.setPayeeMailingAddress(payee.getAddressLine2Address());
		} else {
			pr.setPayeeMailingAddress(payee.getAddressLine1Address());
		}
		pr.setPayeeState(payee.getAddressStateCode());
		pr.setPayeeCountryCode(payee.getAddressCountryCode());

		if (payee.getAddressZipCode() != null) {
			pr.setPayeeZipCode(payee.getAddressZipCode().replace("-", ""));
		}

		pr.setPayeeName1(payee.getVendorName());

		if (StringUtils.isNotBlank(payee.getAddressLine1Address()) && payee.getAddressLine1Address().startsWith(TaxConstants.DBA_BUSINESS_PREFIX)) {
			pr.setPayeeName2(payee.getAddressLine1Address());
		}

		for (String box : taxReporting1099Service.getActive1099Boxes()) {
			pr.setPaymentAmount(box, taxReporting1099Service.getPayeeTaxAmount(payee, box, payee.getTaxYear()));
		}

		if (payee.getVendorForeignIndicator().booleanValue()) {
			pr.setForeignCountryIndicator('1');
		}
	}

	private void setPayerRecord(Integer year, Payer payer, RecordGroup rg) {
		rg.getPayerRecord().setPaymentYear(year);
		rg.getPayerRecord().setPayerNameControl(payer.getNameControl());
		rg.getPayerRecord().setTaxPayerTin(payer.getTin());
		rg.getPayerRecord().setPayerName1(payer.getName1());
		rg.getPayerRecord().setPayerName2(payer.getName2());
		rg.getPayerRecord().setPayerCity(payer.getCity());
		rg.getPayerRecord().setPayerMailingAddress(payer.getAddress());

		if (payer.getPhoneNumber() != null) {
			rg.getPayerRecord().setPayerPhoneNumber(new Long(RecordUtil.removeChar(payer.getPhoneNumber(), '-')));
		}

		rg.getPayerRecord().setPayerState(payer.getState());
		rg.getPayerRecord().setPayerZipCode(payer.getZipCode());
		rg.getPayerRecord().setRecordNumberSequence(new Integer(2));
	}

	private void setTranmitterRecord(Integer year, Payer payer, ElectronicFile ef) {
		ef.getTransmitterRecord().setPaymentYear(year);
		ef.getTransmitterRecord().setTransmitterTin(payer.getTin());
		ef.getTransmitterRecord().setTransmitterControlCode(payer.getTransCd());
		ef.getTransmitterRecord().setTransmitterName1(payer.getName1());
		ef.getTransmitterRecord().setTransmitterName2(payer.getName2());
		ef.getTransmitterRecord().setCompanyName1(payer.getCompanyName1());
		ef.getTransmitterRecord().setCompanyName2(payer.getCompanyName2());
		ef.getTransmitterRecord().setCompanyCity(payer.getCity());
		ef.getTransmitterRecord().setCompanyMailingAddress(payer.getAddress());
		ef.getTransmitterRecord().setCompanyState(payer.getState());

		if (payer.getZipCode() != null) {
			ef.getTransmitterRecord().setCompanyZipCode(payer.getZipCode().replace("-", ""));
		}

		ef.getTransmitterRecord().setContactEmail(payer.getEmail());
		ef.getTransmitterRecord().setContactName(payer.getContact());

		if (payer.getPhoneNumber() != null) {
			ef.getTransmitterRecord().setContactPhoneNumber(new Long(RecordUtil.removeChar(payer.getPhoneNumber(), '-')));
		}
	}

}
