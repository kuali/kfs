package edu.arizona.kfs.module.tax.businessobject;

import java.util.ArrayList;
import java.util.List;

import edu.arizona.kfs.module.tax.util.RecordUtil;

/**
 * The structure of the TransmitterRecord is as follows:
 * 1: recordType
 * 2-5: paymentYear
 * 6: priorYearIndicator - Value of P or Blank for current year
 * 7-15: transmitterTin
 * 16-20: transmitterControlCode 28: testFileIndicator - Value of T or Blank
 * 29: foreignEntityIndicator - Value of 1 or Blank
 * 30-69: transmitterName1
 * 70-109: transmitterName2
 * 110-149: companyName1
 * 150-189: companyName2
 * 190-229: companyMailingAddress
 * **************************************
 * if Domestic:
 * * 230-269: companyCity
 * * 270-271: companyState - Must be valid U.S. state abbreviation
 * * 272-280: companyZipCode
 * if Foreign:
 * * 230-280: companyCity + " " + companyState + " " + companyZipCode + " " + companyCountryCode
 * **************************************
 * 296-303: numberPayees
 * 304-343: contactName
 * 344-358: contactPhoneNumber - No Dashes
 * 359-408: contactEmail
 * 500-507: recordNumberSequence
 * 518: vendorIndicator - Must be V or I
 * 519-558: vendorName
 * 559-598: vendorMailingAddress
 * **************************************
 * if Domestic:
 * * 599-638: vendorCity
 * * 639-640: vendorState - Must be valid U.S. state abbreviation
 * * 641-649: vendorZipCode
 * if Foreign:
 * * 599-649: vendorCity + " " + vendorState + " " + vendorZipCode + " " + vendorCountryCode
 * 650-689 vendorContactName
 * 690-704:vendorPhoneNumber- No Dashes
 * 740: vendorForeignIndicator - Value of 1 or Blank /**
 */
public class TransmitterRecord implements Record {

    private static Character recordType = new Character('T');

    private Integer paymentYear;
    private Character priorYearIndicator = new Character(' ');
    private String transmitterTin;
    private String transmitterControlCode;
    private Character testFileIndicator = new Character(' ');
    private Character foreignEntityIndicator = new Character(' ');
    private String transmitterName1;
    private String transmitterName2 = "";
    private String companyName1;
    private String companyName2 = "";
    private String companyMailingAddress;
    private String companyCity;
    private String companyState;
    private String companyZipCode;
    private String companyCountryCode;
    private Integer numberPayees;
    private String contactName;
    private Long contactPhoneNumber;
    private String contactEmail;
    private String recordNumberSequence = "00000001";
    private Character vendorIndicator = new Character('I');
    private String vendorName = "";
    private String vendorMailingAddress = "";
    private String vendorCity = "";
    private String vendorState = "  ";
    private String vendorZipCode = "";
    private String vendorContactName = "";
    private Long vendorPhoneNumber;
    private Character vendorForeignIndicator = new Character(' ');
    private String vendorCountryCode;

    public static Character getRecordType() {
        return recordType;
    }

    public static void setRecordType(Character recordType) {
        TransmitterRecord.recordType = recordType;
    }

    public Integer getPaymentYear() {
        return paymentYear;
    }

    public void setPaymentYear(Integer paymentYear) {
        this.paymentYear = paymentYear;
    }

    public Character getPriorYearIndicator() {
        return priorYearIndicator;
    }

    public void setPriorYearIndicator(Character priorYearIndicator) {
        this.priorYearIndicator = priorYearIndicator;
    }

    public String getTransmitterTin() {
        return transmitterTin;
    }

    public void setTransmitterTin(String transmitterTin) {
        this.transmitterTin = transmitterTin;
    }

    public String getTransmitterControlCode() {
        return transmitterControlCode;
    }

    public void setTransmitterControlCode(String transmitterControlCode) {
        this.transmitterControlCode = transmitterControlCode;
    }

    public Character getTestFileIndicator() {
        return testFileIndicator;
    }

    public void setTestFileIndicator(Character testFileIndicator) {
        this.testFileIndicator = testFileIndicator;
    }

    public Character getForeignEntityIndicator() {
        return foreignEntityIndicator;
    }

    public void setForeignEntityIndicator(Character foreignEntityIndicator) {
        this.foreignEntityIndicator = foreignEntityIndicator;
    }

