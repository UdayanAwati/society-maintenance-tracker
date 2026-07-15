export default function StatusBadge({ value }) {
  const tone = {
    OPEN: 'bg-amber-100 text-amber-800',
    IN_PROGRESS: 'bg-blue-100 text-blue-800',
    RESOLVED: 'bg-emerald-100 text-emerald-800',
    HIGH: 'bg-red-100 text-red-800',
    MEDIUM: 'bg-violet-100 text-violet-800',
    LOW: 'bg-slate-100 text-slate-700',
  };
  return <span className={`badge ${tone[value] || 'bg-slate-100 text-slate-700'}`}>{String(value || '').replace('_', ' ')}</span>;
}
