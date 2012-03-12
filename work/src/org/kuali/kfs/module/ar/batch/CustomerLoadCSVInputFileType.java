/*
 * Copyright 2011 The Kuali Foundation
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
package org.kuali.kfs.module.ar.batch;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.ArKeyConstants;
import org.kuali.kfs.module.ar.batch.service.CustomerLoadService;
import org.kuali.kfs.module.ar.batch.vo.CustomerAddressCSV;
import org.kuali.kfs.module.ar.batch.vo.CustomerDigesterVO;
import org.kuali.kfs.sys.batch.CsvBatchInputFileTypeBase;
import org.kuali.kfs.sys.exception.ParseException;

/**
 * 
 */
public class CustomerLoadCSVInputFileType extends CsvBatchInputFileTypeBase<CustomerAddressCSV> {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerLoadCSVInputFileType.class);

    private static final String FILE_NAME_PREFIX = "customer_load";
    private static final String FILE_NAME_DELIM = "_";
    
    private CustomerLoadService customerLoadService;
    
    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileName(org.kuali.rice.kim.api.identity.Person, java.lang.Object, java.lang.String)
     */
    public String getFileName(String principalName, Object parsedFileContents, String fileUserIdentifer) {
        return customerLoadService.getFileName(principalName, fileUserIdentifer, FILE_NAME_PREFIX, FILE_NAME_DELIM);
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#getFileTypeIdentifer()
     */
    public String getFileTypeIdentifer() {
        return ArConstants.CustomerLoad.CUSTOMER_CSV_LOAD_FILE_TYPE_IDENTIFIER;
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputFileType#validate(java.lang.Object)
     */
    @Override
    public boolean validate(Object parsedFileContents) {
        List<CustomerDigesterVO> customerVOs = (List<CustomerDigesterVO>)parsedFileContents;
        return customerLoadService.validate(customerVOs);
    }
    
    /**
     * override super class implementation to specify/convert to the expected data structure
     * 
     * For customer load, it will be CustomerDigesterVO
     * 
     * @see org.kuali.kfs.sys.batch.CsvBatchInputFileTypeBase#parse(byte[])
     */
    @Override
    public Object parse(byte[] fileByteContent) throws ParseException {
        
        //super class should have already defined a way to parse the content
        Object parsedContents = super.parse(fileByteContent);
        List<CustomerDigesterVO> customerVOs = (List<CustomerDigesterVO>)convertParsedObjectToVO(parsedContents);
        return customerVOs;    
    }

    /**
     * 
     * @see org.kuali.kfs.sys.batch.BatchInputType#getTitleKey()
     */
    public String getTitleKey() {
        return ArKeyConstants.CustomerLoad.MESSAGE_BATCH_UPLOAD_CSV_TITLE_CUSTOMER;
    }

    public void setCustomerLoadService(CustomerLoadService customerLoadService) {
        this.customerLoadService = customerLoadService;
    }

    public String getAuthorPrincipalName(File file) {
        String[] fileNameParts = StringUtils.split(file.getName(), FILE_NAME_DELIM);
        if (fileNameParts.length > 3) {
            return fileNameParts[2];
        }
        return null;
    }

    /**
     * Convert the parsedFileContents object into CustomerDigesterVO for validation
     * 
     * @see org.kuali.kfs.sys.batch.CsvBatchInputFileTypeBase#convertParsedObjectToVO(java.lang.Object)
     */
    @Override
    protected Object convertParsedObjectToVO(Object parsedContent) {
        List<CustomerDigesterVO> customerVOs = new ArrayList<CustomerDigesterVO>();
        try {
            //  attempt to cast the parsedFileContents into the expected type
            List<Map<String, String>> parseDataList = (List<Map<String, String>>) parsedContent;
            customerVOs = CustomerLoadCSVBuilder.buildCustomerDigestVO(parseDataList);
        }
        catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return customerVOs;
    }
}

