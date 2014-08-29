/*
 * Copyright 2011 The Kuali Foundation.
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
package org.kuali.kfs.sys.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

public class KfsWebUtils {

    /**
     * Zipoutput files that are not of type text/plain or text/html.
     *
     * @param response
     * @param contentType
     * @param outputStreamMap<filename, outputStream>
     * @param zipFileName
     * @throws IOException
     */
    public static void saveMimeZipOutputStreamAsFile(HttpServletResponse response, String contentType, Map<String, ByteArrayOutputStream> outputStreamMap, String zipFileName) throws IOException {

        // set response
        response.setContentType(contentType);
        response.setHeader("Content-disposition", "attachment; filename=" + zipFileName);
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");

        // write to zipoutput
        ZipOutputStream zout = new ZipOutputStream(response.getOutputStream());
        int totalSize = 0;
        Iterator<String> fileNames = outputStreamMap.keySet().iterator();
        while (fileNames.hasNext()) {
            String fileName = fileNames.next();
            ByteArrayOutputStream pdfStream = outputStreamMap.get(fileName);
            totalSize += pdfStream.size();
            zout.putNextEntry(new ZipEntry(fileName));
            zout.write(pdfStream.toByteArray());
            zout.closeEntry();
        }
        response.setContentLength(totalSize);
        zout.flush();
        zout.close();
    }

    /**
     * COPIED from KNS org.kuali.rice.kns.util.WebUtils so we can handle inline streams (for popups)
     * as well as attachments. Ideally we wouldn't need to duplicate Rice functionality in KFS, maybe
     * KRAD will help us here and make this method obsolete.
     *
     * A file that is not of type text/plain or text/html can be output through
     * the response using this method.
     *
     * @param response
     * @param contentType
     * @param byteArrayOutputStream
     * @param fileName
     * @param useJavascript
     * @throws IOException
     */
    public static void saveMimeOutputStreamAsFile(HttpServletResponse response, String contentType,
            ByteArrayOutputStream byteArrayOutputStream, String fileName, boolean useJavascript) throws IOException {

        // If there are quotes in the name, we should replace them to avoid issues.
        // The filename will be wrapped with quotes below when it is set in the header
        String updateFileName;
        if(fileName.contains("\"")) {
            updateFileName = fileName.replaceAll("\"", "");
        } else {
            updateFileName =  fileName;
        }

        String type = "attachment";

        if (useJavascript) {
            type = "inline";
        }

        // set response
        response.setContentType(contentType);
        response.setHeader("Content-disposition", type + "; filename=\"" + updateFileName + "\"");
        response.setHeader("Expires", "0");
        response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");
        response.setHeader("Pragma", "public");
        response.setContentLength(byteArrayOutputStream.size());

        // write to output
        OutputStream outputStream = response.getOutputStream();
        byteArrayOutputStream.writeTo(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

}
