[#-------------- RENDERING --------------]
<ul class="primary_nav nav-right">
    [#list components as component ]
        [@cms.component content=component /]
    [/#list]
</ul>