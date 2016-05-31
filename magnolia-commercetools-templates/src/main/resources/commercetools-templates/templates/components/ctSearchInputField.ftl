[#-------------- INCLUDES AND ASSIGNMENTS --------------]

[#-------------- RENDERING --------------]
<ul>
    <li>
        <form id="navbar-form" role="search" action="${cmsfn.link("website", content.searchResultPage!"")}">
            <input type="text" name="queryStr">
            <button type="submit">${i18n['ct.search']}</button>
        </form>
    </li>
</ul>