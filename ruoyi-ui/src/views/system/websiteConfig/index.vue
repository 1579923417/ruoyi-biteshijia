<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="分组" prop="configGroup">
        <el-select v-model="queryParams.configGroup" placeholder="请选择分组" clearable style="width: 200px">
          <el-option v-for="g in groupOptions" :key="g.value" :label="g.label" :value="g.value" />
        </el-select>
      </el-form-item>
      <el-form-item label="键名" prop="configKey">
        <el-input v-model="queryParams.configKey" placeholder="支持模糊查询" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="类型" prop="configType">
        <el-select v-model="queryParams.configType" placeholder="类型" clearable style="width: 160px">
          <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
        </el-select>
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="handleAdd" v-hasPermi="['system:websiteConfig:add']">新增</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="success" plain icon="el-icon-edit" size="mini" :disabled="single" @click="handleUpdate" v-hasPermi="['system:websiteConfig:edit']">修改</el-button>
      </el-col>
      <el-col :span="1.5">
        <el-button type="danger" plain icon="el-icon-delete" size="mini" :disabled="multiple" @click="handleDelete" v-hasPermi="['system:websiteConfig:remove']">删除</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList" />
    </el-row>

    <el-table v-loading="loading" :data="list" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="55" align="center" />
      <el-table-column label="ID" align="center" prop="id" width="80" />
      <el-table-column label="分组" align="center" prop="configGroup" width="120" />
      <el-table-column label="键名" align="center" prop="configKey" :show-overflow-tooltip="true" />
      <el-table-column label="类型" align="center" prop="configType" width="120" />
      <el-table-column label="值" align="center" prop="configValue" :show-overflow-tooltip="true" />
      <el-table-column label="说明" align="center" prop="description" :show-overflow-tooltip="true" />
      <el-table-column label="创建时间" align="center" prop="createTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.createTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="更新时间" align="center" prop="updateTime" width="180">
        <template slot-scope="scope">
          <span>{{ parseTime(scope.row.updateTime) }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" align="center" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="handleUpdate(scope.row)" v-hasPermi="['system:websiteConfig:edit']">修改</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['system:websiteConfig:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total>0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <el-dialog :title="title" :visible.sync="open" width="600px" append-to-body>
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="分组" prop="configGroup">
          <el-select v-model="form.configGroup" placeholder="请选择分组">
            <el-option v-for="g in groupOptions" :key="g.value" :label="g.label" :value="g.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="键后缀" prop="configKey">
          <el-input v-model="form.configKey" placeholder="不带前缀，如 expire_time" />
        </el-form-item>
        <el-form-item label="类型" prop="configType">
          <el-select v-model="form.configType" placeholder="类型">
            <el-option v-for="t in typeOptions" :key="t.value" :label="t.label" :value="t.value" />
          </el-select>
        </el-form-item>
        <el-form-item label="配置值" prop="configValue" v-if="form.configType !== 'image'">
          <el-input v-model="form.configValue" type="textarea" :rows="3" placeholder="配置值" />
        </el-form-item>
        <el-form-item label="上传图片" prop="configValue" v-else>
          <image-upload :value="form.configValue" :limit="1" :action="'/common/upload'" :file-size="5" :file-type="['png','jpg','jpeg']" @input="onImageChange" />
        </el-form-item>
        <el-form-item label="说明" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="2" placeholder="说明" disabled />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确 定</el-button>
        <el-button @click="cancel">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listWebsiteConfig, getWebsiteConfig, addWebsiteConfig, updateWebsiteConfig, delWebsiteConfig, listWebsiteConfigGroups } from '@/api/system/websiteConfig'
import ImageUpload from '@/components/ImageUpload/index.vue'

export default {
  name: 'WebsiteConfig',
  components: { ImageUpload },
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      loading: true,
      ids: [],
      single: true,
      multiple: true,
      showSearch: true,
      total: 0,
      list: [],
      title: '',
      open: false,
      groupOptions: [],
      typeOptions: [
        { label: 'string', value: 'string' },
        { label: 'number', value: 'number' },
        { label: 'image', value: 'image' },
        { label: 'json', value: 'json' },
        { label: 'text', value: 'text' }
      ],
      queryParams: {
        pageNum: 1,
        pageSize: 10,
        configGroup: undefined,
        configKey: undefined,
        configType: undefined
      },
      form: {},
      rules: {
        configGroup: [{ required: true, message: '请选择分组', trigger: 'change' }],
        configKey: [{ required: true, message: '请输入键后缀', trigger: 'blur' }],
        configValue: [{ required: true, message: '请输入配置值', trigger: 'blur' }]
      }
    }
  },
  created() {
    this.getGroups()
    this.getList()
  },
  methods: {
    getGroups() {
      listWebsiteConfigGroups().then(res => {
        this.groupOptions = res.data || []
      })
    },
    getList() {
      this.loading = true
      listWebsiteConfig(this.queryParams).then(res => {
        this.list = res.rows
        this.total = res.total
        this.loading = false
      })
    },
    handleQuery() {
      this.queryParams.pageNum = 1
      this.getList()
    },
    resetQuery() {
      this.queryParams = { pageNum: 1, pageSize: 10, configGroup: undefined, configKey: undefined, configType: undefined }
      this.getList()
    },
    handleSelectionChange(selection) {
      this.ids = selection.map(item => item.id)
      this.single = selection.length !== 1
      this.multiple = selection.length === 0
    },
    handleAdd() {
      this.form = { configGroup: undefined, configKey: undefined, configType: 'string', configValue: '', description: '' }
      this.title = '新增配置'
      this.open = true
    },
    handleUpdate(row) {
      const id = row.id || this.ids[0]
      if (!id) return
      getWebsiteConfig(id).then(res => {
        this.form = Object.assign({}, res.data)
        this.title = '修改配置'
        this.open = true
      })
    },
    submitForm() {
      this.$refs['form'].validate(valid => {
        if (!valid) return
        const payload = Object.assign({}, this.form)
        delete payload.description
        if (!payload.id) {
          addWebsiteConfig(payload).then(() => {
            this.$modal.msgSuccess('新增成功')
            this.open = false
            this.getList()
          })
        } else {
          updateWebsiteConfig(payload).then(() => {
            this.$modal.msgSuccess('修改成功')
            this.open = false
            this.getList()
          })
        }
      })
    },
    onImageChange(val) {
      this.form.configValue = Array.isArray(val) ? (val[0] || '') : val
    },
    cancel() {
      this.open = false
      this.form = {}
    },
    handleDelete(row) {
      const ids = row.id ? [row.id] : this.ids
      if (!ids || ids.length === 0) return
      this.$modal.confirm('是否确认删除选中的数据?').then(() => {
        return delWebsiteConfig(ids.join(','))
      }).then(() => {
        this.getList()
        this.msgSuccess('删除成功')
      }).catch(() => {})
    }
  }
}
</script>

<style scoped>
</style>
