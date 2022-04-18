function YandexAdsBanner() {}

YandexAdsBanner.prototype.init = function (params) {
    params = defaults(params, {});
    callPlugin('init', [], function (){
        if (isFunction(params.onSuccess)){
            params.onSuccess();
        }
    }, params.onFailure);
};

YandexAdsBanner.prototype.load = function (params) {
  params = defaults(params, {});
  var blockId = false;
  if(params.hasOwnProperty('blockId')) blockId = params.blockId;
  if(params.hasOwnProperty('bannerId')) blockId = params.bannerId;
  if(!blockId || typeof(blockId)!=='string'){
    cordova.fireWindowEvent('YandexAds:bannerFailed', 'blockId is required');
  }
  callPlugin('load', [blockId], function (){
      if (isFunction(params.onSuccess)){
          params.onSuccess();
      }
  }, params.onFailure);
};

YandexAdsBanner.prototype.show = function (params) {
  params = defaults(params, {});
        callPlugin('show', [], function (){
            if (isFunction(params.onSuccess)){
                params.onSuccess();
            }
        }, params.onFailure);
};

YandexAdsBanner.install = function () {
  if (!window.plugins) {
    window.plugins = {};
  }
  window.plugins.YandexAdsBanner = new YandexAdsBanner();
  return window.plugins.YandexAdsBanner;
};

function callPlugin(name, params, onSuccess, onFailure){
    cordova.exec(function callPluginSuccess(result){
        if (isFunction(onSuccess)){
            onSuccess(result);
        }
    }, function callPluginFailure(error){
        if (isFunction(onFailure)){
            onFailure(error)
        }
    }, 'YandexAdsBanner', name, params);
}

function isFunction(functionToCheck)
{
    var getType = {};
    var isFunction = functionToCheck && getType.toString.call(functionToCheck) === '[object Function]';
    return isFunction === true;
}

function defaults(o, defaultObject){
    if (typeof o === 'undefined'){
        return defaults({}, defaultObject);
    }

    for (var j in defaultObject){
        if (defaultObject.hasOwnProperty(j) && o.hasOwnProperty(j) === false){
            o[j] = defaultObject[j];
        }
    }

    return o;
}

cordova.addConstructor(YandexAdsBanner.install);