package edu.arizona.kfs.module.tax.businessobject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.rice.core.api.util.type.KualiDecimal;

import edu.arizona.kfs.module.tax.util.RecordUtil;

/**
 * The structure of the PayeeRecord is as follows:
 * 1: recordType
 * 2-5: paymentYear
 * 6: correctedReturnIndicator (1 or Blank)
 * 7-10: nameControl
 * 11: tinType - 1(EIN), 2(SSN) or Blank Value
 * 12-20: tin
 * 21-40: accountNumber
 * 41-44: officeCode
 * ************* payment locations *************
 * 55-66: Payment Amount 1
 * 67-78: Payment Amount 2
 * 79-90: Payment Amount 3
 * 91-102: Payment Amount 4
 * 103-114: Payment Amount 5
 * 115-126: Payment Amount 6
 * 127-138: Payment Amount 7
 * 139-150: Payment Amount 8
 * 151-162: Payment Amount 9
 * 163-174: Payment Amount 10
 * 175-186: Payment Amount 11
 * 187-198: Payment Amount 12
 * 199-210: Payment Amount 13
 * 211-222: Payment Amount 14
 * **************************************
 * 246: foreignCountryIndicator - Value of 1 or Blank
 * 247-286: payeeName1
 * 287-326: payeeName2
 * 367-406: payeeMailingAddress
 * **************************************
 * if Domestic:
 * * 447-486: payeeCity
 * * 487-488: payeeState - Must be valid U.S. state abbreviation
 * * 489-497: payeeZipCode
 * if Foreign:
 * * 447-497: payeeCity + " " + payeeState + " " + payeeZipCode + " " + payeeCountryCode
 * **************************************
 * 499-506: recordNumberSequence
 * 543: secondTinNoticeIndicator -value of 2 or Blank
 * 546: directSalesIndicator Value of 1 or Blank
 */

public class PayeeRecord implements Record {
    private static Character recordType = new Character('B');
    private Integer paymentYear;
    private Character correctedReturnIndicator = new Character(' ');
    private String nameControl;
    private Character tinType = new Character('1');
    private String tin;
    private String accountNumber = "";
    private String officeCode = "";
    private Map<String, KualiDecimal> taxAmounts = new HashMap<String, KualiDecimal>();
    private Character foreignCountryIndicator = new Character(' ');
    private String payeeName1;
    private String payeeName2;
    private String payeeMailingAddress;
    private String payeeCity;
    private String payeeState;
    private String payeeZipCode;
    private String payeeCountryCode;
    private Integer recordNumberSequence;
    private Character secondTinNoticeIndicator = new Character(' ');
    private Character directSalesIndicator = new Character(' ');

    public Integer getPaymentYear() {
        return paymentYear;
    }

    public void setPaymentYear(Integer paymentYear) {
        this.paymentYear = paymentYear;
    }

    public Character getCorrectedReturnIndicator() {
        return correctedReturnIndicator;
    }

    public void setCorrectedReturnIndicator(Character correctedReturnIndicator) {
        this.correctedReturnIndicator = correctedReturnIndicator;
    }

    public String getNameControl() {
        return nameControl;
    }

    public void setNameControl(String nameControl) {
        this.nameControl = nameControl;
    }

    public Character getTinType() {
        return tinType;
    }

    public void setTinType(Character tinType) {
        this.tinType = tinType;
    }

    public String getTin() {
        return tin;
    }

