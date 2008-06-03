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
package org.kuali.module.capitalAssetBuilder.service.impl;

import java.math.BigDecimal;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.util.StringUtils;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.KFSConstants;
//import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.cams.bo.Pretag;
import org.kuali.module.capitalAssetBuilder.batch.PreAssetTaggingInputFileType;


/**
import org.kuali.module.cams.service.CamsReportService;
import org.kuali.module.cams.util.ObjectUtil;
import org.kuali.module.cams.util.ReportRegistry;
**/

public class PreAssetTaggingServiceImpl implements PreAssetTaggingService {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PreAssetTaggingServiceImpl.class);
    private BusinessObjectService businessObjectService;
    private PersistableBusinessObject persistableBusinessObject;
    private PreAssetTaggingInputFileType preAssetTaggingInputFileType;
    private static final String DATEFORMAT = "MM/dd/yyyy";

/**
* This method copies the pre-asset Tagging entries from a csv flat file
*/
    public void copyPreAssetTaggingEntries(){

        String filename = preAssetTaggingInputFileType.getDirectoryPath() + "." + preAssetTaggingInputFileType.getFileExtension();
        String separator = ",";
//        String fieldNames = "purchaseOrderNumber,lineItemNumber,quantityInvoiced,vendorName,assetTopsDescription,organizationInventoryName,pretagCreateDate,chartOfAccountsCode,organizationCode";
        

        File uploadFile = new File(filename);
        if (!uploadFile.exists()) {
            LOG.error("File " + filename + " does not exist.");
            throw new RuntimeException("File does not exist");
        }
        
        BufferedReader input = null;
        try {
            input = new BufferedReader(new FileReader(filename));
            String line = null;
            while ((line = input.readLine()) != null) {
                Pretag pretag = new Pretag();
/*
 * example: http://www.jdocs.com/spring/1.2.8/org/springframework/beans/propertyeditors/StringArrayPropertyEditor.html 
 * also org.springframework.beans.propertyeditors package
 * 
 */
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
                           
                persistableBusinessObject = (Pretag) businessObjectService.findByPrimaryKey(Pretag.class, map);
                
                if (persistableBusinessObject == null) {

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

 //                   ObjectUtil.convertLineToBusinessObject(pretag, line, separator, fieldNames );
                    businessObjectService.save(pretag);
                }
            }
        }
        catch (Exception ex) {
            LOG.error("PreAssetTaggingService() Error reading file", ex);
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
    }
    /**
     * Gets the businessObjectService attribute.
     * 
     * @return Returns the businessObjectService.
     */
    protected BusinessObjectService getBusinessObjectService() {
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
     * Gets the preAssetTaggingInputFileType attribute.
     * 
     * @return Returns the preAssetTaggingInputFileType.
     */
    protected PreAssetTaggingInputFileType getPreAssetTaggingInputFileType() {
        return preAssetTaggingInputFileType;
    }

    /**
     * Sets the preAssetTaggingInputFileType attribute value.
     * 
     * @param preAssetTaggingInputFileType The preAssetTaggingInputFileType to set.
     */
    public void setPreAssetTaggingInputFileType(PreAssetTaggingInputFileType preAssetTaggingInputFileType) {
        this.preAssetTaggingInputFileType = preAssetTaggingInputFileType;
    }
   
}