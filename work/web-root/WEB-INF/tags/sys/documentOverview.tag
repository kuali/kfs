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
<c:set var="canViewSecuredField" value="${KualiForm.documentActions[KFSConstants.KFS_ACTION_CAN_VIEW_SECURED_FIELD]}" />


<kul:documentOverview editingMode="${editingMode}">	
    <table cellpadding="0" cellspacing="0" class="datatable" summary="KFS Detail Section">
	   <!-- secured field -->
	   <tr>
	       <kul:htmlAttributeHeaderCell
	           labelFor="document.documentHeader.securedField"
	           attributeEntry="${financialDocHeaderAttributes.securedField}"
	           horizontal="true"/>
	      
	        <c:choose>
   	           <c:when test="${canViewSecuredField}">
	               <td class="datacell-nowrap">
 	                   <kul:htmlControlAttribute 
 	                       attributeEntry="${financialDocHeaderAttributes.securedField}" 
 	                       property="document.documentHeader.securedField" 
 	                       readOnly="${readOnly}"/>
 	               </td>	   	
	           </c:when>
	           <c:otherwise>
	               <td class="datacell-nowrap"> ${KFSConstants.SECURED_FIELD_MASK} </td>
	           </c:otherwise>
	       </c:choose>      	
	          
	   </tr>
	</table>   
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
