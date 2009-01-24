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

import org.kuali.kfs.module.cam.businessobject.Asset;
import org.kuali.kfs.module.cam.document.service.AssetTransferService;
import org.kuali.kfs.module.cam.fixture.AssetTransferFixture;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.rice.kns.service.BusinessObjectService;

@ConfigureContext(session = khuntley, shouldCommitTransactions = false)
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
//  // TODO fix for kim

//    public void testSaveApprovedChanges() throws Exception {
//        RoutingData routingData = new RoutingData();
//        RoutingData organizationRoutingData_a = new RoutingData();
//        RoutingData accountRoutingData_a = new RoutingData();
//        Set organizationRoutingSet_a  = new HashSet();        
//        Set accountRoutingSet_a       = new HashSet();
//        
//        //Creating document using fixture data
//        AssetTransferDocument document = buildTransferDocument(AssetTransferFixture.ACTIVE_CAPITAL_ASSET.newAsset());
//        assetTransferService = SpringContext.getBean(AssetTransferService.class);        
//        this.assetTransferService.saveApprovedChanges(document);
//
//        document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ASSET);
//        document.refreshReferenceObject(CamsPropertyConstants.AssetTransferDocument.ORGANIZATION_OWNER_ACCOUNT);
//        document.getAsset().refreshReferenceObject(CamsPropertyConstants.Asset.ORGANIZATION_OWNER_ACCOUNT);
//        
//        //Populating routing info.
//        document.getDocumentHeader().setFinancialDocumentStatusCode(KFSConstants.DocumentStatusCodes.APPROVED);
////        document.populateRoutingInfo();
////        Set<RoutingData> routingInfo_a = document.getRoutingInfo();
////        
////        //Comparing document data with populated data. 
////        for(Iterator i = routingInfo_a.iterator();i.hasNext();) {
////            routingData = (RoutingData)i.next();        
////            if (routingData.getRoutingTypes().contains(KualiOrgReviewAttribute.class.getSimpleName())) {
////                organizationRoutingSet_a =routingData.getRoutingSet();            
////            } else if (routingData.getRoutingTypes().contains(KualiAccountAttribute.class.getSimpleName())) {
////                accountRoutingSet_a = routingData.getRoutingSet();
////            }
////        }
//        
//        //Asset information
//        OrgReviewRoutingData orgReviewRoutingData_b = new OrgReviewRoutingData(document.getAsset().getOrganizationOwnerChartOfAccountsCode(), document.getAsset().getOrganizationOwnerAccount().getOrganizationCode());        
//        assertTrue(organizationRoutingSet_a.contains(orgReviewRoutingData_b));
//        
//        RoutingAccount routingAccount_b = new RoutingAccount(document.getAsset().getOrganizationOwnerChartOfAccountsCode(), document.getAsset().getOrganizationOwnerAccountNumber());
//        assertTrue(accountRoutingSet_a.contains(routingAccount_b));
//               
//        //Asset tranfer information
//        orgReviewRoutingData_b = new OrgReviewRoutingData(document.getOrganizationOwnerChartOfAccountsCode(), document.getOrganizationOwnerAccount().getOrganizationCode());        
//        assertTrue(organizationRoutingSet_a.contains(orgReviewRoutingData_b));
//        
//        routingAccount_b = new RoutingAccount(document.getOrganizationOwnerChartOfAccountsCode(), document.getOrganizationOwnerAccountNumber());
//        assertTrue(accountRoutingSet_a.contains(routingAccount_b));        
//    }    
}

