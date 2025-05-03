<template>
  <!-- 主内容容器 -->
  <div class="main-content">
    <!-- 条件查询区域 -->
    <div v-if="showFlag">
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <!-- 公告标题搜索 -->
          <el-form-item label="公告标题">
            <el-input
                prefix-icon="el-icon-search"
                v-model="searchForm.newsName"
                placeholder="公告标题"
                clearable
            ></el-input>
          </el-form-item>

          <!-- 公告类型下拉搜索 -->
          <el-form-item label="公告类型">
            <el-select v-model="searchForm.newsTypes" placeholder="请选择公告类型">
              <el-option label="=-请选择-=" value=""></el-option>
              <el-option
                  v-for="(item,index) in newsTypesSelectSearch"
                  :key="index"
                  :label="item.indexName"
                  :value="item.codeIndex">
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 查询按钮 -->
          <el-form-item>
            <el-button type="primary" @click="search()">
              查询<i class="el-icon-search el-icon--right"/>
            </el-button>
          </el-form-item>
        </el-row>

        <!-- 操作按钮区域 -->
        <el-row class="ad">
          <el-form-item>
            <!-- 新增按钮 -->
            <el-button
                type="success"
                icon="el-icon-plus"
                @click="addOrUpdateHandler()"
            >新增</el-button>

            <!-- 删除按钮 -->
            <el-button
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
        <!-- 公告数据表格 -->
        <el-table
            class="tables"
            :data="dataList"
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandler"
            stripe
            border
            fit
            size="medium">

          <!-- 多选列 -->
          <el-table-column
              type="selection"
              header-align="center"
              align="center"
              width="50">
          </el-table-column>

          <!-- 公告标题列 -->
          <el-table-column
              prop="newsName"
              label="公告标题"
              header-align="center"
              align="center">
            <template slot-scope="scope">
              {{scope.row.newsName}}
            </template>
          </el-table-column>

          <!-- 公告类型列 -->
          <el-table-column
              prop="newsTypes"
              label="公告类型"
              header-align="center"
              align="center">
            <template slot-scope="scope">
              {{scope.row.newsValue}}
            </template>
          </el-table-column>

          <!-- 公告图片列 -->
          <el-table-column
              prop="newsPhoto"
              header-align="center"
              width="200"
              label="公告图片">
            <template slot-scope="scope">
              <div v-if="scope.row.newsPhoto">
                <img :src="$base.url+scope.row.newsPhoto" width="100" height="100">
              </div>
              <div v-else>无图片</div>
            </template>
          </el-table-column>

          <!-- 添加时间列 -->
          <el-table-column
              prop="insertTime"
              label="添加时间"
              header-align="center"
              align="center">
            <template slot-scope="scope">
              {{scope.row.insertTime}}
            </template>
          </el-table-column>

          <!-- 操作列 -->
          <el-table-column
              width="300"
              align="center"
              header-align="center"
              label="操作">
            <template slot-scope="scope">
              <!-- 详情按钮 -->
              <el-button
                  type="success"
                  icon="el-icon-tickets"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id,'info')">
                详情
              </el-button>

              <!-- 修改按钮 -->
              <el-button
                  type="primary"
                  icon="el-icon-edit"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id)">
                修改
              </el-button>

              <!-- 删除按钮 -->
              <el-button
                  type="danger"
                  icon="el-icon-delete"
                  size="mini"
                  @click="deleteHandler(scope.row.id)">
                删除
              </el-button>
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
            background>
        </el-pagination>
      </div>
    </div>

    <!-- 添加/修改页面 -->
    <add-or-update
        v-if="addOrUpdateFlag"
        :parent="this"
        ref="addOrUpdate">
    </add-or-update>
  </div>
</template>

<script>
// 引入添加/修改组件
import AddOrUpdate from "./add-or-update";

