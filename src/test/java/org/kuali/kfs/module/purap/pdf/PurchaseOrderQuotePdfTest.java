/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2014 The Kuali Foundation
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.kuali.kfs.module.purap.pdf;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;

import org.kuali.kfs.module.purap.businessobject.ItemType;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderItem;
import org.kuali.kfs.module.purap.businessobject.PurchaseOrderVendorQuote;
import org.kuali.kfs.module.purap.document.PurchaseOrderDocument;
import org.kuali.kfs.sys.ConfigureContext;
import org.kuali.kfs.sys.KFSConstants;
import org.kuali.kfs.sys.context.KualiTestBase;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.vnd.businessobject.ContractManager;
import org.kuali.rice.core.api.config.property.ConfigurationService;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.core.api.util.type.KualiDecimal;
import org.kuali.rice.krad.service.BusinessObjectService;
import org.kuali.rice.location.api.country.CountryService;
import org.kuali.rice.location.framework.country.CountryEbo;
import org.kuali.rice.location.impl.country.CountryBo;

@ConfigureContext
public class PurchaseOrderQuotePdfTest extends KualiTestBase {
    PurchaseOrderVendorQuote poqv;
    PurchaseOrderDocument po;
    FileOutputStream fo;
    ByteArrayOutputStream bao = new ByteArrayOutputStream();
    PurchaseOrderQuotePdf poQuotePdf = new PurchaseOrderQuotePdf();

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        BusinessObjectService businessObjectService = SpringContext.getBean(BusinessObjectService.class);
        CountryService countryService = SpringContext.getBean(CountryService.class);
        // Map poCriteria = new HashMap();
        // poCriteria.put("documentNumber", new Integer(291190));
        // Iterator resultIter = (businessObjectService.findMatching(PurchaseOrderDocument.class, poCriteria)).iterator();
        // po = (PurchaseOrderDocument)(resultIter.next());

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
        po.setPurchaseOrderQuoteDueDate(SpringContext.getBean(DateTimeService.class).getCurrentSqlDate());
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

        CountryEbo country = CountryBo.from(countryService.getCountry(KFSConstants.COUNTRY_CODE_UNITED_STATES));
        poqv.setVendorCountry(country);
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
     * This method creates a Purchase Order Quote PDF file. The test will fail if this method fails to generate the Purchase Order
     * Quote PDF file. The Purchase Order Quote PDF file that is created by this method will be removed in the tearDown( ) of this
     * class, so if you want to check how the PO Quote PDF looks like, please remove the line in tearDown( ) that invokes the
     * deletePdf method.
     */
    public void testGeneratePOQuotePDF() throws Exception {

        String environment = SpringContext.getBean(ConfigurationService.class).getPropertyValueAsString(KFSConstants.ENVIRONMENT_KEY);

        poQuotePdf.generatePOQuotePDF(po, poqv, "Bloomington", "BL", getLogoImageName(), bao, environment);
        bao.writeTo(fo);

    }

    private String getLogoImageName() {
        return "work//web-root//static//images//logo_bl.jpg";
    }

}
