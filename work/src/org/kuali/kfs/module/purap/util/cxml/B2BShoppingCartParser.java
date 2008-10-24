/*
 * Copyright 2006-2007 The Kuali Foundation.
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
package org.kuali.kfs.module.purap.util.cxml;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.kuali.kfs.module.purap.businessobject.B2BShoppingCartItem;
import org.kuali.kfs.module.purap.exception.CxmlParseError;
import org.w3c.dom.Node;

public class B2BShoppingCartParser extends CxmlParser {
    private static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(B2BShoppingCartParser.class);

    private Node punchOutOrderMessageNode = null;

    /**
     * @param cXml
     * @throws CxmlParseError
     */
    public B2BShoppingCartParser(String cXml) throws CxmlParseError {
        super(cXml);

        punchOutOrderMessageNode = getXMLNode(document, "cXML/Message/PunchOutOrderMessage");
        if (punchOutOrderMessageNode == null) {
            LOG.error("ShoppingCart() Unable to locate order in XML");
            throw new CxmlParseError("Unable to locate order in XML");
        }
    }

    /**
     * Get the status node out of the XML document
     */
    private Node getStatusNode() {
        LOG.debug("getStatusNode() started");

        return getXMLNode(document, "cXML/Message/Status");
    }

    public String getStatusCode() {
        LOG.debug("getStatusCode() started");

        Node statusNode = getStatusNode();
        if (statusNode == null) {
            LOG.error("getStatusCode() Status node not found");
            return null;
        }

        String code = getNodeAttribute(statusNode, "code");
        return (code != null) ? code.trim() : null;
    }

    public String getStatusText() {
        LOG.debug("getStatusText() started");

        Node statusNode = getStatusNode();
        if (statusNode == null) {
            LOG.error("getStatusText() Status node not found");
            return null;
        }

        return getNodeAttribute(statusNode, "text");
    }

    // public ShoppingCartHeader getHeader() {
    // LOG.debug("getHeader() started");
    //
    // ShoppingCartHeader sch = new ShoppingCartHeader();
    //
    // DomainValue dv = getHeader("From");
    // sch.setFrom(dv.domain,dv.value);
    // dv = getHeader("To");
    // sch.setTo(dv.domain,dv.value);
    // dv = getHeader("Sender");
    // sch.setSender(dv.domain,dv.value);
    //
    // Node n = getXMLNode(document,"cXML/Header/Sender/UserAgent");
    // if ( n != null ) {
    // sch.setSenderUserAgent(getNodeText(n));
    // }
    // return sch;
    // }

    public CxmlHeader getCxmlHeader() {
        LOG.debug("getHeader() started");

        CxmlHeader ch = new CxmlHeader();

        DomainValue dv = getHeader("From");
        ch.setFrom(dv.domain, dv.value);
        dv = getHeader("To");
        ch.setTo(dv.domain, dv.value);
        dv = getHeader("Sender");
        ch.setSender(dv.domain, dv.value);

        Node n = getXMLNode(document, "cXML/Header/Sender/UserAgent");
        if (n != null) {
            ch.setSenderUserAgent(getNodeText(n));
        }
        return ch;
    }

    private class DomainValue {
        public String domain;
        public String value;
    }

    private DomainValue getHeader(String where) {
        DomainValue dv = new DomainValue();
        Node n = getXMLNode(document, "cXML/Header/" + where + "/Credential");
        if (n != null) {
            dv.domain = getNodeAttribute(n, "domain");
            Node n2 = getXMLNode(n, "Identity");
            if (n2 != null) {
                dv.value = getNodeText(n2);
            }
        }
        return dv;
    }

    /**
     * Return the buyer cookie
     * 
     * @return
     */
    public String getBuyerCookie() {
        LOG.debug("getBuyerCookie() started");

        if (!isSuccess()) {
            return null;
        }
        Node cookie = getXMLNode(punchOutOrderMessageNode, "BuyerCookie");
        return (cookie == null) ? null : getNodeText(cookie);
    }

    /**
     * Return the total amount of the order
     * 
     * @return
     */
    public String getTotal() {
        LOG.debug("getTotal() started");
        if (!isSuccess()) {
            return null;
        }
        Node money = getXMLNode(punchOutOrderMessageNode, "PunchOutOrderMessageHeader/Total/Money");
        return (money == null) ? null : getNodeText(money);
    }

    public List getItems() throws CxmlParseError {
        LOG.debug("getOrderItems() started");
        if (!isSuccess()) {
            return null;
        }

        List items = new ArrayList();
        List itemInNodes = findNodes(punchOutOrderMessageNode, Node.ELEMENT_NODE, "ItemIn");
        for (Iterator iter = itemInNodes.iterator(); iter.hasNext();) {
            Node itemNode = (Node) iter.next();
            B2BShoppingCartItem sci = new B2BShoppingCartItem();

            sci.setQuantity(getNodeAttribute(itemNode, "quantity"));
            Node n = getXMLNode(itemNode, "ItemID/SupplierPartID");
            sci.setSupplierPartId(getNodeText(n));

            n = getXMLNode(itemNode, "ItemID/SupplierPartAuxiliaryID");
            sci.setSupplierPartAuxiliaryId(getNodeText(n));

            n = getXMLNode(itemNode, "ItemDetail/UnitPrice/Money");
            sci.setUnitPrice(getNodeText(n));
            sci.setUnitPriceCurrency(getNodeAttribute(n, "currency"));

            n = getXMLNode(itemNode, "ItemDetail/Description");
            sci.setDescription(getNodeText(n));

            n = getXMLNode(itemNode, "ItemDetail/UnitOfMeasure");
            sci.setUnitOfMeasure(getNodeText(n));

            // Get all of the classifications
            n = getXMLNode(itemNode, "ItemDetail");
            List classificationList = findNodes(n, Node.ELEMENT_NODE, "Classification");
            for (Iterator iterator = classificationList.iterator(); iterator.hasNext();) {
                Node classificationNode = (Node) iterator.next();
                sci.setClassification(getNodeAttribute(classificationNode, "domain"), getNodeText(classificationNode));
            }

            // Get all of the extrinsics
            List extrinsicList = findNodes(n, Node.ELEMENT_NODE, "Extrinsic");
            for (Iterator iterator = extrinsicList.iterator(); iterator.hasNext();) {
                Node extrinsicNode = (Node) iterator.next();
                sci.setExtrinsic(getNodeAttribute(extrinsicNode, "name"), getNodeText(extrinsicNode));
            }

            // Get all the supplier ID's
            List supplierList = findNodes(itemNode, Node.ELEMENT_NODE, "SupplierID");
            for (Iterator iterator = supplierList.iterator(); iterator.hasNext();) {
                Node supplierIdNode = (Node) iterator.next();
                sci.setSupplier(getNodeAttribute(supplierIdNode, "domain"), getNodeText(supplierIdNode));
            }

            items.add(sci);
        }

        return items;
    }
}