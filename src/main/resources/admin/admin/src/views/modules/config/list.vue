<template>
  <!-- 主内容容器 -->
  <div class="main-content">
    <!-- 使用 v-if 指令判断是否显示列表页，showFlag 为 true 时显示 -->
    <div v-if="showFlag" class="config-list-container">
      <!-- 表单部分，使用 Element UI 的表单组件，设置为内联表单，绑定搜索表单数据 -->
      <el-form :inline="true" :model="searchForm" class="form-content">
        <!-- 表单行，用于布局 -->
        <el-row class="ad">
          <!-- 表单元素项 -->
          <el-form-item>
            <!-- 新增按钮，点击调用 addOrUpdateHandler 方法 -->
            <el-button
                type="success"
                icon="el-icon-plus"
                @click="addOrUpdateHandler()"
            >新增</el-button>
            <!-- 删除按钮，当选中的数据项数量小于等于 0 时禁用，点击调用 deleteHandler 方法 -->
<!--        3、    :disabled="dataListSelections.length <= 0"：若用户未选中任何数据项，该按钮将被禁用-->
<!--        4、    @click="deleteHandler()"：点击按钮时，会触发 deleteHandler 方法。
如何区分批量删除还是单个删除？通过传入的id进行区分：批量删除触发deleteHandler方法没有传参deleteHandler()，而删除触发deleteHandler方法时传参deleteHandler(scope.row.id)
            ids：若传入了 id 参数，则只删除该条数据；若未传入 id 参数，则删除用户选中的所有数据项。-->
            <el-button
                :disabled="dataListSelections.length <= 0"
                type="danger"
                icon="el-icon-delete"
                @click="deleteHandler()"
            >删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>
      <!-- 表格内容容器 -->
      <div class="table-content">
        <!-- Element UI 的表格组件，绑定表格数据，设置加载状态，监听选择项变化事件 -->
<!--    2、    @selection-change="selectionChangeHandler"：当用户勾选或取消勾选复选框时，会触发 selectionChangeHandler 方法。
        selectionChangeHandler 方法用于获取用户选中的数据项，
        其中val：此参数是一个数组，包含了用户选中的数据项。
        this.dataListSelections：将选中的数据项赋值给 dataListSelections，后续使用

        参数解释：
        stripe为 true 时（在 Vue 中，省略属性值默认表示 true），表格会显示斑马线效果，即奇数行和偶数行使用不同的背景色，这样能增强表格的可读性。
        border为 true 时，表格会显示边框，使表格的结构更加清晰。
        fit为 true 时，表格的列宽会自动调整以适应表格的宽度，确保表格能够完整显示在容器内。
        size设置表格的尺寸大小。Element UI 提供了多种尺寸选项，如 mini、small、medium、large 等，这里设置为 medium 表示使用中等尺寸的表格。-->
        <el-table
            class="tables"
            :data="dataList"
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandler"
            stripe
            border
            fit
            size="medium">
          <!-- 表格选择列，使用了 Element UI 框架中的 <el-table-column> 组件，用于在 <el-table> 里定义一个表格列。
          根据 contents.tableSelection 的值决定是否显示 -->
<!--      1、    type="selection"：此属性表明该列是选择列，用户能够通过勾选复选框来选中数据项。-->

<!--          参数解释：
          header-align="center"设置该列的表头文本的水平对齐方式为居中对齐
          align="center"设置该列单元格内容的水平对齐方式为居中对齐
          width="50"设置该列的宽度为 50 像素
          -->
          <el-table-column
              v-if="contents.tableSelection"
              type="selection"
              header-align="center"
              align="center"
              width="50">
          </el-table-column>

          <!-- 名称列，显示数据的名称字段 -->