    public String getTransmitterName1() {
        return transmitterName1;
    }

    public void setTransmitterName1(String transmitterName1) {
        this.transmitterName1 = transmitterName1;
    }

    public String getTransmitterName2() {
        return transmitterName2;
    }

    public void setTransmitterName2(String transmitterName2) {
        this.transmitterName2 = transmitterName2;
    }

    public String getCompanyName1() {
        return companyName1;
    }

    public void setCompanyName1(String companyName1) {
        this.companyName1 = companyName1;
    }

    public String getCompanyName2() {
        return companyName2;
    }

    public void setCompanyName2(String companyName2) {
        this.companyName2 = companyName2;
    }

    public String getCompanyMailingAddress() {
        return companyMailingAddress;
    }

    public void setCompanyMailingAddress(String companyMailingAddress) {
        this.companyMailingAddress = companyMailingAddress;
    }

    public String getCompanyCity() {
        return companyCity;
    }

    public void setCompanyCity(String companyCity) {
        this.companyCity = companyCity;
    }

    public String getCompanyState() {
        return companyState;
    }

    public void setCompanyState(String companyState) {
        this.companyState = companyState;
    }

    public String getCompanyZipCode() {
        return companyZipCode;
    }

    public void setCompanyZipCode(String companyZipCode) {
        this.companyZipCode = companyZipCode;
    }

    public String getCompanyCountryCode() {
        return companyCountryCode;
    }

    public void setCompanyCountryCode(String companyCountryCode) {
        this.companyCountryCode = companyCountryCode;
    }

    public Integer getNumberPayees() {
        return numberPayees;
    }

    public void setNumberPayees(Integer numberPayees) {
        this.numberPayees = numberPayees;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public Long getContactPhoneNumber() {
        return contactPhoneNumber;
    }

    public void setContactPhoneNumber(Long contactPhoneNumber) {
        this.contactPhoneNumber = contactPhoneNumber;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public void setContactEmail(String contactEmail) {
        this.contactEmail = contactEmail;
    }

    public String getRecordNumberSequence() {
        return recordNumberSequence;
    }

    public void setRecordNumberSequence(String recordNumberSequence) {
        this.recordNumberSequence = recordNumberSequence;
    }

    public Character getVendorIndicator() {
        return vendorIndicator;
    }

    public void setVendorIndicator(Character vendorIndicator) {
        this.vendorIndicator = vendorIndicator;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorMailingAddress() {
        return vendorMailingAddress;
    }

    public void setVendorMailingAddress(String vendorMailingAddress) {
        this.vendorMailingAddress = vendorMailingAddress;
    }

    public String getVendorCity() {
        return vendorCity;
    }

    public void setVendorCity(String vendorCity) {
        this.vendorCity = vendorCity;
    }

    public String getVendorState() {
        return vendorState;
    }

    public void setVendorState(String vendorState) {
        this.vendorState = vendorState;
    }

    public String getVendorZipCode() {
        return vendorZipCode;
    }

    public void setVendorZipCode(String vendorZipCode) {
        this.vendorZipCode = vendorZipCode;
    }

    public String getVendorContactName() {
        return vendorContactName;
    }

    public void setVendorContactName(String vendorContactName) {
        this.vendorContactName = vendorContactName;
    }

    public Long getVendorPhoneNumber() {
        return vendorPhoneNumber;
    }

    public void setVendorPhoneNumber(Long vendorPhoneNumber) {
        this.vendorPhoneNumber = vendorPhoneNumber;
    }

    public Character getVendorForeignIndicator() {
        return vendorForeignIndicator;
    }

    public void setVendorForeignIndicator(Character vendorForeignIndicator) {
        this.vendorForeignIndicator = vendorForeignIndicator;
    }

    public String getVendorCountryCode() {
        return vendorCountryCode;
    }

    public void setVendorCountryCode(String vendorCountryCode) {
        this.vendorCountryCode = vendorCountryCode;
    }

    @Override
    public List<ElectronicFileException> validateRecord() {
        List<ElectronicFileException> errors = new ArrayList<ElectronicFileException>();

        if (paymentYear == null) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Payment Year", paymentYear, "Payment year is a required field."));
        } else if (paymentYear.toString().length() != 4) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Payment Year", paymentYear, "Payment year must be 4 digits long."));
        }

        if (!RecordUtil.hasLength(transmitterTin, 9)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "TIN", transmitterTin, "TIN must be 9 characters long."));
        }

