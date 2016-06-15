[#-------------- ASSIGNMENTS --------------]
[#assign redirectTo = cmsfn.link("website", content.mgnlReturnTo!"")!cmsfn.link(cmsfn.page(content))!"#"]
[#assign authenticationError = (ctx.getAttribute("ctSignupError"))!""]
[#assign customer = (ctx.getAttribute(ctfn.getProjectName() + "_ctCustomerId"))!""]

[#-------------- RENDERING --------------]
<form>
    <input type="hidden" name="ctAction" value="ctDoSignup"/>
    <input type="hidden" name="mgnlReturnTo" value="${redirectTo}"/>

    <div>${i18n['ctSignup.signup']}</div><div>${i18n['ct.required']}*</div>

        <div>
            <label for="signupFirstName">${i18n['ctSignup.signupFirstName']}*</label>
            <input name="ctCustomerFirstName" type="text" placeholder="First name" required>
        </div>
        <div>
            <label for="signupLastName">${i18n['ctSignup.signupLastName']}*</label>
            <input name="ctCustomerLastName" type="text" placeholder="Last name" required>
        </div>
        <div>
            <label for="signupEmail">${i18n['ctSignup.signupEmail']}*</label>
            <input name="ctCustomerEmail" type="email" placeholder="Email" pattern="[a-z0-9._%+-]+@[a-z0-9.-]+\.[a-z]{2,4}$" required>
        </div>
        <div>
            <label for="signupPassword">${i18n['ctSignup.signupPassword']}*</label>
            <input name="ctCustomerPassword" type="password" placeholder="Password" required>
        </div>
        <div>
            <label for="signupPasswordConfirm">${i18n['ctSignup.signupPasswordConfirm']}*</label>
            <input type="password" placeholder="Password" required>
        </div>

    <button type="submit">${i18n['ctSignup.submit']}</button>
</form>

[#if authenticationError?has_content]
    <ul>
        [#list authenticationError as error]
            <li>${error.getMessage()}</li>
        [/#list]
    </ul>
[/#if]