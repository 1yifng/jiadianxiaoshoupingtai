/**
 * Layer模块，替代Layui的Layer模块，使用ElementUI实现
 */
const layer = {
    /**
     * 打开iframe弹窗
     * @param {Object} options 选项
     * @returns {String} 弹窗ID
     */
    open: function(options) {
        const id = 'el-dialog-' + Date.now();
        let dialogHtml = `
            <div id="${id}" class="el-dialog-wrapper" style="position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);z-index:9999;display:flex;justify-content:center;align-items:center;">
                <div class="el-dialog" style="background:#fff;border-radius:4px;box-shadow:0 2px 12px 0 rgba(0,0,0,.1);width:${options.area ? options.area[0] : '50%'};max-width:90%;">
                    <div class="el-dialog__header" style="padding:20px;border-bottom:1px solid #eee;position:relative;">
                        <span class="el-dialog__title" style="font-size:18px;color:#333;">${options.title || '提示'}</span>
                        <button type="button" class="el-dialog__close" style="position:absolute;right:20px;top:20px;background:none;border:none;font-size:20px;cursor:pointer;">&times;</button>
                    </div>
                    <div class="el-dialog__body" style="padding:20px;max-height:70vh;overflow:auto;box-sizing:border-box;">
                        ${options.content || ''}
                    </div>
                </div>
            </div>
        `;
        $('body').append(dialogHtml);
        
        // 如果是iframe类型
        if (options.type === 2 && options.content) {
            const height = options.area && options.area[1] ? options.area[1] : '400px';
            $(`#${id} .el-dialog__body`).html(`<iframe src="${options.content}" style="width:100%;height:${height};border:none;"></iframe>`);
        }
        
        // 关闭按钮事件
        $(`#${id} .el-dialog__close`).on('click', function() {
            $(`#${id}`).remove();
        });
        
        return id;
    },
    
    /**
     * 关闭弹窗
     * @param {String} id 弹窗ID
     */
    close: function(id) {
        $(`#${id}`).remove();
    },
    
    /**
     * 消息提示
     * @param {String} content 内容
     * @param {Object|Function} options 选项或回调
     * @param {Function} callback 回调
     */
    msg: function(content, options, callback) {
        if (typeof options === 'function') {
            callback = options;
            options = {};
        }
        options = options || {};
        
        const id = 'el-message-' + Date.now();
        const time = options.time || 3000;
        
        let type = options.icon || 0;
        let typeClass = '';
        let backgroundColor = '#fff';
        let color = '#606266';
        
        // 提示类型
        switch(type) {
            case 1: // 成功
                typeClass = 'el-icon-success';
                backgroundColor = '#f0f9eb';
                color = '#67C23A';
                break;
            case 2: // 错误
                typeClass = 'el-icon-error';
                backgroundColor = '#fef0f0';
                color = '#F56C6C';
                break;
            case 3: // 警告
                typeClass = 'el-icon-warning';
                backgroundColor = '#fdf6ec';
                color = '#E6A23C';
                break;
            case 4: // 信息
                typeClass = 'el-icon-info';
                backgroundColor = '#edf2fc';
                color = '#909399';
                break;
            default: // 默认
                typeClass = 'el-icon-info';
                break;
        }
        
        let messageHtml = `
            <div id="${id}" class="el-message" style="position:fixed;top:20px;left:50%;transform:translateX(-50%);padding:10px 20px;border-radius:4px;background:${backgroundColor};color:${color};z-index:9999;border:1px solid #EBEEF5;display:flex;align-items:center;transition:opacity 0.3s;">
                <i class="${typeClass}" style="margin-right:10px;"></i>
                <span>${content}</span>
            </div>
        `;
        
        $('body').append(messageHtml);
        
        // 自动关闭
        setTimeout(function() {
            $(`#${id}`).fadeOut(function() {
                $(this).remove();
                callback && callback();
            });
        }, time);
        
        return id;
    },
    
    /**
     * 确认弹窗
     * @param {String} content 内容
     * @param {Object|Function} options 选项或回调
     * @param {Function} callback 回调
     */
    confirm: function(content, options, callback) {
        if (typeof options === 'function') {
            callback = options;
            options = {};
        }
        options = options || {};
        
        const id = 'el-confirm-' + Date.now();
        
        let confirmHtml = `
            <div id="${id}" class="el-message-box__wrapper" style="position:fixed;top:0;left:0;width:100%;height:100%;background:rgba(0,0,0,0.5);z-index:9999;display:flex;justify-content:center;align-items:center;">
                <div class="el-message-box" style="background:#fff;border-radius:4px;box-shadow:0 2px 12px 0 rgba(0,0,0,.1);width:420px;max-width:90%;">
                    <div class="el-message-box__header" style="padding:15px;border-bottom:1px solid #eee;position:relative;">
                        <span class="el-message-box__title" style="font-size:18px;color:#333;">${options.title || '确认'}</span>
                        <button type="button" class="el-message-box__close" style="position:absolute;right:15px;top:15px;background:none;border:none;font-size:20px;cursor:pointer;">&times;</button>
                    </div>
                    <div class="el-message-box__content" style="padding:20px;">
                        <div class="el-message-box__message" style="display:flex;align-items:center;">
                            <i class="el-icon-warning" style="color:#E6A23C;margin-right:10px;font-size:20px;"></i>
                            <span>${content}</span>
                        </div>
                    </div>
                    <div class="el-message-box__btns" style="padding:10px 20px;text-align:right;">
                        <button type="button" class="el-button el-button--default el-button--small el-message-box__cancel" style="margin-right:10px;padding:7px 15px;border:1px solid #dcdfe6;background:#6495ed;border-radius:3px;color:#606266;cursor:pointer;">取消</button>
                        <button type="button" class="el-button el-button--primary el-button--small el-message-box__confirm" style="padding:7px 15px;border:1px solid #409eff;background:#409eff;border-radius:3px;color:#fff;cursor:pointer;">确定</button>
                    </div>
                </div>
            </div>
        `;
        
        $('body').append(confirmHtml);
        
        // 关闭按钮事件
        $(`#${id} .el-message-box__close, #${id} .el-message-box__cancel`).on('click', function() {
            $(`#${id}`).remove();
            callback && callback(false);
        });
        
        // 确认按钮事件
        $(`#${id} .el-message-box__confirm`).on('click', function() {
            $(`#${id}`).remove();
            callback && callback(true);
        });
        
        return id;
    }
};

// 兼容Layui模块系统的导出方式
if (typeof window !== 'undefined') {
    window.layer = layer;
} 