export default {
  components: {
    AddOrUpdate,
  },
  data() {
    return {
      // // 打印相关数据
      // printFrom: {
      //   kuaidiFajianName: "",
      //   kuaidiFajianShoujihao: "",
      //   kuaidiFajianAddress: ""
      // },
      // printVisiable: false,  // 打印对话框显示状态

      // 搜索表单数据
      searchForm: {
        newsName: "",  // 公告标题
        newsTypes: "", // 公告类型
      },

      // 表格数据
      dataList: [],          // 公告数据列表
      pageIndex: 1,          // 当前页码
      pageSize: 10,          // 每页条数
      totalPage: 0,          // 总条数
      dataListLoading: false,// 加载状态
      dataListSelections: [],// 选中项

      // 页面显示控制
      showFlag: true,        // 是否显示列表页
      addOrUpdateFlag: false,// 是否显示添加/修改页
      chartVisiable: false,  // 是否显示统计报表

      // 用户信息
      sessionTable: "",      // 当前登录用户表名
      role: "",              // 用户角色
      userId: "",            // 用户ID

      // 下拉选项数据
      newsTypesSelectSearch: [],  // 公告类型下拉选项

      // 图表相关
      echartsDate: new Date(), // 图表查询日期
    };
  },
  created() {
    // 初始化获取数据
    this.init();
    this.getDataList();

    // 获取当前登录用户信息
    this.sessionTable = this.$storage.get("sessionTable");
    this.role = this.$storage.get("role");
    this.userId = this.$storage.get("userId");
  },

  methods: {
    // 初始化方法
    init() {
      // 可以在这里初始化一些数据
    },

    // 获取公告列表数据
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id',
      };

      // 添加搜索条件到参数
      if (this.searchForm.newsName) {
        params['newsName'] = '%' + this.searchForm.newsName + '%';
      }
      if (this.searchForm.newsTypes) {
        params['newsTypes'] = this.searchForm.newsTypes;
      }

      // 只查询未删除的公告
      params['newsDelete'] = 1;

      // 发送请求获取公告数据
      this.$http({
        url: "news/page",
        method: "get",
        params: params
      }).then(({data}) => {
        if(data && data.code === 0){
          this.dataList = data.data.list;
          this.totalPage = data.data.total;
        }else{
          this.dataList = [];
          this.totalPage = 0;
        }
        this.dataListLoading = false;
      });

      // 获取公告类型下拉选项
      this.$http({
        url: "dictionary/page?dicCode=news_types&page=1&limit=100",
        method: "get",
      }).then(({data}) => {
        if(data && data.code === 0){
          this.newsTypesSelectSearch = data.data.list;
        }
      });
    },

    // 每页条数改变
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },

    // 当前页改变
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },

    // 多选改变
    selectionChangeHandler(val) {
      this.dataListSelections = val;
    },

    // 搜索方法
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },

    // 添加/修改方法
    addOrUpdateHandler(id, type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type || 'else');
      });
    },

    // 删除方法
    deleteHandler(id) {
      // 准备要删除的ID数组
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => {
        return Number(item.id);
      });

      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "news/delete",
          method: "post",
          data: ids
        }).then(({data}) => {
          if(data && data.code === 0){
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.search();
              }
            });
          }else{
            this.$message.error(data.msg);
          }
        });
      });
    },
  }
};
</script>

<style scoped>
/* 主内容样式 */
.main-content {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 84px);
}

/* 表单内容样式 */
.form-content {
  padding: 20px;
  background-color: #fff;
  box-shadow: 0 0 6px rgba(0,0,0,0.1);
  border-radius: 6px;
  margin-bottom: 20px;
}

/* 表单行样式 */
.slt {
  margin: 0 !important;
  display: flex;
  flex-wrap: wrap;
}

.ad {
  margin: 0 !important;
  display: flex;
  flex-wrap: wrap;
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

/* 表格斑马纹样式 */
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

/* 按钮间距 */
.el-button + .el-button {
  margin-left: 10px;
}
</style>