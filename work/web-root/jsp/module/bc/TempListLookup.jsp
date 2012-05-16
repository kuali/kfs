<%--
 Copyright 2007 The Kuali Foundation
 
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

<c:set var="csfTrackerAttributes"	value="${DataDictionary.BudgetConstructionCalculatedSalaryFoundationTracker.attributes}" />

<%--NOTE: DO NOT FORMAT THIS FILE, DISPLAY:COLUMN WILL NOT WORK CORRECTLY IF IT CONTAINS LINE BREAKS --%>
<c:set var="headerMenu" value="" />
<c:if test="${KualiForm.suppressActions!=true}">
    <c:set var="headerMenu" value="${KualiForm.lookupable.createNewUrl}   ${KualiForm.lookupable.htmlMenuBar}" />
</c:if>
<kul:page lookup="true" showDocumentInfo="false"
	headerMenuBar="${headerMenu}"
	headerTitle="Lookup" docTitle="" transactionalDocument="false"
	htmlFormAction="budgetTempListLookup">

	<SCRIPT type="text/javascript">
    var kualiForm = document.forms['KualiForm'];
    var kualiElements = kualiForm.elements;
  </SCRIPT>

	<div class="headerarea-small" id="headerarea-small">
	<h1><c:out value="${KualiForm.lookupable.title}" /><kul:help
		resourceKey="lookupHelpText" altText="lookup help" /></h1>
	</div>
	<kul:enterKey methodToCall="search" />

	<html-el:hidden name="KualiForm" property="backLocation" />
	<html-el:hidden name="KualiForm" property="formKey" />
	<html-el:hidden name="KualiForm" property="lookupableImplServiceName" />
	<html-el:hidden name="KualiForm" property="businessObjectClassName" />
	<html-el:hidden name="KualiForm" property="conversionFields" />
	<html-el:hidden name="KualiForm" property="hideReturnLink" />
	<html-el:hidden name="KualiForm" property="suppressActions" />
	<html-el:hidden name="KualiForm" property="multipleValues" />
	<html-el:hidden name="KualiForm" property="lookupAnchor" />
	<html-el:hidden name="KualiForm" property="readOnlyFields" />
	<html-el:hidden name="KualiForm" property="referencesToRefresh" />
	<html-el:hidden name="KualiForm" property="reportMode" />
	<html-el:hidden name="KualiForm" property="currentPointOfViewKeyCode" />
	<html-el:hidden name="KualiForm" property="buildControlList" />
	<html-el:hidden name="KualiForm" property="reportConsolidation" />
	<html-el:hidden name="KualiForm" property="tempListLookupMode" />
	<html-el:hidden name="KualiForm" property="forceToAccountListScreen" />
	<html-el:hidden name="KualiForm" property="showSalaryByPositionAction" />
	<html-el:hidden name="KualiForm" property="addLine" />
	<html-el:hidden name="KualiForm" property="showSalaryByIncumbentAction" />
	<html-el:hidden name="KualiForm" property="budgetByAccountMode" />
	<html-el:hidden name="KualiForm" property="mainWindow" />

    <%-- only need hidden fiscal year when used inside BC --%>
    <c:if test="${KualiForm.tempListLookupMode != BCConstants.TempListLookupMode.DEFAULT_LOOKUP_MODE}">
      <html-el:hidden name="KualiForm" property="universityFiscalYear" />
    </c:if>

	<c:forEach items="${KualiForm.extraButtons}" varStatus="status">
		<html-el:hidden name="KualiForm" property="extraButtons[${status.index}].extraButtonSource" />
		<html-el:hidden name="KualiForm" property="extraButtons[${status.index}].extraButtonParams" />
	</c:forEach>

   	<table width="100%">
		<tr>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20"
				height="20"></td>
			<td>
			<td>
			   <br/>
	           <kul:errors errorTitle="Errors Found:" />
	           <kul:messages/>
	
	           <c:forEach items="${KualiForm.messages}" var="message">
	             ${message}
	          </c:forEach>
            </td>
         </tr>
    </table>
            
	<table width="100%">
		<tr>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20"
				height="20"></td>
			<td>

			<div id="lookup" align="center"><br />
			<br />
			<table align="center" cellpadding=0 cellspacing=0 class="datatable-100">
				<c:set var="FormName" value="KualiForm" scope="request" />
				<c:set var="FieldRows" value="${KualiForm.lookupable.rows}" scope="request" />
				<c:set var="ActionName" value="budgetTempListLookup.do" scope="request" />
				<c:set var="IsLookupDisplay" value="true" scope="request" />
				<c:set var="cellWidth" value="50%" scope="request" />

                <kul:rowDisplay rows="${FieldRows}" skipTheOldNewBar="true" />

				<!-- changed cancel to call cancel action where the call to clean up temp table is located 
						alt="clear" title="clear" border="0" /> <c:if test="${KualiForm.formKey!=''}">
						<a
							href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}" />'  title="cancel">
						<img src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" class="tinybutton" alt="cancel" title="cancel" 
							border="0" /></a>
					</c:if>
                --> 					
				<tr align=center>
					<td height="30" colspan=2 class="infoline">
					<c:if test="${KualiForm.forceToAccountListScreen != true}">
					<c:if test="${KualiForm.tempListLookupMode == BCConstants.TempListLookupMode.ACCOUNT_SELECT_ABOVE_POV}">
					  <html:image
					  	  property="methodToCall.submitReport" value="submit"
						  src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_submit.gif" styleClass="tinybutton"
						  alt="submit" title="submit" border="0" onblur="formHasAlreadyBeenSubmitted = false"/>
					</c:if>
					<c:if test="${KualiForm.tempListLookupMode != BCConstants.TempListLookupMode.SHOW_BENEFITS}">
					<html:image
						property="methodToCall.search" value="search"
						src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_search.gif" styleClass="tinybutton"
						alt="search" title="search" border="0" /> 
						<html:image
						property="methodToCall.clearValues" value="clearValues"
						src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_clear.gif" styleClass="tinybutton"
						alt="clear" title="clear" border="0" /> 
					</c:if>
					</c:if>
					<c:if test="${KualiForm.forceToAccountListScreen == true}">
						<html:image
					  	  	property="methodToCall.submitReport" value="submit"
						  	src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_submit.gif" styleClass="tinybutton"
						  	alt="submit" title="submit" border="0" onclick="excludeSubmitRestriction=true"/>
					</c:if>
					<c:choose>
						<c:when test="${KualiForm.tempListLookupMode == BCConstants.TempListLookupMode.CSF_TRACKER_POSITION_LOOKUP}" >
							<html:image
							property="methodToCall.refresh" value="cancel"
							src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="tinybutton"
							onclick="window.close()" alt="cancel" title="cancel" border="0" />
						</c:when>
						<c:otherwise>
							<html:image
							property="methodToCall.cancel" value="cancel"
							src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_cancel.gif" styleClass="tinybutton"
							alt="cancel" title="cancel" border="0" />
						</c:otherwise>
	            	</c:choose>
	                <c:if test="${KualiForm.tempListLookupMode == BCConstants.TempListLookupMode.POSITION_SELECT}">
					  <html:image
					  	  property="methodToCall.performExtendedPositionSearch" value="submit"
						  src="${ConfigProperties.externalizable.images.url}buttonsmall_extended.gif" styleClass="tinybutton"
						  alt="perform extended search" title="perform extended search" border="0"/>
					</c:if>
					
	                <c:if test="${KualiForm.tempListLookupMode == BCConstants.TempListLookupMode.INTENDED_INCUMBENT_SELECT}">
					  <html:image
					  	  property="methodToCall.performExtendedIncumbentSearch" value="submit"
						  src="${ConfigProperties.externalizable.images.url}buttonsmall_extended.gif" styleClass="tinybutton"
						  alt="perform extended search" title="perform extended search" border="0"/>
					</c:if>
					
			        <c:if test="${KualiForm.tempListLookupMode == BCConstants.TempListLookupMode.BUDGET_POSITION_LOOKUP}">
					  <c:if test="${KualiForm.getNewPositionEnabled}">
					    <html:image
					  	    property="methodToCall.getNewPosition" value="submit"
						    src="${ConfigProperties.externalizable.images.url}buttonsmall_getnew.gif" styleClass="tinybutton"
						    alt="get new position" title="get new position" border="0"/>
					  </c:if>	
					  <c:if test="${KualiForm.addLine}">
					  	<html-el:hidden name="KualiForm" property="chartOfAccountsCode" />
	                    <html-el:hidden name="KualiForm" property="accountNumber" />
	                    <html-el:hidden name="KualiForm" property="subAccountNumber" />
	                    <html-el:hidden name="KualiForm" property="financialObjectCode" />
	                    <html-el:hidden name="KualiForm" property="financialSubObjectCode" />
					  </c:if>
					</c:if>
					
					<c:if test="${KualiForm.tempListLookupMode == BCConstants.TempListLookupMode.INTENDED_INCUMBENT}">
					  <c:if test="${KualiForm.getNewIncumbentEnabled}">
					    <html:image
					  	    property="methodToCall.getNewIncumbent" value="submit"
						    src="${ConfigProperties.externalizable.images.url}buttonsmall_getnew.gif" styleClass="tinybutton"
						    alt="get new incumbent" title="get new incumbent" border="0"/>
					  </c:if>
					  <c:if test="${KualiForm.addLine}">
					  	<html-el:hidden name="KualiForm" property="chartOfAccountsCode" />
	                    <html-el:hidden name="KualiForm" property="accountNumber" />
	                    <html-el:hidden name="KualiForm" property="subAccountNumber" />
	                    <html-el:hidden name="KualiForm" property="financialObjectCode" />
	                    <html-el:hidden name="KualiForm" property="financialSubObjectCode" />
					  </c:if>
					</c:if>
				
					<!-- Optional extra buttons --> 					
					<c:forEach items="${KualiForm.extraButtons}" var="extraButton" varStatus="status">
						<c:if test="${!empty extraButton.extraButtonSource && !empty extraButton.extraButtonParams}">
							<a href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&refreshCaller=kualiLookupable&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}" /><c:out value="${extraButton.extraButtonParams}" />'>
							<img src='<c:out value="${extraButton.extraButtonSource}" />'
								class="tinybutton" border="0" /></a>
						</c:if> 
					</c:forEach>
					<c:if test="${KualiForm.multipleValues }">
						<a
							href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&anchor=${KualiForm.lookupAnchor}" />'>
						<img src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_retnovalue.gif" class="tinybutton"
							border="0" /></a>
						<a
							href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}&refreshCaller=multipleValues&searchResultKey=${searchResultKey}&searchResultDataKey=${searchResultDataKey}&anchor=${KualiForm.lookupAnchor}"/>'>
						<img src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_returnthese.gif" class="tinybutton"
							border="0" /></a>
					</c:if>						
					</td>
				</tr>
				
			</table>
			</div>
			<c:if test="${KualiForm.tempListLookupMode == BCConstants.TempListLookupMode.CSF_TRACKER_POSITION_LOOKUP}" >
				<br>
				<table bgcolor="#C0C0C0" cellpadding="30" >
					
					<tr>
						<td> 
                            <!-- hiddens for search criteria merging -->
                            <html-el:hidden name="KualiForm" property="financialObjectCode" />
                            <html-el:hidden name="KualiForm" property="financialSubObjectCode" />
                            
							<b><kul:htmlAttributeLabel attributeEntry="${csfTrackerAttributes.universityFiscalYear}" /></b>
								<kul:htmlControlAttribute property="universityFiscalYear" readOnly="true" attributeEntry="${csfTrackerAttributes.universityFiscalYear}"/>
						</td> 
						<td> 
							<b><kul:htmlAttributeLabel attributeEntry="${csfTrackerAttributes.chartOfAccountsCode}" /></b>
							<kul:htmlControlAttribute property="chartOfAccountsCode" readOnly="true" attributeEntry="${csfTrackerAttributes.chartOfAccountsCode}"/>
						</td>
						<td> 
							<b><kul:htmlAttributeLabel attributeEntry="${csfTrackerAttributes.accountNumber}" /></b>
							<kul:htmlControlAttribute property="accountNumber" readOnly="true" attributeEntry="${csfTrackerAttributes.accountNumber}"/>
						</td>
						<td> 
							<b><kul:htmlAttributeLabel attributeEntry="${csfTrackerAttributes.subAccountNumber}" /></b>
							<kul:htmlControlAttribute property="subAccountNumber" readOnly="true" attributeEntry="${csfTrackerAttributes.subAccountNumber}"/>
						</td>
					<tr>
				</table>
			</c:if>
			<br>
			<br>
			<c:if test="${reqSearchResultsActualSize>0}">
				<c:out value="${reqSearchResultsActualSize}" /> items found.  Please refine your search criteria to narrow down your search.
          </c:if> 
			<c:if test="${!empty reqSearchResultsActualSize }">
			    <c:if test="${KualiForm.searchUsingOnlyPrimaryKeyValues}">
			    	<bean-el:message key="lookup.using.primary.keys" arg0="${KualiForm.primaryKeyFieldLabels}"/>
			    	<br/><br/>
			    </c:if>
				<display:table class="datatable-100" cellspacing="0"
				requestURIcontext="false" cellpadding="0" name="${reqSearchResults}"
				id="row" export="true" pagesize="100"
				requestURI="budgetTempListLookup.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&searchResultKey=${searchResultKey}&searchUsingOnlyPrimaryKeyValues=${KualiForm.searchUsingOnlyPrimaryKeyValues}">

				<c:if test="${param['d-16544-e'] == null}">
			  	  <logic:present name="KualiForm" property="formKey">
					  <c:if
						test="${KualiForm.formKey!='' && KualiForm.hideReturnLink!=true && !KualiForm.multipleValues}">
						<display:column class="infocell" property="returnUrl" media="html" />
					  </c:if>
					  <c:if test="${row.actionUrls!='' && KualiForm.suppressActions!=true}">
						<display:column class="infocell" property="actionUrls"
							title="Actions" media="html" />
					  </c:if>
				  </logic:present>
				</c:if>
				
				<c:forEach items="${row.columns}" var="column" varStatus="loopStatus">
          <c:set var="colClass" value="${ fn:startsWith(column.formatter, 'org.kuali.rice.core.web.format.CurrencyFormatter') ? 'numbercell' : 'infocell' }" />
					<c:choose>
						<%--NOTE: Check if exporting first, as this should be outputted without extra HTML formatting --%>
						<c:when	test="${param['d-16544-e'] != null}">
								<display:column class="${colClass}" sortable="${column.sortable}"
									title="${column.columnTitle}" comparator="${column.comparator}"
									maxLength="${column.maxLength}"><c:out value="${column.propertyValue}" escapeXml="false" default="" /></display:column>
						</c:when>
						<c:when	test="${!empty column.propertyURL}">
							<display:column class="${colClass}" sortable="${column.sortable}"
								title="${column.columnTitle}" comparator="${column.comparator}">
								<a href="<c:out value="${column.propertyURL}"/>" target="_blank" title="${column.propertyValue}"><c:out
									value="${fn:substring(column.propertyValue, 0, column.maxLength)}"
									/><c:if test="${column.maxLength gt 0 && fn:length(column.propertyValue) gt column.maxLength}">...</c:if></a> &nbsp;
                            </display:column>
						</c:when>
<%--NOTE: DO NOT FORMAT THIS FILE, DISPLAY:COLUMN WILL NOT WORK CORRECTLY IF IT CONTAINS LINE BREAKS --%>
						<c:when test="${column.columnTitle == 'Project Code'}">
							<display:column class="${colClass}" sortable="${column.sortable}"
								title="${column.columnTitle}" comparator="${column.comparator}"
								maxLength="${column.maxLength}" decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"><div style="white-space: nowrap"><c:out value="${column.propertyValue}" />&nbsp;</div></display:column>
                        </c:when>
						<c:otherwise>
							<display:column class="${colClass}" sortable="${column.sortable}"
								title="${column.columnTitle}" comparator="${column.comparator}"
								maxLength="${column.maxLength}" decorator="org.kuali.rice.kns.web.ui.FormatAwareDecorator"><c:out value="${column.propertyValue}"/>&nbsp;</display:column>
                        </c:otherwise>
					</c:choose>
				</c:forEach>

			</display:table>
			</c:if></td>
			<td width="1%"><img src="${ConfigProperties.kr.externalizable.images.url}pixel_clear.gif" alt="" width="20"
				height="20"></td>
		</tr>
	</table>
</kul:page>
