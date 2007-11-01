/*
 * Copyright 2007 The Kuali Foundation.
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
package org.kuali.module.pdp.xml;

import java.util.List;

import org.kuali.module.pdp.bo.PdpUser;


public interface PdpFileHandler {
    public void setFilename(String filename);

    public void setUser(PdpUser u);

    public void setMaxNoteLines(int lines);

    public void setHeader(XmlHeader header);

    public void setGroup(XmlGroup item);

    public void setTrailer(XmlTrailer trailer);

    public void setErrorMessage(String message);

    public List getErrorMessages();

    public XmlHeader getHeader();

    public XmlTrailer getTrailer();

    public void clear();
}