    public void setTin(String tin) {
        this.tin = tin;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getOfficeCode() {
        return officeCode;
    }

    public void setOfficeCode(String officeCode) {
        this.officeCode = officeCode;
    }

    public KualiDecimal getPaymentAmount(String box) {
        KualiDecimal retval = taxAmounts.get(box);

        if (retval == null) {
            retval = KualiDecimal.ZERO;
        }

        return retval;
    }

    public void setPaymentAmount(String box, KualiDecimal amount) {
        taxAmounts.put(box, amount);
    }

    public Character getForeignCountryIndicator() {
        return foreignCountryIndicator;
    }

    public void setForeignCountryIndicator(Character foreignCountryIndicator) {
        this.foreignCountryIndicator = foreignCountryIndicator;
    }

    public String getPayeeName1() {
        return payeeName1;
    }

    public void setPayeeName1(String payeeName1) {
        this.payeeName1 = payeeName1;
    }

    public String getPayeeName2() {
        return payeeName2;
    }

    public void setPayeeName2(String payeeName2) {
        this.payeeName2 = payeeName2;
    }

    public String getPayeeMailingAddress() {
        return payeeMailingAddress;
    }

    public void setPayeeMailingAddress(String payeeMailingAddress) {
        this.payeeMailingAddress = payeeMailingAddress;
    }

    public String getPayeeCity() {
        return payeeCity;
    }

    public void setPayeeCity(String payeeCity) {
        this.payeeCity = payeeCity;
    }

    public String getPayeeState() {
        return payeeState;
    }

    public void setPayeeState(String payeeState) {
        this.payeeState = payeeState;
    }

    public String getPayeeZipCode() {
        return payeeZipCode;
    }

    public void setPayeeZipCode(String payeeZipCode) {
        this.payeeZipCode = payeeZipCode;
    }

    public String getPayeeCountryCode() {
        return payeeCountryCode;
    }

    public void setPayeeCountryCode(String payeeCountryCode) {
        this.payeeCountryCode = payeeCountryCode;
    }

    public Integer getRecordNumberSequence() {
        return recordNumberSequence;
    }

    public void setRecordNumberSequence(Integer recordNumberSequence) {
        this.recordNumberSequence = recordNumberSequence;
    }

    @Override
    public String getRecordString() {
        StringBuffer buf = new StringBuffer();

        buf.append(recordType);
        buf.append(paymentYear.toString());
        buf.append(correctedReturnIndicator);
        buf.append(RecordUtil.leftJustifyString(nameControl, 4));
        buf.append(tinType);
        buf.append(RecordUtil.leftJustifyString(tin, 9));
        buf.append(RecordUtil.leftJustifyString(accountNumber, 20));
        buf.append(RecordUtil.leftJustifyString(officeCode, 4));
        buf.append(RecordUtil.getBlanks(10));

        for (int i = 1; i < 15; ++i) {
            buf.append(RecordUtil.formatKualiDecimal(getPaymentAmount("" + i), 12));
        }

        buf.append(RecordUtil.getBlanks(24));
        buf.append(foreignCountryIndicator);
        buf.append(RecordUtil.leftJustifyString(payeeName1, 40));
        buf.append(RecordUtil.leftJustifyString(payeeName2, 40));
        buf.append(RecordUtil.getBlanks(40));
        buf.append(RecordUtil.leftJustifyString(payeeMailingAddress, 40));
        buf.append(RecordUtil.getBlanks(40));

        if ('1' == foreignCountryIndicator || !KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(payeeCountryCode)) {
            buf.append(RecordUtil.leftJustifyString(payeeCity + " " + payeeState + " " + payeeZipCode + " " + payeeCountryCode, 51));
        } else {
            buf.append(RecordUtil.leftJustifyString(payeeCity, 40));
            buf.append(payeeState);
            buf.append(RecordUtil.leftJustifyString(payeeZipCode, 9));
        }

        buf.append(RecordUtil.getBlanks(1));
        buf.append(RecordUtil.rightJustifyInteger(recordNumberSequence, 8));
        buf.append(RecordUtil.getBlanks(36));
        buf.append(secondTinNoticeIndicator);
        buf.append(RecordUtil.getBlanks(2));
        buf.append(directSalesIndicator);
        buf.append(RecordUtil.getBlanks(201));
        buf.append("\r\n");

        return buf.toString();
    }

    @Override
    public List<ElectronicFileException> validateRecord() {
        List<ElectronicFileException> errors = new ArrayList<ElectronicFileException>();

        if (paymentYear == null) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payment Year", paymentYear, "Payment year is a required field."));
        } else if (paymentYear.toString().length() != 4) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payment Year", paymentYear, "Payment year must be 4 characters long."));
        }

