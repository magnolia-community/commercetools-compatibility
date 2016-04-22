[#-------------- ASSIGNMENTS --------------]
[#assign redirectTo = cmsfn.link("website", content.mgnlReturnTo!"")!cmsfn.link(cmsfn.page(content))!"#"]
[#assign authenticationError = (ctx.getAttribute("ctLoginError"))!""]
[#assign customer = (ctx.getAttribute("ctCustomer"))!""]

[#-------------- RENDERING --------------]
[#if customer?has_content || cmsfn.editMode]
    ${i18n['ct.authenticated']}, <a href="${redirectTo}?ctAction=ctDoLogout">${i18n['ct.logout']} </a>.
[/#if]
[#if !customer?has_content || cmsfn.editMode]
    <form >
        <input type="hidden" name="ctAction" value="ctDoLogin"/>
        <input type="hidden" name="mgnlReturnTo" value="${redirectTo}"/>
        <div>${i18n['ct.login']}</div><div>${i18n['ct.required']}*</div>

        <div>
            <label for="loginEmail">${i18n['ctLogin.loginEmail']}*</label>
            <input name="ctCustomerEmail" type="email" placeholder="Email" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$" required>
        </div>
        <div>
            <label for="loginPassword">${i18n['ctLogin.loginPassword']}*</label>
            <input name="ctCustomerPassword" type="password" placeholder="Password" required>
        </div>
        <div>
            <input type="checkbox" value="second_checkbox"> <label for="cboxRememberMe">${i18n['ctLogin.cboxRememberMe']}</label>
            <a href="#" >${i18n['ctLogin.forgotYourPassword']}</a>
        </div>
        <button type="submit" >${i18n['ctLogin.submit']}</button>
    </form>

    [#if authenticationError?has_content]
        <ul>
            [#list authenticationError as error]
                <li>${error.getMessage()}</li>
            [/#list]
        </ul>
    [/#if]
[/#if]
