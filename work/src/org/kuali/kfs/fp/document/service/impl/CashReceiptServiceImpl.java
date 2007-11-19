/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.module.financial.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.core.bo.Campus;
import org.kuali.core.bo.DocumentHeader;
import org.kuali.core.bo.user.KualiGroup;
import org.kuali.core.bo.user.UniversalUser;
import org.kuali.core.exceptions.GroupNotFoundException;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.service.KualiGroupService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.kfs.KFSConstants;
import org.kuali.kfs.KFSPropertyConstants;
import org.kuali.kfs.context.SpringContext;
import org.kuali.kfs.service.ParameterService;
import org.kuali.module.financial.bo.CashDrawer;
import org.kuali.module.financial.bo.CashieringTransaction;
import org.kuali.module.financial.bo.CoinDetail;
import org.kuali.module.financial.bo.CurrencyDetail;
import org.kuali.module.financial.dao.CashManagementDao;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.service.CashDrawerService;
import org.kuali.module.financial.service.CashReceiptService;
import org.springframework.transaction.annotation.Transactional;

import edu.iu.uis.eden.exception.WorkflowException;

/**
 * 
 * This is the default implementation of the CashReceiptService interface.
 */
@Transactional
public class CashReceiptServiceImpl implements CashReceiptService {

    private BusinessObjectService businessObjectService;
    private WorkflowDocumentService workflowDocumentService;
    private CashManagementDao cashManagementDao;
    private CashDrawerService cashDrawerService;
    private ParameterService parameterService;
    private KualiGroupService kualiGroupService;

    /**
     * This method uses the campus code provided to determine the cash receipt verification unit.  The verification unit
     * is equivalent to the campus code with a prefix retrieved from the parameter service using the ParameterService
     *  using the key "VERIFICATION_UNIT_GROUP_PREFIX".
     * 
     * @param campusCode The campus code used to determine the verification unit.
     * @return The cash receipt verification unit based on the campus code provided.
     * 
     * @see org.kuali.module.financial.service.CashReceiptService#getCashReceiptVerificationUnitWorkgroupNameByCampusCode(java.lang.String)
     */
    public String getCashReceiptVerificationUnitForCampusCode(String campusCode) {
        String vunit = null;

        if (StringUtils.isBlank(campusCode)) {
            throw new IllegalArgumentException("invalid (blank) campusCode");
        }

        vunit = parameterService.getParameterValue(CashReceiptDocument.class, "VERIFICATION_UNIT_GROUP_PREFIX")+campusCode;
        
        KualiGroup group = null;
        try {
            group = kualiGroupService.getByGroupName(vunit);
        }
        catch (GroupNotFoundException e) {
            throw new IllegalArgumentException(vunit+" does not have a corresponding workgroup");
        }

        return vunit;
    }


    /**
     * This method uses the unit name provided to determine the associated campus code.  The campus code is a substring of the 
     * unit name and can be retrieved by removing the verification unit prefix from the unit name.  The prefix to be removed 
     * can be retrieved from the ParameterService using the key "VERIFICATION_UNIT_GROUP_PREFIX".
     * 
     * @param unitName The unit name to be used to determine the campus code.
     * @return The campus code retrieved from the unit name provided.
     * 
     * @see org.kuali.module.financial.service.CashReceiptService#getCampusCodeForCashReceiptVerificationUnit(java.lang.String)
     */
    public String getCampusCodeForCashReceiptVerificationUnit(String unitName) {
        String campusCode = null;

        if (StringUtils.isBlank(unitName)) {
            throw new IllegalArgumentException("invalid (blank) unitName");
        }

        // pretend that a lookup is actually happening
        campusCode = unitName.replace(parameterService.getParameterValue(CashReceiptDocument.class, "VERIFICATION_UNIT_GROUP_PREFIX"), "").toUpperCase();
        
        if (!verifyCampus(campusCode)) {
            throw new IllegalArgumentException("The campus "+campusCode+" does not exist");
        }

        return campusCode;
    }


    /**
     * This method verifies the campus code provided exists.  This is done by retrieving all the available campuses from
     * the BusinessObjectService and then looking for a matching campus code within the result set.
     * 
     * @param campusCode The campus code to be verified.
     * @return True if the campus code provided is valid and exists, false otherwise.
     */
    private boolean verifyCampus(String campusCode) {
        Iterator campiiIter = businessObjectService.findAll(Campus.class).iterator();
        boolean foundCampus = false;
        while (campiiIter.hasNext() && !foundCampus) {
            Campus campus = (Campus)campiiIter.next();
            if (campus.getCampusCode().equals(campusCode)) {
                foundCampus = true;
            }
        }
        return foundCampus;
        
    }


