package org.kuali.module.pdp.xml.impl;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.kuali.module.pdp.exception.ConfigurationError;
import org.kuali.module.pdp.exception.FileReadException;
import org.kuali.module.pdp.xml.PaymentFileParser;
import org.kuali.module.pdp.xml.PdpFileHandler;
import org.kuali.module.pdp.xml.XmlAccounting;
import org.kuali.module.pdp.xml.XmlDetail;
import org.kuali.module.pdp.xml.XmlGroup;
import org.kuali.module.pdp.xml.XmlHeader;
import org.kuali.module.pdp.xml.XmlTrailer;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;



public class PaymentFileParserImpl extends DefaultHandler implements PaymentFileParser {
  static final String JAXP_SCHEMA_LANGUAGE = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
  static final String JAXP_SCHEMA_SOURCE = "http://java.sun.com/xml/jaxp/properties/schemaSource";
  static final String W3C_XML_SCHEMA = "http://www.w3.org/2001/XMLSchema"; 

  private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(PaymentFileParserImpl.class);

  private String xsdUrl;
  private PdpFileHandler fileHandler = null;

  public PaymentFileParserImpl() {
  }

  public void setFileHandler(PdpFileHandler fileHandler) {
    this.fileHandler = fileHandler;
  }

  public void setXsdUrl(String xsdUrl) {
    this.xsdUrl = xsdUrl;
  }

  public void parse(InputStream stream) throws FileReadException {
    if ( xsdUrl == null ) {
      throw new ConfigurationError("xsdUrl not set");
    }
    if ( fileHandler ==  null ) {
      throw new ConfigurationError("fileHandler not set");
    }

    SAXParserFactory factory = SAXParserFactory.newInstance();
    factory.setNamespaceAware(true);
    factory.setValidating(true);

    try {
      // Parse the input
      SAXParser saxParser = factory.newSAXParser();
      try {
        saxParser.setProperty(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
      } catch (SAXNotRecognizedException x) {
        LOG.error("parse() JAXP version can't validate with schemas", x);
        throw new ConfigurationError("JAXP Parser doesn't support schemas");
      }
      LOG.debug("parse() before setProperty");
      saxParser.setProperty(JAXP_SCHEMA_SOURCE, xsdUrl);
      LOG.debug("parse() before SAX parse");
      saxParser.parse(stream, this);
      LOG.debug("parse() after SAX parse");
    } catch (IllegalArgumentException e) {
      LOG.error("parse() stream is null", e);
      throw new FileReadException("Unable to read input stream: " + e.getMessage());
    } catch (SAXNotSupportedException e) {
      LOG.error("parse() SAX not supported in the parser", e);
      throw new ConfigurationError("No JAXP SAX Parser");
    } catch (ParserConfigurationException e) {
      LOG.error("parse() Parser configuration error", e);
      throw new ConfigurationError("Parser configuration error: " + e.getMessage());
    } catch (SAXException e) {
      LOG.error("parse() Unknown SAX exception", e);
      throw new FileReadException("SAX parser error " + e.getMessage());
    } catch (IOException e) {
      LOG.error("parse() Unable to read file", e);
      throw new FileReadException("IO error, unable to read input stream: " + e.getMessage());
    }
  }

  public void parse(String filename) throws FileReadException {
    try {
      parse(new FileInputStream(filename));
    } catch (FileNotFoundException e) {
      LOG.error("parse() File not found", e);
      throw new FileReadException("File not found");
    }
  }

  private TagStack tags = new TagStack();
  private StringBuffer chars = null;
  
  private XmlHeader header = null;
  private XmlGroup group = null;
  private XmlDetail detail = null;
  private XmlAccounting accounting = null;
  private XmlTrailer trailer = null;

  private String tagPath;

  public void startElement(String namespaceURI, String lName, String qName, Attributes attrs) throws SAXException {
    tags.push(qName);
    chars = new StringBuffer();

    if ( "pdp_file/group/payee_id".equals(tags.getTagPath()) ) {
      group.setId_type(attrs.getValue(0));
    } else if ("pdp_file/header".equals(tags.getTagPath()) ) {
      header = new XmlHeader();
    } else if ("pdp_file/trailer".equals(tags.getTagPath()) ) {
      trailer = new XmlTrailer();
    } else if ( "pdp_file/group".equals(tags.getTagPath()) ) {
      group = new XmlGroup();
    } else if ( "pdp_file/group/detail".equals(tags.getTagPath()) ) {
      detail = new XmlDetail();
    } else if ( "pdp_file/group/detail/accounting".equals(tags.getTagPath()) ) {
      accounting = new XmlAccounting();
    }
  }

  public void endElement(String namespaceURI, String sName, String qName) throws SAXException {
    tagPath = tags.getTagPath();
    
    // clean chars of funny characters not to be loaded in PDP
    StringBuffer data = chars;
    for (int i = (data.length() - 1); i >= 0; i--) {
      char element = data.charAt(i);
      if (element < 32) {
        chars.deleteCharAt(i);
      }
    }
    
    if ( "pdp_file/header".equals(tagPath) ) {
      fileHandler.setHeader(header);
      header = null;
    } else if ( "pdp_file/trailer".equals(tagPath) ) {
      fileHandler.setTrailer(trailer);
      trailer = null;
    } else if ( "pdp_file/group".equals(tagPath) ) {
      fileHandler.setGroup(group);
      group = null;
    } else if ( "pdp_file/group/detail".equals(tagPath) ) {
      group.addDetail(detail);
      detail = null;
    } else if ( "pdp_file/group/detail/accounting".equals(tagPath) ) {
      detail.addAccounting(accounting);
      accounting = null;
    } else if ( "payment_text".equals(qName) ) {
      if ( detail != null ) {
        detail.addPayment_text(chars.toString());
      }
    } else if ( tagPath.startsWith("pdp_file/group/detail/accounting/") ) {
      if ( accounting != null ) {
        accounting.setField(qName,chars.toString());
      }
    } else if ( tagPath.startsWith("pdp_file/group/detail/") ) {
      if ( detail != null ) {
        detail.setField(qName,chars.toString());
      }
    } else if ( tagPath.startsWith("pdp_file/group/") ) {
      if ( group != null ) {
        group.setField(qName,chars.toString());
      }
    } else if ( tagPath.startsWith("pdp_file/header/") ) {
      if ( header != null ) {
        header.setField(qName,chars.toString());
      }
    } else if ( tagPath.startsWith("pdp_file/trailer/") ) {
      if ( trailer != null ) {
        trailer.setField(qName,chars.toString());
      }
    } else if ( "payment_text".equals(qName) ) {
      if ( detail != null ) {
        detail.addPayment_text(chars.toString());
      }
    }
    tags.pop();
  }

  public void characters(char buf[], int offset, int len) throws SAXException {
    chars.append(buf,offset,len);
  }

  public void error(SAXParseException pe) throws SAXException {
    LOG.error("error() Exception", pe);
    fileHandler.setErrorMessage(pe.getMessage());
  }

  public void fatalError(SAXParseException pe) throws SAXException {
    LOG.error("fatalError() Exception", pe);
    fileHandler.setErrorMessage(pe.getMessage());
  }

  public void warning(SAXParseException pe) throws SAXException {
    LOG.error("warning() Exception", pe);
    fileHandler.setErrorMessage(pe.getMessage());
  }
}
