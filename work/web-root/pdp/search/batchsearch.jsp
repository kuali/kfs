<%--
 Copyright 2007 The Kuali Foundation.
 
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
<%@ taglib uri="/WEB-INF/app.tld" prefix="app" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html locale="true">
<link rel="stylesheet" type="text/css"  href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
  <head>
    <html:base />
    <title>Batch Search</title>
    <c:if test="${param.results != null}">
      <script>
        location.hash = "results";
      </script>
    </c:if>
  </head>
  <body>
    <html:form action="/pdp/batchsearch">
      <c:if test="${not empty BatchSearchFormSession}">
        <bean:define id="PdpBatchSearchForm" name="BatchSearchFormSession" />
      </c:if>
      <h1><strong>Search for a Batch</strong></h1>
      <jsp:include page="${request.contextPath}/pdp/TestEnvironmentWarning.jsp" flush="true"/>
      <br>
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="20">&nbsp;</td>
          <td>
            <br>
            <font color="#800000">
              <html:errors/>&nbsp;
            </font>&nbsp;
            <br>
          </td>
        </tr>
      </table>
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="20">&nbsp;</td>
          <td>
            <table width="100%" border="0" cellspacing="0" cellpadding="4">
              <tr>
                <td>
                  <strong>Enter your search criteria:</strong>
                </td>
                <td></td>
              </tr>
            </table>
          </td>
          <td width="20">&nbsp;</td>
        </tr>
        <tr>
          <td width="20">&nbsp;</td>
          <td>&nbsp;</td>
          <td width="20">&nbsp;</td>
        </tr>
        <tr>
          <td>&nbsp;</td>
          <td>
            <table width="100%" border=0 cellpadding=0 cellspacing=0 class="bord-r-t">
              <tbody>
                <tr>
                  <!-- These are the fields for searching on a Batch. -->
                  <th align=right nowrap >Chart:</th>
                  <td width="50%" align=left class="datacell">
                    <html:text name="PdpBatchSearchForm" property="chartCode" tabindex="2" maxlength="2" />
                  </td>
                  <th align=right nowrap >Batch ID:</th>
                  <td colspan=1 align=left class="datacell">
                    <html:text name="PdpBatchSearchForm" property="batchId" tabindex="7" maxlength="8" />
                  </td>
                </tr>
                <tr>
                  <th align=right nowrap >Organization:</th>
                  <td align=left class="datacell">
                    <html:text name="PdpBatchSearchForm" property="orgCode" tabindex="3" maxlength="4" />
                  </td>
                  <th align=right nowrap >Total Payment Count:</th>
                  <td colspan=1 align=left class="datacell">
                    <html:text name="PdpBatchSearchForm" property="paymentCount" tabindex="8" maxlength="5" />
                  </td>
                </tr>
                <tr>
                  <th align=right nowrap >Sub-Unit:</th>
                  <td align=left class="datacell">
                    <html:text name="PdpBatchSearchForm" property="subUnitCode" tabindex="4" maxlength="4" />
                  </td>
                  <th align=right nowrap >Total Payment Amount:</th>
                  <td align=left class="datacell">
                    <html:text name="PdpBatchSearchForm" property="paymentTotalAmount" tabindex="9" maxlength="14" />
                  </td>
                </tr>
                <tr>
                  <th colspan=4 align=left nowrap >&nbsp;&nbsp;&nbsp; File Creation Date Range:</th>
                  <td align=left></td>
                </tr>
                <tr>
                  <th align=right nowrap >Begin Date:</th>
                  <td colspan=3 align=left class="datacell">
                    <html:text name="PdpBatchSearchForm" property="beginDate" tabindex="5" maxlength="10" />&nbsp;&nbsp;<font size="1"> Ex. 1/6/2004  </font>
                  </td>
                </tr>
                <tr>
                  <th align=right nowrap >End Date:</th>
                  <td colspan=3 align=left class="datacell">
                    <html:text name="PdpBatchSearchForm" property="endDate" tabindex="6" maxlength="10" />&nbsp;&nbsp;<font size="1"> Ex. 12/16/2004  </font>
                  </td>
                </tr>
                <tr valign=middle align=left>
                  <th colspan="4" nowrap=nowrap>
                    <div align="center">
                      <input type="image" name="btnSearch" src="<%= request.getContextPath().toString() %>/pdp/images/button_search.gif" alt="Search" align="absmiddle"></a>
                      <input type="image" name="btnClear" src="<%= request.getContextPath().toString() %>/pdp/images/button_clearfields.gif" alt="Clear" align="absmiddle"></a>
                    </div>
                  </th>
                </tr>
              </tbody>
            </table>
          </td>
          <td>&nbsp;</td>
        </tr>
      </table>
    </html:form>
    <br>    
    <a name="results"></a>
    <logic:notEmpty name="batchSearchResults" >
      <table width="100%" border="0" cellspacing="0" cellpadding="0">
        <tr>
          <td width="20">&nbsp;</td>
          <td>
            <display-el:table sort="list" name="sessionScope.batchSearchResults" id="item" pagesize="${perPage}" cellpadding="4" cellspacing="0" class="bord-r-t">
              <display-el:column sortable="true" sortProperty="id" title="Batch ID" headerClass="thfont" class="datacell">
                <a href="<%= request.getContextPath().toString() %>/batchsearch.do?btnBatchDetail=param&BatchId=<c:out value="${item.id}" />"><c:out value="${item.id}" /></a>&nbsp;
              </display-el:column>
              <display-el:column sortable="true" title="Chart" headerClass="thfont" class="datacell">
                <c:out value="${item.customerProfile.chartCode}" />&nbsp;
              </display-el:column>
              <display-el:column sortable="true" title="Organization" headerClass="thfont" class="datacell">
                <c:out value="${item.customerProfile.orgCode}" />&nbsp;
              </display-el:column>
              <display-el:column sortable="true" title="Sub-Unit" headerClass="thfont" class="datacell">
                <c:out value="${item.customerProfile.subUnitCode}" />&nbsp;
              </display-el:column>
              <display-el:column sortable="true" title="Customer File Creation Date" headerClass="thfont" class="datacell">
                <fmt:formatDate value="${item.customerFileCreateTimestamp}" pattern="MM/dd/yyyy'  at  'hh:mm a"/>&nbsp;
              </display-el:column>
              <display-el:column sortable="true" sortProperty="paymentCount" title="Total Number of Payments" headerClass="thfont" class="datacell">
                <c:out value="${item.paymentCount}" />&nbsp;
              </display-el:column>
              <display-el:column sortable="true" sortProperty="paymentTotalAmount" title="Total Payment Amount" headerClass="thfont" class="datacell">
                <fmt:formatNumber value="${item.paymentTotalAmount}" type="currency" />&nbsp;
              </display-el:column>
             <display-el:setProperty name="paging.banner.onepage" value=""/>
             <display-el:setProperty name="basic.msg.empty_list" value=""/>
             <display-el:setProperty name="paging.banner.placement">both</display-el:setProperty>
             <display-el:setProperty name="paging.banner.group_size">15</display-el:setProperty>
             <display-el:setProperty name="paging.banner.page.separator">&nbsp;::&nbsp;</display-el:setProperty>
             <display-el:setProperty name="paging.banner.one_item_found">
                 </td>
                 <td width="20">&nbsp;</td>
               </tr>
             </table>
             <br><br>
             <h1><div align="left">Search Results: 1 found.</div></h1>
             <br><br>
             <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
               <tr>
                 <td width="20">&nbsp;</td>
                 <td>
             </display-el:setProperty>
             <display-el:setProperty name="paging.banner.all_items_found">
                 </td>
                 <td width="20">&nbsp;</td>
               </tr>
             </table>
             <br><br>
             <h1><div align="left">Search Results: {0} found.</div></h1>
             <br><br>
             <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center" >
               <tr>
                 <td width="20">&nbsp;</td>
                 <td>
             </display-el:setProperty>
             <display-el:setProperty name="paging.banner.some_items_found">
                 </td>
                 <td width="20">&nbsp;</td>
               </tr>
             </table>
             <br><br>
             <h1><div align="left">Search Results: {0} found.  Displaying {2} to {3}.</div>
             </display-el:setProperty>
             <display-el:setProperty name="paging.banner.full">
                 <div align="right">[<a href="{1}">First</a>/ <a href="{2}">Prev</a>] {0} [ <a href="{3}">Next</a>/ <a href="{4}">Last </a>]</div>
               </h1>
               <br><br>
               <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                 <tr>
                   <td width="20">&nbsp;</td>
                   <td>
             </display-el:setProperty>
             <display-el:setProperty name="paging.banner.first">
                 <div align="right">[First/Prev] {0} [ <a href="{3}">Next</a>/ <a href="{4}">Last</a>]</div>
               </h1>
               <br><br>
               <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                 <tr>
                   <td width="20">&nbsp;</td>
                   <td>
             </display-el:setProperty>
             <display-el:setProperty name="paging.banner.last">
                 <div align="right">[ <a href="{1}">First</a>/ <a href="{2}">Prev</a>] {0} [Next/Last]</div>
               </h1>
               <br><br>
               <table width="100%" border="0" cellspacing="0" cellpadding="0" align="center">
                 <tr>
                   <td width="20">&nbsp;</td>
                   <td>
             </display-el:setProperty>
           </display-el:table>
         </td>
         <td width="20">&nbsp;</td>
       </tr>
     </table>
     <br>
   </logic:notEmpty>
   <p>&nbsp;</p>
   <c:import url="/backdoor.jsp"/>
</body>
</html:html>
