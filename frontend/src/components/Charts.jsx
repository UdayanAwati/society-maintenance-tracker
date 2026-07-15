import { ArcElement, BarElement, CategoryScale, Chart as ChartJS, Legend, LinearScale, Tooltip } from 'chart.js';
import { Bar, Doughnut } from 'react-chartjs-2';

ChartJS.register(ArcElement, BarElement, CategoryScale, LinearScale, Tooltip, Legend);

const palette = ['#2563eb', '#0f766e', '#dc2626', '#7c3aed', '#ca8a04', '#0891b2'];

export function DoughnutChart({ title, rows = [] }) {
  const data = { labels: rows.map((r) => r.label), datasets: [{ data: rows.map((r) => r.value), backgroundColor: palette }] };
  return <div className="panel"><h2 className="mb-4 font-semibold">{title}</h2><Doughnut data={data} /></div>;
}

export function BarChart({ title, rows = [] }) {
  const data = { labels: rows.map((r) => r.label), datasets: [{ label: title, data: rows.map((r) => r.value), backgroundColor: '#2563eb' }] };
  return <div className="panel"><h2 className="mb-4 font-semibold">{title}</h2><Bar data={data} /></div>;
}
