<template>
  <div class="app-container">
    <el-form :model="queryParams" ref="queryForm" size="small" :inline="true" v-show="showSearch" label-width="80px">
      <el-form-item label="关键词">
        <el-input v-model="queryParams.keyword" placeholder="用户名称关键词" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item label="手机号">
        <el-input v-model="queryParams.phone" placeholder="请输入手机号" clearable style="width: 240px" @keyup.enter.native="handleQuery" />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" size="mini" @click="handleQuery">搜索</el-button>
        <el-button icon="el-icon-refresh" size="mini" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row :gutter="10" class="mb8">
      <el-col :span="1.5">
        <el-button type="primary" plain icon="el-icon-plus" size="mini" @click="openAdd" v-hasPermi="['app:appUser:add']">添加用户</el-button>
      </el-col>
      <right-toolbar :showSearch.sync="showSearch" @queryTable="getList"></right-toolbar>
    </el-row>

    <el-table v-loading="loading" :data="userList" @selection-change="handleSelectionChange">
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column label="名称" align="center" prop="name" />
      <el-table-column label="头像" align="center" width="160">
        <template slot-scope="scope">
          <el-image v-if="scope.row.avatar" :src="baseUrl + scope.row.avatar" :preview-src-list="[baseUrl + scope.row.avatar]" style="width:50px;height:50px;" fit="cover" />
          <span v-else style="color:#999">无头像</span>
        </template>
      </el-table-column>
      <el-table-column label="手机号" align="center" prop="phone" width="140" />
      <el-table-column label="开户行" align="center" prop="bankName" :show-overflow-tooltip="true" />
      <el-table-column label="账户号码" align="center" prop="bankAccount" :show-overflow-tooltip="true" />
      <el-table-column label="矿机数" align="center" prop="minerCount" width="90" />
      <el-table-column label="累计收益" align="center" prop="totalIncome" width="120" />
      <el-table-column label="昨日收益" align="center" prop="yesterdayIncome" width="120" />
      <el-table-column label="今日收益" align="center" prop="todayIncome" width="120" />
      <el-table-column label="操作" align="center" width="240" class-name="small-padding fixed-width">
        <template slot-scope="scope">
          <el-button size="mini" type="text" icon="el-icon-edit" @click="openDetail(scope.row)" v-hasPermi="['app:appUser:edit']">编辑详情</el-button>
          <el-button size="mini" type="text" icon="el-icon-key" @click="resetPwd(scope.row)" v-hasPermi="['app:appUser:resetPwd']">重置密码</el-button>
          <el-button size="mini" type="text" icon="el-icon-delete" @click="handleDelete(scope.row)" v-hasPermi="['app:appUser:remove']">删除</el-button>
        </template>
      </el-table-column>
    </el-table>

    <pagination v-show="total > 0" :total="total" :page.sync="queryParams.pageNum" :limit.sync="queryParams.pageSize" @pagination="getList" />

    <!-- 添加用户 -->
      <el-dialog title="添加用户" :visible.sync="addOpen" width="600px" append-to-body>
      <el-form ref="addFormRef" :model="addForm" :rules="addRules" label-width="100px">
        <el-form-item label="用户名称" prop="name">
          <el-input v-model="addForm.name" placeholder="请输入用户名称" maxlength="30" />
        </el-form-item>
        <el-form-item label="用户手机号" prop="phone">
          <el-input v-model="addForm.phone" placeholder="请输入用户手机号" maxlength="11" />
        </el-form-item>
        <el-form-item label="用户头像" prop="avatar">
          <image-upload :value="addForm.avatar" :limit="1" :action="'/common/upload'" :file-size="5" :file-type="['png','jpg','jpeg']" @input="onAddAvatarChange" />
        </el-form-item>
        <el-form-item label="开户行" prop="bankName">
          <el-input v-model="addForm.bankName" placeholder="请输入开户行" maxlength="50" />
        </el-form-item>
        <el-form-item label="账户号码" prop="bankAccount">
          <el-input v-model="addForm.bankAccount" placeholder="请输入收款账户号码" maxlength="50" />
        </el-form-item>
        <el-form-item label="F2 Token" prop="f2poolToken">
          <el-input v-model="addForm.f2poolToken" placeholder="请输入F2Pool Token" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAdd">确 定</el-button>
        <el-button @click="addOpen = false">取 消</el-button>
      </div>
    </el-dialog>

    <!-- 编辑详情 -->
    <el-dialog title="编辑用户详情" :visible.sync="detailOpen" width="1200px" append-to-body>
      <el-tabs v-model="detailActiveTab" tab-position="top">
        <el-tab-pane label="用户信息" name="user">
          <el-row :gutter="20">
            <el-col :span="12">
              <el-card shadow="never">
                <div slot="header">基础信息</div>
                <div class="field-row">
                  <span class="label">用户头像</span>
                  <el-avatar v-if="!editState.avatar" :size="36" :src="fullImageUrl(detail.avatar)" icon="el-icon-user" />
                  <image-upload v-else :value="detailEdit.avatar" :limit="1" :action="'/common/upload'" :file-size="5" :file-type="['png','jpg','jpeg']" @input="onDetailAvatarChange" />
                  <i class="el-icon-edit" v-if="!editState.avatar" @click="toggleEdit('avatar')"></i>
                  <i class="el-icon-check" v-if="editState.avatar" @click="confirmEdit('avatar')"></i>
                  <i class="el-icon-close" v-if="editState.avatar" @click="cancelEdit('avatar')"></i>
                </div>
                <div class="field-row">
                  <span class="label">用户名称</span>
                  <span v-if="!editState.name">{{ detail.name }}</span>
                  <el-input v-else v-model="detailEdit.name" size="small" style="width:220px" />
                  <i class="el-icon-edit" v-if="!editState.name" @click="toggleEdit('name')"></i>
                  <i class="el-icon-check" v-if="editState.name" @click="confirmEdit('name')"></i>
                  <i class="el-icon-close" v-if="editState.name" @click="cancelEdit('name')"></i>
                </div>
                <div class="field-row">
                  <span class="label">用户手机号</span>
                  <span v-if="!editState.phone">{{ detail.phone }}</span>
                  <el-input v-else v-model="detailEdit.phone" size="small" style="width:220px" />
                  <i class="el-icon-edit" v-if="!editState.phone" @click="toggleEdit('phone')"></i>
                  <i class="el-icon-check" v-if="editState.phone" @click="confirmEdit('phone')"></i>
                  <i class="el-icon-close" v-if="editState.phone" @click="cancelEdit('phone')"></i>
                </div>
                <div class="field-row">
                  <span class="label">开户行</span>
                  <span v-if="!editState.bankName">{{ detail.bankName }}</span>
                  <el-input v-else v-model="detailEdit.bankName" size="small" style="width:220px" />
                  <i class="el-icon-edit" v-if="!editState.bankName" @click="toggleEdit('bankName')"></i>
                  <i class="el-icon-check" v-if="editState.bankName" @click="confirmEdit('bankName')"></i>
                  <i class="el-icon-close" v-if="editState.bankName" @click="cancelEdit('bankName')"></i>
                </div>
                <div class="field-row">
                  <span class="label">账户号码</span>
                  <span v-if="!editState.bankAccount">{{ detail.bankAccount }}</span>
                  <el-input v-else v-model="detailEdit.bankAccount" size="small" style="width:220px" />
                  <i class="el-icon-edit" v-if="!editState.bankAccount" @click="toggleEdit('bankAccount')"></i>
                  <i class="el-icon-check" v-if="editState.bankAccount" @click="confirmEdit('bankAccount')"></i>
                  <i class="el-icon-close" v-if="editState.bankAccount" @click="cancelEdit('bankAccount')"></i>
                </div>
                <el-divider></el-divider>
                <div class="field-row"><span class="label">累计收益</span><span>{{ detail.totalIncome }}</span></div>
                <div class="field-row"><span class="label">昨日收益</span><span>{{ detail.yesterdayIncome }}</span></div>
                <div class="field-row"><span class="label">今日收益</span><span>{{ detail.todayIncome }}</span></div>
              </el-card>
            </el-col>
            <el-col :span="12">
              <el-card shadow="never">
                <div slot="header">安全信息</div>
                <div class="field-row"><span class="label">用户账户</span><span>{{ detail.phone }}</span></div>
                <div class="field-row"><span class="label">登录密码</span><el-button type="text" @click="doResetPwd(detail)">重置为系统默认</el-button></div>
              </el-card>
            </el-col>
          </el-row>
        </el-tab-pane>
        <el-tab-pane label="矿机信息" name="miner">
          <el-card shadow="never">
            <div style="margin-bottom:10px; display:flex; align-items:center; gap:10px; flex-wrap: wrap;">
              <div style="display:flex; align-items:center;">
                <el-form :inline="true" size="mini">
                  <el-form-item label="矿机状态">
                    <el-select v-model="minerQuery.status" placeholder="全部" clearable style="width: 140px">
                      <el-option label="正常" :value="1" />
                      <el-option label="停用" :value="0" />
                    </el-select>
                  </el-form-item>
                  <el-form-item>
                    <el-button type="primary" size="mini" @click="fetchMinerList(detail.id)">查询</el-button>
                    <el-button size="mini" @click="resetMinerQuery">重置</el-button>
                    <el-button type="primary" size="mini" @click="openAddMiner">添加矿机</el-button>
                  </el-form-item>
                </el-form>
              </div>
            </div>
            <el-table v-loading="minerLoading" :data="minerList">
              <el-table-column label="矿机ID" prop="id" align="center" width="120" />
              <el-table-column label="矿机品牌" prop="brandName" align="center" width="120" />
              <el-table-column label="矿机名称" prop="miningUserName" align="center" width="160" />
              <el-table-column label="管理费比率" align="center" width="160">
                <template slot-scope="scope">
                  <el-input-number v-model="scope.row.managementFeeRate" :step="0.01" :precision="2" size="small" />
                  <el-button type="text" @click="saveFeeRate(scope.row)">保存</el-button>
                </template>
              </el-table-column>
              <el-table-column label="今日预计" prop="estimatedTodayIncome" align="center" width="140" />
              <el-table-column label="昨日收益" prop="yesterdayIncome" align="center" width="140" />
              <el-table-column label="累计收益" prop="totalIncome" align="center" width="140" />
              <el-table-column label="启用状态" align="center" prop="status" width="120">
                <template slot-scope="scope">
                  <el-switch v-model="scope.row.status" :active-value="1" :inactive-value="0" disabled></el-switch>
                </template>
              </el-table-column>
              <el-table-column label="创建时间" prop="createTime" align="center" width="180">
                <template slot-scope="scope">
                  <span>{{ parseTime(scope.row.createTime) }}</span>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-tab-pane>
      </el-tabs>

      <div slot="footer" class="dialog-footer">
        <el-button @click="detailOpen = false">关 闭</el-button>
      </div>
    </el-dialog>

    <el-dialog title="添加矿机" :visible.sync="addMinerOpen" width="500px" append-to-body>
      <el-form ref="addMinerFormRef" :model="addMinerForm" :rules="addMinerRules" label-width="100px">
        <el-form-item label="矿机品牌" prop="brandId">
          <el-select v-model="addMinerForm.brandId" placeholder="请选择品牌" style="width: 300px">
            <el-option v-for="b in brandOptions" :key="b.id" :label="b.brandName" :value="b.id" />
          </el-select>
        </el-form-item>
        <el-form-item label="矿机名称" prop="miningUserName">
          <el-input v-model="addMinerForm.miningUserName" placeholder="2-15个小写字母或数字，且不能以数字开头" />
        </el-form-item>
        <el-form-item label="矿机关联码" prop="apiCode">
          <el-input v-model="addMinerForm.apiCode" placeholder="请输入矿机关联码" />
        </el-form-item>
        <el-form-item label="管理费比率" prop="managementFeeRate">
          <el-input-number v-model="addMinerForm.managementFeeRate" :step="0.01" :precision="2" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitAddMiner">确 定</el-button>
        <el-button @click="addMinerOpen = false">取 消</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import { listAppUser, addAppUser, updateAppUser, delAppUser, resetAppUserPwd, getAppUser, listUserMiner, updateUserMiner, unbindMiner, addUserMiner } from '@/api/app/user.js'
