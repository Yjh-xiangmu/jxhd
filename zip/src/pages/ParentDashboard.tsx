import { Routes, Route, Navigate } from "react-router-dom";
import { Baby, Sun, Home, MessageSquare, Calendar, Users } from "lucide-react";
import { DashboardLayout } from "../components/DashboardLayout";

const parentMenu = [
  { icon: <Baby className="w-5 h-5" />, label: "成长时光机", path: "/parent" },
  { icon: <Sun className="w-5 h-5" />, label: "在园动态查看", path: "/parent/school" },
  { icon: <Home className="w-5 h-5" />, label: "在家表现补充", path: "/parent/home" },
  { icon: <MessageSquare className="w-5 h-5" />, label: "家校沟通", path: "/parent/messages" },
  { icon: <Calendar className="w-5 h-5" />, label: "活动发现", path: "/parent/activities" },
  { icon: <Users className="w-5 h-5" />, label: "家长互助论坛", path: "/parent/forum" },
];

export default function ParentDashboard() {
  return (
    <DashboardLayout items={parentMenu} basePath="/parent" roleName="张小明妈妈" themeColor="text-[#5A5A40]">
      <Routes>
        <Route path="/" element={<ParentOverview />} />
        <Route path="*" element={<PlaceholderView title="模块开发中" />} />
      </Routes>
    </DashboardLayout>
  );
}

