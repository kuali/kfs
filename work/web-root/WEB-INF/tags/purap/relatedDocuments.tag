<%--
 Copyright 2007 The Kuali Foundation.
 
 Licensed under the Educational Community License, Version 1.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl1.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ taglib prefix="c" uri="/tlds/c.tld"%>
<%@ taglib prefix="fn" uri="/tlds/fn.tld"%>
<%@ taglib uri="/tlds/struts-html.tld" prefix="html"%>
<%@ taglib uri="/tlds/struts-logic.tld" prefix="logic"%>
<%@ taglib tagdir="/WEB-INF/tags" prefix="kul"%>
<%@ taglib tagdir="/WEB-INF/tags/dd" prefix="dd"%>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map" 
              description="The DataDictionary entry containing attributes for this row's fields."%>

<kul:tab tabTitle="View Related Documents" defaultOpen="false" tabErrorKey="${PurapConstants.ADDITIONAL_TAB_ERRORS}">
    <div class="tab-container" align=center>
        <div class="h2-container">
            <h2>Related Documents</h2>
        </div>
		<br />

	   	<logic:notEmpty name="KualiForm" property="document.relatedRequisitionViews">
			<logic:iterate id="po" name="KualiForm" property="document.relatedRequisitionViews" indexId="viewCtr">
			    <div class="h2-container">
			        <h2>Requisition - <a href=""><bean:write name="KualiForm" property="document.relatedRequisitionViews[${viewCtr}].purapDocumentIdentifier" /></a>
			    </div>
			    <table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
		        	<logic:notEmpty name="KualiForm" property="document.relatedRequisitionViews[${viewCtr}].notes">
					<tr>
						<kul:htmlAttributeHeaderCell scope="col">Date</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col">User</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col">Note</kul:htmlAttributeHeaderCell>
		        	</tr>
			 			<logic:iterate id="note" name="KualiForm" property="document.relatedRequisitionViews[${viewCtr}].notes" indexId="noteCtr">
			        		<tr>
			        			<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedRequisitionViews[${viewCtr}].notes[${noteCtr}].notePostedTimestamp" />
				        		</td>
				        		<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedRequisitionViews[${viewCtr}].notes[${noteCtr}].authorUniversal.personName" />
				        		</td>
				        		<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedRequisitionViews[${viewCtr}].notes[${noteCtr}].noteText" />
				        		</td>
				        	</tr>
			        	</logic:iterate>
			        </logic:notEmpty>
			        <logic:empty name="KualiForm" property="document.relatedRequisitionViews[${viewCtr}].notes">
				        <tr>
				            <th align="center" valign="middle" class="bord-l-b">No Notes</th>
				        </tr>
			        </logic:empty>
		    	</table>
	       	</logic:iterate>
		    <br />
		    <br />
		</logic:notEmpty>

	   	<logic:notEmpty name="KualiForm" property="document.relatedPurchaseOrderViews">
			<logic:iterate id="po" name="KualiForm" property="document.relatedPurchaseOrderViews" indexId="viewCtr">
			    <div class="h2-container">
			        <h2>Purchase Order - <a href=""><bean:write name="KualiForm" property="document.relatedPurchaseOrderViews[${viewCtr}].purapDocumentIdentifier" /></a>
			    </div>
			    <table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
		        	<logic:notEmpty name="KualiForm" property="document.relatedPurchaseOrderViews[${viewCtr}].notes">
					<tr>
						<kul:htmlAttributeHeaderCell scope="col">Date</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col">User</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col">Note</kul:htmlAttributeHeaderCell>
		        	</tr>
			 			<logic:iterate id="note" name="KualiForm" property="document.relatedPurchaseOrderViews[${viewCtr}].notes" indexId="noteCtr">
			        		<tr>
			        			<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedPurchaseOrderViews[${viewCtr}].notes[${noteCtr}].notePostedTimestamp" />
				        		</td>
				        		<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedPurchaseOrderViews[${viewCtr}].notes[${noteCtr}].authorUniversal.personName" />
				        		</td>
				        		<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedPurchaseOrderViews[${viewCtr}].notes[${noteCtr}].noteText" />
				        		</td>
				        	</tr>
			        	</logic:iterate>
			        </logic:notEmpty>
			        <logic:empty name="KualiForm" property="document.relatedPurchaseOrderViews[${viewCtr}].notes">
				        <tr>
				            <th align="center" valign="middle" class="bord-l-b">No Notes</th>
				        </tr>
			        </logic:empty>
		    	</table>
	       	</logic:iterate>
		    <br />
		    <br />
		</logic:notEmpty>

	   	<logic:notEmpty name="KualiForm" property="document.relatedPaymentRequestViews">
			<logic:iterate id="po" name="KualiForm" property="document.relatedPaymentRequestViews" indexId="viewCtr">
			    <div class="h2-container">
			        <h2>Payment Request - <a href=""><bean:write name="KualiForm" property="document.relatedPaymentRequestViews[${viewCtr}].purapDocumentIdentifier" /></a>
			    </div>
			    <table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
		        	<logic:notEmpty name="KualiForm" property="document.relatedPaymentRequestViews[${viewCtr}].notes">
					<tr>
						<kul:htmlAttributeHeaderCell scope="col">Date</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col">User</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col">Note</kul:htmlAttributeHeaderCell>
		        	</tr>
			 			<logic:iterate id="note" name="KualiForm" property="document.relatedPaymentRequestViews[${viewCtr}].notes" indexId="noteCtr">
			        		<tr>
			        			<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedPaymentRequestViews[${viewCtr}].notes[${noteCtr}].notePostedTimestamp" />
				        		</td>
				        		<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedPaymentRequestViews[${viewCtr}].notes[${noteCtr}].authorUniversal.personName" />
				        		</td>
				        		<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedPaymentRequestViews[${viewCtr}].notes[${noteCtr}].noteText" />
				        		</td>
				        	</tr>
			        	</logic:iterate>
			        </logic:notEmpty>
			        <logic:empty name="KualiForm" property="document.relatedPaymentRequestViews[${viewCtr}].notes">
				        <tr>
				            <th align="center" valign="middle" class="bord-l-b">No Notes</th>
				        </tr>
			        </logic:empty>
		    	</table>
	       	</logic:iterate>
		    <br />
		    <br />
		</logic:notEmpty>

	   	<logic:notEmpty name="KualiForm" property="document.relatedCreditMemoViews">
			<logic:iterate id="po" name="KualiForm" property="document.relatedCreditMemoViews" indexId="viewCtr">
			    <div class="h2-container">
			        <h2>Credit Memo - <a href=""><bean:write name="KualiForm" property="document.relatedCreditMemoViews[${viewCtr}].purapDocumentIdentifier" /></a>
			    </div>
			    <table cellpadding="0" cellspacing="0" class="datatable" summary="Notes">
		        	<logic:notEmpty name="KualiForm" property="document.relatedCreditMemoViews[${viewCtr}].notes">
					<tr>
						<kul:htmlAttributeHeaderCell scope="col">Date</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col">User</kul:htmlAttributeHeaderCell>
						<kul:htmlAttributeHeaderCell scope="col">Note</kul:htmlAttributeHeaderCell>
		        	</tr>
			 			<logic:iterate id="note" name="KualiForm" property="document.relatedCreditMemoViews[${viewCtr}].notes" indexId="noteCtr">
			        		<tr>
			        			<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedCreditMemoViews[${viewCtr}].notes[${noteCtr}].notePostedTimestamp" />
				        		</td>
				        		<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedCreditMemoViews[${viewCtr}].notes[${noteCtr}].authorUniversal.personName" />
				        		</td>
				        		<td align="left" valign="middle" class="datacell">
				        			<bean:write name="KualiForm" property="document.relatedCreditMemoViews[${viewCtr}].notes[${noteCtr}].noteText" />
				        		</td>
				        	</tr>
			        	</logic:iterate>
			        </logic:notEmpty>
			        <logic:empty name="KualiForm" property="document.relatedCreditMemoViews[${viewCtr}].notes">
				        <tr>
				            <th align="center" valign="middle" class="bord-l-b">No Notes</th>
				        </tr>
			        </logic:empty>
		    	</table>
	       	</logic:iterate>
		    <br />
		    <br />
		</logic:notEmpty>
    </div>
</kul:tab>
