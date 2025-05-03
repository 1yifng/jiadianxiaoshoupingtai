// Element UI兼容层，替代layer.js
(function(window) {
    // 创建一个兼容layer的对象
    var layer = {
        // 打开对话框
        open: function(options) {
            if (options.type === 2) { // iframe类型
                const iframe = document.createElement('iframe');
                iframe.src = options.content;
                iframe.width = '100%';
                iframe.height = (parseInt(options.area[1]) - 50) + 'px';
                iframe.frameBorder = '0';
                
                // 使用Element UI的对话框
                ELEMENT.MessageBox({
                    title: options.title || '提示',
                    message: iframe,
                    showCancelButton: false,
                    confirmButtonText: '关闭',
                    customClass: 'el-message-box--large',
                    beforeClose: (action, instance, done) => {
                        if (options.end) {
                            options.end();
                        }
                        done();
                    }
                });
            } else { // 普通对话框
                ELEMENT.MessageBox({
                    title: options.title || '提示',
                    message: options.content,
                    showCancelButton: options.btn && options.btn.length > 1,
                    confirmButtonText: options.btn ? options.btn[0] : '确定',
                    cancelButtonText: options.btn && options.btn.length > 1 ? options.btn[1] : '取消',
                    beforeClose: (action, instance, done) => {
                        if (action === 'confirm' && options.yes) {
                            options.yes();
                        } else if (action === 'cancel' && options.btn2) {
                            options.btn2();
                        }
                        if (options.end) {
                            options.end();
                        }
                        done();
                    }
                });
            }
        },
        
        // 消息提示
        msg: function(content, options, end) {
            if (typeof options === 'function') {
                end = options;
                options = {};
            }
            options = options || {};
            
            ELEMENT.Message({
                message: content,
                type: options.icon === 1 ? 'success' : (options.icon === 2 ? 'warning' : (options.icon === 3 ? 'error' : 'info')),
                duration: options.time || 3000,
                onClose: end
            });
        },
        
        // 加载层
        load: function(icon, options) {
            const loading = ELEMENT.Loading.service({
                lock: true,
                text: '加载中...',
                spinner: 'el-icon-loading',
                background: 'rgba(0, 0, 0, 0.7)'
            });
            
            return loading;
        },
        
        // 关闭指定层
        close: function(index) {
            if (typeof index === 'object') {
                index.close();
            }
        },
        
        // 确认对话框
        confirm: function(content, options, yes, cancel) {
            if (typeof options === 'function') {
                cancel = yes;
                yes = options;
                options = {};
            }
            
            ELEMENT.MessageBox.confirm(
                content,
                options.title || '提示',
                {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }
            ).then(() => {
                if (yes) yes();
            }).catch(() => {
                if (cancel) cancel();
            });
        },
        
        // 提示框
        alert: function(content, options, yes) {
            if (typeof options === 'function') {
                yes = options;
                options = {};
            }
            
            ELEMENT.MessageBox.alert(
                content,
                options.title || '提示',
                {
                    confirmButtonText: '确定',
                    callback: action => {
                        if (yes) yes();
                    }
                }
            );
        }
    };
    
    // 将layer对象暴露到全局
    window.layer = layer;
})(window);