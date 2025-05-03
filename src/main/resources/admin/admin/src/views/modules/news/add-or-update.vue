<template>
  <!-- 公告添加/编辑表单 -->
  <div class="addEdit-block">
    <el-form
        class="detail-form-content"
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="auto">

      <!-- 隐藏的ID字段 -->
      <input id="updateId" name="id" type="hidden">

      <el-row>
        <!-- 公告标题 -->
        <el-col :span="12">
          <el-form-item class="input" v-if="type!='info'" label="公告标题" prop="newsName">
            <el-input
                v-model="ruleForm.newsName"
                placeholder="请输入公告标题"
                clearable
                :readonly="ro.newsName">
            </el-input>
          </el-form-item>
          <el-form-item v-else class="input" label="公告标题" prop="newsName">
            <el-input
                v-model="ruleForm.newsName"
                placeholder="公告标题"
                readonly>
            </el-input>
          </el-form-item>
        </el-col>

        <!-- 公告类型 -->
        <el-col :span="12">
          <el-form-item class="select" v-if="type!='info'" label="公告类型" prop="newsTypes">
            <el-select
                v-model="ruleForm.newsTypes"
                :disabled="ro.newsTypes"
                placeholder="请选择公告类型">
              <el-option
                  v-for="(item,index) in newsTypesOptions"
                  :key="item.codeIndex"
                  :label="item.indexName"
                  :value="item.codeIndex">
              </el-option>
            </el-select>
          </el-form-item>
          <el-form-item v-else class="input" label="公告类型" prop="newsValue">
            <el-input
                v-model="ruleForm.newsValue"
                placeholder="公告类型"
                readonly>
            </el-input>
          </el-form-item>
        </el-col>

        <!-- 公告图片 -->
        <el-col :span="24">
          <el-form-item class="upload" v-if="type!='info' && !ro.newsPhoto" label="公告图片" prop="newsPhoto">
            <file-upload
                tip="点击上传公告图片"
                action="file/upload"
                :limit="3"
                :multiple="true"
                :fileUrls="ruleForm.newsPhoto?$base.url+ruleForm.newsPhoto:''"
                @change="newsPhotoUploadChange">
            </file-upload>
          </el-form-item>
          <el-form-item v-else-if="ruleForm.newsPhoto" label="公告图片" prop="newsPhoto">
            <img
                style="margin-right:20px;"
                v-for="(item,index) in (ruleForm.newsPhoto || '').split(',')"
                :key="index"
                :src="$base.url+item"
                width="100"
                height="100">
          </el-form-item>
        </el-col>

        <!-- 公告详情 -->
        <el-col :span="24">
          <el-form-item v-if="type!='info'" label="公告详情" prop="newsContent">
            <editor
                style="min-width: 200px; max-width: 600px;"
                v-model="ruleForm.newsContent"
                class="editor"
                action="file/upload">
            </editor>
          </el-form-item>
          <el-form-item v-else-if="ruleForm.newsContent" label="公告详情" prop="newsContent">
            <div v-html="ruleForm.newsContent" style="border:1px solid #eee;padding:10px;"></div>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 操作按钮 -->
      <el-form-item class="btn">
        <el-button v-if="type!='info'" type="primary" @click="onSubmit">提交</el-button>
        <el-button v-if="type!='info'" @click="back()">取消</el-button>
        <el-button v-if="type=='info'" @click="back()">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      id: '',           // 当前公告ID
      type: '',         // 操作类型：info(查看)、add(添加)、update(修改)
      sessionTable: "", // 当前登录用户表名
      role: "",         // 用户角色
      userId: "",       // 用户ID

      // 只读控制
      ro: {
        newsName: false,    // 公告标题是否只读
        newsTypes: false,   // 公告类型是否只读
        newsPhoto: false,   // 公告图片是否只读
        newsContent: false, // 公告内容是否只读
      },

      // 表单数据
      ruleForm: {
        newsName: '',     // 公告标题
        newsTypes: '',    // 公告类型
        newsPhoto: '',    // 公告图片
        newsContent: '',  // 公告内容
        newsValue: '',    // 公告类型显示值
      },

      // 公告类型选项
      newsTypesOptions: [],

      // 表单验证规则
      rules: {
        newsName: [
          { required: true, message: '公告标题不能为空', trigger: 'blur' }
        ],
        newsTypes: [
          { required: true, message: '公告类型不能为空', trigger: 'blur' },
          { pattern: /^[0-9]*$/, message: '只允许输入整数', trigger: 'blur' }
        ],
        newsPhoto: [
          { required: true, message: '公告图片不能为空', trigger: 'blur' }
        ],
        newsContent: [
          { required: true, message: '公告详情不能为空', trigger: 'blur' }
        ]
      }
    };
  },
  props: ["parent"],  // 父组件引用
  created() {
    // 获取当前登录用户信息
    this.sessionTable = this.$storage.get("sessionTable");
    this.role = this.$storage.get("role");
    this.userId = this.$storage.get("userId");

    // 获取公告类型下拉选项
    this.getNewsTypesOptions();
  },
  methods: {
    // 获取公告类型下拉选项
    getNewsTypesOptions() {
      this.$http({
        url: `dictionary/page?page=1&limit=100&sort=&order=&dicCode=news_types`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.newsTypesOptions = data.data.list;
        }
      });
    },

    // 初始化方法
    init(id, type) {
      if (id) {
        this.id = id;
        this.type = type;
      }

      // 如果是查看或修改，获取详情数据
      if(this.type == 'info' || this.type == 'else'){
        this.getInfo(id);
      }

      // 获取用户信息
      this.getUserInfo();
    },

    // 获取用户信息
    getUserInfo() {
      this.$http({
        url: `${this.$storage.get("sessionTable")}/session`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code !== 0) {
          this.$message.error(data.msg);
        }
      });
    },

    // 获取公告详情
    getInfo(id) {
      this.$http({
        url: `news/info/${id}`,
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.ruleForm = data.data;
          // 处理富文本中的图片路径
          this.ruleForm.newsContent = this.ruleForm.newsContent.replaceAll(
              "src=\"upload/",
              `src=\"${this.$base.url}upload/`
          );
        } else {
          this.$message.error(data.msg);
        }
      });
    },

    // 提交表单
    onSubmit() {
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          // 处理富文本中的图片路径
          this.ruleForm.newsContent = this.ruleForm.newsContent.replaceAll(this.$base.url, "");

          // 根据是否有ID决定是新增还是修改
          const url = `news/${!this.ruleForm.id ? "save" : "update"}`;

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
                  // 操作完成后返回列表页并刷新
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
    newsPhotoUploadChange(fileUrls) {
      this.ruleForm.newsPhoto = fileUrls;
    },

    // 生成UUID
    getUUID() {
      return new Date().getTime();
    }
  }
};
</script>

<style scoped>
/* 表单容器样式 */
.addEdit-block {
  padding: 20px;
}

.detail-form-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 6px;
  box-shadow: 0 0 6px rgba(0,0,0,0.1);
}

/* 表单元素样式 */
.addEdit-block .el-form-item {
  margin-bottom: 20px;
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

/* 富文本编辑器样式
.editor {
  height: 500px;
  border: 1px solid #dcdfe6;
  border-radius: 4px;
}

.editor /deep/ .ql-container {
  height: 400px;
}*/

</style>