<template>
  <!-- 添加/编辑表单的主容器 -->
  <div class="addEdit-block">
    <!-- 使用Element UI的表单组件 -->
<!--    class="detail-form-content"   表单内容样式类 -->
<!--    ref="ruleForm"               表单引用，用于表单验证 -->
<!--    :model="ruleForm"            绑定表单数据对象 -->
<!--    :rules="rules"               绑定表单验证规则 -->
<!--    label-width="auto"           标签宽度自动调整 -->
    <el-form
        class="detail-form-content"
    ref="ruleForm"
    :model="ruleForm"
    :rules="rules"
    label-width="auto"
    >
    <!-- 使用Element UI的栅格布局 -->
    <el-row>
      <!-- 隐藏的ID输入框，用于提交表单时传递ID -->
      <input id="updateId" name="id" type="hidden">

      <!-- 第一列：商品名称 -->
      <el-col :span="12">  <!-- 占据12列（24列栅格系统的一半） -->
        <!-- 非查看模式下的商品名称输入框 -->
        <!--v-if="type!='info'"   如果不是查看模式则显示 -->
        <!--prop="jiadianName"   用于表单验证的字段名 -->
        <el-form-item
            class="input"
            v-if="type!='info'"
        label="商品名称"
        prop="jiadianName"
        >
          <!--v-model="ruleForm.jiadianName"   双向绑定商品名称 -->
          <!--placeholder="商品名称"            输入框提示文字 -->
          <!--clearable                        可清空内容 -->
          <!--:readonly="ro.jiadianName"       根据ro对象决定是否只读 -->
        <el-input
            v-model="ruleForm.jiadianName"
        placeholder="商品名称"
        clearable
        :readonly="ro.jiadianName"
        ></el-input>
        </el-form-item>

        <!-- 查看模式下的商品名称显示 -->
        <div v-else-if="type=='info'">  <!-- 如果是查看模式则显示 -->
          <el-form-item class="input" label="商品名称" prop="jiadianName">
            <el-input
                v-model="ruleForm.jiadianName"
                placeholder="商品名称"
                readonly
            ></el-input>
          </el-form-item>
        </div>
      </el-col>

      <!-- 第二列：商品编号 -->
      <el-col :span="12">
        <!-- 非查看模式下的商品编号输入框 -->
        <el-form-item class="input" v-if="type!='info'" label="商品编号" prop="jiadianUuidNumber">
          <el-input
              v-model="ruleForm.jiadianUuidNumber"
              placeholder="商品编号"
              clearable
              :readonly="ro.jiadianUuidNumber"
          ></el-input>
        </el-form-item>

        <!-- 查看模式下的商品编号显示 -->
        <div v-else-if="type=='info'">
          <el-form-item class="input" label="商品编号" prop="jiadianUuidNumber">
            <el-input
                v-model="ruleForm.jiadianUuidNumber"
                placeholder="商品编号"
                readonly
            ></el-input>
          </el-form-item>
        </div>
      </el-col>

      <!-- 第三列：商品照片 -->
      <el-col :span="12">
        <!-- 非查看模式且非只读状态下的图片上传组件 -->
        <el-form-item
            class="upload"
            v-if="type!='info' && !ro.jiadianPhoto"
            label="商品照片"
            prop="jiadianPhoto"
        >
          <!-- 文件上传组件 -->
          <!--tip="点击上传商品照片"   上传提示文字 -->
          <!--action="file/upload"    上传接口地址 -->
          <!--:limit="1"              最多上传1个文件 -->
          <!--:multiple="false"       不允许多文件上传 -->
          <!--:fileUrls="ruleForm.jiadianPhoto?$base.url+ruleForm.jiadianPhoto:''"   已上传文件的URL -->
          <!--@change="jiadianPhotoUploadChange"   上传状态改变的回调 -->
          <file-upload
              tip="点击上传商品照片"
          action="file/upload"
          :limit="1"
          :multiple="false"
          :fileUrls="ruleForm.jiadianPhoto?$base.url+ruleForm.jiadianPhoto:''"
          @change="jiadianPhotoUploadChange"
          ></file-upload>
        </el-form-item>

        <!-- 查看模式或只读状态下的图片显示 -->
        <div v-else>
          <!-- 如果有商品照片则显示 -->
          <el-form-item v-if="ruleForm.jiadianPhoto" label="商品照片" prop="jiadianPhoto">
            <!-- 遍历图片URL -->
            <!--:src="$base.url+item"   拼接完整的图片URL -->
            <img
                style="margin-right:20px;"
                v-bind:key="index"
                v-for="(item,index) in (ruleForm.jiadianPhoto || '').split(',')"
                :src="$base.url+item"
            width="100"
            height="100"
            >
          </el-form-item>
        </div>
      </el-col>

      <!-- 第四列：商品类型 -->
      <el-col :span="12">
        <!-- 非查看模式下的商品类型选择框 -->
        <!--v-model="ruleForm.jiadianTypes"   双向绑定商品类型 -->
        <!--:disabled="ro.jiadianTypes"        根据ro对象决定是否禁用 -->
        <!--placeholder="请选择商品类型"       选择框提示文字 -->
        <el-form-item class="select" v-if="type!='info'" label="商品类型" prop="jiadianTypes">
          <el-select
              v-model="ruleForm.jiadianTypes"
          :disabled="ro.jiadianTypes"
          placeholder="请选择商品类型"
          >
          <!-- 遍历商品类型选项 -->
          <el-option
              v-for="(item,index) in jiadianTypesOptions"
              :key="item.codeIndex"
          :label="item.indexName"
          :value="item.codeIndex"
          ></el-option>
          </el-select>
        </el-form-item>

        <!-- 查看模式下的商品类型显示 -->
        <div v-else-if="type=='info'">
          <el-form-item class="input" label="商品类型" prop="jiadianValue">
            <el-input
                v-model="ruleForm.jiadianValue"
                placeholder="商品类型"
                readonly
            ></el-input>
          </el-form-item>
        </div>
      </el-col>

      <!-- 第五列：商品库存 -->
      <el-col :span="12">
        <!-- 非查看模式下的库存输入框 -->
        <el-form-item class="input" v-if="type!='info'" label="商品库存" prop="jiadianKucunNumber">
          <el-input
              v-model="ruleForm.jiadianKucunNumber"
              placeholder="商品库存"
              clearable
              :readonly="ro.jiadianKucunNumber"
          ></el-input>
        </el-form-item>

        <!-- 查看模式下的库存显示 -->
        <div v-else-if="type=='info'">
          <el-form-item class="input" label="商品库存" prop="jiadianKucunNumber">
            <el-input
                v-model="ruleForm.jiadianKucunNumber"
                placeholder="商品库存"
                readonly
            ></el-input>
          </el-form-item>
        </div>
      </el-col>

      <!-- 第六列：商品原价 -->
      <el-col :span="12">
        <!-- 非查看模式下的原价输入框 -->
        <el-form-item class="input" v-if="type!='info'" label="商品原价" prop="jiadianOldMoney">
          <el-input
              v-model="ruleForm.jiadianOldMoney"
              placeholder="商品原价"
              clearable
              :readonly="ro.jiadianOldMoney"
          ></el-input>
        </el-form-item>

        <!-- 查看模式下的原价显示 -->
        <div v-else-if="type=='info'">
          <el-form-item class="input" label="商品原价" prop="jiadianOldMoney">
            <el-input
                v-model="ruleForm.jiadianOldMoney"
                placeholder="商品原价"
                readonly
            ></el-input>
          </el-form-item>
        </div>
      </el-col>

      <!-- 第七列：现价 -->
      <el-col :span="12">
        <!-- 非查看模式下的现价输入框 -->
        <el-form-item class="input" v-if="type!='info'" label="现价" prop="jiadianNewMoney">
          <el-input
              v-model="ruleForm.jiadianNewMoney"
              placeholder="现价"
              clearable
              :readonly="ro.jiadianNewMoney"
          ></el-input>
        </el-form-item>

        <!-- 查看模式下的现价显示 -->
        <div v-else-if="type=='info'">
          <el-form-item class="input" label="现价" prop="jiadianNewMoney">
            <el-input
                v-model="ruleForm.jiadianNewMoney"
                placeholder="现价"
                readonly
            ></el-input>
          </el-form-item>
        </div>
      </el-col>

      <!-- 第八列：商品介绍（全宽） -->
      <el-col :span="24">
        <!-- 非查看模式下的富文本编辑器 -->
        <el-form-item v-if="type!='info'" label="商品介绍" prop="jiadianContent">
          <!--style="min-width: 200px; max-width: 600px;"   编辑器最小和最大宽度 -->
          <!--v-model="ruleForm.jiadianContent"             双向绑定商品介绍内容 -->
          <!--action="file/upload"                          图片上传接口 -->
          <editor
              style="min-width: 200px; max-width: 600px;"
          v-model="ruleForm.jiadianContent"
          class="editor"
          action="file/upload"
          ></editor>
        </el-form-item>

        <!-- 查看模式下的商品介绍显示 -->
        <div v-else-if="type=='info'">
          <el-form-item v-if="ruleForm.jiadianContent" label="商品介绍" prop="jiadianContent">
            <!-- 使用v-html显示富文本内容 -->
            <span v-html="ruleForm.jiadianContent"></span>
          </el-form-item>
        </div>
      </el-col>
    </el-row>

    <!-- 表单按钮区域 -->
    <el-form-item class="btn">
      <!-- 非查看模式下的提交按钮 -->
      <el-button
          v-if="type!='info'"
          type="primary"
          class="btn-success"
          @click="onSubmit"
      >
      提交
      </el-button>

      <!-- 非查看模式下的取消按钮 -->
      <el-button
          v-if="type!='info'"
          class="btn-close"
          @click="back()"
      >
      取消
      </el-button>

      <!-- 查看模式下的返回按钮 -->
      <el-button
          v-if="type=='info'"
          class="btn-close"
          @click="back()"
      >
        返回
      </el-button>
    </el-form-item>
    </el-form>
  </div>
