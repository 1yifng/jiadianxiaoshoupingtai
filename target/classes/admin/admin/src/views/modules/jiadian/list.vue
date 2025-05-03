<template>
  <!-- 主内容容器 -->
  <div class="main-content">
    <!-- 条件查询部分 -->
    <div v-if="showFlag">
      <!-- 搜索表单 -->
      <el-form :inline="true" :model="searchForm" class="form-content">
        <!-- 搜索条件行 -->
        <el-row :gutter="20" class="slt">
          <!-- 商品名称搜索框 -->
          <el-form-item label="商品名称">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.jiadianName" placeholder="商品名称" clearable></el-input>
          </el-form-item>

          <el-form-item label="商品编号">
            <el-input prefix-icon="el-icon-search" v-model="searchForm.jiadianUuidNumber" placeholder="商品编号" clearable></el-input>
          </el-form-item>

          <!-- 商品类型下拉选择 -->
          <el-form-item label="商品类型">
            <el-select v-model="searchForm.jiadianTypes" placeholder="请选择商品类型">
              <el-option label="=-请选择-=" value=""></el-option>
              <el-option
                  v-for="(item,index) in jiadianTypesSelectSearch"
                  :key="index"
                  :label="item.indexName"
                  :value="item.codeIndex">
              </el-option>
            </el-select>
          </el-form-item>

          <!-- 查询按钮 -->
          <el-form-item>
            <el-button type="success" @click="search()">查询<i class="el-icon-search el-icon--right"/></el-button>
          </el-form-item>
        </el-row>

        <!-- 操作按钮行 -->
        <el-row class="ad">
          <el-form-item>
            <!-- 新增按钮 -->
            <el-button
                type="success"
                icon="el-icon-plus"
                @click="addOrUpdateHandler()"
            >新增</el-button>
            &nbsp;
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

      <!-- 表格内容区域 -->
      <div class="table-content">
        <!-- 商品数据表格 -->
        <el-table class="tables"
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

          <!-- 商品编号列 -->
          <el-table-column
              prop="jiadianUuidNumber"
              header-align="center"
              align="center"
              label="商品编号">
            <template slot-scope="scope">
              {{scope.row.jiadianUuidNumber}}
            </template>
          </el-table-column>

          <!-- 商品照片列 -->
          <el-table-column prop="jiadianPhoto"
                           header-align="center"
                           align="center"
                           width="200"
                           label="商品照片">
            <template slot-scope="scope">
              <div v-if="scope.row.jiadianPhoto">
                <img :src="$base.url+scope.row.jiadianPhoto" width="100" height="100">
              </div>
              <div v-else>无图片</div>
            </template>
          </el-table-column>

          <!-- 商品类型列 -->
          <el-table-column
              prop="jiadianTypes"
              header-align="center"
              align="center"
              label="商品类型">
            <template slot-scope="scope">
              {{scope.row.jiadianValue}}
            </template>
          </el-table-column>

          <!-- 商品库存列 -->
          <el-table-column
              prop="jiadianKucunNumber"
              header-align="center"
              align="center"
              label="商品库存">
            <template slot-scope="scope">
              {{scope.row.jiadianKucunNumber}}
            </template>
          </el-table-column>

          <!-- 商品原价列 -->
          <el-table-column
              prop="jiadianOldMoney"
              header-align="center"
              align="center"
              label="商品原价">
            <template slot-scope="scope">
              {{scope.row.jiadianOldMoney}}
            </template>
          </el-table-column>

          <!-- 现价列 -->
          <el-table-column
              prop="jiadianNewMoney"
              header-align="center"
              align="center"
              label="现价">
            <template slot-scope="scope">
              {{scope.row.jiadianNewMoney}}
            </template>
          </el-table-column>


          <!-- 是否上架列 -->
          <el-table-column
              prop="shangxiaTypes"
              header-align="center"
              align="center"
              label="是否上架">
            <template slot-scope="scope">
              {{scope.row.shangxiaValue}}
            </template>
          </el-table-column>

          <!-- 录入时间列 -->
          <el-table-column
              prop="insertTime"
              header-align="center"
              align="center"
              label="录入时间">
            <template slot-scope="scope">
              {{scope.row.insertTime}}
            </template>
          </el-table-column>

          <!-- 操作列 -->
          <el-table-column width="300" align="center"
                           header-align="center"
                           label="操作">
            <template slot-scope="scope">
              <!-- 详情按钮 -->
              <el-button  type="success" icon="el-icon-tickets" size="mini" @click="addOrUpdateHandler(scope.row.id,'info')">详情</el-button>
              <!-- 修改按钮 -->
              <el-button  type="primary" icon="el-icon-edit" size="mini" @click="addOrUpdateHandler(scope.row.id)">修改</el-button>
              <!-- 删除按钮 -->
              <el-button  type="danger" icon="el-icon-delete" size="mini" @click="deleteHandler(scope.row.id)">删除</el-button>
              <!-- 上架/下架按钮 -->
              <el-button  type="primary" icon="el-icon-tickets" size="mini" @click="shangxia(scope.row.id,scope.row.shangxiaTypes)">{{scope.row.shangxiaTypes == 1?'下架':'上架'}}</el-button>
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
// 引入添加/修改组件，该组件用于处理商品信息的添加和修改操作
import AddOrUpdate from "./add-or-update";

