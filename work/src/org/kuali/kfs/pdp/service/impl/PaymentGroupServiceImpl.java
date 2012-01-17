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
package org.kuali.kfs.pdp.service.impl;

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.pdp.PdpKeyConstants;
import org.kuali.kfs.pdp.PdpPropertyConstants;
import org.kuali.kfs.pdp.businessobject.PaymentGroup;
import org.kuali.kfs.pdp.businessobject.PaymentProcess;
import org.kuali.kfs.pdp.dataaccess.PaymentGroupDao;
import org.kuali.kfs.pdp.service.PaymentGroupService;
import org.kuali.kfs.sys.DynamicCollectionComparator;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.parameter.ParameterEvaluator;
import org.kuali.rice.core.api.parameter.ParameterEvaluatorService;
import org.kuali.rice.coreservice.framework.parameter.ParameterService;
import org.kuali.rice.kns.service.DataDictionaryService;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.krad.util.ObjectUtils;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public class PaymentGroupServiceImpl implements PaymentGroupService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentGroupServiceImpl.class);

    private PaymentGroupDao paymentGroupDao;
    private ParameterService parameterService;          
    private DataDictionaryService dataDictionaryService;            
    private Map<Integer,ParameterEvaluator> sortGroupSelectionParameters;
    private BusinessObjectService businessObjectService;
    
    public void setPaymentGroupDao(PaymentGroupDao c) {
        paymentGroupDao = c;
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getDisbursementNumbersByDisbursementType(java.lang.Integer, java.lang.String)
     */
    public List<Integer> getDisbursementNumbersByDisbursementType(Integer pid,String disbursementType) {
        LOG.debug("getDisbursementNumbersByDisbursementType() started");

        return paymentGroupDao.getDisbursementNumbersByDisbursementType(pid, disbursementType);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getDisbursementNumbersByDisbursementTypeAndBankCode(java.lang.Integer, java.lang.String, java.lang.String)
     */
    public List<Integer> getDisbursementNumbersByDisbursementTypeAndBankCode(Integer pid, String disbursementType, String bankCode) {
        return paymentGroupDao.getDisbursementNumbersByDisbursementTypeAndBankCode(pid, disbursementType, bankCode);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getDistinctBankCodesForProcessAndType(java.lang.Integer, java.lang.String)
     */
    public List<String> getDistinctBankCodesForProcessAndType(Integer pid, String disbursementType) {
        return paymentGroupDao.getDistinctBankCodesForProcessAndType(pid, disbursementType);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getByDisbursementTypeStatusCode(java.lang.String, java.lang.String)
     */
    public Iterator getByDisbursementTypeStatusCode(String disbursementType, String paymentStatusCode) {
        LOG.debug("getByDisbursementTypeStatusCode() started");
        
        Map fieldValues = new HashMap();
        fieldValues.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_TYPE_CODE, disbursementType);
        fieldValues.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYMENT_STATUS_CODE, paymentStatusCode);
        List paymentGroupList = (List) this.businessObjectService.findMatching(PaymentGroup.class, fieldValues);
        DynamicCollectionComparator.sort(paymentGroupList, PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_NBR);
        
        return paymentGroupList.iterator();
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getByProcess(org.kuali.kfs.pdp.businessobject.PaymentProcess)
     */
    public Iterator getByProcess(PaymentProcess p) {
        LOG.debug("getByProcess() started");
        
        Map fieldValues = new HashMap();
        fieldValues.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PROCESS_ID, p.getId());
        List paymentGroupList = (List) this.businessObjectService.findMatching(PaymentGroup.class, fieldValues);
        DynamicCollectionComparator.sort(paymentGroupList, PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_SORT_VALUE, PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_PAYEE_NAME, PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_LINE1_ADDRESS, PdpPropertyConstants.PaymentGroup.NOTES_LINES);
        
        return paymentGroupList.iterator();
    }
   
    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#get(java.lang.Integer)
     */
    public PaymentGroup get(Integer id) {
        LOG.debug("get() started");
        
        Map primaryKeys = new HashMap();
        primaryKeys.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_ID, id);
        return (PaymentGroup) this.businessObjectService.findByPrimaryKey(PaymentGroup.class, primaryKeys);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getByBatchId(java.lang.Integer)
     */
    public List getByBatchId(Integer batchId) {
        LOG.debug("getByBatchId() started");
        
        Map fieldValues = new HashMap();
        fieldValues.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_BATCH_ID, batchId);
        
        return (List) this.businessObjectService.findMatching(PaymentGroup.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getByDisbursementNumber(java.lang.Integer)
     */
    public List getByDisbursementNumber(Integer disbursementNbr) {
        LOG.debug("getByDisbursementNumber() started");

        Map fieldValues = new HashMap();
        fieldValues.put(PdpPropertyConstants.PaymentGroup.PAYMENT_GROUP_DISBURSEMENT_NBR, disbursementNbr);
        
        return (List) this.businessObjectService.findMatching(PaymentGroup.class, fieldValues);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#processPaidGroup(org.kuali.kfs.pdp.businessobject.PaymentGroup, java.sql.Date)
     */
    public void processPaidGroup(PaymentGroup group, Date processDate) {
        LOG.debug("processPaidGroup() started");

        Timestamp ts = new Timestamp(processDate.getTime());
        group.setEpicPaymentPaidExtractedDate(ts);
        group.setLastUpdate(ts);
        this.businessObjectService.save(group);
    }

    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#processCancelledGroup(org.kuali.kfs.pdp.businessobject.PaymentGroup,
     *      java.sql.Date)
     */
    public void processCancelledGroup(PaymentGroup group, Date processDate) {
        LOG.debug("processCancelledGroup() started");

        Timestamp ts = new Timestamp(processDate.getTime());
        group.setEpicPaymentCancelledExtractedDate(ts);
        group.setLastUpdate(ts);
        this.businessObjectService.save(group);
    }
    
    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#setParameterService(org.kuali.kfs.sys.service.ParameterService)
     */
    public void setParameterService(ParameterService parameterService) {
        this.parameterService = parameterService;
    }
    
    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getSortGroupId(org.kuali.kfs.pdp.businessobject.PaymentGroup)
     */
    public int getSortGroupId(PaymentGroup paymentGroup) {      
        String DEFAULT_SORT_GROUP_ID_PARAMETER = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PdpKeyConstants.DEFAULT_SORT_GROUP_ID_PARAMETER);

        for (Integer sortGroupId : getSortGroupSelectionParameters().keySet()) {         
            List<String> parameterValues = Arrays.asList(StringUtils.substringAfter(getSortGroupSelectionParameters().get(sortGroupId).getValue(), "=").split(";"));         
            String constrainedValue = String.valueOf(ObjectUtils.getPropertyValue(paymentGroup, StringUtils.substringBefore(getSortGroupSelectionParameters().get(sortGroupId).getValue(), "=")));           
            if ((getSortGroupSelectionParameters().get(sortGroupId).constraintIsAllow() && parameterValues.contains(constrainedValue))           
                || (!getSortGroupSelectionParameters().get(sortGroupId).constraintIsAllow() && !parameterValues.contains(constrainedValue))) {           
                    return sortGroupId;         
            }           
        }
        
        return new Integer(parameterService.getParameterValueAsString(PaymentGroup.class, DEFAULT_SORT_GROUP_ID_PARAMETER));            
    }       
       
    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getSortGroupName(int)
     */
    public String getSortGroupName(int sortGroupId) {      
        String DEFAULT_SORT_GROUP_ID_PARAMETER = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PdpKeyConstants.DEFAULT_SORT_GROUP_ID_PARAMETER);

        if ((sortGroupId + "").equals(parameterService.getParameterValueAsString(PaymentGroup.class, DEFAULT_SORT_GROUP_ID_PARAMETER))) {           
            return SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PdpKeyConstants.DEFAULT_GROUP_NAME_OTHER);         
        }       
        
        return dataDictionaryService.getAttributeLabel(PaymentGroup.class, StringUtils.substringBefore(getSortGroupSelectionParameters().get(sortGroupId).getValue(), "="));         
    } 
    
    /**
     * @see org.kuali.kfs.pdp.service.PaymentGroupService#getAchPaymentsNeedingAdviceNotification()
     */
    public List<PaymentGroup> getAchPaymentsNeedingAdviceNotification() {
        return this.paymentGroupDao.getAchPaymentsNeedingAdviceNotification();
    }
    
    /**
     * Gets the sort group parameters
     * 
     * @return
     */
    protected Map<Integer,ParameterEvaluator> getSortGroupSelectionParameters() {
        String SORT_GROUP_SELECTION_PARAMETER_PREFIX = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(PdpKeyConstants.SORT_GROUP_SELECTION_PARAMETER_PREFIX);
        
        if (sortGroupSelectionParameters == null) {         
            sortGroupSelectionParameters = new TreeMap<Integer,ParameterEvaluator>();           
            boolean moreParameters = true;          
            int i = 1;          
            while (moreParameters) {            
                if (parameterService.parameterExists(PaymentGroup.class, SORT_GROUP_SELECTION_PARAMETER_PREFIX + i)) {          
                    sortGroupSelectionParameters.put(i, /*REFACTORME*/SpringContext.getBean(ParameterEvaluatorService.class).getParameterEvaluator(PaymentGroup.class, SORT_GROUP_SELECTION_PARAMETER_PREFIX + i, null));           
                    i++;            
                }           
                else {          
                    moreParameters = false;         
                }           
            }           
        } 
        
        return sortGroupSelectionParameters;            
    }

    public void setDataDictionaryService(DataDictionaryService dataDictionaryService) {
        this.dataDictionaryService = dataDictionaryService;
    }
    
    /**
     * Sets the business object service
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
}
