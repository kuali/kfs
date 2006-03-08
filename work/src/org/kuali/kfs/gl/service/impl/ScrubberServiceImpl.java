/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.gl.service.impl;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.kuali.Constants;
import org.kuali.KeyConstants;
import org.kuali.core.dao.OptionsDao;
import org.kuali.core.service.DateTimeService;
import org.kuali.core.service.KualiConfigurationService;
import org.kuali.core.service.PersistenceService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.module.chart.bo.ObjectCode;
import org.kuali.module.chart.bo.OffsetDefinition;
import org.kuali.module.chart.service.AccountService;
import org.kuali.module.chart.service.ObjectCodeService;
import org.kuali.module.chart.service.OffsetDefinitionService;
import org.kuali.module.gl.batch.scrubber.ScrubberReport;
import org.kuali.module.gl.bo.OriginEntry;
import org.kuali.module.gl.bo.OriginEntryGroup;
import org.kuali.module.gl.bo.OriginEntrySource;
import org.kuali.module.gl.bo.UniversityDate;
import org.kuali.module.gl.dao.UniversityDateDao;
import org.kuali.module.gl.service.OriginEntryGroupService;
import org.kuali.module.gl.service.OriginEntryService;
import org.kuali.module.gl.service.ScrubberService;
import org.kuali.module.gl.service.impl.helper.BatchInfo;
import org.kuali.module.gl.service.impl.helper.DocumentInfo;
import org.kuali.module.gl.service.impl.helper.OriginEntryGroupInfo;
import org.kuali.module.gl.service.impl.helper.OriginEntryInfo;
import org.kuali.module.gl.service.impl.helper.ScrubberServiceErrorHandler;
import org.kuali.module.gl.service.impl.helper.UnitOfWorkInfo;
import org.kuali.module.gl.util.LedgerEntry;
import org.kuali.module.gl.util.ObjectHelper;
import org.kuali.module.gl.util.ScrubberServiceValidationHelper;
import org.kuali.module.gl.util.Summary;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.util.StringUtils;

/**
 * @author Anthony Potts
 * @version $Id: ScrubberServiceImpl.java,v 1.65 2006-03-08 21:39:21 larevans Exp $
 */

public class ScrubberServiceImpl implements ScrubberService,BeanFactoryAware {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceImpl.class);
    
    // Initialize mappings for capitalization. They only need to be initialized once.
    static private Map objectSubTypeCodesToObjectCodesForCapitalization = new TreeMap();
    static {
        objectSubTypeCodesToObjectCodesForCapitalization.put("AM", "8615");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BD", "8601");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BF", "8605");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BI", "8629");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BR", "8601");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BX", "8640");
        objectSubTypeCodesToObjectCodesForCapitalization.put("BY", "8641");
        objectSubTypeCodesToObjectCodesForCapitalization.put("CM", "8610");
        objectSubTypeCodesToObjectCodesForCapitalization.put("CF", "8611");
        objectSubTypeCodesToObjectCodesForCapitalization.put("C1", "8627");
        objectSubTypeCodesToObjectCodesForCapitalization.put("C2", "8628");
        objectSubTypeCodesToObjectCodesForCapitalization.put("C3", "9607");
        objectSubTypeCodesToObjectCodesForCapitalization.put("ES", "8630");
        objectSubTypeCodesToObjectCodesForCapitalization.put("IF", "8604");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LA", "8603");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LE", "8608");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LF", "8614");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LI", "8613");
        objectSubTypeCodesToObjectCodesForCapitalization.put("LR", "8665");
        objectSubTypeCodesToObjectCodesForCapitalization.put("UC", "8618");
        objectSubTypeCodesToObjectCodesForCapitalization.put("UF", "8619");
    }
    
    static private String[] campusObjectSubTypeCodesForPlantFundAccountLookups = new String[] {
        "AM", "AF", "BD", "BF", "BI", "BR", "BX",
        "BY", "IF", "LA", "LE", "LF", "LI", "LR"
    };
    static private String[] organizationObjectSubTypeCodesForPlantFundAccountLookups = new String[] {
        "CL", "CM", "CF", "C1", "C2", "C3", "ES", 
        "UC", "UF"
    };
    static private String[] badDocumentTypeCodesForCapitalization = new String[] {
        "TF", "YETF", "AV", "AVAC", "AVAE", "AVRC"
    };
    static private String[] goodObjectSubTypeCodesForCapitalization = new String[] {
        "AM", "AF", "BD", "BF", "BI", "BR", "BX", 
        "BY", "CM", "CF", "C1", "C2", "C3", "ES", 
        "IF", "LA", "LE", "LF", "LI", "LR", "UC",
        "UF"
    };
    static private String[] invalidDocumentTypesForLiabilities = new String[] {
            "TF", "YETF", "AV", "AVAC", "AVAE", "AVRC"
    };
    static private String[] invalidFiscalPeriodCodesForOffsetGeneration = new String[] {
        "BB", "CB"
    };
    
    private BeanFactory beanFactory;
    private OriginEntryService originEntryService;
    private OriginEntryGroupService originEntryGroupService;
    private DateTimeService dateTimeService;
    private OffsetDefinitionService offsetDefinitionService;
    private ObjectCodeService objectCodeService;
    private OptionsDao optionsDao;
    private AccountService accountService;
    private KualiConfigurationService kualiConfigurationService;
    private UniversityDateDao universityDateDao;
    private PersistenceService persistenceService;
    private ScrubberReport scrubberReportService;
    private Date runDate;
    private Calendar runCal;
    UniversityDate universityRunDate;
    Collection groupsToScrub;
    OriginEntryGroup validGroup;
    OriginEntryGroup errorGroup;
    OriginEntryGroup expiredGroup;
    Map batchError;

