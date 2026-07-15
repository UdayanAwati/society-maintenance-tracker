import { Navigate, Route, Routes } from 'react-router-dom';
import { useAuth } from './context/AuthContext.jsx';
import AppLayout from './layouts/AppLayout.jsx';
import Login from './pages/public/Login.jsx';
import Register from './pages/public/Register.jsx';
import ForgotPassword from './pages/public/ForgotPassword.jsx';
import Dashboard from './pages/Dashboard.jsx';
import CreateComplaint from './pages/CreateComplaint.jsx';
import Complaints from './pages/Complaints.jsx';
import ComplaintDetails from './pages/ComplaintDetails.jsx';
import Notices from './pages/Notices.jsx';
import NoticeForm from './pages/NoticeForm.jsx';
import Analytics from './pages/Analytics.jsx';
import Users from './pages/Users.jsx';
import Profile from './pages/Profile.jsx';
import NotFound from './pages/NotFound.jsx';

function Protected({ children, role }) {
  const { user, loading } = useAuth();
  if (loading) return <div className="p-8">Loading...</div>;
  if (!user) return <Navigate to="/login" replace />;
  if (role && user.role !== role) return <Navigate to="/" replace />;
  return children;
}

export default function App() {
  return (
    <Routes>
      <Route path="/login" element={<Login />} />
      <Route path="/register" element={<Register />} />
      <Route path="/forgot-password" element={<ForgotPassword />} />
      <Route path="/" element={<Protected><AppLayout /></Protected>}>
        <Route index element={<Dashboard />} />
        <Route path="complaints/new" element={<CreateComplaint />} />
        <Route path="complaints" element={<Complaints />} />
        <Route path="complaints/:id" element={<ComplaintDetails />} />
        <Route path="notices" element={<Notices />} />
        <Route path="profile" element={<Profile />} />
        <Route path="admin/notices/new" element={<Protected role="ADMIN"><NoticeForm /></Protected>} />
        <Route path="admin/analytics" element={<Protected role="ADMIN"><Analytics /></Protected>} />
        <Route path="admin/users" element={<Protected role="ADMIN"><Users /></Protected>} />
      </Route>
      <Route path="*" element={<NotFound />} />
    </Routes>
  );
}
