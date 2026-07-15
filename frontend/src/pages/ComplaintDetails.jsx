import { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { toast } from 'react-toastify';
import StatusBadge from '../components/StatusBadge.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { assetUrl } from '../services/assets.js';
import { api } from '../services/api.js';

export default function ComplaintDetails() {
  const { id } = useParams();
  const navigate = useNavigate();
  const { user } = useAuth();
  const [complaint, setComplaint] = useState(null);
  const [imageFailed, setImageFailed] = useState(false);
  const [form, setForm] = useState({ status: '', priority: '', note: '', assignedTechnician: '', overdue: false });
  const load = () => api.get(`/complaints/${id}`).then((res) => {
    setComplaint(res.data);
    setImageFailed(false);
  });
  useEffect(() => { load(); }, [id]);
  useEffect(() => {
    if (!complaint) return;
    setForm({
      status: complaint.status || 'OPEN',
      priority: complaint.priority || 'MEDIUM',
      note: '',
      assignedTechnician: complaint.assignedTechnician || '',
      overdue: Boolean(complaint.overdue),
    });
  }, [complaint?.id]);
  const save = async () => {
    try { await api.patch(`/complaints/${id}`, { ...form, status: form.status || null, priority: form.priority || null }); toast.success('Admin actions saved'); load(); }
    catch (error) { toast.error(error.message); }
  };
  const closeComplaint = async () => {
    try { await api.patch(`/complaints/${id}`, { ...form, closeComplaint: true, note: form.note || 'Complaint closed' }); toast.success('Complaint closed'); load(); }
    catch (error) { toast.error(error.message); }
  };
  const deleteComplaint = async () => {
    if (!window.confirm(`Delete complaint #${id}?`)) return;
    try {
      await api.delete(`/complaints/${id}`);
      toast.success('Complaint deleted');
      navigate('/complaints');
    } catch (error) {
      toast.error(error.message);
    }
  };
  if (!complaint) return <div className="panel">Loading...</div>;
  const photoUrl = assetUrl(complaint.photoUrl);
  return (
    <div className="grid gap-6 xl:grid-cols-[1fr_380px]">
      <section className="panel space-y-4">
        <div className="flex flex-wrap items-center gap-3"><h2 className="text-xl font-semibold">Complaint #{complaint.id}</h2><StatusBadge value={complaint.status} /><StatusBadge value={complaint.priority} />{complaint.overdue && <span className="badge bg-red-100 text-red-700">Overdue</span>}</div>
        {user.role !== 'ADMIN' && <button className="btn-secondary w-fit text-red-600" onClick={deleteComplaint}>Delete Complaint</button>}
        <div className="text-sm text-slate-500">
          <p>{complaint.category} by {complaint.resident.name}</p>
          <p>Flat: {complaint.resident.flatNumber || 'Not added'}</p>
          {complaint.assignedTechnician && <p>Technician: {complaint.assignedTechnician}</p>}
        </div>
        <p>{complaint.description}</p>
        {photoUrl && !imageFailed && <img className="max-h-96 rounded-md border object-contain" src={photoUrl} alt="Complaint" onError={() => setImageFailed(true)} />}
        {photoUrl && imageFailed && (
          <div className="rounded-md border border-amber-200 bg-amber-50 p-3 text-sm text-amber-800">
            Image could not be loaded. <a className="font-semibold underline" href={photoUrl} target="_blank" rel="noreferrer">Open image</a>
          </div>
        )}
        <h3 className="font-semibold">Timeline</h3>
        <ol className="space-y-3">
          {complaint.history.map((item) => <li key={item.id} className="rounded-md border p-3 dark:border-slate-800"><StatusBadge value={item.status} /><p className="mt-2 text-sm">{item.note}</p><p className="text-xs text-slate-500">{item.updatedBy} • {new Date(item.timestamp).toLocaleString()}</p></li>)}
        </ol>
      </section>
      {user.role === 'ADMIN' && (
        <aside className="panel space-y-4">
          <h3 className="font-semibold">Admin Actions</h3>
          <label className="text-sm">Change Status<select className="input mt-1" value={form.status} onChange={(e) => setForm({ ...form, status: e.target.value })}><option>OPEN</option><option>IN_PROGRESS</option><option>RESOLVED</option></select></label>
          <label className="text-sm">Change Priority<select className="input mt-1" value={form.priority} onChange={(e) => setForm({ ...form, priority: e.target.value })}><option>LOW</option><option>MEDIUM</option><option>HIGH</option></select></label>
          <label className="text-sm">Assign Technician<input className="input mt-1" placeholder="Technician name" value={form.assignedTechnician} onChange={(e) => setForm({ ...form, assignedTechnician: e.target.value })} /></label>
          <label className="flex items-center gap-2 text-sm"><input type="checkbox" checked={form.overdue} onChange={(e) => setForm({ ...form, overdue: e.target.checked })} /> Mark as overdue</label>
          <label className="text-sm">Internal Note<textarea className="input mt-1 min-h-28" placeholder="Add note for timeline" value={form.note} onChange={(e) => setForm({ ...form, note: e.target.value })} /></label>
          <button className="btn-primary w-full" onClick={save}>Save Admin Actions</button>
          <button className="btn-secondary w-full text-emerald-700" onClick={closeComplaint}>Close Complaint</button>
        </aside>
      )}
    </div>
  );
}
