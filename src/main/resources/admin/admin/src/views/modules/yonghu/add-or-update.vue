<template>
  <div class="addEdit-block">
    <!-- 使用 Element UI 的表单组件，设置标签宽度为自动 -->
    <el-form
        class="detail-form-content"
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="auto">

      <!-- 使用 Element UI 的行组件，用于布局 -->
      <el-row>
        <!-- 隐藏的ID字段 -->
<!--        <input id="updateId" name="id" type="hidden">-->

        <!-- 账户信息部分，占12列 -->
        <el-col :span="12">
          <el-form-item label="账户" prop="username">
            <el-input
                v-model="ruleForm.username"
                placeholder="账户"
                :readonly="type=='info'">
            </el-input>
          </el-form-item>
        </el-col>

        <!-- 用户姓名部分，占12列 -->
        <el-col :span="12">
          <el-form-item label="用户姓名" prop="yonghuName">
            <el-input
                v-model="ruleForm.yonghuName"
                placeholder="用户姓名"
                :readonly="type=='info'">
            </el-input>
          </el-form-item>
        </el-col>

        <!-- 联系方式部分，占12列 -->
        <el-col :span="12">
          <el-form-item label="联系方式" prop="yonghuPhone">
            <el-input
                v-model="ruleForm.yonghuPhone"
                placeholder="联系方式"
                :readonly="type=='info'">
            </el-input>
          </el-form-item>
        </el-col>

        <!-- 用户头像部分，占12列 -->
        <el-col :span="12">
          <el-form-item label="用户头像" prop="yonghuPhoto">
            <!-- 查看模式下显示图片 -->
            <div v-if="type=='info' && ruleForm.yonghuPhoto">
              <img
                  :src="$base.url+ruleForm.yonghuPhoto"
                  width="100"
                  height="100">
            </div>
            <!-- 编辑模式下显示上传组件 -->
            <file-upload
                v-else-if="type!='info'"
                tip="点击上传用户头像"
                action="file/upload"
                :limit="1"
                :fileUrls="ruleForm.yonghuPhoto?$base.url+ruleForm.yonghuPhoto:''"
                @change="yonghuPhotoUploadChange">
            </file-upload>
          </el-form-item>
        </el-col>

        <!-- 性别部分，占12列 -->
        <el-col :span="12">
          <el-form-item label="性别" prop="sexTypes">
            <!-- 查看模式下显示文本 -->
            <el-input
                v-if="type=='info'"
                v-model="ruleForm.sexValue"
                placeholder="性别"
                readonly>
            </el-input>
            <!-- 编辑模式下显示下拉选择 -->
            <!--  type==info：为查看模式  :disabled="type==='info'"：type为info时禁用，为查看模式时禁用下拉框-->
            <el-select
                v-else
                v-model="ruleForm.sexTypes"
                :disabled="type==='info'"
                placeholder="请选择性别">
              <el-option
                  v-for="(item,index) in sexTypesOptions"
                  :key="item.codeIndex"
                  :label="item.indexName"
                  :value="item.codeIndex">
              </el-option>
            </el-select>
          </el-form-item>
        </el-col>

        <!-- 电子邮箱部分，占12列 -->
        <el-col :span="12">
          <el-form-item label="电子邮箱" prop="yonghuEmail">
            <el-input
                v-model="ruleForm.yonghuEmail"
                placeholder="电子邮箱"
                :readonly="type=='info'">
            </el-input>
          </el-form-item>
        </el-col>

        <!-- 余额部分，占12列 -->
        <el-col :span="12">
          <el-form-item label="余额" prop="newMoney">
            <el-input
                v-model="ruleForm.newMoney"
                placeholder="余额"
                :readonly="type=='info' ">
            </el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 表单操作按钮 -->
      <el-form-item class="btn">
        <!-- 提交按钮（非查看模式显示） -->
        <el-button
            v-if="type!='info'"
            type="primary"
            class="btn-success"
            @click="onSubmit">
          提交
        </el-button>

        <!-- 返回/取消按钮 -->
        <el-button
            class="btn-close"
            @click="back()">
          {{type=='info' ? '返回' : '取消'}}
        </el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