<!--          参数解释
          prop="name"该属性指定了表格列要显示的数据字段，每一行数据对象的 name 属性值会被显示在该列对应的单元格中。
          如果 dataList 是绑定到 el-table 的数据，dataList 中的每个元素的 name 属性值就会在该列显示
          :show-overflow-tooltip="true"这是一个动态绑定的属性，当设置为 true 时，如果单元格内的内容过长，超出了单元格的宽度，
          会以 tooltip（工具提示）的形式显示完整内容。当鼠标悬停在单元格上时，会弹出一个提示框显示完整的文本。-->
          <el-table-column
              prop="name"
              label="名称"
              header-align="center"
              align="center"
              :show-overflow-tooltip="true">
            <!-- 自定义单元格内容，显示当前行的名称 -->
            <template slot-scope="scope">
              <span>{{scope.row.name}}</span>
            </template>
          </el-table-column>
          <!-- 图片列，显示数据的图片字段 -->
          <el-table-column
              prop="value"
              label="图片"
              header-align="center"
              align="center"
              width="200"
          >
            <!-- 自定义单元格内容，根据图片字段的值显示图片或提示信息 -->
            <template slot-scope="scope">
              <div v-if="scope.row.value">
                <!-- 拼接图片的完整 URL 并显示 -->
                <img :src="$base.url+scope.row.value.split(',')[0]" width="100" height="100">
              </div>
              <div v-else>无图片</div>
            </template>
          </el-table-column>
          <!-- 操作列，固定在表格右侧 -->
          <el-table-column
              label="操作"
              header-align="center"
              align="center"
              width="300"
              fixed="right">
            <!-- 自定义单元格内容，包含详情、修改、删除按钮 -->
            <template slot-scope="scope">
              <!-- 详情按钮，点击调用 addOrUpdateHandler 方法并传递当前行的 ID 和 'info' 参数 -->
              <el-button
                  type="success"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id,'info')">
                <i class="el-icon-tickets"></i>详情
              </el-button>
              <!-- 修改按钮，点击调用 addOrUpdateHandler 方法并传递当前行的 ID -->
              <el-button
                  type="primary"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id)">
                <i class="el-icon-edit"></i>修改
              </el-button>
              <!-- 删除按钮，点击调用 deleteHandler 方法并传递当前行的 ID -->
              <el-button
                  type="danger"
                  size="mini"
                  @click="deleteHandler(scope.row.id)">
                <i class="el-icon-delete"></i>删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页组件，监听每页数量变化和当前页码变化事件 -->
        <el-pagination
            class="pagination-content"
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="Number(contents.pageEachNum)"
            :total="totalPage"
            layout="total, sizes, prev, pager, next, jumper"
            background>
        </el-pagination>
      </div>
    </div>
    <!-- 添加/修改页面，根据 addOrUpdateFlag 的值决定是否显示，传递父组件实例并设置引用 -->
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
  </div>
</template>

<script>
// 引入添加/修改组件
import AddOrUpdate from "./add-or-update.vue";

export default {
  // 注册组件
  components: {
    AddOrUpdate,
  },
  // 组件的数据
  data() {
    return {
      // 搜索表单数据
      searchForm: {
        name: ""
      },
      // 表格数据列表
      dataList: [],
      // 当前页码
      pageIndex: 1,
      // 每页显示数量
      pageSize: 10,
      // 总记录数
      totalPage: 0,
      // 表格数据加载状态
      dataListLoading: false,
      // 选中的数据项列表
      dataListSelections: [],
      // 是否显示列表页的标志
      showFlag: true,
      // 是否显示添加/修改页面的标志
      addOrUpdateFlag: false,
      // 表格配置项
      contents: {
        // 是否显示表格选择列
        tableSelection: true,
        // 是否显示表格索引列
        tableIndex: true,
        // 每页显示数量
        pageEachNum: 10
      }
    };
  },
  // 组件创建完成后调用的钩子函数，获取数据列表
  created() {
    this.getDataList();
  },
  // 组件的方法
  methods: {
    // 获取数据列表的方法
    getDataList() {
      // 设置表格数据加载状态为 true
      this.dataListLoading = true;
      // 定义请求参数
      let params = {
        // 当前页码
        page: this.pageIndex,
        // 每页显示数量
        limit: this.pageSize,
        // 排序字段
        sort: 'id',
      };

      // 如果搜索表单中的名称字段有值，添加到请求参数中
      //有值代表用户在搜索框输入了文字，在 params 对象中添加一个 name 属性，并把它的值设置为包含搜索关键字的模糊查询字符串
      //% 位于输入的文字前面，意味着搜索结果里该文字可以出现在任意位置。
      //% 位于输入的文字后面，意味着搜索结果里该文字后面可以有任意字符。
      if(this.searchForm.name) {
        params['name'] = '%' + this.searchForm.name + '%';
      }

      // 发送 HTTP 请求获取数据列表
      //params 对象会作为查询参数通过 this.$http 发送到后端的 config/page 接口。
      // 后端服务器接收到这些参数后，会依据 name 参数进行模糊查询，找出所有名称包含用户输入关键字的记录，再把查询结果返回给前端。
      this.$http({
        url: "config/page",
        method: "get",
        params: params
      }).then(({ data }) => {
        // 如果响应数据存在且状态码为 0
        if (data && data.code === 0) {
          // 更新表格数据列表
          this.dataList = data.data.list;
          // 更新总记录数
          this.totalPage = data.data.total;
        } else {
          // 清空表格数据列表
          this.dataList = [];
          // 总记录数设为 0
          this.totalPage = 0;
        }
        // 设置表格数据加载状态为 false
        this.dataListLoading = false;
      });
    },

    // 每页数量变化时的处理方法
    sizeChangeHandle(val) {
      // 更新每页显示数量
      this.pageSize = val;
      // 当前页码重置为 1
      this.pageIndex = 1;
      // 重新获取数据列表
      this.getDataList();
    },

    // 当前页码变化时的处理方法
    currentChangeHandle(val) {
      // 更新当前页码
      this.pageIndex = val;
      // 重新获取数据列表
      this.getDataList();
    },

    // 表格选择项变化时的处理方法
    selectionChangeHandler(val) {
      // 更新选中的数据项列表
      this.dataListSelections = val;
    },

    // 添加/修改的处理方法
    addOrUpdateHandler(id, type) {
      // 隐藏列表页
      this.showFlag = false;
      // 显示添加/修改页面
      this.addOrUpdateFlag = true;
      // 在下一次 DOM 更新循环结束之后执行延迟回调
      this.$nextTick(() => {
        // 调用添加/修改组件的 init 方法进行初始化
        this.$refs.addOrUpdate.init(id, type || 'else');
      });
    },

    // 删除的处理方法
    deleteHandler(id) {
      // 获取要删除的 ID 列表
      var ids = id
          ? [Number(id)]
          : this.dataListSelections.map(item => {
            return Number(item.id);
          });

      // 弹出确认框，确认是否进行删除操作
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        // 发送 HTTP 请求进行删除操作
        this.$http({
          url: "config/delete",
          method: "post",
          data: ids
        }).then(({ data }) => {
          // 如果响应数据存在且状态码为 0
          if (data && data.code === 0) {
            // 显示成功提示信息，提示信息消失后重新获取数据列表
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.getDataList();
              }
            });
          } else {
            // 显示错误提示信息
            this.$message.error(data.msg);
          }
        });
      });
    },

    // 搜索的处理方法
    search() {
      // 当前页码重置为 1
      this.pageIndex = 1;
      // 重新获取数据列表
      this.getDataList();
    }
  }
};
</script>

