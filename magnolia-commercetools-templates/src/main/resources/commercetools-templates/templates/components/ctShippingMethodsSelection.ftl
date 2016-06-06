[#-------------- INCLUDE AND ASSIGN PART --------------]

[#if content.mandatory!false]
    [#assign requiredAttribute = cmsfn.createHtmlAttribute("required", "required")]
[/#if]

[#-------------- RENDERING PART --------------]

<div ${model.style!}>
    [#if content.title?has_content]
        <label for="${content.controlName!''}">
            <span>
            [#if !model.isValid()]
                <em>${i18n['form.error.field']}</em>
            [/#if]
            ${content.title!}
                [#if content.mandatory!false]
                    <dfn title="required">${model.requiredSymbol!}</dfn>
                [/#if]
            </span>
        </label>
    [/#if]

    [#if content.legend?has_content]
        <legend>${content.legend}</legend>
    [/#if]

    <select ${requiredAttribute!} id="${(content.controlName!'')}" name="${(content.controlName!'')}" >
        [#assign shippingMethodList = ctfn.getShippingMethodList().getResults()!]
        [#if shippingMethodList?has_content]
            [#list shippingMethodList as shippingMethod]
                [#assign selected=""]
                [#if shippingMethod.isDefault() ]
                    [#assign selected="selected=\"selected\""]
                [/#if]
                <option value="${(shippingMethod.getId())!?html}" ${selected!} >${shippingMethod.getName()!?html}</option>
            [/#list]
        [/#if]
    </select>

</div><!-- end ${model.style!} -->