//    private Calendar wsPreviousCal;
    //private String wsAccountChange;
    
    private ScrubberServiceValidationHelper validator;
    
    public ScrubberServiceImpl() {
      super();
    }

    /**
     * This method is called by Spring after it has initialized all dependencies.  It will determine
     * if we are in a test or not.  If we are in a test, replace the date time service & report
     * service with a test version.
     *
     */
    public void init() {
      LOG.debug("init() started");

      // If we are in test mode
      if ( beanFactory.containsBean("testDateTimeService") ) {
        dateTimeService = (DateTimeService)beanFactory.getBean("testDateTimeService");
        scrubberReportService = (ScrubberReport)beanFactory.getBean("testScrubberReport");
      }
    }

    /**
     * Scrub all entries that need it in origin entry.  Put valid scrubbed entries in
     * a scrubber valid group, put errors in a scrubber error group, and transactions
     * with an expired account in the scrubber expired account group.
     */
    public void scrubEntries() {
        LOG.debug("scrubEntries() started");
        
        batchError = new HashMap();
        
        // One validator per run is preferred. The validator is stateless. 
    	validator = new ScrubberServiceValidationHelper(universityDateDao, persistenceService);
    	
        // setup an object to hold the "default" date information
        runDate = new Date(dateTimeService.getCurrentDate().getTime());
        runCal = Calendar.getInstance();
        runCal.setTime(runDate);
        
        universityRunDate = universityDateDao.getByPrimaryKey(runDate);
        if (universityRunDate == null) {
            throw new IllegalStateException(kualiConfigurationService.getPropertyString(KeyConstants.ERROR_UNIV_DATE_NOT_FOUND));
        }

        // Create the groups that will store the valid and error entries that come out of the scrubber
        validGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_VALID, true, true, false);
        errorGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_ERROR, false, true, false);
        expiredGroup = originEntryGroupService.createGroup(runDate, OriginEntrySource.SCRUBBER_EXPIRED, false, true, false);

        groupsToScrub = originEntryGroupService.getGroupsToScrub(runDate);

        LOG.debug("scrubEntries() number of groups to scrub: " + groupsToScrub.size());

        BatchInfo batchInfo = new BatchInfo();
        
        /* MAIN LOOP: -------------------------------------------------
         * Scrub all of the OriginEntryGroups waiting to be scrubbed as
         * of runDate.
         */
        for (Iterator iteratorOverGroups = groupsToScrub.iterator(); iteratorOverGroups.hasNext();) {
            OriginEntryGroup originEntryGroup = (OriginEntryGroup) iteratorOverGroups.next();

            OriginEntryGroupInfo originEntryGroupInfo = 
            	processDocuments(originEntryGroup, originEntryService.getEntriesByGroup(originEntryGroup), batchInfo);
            
            // Mark the origin entry group as being processed ...
            originEntryGroup.setProcess(Boolean.FALSE);
            
            // ... and save the origin entry group with the new process flag.
            originEntryGroupService.save(originEntryGroup);
            
            // Update info about the batch.
            batchInfo.setNumberOfOriginEntryGroups(batchInfo.getNumberOfOriginEntryGroups() + 1);
            batchInfo.setNumberOfDocuments(batchInfo.getNumberOfDocuments() + originEntryGroupInfo.getNumberOfDocuments());
            batchInfo.setNumberOfUnitsOfWork(batchInfo.getNumberOfUnitsOfWork() + originEntryGroupInfo.getNumberOfUnitsOfWork());
            batchInfo.setNumberOfOriginEntries(batchInfo.getNumberOfOriginEntries() + originEntryGroupInfo.getNumberOfEntries());
            batchInfo.setNumberOfErrors(batchInfo.getNumberOfErrors() + originEntryGroupInfo.getErrorCount());
        }

        // write out report and errors
        List reportSummary = buildReportSummary(batchInfo);
        
        // FIXME reports probably won't look right because batchError doesn't currently
        // keep in memory all of the entries in error.
        scrubberReportService.generateStatisticReport(batchError, reportSummary, runDate, 0);
        generateBKUPLedgerReport();
        
        LOG.debug("scrubEntries() exiting scrubber process");
    }

    private void generateBKUPLedgerReport() {
        Map ledgerEntries = new HashMap();
        for (Iterator iterator = originEntryService.getEntriesByGroup(validGroup); iterator.hasNext();) {
            OriginEntry entry = (OriginEntry) iterator.next();
            LedgerEntry ledgerEntry = new LedgerEntry();
            ledgerEntry.balanceType = entry.getFinancialBalanceTypeCode();
            ledgerEntry.originCode = entry.getFinancialSystemOriginationCode();
            ledgerEntry.fiscalYear = entry.getUniversityFiscalYear();
            ledgerEntry.period = entry.getUniversityFiscalPeriodCode();
            String key = ledgerEntry.balanceType + ledgerEntry.originCode + ledgerEntry.fiscalYear + ledgerEntry.period;

            boolean newLedgerEntry = !ledgerEntries.containsKey(key);
            
            if (!newLedgerEntry) {
                ledgerEntry = (LedgerEntry) ledgerEntries.get(key);
            }

            ++ledgerEntry.recordCount;            
            if (entry.isCredit()) {
                ++ledgerEntry.creditCount;
                ledgerEntry.creditAmount.add(entry.getTransactionLedgerEntryAmount());
            } else if (entry.isDebit()) {
                ++ledgerEntry.debitCount;
                ledgerEntry.debitAmount.add(entry.getTransactionLedgerEntryAmount());
            } else {
                ++ledgerEntry.noDCCount;
                ledgerEntry.noDCAmount.add(entry.getTransactionLedgerEntryAmount());
            }

            if (newLedgerEntry) {
                ledgerEntries.put(key, ledgerEntry);
            }
        }
        Map sortedLedgerEntries = new TreeMap(ledgerEntries);
        scrubberReportService.generateLedgerStatReport(sortedLedgerEntries, runDate);
}

    /**
     * 
     * @param batchInfo
     * @return
     */
    private List buildReportSummary(BatchInfo batchInfo) {
        List reportSummary = new ArrayList();
        
        reportSummary.add(new Summary(1, "UNSCRUBBED RECORDS READ", new Integer(batchInfo.getNumberOfOriginEntries())));
        reportSummary.add(new Summary(2, "GROUPS READ", new Integer(batchInfo.getNumberOfOriginEntryGroups())));
        reportSummary.add(new Summary(3, "SCRUBBED RECORDS WRITTEN", new Integer(batchInfo.getNumberOfScrubbedRecordsWritten())));
        reportSummary.add(new Summary(4, "ERROR RECORDS WRITTEN", new Integer(batchInfo.getNumberOfErrorRecordsWritten())));
        reportSummary.add(new Summary(5, "OFFSET ENTRIES GENERATED", new Integer(batchInfo.getNumberOfOffsetEntriesGenerated())));
        reportSummary.add(new Summary(6, "ELIMINATION ENTRIES GENERATED", new Integer(batchInfo.getNumberOfEliminationEntriesGenerated())));
        reportSummary.add(new Summary(7, "CAPITALIZATION ENTRIES GENERATED", new Integer(batchInfo.getNumberOfCapitalizationEntriesGenerated())));
        reportSummary.add(new Summary(8, "LIABLITY ENTRIES GENERATED", new Integer(batchInfo.getNumberOfLiabilityEntriesGenerated())));
        reportSummary.add(new Summary(9, "PLANT INDEBTEDNESS ENTRIES GENERATED", new Integer(batchInfo.getNumberOfPlantIndebtednessEntriesGenerated())));
        reportSummary.add(new Summary(10, "COST SHARE ENTRIES GENERATED", new Integer(batchInfo.getNumberOfCostShareEntriesGenerated())));
        reportSummary.add(new Summary(11, "COST SHARE ENC ENTRIES GENERATED", new Integer(batchInfo.getNumberOfCostShareEncumbrancesGenerated())));
        reportSummary.add(new Summary(12, "TOTAL OUTPUT RECORDS WRITTEN", new Integer(batchInfo.getTotalNumberOfRecordsWritten())));
        reportSummary.add(new Summary(13, "EXPIRED ACCOUNTS FOUND", new Integer(batchInfo.getNumberOfExpiredAccountsFound())));
    	
        return reportSummary;
    }
    
    /**
     * 
     * @param originEntryGroup
     * @param iteratorOverEntries
     * @param batchInfo
     * @return
     */
    private OriginEntryGroupInfo processDocuments(OriginEntryGroup originEntryGroup, Iterator iteratorOverEntries, BatchInfo batchInfo) {
    	OriginEntryGroupInfo originEntryGroupInfo = new OriginEntryGroupInfo(batchInfo);
    	originEntryGroupInfo.setOriginEntryGroup(originEntryGroup);
    	
    	DocumentInfo documentInfo = null;
    	while(true) {
    		
        	preProcessDocument(originEntryGroup);
        	
    		documentInfo = 
    			processDocument(
    				originEntryGroupInfo, iteratorOverEntries, 
    				(null == documentInfo) ? null : documentInfo.getLastUnitOfWork().getFirstEntryOfNextUnitOfWork());
    		
    		postProcessDocument(documentInfo);
    		
    		// If there are no more units of work to process, we're done!
    		if(null == documentInfo.getLastUnitOfWork().getFirstEntryOfNextUnitOfWork()) {
    			break;
    		}
    	}
    	
    	return originEntryGroupInfo;
    }
    
    /**
     * 
     * @param originEntryGroup
     */
    private void preProcessDocument(OriginEntryGroup originEntryGroup) {
    }
    
    /**
     * 
     * @param documentInfo
     */
    private void postProcessDocument(DocumentInfo documentInfo) {
        
    	OriginEntryGroupInfo originEntryGroupInfo = documentInfo.getOriginEntryGroupInfo();
        
    	if(0 < documentInfo.getNumberOfErrors()) {
            
    		performDemerger(documentInfo.getDocumentNumber(), originEntryGroupInfo.getOriginEntryGroup());
            
    	}
    	
		originEntryGroupInfo.setErrorCount(originEntryGroupInfo.getErrorCount() + documentInfo.getNumberOfErrors());
		originEntryGroupInfo.setNumberOfDocuments(originEntryGroupInfo.getNumberOfDocuments() + 1);
		originEntryGroupInfo.setNumberOfEntries(originEntryGroupInfo.getNumberOfEntries() + documentInfo.getNumberOfEntries());
		originEntryGroupInfo.setNumberOfUnitsOfWork(originEntryGroupInfo.getNumberOfUnitsOfWork() + documentInfo.getNumberOfUnitsOfWork());
    }
    
    /**
     * 
     * @param originEntryGroupInfo
     * @param iteratorOverEntries
     * @param firstEntry
     * @return
     */
    private DocumentInfo processDocument(OriginEntryGroupInfo originEntryGroupInfo, Iterator iteratorOverEntries, OriginEntry firstEntry) {
        
    	OriginEntryGroup originEntryGroup = originEntryGroupInfo.getOriginEntryGroup();
    	
    	DocumentInfo documentInfo = new DocumentInfo(originEntryGroupInfo);
    	OriginEntry firstEntryOfNextUnitOfWork = firstEntry;
    	
    	while(true) {
            
        	preProcessUnitOfWork(originEntryGroup);
        	
    		UnitOfWorkInfo unitOfWorkInfo = 
    			processUnitOfWork(originEntryGroup, iteratorOverEntries, firstEntryOfNextUnitOfWork, documentInfo);
    		
    		postProcessUnitOfWork(unitOfWorkInfo);
    		
    		documentInfo.setNumberOfErrors(documentInfo.getNumberOfErrors() + unitOfWorkInfo.getErrorCount());
    		
    		// If there are no more units of work to process, or the 
    		// first entry of the next unit of work has a different
    		// document number we're done with the document.
    		if(null == unitOfWorkInfo.getFirstEntryOfNextUnitOfWork()
    				|| !ObjectHelper.isEqual(
    						unitOfWorkInfo.getFirstEntryOfNextUnitOfWork().getFinancialDocumentNumber(),
    						documentInfo.getDocumentNumber())) {
                
    			documentInfo.setDocumentNumber(unitOfWorkInfo.getDocumentNumber());
    			documentInfo.setLastUnitOfWork(unitOfWorkInfo);
                
    			break;
                
    		} else {
                
    			firstEntryOfNextUnitOfWork = unitOfWorkInfo.getFirstEntryOfNextUnitOfWork();
                
    		}
            
    	}
    	
    	return documentInfo;
    }
    
    /**
     * 
     * @param originEntryGroup
     */
    private void preProcessUnitOfWork(OriginEntryGroup originEntryGroup) {
    }
    
    /**
     * 
     * @param unitOfWorkInfo
     */
    private void postProcessUnitOfWork(UnitOfWorkInfo unitOfWorkInfo) {
        
    	// Generate an offset only if there have been no errors on either the current unit of work
    	// or any of the previous units of work in the current document. Once an error has ocurred
    	// we don't need to generate any more offsets because the demerger would just pull them out
    	// anyway.
    	if(0 == (unitOfWorkInfo.getErrorCount() + unitOfWorkInfo.getDocumentInfo().getNumberOfErrors())
                && !unitOfWorkInfo.getTotalOffsetAmount().isZero()) {
    		
	    	// Generate offset first so that any errors and the total number of entries is reflected properly
	    	// when setting into the documentInfo below.
	    	generateOffset(unitOfWorkInfo);
	    	
    	}
    	
    	// Aggregate the information about the unit of work up to the document level.
    	DocumentInfo documentInfo = unitOfWorkInfo.getDocumentInfo();
    	documentInfo.setNumberOfEntries(documentInfo.getNumberOfEntries() + unitOfWorkInfo.getNumberOfEntries());
    	documentInfo.setNumberOfErrors(documentInfo.getNumberOfErrors() + unitOfWorkInfo.getErrorCount());
    	documentInfo.setNumberOfUnitsOfWork(documentInfo.getNumberOfUnitsOfWork() + 1);
    	documentInfo.setDocumentNumber(unitOfWorkInfo.getDocumentNumber());
    }
    
    /**
     * 
     * @param originEntryGroup
     * @param iteratorOverEntries
     * @param firstEntry
     * @param documentInfo
     */
    private UnitOfWorkInfo processUnitOfWork(OriginEntryGroup originEntryGroup, Iterator iteratorOverEntries, OriginEntry firstEntry, 
    		DocumentInfo documentInfo) {
    	
        // Queue up the first entry or return if there's nothing to process.
    	if(null == firstEntry) {
            
    		if(iteratorOverEntries.hasNext()) {
                
    			firstEntry = (OriginEntry) iteratorOverEntries.next();
                
    		} else {
                
    			return null;
                
    		}
            
    	}
    	
    	BatchInfo batchInfo = documentInfo.getOriginEntryGroupInfo().getBatchInfo();
    	
    	UnitOfWorkInfo unitOfWorkInfo = new UnitOfWorkInfo(documentInfo);
    	unitOfWorkInfo.setOriginEntryGroup(originEntryGroup);
    	
        OriginEntry currentEntry = firstEntry;
        
        // FIXME handle the case where firstEntry is the only entry in the unit of work.
        // What should be done with a unit of work with only one entry?
        // The while() below assumes at least two entries per unit of work.
        
        // Indicates when we've hit the boundary between two units of work.
        boolean isLastEntryOfCurrentUnitOfWork = false;
        
        while(!isLastEntryOfCurrentUnitOfWork) {
            
            // ... look ahead to see if there are more entries in the same unit of work.
            OriginEntry nextEntry = iteratorOverEntries.hasNext() ? (OriginEntry) iteratorOverEntries.next() : null;

            // If we've hit the end of the unit of work, return 
            // nextEntry, which will be the first entry of the 
            // next unit of work.
            if(!isSameUnitOfWork(currentEntry, nextEntry) || null == nextEntry) {
            	
            	// Indicate that we should break out of the loop. We're at the end of the unit of work ...
            	isLastEntryOfCurrentUnitOfWork = true;
            	
            	// ... and setup offset generation.
            	unitOfWorkInfo.setTemplateEntryForOffsetGeneration(currentEntry);
            }
            
            // Scrub the entry in the context of the previously scrubbed entry.
            OriginEntryInfo workingEntryInfo = 
            	validateOriginEntryAndBuildWorkingEntry(currentEntry, unitOfWorkInfo);
            
            // TODO (laran) not sure if this should be the workingEntry or the originEntry that 
            // gets fed to updateAmountsForUnitOfWork.
            
            // updateAmountsForUnitOfWork(currentEntry, unitOfWorkInfo);
            updateAmountsForUnitOfWork(workingEntryInfo.getOriginEntry(), unitOfWorkInfo);

            // TODO (laran) figure out how and why processCostSharing and handleCostSharing are different?
            handleCostSharing(currentEntry, workingEntryInfo);

            unitOfWorkInfo.setDocumentNumber(workingEntryInfo.getOriginEntry().getFinancialDocumentNumber());

            if (workingEntryInfo.getErrors().size() > 0) {                         // Handle entries with errors
            	
        		// write this entry as a scrubber error
        		createOutputEntry(currentEntry, errorGroup);
        		batchInfo.errorRecordWritten();
        		
            } else if (workingEntryInfo.getAccount().isAccountClosedIndicator()) { // Handle entries with closed accounts
            	
//    		    OriginEntry closedEntry = new OriginEntry(workingEntryInfo.getOriginEntry());
//    		    
//    		    Account closedAccount = workingEntryInfo.getAccount();
//    		    closedEntry.setAccountNumber(closedAccount.getAccountNumber());
//    		    closedEntry.setChartOfAccountsCode(closedAccount.getChartOfAccountsCode());
    		    
    		    // write expiredEntry as expired
    		    createOutputEntry(workingEntryInfo.getOriginEntry(), expiredGroup);
    		    batchInfo.expiredAccountFound();
    		    
    		} else {                                                               // Handle valid entries
    			
    			if(!ObjectHelper.isOneOf(workingEntryInfo.getOriginEntry().getFinancialDocumentTypeCode(), 
                        new String[] {"JV", "ACLO"})) {
                    
                    OriginEntry workingEntry = workingEntryInfo.getOriginEntry();
                    
                    String tmpObjectCode = workingEntry.getFinancialObjectCode();
                    String tmpObjectTypeCode = workingEntry.getFinancialObjectTypeCode();
                    String tmpDebitOrCreditCode = workingEntry.getTransactionDebitCreditCode();
                    String tmpDescription = workingEntry.getTransactionLedgerEntryDesc();
                    String tmpAccountNumber = workingEntry.getAccountNumber();
                    String tmpSubAccountNumber = workingEntry.getSubAccountNumber();
                    String tmpCOA = workingEntry.getChartOfAccountsCode();
                    
                    // Handle capitalization processing
                    processCapitalization(workingEntryInfo, tmpAccountNumber, tmpCOA);
                    
                    // Handle liability processing
                    processLiabilities(workingEntryInfo, tmpDebitOrCreditCode, tmpAccountNumber, tmpCOA);
                    
                    // Don't really understand why this block is here.
                    workingEntry.setFinancialObjectCode(tmpObjectCode);
                    workingEntry.setFinancialObjectTypeCode(tmpObjectTypeCode);
                    workingEntry.setTransactionDebitCreditCode(tmpDebitOrCreditCode);
                    workingEntry.setTransactionLedgerEntryDesc(tmpDescription);
                    workingEntry.setAccountNumber(tmpAccountNumber);
                    workingEntry.setSubAccountNumber(tmpSubAccountNumber);
                    
                    // Handle plant indebtedness processing
                    processPlantIndebtedness(workingEntryInfo, tmpObjectCode, tmpObjectTypeCode, tmpDebitOrCreditCode, 
                            tmpDescription, tmpAccountNumber, tmpSubAccountNumber, tmpCOA);
                    
                    // Don't really understand why this block is here.
                    workingEntry.setFinancialObjectCode(tmpObjectCode);
                    workingEntry.setFinancialObjectTypeCode(tmpObjectTypeCode);
                    workingEntry.setTransactionDebitCreditCode(tmpDebitOrCreditCode);
                    workingEntry.setTransactionLedgerEntryDesc(tmpDescription);
                    workingEntry.setAccountNumber(tmpAccountNumber);
                    workingEntry.setSubAccountNumber(tmpSubAccountNumber);
                    workingEntry.setChartOfAccountsCode(tmpCOA);
                    
    			}
    			
    			// handle cost sharing
    			if (workingEntryInfo.getCostSharingAmount().isNonZero()) {
                    
                    // TODO (laran) figure out how and why processCostSharing and handleCostSharing are different?
    			    processCostSharing(workingEntryInfo);
                    
    			}
    			
    			// write this entry as a scrubber valid
    			createOutputEntry(workingEntryInfo.getOriginEntry(), validGroup);
    			
    		}
            
            postProcessEntry(currentEntry, workingEntryInfo);
            
            currentEntry = nextEntry;
        }
        
        
        // And queue up for the next iteration.
        unitOfWorkInfo.setFirstEntryOfNextUnitOfWork(currentEntry);
        return unitOfWorkInfo;
    }

    /**
     * 
     * @param inputEntry
     * @param workingEntryInfo
     */
    private void postProcessEntry(OriginEntry inputEntry, OriginEntryInfo workingEntryInfo) {
        UnitOfWorkInfo unitOfWorkInfo = workingEntryInfo.getUnitOfWorkInfo();
        
		unitOfWorkInfo.setErrorCount(
    			unitOfWorkInfo.getErrorCount() + workingEntryInfo.getErrors().size());
        
        unitOfWorkInfo.setNumberOfEntries(unitOfWorkInfo.getNumberOfEntries() + 1);
        
        if (workingEntryInfo.getErrors().size() > 0) {
            
            batchError.put(inputEntry, workingEntryInfo.getErrors());
            
        }
        
    }
    
    /**
     * 
     * @param originEntry
     * @param previousEntry
     * @return
     */
    private OriginEntryInfo validateOriginEntryAndBuildWorkingEntry(OriginEntry originEntry, UnitOfWorkInfo unitOfWorkInfo) { /* 2500-process-unit-of-work */
        // FIXME (laran) see if this needs to be added back in
        //checkUnitOfWork(workingEntry);
        
        persistenceService.retrieveNonKeyFields(originEntry);
        
    	OriginEntryInfo workingEntryInfo = new OriginEntryInfo(unitOfWorkInfo);

        workingEntryInfo.setAccount(originEntry.getAccount());
    	
    	OriginEntry workingEntry = new OriginEntry();
    	workingEntryInfo.setOriginEntry(workingEntry);
        workingEntry.setFinancialDocumentNumber(originEntry.getFinancialDocumentNumber());
        workingEntry.setOrganizationDocumentNumber(originEntry.getOrganizationDocumentNumber());
        workingEntry.setOrganizationReferenceId(originEntry.getOrganizationReferenceId());
        workingEntry.setFinancialDocumentReferenceNbr(originEntry.getFinancialDocumentReferenceNbr());

        Integer transactionNumber = originEntry.getTrnEntryLedgerSequenceNumber();
        workingEntry.setTrnEntryLedgerSequenceNumber(null == transactionNumber ? new Integer(0) : transactionNumber);
        workingEntry.setTransactionLedgerEntryDesc(originEntry.getTransactionLedgerEntryDesc());
        workingEntry.setTransactionLedgerEntryAmount(originEntry.getTransactionLedgerEntryAmount());
        workingEntry.setTransactionDebitCreditCode(originEntry.getTransactionDebitCreditCode());

        // NOTE the validator WILL set any fields in the workingEntry as appropriate
        // per the validation rules.
        
        // It's important that this check come before the checks for object, sub-object and accountingPeriod
        // because this validation method will set the fiscal year and reload those three objects if the fiscal
        // year was invalid. This will also set originEntry.getOption and workingEntry.getOption. So, it's 
        // probably a good idea to validate the fiscal year first thing.
        validator.validateFiscalYear(originEntry, workingEntryInfo, universityRunDate, optionsDao);
        
        // Must be called after validation of fiscal year as the option object may change in that method.
        workingEntry.setOption(originEntry.getOption());
        
        validator.validateTransactionDate(originEntry, workingEntryInfo, runDate, universityDateDao);
        validator.validateTransactionAmount(originEntry, workingEntryInfo);
        validator.validateAccount(originEntry, workingEntryInfo, accountService, runCal);
        validator.validateSubAccount(originEntry, workingEntryInfo);
        validator.validateSubObjectCode(originEntry, workingEntryInfo);
        validator.validateProjectCode(originEntry, workingEntryInfo);
        validator.validateDocumentType(originEntry, workingEntryInfo);
        validator.validateOrigination(originEntry, workingEntryInfo);
        validator.validateDocumentNumber(originEntry, workingEntryInfo);
        validator.validateChart(originEntry, workingEntryInfo);
        validator.validateObjectCode(originEntry, workingEntryInfo);
        validator.validateObjectType(originEntry, workingEntryInfo);
        validator.validateFinancialSubObjectCode(originEntry, workingEntryInfo);
        validator.validateBalanceType(originEntry, workingEntryInfo);
        validator.validateUniversityFiscalPeriodCode(originEntry, workingEntryInfo, universityRunDate);
        validator.validateEncumbranceUpdateCode(originEntry, workingEntryInfo);
        validator.validateReferenceDocument(originEntry, workingEntryInfo);
        validator.validateReversalDate(originEntry, workingEntryInfo, universityDateDao);

        return workingEntryInfo;
    }

	/**
	 * @param originEntry
	 * @param workingEntry
	 */
	private void handleCostSharing(OriginEntry originEntry, OriginEntryInfo workingEntryInfo) {
		OriginEntry workingEntry = workingEntryInfo.getOriginEntry();
		
		// if (ObjectTypeCode = "EE" or "EX" or "ES" or "TE") AND
        //  (BalanceTypeCode = "EX" or "IE" or "PE") AND (holdFundGroupCD = "CG")
        //  AND (holdSubAccountTypeCD == "CS") AND UniversityFiscalPeriod != "BB"
        //  (beginning balance) AND UniversityFiscalPeriod != "CB" (contract
        //  balance) AND DocumentTypeCD != "JV" and != "AA" //todo: move to properties
        //      DO COST SHARING! (move into separate method)
        if (workingEntry.getOption() != null && null != originEntry.getAccount() &&
                (workingEntry.getOption().getFinObjTypeExpenditureexpCd().equals(workingEntry.getFinancialObjectTypeCode()) ||
                workingEntry.getOption().getFinObjTypeExpNotExpendCode().equals(workingEntry.getFinancialObjectTypeCode()) ||
                workingEntry.getOption().getFinObjTypeExpendNotExpCode().equals(workingEntry.getFinancialObjectTypeCode()) ||
                "TE".equals(workingEntry.getFinancialObjectTypeCode())) &&
                "CG".equals(originEntry.getAccount().getSubFundGroupCode()) &&
                "CS".equals(originEntry.getA21SubAccount().getSubAccountTypeCode()) &&
                !"BB".equals(originEntry.getUniversityFiscalPeriodCode()) &&
                !"CB".equals(originEntry.getUniversityFiscalPeriodCode()) &&
                !"JV".equals(originEntry.getFinancialDocumentTypeCode()) &&
                !"AA".equals(originEntry.getFinancialDocumentTypeCode())) {
            if (originEntry.getOption().getExtrnlEncumFinBalanceTypCd().equals(workingEntry.getFinancialBalanceTypeCode()) ||
                    originEntry.getOption().getIntrnlEncumFinBalanceTypCd().equals(workingEntry.getFinancialBalanceTypeCode()) ||
                    originEntry.getOption().getPreencumbranceFinBalTypeCd().equals(workingEntry.getFinancialBalanceTypeCode())) {
                // Do cost sharing!
                costShareEncumbrance(originEntry, workingEntryInfo);
            }
            // if (ObjectTypeCode = "EE" or "EX" or "ES" or "TE") AND
            // (BalanceTypeCode = "AC") AND (holdFundGroupCD = "CG") AND
            // (holdSubAccountTypeCD == "CS") AND UniversityFiscalPeriod != "BB"
            // (beginning balance) AND UniversityFiscalPeriod != "CB" (contract
            // balance) AND DocumentTypeCD != "JV" and != "AA" 
            //  if (debitCreditCD = "D")
            //      subtract amount from costSharingAccumulator
            //  else
            //      add amount to costSharingAccumulator
            if (originEntry.getOption().getActualFinancialBalanceTypeCd().equals(workingEntry.getFinancialBalanceTypeCode())) {
                if (workingEntry.isDebit()) {
                	workingEntryInfo.getCostSharingAmount().subtract(originEntry.getTransactionLedgerEntryAmount());
                } else {
                	workingEntryInfo.getCostSharingAmount().add(originEntry.getTransactionLedgerEntryAmount());
                }
            }
        }
	}

	/**
	 * @param originEntry
	 */
	private void updateAmountsForUnitOfWork(OriginEntry originEntry, UnitOfWorkInfo unitOfWorkInfo) {

        //            3400  023200     IF CABTYP-FIN-OFFST-GNRTN-CD = 'Y'
        //            3401  023210        AND (FDOC-TYP-CD OF GLEN-RECORD NOT = 'ACLO'
        //            3402  023220             AND UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT
        //            3403  023230             = 'BB' AND UNIV-FISCAL-PRD-CD OF GLEN-RECORD
        //            3404  023240             NOT = 'CB')
        //            3405  023250        IF TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD = DEBIT
        //            3406  023260           ADD      TRN-LDGR-ENTR-AMT OF GLEN-RECORD
        //            3407  023270            TO      SCRB-OFFSET-AMOUNT
        //            3408  023280        ELSE
        //            3409  023290           SUBTRACT TRN-LDGR-ENTR-AMT OF GLEN-RECORD
        //            3410  023300               FROM SCRB-OFFSET-AMOUNT
        //            3411  023310        END-IF
        //            3412  023320     END-IF
        
        if(!ObjectHelper.isNull(originEntry.getBalanceType())
                && originEntry.getBalanceType().isFinancialOffsetGenerationIndicator()
                && !"ACLO".equals(originEntry.getFinancialDocumentTypeCode())
                && !ObjectHelper.isOneOf(
                        originEntry.getUniversityFiscalPeriodCode(), 
                        invalidFiscalPeriodCodesForOffsetGeneration)) {
            
            if(originEntry.isDebit()) {
                
                unitOfWorkInfo.getTotalOffsetAmount().add(originEntry.getTransactionLedgerEntryAmount());
                
            } else {
                
                unitOfWorkInfo.getTotalOffsetAmount().subtract(originEntry.getTransactionLedgerEntryAmount());
                
            }
            
        }
        
        //      3413  023360     EVALUATE TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
        //      3414  023370        WHEN 'D'
        //      3415  023380           ADD TRN-LDGR-ENTR-AMT   OF GLEN-RECORD
        //      3416  023390            TO SCRB-DEBIT-ACCUM-AMOUNT
        //      3417  023400        WHEN 'C'
        //      3418  023410           ADD TRN-LDGR-ENTR-AMT   OF GLEN-RECORD
        //      3419  023420            TO SCRB-CREDIT-ACCUM-AMOUNT
        //      3420  023430     END-EVALUATE
        
        if("D".equals(originEntry.getTransactionDebitCreditCode())) {
            
            unitOfWorkInfo.getTotalDebitAmount().add(originEntry.getTransactionLedgerEntryAmount());
            
        } else if ("C".equals(originEntry.getTransactionDebitCreditCode())) {
            
            unitOfWorkInfo.getTotalCreditAmount().add(originEntry.getTransactionLedgerEntryAmount());
            
        }
        
	}

    /**
     * 
     * @param currentEntry
     * @param nextEntry
     * @return
     */
    private boolean isSameUnitOfWork(OriginEntry currentEntry, OriginEntry nextEntry) {
    	
        // Check the key fields
        return null != currentEntry && null != nextEntry 
        	&& ObjectHelper.isEqual(currentEntry.getFinancialDocumentTypeCode(), nextEntry.getFinancialDocumentTypeCode()) 
        	&& ObjectHelper.isEqual(currentEntry.getFinancialSystemOriginationCode(), nextEntry.getFinancialSystemOriginationCode())
        	&& ObjectHelper.isEqual(currentEntry.getFinancialDocumentNumber(), nextEntry.getFinancialDocumentNumber()) 
        	&& ObjectHelper.isEqual(currentEntry.getChartOfAccountsCode(), nextEntry.getChartOfAccountsCode())
        	&& ObjectHelper.isEqual(currentEntry.getAccountNumber(), nextEntry.getAccountNumber())
        	&& ObjectHelper.isEqual(currentEntry.getSubAccountNumber(), nextEntry.getSubAccountNumber())
            && ObjectHelper.isEqual(currentEntry.getFinancialBalanceTypeCode(), nextEntry.getFinancialBalanceTypeCode())
            && ObjectHelper.isEqual(currentEntry.getFinancialDocumentReversalDate(), nextEntry.getFinancialDocumentReversalDate())
            && ObjectHelper.isEqual(currentEntry.getUniversityFiscalPeriodCode(), nextEntry.getUniversityFiscalPeriodCode());
        
    }

    /**
     * @param workingEntryInfo
     * @param accountNumber
     * @param chartCode
     */
    private void processCapitalization(OriginEntryInfo workingEntryInfo, String accountNumber, String chartCode) {
        
        OriginEntry workingEntry = workingEntryInfo.getOriginEntry();
        
        BatchInfo batchInfo = 
        	workingEntryInfo.getUnitOfWorkInfo().getDocumentInfo().getOriginEntryGroupInfo().getBatchInfo();
        
        if ( workingEntry.getFinancialBalanceTypeCode().equals(workingEntry.getOption().getActualFinancialBalanceTypeCd())
                && !ObjectHelper.isOneOf(workingEntry.getFinancialDocumentTypeCode(), badDocumentTypeCodesForCapitalization)
                && (!ObjectHelper.isNull(workingEntry.getFinancialObject())
                    && ObjectHelper.isOneOf(
                        workingEntry.getFinancialObject().getFinancialObjectSubTypeCode(), goodObjectSubTypeCodesForCapitalization))
                && (!"BB".equals(workingEntry.getUniversityFiscalPeriodCode())
                    && !"CB".equals(workingEntry.getUniversityFiscalPeriodCode())
                    && !"ACLO".equals(workingEntry.getFinancialDocumentTypeCode()))
                && !ObjectHelper.isNull(workingEntry.getAccount()) 
                && !"EXTAGY".equals(workingEntry.getAccount().getSubFundGroupCode())) {
            
            String objectSubTypeCode = workingEntry.getFinancialObject().getFinancialObjectSubTypeCode();
            if(objectSubTypeCodesToObjectCodesForCapitalization.containsKey(objectSubTypeCode)) {
                workingEntry.setFinancialObjectCode((String) objectSubTypeCodesToObjectCodesForCapitalization.get(objectSubTypeCode));
            }
            
            workingEntry.setFinancialObjectTypeCode("AS");
            workingEntry.setTransactionLedgerEntryDesc(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_CAPITALIZATION)); 
            
            plantFundAccountLookup(workingEntry, chartCode, accountNumber);
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.capitalizationEntryGenerated();

            workingEntry.setFinancialObjectCode("9899"); // FUND_BALANCE
            workingEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE

            if (workingEntry.isDebit()) {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                
            } else {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
                
            }
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.capitalizationEntryGenerated();
        }
    }

    /**
     * @param workingEntryInfo
     * @param objectCode
     * @param objectTypeCode
     * @param debitOrCreditCode
     * @param description
     * @param accountNumber
     * @param subAccountNumber
     * @param chartCode
     */
    private void processPlantIndebtedness(OriginEntryInfo workingEntryInfo, String objectCode, String objectTypeCode, String debitOrCreditCode, 
            String description, String accountNumber, String subAccountNumber, String chartCode) {
        OriginEntry workingEntry = workingEntryInfo.getOriginEntry();
        
        if (workingEntry.getFinancialBalanceTypeCode().equals(workingEntry.getOption().getFinObjectTypeFundBalanceCd())
                && ("PFCMR".equals(workingEntry.getAccount().getSubFundGroupCode())
                        || "PFRI".equals(workingEntry.getAccount().getSubFundGroupCode()))
                && "PI".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) {

            workingEntry.setTransactionLedgerEntryDesc("GENERATED TRANSFER TO NET PLANT");
            
            if (workingEntry.isDebit()) {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                
            } else {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
                
            }
            
            BatchInfo batchInfo = 
                workingEntryInfo.getUnitOfWorkInfo().getDocumentInfo().getOriginEntryGroupInfo().getBatchInfo();
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.plantIndebtednessEntryGenerated();

            workingEntry.setFinancialObjectCode("9899"); // FUND_BALANCE TODO: constant
            workingEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE TODO: constant
            workingEntry.setTransactionDebitCreditCode(debitOrCreditCode);
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.plantIndebtednessEntryGenerated();

            workingEntry.setFinancialObjectCode(objectCode);
            workingEntry.setFinancialObjectTypeCode(objectTypeCode);
            workingEntry.setTransactionDebitCreditCode(debitOrCreditCode);
            workingEntry.setTransactionLedgerEntryDesc(description);
            workingEntry.setAccountNumber(accountNumber);
            workingEntry.setSubAccountNumber(subAccountNumber);

            // TODO: do we need to refresh this object first?
            if (ObjectHelper.isNull(workingEntry.getAccount().getOrganization())) {
                
                ScrubberServiceErrorHandler.addTransactionError(
                        kualiConfigurationService.getPropertyString(KeyConstants.ERROR_INVALID_ORG_CODE_FOR_PLANT_FUND), 
                        workingEntry.getAccount().getOrganizationCode(), workingEntryInfo.getErrors());
                
            } else {
                
                workingEntry.setAccountNumber(workingEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                workingEntry.setChartOfAccountsCode(workingEntry.getAccount().getOrganization().getCampusPlantChartCode());
                
            }

            workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
            workingEntry.setTransactionLedgerEntryDesc(chartCode + accountNumber + "GENERATED PLANT FUND TRANSFER");
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.plantIndebtednessEntryGenerated();

            workingEntry.setFinancialObjectCode("9899"); // FUND_BALANCE TODO: constant
            workingEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE TODO: constant

            if (workingEntry.isDebit()) {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                
            } else {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
                
            }
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.plantIndebtednessEntryGenerated();

        }
        
    }

    /**
     * @param workingEntryInfo
     * @param debitOrCreditCode
     * @param accountNumber
     * @param chartCode
     */
    private void processLiabilities(OriginEntryInfo workingEntryInfo, String debitOrCreditCode, String accountNumber, String chartCode) {
        OriginEntry workingEntry = workingEntryInfo.getOriginEntry();
        
        if ( workingEntry.getFinancialBalanceTypeCode().equals(workingEntry.getOption().getActualFinancialBalanceTypeCd())
                && !ObjectHelper.isOneOf(workingEntry.getFinancialDocumentTypeCode(), invalidDocumentTypesForLiabilities)
                && (!"BB".equals(workingEntry.getUniversityFiscalPeriodCode())
                        && !"CB".equals(workingEntry.getUniversityFiscalPeriodCode())
                        && !"ACLO".equals(workingEntry.getFinancialDocumentTypeCode()))
                && null != workingEntry.getFinancialObject() 
                && "CL".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())
                && null != workingEntry.getAccount() 
                && !"EXTAGY".equals(workingEntry.getAccount().getSubFundGroupCode())) {
            
            BatchInfo batchInfo = 
                workingEntryInfo.getUnitOfWorkInfo().getDocumentInfo().getOriginEntryGroupInfo().getBatchInfo();
            
            workingEntry.setFinancialObjectCode("9603"); // NOTES_PAYABLE_CAPITAL_LEASE TODO: constant
            workingEntry.setFinancialObjectTypeCode("LI"); // LIABILITY TODO: constant
            workingEntry.setTransactionDebitCreditCode(debitOrCreditCode);
            workingEntry.setTransactionLedgerEntryDesc(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_LIABILITY));
            plantFundAccountLookup(workingEntry, chartCode, accountNumber);
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.liabilityEntryGenerated();

            workingEntry.setFinancialObjectCode("9899"); // FUND_BALANCE TODO: constant
            workingEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE TODO: constant

            if (workingEntry.isDebit()) {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                
            } else {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
                
            }
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.liabilityEntryGenerated();
            
        }
        
    }

    /**
     * @param workingEntry
     * @param chartCode
     * @param accountNumber
     */
    private void plantFundAccountLookup(OriginEntry workingEntry, String chartCode, String accountNumber) { // 4000-PLANT_FUND_ACCOUNT
        
        workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        
        if (workingEntry.getChartOfAccountsCode().equals(workingEntry.getAccount().getOrganization().getChartOfAccountsCode())
            && workingEntry.getAccount().getOrganizationCode() == workingEntry.getAccount().getOrganization().getOrganizationCode()
            && chartCode.equals(workingEntry.getAccount().getChartOfAccountsCode())
            && accountNumber.equals(workingEntry.getAccount().getAccountNumber())) {
            
            String objectSubTypeCode = workingEntry.getFinancialObject().getFinancialObjectSubTypeCode();
            
            if (ObjectHelper.isOneOf(objectSubTypeCode, campusObjectSubTypeCodesForPlantFundAccountLookups)) {
                
                workingEntry.setAccountNumber(workingEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                workingEntry.setChartOfAccountsCode(workingEntry.getAccount().getOrganization().getCampusPlantChartCode());
                
            } else if (ObjectHelper.isOneOf(objectSubTypeCode, organizationObjectSubTypeCodesForPlantFundAccountLookups)) {
                
                workingEntry.setAccountNumber(workingEntry.getAccount().getOrganization().getOrganizationPlantAccountNumber());
                workingEntry.setChartOfAccountsCode(workingEntry.getAccount().getOrganization().getOrganizationPlantChartCode());
                
            }
            
        }
        
        // In COBOL, the CA_ORG_T table is read at this time to reset the org information. I dont think this is necessary
    }

    /**
     * 3000-COST-SHARE
     * 
     * The purpose of this method is to generate a "Cost Share Entry" and its
     * corresponding offset.
     * 
     * Object code "9915" is used for the cost share object code. The offset object code
     * is determined by reading the GL_OFFSET_DEFN_T table based on the fiscal year,
     * chart of accounts, document type, and balance type code. The object type code
     * is obtained from CA_OBJECT_CODE_T based on fiscal year, chart of accounts code,
     * and object code.
     * 
     * Next it generates an entry based on the cost share chart of accounts and
     * cost share account number from in the CA_A21_SUB_ACCT_T table for the original
     * transaction chart and account number. the object code for this entry is obtained
     * by call the method SET-OBJECT-2400. The offset object code for this entry is
     * obtained by reading the GL_OFFSET_DEFN_T table based on fiscal year, chart,
     * document type, balance type from the original input transaction. The object type code
     * is obtained from the CA_OBJECT_CODE_T table for the object code from the
     * original input transaction.
     * 
     * @param workingEntry
     */
    private void processCostSharing(OriginEntryInfo workingEntryInfo) {
        OriginEntry csEntry = new OriginEntry();
        KualiDecimal costSharingAmount = workingEntryInfo.getCostSharingAmount();
        
        csEntry.setFinancialObjectCode("9915"); //TODO: TRSFRS_OF_FUNDS_REVENUE constant
        csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        csEntry.setFinancialObjectTypeCode("TE"); //TODO: constant
        csEntry.setTrnEntryLedgerSequenceNumber(new Integer(0));
        csEntry.setTransactionLedgerEntryDesc(
            "COSTSHARE_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" 
            + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant
        if (costSharingAmount.isPositive()) {
            csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            csEntry.setTransactionLedgerEntryAmount(costSharingAmount);
        } else {
            csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            csEntry.setTransactionLedgerEntryAmount(costSharingAmount.negated());
        }
        csEntry.setTransactionDate(runDate);
        csEntry.setOrganizationDocumentNumber("");
        csEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        csEntry.setOrganizationReferenceId(null);
        csEntry.setReferenceFinDocumentTypeCode(null);
        csEntry.setFinSystemRefOriginationCode(null);
        csEntry.setFinancialDocumentReferenceNbr(null);
        csEntry.setReversalDate(null);
        csEntry.setTransactionEncumbranceUpdtCd("");

        createOutputEntry(csEntry, validGroup);
        BatchInfo batchInfo = workingEntryInfo.getUnitOfWorkInfo().getDocumentInfo().getOriginEntryGroupInfo().getBatchInfo();
        batchInfo.costShareEntryGenerated();

        csEntry.setTransactionLedgerEntryDesc(
            "OFFSET_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant

        OffsetDefinition offset = offsetDefinitionService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                "TF", csEntry.getFinancialBalanceTypeCode());
        if (ScrubberServiceErrorHandler.ifNullAddTransactionErrorAndReturnFalse(offset, workingEntryInfo.getErrors(),
        		kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), null)) {
            csEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            if(offset.getFinancialSubObjectCode() == null) {
                csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            } else {
                csEntry.setFinancialSubObjectCode(offset.getFinancialSubObjectCode());
            }
        }

        ObjectCode objectCode = objectCodeService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                csEntry.getFinancialObjectCode());
        if (ScrubberServiceErrorHandler.ifNullAddTransactionErrorAndReturnFalse(
                objectCode, workingEntryInfo.getErrors(),
                kualiConfigurationService.getPropertyString(KeyConstants.ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD), 
                csEntry.getFinancialObjectCode())) {
            csEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            if(csEntry.isCredit()) {
                csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
        }

        createOutputEntry(csEntry, validGroup);
        batchInfo.costShareEntryGenerated();

        csEntry.setTransactionLedgerEntryDesc(
            "COSTSHARE_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" 
            + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant
        csEntry.setChartOfAccountsCode("");

        csEntry.setChartOfAccountsCode(csEntry.getA21SubAccount().getCostShareChartOfAccountCode());
        csEntry.setAccountNumber(csEntry.getA21SubAccount().getCostShareSourceAccountNumber());

        lookupObjectCode(csEntry, workingEntryInfo);

        if(csEntry.getA21SubAccount().getCostShareSourceAccountNumber() == null) {
            csEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        } else {
            csEntry.setSubAccountNumber(csEntry.getA21SubAccount().getCostShareSourceSubAccountNumber());
        }

        csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        csEntry.setFinancialObjectTypeCode("TE"); //TODO: move into constants
        csEntry.setTrnEntryLedgerSequenceNumber(new Integer(0));
        csEntry.setTransactionLedgerEntryDesc(
            "COSTSHARE_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" 
            + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant

        if (costSharingAmount.isPositive()) {
            csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            csEntry.setTransactionLedgerEntryAmount(costSharingAmount);
        } else {
            csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            csEntry.setTransactionLedgerEntryAmount(costSharingAmount.negated());
        }

        csEntry.setTransactionDate(runDate);
        csEntry.setOrganizationDocumentNumber("");
        csEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        csEntry.setOrganizationReferenceId(null);
        csEntry.setReferenceFinDocumentTypeCode(null);
        csEntry.setFinSystemRefOriginationCode(null);
        csEntry.setFinancialDocumentReferenceNbr(null);
        csEntry.setReversalDate(null);
        csEntry.setTransactionEncumbranceUpdtCd("");

        createOutputEntry(csEntry, validGroup);
        batchInfo.costShareEntryGenerated();

        csEntry.setTransactionLedgerEntryDesc(
            "OFFSET_DESCRIPTION" + "***" + runCal.get(Calendar.MONTH) + "/" + runCal.get(Calendar.DAY_OF_MONTH)); // TODO: change to constant

        if (ScrubberServiceErrorHandler.ifNullAddTransactionErrorAndReturnFalse(offset,workingEntryInfo.getErrors(), 
        		kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), null)) {
            csEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            if(offset.getFinancialSubObjectCode() == null) {
                csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            } else {
                csEntry.setFinancialSubObjectCode(offset.getFinancialSubObjectCode());
            }
        }

        objectCode = objectCodeService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                csEntry.getFinancialObjectCode());
        if (ScrubberServiceErrorHandler.ifNullAddTransactionErrorAndReturnFalse(objectCode, workingEntryInfo.getErrors(),
        		kualiConfigurationService.getPropertyString(KeyConstants.ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD), 
                csEntry.getFinancialObjectCode())) {
            csEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            if(csEntry.isCredit()) {
                csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
        }
        
        createOutputEntry(csEntry, validGroup);
        batchInfo.costShareEntryGenerated();
    } // End of method

    /**
     * 3200-COST-SHARE-ENC
     * 
     * The purpose of this method is to generate a "Cost Share Encumbrance" transaction
     * for the current transaction and its offset.
     * 
     * The cost share chart and account for current transaction are obtained from the
     * CA_A21_SUB_ACCT_T table. This method calls the method SET-OBJECT-2004 to get
     * the Cost Share Object Code. It then writes out the cost share transaction.
     * Next it read the GL_OFFSET_DEFN_T table for the offset object code that
     * corresponds to the cost share object code. In addition to the object code it
     * needs to get subobject code. It then reads the CA_OBJECT_CODE_T table to make
     * sure the offset object code found in the GL_OFFSET_DEFN_T is valid  and to
     * get the object type code associated with this object code. It writes out the
     * offset transaction and returns.
     * 
     * @param inputEntry
     */
    private void costShareEncumbrance(OriginEntry inputEntry, OriginEntryInfo workingEntryInfo) {

        OriginEntry csEntry = new OriginEntry(inputEntry);

        csEntry.setTransactionLedgerEntryDesc(csEntry.getTransactionLedgerEntryDesc().substring(0, 29) +
                "FR" + csEntry.getChartOfAccountsCode()+ csEntry.getAccountNumber());

        csEntry.setChartOfAccountsCode(csEntry.getA21SubAccount().getCostShareChartOfAccountCode());
        csEntry.setAccountNumber(csEntry.getA21SubAccount().getCostShareSourceAccountNumber());
        csEntry.setSubAccountNumber(csEntry.getA21SubAccount().getCostShareSourceSubAccountNumber());

        if(!StringUtils.hasText(csEntry.getSubAccountNumber())) {
            csEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);
        }

        csEntry.setFinancialBalanceTypeCode("CE"); // TODO: constant
        csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
        csEntry.setTrnEntryLedgerSequenceNumber(new Integer(0));

        if (StringUtils.hasText(inputEntry.getTransactionDebitCreditCode())) {
            if (inputEntry.getTransactionLedgerEntryAmount().isPositive()) {
                csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                csEntry.setTransactionLedgerEntryAmount(inputEntry.getTransactionLedgerEntryAmount().negated());
            }
        }

        csEntry.setTransactionDate(runDate);

        lookupObjectCode(csEntry, workingEntryInfo);
        
        BatchInfo batchInfo = workingEntryInfo.getUnitOfWorkInfo().getDocumentInfo().getOriginEntryGroupInfo().getBatchInfo();
        createOutputEntry(csEntry, validGroup);
        batchInfo.costShareEncumbranceGenerated();

        csEntry.setTransactionLedgerEntryDesc(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_OFFSET));

        OffsetDefinition offset = offsetDefinitionService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                csEntry.getFinancialDocumentTypeCode(), csEntry.getFinancialBalanceTypeCode());
        if (ScrubberServiceErrorHandler.ifNullAddTransactionErrorAndReturnFalse(offset, workingEntryInfo.getErrors(),
        		kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND), null)) {
            csEntry.setFinancialObjectCode(offset.getFinancialObjectCode());
            if(offset.getFinancialSubObjectCode() == null) {
                csEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            } else {
                csEntry.setFinancialSubObjectCode(offset.getFinancialSubObjectCode());
            }
        }

        ObjectCode objectCode = objectCodeService.getByPrimaryId(
                csEntry.getUniversityFiscalYear(), csEntry.getChartOfAccountsCode(),
                csEntry.getFinancialObjectCode());
        if (ScrubberServiceErrorHandler.ifNullAddTransactionErrorAndReturnFalse(objectCode, workingEntryInfo.getErrors(),
        		kualiConfigurationService.getPropertyString(KeyConstants.ERROR_NO_OBJECT_FOR_OBJECT_ON_OFSD), 
                csEntry.getFinancialObjectCode())) {
            csEntry.setFinancialObjectTypeCode(objectCode.getFinancialObjectTypeCode());
            if(csEntry.isCredit()) {
                csEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            } else {
                csEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            }
        }

        csEntry.setTransactionDate(runDate);
        csEntry.setOrganizationDocumentNumber("");
        csEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        csEntry.setOrganizationReferenceId(null);
        csEntry.setReferenceFinDocumentTypeCode(null);
        csEntry.setFinSystemRefOriginationCode(null);
        csEntry.setFinancialDocumentReferenceNbr(null);
        csEntry.setReversalDate(null);
        csEntry.setTransactionEncumbranceUpdtCd("");

        createOutputEntry(csEntry, validGroup); // TODO: is this created if there have been errors?!
        batchInfo.costShareEncumbranceGenerated();
    }// End of method

    /**
     * SET-OBJECT-2004
     * 
     * The purpose of this method is to find a cost share object code. It accomplishes
     * this by reading the CA_OBJECT_CODE_T based on input transaction's object code,
     * fiscal year, and chart of accounts code. It then checks the object level code
     * for the object it just read to determine what the cost share object code should be.
     * 
     * As an example if the object level of the object code on the input transaction is
     * "Travel" then this methods sets the cost share object code to "9960".
     * 
     * This method will then verify the cost share object code against the CA_OBJECT_CODE_T
     * table and obtain the corresponding object type to put into the output transaction.
     * 
     * @param inputEntry
     */
    private void lookupObjectCode(OriginEntry inputEntry, OriginEntryInfo workingEntryInfo) {

        // TODO: cant we just do an inputEntry
        persistenceService.retrieveReferenceObject(inputEntry,"financialObject");

        ScrubberServiceErrorHandler.ifNullAddTransactionErrorAndReturnFalse(
        	inputEntry.getFinancialObject(), 
        	workingEntryInfo.getErrors(), 
        	kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OBJECT_CODE_NOT_FOUND), 
            inputEntry.getFinancialObjectCode());

        String objectCode = inputEntry.getFinancialObjectCode();
        String inputObjectLevelCode = inputEntry.getFinancialObject().getFinancialObjectLevelCode();
        String inputObjectCode = inputEntry.getFinancialObjectCode();

        // TODO: MOVE ALL THIS TO CONSTANTS
        if("ACSA".equals(inputObjectLevelCode)) { //ACADEMIC SALARIES
            objectCode = "9920"; //TRSFRS_OF_FUNDS_ACAD_SAL
        } else if("BASE".equals(inputObjectLevelCode)) { //ASSESMENTS_EXPENDITURES
            objectCode = "9959"; //TRANSFER_OUT_20_REALLOCATION
        } else if("BENF".equals(inputObjectLevelCode) &&
                ("9956".equals(inputObjectCode) || "5700".compareTo(inputObjectCode) < 0 )) { //BENEFITS
            objectCode = "9956"; //TRSFRS_OF_FUNDS_FRINGE_BENF
        } else if("BENF".equals(inputObjectLevelCode)) { //BENEFITS
            objectCode = "9957"; //TRSFRS_OF_FUNDS_RETIREMENT 
        } else if("BISA".equals(inputObjectLevelCode)) { //BI-WEEKLY_SALARY
            objectCode = "9925"; //TRSFRS_OF_FUNDS_CLER_SAL 
        } else if("CAP".equals(inputObjectLevelCode)) { //CAPITAL_ASSETS
            objectCode = "9970"; //TRSFRS_OF_FUNDS_CAPITAL  
        } else if("CORE".equals(inputObjectLevelCode)) { //ALLOTMENTS_AND_CHARGES_OUT
            // Do nothing
        } else if("CORI".equals(inputObjectLevelCode)) { //ALLOTMENTS_AND_CHARGES_IN
            // Do nothing
        } else if("FINA".equals(inputObjectLevelCode) &&
                ("9954".equals(inputObjectCode) || "5400".equals(inputObjectCode))) { //STUDENT_FINANCIAL_AID - TRSFRS_OF_FUNDS_FEE_REM  - GRADUATE_FEE_REMISSIONS
            objectCode = "9954"; //TRSFRS_OF_FUNDS_CAPITAL  
        } else if("FINA".equals(inputObjectLevelCode)) { //STUDENT_FINANCIAL_AID
            objectCode = "9958"; //TRSFRS_OF_FUNDS_FELL_AND_SCHO 
        } else if("HRCO".equals(inputObjectLevelCode)) { //HOURLY_COMPENSATION
            objectCode = "9930"; //TRSFRS_OF_FUNDS_WAGES 
        } else if("ICOE".equals(inputObjectLevelCode)) { //INDIRECT_COST_RECOVERY_EXPENSE
            objectCode = "9955"; //TRSFRS_OF_FUNDS_INDRCT_COST 
        } else if("PART".equals(inputObjectLevelCode)) { //PART_TIME_INSTRUCTION_NON_STUDENT
            objectCode = "9923"; //TRSFRS_OF_FUNDS_ACAD_ASSIST 
        } else if("PRSA".equals(inputObjectLevelCode)) { //PROFESSIONAL_SALARIES
            objectCode = "9924"; //TRSF_OF_FUNDS_PROF_SAL 
        } else if("RESV".equals(inputObjectLevelCode)) { //RESERVES
            objectCode = "9979"; //TRSFRS_OF_FUNDS_UNAPP_BAL
        } else if("SAAP".equals(inputObjectLevelCode)) { //SALARY_ACCRUAL_EXPENSE
            objectCode = "9923"; //TRSFRS_OF_FUNDS_ACAD_ASSIST
        } else if("TRAN".equals(inputObjectLevelCode)) { //TRANSFER_EXPENSE
            objectCode = "9959"; //TRANSFER_OUT_20_REALLOCATION
        } else if("TRAV".equals(inputObjectLevelCode)) { //TRAVEL
            objectCode = "9960"; //TRSFRS_OF_FUNDS_TRAVEL
        } else if("TREX".equals(inputObjectLevelCode)) { //TRANSFER_5199_EXPENSE
            objectCode = "9959"; //TRANSFER_OUT_20_REALLOCATION
        } else if("TRIN".equals(inputObjectLevelCode)) { //TRANSFER_1699_INCOME
            objectCode = "9915"; //TRSFRS_OF_FUNDS_REVENUE  
        } else {
            objectCode = "9940"; //TRSFRS_OF_FUNDS_SUP_AND_EXP 
        }

        inputEntry.setFinancialObjectCode(objectCode);
        persistenceService.retrieveReferenceObject(inputEntry,"financialObject"); // TODO: this needs to be checked!

        if (ScrubberServiceErrorHandler.ifNullAddTransactionErrorAndReturnFalse(inputEntry.getFinancialObject(), workingEntryInfo.getErrors(),
                kualiConfigurationService.getPropertyString(KeyConstants.ERROR_COST_SHARE_OBJECT_NOT_FOUND), 
                inputEntry.getFinancialObjectCode())) {
            inputEntry.setFinancialObjectTypeCode(inputEntry.getFinancialObject().getFinancialObjectTypeCode());
        }
    }// End of method

    /**
     * 3000-Offset.
     * 
     * The purpose of this method is to build the actual offset transaction.
     * It does this by performing the following steps:
     * 1. Getting the offset object code and offset subobject code from the
     *    GL Offset Definition Table.
     * 2. For the offset object code it needs to get the associated object type,
     *    object subtype, and object active code.
     *    
     * @param workingEntry
     */
    private void generateOffset(UnitOfWorkInfo unitOfWorkInfo) {
        
        // The template entry is set inside processUnitOfWork.
        OriginEntry workingEntry = unitOfWorkInfo.getTemplateEntryForOffsetGeneration();
    	
        OriginEntry offsetEntry = new OriginEntry();

        // Temporary storage for any errors.
        ArrayList errors = new ArrayList();
        
//        3970  029760 3000-OFFSET.
//        3971  029840     MOVE OFFSET-DESCRIPTION
//        3972  029850       TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD.
//        3973  029890     MOVE UNIV-FISCAL-YR     OF ALT-GLEN-RECORD
//        3974  029900       TO GLOFSD-UNIV-FISCAL-YR.
//        3975  029910     MOVE FIN-COA-CD         OF ALT-GLEN-RECORD
//        3976  029920       TO GLOFSD-FIN-COA-CD.
//        3977  029930     MOVE FDOC-TYP-CD        OF ALT-GLEN-RECORD
//        3978  029940       TO GLOFSD-FDOC-TYP-CD.
//        3979  029950     MOVE FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD
//        3980  029960       TO GLOFSD-FIN-BALANCE-TYP-CD.
//        3981  029970     EXEC SQL
//        3982  029980          SELECT FIN_OBJECT_CD,
//        3983  029990                 FIN_SUB_OBJ_CD
//        3984  030000          INTO   :GLOFSD-FIN-OBJECT-CD :GLOFSD-FOC-I,
//        3985  030010                 :GLOFSD-FIN-SUB-OBJ-CD :GLOFSD-FSOC-I
//        3986  030020          FROM   GL_OFFSET_DEFN_T
//        3987  030030       WHERE  UNIV_FISCAL_YR = RTRIM(:GLOFSD-UNIV-FISCAL-YR)
//        3988  030040        AND  FIN_COA_CD = RTRIM(:GLOFSD-FIN-COA-CD)
//        3989  030050        AND  FDOC_TYP_CD = RTRIM(:GLOFSD-FDOC-TYP-CD)
//        3990  030060        AND  FIN_BALANCE_TYP_CD
//        3991  030070             = RTRIM(:GLOFSD-FIN-BALANCE-TYP-CD)
//        3992  030080     END-EXEC

        // If the offset amount is zero, don't bother to lookup the offset definition ...
        // NOTE (laran) this is a rule that Sterling suggested.
        
        if(unitOfWorkInfo.getTotalOffsetAmount().isZero()) {
            
            return;
            
        }
        
        // ... otherwise lookup the offset definition appropriate for this entry.
        // We need the offset object code from it.
        
        OffsetDefinition offsetDefinition = offsetDefinitionService.getByPrimaryId(
            workingEntry.getUniversityFiscalYear(),
            workingEntry.getChartOfAccountsCode(),
            workingEntry.getFinancialDocumentTypeCode(),
            workingEntry.getFinancialBalanceTypeCode());
        
//        3993             IF GLOFSD-FOC-I < ZERO
//        3994                MOVE SPACES TO GLOFSD-FIN-OBJECT-CD
//        3995             END-IF
//        3996             IF GLOFSD-FSOC-I < ZERO
//        3997                MOVE SPACES TO GLOFSD-FIN-SUB-OBJ-CD
//        3998             END-IF
        
        // done by default in java, fields just remain null.
        
//        3999  030090     EVALUATE SQLCODE
//        4000  030100          WHEN 0
//        4001  030110              MOVE GLOFSD-FIN-OBJECT-CD TO
//        4002  030120                   FIN-OBJECT-CD OF ALT-GLEN-RECORD
//        4003  030130              IF GLOFSD-FIN-SUB-OBJ-CD = SPACES
//        4004  030140                   MOVE FIN-SUB-OBJ-CD-DASHES
//        4005  030150                       TO FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD
//        4006  030160              ELSE
//        4007  030170                   MOVE GLOFSD-FIN-SUB-OBJ-CD TO
//        4008  030180                          FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD
//        4009  030190              END-IF
//        4010  030200          WHEN +100
//        4011  030210          WHEN +1403
//        4012  030220              MOVE ALT-GLEN-RECORD (1:51) TO RP-TABLE-KEY
//        4013                      MOVE SPACES TO RP-DATA-ERROR
//        4014  030230              MOVE 'OFFSET DEFINITION NOT FOUND' TO RP-MSG-ERROR
//        4015  030240              PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//        4016  030250              MOVE SPACES TO DCLGL-OFFSET-DEFN-T
//        4017  030260          WHEN OTHER
//        4018  030270              DISPLAY ' ERROR ACCESSING OFSD TABLE '
//        4019  030280                      'SQL CODE IS ' SQLCODE
//        4020  030290              MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4021  030300              GO TO 2000-ENTRY-EXIT
//        4022  030310     END-EVALUATE
        
        if(ObjectHelper.isNull(offsetDefinition)) {
            
            StringBuffer sb = new StringBuffer();
            sb.append("Fiscal Year: ").append(workingEntry.getUniversityFiscalYear());
            sb.append(", Chart of Accounts: ").append(workingEntry.getChartOfAccountsCode());
            sb.append(", Document Type: ").append(workingEntry.getFinancialDocumentTypeCode());
            sb.append(", Balance Type: ").append(workingEntry.getFinancialBalanceTypeCode());
            
            ScrubberServiceErrorHandler.addTransactionError(
                    kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND),
                    sb.toString(), errors);
           
        } else {
            
            offsetEntry.setFinancialObject(workingEntry.getFinancialObject());
            offsetEntry.setFinancialObjectCode(workingEntry.getFinancialObjectCode());
            
            if(ObjectHelper.isNull(offsetDefinition.getFinancialSubObjectCode())) {
                
                offsetEntry.setFinancialSubObject(null);
                offsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
                
            } else {
                
                offsetEntry.setFinancialSubObject(workingEntry.getFinancialSubObject());
                offsetEntry.setFinancialSubObjectCode(workingEntry.getFinancialSubObjectCode());
                
            }
            
        }
        
