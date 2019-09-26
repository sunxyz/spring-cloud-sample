<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, shrink-to-fit=no" name="viewport">
    <title>授权 &mdash; Spring Cloud Sample</title>

    <!-- General CSS Files -->
    <link rel="stylesheet" href="/assets/modules/bootstrap/css/bootstrap.min.css">
    <!-- Template CSS -->
    <link rel="stylesheet" href="/assets/css/style.css">
</head>

<body>
<div id="app">
    <section class="section">
        <div class="container mt-5">
            <div class="row">
                <div class="col-12 col-sm-8 offset-sm-2 col-md-6 offset-md-3 col-lg-6 offset-lg-3 col-xl-4 offset-xl-4">
                    <div class="login-brand">
                        <h1>SCS UAA</h1>
                    </div>

                    <div class="card card-primary">
                        <div class="card-header"><h4>授权</h4></div>

                        <div class="card-body">
                            <p class="text-muted">${clientId} 请求授权，该应用将获取你的以下信息</p>
                            <ul class="list-group">
                            <#list scopes as scope>
                                <li class="list-group-item">${scope}</li>
                            </#list>
                            </ul>
                            <p  class="text-muted">授权后表明你已同意 <a  href="#boot"  class="text-small">OAUTH-BOOT 服务协议</a></p>
                            <form method="POST" action="/oauth/authorize">
                                <input type="hidden" name="user_oauth_approval" value="true">
                                <input type="hidden" name="${_csrf.parameterName}" value="${_csrf.token}"/>
                                <div class="form-group">
                                    <button type="submit" class="btn btn-primary btn-lg btn-block" tabindex="4">
                                        同意/授权
                                    </button>
                                </div>
                            </form>
                        </div>
                    </div>
                    <div class="simple-footer">
                        Copyright &copy; Spring Cloud Sample 2019
                    </div>
                </div>
            </div>
        </div>
    </section>
</div>
<!-- General JS Scripts -->
<script src="/assets/modules/jquery.min.js"></script>
<script src="/assets/modules/bootstrap/js/bootstrap.min.js"></script>


</body>
</html>
