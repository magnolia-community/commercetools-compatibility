[#-------------- INCLUDES AND ASSIGNMENTS --------------]
[#assign homePageContent = cmsfn.siteRoot(content)]
[#assign customer = (ctx.getAttribute(ctfn.getProjectName() + "_ctCustomerId"))!""]

[#-------------- RENDERING --------------]
<ul>
    <li>
        [#if customer?has_content || cmsfn.editMode]
            <a href="${cmsfn.link("website", content.authenticationPage!"")!cmsfn.link(homePageContent)}?ctAction=ctDoLogout">${i18n['ct.logout']} </a>
        [/#if]
        [#if !customer?has_content || cmsfn.editMode]
            <a href="${cmsfn.link("website", content.authenticationPage!"")!"#"}">${i18n['ct.login']} </a>
        [/#if]
    </li>
</ul>