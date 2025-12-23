<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="名称">
        <el-input v-model="queryParams.name" placeholder="请输入矿场名称" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="地区">
        <el-input v-model="queryParams.location" placeholder="请输入地区" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="能源">
        <el-select v-model="queryParams.energyType" placeholder="请选择能源类型" clearable style="width: 160px">
          <el-option label="水电" value="水电" />
          <el-option label="火电" value="火电" />
          <el-option label="其他" value="其他" />
        </el-select>
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
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openAdd" v-hasPermi="['admin:minerFarm:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" prop="id" align="center" width="80" />
      <el-table-column label="名称" prop="name" align="center"  />
      <el-table-column label="地区" prop="location" align="center"  />
      <el-table-column label="能源" prop="energyType" align="center"  />
      <el-table-column label="丰水期价" prop="priceWetSeason" align="center" />
      <el-table-column label="枯水期价" prop="priceDrySeason" align="center"  />
      <el-table-column label="全年价" prop="priceAllYear" align="center" />
      <el-table-column label="状态" prop="status" align="center" >
        <template slot-scope="scope">
          <el-tag :type="scope.row.status===1?'success':'info'">{{ scope.row.status===1?'展示':'隐藏' }}</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" prop="updateTime" align="center" width="180">
        <template slot-scope="scope">{{ formatTime(scope.row.updateTime) }}</template>
      </el-table-column>
      <el-table-column label="操作" align="center" width="180" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openEdit(scope.row)" v-hasPermi="['admin:minerFarm:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['admin:minerFarm:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="新增矿场" :visible.sync="addOpen" width="600px" append-to-body>
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="120px">
        <el-form-item label="名称" prop="name"><el-input v-model="addForm.name" /></el-form-item>
        <el-form-item label="地区" prop="location"><el-input v-model="addForm.location" /></el-form-item>
        <el-form-item label="能源" prop="energyType">
          <el-select v-model="addForm.energyType" placeholder="请选择能源"><el-option label="水电" value="水电" /><el-option label="火电" value="火电" /><el-option label="其他" value="其他" /></el-select>
        </el-form-item>
        <el-form-item label="丰水期价"><el-input-number v-model="addForm.priceWetSeason" :precision="3" :step="0.001" :min="0" /></el-form-item>
        <el-form-item label="枯水期价"><el-input-number v-model="addForm.priceDrySeason" :precision="3" :step="0.001" :min="0" /></el-form-item>
        <el-form-item label="全年价"><el-input-number v-model="addForm.priceAllYear" :precision="3" :step="0.001" :min="0" /></el-form-item>
        <el-form-item label="状态" prop="status"><el-switch v-model="addForm.status" :active-value="1" :inactive-value="0" /></el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAdd">保 存</el-button>
        <el-button @click="addOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="编辑矿场" :visible.sync="editOpen" width="600px" append-to-body>
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="120px">
        <el-form-item label="名称" prop="name"><el-input v-model="editForm.name" /></el-form-item>
        <el-form-item label="地区" prop="location"><el-input v-model="editForm.location" /></el-form-item>
        <el-form-item label="能源" prop="energyType">
          <el-select v-model="editForm.energyType" placeholder="请选择能源"><el-option label="水电" value="水电" /><el-option label="火电" value="火电" /><el-option label="其他" value="其他" /></el-select>
        </el-form-item>
        <el-form-item label="丰水期价"><el-input-number v-model="editForm.priceWetSeason" :precision="3" :step="0.001" :min="0" /></el-form-item>
        <el-form-item label="枯水期价"><el-input-number v-model="editForm.priceDrySeason" :precision="3" :step="0.001" :min="0" /></el-form-item>
        <el-form-item label="全年价"><el-input-number v-model="editForm.priceAllYear" :precision="3" :step="0.001" :min="0" /></el-form-item>
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
import { listMinerFarm, addMinerFarm, updateMinerFarm, delMinerFarm } from '@/api/global/miner'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'AdminGlobalMiner',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      ids: [],
      queryParams: { pageNum: 1, pageSize: 10, name: undefined, location: undefined, energyType: undefined, status: undefined },
      addOpen: false,
      addForm: { name: '', location: '', energyType: '', priceWetSeason: 0, priceDrySeason: 0, priceAllYear: 0, status: 1 },
      addRules: { name: [{ required: true, message: '请输入名称', trigger: 'blur' }], location: [{ required: true, message: '请输入地区', trigger: 'blur' }] },
      editOpen: false,
      editForm: { id: undefined, name: '', location: '', energyType: '', priceWetSeason: 0, priceDrySeason: 0, priceAllYear: 0, status: 1 },
      editRules: { name: [{ required: true, message: '请输入名称', trigger: 'blur' }], location: [{ required: true, message: '请输入地区', trigger: 'blur' }] }
    }
  },
  created() { this.getList() },
  methods: {
    getList() {
      this.loading = true
      listMinerFarm(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, name: undefined, location: undefined, energyType: undefined, status: undefined }; this.getList() },
    formatTime(val) { return parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') },
    handleSelectionChange(selection) { this.ids = selection.map(i => i.id) },
    openAdd() { this.addForm = { name: '', location: '', energyType: '', priceWetSeason: 0, priceDrySeason: 0, priceAllYear: 0, status: 1 }; this.addOpen = true },
    submitAdd() {
      this.$refs.addFormRef.validate(valid => { if (!valid) return; addMinerFarm(this.addForm).then(() => { this.$modal.msgSuccess('保存成功'); this.addOpen = false; this.getList() }) })
    },
    openEdit(row) { this.editForm = Object.assign({}, row); this.editOpen = true },
    submitEdit() {
      this.$refs.editFormRef.validate(valid => { if (!valid) return; updateMinerFarm(this.editForm).then(() => { this.$modal.msgSuccess('保存成功'); this.editOpen = false; this.getList() }) })
    },
    handleDelete(row) {
      const ids = row.id ? row.id : (this.ids.join(','))
      if (!ids) return
      this.$modal.confirm('确认删除所选数据吗？').then(() => delMinerFarm(ids)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() })
    }
  }
}
</script>

<style scoped>
</style>