        if (!RecordUtil.hasLength(transmitterControlCode, 5)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Trans Control Code", transmitterControlCode, "Transmitter Control Code must be 5 characters long."));
        }

        ArrayList<Character> priorYearIndicators = new ArrayList<Character>();
        priorYearIndicators.add(new Character(' '));
        priorYearIndicators.add(new Character('P'));

        if (!RecordUtil.isValidCharacter(priorYearIndicator, priorYearIndicators)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Prior Year Ind", priorYearIndicator, "Prior year indicator is not a valid character."));
        }

        ArrayList<Character> validTestFileIndicators = new ArrayList<Character>();
        validTestFileIndicators.add(new Character(' '));
        validTestFileIndicators.add(new Character('T'));

        if (!RecordUtil.isValidCharacter(testFileIndicator, validTestFileIndicators)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Test File Ind", testFileIndicator, "Test file indicator is not a valid character."));
        }

        ArrayList<Character> validForiegnIndicators = new ArrayList<Character>();
        validForiegnIndicators.add(new Character(' '));
        validForiegnIndicators.add(new Character('1'));

        if (!RecordUtil.isValidCharacter(foreignEntityIndicator, validForiegnIndicators)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Foreign Entity Ind", foreignEntityIndicator, "Foreign Entity indicator is not a valid character."));
        }

        ArrayList<Character> validVendorIndicators = new ArrayList<Character>();
        validVendorIndicators.add(new Character('V'));
        validVendorIndicators.add(new Character('I'));

        if (!RecordUtil.isValidCharacter(vendorIndicator, validVendorIndicators)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Vendor Ind", vendorIndicator, "Vendor indicator is not a valid character."));
        }

        if (!RecordUtil.hasCharacter(transmitterName1, 40)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Transmitter Name 1", transmitterName1, "Transmitter Name 1 is a required field. [limit = 40]"));
        }
        if (!RecordUtil.hasCharacterEmpty(transmitterName2, 40)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Transmitter Name 2", transmitterName2, "Transmitter Name 2 is too long. [limit = 40]"));
        }

