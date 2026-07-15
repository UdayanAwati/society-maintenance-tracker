import { useAuth } from '../context/AuthContext.jsx';
import { useState } from 'react';
import { toast } from 'react-toastify';
import { api } from '../services/api.js';

export default function Profile() {
  const { user, setUser, logout } = useAuth();
  const [phone, setPhone] = useState(user.phone || '');
  const [passwords, setPasswords] = useState({ currentPassword: '', newPassword: '' });
  const apiRoot = import.meta.env.VITE_API_URL?.replace('/api', '') || 'http://localhost:8080';
  const photo = user.profilePhotoUrl?.startsWith('/uploads') ? `${apiRoot}${user.profilePhotoUrl}` : user.profilePhotoUrl;

  const savePhone = async () => {
    try {
      const res = await api.patch('/users/me', { phone });
      setUser(res.data);
      toast.success('Profile updated');
    } catch (error) {
      toast.error(error.message);
    }
  };

  const savePhoto = async (file) => {
    if (!file) return;
    const form = new FormData();
    form.append('photo', file);
    try {
      const res = await api.post('/users/me/photo', form);
      setUser(res.data);
      toast.success('Profile photo updated');
    } catch (error) {
      toast.error(error.message);
    }
  };

  const changePassword = async () => {
    try {
      await api.patch('/users/me/password', passwords);
      setPasswords({ currentPassword: '', newPassword: '' });
      toast.success('Password changed');
    } catch (error) {
      toast.error(error.message);
    }
  };

  return (
    <div className="grid gap-6 xl:grid-cols-[1fr_360px]">
      <section className="panel space-y-5">
        <h2 className="text-xl font-semibold">Profile</h2>
        <div className="flex items-center gap-4">
          {photo ? <img className="h-20 w-20 rounded-full object-cover" src={photo} alt="" /> : <div className="flex h-20 w-20 items-center justify-center rounded-full bg-slate-100 text-2xl font-bold text-slate-500">{user.name?.[0]}</div>}
          <div>
            <p className="text-lg font-semibold">{user.name}</p>
            <p className="text-sm text-slate-500">{user.email}</p>
            <p className="text-sm text-slate-500">{user.role} - {user.flatNumber || 'No flat number'}</p>
          </div>
        </div>
        <div className="grid gap-4 md:grid-cols-2">
          <label className="text-sm">Name<input className="input mt-1" value={user.name} disabled /></label>
          <label className="text-sm">Email<input className="input mt-1" value={user.email} disabled /></label>
          <label className="text-sm">Flat Number<input className="input mt-1" value={user.flatNumber || ''} disabled /></label>
          <label className="text-sm">Phone Number<input className="input mt-1" value={phone} onChange={(e) => setPhone(e.target.value)} /></label>
        </div>
        <button className="btn-primary" onClick={savePhone}>Save Profile</button>
      </section>
      <aside className="space-y-6">
        <section className="panel space-y-4">
          <h3 className="font-semibold">Profile Picture</h3>
          <input className="input" type="file" accept="image/png,image/jpeg" onChange={(e) => savePhoto(e.target.files?.[0])} />
        </section>
        <section className="panel space-y-4">
          <h3 className="font-semibold">Password</h3>
          <input className="input" type="password" placeholder="Current password" value={passwords.currentPassword} onChange={(e) => setPasswords({ ...passwords, currentPassword: e.target.value })} />
          <input className="input" type="password" placeholder="New password" value={passwords.newPassword} onChange={(e) => setPasswords({ ...passwords, newPassword: e.target.value })} />
          <button className="btn-primary w-full" onClick={changePassword}>Change Password</button>
          <button className="btn-secondary w-full text-red-600" onClick={logout}>Logout</button>
        </section>
      </aside>
    </div>
  );
}
