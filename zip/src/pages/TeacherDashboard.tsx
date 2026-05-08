import { Routes, Route, Navigate } from "react-router-dom";
import { Users, MessageCircle, Camera, CalendarClock, Activity, BookOpenCheck } from "lucide-react";
import { DashboardLayout } from "../components/DashboardLayout";

const teacherMenu = [
  { icon: <Users className="w-5 h-5" />, label: "班级花名册", path: "/teacher" },
  { icon: <MessageCircle className="w-5 h-5" />, label: "家校通讯录", path: "/teacher/messages" },
  { icon: <Camera className="w-5 h-5" />, label: "成长动态发布", path: "/teacher/growth" },
  { icon: <CalendarClock className="w-5 h-5" />, label: "考勤与课表", path: "/teacher/schedule" },
  { icon: <Activity className="w-5 h-5" />, label: "活动跟进", path: "/teacher/activities" },
  { icon: <BookOpenCheck className="w-5 h-5" />, label: "论坛互动", path: "/teacher/forum" },
];

export default function TeacherDashboard() {
  return (
    <DashboardLayout items={teacherMenu} basePath="/teacher" roleName="大二班·李老师" themeColor="text-[#5A5A40]">
      <Routes>
        <Route path="/" element={<TeacherOverview />} />
        <Route path="*" element={<PlaceholderView title="建设中" />} />
      </Routes>
    </DashboardLayout>
  );
}

function TeacherOverview() {
  const students = [
    { id: 1, name: "张小明", status: "在园", temp: "36.5°C" },
    { id: 2, name: "李艺彤", status: "请假", temp: "-" },
    { id: 3, name: "王浩宇", status: "在园", temp: "36.6°C" },
    { id: 4, name: "赵子轩", status: "在园", temp: "36.4°C" },
    { id: 5, name: "陈紫涵", status: "未到", temp: "-" },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col md:flex-row justify-between items-start md:items-center space-y-4 md:space-y-0">
        <div>
          <h2 className="text-2xl font-serif italic text-[#5A5A40]">大二班 (中班)</h2>
          <p className="text-[#A09E94] text-xs font-bold uppercase tracking-wider mt-1">班主任：李老师 | 应到: 25人, 实到: 23人</p>
        </div>
        <div className="flex space-x-3">
          <button className="bg-[#5A5A40] text-white px-5 py-2 rounded-full text-sm font-medium flex items-center transition-colors">
            <Camera className="w-4 h-4 mr-2" />
            快速抓拍动态
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        <div className="bg-white rounded-[32px] shadow-sm border border-[#EFE9E1] p-6 lg:col-span-2">
          <div className="flex justify-between items-center mb-6">
            <h3 className="text-sm font-bold text-[#5A5A40] uppercase tracking-wider">今日考勤概览</h3>
            <button className="text-[11px] text-[#5A5A40] font-bold underline">一键标记全勤</button>
          </div>
          
          <div className="overflow-x-auto">
            <table className="w-full text-sm text-left">
              <thead className="text-[10px] text-[#A09E94] bg-[#F5F2ED] uppercase tracking-widest rounded-xl">
                <tr>
                  <th className="px-4 py-3 rounded-l-xl font-bold">姓名</th>
                  <th className="px-4 py-3 font-bold">状态</th>
                  <th className="px-4 py-3 font-bold">晨检体温</th>
                  <th className="px-4 py-3 rounded-r-xl font-bold">操作</th>
                </tr>
              </thead>
              <tbody>
                {students.map((s) => (
                  <tr key={s.id} className="border-b border-[#EFE9E1] last:border-0 hover:bg-[#FDFCF8]">
                    <td className="px-4 py-4 font-bold text-[#333322]">{s.name}</td>
                    <td className="px-4 py-4">
                      <span className={`px-3 py-1 rounded-full text-[10px] font-bold uppercase tracking-tighter ${
                        s.status === '在园' ? 'bg-[#EAE5DD] text-[#5A5A40]' :
                        s.status === '请假' ? 'bg-[#FF6B6B] bg-opacity-10 text-[#FF6B6B]' : 'bg-[#EFE9E1] text-[#A09E94]'
                      }`}>
                        {s.status}
                      </span>
                    </td>
                    <td className="px-4 py-4 text-[#7A7A6A] text-xs font-medium">{s.temp}</td>
                    <td className="px-4 py-4 space-x-3">
                      <button className="text-[11px] text-[#5A5A40] font-bold underline">点评</button>
                      <button className="text-[11px] text-[#5A5A40] font-bold underline">私信</button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="space-y-6 flex flex-col">
          <div className="bg-[#EFE9E1] rounded-[32px] p-6 flex flex-col flex-1">
            <h3 className="text-sm font-bold text-[#5A5A40] uppercase tracking-wider mb-6">班级动态</h3>
            <div className="space-y-5">
              <div className="flex items-start gap-3">
                <div className="w-8 h-8 rounded-full bg-white border border-[#EFE9E1] flex items-center justify-center text-[#5A5A40] text-[10px] font-bold shrink-0">王</div>
                <div className="flex-1">
                  <p className="text-xs font-bold text-[#333322]">王浩宇妈妈</p>
                  <p className="text-[11px] text-[#7A7A6A] leading-relaxed mt-1">李老师您好，浩宇今天穿的外套比较薄，能麻烦让他穿上备用的衣服吗？</p>
                  <p className="text-[10px] text-[#A09E94] mt-1 italic">10 分钟前</p>
                </div>
              </div>
              <div className="flex items-start gap-3 opacity-70">
                <div className="w-8 h-8 rounded-full bg-white border border-[#EFE9E1] flex items-center justify-center text-[#5A5A40] text-[10px] font-bold shrink-0">陈</div>
                <div className="flex-1">
                  <p className="text-xs font-bold text-[#333322]">陈紫涵爸爸</p>
                  <p className="text-[11px] text-[#7A7A6A] leading-relaxed mt-1">今天家里有事，紫涵晚点到，已提交请假申请。</p>
                  <p className="text-[10px] text-[#A09E94] mt-1 italic">30 分钟前</p>
                </div>
              </div>
            </div>
            <button className="w-full mt-auto mb-2 py-2.5 border border-[#5A5A40] text-[#5A5A40] text-[11px] font-bold rounded-full hover:bg-[#5A5A40] hover:text-white transition-colors">
              进入大二班沟通群
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

function PlaceholderView({ title }: { title: string }) {
  return (
    <div className="h-96 flex flex-col items-center justify-center text-[#A09E94]">
      <MessageCircle className="w-16 h-16 mb-4 opacity-20" />
      <h2 className="text-xl font-medium">{title}</h2>
    </div>
  );
}
