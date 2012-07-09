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

import static org.kuali.kfs.module.tem.TemConstants.PARAM_NAMESPACE;
import static org.kuali.kfs.module.tem.TemConstants.TravelReimbursementParameters.PARAM_DTL_TYPE;
import static org.kuali.kfs.module.tem.util.BufferedLogger.debug;

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
import org.apache.ojb.broker.PersistenceBrokerException;
import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.coa.service.ObjectCodeService;
import org.kuali.kfs.module.tem.TemConstants;
import org.kuali.kfs.module.tem.TemConstants.ExpenseType;
import org.kuali.kfs.module.tem.businessobject.MileageRateObjCode;
import org.kuali.kfs.module.tem.businessobject.TemDistributionAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TemSourceAccountingLine;
import org.kuali.kfs.module.tem.businessobject.TravelerDetail;
import org.kuali.kfs.module.tem.businessobject.TripType;
import org.kuali.kfs.module.tem.document.TravelDocument;
import org.kuali.kfs.module.tem.document.service.TravelDocumentService;
import org.kuali.kfs.module.tem.document.web.bean.AccountingDistribution;
import org.kuali.kfs.module.tem.service.AccountingDistributionService;
import org.kuali.kfs.module.tem.service.TravelExpenseService;
import org.kuali.kfs.module.tem.util.AccountingDistributionComparator;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.service.UniversityDateService;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.kuali.rice.kns.service.ParameterService;
import org.kuali.rice.kns.util.KualiDecimal;

/**
 * Accounting Distribution Service Implementation
 */
/**
 * This class...
 */
public class AccountingDistributionServiceImpl implements AccountingDistributionService {

