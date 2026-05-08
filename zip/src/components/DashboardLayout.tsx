import React, { useState } from "react";
import { Link, useLocation } from "react-router-dom";
import { motion, AnimatePresence } from "framer-motion";
import { Menu, X, LogOut, Bell } from "lucide-react";
import { cn } from "../lib/utils";

interface SidebarItem {
  icon: React.ReactNode;
  label: string;
  path: string;
}

interface DashboardLayoutProps {
  children: React.ReactNode;
  items: SidebarItem[];
  basePath: string;
  roleName: string;
  themeColor: string;
}

export function DashboardLayout({ children, items, basePath, roleName, themeColor }: DashboardLayoutProps) {
  const [sidebarOpen, setSidebarOpen] = useState(false);
  const location = useLocation();

  return (
    <div className="flex h-screen bg-[#FDFCF8] text-[#333322] overflow-hidden font-sans selection:bg-[#5A5A40] selection:text-white">
      {/* Mobile Sidebar Overlay */}
      <AnimatePresence>
        {sidebarOpen && (
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            exit={{ opacity: 0 }}
            onClick={() => setSidebarOpen(false)}
            className="fixed inset-0 bg-slate-900/50 z-40 lg:hidden"
          />
        )}
      </AnimatePresence>

      {/* Sidebar */}
      <motion.aside
        className={cn(
          "fixed lg:static inset-y-0 left-0 z-50 w-64 bg-[#F5F2ED] border-r border-[#EFE9E1] transform transition-transform duration-300 ease-in-out flex flex-col p-6 gap-6",
          sidebarOpen ? "translate-x-0" : "-translate-x-full lg:translate-x-0"
        )}
      >
        <div className="flex items-center gap-3 mb-2">
          <div className="w-10 h-10 bg-[#5A5A40] rounded-xl flex items-center justify-center shrink-0">
            <div className="w-5 h-5 border-2 border-white rounded-full"></div>
          </div>
          <div>
            <h1 className="text-xl font-bold tracking-tight text-[#5A5A40] uppercase leading-none">Smart<span className="font-light opacity-60">Kiddy</span></h1>
            <p className="text-[10px] text-[#A09E94] font-medium mt-1">{roleName}</p>
          </div>
        </div>

        <nav className="flex-1 overflow-y-auto space-y-2">
          <p className="text-[10px] uppercase tracking-widest text-[#A09E94] font-bold mb-4">Core Functions</p>
          {items.map((item) => {
            const isActive = location.pathname === item.path || (location.pathname === basePath && item.path === basePath);
            return (
              <Link
                key={item.path}
                to={item.path}
                onClick={() => setSidebarOpen(false)}
                className={cn(
                  "flex items-center gap-3 px-3 py-2.5 rounded-lg text-sm font-medium transition-colors",
                  isActive
                    ? `bg-[#5A5A40] text-white`
                    : "text-[#7A7A6A] hover:bg-[#EAE5DD]"
                )}
              >
                <div className={cn("shrink-0", isActive ? "text-white" : "text-[#7A7A6A]")}>
                  {item.icon}
                </div>
                {item.label}
              </Link>
            );
          })}
        </nav>

        <div className="mt-auto">
          <Link
            to="/"
            className="flex items-center gap-3 px-3 py-2 text-[#7A7A6A] hover:bg-[#EAE5DD] rounded-lg text-sm font-medium transition-colors"
          >
            <LogOut className="w-5 h-5 shrink-0" />
            退出系统
          </Link>
        </div>
      </motion.aside>

      {/* Main Content */}
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        {/* Header */}
        <header className="h-16 bg-white border-b border-[#EFE9E1] flex items-center justify-between px-4 sm:px-8 shrink-0 shadow-sm">
          <div className="flex items-center">
            <button
              onClick={() => setSidebarOpen(true)}
              className="lg:hidden p-2 -ml-2 mr-2 text-[#5A5A40] hover:bg-[#F5F2ED] rounded-md"
            >
              <Menu className="w-5 h-5" />
            </button>
            <h2 className="text-2xl font-serif italic text-[#5A5A40]">
              {items.find(i => i.path === location.pathname)?.label || "Dashboard"}
            </h2>
          </div>
          
          <div className="flex items-center gap-6">
            <button className="p-2 text-[#7A7A6A] hover:bg-[#F5F2ED] rounded-full relative transition-colors">
              <Bell className="w-5 h-5" />
              <span className="absolute top-2 right-2 w-2 h-2 bg-[#FF6B6B] rounded-full"></span>
            </button>
            <div className="flex items-center gap-3 border-l pl-6 border-[#EFE9E1]">
              <div className="text-right hidden sm:block">
                <p className="text-xs font-bold leading-none text-[#5A5A40]">用户中心</p>
                <p className="text-[10px] text-[#A09E94]">{roleName}</p>
              </div>
              <div className="w-10 h-10 rounded-full bg-[#E5E0D8] border-2 border-white flex items-center justify-center text-[#A09E94] font-bold">
                U
              </div>
            </div>
          </div>
        </header>

        {/* Dynamic Content */}
        <main className="flex-1 overflow-y-auto bg-[#FDFCF8] p-4 sm:p-8">
          <motion.div
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
            exit={{ opacity: 0, y: -10 }}
            transition={{ duration: 0.2 }}
          >
            {children}
          </motion.div>
        </main>
      </div>
    </div>
  );
}
