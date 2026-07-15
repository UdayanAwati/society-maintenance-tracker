import { useState } from 'react';
import { useForm } from 'react-hook-form';
import { useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { api } from '../services/api.js';

const complaintCategories = [
  'Plumbing',
  'Electrical',
  'Housekeeping',
  'Lift',
  'Security',
  'Parking',
  'Water Supply',
  'Waste Management',
  'Pest Control',
  'Common Area',
  'Noise',
  'Other',
];

export default function CreateComplaint() {
  const { register, handleSubmit } = useForm();
  const [preview, setPreview] = useState(null);
  const navigate = useNavigate();
  const submit = async (values) => {
    const form = new FormData();
    form.append('data', new Blob([JSON.stringify({ category: values.category, description: values.description })], { type: 'application/json' }));
    if (values.photo?.[0]) form.append('photo', values.photo[0]);
    try { await api.post('/complaints', form); toast.success('Complaint created'); navigate('/complaints'); }
    catch (error) { toast.error(error.message); }
  };
  return (
    <form onSubmit={handleSubmit(submit)} className="panel max-w-3xl space-y-4">
      <h2 className="text-xl font-semibold">Create Complaint</h2>
      <select className="input" defaultValue="" {...register('category', { required: true })}>
        <option value="" disabled>Select category</option>
        {complaintCategories.map((category) => (
          <option key={category} value={category}>{category}</option>
        ))}
      </select>
      <textarea className="input min-h-36" placeholder="Describe the issue" {...register('description', { required: true })} />
      <input className="input" type="file" accept="image/png,image/jpeg" {...register('photo', { onChange: (e) => setPreview(URL.createObjectURL(e.target.files[0])) })} />
      {preview && <img className="max-h-72 rounded-md border object-contain" src={preview} alt="Preview" />}
      <button className="btn-primary">Submit Complaint</button>
    </form>
  );
}