    private BusinessObjectService businessObjectService;
    private ObjectCodeService objectCodeService;
    private TravelDocumentService travelDocumentService;
    private ParameterService parameterService;

    
    @SuppressWarnings("deprecation")
    @Override
    public List<TemSourceAccountingLine> distributionToSouceAccountingLines(List<TemDistributionAccountingLine> distributionAccountingLines, List<AccountingDistribution> accountingDistributionList, Integer sequenceNumber){
        List<TemSourceAccountingLine> sourceAccountingList = new ArrayList<TemSourceAccountingLine>();
        Map<String, AccountingDistribution> distributionMap = new HashMap<String, AccountingDistribution>();
        KualiDecimal total = KualiDecimal.ZERO;
        for (AccountingDistribution accountDistribution: accountingDistributionList){
            if (accountDistribution.getSelected()){
                total = total.add(accountDistribution.getRemainingAmount());
            }
        }     
        
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
                    BigDecimal product = accountingLine.getAccountLinePercent().multiply(accountingDistribution.getRemainingAmount().bigDecimalValue());
                    product = product.divide(new BigDecimal(100),5,RoundingMode.HALF_UP);
                    BigDecimal lineAmount = product.divide(total.bigDecimalValue(),5,RoundingMode.HALF_UP);
            
                    newLine.setAmount(new KualiDecimal(product));
                    newLine.setCardType(accountingDistribution.getCardType());
                    Map<String,Object> fieldValues = new HashMap<String,Object>();
                    fieldValues.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, accountingDistribution.getObjectCode());
                    fieldValues.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, newLine.getChartOfAccountsCode());
                    fieldValues.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, SpringContext.getBean(UniversityDateService.class).getCurrentFiscalYear());
                    ObjectCode objCode = (ObjectCode) getBusinessObjectService().findByPrimaryKey(ObjectCode.class, fieldValues);
                    newLine.setObjectCode(objCode);
                    newLine.setSequenceNumber(sequenceNumber);
                    sequenceNumber = new Integer(sequenceNumber.intValue()+1);
                    newLine.setFinancialObjectCode(accountingDistribution.getObjectCode());
                    tempSourceAccountingList.add(newLine);
                }
                sourceAccountingList.addAll(adjustValues(tempSourceAccountingList,accountingDistribution.getRemainingAmount()));
            }
        }
        //Collections.sort(sourceAccountingList, new SourceAccountingLineComparator());
        return sourceAccountingList;
    }
    
    
    /**
     * This method will adjust the last accounting line to make the amounts sum up to the total.
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
            documentDistribution.add(distributionMap.get(distribution));
        }
        Collections.sort(documentDistribution, new AccountingDistributionComparator());

        return documentDistribution;
    }

    protected Map<String, AccountingDistribution> accountingLinesToDistributionMap(TravelDocument travelDocument) {
        Map<String, AccountingDistribution> distributionMap = new HashMap<String, AccountingDistribution>();
        for (TemSourceAccountingLine accountingLine : (List<TemSourceAccountingLine>) travelDocument.getSourceAccountingLines()) {
            // ObjectCode getCode() is generating PersistenceBrokerException when we lookup an invalid object code.
            try {
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
            catch (PersistenceBrokerException ex) {
                ex.printStackTrace();
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
        final String parameterValue = getParameterService().getParameterValue(PARAM_NAMESPACE, PARAM_DTL_TYPE, paramName);
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

    protected String getMileageObjectCodeFrom(final TravelDocument travelDocument, Integer mileageRateId) {
        final String travelerType = travelDocument.getTraveler().getTravelerTypeCode();
        final String tripType = travelDocument.getTripType().getCode();

        String objCode = "";
        Map<String, Object> fields = new HashMap<String, Object>();

        fields.put("travelerTypeCode", travelerType);
        fields.put("tripTypeCode", tripType);
        fields.put("mileageRateId", mileageRateId);

        List<MileageRateObjCode> mileageObjCodes = (List<MileageRateObjCode>) businessObjectService.findMatching(MileageRateObjCode.class, fields);
        for (MileageRateObjCode mileageObjCode : mileageObjCodes) {
            objCode = mileageObjCode.getFinancialObjectCode();
        }

        return objCode;
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
                debug("comparing ", code, " to ", distribution.getObjectCode());
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
// TODO: remove when sure. For TA > TR accounting distribution fix
//        if (!(travelDocument instanceof TravelAuthorizationDocument)){
//            prorateDistributions(travelDocument, distributions); 
//        }
        
        return distributions;
    }

    //TODO: remove when sure.
//    protected void prorateDistributions(final TravelDocument travelDocument, final List<AccountingDistribution> distributions) {
//        if (KualiDecimal.ZERO.equals(travelDocument.getDocumentGrandTotal())) {
//            return;
//        }
//
//        TravelAuthorizationDocument authorization = null;
//        try {
//            authorization = (TravelAuthorizationDocument) getTravelDocumentService().findCurrentTravelAuthorization(travelDocument);
//        }
//        catch (WorkflowException ex) {
//            // TODO Auto-generated catch block
//            ex.printStackTrace();
//        }
//
//        if (authorization != null && !(authorization instanceof TravelAuthorizationCloseDocument)){
//            KualiDecimal encumbranceAmount = new KualiDecimal(0);
//            for (int i=0;i<authorization.getSourceAccountingLines().size();i++){
//                encumbranceAmount = encumbranceAmount.add(authorization.getSourceAccountingLine(i).getAmount());
//            }
//            final KualiDecimal cumulativeTotal  = cumulativeReimbursable(travelDocument.getTravelDocumentIdentifier());
//            final KualiDecimal prorateLimit      = encumbranceAmount.subtract(cumulativeTotal);
//            KualiDecimal prorateFactor     = new KualiDecimal(1);
//            KualiDecimal calculatedTotal = new KualiDecimal(0);
//            if (travelDocument.getDocumentGrandTotal().isGreaterThan(prorateLimit)) {
//                prorateFactor = prorateLimit.divide(travelDocument.getDocumentGrandTotal());
//            }
//                
//            for (int i=0;i<distributions.size();i++) {
//                AccountingDistribution distribution = distributions.get(i);
//                final KualiDecimal lineTotal = travelDocument.getTotalFor(distribution.getObjectCode());
//                final KualiDecimal calculatedAmount = prorateFactor.multiply(distribution.getSubTotal());
//                
//                if (prorateFactor.isLessThan(new KualiDecimal(1)) && i == distributions.size()-1){                    
//                    KualiDecimal remainingAmount = prorateLimit.subtract(calculatedTotal);
//                    distribution.setSubTotal(remainingAmount);
//                }
//                else {
//                    distribution.setSubTotal(prorateFactor.multiply(distribution.getSubTotal()));
//                }
//                
//                final KualiDecimal remaining = distribution.getSubTotal().subtract(lineTotal);
//                distribution.setRemainingAmount(remaining);
//                calculatedTotal = calculatedTotal.add(calculatedAmount);
//            }                
//        }
//    }
//
//    /**
//     * Gets the total for all reimbursements on a trip. {@link TravelReimbursementDocument} instances must be ENROUTE, PROCESSED or
//     * FINAL to qualify.
//     * 
//     * @param travelDocumentIdentifier or trip id to check for qualifying totals
//     * @return the total reimbursable for all reimbursements on the trip
//     */
//    protected KualiDecimal cumulativeReimbursable(final String travelDocumentIdentifier) {
//        KualiDecimal retval = KualiDecimal.ZERO;
//        try {
//            for (final TravelReimbursementDocument reimbursement : getTravelDocumentService().find(TravelReimbursementDocument.class, travelDocumentIdentifier)) {
//                if (!getTravelDocumentService().isUnsuccessful(reimbursement))
//                    retval = retval.add(reimbursement.getReimbursableTotal());
//            }
//        }
//        catch (Exception e) {
//            warn("Not able to get reimbursable total for all documents related to trip ", travelDocumentIdentifier);
//            if (logger().isDebugEnabled()) {
//                e.printStackTrace();
//            }
//        }
//        return retval;
//    }
//
//    /**
//     * loop through all the TemDistributionAccountingLines and set the amount = remainingTotal*percent/100 and the percent = amount/remainingTotal * 100
//     * depending upon which may be null.
//     * 
//     * If both are supplied and the math doesn't work out, use the percentage
//     * 
//     * collect totals and if they are <= 0 before loop ends, set remaining amounts and percents to 0.
//     */
//    public void normalizeAmountAndPercents(List<TemDistributionAccountingLine> lines, KualiDecimal remainingTotal) {
//        BigDecimal percent = new BigDecimal(100);
//        KualiDecimal remaining = new KualiDecimal(remainingTotal.bigDecimalValue());
//        for(TemDistributionAccountingLine line : lines){
//            if (remaining.isLessEqual(KualiDecimal.ZERO)
//                    || percent.intValue() <= 0){
//                line.setAmount(KualiDecimal.ZERO);
//                line.setAccountLinePercent(new BigDecimal(0.00));
//                continue;
//            }
//            if (line.getAccountLinePercent() != null
//                    && line.getAmount() == null){
//                
//                if (percent.subtract(line.getAccountLinePercent()).intValue() < 0){
//                    line.setAccountLinePercent(percent);
//                }
//                BigDecimal product =  line.getAccountLinePercent().multiply(remainingTotal.bigDecimalValue());
//                product = product.divide(new BigDecimal(100),5,RoundingMode.HALF_UP);
//                line.setAmount(new KualiDecimal(product));
//                percent = percent.subtract(line.getAccountLinePercent());
//                remaining = remaining.subtract(line.getAmount());
//            }
//            else if (line.getAccountLinePercent() == null
//                    && line.getAmount() != null){
//                
//                if (remaining.subtract(line.getAmount()).isLessThan(KualiDecimal.ZERO)){
//                    line.setAmount(remaining);
//                }
//                BigDecimal product = line.getAmount().bigDecimalValue().divide(remainingTotal.bigDecimalValue(),5,RoundingMode.HALF_UP);
//                product = product.multiply(new BigDecimal(100));
//                line.setAccountLinePercent(product);
//                percent = percent.subtract(line.getAccountLinePercent());
//                remaining = remaining.subtract(line.getAmount());
//            }
//            else{
//                //check to see if the values match.  If no match, then use the percentage to calculate the amount.
//                BigDecimal product =  line.getAccountLinePercent().multiply(remainingTotal.bigDecimalValue());
//                product = product.divide(new BigDecimal(100),2,RoundingMode.HALF_UP);
//                if (!product.equals(line.getAmount().bigDecimalValue())){
//                    line.setAmount(new KualiDecimal(product));
//                }
//                percent = percent.subtract(line.getAccountLinePercent());
//                remaining = remaining.subtract(line.getAmount());
//            }
//        }
//    }

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
        return (TravelExpenseService) SpringContext.getBean(TravelExpenseService.class);
    }
        
}
