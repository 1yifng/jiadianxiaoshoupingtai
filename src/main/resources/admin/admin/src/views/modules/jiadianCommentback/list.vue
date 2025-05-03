<template>
  <!-- 主内容容器 -->
  <div class="main-content">
    <!-- 条件查询区域 -->
    <div v-if="showFlag">
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="form-content">
        <!-- 搜索条件行 -->
        <el-row :gutter="20" class="slt">
          <!-- 商品名称搜索框 -->
          <el-form-item label="商品名称">
            <el-input
                prefix-icon="el-icon-search"
                v-model="searchForm.jiadianName"
                placeholder="商品名称"
                clearable>
            </el-input>
          </el-form-item>

          <!-- 用户姓名搜索框 -->
          <el-form-item label="用户姓名">
            <el-input
                prefix-icon="el-icon-search"
                v-model="searchForm.yonghuName"
                placeholder="用户姓名"
                clearable>
            </el-input>
          </el-form-item>

          <!-- 查询按钮 -->
          <el-form-item>
            <el-button type="success" @click="search()">
              查询<i class="el-icon-search el-icon--right"/>
            </el-button>
          </el-form-item>
        </el-row>

        <!-- 操作按钮行 -->
        <el-row class="ad">
          <el-form-item>
            <!-- 新增按钮 -->
<!--            <el-button-->
<!--                v-if="isAuth('jiadianCommentback','新增')"-->
<!--                type="success"-->
<!--                icon="el-icon-plus"-->
<!--                @click="addOrUpdateHandler()">-->
<!--              新增-->
<!--            </el-button>-->
<!--            &nbsp;-->
            <!-- 删除按钮 -->
            <el-button
                v-if="isAuth('jiadianCommentback','删除')"
                :disabled="dataListSelections.length <= 0"
                type="danger"
                icon="el-icon-delete"
                @click="deleteHandler()">
              删除
            </el-button>
