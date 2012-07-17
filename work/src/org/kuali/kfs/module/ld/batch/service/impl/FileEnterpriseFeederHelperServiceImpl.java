/*
 * Copyright 2007 The Kuali Foundation
 *
 * Licensed under the Educational Community License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.opensource.org/licenses/ecl2.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.ld.batch.service.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.gl.batch.service.ReconciliationParserService;
import org.kuali.kfs.gl.batch.service.impl.ExceptionCaughtStatus;
import org.kuali.kfs.gl.batch.service.impl.FileReconBadLoadAbortedStatus;
import org.kuali.kfs.gl.batch.service.impl.FileReconOkLoadOkStatus;
import org.kuali.kfs.gl.batch.service.impl.ReconciliationBlock;
import org.kuali.kfs.gl.report.LedgerSummaryReport;
import org.kuali.kfs.gl.service.OriginEntryService;
import org.kuali.kfs.gl.service.impl.EnterpriseFeederStatusAndErrorMessagesWrapper;
import org.kuali.kfs.module.ld.LaborConstants;
import org.kuali.kfs.module.ld.LaborKeyConstants;
import org.kuali.kfs.module.ld.LaborPropertyConstants;
import org.kuali.kfs.module.ld.batch.LaborEnterpriseFeedStep;
import org.kuali.kfs.module.ld.batch.service.FileEnterpriseFeederHelperService;
import org.kuali.kfs.module.ld.batch.service.ReconciliationService;
import org.kuali.kfs.module.ld.businessobject.BenefitsCalculation;
import org.kuali.kfs.module.ld.businessobject.LaborOriginEntry;
import org.kuali.kfs.module.ld.businessobject.PositionObjectBenefit;
import org.kuali.kfs.module.ld.report.EnterpriseFeederReportData;
import org.kuali.kfs.module.ld.service.LaborBenefitsCalculationService;
import org.kuali.kfs.module.ld.service.LaborPositionObjectBenefitService;
import org.kuali.kfs.module.ld.util.LaborOriginEntryFileIterator;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.KFSParameterKeyConstants;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.Message;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.ReportWriterService;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;

/**
 * This class reads origin entries in a flat file format, reconciles them, and loads them into the origin entry table.
 * Note: the feeding algorithm of this service will read the data file twice to minimize memory usage.
 */
public class FileEnterpriseFeederHelperServiceImpl implements FileEnterpriseFeederHelperService {
    private static final org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(FileEnterpriseFeederHelperServiceImpl.class);

    protected ReconciliationParserService reconciliationParserService;
    protected ReconciliationService reconciliationService;
    protected OriginEntryService originEntryService;
    protected ParameterService parameterService;
    protected LaborPositionObjectBenefitService laborPositionObjectBenefitService;
    protected LaborBenefitsCalculationService laborBenefitsCalculationService;
    protected BusinessObjectService businessObjectService;
    protected ConfigurationService configurationService;
    protected DateTimeService dateTimeService;