function ParentOverview() {
  return (
    <div className="space-y-6 max-w-4xl mx-auto">
      {/* Profile Header */}
      <div className="bg-white rounded-[32px] p-6 md:p-8 shadow-sm border border-[#EFE9E1] flex flex-col md:flex-row items-center relative overflow-hidden">
        <div className="absolute top-0 left-0 w-full h-2 bg-[#5A5A40]"></div>
        <div className="relative">
          <div className="w-24 h-24 rounded-full border-4 border-[#EFE9E1] bg-[#F5F2ED] flex items-center justify-center text-4xl font-bold overflow-hidden mb-4 md:mb-0 md:mr-6">
            👦🏻
          </div>
          <div className="absolute bottom-4 -right-2 md:right-4 w-6 h-6 bg-green-400 rounded-full border-4 border-white"></div>
        </div>
        <div className="text-center md:text-left flex-1">
          <h2 className="text-2xl font-serif italic text-[#5A5A40]">张小明</h2>
          <p className="text-[11px] text-[#A09E94] font-bold uppercase tracking-widest mt-1">小青蛙一班 (小班) | 班主任: 王老师</p>
          <div className="mt-4 inline-flex items-center px-4 py-1.5 rounded-full bg-[#F5F2ED] border border-[#EFE9E1] text-[11px] font-bold text-[#5A5A40] uppercase tracking-wider">
            小明现在在园，状态良好 🌟
          </div>
        </div>
        <div className="hidden md:block text-right border-l border-[#EFE9E1] pl-8 ml-6">
          <p className="text-[10px] text-[#A09E94] font-bold uppercase tracking-widest mb-1">本周出勤</p>
          <p className="text-4xl font-serif italic text-[#5A5A40]">4<span className="text-sm font-sans text-[#A09E94] not-italic">/5 天</span></p>
        </div>
      </div>

      {/* Main Content Grid */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {/* Unified Timeline View */}
        <div className="bg-[#EFE9E1] rounded-[32px] p-6 md:p-8 md:col-span-2">
          <div className="flex justify-between items-center mb-8">
            <h3 className="text-xl font-serif italic text-[#5A5A40]">
              Growth Log / 混合成长档案
            </h3>
            <button className="bg-[#5A5A40] text-white font-bold text-[11px] tracking-wider uppercase px-5 py-2 rounded-full transition-colors flex items-center gap-2">
              <span>+</span> 记录在家表现
            </button>
          </div>

          <div className="space-y-6 relative before:absolute before:inset-0 before:ml-5 before:-translate-x-px md:before:mx-auto md:before:translate-x-0 before:h-full before:w-px before:bg-[#D5D0C8]">
            {/* Timeline Item 1 - School */}
            <div className="relative flex items-center justify-between md:justify-normal md:odd:flex-row-reverse group is-active">
              <div className="flex items-center justify-center w-10 h-10 rounded-full border-[3px] border-[#EFE9E1] bg-white text-[#5A5A40] shrink-0 md:order-1 md:group-odd:-translate-x-1/2 md:group-even:translate-x-1/2 shadow-sm z-10">
                <Sun className="w-4 h-4" />
              </div>
              <div className="w-[calc(100%-4rem)] md:w-[calc(50%-2.5rem)] bg-white p-5 rounded-[24px] border border-[#EFE9E1] shadow-sm ml-4 md:ml-0">
                <div className="flex justify-between items-start mb-3">
                  <span className="text-[10px] font-bold uppercase tracking-widest px-3 py-1 bg-[#F5F2ED] text-[#7A7A6A] rounded-full">🏫 在校记录</span>
                  <time className="text-[10px] text-[#A09E94] font-medium italic">上午 10:30</time>
                </div>
                <h4 className="font-bold text-[#333322] text-sm">户外运动区抓拍</h4>
                <p className="text-[11px] text-[#7A7A6A] mt-2 leading-relaxed">小明今天在攀爬架玩得很开心，非常勇敢呢！</p>
                <div className="mt-4 aspect-video bg-[#F5F2ED] rounded-xl flex items-center justify-center text-[#A09E94] text-xs font-bold uppercase tracking-widest overflow-hidden border border-[#EFE9E1]">
                  <span>Photo Attachment</span>
                </div>
                <p className="text-[10px] text-[#A09E94] mt-3 text-right italic">-- 林老师</p>
              </div>
            </div>

            {/* Timeline Item 2 - Home */}
            <div className="relative flex items-center justify-between md:justify-normal md:odd:flex-row-reverse group is-active">
              <div className="flex items-center justify-center w-10 h-10 rounded-full border-[3px] border-[#EFE9E1] bg-[#5A5A40] text-white shrink-0 md:order-1 md:group-odd:-translate-x-1/2 md:group-even:translate-x-1/2 shadow-sm z-10">
                <Home className="w-4 h-4" />
              </div>
              <div className="w-[calc(100%-4rem)] md:w-[calc(50%-2.5rem)] bg-white p-5 rounded-[24px] border border-[#EFE9E1] shadow-sm ml-4 md:ml-0">
                <div className="flex justify-between items-start mb-3">
                  <span className="text-[10px] font-bold uppercase tracking-widest px-3 py-1 bg-[#5A5A40] text-white rounded-full">🏠 在家表现</span>
                  <time className="text-[10px] text-[#A09E94] font-medium italic">昨晚 20:15</time>
                </div>
                <h4 className="font-bold text-[#333322] text-sm">阅读绘本打卡</h4>
                <p className="text-[11px] text-[#7A7A6A] mt-2 leading-relaxed">自己主动拿了《猜猜我有多爱你》看，还能指认出小兔子呢。</p>
              </div>
            </div>
            
             {/* Timeline Item 3 - School */}
             <div className="relative flex items-center justify-between md:justify-normal md:odd:flex-row-reverse group is-active">
              <div className="flex items-center justify-center w-10 h-10 rounded-full border-[3px] border-[#EFE9E1] bg-white text-[#7A7A6A] shrink-0 md:order-1 md:group-odd:-translate-x-1/2 md:group-even:translate-x-1/2 shadow-sm z-10">
                <Calendar className="w-4 h-4" />
              </div>
              <div className="w-[calc(100%-4rem)] md:w-[calc(50%-2.5rem)] bg-white p-5 rounded-[24px] border border-[#EFE9E1] shadow-sm ml-4 md:ml-0 opacity-80">
                <div className="flex justify-between items-start mb-3">
                  <span className="text-[10px] font-bold uppercase tracking-widest px-3 py-1 bg-[#F5F2ED] text-[#A09E94] rounded-full">📢 入园登记</span>
                  <time className="text-[10px] text-[#A09E94] font-medium italic">昨天 08:30</time>
                </div>
                <h4 className="font-bold text-[#7A7A6A] text-sm">晨检通过</h4>
                <p className="text-[11px] text-[#A09E94] mt-2 leading-relaxed">体温: 36.5°C，口腔黏膜正常，状态良好。</p>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

function PlaceholderView({ title }: { title: string }) {
  return (
    <div className="h-96 flex flex-col items-center justify-center text-[#A09E94]">
      <Baby className="w-16 h-16 mb-4 opacity-20" />
      <h2 className="text-xl font-medium">{title}</h2>
    </div>
  );
}
