<%@ page language="java"%>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-html-el" prefix="html" %>
<%@ taglib uri="http://jakarta.apache.org/struts/tags-logic" prefix="logic" %>
<%@ taglib uri="http://java.sun.com/jstl/core_rt" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html>
<head>
<link rel="stylesheet" type="text/css" href="https://docs.onestart.iu.edu/dav/MY/channels/css/styles.css">
<title>Format Disbursement Finished</title>
</head>
<body>
  <h1><strong>Format Disbursement Finished</strong></h1><br>
  <jsp:include page="${request.contextPath}/TestEnvironmentWarning.jsp" flush="true"/>
  <table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td>&nbsp;</td>
              <td>
                <div align="right">
                <!-- Navigation Here? -->
                </div>
              </td>
            </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
  </tbody>
</table>
<br>
<table border="0" cellpadding="0" cellspacing="0" width="100%">
  <tbody>
    <tr>
      <td width="20">&nbsp;</td>
      <td>
        <table border="0" cellpadding="4" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td><html:errors/></td>
              <td>&nbsp;</td>
           </tr>
          </tbody>
        </table>
      </td>
      <td width="20">&nbsp;</td>
    </tr>
  </tbody>
</table>
<p>&nbsp;</p>
<c:import url="/backdoor.jsp"/>
</body>
</html:html>

