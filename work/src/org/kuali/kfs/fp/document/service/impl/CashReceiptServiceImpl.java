/*
 * Copyright (c) 2004, 2005 The National Association of College and University Business Officers,
 * Cornell University, Trustees of Indiana University, Michigan State University Board of Trustees,
 * Trustees of San Joaquin Delta College, University of Hawai'i, The Arizona Board of Regents on
 * behalf of the University of Arizona, and the r*smart group.
 * 
 * Licensed under the Educational Community License Version 1.0 (the "License"); By obtaining,
 * using and/or copying this Original Work, you agree that you have read, understand, and will
 * comply with the terms and conditions of the Educational Community License.
 * 
 * You may obtain a copy of the License at:
 * 
 * http://kualiproject.org/license.html
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING
 * BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.kuali.module.financial.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.Constants;
import org.kuali.PropertyConstants;
import org.kuali.core.bo.user.KualiUser;
import org.kuali.core.document.DocumentHeader;
import org.kuali.core.exceptions.InfrastructureException;
import org.kuali.core.exceptions.UnknownDocumentIdException;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.GlobalVariables;
import org.kuali.core.workflow.service.KualiWorkflowDocument;
import org.kuali.core.workflow.service.WorkflowDocumentService;
import org.kuali.module.financial.document.CashReceiptDocument;
import org.kuali.module.financial.service.CashReceiptService;

import edu.iu.uis.eden.exception.DocumentNotFoundException;
import edu.iu.uis.eden.exception.WorkflowException;

public class CashReceiptServiceImpl implements CashReceiptService {
    private static final String TEST_CASH_RECEIPT_CAMPUS_CD = "HI";
    private static final String TEST_CASH_RECEIPT_VERIFICATION_UNIT = "HAWAII_CR_VERIFICATION_UNIT";


    private BusinessObjectService businessObjectService;
    private WorkflowDocumentService workflowDocumentService;


    /**
     * @see org.kuali.module.financial.service.CashReceiptService#getCashReceiptVerificationUnitWorkgroupNameByCampusCode(java.lang.String)
     */
    public String getCashReceiptVerificationUnitForCampusCode(String campusCode) {
        String vunit = null;

        if (StringUtils.isBlank(campusCode)) {
            throw new IllegalArgumentException("invalid (blank) campusCode");
        }

        // TODO: pretend that a lookup is actually happening
        if (campusCode.equals(TEST_CASH_RECEIPT_CAMPUS_CD)) {
            vunit = TEST_CASH_RECEIPT_VERIFICATION_UNIT;
        }
        else {
            vunit = Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_VERIFICATION_UNIT;
        }

        return vunit;
    }


    /**
     * @see org.kuali.module.financial.service.CashReceiptService#getCampusCodeForCashReceiptVerificationUnit(java.lang.String)
     */
    public String getCampusCodeForCashReceiptVerificationUnit(String unitName) {
        String campusCode = null;

        if (StringUtils.isBlank(unitName)) {
            throw new IllegalArgumentException("invalid (blank) unitName");
        }

        // TODO: pretend that a lookup is actually happening
        if (unitName.equals(TEST_CASH_RECEIPT_VERIFICATION_UNIT)) {
            campusCode = TEST_CASH_RECEIPT_CAMPUS_CD;
        }
        else {
            campusCode = Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_CAMPUS_LOCATION_CODE;
        }

        return campusCode;
    }


    /**
     * @see org.kuali.module.financial.service.CashReceiptService#getCashReceiptVerificationUnit(org.kuali.core.bo.user.KualiUser)
     */
    public String getCashReceiptVerificationUnitForUser(KualiUser user) {
        String unitName = null;

        if (user == null) {
            throw new IllegalArgumentException("invalid (null) user");
        }

        // TODO: pretend that a lookup is actually happening
        unitName = Constants.CashReceiptConstants.DEFAULT_CASH_RECEIPT_VERIFICATION_UNIT;

        return unitName;
    }


    /**
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
     * @param verificationUnit
     * @param statii
     * @return List of CashReceiptDocument instances with their associated workflowDocuments populated
     */
    public List getPopulatedCashReceipts(String verificationUnit, String[] statii) {
        Map queryCriteria = buildCashReceiptCriteriaMap(verificationUnit, statii);

        List documents = new ArrayList(getBusinessObjectService().findMatchingOrderBy(CashReceiptDocument.class, queryCriteria, PropertyConstants.FINANCIAL_DOCUMENT_NUMBER, true));

        populateWorkflowFields(documents);

        return documents;
    }


    /**
     * @param workgroupName
     * @param statii
     * @return Map
     */
    private Map buildCashReceiptCriteriaMap(String workgroupName, String[] statii) {
        Map queryCriteria = new HashMap();

        if (statii.length == 1) {
            queryCriteria.put(Constants.CashReceiptConstants.CASH_RECEIPT_DOC_HEADER_STATUS_CODE_PROPERTY_NAME, statii[0]);
        }
        else if (statii.length > 0) {
            List<String> statusList = Arrays.asList(statii);
            queryCriteria.put(Constants.CashReceiptConstants.CASH_RECEIPT_DOC_HEADER_STATUS_CODE_PROPERTY_NAME, statusList);
        }

        String campusLocationCode = getCampusCodeForCashReceiptVerificationUnit(workgroupName);
        queryCriteria.put(Constants.CashReceiptConstants.CASH_RECEIPT_CAMPUS_LOCATION_CODE_PROPERTY_NAME, campusLocationCode);

        return queryCriteria;
    }

    /**
     * Populates the workflowDocument field of each CashReceiptDocument in the given List
     * 
     * @param documents
     */
    private void populateWorkflowFields(List documents) {
        for (Iterator i = documents.iterator(); i.hasNext();) {
            CashReceiptDocument cr = (CashReceiptDocument) i.next();

            KualiWorkflowDocument workflowDocument = null;
            DocumentHeader docHeader = cr.getDocumentHeader();
            try {
                Long documentHeaderId = Long.valueOf(docHeader.getFinancialDocumentNumber());
                KualiUser user = GlobalVariables.getUserSession().getKualiUser();

                workflowDocument = getWorkflowDocumentService().createWorkflowDocument(documentHeaderId, user);
            }
            catch (DocumentNotFoundException e) {
                throw new UnknownDocumentIdException("no document found for documentHeaderId '" + docHeader.getFinancialDocumentNumber() + "'", e);
            }
            catch (WorkflowException e) {
                throw new InfrastructureException("unable to retrieve workflow document for documentHeaderId '" + docHeader.getFinancialDocumentNumber() + "'", e);
            }

            docHeader.setWorkflowDocument(workflowDocument);
        }
    }


    // injection-molding
    /**
     * @return current value of businessObjectService.
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService attribute value.
     * 
     * @param businessObjectService The businessObjectService to set.
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }


    /**
     * @return current value of workflowDocumentService.
     */
    public WorkflowDocumentService getWorkflowDocumentService() {
        return workflowDocumentService;
    }

    /**
     * Sets the workflowDocumentService attribute value.
     * 
     * @param workflowDocumentService The workflowDocumentService to set.
     */
    public void setWorkflowDocumentService(WorkflowDocumentService workflowDocumentService) {
        this.workflowDocumentService = workflowDocumentService;
    }
}
