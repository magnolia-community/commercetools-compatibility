[#-------------- INCLUDES AND ASSIGNMENTS --------------]

[#-------------- RENDERING --------------]
<li>
    <form id="navbar-form" role="search" action="${cmsfn.link("website", content.searchResultPage!"")}">
        <input class="search-bar" type="text" name="queryStr">
        <button class="btn cta-button search-button" type="submit">${i18n['ct.search']}</button>
    </form>
</li>
