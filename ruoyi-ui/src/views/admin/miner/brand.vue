<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="品牌名称">
        <el-input v-model="queryParams.brandName" placeholder="请输入品牌名称" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openAdd" v-hasPermi="['miner:minerBrand:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="100" />
      <el-table-column label="品牌名称" align="center" prop="brandName" />
      <el-table-column label="价格(¥)" align="center" prop="price" width="120" />
      <el-table-column label="算力(TH/s)" align="center" prop="hashRate" width="140" />
      <el-table-column label="功耗(kW·h)" align="center" prop="powerPerHour" width="140" />
      <el-table-column label="日产出" align="center" prop="dailyYield" width="120" />
      <el-table-column label="创建时间" prop="createTime" align="center" width="180">
        <template slot-scope="scope">
          {{ formatTime(scope.row.createTime) }}
        </template>
      </el-table-column>
      <el-table-column label="更新时间" prop="updateTime" align="center" width="180">
        <template slot-scope="scope">
          {{ formatTime(scope.row.updateTime) }}
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openEdit(scope.row)" v-hasPermi="['miner:minerBrand:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['miner:minerBrand:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 新增 -->
    <el-dialog title="新增矿机品牌" :visible.sync="addOpen" width="600px" append-to-body>
      <el-form ref="addFormRef" :model="addForm" :rules="rules" label-width="120px">
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="addForm.brandName" placeholder="请输入品牌名称" />
        </el-form-item>
        <el-form-item label="价格(¥)" prop="price">
          <el-input-number v-model="addForm.price" :precision="2" :step="0.01" :min="0" />
        </el-form-item>
        <el-form-item label="算力(TH/s)" prop="hashRate">
          <el-input-number v-model="addForm.hashRate" :precision="2" :step="0.01" :min="0" />
        </el-form-item>
        <el-form-item label="功耗(kW·h)" prop="powerPerHour">
          <el-input-number v-model="addForm.powerPerHour" :precision="2" :step="0.01" :min="0" />
        </el-form-item>
        <el-form-item label="日产出" prop="dailyYield">
          <el-input-number v-model="addForm.dailyYield" :precision="6" :step="0.000001" :min="0" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAdd">确 定</el-button>
        <el-button @click="addOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 编辑 -->
    <el-dialog title="编辑矿机品牌" :visible.sync="editOpen" width="600px" append-to-body>
      <el-form ref="editFormRef" :model="editForm" :rules="rules" label-width="120px">
        <el-form-item label="品牌名称" prop="brandName">
          <el-input v-model="editForm.brandName" />
        </el-form-item>
        <el-form-item label="价格(¥)" prop="price">
          <el-input-number v-model="editForm.price" :precision="2" :step="0.01" :min="0" />
        </el-form-item>
        <el-form-item label="算力(TH/s)" prop="hashRate">
          <el-input-number v-model="editForm.hashRate" :precision="2" :step="0.01" :min="0" />
        </el-form-item>
        <el-form-item label="功耗(kW·h)" prop="powerPerHour">
          <el-input-number v-model="editForm.powerPerHour" :precision="2" :step="0.01" :min="0" />
        </el-form-item>
        <el-form-item label="日产出" prop="dailyYield">
          <el-input-number v-model="editForm.dailyYield" :precision="6" :step="0.000001" :min="0" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitEdit">保 存</el-button>
        <el-button @click="editOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
  </template>

<script>
import { listBrand, getBrand, addBrand, updateBrand, delBrand } from '@/api/miner/brand.js'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'MinerBrandIndex',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      ids: [],
      queryParams: { pageNum: 1, pageSize: 10, brandName: undefined },
      addOpen: false,
      editOpen: false,
      addForm: { brandName: '', price: 0, hashRate: 0, powerPerHour: 0, dailyYield: 0 },
      editForm: { id: undefined, brandName: '', price: 0, hashRate: 0, powerPerHour: 0, dailyYield: 0 },
      rules: {
        brandName: [{ required: true, message: '请输入品牌名称', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBrand(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, brandName: undefined }; this.getList() },
    openAdd() { this.addForm = { brandName: '', price: 0, hashRate: 0, powerPerHour: 0, dailyYield: 0 }; this.addOpen = true },
    openEdit(row) {
      getBrand(row.id).then(res => { this.editForm = Object.assign({}, res.data || {}); this.editOpen = true })
    },
    submitAdd() {
      this.$refs.addFormRef.validate(valid => { if (!valid) return; addBrand(this.addForm).then(() => { this.$modal.msgSuccess('新增成功'); this.addOpen = false; this.getList() }) })
    },
    submitEdit() {
      this.$refs.editFormRef.validate(valid => { if (!valid) return; updateBrand(this.editForm).then(() => { this.$modal.msgSuccess('保存成功'); this.editOpen = false; this.getList() }) })
    },
    handleDelete(row) {
      const id = row.id || this.ids[0]
      if (!id) return
      this.$modal.confirm('确认删除该品牌吗？').then(() => delBrand(id)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() })
    },
    handleSelectionChange(selection) { this.ids = selection.map(i => i.id) },
    formatTime(val) { return parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') }
  }
}
</script>

<style scoped>
</style>
