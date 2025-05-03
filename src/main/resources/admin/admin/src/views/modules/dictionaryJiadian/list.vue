<template>
  <div class="main-content">
    <!-- 列表页 -->
    <div v-if="showFlag">
      <!-- 搜索表单区域 -->
      <el-form :inline="true" :model="searchForm" class="form-content">
        <!-- 搜索框行 -->
        <el-row :gutter="20" class="search-row">
          <el-form-item label="商品类型">
            <el-input
                prefix-icon="el-icon-search"
                v-model="searchForm.indexNameSearch"
                placeholder="商品类型"
                clearable
            ></el-input>
          </el-form-item>
          <el-form-item>
            <el-button icon="el-icon-search" type="success" @click="search()">查询</el-button>
          </el-form-item>
        </el-row>

        <!-- 操作按钮行 -->
        <el-row class="action-row">
          <el-form-item>
            <el-button
                v-if="isAuth('dictionaryJiadian','新增')"
                type="success"
                icon="el-icon-plus"
                @click="addOrUpdateHandler()"
            >新增</el-button>
            <el-button
                v-if="isAuth('dictionaryJiadian','删除')"
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
            v-if="isAuth('dictionaryJiadian','查看')"
        >
          <!-- 选择列 -->
          <el-table-column
              v-if="contents.tableSelection"
              type="selection"
              header-align="center"
              align="center"
              width="50">
          </el-table-column>


          <!-- 商品类型编码列 -->
          <el-table-column
              prop="codeIndex"
              header-align="center"
              align="center"
              label="商品类型编码">
            <template slot-scope="scope">
              {{scope.row.codeIndex}}
            </template>
          </el-table-column>

          <!-- 商品类型名称列 -->
          <el-table-column
              prop="indexName"
              header-align="center"
              align="center"
              label="商品类型名称">
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
                  v-if="isAuth('dictionaryJiadian','查看')"
                  type="success"
                  icon="el-icon-tickets"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id,'info')"
              >详情</el-button>
              <el-button
                  v-if="isAuth('dictionaryJiadian','修改')"
                  type="primary"
                  icon="el-icon-edit"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id)"
              >修改</el-button>
              <el-button
                  v-if="isAuth('dictionaryJiadian','删除')"
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
import AddOrUpdate from "./add-or-update";

export default {
  components: {
    AddOrUpdate,
  },
  data() {
    return {
      // 搜索表单数据
      searchForm: {
        indexNameSearch: "" // 商品类型搜索关键词
      },
      // 表格数据
      dataList: [], // 数据列表
      pageIndex: 1, // 当前页码
      pageSize: 10, // 每页显示条数
      totalPage: 0, // 总条数
      dataListLoading: false, // 加载状态
      dataListSelections: [], // 表格选中项
      showFlag: true, // 是否显示列表页
      addOrUpdateFlag: false, // 是否显示添加/修改页

      // 表格配置项
      contents: {
        tableSelection: true, // 是否显示选择列
        tableIndex: true, // 是否显示索引列
      }
    };
  },
  created() {
    // 组件创建时获取数据
    this.getDataList();
  },
  methods: {
    /**
     * 获取数据列表（分页查询）
     */
    getDataList() {
      // 显示加载状态
      this.dataListLoading = true;

      // 初始化请求参数
      let params = {
        page: this.pageIndex,   // 当前页码
        limit: this.pageSize,   // 每页条数
        sort: 'id',             // 排序字段（默认按ID排序）
      }

      // 如果搜索框有值，添加到查询条件
      if(this.searchForm.indexNameSearch) {
        params['indexName'] = this.searchForm.indexNameSearch;  // 商品类型名称模糊搜索
      }

      // 设置字典类型固定参数
      params['dicCode'] = "jiadian_types";  // 字典编码（后端用于区分字典类型）
      params['dicName'] = "商品类型";       // 字典名称（展示用）

      // 发送GET请求获取分页数据
      this.$http({
        url: "dictionary/page",  // 后端分页查询接口
        method: "get",
        params: params           // 查询参数
      }).then(({ data }) => {
        // 请求成功处理
        if (data && data.code === 0) {
          this.dataList = data.data.list;    // 更新表格数据
          this.totalPage = data.data.total;  // 更新总条数（用于分页）
        } else {
          // 数据异常时清空表格
          this.dataList = [];
          this.totalPage = 0;
        }
        // 无论成功失败都关闭加载状态
        this.dataListLoading = false;
      });
    },

    /**
     * 每页显示条数变化事件
     * @param {number} val - 新的每页条数（如10/20/50）
     */
    sizeChangeHandle(val) {
      this.pageSize = val;   // 更新每页条数
      this.pageIndex = 1;    // 重置到第一页（通常改变条数后重新从第一页开始）
      this.getDataList();    // 重新加载数据
    },

    /**
     * 当前页码变化事件
     * @param {number} val - 新的页码
     */
    currentChangeHandle(val) {
      this.pageIndex = val;  // 更新当前页码
      this.getDataList();    // 加载对应页数据
    },

    /**
     * 表格选择项变化事件
     * @param {array} val - 当前选中的行数据数组
     */
    selectionChangeHandler(val) {
      this.dataListSelections = val;  // 更新选中项（用于批量操作）
    },

    /**
     * 搜索方法（点击查询按钮触发）
     */
    search() {
      this.pageIndex = 1;      // 搜索时重置到第一页
      this.getDataList();      // 重新加载数据（会带上搜索条件）
    },

    /**
     * 打开添加/修改/查看表单
     * @param {number|string} [id] - 行ID（新增时不传）
     * @param {string} [type] - 操作类型：'info'查看详情，不传或'else'为编辑/新增
     */
    addOrUpdateHandler(id, type) {
      this.showFlag = false;        // 隐藏列表页
      this.addOrUpdateFlag = true;  // 显示表单页

      // DOM更新后初始化表单组件
      this.$nextTick(() => {
        // 调用子组件的初始化方法
        // id: undefined表示新增，有值表示修改/查看
        // type: 默认'else'表示编辑，'info'表示查看
        this.$refs.addOrUpdate.init(id, type || 'else');
      });
    },

    /**
     * 删除处理方法
     * @param {number|string} [id] - 要删除的行ID（不传则批量删除选中项）
     */
    deleteHandler(id) {
      // 准备要删除的ID数组
      var ids = id
          ? [Number(id)]  // 单个删除：将ID转为数字数组
          : this.dataListSelections.map(item => {
            return Number(item.id);  // 批量删除：提取选中项ID
          });

      // 显示确认对话框
      this.$confirm(
          `确定进行[${id ? "删除" : "批量删除"}]操作?`,  // 动态提示文字
          "提示",
          {
            confirmButtonText: "确定",
            cancelButtonText: "取消",
            type: "warning"  // 警告类型图标
          }
      ).then(() => {
        // 确认删除，发送POST请求
        this.$http({
          url: "dictionary/delete",  // 删除接口
          method: "post",
          data: ids                 // 要删除的ID数组
        }).then(({ data }) => {
          if (data && data.code === 0) {
            // 删除成功提示
            this.$message({
              message: "删除成功",
              type: "success",
              duration: 1500,  // 1.5秒后自动关闭
              onClose: () => {
                this.search();  // 提示关闭后刷新列表
              }
            });
          } else {
            // 删除失败提示
            this.$message.error(data.msg);
          }
        });
      });
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
  border-radius:10px;
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