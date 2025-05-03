// 创建axios实例
const server = axios.create({
    baseURL: (window.baseUrl || '') + '/jiadianxiaoshoupingtai',
    timeout: 10000
});

// 请求拦截器
server.interceptors.request.use(
    config => {
        // 添加token到请求头
        if (localStorage.getItem('Token')) {
            config.headers.token = localStorage.getItem('Token');
        }
        return config;
    },
    error => {
        console.error('请求错误:', error);
        return Promise.reject(error);
    }
);

// 响应拦截器
server.interceptors.response.use(
    response => {
        // 直接返回响应数据
        return response.data;
    },
    error => {
        console.error('响应错误:', error);
        return Promise.reject(error);
    }
);

// 将server对象暴露到全局作用域
window.server = server;