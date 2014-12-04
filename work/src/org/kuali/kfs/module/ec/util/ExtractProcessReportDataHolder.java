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
package org.kuali.kfs.module.ec.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ec.businessobject.EffortCertificationReportDefinition;

/**
 * To group and hold the data presented to working reports of extract process
 */
public class ExtractProcessReportDataHolder {
    private EffortCertificationReportDefinition reportDefinition;
    private Map<String, Integer> basicStatistics;
    private List<LedgerBalanceWithMessage> ledgerBalancesWithMessage;
    
    private Map<String, Object> reportData;
    
    public final static String KEY_OF_STATISTICS_ENTRY = "statistics";
    public final static String KEY_OF_ERRORS_ENTRY = "errors";
    public final static String REPORT_YEAR = "reportYear";
    public final static String REPORT_NUMBER = "reportNumber";
    public final static String REPORT_PERIOD_BEGIN = "reportPeriodBegin";
    public final static String REPORT_PERIOD_END = "reportPeriodEnd";

    /**
     * Constructs a ExtractProcessReportDataHolder.java.
     */
    public ExtractProcessReportDataHolder() {
        this(null);
    }

    /**
     * Constructs a ExtractProcessReportDataHolder.java.
     * 
     * @param reportDefinition
     */
    public ExtractProcessReportDataHolder(EffortCertificationReportDefinition reportDefinition) {
        super();
        this.reportDefinition = reportDefinition;
        this.basicStatistics = new HashMap<String, Integer>();
        this.ledgerBalancesWithMessage = new ArrayList<LedgerBalanceWithMessage>();
        this.reportData = new HashMap<String, Object>();
    }

    /**
     * update the value of the entry with the given key. If the key exists, the value will be the sum of the given and existing
     * values; otherwise, create a new entry with the key and value.
     * 
     * @param key the given key
     * @param count the given count
     */
    public void updateBasicStatistics(String key, Integer count) {
        if (basicStatistics.containsKey(key)) {
            Integer currentCount = basicStatistics.get(key);
            count = currentCount + count;
        }
        basicStatistics.put(key, count);
    }

    /**
     * Gets the reportDefinition attribute.
     * 
     * @return Returns the reportDefinition.
     */
    public EffortCertificationReportDefinition getReportDefinition() {
        return reportDefinition;
    }

    /**
     * Sets the reportDefinition attribute value.
     * 
     * @param reportDefinition The reportDefinition to set.
     */
    public void setReportDefinition(EffortCertificationReportDefinition reportDefinition) {
        this.reportDefinition = reportDefinition;
    }

    /**
     * Gets the basicStatistics attribute.
     * 
     * @return Returns the basicStatistics.
     */
    public Map<String, Integer> getBasicStatistics() {
        return basicStatistics;
    }

    /**
     * Sets the basicStatistics attribute value.
     * 
     * @param basicStatistics The basicStatistics to set.
     */
    public void setBasicStatistics(Map<String, Integer> basicStatistics) {
        this.basicStatistics = basicStatistics;
    }

    /**
     * Gets the ledgerBalancesWithMessage attribute. 
     * @return Returns the ledgerBalancesWithMessage.
     */
    public List<LedgerBalanceWithMessage> getLedgerBalancesWithMessage() {
        return ledgerBalancesWithMessage;
    }

    /**
     * Sets the ledgerBalancesWithMessage attribute value.
     * @param ledgerBalancesWithMessage The ledgerBalancesWithMessage to set.
     */
    public void setLedgerBalancesWithMessage(List<LedgerBalanceWithMessage> ledgerBalancesWithMessage) {
        this.ledgerBalancesWithMessage = ledgerBalancesWithMessage;
    }

    /**
     * Gets the reportData attribute. 
     * @return Returns the reportData.
     */
    public Map<String, Object> getReportData() {        
        reportData.put(REPORT_YEAR, reportDefinition.getUniversityFiscalYear());
        reportData.put(REPORT_NUMBER, reportDefinition.getEffortCertificationReportNumber());
        reportData.put(REPORT_PERIOD_BEGIN, reportDefinition.getEffortCertificationReportBeginPeriodCode() + "/" + reportDefinition.getEffortCertificationReportBeginFiscalYear());
        reportData.put(REPORT_PERIOD_END, reportDefinition.getEffortCertificationReportEndPeriodCode() + "/" + reportDefinition.getEffortCertificationReportEndFiscalYear());
                
        reportData.put(KEY_OF_STATISTICS_ENTRY, basicStatistics);
        reportData.put(KEY_OF_ERRORS_ENTRY, ledgerBalancesWithMessage);
        return reportData;
    }
    
    /**
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return this.getReportData().toString();
    }
}
