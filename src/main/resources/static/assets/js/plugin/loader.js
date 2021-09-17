//-----------------------------------------------------------------------
// Page Loader with preload
//----------------------------------------------------------------------
document.addEventListener('DOMContentLoaded', function () {
    var loader = document.getElementById("loader");
    setTimeout(() => {
        var loaderOpacity = 1;
        var fadeAnimation = setInterval(() => {
            if (loaderOpacity <= 0.05) {
                clearInterval(fadeAnimation);
                loader.style.opacity = "0";
                loader.style.display = "none";
            }
            loader.style.opacity = loaderOpacity;
            loader.style.filter = "alpha(opacity=" + loaderOpacity * 100 + ")";
            loaderOpacity = loaderOpacity - loaderOpacity * 0.5;
        }, 30);
    }, 400);
})
//-----------------------------------------------------------------------