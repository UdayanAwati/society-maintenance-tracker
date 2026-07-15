export default function DataTable({ columns, rows, empty = 'No records found' }) {
  return (
    <div className="overflow-hidden rounded-lg border border-slate-200 dark:border-slate-800">
      <table className="min-w-full divide-y divide-slate-200 text-sm dark:divide-slate-800">
        <thead className="bg-slate-50 dark:bg-slate-900">
          <tr>{columns.map((column) => <th key={column.key} className="px-4 py-3 text-left font-semibold">{column.label}</th>)}</tr>
        </thead>
        <tbody className="divide-y divide-slate-100 bg-white dark:divide-slate-800 dark:bg-slate-950">
          {rows?.length ? rows.map((row, index) => (
            <tr key={row.id || index}>{columns.map((column) => <td key={column.key} className="px-4 py-3">{column.render ? column.render(row) : row[column.key]}</td>)}</tr>
          )) : <tr><td className="px-4 py-6 text-center text-slate-500" colSpan={columns.length}>{empty}</td></tr>}
        </tbody>
      </table>
    </div>
  );
}
