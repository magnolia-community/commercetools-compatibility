[#-------------- RENDERING --------------]
<ul class="primary_nav nav-left">
    [#list components as component ]
        [@cms.component content=component /]
    [/#list]
</ul>