<!--            &nbsp;-->
<!--            &lt;!&ndash; 报表按钮 &ndash;&gt;-->
<!--            <el-button-->
<!--                v-if="isAuth('jiadianCommentback','报表')"-->
<!--                type="success"-->
<!--                icon="el-icon-pie-chart"-->
<!--                @click="chartDialog()">-->
<!--              报表-->
<!--            </el-button>-->
<!--            &nbsp;-->
<!--            &lt;!&ndash; 导入模板按钮 &ndash;&gt;-->
<!--            <a-->
<!--                style="text-decoration:none"-->
<!--                class="el-button el-button&#45;&#45;success"-->
<!--                v-if="isAuth('jiadianCommentback','导入导出')"-->
<!--                icon="el-icon-download"-->
<!--                href="http://localhost:8080/jiadianxiaoshoupingtai/upload/jiadianCommentbackMuBan.xls">-->
<!--              批量导入商品评价数据模板-->
<!--            </a>-->
<!--            &nbsp;-->
<!--            &lt;!&ndash; 导入数据按钮 &ndash;&gt;-->
<!--            <el-upload-->
<!--                v-if="isAuth('jiadianCommentback','导入导出')"-->
<!--                style="display: inline-block"-->
<!--                :action="getActionUrl"-->
<!--                :on-success="jiadianCommentbackUploadSuccess"-->
<!--                :on-error="jiadianCommentbackUploadError"-->
<!--                :show-file-list="false">-->
<!--              <el-button-->
<!--                  type="success"-->
<!--                  icon="el-icon-upload2">-->
<!--                批量导入商品评价数据-->
<!--              </el-button>-->
<!--            </el-upload>-->
<!--            &nbsp;-->
<!--            &lt;!&ndash; 导出按钮 &ndash;&gt;-->
<!--            <download-excel-->
<!--                v-if="isAuth('jiadianCommentback','导入导出')"-->
<!--                style="display: inline-block"-->
<!--                class="export-excel-wrapper"-->
<!--                :data="dataList"-->
<!--                :fields="json_fields"-->
<!--                name="jiadianCommentback.xls">-->
<!--              <el-button-->
<!--                  type="success"-->
<!--                  icon="el-icon-download">-->
<!--                导出-->
<!--              </el-button>-->
<!--            </download-excel>-->
          </el-form-item>
        </el-row>
      </el-form>

      <!-- 表格区域 -->
      <div class="table-content">
        <!-- 商品评价数据表格 -->
        <el-table
            class="tables"
            :data="dataList"
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandler"
            stripe
            border
            fit>

          <!-- 选择列 -->
          <el-table-column
              type="selection"
              header-align="center"
              align="center"
              width="50">
          </el-table-column>

          <!-- 商品名称列 -->
          <el-table-column
              prop="jiadianName"
              header-align="center"
              align="center"
              label="商品名称">
            <template slot-scope="scope">
              {{scope.row.jiadianName}}
            </template>
          </el-table-column>

          <!-- 用户姓名列 -->
          <el-table-column
              prop="yonghuName"
              header-align="center"
              align="center"
              label="用户姓名">
            <template slot-scope="scope">
              {{scope.row.yonghuName}}
            </template>
          </el-table-column>

          <!-- 评价内容列 -->
          <el-table-column
              prop="jiadianCommentbackText"
              header-align="center"
              align="center"
              label="评价内容">
            <template slot-scope="scope">
                            <span v-if="scope.row.jiadianCommentbackText != null && scope.row.jiadianCommentbackText.length>10">
                                {{scope.row.jiadianCommentbackText.slice(0,10)}}...
                            </span>
              <span v-else>
                                {{scope.row.jiadianCommentbackText}}
                            </span>
            </template>
          </el-table-column>

          <!-- 评价时间列 -->
          <el-table-column
              prop="insertTime"
              header-align="center"
              label="评价时间">
            <template slot-scope="scope">
              {{scope.row.insertTime}}
            </template>
          </el-table-column>

          <!-- 回复内容列 -->
          <el-table-column
              prop="replyText"
              header-align="center"
              align="center"
              label="回复内容">
            <template slot-scope="scope">
                            <span v-if="scope.row.replyText != null && scope.row.replyText.length>10">
                                {{scope.row.replyText.slice(0,10)}}...
                            </span>
              <span v-else>
                                {{scope.row.replyText}}
                            </span>
            </template>
          </el-table-column>

          <!-- 回复时间列 -->
          <el-table-column
              prop="updateTime"
              header-align="center"
              align="center"
              label="回复时间">
            <template slot-scope="scope">
              {{scope.row.updateTime}}
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
              <!-- 回复按钮 -->
              <el-button
                  type="primary"
                  icon="el-icon-edit"
                  size="mini"
                  @click="addOrUpdateHandler(scope.row.id)">
                回复
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

    <!-- 添加/修改页面组件 -->
    <add-or-update v-if="addOrUpdateFlag" :parent="this" ref="addOrUpdate"></add-or-update>
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
      // 打印相关数据
      printFrom: {
        name1: "",
        kuaidiFajianName: "",
        kuaidiFajianShoujihao: "",
        kuaidiFajianAddress: ""
      },
      printVisiable: false,  // 打印对话框显示状态

      // 搜索表单数据
      searchForm: {
        key: "",
        jiadianName: "",
        yonghuName: ""
      },

      // 登录用户信息
      sessionTable: "",  // 登录账户所在表名
      role: "",          // 权限
      userId: "",        // 当前登录人ID

      // 表单数据
      form: {
        id: null,
        jiadianId: null,
        yonghuId: null,
        jiadianCommentbackText: null,
        insertTime: null,
        replyText: null,
        updateTime: null,
        createTime: null,
      },

      // 表格数据
      dataList: [],           // 数据列表
      pageIndex: 1,           // 当前页码
      pageSize: 10,           // 每页条数
      totalPage: 0,           // 总页数
      dataListLoading: false, // 加载状态
      dataListSelections: [], // 选中项
      showFlag: true,         // 是否显示列表
      chartVisiable: false,   // 报表对话框显示状态
      echartsDate: new Date(),// 报表查询日期
      addOrUpdateFlag: false, // 添加/修改弹窗显示状态

      // 评价相关数据
      commentbackId: null,       // 操作数据ID
      commentbackContent: null,  // 评价内容
      commentbackVisible: false, // 评价对话框显示状态
    };
  },

  created() {
    // 初始化数据和获取列表
    this.init();
    this.getDataList();
  },
  mounted() {
    // 获取当前登录用户信息
    this.sessionTable = this.$storage.get("sessionTable");
    this.role = this.$storage.get("role");
    this.userId = this.$storage.get("userId");
  },
  methods: {
    // 初始化方法
    init() {
    },

    // 搜索方法
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },

    // 获取数据列表
    getDataList() {
      this.dataListLoading = true;
      let params = {
        page: this.pageIndex,
        limit: this.pageSize,
        sort: 'id',
      }

      // 添加搜索条件
      if (this.searchForm.jiadianName != '' && this.searchForm.jiadianName != undefined) {
        params['jiadianName'] = '%' + this.searchForm.jiadianName + '%';
      }

      if (this.searchForm.yonghuName != '' && this.searchForm.yonghuName != undefined) {
        params['yonghuName'] = '%' + this.searchForm.yonghuName + '%';
      }

      // 只查询未删除的数据
      params['jiadianCommentbackDelete'] = 1;

      // 请求数据
      this.$http({
        url: "jiadianCommentback/page",
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
    },

    // 每页条数变化
    sizeChangeHandle(val) {
      this.pageSize = val;
      this.pageIndex = 1;
      this.getDataList();
    },

    // 当前页变化
    currentChangeHandle(val) {
      this.pageIndex = val;
      this.getDataList();
    },

    // 多选变化
    selectionChangeHandler(val) {
      this.dataListSelections = val;
    },

    // 添加/修改
    addOrUpdateHandler(id, type) {
      this.showFlag = false;
      this.addOrUpdateFlag = true;
      if (type != 'info') {
        type = 'else';
      }
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type);
      });
    },

    // 删除
    deleteHandler(id) {
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => {
        return Number(item.id);
      });

      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        this.$http({
          url: "jiadianCommentback/delete",
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
/* 主内容区域样式 */
.main-content {
  padding: 20px;
  background-color: #f5f5f5;
  min-height: calc(100vh - 84px);
}

/* 表单内容区域样式 */
.form-content {
  padding: 20px;
  background-color: #FFFDF1;
  box-shadow: 0 0 6px rgba(0,0,0,0.1);
  border-radius: 6px;
  margin-bottom: 20px;
}

/* 搜索条件行样式 */
.slt {
  margin: 0 !important;
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;
}

/* 操作按钮行样式 */
.ad {
  margin: 0 !important;
  display: flex;
  justify-content: flex-start;
  flex-wrap: wrap;
}

/* 表格内容区域样式 */
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

/* 表格斑马线样式 - 奇数行 */
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

/* 输入框样式
.el-input__inner {
  height: 36px;
  color: #606266;
  font-size: 14px;
  border-width: 1px;
  border-style: solid;
  border-color: #DCDFE6;
  border-radius: 4px;
  background-color: #fff;
}*/

/* 表单标签样式
.el-form-item__label {
  color: #606266;
  font-size: 14px;
}*/

/* 对话框样式
.el-dialog {
  border-radius: 6px;
}*/

/* 对话框标题样式
.el-dialog__title {
  color: #333;
  font-size: 16px;
  font-weight: bold;
}*/

/* 对话框内容样式
.el-dialog__body {
  padding: 20px;
}*/

/* 对话框底部样式
.el-dialog__footer {
  padding: 10px 20px;
  text-align: right;
}*/
</style>