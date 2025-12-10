<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="关联码">
        <el-input v-model="queryParams.apiCode" placeholder="请输入矿机关联码" clearable style="width: 220px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="品牌">
        <el-select v-model="queryParams.brandId" placeholder="请选择品牌" clearable style="width: 220px">
          <el-option v-for="b in brandOptions" :key="b.id" :label="b.brandName" :value="b.id" />
        </el-select>
      </el-form-item>
      <el-form-item label="用户ID">
        <el-input v-model="queryParams.userId" placeholder="请输入用户ID" clearable style="width: 220px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openAdd" v-hasPermi="['app:appUserMiner:add']">添加矿机</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="warning" plain icon="el-icon-edit" size="mini" :disabled="ids.length===0" @click="openBatchEdit" v-hasPermi="['app:appUserMiner:edit']">批量编辑</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="矿机ID" prop="id" align="center" width="100" />
      <el-table-column label="关联码" prop="apiCode" align="center" width="180" />
      <el-table-column label="品牌名称" prop="brandName" align="center" width="180" />
      <el-table-column label="用户ID" prop="userId" align="center" width="120" />
      <el-table-column label="管理费比率" prop="managementFeeRate" align="center" width="140" />
      <el-table-column label="累计已挖" prop="totalMined" align="center" width="140" />
      <el-table-column label="昨日已挖" prop="yesterdayMined" align="center" width="140" />
      <el-table-column label="今日已挖" prop="todayMined" align="center" width="140" />
      <el-table-column label="累计收益" prop="totalIncome" align="center" width="140" />
      <el-table-column label="启用状态" align="center" prop="status" width="120">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)" v-hasPermi="['app:appUserMiner:edit']"></el-switch>
        </template>
      </el-table-column>
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
      <el-table-column label="操作" align="center" width="200" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openEdit(scope.row)" v-hasPermi="['app:appUserMiner:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['app:appUserMiner:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />
    <el-dialog title="编辑矿机" :visible.sync="editOpen" width="600px" append-to-body>
      <el-form ref="editFormRef" :model="editForm" :rules="editRules" label-width="120px">
        <el-form-item label="矿机关联码" prop="apiCode">
          <el-input v-model="editForm.apiCode" placeholder="请输入矿机关联码" />
        </el-form-item>
        <el-form-item label="关联用户" prop="userId">
          <el-select v-model="editForm.userId" placeholder="请选择关联用户" clearable filterable style="width: 300px">
            <el-option v-for="u in userOptions" :key="u.id" :label="u.name + ' (' + u.phone + ') '" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理费比率" prop="managementFeeRate">
          <el-input-number v-model="editForm.managementFeeRate" :precision="2" :step="0.01" :min="0" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitEdit">保 存</el-button>
        <el-button @click="editOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="批量编辑矿机" :visible.sync="batchOpen" width="600px" append-to-body>
      <el-form ref="batchFormRef" :model="batchForm" :rules="batchRules" label-width="120px">
        <el-form-item label="关联用户" prop="userId">
          <el-select v-model="batchForm.userId" placeholder="请选择关联用户" clearable filterable style="width: 300px">
            <el-option v-for="u in userOptions" :key="u.id" :label="u.name + ' (' + u.phone + ') '" :value="u.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="管理费比率" prop="managementFeeRate">
          <el-input-number v-model="batchForm.managementFeeRate" :precision="2" :step="0.01" :min="0" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitBatchEdit">确 定</el-button>
        <el-button @click="batchOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="批量添加矿机" :visible.sync="addOpen" width="600px" append-to-body>
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="120px">
        <el-form-item label="矿机品牌" prop="brandId">
          <el-select v-model="addForm.brandId" placeholder="请选择品牌" filterable clearable style="width: 300px">
            <el-option v-for="b in brandOptions" :key="b.id" :label="b.brandName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="添加数量" prop="count">
          <el-input-number v-model="addForm.count" :min="1" :step="1" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAdd">确 定</el-button>
        <el-button @click="addOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAppUser, listUserMiner, updateUserMiner, addUserMiner, bindMiner, changeMinerStatus, delUserMiner } from '@/api/app/user.js'
import { parseTime } from '@/utils/ruoyi'
import { listBrand } from '@/api/miner/brand.js'

export default {
  name: 'AdminMinerList',
  data() {
    return {
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      ids: [],
      queryParams: { pageNum: 1, pageSize: 10, apiCode: undefined, brandId: undefined, userId: undefined, status: undefined },
      brandOptions: [],
      userOptions: [],
      editOpen: false,
      editForm: { id: undefined, apiCode: '', userId: undefined, managementFeeRate: 0 },
      editRules: { apiCode: [{ required: true, message: '请输入矿机关联码', trigger: 'blur' }] },
      batchOpen: false,
      batchForm: { userId: undefined, managementFeeRate: 0 },
      batchRules: {},
      addOpen: false,
      addForm: { brandId: undefined, count: 1 },
      addRules: { brandId: [{ required: true, message: '请选择品牌', trigger: 'change' }], count: [{ required: true, message: '请输入数量', trigger: 'change' }] }
    }
  },
  created() {
    this.fetchBrandOptions()
    this.fetchUserOptions()
    this.getList()
  },
  methods: {
    fetchBrandOptions() {
      listBrand({ pageNum: 1, pageSize: 100 }).then(res => { this.brandOptions = res.rows || [] })
    },
    fetchUserOptions() {
      listAppUser({ pageNum: 1, pageSize: 100 }).then(res => { this.userOptions = res.rows || [] })
    },
    getList() {
      this.loading = true
      listUserMiner(this.queryParams).then(res => {
        this.list = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, apiCode: undefined, brandId: undefined, userId: undefined, status: undefined }; this.getList() }
    ,
    formatTime(val) { return parseTime(val, '{y}-{m}-{d} {h}:{i}:{s}') },
    handleSelectionChange(selection) { this.ids = selection.map(i => i.id) },
    openEdit(row) {
      this.editForm = { id: row.id, apiCode: row.apiCode, userId: row.userId, managementFeeRate: row.managementFeeRate }
      this.editOpen = true
    },
    submitEdit() {
      this.$refs.editFormRef.validate(valid => {
        if (!valid) return
        const payload = { id: this.editForm.id, apiCode: this.editForm.apiCode, managementFeeRate: this.editForm.managementFeeRate }
        updateUserMiner(payload).then(() => {
          if (this.editForm.userId) {
            return bindMiner(this.editForm.id, this.editForm.userId)
          }
        }).then(() => {
          this.$modal.msgSuccess('保存成功')
          this.editOpen = false
          this.getList()
        })
      })
    },
    openBatchEdit() { this.batchForm = { userId: undefined, managementFeeRate: 0 }; this.batchOpen = true },
    submitBatchEdit() {
      const tasks = []
      const rate = this.batchForm.managementFeeRate
      const userId = this.batchForm.userId
      this.ids.forEach(id => {
        if (rate !== undefined && rate !== null) {
          tasks.push(updateUserMiner({ id, managementFeeRate: rate }))
        }
        if (userId) {
          tasks.push(bindMiner(id, userId))
        }
      })
      Promise.all(tasks).then(() => { this.$modal.msgSuccess('批量保存成功'); this.batchOpen = false; this.getList() })
    },
    openAdd() { this.addForm = { brandId: undefined, count: 1 }; this.addOpen = true },
    submitAdd() {
      this.$refs.addFormRef.validate(valid => {
        if (!valid) return
        const promises = []
        for (let i = 0; i < this.addForm.count; i++) {
          const apiCode = `M-${this.addForm.brandId}-${Date.now()}-${i+1}`
          promises.push(addUserMiner({ brandId: this.addForm.brandId, apiCode }))
        }
        Promise.all(promises).then(() => { this.$modal.msgSuccess('添加成功'); this.addOpen = false; this.getList() })
      })
    },
    handleDelete(row) {
      const id = row.id || this.ids[0]
      if (!id) return
      this.$modal.confirm('确认删除该矿机吗？').then(() => delUserMiner(id)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() })
    },
    handleStatusChange(row) {
      changeMinerStatus(row.id, row.status).then(() => {
        this.$modal.msgSuccess('状态已更新')
      }).catch(() => {
        row.status = row.status === 1 ? 0 : 1
      })
    }
  }
}
</script>

<style scoped>
</style>
