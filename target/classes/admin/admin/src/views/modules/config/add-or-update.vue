<template>
  <!-- 定义一个包含表单的容器块 -->
  <div class="addEdit-block">
    <!-- 使用 Element UI 的表单组件 -->
    <el-form
        class="detail-form-content"
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="80px"
    >
      <!-- 使用 Element UI 的行组件 -->
      <el-row>
        <!-- 使用 Element UI 的列组件，占据 12 列 -->
        <el-col :span="12">
          <!-- 当 type 不等于 'info' 时显示此表单项 -->
          <el-form-item class="input" v-if="type!='info'" label="名称" prop="name">
            <!-- 绑定输入框的值到 ruleForm.name，设置占位符和清空按钮 -->
            <el-input v-model="ruleForm.name"
                      placeholder="名称" clearable :readonly="ro.name"></el-input>
          </el-form-item>
          <!-- 当 type 等于 'info' 时显示此表单项 -->
          <el-form-item v-else class="input" label="名称" prop="name">
            <!-- 绑定输入框的值到 ruleForm.name，设置为只读 -->
            <el-input v-model="ruleForm.name"
                      placeholder="名称" readonly></el-input>
          </el-form-item>
        </el-col>
        <!-- 使用 Element UI 的列组件，占据 24 列 -->
        <el-col :span="24">
          <!-- 当 type 不等于 'info' 且 ro.value 为 false 时显示此表单项 -->
          <el-form-item class="upload" v-if="type!='info' && !ro.value" label="轮播图片" prop="value">
            <!-- 自定义的文件上传组件 -->
            <!--tip="点击上传图片"   上传提示文字 -->
            <!--action="file/upload"    上传接口地址 -->
            <!--:limit="1"              最多上传1个文件 -->
            <!--:multiple="false"       不允许多文件上传 -->
            <!--:fileUrls="ruleForm.value?$base.url+ruleForm.value:''"   已上传文件的URL -->
            <!--@change="valueUploadChange"   上传状态改变的回调 -->
            <file-upload
                tip="点击上传图片"
                action="file/upload"
                :limit="1"
                :multiple="false"
                :fileUrls="ruleForm.value?$base.url+ruleForm.value:''"
                @change="valueUploadChange"
            ></file-upload>
          </el-form-item>
          <!-- 当 ruleForm.value 存在时显示此表单项 -->
          <el-form-item v-else-if="ruleForm.value" label="轮播图片" prop="value">
            <!-- 循环显示上传的图片 -->
            <img
                style="margin-right:20px;"
                v-for="(item,index) in ruleForm.value.split(',')"
                :key="index"
                :src="$base.url+item"
                width="100"
                height="100">
          </el-form-item>
        </el-col>
      </el-row>
      <!-- 表单按钮区域 -->
      <el-form-item class="btn">
        <!-- 当 type 不等于 'info' 时显示提交按钮 -->
        <el-button v-if="type!='info'" type="primary" @click="onSubmit">提交</el-button>
        <!-- 根据 type 的值显示不同的按钮文本 -->
        <el-button @click="back()">{{type=='info'?'返回':'取消'}}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>

export default {
  // 定义组件的数据
  data() {
    return {
      id: '', // 存储数据的 ID
      type: '', // 存储操作类型
      ro: {
        name: false, // 控制名称输入框是否只读
        value: false, // 控制图片上传是否可用
      },
      ruleForm: {
        name: '', // 存储名称输入框的值
        value: '', // 存储上传图片的 URL
      },
      rules: {
        name: [
          // 名称字段验证规则，不能为空
          { required: true, message: '名称不能为空', trigger: 'blur' },
        ],
        value: [], // 图片字段的验证规则，这里为空
      }
    };
  },
  // 接收父组件传递的属性
  props: ["parent"],
  // 定义组件的方法
  methods: {
    // 初始化方法，根据 id 和 type 进行不同的操作
    init(id, type) {
      if (id) {
        this.id = id;
        this.type = type;
      }
      // 当 type 为 'info' 或 'else' 时，调用 info 方法获取详情
      if(this.type=='info' || this.type=='else'){
        this.info(id);
      }
      // 当 type 为 'cross' 时，从本地存储中获取数据
      else if(this.type=='cross'){
        var obj = this.$storage.getObj('crossObj');
        for (var o in obj){
          if(o=='name'){
            this.ruleForm.name = obj[o];
            this.ro.name = true; // 设置名称输入框为只读
            continue;
          }
          if(o=='value'){
            this.ruleForm.value = obj[o];
            this.ro.value = true; // 设置图片上传不可用
            continue;
          }
        }
      }
    },
    // 获取详情的方法
    info(id) {
      // 发送 HTTP 请求获取数据详情
      this.$http({
        url: `config/info/${id}`,
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.ruleForm = data.data; // 将获取的数据赋值给 ruleForm
          let reg = new RegExp('../../../upload','g');
        } else {
          this.$message.error(data.msg); // 显示错误消息
        }
      });
    },
    // 提交表单的方法
    onSubmit() {
      // 验证表单数据
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          // 发送 HTTP 请求保存或更新数据
          this.$http({
            url: `config/${!this.ruleForm.id ? "save" : "update"}`,
            method: "post",
            data: this.ruleForm
          }).then(({ data }) => {
            if (data && data.code === 0) {
              // 显示成功消息，并在消息关闭后执行一些操作
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.parent.showFlag = true;
                  this.parent.addOrUpdateFlag = false;
                  this.parent.search();
                }
              });
            } else {
              this.$message.error(data.msg); // 显示错误消息
            }
          });
        }
      });
    },
    // 返回的方法
    back() {
      this.parent.showFlag = true;
      this.parent.addOrUpdateFlag = false;
    },
    // 图片上传回调方法
    valueUploadChange(fileUrls) {
      this.ruleForm.value = fileUrls; // 更新上传图片的 URL
    },
  }
};
</script>

<style scoped>
/* 定义表单容器块的样式 */
.addEdit-block {
  padding: 20px;
}

/* 定义表单内容的样式 */
.detail-form-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 6px;
  box-shadow: 0 0 6px rgba(0,0,0,0.1);
}

/* 输入框样式，这里注释掉了，可以根据需要取消注释
.input >>> .el-input__inner {
  height: 36px;
  line-height: 36px;
  color: #606266;
  font-size: 14px;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
  background-color: #fff;
}

.input >>> .el-form-item__label {
  color: #606266;
  font-size: 14px;
}*/

/* 上传组件样式，这里注释掉了，可以根据需要取消注释
.upload >>> .el-upload--picture-card {
  width: 148px;
  height: 148px;
  line-height: 146px;
  border: 1px dashed #DCDFE6;
  border-radius: 4px;
  background-color: #fff;
}

.upload >>> .el-upload-list--picture-card .el-upload-list__item {
  width: 148px;
  height: 148px;
  border: 1px solid #DCDFE6;
  border-radius: 4px;
}

.upload >>> .el-upload-list__item-actions {
  line-height: 146px;
}*/

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

/* 图片预览样式 */
.el-form-item img {
  vertical-align: top;
  border: 1px solid #eee;
  margin-right: 10px;
}
</style>