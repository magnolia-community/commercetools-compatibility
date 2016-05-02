[#macro productPager total currentPage perPage]
    [#-------------- ASSIGNMENTS --------------]
    [#if perPage == 0]
        [#-- set default PagedSearchResult value--]
        [#assign perPage = 20]
    [/#if]

    [#-------------- RENDERING --------------]
    [#if total>perPage]
        <div class="container">
            <div class="row">
                <div class="text-center">
                    <ul class="pagination">
                        [#if currentPage>1]
                            <li><a href="${ctfn.getPageLink(1)}" aria-label="First"><span aria-hidden="true">&laquo;</span></a></li>
                            <li><a href="${ctfn.getPageLink(currentPage-1)}" aria-label="Previous"><span aria-hidden="true">&lsaquo;</span></a></li>
                        [/#if]
                        [#assign pages=(total/perPage)?ceiling]
                        [#list 1..pages as i]
                            <li><a href="${ctfn.getPageLink(i)}" [#if i==currentPage]style="color:green"[/#if]>${i}</a></li>
                        [/#list]
                        [#if currentPage<pages]
                            <li><a href="${ctfn.getPageLink(currentPage+1)}" aria-label="Next"><span aria-hidden="true">&rsaquo;</span></a></li>
                            <li><a href="${ctfn.getPageLink(pages)}" aria-label="Last"><span aria-hidden="true">&raquo;</span></a></li>
                        [/#if]
                    </ul>
                </div>
            </div>
        </div>
    [/#if]
[/#macro]