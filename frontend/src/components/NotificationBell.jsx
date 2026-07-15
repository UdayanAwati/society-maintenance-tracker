import { useEffect, useState } from 'react';
import { Bell } from 'lucide-react';
import { api } from '../services/api.js';

export default function NotificationBell() {
  const [count, setCount] = useState(0);
  useEffect(() => {
    api.get('/notifications/unread-count').then((res) => setCount(res.data)).catch(() => {});
  }, []);
  return (
    <button className="btn-secondary relative px-3" title="Notifications">
      <Bell size={18} />
      {count > 0 && <span className="absolute -right-1 -top-1 rounded-full bg-coral px-1.5 text-xs text-white">{count}</span>}
    </button>
  );
}
