/*
 * Copyright 2014 The Kuali Foundation.
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
package org.kuali.kfs.module.ar.businessobject;

import java.io.ByteArrayOutputStream;

/**
 * Wraps an output report PDF and metadata about it
 */
public class ReportPDFHolder {
    private ByteArrayOutputStream reportBytes;
    private String contentDisposition;

    public ReportPDFHolder(ByteArrayOutputStream baos, String contentDisposition) {
        this.reportBytes = baos;
        this.contentDisposition = contentDisposition;
    }

    public ByteArrayOutputStream getReportBytes() {
        return reportBytes;
    }

    public String getContentDisposition() {
        return contentDisposition;
    }
}