<template>
  <div class="main-content">
    <!-- 条件查询区域 -->
    <div v-if="showFlag" class="config-list-container">
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="form-content">
        <el-row :gutter="20" class="slt">
          <!-- 用户姓名搜索 -->
          <el-form-item label="用户姓名">
            <el-input
                prefix-icon="el-icon-search"
                v-model="searchForm.yonghuName"
                placeholder="用户姓名"
                clearable
            ></el-input>
          </el-form-item>

          <!-- 性别下拉搜索 -->
          <el-form-item label="性别">
            <el-select v-model="searchForm.sexTypes" placeholder="请选择性别">
              <el-option label="=-请选择-=" value=""></el-option>
              <el-option
                  v-for="(item,index) in sexTypesSelectSearch"
                  :key="index"
                  :label="item.indexName"
                  :value="item.codeIndex">
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 查询按钮 -->
          <el-form-item>
            <el-button type="success" @click="search()">
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
        <!-- 用户数据表格 -->
        <el-table
            class="tables"
            :data="dataList"
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandler"
            stripe
            border
            fit
            size="medium"
            >

          <!-- 多选列 -->
          <el-table-column
              type="selection"
              header-align="center"
              align="center"
              width="50">
          </el-table-column>

          <!-- 账户列 -->
          <el-table-column
              prop="username"
              label="账户"
              header-align="center"
              align="center">
            <template slot-scope="scope">
              {{scope.row.username}}
            </template>
          </el-table-column>

          <!-- 用户姓名列 -->
          <el-table-column
              prop="yonghuName"
              label="用户姓名"
              header-align="center"
              align="center">
            <template slot-scope="scope">
              {{scope.row.yonghuName}}
            </template>
          </el-table-column>

          <!-- 联系方式列 -->
          <el-table-column
              prop="yonghuPhone"
              label="联系方式"
              header-align="center"
              align="center">
            <template slot-scope="scope">
              {{scope.row.yonghuPhone}}
            </template>
          </el-table-column>

          <!-- 用户头像列 -->
          <el-table-column
              prop="yonghuPhoto"
              header-align="center"
              width="200"
              label="用户头像">
            <template slot-scope="scope">
              <div v-if="scope.row.yonghuPhoto">
                <img :src="$base.url+scope.row.yonghuPhoto" width="100" height="100">
              </div>
              <div v-else>无图片</div>
            </template>
          </el-table-column>

          <!-- 性别列 -->
          <el-table-column
              prop="sexTypes"
              label="性别"
              header-align="center"
              align="center">
            <template slot-scope="scope">
              {{scope.row.sexValue}}
            </template>
          </el-table-column>

          <!-- 电子邮箱列 -->
          <el-table-column
              prop="yonghuEmail"
              label="电子邮箱"
              header-align="center"
              align="center">
            <template slot-scope="scope">
              {{scope.row.yonghuEmail}}
            </template>
          </el-table-column>

          <!-- 余额列 -->
          <el-table-column
              prop="newMoney"
              label="余额"
              header-align="center"
              align="center">
            <template slot-scope="scope">
              {{scope.row.newMoney}}
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
                  @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>

              <!-- 修改按钮 -->
              <el-button
                  type="primary"
                  icon="el-icon-edit"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id)">修改</el-button>

              <!-- 删除按钮 -->
              <el-button
                  type="danger"
                  icon="el-icon-delete"
                  size="mini"
                  @click="deleteHandler(scope.row.id)">删除</el-button>

              <!-- 重置密码按钮 -->
              <el-button
                  type="success"
                  icon="el-icon-tickets"
                  size="mini"
                  @click="resetPassword(scope.row.id)">重置密码</el-button>
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
import AddOrUpdate from "./add-or-update";

export default {
  components: {
    AddOrUpdate,
  },
  data() {
    return {
      // 搜索表单数据
      searchForm: {
        yonghuName: "",  // 用户姓名
        sexTypes: "",    // 性别
      },

      // 表格数据
      dataList: [],          // 用户数据列表
      pageIndex: 1,          // 当前页码
      pageSize: 10,          // 每页条数
      totalPage: 0,          // 总条数
      dataListLoading: false,// 加载状态
      dataListSelections: [],// 选中项

      // 页面显示控制
      showFlag: true,        // 是否显示列表页
      addOrUpdateFlag: false,// 是否显示添加/修改页

      // 用户信息
      sessionTable: "",      // 当前登录用户表名
      role: "",              // 用户角色
      userId: "",            // 用户ID

      // 下拉选项数据
      sexTypesSelectSearch: [],  // 性别下拉选项
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

    // 获取用户列表数据
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id',
      };

      // 添加搜索条件到参数
      if (this.searchForm.yonghuName) {
        params['yonghuName'] = '%' + this.searchForm.yonghuName + '%';
      }
      if (this.searchForm.sexTypes) {
        params['sexTypes'] = this.searchForm.sexTypes;
      }

      // 只查询未删除的用户
      params['yonghuDelete'] = 1;

      // 发送请求获取用户数据
      this.$http({
        url: "yonghu/page",
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

      // 获取性别下拉选项
      this.$http({
        url: "dictionary/page?dicCode=sex_types&page=1&limit=100",
        method: "get",
      }).then(({data}) => {
        if(data && data.code === 0){
          this.sexTypesSelectSearch = data.data.list;
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
          url: "yonghu/delete",
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

    // 重置密码方法
    resetPassword(id) {
      this.$confirm(`确定要重置密码吗?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "yonghu/resetPassword?id=" + id,
          method: "get",
        }).then(({data}) => {
          if(data && data.code === 0){
            this.$message({
              message: '重置成功,密码已重置为123456',
              type: 'success',
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
    }
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