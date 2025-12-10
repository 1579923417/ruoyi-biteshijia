<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="90px">
      <el-form-item label="菜单类型">
        <el-select v-model="queryParams.menuType" placeholder="全部" clearable style="width: 180px">
          <el-option v-for="t in menuTypeOptions" :key="t.code" :value="t.code" :label="t.desc" />
        </el-select>
      </el-form-item>
      <el-form-item label="名称">
        <el-input v-model="queryParams.title" placeholder="请输入菜单名称" clearable style="width: 220px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="跳转类型">
        <el-select v-model="queryParams.type" placeholder="全部" clearable style="width: 180px">
          <el-option :value="1" label="内部页面" />
          <el-option :value="2" label="外链" />
          <el-option :value="3" label="功能按钮" />
        </el-select>
      </el-form-item>
      <el-form-item label="状态">
        <el-select v-model="queryParams.status" placeholder="全部" clearable style="width: 160px">
          <el-option :value="1" label="显示" />
          <el-option :value="0" label="隐藏" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openAdd" v-hasPermi="['app:appMenu:add']">新增</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="90" />
      <el-table-column label="菜单类型" align="center" width="150">
        <template slot-scope="scope">{{ scope.row.menuTypeDesc || formatTypeByCode(scope.row.menuType) }}</template>
      </el-table-column>
      <el-table-column label="名称" align="center" prop="title" />
      <el-table-column label="图标" align="center" width="100">
        <template slot-scope="scope">
          <el-image v-if="scope.row.icon" :src="baseUrl + scope.row.icon" style="width:36px;height:36px" fit="cover"></el-image>
          <span v-else>-</span>
        </template>
      </el-table-column>
      <el-table-column label="跳转类型" align="center" width="120">
        <template slot-scope="scope">{{ formatType(scope.row.type) }}</template>
      </el-table-column>
      <el-table-column label="路径" align="center" prop="path" :show-overflow-tooltip="true" />
      <el-table-column label="排序" align="center" prop="sort" width="90" />
      <el-table-column label="状态" align="center" prop="status" width="120">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)" v-hasPermi="['app:appMenu:edit']"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="父级ID" align="center" prop="parentId" width="110" />
      <el-table-column label="操作" align="center" width="220" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openEdit(scope.row)" v-hasPermi="['app:appMenu:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['app:appMenu:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="新增菜单" :visible.sync="addOpen" width="700px" append-to-body>
      <el-form ref="addFormRef" :model="addForm" :rules="rules" label-width="120px">
        <el-form-item label="菜单类型" prop="menuType">
          <el-select v-model="addForm.menuType" placeholder="请选择">
            <el-option v-for="t in menuTypeOptions" :key="t.code" :value="t.code" :label="t.desc" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="title">
          <el-input v-model="addForm.title" placeholder="请输入名称" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <image-upload :value="addUploadValue" :limit="1" :action="'/common/upload'" :file-size="5" :file-type="['png','jpg','jpeg']" @input="onAddUploadChange" />
        </el-form-item>
        <el-form-item label="跳转类型" prop="type">
          <el-select v-model="addForm.type" placeholder="请选择">
            <el-option :value="1" label="内部页面" />
            <el-option :value="2" label="外链" />
            <el-option :value="3" label="功能按钮" />
          </el-select>
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="addForm.path" placeholder="请输入路径或链接" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="addForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="addForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="父级菜单" prop="parentId">
          <el-select v-model="addForm.parentId" placeholder="请选择父级" filterable style="width: 240px">
            <el-option :value="0" label="根菜单" />
            <el-option v-for="r in rootOptions" :key="r.id" :label="r.title" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="addForm.remark" type="textarea" :rows="2" placeholder="备注" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAdd">确 定</el-button>
        <el-button @click="addOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="编辑菜单" :visible.sync="editOpen" width="700px" append-to-body>
      <el-form ref="editFormRef" :model="editForm" :rules="rules" label-width="120px">
        <el-form-item label="菜单类型" prop="menuType">
          <el-select v-model="editForm.menuType" placeholder="请选择">
            <el-option v-for="t in menuTypeOptions" :key="t.code" :value="t.code" :label="t.desc" />
          </el-select>
        </el-form-item>
        <el-form-item label="名称" prop="title">
          <el-input v-model="editForm.title" />
        </el-form-item>
        <el-form-item label="图标" prop="icon">
          <image-upload :value="editUploadValue" :limit="1" :action="'/common/upload'" :file-size="5" :file-type="['png','jpg','jpeg']" @input="onEditUploadChange" />
        </el-form-item>
        <el-form-item label="跳转类型" prop="type">
          <el-select v-model="editForm.type" placeholder="请选择">
            <el-option :value="1" label="内部页面" />
            <el-option :value="2" label="外链" />
            <el-option :value="3" label="功能按钮" />
          </el-select>
        </el-form-item>
        <el-form-item label="路径" prop="path">
          <el-input v-model="editForm.path" />
        </el-form-item>
        <el-form-item label="排序" prop="sort">
          <el-input-number v-model="editForm.sort" :min="0" />
        </el-form-item>
        <el-form-item label="状态" prop="status">
          <el-switch v-model="editForm.status" :active-value="1" :inactive-value="0" />
        </el-form-item>
        <el-form-item label="父级菜单" prop="parentId">
          <el-select v-model="editForm.parentId" placeholder="请选择父级" filterable style="width: 240px">
            <el-option :value="0" label="根菜单" />
            <el-option v-for="r in rootOptions" :key="r.id" :label="r.title" :value="r.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="备注" prop="remark">
          <el-input v-model="editForm.remark" type="textarea" :rows="2" />
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
import { listAppMenu, getAppMenu, addAppMenu, updateAppMenu, delAppMenu, changeAppMenuStatus, listRootAppMenu, listMenuTypes } from '@/api/app/menu.js'
import ImageUpload from '@/components/ImageUpload/index.vue'

export default {
  name: 'AppMenuIndex',
  components: { ImageUpload },
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      loading: false,
      showSearch: true,
      list: [],
      total: 0,
      ids: [],
      queryParams: { pageNum: 1, pageSize: 10, menuType: undefined, title: undefined, type: undefined, status: undefined },
      addOpen: false,
      editOpen: false,
      addForm: { menuType: 1, title: '', icon: '', type: 1, path: '', sort: 0, status: 1, parentId: 0, remark: '' },
      editForm: { id: undefined, menuType: 1, title: '', icon: '', type: 1, path: '', sort: 0, status: 1, parentId: 0, remark: '' },
      rootOptions: [],
      menuTypeOptions: [],
      addUploadValue: '',
      editUploadValue: '',
      rules: {
        menuType: [{ required: true, message: '请选择菜单类型', trigger: 'change' }],
        title: [{ required: true, message: '请输入名称', trigger: 'blur' }],
        type: [{ required: true, message: '请选择跳转类型', trigger: 'change' }]
      }
    }
  },
  created() { this.getList(); this.fetchRootOptions(); this.fetchMenuTypes() },
  methods: {
    fetchRootOptions() { listRootAppMenu({ pageNum: 1, pageSize: 100 }).then(res => { this.rootOptions = res.rows || [] }) },
    fetchMenuTypes() { listMenuTypes().then(res => { this.menuTypeOptions = res.data || [] }) },
    formatTypeByCode(v) { const t = this.menuTypeOptions.find(i => i.code === v); return t ? t.desc : v },
    formatType(v) { if (v === 1) return '内部页面'; if (v === 2) return '外链'; if (v === 3) return '功能按钮'; return v },
    getList() {
      this.loading = true
      listAppMenu(this.queryParams).then(res => { this.list = res.rows || []; this.total = res.total || 0; this.loading = false }).catch(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, menuType: undefined, title: undefined, type: undefined, status: undefined }; this.getList() },
    handleSelectionChange(selection) { this.ids = selection.map(i => i.id) },
    openAdd() { this.addForm = { menuType: 1, title: '', icon: '', type: 1, path: '', sort: 0, status: 1, parentId: 0, remark: '' }; this.addUploadValue = ''; this.addOpen = true },
    openEdit(row) { getAppMenu(row.id).then(res => { this.editForm = Object.assign({}, res.data || {}); this.editForm.icon = this.normalizePath(this.editForm.icon || ''); this.editUploadValue = this.editForm.icon || ''; this.editOpen = true }) },
    submitAdd() {
      this.$refs.addFormRef.validate(valid => { if (!valid) return; addAppMenu(this.addForm).then(() => { this.$modal.msgSuccess('新增成功'); this.addOpen = false; this.getList() }) })
    },
    submitEdit() {
      this.$refs.editFormRef.validate(valid => { if (!valid) return; updateAppMenu(this.editForm).then(() => { this.$modal.msgSuccess('保存成功'); this.editOpen = false; this.getList() }) })
    },
    handleDelete(row) { const id = row.id || this.ids[0]; if (!id) return; this.$modal.confirm('确认删除该菜单吗？').then(() => delAppMenu(id)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() }) },
    handleStatusChange(row) { changeAppMenuStatus(row.id, row.status).then(() => { this.$modal.msgSuccess('状态已更新') }).catch(() => { row.status = row.status === 1 ? 0 : 1 }) },
    onAddUploadChange(val) {
      const path = Array.isArray(val) ? (val[0] || '') : (val || '')
      const norm = this.normalizePath(path)
      this.addUploadValue = norm
      this.addForm.icon = norm
    },
    onEditUploadChange(val) {
      const path = Array.isArray(val) ? (val[0] || '') : (val || '')
      const norm = this.normalizePath(path)
      this.editUploadValue = norm
      this.editForm.icon = norm
    },
    normalizePath(p) {
      if (!p) return ''
      const base = this.baseUrl || ''
      if (base && p.indexOf(base) === 0) return p.substring(base.length)
      try {
        const u = new URL(p)
        return (u.pathname || '') + (u.search || '')
      } catch (e) {
        return p
      }
    },
    handleUploadError(err) { this.$modal.msgError('图标上传失败：' + (err && err.message ? err.message : '网络或权限问题')) },
    handlePreview(file) { this.previewUrl = file.url || (file.response && file.response.url) || ''; if (this.previewUrl) this.previewOpen = true },
    handleRemove(formType, file, fileList) { if (formType === 'add') { this.addForm.icon = ''; this.addFileList = [] } else { this.editForm.icon = ''; this.editFileList = [] } }
  }
}
</script>

<style scoped>
</style>
