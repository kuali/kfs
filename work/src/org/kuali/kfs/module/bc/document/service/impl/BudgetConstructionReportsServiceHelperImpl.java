/*
 * Copyright 2008 The Kuali Foundation.
 * 
 * Licensed under the Educational Community License, Version 1.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 * http://www.opensource.org/licenses/ecl1.php
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kuali.kfs.module.bc.document.service.impl;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.kuali.kfs.coa.businessobject.ObjectCode;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionAdministrativePost;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionIntendedIncumbent;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectDump;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectPick;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionPosition;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalaryFunding;
import org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalarySocialSecurityNumber;
import org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionOrganizationReportsService;
import org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper;
import org.kuali.kfs.sys.KFSPropertyConstants;
import org.kuali.kfs.sys.service.NonTransactional;
import org.kuali.rice.kns.service.BusinessObjectService;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

/**
 * This implements the methods described in BudgetConstructionReportsServiceHelper
 */
public class BudgetConstructionReportsServiceHelperImpl implements BudgetConstructionReportsServiceHelper {

    BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService;
    BusinessObjectService businessObjectService;
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getDataForBuildingReports(java.lang.Class, java.lang.String, java.util.List)
     */
    @Transactional
    public Collection getDataForBuildingReports(Class clazz, String principalName, List<String> orderList){
        
        //build searchCriteria 
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.KUALI_USER_PERSON_UNIVERSAL_IDENTIFIER, principalName);

        // build order list
        return budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(clazz, searchCriteria, orderList);
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getDataForBuildingReports(java.lang.Class, java.util.Map, java.util.List)
     */
    @Transactional
    public Collection getDataForBuildingReports(Class clazz, Map searchCriteria, List<String> orderList){
        
        return budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(clazz, searchCriteria, orderList);
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#generatePdf(java.util.List, java.io.ByteArrayOutputStream)
     */
    @NonTransactional
    public void generatePdf(List<String> errorMessages, ByteArrayOutputStream baos) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        for (String error : errorMessages) {
            document.add(new Paragraph(error));
        }

        document.close();
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getObjectCode(java.lang.Integer, java.lang.String, java.lang.String)
     */
    @Transactional
    public ObjectCode getObjectCode(Integer universityFiscalYear, String chartOfAccountsCode, String financialObjectCode){
        
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, chartOfAccountsCode);
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, financialObjectCode);
        return (ObjectCode) businessObjectService.findByPrimaryKey(ObjectCode.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getSelectedObjectCodes(java.lang.String)
     */
    @Transactional
    public String getSelectedObjectCodes (String principalName){
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalName);
        searchCriteria.put(KFSPropertyConstants.SELECT_FLAG, 1);
        Collection<BudgetConstructionObjectPick> objectPickList = businessObjectService.findMatching(BudgetConstructionObjectPick.class, searchCriteria);
        String objectCodes = "";
        int count = 0; 
        for (BudgetConstructionObjectPick objectPick : objectPickList) {
            count += 1;
            objectCodes += objectPick.getFinancialObjectCode();
            if (count < objectPickList.size()){
                objectCodes += ", ";
            }
        }
        return objectCodes;
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getBudgetConstructionAdministrativePost(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @Transactional
    public BudgetConstructionAdministrativePost getBudgetConstructionAdministrativePost(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.EMPLID, appointmentFundingEntry.getEmplid());
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFundingEntry.getPositionNumber());
        return (BudgetConstructionAdministrativePost) businessObjectService.findByPrimaryKey(BudgetConstructionAdministrativePost.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getBudgetConstructionPosition(java.lang.Integer, org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @Transactional
    public BudgetConstructionPosition getBudgetConstructionPosition(Integer universityFiscalYear, PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.POSITION_NUMBER, appointmentFundingEntry.getPositionNumber());
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        return (BudgetConstructionPosition) businessObjectService.findByPrimaryKey(BudgetConstructionPosition.class, searchCriteria);
    }

    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getBudgetConstructionIntendedIncumbent(org.kuali.kfs.module.bc.businessobject.PendingBudgetConstructionAppointmentFunding)
     */
    @Transactional
    public BudgetConstructionIntendedIncumbent getBudgetConstructionIntendedIncumbent(PendingBudgetConstructionAppointmentFunding appointmentFundingEntry) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.EMPLID, appointmentFundingEntry.getEmplid());
        return (BudgetConstructionIntendedIncumbent) businessObjectService.findByPrimaryKey(BudgetConstructionIntendedIncumbent.class, searchCriteria);
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getBudgetConstructionSalarySocialSecurityNumber(java.lang.String, org.kuali.kfs.module.bc.businessobject.BudgetConstructionSalaryFunding)
     */
    @Transactional
    public BudgetConstructionSalarySocialSecurityNumber getBudgetConstructionSalarySocialSecurityNumber(String principalName, BudgetConstructionSalaryFunding salaryFunding){
        
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalName);
        searchCriteria.put(KFSPropertyConstants.EMPLID, salaryFunding.getEmplid());
        
        List<String> orderList = new ArrayList();
        orderList.add(KFSPropertyConstants.ORGANIZATION_CHART_OF_ACCOUNTS_CODE);
        orderList.add(KFSPropertyConstants.ORGANIZATION_CODE);
        orderList.add(KFSPropertyConstants.PERSON_NAME);
        orderList.add(KFSPropertyConstants.EMPLID);

        return (BudgetConstructionSalarySocialSecurityNumber) businessObjectService.findByPrimaryKey(BudgetConstructionSalarySocialSecurityNumber.class, searchCriteria);
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getSalaryFunding(java.lang.String, java.lang.String)
     */
    @Transactional
    public Collection<BudgetConstructionSalaryFunding> getSalaryFunding(String principalName, String emplid){
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.PERSON_UNIVERSAL_IDENTIFIER, principalName);
        searchCriteria.put(KFSPropertyConstants.EMPLID, emplid);
        
        List<String> orderList = new ArrayList();
        orderList.add(KFSPropertyConstants.POSITION_NUMBER);
        orderList.add(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR);
        orderList.add(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE);
        orderList.add(KFSPropertyConstants.ACCOUNT_NUMBER);
        orderList.add(KFSPropertyConstants.SUB_ACCOUNT_NUMBER);
        orderList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        orderList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        
        return budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(BudgetConstructionSalaryFunding.class, searchCriteria, orderList);
    }
    
