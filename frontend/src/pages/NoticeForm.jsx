import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { api } from '../services/api.js';

export default function NoticeForm() {
  const { register, handleSubmit } = useForm();
  const navigate = useNavigate();
  const submit = async (values) => {
    try { await api.post('/notices', { ...values, important: Boolean(values.important) }); toast.success('Notice created'); navigate('/notices'); }
    catch (error) { toast.error(error.message); }
  };
  return (
    <form onSubmit={handleSubmit(submit)} className="panel max-w-3xl space-y-4">
      <h2 className="text-xl font-semibold">Create Notice</h2>
      <input className="input" placeholder="Title" {...register('title', { required: true })} />
      <textarea className="input min-h-36" placeholder="Description" {...register('description', { required: true })} />
      <label className="flex items-center gap-2 text-sm"><input type="checkbox" {...register('important')} /> Mark important</label>
      <button className="btn-primary">Publish Notice</button>
    </form>
  );
}
