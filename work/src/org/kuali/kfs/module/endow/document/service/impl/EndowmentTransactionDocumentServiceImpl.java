/*
 * Copyright 2009 The Kuali Foundation.
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
package org.kuali.kfs.module.endow.document.service.impl;

import java.util.List;

import org.kuali.kfs.module.endow.businessobject.ClassCode;
import org.kuali.kfs.module.endow.businessobject.EndowmentTransactionCode;
import org.kuali.kfs.module.endow.businessobject.GLLink;
import org.kuali.kfs.module.endow.businessobject.KemidGeneralLedgerAccount;
import org.kuali.kfs.module.endow.businessobject.Security;
import org.kuali.kfs.module.endow.businessobject.SecurityValuationMethod;
import org.kuali.kfs.module.endow.document.service.ClassCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionCodeService;
import org.kuali.kfs.module.endow.document.service.EndowmentTransactionDocumentService;
import org.kuali.kfs.module.endow.document.service.KEMIDService;
import org.kuali.kfs.module.endow.document.service.SecurityService;
import org.kuali.kfs.module.endow.document.service.SecurityValuationMethodService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.krad.service.BusinessObjectService;

/**
 * This class is the service implementation for the EndowmentTransactionCodeService. This is the default, Kuali provided implementation.
 */
public class EndowmentTransactionDocumentServiceImpl implements EndowmentTransactionDocumentService 
{
    private BusinessObjectService businessObjectService;
    private KEMIDService kemidService;
    private EndowmentTransactionCodeService endowmentTransactionCodeService;

    public String[] getSecurity(String securityID)
    {
        Security security = SpringContext.getBean(SecurityService.class).getByPrimaryKey(securityID);
        if(null == security)
            return null;

        ClassCode classCode  = SpringContext.getBean(ClassCodeService.class).getByPrimaryKey(security.getSecurityClassCode());
        if(null == classCode)
            return null;
        security.setClassCode(classCode);

        EndowmentTransactionCode tranCode  = SpringContext.getBean(EndowmentTransactionCodeService.class).getByPrimaryKey(classCode.getSecurityEndowmentTransactionCode());
        if(null == tranCode)
            return null;
               
        String returnArray[] = new String[5];
        returnArray[0] = security.getDescription(); 
        returnArray[1] = security.getSecurityClassCode() + " - " + classCode.getName();
        returnArray[2] = tranCode.getCode() + " - " + classCode.getName();
        returnArray[3] = new Boolean( security.getClassCode().isTaxLotIndicator()).toString();
        returnArray[4] = security.getId();
        
        
        return returnArray;
    }
    
