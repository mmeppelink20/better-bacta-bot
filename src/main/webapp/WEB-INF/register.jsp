<%@ page import="java.util.Map" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="com.meppelink.User.User" %>

<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%

  User user = (User) session.getAttribute("user");

  Map<String, String> results = (Map<String, String>)request.getAttribute("results");
  if(results == null) {
    results = new HashMap<>();
  }
  String firstName = results.containsKey("firstName") ? results.get("firstName") : "";
  String lastName = results.containsKey("lastName") ? results.get("lastName") : "";
  String email = results.containsKey("email") ? results.get("email") : "";
  String phone = results.containsKey("phone") ? results.get("phone") : "";
  String password1 = results.containsKey("password1") ? results.get("password1") : "";
  String password2 = results.containsKey("password2") ? results.get("password2") : "";
  String agreeChecked = results.containsKey("agreeToTerms") ? "checked" : "";

  String firstNameError = results.containsKey("firstNameError") ? results.get("firstNameError") : "";
  String lastNameError = results.containsKey("lastNameError") ? results.get("lastNameError") : "";
  String emailError = results.containsKey("emailError") ? results.get("emailError") : "";
  String phoneError = results.containsKey("phoneError") ? results.get("phoneError") : "";
  String password1Error = results.containsKey("password1Error") ? results.get("password1Error") : "";
  String password2Error = results.containsKey("password2Error") ? results.get("password2Error") : "";
  String agreeError = results.containsKey("agreeError") ? results.get("agreeError") : "";

  String firstNameInvalid = results.containsKey("firstNameError") ? "is-invalid" : "";
  String lastNameInvalid = results.containsKey("lastNameError") ? "is-invalid" : "";
  String emailInvalid = results.containsKey("emailError") ? "is-invalid" : "";
  String phoneInvalid = results.containsKey("phoneError") ? "is-invalid" : "";
  String password1Invalid = results.containsKey("password1Error") ? "is-invalid" : "";
  String password2Invalid = results.containsKey("password2Error") ? "is-invalid" : "";
  String agreeInvalid = results.containsKey("agreeError") ? "is-invalid" : "";

  String userAddSuccess = results.containsKey("userAddSuccess") ? results.get("userAddSuccess") : "";
%>
<!doctype html>
<html lang="en">
<head>
  <meta charset="utf-8">
  <meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Bacta Bot | Regisiter</title>
  <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/css/bootstrap.min.css" rel="stylesheet"
        integrity="sha384-rbsA2VBKQhggwzxH7pPCaAqO46MgnOM80zW1RWuH61DGLwZJEdK2Kadq2F9CUG65" crossorigin="anonymous">
  <link href='https://fonts.googleapis.com/css?family=Roboto:500,900,100,300,700,400' rel='stylesheet' type='text/css'>
  <link rel="stylesheet" href="styles/nav.css">
</head>
<body class="bg-light">
<header>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <img src="discord-icon-svgrepo-com.svg" alt="discordIcon" height="50" width="50">
    <form method="post" action="main" class="form-inline my-2 my-md-0">
      <button type="submit" name="homeButton" value="true" class="btn nav-link" style="color: darkgrey;">Bacta Bot</button>
    </form>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExample05" aria-controls="navbarsExample05" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarsExample05">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item active">
            <% if (user != null) { %>
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
</header>

<div class="container">
  <main>
    <div class="pt-4 pb-2 text-center">
      <h2>Register New User</h2>
      <p class="lead">Enter personal information below to create a new user account.</p>
    </div>

    <div class="row">
      <div class="col-md-8 mx-auto">

        <% if(!userAddSuccess.equals("")) { %>
        <div class="alert alert-success mb-2" role="alert">
          <%= userAddSuccess %>
        </div>
        <% } %>

        <form method="post" action="signup">
          <div class="row">
            <div class="col-sm-6 mb-2">
              <label for="firstName" class="form-label">First name</label>
              <input type="text" class="form-control <%= firstNameInvalid %>" id="firstName" name="firstName" value="<%= firstName %>">
              <div class="invalid-feedback"><%= firstNameError %></div>
            </div>

            <div class="col-sm-6 mb-2">
              <label for="lastName" class="form-label">Last name</label>
              <input type="text" class="form-control <%= lastNameInvalid %>" id="lastName" name="lastName" value="<%= lastName %>">
              <div class="invalid-feedback"><%= lastNameError %></div>
            </div>

            <div class="col-sm-12 mb-2">
              <label for="email" class="form-label">Email</label>
              <input type="text" class="form-control <%= emailInvalid %>" id="email" name="email" placeholder="you@example.com" value="<%= email %>">
              <div class="invalid-feedback"><%= emailError %></div>
            </div>

            <div class="col-sm-6 mb-2">
              <label for="password1" class="form-label">Password</label>
              <input type="password" class="form-control <%= password1Invalid %>" id="password1" name="password1" value="<%= password1 %>">
              <div class="invalid-feedback"><%= password1Error %></div>
            </div>

            <div class="col-sm-6 mb-2">
              <label for="password2" class="form-label">Re-enter Password</label>
              <input type="password" class="form-control <%= password2Invalid %>" name="password2" id="password2" value="<%= password2 %>">
              <div class="invalid-feedback"><%= password2Error %></div>
            </div>
          </div>

          <div class="col-12 form-check my-4">
            <input type="checkbox" class="form-check-input <%= agreeInvalid %>" id="agree-to-terms" name="agree-to-terms" value="agree" <%= agreeChecked %>>
            <label class="form-check-label" for="agree-to-terms">I agree to the <a href="#">terms and conditions</a>.</label>
            <div class="invalid-feedback"><%= agreeError %></div>
          </div>

          <button class="w-100 btn btn-primary btn-lg" type="submit">Create new user</button>
        </form>
      </div>
    </div>
  </main>

  <footer class="my-5 pt-5 text-muted text-center text-small">
    <p class="mb-1">&copy; 2017-<script>document.write(new Date().getFullYear())</script> Company Name</p>
    <ul class="list-inline">
      <li class="list-inline-item"><a href="#">Privacy Policy</a></li>
      <li class="list-inline-item"><a href="#">Terms and Conditions</a></li>
      <li class="list-inline-item"><a href="#">Support</a></li>
    </ul>
  </footer>
</div>

<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.2.3/dist/js/bootstrap.bundle.min.js"
        integrity="sha384-kenU1KFdBIe4zVF0s0G1M5b4hcpxyD9F7jL+jjXkk+Q2h455rYXK/7HAuoJl+0I4"
        crossorigin="anonymous"></script>
</body>
</html>