export default {
  data() {
    return {
      id: '',         // 当前记录ID
      type: '',       // 操作类型：info查看，update修改，add添加

      // 用户信息
      sessionTable: "", // 当前登录用户表名
      role: "",       // 用户角色
      userId: "",     // 用户ID

      // 只读控制
      // ro: {
      //   username: false,
      //   password: false,
      //   yonghuName: false,
      //   yonghuPhone: false,
      //   yonghuPhoto: false,
      //   sexTypes: false,
      //   yonghuEmail: false,
      //   newMoney: false,
      // },

      // 表单数据
      ruleForm: {
        username: '',      // 账户
        password: '',      // 密码
        yonghuName: '',    // 用户姓名
        yonghuPhone: '',   // 联系方式
        yonghuPhoto: '',   // 用户头像
        sexTypes: '',      // 性别
        yonghuEmail: '',   // 电子邮箱
        newMoney: '',      // 余额
      },

      // 性别选项
      sexTypesOptions: [],

      // 表单验证规则
      rules: {
        username: [
          { required: true, message: '账户不能为空', trigger: 'blur' },
        ],
        password: [
          { required: true, message: '密码不能为空', trigger: 'blur' },
        ],
        yonghuName: [
          { required: true, message: '用户姓名不能为空', trigger: 'blur' },
        ],
        yonghuPhone: [
          { required: true, message: '联系方式不能为空', trigger: 'blur' },
          { pattern: /^(13[0-9]|14[01456879]|15[0-35-9]|16[2567]|17[0-8]|18[0-9]|19[0-35-9])\d{8}$/,
            message: '联系方式格式不对',
            trigger: 'blur'
          }
        ],
        yonghuPhoto: [
          { required: true, message: '用户头像不能为空', trigger: 'blur' },
        ],
        sexTypes: [
          { required: true, message: '性别不能为空', trigger: 'blur' },
          { pattern: /^[0-9]*$/,
            message: '只允许输入整数',
            trigger: 'blur'
          }
        ],
        yonghuEmail: [
          { required: true, message: '电子邮箱不能为空', trigger: 'blur' },
          { pattern: /^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+(.[a-zA-Z0-9_-])+/,
            message: '电子邮箱只能是邮箱格式',
            trigger: 'blur'
          }
        ],
        newMoney: [
          { required: true, message: '余额不能为空', trigger: 'blur' },
          { pattern: /^[0-9]{0,6}(\.[0-9]{1,2})?$/,
            message: '只允许输入整数6位,小数2位的数字',
            trigger: 'blur'
          }
        ],
      }
    };
  },
  props: ["parent"], // 接收父组件引用
  created() {
    // 获取当前登录用户信息
    this.sessionTable = this.$storage.get("sessionTable");
    this.role = this.$storage.get("role");
    this.userId = this.$storage.get("userId");

    // 非管理员角色设置只读字段
    if (this.role != "管理员") {
      // 可以根据需要设置某些字段为只读
    }

    // 获取性别下拉选项
    this.$http({
      url: `dictionary/page?page=1&limit=100&sort=&order=&dicCode=sex_types`,
      method: "get"
    }).then(({ data }) => {
      if (data && data.code === 0) {
        this.sexTypesOptions = data.data.list;
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

      // 如果是查看或编辑模式，加载数据
      if(this.type=='info' || this.type=='else'){
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

    // 加载详情数据
    info(id) {
      this.$http({
        url: `yonghu/info/${id}`,
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.ruleForm = data.data;
        } else {
          this.$message.error(data.msg);
        }
      });
    },

    // 表单提交
    onSubmit() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          // 根据是否有ID决定是新增还是更新
          const url = !this.ruleForm.id ? "yonghu/save" : "yonghu/update";

          this.$http({
            url: url,
            method: "post",
            data: this.ruleForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  // 返回列表页并刷新
                  this.parent.showFlag = true;
                  this.parent.addOrUpdateFlag = false;
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

    // 返回列表页
    back() {
      this.parent.showFlag = true;
      this.parent.addOrUpdateFlag = false;
    },

    // 图片上传回调
    yonghuPhotoUploadChange(fileUrls) {
      this.ruleForm.yonghuPhoto = fileUrls;
    }
  }
};
</script>

<style scoped>
.addEdit-block {
  padding: 20px;
}

.detail-form-content {
  background: #fff;
  padding: 20px;
  border-radius: 4px;
  box-shadow: 0 0 6px rgba(0,0,0,0.1);
}

/* 图片样式 */
img {
  margin-right: 10px;
  border: 1px solid #eee;
}

/* 按钮样式 */
.btn-success {
  background-color: var(--publicSubColor);
  border-color: var(--publicMainColor);
  color: #333;
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 10px;
  border-width: 1px;
}

.btn-close {
  background-color: #ecf5ff;
  border-color: var(--publicSubColor);
  color: var(--publicSubColor);
  padding: 10px 20px;
  font-size: 14px;
  border-radius: 10px;
  border-width: 1px;
}

/* 表单元素样式 */
.el-input, .el-select {
  width: 100%;
}

.el-form-item {
  margin-bottom: 15px;
}

</style>