    /**
     * @see org.kuali.kfs.module.bc.document.service.BudgetConstructionReportsServiceHelper#getPendingBudgetConstructionAppointmentFundingList(java.lang.Integer, org.kuali.kfs.module.bc.businessobject.BudgetConstructionObjectDump)
     */
    @Transactional
    public Collection<PendingBudgetConstructionAppointmentFunding> getPendingBudgetConstructionAppointmentFundingList(Integer universityFiscalYear, BudgetConstructionObjectDump budgetConstructionObjectDump) {
        Map searchCriteria = new HashMap();
        searchCriteria.put(KFSPropertyConstants.UNIVERSITY_FISCAL_YEAR, universityFiscalYear);
        searchCriteria.put(KFSPropertyConstants.CHART_OF_ACCOUNTS_CODE, budgetConstructionObjectDump.getChartOfAccountsCode());
        searchCriteria.put(KFSPropertyConstants.ACCOUNT_NUMBER, budgetConstructionObjectDump.getAccountNumber());
        searchCriteria.put(KFSPropertyConstants.SUB_ACCOUNT_NUMBER, budgetConstructionObjectDump.getSubAccountNumber());
        searchCriteria.put(KFSPropertyConstants.FINANCIAL_OBJECT_CODE, budgetConstructionObjectDump.getFinancialObjectCode());
        
        List<String> orderList = new ArrayList();
        orderList.add(KFSPropertyConstants.FINANCIAL_OBJECT_CODE);
        orderList.add(KFSPropertyConstants.FINANCIAL_SUB_OBJECT_CODE);
        orderList.add(KFSPropertyConstants.POSITION_NUMBER);
        orderList.add(KFSPropertyConstants.EMPLID);
        return budgetConstructionOrganizationReportsService.getBySearchCriteriaOrderByList(PendingBudgetConstructionAppointmentFunding.class, searchCriteria, orderList);
    }

    /**
     * sets the budgetConstructionOrganizationReportsService attribute value
     * 
     * @param budgetConstructionOrganizationReportsService
     */
    @NonTransactional
    public void setBudgetConstructionOrganizationReportsService(BudgetConstructionOrganizationReportsService budgetConstructionOrganizationReportsService) {
        this.budgetConstructionOrganizationReportsService = budgetConstructionOrganizationReportsService;
    }

    /**
     * sets the businessObjectService attribute value
     * 
     * @param businessObjectService
     */
    @NonTransactional
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
   }

