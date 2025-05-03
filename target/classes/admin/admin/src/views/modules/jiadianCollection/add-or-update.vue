<template>
  <!-- 添加/编辑表单主容器 -->
  <div class="addEdit-block">
    <!-- Element UI表单组件 -->
    <el-form
        class="detail-form-content"
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="auto"
    >
      <!-- 使用栅格布局 -->
      <el-row>
        <!-- 商品名称显示列 -->
        <el-col :span="12" >
          <!-- 查看模式下的商品名称显示 点击详情-->
          <div v-if="type=='info'">
            <el-form-item class="input" label="商品名称" prop="jiadianName">
              <el-input
                  v-model="ruleForm.jiadianName"
                  placeholder="商品名称"
                  readonly></el-input>
            </el-form-item>
          </div>
        </el-col>

        <!-- 商品照片显示列 -->
        <el-col :span="12" >
          <div v-if="type=='info'">
            <el-form-item v-if="ruleForm.jiadianPhoto" label="商品照片" prop="jiadianPhoto">
              <img
                  style="margin-right:20px;"
                  v-for="(item,index) in (ruleForm.jiadianPhoto || '').split(',')"
                  :key="index"
                  :src="$base.url+item"
                  width="100"
                  height="100">
            </el-form-item>
          </div>
        </el-col>

        <!-- 商品类型显示列 -->
        <el-col :span="12" >
          <div v-if="type=='info'">
            <el-form-item class="input" label="商品类型" prop="jiadianValue">
              <el-input
                  v-model="ruleForm.jiadianValue"
                  placeholder="商品类型"
                  readonly></el-input>
            </el-form-item>
          </div>
        </el-col>

        <!-- 用户姓名显示列 -->
        <el-col :span="12" >
          <div v-if="type=='info'">
            <el-form-item class="input" label="用户姓名" prop="yonghuName">
              <el-input
                  v-model="ruleForm.yonghuName"
                  placeholder="用户姓名"
                  readonly></el-input>
            </el-form-item>
          </div>
        </el-col>

        <!-- 收藏类型选择列 -->
        <el-col :span="12">
          <div v-if="type=='info'">
            <el-form-item class="input" label="类型" prop="jiadianCollectionValue">
              <el-input
                  v-model="ruleForm.jiadianCollectionValue"
                  placeholder="类型"
                  readonly></el-input>
            </el-form-item>
          </div>
        </el-col>
      </el-row>

      <!-- 表单按钮区域 -->
      <el-form-item class="btn">
        <el-button v-if="type=='info'" class="btn-close" @click="back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

export default {
  data() {
    return {
      id: '',              // 当前记录ID
      type: '',            // 操作类型：'info'=查看，'update'=修改，'add'=添加
      sessionTable: "",    // 登录账户所在表名
      role: "",            // 权限
      userId: "",          // 当前登录人的id
      jiadianForm: {},    // 商品表单数据
      yonghuForm: {},      // 用户表单数据

      // 字段只读控制对象
      ro:{
        jiadianId: false,
        yonghuId: false,
        jiadianCollectionTypes: false,
        insertTime: false,
      },

      // 表单数据对象
      ruleForm: {
        jiadianId: '',              // 商品ID
        yonghuId: '',               // 用户ID
        jiadianCollectionTypes: '', // 收藏类型
        insertTime: '',             // 收藏时间
      },

      // 下拉选项
      jiadianCollectionTypesOptions: [], // 收藏类型选项
      jiadianOptions: [],                // 商品选项
      yonghuOptions: [],                 // 用户选项

    };
  },
  props: ["parent"],  // 父组件引用
  created() {
    // 获取当前登录用户信息
    this.sessionTable = this.$storage.get("sessionTable");
    this.role = this.$storage.get("role");
    this.userId = this.$storage.get("userId");

    // 获取收藏类型下拉选项
    this.$http({
      url: `dictionary/page?page=1&limit=100&sort=&order=&dicCode=jiadian_collection_types`,
      method: "get"
    }).then(({ data }) => {
      if (data && data.code === 0) {
        this.jiadianCollectionTypesOptions = data.data.list;
      }
    });

    // 获取商品下拉选项
    this.$http({
      url: `jiadian/page?page=1&limit=100`,
      method: "get"
    }).then(({ data }) => {
      if (data && data.code === 0) {
        this.jiadianOptions = data.data.list;
      }
    });

    // 获取用户下拉选项
    this.$http({
      url: `yonghu/page?page=1&limit=100`,
      method: "get"
    }).then(({ data }) => {
      if (data && data.code === 0) {
        this.yonghuOptions = data.data.list;
      }
    });
  },
  methods: {

    // 初始化方法
    init(id, type) {
      if (id) {
        this.id = id;
        this.type = type;
      }
      if(this.type=='info'||this.type=='else'){
        this.info(id);
      }

      // 获取当前用户会话信息
      this.$http({
        url: `${this.$storage.get("sessionTable")}/session`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          var json = data.data;
        } else {
          this.$message.error(data.msg);
        }
      });
    },

    // 商品选择变化事件
    jiadianChange(id) {
      this.$http({
        url: `jiadian/info/`+id,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.jiadianForm = data.data;
        }
      });
    },

    // 用户选择变化事件
    yonghuChange(id) {
      this.$http({
        url: `yonghu/info/`+id,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.yonghuForm = data.data;
        }
      });
    },

    // 获取详情信息
    info(id) {
      this.$http({
        url: `jiadianCollection/info/${id}`,
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.ruleForm = data.data;
          this.jiadianChange(data.data.jiadianId);  // 加载商品信息
          this.yonghuChange(data.data.yonghuId);    // 加载用户信息
        } else {
          this.$message.error(data.msg);
        }
      });
    },

    // 表单提交
    onSubmit() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: `jiadianCollection/${!this.ruleForm.id ? "save" : "update"}`,
            method: "post",
            data: this.ruleForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.parent.showFlag = true;
                  this.parent.addOrUpdateFlag = false;
                  this.parent.jiadianCollectionCrossAddOrUpdateFlag = false;
                  this.parent.search();
                }
              });
            } else {
              this.$message.error(data.msg);
            }
          });
        }
      });
    },

    // 返回列表
    back() {
      this.parent.showFlag = true;
      this.parent.addOrUpdateFlag = false;
      this.parent.jiadianCollectionCrossAddOrUpdateFlag = false;
    }
  }
};
</script>

<style scoped>
/* 表单主容器样式 */
.addEdit-block {
  margin: -10px;
}

/* 表单内容样式 */
.detail-form-content {
  padding: 12px;
  background-color: transparent;
}

/* 按钮样式 */
.btn .el-button {
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 10px;
  margin-right: 10px;
}

/* 主要按钮样式 */
.btn .el-button--primary {
  background-color: var(--publicMainColor);
  border-color: var(--publicMainColor);
  color: #fff;
}

/* 默认按钮样式 */
.btn .el-button--default {
  background-color: #ecf5ff;
  border-color: var(--publicSubColor);
  color: var(--publicSubColor);
}

/* 图片样式 */
.addEdit-block img {
  margin-right: 20px;
  border: 1px solid #eee;
}
</style>