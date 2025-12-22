<template>
  <div class="app-container home">
    <el-row :gutter="20">
      <el-col :span="24" class="col-item">
        <el-button-group>
          <el-button type="primary" :plain="activeView !== 'coins'" @click="setView('coins')">币种排行</el-button>
          <el-button type="primary" :plain="activeView !== 'miners'" @click="setView('miners')">矿机排行</el-button>
        </el-button-group>
      </el-col>
      <el-col :span="24" class="col-item" v-if="activeView === 'coins'">
        <el-card shadow="hover">
          <div slot="header">
            <span>币种排行 Top100</span>
            <el-button type="text" style="float: right" @click="fetchCoinsTop100">刷新</el-button>
          </div>
          <el-table v-loading="loadingCoins" :data="coinsTop100" height="600">
            <el-table-column label="序号" type="index" width="70" align="center" />
            <el-table-column label="币种" prop="display_currency_code" align="center" width="120" />
            <el-table-column label="算法" prop="algorithm" align="center" width="140" />
            <el-table-column label="价格" align="center" width="160">
              <template slot-scope="scope">
                <span>{{ formatCurrency(scope.row.price) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="市值" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ formatCurrency(scope.row.marketcap) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="24小时成交量" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ formatCurrency(scope.row.volume24h) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="网络算力" align="center" width="180">
              <template slot-scope="scope">
                <span>{{ formatHashrate(scope.row.network_hashrate) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="24小时收益" align="center" :show-overflow-tooltip="true">
              <template slot-scope="scope">
                <span>{{ formatCurrency(scope.row.output24h) }}</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
      <el-col :span="24" class="col-item" v-else>
        <el-card shadow="hover">
          <div slot="header">
            <span>矿机排行</span>
            <el-button type="text" style="float: right" @click="fetchMiners">刷新</el-button>
          </div>
          <el-table v-loading="loadingMiners" :data="miners" height="600">
            <el-table-column label="序号" type="index" width="70" align="center" />
            <el-table-column label="矿机型号" prop="name" align="center" :show-overflow-tooltip="true" />
            <el-table-column label="可挖币种" prop="display_currency_code" align="center" width="120" />
            <el-table-column label="矿机算力" align="center" width="140">
              <template slot-scope="scope">
                <span>{{ formatMinerHashrate(scope.row) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="矿机功耗" align="center" width="120">
              <template slot-scope="scope">
                <span>{{ formatPower(scope.row.power) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="日产币数" align="center" width="160">
              <template slot-scope="scope">
                <span>{{ formatDailyCoins(scope.row) }}</span>
              </template>
            </el-table-column>
            <el-table-column label="厂商图标" align="center" width="120">
              <template slot-scope="scope">
                <el-image v-if="scope.row.company_icon" :src="scope.row.company_icon" style="width: 24px; height: 24px" fit="contain" />
                <span v-else>-</span>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import { getCoinsTop100, getMiners } from '@/api/app/coins'
export default {
  name: "Index",
  data() {
    return {
      version: "3.9.0",
      activeView: 'coins',
      coinsTop100: [],
      miners: [],
      loadingCoins: false,
      loadingMiners: false
    }
  },
  created() {
    this.fetchCoinsTop100()
    this.fetchMiners()
  },
  methods: {
    goTarget(href) {
      window.open(href, "_blank")
    },
    setView(view) {
      this.activeView = view
      if (view === 'coins' && this.coinsTop100.length === 0 && !this.loadingCoins) {
        this.fetchCoinsTop100()
      }
      if (view === 'miners' && this.miners.length === 0 && !this.loadingMiners) {
        this.fetchMiners()
      }
    },
    fetchCoinsTop100() {
      this.loadingCoins = true
      getCoinsTop100().then(res => {
        this.coinsTop100 = res.data || []
      }).finally(() => {
        this.loadingCoins = false
      })
    },
    fetchMiners() {
      this.loadingMiners = true
      getMiners().then(res => {
        this.miners = res.data || []
      }).finally(() => {
        this.loadingMiners = false
      })
    },
    formatCurrency(n) {
      const num = Number(n)
      if (!isFinite(num)) return '-'
      const abs = Math.abs(num)
      if (abs >= 1e9) return `$ ${this.toFixed(num / 1e9)}B`
      if (abs >= 1e6) return `$ ${this.toFixed(num / 1e6)}M`
      if (abs >= 1e3) return `$ ${this.toFixed(num / 1e3)}K`
      return `$ ${this.thousands(num)}`
    },
    thousands(n) {
      try { return Number(n).toLocaleString('en-US', { maximumFractionDigits: 2 }) } catch (e) { return String(n) }
    },
    toFixed(n, d = 2) {
      return Number(n).toFixed(d)
    },
    formatHashrate(h) {
      const num = Number(h)
      if (!isFinite(num)) return '-'
      const units = [
        { u: 'EH/s', v: 1e18 },
        { u: 'PH/s', v: 1e15 },
        { u: 'TH/s', v: 1e12 },
        { u: 'GH/s', v: 1e9 },
        { u: 'MH/s', v: 1e6 },
        { u: 'KH/s', v: 1e3 },
        { u: 'H/s', v: 1 }
      ]
      const picked = units.find(x => Math.abs(num) >= x.v) || units[units.length - 1]
      return `${this.toFixed(num / picked.v)}${picked.u}`
    },
    formatMinerHashrate(row) {
      const v = row.hashrate_unit_value
      if (typeof v === 'string') {
        const m = v.match(/^([\d.]+)\s*([KMGTPE])$/i)
        if (m) return `${m[1]}${m[2]}`
        return v
      }
      if (typeof v === 'number') return this.formatHashrate(v).replace('/s', '')
      return '-'
    },
    formatPower(w) {
      const num = Number(w)
      if (!isFinite(num)) return '-'
      return `${this.toFixed(num, 0)}W`
    },
    formatDailyCoins(row) {
      const c = Number(row.coins_24h)
      const sym = row.display_currency_code || ''
      if (!isFinite(c)) return '-'
      return `${this.toFixed(c)}${sym}`
    }
  }
}
</script>

<style scoped lang="scss">
.home {
  blockquote {
    padding: 10px 20px;
    margin: 0 0 20px;
    font-size: 17.5px;
    border-left: 5px solid #eee;
  }
  hr {
    margin-top: 20px;
    margin-bottom: 20px;
    border: 0;
    border-top: 1px solid #eee;
  }
  .col-item {
    margin-bottom: 20px;
  }

  ul {
    padding: 0;
    margin: 0;
  }

  font-family: "open sans", "Helvetica Neue", Helvetica, Arial, sans-serif;
  font-size: 13px;
  color: #676a6c;
  overflow-x: hidden;

  ul {
    list-style-type: none;
  }

  h4 {
    margin-top: 0px;
  }

  h2 {
    margin-top: 10px;
    font-size: 26px;
    font-weight: 100;
  }

  p {
    margin-top: 10px;

    b {
      font-weight: 700;
    }
  }

  .update-log {
    ol {
      display: block;
      list-style-type: decimal;
      margin-block-start: 1em;
      margin-block-end: 1em;
      margin-inline-start: 0;
      margin-inline-end: 0;
      padding-inline-start: 40px;
    }
  }
}
</style>