//        4023  030360     MOVE UNIV-FISCAL-YR OF ALT-GLEN-RECORD
//        4024  030370       TO CAOBJT-UNIV-FISCAL-YR
//        4025  030380     MOVE FIN-COA-CD     OF ALT-GLEN-RECORD
//        4026  030390       TO CAOBJT-FIN-COA-CD
//        4027  030400     MOVE FIN-OBJECT-CD  OF ALT-GLEN-RECORD
//        4028  030410       TO CAOBJT-FIN-OBJECT-CD
//        4029  030420     EXEC SQL
//        4030  030430       SELECT    FIN_OBJ_TYP_CD,
//        4031  030440                 FIN_OBJ_SUB_TYP_CD,
//        4032  030450                 FIN_OBJ_ACTIVE_CD,
//        4033  030460                 FOBJ_MNXFR_ELIM_CD
//        4034  030470       INTO      :CAOBJT-FIN-OBJ-TYP-CD :CAOBJT-FOTC-I,
//        4035  030480                 :CAOBJT-FIN-OBJ-SUB-TYP-CD :CAOBJT-FOSTC-I,
//        4036  030490                 :CAOBJT-FIN-OBJ-ACTIVE-CD :CAOBJT-FOAC-I,
//        4037  030500                 :CAOBJT-FOBJ-MNXFR-ELIM-CD :CAOBJT-FMEC-I
//        4038  030510       FROM      CA_OBJECT_CODE_T
//        4039  030520       WHERE     UNIV_FISCAL_YR= RTRIM(:CAOBJT-UNIV-FISCAL-YR)
//        4040  030530         AND     FIN_COA_CD=     RTRIM(:CAOBJT-FIN-COA-CD)
//        4041  030540         AND     FIN_OBJECT_CD=  RTRIM(:CAOBJT-FIN-OBJECT-CD)
//        4042  030550     END-EXEC
        
        if(ObjectHelper.isNull(workingEntry.getFinancialObject())) {
            
            persistenceService.retrieveReferenceObject(workingEntry, "financialObject");
            
        }
        
