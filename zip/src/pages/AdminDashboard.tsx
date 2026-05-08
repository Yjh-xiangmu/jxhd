import { Routes, Route, Navigate } from "react-router-dom";
import { LayoutDashboard, Users, School, Newspaper, MessageSquareWarning, ShieldAlert } from "lucide-react";
import { DashboardLayout } from "../components/DashboardLayout";

const adminMenu = [
  { icon: <LayoutDashboard className="w-5 h-5" />, label: "总览数据", path: "/admin" },
  { icon: <Users className="w-5 h-5" />, label: "用户与权限", path: "/admin/users" },
  { icon: <School className="w-5 h-5" />, label: "基础班级数据", path: "/admin/classes" },
  { icon: <Newspaper className="w-5 h-5" />, label: "校园资讯", path: "/admin/news" },
  { icon: <MessageSquareWarning className="w-5 h-5" />, label: "论坛监管", path: "/admin/forum" },
  { icon: <ShieldAlert className="w-5 h-5" />, label: "系统操作日志", path: "/admin/logs" },
];

export default function AdminDashboard() {
  return (
    <DashboardLayout items={adminMenu} basePath="/admin" roleName="管理员" themeColor="text-[#5A5A40]">
      <Routes>
        <Route path="/" element={<AdminOverview />} />
        <Route path="*" element={<PlaceholderView title="建设中" />} />
      </Routes>
    </DashboardLayout>
  );
}

function AdminOverview() {
  return (
    <div className="space-y-6">
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard title="在园幼儿总数" value="482" trend="+12" trendUp={true} />
        <StatCard title="教职工总数" value="56" trend="+2" trendUp={true} />
        <StatCard title="活跃家长" value="89%" trend="+5%" trendUp={true} />
        <StatCard title="论坛违规待审" value="3" trend="-2" trendUp={false} />
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="bg-white rounded-[32px] shadow-sm border border-[#EFE9E1] p-6 lg:col-span-2">
          <h3 className="text-sm font-bold text-[#5A5A40] uppercase tracking-wider mb-6">全园最新资讯动态</h3>
          <div className="space-y-4">
            {[1, 2, 3].map((i) => (
              <div key={i} className="flex items-center p-3 hover:bg-[#F5F2ED] rounded-2xl transition-colors cursor-pointer border border-[#EFE9E1] hover:border-transparent">
                <div className="w-12 h-12 rounded-xl bg-[#EFE9E1] flex items-center justify-center text-[#5A5A40] mr-4 shrink-0">
                  <Newspaper className="w-6 h-6" />
                </div>
                <div className="flex-1">
                  <h4 className="font-bold text-[#333322]">2026年春季学期开学须知及安全要求</h4>
                  <p className="text-[11px] text-[#A09E94] mt-1">发布于 2小时前 · 已阅: 320</p>
                </div>
                <button className="text-[11px] text-[#5A5A40] font-bold underline px-2">编辑</button>
              </div>
            ))}
          </div>
        </div>

        <div className="bg-[#EFE9E1] rounded-[32px] p-6 flex flex-col gap-4">
          <h3 className="text-sm font-bold text-[#5A5A40] uppercase tracking-wider mb-2">快捷操作</h3>
          <div className="flex flex-col space-y-3 mt-auto">
            <button className="w-full py-3 px-4 bg-[#5A5A40] hover:bg-[#4A4A30] text-white rounded-full font-bold transition-colors text-xs text-center shadow-sm">
              发布新校园公告
            </button>
            <button className="w-full py-3 px-4 bg-white hover:bg-[#FDFCF8] text-[#5A5A40] border border-[#EFE9E1] rounded-full font-bold transition-colors text-xs text-center">
              新建班级
            </button>
            <button className="w-full py-3 px-4 bg-white hover:bg-[#FDFCF8] text-[#5A5A40] border border-[#EFE9E1] rounded-full font-bold transition-colors text-xs text-center">
              批量导入家长账号
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function StatCard({ title, value, trend, trendUp }: any) {
  return (
    <div className="bg-white rounded-[24px] p-6 shadow-sm border border-[#EFE9E1] flex flex-col">
      <p className="text-[10px] uppercase font-bold text-[#A09E94] mb-2 tracking-widest">{title}</p>
      <div className="flex items-end justify-between mt-auto">
        <h3 className="text-3xl font-serif italic text-[#5A5A40] leading-none">{value}</h3>
        <span className={`px-2 py-1 rounded-full text-[10px] font-bold ${trendUp ? 'bg-[#EAE5DD] text-[#5A5A40]' : 'bg-[#FF6B6B] bg-opacity-10 text-[#FF6B6B]'}`}>
          {trend}
        </span>
      </div>
    </div>
  );
}

function PlaceholderView({ title }: { title: string }) {
  return (
    <div className="h-96 flex flex-col items-center justify-center text-[#A09E94]">
      <LayoutDashboard className="w-16 h-16 mb-4 opacity-20" />
      <h2 className="text-xl font-medium">{title}</h2>
    </div>
  );
}