        if (contactPhoneNumber == null) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Contact Phone Number", contactPhoneNumber, "Contact phone number is a required field."));
        }
        if (!RecordUtil.hasCharacterEmpty(contactEmail, 50)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Contact Email", contactEmail, "Contact email address is too long. [limit = 40]"));
        }

        if (!RecordUtil.hasCharacter(companyName1, 40)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Company Name 1", companyName1, "Company Name 1 is a required field. [limit = 40]"));
        }
        if (!RecordUtil.hasCharacterEmpty(companyName2, 40)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Company Name 2", companyName2, "Company Name 2 is too long. [limit = 40]"));
        }

        if (!RecordUtil.hasCharacter(companyMailingAddress, 40)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Company Mailing Address", companyMailingAddress, "Company mailing address is a required field. [limit = 40]"));
        }
        if (!RecordUtil.hasCharacter(companyCity, 40)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Company City ", companyCity, "Company city is a required field. [limit = 40]"));
        }
        if (!RecordUtil.isValidStateCode(companyState)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Company State", companyState, "Company state is not a valid code. [limit = 2]"));
        }
        if (!RecordUtil.isValidZipCode(companyZipCode)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Company Zip Code", companyZipCode, "Company zip code is not a valid code. [limit = 9]"));
        }
        if ("1".equals(foreignEntityIndicator) && !RecordUtil.isValidCountryCode(companyCountryCode)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Company Country Code", companyCountryCode, "Company country code is not a valid code. [limit = 2]"));
        }

        ArrayList<Character> validVendorForiegnIndicators = new ArrayList<Character>();
        validVendorForiegnIndicators.add(new Character(' '));
        validVendorForiegnIndicators.add(new Character('1'));

        if (!RecordUtil.isValidCharacter(vendorForeignIndicator, validVendorForiegnIndicators)) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Vendor Foreign Ind", vendorForeignIndicator, "Foreign Vendor indicator is not a valid character."));
        }

        if (vendorIndicator.equals(new Character('V'))) {
            if (!RecordUtil.hasCharacter(vendorName, 40)) {
                errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Vendor Name", vendorName, "Vendor name is a required field. [limit = 40]"));
            }
            if (!RecordUtil.hasCharacter(vendorMailingAddress, 40)) {
                errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Vendor Mailing Address", vendorMailingAddress, "Vendor mailing address is a required field. [limit = 40]"));
            }
            if (!RecordUtil.hasCharacter(vendorCity, 40)) {
                errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Vendor City", vendorCity, "Vendor city is a required field. [limit = 40]"));
            }
            if (!RecordUtil.isValidStateCode(vendorState)) {
                errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Vendor State", vendorState, "Vendor state is not a valid code. [limit = 2]"));
            }
            if (!RecordUtil.isValidZipCode(vendorZipCode)) {
                errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Vendor Zip Code", vendorZipCode, "Vendor zip code is not a valid code. [limit = 9]"));
            }
            if (vendorPhoneNumber == null) {
                errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Vendor Phone Number", vendorPhoneNumber, "Vendor phone number is a required field."));
            }
            if ("1".equals(vendorForeignIndicator) && !RecordUtil.isValidCountryCode(vendorCountryCode)) {
                errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Vendor Country Code", vendorCountryCode, "Vendor country code is not a valid code. [limit = 2]"));
            }
        }

        if (getRecordString().length() != 750) {
            errors.add(new ElectronicFileException(transmitterTin, companyName1, ElectronicFileException.TRANSMITTER, "Record Length", new Integer(getRecordString().length()), "Record length is not 750."));
        }

        return errors;
    }

    @Override
    public String getRecordString() {
        StringBuffer buf = new StringBuffer();

        buf.append(recordType);
        buf.append(paymentYear.toString());
        buf.append(priorYearIndicator);
        buf.append(transmitterTin);
        buf.append(transmitterControlCode);
        buf.append(RecordUtil.getBlanks(7));
        buf.append(testFileIndicator);
        buf.append(foreignEntityIndicator);
        buf.append(RecordUtil.leftJustifyString(transmitterName1, 40));
        buf.append(RecordUtil.leftJustifyString(transmitterName2, 40));
        buf.append(RecordUtil.leftJustifyString(companyName1, 40));
        buf.append(RecordUtil.leftJustifyString(companyName2, 40));
        buf.append(RecordUtil.leftJustifyString(companyMailingAddress, 40));

        if ('1' == foreignEntityIndicator) {
            buf.append(RecordUtil.leftJustifyString(companyCity + " " + companyState + " " + companyZipCode + " " + companyCountryCode, 51));
        } else {
            buf.append(RecordUtil.leftJustifyString(companyCity, 40));
            buf.append(companyState);
            buf.append(RecordUtil.leftJustifyString(companyZipCode, 9));
        }

        buf.append(RecordUtil.getBlanks(15));
        buf.append(RecordUtil.rightJustifyInteger(numberPayees, 8));
        buf.append(RecordUtil.leftJustifyString(contactName, 40));
        buf.append(RecordUtil.leftJustifyString(contactPhoneNumber.toString(), 15));
        buf.append(RecordUtil.leftJustifyString(contactEmail, 50));
        buf.append(RecordUtil.getBlanks(91));
        buf.append(recordNumberSequence);
        buf.append(RecordUtil.getBlanks(10));
        buf.append(vendorIndicator);
        buf.append(RecordUtil.leftJustifyString(vendorName, 40));
        buf.append(RecordUtil.leftJustifyString(vendorMailingAddress, 40));

        if ('1' == foreignEntityIndicator) {
            buf.append(RecordUtil.leftJustifyString(vendorCity + " " + vendorState + " " + vendorZipCode + " " + vendorCountryCode, 51));
        } else {
            buf.append(RecordUtil.leftJustifyString(vendorCity, 40));
            buf.append(vendorState);
            buf.append(RecordUtil.leftJustifyString(vendorZipCode, 9));
        }

        buf.append(RecordUtil.leftJustifyString(vendorContactName, 40));
        buf.append(RecordUtil.leftJustifyString(vendorPhoneNumber == null ? "" : vendorPhoneNumber.toString(), 15));
        buf.append(RecordUtil.getBlanks(35));
        buf.append(vendorForeignIndicator);
        buf.append(RecordUtil.getBlanks(8));
        buf.append("\r\n");

        return buf.toString();
    }

}
