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

import static org.kuali.kfs.sys.fixture.UserNameFixture.KHUNTLEY;

import java.util.Iterator;
import java.util.Set;

import org.kuali.kfs.module.cam.CamsPropertyConstants;
import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.service.AssetTransferService;
import org.kuali.kfs.module.cam.fixture.AssetTransferFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.document.workflow.OrgReviewRoutingData;
import org.kuali.kfs.sys.document.workflow.RoutingAccount;
import org.kuali.kfs.sys.document.workflow.RoutingData;
import org.kuali.rice.kns.service.BusinessObjectService;

@ConfigureContext(session = KHUNTLEY, shouldCommitTransactions = false)
public class AssetTransferDocumentTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(AssetTransferDocumentTest.class);

    AssetTransferService assetTransferService;
    
    public AssetTransferDocumentTest() {
        super();
    }

    private AssetTransferDocument buildTransferDocument(Asset asset) {
        BusinessObjectService boService = SpringContext.getBean(BusinessObjectService.class);
        AssetTransferDocument document = AssetTransferFixture.ASSET_TRANSFER.newAssetTransferDocument();
//        asset.setCapitalAssetNumber(null);
//        asset.setCapitalAssetTypeCode("665");
        
        boService.save(asset);

        document.setAsset(asset);
        document.setCapitalAssetNumber(asset.getCapitalAssetNumber());
        return document;
    }

    public void testSaveApprovedChanges() throws Exception {
        RoutingData organizationRoutingData_a = new RoutingData();
        RoutingData accountRoutingData_a = new RoutingData();

        AssetTransferDocument document = buildTransferDocument(AssetTransferFixture.ACTIVE_CAPITAL_ASSET.newAsset());
        assetTransferService = SpringContext.getBean(AssetTransferService.class);        
        this.assetTransferService.saveApprovedChanges(document);

        document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
        document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
        document.getAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
        
        document.populateRoutingInfo();
        Set<RoutingData> routingInfo_a = document.getRoutingInfo();
        
//        LOG.info("*** Routing:"+document.getRoutingInfo().toString());
        //***********************************************************************************************************************
        Iterator i = routingInfo_a.iterator();
        organizationRoutingData_a =(RoutingData)i.next();
        accountRoutingData_a = (RoutingData)i.next();
        
        Set organizationRoutingSet_a  = organizationRoutingData_a.getRoutingSet();
        Set accountRoutingSet_a       = accountRoutingData_a.getRoutingSet();

//        for (Iterator o = organizationRoutingSet_a.iterator(); o.hasNext();) {
//            Object obj = o.next();
//            if (obj instanceof OrgReviewRoutingData) {
//                OrgReviewRoutingData orgReviewRoutingData = (OrgReviewRoutingData)obj;
//                LOG.info("***Org Data:"+orgReviewRoutingData.toString());
//            }
//        }
//        
//        for (Iterator a = accountRoutingSet_a.iterator(); a.hasNext();) {
//            Object obj = a.next();
//            if (obj instanceof RoutingAccount) {
//                RoutingAccount routingAccount = (RoutingAccount)obj;
//                LOG.info("***Acnt Data:"+routingAccount.toString());
//            }
//        }
        
        //Asset information
        OrgReviewRoutingData orgReviewRoutingData_b = new OrgReviewRoutingData(document.getAsset().getOrganizationOwnerChartOfAccountsCode(), document.getAsset().getOrganizationOwnerAccount().getOrganizationCode());        
        assertTrue(organizationRoutingSet_a.contains(orgReviewRoutingData_b));
        
        RoutingAccount routingAccount_b = new RoutingAccount(document.getAsset().getOrganizationOwnerChartOfAccountsCode(), document.getAsset().getOrganizationOwnerAccountNumber());
        assertTrue(accountRoutingSet_a.contains(routingAccount_b));
               
//        LOG.info("**** ASSET **********************");
//        LOG.info("**** COA     :"+document.getAsset().getOrganizationOwnerChartOfAccountsCode());
//        LOG.info("**** Org Code:"+document.getAsset().getOrganizationOwnerAccount().getOrganizationCode());
//        LOG.info("**** Account :"+document.getAsset().getOrganizationOwnerAccountNumber());
//        LOG.info("*********************************");
        
        
        //Asset tranfer information
        orgReviewRoutingData_b = new OrgReviewRoutingData(document.getOrganizationOwnerChartOfAccountsCode(), document.getOrganizationOwnerAccount().getOrganizationCode());        
        assertTrue(organizationRoutingSet_a.contains(orgReviewRoutingData_b));
        
        routingAccount_b = new RoutingAccount(document.getOrganizationOwnerChartOfAccountsCode(), document.getOrganizationOwnerAccountNumber());
        assertTrue(accountRoutingSet_a.contains(routingAccount_b));
        
//        LOG.info("**** TRANSFER **********************");
//        LOG.info("**** COA     :"+document.getOrganizationOwnerChartOfAccountsCode());
//        LOG.info("**** Org Code:"+document.getOrganizationOwnerAccount().getOrganizationCode());
//        LOG.info("**** Account :"+document.getOrganizationOwnerAccountNumber());
//        LOG.info("*********************************");        
    }    
}
