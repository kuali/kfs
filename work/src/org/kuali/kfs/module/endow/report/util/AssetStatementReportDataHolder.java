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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.endow.EndowConstants.IncomePrincipalIndicator;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityReportingGroup;


public class AssetStatementReportDataHolder {

    // header
    private String institution;
    private String monthEndDate;
    private String endingDate;
    // the following two are not necessary for the total report
    private String kemid;
    private String kemidLongTitle;

    // body
    private BigDecimal historyIncomeCash; // 1
    private BigDecimal historyPrincipalCash; // 1p
    // Map<report group order, Map<securityId, ReportGroupData>>
    private Map<Integer, Map<String, ReportGroupData>> reportGroupsForIncome; // 2,3,4,5,6,7 for income
    private Map<Integer, Map<String, ReportGroupData>> reportGroupsForPrincipal; // 2,3,4,5,6,7 for principal
    private BigDecimal totalIncomeMarketValues1; // 8i
    private BigDecimal totalIncomeMarketValuesN; // 9i
    private BigDecimal totalPricipalMarketValues1; // 8p
    private BigDecimal totalPrincipalMarketValuesN; // 9p

    // footer
    private EndowmentReportFooterDataHolder footer;

    public AssetStatementReportDataHolder() {
        reportGroupsForIncome = null;
        reportGroupsForPrincipal = null;
        historyIncomeCash = BigDecimal.ZERO;
        historyPrincipalCash = BigDecimal.ZERO;
        totalIncomeMarketValues1 = BigDecimal.ZERO;
        totalPricipalMarketValues1 = BigDecimal.ZERO;
        totalIncomeMarketValuesN = BigDecimal.ZERO;
        totalPrincipalMarketValuesN = BigDecimal.ZERO;
        footer = null;
    }

    public ReportGroupData createReportGroupData(SecurityReportingGroup reportingGroup, Security security, String ipInd) {
        return addReportGroupData(reportingGroup, security, ipInd.equalsIgnoreCase(IncomePrincipalIndicator.INCOME) ? reportGroupsForIncome : reportGroupsForPrincipal);
    }

    protected ReportGroupData addReportGroupData(SecurityReportingGroup reportingGroup, Security security, Map<Integer, Map<String, ReportGroupData>> reportGroups) {

        Integer reportGroupOrder = reportingGroup.getSecurityReportingGrpOrder();
        String securityId = security.getId();

        // create a new report group data
        ReportGroupData rgd = new ReportGroupData();
        rgd.setSecurityId(securityId);
        rgd.setSecurityDesc(security.getDescription());
        rgd.setReportGroupOrder(reportGroupOrder);
        rgd.setReportGroupDesc(reportingGroup.getName());

        if (reportGroups == null) {
            // a new reporting group
            reportGroups = new HashMap<Integer, Map<String, ReportGroupData>>();
        }

        if (reportGroups.containsKey(reportGroupOrder)) {
            Map<String, ReportGroupData> dataByReportGroupOrder = reportGroups.get(reportGroupOrder);
            if (!dataByReportGroupOrder.containsKey(securityId)) {
                // add the new report group data
                dataByReportGroupOrder.put(securityId, rgd);
            }
        }
        else {
            // needs both a new reporting group order and a new report group data
            Map<String, ReportGroupData> dataBySecurityId = new HashMap<String, ReportGroupData>();
            dataBySecurityId.put(securityId, rgd);
            reportGroups.put(reportGroupOrder, dataBySecurityId);
        }

        return rgd;
    }

