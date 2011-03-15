/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.report.util;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeMap;

import org.kuali.kfs.module.endow.EndowConstants.IncomePrincipalIndicator;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;


public class AssetStatementReportDataHolder {

    // header
    private String institution;
    private String monthEndDate;
    private String endingDate;
    private String kemid;
    private String kemidLongTitle;

    // body
    private BigDecimal historyIncomeCash; // 1i
    private BigDecimal historyPrincipalCash; // 1p

    // Map<report group order, Map<securityId, ReportGroupData>>
    private TreeMap<Integer, TreeMap<String, ReportGroupData>> reportGroupsForIncome; // 2,3,4,5,6,7 for income
    private TreeMap<Integer, TreeMap<String, ReportGroupData>> reportGroupsForPrincipal; // 2,3,4,5,6,7 for principal

    // footer
    private EndowmentReportFooterDataHolder footer;

    public AssetStatementReportDataHolder() {
        reportGroupsForIncome = new TreeMap<Integer, TreeMap<String, ReportGroupData>>();
        reportGroupsForPrincipal = new TreeMap<Integer, TreeMap<String, ReportGroupData>>();
        historyIncomeCash = BigDecimal.ZERO;
        historyPrincipalCash = BigDecimal.ZERO;
        footer = null;
    }

    /**
     * Creates a report group data and registers it
     * 
     * @param reportingGroup
     * @param security
     * @param ipInd
     * @return
     */
    public ReportGroupData createReportGroupData(SecurityReportingGroup reportingGroup, Security security, String ipInd) {

        Integer reportGroupOrder = reportingGroup.getSecurityReportingGrpOrder();
        String securityId = security.getId();

        // create a new report group data
        ReportGroupData rgd = new ReportGroupData();
        rgd.setSecurityId(securityId);
        rgd.setSecurityDesc(security.getDescription());
        rgd.setReportGroupOrder(reportGroupOrder);
        rgd.setReportGroupDesc(reportingGroup.getName());
        
        if (ipInd.equalsIgnoreCase(IncomePrincipalIndicator.INCOME)) {
            if (reportGroupsForIncome.containsKey(reportGroupOrder)) {
                TreeMap<String, ReportGroupData> dataBySecurityId = reportGroupsForIncome.get(reportGroupOrder);
                // assume that the same securityId does not exist
                dataBySecurityId.put(securityId, rgd);
            } else {
                TreeMap<String, ReportGroupData> dataBySecurityId = new TreeMap<String, ReportGroupData>();
                dataBySecurityId.put(securityId, rgd);
                reportGroupsForIncome.put(reportGroupOrder, dataBySecurityId);
            }
        } else {
            if (reportGroupsForPrincipal.containsKey(reportGroupOrder)) {
                TreeMap<String, ReportGroupData> dataBySecurityId = reportGroupsForPrincipal.get(reportGroupOrder);
                dataBySecurityId.put(securityId, rgd);
            } else {
                TreeMap<String, ReportGroupData> dataBySecurityId = new TreeMap<String, ReportGroupData>();
                dataBySecurityId.put(securityId, rgd);
                reportGroupsForPrincipal.put(reportGroupOrder, dataBySecurityId);
            }
        }

        return rgd;
    }
       
    /**
     * Calculates the sum of units
     * 
     * @param ipInd
     * @return
     */
    public BigDecimal getTotalSumOfUnits(String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        Collection<TreeMap<String, ReportGroupData>> reportGroupMap;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            reportGroupMap = reportGroupsForIncome.values();
        } else {
            reportGroupMap = reportGroupsForPrincipal.values();
        }
        for (TreeMap<String, ReportGroupData> reportGroup : reportGroupMap) {
            Iterator<ReportGroupData> iter = reportGroup.values().iterator();
            while (iter.hasNext()) {
                total = total.add(iter.next().getSumOfUnits());
            }
        }
        
