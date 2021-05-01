<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<%--
  Created by dani0.
  Date: 4/24/2021
  Time: 5:05 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="hu.alkfejl.constants.Constants" %>
<html lang="en">
<head>
    <title>Match History</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link href="../img/logo.png" rel="icon">
</head>
    <body>
        <jsp:include page="/MatchHistoryController"/>
        <c:if test="${requestScope.is_empty}">
            <script>alert("Match history is empty!"); window.location.replace("welcome.jsp");</script>
        </c:if>
        <c:if test="${!requestScope.is_empty}">
            <table class="table table-striped">
                <thead>
                    <tr>
                        <th scope="col">#</th>
                        <th scope="col">Size</th>
                        <th scope="col">Date</th>
                        <th scope="col">P1</th>
                        <th scope="col">P2</th>
                        <th scope="col">Winner</th>
                        <th scope="col"></th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="i" begin="0" end="${requestScope.parties.size() - 1}">
                        <tr>
                            <th scope="row">${i+1}</th>
                            <td>${requestScope.parties.get(i).boardSize}</td>
                            <td>${requestScope.parties.get(i).timeOfParty.toLocaleString()}</td>
                            <td>${requestScope.p1s.get(i).name}</td>
                            <td>${requestScope.p2s.get(i).name}</td>
                            <c:choose>
                                <c:when test="${requestScope.winners.get(i).name != null}">
                                    <td>${requestScope.winners.get(i).name}</td>
                                </c:when>
                                <c:otherwise>
                                    <td>Draw</td>
                                </c:otherwise>
                            </c:choose>
                            <td>
                                <form method="post" action="../DeleteMatchController" onsubmit="return confirm('Are you sure you want to delete this party?')">
                                    <input type="hidden" name="${Constants.FORM_INPUT_NAME_PARTY_ID}" value="${requestScope.parties.get(i).id}">
                                    <input type="submit" value="Delete" class="btn btn-outline-danger">
                                </form>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </c:if>
    </body>
</html>
