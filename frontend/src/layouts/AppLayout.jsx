import { NavLink, Outlet, useNavigate } from 'react-router-dom';
import { BarChart3, ClipboardList, Home, LogOut, Megaphone, Moon, PlusCircle, Sun, UserCircle, Users } from 'lucide-react';
import { useState } from 'react';
import { useAuth } from '../context/AuthContext.jsx';
import NotificationBell from '../components/NotificationBell.jsx';
import { assetUrl } from '../services/assets.js';

const linkClass = ({ isActive }) => `flex items-center gap-3 rounded-md px-3 py-2 text-sm font-medium ${isActive ? 'bg-blue-50 text-brand dark:bg-slate-800' : 'text-slate-600 hover:bg-slate-100 dark:text-slate-300 dark:hover:bg-slate-800'}`;

export default function AppLayout() {
  const { user, logout, dark, setDark } = useAuth();
  const [profileOpen, setProfileOpen] = useState(false);
  const navigate = useNavigate();
  const isAdmin = user?.role === 'ADMIN';
  const photo = assetUrl(user?.profilePhotoUrl);
  return (
    <div className="app-shell min-h-screen lg:grid lg:grid-cols-[260px_1fr]">
      <aside className="border-r border-slate-200 bg-white p-4 dark:border-slate-800 dark:bg-slate-950">
        <div className="mb-6">
          <p className="text-lg font-bold text-ink dark:text-white">Society Maintenance</p>
          <p className="text-sm text-slate-500">{user?.flatNumber || user?.role}</p>
        </div>
        <nav className="space-y-1">
          <NavLink className={linkClass} to="/"><Home size={18} />Dashboard</NavLink>
          <NavLink className={linkClass} to="/complaints"><ClipboardList size={18} />{isAdmin ? 'Manage Complaints' : 'My Complaints'}</NavLink>
          {!isAdmin && <NavLink className={linkClass} to="/complaints/new"><PlusCircle size={18} />Create Complaint</NavLink>}
          <NavLink className={linkClass} to="/notices"><Megaphone size={18} />Notice Board</NavLink>
          {isAdmin && <NavLink className={linkClass} to="/admin/notices/new"><PlusCircle size={18} />Create Notice</NavLink>}
          {isAdmin && <NavLink className={linkClass} to="/admin/analytics"><BarChart3 size={18} />Analytics</NavLink>}
          {isAdmin && <NavLink className={linkClass} to="/admin/users"><Users size={18} />Users</NavLink>}
        </nav>
      </aside>
      <main>
        <header className="sticky top-0 z-10 flex items-center justify-between border-b border-slate-200 bg-white/90 px-4 py-3 backdrop-blur dark:border-slate-800 dark:bg-slate-950/90">
          <div>
            <p className="text-sm text-slate-500">Welcome back</p>
            <h1 className="text-xl font-semibold">{user?.name}</h1>
          </div>
          <div className="flex items-center gap-2">
            <NotificationBell />
            <button className="btn-secondary px-3" onClick={() => setDark(!dark)} title="Toggle dark mode">{dark ? <Sun size={18} /> : <Moon size={18} />}</button>
            <div className="relative">
              <button className="btn-secondary px-3" onClick={() => setProfileOpen(!profileOpen)} title="Profile">
                {photo ? <img className="h-6 w-6 rounded-full object-cover" src={photo} alt="" /> : <UserCircle size={20} />}
              </button>
              {profileOpen && (
                <div className="absolute right-0 mt-2 w-72 rounded-lg border border-slate-200 bg-white p-4 shadow-lg dark:border-slate-800 dark:bg-slate-900">
                  <div className="flex items-center gap-3">
                    {photo ? <img className="h-12 w-12 rounded-full object-cover" src={photo} alt="" /> : <UserCircle className="text-slate-400" size={44} />}
                    <div className="min-w-0">
                      <p className="truncate font-semibold">{user?.name}</p>
                      <p className="truncate text-sm text-slate-500">{user?.email}</p>
                    </div>
                  </div>
                  <div className="mt-3 space-y-1 text-sm text-slate-600 dark:text-slate-300">
                    <p>Phone: {user?.phone || 'Not added'}</p>
                    <p>Flat: {user?.flatNumber || 'Not added'}</p>
                  </div>
                  <div className="mt-4 grid gap-2">
                    <button className="btn-secondary w-full" onClick={() => { setProfileOpen(false); navigate('/profile'); }}>Edit Profile</button>
                    <button className="btn-secondary w-full text-red-600" onClick={() => { logout(); navigate('/login'); }}><LogOut size={16} />Logout</button>
                  </div>
                </div>
              )}
            </div>
          </div>
        </header>
        <div className="p-4 lg:p-6"><Outlet /></div>
      </main>
    </div>
  );
}