<style scoped>
/* 主内容容器样式 */
.main-content {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 84px);
}

/* 表单内容样式 */
.form-content {
  padding: 20px;
  background-color: #FFFDF1;
  box-shadow: 0 0 6px rgba(0,0,0,0.1);
  border-radius: 6px;
  margin-bottom: 20px;
}

/* 表单行样式 */
.ad {
  display: flex;
  justify-content: flex-start;
}

/* 表格内容样式 */
.table-content {
  background-color: #fff;
  padding: 20px;
  border-radius: 6px;
  box-shadow: 0 0 6px rgba(0,0,0,0.1);
}

/* 表格样式 */
.tables {
  width: 100%;
  font-size: 14px;
}

/* 表格表头样式 */
.tables >>> .el-table__header th {
  background-color: var(--publicSubColor);
  color: #fff !important;
  font-weight: bold;
}

/* 表格奇数行样式 */
.tables >>> .el-table__body tr:nth-child(odd) {
  background-color: rgba(184, 230, 253, 0.78);
}

/* 表格行悬停样式 */
.tables >>> .el-table__body tr:hover td {
  background-color: var(--publicSubColor) !important;
  color: #333 !important;
}

/* 表格单元格样式 */
.tables >>> .el-table__body td {
  padding: 8px 0;
}

/* 成功按钮样式 */
.el-button--success {
  background-color: var(--publicSubColor);
  border-color: var(--publicMainColor);
  color: #333;
  height: 32px;
  font-size: 14px;
  border-width: 1px;
}

/* 主要按钮样式 */
.el-button--primary {
  background-color: var(--publicSubColor);
  border-color: var(--publicMainColor);
  color: #333;
  height: 32px;
  font-size: 14px;
  border-width: 1px;
}

/* 危险按钮样式 */
.el-button--danger {
  background-color: var(--publicSubColor);
  border-color: var(--publicMainColor);
  color: #333;
  height: 32px;
  font-size: 14px;
  border-width: 1px;
}

/* 按钮通用样式 */
.el-button {
  margin: 0 5px;
  padding: 7px 12px;
  border-radius: 10px;
}

/* 分页样式 */
.pagination-content {
  margin-top: 15px;
  padding: 10px 0;
  text-align: right;
}

</style>