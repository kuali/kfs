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
