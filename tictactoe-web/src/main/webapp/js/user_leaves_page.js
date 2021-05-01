document.addEventListener("visibilitychange", function () {
    if (document.visibilityState === 'hidden') {
        navigator.sendBeacon("../SessionCleaner")
    }
});
