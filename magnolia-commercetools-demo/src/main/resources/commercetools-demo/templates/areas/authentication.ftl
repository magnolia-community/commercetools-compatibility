<div class="divider"></div>

<div class="container">
    <div class="row">
        <div class="account-forms col-lg-8 col-lg-offset-2">
            [#list components as component ]
                [@cms.component content=component /]
            [/#list]
        </div>
    </div>
</div>