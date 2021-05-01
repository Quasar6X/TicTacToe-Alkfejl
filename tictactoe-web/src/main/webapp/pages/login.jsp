<!DOCTYPE html>
<%--
  Created by IntelliJ IDEA.
  User: dani0
  Date: 4/23/2021
  Time: 6:08 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="hu.alkfejl.constants.Constants" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html lang="en">
<head>
    <title>TicTacToe</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link href="../css/custom.css" rel="stylesheet" type="text/css">
    <link href="../img/logo.png" rel="icon">
</head>
    <body>
        <jsp:include page="/LoginController"/>
        <div class="container mt-4">
            <div class="card">
                <div class="card-body">
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th scope="col">#</th>
                                <th scope="col">Name</th>
                                <th scope="col"></th>
                                <th scope="col"></th>
                                <th scope="col"></th>
                            </tr>
                        </thead>
                        <c:set var="i" scope="page" value="${1}"/>
                        <tbody>
                            <c:forEach items="${requestScope.players}" var="player">
                                <c:if test="${player.id != 1}">
                                    <tr>
                                        <th scope="row">${i}</th>
                                        <td>${player.name}</td>
                                        <td>
                                            <form action="../WelcomeController" method="post">
                                                <input type="hidden" name="${Constants.FORM_INPUT_NAME_PLAYER_ID}" value="${player.id}"/>
                                                <input type="hidden" name="${Constants.FORM_INPUT_NAME_PLAYER_NAME}" value="${player.name}"/>
                                                <input type="submit" class="btn btn-outline-primary" value="Login"/>
                                            </form>
                                        </td>
                                        <td>
                                            <form action="add_edit_player.jsp" method="post">
                                                <input type="hidden" name="${Constants.FORM_INPUT_NAME_PLAYER_ID}" value="${player.id}"/>
                                                <input type="submit" class="btn btn-outline-warning" value="Edit">
                                            </form>
                                        </td>
                                        <td>
                                            <form action="../DeletePlayerController" method="post" onsubmit="return confirm('Are you sure you want to delete player \'${player.name}\'')">
                                                <input type="hidden" name="${Constants.FORM_INPUT_NAME_PLAYER_ID}" value="${player.id}"/>
                                                <input type="submit" class="btn btn-outline-danger" value="Delete">
                                            </form>
                                        </td>
                                    </tr>
                                    <c:set var="i" scope="page" value="${i + 1}"/>
                                </c:if>
                            </c:forEach>
                        </tbody>
                        <c:remove var="i"/>
                    </table>
                </div>
            </div>
        </div>
        <footer class="footer mt-auto py-3 card-footer">
            <div class="d-grid gap-2 d-md-flex justify-content-md-end mx-3">
                <a class="btn-lg btn-danger" href="add_edit_player.jsp" style="text-decoration: none">Add new player</a>
            </div>
        </footer>
    </body>
</html>
