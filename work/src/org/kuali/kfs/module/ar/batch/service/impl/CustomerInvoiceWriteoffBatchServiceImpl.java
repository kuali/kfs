/*
  * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.ar.batch.service.impl;

import java.awt.Color;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.kuali.kfs.module.ar.ArConstants;
import org.kuali.kfs.module.ar.batch.service.CustomerInvoiceWriteoffBatchService;
import org.kuali.kfs.module.ar.batch.vo.CustomerInvoiceWriteoffBatchVO;
import org.kuali.kfs.module.ar.businessobject.Customer;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService;
import org.kuali.kfs.module.ar.document.service.CustomerService;
import org.kuali.kfs.sys.batch.BatchInputFileType;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.kuali.kfs.sys.exception.ParseException;
import org.kuali.rice.core.api.datetime.DateTimeService;
import org.kuali.rice.kew.api.exception.WorkflowException;
import org.kuali.rice.kim.api.identity.Person;
import org.kuali.rice.kim.api.identity.entity.Entity;
import org.kuali.rice.kim.api.services.KimApiServiceLocator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.lowagie.text.Chunk;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;

public class CustomerInvoiceWriteoffBatchServiceImpl implements CustomerInvoiceWriteoffBatchService {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(CustomerInvoiceWriteoffBatchServiceImpl.class);

    private static final String XML_ROOT_ELEMENT_NAME = "invoiceWriteoffBatch";
    private static final String XML_BATCH_NAMESPACE = "http://www.kuali.org/kfs/ar/customerInvoiceWriteoffBatch";
    private static final String BATCH_FILE_KEY = "BATCH-FILE";
    private static final String WORKFLOW_DOC_ID_PREFIX = " - WITH WORKFLOW DOCID: ";

    private CustomerService customerService;
    private CustomerInvoiceDocumentService invoiceDocumentService;
    private DateTimeService dateTimeService;
    private BatchInputFileService batchInputFileService;
    private BatchInputFileType batchInputFileType;
    private String reportsDirectory;

    public CustomerInvoiceWriteoffBatchServiceImpl() {}

    @Override
    public boolean loadFiles() {
        LOG.info("Beginning processing of all available files for AR Customer Invoice Writeoff Batch Documents.");

        boolean result = true;

        //  create a list of the files to process
        List<String> fileNamesToLoad = getListOfFilesToProcess();
        LOG.info("Found " + fileNamesToLoad.size() + " file(s) to process.");
        boolean anyFilesFound = (fileNamesToLoad.size() > 0);

        //  create the pdf doc
        com.lowagie.text.Document pdfdoc = null;
        if (anyFilesFound) {
            pdfdoc = getPdfDoc();
        }

        //  process each file in turn
        List<String> processedFiles = new ArrayList<String>();
        for (String inputFileName : fileNamesToLoad) {

            LOG.info("Beginning processing of filename: " + inputFileName + ".");

            //  setup the results reporting
            writeFileNameSectionTitle(pdfdoc, inputFileName);

            //  load the file
            boolean success = false;
            try {
                success = loadFile(inputFileName, pdfdoc);
            }
            catch (Exception e) {
                LOG.error("An unhandled error occurred.  " + e.getMessage());
                writeInvoiceSectionMessage(pdfdoc, "ERROR - Unhandled exception caught.");
                writeInvoiceSectionMessage(pdfdoc, e.getMessage());
            }
            result &= success;

            //  handle result
            if (success) {
                result &= true;
                writeInvoiceSectionMessage(pdfdoc, "File successfully completed processing.");
                processedFiles.add(inputFileName);
            }
            else {
                writeInvoiceSectionMessage(pdfdoc, "File failed to process successfully.");
                result &= false;
            }
        }

        //  if we've written anything, then spool it out to the file
        if (pdfdoc != null) {
            pdfdoc.close();
        }

        //  remove done files
        removeDoneFiles(processedFiles);

        return result;
    }

    /**
     * Clears out associated .done files for the processed data files.
     */
    protected void removeDoneFiles(List<String> dataFileNames) {
        for (String dataFileName : dataFileNames) {
            String doneFileName = doneFileName(dataFileName);
            File doneFile = new File(doneFileName);
            if (doneFile.exists()) {
                doneFile.delete();
            }
        }
    }

    public boolean loadFile(String fileName, com.lowagie.text.Document pdfdoc) {

        boolean result = true;

        //  load up the file into a byte array
        byte[] fileByteContent = safelyLoadFileBytes(fileName);

        //  parse the file against the XSD schema and load it into an object
        LOG.info("Attempting to parse the file using Apache Digester.");
        Object parsedObject = null;
        try {
            parsedObject = batchInputFileService.parse(batchInputFileType, fileByteContent);
        }
        catch (ParseException e) {
            LOG.error("Error parsing batch file: " + e.getMessage());
            writeInvoiceSectionMessage(pdfdoc, "Error parsing batch file: " + e.getMessage());
            throw new ParseException(e.getMessage());
        }

        //  make sure we got the type we expected, then cast it
        if (!(parsedObject instanceof CustomerInvoiceWriteoffBatchVO)) {
            LOG.error("Parsed file was not of the expected type.  Expected [" + CustomerInvoiceWriteoffBatchVO.class + "] but got [" + parsedObject.getClass() + "].");
            writeInvoiceSectionMessage(pdfdoc, "Parsed file was not of the expected type.  Expected [" + CustomerInvoiceWriteoffBatchVO.class + "] but got [" + parsedObject.getClass() + "].");
            throw new RuntimeException("Parsed file was not of the expected type.  Expected [" + CustomerInvoiceWriteoffBatchVO.class + "] but got [" + parsedObject.getClass() + "].");
        }

        //  convert to the real object type
        CustomerInvoiceWriteoffBatchVO batchVO = (CustomerInvoiceWriteoffBatchVO) parsedObject;

        LOG.info("Beginning validation and preparation of batch file.");
        createCustomerInvoiceWriteoffDocumentsFromBatchVO(batchVO, pdfdoc);

        return result;
    }

    /**
     *
     * @see org.kuali.kfs.module.ar.document.service.CustomerInvoiceWriteoffDocumentService#createCustomerInvoiceWriteoffDocumentsFromBatchVO(org.kuali.kfs.module.ar.batch.vo.CustomerInvoiceWriteoffBatchVO)
     */
    protected void createCustomerInvoiceWriteoffDocumentsFromBatchVO(CustomerInvoiceWriteoffBatchVO batchVO, com.lowagie.text.Document pdfdoc) {

        //  retrieve the Person from the batch
        Entity entity = KimApiServiceLocator.getIdentityService().getEntityByPrincipalName(batchVO.getSubmittedByPrincipalName());
        if (entity == null) {
            throw new RuntimeException("The Person who initiated this batch could not be retrieved.");
        }

        String createdOn = batchVO.getSubmittedOn();

        //  retrieve the user note
        String note = batchVO.getNote();

        //  add submittedOn and submittedBy to the pdf
        writeInvoiceSectionMessage(pdfdoc, "Batch Submitted By: " + batchVO.getSubmittedByPrincipalName());
        writeInvoiceSectionMessage(pdfdoc, "Batch Submitted On: " + batchVO.getSubmittedOn());
        if (StringUtils.isNotBlank(note)) {
            writeInvoiceSectionMessage(pdfdoc, "NOTE: " + note);
        }

        //  create a new Invoice Writeoff document for each invoice number in the batch file
        boolean succeeded = true;
        boolean customerNoteIsSet = false;
        String writeoffDocNumber = null;
        for (String invoiceNumber : batchVO.getInvoiceNumbers()) {

            //  set the customer note
            if (!customerNoteIsSet) {
                Customer customer = invoiceDocumentService.getCustomerByInvoiceDocumentNumber(invoiceNumber);
                if (customer != null) {
                    customerService.createCustomerNote(customer.getCustomerNumber(), note);
                    customerNoteIsSet = true;
                }
            }

            //  write the doc # we're trying to write off
            writeInvoiceSectionTitle(pdfdoc, "INVOICE DOC#: " + invoiceNumber);

            //  attempt to create the writeoff document
            succeeded = true;
            writeoffDocNumber = null;
            try {
                writeoffDocNumber = getInvoiceWriteoffDocumentService().createCustomerInvoiceWriteoffDocument(invoiceNumber, note);
            }
            catch (WorkflowException e) {
                succeeded = false;
                writeInvoiceSectionMessage(pdfdoc, "ERROR - Failed to create and route the Invoice Writeoff Document.");
                writeInvoiceSectionMessage(pdfdoc, "EXCEPTION DETAILS: " + e.getMessage());
            }

            //  write the successful information if we got it
            if (succeeded) {
                if (StringUtils.isNotBlank(writeoffDocNumber)) {
                    writeInvoiceSectionMessage(pdfdoc, "SUCCESS - Created new Invoice Writeoff Document #" + writeoffDocNumber);
                }
                else {
                    writeInvoiceSectionMessage(pdfdoc, "FAILURE - No error occurred, but a new Invoice Writeoff Document number was not created.  Check the logs.");
                }
            }
        }
    }

    /**
     *
     * Accepts a file name and returns a byte-array of the file name contents, if possible.
     *
     * Throws RuntimeExceptions if FileNotFound or IOExceptions occur.
     *
     * @param fileName String containing valid path & filename (relative or absolute) of file to load.
     * @return A Byte Array of the contents of the file.
     */
    protected byte[] safelyLoadFileBytes(String fileName) {

        InputStream fileContents;
        byte[] fileByteContent;
        try {
            fileContents = new FileInputStream(fileName);
        }
        catch (FileNotFoundException e1) {
            LOG.error("Batch file not found [" + fileName + "]. " + e1.getMessage());
            throw new RuntimeException("Batch File not found [" + fileName + "]. " + e1.getMessage());
        }
        try {
            fileByteContent = IOUtils.toByteArray(fileContents);
        }
        catch (IOException e1) {
            LOG.error("IO Exception loading: [" + fileName + "]. " + e1.getMessage());
            throw new RuntimeException("IO Exception loading: [" + fileName + "]. " + e1.getMessage());
        }
        return fileByteContent;
    }

    protected List<String> getListOfFilesToProcess() {

        //  create a list of the files to process
        List<String> fileNamesToLoad = batchInputFileService.listInputFileNamesWithDoneFile(batchInputFileType);

        if (fileNamesToLoad == null) {
            LOG.error("BatchInputFileService.listInputFileNamesWithDoneFile(" +
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
            throw new RuntimeException("BatchInputFileService.listInputFileNamesWithDoneFile(" +
                    batchInputFileType.getFileTypeIdentifer() + ") returned NULL which should never happen.");
        }

        //  filenames returned should never be blank/empty/null
        for (String inputFileName : fileNamesToLoad) {
            if (StringUtils.isBlank(inputFileName)) {
                LOG.error("One of the file names returned as ready to process [" + inputFileName +
                        "] was blank.  This should not happen, so throwing an error to investigate.");
                throw new RuntimeException("One of the file names returned as ready to process [" + inputFileName +
                        "] was blank.  This should not happen, so throwing an error to investigate.");
            }
        }

        return fileNamesToLoad;
    }

    protected com.lowagie.text.Document getPdfDoc() {

        String reportDropFolder = reportsDirectory + "/" + ArConstants.CustomerInvoiceWriteoff.CUSTOMER_INVOICE_WRITEOFF_REPORT_SUBFOLDER + "/";
        String fileName = ArConstants.CustomerInvoiceWriteoff.BATCH_REPORT_BASENAME + "_" +
            new SimpleDateFormat("yyyyMMdd_HHmmssSSS").format(dateTimeService.getCurrentDate()) + ".pdf";

        //  setup the writer
        File reportFile = new File(reportDropFolder + fileName);
        FileOutputStream fileOutStream;
        try {
            fileOutStream = new FileOutputStream(reportFile);
        }
        catch (IOException e) {
            LOG.error("IOException thrown when trying to open the FileOutputStream.", e);
            throw new RuntimeException("IOException thrown when trying to open the FileOutputStream.", e);
        }
        BufferedOutputStream buffOutStream = new BufferedOutputStream(fileOutStream);

        com.lowagie.text.Document pdfdoc = new com.lowagie.text.Document(PageSize.LETTER, 54, 54, 72, 72);
        try {
            PdfWriter.getInstance(pdfdoc, buffOutStream);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to start a new instance of the PdfWriter.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to start a new instance of the PdfWriter.", e);
        }

        pdfdoc.open();

        return pdfdoc;
    }

    protected void writeFileNameSectionTitle(com.lowagie.text.Document pdfDoc, String filenameLine) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 10, Font.BOLD);

        //  file name title, get title only, on windows & unix platforms
        String fileNameOnly = filenameLine.toUpperCase();
        int indexOfSlashes = fileNameOnly.lastIndexOf("\\");
        if (indexOfSlashes < fileNameOnly.length()) {
            fileNameOnly = fileNameOnly.substring(indexOfSlashes + 1);
        }
        indexOfSlashes = fileNameOnly.lastIndexOf("/");
        if (indexOfSlashes < fileNameOnly.length()) {
            fileNameOnly = fileNameOnly.substring(indexOfSlashes + 1);
        }

        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
        Chunk chunk = new Chunk(fileNameOnly, font);
        chunk.setBackground(Color.LIGHT_GRAY, 5, 5, 5, 5);
        paragraph.add(chunk);

        //  blank line
        paragraph.add(new Chunk("", font));

        try {
            pdfDoc.add(paragraph);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to write content.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
        }
    }

    protected void writeInvoiceSectionTitle(com.lowagie.text.Document pdfDoc, String customerNameLine) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 8, Font.BOLD + Font.UNDERLINE);

        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
        paragraph.add(new Chunk(customerNameLine, font));

        //  blank line
        paragraph.add(new Chunk("", font));

        try {
            pdfDoc.add(paragraph);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to write content.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
        }
    }

    protected void writeInvoiceSectionMessage(com.lowagie.text.Document pdfDoc, String resultLine) {
        Font font = FontFactory.getFont(FontFactory.COURIER, 8, Font.NORMAL);

        Paragraph paragraph = new Paragraph();
        paragraph.setAlignment(com.lowagie.text.Element.ALIGN_LEFT);
        paragraph.add(new Chunk(resultLine, font));

        //  blank line
        paragraph.add(new Chunk("", font));

        try {
            pdfDoc.add(paragraph);
        }
        catch (DocumentException e) {
            LOG.error("iText DocumentException thrown when trying to write content.", e);
            throw new RuntimeException("iText DocumentException thrown when trying to write content.", e);
        }
    }

    /**
     *
     * @see org.kuali.kfs.module.ar.batch.service.CustomerInvoiceWriteoffBatchService#createBatchDrop(org.kuali.kfs.module.ar.batch.vo.CustomerInvoiceWriteoffBatchVO)
     */
    @Override
    public String createBatchDrop(Person person, CustomerInvoiceWriteoffBatchVO writeoffBatchVO) {

        org.w3c.dom.Document xmldoc = transformVOtoXml(writeoffBatchVO);

        String batchXmlFileName = dropXmlFile(person, xmldoc);

        createDoneFile(batchXmlFileName);

        return batchXmlFileName;
    }

    protected String getBatchXMLNamespace() {
        return XML_BATCH_NAMESPACE;
    }

    protected String doneFileName(String filename) {
        String fileNoExtension = filename.substring(0, filename.lastIndexOf("."));
        return fileNoExtension + ".done";
    }

    protected void createDoneFile(String filename) {
        String fileNoExtension = doneFileName(filename);
        File doneFile = new File(fileNoExtension);
        try {
            doneFile.createNewFile();
        }
        catch (IOException e) {
            throw new RuntimeException("Exception while trying to create .done file.", e);
        }
    }

    protected String getBatchFilePathAndName(Person person) {

        String filename = batchInputFileType.getFileName(person.getPrincipalId(), "", "");

        String filepath = batchInputFileType.getDirectoryPath();
        if (!filepath.endsWith("/")) {
            filepath = filepath + "/";
        }

        String extension = batchInputFileType.getFileExtension();

        return filepath + filename + "." + extension;
    }

    protected String dropXmlFile(Person person, org.w3c.dom.Document xmldoc) {

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

    protected Document transformVOtoXml(CustomerInvoiceWriteoffBatchVO writeoffBatchVO) {

        Document xmldoc = new DocumentImpl();
        Element e = null;
        Element invoicesElement = null;
        Node n = null;

        Element root = xmldoc.createElementNS("http://www.kuali.org/kfs/ar/customer", XML_ROOT_ELEMENT_NAME);
        root.setAttribute("xmlns", getBatchXMLNamespace());
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");

        //  create submittedBy element
        e = xmldoc.createElement("submittedByPrincipalId");
        n = xmldoc.createCDATASection(writeoffBatchVO.getSubmittedByPrincipalName());
        e.appendChild(n);
        root.appendChild(e);

        //  create submittedOn element
        e = xmldoc.createElement("submittedOn");
        n = xmldoc.createCDATASection(writeoffBatchVO.getSubmittedOn());
        e.appendChild(n);
        root.appendChild(e);

        //  create note element
        e = xmldoc.createElement("note");
        n = xmldoc.createCDATASection(writeoffBatchVO.getNote());
        e.appendChild(n);
        root.appendChild(e);

        //  create invoices element and list of invoice child elements
        invoicesElement = xmldoc.createElement("invoiceNumbers");
        for (String invoiceNumber : writeoffBatchVO.getInvoiceNumbers()) {
            e = xmldoc.createElement("invoiceNumber");
            n = xmldoc.createCDATASection(invoiceNumber);
            e.appendChild(n);
            invoicesElement.appendChild(e);
        }
        root.appendChild(invoicesElement);

        xmldoc.appendChild(root);

        return xmldoc;
    }

    // this strange construct (rather than using setter injection) is here to eliminate a
    // circular reference problem with Spring's eager init.
    protected CustomerInvoiceWriteoffDocumentService getInvoiceWriteoffDocumentService() {
        return SpringContext.getBean(CustomerInvoiceWriteoffDocumentService.class);
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


    public void setCustomerService(CustomerService customerService) {
        this.customerService = customerService;
    }

    public void setInvoiceDocumentService(CustomerInvoiceDocumentService invoiceDocumentService) {
        this.invoiceDocumentService = invoiceDocumentService;
    }

}
