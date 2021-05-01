
<!DOCTYPE html>
<%--
  Created by IntelliJ IDEA.
  User: dani0
  Date: 4/23/2021
  Time: 5:48 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page import="hu.alkfejl.constants.Constants" %>

<html lang="en">
<head>
    <title>TicTacToe</title>
    <link href="img/logo.png" rel="icon">
</head>
    <body>
    <jsp:include page="/IndexController"/>
        <c:choose>
            <c:when test="${sessionScope.get(Constants.SESSION_PLAYER_ID) != null}">
                <c:redirect url="pages/welcome.jsp"/>
            </c:when>
            <c:otherwise>
                <c:redirect url="pages/login.jsp"/>
            </c:otherwise>
        </c:choose>
    </body>
</html>
