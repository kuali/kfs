package edu.arizona.kfs.module.purap.service;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.tax.businessobject.Payee;
import edu.arizona.kfs.tax.businessobject.Payer;
import edu.arizona.kfs.tax.document.web.struts.PayeeSearchForm;

public interface TaxReporting1099Service {

	public boolean extractPayees(String jobName, Date jobRunDate);

	public List<String> getObjectCodes(List<String> levels, String type);

	public Payer getDefaultPayer();

	public boolean generateBatchPayeeForms();

	public boolean generatePayeeForms(String vendorNumber);

	public List<Payee> searchPayees(PayeeSearchForm form);

	public Payee getPayee(Integer id);

	public byte[] getPayee1099Form(Integer id, String year);

	public void updatePayeeInformation(VendorDetail vendorDetail);

	public List<Payee> loadPayees(Integer year);

	public KualiDecimal getPayeeTaxAmount(Payee payee, String typeCode, Integer taxYear);

	public Set<String> getActive1099Boxes();

}
