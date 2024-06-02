import { useMemo } from 'react';
import { buildBreadcrumbs } from '@utils/breadcrumbsUtils';
import { useCurrentRoute } from '@hooks/useCurrentRoute';

export const useBreadcrumbs = () => {
  const currentRoute = useCurrentRoute();

  const breadcrumbs = useMemo(() => {
    if (currentRoute) {
      return buildBreadcrumbs(currentRoute);
    }
    return [];
  }, [currentRoute]);

  return breadcrumbs;
};
