<template>
  <div class="add-edit-container">
    <!-- 表单区域 -->
    <el-form
        class="form-content"
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="auto"
    >
      <el-row :gutter="20">
        <!-- 商品类型编码字段 -->
        <el-col :span="12">
          <el-form-item label="商品类型编码" prop="codeIndex">
            <el-input
                v-model="ruleForm.codeIndex"
                placeholder="商品类型编码"
                :readonly="type==='info' || ro.codeIndex"
                clearable
            ></el-input>
          </el-form-item>
        </el-col>

        <!-- 商品类型名称字段 -->
        <el-col :span="12">
          <el-form-item label="商品类型" prop="indexName">
            <el-input
                v-model="ruleForm.indexName"
                placeholder="商品类型"
                :readonly="type==='info' || ro.indexName"
                clearable
            ></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <!-- 按钮区域 -->
      <el-form-item class="btn">
        <el-button
            v-if="type!=='info'"
            type="primary"
            @click="onSubmit"
        >提交</el-button>
        <el-button @click="back">{{ type==='info' ? '返回' : '取消' }}</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  // 接收父组件传递的props
  props: ["parent"],

  data() {
    return {
      id: '',          // 当前操作项的ID（新增时为undefined）
      type: '',        // 操作类型：'info'表示查看详情，'else'表示编辑/添加

      // 控制表单字段的只读状态
      ro: {
        codeIndex: true,   // 商品类型编码字段只读（防止修改）
        indexName: false,  // 商品类型名称字段可编辑
      },

      // 表单数据对象
      ruleForm: {
        codeIndex: '',     // 商品类型编码（自动生成）
        indexName: '',     // 商品类型名称（用户输入）
        superId: '',       // 上级ID（预留字段）
        beizhu: '',        // 备注信息（预留字段）
      },

      // 表单验证规则
      rules: {
        indexName: [
          // 必填校验：商品类型名称不能为空，触发时机为失去焦点时
          { required: true, message: '商品类型不能为空', trigger: 'blur' }
        ]
      }
    };
  },

  methods: {
    /**
     * 初始化表单方法（核心方法）
     * @param {string|number} id - 记录ID（新增时可不传或传undefined）
     * @param {string} [type='else'] - 操作类型，默认'else'表示编辑/新增
     */
    init(id, type = 'else') {
      // 1. 初始化组件状态
      this.id = id || undefined;  // 明确处理id为undefined的情况
      this.type = type;           // 设置操作类型（默认'else'）

      // 2. 清空表单数据（避免数据残留）
      this.ruleForm = {
        codeIndex: '',
        indexName: '',
        superId: '',
        beizhu: ''
      };

      // 3. 根据操作类型执行不同逻辑
      if (this.id && (this.type === 'info' || this.type === 'else')) {
        // 情况1：有ID且是查看/编辑操作 -> 获取详情数据
        this.getInfo(this.id);
      } else {
        // 情况2：新增操作 -> 获取最大编码值
        this.getMaxCode();
      }
    },

    /**
     * 获取最大编码值（用于新增时自动生成编码）
     */
    getMaxCode() {
      this.$http({
        url: `dictionary/maxCodeIndex`,  // 后端接口地址
        method: "post",
        data: { dicCode: "jiadian_types" }  // 传递字典类型标识
      }).then(({ data }) => {
        if (data && data.code === 0) {
          // 成功获取最大编码，更新表单字段
          this.ruleForm.codeIndex = data.maxCodeIndex;
        } else {
          // 接口返回错误信息
          this.$message.error(data.msg);
        }
      }).catch(error => {
        // 网络请求失败处理
        console.error("获取最大编码失败:", error);
        this.$message.error("获取编码失败，请重试");
      });
    },

    /**
     * 获取详情数据（用于查看/编辑）
     * @param {string|number} id - 要查询的记录ID
     */
    getInfo(id) {
      this.$http({
        url: `dictionary/info/${id}`,  // RESTful风格接口
        method: "get"
      }).then(({ data }) => {
        if (data && data.code === 0) {
          // 成功获取数据，填充表单
          this.ruleForm = data.data;
        } else {
          // 接口返回错误信息
          this.$message.error(data.msg);
        }
      });
    },

    /**
     * 表单提交方法
     */
    onSubmit() {
      // 先进行表单验证
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          // 表单验证通过，准备提交数据
          const formData = {
            ...this.ruleForm,              // 展开表单数据
            dicCode: "jiadian_types",      // 固定字典编码
            dicName: "商品类型"            // 固定字典名称
          };

          // 动态判断是更新还是新增
          const url = this.ruleForm.id ? "dictionary/update" : "dictionary/save";

          // 发送请求
          this.$http({
            url: url,
            method: "post",
            data: formData
          }).then(({ data }) => {
            if (data && data.code === 0) {
              // 操作成功提示
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,  // 1.5秒后自动关闭
                onClose: () => {
                  this.back();   // 提示关闭后返回列表页
                }
              });
            } else {
              // 操作失败提示
              this.$message.error(data.msg);
            }
          });
        }
        // 表单验证失败时自动阻止提交（ElementUI内部处理）
      });
    },

    /**
     * 返回列表页方法
     */
    back() {
      // 通过parent引用操作父组件
      this.parent.showFlag = true;          // 显示列表组件
      this.parent.addOrUpdateFlag = false;  // 隐藏表单组件
      this.parent.search();                 // 刷新列表数据
    }
  }
};
</script>

<style scoped>
/* 容器样式 */
.add-edit-container {
  padding: 20px;
  background-color: #fff;
  border-radius: 6px;
  box-shadow: 0 0 6px rgba(0,0,0,0.1);
}

/* 表单内容区域 */
.form-content {
  padding: 20px;
}

/* 表单行间距 */
.el-form-item {
  margin-bottom: 20px;
}

/* 按钮区域 */
.btn {
  text-align: center;
  margin-top: 30px;
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