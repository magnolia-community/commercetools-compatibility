[#-------------- ASSIGNMENTS --------------]
[#assign redirectTo = cmsfn.link("website", content.mgnlReturnTo!"")!cmsfn.link(cmsfn.page(content))!"#"]
[#assign authenticationError = (ctx.getAttribute("ctSignupError"))!""]
[#assign customer = (ctx.getAttribute(ctfn.getProjectName() + "_ctCustomerId"))!""]

[#-------------- RENDERING --------------]
<form class="account-form" id="account-signup-form">
    <input type="hidden" name="ctAction" value="ctDoSignup"/>
    <input type="hidden" name="mgnlReturnTo" value="${redirectTo}"/>

    <div class="clearfix border-bottom-padding-marging">
        <div class="h2 pull-left">${i18n['ctSignup.signup']}</div><div class="h6 pull-right text-muted">${i18n['ct.required']}*</div>
    </div>
    <div class="row">
        <div class="form-group col-lg-6 col-md-6 col-sm-6">
            <label for="signupFirstName">${i18n['ctSignup.signupFirstName']}*</label>
            <input name="ctCustomerFirstName" type="text" id="signupFirstName" placeholder="First name" required>
        </div>
        <div class="form-group col-lg-6 col-md-6 col-sm-6">
            <label for="signupLastName">${i18n['ctSignup.signupLastName']}*</label>
            <input name="ctCustomerLastName" type="text" id="signupLastName" placeholder="Last name" required>
        </div>
    </div>
    <div class="row">
        <div class="form-group col-lg-12 col-md-12 col-sm-12">
            <label for="signupEmail">${i18n['ctSignup.signupEmail']}*</label>
            <input name="ctCustomerEmail" type="email" id="signupEmail" placeholder="Email" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$" required>
        </div>
    </div>
    <div class="row">
        <div class="form-group col-lg-6 col-md-6 col-sm-6">
            <label for="signupPassword">${i18n['ctSignup.signupPassword']}*</label>
            <input name="ctCustomerPassword" type="password" id="signupPassword" placeholder="Password" required>
        </div>
        <div class="form-group col-lg-6 col-md-6 col-sm-6">
            <label for="signupPasswordConfirm">${i18n['ctSignup.signupPasswordConfirm']}*</label>
            <input type="password" id="signupPasswordConfirm" placeholder="Password" required>
        </div>
    </div>
    <button type="submit" class="btn cta-button pull-right">${i18n['ctSignup.submit']}</button>
</form>

[#if authenticationError?has_content]
    <ul>
        [#list authenticationError as error]
            <li>${error.getMessage()}</li>
        [/#list]
    </ul>
[/#if]