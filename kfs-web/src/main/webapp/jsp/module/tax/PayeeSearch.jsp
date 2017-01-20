<%@ include file="/jsp/sys/kfsTldHeader.jsp"%>

<kul:page showDocumentInfo="false"
	headerTitle="Payee 1099 Forms" docTitle="Payee 1099 Forms" renderMultipart="false"
	transactionalDocument="false" htmlFormAction="taxPayeeSearch" errorKey="foo" showTabButtons="true">
	
	<html-el:hidden property="methodToCall" />

	<table width="100%" border="0">
	<tr><td>	
		<kul:errors keyMatch="*" errorTitle="Errors Found On Page:"/>
	</td></tr>
	</table>  
	
	</br>

	<kul:tabTop tabTitle="Run Search" defaultOpen="true" tabErrorKey="">

	    <div class="tab-container" align="center">
			<h3>Payee Search Parameters</h3>

	      	<table width="100%" cellpadding="0" cellspacing="0" class="datatable">
			<tr>
				<th class="grid" width="50%" align="right">Tax Year:</th>
		      	<td class="grid" width="50%">
					<html:text property="taxYear" size="4" />
				</td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right">Tax Number:</th>
		      	<td class="grid" width="50%">
					<html:text property="headerTaxNumber" size="9" />
				</td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right">Vendor Number:</th>
		      	<td class="grid" width="50%">
					<html:text property="vendorNumber" size="20" />
				</td>
			</tr>
			<tr>
				<th class="grid" width="50%" align="right">Vendor Name:</th>
		      	<td class="grid" width="50%">
					<html:text property="vendorName" size="40" />
				</td>
			</tr>
			</table>
		</div>

	</kul:tabTop>

	<kul:panelFooter />

    <div id="globalbuttons" class="globalbuttons">
		<html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_submit.gif" styleClass="globalbuttons" property="methodToCall.searchPayees" title="submit" alt="submit" onclick="excludeSubmitRestriction=true"/>
        <html:image src="${ConfigProperties.kr.externalizable.images.url}buttonsmall_close.gif"  styleClass="globalbuttons" property="methodToCall.returnToIndex" title="close"  alt="close"/>
    </div>

	<c:if test="${!empty reqSearchResults }">
		<c:set var="offset" value="0" />

		<display:table class="datatable-100" cellspacing="0" cellpadding="0" name="${reqSearchResults}" id="row" export="false" pagesize="100" offset="${offset}" requestURI="taxPayeeSearch.do">

		<display:column class="infocell" title="Actions" sortable="false">					
			<a href="downloadTaxForm.do?methodToCall=downloadTaxForm&id=${row.id}&year=${row.taxYear}">download</a>
		</display:column>

		<display:column class="infocell" title="Vendor Name" sortable="true">					
			<c:out value="${row.vendorName}" />
		</display:column>

		<display:column class="infocell" title="Vendor Number" sortable="true">					
			<c:out value="${row.vendorNumber}" />
		</display:column>

		<display:column class="infocell" title="Vendor Type" sortable="true">					
			<c:out value="${row.headerTypeCode}" />
		</display:column>

		<display:column class="infocell" title="Ownership Code" sortable="true">					
			<c:out value="${row.headerOwnershipCode}" />
		</display:column>

		<display:column class="infocell" title="Ownership Category Code" sortable="true">					
			<c:out value="${row.headerOwnershipCategoryCode}" />
		</display:column>

		<display:column class="infocell" title="Tax Year" sortable="true">					
			<c:out value="${row.taxYear}" />
		</display:column>

		<display:column class="numbercell" title="Amount" sortable="true">					
			<c:out value="${row.taxAmount}" />
		</display:column>

		</display:table>
	</c:if>
</kul:page>