        return total;
    }

    /**
     * Calculates the sum of market values for cash and equivalents
     * 
     * @param ipInd
     * @return
     */
    public BigDecimal getTotalMarketValueForCashEquivalents(String ipInd) {
        return getTotalSumOfMarketValue(ipInd, new Integer(1));
    }

    /**
     * Calculates the sum of market values
     * 
     * @param ipInd
     * @param reportGroupOrder
     * @return
     */
    public BigDecimal getTotalSumOfMarketValue(String ipInd, Integer reportGroupOrder) {
        
        BigDecimal total = BigDecimal.ZERO;
        TreeMap<String, ReportGroupData> reportGroupMap;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            reportGroupMap = reportGroupsForIncome.get(reportGroupOrder);
        } else {
            reportGroupMap = reportGroupsForPrincipal.get(reportGroupOrder);
        }
        if (reportGroupMap != null && !reportGroupMap.isEmpty()) {
            Iterator<ReportGroupData> iter = reportGroupMap.values().iterator();
            while (iter.hasNext()) {
                total = total.add(iter.next().getSumOfMarketValue());
            }
        }
        
        return total;
    }
    
    /**
     * Calculates the sum of market values
     * 
     * @param ipInd
     * @return
     */
    public BigDecimal getTotalSumOfMarketValue(String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        Collection<TreeMap<String, ReportGroupData>> reportGroupMap;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            reportGroupMap = reportGroupsForIncome.values();
        } else {
            reportGroupMap = reportGroupsForPrincipal.values();
        }
        for (TreeMap<String, ReportGroupData> reportGroup : reportGroupMap) {
            Iterator<ReportGroupData> iter = reportGroup.values().iterator();
            while (iter.hasNext()) {
                total = total.add(iter.next().getSumOfMarketValue());
            }
        }
        
        return total;
    }
    
    /**
     * Calculates the sum of estimated income
     * 
     * @param ipInd
     * @return
     */
    public BigDecimal getTotalSumOfEstimatedIncome(String ipInd) {

        BigDecimal total = BigDecimal.ZERO;
        Collection<TreeMap<String, ReportGroupData>> reportGroupMap;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            reportGroupMap = reportGroupsForIncome.values();
        } else {
            reportGroupMap = reportGroupsForPrincipal.values();
        }
        for (TreeMap<String, ReportGroupData> reportGroup : reportGroupMap) {
            Iterator<ReportGroupData> iter = reportGroup.values().iterator();
            while (iter.hasNext()) {
                total = total.add(iter.next().getSumOfEstimatedIncome());
            }
        }
        
        return total;       
     }
    
    /**
     * Calculates the sum of remainder of FY estimated income
     * 
     * @param ipInd
     * @return
     */
    public BigDecimal getTotalSumOfRemainderOfFYEstimated(String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        Collection<TreeMap<String, ReportGroupData>> reportGroupMap;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            reportGroupMap = reportGroupsForIncome.values();
        } else {
            reportGroupMap = reportGroupsForPrincipal.values();
        }
        for (TreeMap<String, ReportGroupData> reportGroup : reportGroupMap) {
            Iterator<ReportGroupData> iter = reportGroup.values().iterator();
            while (iter.hasNext()) {
                total = total.add(iter.next().getSumOfRemainderOfFYEstimated());
            }
        }
        
        return total;
    }
    
    /**
     * Calculates the sum of next FY estimated income
     * 
     * @param ipInd
     * @return
     */
    public BigDecimal getTotalSumOfNextFYEstimatedIncome(String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        Collection<TreeMap<String, ReportGroupData>> reportGroupMap;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            reportGroupMap = reportGroupsForIncome.values();
        } else {
            reportGroupMap = reportGroupsForPrincipal.values();
        }
        for (TreeMap<String, ReportGroupData> reportGroup : reportGroupMap) {
            Iterator<ReportGroupData> iter = reportGroup.values().iterator();
            while (iter.hasNext()) {
                total = total.add(iter.next().getSumOfNextFYEstimatedIncome());
            }
        }
        
        return total;
    }

    /**
     * Report group data holder 
     */
    public class ReportGroupData {
        private Integer reportGroupOrder;
        private String reportGroupDesc;
        private String securityId;
        private String securityDesc;
        private BigDecimal sumOfUnits;
        private BigDecimal sumOfMarketValue;
        private BigDecimal sumOfEstimatedIncome;
        private BigDecimal sumOfRemainderOfFYEstimated;
        private BigDecimal sumOfNextFYEstimatedIncome;

        public ReportGroupData() {
            sumOfUnits = BigDecimal.ZERO;
            sumOfMarketValue = BigDecimal.ZERO;
            sumOfEstimatedIncome = BigDecimal.ZERO;
            sumOfRemainderOfFYEstimated = BigDecimal.ZERO;
            sumOfNextFYEstimatedIncome = BigDecimal.ZERO;
        }

        public Integer getReportGroupOrder() {
            return reportGroupOrder;
        }

        public void setReportGroupOrder(Integer reportGroupOrder) {
            this.reportGroupOrder = reportGroupOrder;
        }

        public String getReportGroupDesc() {
            return reportGroupDesc;
        }

        public void setReportGroupDesc(String reportGroupDesc) {
            this.reportGroupDesc = reportGroupDesc;
        }

        public String getSecurityId() {
            return securityId;
        }

        public void setSecurityId(String securityId) {
            this.securityId = securityId;
        }

        public String getSecurityDesc() {
            return securityDesc;
        }

        public void setSecurityDesc(String securityDesc) {
            this.securityDesc = securityDesc;
        }

        public BigDecimal getSumOfUnits() {
            return sumOfUnits;
        }

        public void addSumOfUnits(BigDecimal sumOfUnits) {
            this.sumOfUnits = this.sumOfUnits.add(sumOfUnits);
        }

        public BigDecimal getSumOfMarketValue() {
            return sumOfMarketValue;
        }

        public void addSumOfMarketValue(BigDecimal sumOfMarketValue) {
            this.sumOfMarketValue = this.sumOfMarketValue.add(sumOfMarketValue);
        }

        public BigDecimal getSumOfEstimatedIncome() {
            return sumOfEstimatedIncome;
        }

        public void addSumOfEstimatedIncome(BigDecimal sumOfEstimatedIncome) {
            this.sumOfEstimatedIncome = this.sumOfEstimatedIncome.add(sumOfEstimatedIncome);
        }

        public BigDecimal getSumOfRemainderOfFYEstimated() {
            return sumOfRemainderOfFYEstimated;
        }

        public void addSumOfRemainderOfFYEstimated(BigDecimal sumOfRemainderOfFYEstimated) {
            this.sumOfRemainderOfFYEstimated = this.sumOfRemainderOfFYEstimated.add(sumOfRemainderOfFYEstimated);
        }

        public BigDecimal getSumOfNextFYEstimatedIncome() {
            return sumOfNextFYEstimatedIncome;
        }

        public void addSumOfNextFYEstimatedIncome(BigDecimal sumOfNextFYEstimatedIncome) {
            this.sumOfNextFYEstimatedIncome = this.sumOfNextFYEstimatedIncome.add(sumOfNextFYEstimatedIncome);
        }
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getMonthEndDate() {
        return monthEndDate;
    }

    public void setMonthEndDate(String monthEndDate) {
        this.monthEndDate = monthEndDate;
    }

    public String getEndingDate() {
        return endingDate;
    }

    public void setEndingDate(String endingDate) {
        this.endingDate = endingDate;
    }

    public String getKemid() {
        return kemid;
    }

    public void setKemid(String kemid) {
        this.kemid = kemid;
    }

    public String getKemidLongTitle() {
        return kemidLongTitle;
    }

    public void setKemidLongTitle(String kemidLongTitle) {
        this.kemidLongTitle = kemidLongTitle;
    }

    public BigDecimal getHistoryIncomeCash() {
        return historyIncomeCash;
    }

    public void setHistoryIncomeCash(BigDecimal historyIncomeCash) {
        this.historyIncomeCash = historyIncomeCash;
    }

    public BigDecimal getHistoryPrincipalCash() {
        return historyPrincipalCash;
    }

    public void setHistoryPrincipalCash(BigDecimal historyPrincipalCash) {
        this.historyPrincipalCash = historyPrincipalCash;
    }

    public TreeMap<Integer, TreeMap<String, ReportGroupData>> getReportGroupsForIncome() {
        return reportGroupsForIncome;
    }

    public void setReportGroupsForIncome(TreeMap<Integer, TreeMap<String, ReportGroupData>> reportGroupsForIncome) {
        this.reportGroupsForIncome = reportGroupsForIncome;
    }

    public TreeMap<Integer, TreeMap<String, ReportGroupData>> getReportGroupsForPrincipal() {
        return reportGroupsForPrincipal;
    }

    public void setReportGroupsForPrincipal(TreeMap<Integer, TreeMap<String, ReportGroupData>> reportGroupsForPrincipal) {
        this.reportGroupsForPrincipal = reportGroupsForPrincipal;
    }

    public EndowmentReportFooterDataHolder getFooter() {
        return footer;
    }

    public void setFooter(EndowmentReportFooterDataHolder footer) {
        this.footer = footer;
    }
        
}
