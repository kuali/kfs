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
package org.kuali.kfs.module.cam.document;

import static org.kuali.kfs.sys.fixture.UserNameFixture.khuntley;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.document.routing.attribute.KualiAccountAttribute;
import org.kuali.kfs.sys.document.routing.attribute.KualiOrgReviewAttribute;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;

@ConfigureContext(session = khuntley, shouldCommitTransactions = false)
public class EquipmentLoanOrReturnDocumentTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(EquipmentLoanOrReturnDocumentTest.class);

    public EquipmentLoanOrReturnDocumentTest() {
        super();
    }

    private EquipmentLoanOrReturnDocument buildDocument() {
        EquipmentLoanOrReturnDocument document = new EquipmentLoanOrReturnDocument();        
        document.setCapitalAssetNumber(new Long(1920));
        document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
        return document;
    }

    public void testRouteInformation() throws Exception {
        RoutingData routingData = new RoutingData();
        RoutingData organizationRoutingData_a = new RoutingData();
        RoutingData accountRoutingData_a = new RoutingData();

        Set organizationRoutingSet_a  = new HashSet();        
        Set accountRoutingSet_a       = new HashSet();
        
        EquipmentLoanOrReturnDocument document =this.buildDocument();
        document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
        document.getAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
        
        //Populating routing info.
        document.getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
        document.populateRoutingInfo();
        Set<RoutingData> routingInfo_a = document.getRoutingInfo();
                
        //Comparing document data with populated data. 
        for(Iterator i = routingInfo_a.iterator();i.hasNext();) {
            routingData = (RoutingData)i.next();        
            if (routingData.getRoutingTypes().contains(KualiOrgReviewAttribute.class.getSimpleName())) {
                organizationRoutingSet_a =routingData.getRoutingSet();            
            } else if (routingData.getRoutingTypes().contains(KualiAccountAttribute.class.getSimpleName())) {
                accountRoutingSet_a = routingData.getRoutingSet();
            }
        }
                
//      for (Iterator o = organizationRoutingSet_a.iterator(); o.hasNext();) {
//      Object obj = o.next();
//      LOG.info("***Org***"+obj.toString());          
//      if (obj instanceof OrgReviewRoutingData) {
//          OrgReviewRoutingData orgReviewRoutingData = (OrgReviewRoutingData)obj;
//          LOG.info("***Org Data:"+orgReviewRoutingData.toString());
//      }
//  }
//  
//  for (Iterator a = accountRoutingSet_a.iterator(); a.hasNext();) {
//      Object obj = a.next();
//      LOG.info("***Actn***"+obj.toString());          
//      if (obj instanceof RoutingAccount) {
//          RoutingAccount routingAccount = (RoutingAccount)obj;
//          LOG.info("***Acnt Data:"+routingAccount.toString());
//      }
//  }
        
        
        //Asset information
        OrgReviewRoutingData orgReviewRoutingData_b = new OrgReviewRoutingData(document.getAsset().getOrganizationOwnerChartOfAccountsCode(), document.getAsset().getOrganizationOwnerAccount().getOrganizationCode());        
        assertTrue(organizationRoutingSet_a.contains(orgReviewRoutingData_b));
        
        RoutingAccount routingAccount_b = new RoutingAccount(document.getAsset().getOrganizationOwnerChartOfAccountsCode(), document.getAsset().getOrganizationOwnerAccountNumber());
        assertTrue(accountRoutingSet_a.contains(routingAccount_b));               
    }    
}

