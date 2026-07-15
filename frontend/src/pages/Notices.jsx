import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAuth } from '../context/AuthContext.jsx';
import { api } from '../services/api.js';

export default function Notices() {
  const { user } = useAuth();
  const [rows, setRows] = useState([]);
  const [editing, setEditing] = useState(null);

  const load = () => api.get('/notices').then((res) => setRows(res.data.content || []));
  useEffect(() => { load(); }, []);

  const saveNotice = async (notice, patch) => {
    try {
      await api.put(`/notices/${notice.id}`, { ...notice, ...patch });
      toast.success('Notice updated');
      setEditing(null);
      load();
    } catch (error) {
      toast.error(error.message);
    }
  };

  const deleteNotice = async (notice) => {
    if (!window.confirm(`Delete "${notice.title}"?`)) return;
    try {
      await api.delete(`/notices/${notice.id}`);
      toast.success('Notice deleted');
      load();
    } catch (error) {
      toast.error(error.message);
    }
  };

  return (
    <div className="space-y-4">
      <div className="flex items-center justify-between">
        <h2 className="text-xl font-semibold">Notice Board</h2>
        {user.role === 'ADMIN' && <Link className="btn-primary" to="/admin/notices/new">Create Notice</Link>}
      </div>
      <div className="grid gap-4 md:grid-cols-2">
        {rows.map((notice) => (
          <article key={notice.id} className="panel space-y-3">
            {editing?.id === notice.id ? (
              <>
                <input className="input" value={editing.title} onChange={(e) => setEditing({ ...editing, title: e.target.value })} />
                <textarea className="input min-h-28" value={editing.description} onChange={(e) => setEditing({ ...editing, description: e.target.value })} />
                <label className="flex items-center gap-2 text-sm">
                  <input type="checkbox" checked={editing.important} onChange={(e) => setEditing({ ...editing, important: e.target.checked })} />
                  Pinned
                </label>
                <div className="flex flex-wrap gap-2">
                  <button className="btn-primary" onClick={() => saveNotice(notice, editing)}>Save</button>
                  <button className="btn-secondary" onClick={() => setEditing(null)}>Cancel</button>
                </div>
              </>
            ) : (
              <>
                <div className="flex items-start justify-between gap-3">
                  <h3 className="font-semibold">{notice.title}</h3>
                  {notice.important && <span className="badge bg-red-100 text-red-700">Pinned</span>}
                </div>
                <p className="text-sm text-slate-600 dark:text-slate-300">{notice.description}</p>
                <p className="text-xs text-slate-500">{notice.createdBy} - {new Date(notice.createdAt).toLocaleString()}</p>
                {user.role === 'ADMIN' && (
                  <div className="flex flex-wrap gap-2 pt-2">
                    <button className="btn-secondary" onClick={() => setEditing(notice)}>Edit</button>
                    <button className="btn-secondary" onClick={() => saveNotice(notice, { important: !notice.important })}>{notice.important ? 'Unpin' : 'Pin'}</button>
                    <button className="btn-secondary text-red-600" onClick={() => deleteNotice(notice)}>Delete</button>
                  </div>
                )}
              </>
            )}
          </article>
        ))}
      </div>
      {!rows.length && <div className="panel text-center text-sm text-slate-500">No notices posted yet.</div>}
    </div>
  );
}
