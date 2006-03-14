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
 * @author Kuali General Ledger Team <kualigltech@oncourse.iu.edu>
 * @version $Id: ScrubberServiceImpl.java,v 1.69 2006-03-14 17:52:12 larevans Exp $
 */

public class ScrubberServiceImpl implements ScrubberService,BeanFactoryAware {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(ScrubberServiceImpl.class);
    
    // Initialize mappings for capitalization. They only need to be initialized once.
    static private Map objectSubTypeCodesToObjectCodesForCapitalization = new TreeMap();
    static {
        
//      4733  037020           WHEN 'AM'
//      4734  037030              MOVE '8615' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("AM", "8615");
        
//      4735  037040           WHEN 'AF'
//      4736  037050              MOVE '8616' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("AF", "8616");
        
//      4737  037060           WHEN 'BD'
//      4738  037070              MOVE '8601' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("BD", "8601");
        
//      4739  037080           WHEN 'BF'
//      4740  037090              MOVE '8605' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("BF", "8605");

//      4741                   WHEN 'BI'
//      4742                      MOVE '8629' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("BI", "8629");
        
//      4743  037100           WHEN 'BR'
//      4744  037110              MOVE '8601' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("BR", "8601");

//      4745  037120           WHEN 'BX'
//      4746  037130              MOVE '8640' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("BX", "8640");
        
//      4747  037140           WHEN 'BY'
//      4748  037150              MOVE '8641' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("BY", "8641");
        
//      4749  037160           WHEN 'CM'
//      4750  037170              MOVE '8610' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("CM", "8610");

//      4751  037180           WHEN 'CF'
//      4752  037190              MOVE '8611' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("CF", "8611");
        
//      4753                   WHEN 'C1'
//      4754                      MOVE '8627' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("C1", "8627");

//      4755                   WHEN 'C2'
//      4756                      MOVE '8628' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("C2", "8628");
        
//      4757                   WHEN 'C3'
//      4758                      MOVE '9607' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("C3", "9607");
        
//      4759  037200           WHEN 'ES'
//      4760  037210              MOVE '8630' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("ES", "8630");

//      4761  037220           WHEN 'IF'
//      4762  037230              MOVE '8604' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("IF", "8604");
        
//      4763  037240           WHEN 'LA'
//      4764  037250              MOVE '8603' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("LA", "8603");
        
//      4765  037260           WHEN 'LE'
//      4766  037270              MOVE '8608' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("LE", "8608");
        
//      4767  037280           WHEN 'LF'
//      4768  037290              MOVE '8614' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("LF", "8614");
        
//      4769  037300           WHEN 'LI'
//      4770  037310              MOVE '8613' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("LI", "8613");
        
//      4771  037320           WHEN 'LR'
//      4772  037330              MOVE '8665' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("LR", "8665");
        
//      4773  037340           WHEN 'UC'
//      4774  037350              MOVE '8618' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("UC", "8618");
        
//      4775  037360           WHEN 'UF'
//      4776  037370              MOVE '8619' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD        
        
        objectSubTypeCodesToObjectCodesForCapitalization.put("UF", "8619");
    }
    
