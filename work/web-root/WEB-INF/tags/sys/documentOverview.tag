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
<%@ attribute name="includePostingYear" required="false" description="set to true to include posting year in document overview" %>
<%@ attribute name="postingYearOnChange" required="false" description="set to the value of the onchange event for the posting year control" %>
<%@ attribute name="includePostingYearRefresh" required="false" description="set to true to include posting year refresh button in document overview" %>
<%@ attribute name="postingYearAttributes" required="false" type="java.util.Map" description="The DataDictionary entry containing attributes for the posting year field." %>
<%@ attribute name="fiscalYearReadOnly" required="false" description="set to true to make the posting year read-only" %>
<%@ attribute name="includeBankCode" required="false" description="set to true to include bank code in document overview" %>
<%@ attribute name="bankProperty" required="false" description="name of the property that holds the bank code value in the form" %>
<%@ attribute name="bankObjectProperty" required="false" description="name of the property that holds the bank object in the form" %>
<%@ attribute name="depositOnly" required="false" description="boolean indicating whether the bank lookup call should request only deposit banks" %>
<%@ attribute name="disbursementOnly" required="false" description="boolean indicating whether the bank lookup call should request only disbursement banks" %>
<%@ attribute name="viewYearEndAccountPeriod" required="false" description="boolean indicating whether year end accounting period should display" %>

<c:set var="readOnly" value="${!KualiForm.documentActions[Constants.KUALI_ACTION_CAN_EDIT]}" />
<c:set var="financialDocHeaderAttributes" value="${DataDictionary.FinancialSystemDocumentHeader.attributes}" />
<c:set var="includeTotalAmount" value="${not empty editingMode[KFSConstants.AMOUNT_TOTALING_EDITING_MODE]}" />

<kul:documentOverview editingMode="${editingMode}">	
	<c:if test="${includePostingYear or includeTotalAmount or includeBankCode or viewYearEndAccountPeriod}">
	  <h3><c:out value="Financial Document Detail"/></h3>
	  
	  <c:if test="${includeBankCode}">
	      <div class="tab-container-error"><div class="left-errmsg-tab"><kul:errors keyMatch="${bankProperty}"/></div></div>
	  </c:if>	  
	  <table cellpadding="0" cellspacing="0" class="datatable" summary="KFS Detail Section">	  
	    <tr>
	      <c:choose>
	        <c:when test="${includePostingYear}">
		        <kul:htmlAttributeHeaderCell
		                labelFor="document.postingYear"
		                attributeEntry="${postingYearAttributes.postingYear}"
		                horizontal="true"
		              />
		
		        <td class="datacell-nowrap">
		          <kul:htmlControlAttribute 
		                attributeEntry="${postingYearAttributes.postingYear}" 
		                property="document.postingYear" 
		                onchange="${postingYearOnChange}"
		                readOnly="${readOnly or fiscalYearReadOnly}"
		              />
		          <c:if test="${!readOnly and includePostingYearRefresh}">
		            <html:image property="methodToCall.refresh" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif" alt="refresh" title="refresh" styleClass="tinybutton"/>    
		          </c:if>   
		        </td>
	        </c:when>
	        <c:when test="${includeBankCode}">
	            <sys:bankLabel align="right"/>
	            <sys:bankControl property="${bankProperty}" objectProperty="${bankObjectProperty}" depositOnly="${depositOnly}" disbursementOnly="${disbursementOnly}" readOnly="${readOnly}"/>
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
	    
	    <!-- need to display bank code in a new row if it was not displayed above -->
	    <c:if test="${includePostingYear and includeBankCode}">
	      <tr>
		     <sys:bankLabel align="right"/>
	         <sys:bankControl property="${bankProperty}" objectProperty="${bankObjectProperty}" depositOnly="${depositOnly}" disbursementOnly="${disbursementOnly}" readOnly="${readOnly}"/>      
	      </tr>
	    </c:if>
	   
	   	<!-- CSU 6702 BEGIN -->
	   	<!-- rSmart-jkneal-KFSCSU-199-begin mod for displaying accounting period field -->	   	 
	   	<c:if test="${!empty KualiForm.documentActions[KFSConstants.YEAR_END_ACCOUNTING_PERIOD_VIEW_DOCUMENT_ACTION]}">	   
	      	<c:set var="accountingPeriodAttribute" value="${DataDictionary.LedgerPostingDocumentBase.attributes.accountingPeriodCompositeString}" />
	      	<tr>	      
			  	<kul:htmlAttributeHeaderCell
		         		labelFor="document.accountingPeriodCompositeString"
		                attributeEntry="${accountingPeriodAttribute}"
		                horizontal="true" useShortLabel="false" />
		
		       	<td class="datacell-nowrap">
		          	<kul:htmlControlAttribute 
		                attributeEntry="${accountingPeriodAttribute}" 
		                property="document.accountingPeriodCompositeString" 
		                readOnly="${readOnly || empty KualiForm.documentActions[KFSConstants.YEAR_END_ACCOUNTING_PERIOD_EDIT_DOCUMENT_ACTION]}"
		                readOnlyBody="true" >
		                	${KualiForm.document.accountingPeriod.universityFiscalPeriodName}
		           	</kul:htmlControlAttribute>      
		      	</td>
		      	<th colspan="2">&nbsp;</th>       
	      	</tr>
	   	</c:if>
	   	<!-- rSmart-jkneal-KFSCSU-199-end mod --> 
	   	<!-- CSU 6702 END -->
	   	   
	  </table>
	</c:if>
	<jsp:doBody/>
</kul:documentOverview>
