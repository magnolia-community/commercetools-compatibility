[#macro productFilters attributeFacets facetResults productTypes filterBy]
    [#-------------- RENDERING --------------]
    [#assign keys = facetResults?keys]
    <form>
        <fieldset><legend>FILTERS</legend></fieldset>
        [#list keys as key]
            <div>
                [#assign splitKey = key?split(".")]
                ${ctfn.getLocalizedAttributeName(productTypes, splitKey[2])[0]}<br />
                [#assign terms = facetResults[key].terms]
                [#list terms as termStats]
                    <input type="checkbox" name="${splitKey[2]}" id="${splitKey[2]}_${termStats.term}" value="${termStats.term}" [#if (filterBy[splitKey[2]]?seq_contains(termStats.term))!false]checked[/#if]> <label for="${splitKey[2]}_${termStats.term}">${termStats.term}</label>
                [/#list]
            </div>
        [/#list]
        <input type="submit" value="Filter results">
    </form>
[/#macro]