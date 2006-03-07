<%@ include file="/jsp/core/tldHeader.jsp" %>

<kul:pageLookup showDocumentInfo="false" htmlFormAction="glAccountBalanceByConsolidationLookup" 
	headerMenuBar="${KualiForm.lookupable.htmlMenuBar}" headerTitle="Lookup" 
	docTitle="" transactionalDocument="false">

	<div class="headerarea" id="headerarea">
	  	<h1>
	  		<c:out value="${KualiForm.lookupable.title}" />
	  		<kul:help resourceKey="lookupHelpText" altText="lookup help" />
	  	</h1>
	</div>
	 
    <kul:enterKey methodToCall="search" />
    
    <html-el:hidden name="KualiForm" property="backLocation" />
    <html-el:hidden name="KualiForm" property="formKey" />
    <html-el:hidden name="KualiForm" property="lookupableImplServiceName" />
    <html-el:hidden name="KualiForm" property="businessObjectClassName" />
    <html-el:hidden name="KualiForm" property="conversionFields" />
    <html-el:hidden name="KualiForm" property="hideReturnLink" />
    <html-el:hidden property="listKey" value="${listKey}" />
    
    <kul:errors errorTitle="Errors found in Search Criteria:" />
    
    <table width="100%" cellspacing="0" cellpadding="0">
      <tr>
        <td width="1%"><img src="images/pixel_clear.gif" alt="" width="20" height="20" /></td>
        
        <td>
          <div align="center"><br/><br/>
            <table align="center" cellpadding="0" class="datatable-100">
              <c:set var="FormName" value="KualiForm" scope="request" />
              <c:set var="FieldRows" value="${KualiForm.lookupable.rows}" scope="request" />
              <c:set var="ActionName" value="glModifiedInquiry.do" scope="request" />
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
                      <img src="images/buttonsmall_cancel.gif" class="tinybutton" border="0" />
                    </a>
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
	      
	      <c:set var="offset" value="5"/>
	      
	      <display:table width="100%" class="datatable-100" cellspacing="0"
					cellpadding="0" name="${reqSearchResults}" id="row" export="true" pagesize="100" offset="${offset}"
					requestURI="lookup.do?methodToCall=viewResults&reqSearchResultsActualSize=${reqSearchResultsActualSize}&listKey=${listKey}">
	      
	        <c:forEach items="${row.columns}" var="column">
	        
	          <c:if test="${column.propertyURL!=\"\" && param['d-16544-e'] == null}">
	            <display:column class="infocell" sortable="${column.sortable}" title="${column.columnTitle}">
	              <a href="<c:out value="${column.propertyURL}"/>" target="blank">
	                <c:out value="${column.propertyValue}" />
	              </a>
	              &nbsp;
	            </display:column>
	          </c:if>
	         
	          <c:if test="${column.propertyURL==\"\" || param['d-16544-e'] != null}">
	            <display:column class="infocell" sortable="${column.sortable}" title="${column.columnTitle}">
	              <c:out value="${column.propertyValue}" />
	            </display:column>
	          </c:if>
	        </c:forEach>
	      </display:table>
	      
	      <c:if test="${reqSearchResultsActualSize>0}" >
		      <br/><br/>  
		      <div style="float: right; width: 70%;">
		      <table width="100%" class="datatable-100">
		      	<caption align="top" style="text-align: left; font-weight: bold;">Totals</caption>
			      <c:forEach items="${reqSearchResults}" var="row" end="${offset-2}" varStatus="status">
				      <c:if test="${status.count == 1 }" >
					      <thead>
						      <tr>
							      <c:forEach items="${row.columns}" var="column" begin="4" varStatus="colStatus">
							          <c:if test="${colStatus.count==1}">
							            <td class="infocell"> </td>
							          </c:if>
							      	  				         
							          <c:if test="${column.propertyValue!=\"\" && colStatus.count!=1}">
							            <td class="infocell" title="${column.columnTitle}">
							              Total <c:out value="${column.columnTitle}" />
							            </td>
							          </c:if>
						          </c:forEach>
						      </tr>
						  </thead>
				      </c:if>
				      <c:if test="${status.count < 4}" >
					      <tr>
						      <c:forEach items="${row.columns}" var="column">
						         
						          <c:if test="${column.propertyValue!=\"\"}">
						            <td class="infocell" title="${column.columnTitle}">
						              <c:out value="${column.propertyValue}" />
						            </td>
						          </c:if>
					          </c:forEach>
					     </tr>
				     </c:if>
				     
				     <c:if test="${status.count == 4}" >
					      <tfoot>
						      <tr>				      
								   <td colspan="5" class="infocell" style="text-align: right">
								   		Available Balance: <c:out value="${row.columns[8].propertyValue}"/>
								   <td>
						      </tr>
						  </tfoot>
				      </c:if>			     
				     
			     </c:forEach>
			 </table>
			 </div>
		 </c:if>
	  </td>
    </tr>
</table>
<br/><br/>
</kul:pageLookup>
