<%@ include file="/jsp/core/tldHeader.jsp" %>

<kul:page showDocumentInfo="false" htmlFormAction="glBalanceInquiry" headerMenuBar="${KualiForm.lookupable.htmlMenuBar}" headerTitle="Lookup" docTitle="" transactionalDocument="false">
 
    <kul:enterKey methodToCall="search" />
    
    <html-el:hidden name="KualiForm" property="backLocation" />
    <html-el:hidden name="KualiForm" property="formKey" />
    <html-el:hidden name="KualiForm" property="lookupableImplServiceName" />
    <html-el:hidden name="KualiForm" property="businessObjectClassName" />
    <html-el:hidden name="KualiForm" property="conversionFields" />
    <html-el:hidden name="KualiForm" property="hideReturnLink" />
    <html-el:hidden property="listKey" value="${listKey}" />
    
    <div class="doctitle">
      <div class="left">
        <div class="title">
          <c:out value="${KualiForm.lookupable.title}" />
        </div>
      </div>
      <div class="right">
        <div class="sublinks">
          <kul:help resourceKey="lookupHelpText" altText="lookup help" />
        </div>
      </div>
    </div>  

    <div class="topblurb">
      <c:out value="${KualiForm.lookupable.lookupInstructions}" />
    </div>
    
    <kul:errors errorTitle="Errors found in Search Criteria:" />
    
    <table width="100%" cellspacing="0" cellpadding="0">
      <tr>
        <td width="20">
          <img src="images/pixel_clear.gif" alt="" width="20" height="20">
        </td>
        
        <td>
        <c:if test="${param.inquiryFlag != 'true'}">
          <div align="center">
            <table align="center" cellpadding=0 class="datatable-nowidth">
              <c:set var="FormName" value="KualiForm" scope="request" />
              <c:set var="FieldRows" value="${KualiForm.lookupable.rows}" scope="request" />
              <c:set var="ActionName" value="glBalanceInquiry.do" scope="request" />
              <c:set var="IsLookupDisplay" value="true" scope="request" />
                                       
	          <%@ include file="/jsp/core/RowDisplay.jsp" %>
          
              <tr align=center>
                <td height="30" colspan=2 class="infoline">
                  <html:image
							property="methodToCall.search" value="search"
							src="images/buttonsmall_search.gif" styleClass="tinybutton"
							alt="search" border="0" />
							
                  <html:image
							property="methodToCall.clearValues" value="clearValues"
							src="images/buttonsmall_clear.gif" styleClass="tinybutton"
							alt="clear" border="0" />
							
                  <c:if test="${KualiForm.formKey!=''}">
                    <a href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />'>
                      <img src="images/buttonsmall_cancel.gif" class="tinybutton" border="0" /></a>
                  </c:if>
                  
                  <!-- Optional extra button -->
                  <c:if test="${not empty KualiForm.lookupable.extraButtonSource}">
                		<a href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&refreshCaller=org.kuali.core.lookup.KualiLookupableImpl&docFormKey=${KualiForm.formKey}" /><c:out value="${KualiForm.lookupable.extraButtonParams}" />' >
                  		<img src='<c:out value="${KualiForm.lookupable.extraButtonSource}" />' class="tinybutton" border="0" /></a>
              		</c:if>
                  
                </td>
              </tr>
            </table>
          </div>
          </c:if>
          
          <c:if test="${param.inquiryFlag == 'true'}">
          	<c:set var="url" value="${pageContext.request.requestURL}"/>
          	
			<c:url value="${url}" var="switch">
				<c:forEach items="${param}" var="params">
					<c:if test="${params.key == 'dummyBusinessObject.amountViewOption'}" >
						<c:if test="${params.value == 'Accumulate' }" >
							<c:param name="${params.key}" value="Monthly"/>
						</c:if>
						<c:if test="${params.value != 'Accumulate' }" >
							<c:param name="${params.key}" value="Accumulate"/>
						</c:if>
					</c:if>
					
					<c:if test="${params.key != 'viewAmountOption'}"> 
						<c:param name="${params.key}" value="${params.value}"/>
					</c:if>
				</c:forEach>
			</c:url>
			
			<a href="<c:out value='${switch}'/>">Test</a>
          </c:if>
          
          <br/><br/>
          <div class="right">
            <logic-el:present name="KualiForm" property="formKey">
              <c:if test="${KualiForm.formKey!='' && KualiForm.hideReturnLink != true}">
                <a href='<c:out value="${KualiForm.backLocation}?methodToCall=refresh&docFormKey=${KualiForm.formKey}" />'>
                  return with no value
                </a>
              </c:if>
            </logic-el:present>
          </div>
          
          <c:if test="${reqSearchResultsActualSize>0}" >
          	<c:out value="${reqSearchResultsActualSize}" /> items found.  Please refine your search criteria to narrow down your search.
          </c:if>
         
          <display:table width="100%" class="datatable-100" cellspacing="0" cellpadding="0" 
				name="${reqSearchResults[0]}" export="true" pagesize="100"
				requestURI="lookup.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&listKey=${listKey}">
				
			<display:column>      
	          <table width="100%" class="datatable-100" cellspacing="0" cellpadding="0">
	          	<c:forEach items="${reqSearchResults}" var="row" varStatus="status">
	          		<c:if test="${status.count == 1}">
		               	<tr>
			          		<c:forEach items="${row.columns}" var="column" end="11">
			          			<th class="infocell"><c:out value="${column.columnTitle}" /></th>
			          		</c:forEach>
			          	</tr>
			        </c:if>
	          		
		          	<tr>
		          		<c:forEach items="${row.columns}" var="column" end="11">
			          		<c:if test="${column.propertyURL!=\"\" && param['d-16544-e'] == null}">
			          			<td class="infocell">
				          			<a href="<c:out value="${column.propertyURL}"/>" target="blank">
										<c:out value="${column.propertyValue}" />
									</a>
								</td>
							</c:if>	
							<c:if test="${column.propertyURL==\"\" || param['d-16544-e'] != null}">
			          			<td class="infocell">
									<c:out value="${column.propertyValue}" />
								</td>
							</c:if>	               				          			
		          		</c:forEach>
		          	</tr>
		          	
		          	<tr>
		          		<td></td>
		          		<td colspan="11" class="infocell">
			          		<table width="100%" class="datatable-100" cellspacing="0" cellpadding="0">
				          		<c:forEach items="${row.columns}" var="column" begin="12" varStatus="columnStatus">
				          			<c:if test="${columnStatus.count % 4 == 1}"><tr></c:if>
				          			
				          			<td class="infocell"><c:out value="${column.columnTitle}" /></td>				          			
				          			<c:if test="${column.propertyURL!=\"\" && param['d-16544-e'] == null}">
					          			<td class="infocell">
						          			<a href="<c:out value="${column.propertyURL}"/>" target="blank">
												<c:out value="${column.propertyValue}" />
											</a>
										</td>
									</c:if>	
									<c:if test="${column.propertyURL==\"\" || param['d-16544-e'] != null}">
					          			<td class="infocell">
											<c:out value="${column.propertyValue}" />
										</td>
									</c:if>
										          
				          			<c:if test="${columnStatus.count % 4 == 0}"></tr></c:if>
				          		</c:forEach>
				          	</table>
			          	</td>
		          	</tr>
		        </c:forEach>
		     </table>
		</display:column>
		</display:table>
	           
        </td>
      </tr>
    </table>
</kul:page>