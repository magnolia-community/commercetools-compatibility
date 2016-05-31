[#-------------- RENDERING --------------]
<!-- Navigation area-->
<nav>
    [#list components as component ]
        [@cms.component content=component /]
    [/#list]
</nav>
<!-- Navigation area end-->
