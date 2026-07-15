export default function StatCard({ label, value, tone = 'blue' }) {
  const colors = { blue: 'bg-blue-50 text-blue-700', green: 'bg-emerald-50 text-emerald-700', red: 'bg-red-50 text-red-700', amber: 'bg-amber-50 text-amber-700' };
  return (
    <div className="panel">
      <p className="text-sm text-slate-500">{label}</p>
      <p className={`mt-3 inline-flex rounded-md px-3 py-1 text-2xl font-bold ${colors[tone]}`}>{value ?? 0}</p>
    </div>
  );
}
