<template>
  <div class="main-content">
    <!-- 列表页 -->
    <div v-if="showFlag">
      <!-- 搜索表单区域 -->
      <el-form :inline="true" :model="searchForm" class="form-content">
        <!-- 搜索框行 -->
        <el-row :gutter="20" class="search-row">
          <el-form-item label="公告类型">
            <el-input
                prefix-icon="el-icon-search"
                v-model="searchForm.indexNameSearch"
                placeholder="公告类型"
                clearable
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button
                icon="el-icon-search"
                type="success"
                @click="search()"
            >查询</el-button>
          </el-form-item>
        </el-row>

        <!-- 操作按钮行 -->
        <el-row class="action-row">
          <el-form-item>
            <el-button
                v-if="isAuth('dictionaryNews','新增')"
                type="success"
                icon="el-icon-plus"
                @click="addOrUpdateHandler()"
            >新增</el-button>
            <el-button
                v-if="isAuth('dictionaryNews','删除')"
                :disabled="dataListSelections.length <= 0"
                type="danger"
                icon="el-icon-delete"
                @click="deleteHandler()"
            >删除</el-button>
          </el-form-item>
        </el-row>
      </el-form>

      <!-- 表格区域 -->
      <div class="table-content">
        <el-table
            class="tables"
            :data="dataList"
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandler"
            stripe
            border
            fit
            size="medium"
            v-if="isAuth('dictionaryNews','查看')"
        >
          <!-- 选择列 -->
          <el-table-column
              v-if="contents.tableSelection"
              type="selection"
              header-align="center"
              align="center"
              width="50">
          </el-table-column>

          <!-- 公告类型编码列 -->
          <el-table-column
              prop="codeIndex"
              header-align="center"
              align="center"
              label="公告类型编码">
            <template slot-scope="scope">
              {{scope.row.codeIndex}}
            </template>
          </el-table-column>

          <!-- 公告类型名称列 -->
          <el-table-column
              prop="indexName"
              header-align="center"
              align="center"
              label="公告类型名称">
            <template slot-scope="scope">
              {{scope.row.indexName}}
            </template>
          </el-table-column>

          <!-- 操作列 -->
          <el-table-column
              width="300"
              align="center"
              header-align="center"
              label="操作"
              fixed="right"
          >
            <template slot-scope="scope">
              <el-button
                  type="success"
                  icon="el-icon-tickets"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id,'info')"
              >详情</el-button>
              <el-button
                  type="primary"
                  icon="el-icon-edit"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id)"
              >修改</el-button>
              <el-button
                  type="danger"
                  icon="el-icon-delete"
                  size="mini"
                  @click="deleteHandler(scope.row.id)"
              >删除</el-button>
            </template>
          </el-table-column>
        </el-table>

        <!-- 分页组件 -->
        <el-pagination
            class="pagination-content"
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50, 100]"
            :page-size="pageSize"
            :total="totalPage"
            layout="total, sizes, prev, pager, next, jumper"
            background
        ></el-pagination>
      </div>
    </div>

    <!-- 添加/修改页面 -->
    <add-or-update
        v-if="addOrUpdateFlag"
        :parent="this"
        ref="addOrUpdate"
    ></add-or-update>
  </div>
</template>

<script>
// 导入添加/修改组件
import AddOrUpdate from "./add-or-update";

