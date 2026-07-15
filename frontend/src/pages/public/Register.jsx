import { useForm } from 'react-hook-form';
import { Link, useNavigate } from 'react-router-dom';
import { toast } from 'react-toastify';
import { useAuth } from '../../context/AuthContext.jsx';
import AuthShell from './AuthShell.jsx';

export default function Register() {
  const { register, handleSubmit, formState: { errors, isSubmitting } } = useForm();
  const auth = useAuth();
  const navigate = useNavigate();
  const submit = async (values) => {
    try {
      await auth.register({
        ...values,
        email: values.email.trim().toLowerCase(),
        name: values.name.trim(),
        flatNumber: values.flatNumber.trim(),
        phone: values.phone?.trim(),
      });
      toast.success('Registered');
      navigate('/');
    } catch (error) {
      toast.error(error.message === 'Network Error' ? 'Cannot reach server. Please try again in a moment.' : error.message);
    }
  };
  return (
    <AuthShell title="Register">
      <form onSubmit={handleSubmit(submit)} className="space-y-4" noValidate>
        <div>
          <input className="input" placeholder="Name" autoComplete="name" {...register('name', { required: 'Name is required' })} />
          {errors.name && <p className="mt-1 text-sm text-red-600">{errors.name.message}</p>}
        </div>
        <div>
          <input className="input" placeholder="Email" type="email" autoComplete="email" {...register('email', { required: 'Email is required', pattern: { value: /^\S+@\S+\.\S+$/, message: 'Enter a valid email address' } })} />
          {errors.email && <p className="mt-1 text-sm text-red-600">{errors.email.message}</p>}
        </div>
        <input className="input" placeholder="Phone" type="tel" autoComplete="tel" {...register('phone')} />
        <div>
          <input className="input" placeholder="Flat number" autoComplete="address-line2" {...register('flatNumber', { required: 'Flat number is required' })} />
          {errors.flatNumber && <p className="mt-1 text-sm text-red-600">{errors.flatNumber.message}</p>}
        </div>
        <div>
          <input className="input" placeholder="Password" type="password" autoComplete="new-password" {...register('password', { required: 'Password is required', minLength: { value: 8, message: 'Password must be at least 8 characters' } })} />
          {errors.password && <p className="mt-1 text-sm text-red-600">{errors.password.message}</p>}
        </div>
        <button className="btn-primary w-full" disabled={isSubmitting}>{isSubmitting ? 'Creating account...' : 'Create account'}</button>
      </form>
      <p className="mt-4 text-sm"><Link to="/login">Already registered? Login</Link></p>
    </AuthShell>
  );
}
