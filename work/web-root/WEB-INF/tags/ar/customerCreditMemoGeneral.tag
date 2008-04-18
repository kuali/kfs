<%--
 Copyright 2006-2007 The Kuali Foundation.
 
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
<%@ include file="/jsp/kfs/kfsTldHeader.jsp"%>

<script type='text/javascript' src="dwr/interface/CustomerService.js"></script>
<script language="JavaScript" type="text/javascript" src="scripts/ar/customerObjectInfo.js"></script>

<%@ attribute name="documentAttributes" required="true" type="java.util.Map"
              description="The DataDictionary entry containing attributes." %>
              
<kul:tab tabTitle="General" defaultOpen="false" tabErrorKey="${KFSConstants.CUSTOMER_INVOICE_DOCUMENT_GENERAL_ERRORS}">
    <div class="tab-container" align=center>
    	<div class="h2-container">
        	<h2>General</h2>
        </div>	
        <table cellpadding="0" cellspacing="0" class="datatable" summary="General Section">
        	<tr>
        		<kul:htmlAttributeHeaderCell align="right" literalLabel="Invoice Ref. Number:" scope="row" />
        		<!--  it is going to be a parameter from the first screen -->
        		<td align=left valign=middle class="datacell" style="width: 50%;">12345678</td>
        	</tr>
        	<tr>
        		<kul:htmlAttributeHeaderCell align="right" literalLabel="Customer Id:" scope="row" />
        		<td align=left valign=middle class="datacell" style="width: 50%;">
        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.customerSpecialProcessingCode}"
        			                      	  property="document.invoice.customerSpecialProcessingCode"
        			                          readOnly="true" />
        		</td>
        	</tr>
        	<tr>
        		<kul:htmlAttributeHeaderCell align="right" literalLabel="Customer Name:" scope="row" />
        		<td align=left valign=middle class="datacell" style="width: 50%;">
        			<kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.customerSpecialProcessing.customerSpecialProcessingDescription}"
        			                          property="document.invoice.customerSpecialProcessing.customerSpecialProcessingDescription"
        			                          readOnly="true" />
        		</td>
        	</tr>
            <tr>
            	<kul:htmlAttributeHeaderCell align="right" literalLabel="Invoice Date:" scope="row" />
            	<td align=left valign=middle class="datacell" style="width: 50%;">
            		<kul:htmlControlAttribute attributeEntry="${documentAttributes.invoice.billingDate}"
            		                          property="document.invoice.billingDate"
            		                          readOnly="true" />
            	</td>
            </tr>
            <tr>
        		<kul:htmlAttributeHeaderCell align="right" literalLabel="Invoice Outstanding Days:" scope="row" />
        		<!--  service should be called here -->
        		<td align=left valign=middle class="datacell" style="width: 50%;">5</td>
        	</tr>
		</table>
	</div>
</kul:tab>
