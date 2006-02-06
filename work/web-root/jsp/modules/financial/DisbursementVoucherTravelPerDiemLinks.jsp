<%@ include file="/jsp/core/tldHeader.jsp" %>
<%@ taglib tagdir="/WEB-INF/tags/dv" prefix="dv" %>

<kul:page showDocumentInfo="false" headerTitle="Travel Per Diem Links" docTitle="" transactionalDocument="false" htmlFormAction="help" >

<center>

<table border=0 cellspacing=0 cellpadding=0 width="75%">
  <tr>
    <td>${KualiForm.travelPerDiemLinkPageMessage}</td>
  <tr>
</table>  

<br><br>

<table border=0 cellspacing=0 cellpadding=0>
  <tr>
      <td colspan="2" class="tab-subhead">Per Diem Category Links</td>
  </tr>
  <tr>
     <th class="bord-l-b"> <div align=left>Category Name</div></th>
     <th class="bord-l-b"> <div align=left>Category Link</div></th>
  </tr>
  <logic:iterate indexId="ctr" name="KualiForm" property="travelPerDiemCategoryCodes" id="currentCategory">
     <tr>
        <th scope="row" class="bord-l-b">${currentCategory.perDiemCountryName}</th>
        <td valign=top class="infoline"><a href="${currentCategory.perDiemCountryText}">${currentCategory.perDiemCountryText}</a></td>
     </tr>
  </logic:iterate>
</table>
</center>

</kul:page>
