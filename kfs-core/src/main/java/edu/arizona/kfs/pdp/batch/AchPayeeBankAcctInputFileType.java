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
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.ACHBank;
import org.kuali.kfs.sys.batch.BatchInputFileTypeBase;
import org.kuali.kfs.sys.businessobject.OriginationCode;
import org.kuali.kfs.sys.dataaccess.OriginationCodeDao;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.kim.impl.KIMPropertyConstants;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

import edu.arizona.kfs.pdp.PdpConstants;
import edu.arizona.kfs.pdp.businessobject.PayeeACHAccount;
import edu.arizona.kfs.pdp.util.PayeeACHAcctFlatFileConverter;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSKeyConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;


public class AchPayeeBankAcctInputFileType extends BatchInputFileTypeBase {
	private static final org.apache.log4j.Logger LOG = Logger.getLogger(AchPayeeBankAcctInputFileType.class);

	private Set<String> overridePayeeMemberSet = null;
	private Set<String> existingRoutingNumberSet = null;
	
	private String reportPath;
	private String reportPrefix;
	private String reportExtension;
	
	private BusinessObjectService businessObjectService;
	private DateTimeService dateTimeService;
	private ParameterService parameterService;
	private PersonService personService;
	private GroupService groupService;
	private OriginationCodeDao originationCodeDao;
	
	protected class PayeeReportLine {
		private String payeeID;
		private String payeeNm;
		private String message;
		
		public PayeeReportLine() {
			payeeID = KFSConstants.EMPTY_STRING;
			payeeNm = KFSConstants.EMPTY_STRING;
			message = KFSConstants.EMPTY_STRING;
		}
		
		public PayeeReportLine(String payeeID, String payeeNm, String message) {
			this.payeeID = payeeID;
			this.payeeNm = payeeNm;
			this.message = message;
		}

		public String getPayeeID() {
			return payeeID;
		}

		public void setPayeeID(String payeeID) {
			this.payeeID = payeeID;
		}

		public String getPayeeNm() {
			return payeeNm;
		}

		public void setPayeeNm(String payeeNm) {
			this.payeeNm = payeeNm;
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
		return KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_FILE_TYPE_IDENTIFIER;
	}

	@Override
	public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifier) {
		String fileName = "bank_" + principalName;
		if (StringUtils.isNotBlank(fileUserIdentifier)) {
			fileName += "_" + fileUserIdentifier;
		}
		
		fileName += "_" + dateTimeService.toDateStringForFilename(dateTimeService.getCurrentDate());
		
		fileName = StringUtils.remove(fileName, " ");
		
		return fileName;
	}

	@Override
	public Object parse(byte[] fileByteContent) throws ParseException {
		LOG.debug("parse() - start=" + new Date());
		
		List<PayeeACHAccount> retval = new ArrayList<PayeeACHAccount>();
		List<PayeeReportLine> reportLines = new ArrayList<PayeeReportLine>();
		String systemSource = KFSConstants.EMPTY_STRING;
		BufferedReader bufferedFileReader = null;
		
		try {
			bufferedFileReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(fileByteContent)));
			
			loadOverridePayeeMemberSet();
			
			List<PayeeACHAccount> payeeAccounts = new ArrayList<PayeeACHAccount>();
			
			String fileLine = null;
			
			while ((fileLine = bufferedFileReader.readLine()) != null) {
				
				if (StringUtils.isBlank(systemSource)) {
					systemSource = fileLine.substring(PdpConstants.ACHFilePropertyPositions.START_OF_SYSTEM_SOURCE, PdpConstants.ACHFilePropertyPositions.END_OF_SYSTEM_SOURCE).trim();
				}
				
				payeeAccounts.add(PayeeACHAcctFlatFileConverter.convert(fileLine));
			}
			
			LOG.debug(payeeAccounts.size() + " payee accounts loaded");
			
			loadExistingRoutingNumberSet(payeeAccounts);
			
			Map<String, Person> payees = loadPayees(payeeAccounts);
			
			LOG.debug(payees.size() + " payees loaded");
			
