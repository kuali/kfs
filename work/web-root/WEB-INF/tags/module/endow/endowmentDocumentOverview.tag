<%--
   - The Kuali Financial System, a comprehensive financial management system for higher education.
   - 
   - Copyright 2005-2014 The Kuali Foundation
   - 
   - This program is free software: you can redistribute it and/or modify
   - it under the terms of the GNU Affero General Public License as
   - published by the Free Software Foundation, either version 3 of the
   - License, or (at your option) any later version.
   - 
   - This program is distributed in the hope that it will be useful,
   - but WITHOUT ANY WARRANTY; without even the implied warranty of
   - MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   - GNU Affero General Public License for more details.
   - 
   - You should have received a copy of the GNU Affero General Public License
   - along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
