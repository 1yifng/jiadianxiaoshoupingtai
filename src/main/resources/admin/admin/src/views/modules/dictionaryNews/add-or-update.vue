<template>
  <div class="form-container">
    <!-- 表单区域 -->
    <el-form
        class="form-content"
        ref="ruleForm"
        :model="ruleForm"
        :rules="rules"
        label-width="auto"
    >
      <el-row :gutter="20">
        <!-- 公告类型编码字段 -->
        <el-col :span="12">
          <el-form-item label="公告类型编码" prop="codeIndex">
            <el-input
                v-model="ruleForm.codeIndex"
                placeholder="公告类型编码"
                :readonly="type==='info' || ro.codeIndex"
                clearable
            ></el-input>
          </el-form-item>
        </el-col>

        <!-- 公告类型名称字段 -->
        <el-col :span="12">
          <el-form-item label="公告类型" prop="indexName">
            <el-input
                v-model="ruleForm.indexName"
                placeholder="公告类型"
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
  // 组件接收的props
  props: ["parent"], // 接收父组件实例

  // 组件数据定义
  data() {
    return {
      id: '',          // 当前编辑项的ID，新增时为空
      type: '',        // 操作类型：'info'查看，'else'编辑，空为新增

      // 字段只读控制对象
      ro: {
        codeIndex: true,   // 公告类型编码字段只读
        indexName: false, // 公告类型名称字段可编辑
      },

      // 表单数据对象
      ruleForm: {
        codeIndex: '',     // 公告类型编码
        indexName: '',     // 公告类型名称
        superId: '',       // 上级ID（未使用）
        beizhu: '',        // 备注（未使用）
      },

      // 表单验证规则
      rules: {
        indexName: [
          // 公告类型名称必填验证
          { required: true, message: '公告类型不能为空', trigger: 'blur' }
        ]
      }
    };
  },

  // 组件方法
  methods: {
    /**
     * 初始化表单方法（核心方法）
     * @param {string|number} id - 记录ID（新增时可不传或传undefined）
     * @param {string} [type='else'] - 操作类型，默认'else'表示编辑/新增
     */
    init(id, type='else') {
      // 设置当前ID和操作类型
      this.id = id || undefined;// 明确处理id为undefined的情况
      this.type = type;// 设置操作类型（默认'else'）

      // 清空表单数据（避免数据残留）
      this.ruleForm={
        codeIndex: '',
        indexName: '',
        superId: '',
        beizhu: ''
      }

      // 根据操作类型执行不同逻辑
      if ( this.id && (this.type === 'info' || this.type === 'else')) {
        // 情况1：有ID且是查看/编辑操作 -> 获取详情数据
        this.getInfo(this.id);  // 查看或编辑时获取详情
      } else {
        // 情况2：新增操作 -> 获取最大编码值
        this.getMaxCode(); // 新增时获取最大编码
      }
    },

    /**
     * 获取最大编码值（用于新增时自动生成编码）
     */
    getMaxCode() {
      // 发送请求获取最大编码
      this.$http({
        url: `dictionary/maxCodeIndex`, // 接口地址
        method: "post",                // 请求方法
        data: { dicCode: "news_types" } // 请求参数
      }).then(({ data }) => {
        // 请求成功处理
        if (data && data.code === 0) {
            //成功获取最大编码，更新表单字段
            this.ruleForm.codeIndex=data.maxCodeIndex;
          } else {
            // 无返回数据返回错误信息
            this.$message.error(data.msg)
          }
        }
      ).catch(error => {
        // 请求失败处理
        console.error('获取最大编码失败:', error);
        this.$message.error('获取编码失败，请重试');
      });
    },

    /**
     * 获取详情信息
     * @param {string|number} id - 记录ID
     */
    getInfo(id) {
      // 发送请求获取详情
      this.$http({
        url: `dictionary/info/${id}`, // 接口地址
        method: "get"                 // 请求方法
      }).then(({ data }) => {
        // 请求成功处理
        if (data && data.code === 0) {
          // 将返回数据赋值给表单
          this.ruleForm = data.data;
        } else {
          // 接口返回错误
          this.$message.error(data.msg);
        }
      });
    },

    /**
     * 表单提交
     */
    onSubmit() {
      // 表单验证
      this.$refs["ruleForm"].validate(valid => {
        if (valid) {
          // 表单验证通过
          // 准备提交数据
          const formData = {
            ...this.ruleForm,         // 表单数据
            dicCode: "news_types",    // 字典编码
            dicName: "公告类型"       // 字典名称
          };

          // 根据是否有ID决定是更新还是新增
          const url = this.ruleForm.id ? "dictionary/update" : "dictionary/save";

          // 发送提交请求
          this.$http({
            url: url,       // 接口地址
            method: "post", // 请求方法
            data: formData  // 请求数据
          }).then(({ data }) => {
            // 请求成功处理
            if (data && data.code === 0) {
              // 提交成功提示
              this.$message({
                message: "操作成功",
                type: "success",
                duration: 1500,
                onClose: () => {
                  this.back(); // 提示关闭后返回列表页
                }
              });
            } else {
              // 接口返回错误
              this.$message.error(data.msg);
            }
          });
        }
      });
    },

    /**
     * 返回列表页
     */
    back() {
      // 通过父组件方法控制显示状态
      this.parent.showFlag = true;          // 显示列表页
      this.parent.addOrUpdateFlag = false;  // 隐藏表单页
      this.parent.search();                 // 刷新列表数据
    }
  }
};
</script>

<style scoped>
/* 容器样式 */
.form-container {
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
.btn  {
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