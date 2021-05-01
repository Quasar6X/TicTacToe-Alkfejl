<%@ page import="hu.alkfejl.constants.Constants" %>
<!DOCTYPE html>
<%--
  User: dani0
  Date: 4/23/2021
  Time: 6:08 PM
--%>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" %>
<html lang="en">
<head>
    <title>TicTacToe</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta3/dist/css/bootstrap.min.css" rel="stylesheet"
          integrity="sha384-eOJMYsd53ii+scO/bJGFsiCZc+5NDVN2yr8+0RDqr0Ql0h+rP48ckxlpbzKgwra6" crossorigin="anonymous">
    <link href="../img/logo.png" rel="icon">
</head>
    <body>
        <jsp:include page="/WelcomeController"/>
        <%
            if (session.getAttribute(Constants.SESSION_PLAYER_ID) == null)
                response.sendRedirect("../index.jsp");
        %>
        <div class="container d-grid gap-2 col-3 mx-auto mt-3">
            <div class="card">
                <div class="card-body p-4 d-flex flex-column justify-content-center align-items-center">
                    <h1 class="h1 text-center card-title">Welcome ${sessionScope.get(Constants.SESSION_PLAYER_NAME)}!</h1>
                    <form method="get" action="setup_game.jsp">
                        <input type="hidden" name="${Constants.FORM_INPUT_NAME_IS_AI}" value="true">
                        <input type="submit" value="Play Vs AI" class="btn btn-outline-primary mt-2">
                    </form>
                    <form method="get" action="setup_game.jsp">
                        <input type="hidden" name="${Constants.FORM_INPUT_NAME_IS_AI}" value="false">
                        <input type="submit" value="Play Vs Human" class="btn btn-outline-primary mt-2">
                    </form>
                    <a class="btn btn-outline-primary mt-2" href="match_history.jsp">Match History</a>
                    <a class="btn btn-outline-danger mt-2" href="../LogoutController">Logout</a>
                </div>
            </div>
        </div>
    </body>
</html>
