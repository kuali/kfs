/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ec.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.module.ec.EffortConstants;
import org.kuali.kfs.module.ec.EffortPropertyConstants;
import org.kuali.kfs.module.ec.businessobject.EffortCertificationDetail;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.DynamicCollectionComparator.SortOrder;
import org.kuali.kfs.sys.ObjectUtil;
import org.kuali.rice.core.api.util.type.KualiDecimal;

/**
 * grouping a set of detail lines. The class is implemented to manage: summary line and delegating line.
 */
public class DetailLineGroup {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DetailLineGroup.class);

    EffortCertificationDetail summaryDetailLine;
    EffortCertificationDetail delegateDetailLine;
    List<EffortCertificationDetail> detailLines;

    /**
     * Constructs a DetailLineGroup.java.
     */
    public DetailLineGroup() {
        this(null);
    }

    /**
     * Constructs a DetailLineGroup.java.
     * 
     * @param detailLine the given detail line
     */
    public DetailLineGroup(EffortCertificationDetail newDetailLine) {
        detailLines = new ArrayList<EffortCertificationDetail>();
        summaryDetailLine = new EffortCertificationDetail();

        if (newDetailLine != null) {
            String groupId = getKeysAsString(newDetailLine);
            ObjectUtil.buildObject(summaryDetailLine, newDetailLine);
            summaryDetailLine.setGroupId(groupId);
            
            this.addNewLineIntoGroup(newDetailLine, groupId);
        }

        summaryDetailLine.setFinancialObjectCode(null);
        summaryDetailLine.setPositionNumber(null);
    }

    /**
     * update the effort percent of the delegate detail line if the effort on the summary line has been changed
     */
    public void updateDelegateDetailLineEffort() {
        Integer difference = this.getEffortPercentChanged();
        if (difference != 0) {
            Integer effortPercent = delegateDetailLine.getEffortCertificationUpdatedOverallPercent() + difference;
            delegateDetailLine.setEffortCertificationUpdatedOverallPercent(effortPercent);
        }
    }

    /**
     * update the effort percents of the detail lines if the effort on the summary line has been changed
     */
    public void updateDetailLineEffortPercent() {
        int totalDifference = this.getEffortPercentChanged();

        List<EffortCertificationDetail> detailLines = this.getDetailLines();
        DynamicCollectionComparator.sort(detailLines, SortOrder.DESC, EffortPropertyConstants.PERSISED_PAYROLL_AMOUNT);

        // restore the intial effort percents before update the detail lines
        for (EffortCertificationDetail detailLine : detailLines) {
            detailLine.setEffortCertificationUpdatedOverallPercent(detailLine.getPersistedEffortPercent());
        }

        for (EffortCertificationDetail detailLine : detailLines) {
            if (totalDifference == 0) {
                break;
            }

            int currentPercent = detailLine.getPersistedEffortPercent();
            int currentDifference = currentPercent + totalDifference;
            boolean needUpdateMultipleLines = (currentDifference < 0);

            int effortPercent = needUpdateMultipleLines ? 0 : currentDifference;
            detailLine.setEffortCertificationUpdatedOverallPercent(effortPercent);

            totalDifference = needUpdateMultipleLines ? currentDifference : 0;
        }
    }

    /**
     * update the payroll amounts of the detail lines if the payroll amount on the summary line has been changed
     */
    public void updateDetailLinePayrollAmount() {
        KualiDecimal totalDifference = this.getPayrollAmountChanged();
        if (totalDifference.isZero()) {
            return;
        }

        List<EffortCertificationDetail> detailLines = this.getDetailLines();
        DynamicCollectionComparator.sort(detailLines, SortOrder.DESC, EffortPropertyConstants.PERSISED_PAYROLL_AMOUNT);

        // restore the intial payroll amounts before update the detail lines
        for (EffortCertificationDetail detailLine : detailLines) {
            detailLine.setEffortCertificationPayrollAmount(detailLine.getPersistedPayrollAmount());
        }

        for (EffortCertificationDetail detailLine : detailLines) {
            if (totalDifference.isZero()) {
                break;
            }

            KualiDecimal currentAmount = detailLine.getPersistedPayrollAmount();
            KualiDecimal currentDifference = currentAmount.add(totalDifference);
            boolean needUpdateMultipleLines = currentDifference.isNegative();

            KualiDecimal payrollAmount = needUpdateMultipleLines ? KualiDecimal.ZERO : currentDifference;
            detailLine.setEffortCertificationPayrollAmount(payrollAmount);

            totalDifference = needUpdateMultipleLines ? currentDifference : KualiDecimal.ZERO;
        }
    }

    /**
     * group the given detail lines by the key fields
     * 
     * @param detailLines the given detail lines
     * @param keyFields the given key fields
     * @return the groups of detail lines
     */
    public static Map<String, DetailLineGroup> groupDetailLines(List<EffortCertificationDetail> detailLines) {
        Map<String, DetailLineGroup> detailLineGroupMap = new HashMap<String, DetailLineGroup>();

        for (EffortCertificationDetail line : detailLines) {
            String groupId = getKeysAsString(line);

            if (detailLineGroupMap.containsKey(groupId)) {
                DetailLineGroup group = detailLineGroupMap.get(groupId);
                group.addNewLineIntoGroup(line, groupId);
            }
            else {
                DetailLineGroup group = new DetailLineGroup(line);
                detailLineGroupMap.put(groupId, group);
            }
        }

        return detailLineGroupMap;
    }

    /**
     * concat the keys of the given detail line as a single string
     * 
     * @param line the given detail line
     * @return a single string built from the keys of the given detail line
     */
    public static String getKeysAsString(EffortCertificationDetail line) {
        return ObjectUtil.concatPropertyAsString(line, EffortConstants.DETAIL_LINES_GROUPING_FILEDS);
    }

    /**
     * get the difference between the updated effort amount and the current effort amount
     * 
     * @return the difference between the updated effort amount and the current effort amount
     */
    private Integer getEffortPercentChanged() {
        Integer currentEffortPercent = EffortCertificationDetail.getTotalPersistedEffortPercent(detailLines);
        Integer updatedEffortPercent = summaryDetailLine.getEffortCertificationUpdatedOverallPercent();

        return updatedEffortPercent - currentEffortPercent;
    }

    /**
     * get the difference between the updated payroll amount and the current payroll amount
     * 
     * @return the difference between the updated payroll amount and the current payroll amount
     */
    private KualiDecimal getPayrollAmountChanged() {
        KualiDecimal currentAmount = EffortCertificationDetail.getTotalPersistedPayrollAmount(detailLines);
        KualiDecimal updatedAmount = summaryDetailLine.getEffortCertificationPayrollAmount();

        return updatedAmount.subtract(currentAmount);
    }

    /**
     * update the group when a new detail line is added
     * 
     * @param line the new detail line
     */
    private void addNewLineIntoGroup(EffortCertificationDetail newDetailLine, String groupId) {
        if (detailLines.contains(newDetailLine)) {
            return;
        }
        
        newDetailLine.setGroupId(groupId);
        
        detailLines.add(newDetailLine);
        delegateDetailLine = this.getDetailLineWithMaxPayrollAmount(detailLines);

        this.updateSummaryDetailLineAmount();
    }

    /**
     * update the payroll amounts and effort percents based on current detail lines
     */
    private void updateSummaryDetailLineAmount() {
        Integer originalEffortPercent = EffortCertificationDetail.getTotalOriginalEffortPercent(detailLines);
        summaryDetailLine.setEffortCertificationCalculatedOverallPercent(originalEffortPercent);

        Integer effortPercent = EffortCertificationDetail.getTotalEffortPercent(detailLines);
        summaryDetailLine.setEffortCertificationUpdatedOverallPercent(effortPercent);

        Integer persistedEffortPercent = EffortCertificationDetail.getTotalPersistedEffortPercent(detailLines);
        summaryDetailLine.setPersistedEffortPercent(persistedEffortPercent);

        KualiDecimal originalPayrollAmount = EffortCertificationDetail.getTotalOriginalPayrollAmount(detailLines);
        summaryDetailLine.setEffortCertificationOriginalPayrollAmount(originalPayrollAmount);

        KualiDecimal payrollAmount = EffortCertificationDetail.getTotalPayrollAmount(detailLines);
        summaryDetailLine.setEffortCertificationPayrollAmount(payrollAmount);

        KualiDecimal persistedPayrollAmount = EffortCertificationDetail.getTotalPersistedPayrollAmount(detailLines);
        summaryDetailLine.setPersistedPayrollAmount(persistedPayrollAmount);
    }

    /**
     * find the detail lines that have max payroll amount
     * 
     * @return the detail lines that have max payroll amount
     */
    private EffortCertificationDetail getDetailLineWithMaxPayrollAmount(List<EffortCertificationDetail> detailLines) {
        KualiDecimal maxAmount = null;
        EffortCertificationDetail detailLineWithMaxPayrollAmount = null;

        for (EffortCertificationDetail line : detailLines) {
            KualiDecimal currentAmount = line.getEffortCertificationOriginalPayrollAmount();

            if (detailLineWithMaxPayrollAmount == null) {
                maxAmount = currentAmount;
                detailLineWithMaxPayrollAmount = line;
                continue;
            }

            if (maxAmount.isLessThan(currentAmount)) {
                maxAmount = currentAmount;
                detailLineWithMaxPayrollAmount = line;
            }
        }

        return detailLineWithMaxPayrollAmount;
    }

    /**
     * Gets the summaryDetailLine attribute.
     * 
     * @return Returns the summaryDetailLine.
     */
    public EffortCertificationDetail getSummaryDetailLine() {
        return summaryDetailLine;
    }

    /**
     * Sets the summaryDetailLine attribute value.
     * 
     * @param summaryDetailLine The summaryDetailLine to set.
     */
    public void setSummaryDetailLine(EffortCertificationDetail summaryDetailLine) {
        this.summaryDetailLine = summaryDetailLine;
    }

    /**
     * Gets the detailLines attribute.
     * 
     * @return Returns the detailLines.
     */
    public List<EffortCertificationDetail> getDetailLines() {
        return detailLines;
    }

    /**
     * Sets the detailLines attribute value.
     * 
     * @param detailLines The detailLines to set.
     */
    public void setDetailLines(List<EffortCertificationDetail> detailLines) {
        this.detailLines = detailLines;
    }

    /**
     * Gets the delegateDetailLine attribute.
     * 
     * @return Returns the delegateDetailLine.
     */
    public EffortCertificationDetail getDelegateDetailLine() {
        return delegateDetailLine;
    }

    /**
     * Sets the delegateDetailLine attribute value.
     * 
     * @param delegateDetailLine The delegateDetailLine to set.
     */
    public void setDelegateDetailLine(EffortCertificationDetail delegateDetailLine) {
        this.delegateDetailLine = delegateDetailLine;
    }
}
