package edu.arizona.kfs.pdp.dataaccess;

import java.util.List;

public interface ShippingInvoiceDao {
    /**
     * This method gets a distinct list of shipping companies from the shipping invoices in the database.
     * 
     * @return List of shipping companies.
     */
    public List<String> getShippingInvoiceCompanies();
}
