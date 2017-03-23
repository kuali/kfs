package edu.arizona.kfs.pdp.batch;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.ACHBank;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.dataaccess.OriginationCodeDao;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;

public class AchBankInputFileType extends BatchInputFileTypeBase {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AchBankInputFileType.class);
	
	private BusinessObjectService businessObjectService;
	private DateTimeService dateTimeService;
	private OriginationCodeDao originationCodeDao;
	private String reportPath;
	private String reportPrefix;
	private String reportExtension;
	
	protected class BankReportLine {
		private String bankRoutingNumber;
		private String bankName;
		private String message;
		
		public BankReportLine() {
			bankRoutingNumber = KFSConstants.EMPTY_STRING;
			bankName = KFSConstants.EMPTY_STRING;
			message = KFSConstants.EMPTY_STRING;
		}
		
		public BankReportLine(String bankRoutingNumber, String bankName, String message) {
			super();
			this.bankRoutingNumber = bankRoutingNumber;
			this.bankName = bankName;
			this.message = message;
		}

		public String getBankRoutingNumber() {
			return bankRoutingNumber;
		}

		public void setBankRoutingNumber(String bankRoutingNumber) {
			this.bankRoutingNumber = bankRoutingNumber;
		}

		public String getBankName() {
			return bankName;
		}

		public void setBankName(String bankName) {
			this.bankName = bankName;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}
		
	}
	
	@Override
	public String getFileTypeIdentifer() {
		return KFSConstants.ACHFileConstants.ACH_BANK_FILE_TYPE_IDENTIFIER;
	}

	@Override
	public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
		String fileName = "bank_" + principalName;
		if (StringUtils.isNotBlank(fileUserIdentifier)) {
			fileName += "_" + fileUserIdentifier;
		}
		fileName += "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate());
		
		fileName = StringUtils.remove(fileName,  " ");
		
		return fileName;
	}

	@Override
	public Object parse(byte[] fileByteContent) throws ParseException {
		List<ACHBank> bankList = new ArrayList<ACHBank>();
		List<BankReportLine> reportLines = new ArrayList<BankReportLine>();
		String systemSource = KFSConstants.EMPTY_STRING;
		ACHBank currentBank;
		ACHBank existingBank;
		BankReportLine reportLine;
		Boolean saveBank = true;
		BufferedReader bufferedFileReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));
		String fileLine;
		
		try {
			while((fileLine = bufferedFileReader.readLine()) != null) {
				LOG.info("Bank line: " + fileLine);
				
				//get a valid systemSource from the file
				if (StringUtils.isBlank(systemSource)) {
					systemSource = fileLine.substring(150, 152).trim();
				}
				
				currentBank = new ACHBank(fileLine);
				existingBank = getAchBank(currentBank.getBankRoutingNumber());
				if (ObjectUtils.isNull(existingBank)) {
					saveBank = validateBank(currentBank, reportLines);
					if (saveBank) {
						reportLine = new BankReportLine(currentBank.getBankRoutingNumber(), currentBank.getBankName(), KFSConstants.ACHFileConstants.ACH_BANK_LOADED_SUCCESSFULLY);
						reportLines.add(reportLine);
						businessObjectService.save(currentBank);
						bankList.add(currentBank);
					}
				}
			}
			
			writeReport(reportLines, systemSource);
		}
		catch (IOException e) {
			LOG.error("Error encountered reading from file content", e);
			throw new ParseException("Error encountered reading from file content", e);
		}
		finally {
			IOUtils.closeQuietly(bufferedFileReader);
		}
		return bankList;
	}

	@Override
	public boolean validate(Object parsedFileContents) {
		return true;
	}

	@Override
	public void process(String fileName, Object parsedFileContents) {
		//empty method so compiler doesn't complain
	}

	@Override
	public String getAuthorPrincipalName(File file) {
		String[] fileNameParts = StringUtils.split(file.getName(), "_");
		if (fileNameParts.length >= 2) {
			return fileNameParts[1];
		}
		return null;
	}

	@Override
	public String getTitleKey() {
		return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_ACH_BANK;
	}
	
	private ACHBank getAchBank(String bankRoutingNumber) {
		
		Map<String, String> pkMap = new HashMap<String, String>();
		pkMap.put(PdpPropertyConstants.BANK_ROUTING_NUMBER, bankRoutingNumber);
		ACHBank bank = businessObjectService.findByPrimaryKey(ACHBank.class, pkMap);
		
		return bank;
	}
	
	protected Boolean validateBank(ACHBank bank, List<BankReportLine> reportLines) {
		String bankRoutingNbr = bank.getBankRoutingNumber();
		String bankOfficeCd = bank.getBankOfficeCode();
		String bankTypeCd = bank.getBankTypeCode();
		String bankNewRoutingNbr = bank.getBankNewRoutingNumber();
		String bankNm = bank.getBankName();
		String bankAddr = bank.getBankStreetAddress();
		String bankCity = bank.getBankCityName();
		String bankSt = bank.getBankStateCode();
		String bankZipCd = bank.getBankZipCode();
		String bankPhoneAreaCd = bank.getBankPhoneAreaCode();
		String bankPhonePrefixNbr = bank.getBankPhonePrefixNumber();
		String bankPhoneSuffixNbr = bank.getBankPhoneSuffixNumber();
		String bankInstitutionStatusCd = bank.getBankInstitutionStatusCode();
		String bankDataViewCd = bank.getBankDataViewCode();
		List<String> validOfficeCodes = new ArrayList<String>();
		validOfficeCodes.add(KFSConstants.ACHBankOfficeCodes.MAIN);
		validOfficeCodes.add(KFSConstants.ACHBankOfficeCodes.BRANCH);
		List<String> validTypeCodes = new ArrayList<String>();
		validTypeCodes.add(KFSConstants.ACHBankTypeCodes.FEDERAL_RESERVE);
		validTypeCodes.add(KFSConstants.ACHBankTypeCodes.CUSTOMER_ROUTING_NBR);
		validTypeCodes.add(KFSConstants.ACHBankTypeCodes.NEW_CUSTOMER_ROUTING_NBR);
		
		if (StringUtils.isBlank(bankNm)) {
			reportLines.add(new BankReportLine(bankRoutingNbr, bankNm, KFSConstants.ACHFileConstants.ACH_BANK_MISSING_NM_ERROR));
			return false;
		}
		
		if (StringUtils.isBlank(bankAddr) || StringUtils.isBlank(bankCity) || StringUtils.isBlank(bankSt) || StringUtils.isBlank(bankZipCd)) {
			reportLines.add(new BankReportLine(bankRoutingNbr, bankNm, KFSConstants.ACHFileConstants.ACH_BANK_INCOMPLETE_ADDR_ERROR));
			return false;
		}
		
		if (StringUtils.isBlank(bankTypeCd)) {
			bank.setBankTypeCode(KFSConstants.ACHBankTypeCodes.FEDERAL_RESERVE)	;
		}
		else if (!validTypeCodes.contains(bankTypeCd)) {
			bank.setBankTypeCode(KFSConstants.ACHBankTypeCodes.FEDERAL_RESERVE);
			reportLines.add(new BankReportLine(bankRoutingNbr, bankNm, KFSConstants.ACHFileConstants.ACH_BANK_INVALID_TYP_CD_ERROR));
		}
		else if (bankTypeCd.equalsIgnoreCase(KFSConstants.ACHBankTypeCodes.NEW_CUSTOMER_ROUTING_NBR) && StringUtils.isBlank(bankNewRoutingNbr)) {
			reportLines.add(new BankReportLine(bankRoutingNbr, bankNm, KFSConstants.ACHFileConstants.ACH_BANK_TYP_CD_ROUTING_NBR_ERROR));
			return false;
		}
		
		if (StringUtils.isBlank(bankOfficeCd) || !validOfficeCodes.contains(bankOfficeCd)) {
			bank.setBankOfficeCode(KFSConstants.ACHBankOfficeCodes.MAIN);
			reportLines.add(new BankReportLine(bankRoutingNbr, bankNm, KFSConstants.ACHFileConstants.ACH_BANK_INVALID_OFFICE_CD_ERROR));
		}
		
		if (StringUtils.isBlank(bankNewRoutingNbr)) {
			bank.setBankNewRoutingNumber(KFSConstants.ACHFileDefaultConstants.DEFAULT_BANK_NEW_ROUTING_NUMBER);
		}
		
		if (StringUtils.isBlank(bankPhoneAreaCd)) {
			bank.setBankPhoneAreaCode(KFSConstants.ACHFileDefaultConstants.DEFAULT_BANK_PHONE_AREA_CODE);
			reportLines.add(new BankReportLine(bankRoutingNbr, bankNm, KFSConstants.ACHFileConstants.ACH_BANK_INCOMPLETE_PHONE_ERROR));
		}
		
		if (StringUtils.isBlank(bankPhonePrefixNbr)) {
			bank.setBankPhonePrefixNumber(KFSConstants.ACHFileDefaultConstants.DEFAULT_BANK_PHONE_PREFIX_NUMBER);
			reportLines.add(new BankReportLine(bankRoutingNbr, bankNm, KFSConstants.ACHFileConstants.ACH_BANK_INCOMPLETE_PHONE_ERROR));
		}
		
		if (StringUtils.isBlank(bankPhoneSuffixNbr)) {
			bank.setBankPhoneSuffixNumber(KFSConstants.ACHFileDefaultConstants.DEFAULT_BANK_PHONE_SUFFIX_NUMBER);
			reportLines.add(new BankReportLine(bankRoutingNbr,bankNm, KFSConstants.ACHFileConstants.ACH_BANK_INCOMPLETE_PHONE_ERROR));
		}
		
		if (StringUtils.isBlank(bankInstitutionStatusCd)) {
			bank.setBankInstitutionStatusCode(KFSConstants.ACHFileDefaultConstants.DEFAULT_BANK_INSTITUTION_STATUS_CODE);
			reportLines.add(new BankReportLine(bankRoutingNbr, bankNm, KFSConstants.ACHFileConstants.ACH_BANK_INVALID_INST_STAT_CD));
		}
	
	
		if (StringUtils.isBlank(bankDataViewCd)) {
			bank.setBankDataViewCode(KFSConstants.ACHFileDefaultConstants.DEFAULT_BANK_DATA_VIEW_CODE);
			reportLines.add(new BankReportLine(bankRoutingNbr, bankNm, KFSConstants.ACHFileConstants.ACH_BANK_INVALID_DATA_VIEW_CD));
		}
		
		return true;
	}
		
	protected void writeReport(List<BankReportLine> lines, String systemSource) {
		File reportFile = new File(reportPath + "/" + reportPrefix + "_" + dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()) + "." + reportExtension);
		BufferedWriter writer = null;
		String routingNbr;
		String bankNm;
		
		try {
			writer = new BufferedWriter(new PrintWriter(reportFile));
			writeReportTitle(writer, systemSource);
			writeReportHeadings(writer);
			for (BankReportLine line : lines) {
				routingNbr = rightPad(line.getBankRoutingNumber(), 15, " ");
				bankNm = rightPad(line.getBankName(), 40, " ");
				writer.write(routingNbr + bankNm + line.getMessage());
				writer.newLine();
				writer.newLine();
			}
		}
		catch (FileNotFoundException e) {
			LOG.error(reportFile + " not found " + " " + e.getMessage());
			throw new RuntimeException(reportFile + " not found" + e.getMessage(), e);
		}
		catch (IOException e) {
			LOG.error("Error writing to BufferedWriter " + e.getMessage());
			throw new RuntimeException("Error writing to BufferedWriter " + e.getMessage(), e);
		}
		finally {
			IOUtils.closeQuietly(writer);
		}
	}
	
	protected void writeReportTitle(BufferedWriter writer, String systemSource) {
		OriginationCode originCd = originationCodeDao.findByCode(systemSource);
		String source = systemSource + " - Unknown System Source";
		if (originCd != null) {
			source = originCd.getFinancialSystemDatabaseDesc();
		}
		try {
			writer.write(dateTimeService.toDateString(dateTimeService.getCurrentDate()));
			writer.newLine();
			writer.newLine();
			writer.write("ACH Bank Interface");
			writer.newLine();
			writer.write("System Source: " + systemSource + " - " + source);
			writer.newLine();
			writer.newLine();
		}
		catch(IOException e) {
			LOG.error("Error writing to BufferedWriter " + e.getMessage());
			throw new RuntimeException("Error writing to BufferedWriter " + e.getMessage(), e);
		}
	}
	
	protected void writeReportHeadings(BufferedWriter writer) {
		String bankRoutingHdr = rightPad("BankRouting #", 15, " ");
		String bankNmHdr = rightPad("Bank Name", 40, " ");
		
		try {
			writer.write(bankRoutingHdr + bankNmHdr + "Message");
			writer.newLine();
			writer.newLine();
		}
		catch (IOException e) {
			LOG.error("Error writing to BufferedWriter " + e.getMessage());
			throw new RuntimeException("Error writing to BufferedWriter " + e.getMessage(), e);
		}
	}
	
	protected String rightPad(String valToPad, int sizeToPadTo, String padChar) {
		if (StringUtils.isBlank(valToPad)) {
			return StringUtils.repeat(padChar, sizeToPadTo);
		}
		
		if (valToPad.length() >= sizeToPadTo) {
			return valToPad;
		}
		
		return valToPad + StringUtils.repeat(padChar, sizeToPadTo - valToPad.length());
	}
	
	public void setBusinessObjectService (BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}
	
	public void setDateTimeService (DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}
	
	public void setOriginationCodeDao(OriginationCodeDao originationCodeDao) {
		this.originationCodeDao = originationCodeDao;
	}
	
	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}
	
	public void setReportExtension(String reportExtension) {
		this.reportExtension = reportExtension;
	}
	
	public void setReportPrefix(String reportPrefix) {
		this.reportPrefix = reportPrefix;
	}

}