    public BigDecimal getTotalSumOfUnits(String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForIncome.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfUnits());
            }
        } else {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForPrincipal.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfUnits());
            }
        }
        
        return total;
    }

    public BigDecimal getTotalSumOfMarketValue(String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForIncome.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfUnits());
            }
        } else {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForPrincipal.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfMarketValue());
            }
        }
        
        return total;
    }
    
    public BigDecimal getTotalSumOfEstimatedIncome(String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForIncome.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfUnits());
            }
        } else {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForPrincipal.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfEstimatedIncome());
            }
        }
        
        return total;
    }
    
    public BigDecimal getTotalSumOfRemainderOfFYEstimated(String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForIncome.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfUnits());
            }
        } else {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForPrincipal.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfRemainderOfFYEstimated());
            }
        }
        
        return total;
    }
    
    public BigDecimal getTotalSumOfNextFYEstimatedIncome(String ipInd) {
        
        BigDecimal total = BigDecimal.ZERO;
        if (IncomePrincipalIndicator.INCOME.equalsIgnoreCase(ipInd) && reportGroupsForIncome != null) {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForIncome.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfUnits());
            }
        } else {
            Map<String, ReportGroupData> reportGroupData = (Map<String, ReportGroupData>) reportGroupsForPrincipal.values();
            List<ReportGroupData> reportGroups = (List<ReportGroupData>) reportGroupData.values();
            for (ReportGroupData reportGroup : reportGroups) {
                total = total.add(reportGroup.getSumOfNextFYEstimatedIncome());
            }
        }
        
        return total;
    }
    
    public BigDecimal getHistoryIncomeCash() {
        return historyIncomeCash;
    }

    public void setHistoryIncomeCash(BigDecimal historyIncomeCash) {
        this.historyIncomeCash = historyIncomeCash;
        this.totalIncomeMarketValues1 = totalIncomeMarketValues1.add(historyIncomeCash);
    }

    public BigDecimal getHistoryPrincipalCash() {
        return historyPrincipalCash;
    }

    public void setHistoryPrincipalCash(BigDecimal historyPrincipalCash) {
        this.historyPrincipalCash = historyPrincipalCash;
        this.totalPrincipalMarketValuesN = totalPrincipalMarketValuesN.add(historyPrincipalCash);
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

    public EndowmentReportFooterDataHolder getFooter() {
        return footer;
    }

    public void setFooter(EndowmentReportFooterDataHolder footer) {
        this.footer = footer;
    }

    public Map<Integer, Map<String, ReportGroupData>> getReportGroupsForIncome() {
        return reportGroupsForIncome;
    }

    public void setReportGroupsForIncome(Map<Integer, Map<String, ReportGroupData>> reportGroupsForIncome) {
        this.reportGroupsForIncome = reportGroupsForIncome;
    }

    public Map<Integer, Map<String, ReportGroupData>> getReportGroupsForPrincipal() {
        return reportGroupsForPrincipal;
    }

    public void setReportGroupsForPrincipal(Map<Integer, Map<String, ReportGroupData>> reportGroupsForPrincipal) {
        this.reportGroupsForPrincipal = reportGroupsForPrincipal;
    }

    public BigDecimal getTotalIncomeMarketValues1() {
        return totalIncomeMarketValues1;
    }

    public void addTotalIncomeMarketValues1(BigDecimal totalIncomeMarketValues1) {
        this.totalIncomeMarketValues1 = totalIncomeMarketValues1.add(totalIncomeMarketValues1);
    }

    public BigDecimal getTotalIncomeMarketValuesN() {
        return totalIncomeMarketValuesN;
    }

    public void addTotalIncomeMarketValuesN(BigDecimal totalIncomeMarketValuesN) {
        this.totalIncomeMarketValuesN = totalIncomeMarketValuesN.add(totalIncomeMarketValuesN);
    }

    public BigDecimal getTotalPricipalMarketValues1() {
        return totalPricipalMarketValues1;
    }

    public void addTotalPricipalMarketValues1(BigDecimal totalPricipalMarketValues1) {
        this.totalPricipalMarketValues1 = totalPricipalMarketValues1.add(totalPricipalMarketValues1);
    }

    public BigDecimal getTotalPrincipalMarketValuesN() {
        return totalPrincipalMarketValuesN;
    }

    public void addTotalPrincipalMarketValuesN(BigDecimal totalPrincipalMarketValuesN) {
        this.totalPrincipalMarketValuesN = totalPrincipalMarketValuesN.add(totalPrincipalMarketValuesN);
    }

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
}
