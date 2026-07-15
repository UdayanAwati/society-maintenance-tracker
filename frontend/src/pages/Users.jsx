import { useEffect, useState } from 'react';
import DataTable from '../components/DataTable.jsx';
import { api } from '../services/api.js';

export default function Users() {
  const [rows, setRows] = useState([]);
  useEffect(() => { api.get('/users').then((res) => setRows(res.data.content || [])); }, []);
  return <DataTable columns={[{ key: 'name', label: 'Name' }, { key: 'email', label: 'Email' }, { key: 'flatNumber', label: 'Flat' }, { key: 'role', label: 'Role' }]} rows={rows} />;
}
