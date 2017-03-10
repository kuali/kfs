package edu.arizona.kfs.sys.document;

import java.util.List;

import org.kuali.kfs.vnd.businessobject.VendorHeader;

import edu.arizona.kfs.sys.businessobject.DocumentIncomeType;

/**
 * This interface provides the structure to enforce income type handling in
 * a document. The generic parameters<T1, T2> are defined as follows
 * T1 - implementation of DocumentIncomeType
 * T2 - java type of documentIdentifier (String or Integer)
 */
public interface IncomeTypeContainer<T1 extends DocumentIncomeType<T2>, T2> {

    public String getPaidYear();

    public String getPaymentMethodCode();

    public List<T1> getIncomeTypes();

    public T2 getDocumentIdentifier();

    public VendorHeader getVendorHeader();

    public IncomeTypeHandler<T1, T2> getIncomeTypeHandler();

    public boolean getReportable1099TransactionsFlag();

    /**
     * Gets the Route Status of the current document.
     * 
     * @return
     */
    public String getRouteStatus();

}
