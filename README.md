# cordova-plugin-zhl-yandex-ads-banner

## 1. Installation

`$ cordova plugin add https://github.com/zahalski/cordova-plugin-zhl-yandex-ads-banner.git`

## 2. Quick Start

**use callbacks**

```JavaScript
window.plugins.YandexAdsBanner.init({
    "onSuccess":function(){
        window.plugins.YandexAdsBanner.load({
            "blockId": "R-M-DEMO-interstitial"
            "onSuccess": function(){
                window.plugins.YandexAdsBanner.show();
            }
        });
    }
});
```

**or window events**

```JavaScript
window.plugins.YandexAdsBanner.init();
$(window).on("YandexAds:init",function(){
    window.plugins.YandexAdsBanner.load({
        'blockId':'R-M-DEMO-interstitial'
    });
});
$(window).on("YandexAds:bannerLoaded",function(){
    window.plugins.YandexAdsBanner.show();
});
```

## 3. Window Events

| Event | Description |
| --- | --- |
| `YandexAds:init` |  |
| `YandexAds:bannerLoaded` |  |
| `YandexAds:bannerStartLoad` |  |
| `YandexAds:bannerFailed` |  |
| `YandexAds:bannerShow` |  |
| `YandexAds:bannerStartShow` |  |
| `YandexAds:bannerClose` |  |
| `YandexAds:bannerOutApp` |  |
| `YandexAds:bannerReturnApp` |  |
| `YandexAds:bannerClick` |  |
| `YandexAds:bannerShowRegistered` |  |

