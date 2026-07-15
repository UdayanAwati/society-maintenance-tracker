import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import DataTable from '../components/DataTable.jsx';
import StatusBadge from '../components/StatusBadge.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { api } from '../services/api.js';

export default function Complaints() {
  const { user } = useAuth();
  const [rows, setRows] = useState([]);
  const [filters, setFilters] = useState({ search: '', status: '', priority: '' });
  const load = () => {
    const params = new URLSearchParams(Object.entries(filters).filter(([, v]) => v));
    api.get(user.role === 'ADMIN' ? `/complaints?${params}` : '/complaints/mine').then((res) => setRows(res.data.content || []));
  };
  useEffect(load, [user.role]);
  return (
    <div className="space-y-4">
      <div className="flex flex-wrap items-center gap-3">
        {user.role === 'ADMIN' && <input className="input max-w-xs" placeholder="Search complaints" value={filters.search} onChange={(e) => setFilters({ ...filters, search: e.target.value })} />}
        {user.role === 'ADMIN' && <select className="input max-w-48" value={filters.status} onChange={(e) => setFilters({ ...filters, status: e.target.value })}><option value="">All status</option><option>OPEN</option><option>IN_PROGRESS</option><option>RESOLVED</option></select>}
        {user.role === 'ADMIN' && <select className="input max-w-48" value={filters.priority} onChange={(e) => setFilters({ ...filters, priority: e.target.value })}><option value="">All priority</option><option>LOW</option><option>MEDIUM</option><option>HIGH</option></select>}
        {user.role === 'ADMIN' && <button className="btn-primary" onClick={load}>Apply</button>}
        {user.role !== 'ADMIN' && <Link className="btn-primary" to="/complaints/new">New Complaint</Link>}
      </div>
      <DataTable columns={[
        { key: 'id', label: 'ID', render: (r) => <Link className="text-brand" to={`/complaints/${r.id}`}>#{r.id}</Link> },
        { key: 'category', label: 'Category' },
        { key: 'resident', label: 'Resident', render: (r) => r.resident.name },
        { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
        { key: 'priority', label: 'Priority', render: (r) => <StatusBadge value={r.priority} /> },
        { key: 'overdue', label: 'Overdue', render: (r) => r.overdue ? 'Yes' : 'No' },
      ]} rows={rows} />
    </div>
  );
}
