import Vue from 'vue';
//配置路由
import VueRouter from 'vue-router'
Vue.use(VueRouter);
    // 解决多次点击左侧菜单报错问题
    const VueRouterPush = VueRouter.prototype.push
    VueRouter.prototype.push = function push (to) {
    return VueRouterPush.call(this, to).catch(err => err)
    }
//1.创建组件
import Index from '@/views/index'
import Home from '@/views/home'
import Login from '@/views/login'
import NotFound from '@/views/404'
import pay from '@/views/pay'
import center from '@/views/center'
    import jiadian from '@/views/modules/jiadian/list'
    import jiadianCollection from '@/views/modules/jiadianCollection/list'
    import jiadianCommentback from '@/views/modules/jiadianCommentback/list'
    import jiadianOrder from '@/views/modules/jiadianOrder/list'
    import news from '@/views/modules/news/list'
    import yonghu from '@/views/modules/yonghu/list'
    import config from '@/views/modules/config/list'
    import dictionaryJiadian from '@/views/modules/dictionaryJiadian/list'
    import dictionaryNews from '@/views/modules/dictionaryNews/list'





//2.配置路由   注意：名字
const routes = [{
    path: '/index',
    name: '首页',
    component: Index,
    children: [{
      // 这里不设置值，是把main作为默认页面
      path: '/',
      name: '首页',
      component: Home,
      meta: {icon:'', title:'center'}
    }, {
      path: '/pay',
      name: '支付',
      component: pay,
      meta: {icon:'', title:'pay'}
    }, {
      path: '/center',
      name: '个人信息',
      component: center,
      meta: {icon:'', title:'center'}
    }
    ,{
        path: '/dictionaryJiadian',
        name: '商品类型',
        component: dictionaryJiadian
    }
    ,{
        path: '/dictionaryNews',
        name: '公告类型',
        component: dictionaryNews
    }
    ,{
        path: '/config',
        name: '轮播图',
        component: config
    }
    ,{
        path: '/jiadian',
        name: '商品',
        component: jiadian
      }
    ,{
        path: '/jiadianCollection',
        name: '商品收藏',
        component: jiadianCollection
      }
    ,{
        path: '/jiadianCommentback',
        name: '商品评价',
        component: jiadianCommentback
      }
    ,{
        path: '/jiadianOrder',
        name: '商品订单',
        component: jiadianOrder
      }
    ,{
        path: '/news',
        name: '公告信息',
        component: news
      }
    ,{
        path: '/yonghu',
        name: '用户',
        component: yonghu
      }

    ]
  },
  {
    path: '/login',
    name: 'login',
    component: Login,
    meta: {icon:'', title:'login'}
  },
  {
    path: '/',
    name: '首页',
    redirect: '/index'
  }, /*默认跳转路由*/
  {
    path: '*',
    component: NotFound
  }
]
//3.实例化VueRouter  注意：名字
const router = new VueRouter({
  mode: 'hash',
  /*hash模式改为history*/
  routes // （缩写）相当于 routes: routes
})

export default router;
