import { useEffect, useState } from 'react';
import { BarChart, DoughnutChart } from '../components/Charts.jsx';
import { api } from '../services/api.js';

export default function Analytics() {
  const [data, setData] = useState(null);
  useEffect(() => { api.get('/dashboard/admin').then((res) => setData(res.data)); }, []);
  return (
    <div className="grid gap-4 xl:grid-cols-3">
      <DoughnutChart title="Complaints by Status" rows={data?.byStatus} />
      <BarChart title="Complaints by Category" rows={data?.byCategory} />
      <DoughnutChart title="Complaints by Priority" rows={data?.byPriority} />
    </div>
  );
}
