<template>
  <div>
    <!-- 图片上传组件（隐藏） -->
    <!-- 用于富文本编辑器中的图片上传功能 -->
<!--    name="file" 上传文件的参数名-->
<!--    :headers="header" 请求头（含Token）-->
<!--    :show-file-list="false" 不显示文件列表-->
<!--    :on-success="uploadSuccess" 上传成功回调-->
<!--    :on-error="uploadError"  上传失败回调-->
<!--    :before-upload="beforeUpload" 上传前处理-->
    <el-upload
        class="avatar-uploader"
        :action="getActionUrl"
    name="file"
    :headers="header"
    :show-file-list="false"
    :on-success="uploadSuccess"
    :on-error="uploadError"
    :before-upload="beforeUpload"
    ></el-upload>

    <!-- 富文本编辑器主体 -->
<!--    v-model="content"       // 双向绑定编辑器内容-->
<!--    ref="myQuillEditor"     // 获取编辑器实例的引用-->
<!--    :options="editorOption" // 编辑器配置-->
<!--    @blur="onEditorBlur($event)"   // 失去焦点事件-->
<!--    @focus="onEditorFocus($event)" // 获取焦点事件-->
<!--    @change="onEditorChange($event)" // 内容变化事件-->
    <quill-editor
        class="editor"
        v-model="content"
    ref="myQuillEditor"
    :options="editorOption"
    @blur="onEditorBlur($event)"
    @focus="onEditorFocus($event)"
    @change="onEditorChange($event)"
    ></quill-editor>
  </div>
</template>

<script>
// 导入富文本编辑器相关组件和样式
import { quillEditor } from "vue-quill-editor";
import "quill/dist/quill.core.css";
import "quill/dist/quill.snow.css";
import "quill/dist/quill.bubble.css";

export default {
  // 组件接收的props
  props: {
    value: {  // 编辑器内容（v-model绑定）
      type: String
    },
    action: { // 图片上传接口路径
      type: String
    },
    maxSize: { // 图片大小限制（默认4MB）
      type: Number,
      default: 4000
    }
  },

  // 注册子组件
  components: {
    quillEditor
  },

  // 组件数据
  data() {
    return {
      content: "", // 编辑器内容
      quillUpdateImg: false, // 图片上传状态标记
      editorOption: { // 编辑器配置
        placeholder: "", // 占位文本
        theme: "snow",  // 主题样式
        modules: {
          toolbar: false // 禁用工具栏（设为false表示不显示工具栏）
        }
      },
      header: { // 上传请求头
        'Token': this.$storage.get("Token") // 从本地存储获取Token
      }
    };
  },

  // 计算属性
  computed: {
    // 构造完整的图片上传URL
    getActionUrl: function() {
      this.setContent(this.value); // 同步内容
      return `/${this.$base.name}/` + this.action; // 拼接基础路径和接口路径
    }
  },

  // 方法
  methods: {
    // 设置编辑器内容
    setContent(value) {
      this.content = value;
    },

    // 编辑器失去焦点事件（空实现，可按需扩展）
    onEditorBlur() {},

    // 编辑器获取焦点事件（空实现，可按需扩展）
    onEditorFocus() {},

    // 编辑器内容变化事件
    onEditorChange() {
      this.$emit("input", this.content); // 触发v-model更新
    },

    // 图片上传前的处理
    beforeUpload() {
      this.quillUpdateImg = true; // 标记上传开始
    },

    // 图片上传成功回调
    uploadSuccess(res, file) {
      let quill = this.$refs.myQuillEditor.quill; // 获取编辑器实例
      if (res.code === 0) { // 上传成功
        let length = quill.getSelection().index; // 获取当前光标位置
        // 在光标处插入图片
        quill.insertEmbed(length, "image", this.$base.url + "upload/" + res.file);
        quill.setSelection(length + 1); // 移动光标到图片后
      } else {
        this.$message.error("图片插入失败"); // 错误提示
      }
      this.quillUpdateImg = false; // 标记上传结束
    },

    // 图片上传失败回调
    uploadError() {
      this.quillUpdateImg = false; // 标记上传结束
      this.$message.error("图片插入失败"); // 错误提示
    }
  }
};
</script>

<style>
/* 编辑器基础样式 */
.editor {
  line-height: normal !important; /* 重置行高 */
}

/* 设置编辑器容器高度 */
.ql-container {
  height: 400px; /* 固定高度，可调整 */
}
</style>