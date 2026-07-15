export function assetUrl(value) {
  if (!value) return '';
  if (/^https?:\/\//i.test(value)) return value;
  const apiRoot = (import.meta.env.VITE_API_URL || 'http://localhost:8080/api').replace(/\/api\/?$/, '');
  return `${apiRoot}${value.startsWith('/') ? value : `/${value}`}`;
}
