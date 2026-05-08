<template>
  <div class="space-y-6">
    <div>
      <h3 class="text-2xl font-bold text-[#5A5A40]">基础班级数据</h3>
      <p class="text-sm text-[#A09E94] mt-1">管理班级信息与幼儿档案</p>
    </div>

    <!-- Tab -->
    <div class="flex gap-1 bg-[#F5F2ED] rounded-xl p-1 w-fit">
      <button v-for="tab in tabs" :key="tab.key" @click="switchTab(tab.key)"
              class="px-5 py-2 rounded-lg text-sm font-medium transition-all"
              :class="activeTab === tab.key ? 'bg-white text-[#5A5A40] shadow-sm font-bold' : 'text-[#A09E94] hover:text-[#7A7A6A]'">
        {{ tab.label }}
      </button>
    </div>

    <!-- ===== 班级 Tab ===== -->
    <div v-show="activeTab === 'class'">
      <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
        <div class="flex gap-3">
          <input v-model="classQuery.keyword" @keyup.enter="fetchClasses" placeholder="搜索班级名称"
                 class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none w-48 focus:border-[#5A5A40]" />
          <select v-model="classQuery.status" @change="fetchClasses"
                  class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm text-[#5A5A40] outline-none">
            <option :value="undefined">全部状态</option>
            <option :value="1">启用</option>
            <option :value="0">禁用</option>
          </select>
          <button @click="fetchClasses" class="h-9 px-4 bg-[#5A5A40] text-white rounded-xl text-sm hover:bg-[#4A4A35] transition-colors">搜索</button>
        </div>
        <button @click="openAddClass" class="flex items-center gap-2 px-5 py-2 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors">
          <Plus class="w-4 h-4" /> 新建班级
        </button>
      </div>
      <div v-if="classLoading" class="text-center py-16 text-[#A09E94]">加载中...</div>
      <div v-else-if="classList.length === 0" class="text-center py-16 text-[#A09E94]">暂无班级数据</div>
      <div v-else class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
        <div v-for="cls in classList" :key="cls.id"
             class="bg-white rounded-2xl border border-[#EFE9E1] p-6 hover:shadow-md transition-shadow">
          <div class="flex items-start justify-between mb-4">
            <div>
              <h4 class="text-lg font-bold text-[#333322]">{{ cls.className }}</h4>
              <span class="text-xs font-bold px-2.5 py-1 rounded-full mt-1 inline-block" :class="gradeClass(cls.grade)">{{ cls.grade }}</span>
            </div>
            <span class="text-[11px] px-2.5 py-1 rounded-full font-bold"
                  :class="cls.status === 1 ? 'bg-green-50 text-green-600' : 'bg-gray-100 text-gray-400'">
              {{ cls.status === 1 ? '启用' : '禁用' }}
            </span>
          </div>
          <div class="space-y-2 text-sm text-[#7A7A6A]">
            <div class="flex justify-between"><span>班主任</span><span class="font-medium text-[#333322]">{{ cls.headTeacherName || '暂未分配' }}</span></div>
            <div class="flex justify-between"><span>在园幼儿</span><span class="font-bold text-[#5A5A40]">{{ cls.studentCount }} 人</span></div>
            <div class="flex justify-between"><span>学期</span><span>{{ cls.semester || '-' }}</span></div>
          </div>
          <div class="flex gap-2 mt-5 pt-4 border-t border-[#EFE9E1]">
            <button @click="openEditClass(cls)" class="flex-1 py-1.5 text-xs font-medium text-[#5A5A40] border border-[#EFE9E1] rounded-lg hover:bg-[#F5F2ED] transition-colors">编辑</button>
            <button @click="handleClassStatus(cls)" class="flex-1 py-1.5 text-xs font-medium border rounded-lg transition-colors"
                    :class="cls.status === 1 ? 'text-orange-500 border-orange-100 hover:bg-orange-50' : 'text-green-600 border-green-100 hover:bg-green-50'">
              {{ cls.status === 1 ? '禁用' : '启用' }}
            </button>
            <button @click="handleDeleteClass(cls)" class="flex-1 py-1.5 text-xs font-medium text-red-400 border border-red-100 rounded-lg hover:bg-red-50 transition-colors">删除</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== 幼儿 Tab ===== -->
    <div v-show="activeTab === 'student'">
      <div class="flex flex-wrap items-center justify-between gap-3 mb-4">
        <div class="flex flex-wrap gap-3">
          <select v-model="stuQuery.classId" @change="fetchStudents"
                  class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm text-[#5A5A40] outline-none">
            <option :value="null">全部班级</option>
            <option v-for="cls in classList" :key="cls.id" :value="cls.id">{{ cls.className }}</option>
          </select>
          <select v-model="stuQuery.status" @change="fetchStudents"
                  class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm text-[#5A5A40] outline-none">
            <option :value="undefined">全部状态</option>
            <option :value="1">在园</option>
            <option :value="0">离园</option>
          </select>
          <input v-model="stuQuery.keyword" @keyup.enter="fetchStudents" placeholder="搜索幼儿姓名"
                 class="h-9 px-3 rounded-xl border border-[#EFE9E1] bg-white text-sm outline-none w-40 focus:border-[#5A5A40]" />
          <button @click="fetchStudents" class="h-9 px-4 bg-[#5A5A40] text-white rounded-xl text-sm hover:bg-[#4A4A35] transition-colors">搜索</button>
        </div>
        <button @click="openAddStudent" class="flex items-center gap-2 px-5 py-2 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors">
          <Plus class="w-4 h-4" /> 新增幼儿
        </button>
      </div>

      <div class="bg-white rounded-2xl border border-[#EFE9E1] overflow-hidden">
        <table class="w-full text-sm">
          <thead class="bg-[#F5F2ED]">
          <tr>
            <th class="px-5 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">姓名</th>
            <th class="px-5 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">性别</th>
            <th class="px-5 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">出生日期</th>
            <th class="px-5 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">所在班级</th>
            <th class="px-5 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">状态</th>
            <th class="px-5 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">绑定家长</th>
            <th class="px-5 py-3 text-left text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">操作</th>
          </tr>
          </thead>
          <tbody>
          <tr v-if="stuLoading"><td colspan="7" class="text-center py-12 text-[#A09E94]">加载中...</td></tr>
          <tr v-else-if="studentList.length === 0"><td colspan="7" class="text-center py-12 text-[#A09E94]">暂无幼儿数据</td></tr>
          <tr v-for="stu in studentList" :key="stu.id" class="border-t border-[#EFE9E1] hover:bg-[#FDFCF8] transition-colors">
            <td class="px-5 py-3.5 font-bold text-[#333322]">{{ stu.name }}</td>
            <td class="px-5 py-3.5 text-[#7A7A6A]">{{ stu.genderLabel }}</td>
            <td class="px-5 py-3.5 text-[#7A7A6A]">{{ stu.birthday || '-' }}</td>
            <td class="px-5 py-3.5">
              <span class="px-2.5 py-1 bg-[#F5F2ED] text-[#5A5A40] rounded-full text-[11px] font-bold">{{ stu.className }}</span>
            </td>
            <td class="px-5 py-3.5">
                <span class="px-2.5 py-1 rounded-full text-[11px] font-bold"
                      :class="stu.status === 1 ? 'bg-green-50 text-green-600' : 'bg-gray-100 text-gray-400'">
                  {{ stu.status === 1 ? '在园' : '离园' }}
                </span>
            </td>
            <td class="px-5 py-3.5">
              <button @click="openDetail(stu)" class="flex items-center gap-1 text-xs text-blue-500 hover:underline font-medium">
                <Users class="w-3.5 h-3.5" /> 查看家长
              </button>
            </td>
            <td class="px-5 py-3.5">
              <div class="flex gap-2">
                <button @click="openEditStudent(stu)" class="text-xs text-[#5A5A40] hover:underline font-medium">编辑</button>
                <button @click="openChangeClass(stu)" class="text-xs text-blue-500 hover:underline font-medium">转班</button>
                <button @click="handleDeleteStudent(stu.id)" class="text-xs text-red-400 hover:underline font-medium">删除</button>
              </div>
            </td>
          </tr>
          </tbody>
        </table>
        <div class="flex items-center justify-between px-6 py-4 border-t border-[#EFE9E1]">
          <span class="text-xs text-[#A09E94]">共 {{ stuTotal }} 条</span>
          <div class="flex items-center gap-2">
            <button @click="stuPrev" :disabled="stuQuery.pageNum === 1"
                    class="h-8 px-3 rounded-lg border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40 disabled:cursor-not-allowed">上一页</button>
            <span class="text-xs text-[#5A5A40] font-medium">{{ stuQuery.pageNum }} / {{ stuTotalPages }}</span>
            <button @click="stuNext" :disabled="stuQuery.pageNum >= stuTotalPages"
                    class="h-8 px-3 rounded-lg border border-[#EFE9E1] text-xs text-[#7A7A6A] hover:bg-[#F5F2ED] disabled:opacity-40 disabled:cursor-not-allowed">下一页</button>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== 班级弹框 ===== -->
    <div v-if="classDialog" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl p-8 w-full max-w-md shadow-2xl">
        <div class="flex items-center justify-between mb-6">
          <h4 class="text-lg font-bold text-[#5A5A40]">{{ classIsEdit ? '编辑班级' : '新建班级' }}</h4>
          <button @click="classDialog = false"><X class="w-5 h-5 text-[#A09E94]" /></button>
        </div>
        <div class="space-y-4">
          <div><label class="label">班级名称 *</label><input v-model="classForm.className" placeholder="如：大一班" class="input-field" /></div>
          <div>
            <label class="label">年级 *</label>
            <select v-model="classForm.grade" class="input-field">
              <option value="">请选择年级</option>
              <option value="大班">大班</option><option value="中班">中班</option>
              <option value="小班">小班</option><option value="托班">托班</option>
            </select>
          </div>
          <div>
            <label class="label">班主任</label>
            <select v-model="classForm.headTeacherId" class="input-field">
              <option :value="null">暂不分配</option>
              <option v-for="t in teacherList" :key="t.id" :value="t.id">{{ t.realName || t.username }}</option>
            </select>
          </div>
          <div><label class="label">学期</label><input v-model="classForm.semester" placeholder="如：2024-2025春季" class="input-field" /></div>
        </div>
        <div class="flex gap-3 mt-8">
          <button @click="classDialog = false" class="flex-1 h-11 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">取消</button>
          <button @click="submitClass" :disabled="classSubmitting" class="flex-1 h-11 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors disabled:opacity-60">
            {{ classSubmitting ? '提交中...' : '确认' }}
          </button>
        </div>
      </div>
    </div>

    <!-- ===== 幼儿弹框 ===== -->
    <div v-if="stuDialog" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl p-8 w-full max-w-md shadow-2xl">
        <div class="flex items-center justify-between mb-6">
          <h4 class="text-lg font-bold text-[#5A5A40]">{{ stuIsEdit ? '编辑幼儿' : '新增幼儿' }}</h4>
          <button @click="stuDialog = false"><X class="w-5 h-5 text-[#A09E94]" /></button>
        </div>
        <div class="space-y-4">
          <div><label class="label">姓名 *</label><input v-model="stuForm.name" placeholder="请输入幼儿姓名" class="input-field" /></div>
          <div>
            <label class="label">性别</label>
            <select v-model="stuForm.gender" class="input-field">
              <option :value="undefined">请选择</option><option :value="1">男</option><option :value="2">女</option>
            </select>
          </div>
          <div><label class="label">出生日期</label><input v-model="stuForm.birthday" type="date" class="input-field" /></div>
          <div>
            <label class="label">所在班级</label>
            <select v-model="stuForm.classId" class="input-field">
              <option :value="null">暂不分班</option>
              <option v-for="cls in classList" :key="cls.id" :value="cls.id">{{ cls.className }}</option>
            </select>
          </div>
          <div><label class="label">备注（过敏史等）</label><input v-model="stuForm.remark" placeholder="过敏史、特殊情况等" class="input-field" /></div>
        </div>
        <div class="flex gap-3 mt-8">
          <button @click="stuDialog = false" class="flex-1 h-11 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">取消</button>
          <button @click="submitStudent" :disabled="stuSubmitting" class="flex-1 h-11 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors disabled:opacity-60">
            {{ stuSubmitting ? '提交中...' : '确认' }}
          </button>
        </div>
      </div>
    </div>

    <!-- ===== 转班弹框 ===== -->
    <div v-if="changeClassDialog" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl p-8 w-full max-w-sm shadow-2xl">
        <div class="flex items-center justify-between mb-6">
          <h4 class="text-lg font-bold text-[#5A5A40]">转班 — {{ changeStuName }}</h4>
          <button @click="changeClassDialog = false"><X class="w-5 h-5 text-[#A09E94]" /></button>
        </div>
        <div>
          <label class="label">转入班级</label>
          <select v-model="changeTargetClassId" class="input-field">
            <option :value="null">请选择班级</option>
            <option v-for="cls in classList" :key="cls.id" :value="cls.id">{{ cls.className }}</option>
          </select>
        </div>
        <div class="flex gap-3 mt-8">
          <button @click="changeClassDialog = false" class="flex-1 h-11 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">取消</button>
          <button @click="submitChangeClass" class="flex-1 h-11 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors">确认转班</button>
        </div>
      </div>
    </div>

    <!-- ===== 幼儿详情+家长面板 ===== -->
    <div v-if="detailPanel" class="fixed inset-0 bg-black/40 flex items-center justify-center z-50">
      <div class="bg-white rounded-3xl w-full max-w-lg shadow-2xl overflow-hidden">
        <!-- 头部 -->
        <div class="bg-[#5A5A40] px-8 py-6 flex items-center justify-between">
          <div>
            <h4 class="text-xl font-bold text-white">{{ currentDetail?.name }}</h4>
            <p class="text-[#A09E94] text-xs mt-1">
              {{ currentDetail?.genderLabel }} ·
              {{ currentDetail?.birthday || '未填写生日' }} ·
              {{ currentDetail?.className }}
            </p>
          </div>
          <button @click="detailPanel = false"><X class="w-5 h-5 text-white/70 hover:text-white" /></button>
        </div>

        <div class="p-8 space-y-6 max-h-[70vh] overflow-y-auto">
          <!-- 基本信息 -->
          <div>
            <p class="text-[11px] font-bold uppercase tracking-widest text-[#A09E94] mb-3">基本信息</p>
            <div class="grid grid-cols-2 gap-3 text-sm">
              <div class="bg-[#F5F2ED] rounded-xl p-3">
                <p class="text-[10px] text-[#A09E94] font-bold uppercase mb-1">状态</p>
                <p class="font-bold" :class="currentDetail?.status === 1 ? 'text-green-600' : 'text-gray-400'">
                  {{ currentDetail?.status === 1 ? '在园' : '离园' }}
                </p>
              </div>
              <div class="bg-[#F5F2ED] rounded-xl p-3">
                <p class="text-[10px] text-[#A09E94] font-bold uppercase mb-1">备注</p>
                <p class="font-medium text-[#333322]">{{ currentDetail?.remark || '无' }}</p>
              </div>
            </div>
          </div>

          <!-- 绑定家长 -->
          <div>
            <div class="flex items-center justify-between mb-3">
              <p class="text-[11px] font-bold uppercase tracking-widest text-[#A09E94]">绑定家长</p>
              <button @click="openBindParent" class="flex items-center gap-1 text-xs text-[#5A5A40] font-bold hover:underline">
                <Plus class="w-3.5 h-3.5" /> 添加家长
              </button>
            </div>

            <div v-if="!currentDetail?.parents?.length" class="text-center py-8 text-[#A09E94] bg-[#F5F2ED] rounded-2xl">
              <Users class="w-8 h-8 mx-auto mb-2 opacity-30" />
              <p class="text-sm">暂无绑定家长</p>
            </div>

            <div v-else class="space-y-3">
              <div v-for="p in currentDetail.parents" :key="p.bindId"
                   class="border border-[#EFE9E1] rounded-2xl p-4 hover:border-[#5A5A40]/20 transition-colors">
                <div class="flex items-start justify-between">
                  <div class="flex items-center gap-3">
                    <div class="w-10 h-10 rounded-full bg-[#EFE9E1] flex items-center justify-center text-[#5A5A40] font-bold text-sm shrink-0">
                      {{ (p.realName || p.username || '?').charAt(0) }}
                    </div>
                    <div>
                      <p class="font-bold text-[#333322] text-sm">{{ p.realName || p.username }}</p>
                      <p class="text-xs text-[#A09E94]">账号：{{ p.username }}</p>
                    </div>
                  </div>
                  <!-- 审核状态 -->
                  <span class="text-[11px] px-2.5 py-1 rounded-full font-bold"
                        :class="{
                      'bg-yellow-50 text-yellow-600': p.status === 0,
                      'bg-green-50 text-green-600': p.status === 1,
                      'bg-red-50 text-red-400': p.status === 2
                    }">{{ p.statusLabel }}</span>
                </div>

                <!-- 联系信息 -->
                <div class="mt-3 flex flex-wrap gap-3 text-xs text-[#7A7A6A]">
                  <span class="flex items-center gap-1">
                    <span class="font-bold text-[#5A5A40]">关系：</span>{{ p.relation || '未填写' }}
                  </span>
                  <span v-if="p.phone" class="flex items-center gap-1">
                    <Phone class="w-3 h-3" />
                    <a :href="`tel:${p.phone}`" class="text-blue-500 hover:underline font-medium">{{ p.phone }}</a>
                  </span>
                  <span v-else class="text-[#A09E94]">手机号未填写</span>
                </div>

                <!-- 操作 -->
                <div class="mt-3 flex gap-2 pt-3 border-t border-[#F5F2ED]">
                  <template v-if="p.status === 0">
                    <button @click="handleAudit(p.bindId, 1)" class="text-xs text-green-600 font-bold hover:underline">通过审核</button>
                    <button @click="handleAudit(p.bindId, 2)" class="text-xs text-red-400 font-bold hover:underline">拒绝</button>
                  </template>
                  <button @click="handleUnbind(p.bindId)" class="text-xs text-red-400 hover:underline ml-auto">解除绑定</button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- ===== 添加家长绑定弹框 ===== -->
    <div v-if="bindDialog" class="fixed inset-0 bg-black/40 flex items-center justify-center z-[60]">
      <div class="bg-white rounded-3xl p-8 w-full max-w-sm shadow-2xl">
        <div class="flex items-center justify-between mb-6">
          <h4 class="text-lg font-bold text-[#5A5A40]">添加家长</h4>
          <button @click="bindDialog = false"><X class="w-5 h-5 text-[#A09E94]" /></button>
        </div>
        <div class="space-y-4">
          <div>
            <label class="label">搜索家长账号</label>
            <div class="flex gap-2">
              <input v-model="bindKeyword" placeholder="输入姓名或账号" class="input-field flex-1" @keyup.enter="searchParents" />
              <button @click="searchParents" class="h-11 px-4 bg-[#5A5A40] text-white rounded-xl text-sm hover:bg-[#4A4A35] transition-colors">搜索</button>
            </div>
          </div>
          <!-- 搜索结果 -->
          <div v-if="parentSearchList.length > 0" class="border border-[#EFE9E1] rounded-xl overflow-hidden max-h-48 overflow-y-auto">
            <div v-for="p in parentSearchList" :key="p.id"
                 @click="selectParent(p)"
                 class="flex items-center gap-3 px-4 py-3 hover:bg-[#F5F2ED] cursor-pointer transition-colors"
                 :class="bindForm.parentId === p.id ? 'bg-[#EFE9E1]' : ''">
              <div class="w-8 h-8 rounded-full bg-[#EFE9E1] flex items-center justify-center text-[#5A5A40] text-xs font-bold shrink-0">
                {{ (p.realName || p.username).charAt(0) }}
              </div>
              <div>
                <p class="text-sm font-bold text-[#333322]">{{ p.realName || '未填写姓名' }}</p>
                <p class="text-xs text-[#A09E94]">{{ p.username }} {{ p.phone ? '· ' + p.phone : '' }}</p>
              </div>
              <div v-if="bindForm.parentId === p.id" class="ml-auto text-[#5A5A40]">✓</div>
            </div>
          </div>
          <div>
            <label class="label">与幼儿关系</label>
            <select v-model="bindForm.relation" class="input-field">
              <option value="">请选择</option>
              <option value="爸爸">爸爸</option>
              <option value="妈妈">妈妈</option>
              <option value="爷爷">爷爷</option>
              <option value="奶奶">奶奶</option>
              <option value="外公">外公</option>
              <option value="外婆">外婆</option>
              <option value="其他">其他</option>
            </select>
          </div>
        </div>
        <div class="flex gap-3 mt-8">
          <button @click="bindDialog = false" class="flex-1 h-11 border border-[#EFE9E1] text-[#7A7A6A] rounded-xl text-sm hover:bg-[#F5F2ED] transition-colors">取消</button>
          <button @click="submitBind" :disabled="bindSubmitting" class="flex-1 h-11 bg-[#5A5A40] text-white rounded-xl text-sm font-medium hover:bg-[#4A4A35] transition-colors disabled:opacity-60">
            {{ bindSubmitting ? '绑定中...' : '确认绑定' }}
          </button>
        </div>
      </div>
    </div>

  </div>
</template>

<script setup lang="ts">
import { ref, reactive, computed, onMounted } from 'vue'
import { Plus, X, Users, Phone } from 'lucide-vue-next'
import {
  getClassList, addClass, updateClass, updateClassStatus, deleteClass, getTeachers,
  getStudentList, getStudentDetail, addStudent, updateStudent, deleteStudent, changeClass,
  bindParent, unbindParent, auditBind, getParentUsers
} from '../../api/class'

const tabs = [{ key: 'class', label: '班级管理' }, { key: 'student', label: '幼儿管理' }]
const activeTab = ref('class')

function switchTab(key: string) {
  activeTab.value = key
  if (key === 'student' && studentList.value.length === 0) fetchStudents()
}

// ── 班级 ─────────────────────────────────────────
const classLoading = ref(false)
const classList    = ref<any[]>([])
const teacherList  = ref<any[]>([])
const classQuery   = reactive({ keyword: '', status: undefined as number | undefined })

async function fetchClasses() {
  classLoading.value = true
  try { const res: any = await getClassList(classQuery); classList.value = res.data }
  finally { classLoading.value = false }
}
async function fetchTeachers() { const res: any = await getTeachers(); teacherList.value = res.data }
onMounted(() => { fetchClasses(); fetchTeachers() })

async function handleClassStatus(cls: any) {
  const ns = cls.status === 1 ? 0 : 1
  if (!confirm(`确认${ns === 0 ? '禁用' : '启用'}「${cls.className}」吗？`)) return
  await updateClassStatus(cls.id, ns); fetchClasses()
}
async function handleDeleteClass(cls: any) {
  if (!confirm(`确认删除「${cls.className}」吗？班级下有幼儿时无法删除。`)) return
  await deleteClass(cls.id); fetchClasses()
}

const classDialog     = ref(false)
const classIsEdit     = ref(false)
const classSubmitting = ref(false)
const classForm = reactive({ id: undefined as number | undefined, className: '', grade: '', headTeacherId: null as number | null, semester: '' })

function openAddClass() {
  classIsEdit.value = false
  Object.assign(classForm, { id: undefined, className: '', grade: '', headTeacherId: null, semester: '' })
  classDialog.value = true
}
function openEditClass(cls: any) {
  classIsEdit.value = true
  Object.assign(classForm, { id: cls.id, className: cls.className, grade: cls.grade, headTeacherId: cls.headTeacherId, semester: cls.semester || '' })
  classDialog.value = true
}
async function submitClass() {
  if (!classForm.className) { alert('请填写班级名称'); return }
  if (!classForm.grade) { alert('请选择年级'); return }
  classSubmitting.value = true
  try {
    classIsEdit.value ? await updateClass(classForm.id!, classForm) : await addClass(classForm)
    classDialog.value = false; fetchClasses()
  } finally { classSubmitting.value = false }
}

// ── 幼儿列表 ─────────────────────────────────────
const stuLoading  = ref(false)
const studentList = ref<any[]>([])
const stuTotal    = ref(0)
const stuQuery    = reactive({ pageNum: 1, pageSize: 10, classId: null as number | null, keyword: '', status: undefined as number | undefined })
const stuTotalPages = computed(() => Math.max(1, Math.ceil(stuTotal.value / stuQuery.pageSize)))

async function fetchStudents() {
  stuLoading.value = true
  try { const res: any = await getStudentList(stuQuery); studentList.value = res.data.records; stuTotal.value = res.data.total }
  finally { stuLoading.value = false }
}
function stuPrev() { if (stuQuery.pageNum > 1) { stuQuery.pageNum--; fetchStudents() } }
function stuNext() { if (stuQuery.pageNum < stuTotalPages.value) { stuQuery.pageNum++; fetchStudents() } }

async function handleDeleteStudent(id: number) {
  if (!confirm('确认删除该幼儿信息吗？同时会清除家长绑定关系。')) return
  await deleteStudent(id); fetchStudents()
}

const stuDialog     = ref(false)
const stuIsEdit     = ref(false)
const stuSubmitting = ref(false)
const stuForm = reactive({ id: undefined as number | undefined, name: '', gender: undefined as number | undefined, birthday: '', classId: null as number | null, remark: '' })

function openAddStudent() {
  stuIsEdit.value = false
  Object.assign(stuForm, { id: undefined, name: '', gender: undefined, birthday: '', classId: null, remark: '' })
  stuDialog.value = true
}
function openEditStudent(stu: any) {
  stuIsEdit.value = true
  Object.assign(stuForm, { id: stu.id, name: stu.name, gender: stu.gender, birthday: stu.birthday || '', classId: stu.classId, remark: stu.remark || '' })
  stuDialog.value = true
}
async function submitStudent() {
  if (!stuForm.name) { alert('请填写幼儿姓名'); return }
  stuSubmitting.value = true
  try {
    stuIsEdit.value ? await updateStudent(stuForm.id!, stuForm) : await addStudent(stuForm)
    stuDialog.value = false; fetchStudents()
  } finally { stuSubmitting.value = false }
}

// ── 转班 ─────────────────────────────────────────
const changeClassDialog   = ref(false)
const changeStuId         = ref(0)
const changeStuName       = ref('')
const changeTargetClassId = ref<number | null>(null)

function openChangeClass(stu: any) {
  changeStuId.value = stu.id; changeStuName.value = stu.name; changeTargetClassId.value = stu.classId
  changeClassDialog.value = true
}
async function submitChangeClass() {
  if (!changeTargetClassId.value) { alert('请选择目标班级'); return }
  await changeClass(changeStuId.value, changeTargetClassId.value)
  changeClassDialog.value = false; fetchStudents()
}

// ── 幼儿详情+家长面板 ────────────────────────────
const detailPanel   = ref(false)
const currentDetail = ref<any>(null)
const currentStuId  = ref(0)

async function openDetail(stu: any) {
  currentStuId.value = stu.id
  const res: any = await getStudentDetail(stu.id)
  currentDetail.value = res.data
  detailPanel.value = true
}

async function refreshDetail() {
  const res: any = await getStudentDetail(currentStuId.value)
  currentDetail.value = res.data
}

async function handleUnbind(bindId: number) {
  if (!confirm('确认解除该家长的绑定关系吗？')) return
  await unbindParent(bindId)
  refreshDetail()
}

async function handleAudit(bindId: number, status: number) {
  await auditBind(bindId, status, 1) // 1 = admin ID，实际应从登录信息取
  refreshDetail()
}

// ── 绑定家长弹框 ─────────────────────────────────
const bindDialog        = ref(false)
const bindKeyword       = ref('')
const parentSearchList  = ref<any[]>([])
const bindSubmitting    = ref(false)
const bindForm = reactive({ studentId: 0, parentId: 0, relation: '' })

function openBindParent() {
  bindKeyword.value = ''; parentSearchList.value = []
  Object.assign(bindForm, { studentId: currentStuId.value, parentId: 0, relation: '' })
  bindDialog.value = true
}
async function searchParents() {
  if (!bindKeyword.value) return
  const res: any = await getParentUsers(bindKeyword.value)
  parentSearchList.value = res.data.records || []
}
function selectParent(p: any) { bindForm.parentId = p.id }

async function submitBind() {
  if (!bindForm.parentId) { alert('请先选择家长账号'); return }
  if (!bindForm.relation) { alert('请选择与幼儿的关系'); return }
  bindSubmitting.value = true
  try {
    await bindParent(bindForm)
    bindDialog.value = false
    refreshDetail()
  } finally { bindSubmitting.value = false }
}

function gradeClass(grade: string) {
  const map: Record<string, string> = { '大班': 'bg-purple-50 text-purple-600', '中班': 'bg-blue-50 text-blue-600', '小班': 'bg-green-50 text-green-600', '托班': 'bg-orange-50 text-orange-500' }
  return map[grade] || 'bg-gray-100 text-gray-500'
}
</script>

<style scoped>
@reference "tailwindcss";
.label { @apply block text-xs font-bold text-[#7A7A6A] mb-1.5; }
.input-field { @apply w-full h-11 px-4 bg-[#F5F2ED] rounded-xl border border-[#EFE9E1] text-sm outline-none focus:border-[#5A5A40] transition-colors; }
</style>