import { listBrand } from '@/api/miner/brand.js'
import ImageUpload from '@/components/ImageUpload/index.vue'
import { parseTime } from '@/utils/ruoyi'

export default {
  name: 'AppUser',
  components: { ImageUpload },
  data() {
    return {
      baseUrl: process.env.VUE_APP_BASE_API,
      loading: false,
      userList: [],
      total: 0,
      showSearch: true,
      ids: [],
      queryParams: { pageNum: 1, pageSize: 10, keyword: undefined, phone: undefined },
      addOpen: false,
      addForm: { name: '', phone: '', avatar: '', bankName: '', bankAccount: '', f2poolToken: '' },
      addRules: {
        name: [{ required: true, message: '请输入用户名称', trigger: 'blur' }],
        phone: [{ required: true, message: '请输入手机号', trigger: 'blur' }, { pattern: /^1[3-9]\d{9}$/, message: '手机号格式不正确', trigger: 'blur' }],
        bankName: [{ required: true, message: '请输入开户行', trigger: 'blur' }],
        bankAccount: [{ required: true, message: '请输入账户号码', trigger: 'blur' }],
        f2poolToken: [{ required: true, message: '请输入F2Pool Token', trigger: 'blur' }]
      },
      detailOpen: false,
      detail: {},
      detailEdit: {},
      detailActiveTab: 'user',
      editState: { avatar: false, name: false, phone: false, bankName: false, bankAccount: false },
      minerLoading: false,
      minerList: [],
      minerQuery: { status: undefined },
      addMinerOpen: false,
      addMinerForm: { brandId: undefined, miningUserName: '', apiCode: '', managementFeeRate: 0 },
      addMinerRules: {
        miningUserName: [
          { required: true, message: '请输入矿机名称', trigger: 'blur' },
          { pattern: /^[a-z][a-z0-9]{1,14}$/, message: '请输入2-15个小写字母或数字，且不能以数字开头', trigger: 'blur' }
        ],
        brandId: [{ required: true, message: '请选择品牌', trigger: 'change' }],
        apiCode: [{ required: true, message: '请输入矿机ID', trigger: 'blur' }],
        managementFeeRate: [{ required: true, message: '请输入比率', trigger: 'change' }]
      },
      brandOptions: []
    }
  },
  created() {
    this.getList()
  },
  methods: {
    parseTime,
    fullImageUrl(url) { if (!url) return ''; if (/^https?:\/\//.test(url)) return url; return this.baseUrl + url },
    getList() {
      this.loading = true
      listAppUser(this.queryParams).then(res => {
        this.userList = res.rows || []
        this.total = res.total || 0
        this.loading = false
      }).catch(() => { this.loading = false })
    },
    handleQuery() { this.queryParams.pageNum = 1; this.getList() },
    resetQuery() { this.queryParams = { pageNum: 1, pageSize: 10, keyword: undefined, phone: undefined }; this.getList() },
    handleSelectionChange(selection) { this.ids = selection.map(i => i.id) },
    openAdd() { this.addOpen = true },
    submitAdd() {
      this.$refs.addFormRef.validate(valid => {
        if (!valid) return
        addAppUser(this.addForm).then(() => { this.$modal.msgSuccess('新增成功'); this.$refs.addFormRef.resetFields(); this.addForm = { name: '', phone: '', avatar: '', bankName: '', bankAccount: '' }; this.addOpen = false; this.getList() })
      })
    },
    onAddAvatarChange(val) { this.addForm.avatar = Array.isArray(val) ? (val[0] || '') : val },
    handleDelete(row) {
      const id = row.id || this.ids[0]
      if (!id) return
      this.$modal.confirm('确认删除该用户吗？').then(() => delAppUser(id)).then(() => { this.$modal.msgSuccess('删除成功'); this.getList() })
    },
    resetPwd(row) { this.doResetPwd(row) },
    doResetPwd(row) {
      const id = row.id
      this.$modal.confirm('确认重置该用户密码吗？').then(() => resetAppUserPwd(id)).then(() => { this.$modal.msgSuccess('重置成功') })
    },
    openDetail(row) {
      getAppUser(row.id).then(res => {
        this.detail = res.data || {}
        this.detailEdit = { avatar: this.detail.avatar, name: this.detail.name, phone: this.detail.phone, bankName: this.detail.bankName, bankAccount: this.detail.bankAccount, id: this.detail.id }
        this.detailActiveTab = 'user'
        this.detailOpen = true
        this.fetchMinerList(this.detail.id)
      })
    },
    toggleEdit(field) { this.$set(this.editState, field, true) },
    cancelEdit(field) { this.$set(this.editState, field, false); this.detailEdit[field] = this.detail[field] },
    confirmEdit(field) {
      const payload = { id: this.detail.id }
      payload[field] = this.detailEdit[field]
      updateAppUser(payload).then(() => {
        this.detail[field] = this.detailEdit[field]
        this.$set(this.editState, field, false)
        this.$modal.msgSuccess('保存成功')
      })
    },
    onDetailAvatarChange(val) { this.detailEdit.avatar = Array.isArray(val) ? (val[0] || '') : val },
    fetchMinerList(userId) {
      this.minerLoading = true
      listUserMiner({ userId, status: this.minerQuery.status, pageNum: 1, pageSize: 100 }).then(res => {
        this.minerList = res.rows || []
        this.minerLoading = false
      }).catch(() => { this.minerLoading = false })
    },
    resetMinerQuery() { this.minerQuery = { status: undefined }; if (this.detail && this.detail.id) { this.fetchMinerList(this.detail.id) } },
    openAddMiner() {
      this.addMinerOpen = true
      this.addMinerForm = { brandId: undefined, apiCode: '', managementFeeRate: 0, miningUserName: '' }
      this.fetchBrandOptions()
    },
    fetchBrandOptions() {
      listBrand({ pageNum: 1, pageSize: 100 }).then(res => { this.brandOptions = res.rows || [] })
    },
    submitAddMiner() {
      this.$refs.addMinerFormRef.validate(valid => {
        if (!valid) return
        const data = Object.assign({}, this.addMinerForm, { userId: this.detail.id })
        addUserMiner(data).then(() => { this.$modal.msgSuccess('新增成功'); this.addMinerOpen = false; this.fetchMinerList(this.detail.id) })
      })
    },
    saveFeeRate(row) {
      const payload = { id: row.id, managementFeeRate: row.managementFeeRate }
      updateUserMiner(payload).then(() => { this.$modal.msgSuccess('保存成功') })
    }
  }
}
</script>

<style scoped>
.field-row { display: flex; align-items: center; margin: 8px 0; }
.field-row .label { display: inline-block; width: 100px; color: #666; }
.field-row i { margin-left: 8px; cursor: pointer; }
</style>
