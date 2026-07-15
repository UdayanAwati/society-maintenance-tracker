import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import DataTable from '../components/DataTable.jsx';
import StatCard from '../components/StatCard.jsx';
import StatusBadge from '../components/StatusBadge.jsx';
import { useAuth } from '../context/AuthContext.jsx';
import { api } from '../services/api.js';

export default function Dashboard() {
  const { user } = useAuth();
  const [data, setData] = useState(null);
  useEffect(() => {
    api.get(`/dashboard/${user.role === 'ADMIN' ? 'admin' : 'resident'}`).then((res) => setData(res.data));
  }, [user.role]);
  const complaints = data?.recentComplaints || [];
  return (
    <div className="space-y-6">
      <div className="grid gap-4 md:grid-cols-2 xl:grid-cols-5">
        <StatCard label="Total Complaints" value={data?.totalComplaints} />
        <StatCard label="Open" value={data?.open} tone="amber" />
        {user.role === 'ADMIN' && <StatCard label="In Progress" value={data?.inProgress} />}
        <StatCard label="Resolved" value={data?.resolved} tone="green" />
        {user.role === 'ADMIN' && <StatCard label="Overdue" value={data?.overdue} tone="red" />}
      </div>
      <section className="panel">
        <div className="mb-4 flex items-center justify-between"><h2 className="font-semibold">Recent Complaints</h2><Link to="/complaints" className="text-sm text-brand">View all</Link></div>
        <DataTable columns={[
          { key: 'id', label: 'ID', render: (r) => <Link className="text-brand" to={`/complaints/${r.id}`}>#{r.id}</Link> },
          { key: 'category', label: 'Category' },
          { key: 'resident', label: 'Resident', render: (r) => r.resident.name },
          { key: 'status', label: 'Status', render: (r) => <StatusBadge value={r.status} /> },
          { key: 'priority', label: 'Priority', render: (r) => <StatusBadge value={r.priority} /> },
        ]} rows={complaints} />
      </section>
    </div>
  );
}
