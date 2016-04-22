$(document).ready(function () {
    'use strict';

    //  VARIABLE/FUNCTION DECLARATION
    var $primaryNavigation = $('.primary_nav');
    var $itemDetailDescription = $('.mob-description');
    var $filterMenu = $('.filter');

    function isMobile() {
        return $(window).width() < 768 ? true : false;
    }

    function toggleMenu() {
        if (isMobile()) {
            $primaryNavigation.hide();
        } else {
            $primaryNavigation.show();
        }
    }

    function setMobileMenu() {
        if (isMobile()) {
            $('ul', '.sub_menu > li').each(function () {
                if ($(this).hasClass('hidden')) {
                    return false;
                } else {
                    $(this).addClass('hidden');
                }
            });
        } else {
            $('ul', '.sub_menu > li').each(function () {
                if ($(this).hasClass('hidden')) {
                    $(this).removeClass('hidden');
                } else {
                    return false;
                }
            });
        }
    }

    function hideDescription() {
        if ($('h3.mobile').is(':visible')) {
            $itemDetailDescription.hide();
        } else {
            $itemDetailDescription.show();
        }
    }

    function showFilter() {
        if (!isMobile() && $filterMenu.not(':visible')) {
            $filterMenu.show();
        } else if (isMobile && $filterMenu.is(':visible')) {
            $filterMenu.hide();
        } else {
            return false;
        }
    }

    function changeBuyButton() {
        if (isMobile()) {
            $('.buy')
                .removeClass('glyphicon glyphicon-shopping-cart')
                .addClass('btn cta-button')
                .text('buy');

        } else {
            $('.buy')
                .addClass('glyphicon glyphicon-shopping-cart')
                .removeClass('btn cta-button')
                .text('');
        }
    }

    //  SETUP FOR MOBILE
    toggleMenu();
    setMobileMenu();
    hideDescription();
    changeBuyButton();

    //  EVENTS
    //resize related events
    $(window).resize(function () {
        setMobileMenu();
        toggleMenu();
        hideDescription();
        showFilter();
        changeBuyButton();
    });

    //MAIN NAV toggle on mobile devices
    $('.ham-menu').on('click', function (e) {
        e.preventDefault();
        $primaryNavigation.toggle();
    });

    //SUBMENU toggle on mobile devices
    $('.sub_menu > li > a').on('click', function (e) {
        if (isMobile()) {
            e.preventDefault();
            $(this).next().toggleClass('hidden');
        }
    });

    //FILTER menu toggle
    $('#filter-btn').on('click', function (e) {
        e.preventDefault();
        $('.filter').toggle();
    });

    //reset checkboxes in filter
    $('a', '.notation').on('click', function (e) {
        e.preventDefault();
        var typeToReset = $(this).attr('id');
        if (typeToReset === 'clearall') {
            $('input:checkbox').removeAttr('checked');
        } else {
            $('input:checkbox', '.' + typeToReset).removeAttr('checked');
        }
    });

    //DESCRIPTION toggle on mobile devices
    $('h3.mobile').on('click', function (e) {
        e.preventDefault();
        $itemDetailDescription.toggle();
    });

    //SUB SUB menu effect
    $('ul', '.sub_menu > li > ul > li').hide();
    $('li', '.sub_menu > li > ul').hover(function () {
        $('ul', $(this)).toggle();
    }, function () {
        $('ul', $(this)).toggle();
    });
});