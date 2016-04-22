[#-------------- ASSIGNMENTS --------------]
[#assign redirectTo = cmsfn.link("website", content.mgnlReturnTo!"")!cmsfn.link(cmsfn.page(content))!"#"]
[#assign authenticationError = (ctx.getAttribute("ctLoginError"))!""]
[#assign customer = (ctx.getAttribute("ctCustomerId"))!""]
[#assign forgotYourPasswordLink = cmsfn.link("website", content.forgotYourPasswordPage!"")!"#"]

[#-------------- RENDERING --------------]
[#if customer?has_content || cmsfn.editMode]
    ${i18n['ct.authenticated']}, <a href="${redirectTo}?ctAction=ctDoLogout">${i18n['ct.logout']} </a>.
[/#if]
[#if !customer?has_content || cmsfn.editMode]
    <div class="row">
        <div class="col-lg-6 col-lg-offset-3 col-md-6 col-md-offset-3 col-sm-6 col-sm-offset-3">
            <form class="account-form" id="account-login-form">
                <input type="hidden" name="ctAction" value="ctDoLogin"/>
                <input type="hidden" name="mgnlReturnTo" value="${redirectTo}"/>

                <div class="clearfix border-bottom-padding-marging">
                    <div class="h2 pull-left">${i18n['ct.login']}</div><div class="h6 pull-right text-muted">${i18n['ct.required']}*</div>
                </div>
                <div class="form-group">
                    <label for="loginEmail">${i18n['ctLogin.loginEmail']}*</label>
                    <input name="ctCustomerEmail" id="loginEmail" type="email" placeholder="Email" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$" required>
                </div>
                <div class="form-group">
                    <label for="loginPassword">${i18n['ctLogin.loginPassword']}*</label>
                    <input name="ctCustomerPassword" type="password" id="loginPassword" placeholder="Password" required>
                </div>
                <div>
                    <input type="checkbox" value="second_checkbox"> <label for="cboxRememberMe">${i18n['ctLogin.cboxRememberMe']}</label>
                    <a href="${forgotYourPasswordLink}" class="pull-right">${i18n['ctLogin.forgotYourPassword']}</a>
                </div>
                <button type="submit" class="btn cta-button pull-right">${i18n['ctLogin.submit']}</button>
            </form>

            [#if authenticationError?has_content]
                <ul>
                    [#list authenticationError as error]
                        <li>${error.getMessage()}</li>
                    [/#list]
                </ul>
            [/#if]
        </div>
    </div>
[/#if]
