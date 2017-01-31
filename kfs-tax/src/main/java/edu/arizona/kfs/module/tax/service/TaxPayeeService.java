package edu.arizona.kfs.module.tax.service;

import java.util.List;
import java.util.Map;

import org.kuali.kfs.vnd.businessobject.VendorDetail;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.module.tax.businessobject.Payee;

public interface TaxPayeeService {
    /**
     * Creates a new Payee for the specified vendor and tax year.
     */
    public Payee createNewPayee(Integer taxYear, VendorDetail vendor);

    /**
     * Retrieves the Payee for the specified id.
     */
    public Payee getPayee(Integer id);

    /**
     * Retrieves all Payee information for the specified Payee and year.
     */
    public Payee loadPayee(Integer year, Payee payee);

    /**
     * Retrieves all Payees for the specified year.
     */
    public List<Payee> loadPayees(Integer year);

    /**
     * 
     */
    public List<Payee> searchPayees(String vendorName, String headerTaxNumber, String vendorNumber, Integer taxYear);

    /**
     * Retrieves the tax amount for the specified Payee, Tax Box, and Year.
     */
    public KualiDecimal getPayeeTaxAmount(Payee payee, String box, Integer taxYear);

    /**
     * Retrieve the map of 1099 Boxes. Key=Box Code, Value = Box Name
     */

    Map<String, String> getForm1099Boxes();

}
