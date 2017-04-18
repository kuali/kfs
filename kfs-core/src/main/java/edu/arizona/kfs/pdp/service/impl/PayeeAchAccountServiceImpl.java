package edu.arizona.kfs.pdp.service.impl;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kim.api.group.GroupService;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.PersonService;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

import edu.arizona.kfs.pdp.PdpConstants;
import edu.arizona.kfs.pdp.batch.LoadAchPayeeBankAcctStep;
import edu.arizona.kfs.pdp.businessobject.PayeeACHAccount;
import edu.arizona.kfs.pdp.service.PayeeAchAccountService;
import edu.arizona.kfs.pdp.util.PayeeACHAcctFlatFileConverter;
import edu.arizona.kfs.sys.KFSConstants;
import edu.arizona.kfs.sys.KFSParameterKeyConstants;

@Transactional
public class PayeeAchAccountServiceImpl implements PayeeAchAccountService {
	private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PayeeAchAccountServiceImpl.class);
	
	private BusinessObjectService businessObjectService;
	private BatchInputFileService batchInputFileService;
	private BatchInputFileType achPayeeBankAcctInputFileType;
	private ParameterService parameterService;
	private GroupService groupService;
	private PersonService personService;

	@Override
	public boolean loadAchPayeeAccountFile(String inputFileName) {
		Integer accountsReset = resetRecordsOfType(inputFileName);
		
		LOG.debug("Payees deleted " + accountsReset);
		
		Collection<PayeeACHAccount> payeeAccts = loadFile(inputFileName);
		if (payeeAccts == null || payeeAccts.isEmpty()) {
			LOG.warn("No banks in input file " + inputFileName);
		}
		
		LOG.info("Total accounts loaded: " + Integer.toString(payeeAccts.size()));
		return true;
	}

	@Override
	public boolean isPayeeInOverrideGroup(Person payee) {
		boolean inGroupFlag = false;
		String groupId = parameterService.getParameterValueAsString(LoadAchPayeeBankAcctStep.class, KFSParameterKeyConstants.ACHFileConstants.BANKING_INFORMATION_OVERRIDE_GROUP);
		
		if (payee != null) {
			inGroupFlag = getGroupService().isMemberOfGroup(payee.getPrincipalId(), groupId);
		}
		
		return inGroupFlag;
	}

	@Override
	public List<String> getPrincipalIdsInOverrideGroup() {
		String groupId = parameterService.getParameterValueAsString(LoadAchPayeeBankAcctStep.class, KFSParameterKeyConstants.ACHFileConstants.BANKING_INFORMATION_OVERRIDE_GROUP);
		if (StringUtils.isBlank(groupId)) {
			return null;
		}
		
		List<String> overrideIds = getGroupService().getDirectMemberPrincipalIds(groupId);
		return overrideIds;
	}
	
	protected List<String> addEmplIdsToPrincipalIds(List<String> principalIds) {
		if (principalIds == null) {
			return null;
		}
		
		List<String> emplIds = new ArrayList<String>();
		
		Person person;
		String emplId;
		for (String principalId : principalIds) {
			person = personService.getPerson(principalId);
			emplId = person.getEmployeeId();
			if (StringUtils.isNotBlank(emplId)) {
				if (!emplIds.contains(emplId)) {
					emplIds.add(emplId);
				}
			}
		}
		
		emplIds.addAll(principalIds);
		return emplIds;
	}
	
	protected Integer resetRecordsOfType(String inputFileName) {
		Integer recordsReset = new Integer(0);
		
		String payeeType = validateTypeUsedInFile(inputFileName);
		if (StringUtils.isBlank(payeeType)) {
			throw new RuntimeException("PayeeType was blank from file [" + inputFileName + "]. This should never happen.");
		}
		
		List<String> overridePrincipalIds = getPrincipalIdsInOverrideGroup();
		
		List<String> overridePrincipalAndEmplIds = addEmplIdsToPrincipalIds(overridePrincipalIds);
		
		recordsReset = removePayeeAchAccountsByPayeeType(payeeType, overridePrincipalAndEmplIds);
		
		return recordsReset;
	}
	
	// this method will remove those accounts that match the PayeeIdentifierType on the File provided but not the list of payeeIdNumber's found on the BANKING_INFORMATION_OVERRIDE_GROUP group
	private Integer removePayeeAchAccountsByPayeeType(String payeeType, List<String> overridePrincipalAndEmplIds) {
		List<PayeeACHAccount> payeesToDelete = new ArrayList<PayeeACHAccount>();
		
		Map<String, String> criteria = new HashMap<String, String>();
		criteria.put(PdpPropertyConstants.PAYEE_IDENTIFIER_TYPE_CODE, payeeType);
		Collection<PayeeACHAccount> fetchedPayees = businessObjectService.findMatching(PayeeACHAccount.class, criteria);
		
		if (overridePrincipalAndEmplIds == null || overridePrincipalAndEmplIds.isEmpty()) {
			payeesToDelete.addAll(fetchedPayees);
		} else {
			for (PayeeACHAccount payee : fetchedPayees) {
				if (!overridePrincipalAndEmplIds.contains(payee.getPayeeIdNumber())) {
					payeesToDelete.add(payee);
				}
			}
		}
		
		deletePayees(payeesToDelete);

		return payeesToDelete.size();
	}
	
	private void deletePayees(List<PayeeACHAccount> payees) {
		if (payees == null || payees.isEmpty()) {
			return;
		}
		
		for (PayeeACHAccount payee : payees) {
			LOG.debug("deleting payee = " + payee);
		}
		
		businessObjectService.delete(payees);
	}

	protected String validateTypeUsedInFile(String inputFileName) {
		BufferedReader bufferedFileReader = null;
		String firstType = KFSConstants.EMPTY_STRING;

		try {
			PayeeACHAccount currentPayeeAcct;
			boolean firstLine = true;
			String fileLine;
			bufferedFileReader = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName), "UTF-8"));

			while ((fileLine = bufferedFileReader.readLine()) != null) {
				
				if (fileLine.length() < PdpConstants.ACHFileConstants.PAYEE_ACH_ACCT_RECORD_LENGTH) {
					throw new RuntimeException("invalid input file: [" + inputFileName + "] - record length=" + fileLine.length() +  " should bew " + PdpConstants.ACHFileConstants.PAYEE_ACH_ACCT_RECORD_LENGTH);
				}
				
				currentPayeeAcct = PayeeACHAcctFlatFileConverter.convert(fileLine);
				
				if (firstLine) {
					firstType = currentPayeeAcct.getPayeeIdentifierTypeCode();
					firstLine = false;
				}
				else {
					if (!firstType.equalsIgnoreCase(currentPayeeAcct.getPayeeIdentifierTypeCode())) {
						throw new RuntimeException("The Bank ACH Account file contained payees of multiple types. This should never happen.");
					}
				}
			}
		}
		catch (UnsupportedEncodingException ex) {
			throw new RuntimeException("UnsupportedEncodingException on input file: [" + inputFileName + "]", ex);
		}
		catch (FileNotFoundException ex) {
			throw new RuntimeException("FileNotFoundException on input file: [" + inputFileName + "]", ex);
		}
		catch (IOException ex) {
			throw new RuntimeException("IOException on input file: [" + inputFileName + "]", ex);
		}
		finally {
			IOUtils.closeQuietly(bufferedFileReader);
		}
		return firstType;
	}
	
	protected Collection<PayeeACHAccount> loadFile(String inputFileName) {
		
		FileInputStream fileContents = null;
		Collection<PayeeACHAccount> payeeAccts = null;
		
		try {
			fileContents = new FileInputStream(inputFileName);
			byte[] fileByteContent = IOUtils.toByteArray(fileContents);
			payeeAccts = (Collection<PayeeACHAccount>) batchInputFileService.parse(achPayeeBankAcctInputFileType, fileByteContent);
		}
		catch (FileNotFoundException e) {
			LOG.error("file to parse not found " + inputFileName, e);
			throw new RuntimeException("Cannot find the file requested to be parsed " + inputFileName + " " + e.getMessage(), e);
		}
		catch (IOException e) {
			LOG.error("error while getting file bytes: " + e.getMessage(), e);
			throw new RuntimeException("Error encountered while attempting to get file bytes: " + e.getMessage(), e);
		}
		catch (ParseException e) {
			LOG.error("Error encountered parsing file " + e.getMessage(), e);
			throw new RuntimeException("Error encountered parsing file " + e.getMessage(), e);
		}
		finally {
			IOUtils.closeQuietly(fileContents);
		}
		
		return payeeAccts;
	}
	
	public GroupService getGroupService() {
		if (groupService == null) {
			this.groupService = KimApiServiceLocator.getGroupService();
		}
		
		return this.groupService;
	}

	public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
		this.batchInputFileService = batchInputFileService;
	}

	public void setAchPayeeBankAcctInputFileType(BatchInputFileType achPayeeBankAcctInputFileType) {
		this.achPayeeBankAcctInputFileType = achPayeeBankAcctInputFileType;
	}

	public void setPersonService(PersonService personService) {
		this.personService = personService;
	}

	public void setBusinessObjectService(BusinessObjectService businessObjectService) {
		this.businessObjectService = businessObjectService;
	}

	public void setParameterService(ParameterService parameterService) {
		this.parameterService = parameterService;
	}

}