//        4043              IF CAOBJT-FOTC-I < ZERO
//        4044                 MOVE SPACES TO CAOBJT-FIN-OBJ-TYP-CD
//        4045              END-IF
//        4046              IF CAOBJT-FOSTC-I < ZERO
//        4047                 MOVE SPACES TO CAOBJT-FIN-OBJ-SUB-TYP-CD
//        4048              END-IF
//        4049              IF CAOBJT-FOAC-I < ZERO
//        4050                 MOVE SPACES TO CAOBJT-FIN-OBJ-ACTIVE-CD
//        4051              END-IF
//        4052              IF CAOBJT-FMEC-I < ZERO
//        4053                 MOVE SPACES TO CAOBJT-FOBJ-MNXFR-ELIM-CD
//        4054              END-IF
//        4055  030560        EVALUATE SQLCODE
//        4056  030570           WHEN 0
//        4057  030580            MOVE CAOBJT-FIN-OBJ-TYP-CD TO FIN-OBJ-TYP-CD
//        4058  030590              OF ALT-GLEN-RECORD
//        4059  030600           WHEN +100
//        4060  030610           WHEN +1403
//        4061  030620              MOVE GLEN-RECORD (1:51) TO RP-TABLE-KEY
//        4062  030630              MOVE CAOBJT-FIN-OBJECT-CD TO RP-DATA-ERROR
//        4063  030640                   FIN-OBJECT-CD OF ALT-GLEN-RECORD
//        4064  030650              MOVE 'NO OBJECT FOR OBJECT ON OFSD' TO RP-MSG-ERROR
//        4065  030660              PERFORM WRITE-ERROR-LINE THRU WRITE-ERROR-LINE-EXIT
//        4066  030670              MOVE SPACES TO DCLCA-OBJECT-CODE-T
//        4067  030680           WHEN OTHER
//        4068  030690               DISPLAY 'ERROR ACCESSING OBJECT TABLE'
//        4069  030700                       ' SQL CODE IS ' SQLCODE
//        4070  030710               MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//        4071  030720               GO TO 2000-ENTRY-EXIT
//        4072  030730        END-EVALUATE
        
        if(ObjectHelper.isNull(workingEntry.getFinancialObject())) {
            
            ScrubberServiceErrorHandler.addTransactionError(
                    kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND),
                    workingEntry.getFinancialObjectCode(), errors);
            
        } else {
            
            offsetEntry.setFinancialObjectTypeCode(workingEntry.getFinancialObjectTypeCode());
            
        }
        
