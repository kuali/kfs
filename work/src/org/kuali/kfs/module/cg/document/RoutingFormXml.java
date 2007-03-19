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

package org.kuali.module.kra.routingform.xml;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.lang.ObjectUtils;
import org.kuali.module.kra.routingform.document.RoutingFormDocument;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * This class creates an XML representation of a RoutingForm's data.
 * 
 * 
 */
public class RoutingFormXml {
    /**
     * Driving method for this class. Functions as a hub calling helper methods.
     * 
     * @param routingFormroutingFormDocumentDoc data representation of a routingForm
     * @param xmlDoc target xml representation for the routingForm. This field will be side effected.
     * @param baseUrl ensures that stylesheet may be path idependent
     * @throws Exception
     */
    public static void makeXml(RoutingFormDocument routingFormDocument, Document xmlDoc, String baseUrl) throws Exception {
        // Initialize data needed.
        // TODO anything needed here... ?
        
        // Start of XML elements
        Element proposalElement = xmlDoc.createElement("PROPOSAL");
        xmlDoc.appendChild(proposalElement);

        Element routingFormElement = xmlDoc.createElement("ROUTING_FORM");
        proposalElement.appendChild(routingFormElement);
        
        routingFormElement.setAttribute("TRACKING_NUMBER", routingFormDocument.getDocumentNumber());
        routingFormElement.setAttribute("PROPOSAL_NUMBER", ObjectUtils.toString(routingFormDocument.getContractGrantProposal().getProposalNumber()));
        routingFormElement.setAttribute("LINKED_BUDGET_NUMBER", routingFormDocument.getRoutingFormBudgetNumber());

        // Code to get the current date/time
        Calendar calendar = new GregorianCalendar();
        Date date = calendar.getTime();
        DateFormat localFormat = DateFormat.getDateTimeInstance();

        routingFormElement.setAttribute("XML_CREATE_DATE_TIME", localFormat.format(date));
        routingFormElement.setAttribute("BASE_URL", baseUrl);

        routingFormElement.appendChild(createAgencyElement(routingFormDocument, xmlDoc));
    }
    
    /**
     * Creates AGENCY node.
     * 
     * @param routingFormDocument
     * @param xmlDoc
     * @return resulting node
     */
    private static Element createAgencyElement(RoutingFormDocument routingFormDocument, Document xmlDoc) {
        Element agencyElement = xmlDoc.createElement("AGENCY");

        // TODO
        
        return agencyElement;
    }
}
