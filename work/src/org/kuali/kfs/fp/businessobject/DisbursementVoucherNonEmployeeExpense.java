/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.kuali.kfs.fp.businessobject;

import java.util.LinkedHashMap;

import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * This class is used to represent a disbursement voucher non-employee expense, often associated with a trip or a service rendered.
 */
public class DisbursementVoucherNonEmployeeExpense extends PersistableBusinessObjectBase {

    private String documentNumber;
    private Integer financialDocumentLineNumber;
    private String disbVchrExpenseCode;
    private String disbVchrExpenseCompanyName;
    private KualiDecimal disbVchrExpenseAmount;

    private TravelExpenseTypeCode disbVchrExpense;
    private TravelCompanyCode disbVchrExpenseCompany;
    private DisbursementVoucherNonEmployeeTravel disbursementVoucherNonEmployeeTravel;

    private boolean isPrepaid;

    /**
     * Default no-arg constructor.
     */
    public DisbursementVoucherNonEmployeeExpense() {
        this.setFinancialDocumentLineNumber(1);
    }

    /**
     * Gets the documentNumber attribute.
     *
     * @return Returns the documentNumber
     */
    public String getDocumentNumber() {
        return documentNumber;
    }


    /**
     * Sets the documentNumber attribute.
     *
     * @param documentNumber The documentNumber to set.
     */
    public void setDocumentNumber(String documentNumber) {
        this.documentNumber = documentNumber;
    }

    /**
     * Gets the financialDocumentLineNumber attribute.
     *
     * @return Returns the financialDocumentLineNumber
     */
    public Integer getFinancialDocumentLineNumber() {
        return financialDocumentLineNumber;
    }


    /**
     * Sets the financialDocumentLineNumber attribute.
     *
     * @param financialDocumentLineNumber The financialDocumentLineNumber to set.
     */
    public void setFinancialDocumentLineNumber(Integer financialDocumentLineNumber) {
        this.financialDocumentLineNumber = financialDocumentLineNumber;
    }

    /**
     * Gets the disbVchrExpenseCode attribute.
     *
     * @return Returns the disbVchrExpenseCode
     */
    public String getDisbVchrExpenseCode() {
        return disbVchrExpenseCode;
    }

    /**
     * Dummy field so we can have different select options.
     *
     * @return String
     */
    public String getDisbVchrPrePaidExpenseCode() {
        return disbVchrExpenseCode;
    }


    /**
     * Sets the disbVchrExpenseCode attribute.
     *
     * @param disbVchrExpenseCode The disbVchrExpenseCode to set.
     */
    public void setDisbVchrExpenseCode(String disbVchrExpenseCode) {
        this.disbVchrExpenseCode = disbVchrExpenseCode;
        //KFSMI-798 - refreshNonUpdatableReferences() used instead of refresh(),
        //DisbursementVoucherNonEmployeeExpense does not have any updatable references
        this.refreshNonUpdateableReferences();
    }

    /**
     * Dummy field so we can have different select options.
     *
     * @param disbVchrExpenseCode
     */
    public void setDisbVchrPrePaidExpenseCode(String disbVchrExpenseCode) {
        this.disbVchrExpenseCode = disbVchrExpenseCode;
    }

    /**
     * Gets the disbVchrExpenseCompanyName attribute.
     *
     * @return Returns the disbVchrExpenseCompanyName
     */
    public String getDisbVchrExpenseCompanyName() {
        return disbVchrExpenseCompanyName;
    }


    /**
     * Sets the disbVchrExpenseCompanyName attribute.
     *
     * @param disbVchrExpenseCompanyName The disbVchrExpenseCompanyName to set.
     */
    public void setDisbVchrExpenseCompanyName(String disbVchrExpenseCompanyName) {
        this.disbVchrExpenseCompanyName = disbVchrExpenseCompanyName;
    }

    /**
     * Gets the disbVchrExpenseCompanyName attribute.
     *
     * @return Returns the disbVchrExpenseCompanyName
     */
    public String getDisbVchrPrePaidExpenseCompanyName() {
        return disbVchrExpenseCompanyName;
    }


    /**
     * Sets the disbVchrExpenseCompanyName attribute.
     *
     * @param disbVchrExpenseCompanyName The disbVchrExpenseCompanyName to set.
     */
    public void setDisbVchrPrePaidExpenseCompanyName(String disbVchrExpenseCompanyName) {
        this.disbVchrExpenseCompanyName = disbVchrExpenseCompanyName;
    }

    /**
     * Gets the disbVchrExpenseAmount attribute.
     *
     * @return Returns the disbVchrExpenseAmount
     */
    public KualiDecimal getDisbVchrExpenseAmount() {
        return disbVchrExpenseAmount;
    }


    /**
     * Sets the disbVchrExpenseAmount attribute.
     *
     * @param disbVchrExpenseAmount The disbVchrExpenseAmount to set.
     */
    public void setDisbVchrExpenseAmount(KualiDecimal disbVchrExpenseAmount) {
        this.disbVchrExpenseAmount = disbVchrExpenseAmount;
    }

    /**
     * Gets the disbVchrExpense attribute.
     *
     * @return Returns the disbVchrExpense
     */
    public TravelExpenseTypeCode getDisbVchrExpense() {
        return disbVchrExpense;
    }

    /**
     * Sets the disbVchrExpense attribute.
     *
     * @param disbVchrExpense The disbVchrExpense to set.
     * @deprecated
     */
    @Deprecated
    public void setDisbVchrExpense(TravelExpenseTypeCode disbVchrExpense) {
        this.disbVchrExpense = disbVchrExpense;
    }

    /**
     * Gets the disbVchrExpenseCompany attribute.
     *
     * @return Returns the disbVchrExpenseCompany.
     */
    public TravelCompanyCode getDisbVchrExpenseCompany() {
        return disbVchrExpenseCompany;
    }

    /**
     * Sets the disbVchrExpenseCompany attribute value.
     *
     * @param disbVchrExpenseCompany The disbVchrExpenseCompany to set.
     * @deprecated
     */
    @Deprecated
    public void setDisbVchrExpenseCompany(TravelCompanyCode disbVchrExpenseCompany) {
        this.disbVchrExpenseCompany = disbVchrExpenseCompany;
    }

    /**
     * Gets the disbursementVoucherNonEmployeeTravel attribute.
     *
     * @return Returns the disbursementVoucherNonEmployeeTravel.
     */
    public DisbursementVoucherNonEmployeeTravel getDisbursementVoucherNonEmployeeTravel() {
        return disbursementVoucherNonEmployeeTravel;
    }

    /**
     * Sets the disbursementVoucherNonEmployeeTravel attribute value.
     *
     * @param disbursementVoucherNonEmployeeTravel The disbursementVoucherNonEmployeeTravel to set.
     * @deprecated
     */
    @Deprecated
    public void setDisbursementVoucherNonEmployeeTravel(DisbursementVoucherNonEmployeeTravel disbursementVoucherNonEmployeeTravel) {
        this.disbursementVoucherNonEmployeeTravel = disbursementVoucherNonEmployeeTravel;
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        if (this.financialDocumentLineNumber != null) {
            m.put("financialDocumentLineNumber", this.financialDocumentLineNumber.toString());
        }
        return m;
    }
}
