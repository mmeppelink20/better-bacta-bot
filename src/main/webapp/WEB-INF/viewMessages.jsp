<%@ page import="com.meppelink.User.User" %>
<%@ page import="com.meppelink.Discord.DiscordMessage" %>
<%@ page import="java.util.ArrayList" %><%
  User user = (User) session.getAttribute("user");
  ArrayList<DiscordMessage> messages = (ArrayList<DiscordMessage>) request.getAttribute("messages");
%>
<!DOCTYPE html>
<html>
<head>
  <title>Bacta Bot | Messages </title>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <img src="discord-icon-svgrepo-com.svg" width="50px" width="50px">
    <a class="navbar-brand" href="#">&nbsp Bacta Bot</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExample05" aria-controls="navbarsExample05" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarsExample05">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item active">
            <% if (user != null) { %>
        <li class="nav-item">
          <form method="post" action="main" class="form-inline my-2 my-md-0">
            <button type="submit" name="sendMessageButton" value="true" class="btn nav-link">Send a Message</button>
          </form>
        </li>
        <li class="nav-item">
          <form method="post" action="main" class="form-inline my-2 my-md-0">
            <button type="submit" name="viewMessageButton" value="true" class="btn nav-link">View Messages</button>
          </form>
        </li>
        <li class="nav-item">
          <form method="post" action="main" class="form-inline my-2 my-md-0">
            <button type="submit" name="viewUsersButton" value="true" class="btn nav-link">View Users</button>
          </form>
        </li>
        <% } %>
        </li>

      </ul>
      <% if (user != null) { %>

      <form method="post" action="main" class="form-inline my-2 my-md-0">
        <p class="text-white bg-dark mt-3"><%= user.getFirst_name() %></p>
        &nbsp;
        <button type="submit" name="logoutButton" value="true" class="btn btn btn-outline-primary">Logout</button>
      </form>
      <% } else { %>
      <form method="post" action="main" class="form-inline my-2 my-md-0">
        <button type="submit" name="registerButton" value="true" class="btn btn btn-outline-primary">Register</button>
      </form>
      &nbsp;
      <form method="post" action="main" class="form-inline my-2 my-md-0">
        <button type="submit" name="loginButton" value="true" class="btn btn-primary">Login</button>
      </form>
      <% } %>

    </div>
  </nav>
</head>

<body>

<div class="mt-50 text-center">
  <br>
  <h3>View User Messages</h3>
</div>

<div class="container">
  <table class="table">
    <thead>
    <tr>
      <th scope="col">UserName</th>
      <th scope="col">Message</th>
      <th scope="col">Time</th>
    </tr>
    </thead>
    <p> <% messages.size(); %> </p>
    <tbody class="table-group-divider">
    <% for(DiscordMessage message: messages) { %>
    <tr>
      <td>
        <%= message.getUserName() %>
      </td>
      <td>
        <%= message.getMessage() %>
      </td>
      <td>
        <%= message.getMessageTime() %>
      </td>
    </tr>
    <% } %>
    </tbody>
  </table>
</div>


<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<% if (user != null) { %>
<script src='https://cdn.jsdelivr.net/npm/@widgetbot/crate@3' async defer>
  new Crate({
    server: '1095791760619348120', // test server
    channel: '1095791761433051148' // #general
  })
</script>
<% }%>
</body>



</html>