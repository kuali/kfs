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
 * This class contains the coin breakdown for coin inserted into a cash drawer
 */
public class CoinDetail extends PersistableBusinessObjectBase {

    private String documentNumber;
    private String financialDocumentTypeCode;
    private String cashieringStatus;

    private KualiDecimal financialDocumentFiftyCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentTenCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentFiveCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentOneCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentOtherCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentCentAmount = KualiDecimal.ZERO;
    private KualiDecimal financialDocumentHundredCentAmount = KualiDecimal.ZERO;

    /**
     * Default constructor.
     */
    public CoinDetail() {
    }

    /**
     * Constructs a new CoinDetail with the given documentNumber, financialDocumentTypeCode, and cashieringStatus.
     * with all amount fields default to zero.
     */
    public CoinDetail(String documentNumber, String financialDocumentTypeCode, String cashieringStatus) {
        this();
        this.documentNumber = documentNumber;
        this.financialDocumentTypeCode = financialDocumentTypeCode;
        this.cashieringStatus = cashieringStatus;
    }

    /**
     * Constructs a new CoinDetail by copying the coin amounts from the given CashDrawer, ignoring other non-amount fields.
     */
    public CoinDetail(CashDrawer cashDrawer) {
        this();
        financialDocumentHundredCentAmount = cashDrawer.getFinancialDocumentHundredCentAmount();
        financialDocumentFiftyCentAmount = cashDrawer.getFinancialDocumentFiftyCentAmount();
        financialDocumentTwentyFiveCentAmount = cashDrawer.getFinancialDocumentTwentyFiveCentAmount();
        financialDocumentTenCentAmount = cashDrawer.getFinancialDocumentTenCentAmount();
        financialDocumentFiveCentAmount = cashDrawer.getFinancialDocumentFiveCentAmount();
        financialDocumentOneCentAmount = cashDrawer.getFinancialDocumentOneCentAmount();
        financialDocumentOtherCentAmount = cashDrawer.getFinancialDocumentOtherCentAmount();
    }

