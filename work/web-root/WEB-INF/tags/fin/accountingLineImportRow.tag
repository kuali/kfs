<%@ taglib prefix="c" uri="/tlds/c.tld" %>
<%@ taglib prefix="html" uri="/tlds/struts-html.tld" %>
<%@ attribute name="rightColumnCount" required="true"
    description="The number of columns spanned by the right side
    of this row.  This row is divided into two sides, left and right.
    The left side spans 4 columns and contains the title of this group
    of accounting lines, e.g., 'From', 'Expense', 'Disencumbrance', etc.
    The right side spans 5 or more columns, depending on the number
    of optional fields and whether there is an action button column." %>
<%@ attribute name="isSource" required="true"
    description="Boolean whether this group is of source or target lines." %>
<%@ attribute name="editingMode" required="true" %>

<c:set var="sourceOrTarget" value="${isSource ? 'source' : 'target'}"/>
<c:set var="capitalSourceOrTarget" value="${isSource ? 'Source' : 'Target'}"/>
<c:set var="showLink" value="${sourceOrTarget}ShowLink"/>
<c:set var="uploadDiv" value="upload${capitalSourceOrTarget}Div"/>
<c:set var="hideImport" value="hide${capitalSourceOrTarget}Import"/>
<c:set var="showImport" value="show${capitalSourceOrTarget}Import"/>
<c:set var="file" value="${sourceOrTarget}File"/>
<c:set var="uploadLines" value="upload${capitalSourceOrTarget}Lines"/>
<%-- need var titleName because EL + operator is arithmetic only, not String concat --%>
<c:set var="titleName" value="${sourceOrTarget}AccountingLinesSectionTitle"/>

<c:set var="titleColSpan" value="4" />
<c:if test="${editingMode != 'fullEntry'}" >
    <c:set var="titleColSpan" value="${titleColSpan + rightColumnCount}" />
</c:if>

<tr>
    <td colspan="${titleColSpan}" class="tab-subhead">${KualiForm.document[titleName]}</td>

    <c:if test="${editingMode == 'fullEntry'}">
        <td colspan="${rightColumnCount}" class="tab-subhead-import" align="right" nowrap="nowrap">
            <SCRIPT type="text/javascript">
                <!--
                  function ${hideImport}() {
                      document.getElementById("${showLink}").style.display="inline";
                      document.getElementById("${uploadDiv}").style.display="none";
                  }
                  function ${showImport}() {
                      document.getElementById("${showLink}").style.display="none";
                      document.getElementById("${uploadDiv}").style.display="inline";
                  }
                  document.write(
                    '<a id="${showLink}" href="#" onclick="${showImport}();return false;">' +
                      '<img src="images/importlines.gif" alt="import file"' +
                      '     width=71 height=11 border=0 align="middle" class="det-button">' +
                    '<\/a>' +
                    '<div id="${uploadDiv}" style="display:none;" >' +
                      '<html:file size="30" property="${file}" />' +
                      '<html:image property="methodToCall.${uploadLines}" src="images/imp-add.gif"
                                    styleClass="tinybutton" alt="insert ${sourceOrTarget} accounting lines" />' +
                      '<html:image property="methodToCall.cancel" src="images/imp-cancel.gif"
                                    styleClass="tinybutton" alt="cancel insert" onclick="${hideImport}();return false;" />' +
                    '<\/div>');
                //-->
            </SCRIPT>
            <NOSCRIPT>
                <html:file size="30" property="${file}" style="font:10px;height:16px;"/>
                <html:image property="methodToCall.${uploadLines}" src="images/imp-add.gif"
                            alt="insert ${sourceOrTarget} accounting lines"/>
            </NOSCRIPT>
        </td>
    </c:if>
</tr>