package edu.arizona.kfs.module.tax.businessobject;

import java.util.ArrayList;
import java.util.List;

import edu.arizona.kfs.module.tax.util.RecordUtil;

/**
 * The structure of the PayeeRecord is as follows:
 * 1: recordType
 * 2-5: paymentYear
 * 12-20: taxPayerTin
 * 21-24: payerNameControl
 * 25: lastFilingIndicator - Value of 1 or Blank
 * 26: federalStateFilerIndicator - Value of 1 or Blank
 * 27: returnType - A 1099-MISC
 * 28-41: amountCodes - Ascending Order Numeric Followed by Alpha
 * 52: foreignEntityIndicator - Value of 1 or Blank
 * 53-92: payerName1
 * 93-132: payerName2 - Not Required
 * 133:transferAgentIndicator - Value of 1 or 0
 * 134-173: payerMailingAddress
 * **************************************
 * if Domestic:
 * * 174-213: payerCity
 * * 214-215: payerState - Must be valid U.S. state abbreviation
 * * 216-224: payerZipCode
 * if Foreign:
 * * 174-224: payerCity + " " + payerState + " " + payerZipCode + " " + payerCountryCode
 * **************************************
 * 225-239: payerPhoneNumber - No Dashes
 * 500-507 recordNumberSequence
 */
public class PayerRecord implements Record {
    private static Character recordType = new Character('A');
    private Integer paymentYear;
    private String taxPayerTin;
    private String payerNameControl = "";
    private Character lastFilingIndicator = new Character(' ');
    private Character federalStateFilerIndicator = new Character(' ');
    private Character returnType = new Character('A');
    private String amountCodes = "12345678ABCDE ";
    private Character foreignEntityIndicator = new Character(' ');
    private String payerName1;
    private String payerName2 = "";
    private Character transferAgentIndicator = new Character('0');
    private String payerMailingAddress;
    private String payerCity;
    private String payerState;
    private String payerZipCode;
    private String payerCountryCode;
    private Long payerPhoneNumber;
    private Integer recordNumberSequence;

    public Integer getPaymentYear() {
        return paymentYear;
    }

    public void setPaymentYear(Integer paymentYear) {
        this.paymentYear = paymentYear;
    }

    public String getTaxPayerTin() {
        return taxPayerTin;
    }

    public void setTaxPayerTin(String taxPayerTin) {
        this.taxPayerTin = taxPayerTin;
    }

    public String getPayerNameControl() {
        return payerNameControl;
    }

    public void setPayerNameControl(String payerNameControl) {
        this.payerNameControl = payerNameControl;
    }

    public Character getLastFilingIndicator() {
        return lastFilingIndicator;
    }

    public void setLastFilingIndicator(Character lastFilingIndicator) {
        this.lastFilingIndicator = lastFilingIndicator;
    }

    public Character getFederalStateFilerIndicator() {
        return federalStateFilerIndicator;
    }

    public void setFederalStateFilerIndicator(Character federalStateFilerIndicator) {
        this.federalStateFilerIndicator = federalStateFilerIndicator;
    }

    public Character getReturnType() {
        return returnType;
    }

    public String getAmountCodes() {
        return amountCodes;
    }

    public Character getForeignEntityIndicator() {
        return foreignEntityIndicator;
    }

    public void setForeignEntityIndicator(Character foreignEntityIndicator) {
        this.foreignEntityIndicator = foreignEntityIndicator;
    }

    public String getPayerName1() {
        return payerName1;
    }

    public void setPayerName1(String payerName1) {
        this.payerName1 = payerName1;
    }

    public String getPayerName2() {
        return payerName2;
    }

    public void setPayerName2(String payerName2) {
        this.payerName2 = payerName2;
    }

    public Character getTransferAgentIndicator() {
        return transferAgentIndicator;
    }

    public void setTransferAgentIndicator(Character transferAgentIndicator) {
        this.transferAgentIndicator = transferAgentIndicator;
    }

    public String getPayerMailingAddress() {
        return payerMailingAddress;
    }

    public void setPayerMailingAddress(String payerMailingAddress) {
        this.payerMailingAddress = payerMailingAddress;
    }

    public String getPayerCity() {
        return payerCity;
    }

    public void setPayerCity(String payerCity) {
        this.payerCity = payerCity;
    }

    public String getPayerState() {
        return payerState;
    }

    public void setPayerState(String payerState) {
        this.payerState = payerState;
    }

    public String getPayerZipCode() {
        return payerZipCode;
    }

    public void setPayerZipCode(String payerZipCode) {
        this.payerZipCode = payerZipCode;
    }

    public String getPayerCountryCode() {
        return payerCountryCode;
    }

    public void setPayerCountryCode(String payerCountryCode) {
        this.payerCountryCode = payerCountryCode;
    }

    public Long getPayerPhoneNumber() {
        return payerPhoneNumber;
    }