//        4073  030780     MOVE SCRB-OFFSET-AMOUNT
//        4074  030790       TO TRN-LDGR-ENTR-AMT OF ALT-GLEN-RECORD.
//        4075  030800

        offsetEntry.setTransactionLedgerEntryAmount(unitOfWorkInfo.getTotalOffsetAmount());
        
//        4076  030810     IF SCRB-OFFSET-AMOUNT > ZEROES
//        4077  030820        MOVE CREDIT
//        4078  030830          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//        4079  030840     ELSE
//        4080  030850        MOVE DEBIT
//        4081  030860          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//        4082  030870        COMPUTE TRN-LDGR-ENTR-AMT OF ALT-GLEN-RECORD =
//        4083  030880                SCRB-OFFSET-AMOUNT * NEGATIVE-ONE.
        
        if (unitOfWorkInfo.getTotalOffsetAmount().isPositive()) {
            
            offsetEntry.setTransactionLedgerEntryAmount(unitOfWorkInfo.getTotalOffsetAmount());
            offsetEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
            
        } else {
            
            offsetEntry.setTransactionLedgerEntryAmount(unitOfWorkInfo.getTotalOffsetAmount().multiply(new KualiDecimal(-1)));
            offsetEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
            
        }
        
//        4084  030940     MOVE SPACES TO ORG-DOC-NBR       OF ALT-GLEN-RECORD
//        4085  030950                    ORG-REFERENCE-ID  OF ALT-GLEN-RECORD
//        4086  030960                    FDOC-REF-TYP-CD   OF ALT-GLEN-RECORD
//        4087  030970                    FS-REF-ORIGIN-CD  OF ALT-GLEN-RECORD
//        4088  030980                    FDOC-REF-NBR      OF ALT-GLEN-RECORD
//        4089  030990                    TRN-ENCUM-UPDT-CD OF ALT-GLEN-RECORD.
        
        // done by default. fields just stay null.
        