    static private String[] validPlantIndebtednessSubFundGroupCodes = new String[] {
        "PFCMR", "PFRI"
    };
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
    static private String[] badUniversityFiscalPeriodCodesForCapitalization = new String[] {
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
    		if(null == documentInfo.getLastUnitOfWork() || null == documentInfo.getLastUnitOfWork().getFirstEntryOfNextUnitOfWork()) {
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
    		
    		if (unitOfWorkInfo == null) {
    		    break;      
            }
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
            
            updateAmountsForUnitOfWork(workingEntryInfo.getOriginEntry(), unitOfWorkInfo);

            // TODO (laran) figure out how and why processCostSharing and handleCostSharing are different?
            handleCostSharing(currentEntry, workingEntryInfo);

            unitOfWorkInfo.setDocumentNumber(workingEntryInfo.getOriginEntry().getFinancialDocumentNumber());

            if (workingEntryInfo.getErrors().size() > 0) {                         // Handle entries with errors
            	
        		// write this entry as a scrubber error
        		createOutputEntry(currentEntry, errorGroup);
        		batchInfo.errorRecordWritten();
        		
            } else if (workingEntryInfo.getAccount().isAccountClosedIndicator()) { // Handle entries with closed accounts
            	
    		    // write expiredEntry as expired
    		    createOutputEntry(workingEntryInfo.getOriginEntry(), expiredGroup);
    		    batchInfo.expiredAccountFound();
    		    
    		} else {                                                               // Handle valid entries
    			
    			if(!ObjectHelper.isOneOf(workingEntryInfo.getOriginEntry().getFinancialDocumentTypeCode(), 
                        new String[] {"JV", "ACLO"})) {
                    
                    // Handle capitalization processing
                    processCapitalization(currentEntry, workingEntryInfo);
                    
                    // Handle liability processing
                    processLiabilities(currentEntry, workingEntryInfo);
                    
                    // Handle plant indebtedness processing
                    processPlantIndebtedness(currentEntry, workingEntryInfo);
                    
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
        
        KualiDecimal amount = originEntry.getTransactionLedgerEntryAmount();
        KualiDecimal currentOffsetAmount = unitOfWorkInfo.getTotalOffsetAmount();
        
        if(!ObjectHelper.isNull(originEntry.getBalanceType())
                && originEntry.getBalanceType().isFinancialOffsetGenerationIndicator()
                && !"ACLO".equals(originEntry.getFinancialDocumentTypeCode())
                && !ObjectHelper.isOneOf(
                        originEntry.getUniversityFiscalPeriodCode(), 
                        invalidFiscalPeriodCodesForOffsetGeneration)) {
            
            if(originEntry.isDebit()) {
                
                unitOfWorkInfo.setTotalOffsetAmount(currentOffsetAmount.add(amount));
                
            } else {
                
                unitOfWorkInfo.setTotalOffsetAmount(currentOffsetAmount.subtract(amount));
                
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
            
            KualiDecimal currentDebitAmount = unitOfWorkInfo.getTotalDebitAmount();
            unitOfWorkInfo.setTotalDebitAmount(currentDebitAmount.add(amount));
            
        } else if ("C".equals(originEntry.getTransactionDebitCreditCode())) {
            
            KualiDecimal currentCreditAmount = unitOfWorkInfo.getTotalCreditAmount();
            unitOfWorkInfo.setTotalCreditAmount(currentCreditAmount.add(amount));
            
        }
        
	}

    /**
     * 
     * @param currentEntry
     * @param nextEntry
     * @return
     */
    private boolean isSameUnitOfWork(OriginEntry currentEntry, OriginEntry nextEntry) {
    	
        // TODO consider replacing this with ObjectUtils.equalByKeys(currentEntry, nextEntry)
        
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
    private void processCapitalization(OriginEntry originEntry, OriginEntryInfo workingEntryInfo) {//, String accountNumber, String chartCode) {
        
        OriginEntry workingEntry = workingEntryInfo.getOriginEntry();
        OriginEntry capitalizationEntry = new OriginEntry(workingEntry);
        
        BatchInfo batchInfo = 
            workingEntryInfo.getUnitOfWorkInfo().getDocumentInfo().getOriginEntryGroupInfo().getBatchInfo();
        
//            4694  036670     IF (FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD =
//            4695  036680         FSSOPT-ACT-FIN-BAL-TYP-CD)
//            4696  036690        AND PERFORM-CAP
//            4697  036700        AND UNIV-FISCAL-YR OF ALT-GLEN-RECORD > 1995
//            4698  036710        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'TF  '
//            4699  036720        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'YETF'
//            4700  036730        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AV  '
//            4701  036740        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVAC'
//            4702  036750        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVAE'
//            4703  036760        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVRC'
//            4704  036770        AND (UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'BB'
//            4705  036780        AND  UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'CB'
//            4706  036790        AND  FDOC-TYP-CD OF GLEN-RECORD NOT = 'ACLO')
//            4707  036800        AND (CAOBJT-FIN-OBJ-SUB-TYP-CD = 'AM' OR
//            4708  036810             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'AF' OR
//            4709  036820             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BD' OR
//            4710  036830             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BF' OR
//            4711                     CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BI' OR
//            4712  036840             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BR' OR
//            4713  036850             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BX' OR
//            4714  036860             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'BY' OR
//            4715  036870             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'CM' OR
//            4716  036880             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'CF' OR
//            4717                     CAOBJT-FIN-OBJ-SUB-TYP-CD = 'C1' OR
//            4718                     CAOBJT-FIN-OBJ-SUB-TYP-CD = 'C2' OR
//            4719                     CAOBJT-FIN-OBJ-SUB-TYP-CD = 'C3' OR
//            4720  036890             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'ES' OR
//            4721  036900             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'IF' OR
//            4722  036910             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LA' OR
//            4723  036920             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LE' OR
//            4724  036930             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LF' OR
//            4725  036940             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LI' OR
//            4726  036950             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'LR' OR
//            4727  036960             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'UC' OR
//            4728  036970             CAOBJT-FIN-OBJ-SUB-TYP-CD = 'UF')
//            4729  036980        AND (CAACCT-SUB-FUND-GRP-CD NOT = 'EXTAGY')
//            4730  036990        AND (FIN-COA-CD OF GLEN-RECORD NOT = 'HO')
        
        if (workingEntry.getFinancialBalanceTypeCode().equals(workingEntry.getOption().getActualFinancialBalanceTypeCd())
                && workingEntry.getUniversityFiscalYear().intValue() > 1995
                && !ObjectHelper.isOneOf(workingEntry.getFinancialDocumentTypeCode(), badDocumentTypeCodesForCapitalization)
                && !ObjectHelper.isOneOf(originEntry.getUniversityFiscalPeriodCode(), badUniversityFiscalPeriodCodesForCapitalization)
                && !"ACLO".equals(originEntry.getFinancialDocumentTypeCode())
                && !ObjectHelper.isNull(workingEntry.getFinancialObject()) && ObjectHelper.isOneOf(workingEntry
                        .getFinancialObject().getFinancialObjectSubTypeCode(), goodObjectSubTypeCodesForCapitalization)
                && !ObjectHelper.isNull(workingEntry.getAccount())
                && !"EXTAGY".equals(workingEntry.getAccount().getSubFundGroupCode())
                && !"HO".equals(originEntry.getChartOfAccountsCode())) {
        
//            4731  037000        SET FROM-CAMS TO TRUE
//            4732  037010        EVALUATE CAOBJT-FIN-OBJ-SUB-TYP-CD
//            4733  037020           WHEN 'AM'
//            4734  037030              MOVE '8615' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4735  037040           WHEN 'AF'
//            4736  037050              MOVE '8616' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4737  037060           WHEN 'BD'
//            4738  037070              MOVE '8601' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4739  037080           WHEN 'BF'
//            4740  037090              MOVE '8605' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4741                   WHEN 'BI'
//            4742                      MOVE '8629' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4743  037100           WHEN 'BR'
//            4744  037110              MOVE '8601' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4745  037120           WHEN 'BX'
//            4746  037130              MOVE '8640' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4747  037140           WHEN 'BY'
//            4748  037150              MOVE '8641' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4749  037160           WHEN 'CM'
//            4750  037170              MOVE '8610' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4751  037180           WHEN 'CF'
//            4752  037190              MOVE '8611' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4753                   WHEN 'C1'
//            4754                      MOVE '8627' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4755                   WHEN 'C2'
//            4756                      MOVE '8628' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4757                   WHEN 'C3'
//            4758                      MOVE '9607' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4759  037200           WHEN 'ES'
//            4760  037210              MOVE '8630' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4761  037220           WHEN 'IF'
//            4762  037230              MOVE '8604' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4763  037240           WHEN 'LA'
//            4764  037250              MOVE '8603' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4765  037260           WHEN 'LE'
//            4766  037270              MOVE '8608' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4767  037280           WHEN 'LF'
//            4768  037290              MOVE '8614' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4769  037300           WHEN 'LI'
//            4770  037310              MOVE '8613' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4771  037320           WHEN 'LR'
//            4772  037330              MOVE '8665' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4773  037340           WHEN 'UC'
//            4774  037350              MOVE '8618' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4775  037360           WHEN 'UF'
//            4776  037370              MOVE '8619' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
//            4777  037380        END-EVALUATE
            
            String objectSubTypeCode = workingEntry.getFinancialObject().getFinancialObjectSubTypeCode();
            if(objectSubTypeCodesToObjectCodesForCapitalization.containsKey(objectSubTypeCode)) {
                
                capitalizationEntry.setFinancialObjectCode(
                        (String) objectSubTypeCodesToObjectCodesForCapitalization.get(objectSubTypeCode));
                persistenceService.retrieveReferenceObject(capitalizationEntry, "financialObject");
                
            }
            
//            4778  037440        MOVE 'AS'
//            4779  037450          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD
            
            capitalizationEntry.setFinancialObjectTypeCode("AS");
            persistenceService.retrieveReferenceObject(capitalizationEntry, "objectType");
            
//            4780  037460        MOVE LIT-GEN-CAPITALIZATION
//            4781  037470          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD
            
            capitalizationEntry.setTransactionLedgerEntryDesc(
                    kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_CAPITALIZATION)); 
            
//            4782  037480        PERFORM 4000-PLANT-FUND-ACCT
//            4783  037490           THRU 4000-PLANT-FUND-ACCT-EXIT
            
            plantFundAccountLookup(originEntry, capitalizationEntry);
            
//            4784  037500        PERFORM 8210-WRITE-ALT-GLEN
//            4785  037510           THRU 8210-WRITE-ALT-GLEN-EXIT
            
            createOutputEntry(capitalizationEntry, validGroup);
            batchInfo.capitalizationEntryGenerated();
            
//            4786  037570          MOVE '9899'
//            4787  037580          TO FIN-OBJECT-CD  OF ALT-GLEN-RECORD
            
            capitalizationEntry.setFinancialObjectCode("9899");
            
//            4788  037600        MOVE 'FB'
//            4789  037610          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD
            
            capitalizationEntry.setFinancialObjectTypeCode("FB");
            
//            4790  037620        IF TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS = DEBIT
//            4791  037630           MOVE CREDIT
//            4792  037640             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4793  037650        ELSE
//            4794  037660           MOVE DEBIT
//            4795  037670             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4796  037680        END-IF
            
            if (workingEntry.isDebit()) {
                
                capitalizationEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                
            } else {
                
                capitalizationEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
                
            }
            
//            4797  037690        PERFORM 8210-WRITE-ALT-GLEN
//            4798  037700           THRU 8210-WRITE-ALT-GLEN-EXIT.        

            createOutputEntry(capitalizationEntry, validGroup);
            batchInfo.capitalizationEntryGenerated();
            
        }
        
    }

    /**
     * @param originEntry
     * @param workingEntryInfo
     */
    private void processPlantIndebtedness(OriginEntry originEntry, OriginEntryInfo workingEntryInfo) {
        
        OriginEntry workingEntry = workingEntryInfo.getOriginEntry();
        
//            4855  038410     IF (FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD =
//            4856  038420         FSSOPT-ACT-FIN-BAL-TYP-CD)
//            4857  038430      AND (CAACCT-SUB-FUND-GRP-CD = 'PFCMR ' OR 'PFRI  ')
//            4858  038440      AND (CAOBJT-FIN-OBJ-SUB-TYP-CD =  'PI')
//            4859  038450      AND PERFORM-PLANT
        
        if (workingEntry.getFinancialBalanceTypeCode().equals(workingEntry.getOption().getFinObjectTypeFundBalanceCd())
                && ObjectHelper.isOneOf(workingEntry.getAccount().getSubFundGroupCode(), validPlantIndebtednessSubFundGroupCodes)
                && "PI".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode())) {
        
//            4860  038460        SET FROM-PLANT-INDEBTEDNESS TO TRUE
//            4861  038470        MOVE LIT-GEN-XFER-TO-PLANT
//            4862  038480          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD
            
            workingEntry.setTransactionLedgerEntryDesc(Constants.PLANT_INDEBTEDNESS_ENTRY_DESCRIPTION);
            
//            4863  038490        IF TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS = DEBIT
//            4864  038500           MOVE CREDIT
//            4865  038510             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4866  038520        ELSE
//            4867  038530           MOVE DEBIT
//            4868  038540             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4869  038550        END-IF
            
            if (originEntry.isDebit()) {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                
            } else {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
                
            }
            
//            4870  038560        PERFORM 8210-WRITE-ALT-GLEN
//            4871  038570           THRU 8210-WRITE-ALT-GLEN-EXIT
            
            BatchInfo batchInfo = 
                workingEntryInfo.getUnitOfWorkInfo().getDocumentInfo().getOriginEntryGroupInfo().getBatchInfo();
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.plantIndebtednessEntryGenerated();
            
//            4872  038630        MOVE '9899'
//            4873  038640          TO FIN-OBJECT-CD  OF ALT-GLEN-RECORD
            
            workingEntry.setFinancialObjectCode("9899"); // FUND_BALANCE
            
//            4874  038660        MOVE 'FB'
//            4875  038670          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD
            
            workingEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE
            
//            4876  038680        MOVE TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS
//            4877  038690          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
            
            workingEntry.setTransactionDebitCreditCode(originEntry.getTransactionDebitCreditCode());
            
//            4878  038700        PERFORM 8210-WRITE-ALT-GLEN
//            4879  038710           THRU 8210-WRITE-ALT-GLEN-EXIT
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.plantIndebtednessEntryGenerated();
            
//            4880  038750        MOVE FIN-OBJECT-CD      OF WS-SAVED-FIELDS
//            4881  038760          TO FIN-OBJECT-CD      OF ALT-GLEN-RECORD
            
            workingEntry.setFinancialObjectCode(originEntry.getFinancialObjectCode());
            
//            4882  038770        MOVE FIN-OBJ-TYP-CD     OF WS-SAVED-FIELDS
//            4883  038780          TO FIN-OBJ-TYP-CD     OF ALT-GLEN-RECORD
            
            workingEntry.setFinancialObjectTypeCode(originEntry.getFinancialObjectTypeCode());
            
//            4884  038790        MOVE TRN-DEBIT-CRDT-CD  OF WS-SAVED-FIELDS
//            4885  038800          TO TRN-DEBIT-CRDT-CD  OF ALT-GLEN-RECORD
            
            workingEntry.setTransactionDebitCreditCode(originEntry.getTransactionDebitCreditCode());
            
//            4886  038810        MOVE TRN-LDGR-ENTR-DESC OF WS-SAVED-FIELDS
//            4887  038820          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD
            
            workingEntry.setTransactionLedgerEntryDesc(originEntry.getTransactionLedgerEntryDesc());
            
//            4888  038830        MOVE ACCOUNT-NBR        OF WS-SAVED-FIELDS
//            4889  038840          TO ACCOUNT-NBR        OF ALT-GLEN-RECORD
            
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            
//            4890  038850        MOVE SUB-ACCT-NBR       OF WS-SAVED-FIELDS
//            4891                  TO SUB-ACCT-NBR       OF ALT-GLEN-RECORD        
            
            workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());

            
//          5163  041480     IF CAORGN-FIN-COA-CD = CAACCT-FIN-COA-CD
//          5164  041490       AND
//          5165  041500        CAORGN-ORG-CD = CAACCT-ORG-CD
//          5166  041510       AND
//          5167  041520        ACCOUNT-NBR OF WS-SAVED-FIELDS = CAACCT-ACCOUNT-NBR
//          5168  041530       AND
//          5169  041540        FIN-COA-CD OF WS-SAVED-FIELDS = CAACCT-FIN-COA-CD
          
            if (workingEntry.getChartOfAccountsCode().equals(
                    workingEntry.getAccount().getOrganization().getChartOfAccountsCode())
                && workingEntry.getAccount().getOrganizationCode().equals(
                        workingEntry.getAccount().getOrganization().getOrganizationCode())
                && originEntry.getAccountNumber().equals(
                        workingEntry.getAccount().getAccountNumber())
                && originEntry.getChartOfAccountsCode().equals(
                        workingEntry.getAccount().getChartOfAccountsCode())) {
                    
//            4899  038970        MOVE CAORGN-CMP-PLNT-ACCT-NBR
//            4900  038980            TO ACCOUNT-NBR OF ALT-GLEN-RECORD
//            4901  038990        MOVE CAORGN-CMP-PLNT-COA-CD
//            4902  039000            TO FIN-COA-CD OF ALT-GLEN-RECORD
                    
                workingEntry.setAccountNumber(
                        workingEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                workingEntry.setChartOfAccountsCode(
                        workingEntry.getAccount().getOrganization().getCampusPlantChartCode());

//            4903  039010      ELSE
                    
            } else {
                    
//            4904  039020        MOVE FIN-COA-CD OF WS-SAVED-FIELDS
//            4905  039030          TO CAORGN-FIN-COA-CD
//            4906  039040        MOVE CAACCT-ORG-CD TO CAORGN-ORG-CD
//            4907  039050        EXEC SQL
//            4908  039060           SELECT ORG_ACTIVE_CD,
//            4909  039070                  ORG_PLNT_ACCT_NBR,
//            4910  039080                  CMP_PLNT_ACCT_NBR,
//            4911  039090                  ORG_PLNT_COA_CD,
//            4912  039100                  CMP_PLNT_COA_CD
//            4913  039110           INTO  :CAORGN-ORG-ACTIVE-CD :CAORGN-OAC-I,
//            4914  039120                 :CAORGN-ORG-PLNT-ACCT-NBR :CAORGN-OPAN-I,
//            4915  039130                 :CAORGN-CMP-PLNT-ACCT-NBR :CAORGN-CPAN-I,
//            4916  039140                 :CAORGN-ORG-PLNT-COA-CD :CAORGN-OPCC-I,
//            4917  039150                 :CAORGN-CMP-PLNT-COA-CD :CAORGN-CPCC-I
//            4918  039160           FROM  CA_ORG_T
//            4919  039170           WHERE FIN_COA_CD = RTRIM(:CAORGN-FIN-COA-CD)
//            4920  039180           AND   ORG_CD     = RTRIM(:CAORGN-ORG-CD)
//            4921  039190        END-EXEC
//            4922                 IF CAORGN-OAC-I < ZERO
//            4923                   MOVE SPACE TO CAORGN-ORG-ACTIVE-CD
//            4924                 END-IF
//            4925                 IF CAORGN-OPAN-I < ZERO
//            4926                   MOVE SPACES TO CAORGN-ORG-PLNT-ACCT-NBR
//            4927                 END-IF
//            4928                 IF CAORGN-CPAN-I < ZERO
//            4929                   MOVE SPACES TO CAORGN-CMP-PLNT-ACCT-NBR
//            4930                 END-IF
//            4931                 IF CAORGN-OPCC-I < ZERO
//            4932                   MOVE SPACES TO CAORGN-ORG-PLNT-COA-CD
//            4933                 END-IF
//            4934                 IF CAORGN-CPCC-I < ZERO
//            4935                   MOVE SPACES TO CAORGN-CMP-PLNT-COA-CD
//            4936                 END-IF
//            4937  039200        EVALUATE SQLCODE
//            4938  039210           WHEN 0
//            4939  039220                MOVE CAORGN-CMP-PLNT-ACCT-NBR TO
//            4940  039230                     ACCOUNT-NBR OF ALT-GLEN-RECORD
//            4941  039240                MOVE CAORGN-CMP-PLNT-COA-CD TO
//            4942  039250                     FIN-COA-CD OF ALT-GLEN-RECORD
//            4943  039260           WHEN +100
//            4944  039270           WHEN +1403
//            4945  039280                MOVE ALT-GLEN-RECORD (1:51) TO RP-TABLE-KEY
//            4946                        MOVE SPACES TO RP-DATA-ERROR
//            4947  039290                MOVE 'INVALID ORG CODE FOR PLANT FUND'
//            4948  039300                     TO RP-MSG-ERROR
//            4949  039310                PERFORM WRITE-ERROR-LINE THRU
//            4950  039320                        WRITE-ERROR-LINE-EXIT
//            4951  039330           WHEN OTHER
//            4952  039340                DISPLAY ' ERROR ACCESSING ORGN TABLE'
//            4953  039350                    ' SQL CODE IS ' SQLCODE
//            4954  039360                MOVE 'Y' TO WS-FATAL-ERROR-FLAG
//            4955  039370                GO TO 3000-USER-PROCESSING-EXIT
//            4956  039380     END-IF
              
            }
          
//            4957  039390        MOVE '-----' TO SUB-ACCT-NBR OF ALT-GLEN-RECORD
            
            workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);

//            4958  039400        MOVE FIN-COA-CD  OF WS-SAVED-FIELDS
//            4959  039410          TO FIN-COA-CD  OF LIT-GEN-PLANT-XFER-FROM
            
            StringBuffer litGenPlantXferFrom = new StringBuffer();
            litGenPlantXferFrom.append(originEntry.getChartOfAccountsCode());
            
//            4960  039420        MOVE ACCOUNT-NBR OF WS-SAVED-FIELDS
//            4961  039430          TO ACCOUNT-NBR OF LIT-GEN-PLANT-XFER-FROM
            
            litGenPlantXferFrom.append(originEntry.getAccountNumber());
            
//            4962  039440        MOVE                LIT-GEN-PLANT-XFER-FROM
//            4963  039450          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD
            
            litGenPlantXferFrom.append("GENERATED PLANT FUND TRANSFER");
            
            workingEntry.setTransactionLedgerEntryDesc(litGenPlantXferFrom.toString());
            
//            4964  039460        PERFORM 8210-WRITE-ALT-GLEN
//            4965  039470           THRU 8210-WRITE-ALT-GLEN-EXIT
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.plantIndebtednessEntryGenerated();

//            4966  039490        MOVE '9899'
//            4967  039500          TO FIN-OBJECT-CD  OF ALT-GLEN-RECORD
            
            workingEntry.setFinancialObjectCode("9899");
            
//            4968  039510        MOVE 'FB'
//            4969  039520          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD
            
            workingEntry.setFinancialObjectTypeCode("FB");
            
//            4970  039530        IF TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS = DEBIT
//            4971  039540           MOVE CREDIT
//            4972  039550             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4973  039560        ELSE
//            4974  039570           MOVE DEBIT
//            4975  039580             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//            4976  039590        END-IF
            
            if (workingEntry.isDebit()) {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                
            } else {
                
                workingEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
                
            }
            
//            4977  039600        PERFORM 8210-WRITE-ALT-GLEN
//            4978  039610           THRU 8210-WRITE-ALT-GLEN-EXIT
            
            createOutputEntry(workingEntry, validGroup);
            batchInfo.plantIndebtednessEntryGenerated();
            
//            4979  039620     END-IF.
//            4980  039660     MOVE FIN-OBJECT-CD      OF WS-SAVED-FIELDS
//            4981  039670       TO FIN-OBJECT-CD      OF ALT-GLEN-RECORD.
            
            workingEntry.setFinancialObjectCode(originEntry.getFinancialObjectCode());
            
//            4982  039680     MOVE FIN-OBJ-TYP-CD     OF WS-SAVED-FIELDS
//            4983  039690       TO FIN-OBJ-TYP-CD     OF ALT-GLEN-RECORD.
            
            workingEntry.setFinancialObjectTypeCode(originEntry.getFinancialObjectTypeCode());
            
//            4984  039700     MOVE TRN-DEBIT-CRDT-CD  OF WS-SAVED-FIELDS
//            4985  039710       TO TRN-DEBIT-CRDT-CD  OF ALT-GLEN-RECORD.
            
            workingEntry.setTransactionDebitCreditCode(originEntry.getTransactionDebitCreditCode());
            
//            4986  039720     MOVE TRN-LDGR-ENTR-DESC OF WS-SAVED-FIELDS
//            4987  039730       TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD.
            
            workingEntry.setTransactionLedgerEntryDesc(originEntry.getTransactionLedgerEntryDesc());
            
//            4988  039740     MOVE ACCOUNT-NBR        OF WS-SAVED-FIELDS
//            4989  039750       TO ACCOUNT-NBR        OF ALT-GLEN-RECORD.
            
            workingEntry.setAccountNumber(originEntry.getAccountNumber());
            
//            4990  039760     MOVE SUB-ACCT-NBR       OF WS-SAVED-FIELDS
//            4991  039770       TO SUB-ACCT-NBR       OF ALT-GLEN-RECORD.
          
            workingEntry.setSubAccountNumber(originEntry.getSubAccountNumber());
            
        }
        
    }

    /**
     * @param workingEntryInfo
     * @param debitOrCreditCode
     * @param accountNumber
     * @param chartCode
     */
    private void processLiabilities(OriginEntry originEntry, OriginEntryInfo workingEntryInfo) {//, String debitOrCreditCode, String accountNumber, String chartCode) {
        
        OriginEntry workingEntry = workingEntryInfo.getOriginEntry();
        OriginEntry liabilityEntry = new OriginEntry(workingEntry);
        
//            4799  037740     IF (FIN-BALANCE-TYP-CD OF ALT-GLEN-RECORD =
//            4800  037750         FSSOPT-ACT-FIN-BAL-TYP-CD)
//            4801  037760        AND PERFORM-LIAB
//            4802  037770        AND UNIV-FISCAL-YR OF ALT-GLEN-RECORD > 1995
//            4803  037780        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'TF  '
//            4804  037790        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'YETF'
//            4805  037800        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AV  '
//            4806  037810        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVAC'
//            4807  037820        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVAE'
//            4808  037830        AND FDOC-TYP-CD OF ALT-GLEN-RECORD NOT = 'AVRC'
//            4809  037840        AND (UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'BB'
//            4810  037850        AND  UNIV-FISCAL-PRD-CD OF GLEN-RECORD NOT = 'CB'
//            4811  037860        AND  FDOC-TYP-CD OF GLEN-RECORD NOT = 'ACLO')
//            4812  037870        AND CAOBJT-FIN-OBJ-SUB-TYP-CD =  'CL'
//            4813  037880        AND (CAACCT-SUB-FUND-GRP-CD NOT = 'EXTAGY')
//            4814  037890        AND (FIN-COA-CD OF GLEN-RECORD NOT = 'HO')
        
        if (workingEntry.getFinancialBalanceTypeCode().equals(workingEntry.getOption().getActualFinancialBalanceTypeCd())
                && (null != workingEntry.getUniversityFiscalYear() 
                        && workingEntry.getUniversityFiscalYear().intValue() > 1995)
                && !ObjectHelper.isOneOf(workingEntry.getFinancialDocumentTypeCode(), invalidDocumentTypesForLiabilities)
                && (!ObjectHelper.isOneOf(workingEntry.getUniversityFiscalPeriodCode(), invalidFiscalPeriodCodesForOffsetGeneration)
                        && !"ACLO".equals(workingEntry.getFinancialDocumentTypeCode()))
                && (!ObjectHelper.isNull(workingEntry.getFinancialObject())
                        && "CL".equals(workingEntry.getFinancialObject().getFinancialObjectSubTypeCode()))
                && (!ObjectHelper.isNull(workingEntry.getAccount()) 
                        && !"EXTAGY".equals(workingEntry.getAccount().getSubFundGroupCode()))
                && !"HO".equals(originEntry.getChartOfAccountsCode())) {
            
//          4815  037900        MOVE '9603' TO FIN-OBJECT-CD OF ALT-GLEN-RECORD
            
            liabilityEntry.setFinancialObjectCode("9603"); // NOTES_PAYABLE_CAPITAL_LEASE

//          4816  037910        SET FROM-LIAB TO TRUE
//          4817  037920        MOVE 'LI'
//          4818  037930          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD
            
            liabilityEntry.setFinancialObjectTypeCode("LI"); // LIABILITY

//          4819  037940        MOVE TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS
//          4820  037950          TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
            
            liabilityEntry.setTransactionDebitCreditCode(originEntry.getTransactionDebitCreditCode());
            
//          4821  037960        MOVE LIT-GEN-LIABILITY
//          4822  037970          TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD
            
            liabilityEntry.setTransactionLedgerEntryDesc(kualiConfigurationService.getPropertyString(KeyConstants.MSG_GENERATED_LIABILITY));
            
//          4823  037980        PERFORM 4000-PLANT-FUND-ACCT
//          4824  037990           THRU 4000-PLANT-FUND-ACCT-EXIT
            
            plantFundAccountLookup(originEntry, liabilityEntry);
            
//          4825  038000        PERFORM 8210-WRITE-ALT-GLEN
//          4826  038010           THRU 8210-WRITE-ALT-GLEN-EXIT
            
            BatchInfo batchInfo = 
                workingEntryInfo.getUnitOfWorkInfo().getDocumentInfo().getOriginEntryGroupInfo().getBatchInfo();

            createOutputEntry(liabilityEntry, validGroup);
            batchInfo.liabilityEntryGenerated();
            
            // ... and now generate the other half of the liability entry
            
//          4827  038020         MOVE '9899'
//          4828  038030          TO FIN-OBJECT-CD  OF ALT-GLEN-RECORD
            
            liabilityEntry.setFinancialObjectCode("9899"); // FUND_BALANCE
            
//          4829  038040        MOVE 'FB'
//          4830  038050          TO FIN-OBJ-TYP-CD OF ALT-GLEN-RECORD
            
            liabilityEntry.setFinancialObjectTypeCode("FB"); // FUND_BALANCE
            
//          4831  038060        IF TRN-DEBIT-CRDT-CD OF WS-SAVED-FIELDS = DEBIT
//          4832  038070           MOVE CREDIT
//          4833  038080             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//          4834  038090        ELSE
//          4835  038100           MOVE DEBIT
//          4836  038110             TO TRN-DEBIT-CRDT-CD OF ALT-GLEN-RECORD
//          4837  038120        END-IF

            if (liabilityEntry.isDebit()) {
                
                liabilityEntry.setTransactionDebitCreditCode(Constants.GL_CREDIT_CODE);
                
            } else {
                
                liabilityEntry.setTransactionDebitCreditCode(Constants.GL_DEBIT_CODE);
                
            }
            
//          4838  038130        PERFORM 8210-WRITE-ALT-GLEN
//          4839  038140           THRU 8210-WRITE-ALT-GLEN-EXIT
            
            createOutputEntry(liabilityEntry, validGroup);
            batchInfo.liabilityEntryGenerated();
            
        }

//      4840  038150     ELSE
//      4841  038160        NEXT SENTENCE
//      4842  038170     END-IF.        
        
    }

    /**
     * @param workingEntry
     * @param chartCode
     * @param accountNumber
     */
    private void plantFundAccountLookup(OriginEntry originEntry, OriginEntry workingEntry) {
        
//        5162  041470     MOVE '-----' TO SUB-ACCT-NBR OF ALT-GLEN-RECORD.
        
        workingEntry.setSubAccountNumber(Constants.DASHES_SUB_ACCOUNT_NUMBER);

//        5163  041480     IF CAORGN-FIN-COA-CD = CAACCT-FIN-COA-CD
//        5164  041490       AND
//        5165  041500        CAORGN-ORG-CD = CAACCT-ORG-CD
//        5166  041510       AND
//        5167  041520        ACCOUNT-NBR OF WS-SAVED-FIELDS = CAACCT-ACCOUNT-NBR
//        5168  041530       AND
//        5169  041540        FIN-COA-CD OF WS-SAVED-FIELDS = CAACCT-FIN-COA-CD
        
        persistenceService.retrieveReferenceObject(workingEntry, "account");
        
        if (workingEntry.getChartOfAccountsCode().equals(
                workingEntry.getAccount().getOrganization().getChartOfAccountsCode())
            && workingEntry.getAccount().getOrganizationCode().equals(
                    workingEntry.getAccount().getOrganization().getOrganizationCode())
            && originEntry.getAccountNumber().equals(
                    workingEntry.getAccount().getAccountNumber())
            && originEntry.getChartOfAccountsCode().equals(
                    workingEntry.getAccount().getChartOfAccountsCode())) {
        
//        5170  041550      EVALUATE CAOBJT-FIN-OBJ-SUB-TYP-CD
//        5171  041560       WHEN 'AM'
//        5172  041570       WHEN 'AF'
//        5173  041580       WHEN 'BD'
//        5174  041590       WHEN 'BF'
//        5175               WHEN 'BI'
//        5176  041600       WHEN 'BR'
//        5177  041610       WHEN 'BX'
//        5178  041620       WHEN 'BY'
//        5179  041630       WHEN 'IF'
//        5180  041640       WHEN 'LA'
//        5181  041650       WHEN 'LE'
//        5182  041660       WHEN 'LF'
//        5183  041670       WHEN 'LI'
//        5184  041680       WHEN 'LR'
//        5185  041690        MOVE CAORGN-CMP-PLNT-ACCT-NBR
//        5186  041700          TO ACCOUNT-NBR OF ALT-GLEN-RECORD
//        5187  041710        MOVE CAORGN-CMP-PLNT-COA-CD
//        5188  041720          TO FIN-COA-CD OF ALT-GLEN-RECORD
//        5189  041730       WHEN 'CL'
//        5190  041740       WHEN 'CM'
//        5191  041750       WHEN 'CF'
//        5192               WHEN 'C1'
//        5193               WHEN 'C2'
//        5194               WHEN 'C3'
//        5195  041760       WHEN 'ES'
//        5196  041770       WHEN 'UC'
//        5197  041780       WHEN 'UF'
//        5198  041790        MOVE CAORGN-ORG-PLNT-ACCT-NBR
//        5199  041800          TO ACCOUNT-NBR OF ALT-GLEN-RECORD
//        5200  041810        MOVE CAORGN-ORG-PLNT-COA-CD
//        5201  041820          TO FIN-COA-CD OF ALT-GLEN-RECORD
//        5202  041830      END-EVALUATE        
            
            persistenceService.retrieveReferenceObject(workingEntry, "financialObject");
            persistenceService.retrieveReferenceObject(workingEntry.getAccount(), "organization");
            
            String objectSubTypeCode = originEntry.getFinancialObject().getFinancialObjectSubTypeCode();
            
            if (ObjectHelper.isOneOf(objectSubTypeCode, campusObjectSubTypeCodesForPlantFundAccountLookups)) {
                
                workingEntry.setAccountNumber(workingEntry.getAccount().getOrganization().getCampusPlantAccountNumber());
                workingEntry.setChartOfAccountsCode(workingEntry.getAccount().getOrganization().getCampusPlantChartCode());
                
                persistenceService.retrieveReferenceObject(workingEntry, "account");
                persistenceService.retrieveReferenceObject(workingEntry, "chart");
                
            } else if (ObjectHelper.isOneOf(objectSubTypeCode, organizationObjectSubTypeCodesForPlantFundAccountLookups)) {
                
                workingEntry.setAccountNumber(workingEntry.getAccount().getOrganization().getOrganizationPlantAccountNumber());
                workingEntry.setChartOfAccountsCode(workingEntry.getAccount().getOrganization().getOrganizationPlantChartCode());
                
                persistenceService.retrieveReferenceObject(workingEntry, "account");
                persistenceService.retrieveReferenceObject(workingEntry, "chart");
            }
            
        }
        
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
    	
        OriginEntry offsetEntry = new OriginEntry(workingEntry);

        // Temporary storage for any errors.
        ArrayList errors = new ArrayList();
        
//        3970  029760 3000-OFFSET.
//        3971  029840     MOVE OFFSET-DESCRIPTION
//        3972  029850       TO TRN-LDGR-ENTR-DESC OF ALT-GLEN-RECORD.
        
        offsetEntry.setTransactionLedgerEntryDesc("GENERATED OFFSET");
        
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
//        3999  030090     EVALUATE SQLCODE
//        4000  030100          WHEN 0
        
        if(!ObjectHelper.isNull(offsetDefinition)) {

//        4001  030110              MOVE GLOFSD-FIN-OBJECT-CD TO
//        4002  030120                   FIN-OBJECT-CD OF ALT-GLEN-RECORD
            
            offsetEntry.setFinancialObject(offsetDefinition.getFinancialObject());
            offsetEntry.setFinancialObjectCode(offsetDefinition.getFinancialObjectCode());

//        4003  030130              IF GLOFSD-FIN-SUB-OBJ-CD = SPACES
//        4004  030140                   MOVE FIN-SUB-OBJ-CD-DASHES
//        4005  030150                       TO FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD

            if(ObjectHelper.isNull(offsetDefinition.getFinancialSubObjectCode())) {
                
                offsetEntry.setFinancialSubObject(null);
                offsetEntry.setFinancialSubObjectCode(Constants.DASHES_SUB_OBJECT_CODE);
            
//        4006  030160              ELSE
//        4007  030170                   MOVE GLOFSD-FIN-SUB-OBJ-CD TO
//        4008  030180                          FIN-SUB-OBJ-CD OF ALT-GLEN-RECORD
//        4009  030190              END-IF

            } else {
                
                offsetEntry.setFinancialSubObject(workingEntry.getFinancialSubObject());
                offsetEntry.setFinancialSubObjectCode(workingEntry.getFinancialSubObjectCode());
                
            }

        } else {

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
        
            StringBuffer sb = new StringBuffer();
            sb.append("Fiscal Year: ").append(workingEntry.getUniversityFiscalYear());
            sb.append(", Chart of Accounts: ").append(workingEntry.getChartOfAccountsCode());
            sb.append(", Document Type: ").append(workingEntry.getFinancialDocumentTypeCode());
            sb.append(", Balance Type: ").append(workingEntry.getFinancialBalanceTypeCode());
            
            ScrubberServiceErrorHandler.addTransactionError(
                    kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_NOT_FOUND),
                    sb.toString(), errors);
            
//          4022  030310     END-EVALUATE
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
        
        if(ObjectHelper.isNull(offsetEntry.getFinancialObject())) {
            
            persistenceService.retrieveReferenceObject(offsetEntry, "financialObject");
            
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
        
        if(null != offsetEntry.getFinancialObject()) {
            
            offsetEntry.setFinancialObjectTypeCode(
                    offsetEntry.getFinancialObject().getFinancialObjectTypeCode());
            
        } else {
        
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
            
            ScrubberServiceErrorHandler.addTransactionError(
                    kualiConfigurationService.getPropertyString(KeyConstants.ERROR_OFFSET_DEFINITION_OBJECT_CODE_NOT_FOUND),
                    workingEntry.getFinancialObjectCode(), errors);
            
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
        
        offsetEntry.setOrganizationDocumentNumber(null);
        offsetEntry.setOrganizationReferenceId(null);
        offsetEntry.setReferenceFinDocumentTypeCode(null);
        offsetEntry.setReferenceDocumentType(null);
        offsetEntry.setFinSystemRefOriginationCode(null);
        offsetEntry.setFinancialDocumentReferenceNbr(null);
        offsetEntry.setTransactionEncumbranceUpdtCd(null);
        
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
