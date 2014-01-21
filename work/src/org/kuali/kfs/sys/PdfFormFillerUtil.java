/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.kuali.rice.krad.util.ObjectUtils;

import com.lowagie.text.DocumentException;
import com.lowagie.text.Element;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.AcroFields;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfGState;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.PdfStamper;

/**
 * This class writes the reports onto a pdf file as per a template provided.
 */
public class PdfFormFillerUtil {

    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PdfFormFillerUtil.class);
    private static final SimpleDateFormat FILE_NAME_TIMESTAMP = new SimpleDateFormat("_yyyy-MM-dd_hhmmss");

    /**
     * This method generates the reports from the template file provided.
     *
     * @param template
     * @param outputDir
     * @param baseOutputFileName
     * @param replacementList
     * @return outputFile Generated report file
     * @throws IOException
     */
    public static byte[] populateTemplate(File template, Map<String, String> replacementList, String watermarkText) throws IOException, DocumentException {
        // --------------------------------------------------
        // Validate the parameters
        // --------------------------------------------------
        if (template == null || replacementList == null) {
            throw new IllegalArgumentException("All parameters are required, but one or more were null.");
        }

        // Validate the template file
        if (template.exists() == false) {
            throw new IOException("The template file '" + template.getAbsolutePath() + "' does not exist.");
        }
        if (template.isFile() == false) {
            throw new RuntimeException("The template file '" + template.getAbsolutePath() + "' is not a valid file.");
        }
        if (template.canRead() == false) {
            throw new RuntimeException("The template file '" + template.getAbsolutePath() + "' cannot be read.");
        }

        // --------------------------------------------------
        // Use iText to build the new PDF
        // --------------------------------------------------
        InputStream templateStream = new FileInputStream(template);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        pdfStampValues(templateStream, outputStream, replacementList);
        return outputStream.toByteArray();
    }

    /**
     * This method stamps the values onto the pdf file from the replacement list
     *
     * @param strLine
     * @param replacementList
     * @return
     */
    private static boolean validListTagFound(String strLine) {
        boolean valid = true;
        valid &= strLine.matches("^#[\\S]*$");
        return valid;
    }

    /**
     * This Method stamps the values from the map onto the fields in the template provided.
     *
     * @param templateStream
     * @param outputStream
     * @param replacementList
     * @throws IOException
     */
    private static void pdfStampValues(InputStream templateStream, OutputStream outputStream, Map<String, String> replacementList) throws IOException {
        try {
            // Create a PDF reader for the template
            PdfReader pdfReader = new PdfReader(templateStream);

            // Create a PDF writer
            PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
            // Replace the form data with the final values
            AcroFields fields = pdfStamper.getAcroFields();
            Object[] fieldNameList = fields.getFields().keySet().toArray();
            for (Object fieldName : fieldNameList)

            {
                // Read the field data
                String text = fields.getField(fieldName.toString());
                String newText = fields.getField(fieldName.toString());
                // Replace the keywords
                if (fields.getFieldType(fieldName.toString()) == AcroFields.FIELD_TYPE_TEXT) {
                    newText = replaceValuesIteratingThroughFile(text, replacementList);
                }
                else {
                    if (ObjectUtils.isNotNull(replacementList.get(fieldName.toString()))) {
                        newText = replacementList.get(fieldName);
                    }
                }
                // Populate the field with the final value
                fields.setField(fieldName.toString(), newText);
            }

            // --------------------------------------------------
            // Save the new PDF
            // --------------------------------------------------
            pdfStamper.close();

        }
        catch (IOException e) {
            throw new IOException("IO error processing PDF template", e);
        }
        catch (DocumentException e) {
            throw new IOException("iText error processing PDF template", e);
        }
        finally {
            // --------------------------------------------------
            // Close the files
            // --------------------------------------------------
            templateStream.close();
            outputStream.close();
        }
    }

    /**
     * This method creates a Final watermark on the input Stream.
     *
     * @param templateStream
     * @param finalmarkText
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static byte[] createFinalmarkOnFile(byte[] templateStream, String finalmarkText) throws IOException, DocumentException {
        // Create a PDF reader for the template
        PdfReader pdfReader = new PdfReader(templateStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Create a PDF writer
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        int n = pdfReader.getNumberOfPages();
        int i = 1;
        PdfContentByte over;
        BaseFont bf;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.WINANSI, BaseFont.EMBEDDED);
            PdfGState gstate = new PdfGState();
            while (i <= n) {
                // Watermark under the existing page
                Rectangle pageSize = pdfReader.getPageSizeWithRotation(i);
                over = pdfStamper.getOverContent(i);
                over.beginText();
                over.setFontAndSize(bf, 8);
                over.setGState(gstate);
                over.setColorFill(Color.BLACK);
                over.showTextAligned(Element.ALIGN_CENTER, finalmarkText, (pageSize.width() / 2), (pageSize.height() - 10), 0);
                over.endText();
                i++;
            }
            pdfStamper.close();
        }
        catch (DocumentException ex) {
            throw new IOException("iText error creating final watermark on PDF", ex);
        }
        catch (IOException ex) {
            throw new IOException("IO error creating final watermark on PDF", ex);
        }
        return outputStream.toByteArray();
    }

    /**
     * This Method creates a custom watermark on the File.
     *
     * @param templateStream
     * @param watermarkText
     * @return
     * @throws IOException
     * @throws DocumentException
     */
    public static byte[] createWatermarkOnFile(byte[] templateStream, String watermarkText) throws IOException, DocumentException {
        // Create a PDF reader for the template
        PdfReader pdfReader = new PdfReader(templateStream);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        // Create a PDF writer
        PdfStamper pdfStamper = new PdfStamper(pdfReader, outputStream);
        int n = pdfReader.getNumberOfPages();
        int i = 1;
        PdfContentByte over;
        BaseFont bf;
        try {
            bf = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.WINANSI, BaseFont.EMBEDDED);
            PdfGState gstate = new PdfGState();
            gstate.setFillOpacity(0.5f);
            while (i <= n) {
                // Watermark under the existing page
                Rectangle pageSize = pdfReader.getPageSizeWithRotation(i);
                over = pdfStamper.getOverContent(i);
                over.beginText();
                over.setFontAndSize(bf, 200);
                over.setGState(gstate);
                over.setColorFill(Color.LIGHT_GRAY);
                over.showTextAligned(Element.ALIGN_CENTER, watermarkText, (pageSize.width() / 2), (pageSize.height() / 2), 45);
                over.endText();
                i++;
            }
            pdfStamper.close();
        }
        catch (DocumentException ex) {
            throw new IOException("iText error creating watermark on PDF", ex);
        }
        catch (IOException ex) {
            throw new IOException("IO error creating watermark on PDF", ex);
        }
        return outputStream.toByteArray();
    }

    /**
     * This method splits all the text in the template file and replaces them if they match the keys in the Map.
     *
     * @param template
     * @param replacementList
     * @return
     */
    private static String replaceValuesIteratingThroughFile(String template, Map<String, String> replacementList) {
        StringBuilder buffOriginal = new StringBuilder();
        StringBuilder buffNormalized = new StringBuilder();

        String[] keys = template.split("[\\s]+");

        // Scan for each word
        for (String key : keys) {
            if (validListTagFound(key)) {
                String replacementKey = key.substring(1);
                String value = replacementList.get(replacementKey);
                if (ObjectUtils.isNotNull(value)) {
                    buffOriginal.append(value + " ");
                }
                else {
                    buffOriginal.append(" ");
                }
            }
            else {
                buffOriginal.append(key + " ");
            }
        }
        return buffOriginal.toString();
    }
}
