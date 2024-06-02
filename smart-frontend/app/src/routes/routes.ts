import type { RoutesConfigs } from './routes.types';

const routes: RoutesConfigs = {
  '/': {
    breadcrumbs: [
      {
        label: 'Cluster info',
      },
    ],
  },
  '/rules': {
    breadcrumbs: [
      {
        label: 'Rules',
      },
    ],
  },
  '/actions': {
    breadcrumbs: [
      {
        label: 'Actions',
      },
    ],
  },
  '/audit': {
    breadcrumbs: [
      {
        label: 'Audit',
      },
    ],
  },
};

export default routes;
