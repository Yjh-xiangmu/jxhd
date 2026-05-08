import { Link } from "react-router-dom";
import { UserCog, BookOpen, Baby, ShieldCheck, ArrowRight } from "lucide-react";
import { motion } from "framer-motion";

export default function Home() {
  return (
    <div className="min-h-screen bg-[#FDFCF8] flex flex-col items-center justify-center p-6 font-sans">
      <motion.div 
        initial={{ opacity: 0, y: -20 }}
        animate={{ opacity: 1, y: 0 }}
        className="text-center max-w-2xl text-[#333322] mb-12"
      >
        <h1 className="text-4xl md:text-5xl font-extrabold tracking-tight mb-4 text-[#5A5A40]">
          智云家园 <span className="text-[#5A5A40] opacity-80 italic font-serif">SmartKiddy</span>
        </h1>
        <p className="text-lg text-[#7A7A6A]">
          全场景家校共育平台。连接园所、教师与家庭，记录孩子的每一步成长。
        </p>
      </motion.div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 w-full max-w-5xl">
        <RoleCard 
          to="/admin"
          icon={<ShieldCheck className="w-10 h-10 text-[#5A5A40]" />}
          title="系统管理员"
          desc="全局管理、基础数据维护、资讯发布与论坛监管。"
          color="bg-[#F5F2ED] hover:bg-[#EAE5DD] border-[#EFE9E1]"
        />
        <RoleCard 
          to="/teacher"
          icon={<BookOpen className="w-10 h-10 text-[#5A5A40]" />}
          title="教师端"
          desc="班级管理、家校沟通、打卡考勤与成长点滴记录。"
          color="bg-[#F5F2ED] hover:bg-[#EAE5DD] border-[#EFE9E1]"
        />
        <RoleCard 
          to="/parent"
          icon={<Baby className="w-10 h-10 text-[#5A5A40]" />}
          title="家长端"
          desc="查看幼儿动态、家校双向共建、活动报名与论坛互动。"
          color="bg-[#F5F2ED] hover:bg-[#EAE5DD] border-[#EFE9E1]"
        />
      </div>
    </div>
  );
}

function RoleCard({ to, icon, title, desc, color }: { to: string; icon: React.ReactNode; title: string; desc: string; color: string }) {
  return (
    <Link to={to} className="block group">
      <motion.div 
        whileHover={{ scale: 1.02 }}
        whileTap={{ scale: 0.98 }}
        className={`h-full p-8 rounded-[32px] border transition-all duration-200 ${color} shadow-sm group-hover:shadow-md flex flex-col`}
      >
        <div className="mb-6 bg-white w-16 h-16 rounded-2xl shadow-sm flex items-center justify-center border border-[#EFE9E1]">
          {icon}
        </div>
        <h2 className="text-2xl font-bold text-[#5A5A40] mb-3">{title}</h2>
        <p className="text-[#7A7A6A] flex-grow leading-relaxed">
          {desc}
        </p>
        <div className="mt-6 flex items-center text-[#5A5A40] font-bold text-sm tracking-wider uppercase group-hover:translate-x-2 transition-transform duration-300">
          进入系统 <ArrowRight className="ml-2 w-4 h-4" />
        </div>
      </motion.div>
    </Link>
  );
}
