(function () {
    if (!window.jQuery) {
        let s = document.createElement('script');
        s.type = 'text/javascript';
        s.src = '%s';
        document.body.appendChild(s);
    }
})();