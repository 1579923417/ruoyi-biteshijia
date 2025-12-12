<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="名称">
        <el-input v-model="queryParams.name" placeholder="请输入名称" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="请选择状态" clearable style="width: 160px">
          <el-option label="展示" :value="1" />
          <el-option label="隐藏" :value="0" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openAdd" v-hasPermi="['admin:globalPowerInfo:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" prop="id" align="center" width="80" />
      <el-table-column label="名称" prop="name" align="center" width="180" />
      <el-table-column label="具体地区" prop="country" align="center" width="220" />
      <el-table-column label="电价(CNY/度)" prop="powerPriceCny" align="center" width="140" />
      <el-table-column label="供电时间" prop="supplyPeriod" align="center" width="120" />
      <el-table-column label="状态" prop="status" align="center" width="100">
        <template slot-scope="scope">
          <el-tag :type="scope.row.status===1?'success':'info'">{{ scope.row.status===1?'展示':'隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" prop="updateTime" align="center" width="180">
        <template slot-scope="scope">{{ formatTime(scope.row.updateTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openEdit(scope.row)" v-hasPermi="['admin:globalPowerInfo:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['admin:globalPowerInfo:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="新增电力信息" :visible.sync="addOpen" width="600px" append-to-body>
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="120px">
        <el-form-item label="名称" prop="name"><el-input v-model="addForm.name" /></el-form-item>
      <el-form-item label="具体地区" prop="country"><el-input v-model="addForm.country" /></el-form-item>
        <el-form-item label="电价(CNY/度)" prop="powerPriceCny"><el-input-number v-model="addForm.powerPriceCny" :precision="3" :step="0.001" :min="0" /></el-form-item>
        <el-form-item label="供电时间" prop="supplyPeriod"><el-input v-model="addForm.supplyPeriod" /></el-form-item>
        <el-form-item label="状态" prop="status"><el-switch v-model="addForm.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAdd">保 存</el-button>
        <el-button @click="addOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="编辑电力信息" :visible.sync="editOpen" width="600px" append-to-body>
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="120px">
        <el-form-item label="名称" prop="name"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="具体地区" prop="country"><el-input v-model="editForm.country" /></el-form-item>
        <el-form-item label="电价(CNY/度)" prop="powerPriceCny"><el-input-number v-model="editForm.powerPriceCny" :precision="3" :step="0.001" :min="0" /></el-form-item>
        <el-form-item label="供电时间" prop="supplyPeriod"><el-input v-model="editForm.supplyPeriod" /></el-form-item>
        <el-form-item label="状态" prop="status"><el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitEdit">保 存</el-button>
        <el-button @click="editOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
  </template>

<script>
import { listGlobalPower, addGlobalPower, updateGlobalPower, delGlobalPower } from '@/api/global/power'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'AdminGlobalPower',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      ids: [],
      queryParams: { pageNum: 1, pageSize: 10, name: undefined, status: undefined },
      addOpen: false,
      addForm: { name: '', country: '', powerPriceCny: 0, supplyPeriod: '全年', status: 1 },
      addRules: { country: [{ required: true, message: '请输入国家或地区', trigger: 'blur' }], powerPriceCny: [{ required: true, message: '请输入电价', trigger: 'change' }] },
      editOpen: false,
      editForm: { id: undefined, name: '', country: '', powerPriceCny: 0, supplyPeriod: '全年', status: 1 },
      editRules: { country: [{ required: true, message: '请输入国家或地区', trigger: 'blur' }], powerPriceCny: [{ required: true, message: '请输入电价', trigger: 'change' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listGlobalPower(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, name: undefined, status: undefined }; this.getList() },
    formatTime(val) { return parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') },
    handleSelectionChange(selection) { this.ids = selection.map(i => i.id) },
    openAdd() { this.addForm = { country: '', locationDesc: '', powerPriceCny: 0, supplyPeriod: '全年', status: 1 }; this.addOpen = true },
    submitAdd() {
      this.$refs.addFormRef.validate(valid => { if (!valid) return; addGlobalPower(this.addForm).then(() => { this.$modal.msgSuccess('保存成功'); this.addOpen = false; this.getList() }) })
    },
    openEdit(row) { this.editForm = Object.assign({}, row); this.editOpen = true },
    submitEdit() {
      this.$refs.editFormRef.validate(valid => { if (!valid) return; updateGlobalPower(this.editForm).then(() => { this.$modal.msgSuccess('保存成功'); this.editOpen = false; this.getList() }) })
    },
    handleDelete(row) {
      const ids = row.id ? row.id : (this.ids.join(','))
      if (!ids) return
      this.$modal.confirm('确认删除所选数据吗？').then(() => delGlobalPower(ids)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() })
    }
  }
}
</script>

<style scoped>
</style>
