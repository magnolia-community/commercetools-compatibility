[#macro productFilters attributeFacets facetResults productTypes filterBy params={}]
    [#-------------- RENDERING --------------]
    [#assign keys = facetResults?keys]
    <form>
        [#list params?keys as key]
            <input type="hidden" name="${key}" value="${params[key]}">
        [/#list]
        <div class="visible-xs">
            <button id="filter-btn" class="btn cta-button filter-button center-block">FILTER</button>
        </div>
        <div class="filter col-lg-3 col-md-3 col-sm-3">
            <div class="row">
                <h3>Filter products: <input type="submit" value="Apply filters" class="btn cta-button filter-apply-button"/></h3>
            </div>
            [#list keys as key]
                [#assign splitKey = key?split(".")]
                <div class="divider"></div>
                <div class="notation">
                    <h4>${ctfn.getLocalizedAttributeName(productTypes, splitKey[2])[0]}</h4>
                    <a id="${ctfn.getLocalizedAttributeName(productTypes, splitKey[2])[0]}" class="pull-right">clear</a>
                </div>
                [#assign terms = facetResults[key].terms]
                <div class="row filter-container two-column ${ctfn.getLocalizedAttributeName(productTypes, splitKey[2])[0]}">
                    [#list terms as termStats]
                        <div>
                            <label>
                                <input type="checkbox" name="${splitKey[2]}" value="${termStats.term}" [#if (filterBy[splitKey[2]]?seq_contains(termStats.term))!false]checked[/#if]>
                                ${termStats.term}
                            </label>
                        </div>
                    [/#list]
                </div>
            [/#list]
            <div class="divider"></div>
            <div class="notation">
                <h4></h4>
                <a id="clearall" class="pull-right">clear all</a>
            </div>
        </div>
    </form>
[/#macro]