    public void setPayerPhoneNumber(Long payerPhoneNumber) {
        this.payerPhoneNumber = payerPhoneNumber;
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
        buf.append(RecordUtil.getBlanks(6));
        buf.append(RecordUtil.leftJustifyString(taxPayerTin, 9));
        buf.append(RecordUtil.leftJustifyString(payerNameControl, 4));
        buf.append(lastFilingIndicator);
        buf.append(federalStateFilerIndicator);
        buf.append(returnType);
        buf.append(amountCodes);
        buf.append(RecordUtil.getBlanks(10));
        buf.append(foreignEntityIndicator);
        buf.append(RecordUtil.leftJustifyString(payerName1, 40));
        buf.append(RecordUtil.leftJustifyString(payerName2, 40));
        buf.append(transferAgentIndicator);
        buf.append(RecordUtil.leftJustifyString(payerMailingAddress, 40));

        if ('1' == foreignEntityIndicator) {
            buf.append(RecordUtil.leftJustifyString(payerCity + " " + payerState + " " + payerZipCode + " " + payerCountryCode, 51));
        } else {
            buf.append(RecordUtil.leftJustifyString(payerCity, 40));
            buf.append(payerState);
            buf.append(RecordUtil.leftJustifyString(payerZipCode, 9));
        }

        buf.append(RecordUtil.leftJustifyString(payerPhoneNumber == null ? "" : payerPhoneNumber.toString(), 15));
        buf.append(RecordUtil.getBlanks(260));
        buf.append(RecordUtil.rightJustifyInteger(recordNumberSequence, 8));
        buf.append(RecordUtil.getBlanks(241));
        buf.append("\r\n");

        return buf.toString();
    }

    @Override
    public List<ElectronicFileException> validateRecord() {
        List<ElectronicFileException> errors = new ArrayList<ElectronicFileException>();

        if (paymentYear == null) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payment Year", paymentYear, "Payment year is a required field."));
        } else if (paymentYear.toString().length() != 4) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payment Year", paymentYear, "Payment year must be 4 digits long."));
        }

        if (!RecordUtil.hasLength(taxPayerTin, 9)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "TIN", taxPayerTin, "TIN must be 9 characters long."));
        }

        if (!RecordUtil.hasCharacterEmpty(payerNameControl, 4)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payer Name Control", payerNameControl, "Payer Name Control is too long. [limit = 4]"));
        }

        ArrayList<Character> lastFilingIndicators = new ArrayList<Character>();
        lastFilingIndicators.add(new Character(' '));
        lastFilingIndicators.add(new Character('1'));

        if (!RecordUtil.isValidCharacter(lastFilingIndicator, lastFilingIndicators)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Last Filing Year Indicator", lastFilingIndicator, "Last filing year indicator is not a valid character."));
        }

        ArrayList<Character> federalStateFilerIndicators = new ArrayList<Character>();
        federalStateFilerIndicators.add(new Character(' '));
        federalStateFilerIndicators.add(new Character('1'));

        if (!RecordUtil.isValidCharacter(federalStateFilerIndicator, federalStateFilerIndicators)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Federal State Filing Indicator", federalStateFilerIndicator, "Federal state filing indicator is not a valid character."));
        }

        ArrayList<Character> foreignEntityIndicators = new ArrayList<Character>();
        foreignEntityIndicators.add(new Character(' '));
        foreignEntityIndicators.add(new Character('1'));

        if (!RecordUtil.isValidCharacter(foreignEntityIndicator, foreignEntityIndicators)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Foreign Entity Indicator", foreignEntityIndicator, "Foreign entity indicator is not a valid character."));
        }

        if (!RecordUtil.hasCharacter(payerName1, 40)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payer Name Line 1", payerName1, "Payer Name Line 1 is a required field. [limit = 40]"));
        }
        if (!RecordUtil.hasCharacterEmpty(payerName2, 40)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payer Name Line 2", payerName2, "Payer Name Line 2 is too long. [limit = 40]"));
        }

        ArrayList<Character> transferAgentIndicators = new ArrayList<Character>();
        transferAgentIndicators.add(new Character('0'));
        transferAgentIndicators.add(new Character('1'));

        if (!RecordUtil.isValidCharacter(transferAgentIndicator, transferAgentIndicators)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Transfer Entity Indicator", transferAgentIndicator, "Transfer agent indicator is not a valid character."));
        }

        if (!RecordUtil.hasCharacter(payerMailingAddress, 40)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payer Mailing Address", payerMailingAddress, "Payer mailing address is a required field. [limit = 40]"));
        }
        if (!RecordUtil.hasCharacter(payerCity, 40)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payer City", payerCity, "Payer city is a required field. [limit = 40]"));
        }
        if (!RecordUtil.isValidStateCode(payerState)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payer State", payerState, "Payer state is not a valid code. [limit = 2]"));
        }
        if ("1".equals(foreignEntityIndicator) && !RecordUtil.isValidCountryCode(payerCountryCode)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payer Country Code", payerCountryCode, "Payer country code is not a valid code. [limit = 2]"));
        }
        if (!RecordUtil.isValidZipCode(payerZipCode)) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Payer Zip Code", payerZipCode, "Payer zip code is not a valid code. [limit = 9]"));
        }

        if (recordNumberSequence == null) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Record Number Sequence", recordNumberSequence, "Record number sequence is a required integer value."));
        }

        if (getRecordString().length() != 750) {
            errors.add(new ElectronicFileException(taxPayerTin, payerName1, ElectronicFileException.PAYER, "Record Length", new Integer(getRecordString().length()), "Record length must 750 characters."));
        }

        return errors;
    }

}