</template>

<script>

export default {
  data() {
    return {
      id: '',           // 当前商品的ID
      type: '',         // 操作类型：'info'=查看，'update'=修改，'add'=添加
      sessionTable: "", // 当前登录用户的表名
      role: "",         // 当前用户的角色
      userId: "",       // 当前用户的ID

      // 字段只读控制对象
      ro: {
        jiadianName: false,
        jiadianUuidNumber: false,
        jiadianPhoto: false,
        jiadianTypes: false,
        jiadianKucunNumber: false,
        jiadianOldMoney: false,
        jiadianNewMoney: false,
        // jiadianClicknum: false,
        jiadianContent: false,
        shangxiaTypes: false,
        jiadianDelete: false,
        insertTime: false,
      },

      // 表单数据对象
      ruleForm: {
        jiadianName: '',                     // 商品名称
        jiadianUuidNumber: new Date().getTime(), // 商品编号（默认使用时间戳）
        jiadianPhoto: '',                    // 商品照片
        jiadianTypes: '',                    // 商品类型
        jiadianKucunNumber: '',              // 商品库存
        jiadianOldMoney: '',                 // 商品原价
        jiadianNewMoney: '',                 // 现价
        // jiadianClicknum: '',                 // 商品热度
        jiadianContent: '',                  // 商品介绍
        shangxiaTypes: '',                   // 是否上架
        jiadianDelete: '',                   // 逻辑删除标志
        insertTime: '',                      // 录入时间
      },

      // 商品类型下拉选项
      jiadianTypesOptions: [],

      // 是否上架下拉选项
      shangxiaTypesOptions: [],

      // 表单验证规则
      rules: {
        jiadianName: [
          { required: true, message: '商品名称不能为空', trigger: 'blur' },
        ],
        jiadianUuidNumber: [
          { required: true, message: '商品编号不能为空', trigger: 'blur' },
        ],
        jiadianPhoto: [
          { required: true, message: '商品照片不能为空', trigger: 'blur' },
        ],
        jiadianTypes: [
          { required: true, message: '商品类型不能为空', trigger: 'blur' },
          { pattern: /^[0-9]*$/, message: '只允许输入整数', trigger: 'blur' }
        ],
        jiadianKucunNumber: [
          { required: true, message: '商品库存不能为空', trigger: 'blur' },
          { pattern: /^[0-9]*$/, message: '只允许输入整数', trigger: 'blur' }
        ],
        jiadianOldMoney: [
          { required: true, message: '商品原价不能为空', trigger: 'blur' },
          { pattern: /^[0-9]{0,6}(\.[0-9]{1,2})?$/, message: '只允许输入整数6位,小数2位的数字', trigger: 'blur' }
        ],
        jiadianNewMoney: [
          { required: true, message: '现价不能为空', trigger: 'blur' },
          { pattern: /^[0-9]{0,6}(\.[0-9]{1,2})?$/, message: '只允许输入整数6位,小数2位的数字', trigger: 'blur' }
        ],
        // jiadianClicknum: [
        //   { required: true, message: '商品热度不能为空', trigger: 'blur' },
        //   { pattern: /^[0-9]*$/, message: '只允许输入整数', trigger: 'blur' }
        // ],
        jiadianContent: [
          { required: true, message: '商品介绍不能为空', trigger: 'blur' },
        ],
      }
    };
  },
  props: ["parent"],  // 父组件引用
  computed: {},
  created() {
    // 获取当前登录用户的信息
    this.sessionTable = this.$storage.get("sessionTable");
    this.role = this.$storage.get("role");
    this.userId = this.$storage.get("userId");

    // 获取商品类型下拉选项
    this.$http({
      url: `dictionary/page?page=1&limit=100&sort=&order=&dicCode=jiadian_types`,
      method: "get"
    }).then(({ data }) => {
      if (data && data.code === 0) {
        this.jiadianTypesOptions = data.data.list;
      }
    });

    // 获取是否上架下拉选项
    this.$http({
      url: `dictionary/page?page=1&limit=100&sort=&order=&dicCode=shangxia_types`,
      method: "get"
    }).then(({ data }) => {
      if (data && data.code === 0) {
        this.shangxiaTypesOptions = data.data.list;
      }
    });
  },
  methods: {

    // 初始化方法
    init(id, type) {
      if (id) {
        this.id = id;    // 设置当前商品ID
        this.type = type; // 设置操作类型
      }
      // 如果是查看或编辑模式，则获取商品详情
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

    // 获取商品详情
    info(id) {
      this.$http({
        url: `jiadian/info/${id}`,  // 请求商品详情接口
        method: 'get'
      }).then(({ data }) => {
        if (data && data.code === 0) {
          this.ruleForm = data.data;  // 填充表单数据
          // 处理富文本中的图片路径（添加基础URL）
          this.ruleForm.jiadianContent = this.ruleForm.jiadianContent.replaceAll(
              "src=\"upload/",
              "src=\""+this.$base.url+"upload/"
          );
        } else {
          this.$message.error(data.msg);  // 显示错误信息
        }
      });
    },

    // 表单提交
    onSubmit() {
      // 验证表单
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          // 处理富文本中的图片路径（移除基础URL）
          this.ruleForm.jiadianContent = this.ruleForm.jiadianContent.replaceAll(
              this.$base.url,
              ""
          );

          // 根据是否有ID决定是新增还是修改
          this.$http({
            url: `jiadian/${!this.ruleForm.id ? "save" : "update"}`,  // 动态决定接口地址
            method: "post",
            data: this.ruleForm  // 提交表单数据
          }).then(({ data }) => {
            if (data && data.code === 0) {
              // 提交成功提示
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  // 关闭弹窗后刷新父组件
                  this.parent.showFlag = true;
                  this.parent.addOrUpdateFlag = false;
                  this.parent.jiadianCrossAddOrUpdateFlag = false;
                  this.parent.search();  // 刷新列表
                }
              });
            } else {
              this.$message.error(data.msg);  // 显示错误信息
            }
          });
        }
      });
    },

    // 返回列表页
    back() {
      // 关闭当前弹窗，显示列表
      this.parent.showFlag = true;
      this.parent.addOrUpdateFlag = false;
      this.parent.jiadianCrossAddOrUpdateFlag = false;
    },

    // 图片上传成功回调
    jiadianPhotoUploadChange(fileUrls) {
      this.ruleForm.jiadianPhoto = fileUrls;  // 更新图片URL
    },

    // 生成UUID
    getUUID() {
      return new Date().getTime();  // 使用时间戳作为UUID
    }
  }
};
</script>

<style  scoped>

/* 表单容器样式 */
.addEdit-block {
  margin: -10px;  /* 负边距消除父容器的内边距 */
}

/* 表单内容样式 */
.detail-form-content {
  padding: 12px;  /* 内边距 */
  background-color: transparent;  /* 透明背景 */
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

/* 图片预览样式 */
.el-form-item img {
  vertical-align: top;
  border: 1px solid #eee;
  margin-right: 10px;
}
</style>