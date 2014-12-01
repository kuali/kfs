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
    
}
