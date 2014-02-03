/*
 * Copyright 2010 The Kuali Foundation
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
package org.kuali.kfs.module.tem.service.impl;

import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.log4j.Logger;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.tem.TemConstants.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.AccountingDistribution;
import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.TravelReimbursementDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingLineDistributionKey;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.util.AccountingDistributionComparator;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.businessobject.SourceAccountingLine;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * Accounting Distribution Service Implementation
 */
/**
 * This class...
 */
public class AccountingDistributionServiceImpl implements AccountingDistributionService {

    protected static Logger LOG = Logger.getLogger(AccountingDistributionServiceImpl.class);

    private BusinessObjectService businessObjectService;
    private ObjectCodeService objectCodeService;
    private TravelDocumentService travelDocumentService;
    private ParameterService parameterService;

    @SuppressWarnings("deprecation")
    @Override
    public List<TemSourceAccountingLine> distributionToSouceAccountingLines(List<TemDistributionAccountingLine> distributionAccountingLines, List<AccountingDistribution> accountingDistributionList, KualiDecimal sourceAccountingLinesTotal, KualiDecimal expenseLimit){
        List<TemSourceAccountingLine> sourceAccountingList = new ArrayList<TemSourceAccountingLine>();
        Map<String, AccountingDistribution> distributionMap = new HashMap<String, AccountingDistribution>();
        KualiDecimal total = KualiDecimal.ZERO;
        int distributionTargetCount = 0;
        boolean useExpenseLimit = false;
        for (AccountingDistribution accountDistribution: accountingDistributionList){
            if (accountDistribution.getSelected()){
                total = total.add(accountDistribution.getRemainingAmount());
                distributionTargetCount += 1;
            }
        }

        if (expenseLimit != null && expenseLimit.isPositive()) {
            KualiDecimal expenseLimitTotal = new KualiDecimal(expenseLimit.bigDecimalValue());
            // do we have any accounting line amount to subtract from the expense limit?
            if (sourceAccountingLinesTotal != null && sourceAccountingLinesTotal.isGreaterThan(KualiDecimal.ZERO)) {
                expenseLimitTotal = expenseLimitTotal.subtract(sourceAccountingLinesTotal);
            }
            if (expenseLimitTotal.isLessThan(total)) {
                total = expenseLimitTotal;
                useExpenseLimit = true;
            }
        }

        if (total.isGreaterThan(KualiDecimal.ZERO)) {
            for (AccountingDistribution accountingDistribution : accountingDistributionList){
                List<TemSourceAccountingLine> tempSourceAccountingList = new ArrayList<TemSourceAccountingLine>();
                if (accountingDistribution.getSelected()){
                    for (TemDistributionAccountingLine accountingLine : distributionAccountingLines){
                        TemSourceAccountingLine newLine = new TemSourceAccountingLine();
                        try {
                            BeanUtils.copyProperties(newLine, accountingLine);
                        }
                        catch (IllegalAccessException ex) {
                            ex.printStackTrace();
                        }
                        catch (InvocationTargetException ex) {
                            ex.printStackTrace();
                        }
                        BigDecimal distributionAmount = (distributionTargetCount > 1) ? accountingDistribution.getRemainingAmount().bigDecimalValue() : total.bigDecimalValue();
                        BigDecimal product = accountingLine.getAccountLinePercent().multiply(distributionAmount);
                        product = product.divide(new BigDecimal(100),5,RoundingMode.HALF_UP);
                        BigDecimal lineAmount = product.divide(total.bigDecimalValue(),5,RoundingMode.HALF_UP);

                        newLine.setAmount(new KualiDecimal(product));
                        newLine.setCardType(accountingDistribution.getCardType());
                        Map<String,Object> fieldValues = new HashMap<String,Object>();
                        fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accountingDistribution.getObjectCode());
                        fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, newLine.getChartOfAccountsCode());
                        fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
                        ObjectCode objCode = getBusinessObjectService().findByPrimaryKey(ObjectCode.class, fieldValues);
                        newLine.setObjectCode(objCode);
                        newLine.setFinancialObjectCode(accountingDistribution.getObjectCode());
                        tempSourceAccountingList.add(newLine);
                    }
                    if (useExpenseLimit) {
                        sourceAccountingList.addAll(tempSourceAccountingList); //we just adjusted the accounting lines for the expense...let's not readjust
                    } else {
                        sourceAccountingList.addAll(adjustValues(tempSourceAccountingList,accountingDistribution.getRemainingAmount()));
                    }
                }
            }
        }
        return sourceAccountingList;
    }

    /**
     * This method will adjust the last accounting line to make the amounts sum up to the total.
     *
     * @param sourceAccountingList
     * @param total
     * @return
     */
    private List<TemSourceAccountingLine> adjustValues(List<TemSourceAccountingLine> sourceAccountingList, KualiDecimal total) {
        KualiDecimal totalAmount = KualiDecimal.ZERO;
        for (TemSourceAccountingLine newLine : sourceAccountingList) {
            totalAmount = totalAmount.add(newLine.getAmount());
        }
        TemSourceAccountingLine line = sourceAccountingList.get(sourceAccountingList.size() - 1);
        KualiDecimal remainderAmount = total.subtract(totalAmount);
        if (remainderAmount.isPositive()) {
            line.setAmount(line.getAmount().subtract(remainderAmount));
        }
        else if (remainderAmount.isNegative()) {
            line.setAmount(line.getAmount().add(remainderAmount));
        }

        return sourceAccountingList;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.AccountingDistributionService#distributionToDistributionAccountingLine(java.util.List)
     */
    @Override
    public TemDistributionAccountingLine distributionToDistributionAccountingLine(List<AccountingDistribution> accountingDistributionList) {
        KualiDecimal distributionTotal = KualiDecimal.ZERO;
        for (AccountingDistribution accountingDistribution : accountingDistributionList) {
            if (accountingDistribution.getSelected()) {
                distributionTotal = distributionTotal.add(accountingDistribution.getRemainingAmount());
            }
        }
        TemDistributionAccountingLine newLine = new TemDistributionAccountingLine();
        newLine.setAmount(distributionTotal);
        return newLine;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.AccountingDistributionService#createDistributions(org.kuali.kfs.module.tem.document.TravelDocument)
     */
    @Override
    public List<AccountingDistribution> createDistributions(TravelDocument travelDocument) {
        List<AccountingDistribution> documentDistribution = new ArrayList<AccountingDistribution>();
        Map<String, AccountingDistribution> distributionMap = new HashMap<String, AccountingDistribution>();

        for (ExpenseType expense : EnumSet.allOf(ExpenseType.class)){
            Map<String, AccountingDistribution> newDistributionMap = getTravelExpenseService().getExpenseServiceByType(expense).getAccountingDistribution(travelDocument);
            addMergeDistributionMap(distributionMap, newDistributionMap);
        }
        subtractMergeDistributionMap(distributionMap, accountingLinesToDistributionMap(travelDocument));

        for (String distribution : distributionMap.keySet()){
            if (!distributionMap.get(distribution).getSubTotal().equals(KualiDecimal.ZERO)) {  // don't include distributions of 0.00
                documentDistribution.add(distributionMap.get(distribution));
            }
        }
        Collections.sort(documentDistribution, new AccountingDistributionComparator());

        return documentDistribution;
    }

    protected Map<String, AccountingDistribution> accountingLinesToDistributionMap(TravelDocument travelDocument) {
        Map<String, AccountingDistribution> distributionMap = new HashMap<String, AccountingDistribution>();
        for (TemSourceAccountingLine accountingLine : (List<TemSourceAccountingLine>) travelDocument.getSourceAccountingLines()) {
            AccountingDistribution distribution = null;
            String key = accountingLine.getObjectCode().getCode() + "-" + accountingLine.getCardType();
            if (distributionMap.containsKey(key)) {
                distributionMap.get(key).setSubTotal(distributionMap.get(key).getSubTotal().add(accountingLine.getAmount()));
            }
            else {
                distribution = new AccountingDistribution();
                distribution.setObjectCode(accountingLine.getObjectCode().getCode());
                distribution.setSubTotal(accountingLine.getAmount());
                distributionMap.put(key, distribution);
            }
        }

        return distributionMap;
    }

    protected void addMergeDistributionMap(Map<String, AccountingDistribution> destinationMap, Map<String, AccountingDistribution> originMap) {
        for (String key : originMap.keySet()) {
            if (destinationMap.containsKey(key)) {
                destinationMap.get(key).setSubTotal(destinationMap.get(key).getSubTotal().add(originMap.get(key).getSubTotal()));
                destinationMap.get(key).setRemainingAmount(destinationMap.get(key).getRemainingAmount().add(originMap.get(key).getRemainingAmount()));
            }
            else {
                destinationMap.put(key, originMap.get(key));
            }
        }
    }

    protected void subtractMergeDistributionMap(Map<String, AccountingDistribution> destinationMap, Map<String, AccountingDistribution> originMap) {
        Iterator<String> it = originMap.keySet().iterator();
        while (it.hasNext()) {
            String key = it.next();
            if (destinationMap.containsKey(key)) {
                destinationMap.get(key).setRemainingAmount(destinationMap.get(key).getRemainingAmount().subtract(originMap.get(key).getSubTotal()));
            }
        }
    }

    public String getObjectCodeFrom(final TravelDocument travelDocument, String paramName) {
        final String parameterValue = getParameterService().getParameterValueAsString(TravelReimbursementDocument.class, paramName);
        String paramSearchStr = "";
        TravelerDetail traveler = travelDocument.getTraveler();
        if(traveler != null){
            paramSearchStr += traveler.getTravelerTypeCode() + "=";
        }
        TripType tripType = travelDocument.getTripType();
        if(tripType != null){
            paramSearchStr += tripType.getCode() + "=";
        }

        final int searchIdx = parameterValue.indexOf(paramSearchStr);

        if (searchIdx == -1) {
            return null;
        }

        final int endIdx = parameterValue.indexOf(";", searchIdx);
        if (endIdx == -1) {
            return parameterValue.substring(searchIdx + paramSearchStr.length());
        }

        return parameterValue.substring(searchIdx + paramSearchStr.length(), endIdx);
    }

    protected AccountingDistribution retrieveDistributionFor(final List<AccountingDistribution> distros, ObjectCode objectCode) {
        if (objectCode != null) {
            AccountingDistribution retval = retrieveDistributionFor(distros, objectCode.getCode());
            retval.setObjectCode(objectCode.getCode());
            retval.setObjectCodeName(objectCode.getName());

            return retval;
        }

        return new AccountingDistribution();
    }

    protected AccountingDistribution retrieveDistributionFor(final List<AccountingDistribution> distros, final String code) {
        if (distros != null && code != null) {
            for (final AccountingDistribution distribution : distros) {
                LOG.debug("comparing " + code + " to " + distribution.getObjectCode());
                if (distribution.getObjectCode().equals(code)) {
                    return distribution;
                }
            }
        }

        return new AccountingDistribution();
    }

    /**
     * @see org.kuali.kfs.module.tem.document.service.AccountingDistributionService#buildDistribution(TravelDocument)
     */
    @Override
    public List<AccountingDistribution> buildDistributionFrom(final TravelDocument travelDocument) {
        List<AccountingDistribution> distributions = createDistributions(travelDocument);
        return distributions;
    }

    @Override
    public KualiDecimal getTotalAmount(List<TemDistributionAccountingLine> lines){
        KualiDecimal total = KualiDecimal.ZERO;

        for(TemDistributionAccountingLine line : lines){
            total = total.add(line.getAmount());
        }
        return total;
    }

    @Override
    public BigDecimal getTotalPercent(List<TemDistributionAccountingLine> lines){
        BigDecimal total = new BigDecimal(0);

        for(TemDistributionAccountingLine line : lines){
            total = total.add(line.getAccountLinePercent());
        }
        return total;
    }

    /**
     * @see org.kuali.kfs.module.tem.service.AccountingDistributionService#calculateAccountingLineDistributionPercent(java.util.List)
     */
    @Override
    public Map<AccountingLineDistributionKey, KualiDecimal> calculateAccountingLineDistributionPercent(List<SourceAccountingLine> accountingLine){
        Map<AccountingLineDistributionKey, KualiDecimal> distributionMap = new HashMap<AccountingLineDistributionKey, KualiDecimal>();

        //calculate the total from the accounting line
        KualiDecimal total = KualiDecimal.ZERO;
        for (SourceAccountingLine sourceLine : accountingLine){
            total = total.add(sourceLine.getAmount());
        }

        //calculate the distribution on each of the accounting line distribution keys
        AccountingLineDistributionKey key;
        KualiDecimal factor;
        for (SourceAccountingLine sourceLine : accountingLine){
            key = new AccountingLineDistributionKey(sourceLine);

            factor = sourceLine.getAmount().divide(total);
            if (distributionMap.containsKey(key)){
                //accumulate the stored percentage to the calculated; though this is probably very unlikely
                factor = distributionMap.get(key).add(factor);
            }
            //store the final distribution to the map
            distributionMap.put(key, factor);
        }

        return distributionMap;
    }

    /**
     * Gets the parameterService attribute.
     *
     * @return Returns the parameterService.
     */
    public ParameterService getParameterService() {
        return parameterService;
    }

    /**
     * Sets the parameterService attribute value.
     *
     * @param parameterService The parameterService to set.
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }

    public void setTravelDocumentService(final TravelDocumentService travelDocumentService) {
        this.travelDocumentService = travelDocumentService;
    }

    protected TravelDocumentService getTravelDocumentService() {
        return travelDocumentService;
    }

    /**
     * Gets the objectCodeService attribute.
     *
     * @return Returns the objectCodeService.
     */
    public ObjectCodeService getObjectCodeService() {
        return objectCodeService;
    }

    /**
     * Sets the objectCodeService attribute value.
     *
     * @param objectCodeService The objectCodeService to set.
     */
    public void setObjectCodeService(ObjectCodeService objectCodeService) {
        this.objectCodeService = objectCodeService;
    }

    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }

    public TravelExpenseService getTravelExpenseService(){
        return SpringContext.getBean(TravelExpenseService.class);
    }

}
