[#-------------- ASSIGNMENTS --------------]
[#assign bgImage = cmsfn.siteRoot(content).headerBgImage!]

[#-------------- RENDERING --------------]
<!-- Header -->
[#if bgImage?has_content]
    [#assign assetRendition = damfn.getRendition(bgImage, "original")! /]
    [#if assetRendition?has_content]
        <header class="intro-header" style='background-image: url(${assetRendition.getLink()});'>
            <div class="container">
                <div class="row">
                    <div class="col-lg-12">
                        <div class="intro-message">
                            <div>
                                <!-- <h1>CommerceTools demo shop</h1> -->
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </header>
    [/#if]
[/#if]