    /**
     * This method retrieves the cash receipt verification unit based on the user provided.  This is done by retrieving the campus
     * code associated with the user provided and then performing the lookup using this campus code.
     * 
     * @param user The user to be used to retrieve the verification unit.
     * @return The cash receipt verification unit associated with the user provided.
     * 
     * @see org.kuali.module.financial.service.CashReceiptService#getCashReceiptVerificationUnit(org.kuali.core.bo.user.KualiUser)
     */
    public String getCashReceiptVerificationUnitForUser(UniversalUser user) {
        String unitName = null;

        if (user == null) {
            throw new IllegalArgumentException("invalid (null) user");
        }

        return getCashReceiptVerificationUnitForCampusCode(user.getCampusCode());
    }


    /**
     * This method retrieves a collection of cash receipts using the verification unit and the status provided to 
     * retrieve the cash receipts.  
     * 
     * @param verificationUnit The verification unit used to retrieve a collection of associated cash receipts.
     * @param statusCode The status code of the cash receipts to be retrieved.  
     * @return A collection of cash receipt documents which match the search criteria provided.
     * 
     * @see org.kuali.module.financial.service.CashReceiptService#getCashReceipts(java.lang.String, java.lang.String)
     */
    public List getCashReceipts(String verificationUnit, String statusCode) {
        if (StringUtils.isBlank(statusCode)) {
            throw new IllegalArgumentException("invalid (blank) statusCode");
        }

        String[] statii = new String[] { statusCode };
        return getCashReceipts(verificationUnit, statii);
    }

    /**
     * This method retrieves a collection of cash receipts using the verification unit and the statuses provided to 
     * retrieve the cash receipts.  
     * 
     * @param verificationUnit The verification unit used to retrieve a collection of associated cash receipts.
     * @param statii A collection of possible statuses that will be used in the lookup of cash receipts.
     * @return A collection of cash receipt documents which match the search criteria provided.
     * 
     * @see org.kuali.module.financial.service.CashReceiptService#getCashReceipts(java.lang.String, java.lang.String[])
     */
    public List getCashReceipts(String verificationUnit, String[] statii) {
        if (StringUtils.isBlank(verificationUnit)) {
            throw new IllegalArgumentException("invalid (blank) verificationUnit");
        }
        if (statii == null) {
            throw new IllegalArgumentException("invalid (null) statii");
        }
        else {
            if (statii.length == 0) {
                throw new IllegalArgumentException("invalid (empty) statii");
            }
            else {
                for (int i = 0; i < statii.length; ++i) {
                    if (StringUtils.isBlank(statii[i])) {
                        throw new IllegalArgumentException("invalid (blank) status code " + i);
                    }
                }
            }
        }

        return getPopulatedCashReceipts(verificationUnit, statii);
    }

    /**
     * This method retrieves a populated collection of cash receipts using the lookup parameters provided.  A populated 
     * cash receipt document is a cash receipt document with fully populated workflow fields.
     * 
     * @param verificationUnit The verification unit used to retrieve a collection of associated cash receipts.
     * @param statii A collection of possible statuses that will be used in the lookup of the cash receipts.
     * @return List of CashReceiptDocument instances with their associated workflowDocuments populated.
     */
    public List getPopulatedCashReceipts(String verificationUnit, String[] statii) {
        Map queryCriteria = buildCashReceiptCriteriaMap(verificationUnit, statii);

        List documents = new ArrayList(getBusinessObjectService().findMatchingOrderBy(CashReceiptDocument.class, queryCriteria, KFSPropertyConstants.DOCUMENT_NUMBER, true));

        populateWorkflowFields(documents);

        return documents;
    }


    /**
     * This method builds out a map of search criteria for performing cash receipt lookups using the values provided.
     * 
     * @param workgroupName The workgroup name to use as search criteria for looking up cash receipts.
     * @param statii A collection of possible statuses to use as search criteria for looking up cash receipts.
     * @return The search criteria provided in a map with CashReceiptConstants used as keys to the parameters given.
     */
    private Map buildCashReceiptCriteriaMap(String workgroupName, String[] statii) {
        Map queryCriteria = new HashMap();

        if (statii.length == 1) {
            queryCriteria.put(KFSConstants.CashReceiptConstants.CASH_RECEIPT_DOC_HEADER_STATUS_CODE_PROPERTY_NAME, statii[0]);
        }
        else if (statii.length > 0) {
            List<String> statusList = Arrays.asList(statii);
            queryCriteria.put(KFSConstants.CashReceiptConstants.CASH_RECEIPT_DOC_HEADER_STATUS_CODE_PROPERTY_NAME, statusList);
        }

        String campusLocationCode = getCampusCodeForCashReceiptVerificationUnit(workgroupName);
        queryCriteria.put(KFSConstants.CashReceiptConstants.CASH_RECEIPT_CAMPUS_LOCATION_CODE_PROPERTY_NAME, campusLocationCode);

        return queryCriteria;
    }

