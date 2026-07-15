export default function AuthShell({ title, children }) {
  return (
    <main className="flex min-h-screen items-center justify-center bg-slate-100 px-4 dark:bg-slate-950">
      <section className="w-full max-w-md rounded-lg border border-slate-200 bg-white p-6 shadow-sm dark:border-slate-800 dark:bg-slate-900">
        <p className="mb-2 text-sm font-semibold text-brand">Society Maintenance Tracker</p>
        <h1 className="mb-6 text-2xl font-bold">{title}</h1>
        {children}
      </section>
    </main>
  );
}
