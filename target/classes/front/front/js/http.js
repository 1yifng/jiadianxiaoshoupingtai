/**
 * HTTP请求模块，替代Layui的HTTP模块
 */
const http = {
    /**
     * 获取URL参数
     */
    getParam: function(name) {
        const reg = new RegExp("(^|&)" + name + "=([^&]*)(&|$)");
        const r = window.location.search.substr(1).match(reg);
        if (r != null) return decodeURI(r[2]);
        return null;
    },

    /**
     * GET请求
     */
    get: function(url, params, callback) {
        if (typeof params === 'function') {
            callback = params;
            params = {};
        }
        $.ajax({
            url: url,
            type: 'GET',
            data: params,
            success: function(res) {
                callback && callback(res);
            }
        });
    },

    /**
     * POST请求
     */
    post: function(url, data, callback) {
        if (typeof data === 'function') {
            callback = data;
            data = {};
        }
        $.ajax({
            url: url,
            type: 'POST',
            data: data,
            success: function(res) {
                callback && callback(res);
            }
        });
    },

    /**
     * 通用请求方法
     */
    request: function(options) {
        $.ajax({
            url: options.url,
            type: options.type || 'GET',
            data: options.data,
            contentType: options.contentType || 'application/x-www-form-urlencoded',
            success: function(res) {
                options.success && options.success(res);
            },
            error: function(xhr) {
                options.error && options.error(xhr);
            }
        });
    }
};

// 兼容Layui模块系统的导出方式
if (typeof window !== 'undefined') {
    window.http = http;
} 