<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%
  boolean loginFailed = (Boolean)request.getAttribute("loginFailed");
%>
<!DOCTYPE html>
<html>
<head>
  <title>Discord Bot Website</title>
  <nav class="navbar navbar-expand-lg navbar-dark bg-dark">
    <img src="discord-icon-svgrepo-com.svg" width="50px" width="50px">
    <a class="navbar-brand" href="#">&nbsp Discord Bot</a>
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarsExample05" aria-controls="navbarsExample05" aria-expanded="false" aria-label="Toggle navigation">
      <span class="navbar-toggler-icon"></span>
    </button>

    <div class="collapse navbar-collapse" id="navbarsExample05">
      <ul class="navbar-nav mr-auto">
        <li class="nav-item active">
          <a class="nav-link" href="#">Home <span class="sr-only">(current)</span></a>
        </li>
      </ul>
      <form method="post" action="main" class="form-inline my-2 my-md-0">
        <button type="submit" name="registerButton" value="true" class="btn btn btn-outline-primary">Register</button>
      </form>
      &nbsp;
      <form method="post" action="main" class="form-inline my-2 my-md-0">
        <button type="submit" name="loginButton" value="true" class="btn btn-primary">Login</button>
      </form>
    </div>
  </nav>
  <style>
    .form-signin {
      max-width: 330px;
      padding: 15px;
    }

    .form-signin .form-floating:focus-within {
      z-index: 2;
    }

    .form-signin input[type="email"] {
      margin-bottom: -1px;
      border-bottom-right-radius: 0;
      border-bottom-left-radius: 0;
    }

    .form-signin input[type="password"] {
      margin-bottom: 10px;
      border-top-left-radius: 0;
      border-top-right-radius: 0;
    }
  </style>
  </style>
</head>

<body class="text-center">
<% if(loginFailed) { %>
<div class="alert alert-danger" role="alert">
  Your email and password combination could not be found.
</div>
<% } %>
<main class="form-signin w-100 m-auto">
  <form method="POST" action="login">
    <h1 class="h3 mb-3 fw-normal">Please Login</h1>

    <div class="form-floating">
      <input name="email" type="email" class="form-control" id="floatingInput" placeholder="name@example.com">
      <label for="floatingInput">Email address</label>
    </div>
    <div class="form-floating">
      <input name="password" type="password" class="form-control" id="floatingPassword" placeholder="Password">
      <label for="floatingPassword">Password</label>
    </div>

    <div class="checkbox mb-3">
      <label>
        <input type="checkbox" value="remember-me"> Remember me
      </label>
    </div>



    <button class="w-100 btn btn-lg btn-primary" type="submit">Login</button>
  </form>
</main>


<link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/css/bootstrap.min.css" integrity="sha384-ggOyR0iXCbMQv3Xipma34MD+dH/1fQ784/j6cY/iJTQUOhcWr7x9JvoRxT2MZw1T" crossorigin="anonymous">
<script src="https://code.jquery.com/jquery-3.3.1.slim.min.js" integrity="sha384-q8i/X+965DzO0rT7abK41JStQIAqVgRVzpbzo5smXKp4YfRvH+8abtTE1Pi6jizo" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/popper.js@1.14.7/dist/umd/popper.min.js" integrity="sha384-UO2eT0CpHqdSJQ6hJty5KVphtPhzWj9WO1clHTMGa3JDZwrnQq4sF86dIHNDz0W1" crossorigin="anonymous"></script>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@4.3.1/dist/js/bootstrap.min.js" integrity="sha384-JjSmVgyd0p3pXB1rRibZUAYoIIy6OrQ6VrjIEaFf/nJGzIxFDsf4x0xIM+B07jRM" crossorigin="anonymous"></script>
</body>

</html>