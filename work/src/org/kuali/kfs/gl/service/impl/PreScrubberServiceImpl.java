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
package org.kuali.kfs.gl.service.impl;

import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeSet;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.coa.businessobject.Account;
import org.kuali.kfs.coa.service.AccountService;
import org.kuali.kfs.gl.GeneralLedgerConstants;
import org.kuali.kfs.gl.report.PreScrubberReportData;
import org.kuali.kfs.gl.service.PreScrubberService;
import org.kuali.kfs.sys.KFSConstants.SystemGroupParameterNames;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.impl.KfsParameterConstants;
import org.kuali.kfs.sys.util.TransactionalServiceUtils;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;

/**
 * This class assumes that an account number may only belong to one chart code (i.e. as if the account number were the only primary key column of the account table)
 * Based on that assumption, this code will attempt to fill in the chart code for an origin entry if it is blank and the account number is valid
 */
public class PreScrubberServiceImpl implements PreScrubberService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreScrubberServiceImpl.class);
    
    private int maxCacheSize = 10000;
    private ParameterService parameterService;
    
    public PreScrubberReportData preprocessOriginEntries(Iterator<String> inputOriginEntries, String outputFileName) throws IOException {
        PrintStream outputStream = new PrintStream(outputFileName);
        
        Map<String, String> chartCodeCache = new LinkedHashMap<String, String>() {
            @Override
            protected boolean removeEldestEntry(Entry<String, String> eldest) {
                return size() > getMaxCacheSize();
            }
        };
        
        Set<String> nonExistentAccountCache = new TreeSet<String>();
        Set<String> multipleAccountCache = new TreeSet<String>();
        
        AccountService accountService = SpringContext.getBean(AccountService.class);
        ParameterService parameterService = SpringContext.getBean(ParameterService.class);
        boolean fillInChartCodesIfSpaces = deriveChartOfAccountsCodeIfSpaces();
        
        int inputLines = 0;
        int outputLines = 0;
        
        try {
            while (inputOriginEntries.hasNext()) {
                inputLines++;
                
                String originEntry = inputOriginEntries.next();
                String outputLine = originEntry;
                if (fillInChartCodesIfSpaces && originEntry.length() >= getExclusiveAccountNumberEndPosition()) {
                    String chartOfAccountsCode = originEntry.substring(getInclusiveChartOfAccountsCodeStartPosition(), getExclusiveChartOfAccountsCodeEndPosition());
                    if (GeneralLedgerConstants.getSpaceChartOfAccountsCode().equals(chartOfAccountsCode)) {
                        // blank chart code... try to find the chart code
                        String accountNumber = originEntry.substring(getInclusiveAccountNumberStartPosition(), getExclusiveAccountNumberEndPosition());
                        if (StringUtils.isNotEmpty(accountNumber)) {
                            String replacementChartOfAccountsCode = null;
                            boolean nonExistent = false;
                            boolean multipleFound = false;
                            
                            if (chartCodeCache.containsKey(accountNumber))
                                replacementChartOfAccountsCode = chartCodeCache.get(accountNumber);
                            else if (nonExistentAccountCache.contains(accountNumber))
                                nonExistent = true;
                            else if (multipleAccountCache.contains(accountNumber))
                                multipleFound = true;
                            else {
                                Collection<Account> results = accountService.getAccountsForAccountNumber(accountNumber);
                                
                                if (results.isEmpty()) {
                                    nonExistent = true;
                                    nonExistentAccountCache.add(accountNumber);
                                    LOG.warn("Could not find account record for account number " + accountNumber);
                                }
                                else {
                                    Iterator<Account> accounts = results.iterator();
                                    Account account = accounts.next();
                                    if (accounts.hasNext()) {
                                        LOG.warn("Multiple chart codes found for account number " + accountNumber + ", not filling in chart code for this account");
                                        TransactionalServiceUtils.exhaustIterator(accounts);
                                        multipleAccountCache.add(accountNumber);
                                        multipleFound = true;
                                    }
                                    else {
                                        replacementChartOfAccountsCode = account.getChartOfAccountsCode();
                                        chartCodeCache.put(accountNumber, replacementChartOfAccountsCode);
                                    }
                                }
                            }
                            
                            if (!nonExistent && !multipleFound) {
                                StringBuilder buf = new StringBuilder(originEntry.length());
                                buf.append(originEntry.substring(0, getInclusiveChartOfAccountsCodeStartPosition()));
                                buf.append(replacementChartOfAccountsCode);
                                buf.append(originEntry.subSequence(getExclusiveChartOfAccountsCodeEndPosition(), originEntry.length()));
                                outputLine = buf.toString();
                            }
                        }
                    }
                }
                outputStream.printf("%s\n", outputLine);
                outputLines++;
            }
        }
        finally {
            outputStream.close();
        }
        return new PreScrubberReportData(inputLines, outputLines, nonExistentAccountCache, multipleAccountCache);
    }
    
    /**
     * Returns the position of the chart of accounts code on an origin entry line
     * @return
     */
    protected int getInclusiveChartOfAccountsCodeStartPosition() {
        return 4;
    }
    
    /**
     * Returns the position of the end of the chart of accounts code on an origin entry line,   
     * @return
     */
    protected int getExclusiveChartOfAccountsCodeEndPosition() {
        return 6;
    }
    
    /**
     * Returns the position of the chart of accounts code on an origin entry line
     * @return
     */
    protected int getInclusiveAccountNumberStartPosition() {
        return 6;
    }
    
    /**
     * Returns the position of the end of the chart of accounts code on an origin entry line,   
     * @return
     */
    protected int getExclusiveAccountNumberEndPosition() {
        return 13;
    }
    
    public int getMaxCacheSize() {
        return maxCacheSize;
    }

    public void setMaxCacheSize(int maxCacheSize) {
        this.maxCacheSize = maxCacheSize;
    }
    
    /**
     * @return
     */
    public boolean deriveChartOfAccountsCodeIfSpaces() {
        return !parameterService.getParameterValueAsBoolean(KfsParameterConstants.FINANCIAL_SYSTEM_ALL.class, SystemGroupParameterNames.ACCOUNTS_CAN_CROSS_CHARTS_IND);
    }

    /**
     * Sets the parameterService attribute value.
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
}
