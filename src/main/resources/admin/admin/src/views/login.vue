<template>
  <!-- 登录页面主容器 -->
  <div class="login-container">
    <!-- 使用Element UI的表单组件 label-position="left"：标签位于文本框左边-->
    <el-form class="login-form" label-position="left">
      <!-- 标题容器 -->
      <div class="title-container">
        <h3 class="title">家电销售平台管理登录</h3>
      </div>

      <!-- 用户名输入部分 -->
      <el-form-item>
        <!-- 使用flex布局使图标和输入框水平排列 -->
        <div class="flex-container">
          <!-- 用户图标 -->
          <span class="svg-container">
            <svg-icon icon-class="user" />
          </span>

          <!-- 用户名输入框 -->
          <!--v-model 双向数据绑定,v-model="rulesForm.username"：用户输入的用户名会自动同步到 this.rulesForm.username。-->
          <el-input
              placeholder="请输入用户名"
              v-model="rulesForm.username"
              class="custom-input"/>
        </div>
      </el-form-item>

      <!-- 密码输入部分 -->
      <el-form-item>
        <div class="flex-container">
          <!-- 密码图标 -->
          <span class="svg-container">
            <svg-icon icon-class="password" />
          </span>
          <!-- 密码输入框 -->
          <!--v-model 双向数据绑定,v-model="rulesForm.password"：用户输入的密码会自动同步到 this.rulesForm.password。-->
          <el-input
              placeholder="请输入密码"
              type="password"
              v-model="rulesForm.password"
              class="custom-input"/>
        </div>
      </el-form-item>

      <!-- 登录按钮 -->
      <!-- 点击登录按钮后触发login方法，会判断输入的用户名和密码是否为空，若不为空则发送请求给后端，usersController的/login方法
      this.rulesForm.username为v-model="rulesForm.username
      this.rulesForm.password为v-model="rulesForm.password-->
      <el-form-item>
        <el-button
            type="primary"
            @click="login()"
            class="login-btn">
          登录
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      // 表单数据对象，存储用户名和密码
      rulesForm: {
        username: "",
        password: ""
      }
    };
  },
  methods: {
    // 登录方法
    login() {
      // 表单验证 - 检查用户名是否为空
      if (!this.rulesForm.username) {
        this.$message.error("请输入用户名");
        return;
      }
      // 表单验证 - 检查密码是否为空
      if (!this.rulesForm.password) {
        this.$message.error("请输入密码");
        return;
      }

      // 发送登录请求到后端API
      this.$http({
        url: `users/login?username=${this.rulesForm.username}&password=${this.rulesForm.password}`,
        method: "post"
      }).then(({ data }) => {
        // 请求成功后的处理
        if (data && data.code === 0) {
          // 登录成功，存储用户信息到本地存储
          this.$storage.set("Token", data.token);          // 存储token
          this.$storage.set("userId", data.userId);       // 存储用户ID
          this.$storage.set("role", "管理员");             // 存储用户角色
          this.$storage.set("sessionTable", "users");      // 存储会话表
          this.$storage.set("adminName", this.rulesForm.username); // 存储用户名

          // 跳转到主页
          this.$router.replace({ path: "/index" });
        } else {
          // 登录失败，显示错误信息
          this.$message.error(data.msg);
        }
      });
    }
  }
};
</script>

<style lang="scss" scoped>
/* 弹性盒子布局，用于水平排列图标和输入框 */
.flex-container {
  display: -webkit-flex;
  display: flex;
}

/* 登录容器样式 */
.login-container {
  min-height: 100vh;                  /* 最小高度为视口高度 */
  align-items: center;
  justify-content: center;
  background: url("../../../../img/img/back-img-bg.jpg") center/cover no-repeat; /* 背景图片 */

  /* 登录表单样式 */
  .login-form {
    position: absolute;               /* 绝对定位 */
    top: 50%;                        /* 垂直居中 */
    left: 50%;                       /* 水平居中 */
    transform: translate(-50%, -50%); /* 精确居中 */
    width: 500px;                    /* 表单宽度 */
    height: 300px;                   /* 表单高度 */
    padding: 40px;                   /* 内边距 */
    background: rgba(255, 255, 255, 0.9); /* 半透明白色背景 */
    border-radius: 10px;              /* 圆角 */
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.1); /* 阴影效果 */
  }

  /* 标题样式 */
  .title {
    text-align: center;              /* 文字居中 */
    color: #333;                     /* 文字颜色 */
    margin-bottom: 30px;             /* 底部外边距 */
  }

  /* 表单项统一样式 */
  .el-form-item {
    margin-bottom: 25px;             /* 底部外边距 */
  }

  /* 图标容器样式 */
  .svg-container {
    margin-right: 10px;              /* 右边距 */
    color: #889aa4;                  /* 图标颜色 */
    font-size: 18px;                 /* 图标大小 */
  }

  /* 登录按钮样式 */
  .login-btn {
    width: 50%;                      /* 宽度为父元素50% */
    height: 40px;                    /* 高度 */
    margin-left: 125px;              /* 左边距 */
    margin-top: 10px;                /* 上边距 */
    font-size: 16px;                 /* 字体大小 */
  }
}
</style>