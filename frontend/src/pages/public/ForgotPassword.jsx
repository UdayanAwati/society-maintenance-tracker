import { useForm } from 'react-hook-form';
import { Link } from 'react-router-dom';
import { toast } from 'react-toastify';
import { api } from '../../services/api.js';
import AuthShell from './AuthShell.jsx';

export default function ForgotPassword() {
  const { register, handleSubmit } = useForm();
  const submit = async (values) => {
    try { await api.post('/auth/forgot-password', values); toast.success('Check your email if the account exists'); }
    catch (error) { toast.error(error.message); }
  };
  return (
    <AuthShell title="Reset password">
      <form onSubmit={handleSubmit(submit)} className="space-y-4">
        <input className="input" placeholder="Email" type="email" {...register('email', { required: true })} />
        <button className="btn-primary w-full">Send reset link</button>
      </form>
      <p className="mt-4 text-sm"><Link to="/login">Back to login</Link></p>
    </AuthShell>
  );
}