    /**
     * Constructs a new CoinDetail as a complete copy of the given CoinDetail.
     */
    public CoinDetail(CoinDetail coinDetail) {
        this();
        documentNumber = coinDetail.getDocumentNumber();
        financialDocumentTypeCode = coinDetail.getFinancialDocumentTypeCode();
        cashieringStatus = coinDetail.getCashieringStatus();
        financialDocumentHundredCentAmount = coinDetail.getFinancialDocumentHundredCentAmount();
        financialDocumentFiftyCentAmount = coinDetail.getFinancialDocumentFiftyCentAmount();
        financialDocumentTwentyFiveCentAmount = coinDetail.getFinancialDocumentTwentyFiveCentAmount();
        financialDocumentTenCentAmount = coinDetail.getFinancialDocumentTenCentAmount();
        financialDocumentFiveCentAmount = coinDetail.getFinancialDocumentFiveCentAmount();
        financialDocumentOneCentAmount = coinDetail.getFinancialDocumentOneCentAmount();
        financialDocumentOtherCentAmount = coinDetail.getFinancialDocumentOtherCentAmount();
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
    public void copyAmounts(CoinDetail coinDetail) {
        financialDocumentHundredCentAmount = coinDetail.getFinancialDocumentHundredCentAmount();
        financialDocumentFiftyCentAmount = coinDetail.getFinancialDocumentFiftyCentAmount();
        financialDocumentTwentyFiveCentAmount = coinDetail.getFinancialDocumentTwentyFiveCentAmount();
        financialDocumentTenCentAmount = coinDetail.getFinancialDocumentTenCentAmount();
        financialDocumentFiveCentAmount = coinDetail.getFinancialDocumentFiveCentAmount();
        financialDocumentOneCentAmount = coinDetail.getFinancialDocumentOneCentAmount();
        financialDocumentOtherCentAmount = coinDetail.getFinancialDocumentOtherCentAmount();
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
     * Gets the financialDocumentFiftyCentAmount attribute.
     *
     * @return Returns the financialDocumentFiftyCentAmount
     */
    public KualiDecimal getFinancialDocumentFiftyCentAmount() {
        return financialDocumentFiftyCentAmount;
    }

    /**
     * Sets the financialDocumentFiftyCentAmount attribute.
     *
     * @param financialDocumentFiftyCentAmount The financialDocumentFiftyCentAmount to set.
     */
    public void setFinancialDocumentFiftyCentAmount(KualiDecimal financialDocumentFiftyCentAmount) {
        this.financialDocumentFiftyCentAmount = financialDocumentFiftyCentAmount;
    }

    /**
     * Returns the number of half-cent coins in the drawer
     *
     * @return the count of half cent coins in the drawer
     */
    public Integer getFiftyCentCount() {
        return (financialDocumentFiftyCentAmount == null) ? new Integer(0) : new Integer(financialDocumentFiftyCentAmount.divide(KFSConstants.CoinTypeAmounts.FIFTY_CENT_AMOUNT).intValue());
    }

    /**
     * Sets the number of fifty cent coins in the drawer. This is useful if, you know, you're in da club, with, say a bottle full of
     * "bub"
     *
     * @param count the number of fifty cent coins present in the drawer
     */
    public void setFiftyCentCount(Integer count) {
        if (count != null) {
            financialDocumentFiftyCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.FIFTY_CENT_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentTwentyFiveCentAmount attribute.
     *
     * @return Returns the financialDocumentTwentyFiveCentAmount
     */
    public KualiDecimal getFinancialDocumentTwentyFiveCentAmount() {
        return financialDocumentTwentyFiveCentAmount;
    }

    /**
     * Sets the financialDocumentTwentyFiveCentAmount attribute.
     *
     * @param financialDocumentTwentyFiveCentAmount The financialDocumentTwentyFiveCentAmount to set.
     */
    public void setFinancialDocumentTwentyFiveCentAmount(KualiDecimal financialDocumentTwentyFiveCentAmount) {
        this.financialDocumentTwentyFiveCentAmount = financialDocumentTwentyFiveCentAmount;
    }

    /**
     * Returns the number of quarters in the drawer
     *
     * @return the count of quarters in the drawer
     */
    public Integer getTwentyFiveCentCount() {
        return (financialDocumentTwentyFiveCentAmount == null) ? new Integer(0) : new Integer(financialDocumentTwentyFiveCentAmount.divide(KFSConstants.CoinTypeAmounts.TWENTY_FIVE_CENT_AMOUNT).intValue());
    }

    /**
     * Sets the number of quarters in the drawer
     *
     * @param count the number of quarters present in the drawer
     */
    public void setTwentyFiveCentCount(Integer count) {
        if (count != null) {
            financialDocumentTwentyFiveCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.TWENTY_FIVE_CENT_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentTenCentAmount attribute.
     *
     * @return Returns the financialDocumentTenCentAmount
     */
    public KualiDecimal getFinancialDocumentTenCentAmount() {
        return financialDocumentTenCentAmount;
    }

    /**
     * Sets the financialDocumentTenCentAmount attribute.
     *
     * @param financialDocumentTenCentAmount The financialDocumentTenCentAmount to set.
     */
    public void setFinancialDocumentTenCentAmount(KualiDecimal financialDocumentTenCentAmount) {
        this.financialDocumentTenCentAmount = financialDocumentTenCentAmount;
    }

    /**
     * Returns the number of dimes in the drawer
     *
     * @return the count of dimes in the drawer
     */
    public Integer getTenCentCount() {
        return (financialDocumentTenCentAmount == null) ? new Integer(0) : new Integer(financialDocumentTenCentAmount.divide(KFSConstants.CoinTypeAmounts.TEN_CENT_AMOUNT).intValue());
    }

    /**
     * Sets the number of dimes in the drawer
     *
     * @param count the number of dimes present in the drawer
     */
    public void setTenCentCount(Integer count) {
        if (count != null) {
            financialDocumentTenCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.TEN_CENT_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentFiveCentAmount attribute.
     *
     * @return Returns the financialDocumentFiveCentAmount
     */
    public KualiDecimal getFinancialDocumentFiveCentAmount() {
        return financialDocumentFiveCentAmount;
    }

    /**
     * Sets the financialDocumentFiveCentAmount attribute.
     *
     * @param financialDocumentFiveCentAmount The financialDocumentFiveCentAmount to set.
     */
    public void setFinancialDocumentFiveCentAmount(KualiDecimal financialDocumentFiveCentAmount) {
        this.financialDocumentFiveCentAmount = financialDocumentFiveCentAmount;
    }

    /**
     * Returns the number of nickels in the drawer
     *
     * @return the count of nickels in the drawer
     */
    public Integer getFiveCentCount() {
        return (financialDocumentFiveCentAmount == null) ? new Integer(0) : new Integer(financialDocumentFiveCentAmount.divide(KFSConstants.CoinTypeAmounts.FIVE_CENT_AMOUNT).intValue());
    }

    /**
     * Sets the number of nickels in the drawer
     *
     * @param count the number of nickels present in the drawer
     */
    public void setFiveCentCount(Integer count) {
        if (count != null) {
            financialDocumentFiveCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.FIVE_CENT_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentOneCentAmount attribute.
     *
     * @return Returns the financialDocumentOneCentAmount
     */
    public KualiDecimal getFinancialDocumentOneCentAmount() {
        return financialDocumentOneCentAmount;
    }

    /**
     * Sets the financialDocumentOneCentAmount attribute.
     *
     * @param financialDocumentOneCentAmount The financialDocumentOneCentAmount to set.
     */
    public void setFinancialDocumentOneCentAmount(KualiDecimal financialDocumentOneCentAmount) {
        this.financialDocumentOneCentAmount = financialDocumentOneCentAmount;
    }

    /**
     * Returns the number of pennies in the drawer
     *
     * @return the count of pennies in the drawer
     */
    public Integer getOneCentCount() {
        return (financialDocumentOneCentAmount == null) ? new Integer(0) : new Integer(financialDocumentOneCentAmount.divide(KFSConstants.CoinTypeAmounts.ONE_CENT_AMOUNT).intValue());
    }

    /**
     * Sets the number of pennies in the drawer
     *
     * @param count the number of pennies present in the drawer
     */
    public void setOneCentCount(Integer count) {
        if (count != null) {
            financialDocumentOneCentAmount = new KualiDecimal(count).multiply(KFSConstants.CoinTypeAmounts.ONE_CENT_AMOUNT);
        }
    }

    /**
     * Gets the financialDocumentOtherCentAmount attribute.
     *
     * @return Returns the financialDocumentOtherCentAmount
     */
    public KualiDecimal getFinancialDocumentOtherCentAmount() {
        return financialDocumentOtherCentAmount;
    }

    /**
     * Sets the financialDocumentOtherCentAmount attribute.
     *
     * @param financialDocumentOtherCentAmount The financialDocumentOtherCentAmount to set.
     */
    public void setFinancialDocumentOtherCentAmount(KualiDecimal financialDocumentOtherCentAmount) {
        this.financialDocumentOtherCentAmount = financialDocumentOtherCentAmount;
    }
    
    public KualiDecimal getFinancialDocumentCentAmount() {
        return financialDocumentOtherCentAmount;
    }

    public void setFinancialDocumentCentAmount(KualiDecimal financialDocumentCentAmount) {
        this.financialDocumentOtherCentAmount = financialDocumentCentAmount;
    }


    /**
     * Gets the financialDocumentHundredCentAmount attribute.
     *
     * @return Returns the financialDocumentHundredCentAmount
     */
    public KualiDecimal getFinancialDocumentHundredCentAmount() {
        return financialDocumentHundredCentAmount;
    }

    /**
     * Sets the financialDocumentHundredCentAmount attribute.
     *
     * @param financialDocumentHundredCentAmount The financialDocumentHundredCentAmount to set.
     */
    public void setFinancialDocumentHundredCentAmount(KualiDecimal financialDocumentHundredCentAmount) {
        this.financialDocumentHundredCentAmount = financialDocumentHundredCentAmount;
    }

    /**
     * Returns the number of dollar coins--Sacajawea, Susan B. Anthony, or otherwise--in the drawer
     *
     * @return the count of dollar coins in the drawer
     */
    public Integer getHundredCentCount() {
        return (financialDocumentHundredCentAmount == null) ? new Integer(0) : new Integer(financialDocumentHundredCentAmount.divide(KFSConstants.CoinTypeAmounts.HUNDRED_CENT_AMOUNT).intValue());
    }

    /**
     * Sets the number of hundred cent coins in the drawer
     *
     * @param count the number of hundred cent coins present in the drawer
     */
    public void setHundredCentCount(Integer count) {
        if (count != null) {
            financialDocumentHundredCentAmount = new KualiDecimal(count.intValue()).multiply(KFSConstants.CoinTypeAmounts.HUNDRED_CENT_AMOUNT);
        }
    }


    /**
     * Returns the total amount represented by this coin detail record.
     *
     * @return total amount of this detail
     */
    public KualiDecimal getTotalAmount() {
        KualiDecimal result = KualiDecimal.ZERO;
        if (this.financialDocumentHundredCentAmount != null) {
            result = result.add(this.financialDocumentHundredCentAmount);
        }
        if (this.financialDocumentFiftyCentAmount != null) {
            result = result.add(this.financialDocumentFiftyCentAmount);
        }
        if (this.financialDocumentTwentyFiveCentAmount != null) {
            result = result.add(this.financialDocumentTwentyFiveCentAmount);
        }
        if (this.financialDocumentTenCentAmount != null) {
            result = result.add(this.financialDocumentTenCentAmount);
        }
        if (this.financialDocumentFiveCentAmount != null) {
            result = result.add(this.financialDocumentFiveCentAmount);
        }
        if (this.financialDocumentOneCentAmount != null) {
            result = result.add(this.financialDocumentOneCentAmount);
        }
        if (this.financialDocumentOtherCentAmount != null) {
            result = result.add(this.financialDocumentOtherCentAmount);
        }
        return result;
    }

    /**
     * This method sets all amounts in this record to zero
     */
    public void zeroOutAmounts() {
        this.financialDocumentHundredCentAmount = KualiDecimal.ZERO;
        this.financialDocumentFiftyCentAmount = KualiDecimal.ZERO;
        this.financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO;
        this.financialDocumentTenCentAmount = KualiDecimal.ZERO;
        this.financialDocumentFiveCentAmount = KualiDecimal.ZERO;
        this.financialDocumentOneCentAmount = KualiDecimal.ZERO;
        this.financialDocumentOtherCentAmount = KualiDecimal.ZERO;
    }

    /**
     * This method sets all amounts that are null to zero
     */
    public void zeroOutUnpopulatedAmounts() {
        if (this.financialDocumentHundredCentAmount == null) {
            this.financialDocumentHundredCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentFiftyCentAmount == null) {
            this.financialDocumentFiftyCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentTwentyFiveCentAmount == null) {
            this.financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentTenCentAmount == null) {
            this.financialDocumentTenCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentFiveCentAmount == null) {
            this.financialDocumentFiveCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentOneCentAmount == null) {
            this.financialDocumentOneCentAmount = KualiDecimal.ZERO;
        }
        if (this.financialDocumentOtherCentAmount == null) {
            this.financialDocumentOtherCentAmount = KualiDecimal.ZERO;
        }
    }

    public void add(CoinDetail detail) {
        if (detail.financialDocumentHundredCentAmount != null) {
            if (this.financialDocumentHundredCentAmount == null) {
                this.financialDocumentHundredCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentHundredCentAmount);
            }
            else {
                this.financialDocumentHundredCentAmount = this.financialDocumentHundredCentAmount.add(detail.financialDocumentHundredCentAmount);
            }
        }
        if (detail.financialDocumentFiftyCentAmount != null) {
            if (this.financialDocumentFiftyCentAmount == null) {
                this.financialDocumentFiftyCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentFiftyCentAmount);
            }
            else {
                this.financialDocumentFiftyCentAmount = this.financialDocumentFiftyCentAmount.add(detail.financialDocumentFiftyCentAmount);
            }
        }
        if (detail.financialDocumentTwentyFiveCentAmount != null) {
            if (this.financialDocumentTwentyFiveCentAmount == null) {
                this.financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentTwentyFiveCentAmount);
            }
            else {
                this.financialDocumentTwentyFiveCentAmount = this.financialDocumentTwentyFiveCentAmount.add(detail.financialDocumentTwentyFiveCentAmount);
            }
        }
        if (detail.financialDocumentTenCentAmount != null) {
            if (this.financialDocumentTenCentAmount == null) {
                this.financialDocumentTenCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentTenCentAmount);
            }
            else {
                this.financialDocumentTenCentAmount = this.financialDocumentTenCentAmount.add(detail.financialDocumentTenCentAmount);
            }
        }
        if (detail.financialDocumentFiveCentAmount != null) {
            if (this.financialDocumentFiveCentAmount == null) {
                this.financialDocumentFiveCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentFiveCentAmount);
            }
            else {
                this.financialDocumentFiveCentAmount = this.financialDocumentFiveCentAmount.add(detail.financialDocumentFiveCentAmount);
            }
        }
        if (detail.financialDocumentOneCentAmount != null) {
            if (this.financialDocumentOneCentAmount == null) {
                this.financialDocumentOneCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentOneCentAmount);
            }
            else {
                this.financialDocumentOneCentAmount = this.financialDocumentOneCentAmount.add(detail.financialDocumentOneCentAmount);
            }
        }
        if (detail.financialDocumentOtherCentAmount != null) {
            if (this.financialDocumentOtherCentAmount == null) {
                this.financialDocumentOtherCentAmount = KualiDecimal.ZERO.add(detail.financialDocumentOtherCentAmount);
            }
            else {
                this.financialDocumentOtherCentAmount = this.financialDocumentOtherCentAmount.add(detail.financialDocumentOtherCentAmount);
            }
        }
    }

    public void subtract(CoinDetail detail) {
        if (detail.financialDocumentHundredCentAmount != null) {
            if (this.financialDocumentHundredCentAmount == null) {
                this.financialDocumentHundredCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentHundredCentAmount);
            }
            else {
                this.financialDocumentHundredCentAmount = this.financialDocumentHundredCentAmount.subtract(detail.financialDocumentHundredCentAmount);
            }
        }
        if (detail.financialDocumentFiftyCentAmount != null) {
            if (this.financialDocumentFiftyCentAmount == null) {
                this.financialDocumentFiftyCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentFiftyCentAmount);
            }
            else {
                this.financialDocumentFiftyCentAmount = this.financialDocumentFiftyCentAmount.subtract(detail.financialDocumentFiftyCentAmount);
            }
        }
        if (detail.financialDocumentTwentyFiveCentAmount != null) {
            if (this.financialDocumentTwentyFiveCentAmount == null) {
                this.financialDocumentTwentyFiveCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentTwentyFiveCentAmount);
            }
            else {
                this.financialDocumentTwentyFiveCentAmount = this.financialDocumentTwentyFiveCentAmount.subtract(detail.financialDocumentTwentyFiveCentAmount);
            }
        }
        if (detail.financialDocumentTenCentAmount != null) {
            if (this.financialDocumentTenCentAmount == null) {
                this.financialDocumentTenCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentTenCentAmount);
            }
            else {
                this.financialDocumentTenCentAmount = this.financialDocumentTenCentAmount.subtract(detail.financialDocumentTenCentAmount);
            }
        }
        if (detail.financialDocumentFiveCentAmount != null) {
            if (this.financialDocumentFiveCentAmount == null) {
                this.financialDocumentFiveCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentFiveCentAmount);
            }
            else {
                this.financialDocumentFiveCentAmount = this.financialDocumentFiveCentAmount.subtract(detail.financialDocumentFiveCentAmount);
            }
        }
        if (detail.financialDocumentOneCentAmount != null) {
            if (this.financialDocumentOneCentAmount == null) {
                this.financialDocumentOneCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentOneCentAmount);
            }
            else {
                this.financialDocumentOneCentAmount = this.financialDocumentOneCentAmount.subtract(detail.financialDocumentOneCentAmount);
            }
        }
        if (detail.financialDocumentOtherCentAmount != null) {
            if (this.financialDocumentOtherCentAmount == null) {
                this.financialDocumentOtherCentAmount = KualiDecimal.ZERO.subtract(detail.financialDocumentOtherCentAmount);
            }
            else {
                this.financialDocumentOtherCentAmount = this.financialDocumentOtherCentAmount.subtract(detail.financialDocumentOtherCentAmount);
            }
        }
    }

    /**
     * Is this coin detail empty of any value?
     *
     * @return true if any field at all is neither null nor the amount is zero
     */
    public boolean isEmpty() {
        return ((this.financialDocumentHundredCentAmount == null || this.financialDocumentHundredCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentFiftyCentAmount == null || this.financialDocumentFiftyCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentTwentyFiveCentAmount == null || this.financialDocumentTwentyFiveCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentTenCentAmount == null || this.financialDocumentTenCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentFiveCentAmount == null || this.financialDocumentFiveCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentOneCentAmount == null || this.financialDocumentOneCentAmount.equals(KualiDecimal.ZERO)) &&
                (this.financialDocumentOtherCentAmount == null || this.financialDocumentOtherCentAmount.equals(KualiDecimal.ZERO)));
    }

    /**
     * Checks if this CoinDetail contains any negative amount field.
     * @return true if any amount is negative
     */
    public boolean hasNegativeAmount() {
        return ((financialDocumentHundredCentAmount != null && financialDocumentHundredCentAmount.isNegative()) ||
                (financialDocumentFiftyCentAmount != null && financialDocumentFiftyCentAmount.isNegative()) ||
                (financialDocumentTwentyFiveCentAmount != null && financialDocumentTwentyFiveCentAmount.isNegative()) ||
                (financialDocumentTenCentAmount != null && financialDocumentTenCentAmount.isNegative()) ||
                (financialDocumentFiveCentAmount != null && financialDocumentFiveCentAmount.isNegative()) ||
                (financialDocumentOneCentAmount != null && financialDocumentOneCentAmount.isNegative()) ||
                (financialDocumentOtherCentAmount != null && financialDocumentOtherCentAmount.isNegative()));
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
