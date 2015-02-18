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
package org.kuali.kfs.sys.util;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.kuali.kfs.sys.KFSConstants;

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
        response.setHeader(KFSConstants.HttpHeaderResponse.CONTENT_DIPOSITION, KFSConstants.HttpHeaderResponse.ATTACHMENT_CONTENT_TYPE + "; " + KFSConstants.HttpHeaderResponse.FILENAME + "=" + zipFileName);
        response.setHeader(KFSConstants.HttpHeaderResponse.EXPIRES, KFSConstants.ZERO);
        response.setHeader(KFSConstants.HttpHeaderResponse.CACHE_CONTROL, KFSConstants.HttpHeaderResponse.CACHE_CONTROL_REVALIDATE_PRE_POST_CHECK_ZERO);
        response.setHeader(KFSConstants.HttpHeaderResponse.PRAGMA, KFSConstants.HttpHeaderResponse.PUBLIC);

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

        String type = KFSConstants.HttpHeaderResponse.ATTACHMENT_CONTENT_TYPE;

        if (useJavascript) {
            type = KFSConstants.HttpHeaderResponse.INLINE_CONTENT_TYPE;
        }

        // set response
        response.setContentType(contentType);
        response.setHeader(KFSConstants.HttpHeaderResponse.CONTENT_DIPOSITION, type + "; " + KFSConstants.HttpHeaderResponse.FILENAME + "=\"" + updateFileName + "\"");
        response.setHeader(KFSConstants.HttpHeaderResponse.EXPIRES, KFSConstants.ZERO);
        response.setHeader(KFSConstants.HttpHeaderResponse.CACHE_CONTROL, KFSConstants.HttpHeaderResponse.CACHE_CONTROL_REVALIDATE_PRE_POST_CHECK_ZERO);
        response.setHeader(KFSConstants.HttpHeaderResponse.PRAGMA, KFSConstants.HttpHeaderResponse.PUBLIC);
        response.setContentLength(byteArrayOutputStream.size());

        // write to output
        OutputStream outputStream = response.getOutputStream();
        byteArrayOutputStream.writeTo(response.getOutputStream());
        outputStream.flush();
        outputStream.close();
    }

}