// 导出默认组件配置
export default {
  // 注册子组件
  components: {
    AddOrUpdate, // 添加/修改表单组件
  },

  // 组件数据定义
  data() {
    return {
      // 搜索表单数据对象
      searchForm: {
        indexNameSearch: "" // 公告类型搜索关键词输入框的值
      },

      // 表格相关数据
      dataList: [], // 存储表格数据的数组
      pageIndex: 1, // 当前页码，默认第一页
      pageSize: 10, // 每页显示数据条数，默认10条
      totalPage: 0, // 数据总条数
      dataListLoading: false, // 表格数据加载状态，控制加载动画
      dataListSelections: [], // 表格中选中的数据项

      // 页面显示控制标志
      showFlag: true, // 控制列表页显示/隐藏
      addOrUpdateFlag: false, // 控制添加/修改表单显示/隐藏

      // 表格配置项
      contents: {
        tableSelection: true, // 是否显示表格选择列
        tableIndex: true, // 是否显示表格索引列
      }
    };
  },

  // 组件生命周期钩子 - 创建后调用
  created() {
    // 组件创建时自动获取数据列表
    this.getDataList();
  },

  // 组件方法定义
  methods: {
    /**
     * 获取数据列表方法
     * 从服务器获取表格数据并更新到dataList
     */
    getDataList() {
      // 显示加载状态
      this.dataListLoading = true;

      // 准备请求参数
      let params = {
        page: this.pageIndex, // 当前页码
        limit: this.pageSize, // 每页条数
        sort: 'id', // 排序字段
      };

      // 如果有搜索关键词，添加到请求参数
      if(this.searchForm.indexNameSearch) {
        params['indexName'] = this.searchForm.indexNameSearch;
      }

      // 设置字典类型固定参数
      params['dicCode'] = "news_types"; // 字典编码
      params['dicName'] = "公告类型"; // 字典名称

      // 发送GET请求获取数据
      this.$http({
        url: "dictionary/page", // 请求地址
        method: "get", // 请求方法
        params: params // 请求参数
      }).then(({ data }) => {
        // 请求成功处理
        if (data && data.code === 0) {
          // 成功获取数据，更新表格数据和总条数
          this.dataList = data.data.list;
          this.totalPage = data.data.total;
        } else {
          // 获取数据失败，清空表格数据
          this.dataList = [];
          this.totalPage = 0;
        }
        // 无论成功失败，都关闭加载状态
        this.dataListLoading = false;
      });
    },

    /**
     * 每页显示条数变化处理方法
     * @param {number} val - 新的每页显示条数
     */
    sizeChangeHandle(val) {
      this.pageSize = val; // 更新每页条数
      this.pageIndex = 1; // 重置到第一页
      this.getDataList(); // 重新获取数据
    },

    /**
     * 当前页码变化处理方法
     * @param {number} val - 新的页码
     */
    currentChangeHandle(val) {
      this.pageIndex = val; // 更新当前页码
      this.getDataList(); // 重新获取数据
    },

    /**
     * 表格选择项变化处理方法
     * @param {array} val - 当前选中的行数据数组
     */
    selectionChangeHandler(val) {
      this.dataListSelections = val; // 更新选中的数据项
    },

    /**
     * 搜索方法
     * 点击搜索按钮时触发
     */
    search() {
      this.pageIndex = 1; // 重置到第一页
      this.getDataList(); // 重新获取数据
    },

    /**
     * 添加/修改处理方法
     * @param {number|string} [id] - 行ID，添加时可不传
     * @param {string} [type] - 操作类型，'info'表示详情查看
     */
    addOrUpdateHandler(id, type) {
      this.showFlag = false; // 隐藏列表页
      this.addOrUpdateFlag = true; // 显示表单页

      // DOM更新后初始化表单
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type || 'else');
      });
    },

    /**
     * 删除处理方法
     * @param {number|string} [id] - 要删除的行ID，不传则批量删除选中项
     */
    deleteHandler(id) {
      // 准备要删除的ID数组
      var ids = id
          ? [Number(id)] // 单个删除
          : this.dataListSelections.map(item => { // 批量删除
            return Number(item.id);
          });

      // 显示确认对话框
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定", // 确认按钮文本
        cancelButtonText: "取消", // 取消按钮文本
        type: "warning" // 对话框类型
      }).then(() => {
        // 用户确认删除，发送请求
        this.$http({
          url: "dictionary/delete", // 删除接口地址
          method: "post", // 请求方法
          data: ids // 要删除的ID数组
        }).then(({ data }) => {
          // 删除请求返回处理
          if (data && data.code === 0) {
            // 删除成功提示
            this.$message({
              message: "操作成功", // 提示消息
              type: "success", // 提示类型
              duration: 1500, // 显示时长(毫秒)
              onClose: () => {
                this.search(); // 提示关闭后刷新列表
              }
            });
          } else {
            // 删除失败提示
            this.$message.error(data.msg);
          }
        });
      });
      // 注意：这里没有处理用户取消删除的情况
    }
  }
};
</script>

<style scoped>
/* 主内容区域样式 */
.main-content {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 84px);
}

/* 表单区域样式 */
.form-content {
  padding: 20px;
  background-color: #FFFDF1;
  box-shadow: 0 0 6px rgba(0,0,0,0.1);
  border-radius: 6px;
  margin-bottom: 20px;
}

/* 搜索行样式 */
.search-row {
  display: flex;
  justify-content: flex-start;
  margin-bottom: 15px;
}

/* 操作按钮行样式 */
.action-row {
  display: flex;
  justify-content: flex-start;
}

/* 表格区域样式 */
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

/* 按钮样式 */
.el-button {
  margin: 0 5px;
  padding: 7px 12px;
  border-radius: 10px;
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

/* 分页样式 */
.pagination-content {
  margin-top: 15px;
  padding: 10px 0;
  text-align: right;
}
</style>