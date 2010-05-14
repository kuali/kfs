<%--
 Copyright 2005-2009 The Kuali Foundation
 
 Licensed under the Educational Community License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at
 
 http://www.opensource.org/licenses/ecl2.php
 
 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
--%>
<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<%@ attribute name="endowDocAttributes" required="false" type="java.util.Map" description="The DataDictionary entry containing attributes for the endowment document." %>

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="financialDocHeaderAttributes" value="${DataDictionary.FinancialSystemDocumentHeader.attributes}" />
<c:set var="includeTotalAmount" value="${not empty editingMode[KFSConstants.AMOUNT_TOTALING_EDITING_MODE]}" />
<c:set var="includeTotalUnits" value="${not empty editingMode[EndowConstants.UNITS_TOTALING_EDITING_MODE]}" />

<kul:documentOverview editingMode="${editingMode}">
	<c:if test="${includeTotalUnits or includeTotalAmount}">
	  <h3><c:out value="Endowment Document Details"/></h3>
	  
	  <table cellpadding="0" cellspacing="0" class="datatable" summary="KFS Detail Section">
	    <tr>
	      <c:choose>
	        <c:when test="${includeTotalUnits}">
		        <kul:htmlAttributeHeaderCell
		                labelFor="document.totalUnits"
		                attributeEntry="${endowDocAttributes.totalUnits}"
		                horizontal="true"
		              />
		
		        <td align="left" valign="middle">
		          <kul:htmlControlAttribute 
		                attributeEntry="${endowDocAttributes.totalUnits}" 
		                property="document.totalUnits" 
		                readOnly="true"
		              />
		          
		        </td>
	        </c:when>
	        <c:otherwise>
	          <th colspan="2">
	             &nbsp;
	          </th>
	        </c:otherwise>
	      </c:choose>
	      <c:choose>
	        <c:when test="${includeTotalAmount}">
	          <kul:htmlAttributeHeaderCell
	                  labelFor="document.documentHeader.financialDocumentTotalAmount"
	                  attributeEntry="${financialDocHeaderAttributes.financialDocumentTotalAmount}"
	                  horizontal="true"/>
	
	          <td align="left" valign="middle">
	            <kul:htmlControlAttribute 
	                  attributeEntry="${financialDocHeaderAttributes.financialDocumentTotalAmount}" 
	                  property="document.documentHeader.financialDocumentTotalAmount" 
	                  readOnly="true"/>
	          </td>
	        </c:when>
	        <c:otherwise>
	          <th colspan="2">
	             &nbsp;
	          </th>
	        </c:otherwise>
	      </c:choose>
	    </tr>
	    
	   
	  </table>
	</c:if>
	<jsp:doBody/>
</kul:documentOverview>