//        4090  031010     MOVE PROJECT-CD-DASHES TO PROJECT-CD OF ALT-GLEN-RECORD.
        
        offsetEntry.setProjectCode(Constants.DASHES_PROJECT_CODE);
        
//        4091  031060     MOVE SCRB-TODAYS-DATE
//        4092  031070       TO TRANSACTION-DT OF ALT-GLEN-RECORD.
        
        offsetEntry.setTransactionDate(runDate);

        // Update the UnitOfWorkInfo to reflect the existence of the offset entry
        // as well as any errors that may have occurred.
        unitOfWorkInfo.setNumberOfEntries(unitOfWorkInfo.getNumberOfEntries() + 1);
        
        if(0 < errors.size()) {
            
            unitOfWorkInfo.setErrorCount(unitOfWorkInfo.getErrorCount() + errors.size());
            batchError.put(offsetEntry, errors);
            
        } else {
        
            BatchInfo batchInfo = 
                unitOfWorkInfo.getDocumentInfo().getOriginEntryGroupInfo().getBatchInfo();
            
            // write expiredEntry as expired
            createOutputEntry(offsetEntry, validGroup);
            batchInfo.offsetEntryGenerated();
            
        }
        
    }
    
//
//    /**
//     * 2520-init-SRCbArea
//     */
//    private void initScrubberValues() {
//        wsExpiredAccount = null;
//        wsExpiredChart = null;
//        scrubberUtil.offsetAmountAccumulator = new KualiDecimal(0.0);
//        scrubberUtil.creditAmountAccumulator = new KualiDecimal(0.0);
//        scrubberUtil.debitAmountAccumulator = new KualiDecimal(0.0);
//    }

    public void setOriginEntryService(OriginEntryService oes) {
        this.originEntryService = oes;
    }

    public void setOriginEntryGroupService(OriginEntryGroupService groupService) {
        this.originEntryGroupService = groupService;
    }

    public void setDateTimeService(DateTimeService dts) {
        this.dateTimeService = dts;
    }

    public void setUniversityDateDao(UniversityDateDao universityDateDao) {
        this.universityDateDao = universityDateDao;
    }

    public void setPersistenceService(PersistenceService ps) {
        persistenceService = ps;
    }
    
    /**
     * @return Returns the optionsDao.
     */
    public OptionsDao getOptionsDao() {
        return optionsDao;
    }

    /**
     * @param optionsDao The optionsDao to set.
     */
    public void setOptionsDao(OptionsDao optionsDao) {
        this.optionsDao = optionsDao;
    }

    /**
     * Sets the offsetDefinitionService attribute value.
     * @param offsetDefinitionService The offsetDefinitionService to set.
     */
    public void setOffsetDefinitionService(OffsetDefinitionService offsetDefinitionService) {
        this.offsetDefinitionService = offsetDefinitionService;
    }

    /**
     * Sets the objectCodeService attribute value.
     * @param objectCodeService The objectCodeService to set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public void setAccountService(AccountService accountService) {
        this.accountService = accountService;
    }

    public void setKualiConfigurationService(KualiConfigurationService kualiConfigurationService) {
        this.kualiConfigurationService = kualiConfigurationService;
    }

    public void setScrubberReport(ScrubberReport srs) {
        scrubberReportService = srs;
    }

    public void setBeanFactory(BeanFactory bf) throws BeansException {
      beanFactory = bf;
    }

    private void createOutputEntry(OriginEntry inputEntry, OriginEntryGroup group) {
        originEntryService.createEntry(inputEntry, group);
    }
    
    /**
     * The demerger process removes all the generated entries from the GL and then copies all the origin
     * entries for the given document directly into the error group. 
     * 
     * @param documentNumber
     * @param oeg
     */
    private void performDemerger(String documentNumber, OriginEntryGroup oeg) {
        originEntryService.removeScrubberDocumentEntries(
            validGroup, errorGroup, expiredGroup, documentNumber);
        
        for (Iterator entryIterator = originEntryService.getEntriesByDocument(oeg, documentNumber); entryIterator.hasNext();) {
            
            OriginEntry entry = (OriginEntry) entryIterator.next();
            originEntryService.createEntry(entry, errorGroup);
            
        }
    }

    public class ErrorEntry implements Comparable {
        private String errorMessage;
        private String errorValue;

        public ErrorEntry() {
        }

        public ErrorEntry(String errorMessage, String errorValue) {
            super();
            this.errorMessage = errorMessage;
            this.errorValue = errorValue;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public String getErrorValue() {
            return errorValue;
        }

        public void setErrorValue(String errorValue) {
            this.errorValue = errorValue;
        }

        public int compareTo(Object o) {
            // TODO Auto-generated method stub
            return 0;
        }

    }
    
}
