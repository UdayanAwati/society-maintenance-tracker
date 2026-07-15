import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAuth } from '../../context/AuthContext.jsx';
import AuthShell from './AuthShell.jsx';

export default function Login() {
  const { register, handleSubmit } = useForm();
  const auth = useAuth();
  const navigate = useNavigate();
  const submit = async (values) => {
    try { await auth.login(values); toast.success('Logged in'); navigate('/'); }
    catch (error) { toast.error(error.message); }
  };
  return (
    <AuthShell title="Login">
      <form onSubmit={handleSubmit(submit)} className="space-y-4">
        <input className="input" placeholder="Email" type="email" {...register('email', { required: true })} />
        <input className="input" placeholder="Password" type="password" {...register('password', { required: true })} />
        <button className="btn-primary w-full">Login</button>
      </form>
      <div className="mt-4 flex justify-between text-sm"><Link to="/register">Create account</Link><Link to="/forgot-password">Forgot password?</Link></div>
    </AuthShell>
  );
}
