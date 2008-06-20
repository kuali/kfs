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
package org.kuali.module.budget.service.impl;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.kuali.core.bo.PersistableBusinessObject;
import org.kuali.core.service.BusinessObjectService;
import org.kuali.kfs.annotation.NonTransactional;
import org.kuali.kfs.util.ObjectUtil;
import org.kuali.module.budget.bo.BudgetConstructionPayRateHolding;
import org.springframework.transaction.annotation.Transactional;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class PayrateImportServiceImpl implements org.kuali.module.budget.service.PayrateImportService {
    
    private BusinessObjectService businessObjectService;
    
    /**
     * 
     * @see org.kuali.module.budget.service.PayrateImportService#importFile(java.io.InputStream)
     */
    @Transactional
    public StringBuilder importFile(InputStream fileImportStream) {
        this.businessObjectService.delete(new ArrayList<PersistableBusinessObject>(this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class)));
        BufferedReader fileReader = new BufferedReader(new InputStreamReader(fileImportStream));
        StringBuilder messages = new StringBuilder();
        int importCount = 0;
        
        try {
            while(fileReader.ready()) {
                BudgetConstructionPayRateHolding temp = new BudgetConstructionPayRateHolding();
                String line = fileReader.readLine();
                ObjectUtil.convertLineToBusinessObject(temp, line, DefaultImportFileFormat.fieldLengths, Arrays.asList(DefaultImportFileFormat.fieldNames));
                businessObjectService.save(temp);
                importCount++;
            }
        }
        catch (Exception e) {
            messages.append("Import Aborted \n");
            
            return messages;
        }
        
        messages.append("Import count: " + importCount + "\n");
        messages.append("Import complete \n");
        
        return messages;
    }
    
    /**
     * 
     * @see org.kuali.module.budget.service.PayrateImportService#update()
     */
    @Transactional
    public StringBuilder update() {
        StringBuilder messages = new StringBuilder();
        
        List<BudgetConstructionPayRateHolding> records = (List<BudgetConstructionPayRateHolding>) this.businessObjectService.findAll(BudgetConstructionPayRateHolding.class);
        
        return messages;
    }
    
    /**
     * 
     * @see org.kuali.module.budget.service.PayrateImportService#generatePdf(java.lang.StringBuilder, java.io.ByteArrayOutputStream)
     */
    @NonTransactional
    public void generatePdf(StringBuilder logMessages, ByteArrayOutputStream baos) throws DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();
        
        document.add(new Paragraph(logMessages.toString()));
        
        document.close();
    }

    public void setBusinessObjectService(BusinessObjectService businessObjectService) {
        this.businessObjectService = businessObjectService;
    }
    
    private static class DefaultImportFileFormat {
        private static final int[] fieldLengths = new int[] {11, 8, 50, 5, 4, 3, 3, 10, 8};
        //TODO: use constants for field names
        //TODO: should csf freeze date be used?
        private static final String[] fieldNames = new String[] {"emplid", "positionNumber", "personName", "setidSalary", "salaryAdministrationPlan", "grade", "unionCode", "appointmentRequestedPayRate"};
    }
}
