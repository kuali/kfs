/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.purap.pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.kuali.Constants;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.core.util.KualiDecimal;
import org.kuali.kfs.bo.Country;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.util.SpringServiceLocator;
import org.kuali.module.purap.bo.ItemType;
import org.kuali.module.purap.bo.PurchaseOrderItem;
import org.kuali.module.purap.bo.PurchaseOrderVendorQuote;
import org.kuali.module.purap.document.PurchaseOrderDocument;
import org.kuali.module.vendor.bo.ContractManager;
import org.kuali.test.WithTestSpringContext;

@WithTestSpringContext
public class PurchaseOrderQuotePdfTest extends KualiTestBase {
    PurchaseOrderVendorQuote poqv;
    PurchaseOrderDocument po;
    FileOutputStream fo;
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();
    
    @Override
    protected void setUp() throws Exception {
        super.setUp();     
        BusinessObjectService businessObjectService = SpringServiceLocator.getBusinessObjectService();
        //Map poCriteria = new HashMap();
        //poCriteria.put("documentNumber", new Integer(291190));
        //Iterator resultIter = (businessObjectService.findMatching(PurchaseOrderDocument.class, poCriteria)).iterator();
        //po = (PurchaseOrderDocument)(resultIter.next());

        po = new PurchaseOrderDocument();
        po.setDeliveryCampusCode("BL");
        po.setPurapDocumentIdentifier(new Integer(1000));
        ContractManager contractManager = new ContractManager();
        contractManager.setContractManagerCode(10);
        contractManager.setContractManagerName("Julia Child");
        contractManager.setContractManagerFaxNumber("800-111-1111");
        contractManager.setContractManagerPhoneNumber("800-222-2222");
        po.setContractManager(contractManager);
        
        po.setDeliveryCityName("Timbuktu");
        po.setDeliveryPostalCode("90210");
        po.setDeliveryStateCode("CA");
        po.setDeliveryCampusCode("BL");
        poqv = new PurchaseOrderVendorQuote();
        po.setPurchaseOrderQuoteDueDate(SpringServiceLocator.getDateTimeService().getCurrentSqlDate());
        poqv.setPurchaseOrder(po);
        poqv.setPurchaseOrderVendorQuoteIdentifier(1000);
        poqv.setVendorName("Dusty's Cellar");
        poqv.setVendorHeaderGeneratedIdentifier(1000);
        poqv.setVendorCityName("Okemos");
        poqv.setVendorCountryCode("US");
        poqv.setVendorLine1Address("1 Dobie Rd");
        poqv.setVendorFaxNumber("517-111-1FAX");
        poqv.setVendorPhoneNumber("1-800-DUSTY-CELL");
        poqv.setVendorPostalCode("48864");
        Map countryKey = new HashMap();
        countryKey.put("postalCountryCode", "US");
        poqv.setVendorCountry((Country)businessObjectService.findByPrimaryKey(Country.class, countryKey));
        PurchaseOrderItem poi = new PurchaseOrderItem();
        ItemType it = new ItemType();
        it.setItemTypeCode("ITEM");
        it.setItemTypeDescription("ITEM");
        poi.setItemType(it);
        poi.setItemTypeCode(it.getItemTypeCode());
        poi.setItemDescription("Turtle Cheesecake");
        poi.setItemIdentifier(1000);
        poi.setItemLineNumber(1);
        poi.setItemQuantity(new KualiDecimal(2));
        poi.setItemUnitOfMeasureCode("piece");
        poi.setItemUnitPrice(new BigDecimal("5.50"));
        ArrayList itemList = new ArrayList();
        itemList.add(poi);
        po.setItems(itemList);
        fo = new FileOutputStream("POQuotePDF.pdf");
    }
    
    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        fo.close();
        bao.close();
        poQuotePdf.deletePdf("", "POQuotePDF.pdf");
        
    }
    
    /**
     * This method creates a Purchase Order Quote PDF file. The test will fail if 
     * this method fails to generate the Purchase Order Quote PDF file. The Purchase
     * Order Quote PDF file that is created by this method will be removed in the
     * tearDown( ) of this class, so if you want to check how the PO Quote PDF looks
     * like, please remove the line in tearDown( ) that invokes the deletePdf method.
     * 
     */
    public void testGeneratePOQuotePDF() throws Exception {

        String environment = SpringServiceLocator.getKualiConfigurationService().getPropertyString(Constants.ENVIRONMENT_KEY);
        
        poQuotePdf.generatePOQuotePDF(po, poqv, "East Lansing", "EL", getLogoImageName(), bao, environment);
        bao.writeTo(fo);

    }

    private String getLogoImageName () {
        return "work//web-root//static//images//logo_bl.jpg";
    }

}
