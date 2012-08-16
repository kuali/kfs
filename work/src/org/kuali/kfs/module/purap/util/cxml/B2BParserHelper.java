/*
 * Copyright 2008 The Kuali Foundation
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
package org.kuali.kfs.module.purap.util.cxml;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.kuali.kfs.sys.batch.service.BatchInputFileService;
import org.kuali.kfs.sys.context.SpringContext;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class B2BParserHelper {
    
    private static Logger log = Logger.getLogger(B2BParserHelper.class);
    
    private DocumentBuilder builder;
    private static B2BParserHelper _this;
    
    private B2BParserHelper(){
        
        DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
        builderFactory.setValidating(false); // It's not needed to validate here
        builderFactory.setIgnoringElementContentWhitespace(true); 

        try {
            // This is a funky one. Without setting this "load-external-dtd" feature, even though we're
            // explicitly setting non-validating, the parser will still reach out and retrieve that DTD. If
            // the xml.cxml.org site happens to be down, it'll hang or fail on that dependency.
            //
            // http://xerces.apache.org/xerces2-j/features.html#nonvalidating.load-external-dtd
            builderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
            
            builder = builderFactory.newDocumentBuilder(); // Create the parser
        } catch(ParserConfigurationException e) {
            throw new RuntimeException(e);
        } 
        
    }

    public static B2BParserHelper getInstance(){
        if (_this == null){
            _this = new B2BParserHelper();
        }
        return _this;
    }
    
    public synchronized B2BShoppingCart parseShoppingCartXML(String xmlChunk){
        
        Document xmlDoc = null;
        try {
            xmlDoc = builder.parse(new ByteArrayInputStream(xmlChunk.getBytes()));
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        byte[] xmlDocAsBytes = addXMLNameSpace(xmlDoc,"http://www.kuali.org/kfs/purap/b2bPunchOutOrder");
        
        B2BPunchOutOrderFileType fileType = SpringContext.getBean(B2BPunchOutOrderFileType.class);
        
        B2BShoppingCart cart = (B2BShoppingCart) SpringContext.getBean(BatchInputFileService.class).parse(fileType,xmlDocAsBytes);
        
        return cart;
        
    }
    
    public synchronized PunchOutSetupResponse parsePunchOutSetupResponse(String xmlChunk){
        
        Document xmlDoc = null;
        try {
            xmlDoc = builder.parse(new ByteArrayInputStream(xmlChunk.getBytes()));
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        byte[] xmlDocAsBytes = addXMLNameSpace(xmlDoc,"http://www.kuali.org/kfs/purap/b2bPunchOutResponse");
        
        PunchOutSetupResponseFileType fileType = SpringContext.getBean(PunchOutSetupResponseFileType.class);
        
        PunchOutSetupResponse response = (PunchOutSetupResponse) SpringContext.getBean(BatchInputFileService.class).parse(fileType,xmlDocAsBytes);
        
        return response;
        
    }
    
    public synchronized PurchaseOrderResponse parsePurchaseOrderResponse(String xmlChunk){
        
        Document xmlDoc = null;
        try {
            xmlDoc = builder.parse(new ByteArrayInputStream(xmlChunk.getBytes()));
        } catch(Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
        
        byte[] xmlDocAsBytes = addXMLNameSpace(xmlDoc,"http://www.kuali.org/kfs/purap/b2bPOResponse");
        
        B2BPOResponseFileType fileType = SpringContext.getBean(B2BPOResponseFileType.class);
        
        PurchaseOrderResponse response = (PurchaseOrderResponse) SpringContext.getBean(BatchInputFileService.class).parse(fileType,xmlDocAsBytes);
        
        return response;
        
    }
    
    private byte[] addXMLNameSpace(Document xmlDoc,
                                   String nameSpace){
        
        Node node = xmlDoc.getDocumentElement();
        Element element = (Element)node;
        
        element.setAttribute("xmlns", nameSpace);
        
        OutputFormat outputFormat = new OutputFormat(xmlDoc);
        outputFormat.setOmitDocumentType(true);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        XMLSerializer serializer = new XMLSerializer( out,outputFormat );
        try {
            serializer.asDOMSerializer();
            serializer.serialize( xmlDoc.getDocumentElement());
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        
        return out.toByteArray();
    }
}
