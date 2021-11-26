
function c_ajaxRequest(url, params) {
    var options = {
        asynchronous: false,
        parameters: params,
        method: 'POST'
    };
    var transport = requestHandler.doRequest(url, options);
    return transport.responseText;
}

function c_trim(str) {
    return str.replace(/^\s\s*/, '').replace(/\s\s*$/, '');
}