	/**
	 * This method does the reading and the loading of reconciliation. Read
	 * class description. This method DOES NOT handle the deletion of the done
	 * file
	 *
	 * @param doneFile
	 *            a URL that must be present. The contents may be empty
	 * @param dataFile
	 *            a URL to a flat file of origin entry rows.
	 * @param reconFile
	 *            a URL to the reconciliation file. See the implementation of
	 *            {@link ReconciliationParserService} for file format.
	 * @param originEntryGroup
	 *            the group into which the origin entries will be loaded
	 * @param feederProcessName
	 *            the name of the feeder process
	 * @param reconciliationTableId
	 *            the name of the block to use for reconciliation within the
	 *            reconciliation file
	 * @param statusAndErrors
	 *            any status information should be stored within this object
	 * @see org.kuali.module.gl.service.impl.FileEnterpriseFeederHelperService#feedOnFile(java.io.File,
	 *      java.io.File, java.io.File,
	 *      org.kuali.kfs.gl.businessobject.OriginEntryGroup)
	 */
	@Override
    public void feedOnFile(File doneFile, File dataFile, File reconFile, PrintStream enterpriseFeedPs,
			String feederProcessName, String reconciliationTableId,
			EnterpriseFeederStatusAndErrorMessagesWrapper statusAndErrors, LedgerSummaryReport ledgerSummaryReport,
			ReportWriterService errorStatisticsReport, EnterpriseFeederReportData feederReportData) {
	    if ( LOG.isInfoEnabled() ) {
	        LOG.info("Processing done file: " + doneFile.getAbsolutePath());
	    }

        List<Message> errorMessages = statusAndErrors.getErrorMessages();
        BufferedReader dataFileReader = null;

        ReconciliationBlock reconciliationBlock = null;
        Reader reconReader = null;
        try {
            reconReader = new FileReader(reconFile);
            reconciliationBlock = reconciliationParserService.parseReconciliationBlock(reconReader, reconciliationTableId);
        }
        catch (IOException e) {
            LOG.error("IO Error occured trying to read the recon file.", e);
            errorMessages.add(new Message("IO Error occured trying to read the recon file.", Message.TYPE_FATAL));
            reconciliationBlock = null;
            statusAndErrors.setStatus(new FileReconBadLoadAbortedStatus());
            throw new RuntimeException(e);
        }
        catch (RuntimeException e) {
            LOG.error("Error occured trying to parse the recon file.", e);
            errorMessages.add(new Message("Error occured trying to parse the recon file.", Message.TYPE_FATAL));
            reconciliationBlock = null;
            statusAndErrors.setStatus(new FileReconBadLoadAbortedStatus());
            throw e;
        }
        finally {
            if (reconReader != null) {
                try {
                    reconReader.close();
                }
                catch (IOException e) {
                    LOG.error("Error occured trying to close recon file: " + reconFile.getAbsolutePath(), e);
                }
            }
        }

        try {
            if (reconciliationBlock == null) {
                errorMessages.add(new Message("Unable to parse reconciliation file.", Message.TYPE_FATAL));
            }
            else {
                dataFileReader = new BufferedReader(new FileReader(dataFile));
                Iterator<LaborOriginEntry> fileIterator = new LaborOriginEntryFileIterator(dataFileReader, false);
                reconciliationService.reconcile(fileIterator, reconciliationBlock, errorMessages);

                fileIterator = null;
                dataFileReader.close();
                dataFileReader = null;
            }

            if (reconciliationProcessSucceeded(errorMessages)) {
                dataFileReader = new BufferedReader(new FileReader(dataFile));
                String line;
                int count = 0;

                // create an entry to temporarily parse each line as it comes in
                Map<String, List<LaborOriginEntry>> salaryBenefitOffsets = new HashMap<String, List<LaborOriginEntry>>();
                List<LaborOriginEntry> entries = new ArrayList<LaborOriginEntry>();
                boolean calculateOffsets = parameterService.getParameterValueAsBoolean(LaborEnterpriseFeedStep.class, LaborConstants.BenefitCalculation.LABOR_BENEFIT_CALCULATION_OFFSET_IND);
                String offsetDocTypes = null;
                Collection<String> offsetDocTypeList = parameterService.getParameterValuesAsString(LaborEnterpriseFeedStep.class, LaborConstants.BenefitCalculation.LABOR_BENEFIT_OFFSET_DOCTYPE);
                if( !offsetDocTypeList.isEmpty() ) {
                    offsetDocTypes = "," + StringUtils.join(offsetDocTypeList, ',');
                }

                while ((line = dataFileReader.readLine()) != null) {
                    try {
                        LaborOriginEntry tempEntry = new LaborOriginEntry();
                        tempEntry.setFromTextFileForBatch(line, count);
                        this.applyDefaultIfNecessary(tempEntry);
                        
                        feederReportData.incrementNumberOfRecordsRead();
                        feederReportData.addToTotalAmountRead(tempEntry.getTransactionLedgerEntryAmount());

                        enterpriseFeedPs.printf("%s\n", line);

                        ledgerSummaryReport.summarizeEntry(tempEntry);
                        feederReportData.incrementNumberOfRecordsWritten();
                        feederReportData.addToTotalAmountWritten(tempEntry.getTransactionLedgerEntryAmount());

                        List<LaborOriginEntry> benefitEntries = generateBenefits(tempEntry, errorStatisticsReport, feederReportData);
                        for(LaborOriginEntry benefitEntry : benefitEntries) {
                            enterpriseFeedPs.printf("%s\n", benefitEntry.getLine());

                            feederReportData.incrementNumberOfRecordsWritten();
                            feederReportData.addToTotalAmountWritten(benefitEntry.getTransactionLedgerEntryAmount());

                            //If the LABOR_BENEFIT_CALCULATION_OFFSET_IND system parameter is set to 'Y'
                            //and the LABOR_BENEFIT_OFFSET_DOCTYPE system parameter is not empty
                            //and the document type is in the LABOR_BENEFIT_OFFSET_DOCTYPE system parameter then
                            //group together the benefit entries for the salary benefit offset calculation
                            if(calculateOffsets && offsetDocTypes != null && offsetDocTypes.toUpperCase().contains("," + tempEntry.getFinancialDocumentTypeCode().toUpperCase() + ",")) {
                                String key = tempEntry.getUniversityFiscalYear() + "_" + tempEntry.getChartOfAccountsCode() + "_" + tempEntry.getAccountNumber() + "_" + tempEntry.getFinancialObjectCode();
                                if(!salaryBenefitOffsets.containsKey(key)) {
                                    entries = new ArrayList<LaborOriginEntry>();
                                    salaryBenefitOffsets.put(key, entries);
                                } else {
                                    entries = salaryBenefitOffsets.get(key);
                                }
                                benefitEntry.setFinancialObjectCode(tempEntry.getFinancialObjectCode());
                                benefitEntry.setAccountNumber(tempEntry.getAccountNumber());
                                benefitEntry.setChartOfAccountsCode(tempEntry.getChartOfAccountsCode());
                                benefitEntry.setUniversityFiscalYear(tempEntry.getUniversityFiscalYear());
                                benefitEntry.setUniversityFiscalPeriodCode(tempEntry.getUniversityFiscalPeriodCode());
                                entries.add(benefitEntry);
                            }
                        }

                    } catch (Exception e) {
                        throw new IOException(e);
                    }

                    count++;
                }

                //If the LABOR_BENEFIT_CALCULATION_OFFSET_IND system parameter is set to 'Y'
                //and the LABOR_BENEFIT_OFFSET_DOCTYPE system parameter is not empty
                //then create the salary benefit offset entries
                if(calculateOffsets && offsetDocTypes != null) {
                    for(List<LaborOriginEntry> entryList : salaryBenefitOffsets.values()) {
                        if(entryList != null && entryList.size() > 0) {
                            LaborOriginEntry offsetEntry = new LaborOriginEntry();
                            KualiDecimal total = KualiDecimal.ZERO;

                            //Loop through all the benefit entries to calculate the total for the salary benefit offset entry
                            for(LaborOriginEntry entry : entryList) {
                                if(entry.getTransactionDebitCreditCode().equalsIgnoreCase(KFSConstants.GL_DEBIT_CODE)) {
                                    total = total.add(entry.getTransactionLedgerEntryAmount());
                                } else {
                                    total = total.subtract(entry.getTransactionLedgerEntryAmount());
                                }
                            }

                            //No need to process for the salary benefit offset if the total is 0
                            if(total.isNonZero()) {

                                //Lookup the position object benefit to get the object benefit type code
                                Collection<PositionObjectBenefit> positionObjectBenefits = laborPositionObjectBenefitService.getPositionObjectBenefits(entryList.get(0).getUniversityFiscalYear(), entryList.get(0).getChartOfAccountsCode(), entryList.get(0).getFinancialObjectCode());
                                LaborOriginEntry entry = entryList.get(0);
                                if (positionObjectBenefits == null || positionObjectBenefits.isEmpty()) {
                                    writeMissingBenefitsTypeError(entry, errorStatisticsReport, feederReportData);
                                } else {
                                    for (PositionObjectBenefit positionObjectBenefit : positionObjectBenefits) {
                                        Map<String, Object> fieldValues = new HashMap<String, Object>(3);
                                        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, entry.getUniversityFiscalYear());
                                        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, entry.getChartOfAccountsCode());
                                        fieldValues.put(LaborPropertyConstants.POSITION_BENEFIT_TYPE_CODE, positionObjectBenefit.getFinancialObjectBenefitsTypeCode());

                                        //Lookup the benefit calculation to get the offset account number and object code
                                        BenefitsCalculation benefitsCalculation = businessObjectService.findByPrimaryKey(BenefitsCalculation.class, fieldValues);

                                        offsetEntry.setAccountNumber(benefitsCalculation.getAccountCodeOffset());
                                        offsetEntry.setFinancialObjectCode(benefitsCalculation.getObjectCodeOffset());
                                    }
                                }

                                //Set all the fields required to process through the scrubber and poster jobs
                                offsetEntry.setUniversityFiscalPeriodCode(entry.getUniversityFiscalPeriodCode());
                                offsetEntry.setChartOfAccountsCode(entry.getChartOfAccountsCode());
                                offsetEntry.setUniversityFiscalYear(entry.getUniversityFiscalYear());
                                offsetEntry.setTransactionLedgerEntryDescription("GENERATED BENEFIT OFFSET");
                                offsetEntry.setFinancialSystemOriginationCode("RN");
                                offsetEntry.setDocumentNumber(dateTimeService.toString(dateTimeService.getCurrentDate(), "yyyyMMddhhmmssSSS"));

                                //Only + signed amounts
                                offsetEntry.setTransactionLedgerEntryAmount(total.abs());

                                //Credit if the total is positive and Debit of the total is negative
                                if(total.isGreaterThan(new KualiDecimal(0))) {
                                    offsetEntry.setTransactionDebitCreditCode("C");
                                } else if(total.isLessThan(new KualiDecimal(0))) {
                                    offsetEntry.setTransactionDebitCreditCode("D");
                                }

                                //Set the doc type to the value in the LABOR_BENEFIT_OFFSET_DOCTYPE system parameter (the first value if there is a list)
                                String docTypeCode = offsetDocTypes;
                                if (offsetDocTypes.contains(",")) {
                                    String[] splits = offsetDocTypes.split(",");
                                    for(String split : splits) {
                                        if(!StringUtils.isEmpty(split)) {
                                            docTypeCode = split;
                                            break;
                                        }
                                    }
                                }
                                offsetEntry.setFinancialDocumentTypeCode(docTypeCode);

                                //Write the offset entry to the file
                                enterpriseFeedPs.printf("%s\n", offsetEntry.getLine());
                            }
                        }
                    }
                }

                dataFileReader.close();
                dataFileReader = null;

                statusAndErrors.setStatus(new FileReconOkLoadOkStatus());
            }
            else {
                statusAndErrors.setStatus(new FileReconBadLoadAbortedStatus());
            }
        }
        catch (Exception e) {
            LOG.error("Caught exception when reconciling/loading done file: " + doneFile, e);
            statusAndErrors.setStatus(new ExceptionCaughtStatus());
            errorMessages.add(new Message("Caught exception attempting to reconcile/load done file: " + doneFile + ".  File contents are NOT loaded", Message.TYPE_FATAL));
            // re-throw the exception rather than returning a value so that Spring will auto-rollback
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
            else {
                // Spring only rolls back when throwing a runtime exception (by default), so we throw a new exception
                throw new RuntimeException(e);
            }
        }
        finally {
            if (dataFileReader != null) {
                try {
                    dataFileReader.close();
                }
                catch (IOException e) {
                    LOG.error("IO Exception occured trying to close connection to the data file", e);
                    errorMessages.add(new Message("IO Exception occured trying to close connection to the data file", Message.TYPE_FATAL));
                }
            }
        }
    }

    /**
	 * <p>
	 * Generates the benefit
	 * <code>LaborOriginEntry<code> lines for the given wage entry
	 * </p>
	 * <p>
	 * Steps for generating benefit lines are:
	 * </p>
	 * <ol>
	 * <li>Determine balance type of entry line and whether benefits should be
	 * generated (based on system parameter)</li>
	 * <li>Find the benefit rate category code for the salary account</li>
	 * <li>Find the benefit type for the year, chart, and object code</li>
	 * <li>Find the benefit calculations based on benefit type and category code
	 * (if not blank)</li>
	 * <li>For each benefit calculation, generate a benefit line with the
	 * benefit object code and benefit amount</li>
	 * </ol>
	 *
	 * @param wageEntry
	 *            - the
	 *            <code>LaborOriginEntry<code> for wages that benefits should be generated for
	 * @param errorStatisticsReport
	 *            - the error report writer for which entries that we cannot
	 *            generate benefits for will be summarized in
	 * @param feederReportData
	 *            - holds statistic counts
	 * @return the generated benefit entries as a List<LaborOriginEntry>
	 */
	protected List<LaborOriginEntry> generateBenefits(LaborOriginEntry wageEntry,
			ReportWriterService errorStatisticsReport, EnterpriseFeederReportData feederReportData) {
		List<LaborOriginEntry> benefits = new ArrayList<LaborOriginEntry>();

		String balanceTypeCode = wageEntry.getFinancialBalanceTypeCode();
		boolean isActual = KFSConstants.BALANCE_TYPE_ACTUAL.equals(balanceTypeCode)
				|| KFSConstants.BALANCE_TYPE_A21.equals(balanceTypeCode);
		boolean isEncumbrance = KFSConstants.ENCUMB_UPDT_DOCUMENT_CD.equals(wageEntry
				.getTransactionEncumbranceUpdateCode())
				|| KFSConstants.ENCUMB_UPDT_REFERENCE_DOCUMENT_CD.equals(wageEntry
						.getTransactionEncumbranceUpdateCode());

		if (isActual) {
			feederReportData.incrementNumberOfBalanceTypeActualsRead();

			// check parameter that indicates whether benefits should be generated for actual  balance types
			boolean generateActualBenefits = parameterService.getParameterValueAsBoolean(LaborEnterpriseFeedStep.class,
					LaborConstants.BenefitCalculation.GENERATE_FRINGE_BENEFIT_PARAMETER);
			if (!generateActualBenefits) {
				LOG.info("Skipping benefit generation due to parameter disabling benefit generation for actual balance type");
				return benefits;
			}
		}

		if (isEncumbrance) {
			feederReportData.incrementNumberOfBalanceTypeEncumbranceRead();

			// check parameter that indicates whether benefits should be generated for encumbrance  balance types
			boolean generateEncumbranceBenefits = parameterService.getParameterValueAsBoolean(LaborEnterpriseFeedStep.class,
					LaborConstants.BenefitCalculation.GENERATE_FRINGE_BENEFIT_ENCUMBRANCE_PARAMETER);
			if (!generateEncumbranceBenefits) {
				LOG.info("Skipping benefit generation due to parameter disabling benefit generation for encumbrance balance type");
				return benefits;
			}
		}

		// get the benefit rate category code for the entries account number
		String benefitRateCategoryCode = laborBenefitsCalculationService.getBenefitRateCategoryCode(
				wageEntry.getChartOfAccountsCode(), wageEntry.getAccountNumber(), wageEntry.getSubAccountNumber());

        String defaultLaborBenefitsRateCategoryCode =
                StringUtils.trimToEmpty( parameterService.getParameterValueAsString(Account.class, KFSParameterKeyConstants.LdParameterConstants.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_IND) );

        //if sysParam == Y then use the Labor Benefit Rate Category Code to help determine the fringe benefit rate
        boolean useBenefitRateCategoryCode = parameterService.getParameterValueAsBoolean(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, LaborConstants.BenefitCalculation.ENABLE_FRINGE_BENEFIT_CALC_BY_BENEFIT_RATE_CATEGORY_PARAMETER);

		// get benefit types for the entry object code and for each calculation generation an origin entry
		Collection<PositionObjectBenefit> positionObjectBenefits = laborPositionObjectBenefitService
				.getPositionObjectBenefits(wageEntry.getUniversityFiscalYear(), wageEntry.getChartOfAccountsCode(),
						wageEntry.getFinancialObjectCode());
		if (positionObjectBenefits == null || positionObjectBenefits.isEmpty()) {
			writeMissingBenefitsTypeError(wageEntry, errorStatisticsReport, feederReportData);
		}

		for (PositionObjectBenefit positionObjectBenefit : positionObjectBenefits) {
			BenefitsCalculation benefitsCalculation = null;
			if (useBenefitRateCategoryCode) {
				benefitsCalculation = laborBenefitsCalculationService.getBenefitsCalculation(
						wageEntry.getUniversityFiscalYear(), wageEntry.getChartOfAccountsCode(),
						positionObjectBenefit.getFinancialObjectBenefitsTypeCode(), benefitRateCategoryCode);
			}else{
			    benefitsCalculation = laborBenefitsCalculationService.getBenefitsCalculation(
                        wageEntry.getUniversityFiscalYear(), wageEntry.getChartOfAccountsCode(),
                        positionObjectBenefit.getFinancialObjectBenefitsTypeCode(), defaultLaborBenefitsRateCategoryCode);
			}

			if(ObjectUtils.isNull(benefitsCalculation)){
			    continue;
			}

			LaborOriginEntry benefitEntry = new LaborOriginEntry(wageEntry);
			benefitEntry.setFinancialObjectCode(benefitsCalculation.getPositionFringeBenefitObjectCode());

			// calculate the benefit amount (ledger amt * (benfit pct/100) )
			KualiDecimal fringeBenefitPercent = benefitsCalculation.getPositionFringeBenefitPercent();
			KualiDecimal fringeBenefitAmount = fringeBenefitPercent.multiply(
					wageEntry.getTransactionLedgerEntryAmount()).divide(KFSConstants.ONE_HUNDRED.kualiDecimalValue());
			benefitEntry.setTransactionLedgerEntryAmount(fringeBenefitAmount);

			benefits.add(benefitEntry);

			// increment count for successfully generated benefit line
			if (isActual) {
				feederReportData.incrementNumberOfFringeActualsGenerated();
			}
			else {
				feederReportData.incrementNumberOfFringeEncumbrancesGenerated();
			}
		}

		return benefits;
	}

	protected void writeMissingBenefitsTypeError(LaborOriginEntry wageEntry, ReportWriterService errorStatisticsReport,
			EnterpriseFeederReportData feederReportData) {
		String benefitKey = wageEntry.getUniversityFiscalYear() + "-" + wageEntry.getChartOfAccountsCode() + "-"
				+ wageEntry.getFinancialObjectCode();

		String message = configurationService
				.getPropertyValueAsString(LaborKeyConstants.EnterpriseFeed.ERROR_BENEFIT_TYPE_NOT_FOUND);
		message = MessageFormat.format(message, benefitKey);

		feederReportData.incrementNumberOfErrorEncountered();

		LOG.error(message);
		errorStatisticsReport.writeError(wageEntry, new Message(message, Message.TYPE_FATAL));
	}

	protected void writeMissingBenefitsCalculationError(LaborOriginEntry wageEntry,
			ReportWriterService errorStatisticsReport, EnterpriseFeederReportData feederReportData,
			String benefitsTypeCode, String benefitRateCategoryCode, boolean useBenefitRateCategoryCode) {
		String benefitKey = wageEntry.getUniversityFiscalYear() + "-" + wageEntry.getFinancialObjectCode() + "-"
				+ benefitsTypeCode;
		if (useBenefitRateCategoryCode) {
			benefitKey += "-" + benefitRateCategoryCode;
		}

		String message = configurationService
				.getPropertyValueAsString(LaborKeyConstants.EnterpriseFeed.ERROR_BENEFIT_CALCULATION_NOT_FOUND);
		message = MessageFormat.format(message, benefitKey);

		feederReportData.incrementNumberOfErrorEncountered();

		LOG.error(message);
		errorStatisticsReport.writeError(wageEntry, new Message(message, Message.TYPE_FATAL));
	}

    /**
     * Returns whether the reconciliation process succeeded by looking at the reconciliation error messages For this implementation,
     * the reconciliation does not succeed if at least one of the error messages in the list has a type of
     * {@link Message#TYPE_FATAL}
     *
     * @param errorMessages a List of errorMessages
     * @return true if any of those error messages were fatal
     */
    protected boolean reconciliationProcessSucceeded(List<Message> errorMessages) {
        for (Message message : errorMessages) {
            if (message.getType() == Message.TYPE_FATAL) {
                return false;
            }
        }
        return true;
    }
    
    /**
     * apply default values to the fields if their values are not provided
     */
    protected void applyDefaultIfNecessary(LaborOriginEntry laborOriginEntry) {
        // apply the current fiscal year if it is not provided
        if(ObjectUtils.isNull(laborOriginEntry.getUniversityFiscalYear())){
            UniversityDateService universityDateService = SpringContext.getBean(UniversityDateService.class);
            laborOriginEntry.setUniversityFiscalYear(universityDateService.getCurrentFiscalYear());
        }        
    }

    public void setReconciliationParserService(ReconciliationParserService reconciliationParserService) {
        this.reconciliationParserService = reconciliationParserService;
    }
    public void setReconciliationService(ReconciliationService reconciliationService) {
        this.reconciliationService = reconciliationService;
    }
    public void setOriginEntryService(OriginEntryService originEntryService) {
        this.originEntryService = originEntryService;
    }
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    public void setLaborPositionObjectBenefitService(LaborPositionObjectBenefitService laborPositionObjectBenefitService) {
        this.laborPositionObjectBenefitService = laborPositionObjectBenefitService;
    }
    public void setLaborBenefitsCalculationService(LaborBenefitsCalculationService laborBenefitsCalculationService) {
        this.laborBenefitsCalculationService = laborBenefitsCalculationService;
    }
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

	public void setConfigurationService(ConfigurationService configurationService) {
		this.configurationService = configurationService;
	}
    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }
}