			for (PayeeACHAccount payeeAccount : payeeAccounts) {
				processPayeeAcct(getPayee(payeeAccount, payees), payeeAccount, reportLines, retval);
			}
			
			LOG.debug(reportLines.size() + " report lines generated");
			
			writeReport(reportLines, systemSource);
		}
		catch (IOException e) { 
			LOG.error("Error encountered reading from file content", e);
			throw new ParseException("Error encountered reading from file content", e);
		}
		finally {
			try {
				bufferedFileReader.close();
			}
			catch (Exception ex) {
				LOG.error("Error encountered cloasing BufferedReader");
			}
		}
		
		LOG.debug("parse() - ends =" + new Date());
		
		return retval;
	}

	@Override
	public boolean validate(Object parsedFileContents) {
		return true;
	}

	@Override
	public void process(String fileName, Object parsedFileContents) {
		// empty method so compiler doesn't complain

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
		return KFSKeyConstants.MESSAGE_BATCH_UPLOAD_TITLE_ACH_PAYEE;
	}
	
	private Person getPayee(PayeeACHAccount payeeAccount, Map<String, Person> payees) {
		Person retval = payees.get(getPayeeKey(payeeAccount));
		
		if (retval == null) {
			retval = getPersonFromPayeeInfo(payeeAccount.getPayeeIdNumber(), payeeAccount.getPayeeIdentifierTypeCode());
		}
		
		return retval;
	}
	
	private File getFile(String dir, String prefix, String ext) {
		StringBuilder s = new StringBuilder(128);
		s.append(dir);
		s.append(File.separator);
		s.append(prefix);
		s.append("_");
		s.append(dateTimeService.toDateTimeStringForFilename(dateTimeService.getCurrentDate()));
		s.append(".");
		s.append(ext);
		
		return new File(s.toString());
	}
	
	private String getPayeeKey(PayeeACHAccount payeeAccount) {
		String idField = null;
		if (payeeAccount.getPayeeIdentifierTypeCode().equalsIgnoreCase(PdpConstants.PayeeIdTypeCodes.EMPLOYEE)) {
			idField = KIMPropertyConstants.Person.EMPLOYEE_ID;
		}
		else {
			idField = KIMPropertyConstants.Person.ENTITY_ID;
		}
		
		return getPayeeKey(idField, payeeAccount.getPayeeIdNumber());
	}
	
	private String getPayeeKey(String idField, String id) {
		StringBuilder retval = new StringBuilder(64);
		
		retval.append(idField);
		retval.append(".");
		retval.append(id);
		
		return retval.toString();
	}
	
	private Map<String, Person> loadPayees(List<PayeeACHAccount> payeeAccounts) {
		Map<String, Person> retval = new HashMap<String, Person>();
		
		Set<String> hsEmployees = new HashSet<String>();
		Set<String> hsEntities = new HashSet<String>();
		
		for (PayeeACHAccount payeeAccount : payeeAccounts) {
			if (payeeAccount.getPayeeIdentifierTypeCode().equalsIgnoreCase(PdpConstants.PayeeIdTypeCodes.EMPLOYEE)) {
				hsEmployees.add(payeeAccount.getPayeeIdNumber());
			}
			else {
				hsEntities.add(payeeAccount.getPayeeIdNumber());
			}
		}
		
		if (LOG.isDebugEnabled()) {
			LOG.debug("employeeCount=" + hsEmployees.size());
			LOG.debug("entityCount=" + hsEntities.size());
		}
		
		List<Map<String, String>> searchCriteria = loadSearchCriteriaMaps(hsEmployees, hsEntities);
		
		if (!searchCriteria.isEmpty()) {
			for (Map<String, String> criteria : searchCriteria) {
				long start = System.currentTimeMillis();
				
				String payeeIdType = getPayeeIdType(criteria);
				
				List<? extends Person> persons = personService.findPeople(criteria);
				
				if (persons != null) {
					for (Person person : persons) {
						String principalId = person.getPrincipalId();
						
						if (StringUtils.isNotBlank(principalId) && principalId.startsWith(PdpConstants.ACHFileConstants.TEST_PRINCIPAL_PREFIX)) {
							LOG.debug("found test person= " + person.getNameUnmasked() + ", principalId=" + principalId);
						}
						else {
							String employeeId = person.getEmployeeId();
							
							if (StringUtils.isBlank(employeeId) && KIMPropertyConstants.Person.EMPLOYEE_ID.equals(payeeIdType)) {
								Map<String, String> map = person.getExternalIdentifiers();
								
								if (map != null) {
									employeeId = map.get(KIMPropertyConstants.Person.EMPLOYEE_ID);
								}
							}
							
							if (StringUtils.isNotBlank(employeeId)) {
								retval.put(getPayeeKey(KIMPropertyConstants.Person.EMPLOYEE_ID, employeeId), person);
							}
							else if (StringUtils.isNotBlank(person.getEntityId())) {
								retval.put(getPayeeKey(KIMPropertyConstants.Person.ENTITY_ID, person.getEntityId()), person);
							}
							else {
								LOG.warn("person " + person.getNameUnmasked()  + " employeeId and entityId are empty");
							}
						}
					}
				}
				
				LOG.debug("found " + persons.size() + " persons in " + ((System.currentTimeMillis() - start)/1000) + "sec");
			}
		}
		
		LOG.debug("persons found: " + retval.size());
		
		return retval;
	}
	
	private String getPayeeIdType(Map<String, String> map) {
		String retval = null;
		
		if (map.keySet().size() == 1) {
			if (map.keySet().contains(KIMPropertyConstants.Person.EMPLOYEE_ID)) {
				retval = KIMPropertyConstants.Person.EMPLOYEE_ID;
			}
			else if (map.keySet().contains(KIMPropertyConstants.Person.ENTITY_ID)) {
				retval = KIMPropertyConstants.Person.ENTITY_ID;
			}
		}
		return retval;
	}
	
	private List<String> buildPersonSearchIdLists(Set<String> idset) {
		List<String> retval = new ArrayList<String>();
		
		StringBuilder s = new StringBuilder(1024);
		String comma = KFSConstants.EMPTY_STRING;
		int cnt = 0;
		
		for (String id : idset) {
			if (cnt > PdpConstants.ACHFileConstants.MAX_PERSON_SEARCH_ID_COUNT) {
				cnt = 0;
				retval.add(s.toString());
				s.setLength(0);
				comma = KFSConstants.EMPTY_STRING;
			}
			
			cnt++;
			s.append(comma);
			s.append(id);
			comma = ",";
		}
		
		if (s.length() > 0) {
			retval.add(s.toString());
		}
		
		return retval;
	}
	
	private List<Map<String, String>> loadSearchCriteriaMaps(Set<String> hsEmployees, Set<String> hsEntities) {
		List<Map<String, String>> retval = new ArrayList<Map<String, String>>();
		
		if (!hsEmployees.isEmpty()) {
			List<String> idLists = buildPersonSearchIdLists(hsEmployees);
			
			for (String idList : idLists) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(KIMPropertyConstants.Person.EMPLOYEE_ID, idList);
				retval.add(map);
			}
		}
		
		if (!hsEntities.isEmpty()) {
			List<String> idLists = buildPersonSearchIdLists(hsEntities);
			
			for (String idList : idLists) {
				Map<String, String> map = new HashMap<String, String>();
				map.put(KIMPropertyConstants.Person.ENTITY_ID, idList);
				retval.add(map);
			}
		}
		
		return retval;
	}
	
	private boolean isExistingRoutingNumber(String routingNumber) {
		boolean retval = false;
		
		if (existingRoutingNumberSet != null) {
			if (StringUtils.isNotBlank(routingNumber)) {
				retval = existingRoutingNumberSet.contains(routingNumber);
			}
		}
		else {
			Map<String, String> pkMap = new HashMap<String, String>();
			pkMap.put(PdpPropertyConstants.BANK_ROUTING_NUMBER, routingNumber);
			ACHBank bank = businessObjectService.findByPrimaryKey(ACHBank.class, pkMap);
			retval = ObjectUtils.isNotNull(bank);
		}
		
		return retval;
	}
	
	protected void processPayeeAcct(Person payee, PayeeACHAccount currentPayeeAcct, List<PayeeReportLine> reportLines, List<PayeeACHAccount> payeeAccountList) {
		List<String> validPayeeIDTypeCodes = new ArrayList<String>();
		validPayeeIDTypeCodes.add(PdpConstants.PayeeIdTypeCodes.ENTITY);
		validPayeeIDTypeCodes.add(PdpConstants.PayeeIdTypeCodes.EMPLOYEE);
		PayeeReportLine reportLine = new PayeeReportLine();
		PayeeACHAccount existingPayeeAcct;
		boolean validChanges = false;
		boolean savePayeeAcct = true;
		
		String payeeName = currentPayeeAcct.getPayeeName();
		
		if (payeeName.length() > PdpConstants.ACHFileConstants.REPORT_NAME_FIELD_LENGTH) {
			payeeName = getTruncatedPayeeName(payeeName);
		}
		
		if (!isExistingRoutingNumber(currentPayeeAcct.getBankRoutingNumber())) {
			reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_INVALID_ROUTING);
			reportLines.add(reportLine);
		}
		else if (StringUtils.isBlank(currentPayeeAcct.getPayeeIdNumber())) {
			reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_MISSING_PAYEE_ID_NBR_ERROR);
			reportLines.add(reportLine);
		}
		else if (StringUtils.isBlank(currentPayeeAcct.getPayeeEmailAddress())) {
			reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_MISSING_EMAIL);
			reportLines.add(reportLine);
		}
		else if (StringUtils.isBlank(currentPayeeAcct.getBankAccountNumber())) {
			reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_MISSING_ACCT_NBR_ERROR);
			reportLines.add(reportLine);
		}
		else if (StringUtils.isBlank(currentPayeeAcct.getPayeeIdentifierTypeCode()) || !validPayeeIDTypeCodes.contains(currentPayeeAcct.getPayeeIdentifierTypeCode())) {
			reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_INVALID_PAYEE_ID_TYP_ERROR);
			reportLines.add(reportLine);
		}
		else if (isPayeeInOverrideGroup(payee)) {
			reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_OVERRIDE_EMPL_ERROR);
			reportLines.add(reportLine);
		}
		else {
			existingPayeeAcct = getPayeeACHAccount(currentPayeeAcct.getPayeeIdNumber(), currentPayeeAcct.getPayeeIdentifierTypeCode());
			if (ObjectUtils.isNull(existingPayeeAcct)) {
				savePayeeAcct = validatePayeeAcct(payee, currentPayeeAcct, reportLines);
				if (savePayeeAcct) {
					if (PdpConstants.PayeeIdTypeCodes.ENTITY.equalsIgnoreCase(currentPayeeAcct.getPayeeIdentifierTypeCode())) {
						reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_ENT_LOADED_SUCCESSFULLY);
					}
					else if (PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equalsIgnoreCase(currentPayeeAcct.getPayeeIdentifierTypeCode())) {
						reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_EMP_LOADED_SUCCESSFULLY);
					}
					else {
						reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_LOADED_SUCCESSFULLY);
					}
					
					reportLines.add(reportLine);
					payeeAccountList.add(currentPayeeAcct);
					businessObjectService.save(currentPayeeAcct);
				}
			}
			else {
				validChanges = comparePayeeAccts(payee, currentPayeeAcct, existingPayeeAcct, reportLines);
				
				if (validChanges) {
					if (PdpConstants.PayeeIdTypeCodes.ENTITY.equalsIgnoreCase(existingPayeeAcct.getPayeeIdentifierTypeCode())) {
						reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_ENT_UPDATED_SUCCESSFULLY);
					}
					else if (PdpConstants.PayeeIdTypeCodes.EMPLOYEE.equalsIgnoreCase(existingPayeeAcct.getPayeeIdentifierTypeCode())) {
						reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_EMP_UPDATED_SUCCESSFULLY);
					}
					else {
						reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_UPDATED_SUCCESSFULLY);
					}
					
					reportLines.add(reportLine);
					payeeAccountList.add(existingPayeeAcct);
					businessObjectService.save(existingPayeeAcct);
				}
			}
		}
	}
	
	private void loadExistingRoutingNumberSet(List <PayeeACHAccount> payeeAccounts) {
		Map<String, Collection<String>> map = new HashMap<String, Collection<String>>();
		
		Set<String> routingNumbers = new HashSet<String>();
		for (PayeeACHAccount payeeAccount : payeeAccounts) {
			String routingNumber = payeeAccount.getBankRoutingNumber();
			if (StringUtils.isNotBlank(routingNumber)) {
				routingNumbers.add(routingNumber);
			}
		}
		
		LOG.debug("found " + routingNumbers.size() + " unique routing numbers in the input");
		
		map.put(PdpPropertyConstants.BANK_ROUTING_NUMBER, routingNumbers);
		
		Collection<ACHBank> banks = businessObjectService.findMatching(ACHBank.class, map);
		
		existingRoutingNumberSet = new HashSet<String>();
		
		for (ACHBank bank : banks) {
			existingRoutingNumberSet.add(bank.getBankRoutingNumber());
		}
		
		LOG.debug("found " + existingRoutingNumberSet.size() + " existing routing numbers");
	}
	
	private PayeeACHAccount getPayeeACHAccount(String payeeIdNbr, String payeeIdType) {
		Map<String, String> pkMap = new HashMap<String, String>();
		pkMap.put(PdpPropertyConstants.PAYEE_ID_NUMBER, payeeIdNbr);
		pkMap.put(PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE, payeeIdType);
		PayeeACHAccount payeeACHAccount = businessObjectService.findByPrimaryKey(PayeeACHAccount.class, pkMap);
		
		return payeeACHAccount;
	}
	
	private Person getPersonFromPayeeInfo(String payeeIdNbr, String payeeIdType) {
		Person retval = null;
		Map<String, String> fieldsForLookup =  new HashMap<String, String>();
		
		if (payeeIdType.equalsIgnoreCase(PdpConstants.PayeeIdTypeCodes.EMPLOYEE)) {
			fieldsForLookup.put(KIMPropertyConstants.Person.EMPLOYEE_ID, payeeIdNbr);
		}
		else {
			fieldsForLookup.put(KIMPropertyConstants.Person.ENTITY_ID, payeeIdNbr);
		}
		
		try {
			List<? extends Person> persons = personService.findPeople(fieldsForLookup);
			if (!persons.isEmpty()) {
				retval = persons.get(0);
			}
		}
		catch (Exception ex) {
			LOG.error("Exception ocurred trying to find person", ex);
		}
		
		return retval;
	}
	
	private void loadOverridePayeeMemberSet() {
		String groupId = parameterService.getParameterValueAsString(LoadAchPayeeBankAcctStep.class, KFSParameterKeyConstants.ACHFileConstants.BANKING_INFORMATION_OVERRIDE_GROUP);
		List<String> members = getGroupService().getMemberPrincipalIds(groupId);
		overridePayeeMemberSet = new HashSet<String>();
		if (members != null) {
			overridePayeeMemberSet.addAll(members);
		}
		
		LOG.debug("overridePayeeMemberSet.size()=" + overridePayeeMemberSet.size());
	}
	
	private boolean isPayeeInOverrideGroup(Person payee) {
		boolean retval = false; 
		
		if (overridePayeeMemberSet == null) {
			loadOverridePayeeMemberSet();
		}
		
		if ((payee != null) && StringUtils.isNotBlank(payee.getPrincipalId())) {
			retval = overridePayeeMemberSet.contains(payee.getPrincipalId());
		}
		
		return retval;
	}
	
	protected boolean comparePayeeAccts(Person payee, PayeeACHAccount currentPayeeAcct, PayeeACHAccount existingPayeeAcct, List<PayeeReportLine> reportLines) {
		PayeeReportLine reportLine;
		boolean payeeExists = true;
		boolean validAcct = false; 
		boolean changesMade = false;
		List<String> validPayeeIdTypeCodes = new ArrayList<String>();
		List<String> validAcctTypeCodes = new ArrayList<String>();
		String oldBankRoutingNumber;
		String newBankRoutingNumber;
		String oldBankAccountNumber;
		String newBankAccountNumber;
		String oldPayeeName;
		String newPayeeName;
		String oldPayeeEmailAddr;
		String newPayeeEmailAddr;
		String oldPayeeIdTypeCode;
		String newPayeeIdTypeCode;
		String oldAchTransactionType;
		String newAchTransactionType;
		String oldBankAccountTypeCode;
		String newBankAccountTypeCode;
		
		validPayeeIdTypeCodes.add(PdpConstants.PayeeIdTypeCodes.ENTITY);
		validPayeeIdTypeCodes.add(PdpConstants.PayeeIdTypeCodes.EMPLOYEE);
		validAcctTypeCodes.add(KFSConstants.ACHAcctTypeCodes.CHECKING);
		validAcctTypeCodes.add(KFSConstants.ACHAcctTypeCodes.SAVINGS);
		oldBankRoutingNumber = existingPayeeAcct.getBankRoutingNumber();
		oldBankAccountNumber = existingPayeeAcct.getBankAccountNumber();
		oldPayeeName = existingPayeeAcct.getPayeeName();
		oldPayeeEmailAddr = existingPayeeAcct.getPayeeEmailAddress();
		oldPayeeIdTypeCode = existingPayeeAcct.getPayeeIdentifierTypeCode();
		oldAchTransactionType = existingPayeeAcct.getAchTransactionType();
		oldBankAccountTypeCode = existingPayeeAcct.getBankAccountTypeCode();
		
		if (payee == null) {
			reportLine = new PayeeReportLine(currentPayeeAcct.getPayeeIdNumber(), currentPayeeAcct.getPayeeName(), KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_INVALID_EXISTING_MATCH);
			reportLines.add(reportLine);
			payeeExists = false;
		}
		
		if (payeeExists) {
			validAcct = validatePayeeAcct(payee, currentPayeeAcct, reportLines);
		}
		
		newBankRoutingNumber = currentPayeeAcct.getBankRoutingNumber();
		newBankAccountNumber = currentPayeeAcct.getBankAccountNumber();
		newPayeeName = currentPayeeAcct.getPayeeName();
		newPayeeEmailAddr = currentPayeeAcct.getPayeeEmailAddress();
		newPayeeIdTypeCode = currentPayeeAcct.getPayeeIdentifierTypeCode();
		newAchTransactionType = currentPayeeAcct.getAchTransactionType();
		newBankAccountTypeCode = currentPayeeAcct.getBankAccountTypeCode();
		
		if (validAcct) {
			
			if (StringUtils.isNotBlank(newPayeeName) && !newPayeeName.equals(oldPayeeName)) {
				existingPayeeAcct.setPayeeName(newPayeeName);
				changesMade = true;
			}
			
			if (StringUtils.isNotBlank(newBankRoutingNumber) && !newBankRoutingNumber.equalsIgnoreCase(oldBankRoutingNumber)) {
				existingPayeeAcct.setBankRoutingNumber(newBankRoutingNumber);
				changesMade = true;
			}
			
			if (StringUtils.isNotBlank(newBankAccountNumber) && !newBankRoutingNumber.equalsIgnoreCase(oldBankAccountNumber)) {
				existingPayeeAcct.setBankAccountNumber(newBankAccountNumber);
				changesMade = true;
			}
			
			if (StringUtils.isNotBlank(newPayeeEmailAddr) && !newPayeeEmailAddr.equalsIgnoreCase(oldPayeeEmailAddr)) {
				existingPayeeAcct.setPayeeEmailAddress(newPayeeEmailAddr);
				changesMade = true;
			}
			
			if (validPayeeIdTypeCodes.contains(newPayeeIdTypeCode) && !newPayeeIdTypeCode.equalsIgnoreCase(oldPayeeIdTypeCode)) {
				existingPayeeAcct.setPayeeIdentifierTypeCode(newPayeeIdTypeCode);
				changesMade = true;
			}
			
			if (StringUtils.isBlank(oldAchTransactionType) || !newAchTransactionType.equals(oldAchTransactionType)) {
				existingPayeeAcct.setAchTransactionType(newAchTransactionType);
				changesMade = true;
			}
			
			if (existingPayeeAcct.isActive() != currentPayeeAcct.isActive()) {
				existingPayeeAcct.setActive(currentPayeeAcct.isActive());
				changesMade = true;
			}
			
			if (validAcctTypeCodes.contains(newBankAccountTypeCode) && !newBankAccountTypeCode.equalsIgnoreCase(oldBankAccountTypeCode)) {
				existingPayeeAcct.setBankAccountTypeCode(newBankAccountTypeCode);
				changesMade = true;
			}
		}
		return changesMade;
	}
	
	protected boolean validatePayeeAcct(Person payee, PayeeACHAccount payeeAcct, List<PayeeReportLine> reportLines) {
		PayeeReportLine reportLine;
		boolean validAcct = true;
		List<String> validAcctTypeCodes = new ArrayList<String>();
		validAcctTypeCodes.add(KFSConstants.ACHAcctTypeCodes.CHECKING);
		validAcctTypeCodes.add(KFSConstants.ACHAcctTypeCodes.SAVINGS);
		String bankRoutingNumber = payeeAcct.getBankRoutingNumber();
		String payeeName = payeeAcct.getPayeeName();
		String payeeIdNumber = payeeAcct.getPayeeIdNumber();
		String achTransactionType = payeeAcct.getAchTransactionType();
		String bankAcctTypeCode = payeeAcct.getBankAccountTypeCode();
		
		if (StringUtils.isBlank(bankRoutingNumber)) {
			reportLine = new PayeeReportLine(payeeIdNumber, payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_MISSING_ROUTING_ERROR);
			reportLines.add(reportLine);
		}
		
		if (StringUtils.isBlank(payeeName)) {
			reportLine = new PayeeReportLine(payeeIdNumber, payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_MISSING_PAYEE_NM_ERROR);
			reportLines.add(reportLine);
			validAcct = false;
		}
		
		if (payee == null) {
			reportLine = new PayeeReportLine(payeeIdNumber, payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_NO_NAME_MATCH);
			reportLines.add(reportLine);
			validAcct = false;
		}
		else {
			payeeAcct.setPayeeName(payee.getNameUnmasked().toUpperCase());
		}
		
		if (StringUtils.isBlank(achTransactionType)) {
			payeeAcct.setAchTransactionType(KFSConstants.ACHFileDefaultConstants.DEFAULT_ACH_TRANSACTION_TYPE);
		}
		
		if (StringUtils.isBlank(bankAcctTypeCode)) {
			reportLine = new PayeeReportLine(payeeIdNumber, payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_MISSING_BANK_ACCT_TYP_ERROR);
			reportLines.add(reportLine);
			validAcct = false;
		}
		else if (!validAcctTypeCodes.contains(bankAcctTypeCode)) {
			reportLine = new PayeeReportLine(payeeIdNumber, payeeName, KFSConstants.ACHFileConstants.ACH_PAYEE_ACCT_INVALID_BANK_ACCT_TYP_ERROR);
			reportLines.add(reportLine);
			validAcct = false;
		}
		
		return validAcct;
	}
	
	protected void writeReport(List<PayeeReportLine> lines, String systemSource) {
		File reportFile = getFile(reportPath, reportPrefix, reportExtension);
		BufferedWriter writer = null;
		String payeeId;
		String payeeNm;
		
		try {
			LOG.debug("reportFile: " + reportFile.getAbsolutePath());
			
			writer = new BufferedWriter(new PrintWriter(reportFile));
			writeReportTitle(writer, systemSource);
			writeReportHeadings(writer);
			for (PayeeReportLine line : lines) {
				payeeId = rightPad(line.getPayeeID(), 20, " ");
				payeeNm = rightPad(line.getPayeeNm(), 35, " ");
				writer.write(payeeId + payeeNm + line.getMessage());
				writer.newLine();
				writer.newLine();
			}
		}
		catch (FileNotFoundException e) {
			LOG.error(reportFile + " not found " + " " + e.getMessage());
			throw new RuntimeException(reportFile + " not found " + " " + e.getMessage(), e);
		}
		catch (IOException e) {
			LOG.error("Error writing to BufferedWriter " + e.getMessage());
			throw new RuntimeException("Error writing to BufferedWriter " + e.getMessage(), e);
		}
		finally {
			try {
				IOUtils.closeQuietly(writer);
			}
			catch (Exception e) {
				LOG.error("Error encountered when closing BufferedWriter");
			}
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
			writer.write("Payee ACH Account Interface");
			writer.newLine();
			writer.write("System Source: " + systemSource + " - " + source);
			writer.newLine();
			writer.newLine();
		}
		catch (IOException e) {
			LOG.error("Error writing to BufferedWrtier " + e.getMessage());
			throw new RuntimeException("Error writing to BufferedWriter " + e.getMessage(), e);
		}
	}
	
	protected void writeReportHeadings(BufferedWriter writer) {
		String payeeIdHdr = rightPad("Payee ID #", 20, " ");
		String payeeNmHdr = rightPad("Payee Name", 35, " ");
		
		try {
			writer.write(payeeIdHdr + payeeNmHdr + "Message");
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

	public BusinessObjectService getBusinessObjectService() {
		return businessObjectService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	public ParameterService getParameterService() {
		return parameterService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

	public void setDateTimeService(DateTimeService dateTimeService) {
		this.dateTimeService = dateTimeService;
	}

	public PersonService getPersonService() {
		return personService;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public GroupService getGroupService() {
		if (groupService == null) {
			this.groupService = KimApiServiceLocator.getGroupService();
		}
		
		return this.groupService;
	}

	public void setGroupService(GroupService groupService) {
		if (groupService == null) {
			this.groupService = KimApiServiceLocator.getGroupService();
		}
		else {
			this.groupService = groupService;
		}
	}

	public void setReportPath(String reportPath) {
		this.reportPath = reportPath;
	}

	public void setReportPrefix(String reportPrefix) {
		this.reportPrefix = reportPrefix;
	}

	public void setReportExtension(String reportExtension) {
		this.reportExtension = reportExtension;
	}

	public void setOriginationCodeDao(OriginationCodeDao originationCodeDao) {
		this.originationCodeDao = originationCodeDao;
	}
	
	private String getTruncatedPayeeName(String payeeName) {
		String retval = payeeName;
		
		if (StringUtils.isNotBlank(payeeName)) {
			int pos =  payeeName.indexOf(",");
			
			StringBuffer nameBuf = new StringBuffer(PdpConstants.ACHFileConstants.REPORT_NAME_FIELD_LENGTH);
			if (pos > -1) {
				nameBuf.append(payeeName.substring(0, pos));
				nameBuf.append(",");
				
				StringTokenizer st = new StringTokenizer(payeeName.substring(pos + 1));
				
				while (st.hasMoreTokens()) {
					String token = st.nextToken();
					if ((nameBuf.length() + token.length() + 1) <= PdpConstants.ACHFileConstants.REPORT_NAME_FIELD_LENGTH) {
						nameBuf.append(" ");
						nameBuf.append(token);
					}
				}
				
				retval = nameBuf.toString();
			}
			else {
				
				StringTokenizer st = new StringTokenizer(payeeName);
				
				if (st.countTokens() > 2) {
					List<String> nameParts = new ArrayList<String>();
					
					while (st.hasMoreTokens()) {
						nameParts.add(st.nextToken());
					}
					
					String firstName = nameParts.get(0);
					String middleName = nameParts.get(1);
					String lastName = nameParts.get(nameParts.size() - 1);
					
					if ((firstName.length() + 3 + lastName.length()) <= PdpConstants.ACHFileConstants.REPORT_NAME_FIELD_LENGTH) {
						nameBuf.append(firstName);
						nameBuf.append(" ");
						nameBuf.append(middleName.charAt(0));
						nameBuf.append(" ");
						nameBuf.append(lastName);
					}
					
					retval = nameBuf.toString();
				}
			}
		}
		
		LOG.debug("payee name: " + payeeName);
		LOG.debug("truncated payee name: " + retval);
		return retval;
	}
	
}