export default {
  // 注册组件，将引入的 AddOrUpdate 组件注册到当前组件中，以便在模板中使用
  components: {
    AddOrUpdate,
  },
  // data 函数用于定义组件的数据，返回一个包含所有数据属性的对象
  data() {
    return {
      // 打印相关数据，name1 用于存储打印相关的名称信息，初始为空字符串
      printFrom: {
        name1: "",
      },
      // 控制打印弹窗是否可见，初始为 false 表示隐藏
      printVisiable: false,

      // 搜索表单数据，key 用于存储搜索关键字，初始为空字符串
      searchForm: {
        key: ""
      },
      // 登录账户所在表名，初始为空字符串
      sessionTable: "",
      // 用户权限信息，初始为空字符串
      role: "",
      // 当前登录人的 id，初始为空字符串
      userId: "",

      // 商品类型下拉框选项，存储从后端获取的商品类型列表，初始为空数组
      jiadianTypesSelectSearch: [],

      // 表单数据，用于存储商品信息，各个字段初始值为 null
      form: {
        id: null, // 商品 id
        jiadianName: null, // 商品名称
        jiadianUuidNumber: null, // 商品编号
        jiadianPhoto: null, // 商品照片
        jiadianTypes: null, // 商品类型
        jiadianKucunNumber: null, // 商品库存数量
        jiadianOldMoney: null, // 商品原价
        jiadianNewMoney: null, // 商品现价
        // jiadianClicknum: null, // 商品点击数，此处注释掉未使用
        jiadianContent: null, // 商品描述内容
        shangxiaTypes: null, // 商品上下架状态
        jiadianDelete: null, // 商品删除状态
        insertTime: null, // 商品录入时间
        createTime: null, // 商品创建时间
      },

      // 表格数据，存储从后端获取的商品列表数据，初始为空数组
      dataList: [],
      // 当前页码，初始为 1
      pageIndex: 1,
      // 每页显示数量，初始为 10
      pageSize: 10,
      // 总记录数，初始为 0
      totalPage: 0,
      // 表格加载状态，控制表格是否显示加载中效果，初始为 false 表示未加载
      dataListLoading: false,
      // 表格选中项，存储用户在表格中选中的商品数据，初始为空数组
      dataListSelections: [],
      // 是否显示列表页，初始为 true 表示显示
      showFlag: true,
      // 是否显示添加/修改页，初始为 false 表示隐藏
      addOrUpdateFlag: false,

      // 库存操作相关数据
      kucunNumber: 1, // 操作数量，初始为 1
      kucunNumberId: null, // 操作数据 id，初始为 null
      kucunNumberTypes: null, // 操作类型，初始为 null
      jiadianKucunNumber: 0, // 原有数量，初始为 0
      jiadianKucunNumberTitle: null, // 对话框标题，初始为 null
      jiadianKucunNumberVisible: false, // 对话框显示状态，初始为 false 表示隐藏
    };
  },
  // created 钩子函数，在组件实例初始化完成后立即调用，常用于初始化数据
  created() {
    // 调用初始化方法
    this.init();
    // 调用获取数据列表方法
    this.getDataList();
  },
  // mounted 钩子函数，在组件挂载到 DOM 后调用，常用于获取 DOM 元素或进行异步操作
  mounted() {
    // 从存储中获取当前登录用户的信息，包括所在表名、权限和 id
    this.sessionTable = this.$storage.get("sessionTable");
    this.role = this.$storage.get("role");
    this.userId = this.$storage.get("userId");
  },

  // methods 对象用于定义组件的方法
  methods: {
    // 初始化方法，目前为空，可用于后续添加初始化逻辑
    init() {
    },

    // 查询方法，用于重新加载数据列表，将当前页码重置为 1 并调用获取数据列表方法
    search() {
      this.pageIndex = 1;
      this.getDataList();
    },

    // 获取数据列表方法，用于从后端获取商品数据列表
    getDataList() {
      // 设置表格加载状态为 true，表示正在加载数据
      this.dataListLoading = true;
      // 定义请求参数对象，包含分页和排序信息
      let params = {
        page: this.pageIndex, // 当前页码
        limit: this.pageSize, // 每页显示数量
        sort: 'id', // 排序字段为 id
      }

      // 添加搜索条件，如果搜索表单中的商品名称不为空且已定义，则添加到请求参数中
      if (this.searchForm.jiadianName != '' && this.searchForm.jiadianName != undefined) {
        // 前后添加 % 表示模糊查询，即输入的文字可位于任意位置且后面可加任意符号
        params['jiadianName'] = '%' + this.searchForm.jiadianName + '%';
      }

      // 如果搜索表单中的商品编号不为空且已定义，则添加到请求参数中
      if (this.searchForm.jiadianUuidNumber != '' && this.searchForm.jiadianUuidNumber != undefined) {
        // 前后添加 % 表示模糊查询
        params['jiadianUuidNumber'] = '%' + this.searchForm.jiadianUuidNumber + '%';
      }

      // 如果搜索表单中的商品类型不为空且已定义，则添加到请求参数中
      if (this.searchForm.jiadianTypes != '' && this.searchForm.jiadianTypes != undefined) {
        params['jiadianTypes'] = this.searchForm.jiadianTypes;
      }

      // 只查询未删除的数据，将删除状态设置为 1 表示未删除 没有直接删除数据库的数据是因为想后面要使用该商品时直接改状态就行,提高复用性
      params['jiadianDelete'] = 1;

      // 发送请求获取数据，使用 $http 发送 GET 请求到指定 URL，并携带参数
      this.$http({
        url: "jiadian/page",
        method: "get",
        params: params
      }).then(({ data }) => {
        // 如果响应数据存在且状态码为 0 表示请求成功
        if (data && data.code === 0) {
          // 将响应数据中的商品列表赋值给 dataList
          this.dataList = data.data.list;
          // 将响应数据中的总记录数赋值给 totalPage
          this.totalPage = data.data.total;
        } else {
          // 请求失败，清空数据列表和总记录数
          this.dataList = [];
          this.totalPage = 0;
        }
        // 设置表格加载状态为 false，表示数据加载完成
        this.dataListLoading = false;
      });

      // 获取商品类型下拉框选项，发送 GET 请求到指定 URL
      this.$http({
        url: "dictionary/page?dicCode=jiadian_types&page=1&limit=100",
        method: "get",
      }).then(({ data }) => {
        // 如果响应数据存在且状态码为 0 表示请求成功
        if (data && data.code === 0) {
          // 将响应数据中的商品类型列表赋值给 jiadianTypesSelectSearch
          this.jiadianTypesSelectSearch = data.data.list;
        }
      });
    },

    // 每页数量变化处理方法，当用户更改每页显示数量时调用
    sizeChangeHandle(val) {
      // 更新每页显示数量
      this.pageSize = val;
      // 将当前页码重置为 1
      this.pageIndex = 1;
      // 重新获取数据列表
      this.getDataList();
    },

    // 当前页变化处理方法，当用户切换页码时调用
    currentChangeHandle(val) {
      // 更新当前页码
      this.pageIndex = val;
      // 重新获取数据列表
      this.getDataList();
    },

    // 表格选择项变化处理方法，当用户在表格中选择或取消选择商品时调用
    selectionChangeHandler(val) {
      // 更新表格选中项
      this.dataListSelections = val;
    },

    // 添加/修改处理方法，用于显示添加/修改页面并初始化相关数据
    addOrUpdateHandler(id, type) {
      // 隐藏列表页
      this.showFlag = false;
      // 显示添加/修改页
      this.addOrUpdateFlag = true;
      // 等待 DOM 更新完成后调用 AddOrUpdate 组件的 init 方法进行初始化
      this.$nextTick(() => {
        this.$refs.addOrUpdate.init(id, type || 'else');
      });
    },

    // 删除处理方法，用于删除单个或批量商品
    deleteHandler(id) {
      // 如果传入了 id，则将其转换为数组；否则将表格选中项的 id 提取出来组成数组
      var ids = id ? [Number(id)] : this.dataListSelections.map(item => {
        return Number(item.id);
      });

      // 弹出确认对话框，询问用户是否确定进行删除操作
      this.$confirm(`确定进行[${id ? "删除" : "批量删除"}]操作?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        // 用户点击确定后，发送 POST 请求到指定 URL 进行删除操作
        this.$http({
          url: "jiadian/delete",
          method: "post",
          data: ids
        }).then(({ data }) => {
          // 如果响应数据存在且状态码为 0 表示删除成功
          if (data && data.code === 0) {
            // 显示成功提示信息，并在提示信息关闭后重新查询数据列表
            this.$message({
              message: "操作成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.search();
              }
            });
          } else {
            // 删除失败，显示错误提示信息
            this.$message.error(data.msg);
          }
        });
      });
    },

    // 上架/下架操作方法，用于更改商品的上下架状态
    shangxia(id, shangxiaTypes) {
      var msg = "";
      // 根据当前上下架状态进行切换，并设置相应的提示信息
      if (shangxiaTypes == 1) {
        shangxiaTypes = 2;
        msg = "下架";
      } else if (shangxiaTypes == 2) {
        shangxiaTypes = 1;
        msg = "上架";
      }

      // 构造要更新的数据对象
      var shuju = {
        'id': id,
        'shangxiaTypes': shangxiaTypes,
      }

      // 弹出确认对话框，询问用户是否确定进行上架/下架操作
      this.$confirm(`确定要将它` + msg + `吗?`, "提示", {
        confirmButtonText: "确定",
        cancelButtonText: "取消",
        type: "warning"
      }).then(() => {
        // 用户点击确定后，发送 POST 请求到指定 URL 进行更新操作
        this.$http({
          url: "jiadian/update",
          method: "post",
          data: shuju
        }).then(({ data }) => {
          // 如果响应数据存在且状态码为 0 表示操作成功
          if (data && data.code === 0) {
            // 显示成功提示信息，并在提示信息关闭后重新查询数据列表
            this.$message({
              message: msg + "成功",
              type: "success",
              duration: 1500,
              onClose: () => {
                this.search();
              }
            });
          } else {
            // 操作失败，显示错误提示信息
            this.$message.error(data.msg);
          }
        });
      });
    },
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
</style>