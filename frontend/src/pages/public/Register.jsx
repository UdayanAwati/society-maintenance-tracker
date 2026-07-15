import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAuth } from '../../context/AuthContext.jsx';
import AuthShell from './AuthShell.jsx';

export default function Register() {
  const { register, handleSubmit } = useForm();
  const auth = useAuth();
  const navigate = useNavigate();
  const submit = async (values) => {
    try { await auth.register(values); toast.success('Registered'); navigate('/'); }
    catch (error) { toast.error(error.message); }
  };
  return (
    <AuthShell title="Register">
      <form onSubmit={handleSubmit(submit)} className="space-y-4">
        <input className="input" placeholder="Name" {...register('name', { required: true })} />
        <input className="input" placeholder="Email" type="email" {...register('email', { required: true })} />
        <input className="input" placeholder="Phone" {...register('phone')} />
        <input className="input" placeholder="Flat number" {...register('flatNumber', { required: true })} />
        <input className="input" placeholder="Password" type="password" {...register('password', { required: true, minLength: 8 })} />
        <button className="btn-primary w-full">Create account</button>
      </form>
      <p className="mt-4 text-sm"><Link to="/login">Already registered? Login</Link></p>
    </AuthShell>
  );
}
