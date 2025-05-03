<template>
  <!-- 添加/编辑表单的主容器 -->
  <div class="addEdit-block">
    <!-- 使用Element UI的表单组件 -->
    <el-form
        class="detail-form-content"
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="auto">
      <el-row>
        <!-- 商品名称显示列 -->
        <el-col :span="12" v-if="sessionTable !='jiadian'">
          <el-form-item class="input" v-if="type!='info'" label="商品名称" prop="jiadianName" >
            <el-input
                v-model="jiadianForm.jiadianName"
                placeholder="商品名称"
                clearable
                readonly></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="商品名称" prop="jiadianName">
              <el-input
                  v-model="ruleForm.jiadianName"
                  placeholder="商品名称"
                  readonly></el-input>
            </el-form-item>
          </div>
        </el-col>

        <!-- 用户姓名显示列 -->
        <el-col :span="12" v-if="sessionTable !='yonghu'">
          <el-form-item class="input" v-if="type!='info'" label="用户姓名" prop="yonghuName">
            <el-input
                v-model="yonghuForm.yonghuName"
                placeholder="用户姓名"
                clearable
                readonly></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item class="input" label="用户姓名" prop="yonghuName">
              <el-input
                  v-model="ruleForm.yonghuName"
                  placeholder="用户姓名"
                  readonly></el-input>
            </el-form-item>
          </div>
        </el-col>

        <!-- 评价内容列 -->
        <el-col :span="24">
          <el-form-item v-if="type!='info'" label="评价内容" prop="jiadianCommentbackText">
            <el-input
                type="textarea"
                readonly="readonly"
                v-model="ruleForm.jiadianCommentbackText"
                placeholder="评价内容"
                ></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item v-if="ruleForm.jiadianCommentbackText" label="评价内容" prop="jiadianCommentbackText">
              <span v-html="ruleForm.jiadianCommentbackText"></span>
            </el-form-item>
          </div>
        </el-col>

        <!-- 回复内容列 -->
        <el-col :span="24">
          <el-form-item v-if="type!='info'" label="回复内容" prop="replyText">
            <el-input
                type="textarea"
                :readonly="false"
                v-model="ruleForm.replyText"
                placeholder="回复内容"></el-input>
          </el-form-item>
          <div v-else-if="type=='info'">
            <el-form-item v-if="ruleForm.replyText" label="回复内容" prop="replyText">
              <span v-html="ruleForm.replyText"></span>
            </el-form-item>
          </div>
        </el-col>
      </el-row>

      <!-- 表单按钮区域 -->
      <el-form-item class="btn">
        <el-button v-if="type!='info'" type="primary" class="btn-success" @click="onSubmit">提交</el-button>
        <el-button v-if="type!='info'" class="btn-close" @click="back()">取消</el-button>
        <el-button v-if="type=='info'" class="btn-close" @click="back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

export default {
  data() {
    return {
      id: '', // 当前记录ID
      type: '', // 操作类型：'info'=查看，'update'=修改，'add'=添加
      sessionTable: "", // 登录账户所在表名
      role: "", // 权限
      userId: "", // 当前登录人的id

      // 关联表表单数据
      jiadianForm: {}, // 商品表单数据
      yonghuForm: {}, // 用户表单数据

      // 表单数据对象
      ruleForm: {
        jiadianId: '', // 商品ID
        yonghuId: '', // 用户ID
        jiadianCommentbackText: '', // 评价内容
        insertTime: '', // 评价时间
        replyText: '', // 回复内容
        updateTime: '', // 回复时间
      },

      // 下拉选项数据
      jiadianOptions: [], // 商品下拉选项
      yonghuOptions: [], // 用户下拉选项

      // 表单验证规则
      rules: {
        replyText: [
          { required: true, message: '回复内容不能为空', trigger: 'blur' },
        ],
      }
    };
  },
  props: ["parent"], // 父组件引用
  computed: {},
  created() {
    // 获取当前登录用户的信息
    this.sessionTable = this.$storage.get("sessionTable");
    this.role = this.$storage.get("role");
    this.userId = this.$storage.get("userId");

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

      // 如果是查看或编辑模式，获取详情
      if(this.type == 'info' || this.type == 'else') {
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

    // 获取详情
    info(id) {
      let _this = this;
      _this.$http({
        url: `jiadianCommentback/info/${id}`,
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          _this.ruleForm = data.data;
          _this.jiadianChange(data.data.jiadianId);
          _this.yonghuChange(data.data.yonghuId);
        } else {
          _this.$message.error(data.msg);
        }
      });
    },

    // 表单提交
    onSubmit() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          this.$http({
            url: `jiadianCommentback/${!this.ruleForm.id ? "save" : "update"}`,
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
                  this.parent.jiadianCommentbackCrossAddOrUpdateFlag = false;
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

    // 获取UUID
    // getUUID() {
    //   return new Date().getTime();
    // },

    // 返回列表
    back() {
      this.parent.showFlag = true;
      this.parent.addOrUpdateFlag = false;
      this.parent.jiadianCommentbackCrossAddOrUpdateFlag = false;
    }
  }
};
</script>

<style scoped>
/* 表单容器样式 */
.addEdit-block {
  margin: -10px;
}

/* 表单内容区域样式 */
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

</style>