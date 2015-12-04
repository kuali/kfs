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

import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.bo.PersistableBusinessObjectBase;

/**
 * Represents a breakdown of currency amounts (i.e. $100, $50, $20, etc.)
 */
public class CurrencyDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String financialDocumentTypeCode;
    private String cashieringStatus;
    private KualiDecimal financialDocumentHundredDollarAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentFiftyDollarAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentTwentyDollarAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentTenDollarAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentFiveDollarAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentTwoDollarAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentOneDollarAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentOtherDollarAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentDollarAmount = KualiDecimal.ZERO;

    /**
     * Default constructor.
     */
    public CurrencyDetail() {
    }

    /**
     * Constructs a new CurrencyDetail with the given documentNumber, financialDocumentTypeCode, and cashieringStatus;
     * with all amount fields default to zero.
     */
    public CurrencyDetail(String documentNumber, String financialDocumentTypeCode, String cashieringStatus) {
        this.documentNumber = documentNumber;
        this.financialDocumentTypeCode = financialDocumentTypeCode;
        this.cashieringStatus = cashieringStatus;
    }

    /**
     * Constructs a new CurrencyDetail by copying the coin amounts from the given CashDrawer, ignoring other non-amount fields.
     */
    public CurrencyDetail(CashDrawer cashDrawer) {
        financialDocumentHundredDollarAmount = cashDrawer.getFinancialDocumentHundredDollarAmount();
        financialDocumentFiftyDollarAmount = cashDrawer.getFinancialDocumentFiftyDollarAmount();
        financialDocumentTwentyDollarAmount = cashDrawer.getFinancialDocumentTwentyDollarAmount();
        financialDocumentTenDollarAmount = cashDrawer.getFinancialDocumentTenDollarAmount();
        financialDocumentFiveDollarAmount = cashDrawer.getFinancialDocumentFiveDollarAmount();
        financialDocumentTwoDollarAmount = cashDrawer.getFinancialDocumentTwoDollarAmount();
        financialDocumentOneDollarAmount = cashDrawer.getFinancialDocumentOneDollarAmount();
        financialDocumentOtherDollarAmount = cashDrawer.getFinancialDocumentOtherDollarAmount();
    }

    /**
     * Constructs a new CurrencyDetail as a complete copy of the given CurrencyDetail.
     */
    public CurrencyDetail(CurrencyDetail currencyDetail) {
        documentNumber = currencyDetail.getDocumentNumber();
        financialDocumentTypeCode = currencyDetail.getFinancialDocumentTypeCode();
        cashieringStatus = currencyDetail.getCashieringStatus();
        financialDocumentHundredDollarAmount = currencyDetail.getFinancialDocumentHundredDollarAmount();
        financialDocumentFiftyDollarAmount = currencyDetail.getFinancialDocumentFiftyDollarAmount();
        financialDocumentTwentyDollarAmount = currencyDetail.getFinancialDocumentTwentyDollarAmount();
        financialDocumentTenDollarAmount = currencyDetail.getFinancialDocumentTenDollarAmount();
        financialDocumentFiveDollarAmount = currencyDetail.getFinancialDocumentFiveDollarAmount();
        financialDocumentTwoDollarAmount = currencyDetail.getFinancialDocumentTwoDollarAmount();
        financialDocumentOneDollarAmount = currencyDetail.getFinancialDocumentOneDollarAmount();
        financialDocumentOtherDollarAmount = currencyDetail.getFinancialDocumentOtherDollarAmount();
    }

    /**
     * Sets the non-amount primary key fields with the given documentNumber, financialDocumentTypeCode, and cashieringStatus.
     */
    public void setKeys(String documentNumber, String financialDocumentTypeCode, String cashieringStatus) {
        this.documentNumber = documentNumber;
        this.financialDocumentTypeCode = financialDocumentTypeCode;
        this.cashieringStatus = cashieringStatus;
    }

    /**
     * Copies all amounts from the given CoinDetail to this CoinDetail.
     */
    public void copyAmounts(CurrencyDetail currencyDetail) {
        financialDocumentHundredDollarAmount = currencyDetail.getFinancialDocumentHundredDollarAmount();
        financialDocumentFiftyDollarAmount = currencyDetail.getFinancialDocumentFiftyDollarAmount();
        financialDocumentTwentyDollarAmount = currencyDetail.getFinancialDocumentTwentyDollarAmount();
        financialDocumentTenDollarAmount = currencyDetail.getFinancialDocumentTenDollarAmount();
        financialDocumentFiveDollarAmount = currencyDetail.getFinancialDocumentFiveDollarAmount();
        financialDocumentTwoDollarAmount = currencyDetail.getFinancialDocumentTwoDollarAmount();
        financialDocumentOneDollarAmount = currencyDetail.getFinancialDocumentOneDollarAmount();
        financialDocumentOtherDollarAmount = currencyDetail.getFinancialDocumentOtherDollarAmount();
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
     * Gets the financialDocumentTypeCode attribute.
     *
     * @return Returns the financialDocumentTypeCode
     */
    public String getFinancialDocumentTypeCode() {
        return financialDocumentTypeCode;
    }

    /**
     * Sets the financialDocumentTypeCode attribute.
     *
     * @param financialDocumentTypeCode The financialDocumentTypeCode to set.
     */
    public void setFinancialDocumentTypeCode(String financialDocumentTypeCode) {
        this.financialDocumentTypeCode = financialDocumentTypeCode;
    }


    /**
     * Gets the cashieringStatus attribute.
     *
     * @return Returns the cashieringStatus
     */
    public String getCashieringStatus() {
        return cashieringStatus;
    }

    /**
     * Sets the cashieringStatus attribute.
     *
     * @param cashieringStatus The cashieringStatus to set.
     */
    public void setCashieringStatus(String financialDocumentColumnTypeCode) {
        this.cashieringStatus = financialDocumentColumnTypeCode;
    }


    /**
     * Gets the financialDocumentHundredDollarAmount attribute.
     *
     * @return Returns the financialDocumentHundredDollarAmount
     */
    public KualiDecimal getFinancialDocumentHundredDollarAmount() {
        return financialDocumentHundredDollarAmount;
    }

    /**
     * Sets the financialDocumentHundredDollarAmount attribute.
     *
     * @param financialDocumentHundredDollarAmount The financialDocumentHundredDollarAmount to set.
     */
    public void setFinancialDocumentHundredDollarAmount(KualiDecimal financialDocumentHundredDollarAmount) {
        this.financialDocumentHundredDollarAmount = financialDocumentHundredDollarAmount;
    }

    /**
     * Returns the actual count of hundred dollar bills
     *
     * @return the number of hundred dollar bills present in the drawer
     */
    public Integer getHundredDollarCount() {
        return (financialDocumentHundredDollarAmount == null) ? new Integer(0) : new Integer(financialDocumentHundredDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.HUNDRED_DOLLAR_AMOUNT).intValue());
    }

    /**
     * This sets the count of hundred dollar bills present in the drawer
     *
     * @param count the number of hundred dollar bills present in the drawer
     */
    public void setHundredDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentHundredDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.HUNDRED_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentFiftyDollarAmount attribute.
     *
     * @return Returns the financialDocumentFiftyDollarAmount
     */
    public KualiDecimal getFinancialDocumentFiftyDollarAmount() {
        return financialDocumentFiftyDollarAmount;
    }

    /**
     * Sets the financialDocumentFiftyDollarAmount attribute.
     *
     * @param financialDocumentFiftyDollarAmount The financialDocumentFiftyDollarAmount to set.
     */
    public void setFinancialDocumentFiftyDollarAmount(KualiDecimal financialDocumentFiftyDollarAmount) {
        this.financialDocumentFiftyDollarAmount = financialDocumentFiftyDollarAmount;
    }

    /**
     * Returns the actual count of fifty dollar bills
     *
     * @return the number of fifty dollar bills present in the drawer
     */
    public Integer getFiftyDollarCount() {
        return (financialDocumentFiftyDollarAmount == null) ? new Integer(0) : new Integer(financialDocumentFiftyDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.FIFTY_DOLLAR_AMOUNT).intValue());
    }

    /**
     * This sets the count of hundred dollar bills present in the drawer
     *
     * @param count the number of hundred dollar bills present in the drawer
     */
    public void setFiftyDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentFiftyDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.FIFTY_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentTwentyDollarAmount attribute.
     *
     * @return Returns the financialDocumentTwentyDollarAmount
     */
    public KualiDecimal getFinancialDocumentTwentyDollarAmount() {
        return financialDocumentTwentyDollarAmount;
    }

    /**
     * Sets the financialDocumentTwentyDollarAmount attribute.
     *
     * @param financialDocumentTwentyDollarAmount The financialDocumentTwentyDollarAmount to set.
     */
    public void setFinancialDocumentTwentyDollarAmount(KualiDecimal financialDocumentTwentyDollarAmount) {
        this.financialDocumentTwentyDollarAmount = financialDocumentTwentyDollarAmount;
    }

    /**
     * Returns the actual count of twenty dollar bills
     *
     * @return the number of twenty dollar bills present in the drawer
     */
    public Integer getTwentyDollarCount() {
        return (financialDocumentTwentyDollarAmount == null) ? new Integer(0) : new Integer(financialDocumentTwentyDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TWENTY_DOLLAR_AMOUNT).intValue());
    }

    /**
     * This sets the count of twenty dollar bills present in the drawer
     *
     * @param count the number of twenty dollar bills present in the drawer
     */
    public void setTwentyDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentTwentyDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.TWENTY_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentTenDollarAmount attribute.
     *
     * @return Returns the financialDocumentTenDollarAmount
     */
    public KualiDecimal getFinancialDocumentTenDollarAmount() {
        return financialDocumentTenDollarAmount;
    }

    /**
     * Sets the financialDocumentTenDollarAmount attribute.
     *
     * @param financialDocumentTenDollarAmount The financialDocumentTenDollarAmount to set.
     */
    public void setFinancialDocumentTenDollarAmount(KualiDecimal financialDocumentTenDollarAmount) {
        this.financialDocumentTenDollarAmount = financialDocumentTenDollarAmount;
    }

    /**
     * Returns the actual count of ten dollar bills
     *
     * @return the number of ten dollar bills present in the drawer
     */
    public Integer getTenDollarCount() {
        return (financialDocumentTenDollarAmount == null) ? new Integer(0) : new Integer(financialDocumentTenDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TEN_DOLLAR_AMOUNT).intValue());
    }

    /**
     * This sets the count of ten dollar bills present in the drawer
     *
     * @param count the number of ten dollar bills present in the drawer
     */
    public void setTenDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentTenDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.TEN_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentFiveDollarAmount attribute.
     *
     * @return Returns the financialDocumentFiveDollarAmount
     */
    public KualiDecimal getFinancialDocumentFiveDollarAmount() {
        return financialDocumentFiveDollarAmount;
    }

    /**
     * Sets the financialDocumentFiveDollarAmount attribute.
     *
     * @param financialDocumentFiveDollarAmount The financialDocumentFiveDollarAmount to set.
     */
    public void setFinancialDocumentFiveDollarAmount(KualiDecimal financialDocumentFiveDollarAmount) {
        this.financialDocumentFiveDollarAmount = financialDocumentFiveDollarAmount;
    }

    /**
     * Returns the actual count of five dollar bills
     *
     * @return the number of five dollar bills present in the drawer
     */
    public Integer getFiveDollarCount() {
        return (financialDocumentFiveDollarAmount == null) ? new Integer(0) : new Integer(financialDocumentFiveDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.FIVE_DOLLAR_AMOUNT).intValue());
    }

    /**
     * This sets the count of five dollar bills present in the drawer
     *
     * @param count the number of five dollar bills present in the drawer
     */
    public void setFiveDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentFiveDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.FIVE_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentTwoDollarAmount attribute.
     *
     * @return Returns the financialDocumentTwoDollarAmount
     */
    public KualiDecimal getFinancialDocumentTwoDollarAmount() {
        return financialDocumentTwoDollarAmount;
    }

    /**
     * Sets the financialDocumentTwoDollarAmount attribute.
     *
     * @param financialDocumentTwoDollarAmount The financialDocumentTwoDollarAmount to set.
     */
    public void setFinancialDocumentTwoDollarAmount(KualiDecimal financialDocumentTwoDollarAmount) {
        this.financialDocumentTwoDollarAmount = financialDocumentTwoDollarAmount;
    }

    /**
     * Returns the actual count of two dollar bills
     *
     * @return the number of two dollar bills present in the drawer
     */
    public Integer getTwoDollarCount() {
        return (financialDocumentTwoDollarAmount == null) ? new Integer(0) : new Integer(financialDocumentTwoDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.TWO_DOLLAR_AMOUNT).intValue());
    }

    /**
     * This sets the count of two dollar bills present in the drawer
     *
     * @param count the number of two dollar bills present in the drawer
     */
    public void setTwoDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentTwoDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.TWO_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentOneDollarAmount attribute.
     *
     * @return Returns the financialDocumentOneDollarAmount
     */
    public KualiDecimal getFinancialDocumentOneDollarAmount() {
        return financialDocumentOneDollarAmount;
    }

    /**
     * Sets the financialDocumentOneDollarAmount attribute.
     *
     * @param financialDocumentOneDollarAmount The financialDocumentOneDollarAmount to set.
     */
    public void setFinancialDocumentOneDollarAmount(KualiDecimal financialDocumentOneDollarAmount) {
        this.financialDocumentOneDollarAmount = financialDocumentOneDollarAmount;
    }

    /**
     * Returns the actual count of one dollar bills
     *
     * @return the number of one dollar bills present in the drawer
     */
    public Integer getOneDollarCount() {
        return (financialDocumentOneDollarAmount == null) ? new Integer(0) : new Integer(financialDocumentOneDollarAmount.divide(KFSConstants.CurrencyTypeAmounts.ONE_DOLLAR_AMOUNT).intValue());
    }

    /**
     * This sets the count of one dollar bills present in the drawer
     *
     * @param count the number of one dollar bills present in the drawer
     */
    public void setOneDollarCount(Integer count) {
        if (count != null) {
            this.financialDocumentOneDollarAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CurrencyTypeAmounts.ONE_DOLLAR_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentOtherDollarAmount attribute.
     *
     * @return Returns the financialDocumentOtherDollarAmount
     */
    public KualiDecimal getFinancialDocumentOtherDollarAmount() {
        return financialDocumentOtherDollarAmount;
    }

    /**
     * Sets the financialDocumentOtherDollarAmount attribute.
     *
     * @param financialDocumentOtherDollarAmount The financialDocumentOtherDollarAmount to set.
     */
    public void setFinancialDocumentOtherDollarAmount(KualiDecimal financialDocumentOtherDollarAmount) {
        this.financialDocumentOtherDollarAmount = financialDocumentOtherDollarAmount;
    }
    
    public KualiDecimal getFinancialDocumentDollarAmount() {
        return financialDocumentOtherDollarAmount;
    }

    public void setFinancialDocumentDollarAmount(KualiDecimal financialDocumentDollarAmount) {
        this.financialDocumentOtherDollarAmount = financialDocumentDollarAmount;
    }

    /**
     * This method calculates the total amount represented by all the currency listed in this detail record
     *
     * @return total amount of this detail
     */
    public KualiDecimal getTotalAmount() {
        KualiDecimal result = KualiDecimal.ZERO;
        if (this.financialDocumentHundredDollarAmount != null) {
            result = result.add(this.financialDocumentHundredDollarAmount);
        }
        if (this.financialDocumentFiftyDollarAmount != null) {
            result = result.add(this.financialDocumentFiftyDollarAmount);
        }
        if (this.financialDocumentTwentyDollarAmount != null) {
            result = result.add(this.financialDocumentTwentyDollarAmount);
        }
        if (this.financialDocumentTenDollarAmount != null) {
            result = result.add(this.financialDocumentTenDollarAmount);
        }
        if (this.financialDocumentFiveDollarAmount != null) {
            result = result.add(this.financialDocumentFiveDollarAmount);
        }
        if (this.financialDocumentTwoDollarAmount != null) {
            result = result.add(this.financialDocumentTwoDollarAmount);
        }
        if (this.financialDocumentOneDollarAmount != null) {
            result = result.add(this.financialDocumentOneDollarAmount);
        }
        if (this.financialDocumentOtherDollarAmount != null) {
            result = result.add(this.financialDocumentOtherDollarAmount);
        }
        return result;
    }

    /**
     * This method sets all the amounts to zero
     */
    public void zeroOutAmounts() {
        this.financialDocumentHundredDollarAmount = KualiDecimal.ZERO;
        this.financialDocumentFiftyDollarAmount = KualiDecimal.ZERO;
        this.financialDocumentTwentyDollarAmount = KualiDecimal.ZERO;
        this.financialDocumentTenDollarAmount = KualiDecimal.ZERO;
        this.financialDocumentFiveDollarAmount = KualiDecimal.ZERO;
        this.financialDocumentTwoDollarAmount = KualiDecimal.ZERO;
        this.financialDocumentOneDollarAmount = KualiDecimal.ZERO;
        this.financialDocumentOtherDollarAmount = KualiDecimal.ZERO;
    }

    /**
     * This method sets all amounts that are null to zero
     */
    public void zeroOutUnpopulatedAmounts() {
        if (financialDocumentHundredDollarAmount == null) {
            this.financialDocumentHundredDollarAmount = KualiDecimal.ZERO;
        }
        if (financialDocumentFiftyDollarAmount == null) {
            this.financialDocumentFiftyDollarAmount = KualiDecimal.ZERO;
        }
        if (financialDocumentTwentyDollarAmount == null) {
            this.financialDocumentTwentyDollarAmount = KualiDecimal.ZERO;
        }
        if (financialDocumentTenDollarAmount == null) {
            this.financialDocumentTenDollarAmount = KualiDecimal.ZERO;
        }
        if (financialDocumentFiveDollarAmount == null) {
            this.financialDocumentFiveDollarAmount = KualiDecimal.ZERO;
        }
        if (financialDocumentTwoDollarAmount == null) {
            this.financialDocumentTwoDollarAmount = KualiDecimal.ZERO;
        }
        if (financialDocumentOneDollarAmount == null) {
            this.financialDocumentOneDollarAmount = KualiDecimal.ZERO;
        }
        if (financialDocumentOtherDollarAmount == null) {
            this.financialDocumentOtherDollarAmount = KualiDecimal.ZERO;
        }
    }

    /**
     * This method adds the amounts from the given currency detail record to this one
     *
     * @param detail the currency detail to add onto this
     */
    public void add(CurrencyDetail detail) {
        if (detail.financialDocumentHundredDollarAmount != null) {
            if (this.financialDocumentHundredDollarAmount == null) {
                this.financialDocumentHundredDollarAmount = KualiDecimal.ZERO.add(detail.financialDocumentHundredDollarAmount);
            }
            else {
                this.financialDocumentHundredDollarAmount = this.financialDocumentHundredDollarAmount.add(detail.financialDocumentHundredDollarAmount);
            }
        }
        if (detail.financialDocumentFiftyDollarAmount != null) {
            if (this.financialDocumentFiftyDollarAmount == null) {
                this.financialDocumentFiftyDollarAmount = KualiDecimal.ZERO.add(detail.financialDocumentFiftyDollarAmount);
            }
            else {
                this.financialDocumentFiftyDollarAmount = this.financialDocumentFiftyDollarAmount.add(detail.financialDocumentFiftyDollarAmount);
            }
        }
        if (detail.financialDocumentTwentyDollarAmount != null) {
            if (this.financialDocumentTwentyDollarAmount == null) {
                this.financialDocumentTwentyDollarAmount = KualiDecimal.ZERO.add(detail.financialDocumentTwentyDollarAmount);
            }
            else {
                this.financialDocumentTwentyDollarAmount = this.financialDocumentTwentyDollarAmount.add(detail.financialDocumentTwentyDollarAmount);
            }
        }
        if (detail.financialDocumentTenDollarAmount != null) {
            if (this.financialDocumentTenDollarAmount == null) {
                this.financialDocumentTenDollarAmount = KualiDecimal.ZERO.add(detail.financialDocumentTenDollarAmount);
            }
            else {
                this.financialDocumentTenDollarAmount = this.financialDocumentTenDollarAmount.add(detail.financialDocumentTenDollarAmount);
            }
        }
        if (detail.financialDocumentFiveDollarAmount != null) {
            if (this.financialDocumentFiveDollarAmount == null) {
                this.financialDocumentFiveDollarAmount = KualiDecimal.ZERO.add(detail.financialDocumentFiveDollarAmount);
            }
            else {
                this.financialDocumentFiveDollarAmount = this.financialDocumentFiveDollarAmount.add(detail.financialDocumentFiveDollarAmount);
            }
        }
        if (detail.financialDocumentTwoDollarAmount != null) {
            if (this.financialDocumentTwoDollarAmount == null) {
                this.financialDocumentTwoDollarAmount = KualiDecimal.ZERO.add(detail.financialDocumentTwoDollarAmount);
            }
            else {
                this.financialDocumentTwoDollarAmount = this.financialDocumentTwoDollarAmount.add(detail.financialDocumentTwoDollarAmount);
            }
        }
        if (detail.financialDocumentOneDollarAmount != null) {
            if (this.financialDocumentOneDollarAmount == null) {
                this.financialDocumentOneDollarAmount = KualiDecimal.ZERO.add(detail.financialDocumentOneDollarAmount);
            }
            else {
                this.financialDocumentOneDollarAmount = this.financialDocumentOneDollarAmount.add(detail.financialDocumentOneDollarAmount);
            }
        }
        if (detail.financialDocumentOtherDollarAmount != null) {
            if (this.financialDocumentOtherDollarAmount == null) {
                this.financialDocumentOtherDollarAmount = KualiDecimal.ZERO.add(detail.financialDocumentOtherDollarAmount);
            }
            else {
                this.financialDocumentOtherDollarAmount = this.financialDocumentOtherDollarAmount.add(detail.financialDocumentOtherDollarAmount);
            }
        }
    }

    /**
     * This method subtracts the given currency detail from this one
     *
     * @param detail the detail to subtract
     */
    public void subtract(CurrencyDetail detail) {
        if (detail.financialDocumentHundredDollarAmount != null) {
            if (this.financialDocumentHundredDollarAmount == null) {
                this.financialDocumentHundredDollarAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentHundredDollarAmount);
            }
            else {
                this.financialDocumentHundredDollarAmount = this.financialDocumentHundredDollarAmount.subtract(detail.financialDocumentHundredDollarAmount);
            }
        }
        if (detail.financialDocumentFiftyDollarAmount != null) {
            if (this.financialDocumentFiftyDollarAmount == null) {
                this.financialDocumentFiftyDollarAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentFiftyDollarAmount);
            }
            else {
                this.financialDocumentFiftyDollarAmount = this.financialDocumentFiftyDollarAmount.subtract(detail.financialDocumentFiftyDollarAmount);
            }
        }
        if (detail.financialDocumentTwentyDollarAmount != null) {
            if (this.financialDocumentTwentyDollarAmount == null) {
                this.financialDocumentTwentyDollarAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentTwentyDollarAmount);
            }
            else {
                this.financialDocumentTwentyDollarAmount = this.financialDocumentTwentyDollarAmount.subtract(detail.financialDocumentTwentyDollarAmount);
            }
        }
        if (detail.financialDocumentTenDollarAmount != null) {
            if (this.financialDocumentTenDollarAmount == null) {
                this.financialDocumentTenDollarAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentTenDollarAmount);
            }
            else {
                this.financialDocumentTenDollarAmount = this.financialDocumentTenDollarAmount.subtract(detail.financialDocumentTenDollarAmount);
            }
        }
        if (detail.financialDocumentFiveDollarAmount != null) {
            if (this.financialDocumentFiveDollarAmount == null) {
                this.financialDocumentFiveDollarAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentFiveDollarAmount);
            }
            else {
                this.financialDocumentFiveDollarAmount = this.financialDocumentFiveDollarAmount.subtract(detail.financialDocumentFiveDollarAmount);
            }
        }
        if (detail.financialDocumentTwoDollarAmount != null) {
            if (this.financialDocumentTwoDollarAmount == null) {
                this.financialDocumentTwoDollarAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentTwoDollarAmount);
            }
            else {
                this.financialDocumentTwoDollarAmount = this.financialDocumentTwoDollarAmount.subtract(detail.financialDocumentTwoDollarAmount);
            }
        }
        if (detail.financialDocumentOneDollarAmount != null) {
            if (this.financialDocumentOneDollarAmount == null) {
                this.financialDocumentOneDollarAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentOneDollarAmount);
            }
            else {
                this.financialDocumentOneDollarAmount = this.financialDocumentOneDollarAmount.subtract(detail.financialDocumentOneDollarAmount);
            }
        }
        if (detail.financialDocumentOtherDollarAmount != null) {
            if (this.financialDocumentOtherDollarAmount == null) {
                this.financialDocumentOtherDollarAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentOtherDollarAmount);
            }
            else {
                this.financialDocumentOtherDollarAmount = this.financialDocumentOtherDollarAmount.subtract(detail.financialDocumentOtherDollarAmount);
            }
        }
    }

    /**
     * Does this currency detail actually have any information in it?
     *
     * @return true if any field at all is not null and not zero, false if otherwise
     */
    public boolean isEmpty() {
        return ((this.financialDocumentHundredDollarAmount == null || this.financialDocumentHundredDollarAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentFiftyDollarAmount == null || this.financialDocumentFiftyDollarAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentTwentyDollarAmount == null || this.financialDocumentTwentyDollarAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentTenDollarAmount == null || this.financialDocumentTenDollarAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentFiveDollarAmount == null || this.financialDocumentFiveDollarAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentTwoDollarAmount == null || this.financialDocumentTwoDollarAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentOneDollarAmount == null || this.financialDocumentOneDollarAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentOtherDollarAmount == null || this.financialDocumentOtherDollarAmount.equals(KualiDecimal.ZERO)));
    }

    /**
     * Checks if this CurrencyDetail contains any negative amount field.
     * @return true if any amount is negative
     */
    public boolean hasNegativeAmount() {
        return ((financialDocumentHundredDollarAmount != null && financialDocumentHundredDollarAmount.isNegative()) ||
                (financialDocumentFiftyDollarAmount != null && financialDocumentFiftyDollarAmount.isNegative()) ||
                (financialDocumentTwentyDollarAmount != null && financialDocumentTwentyDollarAmount.isNegative()) ||
                (financialDocumentTenDollarAmount != null && financialDocumentTenDollarAmount.isNegative()) ||
                (financialDocumentFiveDollarAmount != null && financialDocumentFiveDollarAmount.isNegative()) ||
                (financialDocumentTwoDollarAmount != null && financialDocumentTwoDollarAmount.isNegative()) ||
                (financialDocumentOneDollarAmount != null && financialDocumentOneDollarAmount.isNegative()) ||
                (financialDocumentOtherDollarAmount != null && financialDocumentOtherDollarAmount.isNegative()));
    }

    /**
     * @see org.kuali.rice.krad.bo.BusinessObjectBase#toStringMapper()
     */
    protected LinkedHashMap toStringMapper_RICE20_REFACTORME() {
        LinkedHashMap m = new LinkedHashMap();
        m.put(KFSPropertyConstants.DOCUMENT_NUMBER, this.documentNumber);
        m.put("financialDocumentTypeCode", this.financialDocumentTypeCode);
        m.put("cashieringStatus", this.cashieringStatus);
        return m;
    }
}
