/*
 * Copyright 2007 The Kuali Foundation
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

package org.kuali.kfs.sys.exception;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXParseException;

/**
 * Defines exception-handling for the XML parses
 */
public class XmlErrorHandler implements ErrorHandler {
    private static Log LOG = LogFactory.getLog(XmlErrorHandler.class);

    public XmlErrorHandler() {
    }

    public void warning(SAXParseException e) {
        String parseMessage = assembleMessage("warning", e);
        LOG.error(parseMessage);
        throw new ParseException(parseMessage, e);
    }

    public void error(SAXParseException e) {
        String parseMessage = assembleMessage("error", e);
        LOG.error(parseMessage);
        throw new ParseException(parseMessage, e);
    }

    public void fatalError(SAXParseException e) {
        String parseMessage = assembleMessage("fatal error", e);
        LOG.error(parseMessage);
        throw new ParseException(parseMessage, e);
    }

    private String assembleMessage(String messageType, SAXParseException e) {
        StringBuffer message = new StringBuffer(messageType);
        message.append(" Parsing error was encountered on line ");
        message.append(e.getLineNumber());
        message.append(", column ");
        message.append(e.getColumnNumber());
        message.append(": ");
        message.append(e.getMessage());

        return message.toString();
    }
}