        if (tin != null) {
            if (tin.length() > 0 && !RecordUtil.hasLength(tin, 9)) {
                errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "TIN", tin, "Payee TIN must be 9 characters long."));
            }
        }

        if (StringUtils.isBlank(tin) || StringUtils.isEmpty(tin)) {
            errors.add(new ElectronicFileException("NULL", payeeName1, ElectronicFileException.PAYEE, "TIN", "NULL", "Payee TIN is a required field."));
        }

        ArrayList<Character> correctedReturnIndicators = new ArrayList<Character>();
        correctedReturnIndicators.add(new Character(' '));
        correctedReturnIndicators.add(new Character('1'));

        if (!RecordUtil.isValidCharacter(correctedReturnIndicator, correctedReturnIndicators)) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Corrected Return Ind", correctedReturnIndicator, "Corrected return indicator is not a valid character."));
        }

        ArrayList<Character> tinTypes = new ArrayList<Character>();
        tinTypes.add(new Character('1'));
        tinTypes.add(new Character('2'));
        tinTypes.add(new Character(' '));

        if (!RecordUtil.isValidCharacter(tinType, tinTypes)) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Tin Type", tinType, "TIN Type is not a valid character."));
        }

        ArrayList<Character> foreignCountryIndicators = new ArrayList<Character>();
        foreignCountryIndicators.add(new Character(' '));
        foreignCountryIndicators.add(new Character('1'));

        if (!RecordUtil.isValidCharacter(foreignCountryIndicator, foreignCountryIndicators)) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Foreign Country Ind", foreignCountryIndicator, "Foreign country indicator is not a valid character."));
        }

        if (!RecordUtil.hasCharacter(payeeName1, 40)) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payee Name Line 1", payeeName1, "Payee Name Line 1 is a required field. [limit = 40]"));
        }
        if (!RecordUtil.hasCharacterEmpty(payeeName2, 40)) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payee Name Line 2", payeeName2, "Payee Name Line 2 is too long. [limit = 40]"));
        }

        if (!RecordUtil.hasCharacter(payeeMailingAddress, 40)) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payee Mailing Address", payeeMailingAddress, "Payee mailing address is a required field. [limit = 40]"));
        }
        if (!RecordUtil.hasCharacter(payeeCity, 40)) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payee City", payeeCity, "Payee city is a required field. [limit = 40]"));
        }

        if ('1' == foreignCountryIndicator || !KFSConstants.COUNTRY_CODE_UNITED_STATES.equals(payeeCountryCode)) {
            if (!RecordUtil.hasCharacter(payeeCity + " " + payeeState + " " + payeeZipCode + " " + payeeCountryCode, 51)) {
                errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payee Address", (payeeCity + " " + payeeState + " " + payeeZipCode + " " + payeeCountryCode), "Foreign Payee address is not a valid. [limit = 51]"));
            }

            if (!RecordUtil.isValidCountryCode(payeeCountryCode)) {
                errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payee Country Code", payeeCountryCode, "Payee country code is not a valid code. [limit = 2]"));
            }
        } else {
            if (!RecordUtil.isValidStateCode(payeeState)) {
                errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payee State", payeeState, "Payee state is not a valid code. [limit = 2]"));
            }

            if (!RecordUtil.isValidZipCode(payeeZipCode)) {
                errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Payee Zip Code", payeeZipCode, "Payee zip code is not a valid code. [limit = 9]"));
            }
        }

        if (recordNumberSequence == null) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Record Number Sequence", recordNumberSequence, "Record number sequence is a required integer value."));
        }

        if (getRecordString().length() != 750) {
            errors.add(new ElectronicFileException(tin, payeeName1, ElectronicFileException.PAYEE, "Record Length", new Integer(getRecordString().length()), "Record length must be 750 characters."));
        }

        return errors;
    }

}
