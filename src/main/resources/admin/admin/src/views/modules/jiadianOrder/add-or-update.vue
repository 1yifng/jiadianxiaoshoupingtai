<template>
  <div class="addEdit-block">
    <!-- 使用 Element UI 的表单组件，设置标签宽度为自动 -->
    <el-form class="detail-form-content" label-width="auto">
      <!-- 使用 Element UI 的行组件，用于布局 -->
      <el-row>
        <!-- 订单基础信息部分，占 12 列 -->
        <el-col :span="12">
          <el-form-item label="订单号">
            <!-- 输入框，绑定 ruleForm 的 jiadianOrderUuidNumber 属性，设置为只读 -->
            <el-input v-model="ruleForm.jiadianOrderUuidNumber" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="订单状态">
            <!-- 输入框，绑定 ruleForm 的 jiadianOrderValue 属性，设置为只读 -->
            <el-input v-model="ruleForm.jiadianOrderValue" readonly></el-input>
          </el-form-item>
        </el-col>

        <!-- 商品信息部分，占 12 列 -->
        <el-col :span="12">
          <el-form-item label="商品名称">
            <!-- 输入框，绑定 ruleForm 的 jiadianName 属性，设置为只读 -->
            <el-input v-model="ruleForm.jiadianName" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="商品图片" v-if="ruleForm.jiadianPhoto">
            <!-- 循环展示商品图片，根据 jiadianPhoto 字符串按逗号分割后的数组进行循环 -->
            <!-- 拼接图片路径，$base.url 为基础路径 -->
            <img
                v-for="(item,index) in ruleForm.jiadianPhoto.split(',')"
            :src="$base.url+item"
            width="100"
            height="100"
            :key="index">
          </el-form-item>
        </el-col>

        <!-- 收货信息部分，占 12 列 Element UI 的栅格系统将一行（el-row 组件）平均分成 24 份，通过设置 span 的值来控制每列所占的份数，从而实现灵活的布局。比如 span="12" 表示该列占一行的一半宽度-->
        <el-col :span="12">
          <el-form-item label="收货人">
            <!-- 输入框，绑定 ruleForm 的 addressName 属性，设置为只读 -->
            <el-input v-model="ruleForm.addressName" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="12">
          <el-form-item label="联系电话">
            <!-- 输入框，绑定 ruleForm 的 addressPhone 属性，设置为只读 -->
            <el-input v-model="ruleForm.addressPhone" readonly></el-input>
          </el-form-item>
        </el-col>
        <el-col :span="24">
          <el-form-item label="收货地址">
            <!-- 文本域输入框，绑定 ruleForm 的 addressDizhi 属性，设置为只读 -->
            <el-input v-model="ruleForm.addressDizhi" readonly type="textarea"></el-input>
          </el-form-item>
        </el-col>

        <!-- 订单金额部分，占 12 列 -->
        <el-col :span="12">
          <el-form-item label="实付金额">
            <!-- 输入框，绑定 ruleForm 的 jiadianOrderTruePrice 属性，设置为只读 -->
            <el-input v-model="ruleForm.jiadianOrderTruePrice" readonly></el-input>
          </el-form-item>
        </el-col>
      </el-row>

      <el-form-item>
        <!-- 返回按钮，点击时调用 back 方法 -->
        <el-button @click="back" class="backbutton">返回</el-button>
      </el-form-item>
    </el-form>
  </div>
</template>

<script>
export default {
  data() {
    return {
      id: '', // 用于存储订单 ID
      ruleForm: {
        jiadianOrderUuidNumber: '', // 订单号
        jiadianOrderValue: '', // 订单状态值
        jiadianName: '', // 商品名称
        jiadianPhoto: '', // 商品图片路径（字符串，可能包含多个路径，用逗号分隔）
        addressName: '', // 收货人姓名
        addressPhone: '', // 联系电话
        addressDizhi: '', // 收货地址
        jiadianOrderTruePrice: '' // 实付金额
      }
    };
  },
  props: ["parent"], // 接收来自父组件的 parent 属性，用于父子组件通信
  methods: {
    // 初始化方法，接收订单 ID，设置组件的 id 属性并加载订单详情
    init(id) {
      this.id = id;
      this.loadDetail(id);
    },

    // 加载订单详情数据的方法，根据订单 ID 发送请求获取数据
    loadDetail(id) {
      this.$http.get(`jiadianOrder/info/${id}`).then(({ data }) => {
        if (data.code === 0) {
          this.ruleForm = data.data; // 将返回的数据赋值给 ruleForm
          // 根据订单类型码转换为对应的文字状态（示例映射）
          if (data.data.jiadianOrderTypes) {
            const statusMap = { '101':'已支付', '102':'已发货', '103':'已完成' };
            this.ruleForm.jiadianOrderValue = statusMap[data.data.jiadianOrderTypes] || data.data.jiadianOrderTypes;
          }
        }
      });
    },

    // 返回列表页的方法，通过修改父组件的属性实现返回功能
    back() {
      this.parent.showFlag = true;
      this.parent.addOrUpdateFlag = false;
    }
  }
};
</script>

<style scoped>
.addEdit-block {
  padding: 20px; /*设置页面块的内边距为20像素*/
}
.detail-form-content {
  background: #fff; /*设置表单内容的背景颜色为白色*/
  padding: 20px; /*设置表单内容的内边距为20像素*/
  border-radius: 4px; /*设置表单内容的边框圆角为4像素*/
}
img {
  margin-right: 10px; /*设置图片的右侧外边距为10像素*/
  border: 1px solid #eee; /*设置图片的边框为1像素的浅灰色实线*/
}

.backbutton{
  background-color: #ecf5ff; /*设置返回按钮的背景颜色*/
  border-color: var(--publicSubColor);/*设置返回按钮的边框颜色*/
  color:var(--publicSubColor);/*设置返回按钮的字的颜色*/
  padding: 10px 20px; /*设置返回按钮的上下边距为10像素，左右边距为20像素*/
  font-size: 14px; /*设置返回按钮的字体大小为14像素*/
  border-radius: 10px; /*设置返回按钮的边框圆角为4像素*/
}

/*border-color:#7fcfef ; 设置返回按钮的边框颜色
border-width: 6px; 设置返回按钮的边框宽度为6像素*/
</style>