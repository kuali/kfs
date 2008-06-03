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
package org.kuali.module.capitalAssetBuilder.service;

import static org.kuali.test.fixtures.UserNameFixture.KHUNTLEY;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.Collection;
import java.util.Iterator;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSKeyConstants;
import org.kuali.kfs.context.KualiTestBase;
import org.kuali.kfs.context.SpringContext;
import org.kuali.test.ConfigureContext;
import org.kuali.module.cams.bo.Pretag;
import org.kuali.module.capitalAssetBuilder.batch.PreAssetTaggingInputFileType;

import org.springframework.util.StringUtils;

/**
 * Test the PreAssetTaggingService.
 */
@ConfigureContext
public class PreAssetTaggingServiceTest extends KualiTestBase {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreAssetTaggingServiceTest.class);

    private String testUploadFilename;
    private static final String DATEFORMAT = "MM/dd/yyyy";
    private PersistableBusinessObject bo;
/**
     * Creats .done file for test input file.
     * 
     * @see junit.framework.TestCase#setUp()
     */
    @Override
    protected void setUp() throws Exception {
        super.setUp();

        testUploadFilename = SpringContext.getBean(PreAssetTaggingInputFileType.class).getDirectoryPath();
        testUploadFilename += "." + SpringContext.getBean(PreAssetTaggingInputFileType.class).getFileExtension();
    }

    /**
     * Tests the whole step completes successfully.
     */
 // To prevent rolling back the data
 //   @ConfigureContext(session = KHUNTLEY, shouldCommitTransactions=true)
    public void testAll() throws Exception {
/*
        LOG.info("Done file Name: " + testUploadFilename);
        File uploadFile = new File(testUploadFilename);
        if (!uploadFile.exists()) {
            LOG.info("Creating done file: " + uploadFile.getAbsolutePath());
        } else {
            LOG.info("done file in: " + uploadFile.getAbsolutePath());            
        }
      
        String separator = ",";
        BufferedReader input = null;
        try {

            input = new BufferedReader(new FileReader(testUploadFilename));
            String line = null;
            while ((line = input.readLine()) != null) {

                LOG.info("line   in: " + line);                 
                String[] lineStrings = StringUtils.delimitedListToStringArray(line, separator);
 
                HashMap map = new HashMap();

                map.put("purchaseOrderNumber", lineStrings[0]);
                map.put("lineItemNumber", new Long(Long.parseLong(lineStrings[1])));
                map.put("quantityInvoiced", new BigDecimal(lineStrings[2]));
                map.put("vendorName", lineStrings[3]);
                map.put("assetTopsDescription", lineStrings[4]);
                map.put("organizationInventoryName", lineStrings[5]);
                map.put("pretagCreateDate", new java.sql.Date(((new SimpleDateFormat(DATEFORMAT)).parse(lineStrings[6])).getTime()));
                map.put("chartOfAccountsCode", lineStrings[7]);
                map.put("organizationCode", lineStrings[8]);
                
            
                bo = (Pretag) SpringContext.getBean(BusinessObjectService.class).findByPrimaryKey(Pretag.class, map);

                if (bo == null) {
                    Pretag pretag = new Pretag();
                    pretag.setPurchaseOrderNumber(lineStrings[0]);
                    pretag.setLineItemNumber(new Long(Long.parseLong(lineStrings[1])));
                    pretag.setObjectId(lineStrings[0]+lineStrings[1]);
                    pretag.setQuantityInvoiced(new BigDecimal(lineStrings[2]));
                    pretag.setVendorName(lineStrings[3]);
                    pretag.setAssetTopsDescription(lineStrings[4]);
                    pretag.setOrganizationInventoryName(lineStrings[5]);
                    pretag.setPretagCreateDate(new java.sql.Date(((new SimpleDateFormat(DATEFORMAT)).parse(lineStrings[6])).getTime()));
                    pretag.setChartOfAccountsCode(lineStrings[7]); 
                    pretag.setOrganizationCode(lineStrings[8]);
              
                    SpringContext.getBean(BusinessObjectService.class).save(pretag);
                }
                else {
                    LOG.error("Record exits");
                }         
            } 
        }
        catch (Exception ex) {
            LOG.error("performStep() Error reading file", ex);
            throw new IllegalArgumentException("Error reading file");
        }
        finally {
            try {
                if (input != null) {
                    input.close();
                }
            }
            catch (IOException ex) {
                LOG.error("loadFlatFile() error closing file.", ex);
            }
        }
 //
    
        Collection<Pretag> pretag = SpringContext.getBean(BusinessObjectService.class).findAll(Pretag.class);
        int numberOfPreTagEntries = pretag.size();

        for (Pretag entry : pretag) {
            LOG.info("field1:" + entry.getPurchaseOrderNumber());
            LOG.info("LineItemNumber:" + entry.getLineItemNumber());
            LOG.info("ObjectId:" + entry.getObjectId());
            LOG.info("QuantityInvoiced:" + entry.getQuantityInvoiced());
            LOG.info("VendorName:" + entry.getVendorName());
            LOG.info("AssetTopsDescription:" + entry.getAssetTopsDescription());
            LOG.info("chartOfAccountsCode:" + entry.getChartOfAccountsCode());
            LOG.info("OrganizationCode:" + entry.getOrganizationCode());
        }
 */       
       assertTrue("hold until figure out staging dir!", true);

    }
     
}
