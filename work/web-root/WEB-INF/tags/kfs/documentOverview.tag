<%--
 Copyright 2005-2007 The Kuali Foundation.
 
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

<%@ attribute name="editingMode" required="true" description="used to decide editability of overview fields" type="java.util.Map"%>
<%@ attribute name="includePostingYear" required="false" description="set to true to include posting year in document overview" %>
<%@ attribute name="postingYearOnChange" required="false" description="set to the value of the onchange event for the posting year control" %>
<%@ attribute name="includePostingYearRefresh" required="false" description="set to true to include posting year refresh button in document overview" %>
<%@ attribute name="postingYearAttributes" required="false" type="java.util.Map" description="The DataDictionary entry containing attributes for the posting year field." %>
<%@ attribute name="fiscalYearReadOnly" required="false" description="set to true to make the posting year read-only" %>

<c:set var="readOnly" value="${empty editingMode['fullEntry']}" />
<c:set var="financialDocHeaderAttributes" value="${DataDictionary.FinancialSystemDocumentHeader.attributes}" />
<c:set var="includeTotalAmount" value="${KualiForm.documentActionFlags.hasAmountTotal}" />

<kul:documentOverview editingMode="${editingMode}">
	<c:if test="${includePostingYear or includeTotalAmount}">
	    <h3><c:out value="Financial Document Detail"/></h3>
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
		                readOnly="${!KualiForm.editingMode['fullEntry'] or fiscalYearReadOnly}"
		              />
		          <c:if test="${!readOnly and includePostingYearRefresh}">
		            <html:image property="methodToCall.refresh" src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_refresh.gif" alt="refresh" title="refresh" styleClass="tinybutton"/>    
		          </c:if>   
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
