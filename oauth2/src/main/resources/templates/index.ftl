
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta content="width=device-width, initial-scale=1, maximum-scale=1, shrink-to-fit=no" name="viewport">
    <title>主页 &mdash; spring cloud sample</title>

    <!-- General CSS Files -->
    <link rel="stylesheet" href="/assets/modules/bootstrap/css/bootstrap.min.css">

    <!-- Template CSS -->
    <link rel="stylesheet" href="/assets/css/style.css">


<body>
<div id="app">
    <section class="section">
        <div class="container mt-5">
            <div class="page-error">
                <div class="page-inner">
                    <h2>SCS UAA</h2>
<#--                    <h1>主页</h1>-->
                    <div class="page-description">
                        欢迎：${username} <a href="/logout">退出</a>
                    </div>
                    <div class="page-search">
                        <form>
                            <div class="form-group floating-addon floating-addon-not-append">
                                <div class="input-group">
                                    <div class="input-group-prepend">
                                        <div class="input-group-text">
                                            <i class="fas fa-search"></i>
                                        </div>
                                    </div>
                                    <input type="text" class="form-control" placeholder="搜索">
                                    <div class="input-group-append">
                                        <button class="btn btn-primary btn-lg">
                                            搜索
                                        </button>
                                    </div>
                                </div>
                            </div>
                        </form>
                        <div class="mt-3">
                            <a href="/">回首页</a>
                        </div>
                    </div>
                </div>
            </div>
            <div class="simple-footer mt-5">
                Copyright &copy; Spring Cloud Sample 2019
            </div>
        </div>
    </section>
</div>
</body>
</html>
