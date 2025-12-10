<template>
  <div class="app-container">
    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:banner:add']">添加</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-refresh" size="mini" @click="getList" v-hasPermi="['system:banner:list']">刷新</el-button>
      </el-col>
    </el-row>

    <el-table v-loading="loading" :data="bannerList" border>
      <el-table-column label="序号" type="index" width="60" align="center" />
      <el-table-column label="预览" align="center" width="220">
        <template slot-scope="scope">
          <el-image v-if="scope.row.imageUrl" :src="baseUrl + scope.row.imageUrl" :preview-src-list="[baseUrl + scope.row.imageUrl]" style="width:200px;height:80px;" fit="cover" />
          <span v-else style="color:#999">未上传</span>
        </template>
      </el-table-column>
      <el-table-column label="排序" align="center" prop="sort" width="100" />
      <el-table-column label="启用状态" align="center" prop="status" width="120">
        <template slot-scope="scope">
          <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" @change="handleStatusChange(scope.row)" v-hasPermi="['system:banner:edit']"></el-switch>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-picture" @click="openUpload(scope.row)" v-hasPermi="['system:banner:edit']">更换/上传</el-button>
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openEdit(scope.row)" v-hasPermi="['system:banner:edit']">编辑</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['system:banner:remove']" v-if="bannerList && bannerList.length > 1">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog title="上传Banner" :visible.sync="uploadOpen" width="600px" append-to-body>
      <div>
        <image-upload :value="uploadValue" :limit="1" :action="'/common/upload'" :file-size="5" :file-type="['png','jpg','jpeg']" @input="onUploadChange" />
      </div>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitUpload">确 定</el-button>
        <el-button @click="uploadOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <el-dialog title="编辑Banner" :visible.sync="editOpen" width="500px" append-to-body>
      <el-form :model="editForm" label-width="80px">
        <el-form-item label="排序">
          <el-input-number v-model="editForm.sort" :min="1" :step="1" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitEdit">确 定</el-button>
        <el-button @click="editOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
  
</template>

<script>
import { listBanner, addBanner, updateBanner, delBanner, changeStatus } from '@/api/app/banner'
import ImageUpload from '@/components/ImageUpload/index.vue'

export default {
  name: 'Banner',
  components: { ImageUpload },
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      loading: false,
      bannerList: [],
      total: 0,
      queryParams: {
        pageNum: 1,
        pageSize: 10
      },
      uploadOpen: false,
      uploadValue: '',
      currentRow: null,
      editOpen: false,
      editForm: { id: undefined, sort: 1, status: '0' }
    }
  },
  created() {
    this.getList()
  },
  methods: {
    getList() {
      this.loading = true
      listBanner(this.queryParams).then(res => {
        this.bannerList = res.rows || []
        this.total = res.total || (this.bannerList ? this.bannerList.length : 0)
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleAdd() {
      const nextSort = (this.bannerList && this.bannerList.length) ? Math.max(...this.bannerList.map(b => b.sort || 0)) + 1 : 1
      const data = { imageUrl: '', sort: nextSort, status: '0' }
      addBanner(data).then(() => {
        this.$modal.msgSuccess('新增成功')
        this.getList()
      })
    },
    openUpload(row) {
      this.currentRow = row
      this.uploadValue = row.imageUrl || ''
      this.uploadOpen = true
    },
    openEdit(row) {
      this.editForm = { id: row.id || row.bannerId, sort: row.sort || 1, status: String(row.status == null ? '0' : row.status) }
      this.editOpen = true
    },
    onUploadChange(val) {
      this.uploadValue = Array.isArray(val) ? (val[0] || '') : val
    },
    submitUpload() {
      if (!this.currentRow) return
      const payload = Object.assign({}, this.currentRow, { imageUrl: this.uploadValue })
      updateBanner(payload).then(() => {
        this.$modal.msgSuccess('更新成功')
        this.uploadOpen = false
        this.getList()
      })
    },
    submitEdit() {
      const payload = { id: this.editForm.id, sort: this.editForm.sort }
      updateBanner(payload).then(() => {
        return changeStatus(this.editForm.id, this.editForm.status)
      }).then(() => {
        this.$modal.msgSuccess('更新成功')
        this.editOpen = false
        this.getList()
      })
    },
    handleStatusChange(row) {
      const id = row.bannerId || row.id
      const status = row.status
      changeStatus(id, status).then(() => {
        this.$modal.msgSuccess('状态更新成功')
      }).catch(() => {
        row.status = row.status === '0' ? '1' : '0'
      })
    },
    handleDelete(row) {
      if (this.bannerList.length <= 1) {
        return
      }
      this.$modal.confirm('确认删除该banner吗？').then(() => {
        const id = row.bannerId || row.id
        return delBanner(id)
      }).then(() => {
        this.$modal.msgSuccess('删除成功')
        this.getList()
      })
    }
  }
}
</script>

<style scoped>
.el-image { border: 1px solid #eee; }
</style>
