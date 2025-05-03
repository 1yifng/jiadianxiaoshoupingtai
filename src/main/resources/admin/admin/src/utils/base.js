const base = {
    get() {
        return {
            url : "http://localhost:8080/jiadianxiaoshoupingtai/",
            name: "jiadianxiaoshoupingtai",
            // 退出到首页链接
            indexUrl: 'http://localhost:8080/jiadianxiaoshoupingtai/front/index.html'
        };
    },
    getProjectName(){
        return {
            projectName: "家电展示销售平台"
        } 
    }
}
export default base