    /**
     * Gets the security, class and valuationMethod details based on the securityId.
     * @see org.kuali.kfs.module.endow.document.service.EndowmentTransactionDocumentService#getSecurity(java.lang.String)
     */
    public String[] getSecurityForHoldingHistoryValueAdjustment(String securityId) {
        Security security = SpringContext.getBean(SecurityService.class).getByPrimaryKey(securityId);
        if(null == security)
            return null;

        ClassCode classCode  = SpringContext.getBean(ClassCodeService.class).getByPrimaryKey(security.getSecurityClassCode());
        if(null == classCode)
            return null;

        SecurityValuationMethod securityValuation = SpringContext.getBean(SecurityValuationMethodService.class).getByPrimaryKey(classCode.getValuationMethod());
        
        if(null == securityValuation)
            return null;
               
        classCode.setSecurityValuationMethod(securityValuation);
        security.setClassCode(classCode);
        
        String returnArray[] = new String[4];
        returnArray[0] = security.getDescription(); 
        returnArray[1] = classCode.getCodeAndDescription();
        returnArray[2] = securityValuation.getCodeAndDescription();
        returnArray[3] = security.getId();
        
        return returnArray;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.EndowmentTransactionDocumentService#matchChartBetweenKEMIDAndETranCode(java.lang.String, java.lang.String, java.lang.String)
     * 
     * Check if the chart code matches based on the following rule:
     * The ETRAN Code used must have an appropriately identified general ledger object code record; 
     * one that matches the Chart for the KEMID associated general ledger account.   
     * - If the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to I, the chart must match the chart of the active END_KEMID_GL_LNK_T record where the IP_IND_CD is equal to I.
     * - If the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P, the chart must match the chart of the active END_KEMID_GL_LNK_T record where the IP_IND_CD is equal to P.
     * Assume that all inputs are valid.
     */
    public boolean matchChartBetweenKEMIDAndETranCode(String kemid, String etranCode, String ipIndicator){
        boolean matchChartIndicator = false;
        List<KemidGeneralLedgerAccount> kemidGeneralLedgerAccounts = null;
        List<GLLink> glLinks = null;
        String theChartCode = null;
        
        //Get the chart code, it can't be null because each KEMID always have one active income KemidGeneralLedgerAccount.
        //Each KEMID always have one active principal KemidGeneralLedgerAccount if type code --> principal restriction code is not NA
        //This will be valid before checking if the chart codes match.
        kemidGeneralLedgerAccounts = kemidService.getByPrimaryKey(kemid).getKemidGeneralLedgerAccounts();
        String theIpIndicator = null;
        boolean activeIndicatorForKemidGLAccount = false;
        
        for (KemidGeneralLedgerAccount kemidGeneralLedgerAccount:kemidGeneralLedgerAccounts){
           theIpIndicator = kemidGeneralLedgerAccount.getIncomePrincipalIndicatorCode();
           activeIndicatorForKemidGLAccount = kemidGeneralLedgerAccount.isActive();
           if (theIpIndicator.equalsIgnoreCase(ipIndicator) && activeIndicatorForKemidGLAccount ){
               theChartCode = kemidGeneralLedgerAccount.getChartCode();
               break;
           }
        }
        glLinks = endowmentTransactionCodeService.getByPrimaryKey(etranCode).getGlLinks();
        if (theChartCode != null){
            for (GLLink glLink:glLinks){
                if (glLink.getChartCode().equalsIgnoreCase(theChartCode) && glLink.isActive()){
                    matchChartIndicator = true;
                    break;
                }
            }
        }
        return matchChartIndicator;
    }
    
    /**
     * @see org.kuali.kfs.module.endow.document.service.EndowmentTransactionDocumentService#matchChartBetweenKEMIDAndETranCode(java.lang.String, java.lang.String, java.lang.String)
     * 
     * Check if the chart code matches based on the following rule:
     * The Securities ETRAN Code used must have an appropriately identified general ledger object code record; 
     * one that matches the Chart for the KEMID associated general ledger account.   
     * - If the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to I, the chart must match the chart of the active END_KEMID_GL_LNK_T record where the IP_IND_CD is equal to I.
     * - If the END_TRAN_LN_T: TRAN_IP_IND_CD is equal to P, the chart must match the chart of the active END_KEMID_GL_LNK_T record where the IP_IND_CD is equal to P.
     * Assume that all inputs are valid.
     */
    public boolean matchChartBetweenSecurityAndETranCode(Security security, String kemid, String ipIndicator){
        boolean matchChartIndicator = false;
        List<KemidGeneralLedgerAccount> kemidGeneralLedgerAccounts = null;
        List<GLLink> glLinks = null;
        String theChartCode = null;
        
        //Get the chart code, it can't be null because each KEMID always have one active income KemidGeneralLedgerAccount.
        //Each KEMID always have one active principal KemidGeneralLedgerAccount if type code --> principal restriction code is not NA
        //This will be valid before checking if the chart codes match.
        kemidGeneralLedgerAccounts = kemidService.getByPrimaryKey(kemid).getKemidGeneralLedgerAccounts();
        String theIpIndicator = null;
        boolean activeIndicatorForKemidGLAccount = false;
        
        for (KemidGeneralLedgerAccount kemidGeneralLedgerAccount:kemidGeneralLedgerAccounts){
           theIpIndicator = kemidGeneralLedgerAccount.getIncomePrincipalIndicatorCode();
           activeIndicatorForKemidGLAccount = kemidGeneralLedgerAccount.isActive();
           if (theIpIndicator.equalsIgnoreCase(ipIndicator) && activeIndicatorForKemidGLAccount ){
               theChartCode = kemidGeneralLedgerAccount.getChartCode();
               break;
           }
        }
        
        //Obtain Etran code from security
        security.refreshNonUpdateableReferences();
        //Obtain Security GL Links
        glLinks = endowmentTransactionCodeService.getByPrimaryKey(security.getClassCode().getSecurityEndowmentTransactionCode()).getGlLinks();
        
        if (theChartCode != null){
            for (GLLink glLink:glLinks){
                if (glLink.getChartCode().equalsIgnoreCase(theChartCode) && glLink.isActive()){
                    matchChartIndicator = true;
                    break;
                }
            }
        }
        return matchChartIndicator;
    }
    /**
     * Gets the businessObjectService.
     * 
     * @return businessObjectService
     */
    public BusinessObjectService getBusinessObjectService() {
        return businessObjectService;
    }

    /**
     * Sets the businessObjectService
     * 
     * @param businessObjectService
     */
    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    /**
     * Gets the kemidService.
     * 
     * @return kemidService
     */
    public KEMIDService getKemidService(){
        return kemidService;
    }
    
    /**
     * Sets the kemidService.
     * 
     * @param kemidService
     */
    public void setKemidService (KEMIDService kemidService){
        this.kemidService = kemidService; 
    }
    
    /**
     * Gets the endowmentTransactionCodeService.
     * 
     * @return endowmentTransactionCodeService
     */
    public EndowmentTransactionCodeService getEndowmentTransactionCodeService(){
        return endowmentTransactionCodeService;
    }
    
    /**
     * Sets the endowmentTransactionCodeService.
     * 
     * @param endowmentTransactionCodeService
     */
    public void setEndowmentTransactionCodeService (EndowmentTransactionCodeService endowmentTransactionCodeService){
        this.endowmentTransactionCodeService = endowmentTransactionCodeService; 
    }

}
