/**
 * Copyright 2005-2014 The Kuali Foundation
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
package org.kuali.rice.kns.web.struts.form;

import org.apache.struts.upload.FormFile;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;

/**
 * Empty FormFile instance, used to clear out FormFile attributes of Struts forms.
 * 
 * 
 */
public class BlankFormFile implements FormFile, Serializable {
    public void destroy() {
    }

    public String getContentType() {
        throw new UnsupportedOperationException();
    }

    public byte[] getFileData() throws FileNotFoundException, IOException {
        throw new UnsupportedOperationException();
    }

    public String getFileName() {
        return "";
    }

    public int getFileSize() {
        return 0;
    }

    public InputStream getInputStream() throws FileNotFoundException, IOException {
        throw new UnsupportedOperationException();
    }

    public void setContentType(String contentType) {
        throw new UnsupportedOperationException();
    }

    public void setFileName(String fileName) {
        throw new UnsupportedOperationException();
    }

    public void setFileSize(int fileSize) {
        throw new UnsupportedOperationException();
    }
}