    /**
     * This method populates the workflowDocument field of each CashReceiptDocument in the given List
     * 
     * @param documents A collection of CashReceiptDocuments to be populated with workflow document data.
     */
    private void populateWorkflowFields(List documents) {
        for (Iterator i = documents.iterator(); i.hasNext();) {
            CashReceiptDocument cr = (CashReceiptDocument) i.next();

            KualiWorkflowDocument workflowDocument = null;
            DocumentHeader docHeader = cr.getDocumentHeader();
            try {
                Long documentHeaderId = Long.valueOf(docHeader.getDocumentNumber());
                UniversalUser user = GlobalVariables.getUserSession().getUniversalUser();

                workflowDocument = getWorkflowDocumentService().createWorkflowDocument(documentHeaderId, user);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve workflow document for documentHeaderId '" + docHeader.getDocumentNumber() + "'", e);
            }

            docHeader.setWorkflowDocument(workflowDocument);
        }
    }

    /**
     * This method retrieves the cash details from the cash receipt document provided and adds those details to the 
     * associated cash drawer.  After the details are added to the drawer, the drawer is persisted to the database.
     * 
     * @param crDoc The cash receipt document the cash details will be retrieved from.
     * 
     * @see org.kuali.module.financial.service.CashReceiptService#addCashDetailsToCashDrawer(org.kuali.module.financial.document.CashReceiptDocument)
     */
    public void addCashDetailsToCashDrawer(CashReceiptDocument crDoc) {
        CashDrawer drawer = retrieveCashDrawer(crDoc);
        // we need to to add the currency and coin to the cash management doc's cumulative CR as well
        if (crDoc.getCurrencyDetail() != null && !crDoc.getCurrencyDetail().isEmpty()) {
            CurrencyDetail cumulativeCurrencyDetail = cashManagementDao.findCurrencyDetailByCashieringRecordSource(drawer.getReferenceFinancialDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
            cumulativeCurrencyDetail.add(crDoc.getCurrencyDetail());
            businessObjectService.save(cumulativeCurrencyDetail);
            
            drawer.addCurrency(crDoc.getCurrencyDetail());
        }
        if (crDoc.getCoinDetail() != null && !crDoc.getCoinDetail().isEmpty()) {
            CoinDetail cumulativeCoinDetail = cashManagementDao.findCoinDetailByCashieringRecordSource(drawer.getReferenceFinancialDocumentNumber(), CashieringTransaction.DETAIL_DOCUMENT_TYPE, KFSConstants.CurrencyCoinSources.CASH_RECEIPTS);
            cumulativeCoinDetail.add(crDoc.getCoinDetail());
            businessObjectService.save(cumulativeCoinDetail);
            
            drawer.addCoin(crDoc.getCoinDetail());
        }
        SpringContext.getBean(BusinessObjectService.class).save(drawer);
    }
    
    /**
     * This method finds the appropriate cash drawer for this cash receipt document to add cash to.
     * 
     * @param crDoc The document the cash drawer will be retrieved from.
     * @return An instance of a cash drawer associated with the cash receipt document provided.
     */
    private CashDrawer retrieveCashDrawer(CashReceiptDocument crDoc) {
        String workgroupName = getCashReceiptVerificationUnitForCampusCode(crDoc.getCampusLocationCode());
        if (workgroupName == null) {
            throw new RuntimeException("Cannot find workgroup name for Cash Receipt document: "+crDoc.getDocumentNumber());
        }
        
        CashDrawer drawer = cashDrawerService.getByWorkgroupName(workgroupName, false);
        if (drawer == null) {
            throw new RuntimeException("There is no Cash Drawer for Workgroup "+workgroupName);
        }
        return drawer;
    }

    // injection-molding
    /**
     * Gets the businessObjectService attribute. 
     * @return current value of businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * Gets the workflowDocumentService attribute. 
     * @return current value of workflowDocumentService.
     */
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }

    /**
     * Gets the cashManagementDao attribute. 
     * 
     * @return Returns the cashManagementDao.
     */
    public CashManagementDao getCashManagementDao() {
        return cashManagementDao;
    }


    /**
     * Sets the cashManagementDao attribute value.
     * 
     * @param cashManagementDao The cashManagementDao to set.
     */
    public void setCashManagementDao(CashManagementDao cashManagementDao) {
        this.cashManagementDao = cashManagementDao;
    }

    /**
     * Gets the cashDrawerService attribute. 
     * 
     * @return Returns the cashDrawerService.
     */
    public CashDrawerService getCashDrawerService() {
        return cashDrawerService;
    }


    /**
     * Sets the cashDrawerService attribute value.
     * 
     * @param cashDrawerService The cashDrawerService to set.
     */
    public void setCashDrawerService(CashDrawerService cashDrawerService) {
        this.cashDrawerService = cashDrawerService;
    }


    /**
     * Gets the kualiGroupService attribute. 
     * 
     * @return Returns the kualiGroupService.
     */
    public KualiGroupService getKualiGroupService() {
        return kualiGroupService;
    }


    /**
     * Sets the kualiGroupService attribute value.
     * 
     * @param kualiGroupService The kualiGroupService to set.
     */
    public void setKualiGroupService(KualiGroupService kualiGroupService) {
        this.kualiGroupService = kualiGroupService;
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
    
    
}
