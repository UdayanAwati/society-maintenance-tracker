import { Link } from 'react-router-dom';

export default function NotFound() {
  return <main className="flex min-h-screen items-center justify-center"><div className="text-center"><h1 className="text-4xl font-bold">404</h1><p className="mt-2 text-slate-500">Page not found</p><Link className="mt-4 inline-flex text-brand" to="/">Go home</Link></div></main>;
}
