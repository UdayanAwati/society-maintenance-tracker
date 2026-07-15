import { useEffect, useState } from 'react';
import { Bell, CheckCircle2 } from 'lucide-react';
import { api } from '../services/api.js';

export default function NotificationBell() {
  const [count, setCount] = useState(0);
  const [open, setOpen] = useState(false);
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(false);

  const openNotifications = async () => {
    const nextOpen = !open;
    setOpen(nextOpen);
    if (!nextOpen) return;
    setLoading(true);
    try {
      const res = await api.get('/notifications');
      const notifications = res.data || [];
      setItems(notifications);
      await Promise.all(notifications.filter((item) => !item.read).map((item) => api.patch(`/notifications/${item.id}/read`)));
      setCount(0);
    } catch {
      setItems([]);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    api.get('/notifications/unread-count').then((res) => setCount(res.data)).catch(() => {});
  }, []);

  return (
    <div className="relative">
      <button className="btn-secondary relative px-3" onClick={openNotifications} title="Notifications">
        <Bell size={18} />
        {count > 0 && <span className="absolute -right-1 -top-1 rounded-full bg-coral px-1.5 text-xs text-white">{count}</span>}
      </button>
      {open && (
        <div className="absolute right-0 mt-2 w-80 overflow-hidden rounded-lg border border-slate-200 bg-white shadow-lg dark:border-slate-800 dark:bg-slate-900">
          <div className="flex items-center justify-between border-b border-slate-100 px-4 py-3 dark:border-slate-800">
            <p className="font-semibold">Notifications</p>
            <CheckCircle2 className="text-emerald-500" size={18} />
          </div>
          <div className="max-h-96 overflow-y-auto">
            {loading && <p className="px-4 py-3 text-sm text-slate-500">Loading...</p>}
            {!loading && items.length === 0 && <p className="px-4 py-3 text-sm text-slate-500">No notifications yet.</p>}
            {!loading && items.map((item) => (
              <div key={item.id} className="border-b border-slate-100 px-4 py-3 text-sm last:border-b-0 dark:border-slate-800">
                <p className="text-slate-800 dark:text-slate-100">{item.message}</p>
                <p className="mt-1 text-xs text-slate-500">{new Date(item.createdAt).toLocaleString()}</p>
              </div>
            ))}
          </div>
        </div>
      )}
    </div>
  );
}
