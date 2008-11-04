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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.kuali.kfs.module.ar.batch.service.CustomerInvoiceWriteoffBatchService;
import org.kuali.kfs.module.ar.batch.vo.CustomerInvoiceWriteoffBatchVO;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.rice.kim.bo.Person;
import org.kuali.rice.kns.service.DateTimeService;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class CustomerInvoiceWriteoffBatchServiceImpl implements CustomerInvoiceWriteoffBatchService {

    private static final String XML_ROOT_ELEMENT_NAME = "invoiceWriteoffBatch";
    private static final String XML_BATCH_NAMESPACE = "http://www.kuali.org/kfs/ar/customerInvoiceWriteoffBatch";
    
    private DateTimeService dateTimeService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType batchInputFileType;
    private String reportsDirectory;
    
    public boolean loadFiles() {
        // do nothing for now
        return true;
    }
    
    /**
     * 
     * @see org.kuali.kfs.module.ar.batch.service.CustomerInvoiceWriteoffBatchService#createBatchDrop(org.kuali.kfs.module.ar.batch.vo.CustomerInvoiceWriteoffBatchVO)
     */
    public String createBatchDrop(Person person, CustomerInvoiceWriteoffBatchVO writeoffBatchVO) {
        
        org.w3c.dom.Document xmldoc = transformVOtoXml(writeoffBatchVO);
        
        String batchXmlFileName = dropXmlFile(person, xmldoc);
        
        return batchXmlFileName;
    }

    private String getBatchXMLNamespace() {
        return XML_BATCH_NAMESPACE;
    }
    
    private String getBatchFilePathAndName(Person person) {
        
        String filename = batchInputFileType.getFileName(person, "", "");
        
        String filepath = batchInputFileType.getDirectoryPath();
        if (!filepath.endsWith("/")) filepath = filepath + "/";
        
        return filepath + filename;
    }
    
    private String dropXmlFile(Person person, org.w3c.dom.Document xmldoc) {

        //  determine file paths and names
        String filename = getBatchFilePathAndName(person); 
        
        //  setup the file stream
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(filename);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find/create output file at: '" + filename + "'.", e);
        } 
        
        //  setup the output format
        OutputFormat of = new OutputFormat("XML", "UTF-8", true);
        of.setIndent(1);
        of.setIndenting(true);

        //  setup the xml serializer and do the serialization
        Element docElement = xmldoc.getDocumentElement();
        XMLSerializer serializer = new XMLSerializer(fos, of);
        try {
            serializer.asDOMSerializer();
            serializer.serialize(docElement);
        }
        catch (IOException e) {
            throw new RuntimeException("Exception while serializing the DOM Document.", e);
        }
        
        //  close the output stream
        try {
            fos.close();
        }
        catch (IOException e) {
            throw new RuntimeException("Exception while closing the FileOutputStream.", e);
        }
        
        return filename;
    }
    
    private Document transformVOtoXml(CustomerInvoiceWriteoffBatchVO writeoffBatchVO) {

        Document xmldoc = new DocumentImpl();
        Element e = null;
        Element invoicesElement = null;
        Node n = null;
        
        Element root = xmldoc.createElementNS("http://www.kuali.org/kfs/ar/customer", XML_ROOT_ELEMENT_NAME);
        root.setAttribute("xmlns", getBatchXMLNamespace());
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
        
        //  create submittedBy element
        e = xmldoc.createElement("submittedByPrincipalId");
        n = xmldoc.createTextNode(writeoffBatchVO.getSubmittedByPrincipalId());
        e.appendChild(n);
        root.appendChild(e);
        
        //  create submittedOn element
        e = xmldoc.createElement("submittedOn");
        n = xmldoc.createTextNode(dateTimeService.toDateTimeString(writeoffBatchVO.getSubmittedOn()));
        e.appendChild(n);
        root.appendChild(e);
        
        //  create invoices element and list of invoice child elements
        invoicesElement = xmldoc.createElement("invoiceNumbers");
        for (String invoiceNumber : writeoffBatchVO.getInvoiceNumbers()) {
            e = xmldoc.createElement("invoiceNumber");
            n = xmldoc.createTextNode(invoiceNumber);
            e.appendChild(n);
            invoicesElement.appendChild(e);
        }
        root.appendChild(invoicesElement);
        
        xmldoc.appendChild(root);
        
        return xmldoc;
    }

    public void setDateTimeService(DateTimeService dateTimeService) {
        this.dateTimeService = dateTimeService;
    }

    public void setBatchInputFileService(BatchInputFileService batchInputFileService) {
        this.batchInputFileService = batchInputFileService;
    }

    public void setBatchInputFileType(BatchInputFileType batchInputFileType) {
        this.batchInputFileType = batchInputFileType;
    }

    public void setReportsDirectory(String reportsDirectory) {
        this.reportsDirectory = reportsDirectory;
    }
    
    
}   
